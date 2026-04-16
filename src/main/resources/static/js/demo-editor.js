// CodeMirror编辑器实例
let fullHtmlEditor;

// 初始化编辑器
document.addEventListener('DOMContentLoaded', function() {
    // 完整HTML编辑器
    fullHtmlEditor = CodeMirror.fromTextArea(document.getElementById('fullHtmlEditor'), {
        mode: 'htmlmixed',
        theme: 'darcula',
        lineNumbers: true,
        autoCloseTags: true,
        autoCloseBrackets: true
    });

    // 自动预览（每3秒更新）
    setInterval(updatePreview, 3000);

    // 检查是否为编辑模式
    const path = window.location.pathname;
    if (path.includes('/edit')) {
        const id = path.split('/')[3];
        loadDemo(id);
    }
});

// 更新预览
function updatePreview() {
    const previewFrame = document.getElementById('previewFrame');
    const fullHtml = fullHtmlEditor.getValue();

    // 直接使用完整HTML更新iframe
    previewFrame.srcdoc = fullHtml;
}

// 表单提交
document.getElementById('demoForm').addEventListener('submit', function(e) {
    e.preventDefault();

    const demoId = document.getElementById('demoId').value;
    const name = document.getElementById('name').value;
    const slug = document.getElementById('slug').value;
    const description = document.getElementById('description').value;
    const fullHtmlContent = fullHtmlEditor.getValue();
    const status = document.getElementById('status').value;

    const data = {
        name: name,
        slug: slug,
        description: description,
        fullHtmlContent: fullHtmlContent,
        status: status
    };

    const url = demoId ? '/api/admin/demos/' + demoId : '/api/admin/demos';
    const method = demoId ? 'PUT' : 'POST';

    fetch(url, {
        method: method,
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
    .then(response => response.json())
    .then(result => {
        if (result.code === 200) {
            alert('保存成功！');
            window.location.href = '/admin/demos';
        } else {
            alert('保存失败：' + result.message);
        }
    })
    .catch(error => {
        alert('请求失败：' + error);
    });
});

// 加载演示数据（编辑模式）
function loadDemo(id) {
    document.getElementById('pageTitle').innerHTML = '<i class="bi bi-pencil"></i> 编辑演示';

    fetch('/api/admin/demos/' + id)
        .then(response => response.json())
        .then(result => {
            if (result.code === 200) {
                const demo = result.data;
                document.getElementById('demoId').value = demo.id;
                document.getElementById('name').value = demo.name;
                document.getElementById('slug').value = demo.slug;
                document.getElementById('description').value = demo.description || '';
                fullHtmlEditor.setValue(demo.fullHtmlContent || '');
                document.getElementById('status').value = demo.status;
                updatePreview();
            }
        });
}