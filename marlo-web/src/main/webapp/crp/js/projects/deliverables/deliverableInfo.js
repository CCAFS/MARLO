
var $statuses, $statusDescription;
var deliverableAjaxURL = '/qaAssessmentStatus.do?year=2021&indicatorTypeID=3&crpID=';
var deliverableArrName = 'fullItemsAssessmentStatus';

$(document).ready(init);
function hideOrShowCheckBoxIsOtherUrl(value) {
  if (value) {
    $('.isOtherUrlTohide').show('slow');
  } else {
    $('input.isOtherUrl').prop('checked', false);
    $('.other-url').hide('slow');
    $('.isOtherUrlFiel').val(false);

  }
}
function checkDOI() {
  setTimeout(() => {
    if ($('.deliverableDisseminationUrl ').prop('readonly') || $('.disseminationChannel').val() == 'other') {
      if ($('#doi-bridge').val()) {
        $('.isOtherUrlTohide').hide('slow');
        hideOrShowCheckBoxIsOtherUrl(false);
      } else {
        if (($('.subTypeSelect ').val() == 63)) {
          hideOrShowCheckBoxIsOtherUrl(true);
        }
      }
    }
    // ^((https?:\/\/)?(www\.)?[a-zA-Z0-9.-]+\.+[a-zA-Z0-9.-]+\/10\.\d{4,9}[-._;()/:A-Z0-9]+$|^(doi\:)?10.\d{4,9}[-._;()/:A-Z0-9]+$)
    // ^((https?:\/\/)?(www\.)?[a-zA-Z0-9.-]+\.+[a-zA-Z0-9.-]+\/10\.\d{4,9}[-._;()/:A-Z0-9]+$|^10.\d{4,9}[-._;()/:A-Z0-9]+$)
    //    /^10.\d{4,9}[-._;()/:A-Z0-9]+$/i           comienza
    //    /10.\d{4,9}[-._;()/:A-Z0-9]+$/i            si lo contiene

    // nuevo doi ^((https?:\/\/)?(www\.)?[a-zA-Z0-9.-]+\.+[a-zA-Z0-9.-]+\/10\.\d{4,9}\/[-._;():A-Z0-9]+$|^10\.\d{4,9}\/[-._;():A-Z0-9]+$)
    var result = /^((https?:\/\/)?(www\.)?[a-zA-Z0-9.-]+\.+[a-zA-Z0-9.-]+\/10\.\d{4,9}\/[-._;()/:A-Z0-9]+$|^10\.\d{4,9}\/[-._;()/:A-Z0-9]+$)/i.test($('#doi-bridge').val());

    if (result) {
      $('#doi-bridge').css("border", "1px solid #ccc");
      $('.invalidDOI').hide('slow');
      $('.validDOI').show('slow');
    }
    if (!result && $('#doi-bridge').val()) {
      $('#doi-bridge').css("border", "red solid 1px");
      $('.invalidDOI').show('slow');
      $('.validDOI').hide('slow');

    }
    if (!$('#doi-bridge').val()) {
      $('#doi-bridge').css("border", "1px solid #ccc");
      $('.invalidDOI').hide('slow');
      $('.validDOI').hide('slow');

    }

  }, 50);

}

function init() {

  $statuses = $('select.status');
  isDeliverableNew = $statuses.classParam('isNew') == "true";
  $statusDescription = $('#statusDescription');

  // Take out the 0 - Not Targeted Dimension
  $('.crossCuttingDimensionsSelect option[value=0]').remove();

  selectKeyOutput();

  $('.helpMessage3').on("click", openDialog);

  // Event to validate the expected date
  $(".yearExpected").on("change", validateCurrentDate);

  // Event when status is changed
  $statuses.on("change", function () {
    justificationByStatus(this.value);
    disseminationMetadataRequiredFields();
  });

  disseminationMetadataRequiredFields();
  validateDeliverableStatus();


  $('#doi-bridge').keydown(checkDOI);
  $('#doi-bridge').change(checkDOI);
  $('#doi-bridge').bind("paste", checkDOI);
  if ($('#doi-bridge')[0]) {
    document.getElementById("doi-bridge").addEventListener("paste", checkDOI);
  }


  $('input.isOtherUrl').on("click", activeByNoDOIProvidedCheckbox);
  activeByNoDOIProvidedCheckbox();
  // justificationByStatus($statuses.val());
  // validateCurrentDate();

  /** Funding source * */

  $(".fundingSource").on("change", function () {
    var option = $(this).find("option:selected");
    if (option.val() != "-1") {
      addFundingSource(option);
    }
    // Remove option from select
    option.remove();
    $(this).trigger("change.select2");
  });
  $(".removeFundingSource").on("click", removeFundingSource);

  // Validate if funding source exists in select
  $("form .fundingSources").each(function (i, e) {
    var options = $(".fundingSource option");
    options.each(function (iOption, eOption) {
      if ($(e).find(".fId").val() == $(eOption).val()) {
        $(eOption).remove();
      }
    });
  });

  /** Gender Levels * */

  $(".genderLevelsSelect.add").on("change", function () {
    var option = $(this).find("option:selected");
    if (option.val() != "-1") {
      addGenderLevel(option);
    }
    // Remove option from select
    option.remove();
    $(this).trigger("change.select2");
  });
  $(".removeGenderLevel").on("click", removeGenderLevel);

  // Validate if funding source exists in select
  $("form .genderLevel").each(function (i, e) {
    var options = $(".genderLevelsSelect option");
    options.each(function (iOption, eOption) {
      if ($(e).find(".fId").val() == $(eOption).val()) {
        $(eOption).remove();
      }
    });
  });

  $('.typeSelect').on('change', function () {
    var $subTypeSelect = $(".subTypeSelect");
    $subTypeSelect.empty();
    $subTypeSelect.append("<option value='-1' >Select an option... </option>");
    $subTypeSelect.trigger("change.select2");
    var option = $(this).find("option:selected");

    if (option.val() != "-1") {
      $.ajax({
        url: baseURL + "/deliverableSubType.do",
        type: 'GET',
        dataType: "json",
        data: {
          deliverableTypeId: option.val(),
          phaseID: phaseID
        }
      }).success(function (m) {
        for (var i = 0; i < m.deliverableSubTypes.length; i++) {
          $subTypeSelect.addOption(m.deliverableSubTypes[i].id, m.deliverableSubTypes[i].name)
        }
      });
    }
    $subTypeSelect.trigger('change');
  });

  $(".subTypeSelect").on("change", function () {
    var subTypeOption = $(this).find("option:selected").val();
    var typeOption = $('.typeSelect').find("option:selected").val();
    // Show or hide publication metadata
    if (hasDeliverableRule('publicationMetadata', [
      subTypeOption, typeOption
    ])) {
      $(".publicationMetadataBlock").show("slow");
    } else {
      $(".publicationMetadataBlock").hide("slow");
    }
    // Compliance Check Rule
    if (hasDeliverableRule('complianceCheck', [
      subTypeOption, typeOption
    ])) {
      $("#complianceCheck").show("slow");
    } else {
      $("#complianceCheck").hide("slow");
    }
    // Data License
    if (hasDeliverableRule('dataLicense', [
      subTypeOption, typeOption
    ])) {
      $(".dataLicense").show("slow");
    } else {
      $(".dataLicense").hide("slow");
      // Clear data licenses
      $('.computerLicense input').prop("checked", false);
    }
    // Computer software
    if (hasDeliverableRule('computerLicense', [
      subTypeOption, typeOption
    ])) {
      $(".computerLicense").show("slow");
    } else {
      $(".computerLicense").hide("slow");
      // Clear computer licenses
      $('.computerLicense input').prop("checked", false);
    }

  });

  /* Init Select2 plugin */
  $('form select').select2({
    width: '100%'
  });

  $(".fundingSource").select2({
    templateResult: formatState,
    templateSelection: formatState,
    width: "100%"
  });

  $(".genderLevelsSelect").select2({
    templateResult: formatStateGenderType,
    width: "100%"
  });

  // Deliverable Geographic Scope
  $('select.elementType-repIndGeographicScope').on("addElement removeElement", function (event, id, name) {
    setGeographicScope(this);
  });
  setGeographicScope($('form select.elementType-repIndGeographicScope')[0]);
  // valiate checkbox "No DOI provided" value

  deliverablePartnersModule.init();
}

function disseminationMetadataRequiredFields() {
  var statusID = $('select[name="deliverable.deliverableInfo.status"]').val();
  var requiredTagsToHide = $('#deliverable-disseminationMetadata').find('span[class*="requiredTag"]').filter(function () {
    if (statusID == '7' || statusID == '5') {
      return $(this).css('display').includes('inline');
    } else {
      return $(this).css('display').includes('none');
    }
  });
  if (statusID == '7' || statusID == '5') {
    $(requiredTagsToHide).addClass('tagToHide');
    $(requiredTagsToHide).hide();
  } else {
    $(requiredTagsToHide).each((index, item) => {
      if ($(item).hasClass('tagToHide')) {
        $(item).show();
        $(item).removeClass('tagToHide');
      }
    });
  }
}

function activeByNoDOIProvidedCheckbox() {
  if ($('input.isOtherUrl').is(":checked")) {
    console.log("checked");
    $('.isOtherUrlFiel').val(true);
  } else {
    console.log("No checked");
    $('.isOtherUrlFiel').val(false);

  }

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
  if (option.val() == "-1") {
    canAdd = false;
  }

  var $list = $(option).parents(".select").parents("#fundingSourceList").find(".list");
  var $item = $("#fsourceTemplate").clone(true).removeAttr("id");
  var v = $(option).text().length > 80 ? $(option).text().substr(0, 80) + ' ... ' : $(option).text();

  // Check if is already selected
  $list.find('.fundingSources').each(function (i, e) {
    if ($(e).find('input.fId').val() == option.val()) {
      canAdd = false;
      return;
    }
  });
  if (!canAdd) {
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
  $item.hide(500, function () {
    $item.remove();
    checkFundingItems($list);
    updateFundingSources($list);
  });
  // Add funding source option again
  $select.addOption(value, name);
  $select.trigger("change.select2");
}

function updateFundingSources($list) {
  $($list).find('.fundingSources').each(function (i, e) {
    // Set funding sources indexes
    $(e).setNameIndexes(1, i);
  });
}

/** Add gender level * */

function addGenderLevel(option) {
  var canAdd = true;
  console.log(option.val());
  if (option.val() == "-1") {
    canAdd = false;
  }

  var $list = $(option).parents(".select").parents("#genderLevelsList").find(".list");
  var $item = $("#genderLevel-template").clone(true).removeAttr("id");
  var v = $(option).text().length > 80 ? $(option).text().substr(0, 80) + ' ... ' : $(option).text();

  // Check if is already selected
  $list.find('.genderLevel').each(function (i, e) {
    if ($(e).find('input.fId').val() == option.val()) {
      canAdd = false;
      return;
    }
  });
  if (!canAdd) {
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
  $item.hide(800, function () {
    $item.remove();
    checkGenderItems($list);
    updateGenderLevels($list);
  });
  // Add funding source option again
  $select.addOption(value, name);
  $select.trigger("change.select2");
}

function updateGenderLevels($list) {
  $($list).find('.genderLevel').each(function (i, e) {
    // Set funding sources indexes
    $(e).setNameIndexes(1, i);
  });
}

function validateCurrentDate() {
  var $statuses = $("form select.status");
  var statusValue = $("form select.status").val();
  var $yearSelect = $(".yearExpected");
  var status = function () {
    return $statuses.val();
  }
  var year = function () {
    return $yearSelect.val();
  };

  console.log("changed year");

  // Ajax
  $.ajax({
    url: baseURL + '/deliverableStatus.do',
    data: {
      deliverableId: $('input[name=deliverableID]').val(),
      year: year(),
      phaseID: phaseID
    },
    beforeSend: function () {
      statusValue = $("form select.status").val();
      $statuses.empty();
    },
    success: function (data) {
      $.each(data.status, function (val, name) {
        $statuses.addOption(val, name);
      });
      $statuses.val(statusValue);
      $statuses.trigger("change.select2");
      justificationByStatus(statusValue);
      disseminationMetadataRequiredFields(statusValue);

      // Check year and status
      if ((year() < currentCycleYear) && (status() == 4)) {
        // $('#newExpectedYear').show();
      } else {
        // $('#newExpectedYear').hide();
      }

    }
  });

  $statuses.trigger("change.select2");
}

function isStatusPartiallyComplete(statusId) {
  return (statusId == "7");
}

function justificationByStatus(statusId) {
  var $yearOverlay = $('#deliverableYear .overlay');
  var $newExpectedYearBlock = $('#newExpectedYear');
  var $newExpectedYearSelect = $newExpectedYearBlock.find('select');
  var newExpectedYear = $newExpectedYearSelect.val();
  var hasExpectedYear =
    ((newExpectedYear != "") && newExpectedYear != "-1") && (typeof newExpectedYear !== 'undefined');
  var expectedYear = $('#deliverableYear').find('select').val();
  var isCompletedWithoutExpectedYear = ((!reportingActive) && isStatusComplete(statusId) && hasExpectedYear);

  // Validate the justification
  if (isStatusCancelled(statusId) || isStatusExtended(statusId) || isStatusPartiallyComplete(statusId)) {
    $statusDescription.slideDown(400);
    $statusDescription.find('label').html($('#status-' + statusId).html());
  } else {
    $statusDescription.slideUp(400);
  }

  if (reportingActive || upKeepActive) {
    // Validate the new extended year
    if (isDeliverableNew) {
      showNewExpectedComponent(isStatusExtended(statusId) && upKeepActive);
    } else {
      if ($('#actualYear').html() == '2021') {
        if (statusId == 2 || statusId == 3 || statusId == 4 || statusId == 5 || statusId == 6 || statusId == 7) {
          if (expectedYear < '2021') {
            showExpectedComponent(true, false);

            if (statusId == 2 && newExpectedYear == '-1') {
              showNewExpectedComponent(false, true);
            } else {
              showNewExpectedComponent(true, true);
            }
            if (statusId == 3 && newExpectedYear == '-1') {
              showNewExpectedComponent(false, true);
            } else {
              showNewExpectedComponent(true, true);
            }
            if (statusId == 4 && newExpectedYear == '2021') {
              showExpectedComponent(true, true);
            }
          } else {
            if (statusId == 4) {
              showNewExpectedComponent(true, true);
            } else {
              showNewExpectedComponent(false, false);
              showExpectedComponent(true, false);
            }
          }
        }
      } else {
        if (newExpectedYear == '-1') {
          showNewExpectedComponent(false, true);
        } else {
          showNewExpectedComponent(true, true);
        }
        showExpectedComponent(true, true);
      }
    }
  }
}

function showNewExpectedComponent(state, overlay) {
  var $newExpectedYearBlock = $('#newExpectedYear');
  var $yearOverlay = $('#deliverableYear .overlay');
  if (state) {
    $newExpectedYearBlock.show();
    $yearOverlay.show();
  } else {
    $newExpectedYearBlock.hide();
    if (isDeliverableNew) {
      $yearOverlay.hide();
    }
  }

  if (overlay) {
    $yearOverlay.show();
  } else {
    $yearOverlay.hide();
  }
}

function showExpectedComponent(state, overlay) {
  var $expectedYearBlock = $('#deliverableYear');
  var $yearOverlay = $('#newExpectedYear .overlay');
  if (state) {
    $expectedYearBlock.show();
    $yearOverlay.show();
  } else {
    $expectedYearBlock.hide();
    if (isDeliverableNew) {
      $yearOverlay.hide();
    }
  }

  if (overlay) {
    $yearOverlay.show();
  } else {
    $yearOverlay.hide();
  }
}

function validateDeliverableStatus() {
  var selectYearExpected = $('#deliverableYear select.yearExpected');
  var selectYearNewExpected = $('#newExpectedYear select.yearNewExpected');
  var selectedStatus = $statuses.val();
  var expectedYear = $('#deliverableYear').find('select').val();
  var newExpectedYear = $('#newExpectedYear').find('select').val();
  // New Expected year should be greater than current reporting cycle year
  if (reportingActive || upKeepActive) {
    if (isDeliverableNew && reportingActive) {
      if ($('#actualYear').html() == '2021') {
        selectYearExpected.find('option[value="2022"]').prop("disabled", true);

        if (expectedYear == '2021') {
          // $statuses.find('option[value="2"]').prop("disabled", true); // Disable On-going
          $statuses.find('option[value="3"]').prop("disabled", false); // Enable Completed
          $statuses.find('option[value="4"]').prop("disabled", true); // Disable Extended
          $statuses.find('option[value="5"]').prop("disabled", false); // Enable Cancelled
          $statuses.find('option[value="7"]').prop("disabled", false); // Enable Partially complete
        }
      }
    } else {
      if ($('#actualYear').html() != '2021') {
        $statuses.find('option').prop("disabled", true); // Disable All
      } else {
        var statusReadOnly = $('input[name="deliverable.deliverableInfo.status"]').siblings('p');

        if (statusReadOnly) {
          statusReadOnly.html('Ready to be reported on');
        }
        
        $statuses.find('option[value="2"]').html('Ready to be reported on');
        selectYearExpected.find('option[value="2022"]').prop("disabled", true);
        selectYearNewExpected.find('option[value="2017"]').prop("disabled", true);
        selectYearNewExpected.find('option[value="2018"]').prop("disabled", true);
        selectYearNewExpected.find('option[value="2019"]').prop("disabled", true);
        selectYearNewExpected.find('option[value="2020"]').prop("disabled", true);
        selectYearNewExpected.find('option[value="2022"]').prop("disabled", true);

        if (expectedYear < '2021') {
          if (selectedStatus == 2) {
            if (expectedYear != '-1' && newExpectedYear != '-1') {
              // $statuses.find('option[value="2"]').prop("disabled", true); // Disable On-going
              $statuses.find('option[value="3"]').prop("disabled", false); // Enable Completed
              $statuses.find('option[value="4"]').prop("disabled", true); // Disable Extended
              $statuses.find('option[value="5"]').prop("disabled", false); // Enable Cancelled
              $statuses.find('option[value="7"]').prop("disabled", false); // Enable Partially complete
            } else {
              // $statuses.find('option[value="2"]').prop("disabled", true); // Disable On-going
              $statuses.find('option[value="3"]').prop("disabled", true); // Disable Completed
              $statuses.find('option[value="4"]').prop("disabled", false); // Enable Extended
              $statuses.find('option[value="5"]').prop("disabled", true); // Disable Cancelled
              $statuses.find('option[value="7"]').prop("disabled", true); // Disable Partially complete
            }
          } else {
            // $statuses.find('option[value="2"]').prop("disabled", true); // Disable On-going
            $statuses.find('option[value="3"]').prop("disabled", false); // Enable Completed
            $statuses.find('option[value="4"]').prop("disabled", true); // Disable Extended
            $statuses.find('option[value="5"]').prop("disabled", false); // Enable Cancelled
            $statuses.find('option[value="7"]').prop("disabled", false); // Enable Partially complete
          }
        } else {
          // $statuses.find('option[value="2"]').prop("disabled", true); // Disable On-going
          $statuses.find('option[value="4"]').prop("disabled", true); // Disable Extended
        }
      }
    }
    $('#deliverableYear .overlay').show();
    $statuses.trigger("change");
  }
}

function checkFundingItems(block) {
  console.log(block);
  var items = $(block).find('.fundingSources').length;
  console.log(items);
  if (items == 0) {
    $(block).parent().find('p.emptyText').fadeIn();
  } else {
    $(block).parent().find('p.emptyText').fadeOut();
  }
}

function checkGenderItems(block) {
  console.log(block);
  var items = $(block).find('.genderLevel').length;
  console.log(items);
  if (items == 0) {
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
  if (state.id == -1) {
    return state.text;
  }
  var $state = $("#fundingSource-" + state.id).clone(true);
  return $state;

};

function formatStateGenderType(state) {
  if (state.id == -1) {
    return;
  }
  var $state = $("#genderLevel-" + state.id).clone(true);
  return $state;
}

function hasDeliverableRule(rule, arrValues) {
  var result = 0;
  $.each(arrValues, function (index, value) {
    if (($.inArray(value, getDeliverableTypesByRule(rule))) != -1) {
      result++;
    }
  });
  return (result > 0);
}

function getDeliverableTypesByRule(rule) {
  var result = $('#getDeliverableTypesByRule-' + rule).val().split(", ");
  return result;
}

function selectKeyOutput() {
  var $keyOutputList = $('.selectList').find('.keyOutput');
  var keyOutputOptions = $keyOutputList.find('option')
  var keyOutputsLength = keyOutputOptions.length;

  if (keyOutputsLength == 2) {
    var optionValue = keyOutputOptions[1].value;
    $keyOutputList.val(optionValue);
    $keyOutputList.trigger('change');
  }
}

var deliverablePartnersModule = (function () {

  function init() {
    console.log('Starting deliverablePartnersModule');

    updateInstitutionSelects();

    attachEvents();
  }

  function attachEvents() {
    if ($('#actualPhase').html() == 'true') {
      loadQualityAssessmentStatus(deliverableAjaxURL, deliverableArrName);
    }

    // On change institution
    $('select.partnerInstitutionID').on('change', changePartnerInstitution);
    // On remove a deliverable partner item
    $('.removePartnerItem').on('click', removePartnerItem);
    // On add a new deliverable partner Item
    $('.addPartnerItem').on('click', addPartnerItem);
  }

  function loadQualityAssessmentStatus(ajaxURL, arrName) {
    var currentCrpID = $('#actualCrpID').html();

    if (currentCrpID != '-1') {

      var finalAjaxURL = ajaxURL + currentCrpID;

      $.ajax({
        url: baseURL + finalAjaxURL,
        async: false,
        success: function (data) {
          if (data && Object.keys(data).length != 0) {
            var newData = data[arrName].map(function (x) {
              var arr = [];

              arr.push(x.id);
              arr.push(x.assessmentStatus);
              arr.push(x.updatedAt);

              return arr;
            });
            updateQualityAssessmentStatusData(newData);
          }
        }
      });
    }
  }

  function updateQualityAssessmentStatusData(data) {
    data.map(function (x) {
      if (x[0] == $('#deliverableID').html()) {
        var container = document.getElementsByClassName('containerTitleElementsProject')[0];
        var element = document.getElementById('qualityAssessedIcon');
        var date, status, statusClass;

        switch (x[1]) {
          case 'pending':
            status = 'Pending assessment';
            statusClass = 'pending-mode';
            break;
          case 'pending_crp':
            status = 'Pending CRP response';
            statusClass = 'pending-mode';
            break;
          case 'in_progress':
            status = 'Quality Assessed (Requires 2nd assessment)';
            statusClass = 'qualityAssessed-mode';
            break;
          case 'quality_assessed':
            date = new Date((x[2].split('T')[0])).toDateString();
            status = 'Deliverable was Quality Assessed on ' + date;
            statusClass = 'qualityAssessed-mode';
            break;
          case 'automatically_validated':
            date = new Date((x[2].split('T')[0])).toDateString();
            status = 'Deliverable was Automatically Validated on ' + date;
            statusClass = 'autoValidated-mode';
            break;
          default:
            break;
        }

        if (element) {
          var pTag = document.createElement('p');
          var text = document.createTextNode(status);

          element.innerHTML = '';
          element.classList.remove('pendingForReview-mode');
          element.classList.add(statusClass);
          pTag.style.margin = '0';
          pTag.appendChild(text);
          element.appendChild(pTag);

          if ((x[1] == 'quality_assessed') || (x[1] == 'automatically_validated') || (x[1] == 'pending')) {
            var pMessageTag = document.createElement('p');
            if (x[1] == 'quality_assessed') {
              var textMessage = document.createTextNode('As this item has already been Quality Assessed, no changes are recommended');
            } else if (x[1] == 'automatically_validated') {
              var textMessage = document.createTextNode('As this item has already been Automatically Validated, no changes are recommended');
              element.style.backgroundPosition = '655px';
            } else {
              var textMessage = document.createTextNode('As this item is being assessed by the SMO, no changes are recommended');
            }

            pMessageTag.classList.add('messageQAInfo');
            pMessageTag.appendChild(textMessage);
            container.appendChild(pMessageTag);
          }
        }
      }
    });
  }

  function addPartnerItem() {
    var $listBlock = $('.otherDeliverablePartners');
    var $newItem = $('#deliverablePartnerItem-template').clone(true).removeAttr('id');
    $listBlock.append($newItem);
    $newItem.show();
    updateIndexes();
  }

  function removePartnerItem() {
    var $item = $(this).parents('.deliverablePartnerItem');
    $item.hide(500, function () {
      $item.remove();
      updateIndexes();
    });
  }

  function changePartnerInstitution() {
    var $deliverablePartner = $(this).parents('.deliverablePartnerItem');
    var $usersBlock = $deliverablePartner.find('.usersBlock');
    var typeID = $deliverablePartner.find('input.partnerTypeID').val();
    var isResponsible = (typeID == 1);
    // Clean users list
    $usersBlock.empty();
    // Get new users list
    var $newUsersBlock = $('#partnerUsers .institution-' + this.value + ' .users-' + typeID).clone(true);
    // Show them
    $usersBlock.append($newUsersBlock.html());
    // Update indexes
    if (!isResponsible) {
      updateIndexes();
    }
  }

  function updateIndexes() {
    $('.otherDeliverablePartners .deliverablePartnerItem').each(function (i, partner) {

      // Update deliverable partner index
      $(partner).setNameIndexes(1, i);

      $(partner).find('.deliverableUserItem').each(function (j, user) {
        var personID = $(user).find('input[type="checkbox"]').val();
        var customID = "jsGenerated-" + i + "-" + j + "-" + personID;
        // Update user index
        $(user).setNameIndexes(2, j);
        // Update user checks/radios labels and inputs ids
        $(user).find('input[type="checkbox"]').attr('id', customID);
        $(user).find('label.checkbox-label').attr('for', customID);
      });

    });

    updateInstitutionSelects()
  }

  function updateInstitutionSelects() {
    var $listBlock = $('.otherDeliverablePartners');
    var $institutionsSelects = $listBlock.find('select.partnerInstitutionID');

    // Get selected values
    selectedValues = $institutionsSelects.map(function (i, select) {
      return select.value;
    });

    $institutionsSelects.each(function (i, select) {
      // Enable options
      $(select).find('option').prop('disabled', false);

      // Disable only the selected values
      $.each(selectedValues, function (key, val) {
        if (select.value != val) {
          $(select).find('option[value="' + val + '"]').prop('disabled', true);
        }
      });
    });

    // Reset Select2
    setTimeout(function () {
      $institutionsSelects.select2({
        width: '98%'
      });
    });

  }

  return {
    init: init
  }
})();