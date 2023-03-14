$(document).ready(function() {

  // Add Data Table
  addDataTable();
});

function addDataTable() {

  $('table').each(function(i,table) {
    var columns = $(table).find('th').length;
    console.log(columns);

    noSortColumns = $(table).find('th.no-sort').map(function(j,th) {
      return $(th).index();
    });

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
        "order": [
          [
              1, 'desc'
          ]
        ],
        aoColumnDefs: [
            {
                bSortable: false,
                aTargets: noSortColumns
            }, {
                sType: "natural",
                aTargets: [
                  0
                ]
            }
        ]
    });

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