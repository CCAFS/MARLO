var timeoutAutoSave;
$(document).ready(function() {

  /* Event triggers */
  $(document).on('updateComponent', changeDetected);
  $(':input').on('keyup change', changeDetected);

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
        if(data.status.status) {
          successNotification('Successfully Saved');
          $('[name="save"]').find('.draft').text('(Draft Version)').addClass('animated flipInX');
        } else {
          errorNotification('Auto save error' + data.status.statusMessage);
        }
      },
      complete: function() {
      },
      error: function(e) {
        errorNotification('Auto save error');
      }
  });
}

function successNotification(msj) {
  var notyOptions = jQuery.extend({}, notyDefaultOptions);
  notyOptions.text = msj;
  notyOptions.type = 'alert';
  notyOptions.layout = 'topCenter';
  notyOptions.animation = {
      open: 'animated fadeInDown',
      close: 'animated fadeOutUp'
  };
  noty(notyOptions);
}

function errorNotification(msj) {
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

function changeDetected(e) {

  if(timeoutAutoSave) {
    clearTimeout(timeoutAutoSave);
  }
  // Start a timer that will search when finished
  timeoutAutoSave = setTimeout(function() {
    autoSave();
  }, 5000);

}