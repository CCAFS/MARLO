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

  addJustificationPopUp();
  // Event to open dialog to remove project
  $("a.removeDeliverable").on("click", removeDeliverable);

  $('.loadingBlock').hide().next().fadeIn(500);
});

function addJustificationPopUp() {
  $dialogContent = $("#dialog-justification");
  // Initializing justification dialog
  dialog = $dialogContent.dialog({
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

}

function removeDeliverable(e) {
  e.preventDefault();
  $dialogContent.find("#justification").val('').removeClass('fieldError');
  // Getting deliverable ID and setting input hidden to remove that deliverable
  $dialogContent.find('input[name$=deliverableID]').val($(e.target).parent().attr('id').split('-')[1]);
  dialog.dialog("open");
}