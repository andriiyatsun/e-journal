document.addEventListener("DOMContentLoaded", function() {
    // Tabs switcher
    const tabs = document.querySelectorAll('#headTabs .tab-btn');
    const panels = {
        overview: document.getElementById('panel-overview'),
        subjects: document.getElementById('panel-subjects'),
        teachers: document.getElementById('panel-teachers'),
        reports: document.getElementById('panel-reports')
    };

    function activate(name){
        if (!panels[name] && name !== 'overview' && name !== 'subjects') return; // Basic checks

        tabs.forEach(b => b.classList.toggle('active', b.dataset.tab === name));

        // Hide all
        Object.values(panels).forEach(p => { if(p) p.classList.remove('active') });
        // Show current
        if (panels[name]) panels[name].classList.add('active');

        try { history.replaceState(null, '', '#' + name); } catch (e) {}
    }

    tabs.forEach(btn => btn.addEventListener('click', () => activate(btn.dataset.tab)));

    const fromHash = location.hash?.replace('#','');
    if(fromHash && panels[fromHash]) {
        activate(fromHash);
    } else {
        // Default tab
        if(tabs.length > 0) activate(tabs[0].dataset.tab);
    }
});

// Report functions (Global scope needed for onclick in HTML)
function generateDeptReport() {
    showReport('Звіт по відділу генерується...', '/head/reports/department');
}

function generateGradesReport() {
    showReport('Звіт по оцінках генерується...', '/head/reports/grades');
}

function generateAttendanceReport() {
    showReport('Звіт по відвідуваності генерується...', '/head/reports/attendance');
}

function generateTeachersReport() {
    showReport('Звіт по викладачах генерується...', '/head/reports/teachers');
}

function showReport(message, url) {
    const content = document.getElementById('reportContent');
    const data = document.getElementById('reportData');
    data.innerHTML = message;
    content.style.display = 'block';

    // Simulate API call
    setTimeout(() => {
        data.innerHTML = '<p>Звіт готовий! <a href="' + url + '" target="_blank">Завантажити PDF</a></p>';
    }, 2000);
}

// Quick actions
function exportAllGrades() {
    window.location.href = '/head/export/all-grades';
}

function sendReminders() {
    alert('Нагадування відправлено всім викладачам відділу');
}

function scheduleReport() {
    alert('Функція планування автозвітів буде доступна найближчим часом');
}

// Navigation helpers
function viewSubjectDetails(subjectId) {
    window.location.href = `/head/subjects/${subjectId}/details`;
}
function viewSubject(subjectId) {
    window.location.href = `/head/subjects/${subjectId}`;
}
function viewTeacher(teacherId) {
    window.location.href = `/head/teachers/${teacherId}`;
}