/**
 * Homepage Schedule card.
 *
 * The server renders everything that depends only on dates — the header, the
 * legend, the label column, the countdown pills and the footer templates. This
 * script owns everything that depends on scale, because scale is the one thing
 * the server cannot know: pixels-per-day comes from the measured width of the
 * track divided by the zoom stop.
 *
 * Deliberately ES5, like the other files in the homepage redesign
 * (dashboard.js, timeline-phases.js, global-unit-switcher.js). There is no
 * transpiler for webapp JavaScript.
 */
(function () {
  'use strict';

  var DAY = 86400000;
  var ZOOMS = [2, 4, 8, 16];
  var LANES = 3;

  /* Pixel constants. These are the only absolute lengths in the packer, which
     is why overflow grows as the window widens: a 16-week view compresses the
     date axis but not the 8px gutter or the room a milestone label needs. */
  var GUTTER = 8;
  var MIN_W = 6;
  var MILESTONE_W = 178;
  var MILESTONE_TAIL = 170;
  var CHIP_GAP = 190;

  var STORAGE_KEY = 'marlo.schedule.weeks';

  /* ---- Dates ---- */

  /* Bare "yyyy-MM-dd" through new Date() is parsed as UTC, which lands on the
     previous day everywhere west of Greenwich. Split it instead. */
  function parseDay(text) {
    var parts = String(text).split('-');
    return new Date(+parts[0], +parts[1] - 1, +parts[2]);
  }

  /* Whole days between two local midnights. Rounded because a DST boundary
     makes the interval 23 or 25 hours. */
  function daysBetween(from, to) {
    return Math.round((to.getTime() - from.getTime()) / DAY);
  }

  function addDays(date, count) {
    return new Date(date.getFullYear(), date.getMonth(), date.getDate() + count);
  }

  /* Back up to the Monday of this date's week, so week gridlines and their
     labels fall on real week boundaries. */
  function startOfWeek(date) {
    var weekday = (date.getDay() + 6) % 7;
    return addDays(date, -weekday);
  }

  function pad2(value) {
    return (value < 10 ? '0' : '') + value;
  }

  /* ---- Element helpers ---- */

  function el(tag, className) {
    var node = document.createElement(tag);
    if (className) {
      node.className = className;
    }
    return node;
  }

  function clear(node) {
    while (node.firstChild) {
      node.removeChild(node.firstChild);
    }
  }

  function format(template, values) {
    var out = String(template || '');
    for (var i = 0; i < values.length; i++) {
      out = out.replace('{' + i + '}', values[i]);
    }
    return out;
  }

  document.addEventListener('DOMContentLoaded', function () {
    var card = document.getElementById('scheduleCard');
    if (!card) {
      return;
    }

    var payload;
    try {
      payload = JSON.parse(card.getAttribute('data-schedule') || '{}');
    } catch (e) {
      return;
    }
    if (!payload.today) {
      return;
    }

    var scroll = document.getElementById('scheduleScroll');
    var canvas = document.getElementById('scheduleCanvas');
    var axis = document.getElementById('scheduleAxis');
    var gridLayer = document.getElementById('scheduleGrid');
    var nowLayer = document.getElementById('scheduleNow');
    var overflowTrack = document.getElementById('scheduleOverflowTrack');
    var overflowCount = document.getElementById('scheduleOverflowCount');
    var footLeft = document.getElementById('scheduleFootLeft');
    var footRight = document.getElementById('scheduleFootRight');
    var viewLabel = document.getElementById('scheduleViewLabel');
    var jump = document.getElementById('scheduleJump');

    /* Wired before the guard below: in the zero-open-phases state this button
       is the only interactive thing on the card, and the guard returns. Closed
       phases are browsed in the phase selector's own popover rather than
       duplicated inside the card. */
    var browseClosed = document.getElementById('scheduleBrowseClosed');
    if (browseClosed) {
      browseClosed.addEventListener('click', function () {
        var selectorToggle = document.getElementById('allPhasesToggle');
        if (!selectorToggle) {
          return;
        }
        selectorToggle.scrollIntoView({ block: 'center' });
        if (selectorToggle.getAttribute('aria-expanded') !== 'true') {
          selectorToggle.click();
        }
      });
    }

    /* Without the scroll frame there is nothing to draw into — the card is in
       its zero-open-phases state, which is entirely server-rendered. */
    if (!scroll || !canvas || !axis || !gridLayer || !nowLayer || !overflowTrack || !overflowCount) {
      return;
    }

    var months = payload.months || [];
    var today = parseDay(payload.today);

    var phaseTracks = card.querySelectorAll('[data-phase-track]');
    var laneTracks = card.querySelectorAll('[data-lane]');
    var laneCounts = card.querySelectorAll('[data-lane-count]');

    var statusLabels = {
      notStarted: card.getAttribute('data-label-notstarted') || '',
      inProgress: card.getAttribute('data-label-inprogress') || '',
      completed: card.getAttribute('data-label-completed') || '',
      upcoming: card.getAttribute('data-label-upcoming') || ''
    };
    var tplItem = card.getAttribute('data-tpl-item') || '{0}. {1}. {2}.';
    var tplMore = card.getAttribute('data-tpl-more') || '{0} more';

    /* ---- Model ---- */

    var phases = [];
    var i;
    for (i = 0; i < (payload.phases || []).length; i++) {
      var rawPhase = payload.phases[i];
      phases.push({
        name: rawPhase.name,
        status: rawPhase.status,
        dates: rawPhase.dates,
        start: parseDay(rawPhase.start),
        end: parseDay(rawPhase.end)
      });
    }

    var activities = [];
    for (i = 0; i < (payload.activities || []).length; i++) {
      var rawItem = payload.activities[i];
      var start = parseDay(rawItem.start);
      var end = parseDay(rawItem.end);
      /* An activity with the dates the wrong way round would otherwise render
         as a negative-width pill. */
      if (end.getTime() < start.getTime()) {
        var swap = start;
        start = end;
        end = swap;
      }
      activities.push({
        id: rawItem.id,
        name: rawItem.name,
        dates: rawItem.dates,
        order: rawItem.order === null || rawItem.order === undefined ? Infinity : rawItem.order,
        start: start,
        end: end,
        milestone: start.getTime() === end.getTime(),
        status: end.getTime() < today.getTime()
          ? 'completed'
          : (start.getTime() > today.getTime() ? 'notStarted' : 'inProgress')
      });
    }

    /* ---- Rendered range ---- */
    /* Spans every phase and activity plus a week of air either side, snapped to
       week boundaries, and always includes today so the marker has somewhere to
       sit. A pathological outlier date is clamped rather than allowed to
       stretch the track to nothing. */
    var CLAMP_DAYS = 550;
    var earliest = today;
    var latest = today;
    var all = phases.concat(activities);
    for (i = 0; i < all.length; i++) {
      if (all[i].start.getTime() < earliest.getTime()) {
        earliest = all[i].start;
      }
      if (all[i].end.getTime() > latest.getTime()) {
        latest = all[i].end;
      }
    }
    var clampFrom = addDays(today, -CLAMP_DAYS);
    var clampTo = addDays(today, CLAMP_DAYS);
    if (earliest.getTime() < clampFrom.getTime()) {
      earliest = clampFrom;
    }
    if (latest.getTime() > clampTo.getTime()) {
      latest = clampTo;
    }
    var rangeStart = startOfWeek(addDays(earliest, -7));
    var rangeEnd = startOfWeek(addDays(latest, 13));
    var totalDays = daysBetween(rangeStart, rangeEnd);
    var todayDay = daysBetween(rangeStart, today);

    function dayIndex(date) {
      return daysBetween(rangeStart, date);
    }

    function formatDay(date) {
      return pad2(date.getDate()) + ' ' + (months[date.getMonth()] || '');
    }

    function formatDayYear(date) {
      return formatDay(date) + ' ' + date.getFullYear();
    }

    /* ---- Zoom state ---- */

    var weeks = 8;
    try {
      var stored = parseInt(window.localStorage.getItem(STORAGE_KEY), 10);
      if (ZOOMS.indexOf(stored) !== -1) {
        weeks = stored;
      }
    } catch (e) {
      // Private mode or blocked storage: keep the 8-week default.
    }

    var zoomButtons = card.querySelectorAll('[data-weeks]');
    var labelWidth = 276;
    var trackWidth = 0;
    var pxPerDay = 0;
    var contentWidth = 0;

    function measure() {
      var raw = window.getComputedStyle(card).getPropertyValue('--sched-label');
      var parsed = parseFloat(raw);
      labelWidth = isNaN(parsed) ? 276 : parsed;
      /* The visible track is the card interior minus the sticky label column.
         Measured, never assumed: .container drops to 95% below 1300px, so no
         fixed number survives every viewport. */
      trackWidth = Math.max(120, scroll.clientWidth - labelWidth);
      pxPerDay = trackWidth / (weeks * 7);
      contentWidth = totalDays * pxPerDay;
    }

    function centreOnToday() {
      scroll.scrollLeft = Math.max(0, todayDay * pxPerDay - trackWidth / 2);
    }

    /* ---- Packing ---- */
    /* Greedy first-fit over three lanes. Whatever does not fit becomes an
       overflow chip; the lane count never changes, so the card cannot grow. */
    function packLanes() {
      var lanes = [];
      var ends = [];
      var overflow = [];
      var index;
      for (index = 0; index < LANES; index++) {
        lanes.push([]);
        ends.push(-Infinity);
      }

      var sorted = activities.slice().sort(function (a, b) {
        var byStart = a.start.getTime() - b.start.getTime();
        if (byStart !== 0) {
          return byStart;
        }
        if (a.order !== b.order) {
          return a.order < b.order ? -1 : 1;
        }
        return a.id - b.id;
      });

      for (var k = 0; k < sorted.length; k++) {
        var item = sorted[k];
        var rawLeft = dayIndex(item.start) * pxPerDay;
        var left = Math.max(0, rawLeft);
        var width = item.milestone
          ? MILESTONE_W
          : Math.max(MIN_W, (dayIndex(item.end) + 1) * pxPerDay - left);
        var need = left - GUTTER;
        var tail = left + (item.milestone ? MILESTONE_TAIL : width);

        var placed = -1;
        for (index = 0; index < LANES; index++) {
          if (ends[index] <= need) {
            placed = index;
            break;
          }
        }

        if (placed < 0) {
          overflow.push({ left: left, item: item });
          continue;
        }

        ends[placed] = tail;
        var capped = Math.max(MIN_W, Math.min(width, contentWidth - left - 2));
        lanes[placed].push({
          left: left,
          width: capped,
          clipStart: rawLeft < -0.5,
          clipEnd: capped < width - 0.5,
          item: item
        });
      }

      /* Merge neighbouring overflow into one chip so a busy fortnight does not
         produce a row of touching "+1 more" buttons. */
      var chips = [];
      overflow.sort(function (a, b) {
        return a.left - b.left;
      });
      for (var c = 0; c < overflow.length; c++) {
        var last = chips.length ? chips[chips.length - 1] : null;
        if (last && overflow[c].left - last.left < CHIP_GAP) {
          last.items.push(overflow[c].item);
        } else {
          chips.push({ left: overflow[c].left, items: [overflow[c].item] });
        }
      }

      return { lanes: lanes, chips: chips, overflowTotal: overflow.length };
    }

    /* ---- Painting ---- */

    /* The visible label is truncated with an ellipsis, so the full name, the
       date range and the status all have to reach assistive tech another way. */
    function accessibleName(item) {
      return format(tplItem, [item.name, item.dates, statusLabels[item.status]]);
    }

    function srOnly(text) {
      var node = el('span', 'sr-only');
      node.textContent = text;
      return node;
    }

    function paintAxis() {
      clear(axis);
      clear(gridLayer);
      clear(nowLayer);

      var weekly = weeks <= 4;
      var cursor = new Date(rangeStart.getTime());

      /* Week boundaries: a gridline always, a label only when zoomed in far
         enough for one to fit. */
      while (dayIndex(cursor) <= totalDays) {
        var offset = dayIndex(cursor) * pxPerDay;
        var isMonthStart = cursor.getDate() <= 7 && weekly;
        var line = el('div', 'scheduleCard__gridline');
        line.style.left = offset + 'px';
        gridLayer.appendChild(line);

        if (weekly) {
          var weekTick = el('span', 'scheduleCard__tick' + (isMonthStart ? ' scheduleCard__tick--month' : ''));
          weekTick.textContent = formatDay(cursor);
          weekTick.style.left = offset + 'px';
          /* A visual scale only. Left exposed, a 16-week range reads as dozens of
             bare date fragments before the first phase row; every bar and pill
             already carries its own dates. */
          weekTick.setAttribute('aria-hidden', 'true');
          axis.appendChild(weekTick);
        }
        cursor = addDays(cursor, 7);
      }

      /* Month starts: a stronger gridline in every zoom, and the only labels
         when the window is 8 weeks or wider. */
      var month = new Date(rangeStart.getFullYear(), rangeStart.getMonth(), 1);
      if (month.getTime() < rangeStart.getTime()) {
        month = new Date(rangeStart.getFullYear(), rangeStart.getMonth() + 1, 1);
      }
      while (dayIndex(month) <= totalDays) {
        var monthOffset = dayIndex(month) * pxPerDay;
        var monthLine = el('div', 'scheduleCard__gridline scheduleCard__gridline--month');
        monthLine.style.left = monthOffset + 'px';
        gridLayer.appendChild(monthLine);

        if (!weekly) {
          var monthTick = el('span', 'scheduleCard__tick scheduleCard__tick--month');
          monthTick.textContent = (months[month.getMonth()] || '').toUpperCase();
          monthTick.style.left = monthOffset + 'px';
          monthTick.setAttribute('aria-hidden', 'true');
          axis.appendChild(monthTick);
        }
        month = new Date(month.getFullYear(), month.getMonth() + 1, 1);
      }

      /* Today: one hairline across every lane, and its badge on the axis. The
         two share an offset so they cannot drift apart. */
      if (todayDay >= 0 && todayDay <= totalDays) {
        var todayOffset = todayDay * pxPerDay;
        var now = el('div', 'scheduleCard__now');
        now.style.left = todayOffset + 'px';
        nowLayer.appendChild(now);

        var tag = el('span', 'scheduleCard__todayTag');
        tag.textContent = card.getAttribute('data-label-today') || '';
        tag.style.left = todayOffset + 'px';
        axis.appendChild(tag);
      }
    }

    function paintPhases() {
      for (var p = 0; p < phaseTracks.length; p++) {
        var track = phaseTracks[p];
        clear(track);
        var phase = phases[+track.getAttribute('data-phase-track')];
        if (!phase) {
          continue;
        }

        var rawLeft = dayIndex(phase.start) * pxPerDay;
        var left = Math.max(0, rawLeft);
        var right = Math.min(contentWidth, (dayIndex(phase.end) + 1) * pxPerDay);
        var width = right - left;
        if (width < MIN_W) {
          width = MIN_W;
        }

        var bar = el('div', 'scheduleCard__bar scheduleCard__bar--' + phase.status);
        if (rawLeft < -0.5) {
          bar.className += ' scheduleCard__bar--clipStart';
        }
        if ((dayIndex(phase.end) + 1) * pxPerDay > contentWidth + 0.5) {
          bar.className += ' scheduleCard__bar--clipEnd';
        }
        bar.style.left = left + 'px';
        bar.style.width = width + 'px';

        var label = el('span');
        label.textContent = phase.name + ' · ' + statusLabels[phase.status];
        bar.appendChild(label);
        bar.appendChild(srOnly(accessibleName(phase)));
        bar.setAttribute('title', phase.name + ' · ' + phase.dates + ' · ' + statusLabels[phase.status]);
        track.appendChild(bar);
      }
    }

    function paintActivities(packed) {
      var placedTotal = 0;
      var lane;
      for (lane = 0; lane < laneTracks.length; lane++) {
        var track = laneTracks[lane];
        clear(track);
        var bucket = packed.lanes[+track.getAttribute('data-lane')] || [];
        placedTotal += bucket.length;

        for (var b = 0; b < bucket.length; b++) {
          var slot = bucket[b];
          var item = slot.item;
          var pill = el('div', 'scheduleCard__pill scheduleCard__pill--' +
            (item.milestone ? 'milestone' : item.status));
          if (slot.clipStart) {
            pill.className += ' scheduleCard__pill--clipStart';
          }
          if (slot.clipEnd) {
            pill.className += ' scheduleCard__pill--clipEnd';
          }
          pill.style.left = slot.left + 'px';
          pill.style.width = slot.width + 'px';

          var text = el('span');
          text.textContent = item.name;
          pill.appendChild(text);
          pill.appendChild(srOnly(accessibleName(item)));
          pill.setAttribute('title', item.name + ' · ' + item.dates + ' · ' + statusLabels[item.status]);
          track.appendChild(pill);
        }
      }

      for (lane = 0; lane < laneCounts.length; lane++) {
        var counted = packed.lanes[+laneCounts[lane].getAttribute('data-lane-count')] || [];
        laneCounts[lane].textContent = String(counted.length);
      }

      return placedTotal;
    }

    function paintOverflow(packed) {
      clear(overflowTrack);
      closePopover();
      overflowCount.textContent = String(packed.overflowTotal);

      for (var c = 0; c < packed.chips.length; c++) {
        var chip = packed.chips[c];
        var button = el('button', 'scheduleCard__chip');
        button.type = 'button';
        button.setAttribute('aria-expanded', 'false');
        button.setAttribute('aria-haspopup', 'dialog');
        button.style.left = chip.left + 'px';
        button.textContent = format(tplMore, [chip.items.length]);
        button.chipItems = chip.items;
        overflowTrack.appendChild(button);
      }
    }

    function paintFoot(placedTotal) {
      if (viewLabel) {
        viewLabel.textContent = format(viewLabel.getAttribute('data-label'), [weeks]);
      }
      if (footLeft) {
        footLeft.textContent = format(footLeft.getAttribute('data-window'), [weeks]) +
          ' · ' +
          format(footLeft.getAttribute('data-span'), [
            formatDayYear(rangeStart),
            formatDayYear(addDays(rangeEnd, -1))
          ]);
      }
      if (footRight) {
        var total = activities.length;
        var placedText = placedTotal === total
          ? format(footRight.getAttribute('data-all-placed'), [total, LANES])
          : format(footRight.getAttribute('data-placed'), [placedTotal, total, LANES]);
        footRight.textContent = placedText + ' · ' + footRight.getAttribute('data-hint');
      }
    }

    function render() {
      measure();

      for (var t = 0; t < phaseTracks.length; t++) {
        phaseTracks[t].style.width = contentWidth + 'px';
      }
      for (var l = 0; l < laneTracks.length; l++) {
        laneTracks[l].style.width = contentWidth + 'px';
      }
      overflowTrack.style.width = contentWidth + 'px';
      axis.style.width = contentWidth + 'px';
      var sectionTracks = card.querySelectorAll('[data-section-track]');
      for (var s = 0; s < sectionTracks.length; s++) {
        sectionTracks[s].style.width = contentWidth + 'px';
      }

      paintAxis();
      paintPhases();
      var packed = packLanes();
      var placedTotal = paintActivities(packed);
      paintOverflow(packed);
      paintFoot(placedTotal);
    }

    /* ---- Overflow popover ---- */

    var popover = el('div', 'scheduleCard__popover');
    popover.setAttribute('role', 'dialog');
    popover.hidden = true;
    var popoverTitle = el('span', 'scheduleCard__popoverTitle');
    popoverTitle.textContent = card.getAttribute('data-label-overflow') || '';
    var popoverList = el('ul', 'scheduleCard__popoverList');
    popover.appendChild(popoverTitle);
    popover.appendChild(popoverList);
    card.appendChild(popover);
    popover.setAttribute('aria-label', popoverTitle.textContent);

    var openChip = null;

    function closePopover() {
      if (openChip) {
        openChip.setAttribute('aria-expanded', 'false');
        openChip = null;
      }
      popover.hidden = true;
    }

    function openPopover(chip) {
      clear(popoverList);
      var items = chip.chipItems || [];
      for (var n = 0; n < items.length; n++) {
        var row = el('li');
        row.appendChild(el('span', 'scheduleCard__popoverDot scheduleCard__popoverDot--' + items[n].status));
        var body = el('span');
        var name = el('span');
        name.textContent = items[n].name;
        var dates = el('span', 'scheduleCard__popoverDates');
        dates.textContent = items[n].dates + ' · ' + statusLabels[items[n].status];
        body.appendChild(name);
        body.appendChild(dates);
        row.appendChild(body);
        popoverList.appendChild(row);
      }

      popover.hidden = false;
      var chipBox = chip.getBoundingClientRect();
      var cardBox = card.getBoundingClientRect();
      var left = chipBox.left - cardBox.left;
      var maxLeft = card.clientWidth - popover.offsetWidth;
      popover.style.left = Math.max(0, Math.min(left, maxLeft)) + 'px';
      popover.style.top = (chipBox.bottom - cardBox.top + 6) + 'px';

      chip.setAttribute('aria-expanded', 'true');
      openChip = chip;
    }

    overflowTrack.addEventListener('click', function (event) {
      var chip = event.target.closest('.scheduleCard__chip');
      if (!chip) {
        return;
      }
      if (openChip === chip) {
        closePopover();
      } else {
        closePopover();
        openPopover(chip);
      }
    });

    document.addEventListener('click', function (event) {
      if (openChip && !popover.contains(event.target) && !openChip.contains(event.target)) {
        closePopover();
      }
    });

    document.addEventListener('keydown', function (event) {
      if (event.key === 'Escape' && openChip) {
        var chip = openChip;
        closePopover();
        chip.focus();
      }
    });

    /* ---- Zoom ---- */

    function setWeeks(next, recentre) {
      if (ZOOMS.indexOf(next) === -1 || next === weeks) {
        return;
      }
      weeks = next;
      try {
        window.localStorage.setItem(STORAGE_KEY, String(weeks));
      } catch (e) {
        // The zoom still applies to this page view.
      }
      syncZoomButtons();
      render();
      if (recentre !== false) {
        centreOnToday();
      }
    }

    function syncZoomButtons() {
      for (var z = 0; z < zoomButtons.length; z++) {
        var pressed = +zoomButtons[z].getAttribute('data-weeks') === weeks;
        zoomButtons[z].setAttribute('aria-pressed', String(pressed));
      }
    }

    for (i = 0; i < zoomButtons.length; i++) {
      zoomButtons[i].addEventListener('click', function () {
        setWeeks(+this.getAttribute('data-weeks'));
      });
    }

    if (jump) {
      jump.addEventListener('click', function () {
        centreOnToday();
      });
    }

    /* Modifier + wheel steps through the zoom stops. A plain wheel is left
       alone so the page keeps scrolling normally; a trackpad's sideways
       gesture and shift+wheel already scroll the track natively. */
    scroll.addEventListener('wheel', function (event) {
      if (!event.ctrlKey && !event.metaKey) {
        return;
      }
      event.preventDefault();
      var at = ZOOMS.indexOf(weeks);
      var step = event.deltaY > 0 ? 1 : -1;
      var target = ZOOMS[Math.min(ZOOMS.length - 1, Math.max(0, at + step))];
      setWeeks(target);
    }, { passive: false });

    /* ---- Reflow ---- */

    var resizeTimer = null;
    function scheduleReflow() {
      if (resizeTimer) {
        window.clearTimeout(resizeTimer);
      }
      resizeTimer = window.setTimeout(function () {
        resizeTimer = null;
        var anchor = scroll.scrollLeft + trackWidth / 2;
        var anchorDay = pxPerDay ? anchor / pxPerDay : todayDay;
        render();
        scroll.scrollLeft = Math.max(0, anchorDay * pxPerDay - trackWidth / 2);
      }, 120);
    }

    if (window.ResizeObserver) {
      new window.ResizeObserver(scheduleReflow).observe(scroll);
    } else {
      window.addEventListener('resize', scheduleReflow);
    }

    /* ---- Boot ---- */

    syncZoomButtons();
    render();
    centreOnToday();
  });
})();
