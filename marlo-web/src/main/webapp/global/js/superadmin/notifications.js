$(document).ready(init);

function init() {

  /* Declaring Events */
  attachEvents();

}

function attachEvents() {
  $('#systemReset button').on('click', function() {
    var pushData = {
        message: $('textarea.systemReset-message').val(),
        diffTime: $('input.systemReset-diffTime').val() || 120,
    }
    // globalChannel.trigger("client-system-reset", pushData);
    $.ajax({
        url: baseURL + '/sendNotification.do',
        data: pushData,
        success: resetSuccess
    });
    $('#systemReset').find('textarea, input').val('');

    var slackMessage = {
        "text": "MARLO Restart Message",
        "attachments": [
          {
              "color": "#f1c40f",
              "author_name": $('#userInfo.name').text(),
              "text": pushData.message,
              "fields": [
                {
                    "title": "Time",
                    "value": pushData.diffTime,
                    "short": true
                }
              ],
              "footer": window.location.href,
          }
        ]
    };
    postMessageToSlack(JSON.stringify(slackMessage));

  });

  $('#simpleNotification button').on('click', function() {
    var pushData = {
      message: $('textarea.simpleNotification-message').val()
    }
    globalChannel.trigger("client-simple-notification", pushData);
    $('#simpleNotification').find('textarea, input').val('');
  });
}
function resetSuccess() {
}