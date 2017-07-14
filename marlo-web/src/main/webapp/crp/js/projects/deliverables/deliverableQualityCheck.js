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
  // If the deliverables is disseminated
  if($('.findable input').val() == "true") {
    $fairCompliant.addClass('achieved');
  } else {
    $fairCompliant.addClass('not-achieved');
  }
}

function checkAccessible() {
  var $fairCompliant = $('.fairCompliant.accessible');
  // Is this deliverable Open Access?
  if($('.accessible input').val() == "true") {
    $fairCompliant.addClass('achieved');
  } else {
    $fairCompliant.addClass('not-achieved');
  }
}

function checkInteroperable() {
  var $fairCompliant = $('.fairCompliant.interoperable');
  // If the deliverables is disseminated
  if($('.findable input').val() == "true") {
    var channelSelected = $('select.disseminationChannel').val();
    // If is disseminated in CGSpace or Dataverse
    if((channelSelected == "cgspace") || (channelSelected == "dataverse")) {
      // If is dissemination URL filled correctly
      var inputURL = $('input.deliverableDisseminationUrl').val();
      if(inputURL != "") {

        // If CGSpace
        if((channelSelected == "cgspace")) {
          if((inputURL.indexOf("cgspace") >= 0) || (inputURL.indexOf("hdl") >= 0) || (inputURL.indexOf("handle") >= 0)) {
            $fairCompliant.addClass('achieved');
          }
        }
        // If Dataverse
        if((channelSelected == "dataverse")) {
          if(inputURL.indexOf("dataverse") >= 0) {
            $fairCompliant.addClass('achieved');
          }
        }
      }
    } else if((channelSelected == "other")) {
      // If other

    }

    // If is Synced
    if($('#fillMetadata input:hidden').val() === "true") {
      $fairCompliant.addClass('achieved');
    }
  }
}

function checkReusable() {
  var $fairCompliant = $('.fairCompliant.reusable');
  // If has the deliverable adopted a license
  if($('.license input').val() == "true") {
    // If is different to "Other"
    var inputChecked = $('input[name="deliverable.license"]:checked').val();
    if(!(typeof inputChecked === "undefined")
        && !((inputChecked == "OTHER") || (inputChecked == "CC_BY_ND") || (inputChecked == "CC_BY_NC_ND"))) {
      $fairCompliant.addClass('achieved');
    } else {
      // Does this license allow modifications?
      if(($('.licenceModifications input').val() == "true") && ($('input.otherLicense').val() != "")) {
        $fairCompliant.addClass('achieved');
      } else {
        $fairCompliant.addClass('not-achieved');
      }
    }
  } else {
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
  console.log("checking golden data");
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