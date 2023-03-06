$(document).ready(init);

function init() {
  // Add events for project next users section
  attachEvents();

  // Add Data Table
  addDataTable();

  // Add delete confirmation popup
  addJustificationPopUp();
}

function attachEvents() {

}

// Justification popup global vars
var dialog, deliverableId;
var $dialogContent;

function addJustificationPopUp() {

  $dialogContent = $("#dialog-justification");
  // Initializing justification dialog
  dialog = $dialogContent.dialog(dialogOptions = {
      autoOpen: false,
      height: 200,
      width: 400,
      modal: true,
      buttons: {
          Cancel: function() {
            $(this).dialog("close");
          },
          Remove: function() {
            var $justification = $dialogContent.find("#justification");
            if($justification.val().length > 0) {
              $justification.removeClass('fieldError');
              $dialogContent.find("form").submit();
            } else {
              $justification.addClass('fieldError');
            }
          }
      },
      closeText: ''
  });
  // Event to open dialog to remove deliverable
  $("a.removeElementList").on("click", removeElementList);
}

function removeElementList(e) {
  e.preventDefault();
  $dialogContent.find("#justification").val('').removeClass('fieldError');
  // Getting deliverable ID and setting input hidden to remove that deliverable
  $dialogContent.find('input[name$=expectedID]').val($(e.target).parent().attr('id').split('-')[1]);
  dialog.dialog("open");
}

function addDataTable() {
  $('table#projectHighlights').dataTable({
      "bPaginate": true, // This option enable the table pagination
      "bLengthChange": true, // This option disables the select table size option
      "bFilter": true, // This option enable the search
      "bSort": true, // this option enable the sort of contents by columns
      "bAutoWidth": false, // This option enables the auto adjust columns width
      "iDisplayLength": 10,// Number of rows to show on the table
      "language": {
        searchPlaceholder: "Search...",
        "emptyTable": "No studies entered into the system yet."
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
  $('table#projectHighlights').on('draw.dt', function() {
    $("a.removeElementList").on("click", removeElementList);
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
}
