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
      language:{
        searchPlaceholder: "Search..."
      },
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

//Add styles to the table
  var iconSearch = $("<div></div>").addClass("iconSearch");
  var divDataTables_filter = $('.dataTables_filter').parent();
  iconSearch.append('<img src="' + baseUrl + '/global/images/search_outline.png" alt="Imagen"  style="width: 24px; margin: auto;" >');
  iconSearch.prependTo(divDataTables_filter)
  src="' + baseUrl + '/global/images/loading_3.gif"
  var divDataTables_length =$('.dataTables_length').parent();
  divDataTables_length.css("position", "absolute");
  divDataTables_length.css("bottom", "8px");
  divDataTables_length.css("margin-left", "43%");
  divDataTables_length.css("z-index", "1");

  
});

function openDialog() {
  $("#diagramPopup").dialog({
      title: 'FAIR Diagram',
      width: '90%',
      /* heigth: '100%', */
      modal: true,
      closeText: ""
  });
}