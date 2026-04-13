/**
 * 文章目录导航（TOC）功能
 * 自动提取文章标题生成目录，支持点击跳转和滚动高亮
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-13
 */

(function() {
    'use strict';

    // 配置
    const CONFIG = {
        MIN_HEADINGS: 2,  // 最少标题数量才显示目录
        MAX_DEPTH: 3,     // 提取标题的最大深度（h1-h3）
        SCROLL_OFFSET: 80 // 滚动偏移量（考虑固定导航栏）
    };

    // DOM元素
    const tocContainer = document.getElementById('toc-container');
    const tocNav = document.getElementById('toc-nav');
    const articleContent = document.querySelector('.article-content');

    /**
     * 初始化
     */
    function init() {
        if (!articleContent || !tocNav) {
            console.warn('未找到文章内容区域或目录容器');
            return;
        }

        // 提取标题
        const headings = extractHeadings();

        // 如果标题数量不足，隐藏目录容器
        if (headings.length < CONFIG.MIN_HEADINGS) {
            if (tocContainer) {
                tocContainer.style.display = 'none';
            }
            return;
        }

        // 生成目录
        generateTOC(headings);

        // 为标题添加ID
        addHeadingIds(headings);

        // 绑定事件
        bindEvents(headings);

        // 初始化高亮
        highlightCurrentHeading(headings);
    }

    /**
     * 提取文章标题
     * @returns {Array} 标题数组
     */
    function extractHeadings() {
        const headings = [];
        const selector = 'h1, h2, h3, h4, h5, h6';
        const elements = articleContent.querySelectorAll(selector);

        elements.forEach((element, index) => {
            const level = parseInt(element.tagName.charAt(1));

            // 只提取指定深度的标题
            if (level <= CONFIG.MAX_DEPTH) {
                headings.push({
                    element: element,
                    level: level,
                    text: element.textContent.trim(),
                    index: index
                });
            }
        });

        return headings;
    }

    /**
     * 为标题添加ID（用于锚点跳转）
     * @param {Array} headings 标题数组
     */
    function addHeadingIds(headings) {
        headings.forEach((heading, index) => {
            if (!heading.element.id) {
                // 生成唯一ID
                const id = generateHeadingId(heading.text, index);
                heading.element.id = id;
            }
            heading.id = heading.element.id;
        });
    }

    /**
     * 生成标题ID
     * @param {String} text 标题文本
     * @param {Number} index 标题索引
     * @returns {String} 生成的ID
     */
    function generateHeadingId(text, index) {
        // 移除特殊字符，转换为小写
        const cleanText = text
            .toLowerCase()
            .replace(/[^\w\u4e00-\u9fa5]+/g, '-')
            .replace(/^-+|-+$/g, '');

        return `heading-${index}-${cleanText}`;
    }

    /**
     * 生成目录HTML
     * @param {Array} headings 标题数组
     */
    function generateTOC(headings) {
        let html = '<ul class="toc-list">';

        headings.forEach((heading, index) => {
            const indentClass = `toc-indent-${heading.level}`;
            const isActive = index === 0 ? 'toc-item-active' : '';

            html += `
                <li class="toc-item ${indentClass}" data-heading-id="${heading.id}">
                    <a href="#${heading.id}" class="toc-link ${isActive}">
                        ${escapeHtml(heading.text)}
                    </a>
                </li>
            `;
        });

        html += '</ul>';
        tocNav.innerHTML = html;
    }

    /**
     * 绑定事件
     * @param {Array} headings 标题数组
     */
    function bindEvents(headings) {
        // 目录点击事件
        tocNav.addEventListener('click', function(e) {
            if (e.target.classList.contains('toc-link')) {
                e.preventDefault();

                const targetId = e.target.getAttribute('href').substring(1);
                const targetElement = document.getElementById(targetId);

                if (targetElement) {
                    // 平滑滚动到目标位置
                    scrollToHeading(targetElement);

                    // 更新URL（不触发滚动）
                    history.pushState(null, null, `#${targetId}`);

                    // 更新高亮状态
                    updateActiveLink(targetId);
                }
            }
        });

        // 滚动监听（高亮当前章节）
        let scrollTimeout;
        window.addEventListener('scroll', function() {
            if (scrollTimeout) {
                clearTimeout(scrollTimeout);
            }

            scrollTimeout = setTimeout(function() {
                highlightCurrentHeading(headings);
            }, 100);
        });
    }

    /**
     * 平滑滚动到标题位置
     * @param {HTMLElement} element 目标元素
     */
    function scrollToHeading(element) {
        const elementPosition = element.getBoundingClientRect().top;
        const offsetPosition = elementPosition + window.pageYOffset - CONFIG.SCROLL_OFFSET;

        window.scrollTo({
            top: offsetPosition,
            behavior: 'smooth'
        });
    }

    /**
     * 高亮当前阅读的章节
     * @param {Array} headings 标题数组
     */
    function highlightCurrentHeading(headings) {
        const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
        const windowHeight = window.innerHeight;

        let currentHeadingId = null;

        // 找到当前可见的标题
        headings.forEach((heading) => {
            const element = heading.element;
            const elementTop = element.getBoundingClientRect().top + scrollTop;
            const elementBottom = elementTop + element.offsetHeight;

            // 判断标题是否在视窗范围内
            if (elementTop <= scrollTop + CONFIG.SCROLL_OFFSET + 50 &&
                elementBottom > scrollTop + CONFIG.SCROLL_OFFSET) {
                currentHeadingId = heading.id;
            }
        });

        // 如果没有找到，默认第一个标题
        if (!currentHeadingId && headings.length > 0) {
            if (scrollTop < headings[0].element.getBoundingClientRect().top + scrollTop) {
                currentHeadingId = headings[0].id;
            }
        }

        // 更新高亮状态
        if (currentHeadingId) {
            updateActiveLink(currentHeadingId);
        }
    }

    /**
     * 更新目录高亮状态
     * @param {String} activeId 当前高亮的标题ID
     */
    function updateActiveLink(activeId) {
        // 移除所有高亮
        const links = tocNav.querySelectorAll('.toc-link');
        links.forEach(link => {
            link.classList.remove('toc-item-active');
        });

        // 添加当前高亮
        const activeLink = tocNav.querySelector(`[href="#${activeId}"]`);
        if (activeLink) {
            activeLink.classList.add('toc-item-active');

            // 确保高亮项在目录容器内可见
            scrollTOCIntoView(activeLink);
        }
    }

    /**
     * 确保高亮的目录项在容器内可见
     * @param {HTMLElement} activeLink 高亮的链接元素
     */
    function scrollTOCIntoView(activeLink) {
        const tocContainerBody = tocNav.parentElement;
        const linkTop = activeLink.offsetTop;
        const containerHeight = tocContainerBody.offsetHeight;
        const scrollTop = tocContainerBody.scrollTop;

        // 如果高亮项不在可视区域内，滚动目录容器
        if (linkTop < scrollTop || linkTop > scrollTop + containerHeight - 20) {
            tocContainerBody.scrollTop = linkTop - containerHeight / 2;
        }
    }

    /**
     * HTML转义（防止XSS）
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