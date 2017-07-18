var dialogOptions = {};
$(document).ready(function() {

  var $projectList = $('table.projectsList');

  var table = $projectList.DataTable({
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

  $('.programTag').on('click', filterByProgram);

  $projectList.on('draw.dt', function() {
    $("a.removeProject").on("click", removeProject);
    $('.programTag').on('click', filterByProgram);
  });

  $('table.projectsHome').dataTable({
      "bPaginate": true, // This option enable the table pagination
      "bLengthChange": true, // This option disables the select table size option
      "bFilter": true, // This option enable the search
      "bSort": true, // this option enable the sort of contents by columns
      "bAutoWidth": false, // This option enables the auto adjust columns width
      "iDisplayLength": 5, // Number of rows to show on the table
      "fnDrawCallback": function() {
        // This function locates the add activity button at left to the filter box
        var table = $(this).parent().find("table");
        if($(table).attr("id") == "currentActivities") {
          $("#currentActivities_filter").prepend($("#addActivity"));
        }
      },
      aoColumnDefs: [
        {
            sType: "natural",
            aTargets: [
              0
            ]
        }
      ]
  });
  $('table.projectsHome').on('draw.dt', function() {
    $("a.removeProject").on("click", removeProject);
  });

  $("#submitForm").on("submit", function(evt) {
    if(confirm($("#beforeSubmitMessage").val())) {
      return true;
    } else {
      evt.preventDefault();
      return false;
    }
  });

  $('.loadingBlock').hide().next().fadeIn(500);

  addJustificationPopUp();

  $('.dataTables_filter input').on("keyup", function() {
    $(this).removeClass("fieldFocus");
  })
});

function filterTablesBy(query) {
  console.log(query);
  $('.dataTables_wrapper').find('.dataTables_filter input').val(query).trigger('keyup');
}

function filterByProgram() {
  $(this).parents('.dataTables_wrapper').find('.dataTables_filter input').val($(this).text()).trigger('keyup');
  $(this).parents('.dataTables_wrapper').find('.dataTables_filter input').addClass("fieldFocus");
}

// Justification popup global vars
var dialog, projectId;
var $dialogContent;

function addJustificationPopUp() {
  $dialogContent = $("#dialog-justification");
  // Initializing justification dialog
  dialog = $dialogContent.dialog({
      autoOpen: false,
      closeText: "",
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
  $("a.removeProject").on("click", removeProject);
}

function removeProject(e) {
  e.preventDefault();
  $dialogContent.find("#justification").val('').removeClass('fieldError');
  // Getting deliverable ID and setting input hidden to remove that deliverable
  $dialogContent.find('input[name$=projectID]').val($(e.target).parent().attr('id').split('-')[1]);
  dialog.dialog("open");
}