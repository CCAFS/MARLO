/**
 * * MARLO Pusher Initializing
 */
// Pusher.logToConsole = debugMode;

var pusher = new Pusher(PUSHER_KEY, {
  authEndpoint: baseURL + '/pusherAutentication.do',
  encrypted: true
});

var globalChannel = pusher.subscribe('presence-global');
globalChannel.bind('client-system-reset', function(data) {
  showSystemResetMessage(data);
});

globalChannel.bind('client-simple-notification', function(data) {
  noty({
    text: data.message,
    theme: 'relax',
    type : 'information',
    layout: 'topRight',
});
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


// Pusher Users Online

var currentSectionString = $('#currentSectionString').text();
var presenceChannel = pusher.subscribe("presence-"+currentSectionString);

if(editable && canEdit){
  var editingChannel = pusher.subscribe("presence-"+currentSectionString+"-canEdit");
  var myID;
  var myTurn;
  var savedBy;
  var currentUsers = [];
  
  editingChannel.bind('pusher:subscription_succeeded', function(members) {
    myID = members.myID;
    $.each(members.members, function(key, value) {
      currentUsers.push(key);
    });
    removeFromArray(currentUsers, myID);
    addToArray(currentUsers, myID);
    
    // If there is messages before save push a save action to the others members
    if($('#generalMessages ul.messages li').exists()) {
      pushSave();
    }
    
    checkEditingUsers();
  });
  
  editingChannel.bind('pusher:member_added', function(member) {
    addToArray(currentUsers, member.id);
    checkEditingUsers();
  });
  
  editingChannel.bind('pusher:member_removed', function(member) {
    removeFromArray(currentUsers, member.id);
    checkEditingUsers();
    
    if(savedBy){
      informSavedVersion(savedBy);
    }
    
  });
  
  editingChannel.bind('client-section-saved', function(data) {
    savedBy=data.name;
    informSavedVersion(savedBy);
  });

  function pushSave(){
    editingChannel.trigger("client-section-saved", {name: $('#userInfo .name').text()});
  }
  
  function informSavedVersion(name){
    if(myTurn == 1){
      $('#concurrenceMessage').find('.person').text(name); $('#concurrenceMessage').fadeIn();
    }
  }
  
  function checkEditingUsers(){
    myTurn = parseInt(currentUsers.indexOf(myID)) + 1
    checkStatus();
  }
  
  function checkStatus(){
    if(myTurn == 1){
      $('#concurrenceBlock').hide();
    }else{
      $('#concurrenceBlock').show();
    }
  }
  
  function removeFromArray(array, string){
    var index = array.indexOf(string);
    if (index > -1) {
      array.splice(index, 1);
    }
    return array;
  }
    
  function addToArray(array, string){
    return array.push(string);
  }
  
}
 


presenceChannel.bind('pusher:subscription_succeeded', function(members) {
  var me = presenceChannel.members.me;
  $('#mySessionID span').text(me.id + ' '+me.info.name);
  members.each(function(member) {
    if(me.id != member.id){
      createMousePointer(member);
    }
    createBadge(member);
  });
  updateUsersCount(presenceChannel.members.count);

});

presenceChannel.bind('pusher:member_added', function(member) {
  createBadge(member);
  createMousePointer(member);
  updateUsersCount(presenceChannel.members.count);
}); 

presenceChannel.bind('pusher:member_removed', function(member) {
  removeBadge(member);
  removeMousePointer(member);
  updateUsersCount(presenceChannel.members.count);
});

presenceChannel.bind('client-mouse-moved', function(data) {
  var $mousePointer = $('#mouse-'+data.sessionID);
  $mousePointer.css("top", data.y+"px").css("left", data.x+"px");
  $mousePointer.fadeIn();
});



// document.body.addEventListener('click', onMouseClick, true);
function onMouseClick(ev){
  ev = ev || window.event;
  presenceChannel.trigger("client-mouse-moved", {
    sessionID: presenceChannel.members.me.id,
    x: ev.pageX || ev.clientX,
    y: ev.pageY || ev.clientY
  });

}

function updateUsersCount(count){
  $('#usersOnline span').text(count);
}

function createMousePointer(member){
  var $mousePointer = $('#mouse-template').clone(true).attr('id','mouse-'+member.id );
  $mousePointer.find("small").text(member.info.name);
  $mousePointer.css("color", member.info.color);
  $mousePointer.appendTo('body');
}

function removeMousePointer(member){
  var $mousePointer = $('#mouse-'+member.id);
  $mousePointer.fadeOut('slow', function(){
    $mousePointer.remove();
  });
}

function createBadge(member){
  if(!($('#user-badge-'+member.id).exists())){
    var $badge = $('#user-badge-template').clone(true).attr('id','user-badge-'+member.id );
    $badge.text((member.info.name).replace(/[^A-Z]/g, ''));
    $badge.attr('title', member.info.name);
    $badge.css("background-color", member.info.color);
    $badge.appendTo('.breadcrumb .usersInfo');
    $badge.fadeIn('slow');
  }
}

function removeBadge(member){
  var $badge = $('#user-badge-'+member.id);
  $badge.fadeOut('slow', function(){
    $badge.remove();
  });
}
