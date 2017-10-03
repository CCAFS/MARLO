var $modal, $modalProjects;
$(document).ready(function() {

  console.log('addProjectPopup.js');

  $modal = $('#addProjectsModal');

  // This event fires immediately when the show instance method is called.
  $modal.on('show.bs.modal', function(e) {

  });

  $('.radioSyncType').on('change', function() {
    if($(this).hasClass('requiredCode')) {
      $('.syncCodeBlock').show();
    } else {
      $('.syncCodeBlock').hide();
    }
  });

  $("form").submit(function() {
    $('.loading').fadeIn();
    var syncType = $('.radioSyncType:checked').val();
    console.log(syncType);
    if(syncType != "-1") {
      if(!$('input[name="syncCode"]').val()) {
        notificationError("You must enter a valid OCS/Project Code or chosee Manually")
        $('.loading').fadeOut(200);
        event.preventDefault();
      }
    }
  });

});