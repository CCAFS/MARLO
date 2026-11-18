/**
 * Cluster contribution to performance indicators list (A2-2741 redesign).
 *
 * projectContributionsCrpList.ftl renders every mapped indicator of every set
 * (one set, or the deprecated / portfolio tabs some global units use) as a row
 * with its filter keys in data-* attributes. This script owns what is shown: the
 * active set, component, search, status, sorting, the mapped-items panel, the
 * inline remove confirm and the add dialog. It follows innovationList.js; the
 * view state is kept per project in sessionStorage so coming back from an
 * indicator lands where it left.
 */
$(document).ready(function() {
  /* Reporting events (LP6 block, other contribution template) */
  attachReportingEvents();
  lp6menu();

  var $root = $('[data-ci]');
  if(!$root.length) {
    return;
  }

  var STORAGE_KEY = 'ci:' + $root.data('projectId');
  var $body = $root.find('[data-ci-body]');
  var $search = $root.find('[data-ci-search]');
  var $sets = $root.find('[data-ci-set]');
  var setKeys = $sets.map(function() {
    return $(this).attr('data-ci-set');
  }).get();

  var rows = $body.find('[data-ci-row]').map(function(index) {
    var $row = $(this);
    return {
        $el: $row,
        order: index,
        set: $row.attr('data-set'),
        comp: $row.attr('data-comp') || '',
        code: $row.attr('data-code') || '',
        ok: $row.attr('data-ok') === '1',
        search: ($row.attr('data-search') || '').toLowerCase()
    };
  }).get();

  // Components in their listed order, for sorting by component
  var compOrder = [];
  $.each(rows, function(i, row) {
    if($.inArray(row.comp, compOrder) === -1) {
      compOrder.push(row.comp);
    }
  });

  var defaults = {
      set: $root.attr('data-active-set'),
      comp: '',
      q: '',
      status: '',
      sort: {
          key: '',
          dir: 1
      }
  };
  var state = $.extend(true, {}, defaults, readState());
  if(setKeys.length && $.inArray(state.set, setKeys) === -1) {
    state.set = defaults.set;
  }
  if(!setKeys.length) {
    state.set = defaults.set;
  }

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
    return row.set === state.set;
  }

  function matchesSearch(row) {
    var q = $.trim(state.q).toLowerCase();
    return !q || row.search.indexOf(q) !== -1;
  }

  function matchesComp(row) {
    return !state.comp || row.comp === state.comp;
  }

  function matchesStatus(row) {
    return !state.status || (state.status === 'complete' ? row.ok : !row.ok);
  }

  function hasFilters() {
    return !!($.trim(state.q) || state.comp || state.status);
  }

  function sortValue(row, key) {
    if(key === 'comp') {
      return $.inArray(row.comp, compOrder) * 10000 + row.order;
    }
    if(key === 'status') {
      return (row.ok ? 1 : 0) * 10000 + row.order;
    }
    if(key === 'code') {
      return row.code;
    }
    return row.order;
  }

  function compare(a, b) {
    var key = state.sort.key;
    var x = sortValue(a, key);
    var y = sortValue(b, key);
    if(key === 'code') {
      var byCode = String(x).localeCompare(String(y), undefined, {
          numeric: true,
          sensitivity: 'base'
      });
      return (byCode || a.order - b.order) * state.sort.dir;
    }
    return (x === y ? 0 : x > y ? 1 : -1) * state.sort.dir;
  }

  function render() {
    var setRows = $.grep(rows, inSet);
    var searched = $.grep(setRows, matchesSearch);

    // Component counts follow the set and the search; a component with nothing in the set is hidden
    $root.find('[data-ci-comp]').each(function() {
      var comp = $(this).attr('data-ci-comp');
      var inThisSet = $.grep(setRows, function(row) {
        return !comp || row.comp === comp;
      }).length;
      var count = $.grep(searched, function(row) {
        return !comp || row.comp === comp;
      }).length;
      $(this).find('[data-ci-comp-count]').text(count);
      $(this).prop('hidden', !!comp && inThisSet === 0);
    });
    if(state.comp && !$.grep(setRows, function(row) {
      return row.comp === state.comp;
    }).length) {
      state.comp = '';
    }

    // Status counts follow the set, the component and the search
    var base = $.grep(searched, matchesComp);
    var missing = $.grep(base, function(row) {
      return !row.ok;
    }).length;
    $root.find('[data-ci-status-count=""]').text(base.length);
    $root.find('[data-ci-status-count="missing"]').text(missing);
    $root.find('[data-ci-status-count="complete"]').text(base.length - missing);

    var filtered = $.grep(base, matchesStatus).sort(compare);

    // Re-append in sorted order, then show only the matches
    $.each(rows, function(i, row) {
      row.$el.prop('hidden', true);
    });
    $.each(filtered, function(i, row) {
      $body.append(row.$el);
      row.$el.prop('hidden', false);
    });

    $sets.each(function() {
      var isOn = $(this).attr('data-ci-set') === state.set;
      $(this).toggleClass('is-on', isOn).attr('aria-selected', isOn);
    });
    $root.find('[data-ci-comp]').each(function() {
      var isOn = $(this).attr('data-ci-comp') === state.comp;
      $(this).toggleClass('is-on', isOn).attr('aria-selected', isOn);
    });
    $root.find('[data-ci-status]').each(function() {
      var isOn = $(this).attr('data-ci-status') === state.status;
      $(this).toggleClass('is-on', isOn).attr('aria-pressed', isOn);
    });
    $root.find('[data-ci-sort-col]').each(function() {
      var key = $(this).attr('data-ci-sort-col');
      $(this).attr('aria-sort', key === state.sort.key ? (state.sort.dir === 1 ? 'ascending' : 'descending') : 'none');
    });

    // Add is offered only where the set allows it (the portfolio of the current phase)
    $root.find('[data-ci-open-add]').each(function() {
      var allowed = ($(this).attr('data-ci-add-sets') || '').split(' ');
      $(this).closest('.dlv-toolbar__end').add(this).prop('hidden', $.inArray(state.set, allowed) === -1);
    });

    // An empty set drops the filters, the table and the footer for its own empty state
    var setEmpty = setRows.length === 0;
    $root.find('[data-ci-filters], [data-ci-grid], [data-ci-foot]').prop('hidden', setEmpty);
    $root.find('[data-ci-none]').prop('hidden', !setEmpty);
    $root.find('[data-ci-empty]').prop('hidden', setEmpty || filtered.length > 0);
    $root.find('[data-ci-clear-filters]').prop('hidden', !hasFilters());
    $root.find('[data-ci-report-strip]').prop('hidden', !$.grep(setRows, function(row) {
      return !row.ok;
    }).length);

    $root.find('[data-ci-showing]').text(filtered.length === setRows.length ? format($root.attr('data-showing-all'), [
      setRows.length
    ]) : format($root.attr('data-showing'), [
        filtered.length, setRows.length
    ]));

    saveState();
  }

  // ---------- Row panels: mapped items, locked note and remove confirm ----------

  function closePanels($except) {
    $body.find('[data-ci-row]').not($except).each(function() {
      var $row = $(this);
      $row.removeClass('is-confirming');
      $row.find('[data-ci-rel-panel], [data-ci-locked-note], [data-ci-confirm]').prop('hidden', true);
      $row.find('[data-ci-rel], [data-ci-locked], [data-ci-remove]').attr('aria-expanded', false).removeClass('is-on');
    });
  }

  function showRelations($row, type) {
    var $panel = $row.find('[data-ci-rel-panel]');
    $row.find('[data-ci-locked-note], [data-ci-confirm]').prop('hidden', true);
    $row.find('[data-ci-locked], [data-ci-remove]').attr('aria-expanded', false);
    $row.removeClass('is-confirming');
    $panel.prop('hidden', false);
    $panel.find('[data-ci-rel-tab]').each(function() {
      var isOn = $(this).attr('data-ci-rel-tab') === type;
      $(this).toggleClass('is-on', isOn).attr('aria-selected', isOn);
    });
    $panel.find('[data-ci-rel-list]').each(function() {
      $(this).prop('hidden', $(this).attr('data-ci-rel-list') !== type);
    });
    $row.find('[data-ci-rel]').each(function() {
      var isOn = $(this).attr('data-ci-rel') === type;
      $(this).toggleClass('is-on', isOn).attr('aria-expanded', isOn);
    });
  }

  function hideRelations($row) {
    $row.find('[data-ci-rel-panel]').prop('hidden', true);
    $row.find('[data-ci-rel]').removeClass('is-on').attr('aria-expanded', false);
  }

  $body.on('click', '[data-ci-rel]', function() {
    var $row = $(this).closest('[data-ci-row]');
    closePanels($row);
    if($(this).attr('aria-expanded') === 'true') {
      hideRelations($row);
    } else {
      showRelations($row, $(this).attr('data-ci-rel'));
    }
  });
  $body.on('click', '[data-ci-rel-tab]', function() {
    showRelations($(this).closest('[data-ci-row]'), $(this).attr('data-ci-rel-tab'));
  });
  $body.on('click', '[data-ci-rel-close]', function() {
    var $row = $(this).closest('[data-ci-row]');
    hideRelations($row);
    $row.find('[data-ci-rel]').first().trigger('focus');
  });

  $body.on('click', '[data-ci-locked]', function() {
    var $row = $(this).closest('[data-ci-row]');
    var wasOpen = $(this).attr('aria-expanded') === 'true';
    closePanels($row);
    hideRelations($row);
    $row.find('[data-ci-locked-note]').prop('hidden', wasOpen);
    $(this).attr('aria-expanded', !wasOpen);
  });
  $body.on('click', '[data-ci-show-mapped]', function() {
    var $row = $(this).closest('[data-ci-row]');
    $row.find('[data-ci-locked]').attr('aria-expanded', false);
    showRelations($row, $row.find('[data-ci-rel]').first().attr('data-ci-rel'));
  });

  $body.on('click', '[data-ci-remove]', function() {
    var $row = $(this).closest('[data-ci-row]');
    var wasOpen = $(this).attr('aria-expanded') === 'true';
    closePanels($row);
    hideRelations($row);
    $row.toggleClass('is-confirming', !wasOpen);
    $(this).attr('aria-expanded', !wasOpen);
    $row.find('[data-ci-confirm]').prop('hidden', wasOpen);
    if(!wasOpen) {
      $row.find('[data-ci-keep]').trigger('focus');
    }
  });
  $body.on('click', '[data-ci-keep]', function() {
    var $row = $(this).closest('[data-ci-row]');
    $row.removeClass('is-confirming').find('[data-ci-confirm]').prop('hidden', true);
    $row.find('[data-ci-remove]').attr('aria-expanded', false).trigger('focus');
  });
  $body.on('keydown', '[data-ci-confirm]', function(e) {
    if(e.key === 'Escape') {
      $(this).find('[data-ci-keep]').trigger('click');
    }
  });
  // One click: the page reloads on the list after the removal
  $body.on('click', '[data-ci-confirm] a', function() {
    $(this).addClass('is-disabled').attr('aria-disabled', true).on('click', function(e) {
      e.preventDefault();
    });
  });

  // ---------- Toolbar and filters ----------

  $root.on('click', '[data-ci-set]', function() {
    state.set = $(this).attr('data-ci-set');
    closePanels();
    render();
  });

  $root.on('click', '[data-ci-comp]', function() {
    state.comp = $(this).attr('data-ci-comp');
    render();
  });

  $root.on('click', '[data-ci-status]', function() {
    var value = $(this).attr('data-ci-status');
    state.status = state.status === value ? '' : value;
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

  // Ascending, descending, then back to the listed order
  $root.on('click', '[data-ci-sort]', function() {
    var key = $(this).attr('data-ci-sort');
    if(state.sort.key === key && state.sort.dir === -1) {
      state.sort = {
          key: '',
          dir: 1
      };
    } else {
      state.sort = {
          key: key,
          dir: state.sort.key === key ? -1 : 1
      };
    }
    render();
  });

  $root.on('click', '[data-ci-clear]', function() {
    state.q = '';
    state.comp = '';
    state.status = '';
    $search.val('');
    render();
  });

  // ---------- Add dialog ----------

  var dialog = $root.find('[data-ci-dlg]')[0];
  if(dialog) {
    var $dlg = $(dialog);
    var $dlgSearch = $dlg.find('[data-ci-dlg-search]');
    var $error = $dlg.find('[data-ci-dlg-error]');
    var $count = $dlg.find('[data-ci-dlg-count]');

    var showError = function(on) {
      $error.prop('hidden', !on);
      $count.prop('hidden', on);
    };

    var filterOptions = function() {
      var q = $.trim($dlgSearch.val()).toLowerCase();
      var shown = 0;
      $dlg.find('[data-ci-dlg-opt]').each(function() {
        var match = !q || ($(this).attr('data-search') || '').indexOf(q) !== -1;
        $(this).prop('hidden', !match);
        if(match && !$(this).closest('[data-ci-dlg-dep-list]').length) {
          shown++;
        }
      });
      $dlg.find('[data-ci-dlg-group]').each(function() {
        $(this).prop('hidden', !$(this).find('[data-ci-dlg-opt]:not([hidden])').length);
      });
      $dlg.find('[data-ci-dlg-empty]').prop('hidden', shown > 0);
    };

    var closeDialog = function() {
      if(dialog.open) {
        dialog.close();
      }
    };

    $root.on('click', '[data-ci-open-add]', function() {
      $dlg.find('input[type="radio"]').prop('checked', false);
      $dlg.find('.ci-opt').removeClass('is-on');
      $dlgSearch.val('');
      filterOptions();
      showError(false);
      if(typeof dialog.showModal === 'function') {
        dialog.showModal();
      } else {
        dialog.setAttribute('open', '');
      }
      $dlgSearch.trigger('focus');
    });

    $dlgSearch.on('input', filterOptions).on('keydown', function(e) {
      if(e.key === 'Enter') {
        e.preventDefault();
      }
    });

    $dlg.on('change', 'input[type="radio"]', function() {
      $dlg.find('.ci-opt').removeClass('is-on');
      $(this).closest('.ci-opt').addClass('is-on');
      showError(false);
    });

    $dlg.on('click', '[data-ci-dlg-dep]', function() {
      var open = $(this).attr('aria-expanded') !== 'true';
      $(this).attr('aria-expanded', open);
      $(this).find('[data-ci-dlg-dep-show]').prop('hidden', open);
      $(this).find('[data-ci-dlg-dep-hide]').prop('hidden', !open);
      $dlg.find('[data-ci-dlg-dep-list]').prop('hidden', !open);
    });

    $dlg.on('click', '[data-ci-dlg-close]', closeDialog);

    // A click on the backdrop lands on the dialog element itself
    $dlg.on('click', function(e) {
      if(e.target === dialog) {
        closeDialog();
      }
    });

    // addNewProjectOuctome.do saves the mapping and opens the indicator form
    $dlg.on('click', '[data-ci-dlg-add]', function() {
      var outcomeId = $dlg.find('input[type="radio"]:checked').val();
      if(!outcomeId) {
        showError(true);
        return;
      }
      $(this).prop('disabled', true);
      window.location.href = $dlg.attr('data-add-url') + '&outcomeId=' + encodeURIComponent(outcomeId);
    });
  }

  render();
});

/** Reporting functions */
function attachReportingEvents() {
  $('.addOtherContribution').on('click', addOtherContribution);
  $('.removeElement').on('click', removeOtherContribution);
  $('input[name="lp6Contribution"]').on('change', contributionToLP6);
}

function addOtherContribution() {
  var $item = $('#otherContribution-template').clone(true).removeAttr('id');
  var $list = $(this).parents('.tab-pane').find('.otherContributionsBlock');
  $list.append($item);
  $item.show('slow');
}

function removeOtherContribution() {
  var $parent = $(this).parent();
  $parent.hide('slow', function() {
    $parent.remove();
  });
}

function contributionToLP6() {
  var projectID = $(this).parents('.borderBox').classParam('project');
  var phaseID = $(this).parents('.borderBox').classParam('phase');
  var contributionValue = this.value;

  $.ajax({
    url: baseURL + '/projectCollaborationValue.do',
    data: {
      projectID: projectID,
      phaseID: phaseID,
      "crp_lp6_contribution_value": contributionValue
    },
    success: function(data) {
      window.location.href = window.location.href;
    }
  });
}

function lp6menu() {
  var $menu = $("#menu-contributionsLP6");
  if ($menu.hasClass('toSubmit')) {
    // $menu.animateCss('rubberBand');
  }
}
