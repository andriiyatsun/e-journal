// JS для вкладки "Журнали"

// --- КРОК 1: Логіка для Відділів (Departments) ---
const journalDeptButtons = document.querySelectorAll('#panel-journals .dept-pill-btn');
const journalDeptBoxes = document.querySelectorAll('#panel-journals .dept-box');
const allSubjectButtons = document.querySelectorAll('#panel-journals .subject-pill-btn');
const allJournalBoxes = document.querySelectorAll('#panel-journals .journal-list-box');

function activateJournalDept(id) {
    if (!id) return;

    const btn = document.querySelector(`.dept-pill-btn[data-dept-id='${id}']`);
    const wasActive = btn ? btn.classList.contains('active') : false;

    // Скидаємо все
    journalDeptButtons.forEach(b => b.classList.remove('active'));
    journalDeptBoxes.forEach(box => box.classList.remove('active'));
    allSubjectButtons.forEach(b => b.classList.remove('active'));
    allJournalBoxes.forEach(box => box.classList.remove('active'));

    if (!wasActive && btn) {
        btn.classList.add('active');
        const deptBox = document.getElementById('dept-box-' + id);
        if(deptBox) deptBox.classList.add('active');
    }
}
journalDeptButtons.forEach(btn => btn.addEventListener('click', () => activateJournalDept(btn.dataset.deptId)));

// --- КРОК 2: Логіка: Для Предметів (Subjects) ---
function activateSubject(subjectId) {
    if (!subjectId) return;

    const btn = document.querySelector(`.subject-pill-btn[data-subject-id='${subjectId}']`);
    const box = document.getElementById('subject-box-' + subjectId);
    if (!btn || !box) return;

    const wasActive = btn.classList.contains('active');

    // Скидаємо ТІЛЬКИ сусідні предмети
    btn.closest('.subject-list').querySelectorAll('.subject-pill-btn').forEach(b => b.classList.remove('active'));
    btn.closest('.subject-list').querySelectorAll('.journal-list-box').forEach(b => b.classList.remove('active'));

    if (!wasActive) {
        btn.classList.add('active');
        box.classList.add('active');
    }
}
allSubjectButtons.forEach(btn => btn.addEventListener('click', (e) => {
    e.stopPropagation();
    activateSubject(btn.dataset.subjectId);
}));

// Функція підтвердження видалення предмета
function confirmDeleteSubject(formElement) {
    const subjectName = formElement.getAttribute('data-subject-name');
    const message = `УВАГА! Ви впевнені, що хочете видалити предмет '${subjectName}'?\n\nЦя дія видалить ВСІ пов'язані з ним журнали (за всі роки) та всі їхні дані (КТП, списки учнів тощо).\n\nЦя дія НЕЗВОРОТНА.`;
    return confirm(message);
}

// --- КРОК 3: "Tabs switcher" (Головні вкладки) ---
document.addEventListener("DOMContentLoaded", function() {
    const tabs = document.querySelectorAll('#adminTabs .tab-btn');
    const panels = {
        teachers: document.getElementById('panel-teachers'),
        heads: document.getElementById('panel-heads'),
        admins: document.getElementById('panel-admins'),
        journals: document.getElementById('panel-journals'),
        kids: document.getElementById('panel-kids'),
        calendar: document.getElementById('panel-calendar')
    };

    function activate(name){
        if (!panels[name]) return;
        tabs.forEach(b=>b.classList.toggle('active', b.dataset.tab===name));
        Object.keys(panels).forEach(k=>{
            if (panels[k]) panels[k].classList.toggle('active', k===name);
        });
        try { history.replaceState(null, '', location.pathname + '#' + name); } catch (e) {}

        if (name === 'journals') {
            const firstDeptButton = document.querySelector('#panel-journals .dept-pill-btn');
            if (firstDeptButton) {
                const anyActive = document.querySelector('#panel-journals .dept-pill-btn.active');
                if (!anyActive) {
                    activateJournalDept(firstDeptButton.dataset.deptId);
                }
            }
        }
    }
    tabs.forEach(btn=>btn.addEventListener('click',()=>activate(btn.dataset.tab)));

    const fromHash = location.hash?.replace('#','');
    if(fromHash && panels[fromHash]) {
        activate(fromHash);
    } else {
        activate('teachers');
    }

    // --- КРОК 4: Логіка для форм додавання ---
    const map = [
        ['showAddTeacherForm', 'addTeacherForm'],
        ['showAddHeadForm', 'addHeadForm'],
        ['showAddAdminForm', 'addAdminForm']
    ];
    map.forEach(([btnId, formId])=>{
        const btn = document.getElementById(btnId);
        const form = document.getElementById(formId);
        if(btn && form){ btn.addEventListener('click', ()=>{
            form.style.display = form.style.display==='none' || !form.style.display ? 'block' : 'none';
        }); }
    });
});