$(document).ready(init);

function init() {

  /* Declaring Events */
  attachEvents();

}

function attachEvents() {
  $('#systemReset button').on('click', function() {
    const message = $.trim($('textarea.systemReset-message').val());
    const diffTime = $.trim($('input.systemReset-diffTime').val());

    if (!message || !diffTime) {
      notificationError('Please enter both message and time before sending System Reset notification.');
      return;
    }

    const pushData = {
        message: message,
        diffTime: diffTime,
        notificationType: 'systemReset'
    }
    $.ajax({
        url: baseURL + '/sendNotification.do',
        data: pushData,
      success: function() {
        $('#systemReset').find('textarea, input').val('');
      }
    });

    const slackMessage = {
        "text": "MARLO AICCRA Restart Message",
        "attachments": [
          {
              "color": "#f1c40f",
              "author_name": $('#userInfo .name, #userInfoOld .name').first().text().trim(),
              "text": pushData.message,
              "fields": [
                {
                    "title": "Time",
                    "value": pushData.diffTime,
                    "short": true
                }
              ],
              "footer": globalThis.location.href,
          }
        ]
    };
    postMessageToSlack(JSON.stringify(slackMessage));

  });

  $('#simpleNotification button').on('click', function() {
    const message = $.trim($('textarea.simpleNotification-message').val());
    if (!message) {
      notificationError('Please enter a message before sending Simple Message notification.');
      return;
    }

    const pushData = {
      message: message,
      notificationType: 'simple'
    }
    $.ajax({
      url: baseURL + '/sendNotification.do',
      data: pushData,
      success: function() {
        $('#simpleNotification').find('textarea, input').val('');
      }
    });
  });
}