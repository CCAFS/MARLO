/**
 * Project / cluster innovations list (A2-2613 redesign).
 *
 * projectInnovationList.ftl renders every innovation of both sets (this phase and
 * the previous ones) as a row with its filter keys in data-* attributes. This
 * script owns what is shown: the active set, search, readiness / type / year
 * filters, sorting and pagination. It follows deliverableList.js; the view state
 * is kept per project in sessionStorage so coming back from an innovation lands
 * where it left.
 */
$(document).ready(function() {
  var $root = $('[data-inv]');
  if(!$root.length) {
    return;
  }

  var FILTERS = ['rl', 'type', 'year'];
  var STORAGE_KEY = 'inv:' + $root.data('projectId');
  var $body = $root.find('[data-inv-body]');
  var $search = $root.find('[data-inv-search]');
  var $size = $root.find('[data-inv-size]');
  var $pager = $root.find('[data-inv-pager]');
  var $filters = {};
  $.each(FILTERS, function(i, key) {
    $filters[key] = $root.find('[data-inv-filter="' + key + '"]');
  });

  var rows = $body.find('[data-inv-row]').map(function() {
    var $row = $(this);
    return {
        $el: $row,
        set: $row.attr('data-set'),
        id: parseInt($row.attr('data-id'), 10) || 0,
        title: $row.attr('data-title') || '',
        rl: $row.attr('data-rl') || '-1',
        rlLabel: $row.attr('data-rl-label') || '',
        type: $row.attr('data-type') || '',
        year: $row.attr('data-year') || '',
        search: ($row.attr('data-search') || '').toLowerCase()
    };
  }).get();

  var defaults = {
      tab: 'current',
      q: '',
      rl: '',
      type: '',
      year: '',
      sort: {
          key: 'id',
          dir: -1
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

  function hasFilters() {
    return !!($.trim(state.q) || state.rl || state.type || state.year);
  }

  function matches(row) {
    var q = $.trim(state.q).toLowerCase();
    return inSet(row) && (!q || row.search.indexOf(q) !== -1) && (!state.rl || row.rl === state.rl)
      && (!state.type || row.type === state.type) && (!state.year || row.year === state.year);
  }

  function sortValue(row, key) {
    if(key === 'id') {
      return row.id;
    }
    if(key === 'rl') {
      return parseInt(row.rl, 10);
    }
    return row[key];
  }

  function compare(a, b) {
    var x = sortValue(a, state.sort.key);
    var y = sortValue(b, state.sort.key);
    if(x === y) {
      return (a.id - b.id) * state.sort.dir;
    }
    return (x > y ? 1 : -1) * state.sort.dir;
  }

  // Options follow the active set; a stale choice from the other set is dropped
  function fillSelect($select, options, current) {
    var values = $.map(options, function(option) {
      return option.value;
    });
    $select.find('option').not(':first').remove();
    $.each(options, function(i, option) {
      $('<option>').val(option.value).text(option.label).appendTo($select);
    });
    if($.inArray(current, values) === -1) {
      current = '';
    }
    $select.val(current);
    return current;
  }

  function optionsFor(setRows, key) {
    var seen = {};
    var options = [];
    $.each(setRows, function(i, row) {
      var value = row[key];
      if(key === 'rl' && value === '-1') {
        return;
      }
      if(value && !seen[value]) {
        seen[value] = true;
        options.push({
            value: value,
            label: key === 'rl' ? row.rlLabel : value
        });
      }
    });
    return options.sort(function(a, b) {
      var x = key === 'rl' ? parseInt(a.value, 10) : a.label;
      var y = key === 'rl' ? parseInt(b.value, 10) : b.label;
      return x > y ? 1 : x < y ? -1 : 0;
    });
  }

  function renderPager(pages, page) {
    var $prev = $('<button type="button" class="dlv-pager__btn" data-inv-page="prev">')
      .attr('aria-label', $pager.attr('data-label-prev')).prop('disabled', page <= 1)
      .html('<svg width="10" height="10" viewBox="0 0 12 12" fill="none" aria-hidden="true"><path d="M8 2.5 4.5 6 8 9.5" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"></path></svg>');
    var $next = $('<button type="button" class="dlv-pager__btn" data-inv-page="next">')
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
      var $btn = $('<button type="button" class="dlv-pager__btn">').attr('data-inv-page', n).text(n);
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
    $.each(FILTERS, function(i, key) {
      state[key] = fillSelect($filters[key], optionsFor(setRows, key), state[key]);
    });

    var filtered = $.grep(rows, matches).sort(compare);
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

    $root.find('[data-inv-tab]').each(function() {
      var isOn = $(this).attr('data-inv-tab') === state.tab;
      $(this).toggleClass('is-on', isOn).attr('aria-selected', isOn);
    });
    $root.find('[data-inv-sort-col]').each(function() {
      var key = $(this).attr('data-inv-sort-col');
      $(this).attr('aria-sort', key === state.sort.key ? (state.sort.dir === 1 ? 'ascending' : 'descending') : 'none');
    });

    // An empty set drops the filters, the table and the footer for its own empty state
    var setEmpty = setRows.length === 0;
    $root.find('[data-inv-filters], [data-inv-grid], [data-inv-foot]').prop('hidden', setEmpty);
    $root.find('[data-inv-none]').each(function() {
      $(this).prop('hidden', !setEmpty || $(this).attr('data-inv-none') !== state.tab);
    });
    $root.find('[data-inv-empty]').prop('hidden', setEmpty || filtered.length > 0);
    $root.find('[data-inv-clear-filters]').prop('hidden', !hasFilters());

    $root.find('[data-inv-showing]').text(filtered.length ? format($root.attr('data-showing'), [
        from + 1, from + visible.length, filtered.length
    ]) : $root.attr('data-no-entries'));
    $size.val(String(state.size));
    renderPager(pages, state.page);

    saveState();
  }

  function closeConfirms() {
    $body.find('[data-inv-confirm]').prop('hidden', true);
    $body.find('[data-inv-remove]').attr('aria-expanded', false);
    $body.find('.dlv-row.is-confirming').removeClass('is-confirming');
  }

  // ---------- Events ----------

  $root.on('click', '[data-inv-tab]', function() {
    state.tab = $(this).attr('data-inv-tab');
    state.page = 1;
    closeConfirms();
    render();
  });

  $search.val(state.q).on('input', function() {
    state.q = this.value;
    state.page = 1;
    render();
  }).on('keydown', function(e) {
    if(e.key === 'Enter') {
      e.preventDefault();
    }
  });

  $.each(FILTERS, function(i, key) {
    $filters[key].on('change', function() {
      state[key] = this.value;
      state.page = 1;
      render();
    });
  });

  $root.on('click', '[data-inv-sort]', function() {
    var key = $(this).attr('data-inv-sort');
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

  $pager.on('click', '[data-inv-page]', function() {
    var target = $(this).attr('data-inv-page');
    state.page = target === 'prev' ? state.page - 1 : target === 'next' ? state.page + 1 : parseInt(target, 10);
    render();
    $root.find('.dlv-table')[0].scrollIntoView({
        block: 'start',
        behavior: 'smooth'
    });
  });

  // Clear filters, from the filter row or the empty state
  $root.on('click', '[data-inv-clear]', function() {
    state.q = '';
    state.rl = '';
    state.type = '';
    state.year = '';
    state.page = 1;
    $search.val('');
    render();
  });

  // Delete: confirm inline with an optional justification; the form posts deleteInnovation.do
  $body.on('click', '[data-inv-remove]', function() {
    var $row = $(this).closest('[data-inv-row]');
    var wasOpen = $(this).attr('aria-expanded') === 'true';
    closeConfirms();
    if(!wasOpen) {
      $row.addClass('is-confirming');
      $(this).attr('aria-expanded', true);
      $row.find('[data-inv-confirm]').prop('hidden', false).find('textarea').trigger('focus');
    }
  });
  $body.on('click', '[data-inv-keep]', function() {
    var $row = $(this).closest('[data-inv-row]');
    closeConfirms();
    $row.find('[data-inv-remove]').trigger('focus');
  });
  $body.on('keydown', '[data-inv-confirm]', function(e) {
    if(e.key === 'Escape') {
      $(this).find('[data-inv-keep]').trigger('click');
    }
  });
  // One submit: the page reloads on the list after the delete
  $body.on('submit', '[data-inv-confirm]', function() {
    $(this).find('button[type="submit"]').prop('disabled', true);
  });

  render();
});
