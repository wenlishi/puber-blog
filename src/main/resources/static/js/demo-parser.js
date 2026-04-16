/**
 * 演示内容自动解析工具
 * 从完整HTML文件中自动提取HTML、CSS、JavaScript
 */

class DemoParser {

    /**
     * 从完整HTML解析出三个部分
     * @param {string} fullHtml 完整的HTML内容
     * @returns {Object} {html, css, js}
     */
    static parseFullHtml(fullHtml) {
        if (!fullHtml || fullHtml.trim() === '') {
            return { html: '', css: '', js: '' };
        }

        // 创建临时DOM解析器
        const parser = new DOMParser();
        const doc = parser.parseFromString(fullHtml, 'text/html');

        // 提取CSS（所有<style>标签内容）
        let css = '';
        const styleTags = doc.querySelectorAll('style');
        styleTags.forEach(tag => {
            css += tag.textContent + '\n';
        });

        // 提取JavaScript（所有<script>标签内容）
        let js = '';
        const scriptTags = doc.querySelectorAll('script');
        scriptTags.forEach(tag => {
            // 排除外部脚本（src属性）
            if (!tag.src) {
                js += tag.textContent + '\n';
            }
        });

        // 提取HTML内容（body内容，移除style和script标签）
        let html = '';

        // 获取body内容
        const body = doc.body;
        if (body) {
            // 创建body的副本
            const bodyClone = body.cloneNode(true);

            // 移除所有style和script标签
            const stylesToRemove = bodyClone.querySelectorAll('style');
            stylesToRemove.forEach(tag => tag.remove());

            const scriptsToRemove = bodyClone.querySelectorAll('script');
            scriptsToRemove.forEach(tag => tag.remove());

            // 获取剩余的HTML内容
            html = bodyClone.innerHTML;
        }

        return {
            html: html.trim(),
            css: css.trim(),
            js: js.trim()
        };
    }

    /**
     * 验证解析结果是否有效
     * @param {Object} parsed 解析后的对象
     * @returns {boolean}
     */
    static validateParsed(parsed) {
        return parsed.html || parsed.css || parsed.js;
    }

    /**
     * 显示解析预览（可选）
     * @param {Object} parsed 解析后的对象
     * @returns {string} 解析结果摘要
     */
    static getSummary(parsed) {
        const htmlLines = parsed.html ? parsed.html.split('\n').length : 0;
        const cssLines = parsed.css ? parsed.css.split('\n').length : 0;
        const jsLines = parsed.js ? parsed.js.split('\n').length : 0;

        return `解析完成：HTML ${htmlLines}行，CSS ${cssLines}行，JavaScript ${jsLines}行`;
    }
}