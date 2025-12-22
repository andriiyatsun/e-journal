document.addEventListener("DOMContentLoaded", function() {
    // JS для перемикання вкладок
    const tabsContainer = document.getElementById('journalTabs');
    if (!tabsContainer) return;

    const tabs = tabsContainer.querySelectorAll('.tab-btn');
    const panels = document.querySelectorAll('.tab-panel');

    function activate(tabName){
        if (!tabName) return;

        // Перемикання класу active на кнопках
        tabs.forEach(b => b.classList.toggle('active', b.dataset.tab === tabName));

        // Перемикання відображення панелей
        panels.forEach(p => p.classList.toggle('active', p.id === 'panel-' + tabName));

        // Збереження стану в URL (щоб працювало при перезавантаженні сторінки)
        try { history.replaceState(null, '', '#' + tabName); } catch (e) {}
    }

    tabs.forEach(btn => btn.addEventListener('click', (e) => {
        e.preventDefault();
        activate(btn.dataset.tab);
    }));

    // Відновлення активної вкладки з URL (хешу) або за замовчуванням 'info'
    const fromHash = location.hash?.replace('#','');
    activate(fromHash || 'info');
});