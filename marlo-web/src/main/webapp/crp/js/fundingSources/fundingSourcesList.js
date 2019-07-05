var dialogOptions = {};
$(document).ready(function() {

  $('form select').select2({
    width: "100%"
  });

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

  $projectList.on('draw.dt', function() {
    $("a.removeProject").on("click", removeProject);
  });

  // Add Button click (Adding state)
  $('a.addButton').on('click', function(e) {
    if($(this).hasClass('disabled')) {
      e.preventDefault();
      return;
    }
    // Turn add button in adding button
    $(this).addClass('disabled animated flipInY');
    $(this).find('.glyphicon').hide();
    $(this).find('.saveText').html('Adding ... <img src="' + baseUrl + '/global/images/loading_3.gif" />&nbsp;');

  });

  $('.loadingBlock').hide().next().fadeIn(1000);

  addJustificationPopUp();

  /**
   * Pop up to add a new Funding Source
   */
  (function() {
    // Start Here coding
    var $instPartnersSelect = $('select[class*="elementType-institution"]');
    var $institutionLeadSelect = $('select.institutionLead');
    var $statusSelect = $('select.agreementStatus');
    var $financeCode = $('input.financeCode');
    var $submitButton = $('button.addFundingSourceFromPopup');

    $instPartnersSelect.on("addElement removeElement", function(event,id,name) {
      if(event.type == "addElement") {
        $('select.institutionLead').addOption(id, name);
      } else if(event.type == "removeElement") {
        $('select.institutionLead').removeOption(id);
      }
      validateForm();
    });

    $institutionLeadSelect.on("change", function() {
      validateForm();
    });
    $statusSelect.on("change", function() {
      validateForm();
    });

    var keyupTimer = null;
    $financeCode.on("keyup", function(e,v) {
      if(keyupTimer) {
        clearTimeout(keyupTimer);
        keyupTimer = null;
      }
      keyupTimer = setTimeout(searchDuplicated, 1000);
    });

    function searchDuplicated() {
      $.ajax({
          'url': baseURL + '/FundingSourceService.do',
          'data': {
              financeCode: $financeCode.val(),
              phaseID: phaseID
          },
          beforeSend: function() {
            $financeCode.addClass('input-loading');
          },
          success: function(data) {
            console.log(data);
          },
          complete: function() {
            $financeCode.removeClass('input-loading');
          },
          error: function() {
          }
      });
    }

    function validateForm() {
      var instPartners = $instPartnersSelect.parents('.panel-body').find('ul li').length;
      var statusValue = $statusSelect.val();
      var leadValue = $institutionLeadSelect.val();

      if((instPartners > 0) && (statusValue > 0) && (leadValue > 0)) {
        $submitButton.prop("disabled", false);
        return true;
      }
      $submitButton.prop("disabled", true);
      return false;
    }

  })();

});

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
  $dialogContent.find('input.nameId').val($(e.target).parent().attr('id').split('-')[1]);
  dialog.dialog("open");
}