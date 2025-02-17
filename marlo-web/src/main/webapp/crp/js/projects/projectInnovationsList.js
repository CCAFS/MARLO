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

  $('table').each(function(i,table) {

    if ($(table).find('thead th').length === 0) {
      console.warn('Skipping DataTables initialization for a table without headers.');
      return;
    }

    if ($.fn.dataTable.isDataTable(table)) {
      return; // Prevent re-initialization
    }

    var columns = $(table).find('th').length;
    console.log(columns);

    noSortColumns = $(table).find('th.no-sort').map(function() {
      let index = $(this).index();
      return index < columns ? index : null;
    }).get().filter(index => index !== null);

    // Ensure noSortColumns is a valid array
    if (!Array.isArray(noSortColumns)) {
        noSortColumns = [];
    }

    console.log('No-sort columns:', noSortColumns); // Debugging

    try {
      $(table).dataTable({
        "bPaginate": true, // This option enable the table pagination
        "bLengthChange": true, // This option disables the select table size option
        "bFilter": true, // This option enable the search
        "bSort": true, // this option enable the sort of contents by columns
        "bAutoWidth": false, // This option enables the auto adjust columns width
        "iDisplayLength": 50,// Number of rows to show on the table
        "language": {
          searchPlaceholder: "Search...",
          "emptyTable": "No entries entered into the system yet."
        },
        "order": $(table).find('th').length > 1 ? [[1, 'desc']] : [],
        "aoColumnDefs": noSortColumns.length > 0 ? [
          {
            "bSortable": false,
            "aTargets": noSortColumns
          },
          {
            "sType": "natural",
            "aTargets": [0]
          }
        ] : [] // Prevent errors if no columns are defined
      });
    } catch (error) {
      console.error('Error initializing DataTables:', error);
      
    }

  //Add styles to the table
  var iconSearch = $("<div></div>").addClass("iconSearch");
  var divDataTables_filter = $('.dataTables_filter').parent();
  iconSearch.append('<img src="' + baseUrl + '/global/images/search_outline.png" alt="Imagen"  style="width: 24px; margin: auto;" >');
  iconSearch.prependTo(divDataTables_filter);
  var divDataTables_length =$('.dataTables_length').parent();
  divDataTables_length.css("position", "absolute");
  divDataTables_length.css("bottom", "8px");
  divDataTables_length.css("margin-left", "43%");
  divDataTables_length.css("z-index", "1");

  });



}