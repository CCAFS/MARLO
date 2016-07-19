$(document).ready(init);

function init() {

  /* Declaring Events */
  attachEvents();

}

function attachEvents() {
  $('#systemReset button').on('click', function() {
    var pushData = {
        message: $('textarea.systemReset-message').val(),
        diffTime: $('input.systemReset-diffTime').val(),
    }
    globalChannel.trigger("client-system-reset", pushData);
    $('#systemReset').find('textarea, input').val('');
    /*
     * $.ajax({ url: baseURL + '/sendNotification.do', data: pushData, success: resetSuccess });
     */
  });

  $('#simpleNotification button').on('click', function() {
    var pushData = {
      message: $('textarea.simpleNotification-message').val()
    }
    globalChannel.trigger("client-simple-notification", pushData);
    $('#simpleNotification').find('textarea, input').val('');
  });
}
