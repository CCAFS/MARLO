/**
 * * MARLO Pusher Initializing
 */

Pusher.logToConsole = debugMode;
var pusher = new Pusher(PUSHER_KEY, {
  authEndpoint: baseURL + '/pusherAutentication.do',
  encrypted: true,
  params: {
    
  }
});


var presenceChannel = pusher.subscribe("presence-mousemoves");
presenceChannel.bind('pusher:subscription_error', function(status) {
  // console.log(status);
});
presenceChannel.bind('pusher:subscription_succeeded', function(members) {
  var me = presenceChannel.members.me;
  console.log(me);
})


var globalChannel = pusher.subscribe('presence-global');
globalChannel.bind('system-reset', function(data) {
  showSystemResetMessage(data);
});

function showSystemResetMessage(data) {
  var diffTime = data.diffTime
  if(diffTime <= 0){
    return
  }
  var $timer = $('#timer-content').clone(true).removeAttr('id');
  $timer.find('.message').html(data.message);
  $timer.find('.countdown').countdown({
      date: +(new Date) + (1000*diffTime),
      render: function(data) {
        $(this.el).text(this.leadingZeros(data.min, 2) + " min " + this.leadingZeros(data.sec, 2) + " sec");
        if(this.leadingZeros(data.min, 1) == 0) {
          $(this.el).addClass('ended animated infinite flash');
        } else {
          $(this.el).removeClass('ended animated infinite flash');
        }
      }
  });
  showFullNotification($timer);

}
  
function showFullNotification(timer) {
  noty({
      text: $(timer),
      type: 'alert',
      dismissQueue: true,
      layout: 'center',
      theme: 'relax',
      modal: true,
      buttons: [
        {
            addClass: 'btn btn-primary',
            text: 'Roger that',
            onClick: function($noty) {$noty.close();}
        }
      ],
      callback: {
        afterClose: function() {
          document.cookie = 'messageMin=true';
          showMinNotification($(timer));
        },
      },
  });
}

function showMinNotification(timer) {
  $(timer).find('.countdown').addClass('small');
  $(timer).find('.message').hide();
  noty({
      text: $(timer),
      theme: 'relax',
      layout: 'topCenter',
      animation: {
          open: 'animated fadeInUp',
          close: 'animated fadeOutUp'
      }
  });
}
