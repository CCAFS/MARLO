$(document).ready(function() {

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