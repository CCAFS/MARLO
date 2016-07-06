$(document).ready(function() {
  var $form = $('form')[0];

  function autoSave() {
    $.ajax({
        url: baseURL + 'autoSave.do',
        data: $form.serializeObject()
    }).done(function(msg) {
      console.log("Data Saved: " + msg);
    });
  }

});
