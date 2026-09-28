/*
 * 현재 URL 에 해당하는 사이드바 메뉴에 active 클래스를 붙인다.
 *
 * Mazer 번들(assets/compiled/js/app.js)은 사이드바 초기화 때
 *   - document.querySelector(".sidebar-item.active") 를 화면 안으로 스크롤하고(forceElementVisibility)
 *   - active 인 .sidebar-item.has-sub 의 하위 메뉴를 펼친다(submenu-open)
 * active 항목이 하나도 없으면 null.getBoundingClientRect() 로 모든 페이지에서 TypeError 가 났다.
 * 그래서 app.js 보다 먼저(동기 로드) 이 스크립트를 실행해 항상 active 항목 하나를 정한다.
 *
 * 매칭 규칙: 메뉴 href 와 현재 경로의 앞쪽 경로 세그먼트가 가장 많이 겹치는 항목
 * (예: /admin/reservations/15 → "/admin/reservations/my" 의 "예약 관리" 그룹). 동점이면 먼저 나온 항목.
 * 겹치는 항목이 없으면 첫 번째 메뉴(대시보드)를 active 로 둔다.
 */
(function () {
    var sidebar = document.getElementById('sidebar');
    if (!sidebar) return;

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
