/**
 * Project / cluster deliverables list (A2-2607 redesign).
 *
 * deliverableList.ftl renders every deliverable of both sets (this phase and the
 * previous ones) as a row with its filter keys in data-* attributes. This script
 * owns what is shown: the active set, search, status chips, sub-category and year
 * filters, sorting and pagination. The view state is kept per
 * project in sessionStorage so coming back from a deliverable lands where it left.
 */
$(document).ready(function() {
  var $root = $('[data-dlv]');
  if(!$root.length) {
    return;
  }

  var STATUS_ORDER = ['c', 'o', 'e', 'r', 'x', ''];
  var STORAGE_KEY = 'dlv:' + $root.data('projectId');
  var $body = $root.find('[data-dlv-body]');
  var $chips = $root.find('[data-dlv-chips]');
  var $sub = $root.find('[data-dlv-sub]');
  var $year = $root.find('[data-dlv-year]');
  var $search = $root.find('[data-dlv-search]');
  var $size = $root.find('[data-dlv-size]');
  var $pager = $root.find('[data-dlv-pager]');

  var rows = $body.find('[data-dlv-row]').map(function() {
    var $row = $(this);
    return {
      $el: $row,
      set: $row.attr('data-set'),
      id: parseInt($row.attr('data-id'), 10) || 0,
      title: $row.attr('data-title') || '',
      status: $row.attr('data-status') || '',
      year: $row.attr('data-year') || '',
      sub: $row.attr('data-sub') || '',
      search: ($row.attr('data-search') || '').toLowerCase()
    };
  }).get();

  var defaults = {
      tab: 'current',
      q: '',
      status: 'all',
      sub: '',
      year: '',
      sort: {
          key: 'year',
          dir: 1
      },
      page: 1,
      size: 25
  };
  var state = $.extend(true, {}, defaults, readState());

  // Text with {0}, {1}... placeholders, as global.properties writes them
  function format(text, args) {
    return String(text || '').replace(/\{(\d+)\}/g, function(match, index) {
      return args[index] !== undefined ? args[index] : match;
    });
  }

  function readState() {
    try {
      return JSON.parse(window.sessionStorage.getItem(STORAGE_KEY)) || {};
    } catch(e) {
      return {};
    }
  }

  function saveState() {
    try {
      window.sessionStorage.setItem(STORAGE_KEY, JSON.stringify(state));
    } catch(e) {
      // Storage blocked (private mode): the list still works, it just does not remember
    }
  }

  function inSet(row) {
    return row.set === state.tab;
  }

  // Search, sub-category and year: the base the status chips count from
  function matchesBase(row) {
    var q = $.trim(state.q).toLowerCase();
    return inSet(row) && (!q || row.search.indexOf(q) !== -1) && (!state.sub || row.sub === state.sub)
      && (!state.year || row.year === state.year);
  }

  function compare(a, b) {
    var key = state.sort.key;
    var x, y;
    if(key === 'id') {
      x = a.id;
      y = b.id;
    } else if(key === 'title') {
      x = a.title;
      y = b.title;
    } else if(key === 'status') {
      x = STATUS_ORDER.indexOf(a.status);
      y = STATUS_ORDER.indexOf(b.status);
    } else {
      x = a.year;
      y = b.year;
    }
    if(x === y) {
      return (a.id - b.id) * state.sort.dir;
    }
    return (x > y ? 1 : -1) * state.sort.dir;
  }

  function uniqueSorted(values) {
    return $.grep(values, function(value, index) {
      return value && $.inArray(value, values) === index;
    }).sort();
  }

  // Options follow the active set; a stale choice from the other set is dropped
  function fillSelect($select, values, current) {
    $select.find('option').not(':first').remove();
    $.each(values, function(i, value) {
      $('<option>').val(value).text(value).appendTo($select);
    });
    if($.inArray(current, values) === -1) {
      current = '';
    }
    $select.val(current);
    return current;
  }

  function renderChips(base) {
    var counts = {
      all: base.length
    };
    $.each(base, function(i, row) {
      counts[row.status] = (counts[row.status] || 0) + 1;
    });
    $chips.empty();
    $.each(['all'].concat(STATUS_ORDER), function(i, key) {
      if(key !== 'all' && !counts[key] && state.status !== key) {
        return;
      }
      var isOn = state.status === key;
      var label = $chips.attr('data-label-' + (key === 'all' ? 'all' : (key || 'none')));
      var $chip = $('<button type="button" class="dlv-chip">').toggleClass('is-on', isOn).attr('aria-pressed', isOn)
        .attr('data-dlv-status', key);
      if(key !== 'all') {
        $('<span class="dlv-dot">').addClass('dlv-dot--' + (key || 'none')).appendTo($chip);
      }
      $chip.append(document.createTextNode(label));
      $('<span class="dlv-chip__count">').text(counts[key] || 0).appendTo($chip);
      $chips.append($chip);
    });
  }

  function renderPager(pages, page) {
    var $prev = $('<button type="button" class="dlv-pager__btn" data-dlv-page="prev">')
      .attr('aria-label', $pager.attr('data-label-prev')).prop('disabled', page <= 1)
      .html('<svg width="10" height="10" viewBox="0 0 12 12" fill="none" aria-hidden="true"><path d="M8 2.5 4.5 6 8 9.5" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"></path></svg>');
    var $next = $('<button type="button" class="dlv-pager__btn" data-dlv-page="next">')
      .attr('aria-label', $pager.attr('data-label-next')).prop('disabled', page >= pages)
      .html('<svg width="10" height="10" viewBox="0 0 12 12" fill="none" aria-hidden="true"><path d="M4 2.5 7.5 6 4 9.5" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"></path></svg>');
    $pager.empty().append($prev);
    var last = 0;
    for(var n = 1; n <= pages; n++) {
      // First, last and the two around the current page; the rest collapse into a gap
      if(n !== 1 && n !== pages && Math.abs(n - page) > 1) {
        continue;
      }
      if(last && n - last > 1) {
        $pager.append('<span class="dlv-pager__gap" aria-hidden="true">…</span>');
      }
      var $btn = $('<button type="button" class="dlv-pager__btn">').attr('data-dlv-page', n).text(n);
      if(n === page) {
        $btn.attr('aria-current', 'page');
      }
      $pager.append($btn);
      last = n;
    }
    $pager.append($next);
  }

  function render() {
    var setRows = $.grep(rows, inSet);
    state.sub = fillSelect($sub, uniqueSorted($.map(setRows, function(row) {
      return row.sub;
    })), state.sub);
    state.year = fillSelect($year, uniqueSorted($.map(setRows, function(row) {
      return row.year;
    })), state.year);

    var base = $.grep(rows, matchesBase);
    var filtered = $.grep(base, function(row) {
      return state.status === 'all' || row.status === state.status;
    }).sort(compare);
    var pages = Math.max(1, Math.ceil(filtered.length / state.size));
    state.page = Math.min(Math.max(1, state.page), pages);
    var from = (state.page - 1) * state.size;
    var visible = filtered.slice(from, from + state.size);

    // Re-append in sorted order, then show only the current page
    $.each(rows, function(i, row) {
      row.$el.prop('hidden', true);
    });
    $.each(filtered, function(i, row) {
      $body.append(row.$el);
    });
    $.each(visible, function(i, row) {
      row.$el.prop('hidden', false);
    });

    // Tabs, set-only blocks and column headers
    $root.find('[data-dlv-tab]').each(function() {
      var isOn = $(this).attr('data-dlv-tab') === state.tab;
      $(this).toggleClass('is-on', isOn).attr('aria-selected', isOn);
    });
    $root.find('[data-dlv-only]').each(function() {
      $(this).prop('hidden', $(this).attr('data-dlv-only') !== state.tab);
    });
    $root.find('[data-dlv-sort-col]').each(function() {
      var key = $(this).attr('data-dlv-sort-col');
      $(this).attr('aria-sort', key === state.sort.key ? (state.sort.dir === 1 ? 'ascending' : 'descending') : 'none');
    });

    renderChips(base);

    // Empty states: nothing in the set at all, or nothing left after filtering
    var setEmpty = setRows.length === 0;
    $root.find('[data-dlv-none]').each(function() {
      $(this).prop('hidden', !setEmpty || $(this).attr('data-dlv-none') !== state.tab);
    });
    $root.find('[data-dlv-empty]').prop('hidden', setEmpty || filtered.length > 0);
    $root.find('[data-dlv-clear-filters]').prop('hidden',
      state.status === 'all' && !state.sub && !state.year && !$.trim(state.q));

    $root.find('[data-dlv-showing]').text(filtered.length ? format($root.attr('data-showing'), [
        from + 1, from + visible.length, filtered.length
    ]) : $root.attr('data-no-entries'));
    $size.val(String(state.size));
    renderPager(pages, state.page);

    saveState();
  }

  function closeConfirms() {
    $body.find('[data-dlv-confirm]').prop('hidden', true);
    $body.find('[data-dlv-remove]').attr('aria-expanded', false);
    $body.find('.dlv-row.is-confirming').removeClass('is-confirming');
  }

  // ---------- Events ----------

  $root.on('click', '[data-dlv-tab]', function() {
    state.tab = $(this).attr('data-dlv-tab');
    state.status = 'all';
    state.page = 1;
    closeConfirms();
    render();
  });

  // The form around the list has no submit of its own: Enter must not post it
  $search.val(state.q).on('input', function() {
    state.q = this.value;
    state.page = 1;
    render();
  }).on('keydown', function(e) {
    if(e.key === 'Enter') {
      e.preventDefault();
    }
  });

  $chips.on('click', '[data-dlv-status]', function() {
    state.status = $(this).attr('data-dlv-status');
    state.page = 1;
    render();
  });

  $sub.on('change', function() {
    state.sub = this.value;
    state.page = 1;
    render();
  });

  $year.on('change', function() {
    state.year = this.value;
    state.page = 1;
    render();
  });

  $root.on('click', '[data-dlv-sort]', function() {
    var key = $(this).attr('data-dlv-sort');
    state.sort = {
        key: key,
        dir: state.sort.key === key ? -state.sort.dir : 1
    };
    state.page = 1;
    render();
  });

  $size.on('change', function() {
    state.size = parseInt(this.value, 10) || defaults.size;
    state.page = 1;
    render();
  });

  $pager.on('click', '[data-dlv-page]', function() {
    var target = $(this).attr('data-dlv-page');
    state.page = target === 'prev' ? state.page - 1 : target === 'next' ? state.page + 1 : parseInt(target, 10);
    render();
    $root.find('.dlv-table')[0].scrollIntoView({
      block: 'start',
      behavior: 'smooth'
    });
  });

  // Clear filters, from the filter row or the empty state: search, status, sub-category and year
  $root.on('click', '[data-dlv-clear]', function() {
    state.q = '';
    state.status = 'all';
    state.sub = '';
    state.year = '';
    state.page = 1;
    $search.val('');
    render();
  });

  // Delete: confirm inline, the Delete link then goes to deleteDeliverable.do
  $body.on('click', '[data-dlv-remove]', function() {
    var $row = $(this).closest('[data-dlv-row]');
    var wasOpen = $(this).attr('aria-expanded') === 'true';
    closeConfirms();
    if(!wasOpen) {
      $row.addClass('is-confirming');
      $(this).attr('aria-expanded', true);
      $row.find('[data-dlv-confirm]').prop('hidden', false).find('[data-dlv-keep]').trigger('focus');
    }
  });
  $body.on('click', '[data-dlv-keep]', function() {
    var $row = $(this).closest('[data-dlv-row]');
    closeConfirms();
    $row.find('[data-dlv-remove]').trigger('focus');
  });

  // Legend toggle
  $root.on('click', '[data-dlv-legend-toggle]', function() {
    var open = $(this).attr('aria-expanded') !== 'true';
    $(this).attr('aria-expanded', open).text($(this).attr(open ? 'data-label-less' : 'data-label-more'));
    $root.find('#dlv-legend').prop('hidden', !open);
  });

  // "click here" in the FAIR explanation opens the FAIR diagram
  $root.find('.dlv-legend__fair u').attr({
      role: 'button',
      tabindex: 0
  }).on('click keydown', function(e) {
    if(e.type === 'keydown' && e.key !== 'Enter' && e.key !== ' ') {
      return;
    }
    e.preventDefault();
    $('#diagramPopup').dialog({
        title: $('#diagramPopup').attr('title'),
        width: '90%',
        modal: true,
        closeText: ''
    });
  });

  render();
});
