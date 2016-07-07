var timeoutID;
$(document).ready(function() {

  $('input, textarea').on('keyup', changeDetected);

});

function autoSave() {
  $.ajax({
      dataType: 'json',
      url: baseURL + '/autosaveWriter.do',
      data: {
        autoSave: JSON.stringify($('form').serializeObject())
      },
      beforeSend: function() {
      },
      success: function(data) {
        var notyOptions = jQuery.extend({}, notyDefaultOptions);
        notyOptions.text = 'Succesfully saved';
        notyOptions.type = 'alert';
        notyOptions.layout = 'topCenter';
        notyOptions.animation = {
            open: 'animated fadeInDown',
            close: 'animated fadeOutUp'
        };
        noty(notyOptions);
      },
      complete: function() {
      },
      error: function(e) {
        var notyOptions = jQuery.extend({}, notyDefaultOptions);
        notyOptions.text = 'Auto save error';
        notyOptions.type = 'error';
        notyOptions.layout = 'topCenter';
        notyOptions.animation = {
            open: 'animated fadeInDown',
            close: 'animated fadeOutUp'
        };
        noty(notyOptions);
      }
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