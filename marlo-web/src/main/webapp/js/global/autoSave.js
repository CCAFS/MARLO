var timeoutAutoSave;
var $draftTag, $editedBy, $cancelButton;

$(document).ready(function() {

  $draftTag = $('[name="save"]').find('.draft');
  $editedBy = $('#lastUpdateMessage');
  $cancelButton = $('.button-cancel');

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
        $draftTag.text('... Saving');
      },
      success: function(data) {
        if(data.status.status) {
          successNotification('Successfully saved a draft version');
          $draftTag.text('(Draft Version)').addClass('animated flipInX');
          $editedBy.find('.datetime').text(data.status.activeSince);
          $editedBy.find('.modifiedBy').text(data.status.modifiedBy);
          $cancelButton.css('display', 'inline-block');
        } else {
          errorNotification('Auto save error' + data.status.statusMessage);
        }
      },
      complete: function() {
      },
      error: function(e) {
        errorNotification('Auto save ' + e.statusText);
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
  notyOptions.text = msj;
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
  }, 10000);

}