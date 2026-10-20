/*
 * 현재 URL 에 해당하는 사이드바 메뉴에 active 클래스를 붙인다.
 * Mazer app.js 는 active 항목이 없으면 초기화 중 TypeError 가 나므로 app.js 보다 먼저 실행한다.
 * 경로 세그먼트가 가장 많이 겹치는 메뉴를 고르고, 없으면 첫 번째 메뉴(대시보드)를 쓴다.
 */
(function () {
    var sidebar = document.getElementById('sidebar');
    if (!sidebar) return;

    // 1200px(Mazer 데스크톱 기준) 미만이면 사이드바를 접은 상태로 시작한다
    if (window.innerWidth < 1200) {
        sidebar.classList.remove('active');
    }

    function segments(path) {
        return (path || '').split('?')[0].split('#')[0].split('/').filter(function (s) { return s.length > 0; });
    }

    var current = segments(window.location.pathname);
    var best = null;
    var bestScore = 0;

    var links = sidebar.querySelectorAll('.sidebar-item > a.sidebar-link, .submenu-item > a');
    for (var i = 0; i < links.length; i++) {
        var href = links[i].getAttribute('href');
        if (!href || href.charAt(0) !== '/') continue; // 그룹 토글(href="#") 제외
        var target = segments(href);
        var score = 0;
        while (score < target.length && score < current.length && target[score] === current[score]) score++;
        // 정확히 같은 경로면 가산점 (예: /admin/rooms/list_all 과 /admin/rooms/add 구분)
        if (score === target.length && score === current.length) score += 0.5;
        if (score > bestScore) {
            bestScore = score;
            best = links[i];
        }
    }

    // /admin, /owner 같은 첫 세그먼트만 겹치는 경우는 매칭으로 보지 않는다
    if (!best || bestScore < 2) {
        var first = sidebar.querySelector('.sidebar-item');
        if (first) first.classList.add('active');
        return;
    }

    var subItem = best.closest('.submenu-item');
    if (subItem) subItem.classList.add('active');
    var item = best.closest('.sidebar-item');
    if (item) item.classList.add('active');
})();
