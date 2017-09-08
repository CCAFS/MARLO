var timeline_options = {
    script_path: "",
    // scale_factor: 3, // How many screen widths wide should the timeline be
    initial_zoom: 4,
    layout: "landscape", // portrait or landscape
    timenav_position: "bottom", // timeline on top or bottom
    // optimal_tick_width: 100, // optimal distance (in pixels) between ticks on axis
    // timenav_height: 50,
    timenav_height_percentage: 72, // Overrides timenav height as a percentage of the screen
    timenav_height_min: 50, // Minimum timenav height
    marker_height_min: 25, // Minimum Marker Height
    marker_width_min: 250, // Minimum Marker Width
    marker_padding: 1, // Top Bottom Marker Padding
    start_at_slide: currenPhaseIndex,
    menubar_height: 50,
    // skinny_size: 650,
    // relative_date: true, // Use momentjs to show a relative date from the slide.text.date.created_time
    // field
    use_bc: false, // Use declared suffix on dates earlier than 0
    // animation
    duration: 1000,
    ease: TL.Ease.easeInOutQuint,
    // interaction
    dragging: true,
    trackResize: true,
    map_type: "stamen:toner-lite",
    slide_padding_lr: 0, // padding on slide of slide
    slide_default_fade: "0%", // landscape fade
    // default_bg_color: '#f5f5f5',
    api_key_flickr: "", // Flickr API Key
    language: "en"
}

$(document).ready(function() {
  var timeline = new TL.Timeline('timeline-phases', dataObject, timeline_options);
  window.onresize = function(event) {
    timeline.updateDisplay();
  }

  // Set event handlers
  timeline.on('change', init_slide_menu);
});

// Build slide_id menu
function init_slide_menu(data) {
  console.log('EVENT=' + data.type, data);
  var phaseID = (data.unique_id).split('-')[1];
  // Execute a change of phase
  $.ajax({
      url: baseURL + '/changePhase.do',
      method: 'POST',
      data: {
        phaseID: phaseID
      },
      beforeSend: function() {
      },
      success: function(data) {
      },
      complete: function() {
        location.reload();
      }
  });
}