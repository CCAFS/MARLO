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
    console.log('>> input : '+ $input.attr('class'));
    var $spanSuggested = $parent.find(".metadataSuggested");
    var $hide = $parent.find('.hide');

    $input.val(value);
    $spanSuggested.text("Suggested: " + value).animateCss("flipInY");
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
  // Update Funding source
  $('.fundingSourceSyncedDate').val(new Date());
  // Hide Date labels
  $('.dateLabel').addClass('disabled');
  // Update component
  $(document).trigger('updateComponent');
  
  // Set isSynced parameter
  isSynced = true;
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
  // Set datepicker
  settingDate(".startDateInput", ".endDateInput", ".extensionDateInput");

  // Update component
  $(document).trigger('updateComponent');
  
  // Set isSynced parameter
  isSynced = false;
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
        ocsCode: $('input.financeCode').val()
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
          // Principal Investigator
          agreement.pInvestigator = agreement.researcher.name;
          // Donor
          agreement.donorName = agreement.donor.name;
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
            agreement.contractStatusId = 3;
          } else if(agreement.contractStatus == "O") {
            agreement.contractStatusId = 2;
          } else if(agreement.contractStatus == "S") {
            agreement.contractStatusId = 2;
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