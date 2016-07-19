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

    /*
     * $.ajax({ url: baseURL + '/sendNotification.do', data: pushData, success: resetSuccess });
     */

  });
}

function resetSuccess() {
  $('#systemReset').find('textarea, input').val('');
}
