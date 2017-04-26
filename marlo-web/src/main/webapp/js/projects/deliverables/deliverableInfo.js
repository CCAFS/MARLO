var $statuses, $statusDescription;

$(document).ready(init);

function init() {

  $statuses = $('select.status');
  $statusDescription = $('#statusDescription');

  /* Init Select2 plugin */
  $('form select').select2({
    width: '100%'
  });

  $(".fundingSource").select2({
      templateResult: formatState,
      templateSelection: formatState
  });

  $(".genderLevelsSelect").select2({
    templateResult: formatStateGenderType
  });

  $('.helpMessage3').on("click", openDialog);

  // select name
  $(".keyOutput").attr("name", "deliverable.crpClusterKeyOutput.id");
  /* Events select */
  subTypes();
  keyOutputs();

  // justificationByStatus($(".status").find("option:selected").val());
  // validateCurrentDate();

  $(".addPartner").on("click", addPartnerEvent);
  $(".removeElement").on("click", removePartnerEvent);

  // On change any partner person
  $('.projectPartnerPerson select.id').on("change", function() {
    var option = $(this).find("option:selected");
    var $division = $(this).parents('.projectPartnerPerson').find('.division-IFPRI');
    // Show IFPRI Division
    if((option.text()).indexOf("IFPRI") > -1) {
      $division.show();
    } else {
      $division.hide();
    }
  });

  // Update value of responsible person
  $(".responsible").on("change", function() {
    var option = $(this).find("option:selected");
    // validate if exists this person in contact person list
    var validation = $(this).parents(".fullBlock").parent().find(".personList").find("select");
    if(option.val() != "-1") {
      if(validation.exists()) {
        validation.each(function(i,e) {
          if($(e).val() == option.val()) {
            // Remove from contact person list
            $(e).parents(".deliverablePartner").hide("slow", function() {
              $(this).remove();
              updatePartners();
            })
            // Show message
            var text = option.html() + ' was removed from contact persons list';
            notify(text);
          }
        });
      }
    } else {
      $(this).parents(".responsiblePartner").find(".id").val(-1);
    }
  });

  // Update value of partner
  $(".partner").on("change", function() {
    var option = $(this).find("option:selected");
    // validate if exists this person in contact person list
    var validation = $(this).parents(".partnerWrapper").find(".responsibleWrapper").find("select");
    if(validation.exists()) {
      if(validation.val() == option.val()) {
        option.parent().val(-1);
        option.parent().trigger("change.select2");
        // Show message
        var text = option.html() + ' is the responsible person of this deliverable';
        notify(text);
      }
    }
  });

  // CHANGE STATUS
  $statuses.on("change", function() {
    justificationByStatus($(this).val());
  });

  $(".yearExpected").on("change", validateCurrentDate);

  // New Expected year should be greater than current reporting cycle year
  if(reportingActive) {
    $('#newExpectedYear select option[value=' + currentCycleYear + ']').attr('disabled', 'disabled')
    $('#newExpectedYear select').trigger("change.select2");
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

  /** Gender questions * */

  $('input#gender').on('change', function() {
    if($(this).is(':checked')) {
      $('#gender-levels').slideDown();
      $(".genderLevelsSelect").select2({
        templateResult: formatStateGenderType
      });
    } else {
      $('#gender-levels').slideUp();
    }
  });

  $('input#gender, input#youth, input#capacity').on('change', function() {
    $('input#na').prop("checked", false);
  });

  $('input#na').on('change', function() {
    $('input#gender, input#youth, input#capacity').prop("checked", false);
    $('#gender-levels').slideUp();
  });

  $(".subTypeSelect").on("change", function() {
    var subTypeOption = $(this).find("option:selected");
    // Data
    if(subTypeOption.val() == "51" || subTypeOption.val() == "74") {
      $(".dataLicense").show("slow");
      $("#complianceCheck").show("slow");
    } else {
      $(".dataLicense").hide("slow");
      $("#complianceCheck").hide("slow");
    }
    // Computer software
    if(subTypeOption.val() == "52") {
      $(".computerLicense").show("slow");
    } else {
      $(".computerLicense").hide("slow");
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
  $item.find(".name").attr("title", $(option).text());
  $item.find(".name").html(v);
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
  $item.hide(1000, function() {
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
          year: year()
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

  console.log($('#newExpectedYear select').val());

  if(isStatusExtended(statusId) || (isStatusComplete(statusId) && ($('#newExpectedYear select').val() != ""))) {
    $('#newExpectedYear').show();
    $('#newExpectedYear select').attr('disabled', false);
    if(isStatusComplete(statusId) && ($('#newExpectedYear select').val() != "")) {
      $('#newExpectedYear select').attr('disabled', true);
    } else {
      $('#newExpectedYear select').attr('disabled', false);
    }
  } else {
    $('#newExpectedYear').hide();
  }

  $statusDescription.find('textarea').val('');
}

// Add a new person element
function addPartnerEvent() {
  var $list = $(".personList");
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
  var $list = $(this).parents('.personList');
  var $item = $(this).parents('.deliverablePartner');
  $item.hide(1000, function() {
    $item.remove();
    checkItems($list);
    updatePartners();
  });

}

function updatePartners() {
  var name = "deliverable.otherPartners";
  $(".personList").find('.deliverablePartner').each(function(i,item) {

    var customName = name + '[' + i + ']';
    $(item).find('span.index').html(i + 1);
    $(item).find('.id').attr('name', customName + '.projectPartnerPerson.id');
    $(item).find('.type').attr('name', customName + '.projectPartnerPerson.type');
    $(item).find('.divisionField').attr('name', customName + '.partnerDivision.id');
    $(item).find('.element').attr('name', customName + '.id');
  });
}

function subTypes() {
  var url = baseURL + "/deliverableSubType.do";
  var typeSelect = $(".typeSelect");
  var subTypeSelect = $(".subTypeSelect");
  typeSelect.on("change", function() {

    subTypeSelect.empty();
    subTypeSelect.append("<option value='-1' >Select an option... </option>");
    subTypeSelect.trigger("change.select2");
    var option = $(this).find("option:selected");

    if(option.val() != "-1") {
      var data = {
        deliverableTypeId: option.val()
      }
      $.ajax({
          url: url,
          type: 'GET',
          dataType: "json",
          data: data
      }).success(
          function(m) {
            for(var i = 0; i < m.deliverableSubTypes.length; i++) {
              subTypeSelect.append("<option value='" + m.deliverableSubTypes[i].id + "' >"
                  + m.deliverableSubTypes[i].name + "</option>");
            }
          });
    }
    // show or hide publication metadata
    if(option.val() == "49") {
      $(".publicationMetadataBlock").show("slow");
    } else {
      $(".publicationMetadataBlock").hide("slow");
    }
  });
}

function keyOutputs() {
  /*
   * var url = baseURL + "/keyOutputList.do"; var keyOutputSelect = $(".keyOutput"); keyOutputSelect.empty();
   * keyOutputSelect.append("<option value='-1' >Select an option... </option>");
   * keyOutputSelect.trigger("change.select2"); var option = $(this).find("option:selected"); var data = {
   * clusterActivityID: option.val() } $.ajax({ url: url, type: 'GET', dataType: "json", data: data }).success(
   * function(m) { console.log(m); for(var i = 0; i < m.keyOutputs.length; i++) { keyOutputSelect.append("<option
   * value='" + m.keyOutputs[i].id + "' >" + m.keyOutputs[i].description + "</option>"); } });
   */
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
  var $state = $("<span>" + state.text + "</span>");
  return $state;

};

function formatStateGenderType(state) {
  if(state.id == -1) {
    return;
  }
  var $state = $("#genderLevel-" + state.id).clone(true);
  return $state;
}
