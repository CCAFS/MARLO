$(document).ready(function() {

  // Add Data Table
  addDataTable();
});

function addDataTable() {
  $('table').dataTable({
      "bPaginate": true, // This option enable the table pagination
      "bLengthChange": true, // This option disables the select table size option
      "bFilter": true, // This option enable the search
      "bSort": true, // this option enable the sort of contents by columns
      "bAutoWidth": false, // This option enables the auto adjust columns width
      "iDisplayLength": 50,// Number of rows to show on the table
      "language": {
        "emptyTable": "No entries entered into the system yet."
      },
      "order": [
        [
            1, 'desc'
        ]
      ],
      aoColumnDefs: [
          {
              bSortable: true,
              aTargets: [
                -1
              ]
          }, {
              sType: "natural",
              aTargets: [
                0
              ]
          }
      ]
  });
}