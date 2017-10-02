var isSynced, $syncButtons, $unSyncButtons, $fsSelected, syncing;
var allowExtensionDate;
$(document).ready(initSync);

function initSync() {
  console.log('Sync FS started');
  syncing = false;
  allowExtensionDate = true;
  isSynced = $('#isSynced').val() === "true";
  $syncButtons = $(".fillMetadata .checkButton, .fillMetadata .updateButton");
  $unSyncButtons = $(".fillMetadata .uncheckButton");

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
  $fsSelected = $(this).parents('.fsSync');
  getOCSMetadata();
}

function setMetadata(data) {
  console.log(data);

  // Clear inputs hidden from selects disabled
  $fsSelected.find('input.selectHiddenInput').remove();

  // Text area & Inputs fields
  $.each(data, function(key,value) {
    var $parent = $fsSelected.find('.metadataElement-' + key);
    var $input = $parent.find(".metadataValue");
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
  $fsSelected.find('.fillMetadata .checkButton, .disseminationChannelBlock').hide('slow');
  // Show UnSync & Update Button
  $fsSelected.find('.fillMetadata .unSyncBlock').show();
  // Hide some components
  $fsSelected.find('.syncVisibles').hide();
  $fsSelected.find('.unsyncVisibles').show();
  // Set hidden inputs
  $fsSelected.find('.fillMetadata input:hidden').val(true);
  // Dissemination URL
  $fsSelected.find('.financeCode').attr('readOnly', true);
  // Update Grant total amount triggering the currency inputs
  $fsSelected.find('.currencyInput').trigger('keyup');
  // Update Funding source last update
  var today = new Date();
  var dd = today.getDate();
  var mm = today.getMonth() + 1; // January is 0!

  var yyyy = today.getFullYear();
  if(dd < 10) {
    dd = '0' + dd;
  }
  if(mm < 10) {
    mm = '0' + mm;
  }
  $fsSelected.find('.fundingSourceSyncedDate').val(yyyy + '-' + mm + '-' + dd);
  $fsSelected.find('.lastDaySync').show();
  $fsSelected.find('.lastDaySync span').html($.datepicker.formatDate("M dd, yy", new Date(yyyy, mm - 1, dd)));
  // Hide Date labels
  $fsSelected.find('.dateLabel').addClass('disabled');
  // Update component
  $(document).trigger('updateComponent');

  // Set isSynced parameter
  isSynced = true;
}

function unSyncFundingSource() {
  $fsSelected = $(this).parents('.fsSync');
  // Show metadata
  $fsSelected.find('[class*="metadataElement"]').each(function(i,e) {
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
  $fsSelected.find('.fillMetadata .checkButton, .disseminationChannelBlock').show('slow');
  // Hide UnSync & Update Button
  $fsSelected.find('.fillMetadata .unSyncBlock').hide();
  // Hide grand amount
  $fsSelected.find('#grantTotalAmount').hide();
  // Show some components
  $fsSelected.find('.syncVisibles').show();
  $fsSelected.find('.unsyncVisibles').hide();
  // Set hidden inputs
  $fsSelected.find('.fillMetadata input:hidden').val(false);
  // Dissemination URL
  $fsSelected.find('.financeCode').attr('readOnly', false);
  // show Date labels
  $fsSelected.find('.dateLabel').removeClass('disabled');
  // Hide Last update label
  $fsSelected.find('.lastDaySync').hide();
  // Set datepicker
  settingDate(".startDateInput", ".endDateInput", ".extensionDateInput");

  // Update component
  $(document).trigger('updateComponent');

  // Set isSynced parameter
  isSynced = false;
}

function getOCSMetadata() {
  var currentCode = $fsSelected.find('input.financeCode').val();
  var source = $fsSelected.find('.radioSyncType:checked').val();
  var serviceURL = baseURL;
  if(!currentCode || syncing) {
    notificationError("You must enter a finance code.");
    return
    

  }
  // Setting source
  if(source == 1) {
    // OCS
    currentCode.toUpperCase();
    serviceURL += '/ocsService.do'
  }else if(source == 2){
    // MARLO CRPs
    currentCode.replace(/\D/g,''); // Remove all non-digits
    serviceURL += '/projectSync.do'
  }
  
 // Ajax to service
  $.ajax({
      'url': serviceURL,
      'data': {
        ocsCode: currentCode
      },
      beforeSend: function() {

        $fsSelected.find('.loading.syncBlock').show();

        $fsSelected.find(".financeCode").addClass('input-loading');
        $fsSelected.find('.financeCode-message').text("");
        // $syncButtons.addClass('button-disabled');
        syncing = true;
      },
      success: function(data) {
        if(data.json) {
          var agreement = data.json;
          console.log(agreement);
          // Extension date validation
          if(!allowExtensionDate) {
            agreement.endDate = agreement.extensionDate;
          }
          // Principal Investigator
          agreement.pInvestigator = agreement.researcher.name;
          // Donors
          if(agreement.originalDonor) {
            agreement.originalDonorName = agreement.originalDonor.name;
          }
          if(agreement.directDonor) {
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
            agreement.contractStatusId = 3;
          } else if(agreement.contractStatus == "O") {
            agreement.contractStatusId = 2;
          } else if(agreement.contractStatus == "S") {
            agreement.contractStatusId = 2;
          }
          // Set Countries
          $fsSelected.find('#countryList ul').empty();
          $.each(agreement.countries, function(i,e) {
            addCountry(e.code, e.description, e.percentage);
          });

          // Set Grand Amount
          $fsSelected.find('#grantTotalAmount .amount').text(setCurrencyFormat(agreement.grantAmount));
          $fsSelected.find('#grantTotalAmount').show();

          // Set Metadata
          setMetadata(agreement);

        } else {
          $fsSelected.find('.financeCode-message').text("Agreement " + currentCode + " not found");
        }
      },
      complete: function() {
        $fsSelected.find('.loading.syncBlock').hide();
        $fsSelected.find(".financeCode").removeClass('input-loading');
        $syncButtons.removeClass('button-disabled');
        syncing = false;
      },
      error: function() {
        $fsSelected.find('.metadata-output').empty().append("Invalid URL for searching metadata");
      }
  });
}