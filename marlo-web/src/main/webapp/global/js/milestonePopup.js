var dialog, notyDialog;
var $elementSelected, $dialogContent;

$(document).ready(function() {
  /** Initialize */
  $dialogContent = $("#dialog-searchProjects");
  var dialogOptions = {
      autoOpen: false,
      width: 550,
      modal: true,
      dialogClass: 'dialog-searchUsers',
      open: function(event,ui) {
      }
  };

  // Initializing Manage users dialog
  dialog = $dialogContent.dialog(dialogOptions);

  // Loading initial data with all users
  // getData('');

  /** Events */

  // Change target unit value
  $(".tagetUnitPopup").on("change", function() {
    var option = $(this).find("option:selected");
    if(option.val() != "-1") {
      $(".targetValuePopup").parent().parent().show("slow");
    } else {
      $(".targetValuePopup").parent().parent().hide("slow");
    }
  });

  $('.close-dialog').on('click', function() {
    dialog.dialog("close");
  });

  // Event to open dialog box and search an contact person
  $(".addMilestone").on("click", function(e) {
    e.preventDefault();
    openSearchDialog($(this));
  });

  // Event to Create a funding source clicking in the button
  $dialogContent.find(".create-button").on("click", function() {
    $dialogContent.find('.warning-info').empty().hide();
    var targetUnit = "-1";
    var targetValue = null;
    var statement = $(".statementPopup").val();
    var targetYear = $(".targetYearPopup").find("option:selected").val();
    if($(".tagetUnitPopup").find("option:selected").val() != "-1") {
      targetUnit = $(".tagetUnitPopup").find("option:selected").val();
      targetValue = $(".targetValuePopup").val();
    }
    if(statement.trim() == "") {
      $(".statementPopup").addClass("fieldError");
      return false;
    } else {
      var data = {
          title: statement,
          targetUnit: targetUnit,
          value: targetValue,
          year: targetYear,
          outcomeID: $("input[name='outcomeID']").val(),
          phaseID: phaseID
      }
      $.ajax({
          'url': baseURL + '/addMilestone.do',
          method: 'POST',
          data: data,
          beforeSend: function() {
            $dialogContent.find('.loading').show();
          },
          success: function(response) {
            console.log(data);
            var $list = $(".milestoneList");
            $list.each(function(i,e) {
              var $item = $("#milestone-template").clone(true).removeAttr("id");
              $item.find(".milestone-statement").parent().find("p").text(response.newMilestone[0].title);
              $item.find(".milestone-targetYear").val(response.newMilestone[0].targetYear);
              $item.find(".milestone-targetYear").parent().find("p").text(response.newMilestone[0].targetYear);
              $item.find(".mileStoneId").val(response.newMilestone[0].id);
              $item.find(".elementId").val(response.newMilestone[i + 1].Elementid);
              if(data.targetUnit == "-1") {
                $item.find(".achieved").parent().parent().remove();
              }
              $(e).append($item);
              $($item).show("slow");
            });
            updateAllIndexes();
            dialog.dialog("close");
          },
          complete: function(response) {
            $dialogContent.find('.loading').fadeOut();
          }
      });
    }

  });

  $dialogContent.find("form").on("submit", function(e) {
    event.preventDefault();
  });

  /** Functions * */

  function updateAllIndexes() {
    $('form .outcomeTab').each(function(i,outcome) {

      $(outcome).setNameIndexes(1, i);

      // Update evidences
      $(outcome).find('.milestone').each(function(i,m) {
        $(m).find(".index").text(i + 1);
        $(m).setNameIndexes(2, i);

      });
    });

    // Update component event
    $(document).trigger('updateComponent');

  }

  openSearchDialog = function(selected) {

    $elementSelected = $(selected);
    selectedPartnerTitle = $elementSelected.parents('.projectPartner').find('.partnerTitle').text();
    $dialogContent.find('.cgiarCenter').text(selectedPartnerTitle);
    institutionSelected = $elementSelected.parents('.projectPartner').find('.partnerInstitutionId').text();
    selectedYear = $elementSelected.parents('.tab-pane').attr('id').split('-')[1];

    dialog.dialog("open");
    $('form select').select2({
      width: "100%"
    });
    $.ui.dialog.prototype._allowInteraction = function(e) {
      return !!$(e.target).closest('.ui-dialog, .ui-datepicker, .select2-dropdown').length;
    };
  }

});// End document ready event

