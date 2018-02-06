$(document).ready(function() {
    // We can attach the `file select` event to all file inputs on the page
    $(document).on('change', ':file', function() {
      var input = $(this),
      numFiles = input.get(0).files ? input.get(0).files.length : 1,
          label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
      input.trigger('fileselect', [numFiles, label]);
    });

    // We can watch for our custom `fileselect` event like this
    $(':file').on('fileselect', function(event, numFiles, label) {

      var input = $(this).parents('.input-group').find(':text'),
      log = numFiles > 1 ? numFiles + ' files selected' : label;

      if( input.length ) {
        input.val(log);
      } else {
        if( log ){alert(log);}
      }
    });


    /**
     * File upload (blueimp-tmpl)
     */

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

    /** End File upload* */
});
