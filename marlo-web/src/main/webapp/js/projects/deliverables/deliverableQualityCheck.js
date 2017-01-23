$(document).ready(init);

function init() {

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

  // Validate FAIR Complain
  checkFAIRCompliant();
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

/** FAIR Functions* */

function checkFiandable() {
  // If the deliverables is disseminated
  if($('.findable input').val() == "true") {
    $('.fairCompliant.findable').addClass('achieved');
  }
}

function checkAccessible() {
  if($('.accessible input').val() == "true") {
    $('.fairCompliant.accessible').addClass('achieved');
  }
}

function checkInteroperable() {
  // If the deliverables is disseminated
  if($('.findable input').val() == "true") {
    var channelSelected = $('select.disseminationChannel').val();
    // If is disseminated in CGSpace or Dataverse
    if((channelSelected == "2") || (channelSelected == "3")) {
      // If is dissemination URL filled correctly
      if($('input.deliverableDisseminationUrl').val() != "") {
        $('.fairCompliant.interoperable').addClass('achieved');
      }
    }
  }
}

function checkReusable() {
  // If has the deliverable adopted a license
  if($('.license input').val() == "true") {
    // If is different to "Other"
    if($('input[name="deliverable.license"]:checked').val() != "12") {
      $('.fairCompliant.reusable').addClass('achieved');
    } else {
      // Does this license allow modifications?
      if($('.licenceModifications input').val() == "true") {
        $('.fairCompliant.reusable').addClass('achieved');
      }
    }
  }

}

function checkFAIRCompliant() {
  console.log('Check FAIR compliant');
  $('.fairCompliant').removeClass('achieved');
  checkFiandable();
  checkAccessible();
  checkInteroperable();
  checkReusable();
}