$(document).ready(
    function() {

      var $deliverableList = $('table.outcomeList');
      showHelpText();
      var table = $deliverableList.DataTable({
          "bPaginate": true, // This option enable the table pagination
          "bLengthChange": true, // This option disables the select table size option
          "bFilter": true, // This option enable the search
          "bSort": true, // this option enable the sort of contents by columns
          "bAutoWidth": false, // This option enables the auto adjust columns width
          "iDisplayLength": 10, // Number of rows to show on the table
          "fnDrawCallback": function() {
            // This function locates the add activity button at left to the filter box
            var table = $(this).parent().find("table");
            if($(table).attr("id") == "currentActivities") {
              $("#currentActivities_filter").prepend($("#addActivity"));
            }
          },
          aoColumnDefs: [
              {
                  bSortable: false,
                  aTargets: [
                      -1, -2, -3,
                  ]
              }, {
                  sType: "natural",
                  aTargets: [
                    0
                  ]
              }
          ]
      });

      $('form select').select2({
        width: '100%'
      });

      $("#researchTopics").on(
          "change",
          function() {
            var programID = $("#programSelected").html();
            var option = $(this).find("option:selected");

            var url =
                baseURL + "/impactPathway/" + centerSession + "/outcomesList.do?programID=" + programID + "&edit="
                    + editable + "&topicID=" + option.val();
            window.location = url;

          });

      addJustificationPopUp();
      // Event to open dialog to remove deliverable
      $("a.removeOutcome").on("click", removeOutcome);

    });

// Justification popup global vars
var dialog, outcomeId;
var $dialogContent;

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

function removeOutcome(e) {
  e.preventDefault();
  $dialogContent.find("#justification").val('').removeClass('fieldError');
  // Getting deliverable ID and setting input hidden to remove that deliverable
  $dialogContent.find('input[name$=outcomeID]').val($(e.target).parent().attr('id').split('-')[1]);
  dialog.dialog("open");
}
function showHelpText() {
  $('.helpMessage').show();
  $('.helpMessage').addClass('animated flipInX');
}