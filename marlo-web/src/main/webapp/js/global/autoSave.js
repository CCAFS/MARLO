$(document).ready(function() {

});

function autoSave() {
  $.ajax({
      method: 'GET',
      url: baseURL + '/autosaveWriter.do',
      dataType: 'json',
      data: {autoSave : JSON.stringify($('form').serializeObject())}      
  }).done(function(msg) {
    console.log("Data Saved: " + msg);
  });
}