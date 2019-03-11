$(document).ready(init);

function init() {

  // Add Select2
  $('form select').select2({
    width: '100%'
  });

  // Add file uploads
  setFileUploads();

  attachEvents();

  $('select[class*="elementType-"]').on("addElement removeElement", function(event,id,name) {
    var $parent = $(this).parents('.keyPartnership');
    // Other Please Specify
    if(id == 6) {
      if(event.type == "addElement") {
        $parent.find('.block-pleaseSpecify').slideDown();
      }
      if(event.type == "removeElement") {
        $parent.find('.block-pleaseSpecify').slideUp();
      }
    }
  });

}

function attachEvents() {

  // Add a Key Partnership
  $('.addKeyPartnership').on('click', addKeyPartnership);

  // Remove a Key Partnership
  $('.removeKeyPartnership').on('click', removeKeyPartnership);

  // Add a Cross CGIAR Partnership
  $('.addCrossPartnership').on('click', addCrossPartnership);

  // Remove a Cross CGIAR Partnership
  $('.removeCrossPartnership').on('click', removeCrossPartnership);

}

// Key partnerships functions

function addKeyPartnership() {
  var $list = $(this).parents("form").find('.listKeyPartnerships');
  var $item = $('#keyPartnerships-template').clone(true).removeAttr("id");
  $list.append($item);

  // Add select
  $item.find('select').select2({
    width: '100%'
  });

  $item.find('textarea').setTrumbowyg();

  $item.show('slow');
  updateIndexes();
}

function removeKeyPartnership() {
  var $item = $(this).parents('.keyPartnership');
  $item.hide(function() {
    $item.remove();
    updateIndexes();
  });
}

function updateIndexes() {
  $(".listKeyPartnerships").find(".keyPartnership").each(function(i,element) {
    $(element).setNameIndexes(1, i);
    $(element).find(".index").html(i + 1);
  });
}

// Cross CGIAR partnerships functions

function addCrossPartnership() {
  var $list = $(this).parents("form").find('.listCrossParnterships');
  var $item = $('#crossPartnerships-template').clone(true).removeAttr("id");
  $list.append($item);

  // Add select
  $item.find('select').select2({
    width: '100%'
  });

  $item.find('textarea').setTrumbowyg();

  $item.show('slow');
  updateCrossIndexes();
}

function removeCrossPartnership() {
  var $item = $(this).parents('.crossPartnership');
  $item.hide(function() {
    $item.remove();
    updateCrossIndexes();
  });
}

function updateCrossIndexes() {
  $(".listCrossParnterships").find(".crossPartnership").each(function(i,element) {
    $(element).setNameIndexes(1, i);
    $(element).find(".index").html(i + 1);
  });
}

/**
 * File upload (blueimp-tmpl)
 */
function setFileUploads() {

  var containerClass = ".fileUploadContainer";
  var $uploadBlock = $(containerClass);
  var $fileUpload = $uploadBlock.find('.upload');

  $fileUpload.fileupload({
      dataType: 'json',
      start: function(e) {
        var $ub = $(e.target).parents(containerClass);
        $ub.addClass('blockLoading');
      },
      stop: function(e) {
        var $ub = $(e.target).parents(containerClass);
        $ub.removeClass('blockLoading');
      },
      done: function(e,data) {
        var r = data.result;
        console.log(r);
        if(r.saved) {
          var $ub = $(e.target).parents(containerClass);
          $ub.find('.textMessage .contentResult').html(r.fileFileName);
          $ub.find('.textMessage').show();
          $ub.find('.fileUpload').hide();
          // Set file ID
          $ub.find('input.fileID').val(r.fileID);
          // Set file URL
          $ub.find('.fileUploaded a').attr('href', r.path + '/' + r.fileFileName)
        }
      },
      fail: function(e,data) {
        var $ub = $(e.target).parents(containerClass);
        $ub.animateCss('shake');
      },
      progressall: function(e,data) {
        var $ub = $(e.target).parents(containerClass);
        var progress = parseInt(data.loaded / data.total * 100, 10);
        $ub.find('.progress').fadeIn(100);
        $ub.find('.progress .progress-bar').width(progress + '%');
        if(progress == 100) {
          $ub.find('.progress').fadeOut(1000, function() {
            $ub.find('.progress .progress-bar').width(0);
          });
        }
      }
  });

  // Prepare data
  $fileUpload.bind('fileuploadsubmit', function(e,data) {

  });

  // Remove file event
  $uploadBlock.find('.removeIcon').on('click', function() {
    var $ub = $(this).parents(containerClass);
    $ub.find('.textMessage .contentResult').html("");
    $ub.find('.textMessage').hide();
    $ub.find('.fileUpload').show();
    $ub.find('input.fileID').val('');
    // Clear URL
    $ub.find('.fileUploaded a').attr('href', '');
  });

}