var timeoutID;
$(document).ready(function() {

  $('input, textarea').on('change', changeDetected);

});

function autoSave() {
  $.ajax({
      method: 'POST',
      url: baseURL + '/autosaveWriter.do',
      data: $('form').serializeObject()
  }).done(function(msg) {
    console.log("Data Saved: " + msg);
  });
}

function changeDetected(e) {
  if(timeoutID) {
    clearTimeout(timeoutID);
  }
  // Start a timer that will search when finished
  timeoutID = setTimeout(function() {
    autoSave();
  }, 1000);

}