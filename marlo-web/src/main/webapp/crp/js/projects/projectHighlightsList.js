$(document).ready(init);

function init() {
  addDataTable();
  addJustificationPopUp();

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
  });
  // Event to open dialog to remove deliverable
  $("a.removeHighlight").on("click", removeDeliverable);
}

function removeDeliverable(e) {
  e.preventDefault();
  $dialogContent.find("#justification").val('').removeClass('fieldError');
  // Getting deliverable ID and setting input hidden to remove that deliverable
  $dialogContent.find('input[name$=higlightID]').val($(e.target).parent().attr('id').split('-')[1]);
  dialog.dialog("open");
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
        "emptyTable": "No highlights entered into the system yet."
      },
      aoColumnDefs: [
        {
            bSortable: false,
            aTargets: [
                -1, -2
            ]
        }
      ]
  });
  $('table#projectHighlights').on('draw.dt', function() {
    $("a.removeHightlights").on("click", removeDeliverable);
  });
}
