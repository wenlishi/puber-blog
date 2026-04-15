/**
 * 文章评论前端交互逻辑
 * 处理评论加载、提交、回复等功能
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-13
 */

(function() {
    'use strict';

    // 当前文章ID
    const articleId = document.getElementById('article-id').value;

    // API端点
    const API = {
        GET_COMMENTS: `/api/public/comments/article/${articleId}`,
        POST_COMMENT: '/api/public/comments'
    };

    console.log('评论API端点：', API); // 调试日志

    // DOM元素
    const commentsListContainer = document.getElementById('comments-list-container');
    const commentForm = document.getElementById('comment-form');
    const replyInfo = document.getElementById('reply-info');
    const replyTargetNickname = document.getElementById('reply-target-nickname');
    const cancelReplyBtn = document.getElementById('cancel-reply-btn');

    // 表单字段
    const parentIdInput = document.getElementById('parent-id');
    const replyToIdInput = document.getElementById('reply-to-id');
    const replyToNicknameInput = document.getElementById('reply-to-nickname');
    const nicknameInput = document.getElementById('nickname');
    const emailInput = document.getElementById('email');
    const websiteInput = document.getElementById('website');
    const contentInput = document.getElementById('content');

    /**
     * 初始化
     */
    function init() {
        loadComments();
        bindEvents();
    }

    /**
     * 加载评论列表
     */
    function loadComments() {
        fetch(API.GET_COMMENTS)
            .then(response => response.json())
            .then(result => {
                if (result.success) {
                    renderComments(result.data);
                } else {
                    showError('加载评论失败：' + result.message);
                }
            })
            .catch(error => {
                console.error('加载评论失败：', error);
                showError('加载评论失败，请稍后重试');
            });
    }

    /**
     * 渲染评论列表
     * @param {Array} comments 评论数据（树形结构）
     */
    function renderComments(comments) {
        if (!comments || comments.length === 0) {
            commentsListContainer.innerHTML = `
                <div class="comments-empty">
                    <i class="bi bi-chat-dots"></i>
                    <p>暂无评论，快来发表第一条评论吧！</p>
                </div>
            `;
            return;
        }

        let html = '<div class="comments-list">';
        comments.forEach(comment => {
            html += renderCommentItem(comment);
        });
        html += '</div>';

        commentsListContainer.innerHTML = html;
    }

    /**
     * 渲染单条评论
     * @param {Object} comment 评论对象
     * @param {Boolean} isReply 是否为回复评论
     * @returns {String} HTML字符串
     */
    function renderCommentItem(comment, isReply = false) {
        const nickname = escapeHtml(comment.nickname);
        // 评论内容不需要转义：后台已经做了 XSS 过滤，移除了危险标签
        const content = comment.content;
        const createdAt = formatDate(comment.createdAt);
        const website = comment.website ? escapeHtml(comment.website) : null;
        const initials = nickname.charAt(0).toUpperCase();

        let replyToHtml = '';
        if (comment.replyToNickname) {
            replyToHtml = `
                <span class="reply-to">
                    <i class="bi bi-reply-fill"></i>
                    回复 <span class="reply-to-nickname">${escapeHtml(comment.replyToNickname)}</span>：
                </span>
            `;
        }

        let repliesHtml = '';
        if (comment.replies && comment.replies.length > 0) {
            repliesHtml = '<div class="comment-replies">';
            comment.replies.forEach(reply => {
                repliesHtml += renderCommentItem(reply, true);
            });
            repliesHtml += '</div>';
        }

        return `
            <div class="comment-item" data-comment-id="${comment.id}">
                <div class="comment-header">
                    <div class="comment-avatar">${initials}</div>
                    <div>
                        <span class="comment-author">
                            ${website ? `<a href="${website}" class="comment-author-link" target="_blank" rel="noopener noreferrer">${nickname}</a>` : nickname}
                        </span>
                        <span class="comment-date">${createdAt}</span>
                    </div>
                </div>
                <div class="comment-content">
                    ${replyToHtml}${content}
                </div>
                <div class="comment-actions">
                    <button class="btn comment-reply-btn" onclick="handleReply(${comment.id}, '${nickname}')">
                        <i class="bi bi-reply-fill"></i>回复
                    </button>
                </div>
                ${repliesHtml}
            </div>
        `;
    }

    /**
     * 绑定事件
     */
    function bindEvents() {
        // 提交评论表单
        commentForm.addEventListener('submit', handleSubmitComment);

        // 取消回复
        cancelReplyBtn.addEventListener('click', cancelReply);
    }

    /**
     * 处理提交评论
     * @param {Event} event 表单提交事件
     */
    function handleSubmitComment(event) {
        event.preventDefault();

        // 获取表单数据
        const nickname = nicknameInput.value.trim();
        const email = emailInput.value.trim();
        const website = websiteInput.value.trim();
        const content = contentInput.value.trim();
        const parentId = parentIdInput.value;
        const replyToId = replyToIdInput.value;

        // 验证必填字段
        if (!nickname) {
            showValidationError('请输入昵称', nicknameInput);
            return;
        }
        if (!email) {
            showValidationError('请输入邮箱', emailInput);
            return;
        }
        if (!content) {
            showValidationError('请输入评论内容', contentInput);
            return;
        }

        // 构建评论数据
        const commentData = {
            nickname: nickname,
            email: email,
            website: website || null,
            content: content,
            articleId: parseInt(articleId),
            parentId: parentId ? parseInt(parentId) : null,
            replyToId: replyToId ? parseInt(replyToId) : null
        };

        // 发送请求
        submitComment(commentData);
    }

    /**
     * 提交评论到服务器
     * @param {Object} commentData 评论数据
     */
    function submitComment(commentData) {
        // 禁用提交按钮
        const submitBtn = commentForm.querySelector('button[type="submit"]');
        const originalBtnText = submitBtn.innerHTML;
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<i class="bi bi-hourglass-split"></i> 提交中...';

        fetch(API.POST_COMMENT, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(commentData)
        })
            .then(response => response.json())
            .then(result => {
                if (result.success) {
                    showSuccess('评论提交成功！您的评论将在审核后显示。');
                    resetForm();
                    cancelReply();
                    // 重新加载评论列表
                    loadComments();
                } else {
                    showError('提交评论失败：' + result.message);
                }
            })
            .catch(error => {
                console.error('提交评论失败：', error);
                showError('提交评论失败，请稍后重试');
            })
            .finally(() => {
                // 恢复提交按钮
                submitBtn.disabled = false;
                submitBtn.innerHTML = originalBtnText;
            });
    }

    /**
     * 处理回复评论
     * @param {Number} commentId 评论ID
     * @param {String} nickname 评论者昵称
     */
    window.handleReply = function(commentId, nickname) {
        // 设置回复信息
        parentIdInput.value = commentId;
        replyToIdInput.value = commentId;
        replyToNicknameInput.value = nickname;

        // 显示回复提示
        replyTargetNickname.textContent = nickname;
        replyInfo.style.display = 'block';

        // 滚动到评论表单
        commentForm.scrollIntoView({ behavior: 'smooth', block: 'center' });

        // 清空内容并聚焦
        contentInput.value = '';
        contentInput.focus();
    };

    /**
     * 取消回复
     */
    function cancelReply() {
        // 清空回复信息
        parentIdInput.value = '';
        replyToIdInput.value = '';
        replyToNicknameInput.value = '';

        // 隐藏回复提示
        replyInfo.style.display = 'none';
    }

    /**
     * 重置表单
     */
    function resetForm() {
        // 只清空内容字段，保留昵称、邮箱等信息（方便下次评论）
        contentInput.value = '';
    }

    /**
     * 显示成功消息
     * @param {String} message 消息内容
     */
    function showSuccess(message) {
        showAlert(message, 'success');
    }

    /**
     * 显示错误消息
     * @param {String} message 消息内容
     */
    function showError(message) {
        showAlert(message, 'danger');
    }

    /**
     * 显示提示框
     * @param {String} message 消息内容
     * @param {String} type 类型（success, danger, warning, info）
     */
    function showAlert(message, type) {
        // 创建提示框
        const alertDiv = document.createElement('div');
        alertDiv.className = `alert alert-${type} alert-dismissible fade show comment-success-alert`;
        alertDiv.role = 'alert';
        alertDiv.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        `;

        // 插入到评论表单前
        commentForm.parentNode.insertBefore(alertDiv, commentForm);

        // 5秒后自动关闭
        setTimeout(() => {
            alertDiv.remove();
        }, 5000);
    }

    /**
     * 显示表单验证错误
     * @param {String} message 错误消息
     * @param {HTMLElement} inputElement 输入元素
     */
    function showValidationError(message, inputElement) {
        inputElement.classList.add('is-invalid');
        inputElement.focus();

        // 创建错误提示
        const feedback = document.createElement('div');
        feedback.className = 'invalid-feedback';
        feedback.textContent = message;

        // 如果不存在feedback元素则添加
        if (!inputElement.nextElementSibling || !inputElement.nextElementSibling.classList.contains('invalid-feedback')) {
            inputElement.parentNode.appendChild(feedback);
        }

        // 3秒后移除错误状态
        setTimeout(() => {
            inputElement.classList.remove('is-invalid');
            if (feedback.parentNode) {
                feedback.remove();
            }
        }, 3000);
    }

    /**
     * 格式化日期
     * @param {String} dateStr 日期字符串
     * @returns {String} 格式化后的日期
     */
    function formatDate(dateStr) {
        if (!dateStr) return '';

        const date = new Date(dateStr);
        const now = new Date();
        const diff = now - date;

        // 如果是1小时内
        if (diff < 3600000) {
            const minutes = Math.floor(diff / 60000);
            return minutes <= 1 ? '刚刚' : `${minutes}分钟前`;
        }

        // 如果是24小时内
        if (diff < 86400000) {
            const hours = Math.floor(diff / 3600000);
            return `${hours}小时前`;
        }

        // 如果是7天内
        if (diff < 604800000) {
            const days = Math.floor(diff / 86400000);
            return `${days}天前`;
        }

        // 否则显示完整日期
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hour = String(date.getHours()).padStart(2, '0');
        const minute = String(date.getMinutes()).padStart(2, '0');

        return `${year}-${month}-${day} ${hour}:${minute}`;
    }

    /**
     * HTML转义（防止XSS攻击）
     * @param {String} text 待转义的文本
     * @returns {String} 转义后的文本
     */
    function escapeHtml(text) {
        if (!text) return '';
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    // 页面加载完成后初始化
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();