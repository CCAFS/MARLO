var caseStudiesName, multiInputStudies;
var $elementsBlock;

$(document).ready(init);

function init() {

  // Add Events
  attachEvents();

  // Add select2
  addSelect2();

  // Add Geographic Scope
  $('select.elementType-repIndGeographicScope ').on("addElement removeElement", function(_event,_id,_name) {
    setGeographicScope(this);
    dynamicMarginToSelectedRender();
    disabledNationalOrMultiNational();
    displayLabelGeographicScope();
  });

  $('.removeElementType-repIndGeographicScope').on('click', displayLabelGeographicScope);
  $('select.elementType-repIndGeographicScope').on('change', displayLabelGeographicScope);

  setGeographicScope($('form select.elementType-repIndGeographicScope')[0]);

  disabledNationalOrMultiNational();
  dynamicMarginToSelectedRender();

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
  updateAllianceTab()
  $('select.elementType-institution').on('change',updateAllianceTab);
  $('div.removeElementType-institution').on('click',updateAllianceTab);

  $('select.countriesSelect').on('change', dynamicMarginToSelectedRender);

  //init partners methods
  deliverablePartnersModule.init();

  //init dynamic selector
  dynamicSelectorSDGImageModule.init();
}

function bottonPading(){
  $("[name='expectedStudy.projectExpectedStudyInfo.topLevelComments']").parent().css("padding-bottom", " 10px")  
}

function checkHyperlinks() {
  multiInputStudies.each((_index, item) => {
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
    function addItem(e) {
      var eventSelect = e instanceof jQuery.fn.init ? e : event.target;
      var $list = $(eventSelect).parents('.linksBlock').find('.linksList');
      var $element = $('#studyLink-template').clone(true).removeAttr("id");
      // Remove template tag
      $element.find('input, textarea').each(function(_i,e) {
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

    setTimeout(() => {
      var linksListLength = $('.linksList').children().length;
      if (linksListLength == 0) {
        addItem($('.addButtonLink'));
        updateIndexes();
      }
    }, 1000);

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
    function addItem(e) {
      var eventSelect = e instanceof jQuery.fn.init ? e : event.target;
      var $list = $(eventSelect).parent('.referenceBlock').find('.referenceList');
      var $element = $('#multiInput-references-template').clone(true).removeAttr("id");
      var $listLength = $list.children().length;

      if ($listLength <= 30) {
        // Remove template tag
        $element.find('input, textarea').each(function (_i, e) {
          e.name = (e.name).replace("_TEMPLATE_", "");
          e.id = (e.id).replace("_TEMPLATE_", "");
        });
        // Show the element
        $element.appendTo($list).hide().show(350);
        // Update indexes
        updateIndexes();
      }
    }
    function removeItem() {
      var $parent = $(this).parent('.multiInput.references');
      var $addBtn = $(this).parent().parent().parent().find('.addButtonReference');
      $parent.hide(500, function () {
        // Remove DOM element
        $parent.remove();
        // Update indexes
        updateIndexes();
      });
    }
    function updateIndexes() {
      var linksList = $('.referenceBlock').find('.referenceList');
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
      $('.referenceList').find('.multiInput span input').map((_index, item) => {
        if (item.value != '') {
          $('#warningEmptyReferencesTag').hide();
        } else {
          $('#warningEmptyReferencesTag').show();
        }
      });
    }

    setTimeout(() => {
      var referenceListLength = $('.referenceList').children().length - 1;
      if (referenceListLength == 0) {
        addItem($('.addButtonReference'));
        updateIndexes();
      }
      
    }, 1000);

  })();

  /**
   * Publications Component
   */
  (function () {
    // Events
    $('.addPublication').on('click', addItem);
    $('.removePublication').on('click', removeItem);
    validateEmptyLinks();

    // Functions
    function addItem(e) {
      var eventSelect = e instanceof jQuery.fn.init ? e : event.target;
      var $list = $(eventSelect).parent('.publicationsBlock').find('.publicationsList');
      var $element = $('#studyPublication-template').clone(true).removeAttr("id");
      var $listLength = $list.children().length;
      if ($listLength <= 30) {
        // Remove template tag
        $element.find('input, textarea').each(function (_i, e) {
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
      var $parent = $(this).parent('.studyPublication');
      var $addBtn = $(this).parent().parent().parent().find('.addPublication');
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
      $('.publicationsList').find('.studyPublication span input').map((_index, item) => {
        if (item.value != '') {
          $('#warningEmptyPublicationsTag').hide();
        } else {
          $('#warningEmptyPublicationsTag').show();
        }
      });
    }

    setTimeout(() => {
      var publicationListLength = $('.publicationsList').children().length - 1;
      if (publicationListLength == 0) {
        addItem($('.addPublication'));
        updateIndexes($('.addPublication'));
      }
      
    }, 1000);

  })();

  /**
   * Quantification Component
   */
  (function() {
    // Events
    $('.addStudyQualification').on('click', addItem);
    $('.removeQuantification').on('click', removeItem);

    // Functions
    function addItem(e) {
      var eventSelect = e instanceof jQuery.fn.init ? e : event.target;
      var $list = $(eventSelect).parents('.quantificationsBlock').find('.quantificationsList');
      var $element = $('#quantification-template');

      // remove select2 data to avoid corruption in clone process
      if ($element.find('select').data('select2')) {
        $element.find('select').select2("destroy");
      }

      // clone the item
      var $clone = $element.clone(true).removeAttr("id");
      // Remove template tag
      $clone.find('input, textarea').each(function(_i,e) {
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

    setTimeout(() => {
      var quantificationsListLength = $('.quantificationsList').children().length;
      if (quantificationsListLength == 0) {
        addItem($('.addStudyQualification'));
        updateIndexes();
      }

    }, 1000);

  })();
  //after load functions
  disableRelatedLeversBasedOnPrimaryLever();

	//On change radio buttons
	$('input[class*="radioType-"]').on('change', onChangeRadioButton);

  $('input[name*="expectedStudy."]').on('change', onChangeCheckboxButton);

  $('input.radioType-contributionToCGIAR').on('change', onDisplayItemsInOneCGIAR);

  $('input[id*="radioCheckDisplay_"]').on('change', displayInnerCheckbox);

  $('.containerPrimaryLever input[name="expectedStudy.allianceLever.id"]').on('change', disableRelatedLeversBasedOnPrimaryLever);

  $('.containerPrimaryLever input[name*="expectedStudy.allianceLever.sdgContributions"]').on('change', dynamicSelectorSDGImageModule.init);
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

function onChangeCheckboxButton() {

  //Verify if contains the class fieldError
  const $this = $(this);
  const $parent = $this.parents('.containerRadioToCheckbox');

  if($this.hasClass('fieldError')) {
    $this.removeClass('fieldError');
  }

}

function onDisplayItemsInOneCGIAR(){
  var $commentBox = $('.contributionToCGIARComment');
  var $radioBox = $('.linkToImpactAndTarget');
  var $radioButton = $('input.radioType-contributionToCGIAR:checked');

  if($radioButton.val() === "false"){
    $radioBox.slideUp();
    $commentBox.slideDown();
  } else {
    $radioBox.slideDown();
    $commentBox.slideUp();
    
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
      fail: function(e,_data) {
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
  $fileUpload.bind('fileuploadsubmit', function(_e,_data) {

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
          //remove disabled class alliance tab
          $('#allianceTab').removeClass('disabled');
          disabledTabAlliance();
        } else {
          //add disabled class alliance tab
          $('#allianceTab').addClass('disabled');
          disabledTabAlliance();
        }
      }
    }, 1000);

}


function disabledTabAlliance() {
  var $tabs = $('.nav-tabs li');

  $tabs.each(function() {
    var $this = $(this);
    if($this.attr('id') == "allianceTab") {
      if($this.hasClass('disabled')) {
        $this.find('a').removeAttr('data-toggle');
        $this.attr('title', "This tab will be available for reporting only if Alliance is part of the OICR key contributors.");
      } else {
        $this.find('a').attr('data-toggle', 'tab');
        $this.removeAttr('title');
      }
    }
  });
}
      

function displayInnerCheckbox() {

  var $parentMacro = $(this).parents('.radioToCheckbox');
  var $radioButtons = $parentMacro.find('input[id*="radioCheckDisplay_"]');
  var $radioSelected = $radioButtons.filter(':checked');

  $radioButtons.each(function() {
    var $this = $(this);
    var $innerCheckbox = $parentMacro.find(`#innerCheckbox[data-radioButton='${$this.val()}']`);
    if($this.is($radioSelected)) {
      $innerCheckbox.slideDown("slow");
      //get name inner inputs and remove _TEMPLATE_
      $innerCheckbox.find('input').each(function(_i,e) {
        e.name = (e.name).replace("_TEMPLATE_", "");
        e.id = (e.id).replace("_TEMPLATE_", "");
      });

      $innerCheckbox.find('label').each(function(_i,e) {
        e.htmlFor = (e.htmlFor).replace("_TEMPLATE_", "");
      } );
    } else {
      $innerCheckbox.slideUp("slow");
      //get name inner inputs and add _TEMPLATE_
      $innerCheckbox.find('input').each(function(_i,e) {
        if(e.name.indexOf("_TEMPLATE_") == -1){
          e.name = "_TEMPLATE_" + (e.name);
          e.id = "_TEMPLATE_" + (e.id);
        } 
      });

      $innerCheckbox.find('label').each(function(_i,e) {
        if(e.htmlFor.indexOf("_TEMPLATE_") == -1){
          e.htmlFor = "_TEMPLATE_" + (e.htmlFor);
        }
      });
    }
  });

}

function disableRelatedLeversBasedOnPrimaryLever() {
  //get selected option of primary lever
  var $selectedPrimaryLever = $('.containerPrimaryLever input[name="expectedStudy.allianceLever.id"]:checked');
  
  //get all related levers options
  var $relatedLevers = $('.containerRelatedLever input[name*="expectedStudy.allianceLevers"]');

  //disable related lever that shares the same id with the selected primary lever
  $relatedLevers.each(function() {
    var $this = $(this);
    if($this.val() == $selectedPrimaryLever.val()) {
      if($this.val() == "9" || $this.val() == 9){
        $this.prop('disabled', false);
        return
      } 
        $this.prop('disabled', true);
      
    } else {
      $this.prop('disabled', false);
    }
  });
}

function displayLabelGeographicScope() {
  // Display label if there are elements in the geographic scope
  var $label = $('label[name="study.generalInformation.geographicImpact"]');
  var $geographicScope = $('select.elementType-repIndGeographicScope option:disabled');
  if($geographicScope.length > 0) {
    if ($geographicScope.filter((_, option) => option.value == "-1" || option.value == "1").length > 0) {
      $label.hide();
    } else {
      $label.show();
    }
  } else {
    $label.hide();
  }
}

function disabledNationalOrMultiNational() {
  var $geographicScope = $('select.elementType-repIndGeographicScope');

  var $listSelected = $('div[listname="expectedStudy.geographicScopes"] div.panel-body ul.list li');

  if($listSelected.length == 0 || !$listSelected.toArray().some((item) => $(item).find('input.elementRelationID').val() == "3" || $(item).find('input.elementRelationID').val() == "4")) {
    // Enable all options
    console.log('enable all');	
    $geographicScope.find('option[value="3"]').prop('disabled', false);
    $geographicScope.find('option[value="4"]').prop('disabled', false);
  }

  var $geographicScopeDisabled = $geographicScope.find('option:disabled');
  var $national = $geographicScopeDisabled.filter((_, option) => option.value == "4");
  var $multiNational = $geographicScopeDisabled.filter((_, option) => option.value == "3");

  if($national.length > 0) {
    console.log('disable national');
    $geographicScope.find('option[value="3"]').prop('disabled', true);
    return;
  }

  if($multiNational.length > 0) {
    console.log('disable multinational');
    $geographicScope.find('option[value="4"]').prop('disabled', true);
    return;
  }
}

var deliverablePartnersModule = (function () {

  function init() {
    console.log('Starting deliverablePartnersModule');

    updateInstitutionSelects();

    attachEvents();
  }

  function attachEvents() {
    // On change institution
    $('select.partnerInstitutionID').on('change', changePartnerInstitution);
    // On remove a deliverable partner item
    $('.removePartnerItem').on('click', removePartnerItem);
    // On add a new deliverable partner Item
    $('.addPartnerItem').on('click', addPartnerItem);
    // On add a shrfm sub action
    $('.addSlo').on('click', addIdo);

    updateIndexes();

  }

  function addIdo() {
    console.log("add sub action");
    var $itemsList = $(this).parent().find('.slos-list');
    var $item = $("#srfSlo-template").clone(true).removeAttr("id");
    $item.find('.blockTitle').trigger('click');
    $itemsList.append($item);
    $item.slideDown('slow');
    updateSubActionIndexes();
    $item.trigger('addComponent');
  }

  function updateSubActionIndexes() {
    $('.slos-list .srfSlo').each(function (i, slo) {
      // Updating indexes
      $(slo).setNameIndexes(1, i);
      $(slo).find('.srfSloIndicator').each(function (subIdoIndex, subIdo) {
        // Updating indexes
        $(subIdo).setNameIndexes(2, subIdoIndex);
      });
    });

    $('.issues-list .srfCCIssue').each(function (i, crossCutting) {
      // Updating indexes
      $(crossCutting).setNameIndexes(1, i);

    });
  }

  function addPartnerItem() {
    var $listBlock = $('.projectExpectedStudyPartners');
    var $template = $('#deliverablePartnerItem-template');

    if($template.find('select').data('select2')){
      $template.find('select').select2("destroy");
    }
    
    var $newItem = $template.clone(true).removeAttr('id');

    $template.find('select').select2();
    $newItem.find('select').select2();
    $listBlock.append($newItem);
    $newItem.show();
    updateIndexes();
  }

  function removePartnerItem() {
    var $item = $(this).parents('.deliverablePartnerItem');
    $item.hide(500, function () {
      $item.remove();
      updateIndexes();
    });
  }

  function changePartnerInstitution() {
    var $deliverablePartner = $(this).parents('.deliverablePartnerItem');
    var $usersBlock = $deliverablePartner.find('.usersBlock');
    var typeID = $deliverablePartner.find('input.partnerTypeID').val();
    var isResponsible = (typeID == 1);
    // Clean users list
    $usersBlock.empty();
    // Get new users list
    var $newUsersBlock = $('#partnerUsers .institution-' + this.value + ' .users-' + typeID).clone(true);
    //Remove name _TEMPLATE_ from inputs
    $newUsersBlock.find('input').each(function(_i,e) {
      e.name = (e.name).replace("_TEMPLATE_", "");
      e.id = (e.id).replace("_TEMPLATE_", "");
    });

    // Show them
    $usersBlock.append($newUsersBlock.html());
    // Update indexes
    if (!isResponsible) {
      updateIndexes();
    }
  }

  function updateIndexes() {
    $('.projectExpectedStudyPartners .deliverablePartnerItem').each(function (i, partner) {

      // Update deliverable partner index
      $(partner).setNameIndexes(1, i);

      $(partner).find('.deliverableUserItem').each(function (j, user) {
        var personID = $(user).find('input[type="checkbox"]').val();
        var customID = "jsGenerated-" + i + "-" + j + "-" + personID;
        // Update user index
        $(user).setNameIndexes(2, j);

        //Remove name _TEMPLATE_ from inputs
        $(user).find('input').each(function(_i,e) {
          e.name = (e.name).replace("_TEMPLATE_", "");
          e.id = (e.id).replace("_TEMPLATE_", "");
        });

        // Update user checks/radios labels and inputs ids
        $(user).find('input[type="checkbox"]').attr('id', customID);
        $(user).find('label.checkbox-label').attr('for', customID);
      });

    });

    updateInstitutionSelects()
  }

  function updateInstitutionSelects() {
    var $listBlock = $('.projectExpectedStudyPartners');
    var $institutionsSelects = $listBlock.find('select.partnerInstitutionID');

    // Get selected values
    selectedValues = $institutionsSelects.map(function (i, select) {
      return select.value;
    });

    $institutionsSelects.each(function (i, select) {
      // Enable options
      $(select).find('option').prop('disabled', false);

      // Disable only the selected values
      $.each(selectedValues, function (key, val) {
        if (select.value != val) {
          $(select).find('option[value="' + val + '"]').prop('disabled', true);
        }
      });
    });

    // Reset Select2
    setTimeout(function () {
      $institutionsSelects.select2({
        width: '98%'
      });
    });

  }

  return {
    init: init
  }
})();

var dynamicSelectorSDGImageModule = (function (){

  function init() {
    console.log("Starting dynamicSelectorSDGImageModule");
    changeCurrentDisplaySDGImage();
  }

  function changeCurrentDisplaySDGImage() {
    const $containerReference = $('.selectedLeverContainer');
    const $containerImage = $('.selectedLeverContainer__image');
    const $titleSelectedLeverContainer = $('.titleSelectedLeverContainer');
    const $image = $containerImage.find('img');

    const $containerPrimaryLever = $('.containerPrimaryLever');
    const $checkedRadioButtonLever = $containerPrimaryLever.find('input[name="expectedStudy.allianceLever.id"]:checked');
    const $checkedRadioButtonLeverParent = $checkedRadioButtonLever.parents('.containerRadioToCheckbox');
    const $innerCheckbox = $checkedRadioButtonLeverParent.find('#innerCheckbox');
    const $checkedInnerCheckbox = $innerCheckbox.find('input[name*="expectedStudy.allianceLever.sdgContributions"]:checked');
    const $checkedInnerCheckboxValue = $checkedInnerCheckbox.val();

    //Set image of the SDG Contribution
    $.ajax({
      url: baseURL + '/getSdgImage.do',
      async: true,
      data: {
        requestID: Number.parseInt($checkedInnerCheckboxValue)
      },
      success: function(data) {
        console.log(data);
        if(data.image.adsoluteURL == null){
          console.error("Image not found");
          $titleSelectedLeverContainer.hide();
          $containerReference.hide();
        } else {
          console.log("Image found");
          $titleSelectedLeverContainer.show();
          $containerReference.show();
          $image.attr("src",data.image.adsoluteURL);
        }
      },
      error: function(xhr, status, error) {
        console.error(error);
        reject(error);
      }
    });

    //Set information of the SDG Contribution
    const $containerSDGInformation = $('.selectedLeverContainer__content');
    const $leverName = $containerSDGInformation.find('.selectedLeverContainer__content__lever');
    const $leverContributionSDG = $containerSDGInformation.find('.selectedLeverContainer__content__contributionSDG');

    $leverName.text($checkedRadioButtonLever.next().text());
    $leverContributionSDG.text($checkedInnerCheckbox.next().text());

  }

  return {
    init: init
  }

})();

function dynamicMarginToSelectedRender(){
  const $selectedMultiple = $('.select2-selection--multiple');
  const $rendered = $('ul.select2-selection__rendered');

  if($rendered.children().length > 0){
    $selectedMultiple.css('margin-bottom',`${$rendered.height()+30}px`);
  } else {
    $selectedMultiple.css('margin-bottom','0');

  }

}