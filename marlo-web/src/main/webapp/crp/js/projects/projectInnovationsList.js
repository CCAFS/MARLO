$(document).ready(function() {

  // Add Data Table
  addDataTable();

  // Add click event to tsURL links
  $('a[href*="/projects"], a[href*="/marlo-web/projects"]').on('click', function() {
    const loadingElement = $('.container_page_load');
    loadingElement.show();
  });
});

function addDataTable() {
  $('table').each(function(i, table) {
    // Skip empty tables or tables without proper structure
    if ($(table).find('thead th').length === 0 || $(table).find('tbody').length === 0) {
      console.warn('Skipping DataTables initialization for invalid table structure.');
      return;
    }

    if ($(table).find('tbody tr').length === 0) {
      console.warn('Table is empty. Skipping DataTables initialization.');
      return;
    }

    // Prevent re-initialization
    if ($.fn.dataTable.isDataTable(table)) {
      return;
    }

    // Get total number of columns
    var columns = $(table).find('thead th').length;
    
    // Find columns with no-sort class
    var noSortColumns = [];
    $(table).find('thead th.no-sort').each(function() {
      noSortColumns.push($(this).index());
    });

    try {
      $(table).DataTable({
        "bPaginate": true,
        "bLengthChange": true, 
        "bFilter": true,
        "bSort": true,
        "bAutoWidth": false,
        "iDisplayLength": 50,
        "language": {
          "searchPlaceholder": "Search...",
          "emptyTable": "No entries entered into the system yet."
        },
        // Set default sorting only if there are enough columns
        "order": columns > 1 ? [[1, 'desc']] : [],
        // Set column definitions only if there are no-sort columns
        "columnDefs": noSortColumns.length > 0 ? [
          {
            "targets": noSortColumns,
            "orderable": false
          }
        ] : []
      });

      // Add styles to the table
      var $table = $(table);
      var $wrapper = $table.closest('.dataTables_wrapper');
      
      if ($wrapper.length) {
        var iconSearch = $("<div></div>").addClass("iconSearch");
        var $filter = $wrapper.find('.dataTables_filter');
        
        if ($filter.length) {
          iconSearch.append('<img src="' + baseUrl + '/global/images/search_outline.png" alt="Search" style="width: 24px; margin: auto;">');
          $filter.parent().prepend(iconSearch);
        }
        
        var $length = $wrapper.find('.dataTables_length');
        if ($length.length) {
          $length.parent().css({
            "position": "absolute",
            "bottom": "8px",
            "margin-left": "43%",
            "z-index": "1"
          });
        }
      }
    } catch (error) {
      console.error('Error initializing DataTables:', error);
    }
  });
}