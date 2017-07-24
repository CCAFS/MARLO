$(document).ready(init);

function init() {

  // Harvest metadata from URL
  $("#fillMetadata .checkButton, #fillMetadata .updateButton").on("click", syncMetadata);

  // Unsync metadata
  $("#fillMetadata .uncheckButton").on("click", unSyncDeliverable);

}

/**
 * Harvest metadata functions
 */

function syncMetadata() {
  getOCSMetadata();
}

function setMetadata(data) {
  console.log(data);

  // Text area & Inputs fields
  $.each(data, function(key,value) {
    var $parent = $('.metadataElement-' + key);
    var $input = $parent.find(".metadataValue");
    var $spanSuggested = $parent.find(".metadataSuggested");
    var $hide = $parent.find('.hide');
    if(value) {
      $input.val(value);
      $spanSuggested.text("Suggested: " + value).animateCss("flipInY");
      $parent.find('textarea').autoGrow();
      $input.attr('readOnly', true);
      $hide.val("true");
    } else {
      $input.attr('readOnly', false);
      $spanSuggested.text("");
      $hide.val("false");
    }

    // Date picker
    if($input.hasClass('hasDatepicker')) {
      console.log(key + " in date");
      $input.datepicker("destroy");
    }
  });

  syncDeliverable();

}

function syncDeliverable() {
  // Hide Sync Button & dissemination channel
  $('#fillMetadata .checkButton, .disseminationChannelBlock').hide('slow');
  // Show UnSync & Update Button
  $('#fillMetadata .unSyncBlock').show();
  // Set hidden inputs
  $('#fillMetadata input:hidden').val(true);
  // Dissemination URL
  $('.financeCode').attr('readOnly', true);
  // Update component
  $(document).trigger('updateComponent');
}

function unSyncDeliverable() {
  // Show metadata
  $('[class*="metadataElement"]').each(function(i,e) {
    var $parent = $(e);
    var $input = $parent.find('.metadataValue');
    var $spanSuggested = $parent.find(".metadataSuggested");
    var $hide = $parent.find('.hide');
    $spanSuggested.text("");
    $input.attr('readOnly', false);
    $hide.val("false");
  });

  // Show Sync Button & dissemination channel
  $('#fillMetadata .checkButton, .disseminationChannelBlock').show('slow');
  // Hide UnSync & Update Button
  $('#fillMetadata .unSyncBlock').hide();
  // Set hidden inputs
  $('#fillMetadata input:hidden').val(false);
  // Dissemination URL
  $('.financeCode').attr('readOnly', false);

  // Update component
  $(document).trigger('updateComponent');
}

function getOCSMetadata() {
  var currentCode = $('input.financeCode').val();
  // Ajax to service
  $.ajax({
      'url': baseURL + '/ocsService.do',
      'data': {
        ocsCode: $('input.financeCode').val()
      },
      beforeSend: function() {
        $(".financeCode").addClass('input-loading');
        $('.financeCode-message').text("");
      },
      success: function(data) {
        if(data.json) {
          var agreement = data.json;
          // Principal Investigator
          agreement.pInvestigator = agreement.researcher.name;
          // Donor
          agreement.donorName = agreement.donor.name;
          // Setting Metadata
          setMetadata(agreement);

          // Setting Countries
          $.each(agreement.countries, function(i,e) {
            addCountry(e.code, e.description);
          });

        } else {
          $('.financeCode-message').text("Agreement " + currentCode + " not found");
        }
      },
      complete: function() {
        $(".financeCode").removeClass('input-loading');
      },
      error: function() {
        $('#metadata-output').empty().append("Invalid URL for searching metadata");
      }
  });
}