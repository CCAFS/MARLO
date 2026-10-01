/**
 * Projects / clusters list.
 *
 * Every list on the page (editable, read-only, archived) is a DataTable whose built-in search, length menu and pager
 * are hidden: the toolbar above the lists drives all of them at once, and each card renders its own footer.
 * Filtering runs on the row data attributes written by projectsListTemplate.ftl (data-search, data-type,
 * data-status, data-submitted).
 */
$(document).ready(function() {
  var $page = $('#clustersPage');
  if(!$page.length) {
    return;
  }

  var TYPE_ORDER = ['country', 'theme', 'regional', 'management'];
  var PAGE_SIZE = 50;
  var filters = {
      q: '',
      type: 'all',
      status: 'all'
  };

  var $search = $('#clustersSearch');
  var $typeFilter = $('#clustersTypeFilter');
  var $statusFilter = $('#clustersStatusFilter');
  var $clear = $('#clustersClearFilters');
  var $tables = $page.find('table.clustersTable');

  // ---- Filtering ---------------------------------------------------------------------------------------------------

  function matchesQuery($row) {
    return !filters.q || String($row.attr('data-search') || '').indexOf(filters.q) > -1;
  }

  function matchesStatus($row) {
    if(filters.status === 'all') {
      return true;
    }
    if(filters.status === '__submitted') {
      return $row.attr('data-submitted') === 'true';
    }
    return String($row.attr('data-status')) === filters.status;
  }

  function matchesType($row) {
    return filters.type === 'all' || $row.attr('data-type') === filters.type;
  }

  $.fn.dataTable.ext.search.push(function(settings, data, dataIndex) {
    if(!$(settings.nTable).hasClass('clustersTable')) {
      return true;
    }
    var $row = $(settings.aoData[dataIndex].nTr);
    return matchesQuery($row) && matchesStatus($row) && matchesType($row);
  });

  // ---- Tables ------------------------------------------------------------------------------------------------------

  $tables.each(function() {
    var $table = $(this);
    $table.DataTable({
        dom: 't',
        paging: true,
        pageLength: PAGE_SIZE,
        info: false,
        autoWidth: false,
        order: [
          [
              0, 'asc'
          ]
        ],
        language: {
            emptyTable: '',
            zeroRecords: ''
        },
        columnDefs: [
          {
              orderable: false,
              targets: 'no-sort'
          }
        ]
    });
    $table.on('draw.dt', function() {
      renderFooter($table);
    });
    renderFooter($table);
  });

  function allRows() {
    return $tables.DataTable().rows().nodes().to$();
  }

  // The counts next to each filter option describe the main list (the one the user can edit), like its heading does;
  // the other lists only fall back in when the user can edit nothing
  function countedRows() {
    var $main = $('#myProjects');
    return $main.find('tbody tr.cl-row').length ? $main.DataTable().rows().nodes().to$() : allRows();
  }

  // ---- Toolbar -----------------------------------------------------------------------------------------------------

  function format(text, values) {
    var out = text;
    for(var i = 0; i < values.length; i++) {
      out = out.split('__' + (i + 1) + '__').join(values[i]);
    }
    return out;
  }

  function renderTypeFilter() {
    var $rows = allRows();
    var labels = {};
    $rows.each(function() {
      var type = $(this).attr('data-type');
      if(type) {
        labels[type] = $(this).attr('data-type-label');
      }
    });
    var types = TYPE_ORDER.filter(function(type) {
      return labels[type];
    });
    // A single type (or none) is nothing to filter by
    if(types.length < 2) {
      $typeFilter.prop('hidden', true).empty();
      return;
    }
    var base = countedRows().filter(function() {
      return matchesQuery($(this)) && matchesStatus($(this));
    });
    var options = [
      {
          key: 'all',
          label: $page.data('textAll'),
          count: base.length
      }
    ].concat(types.map(function(type) {
      return {
          key: type,
          label: labels[type],
          count: base.filter('[data-type="' + type + '"]').length
      };
    }));

    $typeFilter.empty().prop('hidden', false);
    options.forEach(function(option) {
      var on = filters.type === option.key;
      $('<button type="button" class="cl-segment"></button>').toggleClass('is-active', on).attr('aria-pressed', on)
          .attr('data-type', option.key).text(option.label)
          .append($('<span class="cl-segmentCount"></span>').text(option.count)).appendTo($typeFilter);
    });
  }

  function renderStatusFilter() {
    var $rows = countedRows();
    var statuses = [];
    allRows().each(function() {
      var status = $(this).attr('data-status');
      if(status && statuses.indexOf(status) < 0) {
        statuses.push(status);
      }
    });
    statuses.sort();

    var options = [
      {
          value: 'all',
          label: $page.data('textAllStatuses')
      }
    ].concat(statuses.map(function(status) {
      return {
          value: status,
          label: status + ' (' + $rows.filter(function() {
            return $(this).attr('data-status') === status;
          }).length + ')'
      };
    }));
    var submitted = $rows.filter('[data-submitted="true"]').length;
    if(submitted) {
      options.push({
          value: '__submitted',
          label: $page.data('textSubmitted') + ' (' + submitted + ')'
      });
    }

    $statusFilter.empty();
    options.forEach(function(option) {
      $('<option></option>').val(option.value).text(option.label).appendTo($statusFilter);
    });
    $statusFilter.val(filters.status);
  }

  function hasFilters() {
    return !!filters.q || filters.type !== 'all' || filters.status !== 'all';
  }

  function applyFilters() {
    renderTypeFilter();
    $clear.prop('hidden', !hasFilters());
    $tables.each(function() {
      $(this).DataTable().draw();
    });
  }

  $search.on('input', function() {
    filters.q = $.trim($(this).val()).toLowerCase();
    applyFilters();
  });

  $typeFilter.on('click', '.cl-segment', function() {
    filters.type = $(this).attr('data-type');
    applyFilters();
  });

  $statusFilter.on('change', function() {
    filters.status = $(this).val();
    applyFilters();
  });

  $page.on('click', '#clustersClearFilters, .clearFilters', function() {
    filters = {
        q: '',
        type: 'all',
        status: 'all'
    };
    $search.val('');
    $statusFilter.val('all');
    applyFilters();
  });

  // ---- Card footer: "Showing x–y of z" and the pager ---------------------------------------------------------------

  function renderFooter($table) {
    var api = $table.DataTable();
    var info = api.page.info();
    var $card = $table.closest('.cl-listCard');
    var total = info.recordsTotal;
    var shown = info.recordsDisplay;
    var text;

    if(!shown) {
      text = format($page.data('textShowingNone'), [total]);
    } else if(shown < total) {
      text = format($page.data('textShowingFiltered'), [info.start + 1, info.end, shown, total]);
    } else {
      text = format($page.data('textShowing'), [info.start + 1, info.end, shown]);
    }
    $card.find('.cl-showing').text(text);
    $card.find('.cl-empty').prop('hidden', shown > 0);
    $table.toggleClass('is-empty', shown === 0);
    $card.closest('.cl-section').find('.cl-count').text(shown < total ? shown + ' / ' + total : total);

    var $pager = $card.find('.cl-pager').empty();
    $('<button type="button" class="cl-pageBtn" data-page="previous"></button>').text($page.data('textPrevious'))
        .prop('disabled', info.page === 0).appendTo($pager);
    for(var i = 0; i < Math.max(info.pages, 1); i++) {
      var current = i === info.page;
      $('<button type="button" class="cl-pageNum"></button>').text(i + 1).attr('data-page', i)
          .toggleClass('is-current', current).attr('aria-current', current ? 'page' : null)
          .attr('aria-label', format($page.data('textPage'), [i + 1])).appendTo($pager);
    }
    $('<button type="button" class="cl-pageBtn" data-page="next"></button>').text($page.data('textNext'))
        .prop('disabled', info.page >= info.pages - 1).appendTo($pager);
  }

  $page.on('click', '.cl-pager button', function() {
    var $table = $(this).closest('.cl-listCard').find('table.clustersTable');
    var page = $(this).attr('data-page');
    $table.DataTable().page(isNaN(page) ? page : Number(page)).draw('page');
  });

  // ---- Remove: confirm inline, on the row ---------------------------------------------------------------------------

  function closeConfirm($row) {
    $row.removeClass('is-confirming');
    $row.find('.cl-confirm').prop('hidden', true);
    $row.find('.cl-actions').prop('hidden', false);
  }

  $page.on('click', '.removeProject', function() {
    allRows().filter('.is-confirming').each(function() {
      closeConfirm($(this));
    });
    var $row = $(this).closest('tr');
    $row.addClass('is-confirming');
    $row.find('.cl-actions').prop('hidden', true);
    $row.find('.cl-confirm').prop('hidden', false).find('.confirmRemoveProject').trigger('focus');
  });

  $page.on('click', '.cancelRemoveProject', function() {
    var $row = $(this).closest('tr');
    closeConfirm($row);
    $row.find('.removeProject').trigger('focus');
  });

  $page.on('click', '.confirmRemoveProject', function() {
    var $form = $('#removeProjectForm');
    $(this).prop('disabled', true);
    $form.find('input[name="projectID"]').val($(this).data('projectId'));
    $form.trigger('submit');
  });

  $page.on('keydown', '.cl-confirm', function(e) {
    if(e.key === 'Escape') {
      $(this).find('.cancelRemoveProject').trigger('click');
    }
  });

  renderStatusFilter();
  applyFilters();
});
