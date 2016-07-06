var timeoutID;
$(document).ready(function() {

  $('input, textarea').on('keyup', changeDetected);

});

function autoSave() {
  $.ajax({
      method: 'GET',
      url: baseURL + '/autosaveWriter.do',
      dataType: 'json',
      data: {
        autoSave: JSON.stringify($('form').serializeObject())
      }
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