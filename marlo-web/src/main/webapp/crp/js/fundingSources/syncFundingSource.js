var isSynced, $syncButtons, $unSyncButtons, syncing;
$(document).ready(initSync);

function initSync() {
  console.log('Sync FS started');
  syncing = false;
  isSynced = $('#isSynced').val() === "true";
  $syncButtons = $("#fillMetadata .checkButton, #fillMetadata .updateButton");
  $unSyncButtons = $("#fillMetadata .uncheckButton");

  if(!isSynced) {
    // Set Datepicker
    settingDate(".startDateInput", ".endDateInput", ".extensionDateInput");
  }

  // Harvest metadata from URL
  $syncButtons.on("click", syncMetadata);

  // Unsync metadata
  $unSyncButtons.on("click", unSyncFundingSource);
}

/**
 * Harvest metadata functions
 */

function syncMetadata() {
  getOCSMetadata();
}

function setMetadata(data) {
  console.log(data);

  // Clear inputs hidden from selects disabled
  $('input.selectHiddenInput').remove();

  // Text area & Inputs fields
  $.each(data, function(key,value) {
    var $parent = $('.metadataElement-' + key);
    var $input = $parent.find(".metadataValue");
    var $spanSuggested = $parent.find(".metadataSuggested");
    var $hide = $parent.find('.hide');

    $input.val(value);
    $spanSuggested.text("Recorded in CIAT-OCS as: " + value).animateCss("flipInY");
    $parent.find('textarea').autoGrow();
    $input.attr('readOnly', true);
    $hide.val("true");

    // Date picker
    if($input.hasClass('hasDatepicker')) {
      console.log(key + " is date");
      $input.trigger('change');
      $input.datepicker("destroy");
    }

    // Select2
    if($input.hasClass('select2-hidden-accessible')) {
      console.log(key + " is select2");
      var inputValue = $input.val();
      var inputName = $input.attr("name");
      var $hiddenInput =
          "<input type='hidden' class='selectHiddenInput' name='" + inputName + "' value='" + inputValue + "'>"
      $input.prop('disabled', true);
      $input.trigger('change');
      $input.parent().append($hiddenInput);
    }

  });

  syncFundingSource();

}

function syncFundingSource() {
  // Hide Sync Button & dissemination channel
  $('#fillMetadata .checkButton, .disseminationChannelBlock').hide('slow');
  // Show UnSync & Update Button
  $('#fillMetadata .unSyncBlock').show();
  // Hide some components
  $('.syncVisibles').hide();
  // Set hidden inputs
  $('#fillMetadata input:hidden').val(true);
  // Dissemination URL
  $('.financeCode').attr('readOnly', true);
  // Update Grant total amount triggering the currency inputs
  $('.currencyInput').trigger('keyup');
  // Update Funding source last update
  var today = new Date();
  var dd = today.getDate();
  var mm = today.getMonth()+1; // January is 0!

  var yyyy = today.getFullYear();
  if(dd<10){
      dd='0'+dd;
  }
  if(mm<10){
      mm='0'+mm;
  }
  $('.fundingSourceSyncedDate').val(yyyy+'-'+mm+'-'+dd);
  $('.lastDaySync').show();
  $('.lastDaySync span').html($.datepicker.formatDate( "M dd, yy", new Date(yyyy, mm -1, dd) ));
  // Hide Date labels
  $('.dateLabel').addClass('disabled');
  $('.dateLabel').removeClass('fieldError');
  // Update component
  $(document).trigger('updateComponent');

  // Set isSynced parameter
  isSynced = true;
  $('#isSynced').val(isSynced);
}

function unSyncFundingSource() {
  // Show metadata
  $('[class*="metadataElement"]').each(function(i,e) {
    var $parent = $(e);
    var $input = $parent.find('.metadataValue');
    var $spanSuggested = $parent.find(".metadataSuggested");
    var $hide = $parent.find('.hide');

    $spanSuggested.text("");
    $input.attr('readOnly', false);
    $hide.val("false");

    // Date picker
    if($input.hasClass('hasDatepicker')) {
      // console.log(key + " is date");
    }

    // Select2
    if($input.hasClass('select2-hidden-accessible')) {
      $input.prop('disabled', false);
      $input.trigger('change');
    }
    $('input.selectHiddenInput').remove();

  });

  // Show Sync Button & dissemination channel
  $('#fillMetadata .checkButton, .disseminationChannelBlock').show('slow');
  // Hide UnSync & Update Button
  $('#fillMetadata .unSyncBlock').hide();
  // Hide grand amount
  $('#grantTotalAmount').hide();
  // Show some components
  $('.syncVisibles').show();
  // Set hidden inputs
  $('#fillMetadata input:hidden').val(false);
  // Dissemination URL
  $('.financeCode').attr('readOnly', false);
  // show Date labels
  $('.dateLabel').removeClass('disabled');
  // Hide Last update label
  $('.lastDaySync').hide();
  // Set datepicker
  settingDate(".startDateInput", ".endDateInput", ".extensionDateInput");

  // Hide End date
  if($('.extensionDateInput').val()){
    $('.extensionDateBlock').show();
    $('.endDateBlock .dateLabel').addClass('disabled');
  } else {
    $('.extensionDateBlock').hide();
    $('.endDateBlock .dateLabel').removeClass('disabled');
  }
  
  // Update component
  $(document).trigger('updateComponent');

  // Set isSynced parameter
  isSynced = false;
  $('#isSynced').val(isSynced);
}

function getOCSMetadata() {
  var currentCode = $('input.financeCode').val();
  if(!currentCode || syncing){
    return
  }

  // Ajax to service
  $.ajax({
      'url': baseURL + '/ocsService.do',
      'data': {
        ocsCode: $('input.financeCode').val(),
        phaseID: phaseID
      },
      beforeSend: function() {
        $('.loading.syncBlock').show();
        $(".financeCode").addClass('input-loading');
        $('.financeCode-message').text("");
        $syncButtons.addClass('button-disabled');
        syncing = true;
      },
      success: function(data) {
        if(data.json) {
          var agreement = data.json;
          console.log(agreement);
          // Principal Investigator
          agreement.pInvestigator = agreement.researcher.name;
          // Donors
          if(agreement.originalDonor){
            agreement.originalDonorName = agreement.originalDonor.name;
          }
          if(agreement.directDonor){
            agreement.directDonorName = agreement.directDonor.name;
          }
          // Validate extension date
          if(agreement.extensionDate == "1900-01-01") {
            agreement.extensionDate = "";
          }
          // Set Budget type
          if(agreement.fundingType == "BLR") {
            agreement.fundingTypeId = 3;
          } else if(agreement.fundingType == "W1/W2") {
            agreement.fundingTypeId = 1;
          } else if((agreement.fundingType == "W3R") || (agreement.fundingType == "W3U")) {
            agreement.fundingTypeId = 2;
          }
          // Set Agreement Status
          if(agreement.contractStatus == "C") { 
            agreement.agreementStatus = 3; 
          } else if(agreement.contractStatus =="O") {
            agreement.agreementStatus = 2; 
          } else if(agreement.contractStatus == "S") {
            agreement.agreementStatus = 2; 
          }
           

          // Set Funding Source Agreement Status
          if(agreement.extensionDate){
            agreement.agreementStatus = EXTENDED_STATUS;
            $('.extensionDateBlock').show();
            $('.endDateBlock .dateLabel').addClass('disabled');
          } else {
            agreement.agreementStatus = ON_GOING;
            $('.extensionDateBlock').hide();
            $('.endDateBlock .dateLabel').removeClass('disabled');
          }

          // Set Countries
          $('#countryList ul').empty();
          $.each(agreement.countries, function(i,e) {
            addCountry(e.code, e.description, e.percentage);
          });

          // Set Grand Amount
          $('#grantTotalAmount .amount').text(setCurrencyFormat(agreement.grantAmount));
          $('#grantTotalAmount').show();

          // Set Metadata
          setMetadata(agreement);

        } else {
          $('.financeCode-message').text("Agreement " + currentCode + " not found");
        }
      },
      complete: function() {
        $('.loading.syncBlock').hide();
        $(".financeCode").removeClass('input-loading');
        $syncButtons.removeClass('button-disabled');
        syncing = false;
      },
      error: function() {
        $('#metadata-output').empty().append("Invalid URL for searching metadata");
      }
  });
}