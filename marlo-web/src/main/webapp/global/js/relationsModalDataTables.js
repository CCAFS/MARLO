/*****************************************************************
 * DataTables for relationsPopupMacro modals (e.g. PPA Partners).
 * Same options and search UI as projectContributionsCrpList.js
 * initializeDataTable(), without duplicating that script on admin.
 *****************************************************************/
$(function() {
  function appendSearchIconToFilter($filter) {
    if (!$filter.length || $filter.data('relationsModalSearchIcon')) {
      return;
    }
    var $icon = $('<div class="iconSearch"></div>');
    $icon.append(
      '<img src="' + baseUrl + '/global/images/search_outline.png" alt="" ' +
      'style="width: 24px; margin: auto;">'
    );
    $icon.prependTo($filter);
    $filter.data('relationsModalSearchIcon', true);
  }

  /**
   * Opt in per column filters. A modal gets them by rendering, before its table:
   *   <div class="relationsModalFilters" data-filter-columns="3,4" data-label-all="All"></div>
   * One select is built per listed column, offering the distinct values found in that column and labelled with the
   * column header, so no label has to be duplicated in the markup. Modals without the container are untouched.
   */
  function buildColumnFilters($table) {
    var $container = $table.closest('.modal-body').find('.relationsModalFilters').first();
    if (!$container.length || $container.data('relationsModalFiltersReady')) {
      return;
    }
    var columns = String($container.data('filter-columns') || '').split(',');
    var labelAll = $container.data('label-all') || 'All';
    var table = $table.DataTable();

    $.each(columns, function(i, rawIndex) {
      var index = parseInt(String(rawIndex).trim(), 10);
      if (isNaN(index)) {
        return;
      }
      var column = table.column(index);
      if (!column || typeof column.data !== 'function') {
        return;
      }

      /* Distinct plain text values of the column, so markup such as labels does not leak into the option */
      var values = [];
      column.data().each(function(value) {
        var text = $('<div></div>').html(value).text().trim();
        if (text.length && values.indexOf(text) === -1) {
          values.push(text);
        }
      });
      if (values.length < 2) {
        /* A single value filters nothing, do not add noise to the popup */
        return;
      }
      values.sort();

      var label = $(column.header()).text().trim() || ('#' + index);
      var $group = $('<div class="relationsModalFilter"></div>');
      $group.append($('<label></label>').text(label + ':'));

      var $select = $('<select class="form-control input-sm"></select>');
      $select.append($('<option></option>').val('').text(labelAll));
      $.each(values, function(j, value) {
        $select.append($('<option></option>').val(value).text(value));
      });
      $group.append($select);
      $container.append($group);

      $select.on('change', function() {
        var value = $(this).val();
        /* Anchored so "AR 2023" does not also match "AR 2023 extended"; \s* absorbs cell indentation */
        var term = value ? '^\\s*' + $.fn.dataTable.util.escapeRegex(value) + '\\s*$' : '';
        column.search(term, true, false).draw();
      });
    });

    $container.data('relationsModalFiltersReady', true);
  }

  /**
   * Mirrors projectContributionsCrpList.js initializeDataTable($table).
   */
  function initializeDataTableLikeContributions($table) {
    var el = $table.get(0);
    if (!el || $table.find('tbody tr').length === 0) {
      return;
    }
    if ($.fn.dataTable.isDataTable(el)) {
      $table.DataTable().columns.adjust().draw(false);
      return;
    }

    var colCount = $table.find('thead tr:first th').length;
    var initialOrder = colCount > 3 ? [[3, 'asc']] : [[0, 'asc']];

    $table.DataTable({
      bPaginate: true,
      bLengthChange: true,
      bFilter: true,
      bSort: true,
      bAutoWidth: false,
      /* DataTables merges the table data-* attributes over these options, so a table can override this
         default page size with data-page-length (see _fnCamelToHungarian on init). */
      iDisplayLength: 25,
      language: {
        searchPlaceholder: 'Search...'
      },
      fnDrawCallback: function() {
        /* Same hook as projectContributionsCrpList.js; modals do not use currentActivities. */
      },
      order: initialOrder,
      aoColumnDefs: [
        {
          bSortable: true,
          aTargets: [-1]
        },
        {
          sType: 'natural',
          aTargets: [0]
        }
      ]
    });

    var $wrapper = $table.closest('.dataTables_wrapper');
    var $filter = $wrapper.find('.dataTables_filter');
    appendSearchIconToFilter($filter);

    buildColumnFilters($table);

    var $next = $table.next();
    if ($next.length) {
      $next.css('width', '35%');
    } else {
      $wrapper.css('width', '35%');
    }
  }

  $(document).on('shown.bs.modal', '.modal', function() {
    var $modal = $(this);
    $modal.find('.modal-body table').each(function() {
      initializeDataTableLikeContributions($(this));
    });

    $modal.find('.dataTables_filter').each(function() {
      appendSearchIconToFilter($(this));
    });
  });
});
