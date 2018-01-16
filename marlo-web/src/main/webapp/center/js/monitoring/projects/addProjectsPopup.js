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
    $('.radioSyncType:checked').trigger('change');
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
  
  $('span.addProjectButton').on('click', function(){
    if(validating){
      return
    }
    
    var syncType = $('.radioSyncType:checked').val();
    console.log(syncType);
    if(syncType != "-1") {
      if(!$syncCode.val() ) { // || !($syncCode.hasClass('fieldChecked'))
        notificationError("You must enter a valid OCS/Project Code or chosee Manually")
        $('.loading').fadeOut(200);
        return 
      }else{
        if($syncCode.hasClass('fieldChecked')){
          console.log('already checked');
          submitCreateProject();
        }else{
          validateSyncCode(true);
        }
      }
    }else{
      submitCreateProject();
    }
    
  });

  $("#addProjectsModal form").submit(function() {
    $('.loading').fadeIn();
  });

  $syncCode.on('keyup change', changeSyncCode);

});

function changeSyncCode(e) {
  // Validate empty field
  if(!($syncCode.val())){
    $syncCode.removeClass('fieldChecked fieldError');
  }
  if(timeoutSyncCode) {
    clearTimeout(timeoutSyncCode);
  }
  // Start a timer that will execute sync code validation function
  timeoutSyncCode = setTimeout(function() {
    validateSyncCode(false);
  }, 1000);
}

function validateSyncCode(createProject) {
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
          syncTypeID: syncTypeID,
          phaseID: phaseID
      },
      beforeSend: function(xhr,opts) {
        $syncCode.removeClass('fieldChecked fieldError');
        $syncCode.addClass('input-loading');
        validating = true;
        $('span.addProjectButton').addClass('disabled');
      },
      success: function(data) {
        console.log(data)
        if(data.message.status) {
          $syncCode.addClass('fieldChecked');
          $syncCode.removeClass('fieldError');
          
          // Create Project
          if(createProject){
            submitCreateProject();
          }
        } else {
          $syncCode.addClass('fieldError');
          $syncCode.removeClass('fieldChecked');
        }
      },
      complete: function() {
        $syncCode.removeClass('input-loading');
        validating = false;
        $('span.addProjectButton').removeClass('disabled');
      },
      error: function(e) {
      }
  });

}

function submitCreateProject(){
  $("#addProjectsModal form button[name='submit']").trigger('click');
}