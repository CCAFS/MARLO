var timeoutSyncCode, validating;
var $modal, $modalProjects, $syncCode;
$(document).ready(function() {

  console.log('addProjectPopup.js');

  $modal = $('#addProjectsModal');
  $syncCode = $('input[name="syncCode"]');
  $syncCode.attr('autocomplete', 'off');
  validating = false;

  // This event fires immediately when the show instance method is called.
  $modal.on('show.bs.modal', function(e) {

  });

  $('.radioSyncType').on('change', function() {
    // Clean syncCode
    $syncCode.val('');
    $syncCode.removeClass('fieldChecked fieldError');
    
    // Show syncCode
    if($(this).hasClass('requiredCode')) {
      $('.syncCodeBlock').show();
    } else {
      $('.syncCodeBlock').hide();
    }
    
    // Setting placeholder
    if($(this).val() == 1) {
      // OCS
      $syncCode.attr('placeholder', 'e.g. G119');
    } else if($(this).val() == 2) {
      // MARLO CRPs
      $syncCode.attr('placeholder', 'e.g. P56');
    }
  });

  $("form").submit(function() {
    if(validating){
      event.preventDefault();
      return
    }
    
    $('.loading').fadeIn();
    var syncType = $('.radioSyncType:checked').val();
    console.log(syncType);
    if(syncType != "-1") {
      if(!$syncCode.val() || !($syncCode.hasClass('fieldChecked'))) {
        notificationError("You must enter a valid OCS/Project Code or chosee Manually")
        $('.loading').fadeOut(200);
        event.preventDefault();
      }
    }
  });

  $syncCode.on('keyup change', changeSyncCode);

});

function changeSyncCode(e) {
  if(timeoutSyncCode) {
    clearTimeout(timeoutSyncCode);
  }
  // Start a timer that will execute sync code validation function
  timeoutSyncCode = setTimeout(function() {
    validateSyncCode();
  }, 1000);
}

function validateSyncCode() {
  var syncCode = $syncCode.val();
  var syncTypeID = $('.radioSyncType:checked').val();

  // Setting syncCode
  if(syncTypeID == 1) {
    // OCS
    syncCode = syncCode.toUpperCase();
  } else if(syncTypeID == 2) {
    // MARLO CRPs
    syncCode = syncCode.replace(/\D/g, ''); // Remove all non-digits
  }
  
  if(!syncCode){
    return
  }

  $.ajax({
      url: baseURL + '/validateSyncCode.do',
      data: {
          syncCode: syncCode,
          syncTypeID: syncTypeID
      },
      beforeSend: function(xhr,opts) {
        $syncCode.removeClass('fieldChecked fieldError');
        $syncCode.addClass('input-loading');
        validating = true;
      },
      success: function(data) {
        console.log(data)
        if(data.message.status) {
          $syncCode.addClass('fieldChecked');
          $syncCode.removeClass('fieldError');
        } else {
          $syncCode.addClass('fieldError');
          $syncCode.removeClass('fieldChecked');
        }
      },
      complete: function() {
        $syncCode.removeClass('input-loading');
        validating = false;
      },
      error: function(e) {
      }
  });

}