/**
 * 留言板前端交互逻辑
 * 处理留言加载、提交、回复等功能
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-19
 */

(function() {
    'use strict';

    // API端点
    const API = {
        GET_MESSAGES: '/api/public/messages',
        POST_MESSAGE: '/api/public/messages'
    };

    // DOM元素
    const messagesListContainer = document.getElementById('messages-list-container');
    const messageForm = document.getElementById('message-form');
    const replyInfo = document.getElementById('reply-info');
    const replyTargetNickname = document.getElementById('reply-target-nickname');
    const cancelReplyBtn = document.getElementById('cancel-reply-btn');

    // 表单字段
    const parentIdInput = document.getElementById('parent-id');
    const replyToIdInput = document.getElementById('reply-to-id');
    const nicknameInput = document.getElementById('nickname');
    const emailInput = document.getElementById('email');
    const websiteInput = document.getElementById('website');
    const contentInput = document.getElementById('content');

    /**
     * 初始化
     */
    function init() {
        loadMessages();
        bindEvents();
    }

    /**
     * 加载留言列表
     */
    function loadMessages() {
        fetch(API.GET_MESSAGES)
            .then(response => response.json())
            .then(result => {
                if (result.success) {
                    renderMessages(result.data);
                } else {
                    showError('加载留言失败：' + result.message);
                }
            })
            .catch(error => {
                console.error('加载留言失败：', error);
                showError('加载留言失败，请稍后重试');
            });
    }

    /**
     * 渲染留言列表
     * @param {Array} messages 留言数据（树形结构）
     */
    function renderMessages(messages) {
        if (!messages || messages.length === 0) {
            messagesListContainer.innerHTML = `
                <div class="messages-empty">
                    <i class="bi bi-chat-dots"></i>
                    <p>暂无留言，快来发表第一条留言吧！</p>
                </div>
            `;
            return;
        }

        let html = '<div class="messages-list">';
        messages.forEach(message => {
            html += renderMessageItem(message);
        });
        html += '</div>';

        messagesListContainer.innerHTML = html;
    }

    /**
     * 渲染单条留言
     * @param {Object} message 留言对象
     * @param {Boolean} isReply 是否为回复留言
     * @returns {String} HTML字符串
     */
    function renderMessageItem(message, isReply = false) {
        const nickname = escapeHtml(message.nickname);
        // 留言内容不需要转义：后台已经做了 XSS 过滤，移除了危险标签
        const content = message.content;
        const createdAt = formatDate(message.createdAt);
        const website = message.website ? escapeHtml(message.website) : null;
        const initials = nickname.charAt(0).toUpperCase();

        let replyToHtml = '';
        if (message.replyToNickname) {
            replyToHtml = `
                <span class="reply-to">
                    <i class="bi bi-reply-fill"></i>
                    回复 <span class="reply-to-nickname">${escapeHtml(message.replyToNickname)}</span>：
                </span>
            `;
        }

        // 管理员回复部分
        let adminReplyHtml = '';
        if (message.replyContent) {
            const replyTime = formatDate(message.replyTime);
            adminReplyHtml = `
                <div class="admin-reply">
                    <div class="admin-reply-header">
                        <span class="admin-badge"><i class="bi bi-person-badge-fill"></i> 管理员</span>
                        <span class="admin-reply-time">${replyTime}</span>
                    </div>
                    <div class="admin-reply-content">${message.replyContent}</div>
                </div>
            `;
        }

        let repliesHtml = '';
        if (message.replies && message.replies.length > 0) {
            repliesHtml = '<div class="message-replies">';
            message.replies.forEach(reply => {
                repliesHtml += renderMessageItem(reply, true);
            });
            repliesHtml += '</div>';
        }

        return `
            <div class="message-item" data-message-id="${message.id}">
                <div class="message-header">
                    <div class="message-avatar">${initials}</div>
                    <div>
                        <span class="message-author">
                            ${website ? `<a href="${website}" class="message-author-link" target="_blank" rel="noopener noreferrer">${nickname}</a>` : nickname}
                        </span>
                        <span class="message-date">${createdAt}</span>
                    </div>
                    <button class="btn btn-sm btn-outline-primary reply-btn" data-message-id="${message.id}">
                        <i class="bi bi-reply"></i> 回复
                    </button>
                </div>
                <div class="message-content">
                    ${replyToHtml}
                    <p>${content}</p>
                </div>
                ${adminReplyHtml}
                ${repliesHtml}
            </div>
        `;
    }

    /**
     * 绑定事件
     */
    function bindEvents() {
        // 提交留言表单
        messageForm.addEventListener('submit', handleSubmit);

        // 回复按钮点击（动态绑定）
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('reply-btn') || e.target.parentElement.classList.contains('reply-btn')) {
                const messageId = e.target.dataset.messageId || e.target.parentElement.dataset.messageId;
                handleReply(messageId);
            }
        });

        // 取消回复
        cancelReplyBtn.addEventListener('click', cancelReply);
    }

    /**
     * 提交留言
     * @param {Event} e 表单提交事件
     */
    function handleSubmit(e) {
        e.preventDefault();

        const formData = {
            nickname: nicknameInput.value.trim(),
            email: emailInput.value.trim(),
            website: websiteInput.value.trim() || null,
            content: contentInput.value.trim(),
            parentId: parentIdInput.value || null,
            replyToId: replyToIdInput.value || null
        };

        fetch(API.POST_MESSAGE, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(formData)
        })
        .then(response => response.json())
        .then(result => {
            if (result.success) {
                showSuccess('留言提交成功，等待审核后显示');
                messageForm.reset();
                cancelReply();
            } else {
                showError(result.message);
            }
        })
        .catch(error => {
            console.error('提交留言失败：', error);
            showError('提交失败，请稍后重试');
        });
    }

    /**
     * 处理回复
     * @param {String} messageId 留言ID
     */
    function handleReply(messageId) {
        const messageItem = document.querySelector(`[data-message-id="${messageId}"]`);
        const nickname = messageItem.querySelector('.message-author').textContent.trim();

        parentIdInput.value = messageId;
        replyToIdInput.value = messageId;
        replyTargetNickname.textContent = nickname;

        replyInfo.classList.remove('d-none');
        contentInput.focus();
    }

    /**
     * 取消回复
     */
    function cancelReply() {
        parentIdInput.value = '';
        replyToIdInput.value = '';
        replyInfo.classList.add('d-none');
    }

    /**
     * HTML转义（防止XSS）
     * @param {String} text 待转义文本
     * @returns {String} 转义后的文本
     */
    function escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    /**
     * 日期格式化
     * @param {String} dateString 日期字符串
     * @returns {String} 格式化后的日期
     */
    function formatDate(dateString) {
        const date = new Date(dateString);
        return date.toLocaleString('zh-CN', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    /**
     * 显示成功提示
     * @param {String} message 提示消息
     */
    function showSuccess(message) {
        alert(message); // 简单实现，可以使用更优雅的Toast组件
    }

    /**
     * 显示错误提示
     * @param {String} message 错误消息
     */
    function showError(message) {
        alert(message); // 简单实现，可以使用更优雅的Toast组件
    }

    // 页面加载完成后初始化
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();