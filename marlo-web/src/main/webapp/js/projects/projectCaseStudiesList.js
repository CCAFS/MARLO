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
  $('table#projectHighlights').dataTable({
      "bPaginate": true, // This option enable the table pagination
      "bLengthChange": true, // This option disables the select table size option
      "bFilter": true, // This option enable the search
      "bSort": true, // this option enable the sort of contents by columns
      "bAutoWidth": false, // This option enables the auto adjust columns width
      "iDisplayLength": 50,// Number of rows to show on the table
      "language": {
        "emptyTable": "No outcome case studies entered into the system yet."
      },
      aoColumnDefs: [
        {
            bSortable: false,
            aTargets: [
              -1
            ]
        }
      ]
  });
  $('table#projectHighlights').on('draw.dt', function() {
    // $("a.removeHightlights").on("click", removeDeliverable);
  });
}
