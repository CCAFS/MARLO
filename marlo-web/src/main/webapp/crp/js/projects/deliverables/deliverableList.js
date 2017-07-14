$(document).ready(function() {

  var $deliverableList = $('table.deliverableList');

  $(".fairDiagram").on("click", openDialog);

  var table = $deliverableList.DataTable({
      "bPaginate": true, // This option enable the table pagination
      "bLengthChange": true, // This option disables the select table size option
      "bFilter": true, // This option enable the search
      "bSort": true, // this option enable the sort of contents by columns
      "bAutoWidth": false, // This option enables the auto adjust columns width
      "iDisplayLength": 50, // Number of rows to show on the table
      "fnDrawCallback": function() {
        // This function locates the add activity button at left to the filter box
        var table = $(this).parent().find("table");
        if($(table).attr("id") == "currentActivities") {
          $("#currentActivities_filter").prepend($("#addActivity"));
        }
      },
      "order": [
        [
            3, 'asc'
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
});

function openDialog() {
  $("#diagramPopup").dialog({
      title: 'FAIR Diagram',
      width: '990',
      heigth: '100%',
      modal: true,
      closeText: ""
  });
}