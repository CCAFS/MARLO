$(document).ready(init);

function init() {
  // Add events for project next users section
  attachEvents();

  // Add Data Table
  addDataTable();
}

function attachEvents() {

}

function addDataTable() {
  $('table#projectPolicies').dataTable({
      "bPaginate": true, // This option enable the table pagination
      "bLengthChange": true, // This option disables the select table size option
      "bFilter": true, // This option enable the search
      "bSort": true, // this option enable the sort of contents by columns
      "bAutoWidth": false, // This option enables the auto adjust columns width
      "iDisplayLength": 50,// Number of rows to show on the table
      "language": {
        "emptyTable": "No policies entered into the system yet."
      },
      "order": [
        [
            4, 'desc'
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
