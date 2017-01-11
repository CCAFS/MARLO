$(document).ready(init);

function init() {
  console.log($('input[name=deliverableID]').val());

// Set file upload (blueimp-tmpl)
  var $uploadBlock = $('.fileAssuranceContent');
  var $fileUpload = $uploadBlock.find('.uploadFileAssurance')
  $fileUpload.fileupload({
      dataType: 'json',
      formData: {
          fileType: 'Assurance',
          deliverableID: $('input[name=deliverableID]').val()
      },
      start: function(e) {
        $uploadBlock.addClass('blockLoading');
      },
      stop: function(e) {
        $uploadBlock.removeClass('blockLoading');
      },
      done: function(e,data) {
        var r = data.result;
        console.log(r);
        if(r.saved) {
          $uploadBlock.find('.textMessage .contentResult').html(r.fileFileName);
          $uploadBlock.find('.textMessage').show();
          $uploadBlock.find('.fileUpload').hide();
          // Set file ID
          $('input#fileID').val(r.fileID);
        }
      },
      progressall: function(e,data) {
        var progress = parseInt(data.loaded / data.total * 100, 10);
      }
  });

  $uploadBlock.find('.removeIcon').on('click', function() {
    $uploadBlock.find('.textMessage .contentResult').html("");
    $uploadBlock.find('.textMessage').hide();
    $uploadBlock.find('.fileUpload').show();
    $('input#fileID').val('');
  });
}