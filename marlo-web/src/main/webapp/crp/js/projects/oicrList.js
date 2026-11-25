/**
 * Project / cluster OICRs list (A2-2743 redesign).
 *
 * projectStudies.ftl renders every OICR of both sets (the current report year and
 * the earlier ones) as a row with its filter keys in data-* attributes. This script
 * owns what is shown: the active set, search, status chips with counts and the owner
 * filter. Rows are ordered by year, then ID, newest first. The view state is kept
 * per cluster in sessionStorage so coming back from an OICR lands where it left.
 */
$(document).ready(function() {
  var $root = $('[data-ocl]');
  if(!$root.length) {
    return;
  }

  var STORAGE_KEY = 'ocl:' + $root.data('projectId');
  var REMOVED_KEY = 'ocl:removed';
  var $table = $root.find('[data-ocl-table]');
  var $body = $root.find('[data-ocl-body]');
  var $search = $root.find('[data-ocl-search]');
  var $owner = $root.find('[data-ocl-owner]');
  var $toast = $root.find('[data-ocl-toast]');

  var rows = $body.find('[data-ocl-row]').map(function() {
    var $row = $(this);
    return {
        $el: $row,
        set: $row.attr('data-set'),
        id: parseInt($row.attr('data-id'), 10) || 0,
        year: parseInt($row.find('.ocl-td--year').text(), 10) || 0,
        status: $row.attr('data-status') || '',
        own: $row.attr('data-own') === '1',
        search: ($row.attr('data-search') || '').toLowerCase()
    };
  }).get().sort(function(a, b) {
    return (b.year - a.year) || (b.id - a.id);
  });

  // Still On-going list: newest year first, then newest ID
  var $carryBody = $root.find('[data-ocl-carry-body]');
  $carryBody.find('[data-ocl-carry-row]').sort(function(a, b) {
    var yearA = parseInt($(a).attr('data-year'), 10) || 0;
    var yearB = parseInt($(b).attr('data-year'), 10) || 0;
    return (yearB - yearA) || ((parseInt($(b).attr('data-id'), 10) || 0) - (parseInt($(a).attr('data-id'), 10) || 0));
  }).appendTo($carryBody);

  var defaults = {
      tab: 'current',
      q: '',
      status: '',
      owner: ''
  };
  var state = $.extend({}, defaults, readState());

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

  function hasFilters() {
    return !!($.trim(state.q) || state.status || state.owner);
  }

  // Tab, owner and search: what the status chips count
  function inScope(row) {
    var q = $.trim(state.q).toLowerCase();
    return row.set === state.tab && (!state.owner || (state.owner === 'own') === row.own)
      && (!q || row.search.indexOf(q) !== -1);
  }

  function matches(row) {
    return inScope(row) && (!state.status || row.status === state.status);
  }

  function render() {
    var setRows = $.grep(rows, function(row) {
      return row.set === state.tab;
    });
    var scoped = $.grep(rows, inScope);
    var filtered = $.grep(scoped, matches);

    $.each(rows, function(i, row) {
      row.$el.prop('hidden', true);
    });
    $.each(filtered, function(i, row) {
      $body.append(row.$el.prop('hidden', false));
    });

    $root.find('[data-ocl-tab]').each(function() {
      var isOn = $(this).attr('data-ocl-tab') === state.tab;
      $(this).toggleClass('is-on', isOn).attr('aria-selected', isOn);
    });
    $table.toggleClass('is-previous', state.tab === 'previous');
    // The still On-going OICRs from earlier years go with the current set
    $root.find('[data-ocl-carry]').prop('hidden', state.tab !== 'current');
    $root.find('[data-ocl-focus]').each(function() {
      $(this).prop('hidden', $(this).attr('data-ocl-focus') !== state.tab);
    });

    // Status chips: counts follow tab, owner and search; Extended only shows when there is one
    $root.find('[data-ocl-status]').each(function() {
      var value = $(this).attr('data-ocl-status');
      var count = value ? $.grep(scoped, function(row) {
        return row.status === value;
      }).length : scoped.length;
      var isOn = value === state.status;
      $(this).toggleClass('is-on', isOn).attr('aria-pressed', isOn);
      $(this).find('[data-ocl-count]').text(count);
      if($(this).is('[data-ocl-optional]')) {
        $(this).prop('hidden', count === 0 && !isOn);
      }
    });
    $owner.val(state.owner);

    // An empty set drops the filters and the table for its own empty state
    var setEmpty = setRows.length === 0;
    $root.find('[data-ocl-filters], [data-ocl-grid]').prop('hidden', setEmpty);
    $root.find('[data-ocl-focus]').filter(function() {
      return $(this).attr('data-ocl-focus') === state.tab;
    }).prop('hidden', setEmpty);
    $root.find('[data-ocl-none]').each(function() {
      $(this).prop('hidden', !setEmpty || $(this).attr('data-ocl-none') !== state.tab);
    });
    $root.find('[data-ocl-grid]').prop('hidden', setEmpty || filtered.length === 0);
    $root.find('[data-ocl-empty]').prop('hidden', setEmpty || filtered.length > 0);

    saveState();
  }

  function closeConfirms() {
    $body.find('[data-ocl-confirm]').prop('hidden', true);
    $body.find('[data-ocl-remove]').attr('aria-expanded', false);
    $body.find('.dlv-row.is-confirming').removeClass('is-confirming');
  }

  function showToast(text) {
    $toast.text(text).prop('hidden', false);
    window.setTimeout(function() {
      $toast.prop('hidden', true);
    }, 7000);
  }

  // ---------- Events ----------

  $root.on('click', '[data-ocl-more]', function() {
    var $more = $root.find('#' + $(this).attr('aria-controls'));
    var open = $(this).attr('aria-expanded') !== 'true';
    $more.prop('hidden', !open);
    $(this).attr('aria-expanded', open).text($(this).attr(open ? 'data-less' : 'data-more'));
  });

  $root.on('click', '[data-ocl-tab]', function() {
    state.tab = $(this).attr('data-ocl-tab');
    state.status = '';
    closeConfirms();
    render();
  });

  $search.val(state.q).on('input', function() {
    state.q = this.value;
    render();
  }).on('keydown', function(e) {
    if(e.key === 'Enter') {
      e.preventDefault();
    }
  });

  $root.on('click', '[data-ocl-status]', function() {
    state.status = $(this).attr('data-ocl-status');
    render();
  });

  $owner.on('change', function() {
    state.owner = this.value;
    render();
  });

  $root.on('click', '[data-ocl-clear]', function() {
    state.q = '';
    state.status = '';
    state.owner = '';
    $search.val('');
    render();
  });

  // Delete: confirm inline with an optional justification; the form posts deleteStudy.do
  $body.on('click', '[data-ocl-remove]', function() {
    var $row = $(this).closest('[data-ocl-row]');
    var wasOpen = $(this).attr('aria-expanded') === 'true';
    closeConfirms();
    if(!wasOpen) {
      $row.addClass('is-confirming');
      $(this).attr('aria-expanded', true);
      $row.find('[data-ocl-confirm]').prop('hidden', false).find('textarea').trigger('focus');
    }
  });
  $body.on('click', '[data-ocl-keep]', function() {
    var $row = $(this).closest('[data-ocl-row]');
    closeConfirms();
    $row.find('[data-ocl-remove]').trigger('focus');
  });
  $body.on('keydown', '[data-ocl-confirm]', function(e) {
    if(e.key === 'Escape') {
      $(this).find('[data-ocl-keep]').trigger('click');
    }
  });
  // A2-2747: send each delete once (a second submit reached the server after the OICR was gone)
  $body.on('submit', '[data-ocl-confirm]', function(e) {
    var $form = $(this);
    if($form.data('submitted')) {
      e.preventDefault();
      return false;
    }
    $form.data('submitted', true);
    $form.find('button[type="submit"]').prop('disabled', true);
    try {
      window.sessionStorage.setItem(REMOVED_KEY, $form.find('input[name="expectedID"]').val());
    } catch(err) {
      // No toast without storage; the delete still goes through
    }
    return true;
  });

  // The page reloads on the list after a delete: confirm it once the OICR is gone
  try {
    var removedId = window.sessionStorage.getItem(REMOVED_KEY);
    window.sessionStorage.removeItem(REMOVED_KEY);
    if(removedId && !$body.find('[data-ocl-row][data-id="' + removedId + '"]').length) {
      showToast(String($root.attr('data-removed')).replace('{0}', removedId));
    }
  } catch(e) {
    // Storage blocked: no toast
  }

  render();
});
