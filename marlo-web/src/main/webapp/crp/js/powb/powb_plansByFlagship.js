$(document).ready(function() {

  console.log('Init');

  setFileUploads();

});

/**
 * File upload (blueimp-tmpl)
 */
function setFileUploads() {

  var $uploadBlock = $('.fileUploadContainer');
  var $fileUpload = $uploadBlock.find('.upload');
  $fileUpload.fileupload({
      dataType: 'json',
      start: function(e) {
        var $ub = $(e.target).parents('.fileUploadContainer');
        $ub.addClass('blockLoading');
      },
      stop: function(e) {
        var $ub = $(e.target).parents('.fileUploadContainer');
        $ub.removeClass('blockLoading');
      },
      done: function(e,data) {
        var r = data.result;
        console.log(r);
        if(r.saved) {
          var $ub = $(e.target).parents('.fileUploadContainer');
          $ub.find('.textMessage .contentResult').html(r.fileFileName);
          $ub.find('.textMessage').show();
          $ub.find('.fileUpload').hide();
          // Set file ID
          $ub.find('input.fileID').val(r.fileID);
          // Set file URL
          $ub.find('.fileUploaded a').attr('href', r.path + '/' + r.fileFileName)
        }
      },
      progressall: function(e,data) {
        var progress = parseInt(data.loaded / data.total * 100, 10);
      }
  });

  // Prepare data
  $fileUpload.bind('fileuploadsubmit', function(e,data) {

  });

  // Remove file event
  $uploadBlock.find('.removeIcon').on('click', function() {
    var $ub = $(this).parents('.fileUploadContainer');
    $ub.find('.textMessage .contentResult').html("");
    $ub.find('.textMessage').hide();
    $ub.find('.fileUpload').show();
    $ub.find('input.fileID').val('');
    $ub.find('input.outcomeID').val('');
    // Clear URL
    $ub.find('.fileUploaded a').attr('href', '');
  });

}