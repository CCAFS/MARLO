var $statuses, $statusDescription;

$(document).ready(init);

function init() {

  $statuses = $('select.status');
  $statusDescription = $('#statusDescription');

  // Take out the 0 - Not Targeted Dimension
  $('.crossCuttingDimensionsSelect option[value=0]').remove();

  selectKeyOutput();

  /* Init Select2 plugin */
  $('form select').select2({
    width: '100%'
  });

  // Update Parters selected lists
  updateProjectPartnersSelects();

  $(".fundingSource").select2({
      templateResult: formatState,
      templateSelection: formatState,
      width: "100%"
  });

  $(".genderLevelsSelect").select2({
      templateResult: formatStateGenderType,
      width: "100%"
  });

  $('.helpMessage3').on("click", openDialog);

  // justificationByStatus($(".status").find("option:selected").val());
  // validateCurrentDate();

  $(".addPartner").on("click", addPartnerEvent);
  $(".removeElement").on("click", removePartnerEvent);

  // On change any partner person
  $('.projectPartnerPerson select.id').on("change", function() {
    var $select = $(this);
    var isResp = $select.hasClass('responsible');
    if(isResp) {
      var $list = $select.parents(".responsiblePartner").find(".partnerPersons");
    } else {
      var $list = $select.parents(".deliverablePartner").find(".partnerPersons");
    }

    var $list = $select.parents(".deliverablePartner, .responsiblePartner").find(".partnerPersons");
    var option = $select.find("option:selected");
    var projectPartnerID = option.val();

    $.ajax({
        url: baseURL + '/personByParnters.do',
        data: {
            partnerId: projectPartnerID,
            phaseID: phaseID
        },
        beforeSend: function() {
          $list.empty();
        },
        success: function(data) {

          if(data.persons.length) {
            $.each(data.persons, function(i,person) {
              if(isResp) {
                var $item = $('#deliverablePerson-template.resp').clone(true);
              } else {
                var $item = $('#deliverablePerson-template.other').clone(true);
                $item.find('input.projectPartnerID').val(projectPartnerID);
              }
              $item.removeAttr('id');
              $item.find('input[type="checkbox"], input[type="radio"]').val(person.id);
              $item.find('label.checkbox-label, label.radio-label').text(person.user);
              $list.append($item);
              $item.show();
            });
          } else {
            if(isResp) {
              var $item = $('#deliverablePerson-template.resp').clone(true);
            } else {
              var $item = $('#deliverablePerson-template.other').clone(true);
              $item.find('input.projectPartnerID').val(projectPartnerID);
            }
            $item.removeAttr('id');
            $list.append($item);
          }

        },
        complete: function() {
          if(isResp) {
            updatePartnersResp();
          } else {
            updatePartners();
          }

          var $division = $select.parents('.projectPartnerPerson').find('.division-IFPRI');

          // Show IFPRI Division
          if((option.text()).indexOf("IFPRI") > -1) {
            $division.show();
          } else {
            $division.find('.divisionField').val("-1")
            $division.hide();
          }

        }
    });

  });

  // CHANGE CATEGORY
  $("#CCAFS_deliverable_deliverable_deliverableType_deliverableType_id").on('change', function(e) {
    var selectedOption = $("#CCAFS_deliverable_deliverable_deliverableType_deliverableType_id option:selected").val();
    if(selectedOption != -1) {
      $(".subType-select").show(600);
    } else {
      $(".subType-select").hide(600);
    }
  });

  // CHANGE STATUS
  $statuses.on("change", function() {
    justificationByStatus(this.value);
  });

  $(".yearExpected").on("change", validateCurrentDate);

  // New Expected year should be greater than current reporting cycle year
  if(reportingActive) {
    // $('#newExpectedYear select option[value=' + currentCycleYear - 1 + ']').attr('disabled', 'disabled')
    // $('#newExpectedYear select').trigger("change.select2");
  }

  /** Funding source * */

  $(".fundingSource").on("change", function() {
    var option = $(this).find("option:selected");
    if(option.val() != "-1") {
      addFundingSource(option);
    }
    // Remove option from select
    option.remove();
    $(this).trigger("change.select2");
  });
  $(".removeFundingSource").on("click", removeFundingSource);

  // Validate if funding source exists in select
  $("form .fundingSources").each(function(i,e) {
    var options = $(".fundingSource option");
    options.each(function(iOption,eOption) {
      if($(e).find(".fId").val() == $(eOption).val()) {
        $(eOption).remove();
      }
    });
  });

  /** Gender Levels * */

  $(".genderLevelsSelect.add").on("change", function() {
    var option = $(this).find("option:selected");
    if(option.val() != "-1") {
      addGenderLevel(option);
    }
    // Remove option from select
    option.remove();
    $(this).trigger("change.select2");
  });
  $(".removeGenderLevel").on("click", removeGenderLevel);

  // Validate if funding source exists in select
  $("form .genderLevel").each(function(i,e) {
    var options = $(".genderLevelsSelect option");
    options.each(function(iOption,eOption) {
      if($(e).find(".fId").val() == $(eOption).val()) {
        $(eOption).remove();
      }
    });
  });

  /** Cross-Cutting dimensions * */

  $('input.crosscutingDimension').on('change', function() {
    var $crosscutingDimensionBlock = $('#ccDimension-' + this.id);

    if($(this).is(':checked')) {
      $crosscutingDimensionBlock.slideDown();
    } else {
      $crosscutingDimensionBlock.slideUp();
    }
  });

  /** Gender questions * */

  $('input#gender').on('change', function() {
    if($(this).is(':checked')) {
      $('#gender-levels').slideDown();
      $(".genderLevelsSelect").select2({
          templateResult: formatStateGenderType,
          width: "100%"
      });
    } else {
      $('#gender-levels').slideUp();
    }
  });

  $('input.crosscutingDimension').on('change', function() {
    $('input#na').prop("checked", false);
  });

  $('input#na').on('change', function() {
    $('input.crosscutingDimension').prop("checked", false);
    $('#gender-levels').slideUp();
    $('.ccDimension').slideUp();
  });

  $('.typeSelect').on('change', function() {
    var $subTypeSelect = $(".subTypeSelect");
    $subTypeSelect.empty();
    $subTypeSelect.append("<option value='-1' >Select an option... </option>");
    $subTypeSelect.trigger("change.select2");
    var option = $(this).find("option:selected");

    if(option.val() != "-1") {
      $.ajax({
          url: baseURL + "/deliverableSubType.do",
          type: 'GET',
          dataType: "json",
          data: {
              deliverableTypeId: option.val(),
              phaseID: phaseID
          }
      }).success(function(m) {
        for(var i = 0; i < m.deliverableSubTypes.length; i++) {
          $subTypeSelect.addOption(m.deliverableSubTypes[i].id, m.deliverableSubTypes[i].name)
        }
      });
    }
    $subTypeSelect.trigger('change');
  });

  $(".subTypeSelect").on("change", function() {
    var subTypeOption = $(this).find("option:selected").val();
    var typeOption = $('.typeSelect').find("option:selected").val();
    // Show or hide publication metadata
    if(hasDeliverableRule('publicationMetadata', [
        subTypeOption, typeOption
    ])) {
      $(".publicationMetadataBlock").show("slow");
    } else {
      $(".publicationMetadataBlock").hide("slow");
    }
    // Compliance Check Rule
    if(hasDeliverableRule('complianceCheck', [
        subTypeOption, typeOption
    ])) {
      $("#complianceCheck").show("slow");
    } else {
      $("#complianceCheck").hide("slow");
    }
    // Data License
    if(hasDeliverableRule('dataLicense', [
        subTypeOption, typeOption
    ])) {
      $(".dataLicense").show("slow");
    } else {
      $(".dataLicense").hide("slow");
      // Clear data licenses
      $('.computerLicense input').prop("checked", false);
    }
    // Computer software
    if(hasDeliverableRule('computerLicense', [
        subTypeOption, typeOption
    ])) {
      $(".computerLicense").show("slow");
    } else {
      $(".computerLicense").hide("slow");
      // Clear computer licenses
      $('.computerLicense input').prop("checked", false);
    }

  });
}

function openDialog() {
  $("#dialog").dialog({
      width: '980',
      modal: true,
      closeText: ""
  });
}

// Add a new fundingSource element
function addFundingSource(option) {
  var canAdd = true;
  console.log(option.val());
  if(option.val() == "-1") {
    canAdd = false;
  }

  var $list = $(option).parents(".select").parents("#fundingSourceList").find(".list");
  var $item = $("#fsourceTemplate").clone(true).removeAttr("id");
  var v = $(option).text().length > 80 ? $(option).text().substr(0, 80) + ' ... ' : $(option).text();

  // Check if is already selected
  $list.find('.fundingSources').each(function(i,e) {
    if($(e).find('input.fId').val() == option.val()) {
      canAdd = false;
      return;
    }
  });
  if(!canAdd) {
    return;
  }

  // Set funding source parameters
  $item.find(".name").html($("#fundingSource-" + option.val()).clone(true));
  $item.find(".fId").val(option.val());
  $list.append($item);
  $item.show('slow');
  updateFundingSources($list);
  checkFundingItems($list);

  // Reset select
  $(option).val("-1");
  $(option).trigger('change.select2');

}

function removeFundingSource() {
  var $list = $(this).parents('.list');
  var $item = $(this).parents('.fundingSources');
  var value = $item.find(".fId").val();
  var name = $item.find(".name").attr("title");
  console.log(name + "-" + value);
  var $select = $(".fundingSource");
  $item.hide(500, function() {
    $item.remove();
    checkFundingItems($list);
    updateFundingSources($list);
  });
// Add funding source option again
  $select.addOption(value, name);
  $select.trigger("change.select2");
}

function updateFundingSources($list) {
  $($list).find('.fundingSources').each(function(i,e) {
    // Set funding sources indexes
    $(e).setNameIndexes(1, i);
  });
}

/** Add gender level * */

function addGenderLevel(option) {
  var canAdd = true;
  console.log(option.val());
  if(option.val() == "-1") {
    canAdd = false;
  }

  var $list = $(option).parents(".select").parents("#genderLevelsList").find(".list");
  var $item = $("#genderLevel-template").clone(true).removeAttr("id");
  var v = $(option).text().length > 80 ? $(option).text().substr(0, 80) + ' ... ' : $(option).text();

  // Check if is already selected
  $list.find('.genderLevel').each(function(i,e) {
    if($(e).find('input.fId').val() == option.val()) {
      canAdd = false;
      return;
    }
  });
  if(!canAdd) {
    return;
  }

  // Set parameters
  $item.find(".name").attr("title", $(option).text()).tooltip();
  $item.find(".name").html(v);
  $item.find(".fId").val(option.val());
  $list.append($item);
  $item.show('slow');
  updateGenderLevels($list);
  checkGenderItems($list);

  // Reset select
  $(option).val("-1");
  $(option).trigger('change.select2');

}

function removeGenderLevel() {
  var $list = $(this).parents('.list');
  var $item = $(this).parents('.genderLevel');
  var value = $item.find(".fId").val();
  var name = $item.find(".name").attr("title");
  console.log(name + "-" + value);
  var $select = $(".genderLevelsSelect");
  $item.hide(800, function() {
    $item.remove();
    checkGenderItems($list);
    updateGenderLevels($list);
  });
// Add funding source option again
  $select.addOption(value, name);
  $select.trigger("change.select2");
}

function updateGenderLevels($list) {
  $($list).find('.genderLevel').each(function(i,e) {
    // Set funding sources indexes
    $(e).setNameIndexes(1, i);
  });
}

function validateCurrentDate() {
  var $statusSelect = $("form select.status");
  var $yearSelect = $(".yearExpected");
  var status = function() {
    return $statusSelect.val();
  }
  var year = function() {
    return $yearSelect.val();
  };

  // Ajax
  $.ajax({
      url: baseURL + '/deliverableStatus.do',
      data: {
          deliverableId: $('input[name=deliverableID]').val(),
          year: year(),
          phaseID: phaseID
      },
      beforeSend: function() {
        $statusSelect.empty();
      },
      success: function(data) {
        $.each(data.status, function(val,name) {
          $statusSelect.addOption(val, name);
        });
        $statusSelect.trigger("change.select2");

        // Check year and status
        if((year() < currentCycleYear) && (status() == 4)) {
          $('#newExpectedYear').show();
        } else {
          $('#newExpectedYear').hide();
        }

      }
  });

  $statusSelect.trigger("change.select2");
}

function justificationByStatus(statusId) {
  if(isStatusCancelled(statusId) || isStatusExtended(statusId)) {
    $statusDescription.hide().show(400);
    $statusDescription.find('label').html($('#status-' + statusId).html());
  } else {
    $statusDescription.show().hide(400);
  }

  var newExpectedYear = $('#newExpectedYear select').val();
  console.log(newExpectedYear);
  var isCompletedWithoutExpectedYear =
      ((!reportingActive) && isStatusComplete(statusId) && ((newExpectedYear != "") || newExpectedYear != "-1"));

  if(isStatusExtended(statusId) || isCompletedWithoutExpectedYear) {
    $('#newExpectedYear').show();
    $('#newExpectedYear select').attr('disabled', false);
    if(isCompletedWithoutExpectedYear) {
      $('#newExpectedYear select').attr('disabled', true);
      $('#newExpectedYear').hide();
    } else {
      $('#newExpectedYear select').attr('disabled', false);
    }
  } else {
    $('#newExpectedYear').hide();
  }

  var $yearOverlay = $('#deliverableYear .overlay');
  if(isStatusExtended(statusId)) {
    $yearOverlay.show();
  } else {
    $yearOverlay.hide();
  }

  $statusDescription.find('textarea').val('');
}

// Add a new person element
function addPartnerEvent() {
  var $list = $(".partnersList");
  var $item = $("#deliverablePartner-template").clone(true).removeAttr("id");
  $list.append($item);
  $item.find('select').select2({
    width: "100%"
  });
  $item.show('slow');
  checkItems($list);
  updatePartners();
}

// Remove person element
function removePartnerEvent() {
  var $list = $(this).parents('.partnersList');
  var $item = $(this).parents('.deliverablePartner');
  $item.hide(500, function() {
    $item.remove();
    checkItems($list);
    updatePartners();
  });

}

function updatePartners() {
  $(".partnersList").find('.deliverablePerson.checkbox').each(function(i,item) {
    var personID = $(item).find('input[type="checkbox"]').val();
    var customID = "checkbox-" + i + "-" + personID;
    $(item).setNameIndexes(1, i);

    $(item).find('input[type="checkbox"]').attr('id', customID);
    $(item).find('label.checkbox-label').attr('for', customID);
  });

  updateProjectPartnersSelects();
}

function updatePartnersResp() {
  $(".responsibleWrapper ").find('.deliverablePerson.radio').each(function(i,item) {
    var personID = $(item).find('input[type="radio"]').val();
    var customID = "radio-" + i + "-" + personID;

    $(item).find('input[type="radio"]').attr('id', customID);
    $(item).find('label.radio-label').attr('for', customID);
  });

  updateProjectPartnersSelects();

}

function updateProjectPartnersSelects() {
  // Update selects
  var selectedValues = []
  $("select.partner.id").each(function(i,select) {
    selectedValues.push($(select).find("option:selected").val());
  });

  $("select.partner.id").each(function(i,select) {
    $(select).find('option').attr('disabled', false).prop('disabled', false);
    $.each(selectedValues, function(i,optionValue) {
      if(optionValue != -1) {
        $(select).find('option[value="' + optionValue + '"]').attr('disabled', true).prop('disabled', true);
      }
    });
    $(select).trigger("select2.change");
  });
}

function checkItems(block) {
  console.log(block);
  var items = $(block).find('.deliverablePartner').length;
  if(items == 0) {
    $(block).parent().find('p.emptyText').fadeIn();
  } else {
    $(block).parent().find('p.emptyText').fadeOut();
  }
}

function checkFundingItems(block) {
  console.log(block);
  var items = $(block).find('.fundingSources').length;
  console.log(items);
  if(items == 0) {
    $(block).parent().find('p.emptyText').fadeIn();
  } else {
    $(block).parent().find('p.emptyText').fadeOut();
  }
}

function checkGenderItems(block) {
  console.log(block);
  var items = $(block).find('.genderLevel').length;
  console.log(items);
  if(items == 0) {
    $(block).parent().find('p.emptyText').fadeIn();
  } else {
    $(block).parent().find('p.emptyText').fadeOut();
  }
}

function notify(text) {
  var notyOptions = jQuery.extend({}, notyDefaultOptions);
  notyOptions.text = text;
  notyOptions.type = 'alert';
  noty(notyOptions);
}

function formatState(state) {
  if(state.id == -1) {
    return state.text;
  }
  var $state = $("#fundingSource-" + state.id).clone(true);
  return $state;

};

function formatStateGenderType(state) {
  if(state.id == -1) {
    return;
  }
  var $state = $("#genderLevel-" + state.id).clone(true);
  return $state;
}

function hasDeliverableRule(rule,arrValues) {
  var result = 0;
  $.each(arrValues, function(index,value) {
    if(($.inArray(value, getDeliverableTypesByRule(rule))) != -1) {
      result++;
    }
  });
  return(result > 0);
}

function getDeliverableTypesByRule(rule) {
  var result = $('#getDeliverableTypesByRule-' + rule).val().split(", ");
  return result;
}

function selectKeyOutput() {
  var $keyOutputList = $('.selectList').find('.keyOutput');
  var keyOutputOptions = $keyOutputList.find('option')
  var keyOutputsLength = keyOutputOptions.length;

  if( keyOutputsLength == 2 ) {
    var optionValue = keyOutputOptions[1].value;
    $keyOutputList.val(optionValue);
    $keyOutputList.trigger('change');
  }

}