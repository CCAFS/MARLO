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

  /* Removal has to go through jQuery when it is loaded, not just removeChild.
     global.js binds jQuery UI's tooltip widget inside $(document).ready, so the
     widget element IS document and every bar and pill is a *delegated* target.
     For those, the widget tears an open tooltip down from a `remove` handler on
     the target, and that handler is only reached via jQuery UI's $.cleanData
     override -- which jQuery calls from .empty()/.remove() and never from native
     removeChild. Repainting while the cursor sits on a bar, which Cmd/Ctrl +
     wheel zoom guarantees, therefore orphaned the tooltip div in <body>, where
     it stayed stuck on screen. Going through jQuery lets the widget clean up
     after itself and also releases its data on the discarded nodes. The native
     loop remains, both as the no-jQuery fallback and to catch anything left. */
  function clear(node) {
    var jq = window.jQuery;
    if (jq && node.firstChild) {
      jq(node).empty();
    }
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
    var zoomStatus = document.getElementById('scheduleZoomStatus');
    var jump = document.getElementById('scheduleJump');
    /* The popover is positioned against the timeline column rather than the
       whole card: with the "what's next" panel beside it, the card is wider
       than the track and clamping to the card would let the popover drift over
       the panel. */
    var main = document.getElementById('scheduleMain');

    /* Without the scroll frame there is nothing to draw into. Only the elements
       actually drawn into belong in this guard: the label column's readouts
       (#scheduleOverflowCount, #scheduleViewLabel, [data-lane-count]) are
       optional chrome, and requiring them here would blank the whole card the
       moment that column is removed. */
    if (!scroll || !canvas || !axis || !gridLayer || !nowLayer || !overflowTrack) {
      return;
    }

    var months = payload.months || [];
    var today = parseDay(payload.today);

    var laneTracks = card.querySelectorAll('[data-lane]');
    var laneCounts = card.querySelectorAll('[data-lane-count]');

    var statusLabels = {
      notStarted: card.getAttribute('data-label-notstarted') || '',
      inProgress: card.getAttribute('data-label-inprogress') || '',
      completed: card.getAttribute('data-label-completed') || ''
    };
    var tplItem = card.getAttribute('data-tpl-item') || '{0}. {1}. {2}.';
    var tplMore = card.getAttribute('data-tpl-more') || '{0} more';

    /* ---- Model ---- */

    var activities = [];
    /* Shared by the loops below. It used to be declared with the phase
       normalisation that lived here; the file is 'use strict', so leaving it
       implicit would be a ReferenceError, not a global. */
    var i;
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
    /* Activities only: the reporting-phases lanes were removed from the
       timeline, so nothing else contributes to the rendered span. today is
       already the seed for earliest/latest, so an empty list still renders. */
    var all = activities;
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

    /* The edge masks live on the frame, which is one box wider and taller than
       the scroll viewport whenever a classic scrollbar is present. Handing the
       measured thickness to CSS keeps them off the bars; overlay scrollbars
       measure 0, which is exactly the fallback. Must run after the track widths
       are set, because that is what decides whether a bar exists at all. */
    function measureScrollbars() {
      var hbar = scroll.offsetHeight - scroll.clientHeight;
      var vbar = scroll.offsetWidth - scroll.clientWidth;
      card.style.setProperty('--sched-hbar', (hbar > 0 ? hbar : 0) + 'px');
      card.style.setProperty('--sched-vbar', (vbar > 0 ? vbar : 0) + 'px');
    }

    function centreOnToday() {
      scroll.scrollLeft = Math.max(0, todayDay * pxPerDay - trackWidth / 2);
    }

    /* Track offsets, not canvas offsets: the label column is sticky, so it
       overlays the first labelWidth pixels of the viewport and day 0 sits at
       scrollLeft 0. Both helpers share that convention with centreOnToday. */
    function dayAtTrackOffset(offset) {
      return pxPerDay ? (scroll.scrollLeft + offset) / pxPerDay : todayDay;
    }

    /* Re-render at the current scale while keeping anchorDay in the same place
       in the viewport. Anything that changes pxPerDay -- a zoom, a resize --
       would otherwise leave the user looking at a different date than the one
       they were reading.

       afterOffset is where anchorDay should land AFTER the render, and that is
       not always where it was before: a zoom leaves trackWidth alone, but a
       resize changes it, so "keep it centred" means the new half-width, not the
       old one. Omit the argument for that case; pass an explicit offset only
       when it is tied to something that did not move, such as the pointer. */
    function renderAnchored(anchorDay, afterOffset) {
      render();
      var offset = typeof afterOffset === 'number' ? afterOffset : trackWidth / 2;
      scroll.scrollLeft = Math.max(0, anchorDay * pxPerDay - offset);
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
          /* Status and shape are independent: a single-day activity is still
             completed, running or not started. Using --milestone *instead of*
             the status class left every milestone with no fill, no border
             colour and no status dot, so a finished one read as blank. */
          var pill = el('div', 'scheduleCard__pill scheduleCard__pill--' + item.status +
            (item.milestone ? ' scheduleCard__pill--milestone' : ''));
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
      if (overflowCount) {
        overflowCount.textContent = String(packed.overflowTotal);
      }

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
      var packed = packLanes();
      var placedTotal = paintActivities(packed);
      paintOverflow(packed);
      paintFoot(placedTotal);
      measureScrollbars();
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
    var popoverHost = main || card;
    popoverHost.appendChild(popover);
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
      var hostBox = popoverHost.getBoundingClientRect();
      var left = chipBox.left - hostBox.left;
      var maxLeft = popoverHost.clientWidth - popover.offsetWidth;
      popover.style.left = Math.max(0, Math.min(left, maxLeft)) + 'px';
      popover.style.top = (chipBox.bottom - hostBox.top + 6) + 'px';

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

    /* anchorOffset is where in the visible track the date being read should
       stay put; it defaults to the middle. Zooming does NOT return to today --
       that would throw away the user's position every time they changed scale,
       and the "Today" button is there for when they do want to go back. */
    function setWeeks(next, anchorOffset) {
      if (ZOOMS.indexOf(next) === -1 || next === weeks) {
        return;
      }
      /* trackWidth depends only on the measured viewport, never on the zoom
         stop, so the same offset is valid before and after the change. */
      var offset = typeof anchorOffset === 'number' ? anchorOffset : trackWidth / 2;
      var anchorDay = dayAtTrackOffset(offset);
      weeks = next;
      try {
        window.localStorage.setItem(STORAGE_KEY, String(weeks));
      } catch (e) {
        // The zoom still applies to this page view.
      }
      syncZoomButtons();
      renderAnchored(anchorDay, offset);
      announceWindow();
    }

    /* aria-pressed tells a screen reader which stop is selected, but only when
       the user goes looking for it: the zoom changes no text and moves no focus,
       so nothing is spoken and there is no confirmation that the visible window
       changed. This pushes the window that resulted into a polite live region.

       Debounced because Cmd/Ctrl + wheel walks the stops one notch per event.
       Without the delay a single gesture queues an announcement per notch and
       the user hears the windows they passed through rather than the one they
       settled on. Clicks go through the same path, which also collapses a burst
       of rapid clicks down to the stop that stuck. */
    var ANNOUNCE_DELAY = 400;
    var announceTimer = null;

    function announceWindow() {
      if (!zoomStatus) {
        return;
      }
      if (announceTimer) {
        window.clearTimeout(announceTimer);
      }
      announceTimer = window.setTimeout(function () {
        announceTimer = null;
        var lastDay = Math.max(0, totalDays - 1);
        /* The visible track is exactly weeks * 7 days wide by construction --
           pxPerDay is trackWidth / (weeks * 7) -- but it is clamped to the
           rendered range: scrolled hard against either end the window is
           shorter than the stop, and reporting the stop's own arithmetic would
           name a date the track never shows. */
        var fromDay = Math.min(lastDay, Math.max(0, Math.round(dayAtTrackOffset(0))));
        var toDay = Math.min(lastDay, fromDay + weeks * 7 - 1);
        zoomStatus.textContent = format(zoomStatus.getAttribute('data-announcement'), [
          weeks,
          formatDayYear(addDays(rangeStart, fromDay)),
          formatDayYear(addDays(rangeStart, toDay))
        ]);
      }, ANNOUNCE_DELAY);
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
      /* Zoom about the pointer, the way every map does: the date under the
         cursor is the one the user is pointing at. Over the label column the
         offset goes negative, so fall back to the centre. */
      var pointer = event.clientX - scroll.getBoundingClientRect().left - labelWidth;
      setWeeks(target, pointer >= 0 && pointer <= trackWidth ? pointer : trackWidth / 2);
    }, { passive: false });

    /* ---- Reflow ---- */

    var resizeTimer = null;
    function scheduleReflow() {
      if (resizeTimer) {
        window.clearTimeout(resizeTimer);
      }
      resizeTimer = window.setTimeout(function () {
        resizeTimer = null;
        /* Centre before, centre after -- each measured at its own trackWidth. */
        renderAnchored(dayAtTrackOffset(trackWidth / 2));
      }, 120);
    }

    if (window.ResizeObserver) {
      new window.ResizeObserver(scheduleReflow).observe(scroll);
      /* Whether a scrollbar exists depends on the canvas, not on the viewport,
         and the canvas can grow after boot without any resize -- a web font
         swapping in makes every row taller and can push it past max-height,
         adding a vertical bar that the observer above never sees because the
         scroll container's own height is capped. This second observer only
         re-measures the bars; that changes no geometry which affects layout, so
         it cannot feed itself. */
      new window.ResizeObserver(measureScrollbars).observe(canvas);
    } else {
      window.addEventListener('resize', scheduleReflow);
    }

    /* ---- Boot ---- */

    syncZoomButtons();
    render();
    centreOnToday();
  });
})();
