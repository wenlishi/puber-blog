/**
 * 留言管理后台交互逻辑
 * 处理留言审核、回复、删除等功能
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-19
 */

(function() {
    'use strict';

    // API端点
    const API = {
        GET_MESSAGES: '/api/admin/messages',
        GET_PENDING: '/api/admin/messages/pending',
        APPROVE: '/api/admin/messages',
        REJECT: '/api/admin/messages',
        DELETE: '/api/admin/messages',
        REPLY: '/api/admin/messages',
        COUNT_PENDING: '/api/admin/messages/count/pending'
    };

    // 当前状态
    let currentStatus = 'PENDING';
    let currentPage = 0;
    let currentMessageId = null;

    // DOM元素
    const messageList = document.getElementById('messageList');
    const loadingSpinner = document.getElementById('loadingSpinner');
    const emptyMessage = document.getElementById('emptyMessage');
    const pendingCount = document.getElementById('pendingCount');
    const totalCount = document.getElementById('totalCount');
    const pagination = document.getElementById('pagination');
    const replyModal = document.getElementById('replyModal');
    const replyOriginalMessage = document.getElementById('replyOriginalMessage');
    const replyContent = document.getElementById('replyContent');
    const toast = document.getElementById('toast');

    /**
     * 初始化
     */
    function init() {
        loadMessages();
        loadPendingCount();
    }

    /**
     * 加载留言列表
     */
    function loadMessages() {
        loadingSpinner.style.display = 'block';
        messageList.innerHTML = '';
        emptyMessage.style.display = 'none';
        pagination.style.display = 'none';

        let url = currentStatus === 'ALL' ? API.GET_MESSAGES : API.GET_PENDING;
        url += `?page=${currentPage}&size=10`;

        fetch(url)
            .then(response => response.json())
            .then(result => {
                loadingSpinner.style.display = 'none';

                if (result.success) {
                    const messages = result.data.content;
                    totalCount.textContent = result.data.totalElements;

                    if (messages.length === 0) {
                        emptyMessage.style.display = 'block';
                    } else {
                        renderMessages(messages);
                        renderPagination(result.data);
                    }
                } else {
                    showToast('加载失败：' + result.message, 'error');
                }
            })
            .catch(error => {
                loadingSpinner.style.display = 'none';
                showToast('加载失败，请稍后重试', 'error');
            });
    }

    /**
     * 加载待审核数量
     */
    function loadPendingCount() {
        fetch(API.COUNT_PENDING)
            .then(response => response.json())
            .then(result => {
                if (result.success) {
                    pendingCount.textContent = result.data;
                }
            })
            .catch(error => console.error('加载待审核数量失败', error));
    }

    /**
     * 渲染留言列表
     * @param {Array} messages 留言数据
     */
    function renderMessages(messages) {
        messageList.innerHTML = messages.map(message => {
            const statusBadge = message.status === 'PENDING' ?
                '<span class="badge badge-warning"><i class="ph-bold ph-clock"></i> 待审核</span>' :
                message.status === 'APPROVED' ?
                '<span class="badge badge-success"><i class="ph-bold ph-check"></i> 已批准</span>' :
                '<span class="badge" style="background:#FEF2F2;color:#DC2626;"><i class="ph-bold ph-x"></i> 已拒绝</span>';

            const actions = message.status === 'PENDING' ? `
                <button class="action-btn approve" onclick="approveMessage(${message.id})" title="批准">
                    <i class="ph-bold ph-check"></i>
                </button>
                <button class="action-btn delete" onclick="rejectMessage(${message.id})" title="拒绝">
                    <i class="ph-bold ph-x"></i>
                </button>
            ` : '';

            const replyButton = `
                <button class="action-btn" onclick="openReplyModal(${message.id}, '${escapeHtml(message.content)}')" title="回复">
                    <i class="ph-bold ph-chat-dots"></i>
                </button>
            `;

            const replySection = message.replyContent ? `
                <div style="margin-top: 12px; padding-top: 12px; border-top: 1px dashed var(--border-light);">
                    <div style="color: var(--text-muted); font-size: 12px; margin-bottom: 4px;">
                        <i class="ph-bold ph-user-circle"></i> 管理员回复 (${formatDate(message.replyTime)})
                    </div>
                    <div style="color: var(--text-regular);">${escapeHtml(message.replyContent)}</div>
                </div>
            ` : '';

            return `
                <div class="message-item" style="background: var(--bg-surface); border: 1px solid var(--border-light); border-radius: var(--radius-md); padding: 16px;">
                    <div style="display: flex; justify-content: space-between; align-items: start; margin-bottom: 12px;">
                        <div style="flex: 1;">
                            <div style="display: flex; align-items: center; gap: 8px; margin-bottom: 8px;">
                                <span style="font-weight: 600; color: var(--text-main);">${escapeHtml(message.nickname)}</span>
                                <span style="font-size: 12px; color: var(--text-muted);">${escapeHtml(message.email)}</span>
                                ${message.website ? `<a href="${escapeHtml(message.website)}" target="_blank" rel="noopener" style="font-size:12px;color:var(--primary);"><i class="ph-bold ph-link"></i></a>` : ''}
                            </div>
                            <div style="font-size: 12px; color: var(--text-muted);">
                                <span><i class="ph-bold ph-clock"></i> ${formatDate(message.createdAt)}</span>
                                <span style="margin-left: 16px;"><i class="ph-bold ph-location"></i> IP: ${escapeHtml(message.ipAddress || '未知')}</span>
                            </div>
                        </div>
                        ${statusBadge}
                    </div>
                    <div class="message-content">${escapeHtml(message.content)}</div>
                    ${replySection}
                    <div style="display: flex; gap: 8px; margin-top: 12px; padding-top: 12px; border-top: 1px solid var(--border-light);">
                        ${actions}
                        ${replyButton}
                        <button class="action-btn delete" onclick="deleteMessage(${message.id})" title="删除">
                            <i class="ph-bold ph-trash"></i>
                        </button>
                    </div>
                </div>
            `;
        }).join('');
    }

    /**
     * 渲染分页
     * @param {Object} data 分页数据
     */
    function renderPagination(data) {
        if (data.totalPages <= 1) {
            pagination.style.display = 'none';
            return;
        }

        pagination.style.display = 'flex';
        pagination.innerHTML = `
            <button class="btn-outline-custom btn-sm-custom" onclick="goToPage(${currentPage - 1})" ${currentPage === 0 ? 'disabled' : ''}>
                <i class="ph-bold ph-caret-left"></i> 上一页
            </button>
            <span style="color: var(--text-muted); font-size: 13px;">
                第 ${currentPage + 1} 页 / 共 ${data.totalPages} 页
            </span>
            <button class="btn-outline-custom btn-sm-custom" onclick="goToPage(${currentPage + 1})" ${currentPage >= data.totalPages - 1 ? 'disabled' : ''}>
                下一页 <i class="ph-bold ph-caret-right"></i>
            </button>
        `;
    }

    /**
     * 状态筛选
     * @param {String} status 状态
     */
    window.filterByStatus = function(status) {
        currentStatus = status;
        currentPage = 0;

        // 更新按钮样式
        document.getElementById('btnAll').className = status === 'ALL' ? 'btn-primary-custom' : 'btn-outline-custom btn-sm-custom';
        document.getElementById('btnPending').className = status === 'PENDING' ? 'btn-primary-custom' : 'btn-outline-custom btn-sm-custom';

        loadMessages();
    };

    /**
     * 跳转到指定页
     * @param {Number} page 页码
     */
    window.goToPage = function(page) {
        currentPage = page;
        loadMessages();
    };

    /**
     * 批准留言
     * @param {Number} id 留言ID
     */
    window.approveMessage = function(id) {
        fetch(`${API.APPROVE}/${id}/approve`, {method: 'PUT'})
            .then(response => response.json())
            .then(result => {
                if (result.success) {
                    showToast('已批准', 'success');
                    loadMessages();
                    loadPendingCount();
                } else {
                    showToast('操作失败：' + result.message, 'error');
                }
            })
            .catch(error => showToast('操作失败，请稍后重试', 'error'));
    };

    /**
     * 拒绝留言
     * @param {Number} id 留言ID
     */
    window.rejectMessage = function(id) {
        fetch(`${API.REJECT}/${id}/reject`, {method: 'PUT'})
            .then(response => response.json())
            .then(result => {
                if (result.success) {
                    showToast('已拒绝', 'success');
                    loadMessages();
                    loadPendingCount();
                } else {
                    showToast('操作失败：' + result.message, 'error');
                }
            })
            .catch(error => showToast('操作失败，请稍后重试', 'error'));
    };

    /**
     * 删除留言
     * @param {Number} id 留言ID
     */
    window.deleteMessage = function(id) {
        if (!confirm('确定删除这条留言吗？')) return;

        fetch(`${API.DELETE}/${id}`, {method: 'DELETE'})
            .then(response => response.json())
            .then(result => {
                if (result.success) {
                    showToast('已删除', 'success');
                    loadMessages();
                    loadPendingCount();
                } else {
                    showToast('删除失败：' + result.message, 'error');
                }
            })
            .catch(error => showToast('删除失败，请稍后重试', 'error'));
    };

    /**
     * 打开回复模态框
     * @param {Number} id 留言ID
     * @param {String} content 留言内容
     */
    window.openReplyModal = function(id, content) {
        currentMessageId = id;
        replyOriginalMessage.textContent = content;
        replyContent.value = '';
        replyModal.classList.add('show');
    };

    /**
     * 关闭回复模态框
     */
    window.closeReplyModal = function() {
        replyModal.classList.remove('show');
        currentMessageId = null;
    };

    /**
     * 提交回复
     */
    window.submitReply = function() {
        const content = replyContent.value.trim();
        if (!content) {
            showToast('请输入回复内容', 'error');
            return;
        }

        fetch(`${API.REPLY}/${currentMessageId}/reply`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({content})
        })
        .then(response => response.json())
        .then(result => {
            if (result.success) {
                showToast('回复成功', 'success');
                closeReplyModal();
                loadMessages();
            } else {
                showToast('回复失败：' + result.message, 'error');
            }
        })
        .catch(error => showToast('回复失败，请稍后重试', 'error'));
    };

    /**
     * HTML转义
     * @param {String} text 待转义文本
     * @returns {String} 转义后的文本
     */
    function escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text || '';
        return div.innerHTML;
    }

    /**
     * 日期格式化
     * @param {String} dateString 日期字符串
     * @returns {String} 格式化后的日期
     */
    function formatDate(dateString) {
        if (!dateString) return '';
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
     * 显示Toast提示
     * @param {String} message 提示消息
     * @param {String} type 类型（success/error）
     */
    function showToast(message, type) {
        toast.className = `toast ${type}`;
        toast.innerHTML = `
            <i class="ph-bold ${type === 'success' ? 'ph-check-circle' : 'ph-warning-circle'}"
               style="color: ${type === 'success' ? '#059669' : '#DC2626'}; font-size: 20px;"></i>
            <span style="color: var(--text-regular);">${escapeHtml(message)}</span>
        `;
        toast.classList.add('show');

        setTimeout(() => {
            toast.classList.remove('show');
        }, 3000);
    }

    // 页面加载完成后初始化
    init();
})();