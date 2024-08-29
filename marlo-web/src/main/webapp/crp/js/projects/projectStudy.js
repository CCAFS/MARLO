var caseStudiesName, multiInputStudies;
var $elementsBlock;

$(document).ready(init);

function init() {

  // Add Events
  attachEvents();

  // Add select2
  addSelect2();

  // Add Geographic Scope
  $('select.elementType-repIndGeographicScope ').on("addElement removeElement", function(event,id,name) {
    setGeographicScope(this);
  });
  setGeographicScope($('form select.elementType-repIndGeographicScope')[0]);

  // Add file uploads
  setFileUploads();

  // This function enables launch the pop up window
  popups();

  // Add amount format
  $('input.currencyInput').currencyInput();

  // Numeric field
  $('input.numericInput').numericInput();

  $('.ccRelevanceBlock input:radio').on('change', function() {
    var $commentBox = $(this).parents('.ccRelevanceBlock').find('.ccCommentBox');
    var id = this.value;
    console.log("CC", id);
    if((id == "1") || (id == "4")) {
      $commentBox.slideUp();
    } else {
      $commentBox.slideDown();
    }
  });

  feedbackAutoImplementation();

  bottonPading();
  
  multiInputStudies = $('.multiInput').find('span input[name*="link"]');
  checkHyperlinks();

  // Add mask to Alliance ID
  setMaskInputAllianceId();
  
  // Change counter value of Shared Cluster
  counterSharedCluster();

  // Update the dynamic visualization of the "Alliance" Tab after selecting in Key Contributors
  $('select.elementType-institution').on('change',updateAllianceTab);
  $('div.removeElementType-institution').on('click',updateAllianceTab);
}

function bottonPading(){
  $("[name='expectedStudy.projectExpectedStudyInfo.topLevelComments']").parent().css("padding-bottom", " 10px")  
}

function checkHyperlinks() {
  multiInputStudies.each((index, item) => {
    validateURL(item);
  });
}

function validateURL(item) {
  var url = item.value;
  var expression = /((([A-Za-z]{3,9}:(?:\/\/)?)(?:[-;:&=\+\$,\w]+@)?[A-Za-z0-9.-]+|(?:www.|[-;:&=\+\$,\w]+@)[A-Za-z0-9.-]+)((?:\/[\+~%\/.\w-_]*)?\??(?:[-\+=&;%@.\w_]*)#?(?:[\w]*))?)/i;
  var regex = new RegExp(expression);
  var res = "";
  if (url) {
    if (url.match(regex)) {
      res = "Valid URL";
      $(item).css('border', 'none');
    } else {
      res = "Invalid URL";
      $(item).css('border', '1px solid red');
    }
  } else {
    $(item).css('border', '1px solid red');
  }
}

function attachEvents() {

  //Expected Year
  $('select.statusSelect').on('change', function() {
    console.log(this.value);
    if(this.value == 4) {
      $('.block-extendedYear').slideDown();
      $('.block-year').slideUp();
    } else {
      $('.block-extendedYear').slideUp();
      $('.block-year').slideDown();
    }
  });


    $('textarea[name="expectedStudy.projectExpectedStudyInfo.referencesText"]').before('<label style="margin-top: 5px;">References reported in previous years:</label>');
    $('textarea[name="expectedStudy.projectExpectedStudyInfo.referencesText"]').attr('disabled', true);


  // On change studyType
  $('select.studyType').on('change', function() {

    // Clean indicator #3 option
    $('input.radioType-studyIndicatorThree:checked').prop('checked', false).trigger('change');
  });

  // On change radio buttons
  $('input[class*="radioType-"]').on('change', onChangeRadioButton);

  // On change policyInvestimentTypes
  $('select.policyInvestimentTypes').on('change', function() {
    console.log(this.value);
    if(this.value == 3) {
      $('.block-budgetInvestment').slideDown();
    } else {
      $('.block-budgetInvestment').slideUp();
    }
  });

  // On change stage of process
  $('select.stageProcess, input.radioType-studyIndicatorThree').on('change', function() {
    var isPolicy = $('input.radioType-studyIndicatorThree:checked').val() == "true";
    var stageProcessOne = ($('select.stageProcess').val() == 1);

    if(isPolicy && stageProcessOne) {
      // $('.stageProcessOne span.requiredTag').slideUp();
    } else {
      // $('.stageProcessOne span.requiredTag').slideDown();
    }
  });

  // SRF Targets validation
  $('input.radioType-targetsOption').on('change', function() {
    var showComponent = $(this).val() == "targetsOptionYes";
    if(showComponent) {
      $('.srfTargetsComponent').slideDown(); // Show
    } else {
      $('.srfTargetsComponent').slideUp(); // Hide
    }
  });

  // Other CrossCutting Component validation
  $('input.radioType-otherCrossCuttingOption').on('change', function() {
    var showComponent = $(this).val() == "Yes";
    if(showComponent) {
      // $('.otherCrossCuttingOptionsComponent').slideDown(); // Show
    } else {
      // $('.otherCrossCuttingOptionsComponent').slideUp(); // Hide
    }
  });

  // Public link
  $('input.radioType-optionPublic').on('change', function() {
    var showComponent = $(this).val() == "true";
    if(showComponent) {
      $('.optionPublicComponent').slideDown(); // Show
    } else {
      $('.optionPublicComponent').slideUp(); // Hide
    }
  });

  // Copy URL Button event
  $('.copyButton').on('click', function() {
    
    var $parent = $(this).closest(".generalStudyOptions, .optionPublicComponent");

    var $input = $parent.find('.urlInput');
    var $message = $parent.find('.message');

    // Temporarily make the input visible if it is hidden
    var wasHidden = $input.parent('.input').css('display') === 'none';
    if (wasHidden) {
      $input.parent('.input').css('display', 'block');
    }

    $input.select();

    if(document.execCommand("copy")) {
      if($message.length > 0) {
      $message.text("Copied to clipboard");
      $message.fadeIn(400, function() {
        $message.fadeOut(300);
      });
      }
    }

    if(wasHidden) {
      $input.parent('.input').css('display', 'none');
    }
  });

  /**
   * Links Component
   */
  (function() {
    // Events
    $('.addButtonLink').on('click', addItem);
    $('.removeLink').on('click', removeItem);

    // Functions
    function addItem() {
      var $list = $(this).parents('.linksBlock').find('.linksList');
      var $element = $('#studyLink-template').clone(true).removeAttr("id");
      // Remove template tag
      $element.find('input, textarea').each(function(i,e) {
        e.name = (e.name).replace("_TEMPLATE_", "");
        e.id = (e.id).replace("_TEMPLATE_", "");
      });
      // Show the element
      $element.appendTo($list).hide().show(350);
      // Update indexes
      updateIndexes();
    }
    function removeItem() {
      var $parent = $(this).parents('.studyLink');
      $parent.hide(500, function() {
        // Remove DOM element
        $parent.remove();
        // Update indexes
        updateIndexes();
      });
    }
    function updateIndexes() {
      $('.linksList').find('.studyLink').each(function(i,element) {
        $(element).find('.indexTag').text(i + 1);
        $(element).setNameIndexes(1, i);
      });
    }

  })();
  
  
  validateEmptyLinks();
  $('.addButtonLink').on('click', validateEmptyLinks);
  $('.removeLink.links').on('click', validateEmptyLinks);

  function validateEmptyLinks() {
    var linksList = $('.linksList').children('div').length;

    if ($(this).hasClass('removeElement')) {
      linksList -= 1;
    } else {
      linksList = linksList;
    }
    if (linksList > 0) {
      $('#warningEmptyLinksTag').hide();
    } else {
      $('#warningEmptyLinksTag').show();
    }
  }
  
   /**
   * References Component
   */
  (function () {
    // Events
    $('.addButtonReference').on('click', addItem);
    $('.removeLink.references').on('click', removeItem);
    validateEmptyLinks();

    // Functions
    function addItem() {
      var $list = $(this).parent('.referenceBlock').find('.referenceList');
      var $element = $('#multiInput-references-template').clone(true).removeAttr("id");
      var $listLength = $list.children().length;

      if ($listLength <= 30) {
        // Remove template tag
        $element.find('input, textarea').each(function (i, e) {
          e.name = (e.name).replace("_TEMPLATE_", "");
          e.id = (e.id).replace("_TEMPLATE_", "");
        });
        // Show the element
        $element.appendTo($list).hide().show(350);
        // Update indexes
        updateIndexes(this);
      }
    }
    function removeItem() {
      var $parent = $(this).parent('.multiInput.references');
      var $addBtn = $(this).parent().parent().parent().find('.addButtonReference');
      $parent.hide(500, function () {
        // Remove DOM element
        $parent.remove();
        // Update indexes
        updateIndexes($addBtn);
      });
    }
    function updateIndexes(list) {
      var linksList = $(list).parent('.referenceBlock').find('.referenceList');
      linksList.find('.multiInput').each(function (i, element) {
        $(element).find('.indexTag').text(i + 1);
        $(element).setNameIndexes(1, i);
        $(element).find('label').removeAttr('for');
        $(element).find('label').attr('for', 'expectedStudy.references['+i+'].externalAuthor');
      });
      
      if ((linksList.children().length - 1) != 0) {
        $('#warningEmptyReferencesTag').hide();
        validateEmptyLinks();
      } else {
        $('#warningEmptyReferencesTag').show();
      }
    }
    function validateEmptyLinks() {
      $('.referenceList').find('.multiInput span input').map((index, item) => {
        if (item.value != '') {
          $('#warningEmptyReferencesTag').hide();
        } else {
          $('#warningEmptyReferencesTag').show();
        }
      });
    }

  })();

  /**
   * Publications Component
   */
  (function () {
    // Events
    console.log('publications functions');
    $('.addPublication').on('click', addItem);
    $('.removeLink.publications').on('click', removeItem);
    validateEmptyLinks();

    // Functions
    function addItem() {
      var $list = $(this).parent('.publicationsBlock').find('.publicationsList');
      var $element = $('#studyPublication-template').clone(true).removeAttr("id");
      var $listLength = $list.children().length;
      console.log('add publication');
      console.log($listLength);
      console.log($list);
      console.log($element);
      if ($listLength <= 30) {
        // Remove template tag
        $element.find('input, textarea').each(function (i, e) {
          e.name = (e.name).replace("_TEMPLATE_", "");
          e.id = (e.id).replace("_TEMPLATE_", "");
        });
        // Show the element
        $element.appendTo($list).hide().show(350);
        // Update indexes
        updateIndexes(this);
      }
    }
    function removeItem() {
      var $parent = $(this).parent('.studyPublication.publications');
      var $addBtn = $(this).parent().parent().parent().find('.addButtonPublication');
      $parent.hide(500, function () {
        // Remove DOM element
        $parent.remove();
        // Update indexes
        updateIndexes($addBtn);
      });
    }
    function updateIndexes(list) {
      var linksList = $(list).parent('.publicationsBlock').find('.publicationsList');
      linksList.find('.studyPublication').each(function (i, element) {
        $(element).find('.indexTag').text(i + 1);
        $(element).setNameIndexes(1, i);
        $(element).find('label').removeAttr('for');
      });
      
      if ((linksList.children().length - 1) != 0) {
        $('#warningEmptyPublicationsTag').hide();
        validateEmptyLinks();
      } else {
        $('#warningEmptyPublicationsTag').show();
      }
    }
    function validateEmptyLinks() {
      $('.publicationsList').find('.studyPublication span input').map((index, item) => {
        if (item.value != '') {
          $('#warningEmptyPublicationsTag').hide();
        } else {
          $('#warningEmptyPublicationsTag').show();
        }
      });
    }

  })();

  /**
   * Qualification Component
   */
  (function() {
    // Events
    $('.addStudyQualification').on('click', addItem);
    $('.removeQuantification').on('click', removeItem);

    // Functions
    function addItem() {
      var $list = $(this).parents('.quantificationsBlock').find('.quantificationsList');
      var $element = $('#quantification-template');

      // remove select2 data to avoid corruption in clone process
      if ($element.find('select').data('select2')) {
        $element.find('select').select2("destroy");
      }

      // clone the item
      var $clone = $element.clone(true).removeAttr("id");
      // Remove template tag
      $clone.find('input, textarea').each(function(i,e) {
        e.name = (e.name).replace("_TEMPLATE_", "");
        e.id = (e.id).replace("_TEMPLATE_", "");
      });
      // Add select2 to select type of quantification
      $element.find('select').select2();
      $clone.find('select').select2();
      // Show the element
      $clone.appendTo($list).hide().show(350);
      // Update indexes
      updateIndexes();
    }
    function removeItem() {
      var $parent = $(this).parents('.quantification');
      $parent.hide(500, function() {
        // Remove DOM element
        $parent.remove();
        // Update indexes
        updateIndexes();
      });
    }
    function updateIndexes() {
      $('.quantificationsList').find('.quantification').each(function(i,element) {
        $(element).find('span.index').text(i + 1);
        $(element).setNameIndexes(1, i);

        // Update Radios
        $(element).find('.radioFlat ').each(function(j,item) {
          var radioName = 'quantificationRadio-' + i + '-' + j;
          $(item).find('input').attr('id', radioName);
          $(item).find('label').attr('for', radioName);
        });

      });
    }

  })();
	//On change radio buttons
	$('input[class*="radioType-"]').on('change', onChangeRadioButton);
}

function addSelect2() {

  var isNew = $('.evidenceBlock').classParam('isNew') == "true";
  var hasEvidenceTag = $('input.radioType-tags:checked').val();

  if(isNew) {
    // Adjust status
    // it was disable in 6/11/2019
    // $('select.statusSelect option[value="4"]').prop('disabled', true); //
    // Extended
    // $('select.statusSelect option[value="5"]').prop('disabled', true); //
    // Cancelled

    // Suggest Evidence tag
    if(!hasEvidenceTag) {
      $('input.radioType-tags[value="1"]').prop("checked", true); // New
      // tag
    }
  } else {

  }

  if(reportingActive) {
    // Adjust status

  } else {
    // Adjust status
    $('select.statusSelect option[value="3"]').prop('disabled', true);
    $('select.statusSelect option[value="4"]').prop('disabled', true);
    $('select.statusSelect option[value="5"]').prop('disabled', true);
  }

  $('form select').select2({
    width: '100%'
  });
}

function onChangeRadioButton() {
	  var thisValue = this.value === "true";
	  var radioType = $(this).classParam('radioType');
	  if (thisValue) {
	    $('.block-' + radioType).slideDown();
	  } else {
	    $('.block-' + radioType).slideUp();
	  }
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

function counterSharedCluster() {

  let currentAmount = $('div[listname="expectedStudy.projects"] ul.list li').length;
  const $counter = $('#modalCounterShared');
  $counter.text(currentAmount);
  
  $('div[listname="expectedStudy.projects"] .setSelect2').on('change', function() {
    currentAmount = $('div[listname="expectedStudy.projects"] ul.list li').length;
    $counter.text(currentAmount);
  });
}

function updateAllianceTab() {
  var $selectCenters = $('select.elementType-institution');


    setTimeout(() => {
      $option = $selectCenters.find('option[disabled]');


      if($option.length > 0) {

        if($option.toArray().some((item) => item.value == "7320")) {
          $('#allianceTab').slideDown();
        } else {
          $('#allianceTab').slideUp();
        }
      }
    }, 1000);

      

}