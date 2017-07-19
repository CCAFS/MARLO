$(document).ready(init);

function init() {
  Dropzone.autoDiscover = false;
  // Create a dropzone to attach files
  addDropzone();

  // Select an storage option
  $("input[name='sharingOption']").on("change", function() {
    console.log($(this).val());
    if($(this).val() == "Externally") {
      $("#dragAndDrop").hide("slow");
      $("#fileURL").show("slow");
    } else {
      $("#fileURL").hide("slow");
      $("#dragAndDrop").show("slow");
    }
  });

  // This event is for remove file upload fields and inputs
  $(".removeInput").on("click", removeFileUploaded);

  // This event is when will be add one URL
  $(".addFileURL").on("click", addfileURL);

  // Set names to deliverable files already uploaded
  setDeliverableFilesIndexes();

}

function removeFileUploaded() {
  $(this).parent().fadeOut(function() {
    $(this).remove();
    setDeliverableFilesIndexes();
  });
}

function checkOption() {
  var $optionSelectd = $('#dataSharingOptions input[type=radio]:checked');
  $(".uploadBlock").hide();
  $optionSelectd.parent().next().fadeIn();
}

function addDropzone() {
  $("div#dragAndDrop").dropzone({
      init: initDropzone,
      fallback: fallBackDropzone, // Run this function if the browser not support dropzone plugin
      forceFallback: false,
      paramName: "file", // The name that will be used to transfer the file
      addRemoveLinks: true,
      params: {
          projectID: $("input[name^='projectID']").val(),
          deliverableID: $("input[name^='deliverableID']").val()
      },
      url: baseURL + '/uploadDeliverable.do',
      maxFilesize: 30,
      accept: function(file,done) {
        canAddFile = true;
        console.log(file.name);
        // Check is file is already uploaded
        if(checkDuplicateFile(file.name)) {
          $.prompt("Do you want replace this file (" + file.name + ") ?", {
              title: "There is already a file with the same name.",
              buttons: {
                  "Yes": true,
                  "No": false
              },
              submit: function(e,v,m,f) {
                if(v == true) {
                  done();
                  canAddFile = false;
                } else {
                  done("There is already a file with the same name");
                }
              }
          });
        } else {
          done();
        }
      }
  });
}

function initDropzone() {

  this.on("success", function(file,done) {
    console.log(done);
    var result = done.fileInfo;
    if(result.fileSaved) {
      file.hosted = "Locally";
      file.fileID = result.fileID;
      if(canAddFile) {
        addFileToUploaded(file);
      }
      this.removeFile(file);
    }
  });

}

function addfileURL(e) {
  var $target = $(e.target).parent();
  var $parent = $target.parent();
  var urlString = $target.find("input").val();
  if(checkUrl(urlString)) {
    var file = {
        "name": urlString,
        "hosted": $parent.find("input[type=radio]").val(),
        fileID: -1
    };
    addFileToUploaded(file);
    $target.find("input").val("http://");
  } else {
    var notyOptions = jQuery.extend({}, notyDefaultOptions);
    notyOptions.text = 'Invalid URL';
    noty(notyOptions);
  }
}

// This function run when the browser not support dropzone plugin
function fallBackDropzone() {
  $("#addFileInput").on("click", function() {
    var $newElement = $("#fileInputTemplate").clone(true).removeAttr("id");
    $(this).parent().prepend($newElement);
    $newElement.fadeIn();
  });
  $('#fileInputTemplate input').on("change", checkDublicateFileInput);
  $("#addFileInput").trigger("click", true);
}

function checkDublicateFileInput(e) {
  var $file = $(this);
  var fileName = $file.val().split('/').pop().split('\\').pop();

  if(checkDuplicateFile(fileName)) {
    $.prompt("Do you want replace this file (" + fileName + ") ?", {
        title: "There is already a file with the same name.",
        buttons: {
            "Yes": true,
            "No": false
        },
        submit: function(e,v,m,f) {
          if(v == true) {
          } else {
            $file.replaceWith($('#fileInputTemplate input').clone(true));
          }
        }
    });
  }
}

function addFileToUploaded(file) {
  var $newElement = $("#deliverableFileTemplate").clone(true).removeAttr("id");

  // Filling information obtained
  $newElement.find(".fileID").val(file.fileID);
  if(file.hosted == "Locally") {
    // $newElement.find("input[type='hidden'].fileHosted").remove();
    // $newElement.find("input[type='hidden'].fileLink").remove();
    $newElement.find(".fileSize").html((file.size / 1024).toFixed(1) + " KB");
  }
  $newElement.find("input.fileHosted").val(file.hosted);
  $newElement.find("input.fileName").val(file.name || file.link);
  if((file.name).length > 70) {
    file.name = (file.name).substring(0, 70) + "...";
  }

  $newElement.find(".fileName").html(file.name);
  $newElement.find(".fileFormat").html(file.hosted);

  $("#filesUploaded .text").hide();
  // Show file uploaded
  $("#filesUploaded ul").prepend($newElement);
  $newElement.show("slow");
  setDeliverableFilesIndexes();
}

function setDeliverableFilesIndexes() {
  $("form li.fileUploaded").each(function(i,element) {
    var elementName = "deliverable.files[" + i + "].";
    $(element).find("input[type='hidden'].fileID").attr("name", elementName + "id");
    $(element).find("input[type='hidden'].fileHosted").attr("name", elementName + "hosted");
    $(element).find("input[type='hidden'].fileName").attr("name", elementName + "name");

    var fileName = $(element).find(".fileName").html() || "Not Defined";
    if((fileName).length > 70) {
      $(element).find(".fileName").html((fileName).substring(0, 70) + "...");
    }
    $(element).find(".fileName").attr("title", fileName);

  });
  if($("form li.fileUploaded").length == 0) {
    $("#filesUploaded .text").show();
  }
}

function checkUrl(url) {
  return url.match(/(http|ftp|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\.,@?^=%&amp;:/~\+#]*[\w\-\@?^=%&amp;/~\+#])?/);
}

function checkDuplicateFile(fileName) {
  var alreadyExist = false;
  $("form .fileUploaded .fileName").each(function(i,element) {
    if(fileName == $(element).text()) {
      alreadyExist = true;
    }
  });
  return alreadyExist;
}