/**
 * 后台主题颜色管理
 * 负责主题颜色的保存、加载和应用
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-19
 */

// 主题配置
const THEME_CONFIG = {
    STORAGE_KEY: 'admin_theme_color',
    DEFAULT_THEME: 'emerald',
    themes: {
        emerald: { hex: '#059669', rgb: '5, 150, 105' },
        orange: { hex: '#EA580C', rgb: '234, 88, 12' },
        blue: { hex: '#2563EB', rgb: '37, 99, 235' },
        beige: { hex: '#C89F70', rgb: '200, 159, 112' }
    }
};

/**
 * 保存主题颜色到 localStorage
 */
function saveThemeColor(themeColor) {
    try {
        localStorage.setItem(THEME_CONFIG.STORAGE_KEY, themeColor);
    } catch (e) {
        console.error('保存主题颜色失败:', e);
    }
}

/**
 * 从 localStorage 加载主题颜色
 */
function loadThemeColor() {
    try {
        const savedTheme = localStorage.getItem(THEME_CONFIG.STORAGE_KEY);
        return savedTheme || THEME_CONFIG.DEFAULT_THEME;
    } catch (e) {
        console.error('加载主题颜色失败:', e);
        return THEME_CONFIG.DEFAULT_THEME;
    }
}

/**
 * 应用主题颜色到页面
 */
function applyThemeColor(themeColor) {
    const theme = THEME_CONFIG.themes[themeColor];
    if (!theme) {
        console.warn('未知的主题颜色:', themeColor);
        return;
    }

    // 设置 CSS 变量
    document.documentElement.setAttribute('data-theme', themeColor);

    // 更新侧边栏头像
    const sidebarAvatar = document.getElementById('sidebarAvatar');
    if (sidebarAvatar) {
        sidebarAvatar.src = `https://ui-avatars.com/api/?name=Li&background=${theme.hex.substring(1)}&color=fff&font-size=0.4`;
    }

    // 更新主题选择按钮的激活状态
    const themeOptions = document.querySelectorAll('.theme-option');
    themeOptions.forEach(option => {
        const isActive = option.getAttribute('data-color') === themeColor;
        option.classList.toggle('active', isActive);
    });

    // 如果存在图表，更新图表颜色
    if (typeof myChart !== 'undefined' && myChart) {
        const ctx = document.getElementById('trafficChart');
        if (ctx) {
            const chartCtx = ctx.getContext('2d');
            const newGradient = chartCtx.createLinearGradient(0, 0, 0, 320);
            newGradient.addColorStop(0, `rgba(${theme.rgb}, 0.15)`);
            newGradient.addColorStop(1, `rgba(${theme.rgb}, 0)`);
            myChart.data.datasets[0].borderColor = theme.hex;
            myChart.data.datasets[0].pointBorderColor = theme.hex;
            myChart.data.datasets[0].backgroundColor = newGradient;
            myChart.update();
        }
    }

    console.log('主题颜色已应用:', themeColor);
}

/**
 * 初始化主题颜色
 * 页面加载时调用此函数
 */
function initThemeColor() {
    const savedTheme = loadThemeColor();
    applyThemeColor(savedTheme);
}

/**
 * 切换主题颜色
 * 用户点击主题选项时调用
 */
function switchThemeColor(themeColor) {
    saveThemeColor(themeColor);
    applyThemeColor(themeColor);
}

// 页面加载完成后自动初始化
document.addEventListener('DOMContentLoaded', function() {
    initThemeColor();
});