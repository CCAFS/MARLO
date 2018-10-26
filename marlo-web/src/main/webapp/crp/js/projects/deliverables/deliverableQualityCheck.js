$(document).ready(init);
function init() {
  var qualityAssurance = $(".qualityAssurance");
  var dataDictionary = $(".dataDictionary");
  var dataTools = $(".dataTools");

  // Set file upload (blueimp-tmpl)
  uploadFile($('.fileAssuranceContent'), $('.fileAssuranceContent').find('.uploadFileAssurance'), 'Assurance');
  uploadFile($('.fileDictionaryContent'), $('.fileDictionaryContent').find('.uploadFileDictionary'), 'Dictionary');
  uploadFile($('.fileToolsContent'), $('.fileToolsContent').find('.uploadFileTools'), 'Tools');

  $("a[data-toggle='tab']").on('shown.bs.tab', function(e) {
    $("#indexTab").val($(this).attr("index"));
    $(".radio-block").each(function(i,e) {
      showHiddenTags(e);
    });
  });

  // Validate url format
  $(".urlLink").on("change keyup", function() {
    var $this = $(this).val();
    if($this.indexOf("http://") == 0 || $this.indexOf("https://") == 0) {
      $(this).removeClass("fieldError");
    } else {
      $(this).addClass("fieldError");
    }
    checkGolData();

    if($(this).val().length == 0) {
      $(this).removeClass("fieldError");
    }
  });
  $(qualityAssurance).on("change", changeOptions);
  $(dataDictionary).on("change", changeOptions);
  $(dataTools).on("change", changeOptions);
  $(".fileID").on("change", checkGolData);
  // checkGolData();

  // Validate FAIR Complain
  // checkFAIRCompliant();
}

function changeOptions() {
  checkGolData();
  showFileLink(this);
}

function showFileLink($this) {
  if($($this).val() == "2") {
    $($this).parents(".question").find(".fileOptions").show("slow");
  } else {
    $($this).parents(".question").find(".fileOptions").hide("slow");
  }
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
          checkGolData();
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
    checkGolData();
  });
}

/** FAIR Functions* */

function checkFiandable() {
  var $fairCompliant = $('.fairCompliant.findable');
  var isDisseminated = $('.type-findable label.radio-checked').classParam('value');

  // If the deliverables is disseminated
  var channelSelected = $('select.disseminationChannel').val();
  var inputURL = $('input.deliverableDisseminationUrl').val();
  // Channel selected is OTHER and valid URL
  if((channelSelected == "other") && (inputURL != "")) {
    $fairCompliant.addClass('achieved');
  }

  // If is Sync
  if($('#fillMetadata input:hidden').val() === "true") {
    $fairCompliant.addClass('achieved');
  }

  if(isDisseminated == "false") {
    $fairCompliant.addClass('not-achieved');
  }
}

function checkAccessible() {
  var $fairCompliant = $('.fairCompliant.accessible');
  var isOpenAccessVal = $('.type-accessible label.radio-checked').classParam('value');

  // Is this deliverable Open Access?
  if(isOpenAccessVal == "true") {
    $fairCompliant.addClass('achieved');
  }
  if(isOpenAccessVal == "false") {
    $fairCompliant.addClass('not-achieved');
  }
}

function checkInteroperable() {
  var $fairCompliant = $('.fairCompliant.interoperable');
  // If the deliverables is disseminated and already connected with MARLO
  var channelSelected = $('select.disseminationChannel').val();
  if(channelSelected != "-1") {
    // If is Synced
    if($('#fillMetadata input:hidden').val() === "true") {
      $fairCompliant.addClass('achieved');
    }
  }
}

function checkReusable() {
  var $fairCompliant = $('.fairCompliant.reusable');
  var adoptedLicenseVal = $('.type-license label.radio-checked').classParam('value');

  // If has the deliverable adopted a license
  if(adoptedLicenseVal == "true") {
    // If is different to "Other"
    var inputChecked = $('input[name="deliverable.deliverableInfo.license"]:checked').val();
    if(!(typeof inputChecked === "undefined") && (inputChecked != "OTHER")) {
      $fairCompliant.addClass('achieved');
    } else {
      // Does this license allow modifications?
      if($('input.otherLicense').val() != "") {
        $fairCompliant.addClass('achieved');
      }
    }
  }
  if(adoptedLicenseVal == "false") {
    $fairCompliant.addClass('not-achieved');
  }

}

function checkFAIRCompliant() {
  console.log('Check FAIR compliant');
  $('.fairCompliant').removeClass('achieved not-achieved');
  checkFiandable();
  checkAccessible();
  checkInteroperable();
  checkReusable();
}

/** Quality Check * */
function checkQualityAssurance() {
  var qualityAssurance = $("input.qualityAssurance:checked");
  var value = qualityAssurance.val();
  if(value == "1") {
    return 25;
  } else if(value == "2") {
    var fileInput = $(qualityAssurance).parents(".question").find(".fileID");
    var url = $(qualityAssurance).parents(".question").find(".urlLink");
    if((fileInput.val() != "" && fileInput.val() != "-1")
        || (url.val().indexOf("http://") == 0 || url.val().indexOf("https://") == 0) && url.val() != "") {
      return 50;
    } else {
      return 0;
    }
  } else if(value == "3") {
    return 5;
  }
}

function checkDictionary() {
  var dataDictionary = $("input.dataDictionary:checked");
  var value = dataDictionary.val();
  if(value == "1") {
    return 25;
  } else if(value == "2") {
    var fileInput = $(dataDictionary).parents(".question").find(".fileID");
    var url = $(dataDictionary).parents(".question").find(".urlLink");
    if((fileInput.val() != "" && fileInput.val() != "-1")
        || (url.val().indexOf("http://") == 0 || url.val().indexOf("https://") == 0) && url.val() != "") {
      return 50;
    } else {
      return 0;
    }
  } else if(value == "3") {
    return 5;
  }
}

function checkCollection() {
  var dataTools = $("input.dataTools:checked");
  var value = dataTools.val();
  if(value == "1") {
    return 25;
  } else if(value == "2") {
    var fileInput = $(dataTools).parents(".question").find(".fileID");
    var url = $(dataTools).parents(".question").find(".urlLink");
    if((fileInput.val() != "" && fileInput.val() != "-1")
        || (url.val().indexOf("http://") == 0 || url.val().indexOf("https://") == 0) && url.val() != "") {
      return 50;
    } else {
      return 0;
    }
  } else if(value == "3") {
    return 5;
  }
}

function checkGolData() {
  var $red = $("#red");
  var $yellow = $("#yellow");
  var $green = $("#green");
  /* remove class */
  $red.removeClass("highlightRed");
  $yellow.removeClass("highlightYellow");
  $green.removeClass("highlightGreen");
  /* Calculate */
  var item1 = parseInt(checkQualityAssurance());
  var item2 = parseInt(checkDictionary());
  var item3 = parseInt(checkCollection());
  var suma = item1 + item2 + item3;
  if(suma <= 74) {
    $red.addClass("highlightRed");
  } else if(suma >= 75 && suma <= 104) {
    $yellow.addClass("highlightYellow");
  } else if(suma >= 105) {
    $green.addClass("highlightGreen");
  }
}