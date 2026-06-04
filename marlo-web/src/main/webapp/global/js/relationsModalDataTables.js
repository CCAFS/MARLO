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
