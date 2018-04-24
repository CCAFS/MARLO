var caseStudiesName;
var $elementsBlock;

$(document).ready(init);

function init() {

  // Add Events
  attachEvents();

  // Add select2
  addSelect2();

  // Add file uploads
  setFileUploads();

}

function attachEvents() {

  // On change radio buttons
  $('input[class*="radioType-"]').on('change', onChangeRadioButton);

  // On select element
  $('select[class*="elementType-"]').on('change', onSelectElement);

  // On click remove button
  $('[class*="removeElementType-"]').on('click', onClickRemoveElement);

}

function onChangeRadioButton() {
  var thisValue = this.value === "true";
  var radioType = $(this).classParam('radioType');
  if(thisValue) {
    $('.block-' + radioType).slideDown();
  } else {
    $('.block-' + radioType).slideUp();
  }
}

function onSelectElement() {
  var $select = $(this);
  var $option = $select.find('option:selected');
  var elementType = $(this).classParam('elementType');
  var maxLimit = $(this).classParam('maxLimit');
  var $list = $('.listType-' + elementType);
  var counted = $list.find('li').length;

  // Verify limit if applicable
  if((maxLimit > 0) && (counted >= maxLimit)) {
    return;
  }

  // Verify repeated selection
  if($list.find('.elementRelationID[value="' + $option.val() + '"]').length) {
    $select.val('-1').trigger('change.select2');
    $select.parent().animateCss('shake');
    var notyOptions = jQuery.extend({}, notyDefaultOptions);
    notyOptions.text = 'It was already selected';
    noty(notyOptions);
    return;
  }

  // Clone the new element
  var $element = $('#relationElement-' + elementType + '-template').clone(true).removeAttr("id");
  // Set attributes
  $element.find('.elementRelationID').val($option.val());
  $element.find('.elementName').html($option.text());
  // Show the element
  $element.appendTo($list).hide().show('slow', function() {
    $select.val('-1').trigger('change.select2');
  });

  // Update indexes
  $list.find('li.relationElement').each(function(i,element) {
    $(element).setNameIndexes(1, i);
  });
}

function onClickRemoveElement() {
  var removeElementType = $(this).classParam('removeElementType');
  var $parent = $(this).parent();
  var $list = $('.listType-' + removeElementType);
  $parent.slideUp(function() {
    $parent.remove();

    // Update indexes
    $list.find('li.relationElement').each(function(i,element) {
      $(element).setNameIndexes(1, i);
    });
  });
}

function addSelect2() {
  $('form select').select2({
    width: '100%'
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