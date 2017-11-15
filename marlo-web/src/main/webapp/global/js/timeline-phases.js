$(document).ready(function() {

  var $example = $('#example');
  var $frame = $example.find('.frame');
  window.frr = $frame;
  var sly = new Sly($frame, {
      horizontal: 1,
      itemNav: 'forceCentered',
      activateMiddle: 1,
      smart: 1,
      activateOn: 'click',
      mouseDragging: 0,
      touchDragging: 0,
      releaseSwing: 1,
      startAt: currenPhaseIndex,
      scrollBar: $example.find('.scrollbar'),
      scrollBy: 0,
      pagesBar: $example.find('.pages'),
      activatePageOn: 'click',
      speed: 200,
      moveBy: 600,
      elasticBounds: 1,
      dragHandle: 1,
      dynamicHandle: 1,
      clickBar: 1,

      // Buttons
      forward: $example.find('._forward'),
      backward: $example.find('._backward'),
      prev: $example.find('.backward'),
      next: $example.find('.forward'),
      prevPage: $example.find('.prevPage'),
      nextPage: $example.find('.nextPage')
  }).init();

  sly.on('active', function(eventName) {
    console.log('active');
    var phaseID = $frame.find('li.active').attr('id').split('-')[1];
    setPhaseID(phaseID);
  });

  $('.phaseBox').on('click', function(e) {
    console.log(e.which);
    var phaseID = $(this).attr('id').split('-')[1];
  });

});

// Build slide_id menu
function init_slide_menu(data) {
  console.log('EVENT=' + data.type, data);
  var phaseID = (data.unique_id).split('-')[1];

  setPhaseID(phaseID);
}

function setPhaseID(phaseID) {
  var currentURL = new Uri(window.location.href);
  console.log(currentURL.deleteQueryParam('phaseID').addQueryParam('phaseID', phaseID));

  // Execute a change of phase
  $.ajax({
      url: baseURL + '/changePhase.do',
      method: 'POST',
      data: {
        phaseID: phaseID
      },
      beforeSend: function() {
        $('.timeline-loader').fadeIn();
      },
      success: function(data) {
        // $('.timeline-loader').fadeOut();
        // location.reload();
        window.location.href = currentURL;
      },
      complete: function() {
      }
  });
}