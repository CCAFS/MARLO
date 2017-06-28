var dialogOptions = {};
$(document).ready(function() {

  var $deliverableList = $('table.deliverablesList');

  var table = $deliverableList.DataTable({
      "bPaginate": true, // This option enable the table pagination
      "bLengthChange": true, // This option disables the select table size option
      "bFilter": true, // This option enable the search
      "bSort": true, // this option enable the sort of contents by columns
      "bAutoWidth": false, // This option enables the auto adjust columns width
      "iDisplayLength": 15, // Number of rows to show on the table,
      aoColumnDefs: [
          {
              bSortable: false,
              aTargets: [
                  -1, -2
              ]
          }, {
              sType: "natural",
              aTargets: [
                0
              ]
          }
      ]
  });

  $('.loadingBlock').hide().next().fadeIn(500);
});