$(document).ready(init);

function init() {
  console.log($('input[name=deliverableID]').val());

// Set file upload (blueimp-tmpl)
  uploadFile($('.fileAssuranceContent'), $('.fileAssuranceContent').find('.uploadFileAssurance'), 'Assurance');
  uploadFile($('.fileDictionaryContent'), $('.fileDictionaryContent').find('.uploadFileDictionary'), 'Dictionary');
  uploadFile($('.fileToolsContent'), $('.fileToolsContent').find('.uploadFileTools'), 'Tools');

  // Validate url format
  $(".urlLink").on("change keyup", function() {
    var $this = $(this).val();
    if($this.indexOf("http://") == 0 || $this.indexOf("https://") == 0) {
      $(this).removeClass("fieldError");
    } else {
      $(this).addClass("fieldError");
    }

    if($(this).val().length == 0) {
      $(this).removeClass("fieldError");
    }
  });

}

function uploadFile($uploadBlock,$fileUpload,type) {
  $fileUpload.fileupload({
      dataType: 'json',
      formData: {
          fileType: type,
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
          var v = r.fileFileName.length > 20 ? r.fileFileName.substr(0, 20) + ' ... ' : r.fileFileName;
          $uploadBlock.find('.textMessage .contentResult').html(v);
          $uploadBlock.find('.textMessage .contentResult').attr("title", r.fileFileName);
          $uploadBlock.find('.textMessage').show();
          $uploadBlock.find('.fileUpload').hide();
          // Set file ID
          $uploadBlock.find('input.fileID').val(r.fileID);
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
    $uploadBlock.find('input.fileID').val('');
  });
}