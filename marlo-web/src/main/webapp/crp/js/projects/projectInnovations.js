$(document).ready(function() {

  // Add select2
  addSelect2();


  // Add Geographic Scope
  $('select.elementType-repIndGeographicScope ').on("addElement removeElement", function(event,id,name) {
    setGeographicScope(this);

    $('div.nationalBlock span.selection span.select2-selection--multiple').append('<span class="select2-selection__arrow" role="presentation"><b role="presentation"></b></span>');
  });
  setGeographicScope($('form select.elementType-repIndGeographicScope')[0]);

  $('div.nationalBlock span.selection span.select2-selection--multiple').append('<span class="select2-selection__arrow" role="presentation"><b role="presentation"></b></span>');

  // Activate Popup
  popups();

  // Attach Events
  attachEvents();
  AddRequired();

  // Change counter value of Shared Cluster
  counterSharedCluster();

  // Add image to SDG Targets
  $('select.elementType-sdg').on("change", addImageToSelectSDGTargets);
  addImageToSelectSDGTargets();
  // Add image to Impact Areas
  $('select.elementType-impactArea').on("change", addImageToSelectImpactAreas);
  addImageToSelectImpactAreas();

  // Change display message in Scaling
  $('input[name="innovation.projectInnovationInfo.readinessScale"]').on('change', changeDisplayMessageInScaling);
  changeDisplayMessageInScaling();

  // Update the dynamic visualization of the "Alliance" Tab after selecting in Key Contributors
  updateAllianceTab();
  $('select.elementType-institution').on('change',updateAllianceTab);
  $('div.removeElementType-institution').on('click',updateAllianceTab);

  //init partners methods
  deliverablePartnersModule.init();

  feedbackAutoImplementation();

  initTestDropdown();
});

function attachEvents() {

  /**
   * Actors Component
   */

  ( function () {
    // Events
    $('.addActors').on('click', addActor);
    $('.removeActor').on('click', removeActor);
    $('.actorsList .actorsInnovation').each(function(_i,e) {
      $(e).find('input[type="checkbox"].sexAgeNotApply').on('change', onChangeCheckboxSexAndAge);
    });
    
    // Function
    function addActor() {

      const $listBlock = $('.actorsList');
      const $template = $('#actorsInnovation-template');

      // remove select2 data to avoid corruption in clone process
      if ($template.find('select').data('select2')) {
        $template.find('select').select2("destroy");
      }

      const $newItem = $template.clone(true).removeAttr('id');
      $newItem.find('input, select').each(function(_i,e) {
        e.name = (e.name).replace("_TEMPLATE_", "");
        e.id = (e.id).replace("_TEMPLATE_", "");
      });

      $newItem.find('label').each(function(_i,e) {
        e.htmlFor = (e.htmlFor).replace("_TEMPLATE_", "");
      });

      // Add select2 to select2 library
      $template.find('select').select2();
      $newItem.find('select').select2();

      // Add function to onChangeCheckboxSexAndAge
      $newItem.find('input[type="checkbox"].sexAgeNotApply').on('change', onChangeCheckboxSexAndAge);

      // Show the element
      $newItem.appendTo($listBlock).hide().show(350);
      // Update indexes
      updateIndexes();

    }

    function removeActor() {
      var $parent = $(this).parents('.actorsInnovation');
      $parent.hide(500, function() {
        // Remove DOM element
        $parent.remove();
        // Update indexes
        updateIndexes();
      });
    }

    function updateIndexes() {
      $('.actorsList').find('.actorsInnovation').each(function(i, actor) {
        $(actor).setNameIndexes(1, i);
        
        $(actor).find('label').each(function(_i,e) {
          const newForValue = $(e).prev('input').attr('id');
          $(e).attr('for', newForValue);
        });
      });
    }

    function onChangeCheckboxSexAndAge() {
      const $element = $(this);
      const $blockSexAgeNotApply = $element.parents('.actorsInnovation').find('.block-sexAgeNotApply');
      const $checkboxSexAgeNotApply = $blockSexAgeNotApply.find('input[type="checkbox"]');

      if($element.is(':checked')) {
        $blockSexAgeNotApply.slideUp();
        $checkboxSexAgeNotApply.prop('checked', false);
      } else {
        $blockSexAgeNotApply.slideDown();
      }
    }

  })();

  /**
   * Organizations Component
   */
  ( function () {
    // Events
    $('.addOrganizations').on('click', addOrganization);
    $('.removeOrganization').on('click', removeOrganization);

    // Function
    function addOrganization() {

      const $listBlock = $('.organizationsList');
      const $template = $('#organizationsInnovation-template');

      // remove select2 data to avoid corruption in clone process
/*       if ($template.find('select').data('select2')) {
        $template.find('select').select2("destroy");
      } */

      const $newItem = $template.clone(true).removeAttr('id');
      $newItem.find('input, select, input[type="checkbox"]').each(function(_i,e) {
        e.name = (e.name).replace("_TEMPLATE_", "");
        e.id = (e.id).replace("_TEMPLATE_", "");
      });
      $newItem.find('label').each(function(_i,e) {
        e.htmlFor = (e.htmlFor).replace("_TEMPLATE_", "");
      });
/*       // Add select2 to select2 library
      $template.find('select').select2();
      $newItem.find('select').select2(); */

      $newItem.find('.allianceOrganizations-institutions')[0].data = $template.find('.allianceOrganizations-institutions')[0].data;
    
      // Show the element
      $newItem.appendTo($listBlock).hide().show(350);
      // Update indexes
      updateIndexes();

    }

    function removeOrganization() {
      var $parent = $(this).parents('.organizationsInnovation');
      $parent.hide(500, function() {
        // Remove DOM element
        $parent.remove();
        // Update indexes
        updateIndexes();
      });
    }

    function updateIndexes() {

      $('.organizationsList').find('.organizationsInnovation').each(function(i, organization) {
        $(organization).setNameIndexes(1, i);

        const newForValue = $(organization).find('label').prev('input').attr('id');
        $(organization).find('label').attr('for', newForValue);
        

      });
    }

  })();

  const readinessModule = evidencesModule();
  readinessModule.init('Readiness');

  const urlModule = evidencesModule();
  urlModule.init('Url');

  const complementaryModule = evidencesModule();
  complementaryModule.init('Complementary');

  // 
  $('#isClearLeadToAddRequired').on('click', AddRequired);

  // Check the stage of innovation - DEPRECATED
/*   $('select.stageInnovationSelect').on('change', function() {
    var isStageFour = this.value == 4;
    if(isStageFour) {
      $('.stageFourBlock-true').slideDown();
      $('.stageFourBlock-false').slideUp();
    } else {
      $('.stageFourBlock-true').slideUp();
      $('.stageFourBlock-false').slideDown();
    }
  }); */

  // If select other in innovation type
  $('select.innovationTypeSelect').on('change', function() {
    var id = this.value;
    if(id == 6) {
      $('.typeSixBlock').slideDown();
    } else {
      $('.typeSixBlock').slideUp();
    }
  });

  // If select other in innovation nature
  $('select.innovationNatureSelect').on('change', function() {
    var id = this.value;
    if(id == 4) {
      $('.natureFourBlock').slideDown();
    } else {
      $('.natureFourBlock').slideUp();
    }
  });

  // If select other in innovation property rights
  $('select.innovationPropertyRightsSelect').on('change', function() {
    var id = this.value;
    if(id == 4) {
      $('.otherIntellectualProperty').slideDown();
    } else {
      $('.otherIntellectualProperty').slideUp();
    }
  });
  
  $('input.isClearLead').on('change', function() {
    var selected = $('input.isClearLead').is(":checked");

    if(selected == true) {
      $('.lead-organization').slideUp();
    } else {
      $('.lead-organization').slideDown();
    }

  })

  //On change radio buttons
  $('input[class*="radioType-"]').on('change', onChangeRadioButton);

  // On change checkbox buttons in alliance lever display other field
  displayInnerOtherInput();
  $('.containerRadioToCheckbox--other input[type="checkbox"][id*="lever-"]').on('change',displayInnerOtherInput);
  //On change checkbox buttons - alliance
  updateIndexListCheckbox();
  $('input[type="checkbox"][id*="lever-"]').on('change', updateIndexListCheckbox);

  //On change radio buttons - One CGIAR
  $('input.radioType-contributionToCGIAR').on('change', onDisplayItemsInOneCGIAR);

}
function AddRequired(){
  if ($('#isClearLeadToAddRequired').is(":checked")) {
    $('.top-five-contributing').find('.requiredTag').show();
  }else{
    $('.top-five-contributing').find('.requiredTag').hide();
  }
}
function addSelect2() {
  $('form select').select2({
      width: '100%',
      templateResult: formatList,
      templateSelection: formatList
  });

  $('form select.countriesSelect').select2({
      maximumSelectionLength: 0,
      placeholder: "Select a country",
      templateResult: formatStateCountries,
      templateSelection: formatStateCountries,
      width: '100%'
  });

}

function onChangeRadioButton() {
	  var thisValue = this.value === "true";
	  var radioType = $(this).classParam('radioType');
	  if (thisValue) {
      //if just is one block
	    $('.block-' + radioType).slideDown();

      //if is a block with two different blocks
      $('.block-yes-'+radioType).slideDown();
      $('.block-no-'+radioType).slideUp();
	  } else {
      //if just is one block
	    $('.block-' + radioType).slideUp();

      //if is a block with two different blocks
      $('.block-yes-'+radioType).slideUp();
      $('.block-no-'+radioType).slideDown();
	  }
}

function formatList(state) {
  if(!state.id || (state.id == "-1")) {
    return state.text;
  }
  var result = "<span>" + state.text + "</span>";
  return $(result);
};

function formatStateCountries(state) {
  if(!state.id) {
    return state.text;
  }
  var flag = '<i class="flag-icon flag-icon-' + state.element.value.toLowerCase() + '"></i> ';
  var $state;
  if(state.id != -1) {
    $state = $('<span>' + flag + state.text + '</span>');
  } else {
    $state = $('<span>' + state.text + '</span>');
  }
  return $state;
};

function counterSharedCluster() {

  let currentAmount = $('div[listname="innovation.sharedInnovations"] ul.list li').length;
  const $counter = $('#modalCounterShared');
  $counter.text(currentAmount);
  
  $('div[listname="innovation.sharedInnovations"] .setSelect2').on('change', function() {
    currentAmount = $('div[listname="innovation.sharedInnovations"] ul.list li').length;
    $counter.text(currentAmount);
  });
}

function updateAllianceTab() {
  var $selectCenters = $('div[listname="innovation.centers"] select.elementType-institution');

    setTimeout(() => {
      $option = $selectCenters.find('option[disabled]');

        if($option.toArray().some((item) => item.innerHTML.toLowerCase().includes("alliance"))) {
          //remove disabled class alliance tab
          $('#allianceTab').slideDown();
        } else {
          //add disabled class alliance tab
          $('#allianceTab').slideUp();
        }

    }, 1000);

}  

function addImageToSelectSDGTargets() {

  const $listRender = $('div[listname="innovation.sdgs"] .panel-body li.relationElement');

  $listRender.each(function(index, element) {
    const $elementVisualization = $(element);
    const $elementId = $(element).find('input[type="hidden"].elementRelationID').val();

    if($elementVisualization.find('.sdgImage').length == 0) {
      
      $.ajax({
        url: baseURL + '/getSdgImage.do',
        async: true,
        data: {
          sdgID: Number.parseInt(Number.parseInt($elementId))
        },
        success: function(data) {
  
          if(data) {
            //render image in a before element in the elementVisualization
            $elementVisualization.find('.elementName').before(`<img src="${data.image.adsoluteURL}" class="sdgImage" alt="sdg-${$elementId}">`);
          }
        },
        error: function(xhr, status, error) {
          console.error(error);
          reject(error);
        }
      });
    }

  });
  
}

function updateIndexListCheckbox() {
  const $list = $('input[type="checkbox"][id*="lever-"]');
  const $listChecked = $list.filter(':checked');
  const $listUnchecked = $list.filter(':not(:checked)');
  
  $listChecked.each(function(index, element) {
    
    const $element = $(element);

    const newValueForLever = "innovation.allianceLevers[" + index + "].allianceLever.id";
    const newValueForReference = "innovation.allianceLevers[" + index + "].id";

    $element.attr('name', newValueForLever);

    if($element.parent('.inputsFlat').prev('.hiddenIdReference')){
      const $hiddenIdReferenceInput = $element.parent('.inputsFlat').prev('.hiddenIdReference').find('input[type="hidden"]');
      const $hiddenIdReferenceLabel = $hiddenIdReferenceInput.prevAll('label');

      $hiddenIdReferenceInput.attr('name', newValueForReference);
      $hiddenIdReferenceInput.attr('id', newValueForReference);
      $hiddenIdReferenceLabel.attr('for', newValueForReference);
    }
  });

  $listUnchecked.each(function(index, element) {
    const $element = $(element);

    if($element.parent('.inputsFlat').prev('.hiddenIdReference')){
      const $hiddenIdReferenceInput = $element.parent('.inputsFlat').prev('.hiddenIdReference').find('input[type="hidden"]');
      const $hiddenIdReferenceLabel = $hiddenIdReferenceInput.prevAll('label');

      $hiddenIdReferenceInput.attr('name', "");
      $hiddenIdReferenceInput.attr('id', "");
      $hiddenIdReferenceLabel.attr('for', "");
    }

  });
}

function displayInnerOtherInput() {
  const $inputButtons = $('.containerRadioToCheckbox--other .inputOther');
  const $inputOther = $('.containerRadioToCheckbox--other input[id*="lever-"]');

  if ($inputOther.is(':checked')) {
    $inputButtons.slideDown();
  } else {
    $inputButtons.slideUp();
  }
}

function addImageToSelectImpactAreas() {
  
    const $listRender = $('div[listname="innovation.impactAreas"] .panel-body li.relationElement');
  
    $listRender.each(function(index, element) {
      const $elementVisualization = $(element);
      const $elementId = $(element).find('input[type="hidden"].elementRelationID').val();
  
      if($elementVisualization.find('.impactAreaImage').length == 0) {
        
        $.ajax({
          url: baseURL + '/getImpactAreaImage.do',
          async: true,
          data: {
            requestID: Number.parseInt(Number.parseInt($elementId))
          },
          success: function(data) {
    
            if(data) {
              //render image in a before element in the elementVisualization
              $elementVisualization.find('.elementName').before(`<img src="${data.image.adsoluteURL}" class="impactAreaImage" alt="impactArea-${$elementId}">`);
            }
          },
          error: function(xhr, status, error) {
            console.error(error);
            reject(error);
          }
        });
      }
  
    });
}

function onDisplayItemsInOneCGIAR(){
  const $commentBox = $('.contributionToCGIARComment');
  const $selectImpactArea = $('.linkToImpactAreas');
  const $radioButton = $('input.radioType-contributionToCGIAR:checked');

  const content = $selectImpactArea.find('.form-group');

  if($radioButton.val() === "false"){
    $selectImpactArea.not(content).slideUp(400);
    $commentBox.slideDown("slow");

  } else {
    $selectImpactArea.not(content).slideDown("slow");
    $commentBox.slideUp(400);
    
  }
}

function changeDisplayMessageInScaling() {
  const $readinessScale = $('input[name="innovation.projectInnovationInfo.readinessScale"]:checked').val();

  const $scalingMessageContainer = $('.scaling__message');
  const $listScalingHiddenInfo = $('.scaling__hiddenInfo .scaling__hiddenInfo__item');

  $listScalingHiddenInfo.each(function(index, element) {
    const $element = $(element);
    const $elementValue = $element.attr('id');

    if($elementValue == $readinessScale) {
      $scalingMessageContainer.find('h5').html($element.find('h5').html());
      $scalingMessageContainer.find('p').html($element.find('p').html());
    }
  });
}

const deliverablePartnersModule = (function () {

  function init() {
    console.log('Starting deliverablePartnersModule');

    updateInstitutionSelects();

    attachEvents();
  }

  function attachEvents() {
    // On change institution
    $('select.partnerInstitutionID').on('change', changePartnerInstitution);

  }

  function changePartnerInstitution() {
    var $deliverablePartner = $(this).parents('.deliverablePartnerItem');
    var $usersBlock = $deliverablePartner.find('.usersBlock');
    var typeID = $deliverablePartner.find('input.partnerTypeID').val();
    var isResponsible = (typeID == 1);
    // Clean users list
    $usersBlock.empty();

    const $partnerUsersBlock = $('#partnerUsers .institution-' + this.value + ' .users-' + typeID);

    $partnerUsersBlock.find('input').each(function (_i, user) {
      user.id = user.id + '_TEMPLATE_';
    });

    $partnerUsersBlock.find('label').each(function (_i, label) {
      label.htmlFor = label.htmlFor + '_TEMPLATE_';
    });
    // Get new users list
    var $newUsersBlock = $partnerUsersBlock.clone(true);

    $newUsersBlock.find('input').each(function (_i, user) {
      if((user.id).includes('_TEMPLATE_')){
        user.id = user.id.replace('_TEMPLATE_', '');
      }
    });

    $newUsersBlock.find('label').each(function (_i, label) {
      if((label.htmlFor).includes('_TEMPLATE_')){
        label.htmlFor = label.htmlFor.replace('_TEMPLATE_', '');
      }
    });

    // Show them
    $usersBlock.append($newUsersBlock.html());
  }

  function updateInstitutionSelects() {
    var $listBlock = $('.projectInnovationsPartners');
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

/**
 * Module for managing evidence references in a project.
 * 
 * This module provides functionality to initialize event listeners, add and remove reference items,
 * update indexes, handle changes in deliverable types, and track selected options to prevent duplication.
 * 
 * @module evidencesModule
 * 
 * @function init
 * @description Initializes event listeners and updates indexes for the specified reference.
 * @param {string} nameReferenceParam - The reference name used to identify elements and bind events.
 * 
 * @function addReference
 * @description Adds a new reference item to the reference list. This function clones a template element,
 * updates its attributes, and appends it to the reference list. It also initializes the select2 plugin on
 * the new select elements and updates the indexes and options.
 * 
 * @function removeReference
 * @description Removes the reference element from the DOM with a hide animation. This function hides the
 * parent element with a class of 'evidences' of the element that triggered the event, then removes it from
 * the DOM. After removal, it updates the selected options in all instances and updates the indexes.
 * 
 * @function updateIndexes
 * @description Updates the indexes of elements within a reference list. This function iterates over each
 * `.evidences` element within a reference list and updates their indexes. It performs tasks such as setting
 * name indexes, updating the `for` attribute of labels, and updating the class of radio inputs and blocks.
 * 
 * @function changeDeliverableType
 * @description Handles the change event for the deliverable type dropdown. Fetches and updates the sub-type
 * options based on the selected deliverable type.
 * 
 * @function trackOptionsSelectedInAllInstances
 * @description Tracks the options selected in all instances of the evidence select elements and disables
 * the options that are already selected in other instances to prevent duplication.
 * 
 * @returns {Object} An object containing the `init` function to initialize the module.
 */
const evidencesModule = function () {

  let nameReference = '';

  /**
   * Initializes event listeners and updates indexes for the specified reference.
   *
   * @param {string} nameReferenceParam - The reference name used to identify elements and bind events.
   */
  function init(nameReferenceParam) {

    nameReference = nameReferenceParam;
    $(`.addButtonReference${nameReference}`).on('click', addReference);
    $(`.removeButtonReference${nameReference}`).on('click', removeReference);

    // Change deliverable type
    $(".typeSelect").on("change", changeDeliverableType);

    trackOptionsSelectedInAllInstances();

    $(`.evidenceInnovation${nameReference} , .evidenceDeliverable${nameReference}`).on("change", trackOptionsSelectedInAllInstances);

    updateIndexes();
  }

  // Functions

  /**
   * Adds a new reference item to the reference list.
   * 
   * This function clones a template element, updates its attributes, and appends it to the reference list.
   * It also initializes the select2 plugin on the new select elements and updates the indexes and options.
   * 
   * @function
   */
  function addReference() {
    
    const $listBlock = $(`.referenceList${nameReference}`);
    const $template = $(`#evidences-${nameReference}-template`);

    // remove select2 data to avoid corruption in clone process
    if ($template.find('select').data('select2')) {
      $template.find('select').select2("destroy");
    }

    const $newItem = $template.clone(true).removeAttr('id');
    $newItem.find('input, select').each(function(_i,e) {
      e.name = (e.name).replace("_TEMPLATE_", "");
      e.id = (e.id).replace("_TEMPLATE_", "");
    });
    $newItem.find('label').each(function(_i,e) {
      e.htmlFor = (e.htmlFor).replace("_TEMPLATE_", "");
    });

    // Remove class _TEMPLATE_ from divs blocks inner display option
    $newItem.find('div[class^="block-"]').each(function(_i,e) {
      e.className = (e.className).replace("_TEMPLATE_", "");
    });
    // Add select2 to select2 library
    $template.find('select').select2();
    $newItem.find('select').select2({
      data: function (data) {
        return data;
      },
      escapeMarkup: function(markup) {
        return markup;
      },
      templateResult: function(data) {
        return data.text;
      },
      templateSelection: function(data) {
        return data.text;
      }
    });

    // Show the element
    $newItem.appendTo($listBlock).hide().show(350);

    //Update options selected in all instances
    trackOptionsSelectedInAllInstances();
    // Update indexes
    updateIndexes();
  }

  /**
   * Removes the reference element from the DOM with a hide animation.
   * 
   * This function hides the parent element with a class of 'evidences' 
   * of the element that triggered the event, then removes it from the DOM.
   * After removal, it updates the selected options in all instances and 
   * updates the indexes.
   * 
   * @function
   */
  function removeReference() {
    var $parent = $(this).parents('.evidences');
    $parent.hide(500, function() {
      // Remove DOM element
      $parent.remove();
      //Update options selected in all instances
      trackOptionsSelectedInAllInstances();
      // Update indexes
      updateIndexes();
    });
  }

  /**
   * Updates the indexes of elements within a reference list.
   * 
   * This function iterates over each `.evidences` element within a reference list and updates their indexes.
   * It performs the following tasks:
   * - Sets name indexes for each reference.
   * - Updates the `for` attribute of labels to match the `id` of the preceding input element.
   * - For radio inputs, updates the `id` and `for` attributes to include the reference and index.
   * - Updates the class of radio inputs to include the reference and index.
   * - Updates the class of blocks to match the `radioType-` indexes.
   * 
   * @function updateIndexes
   * @returns {void}
   */
  function updateIndexes() {

    $(`.referenceList${nameReference}`).find('.evidences').each(function(i, reference) {
      $(reference).setNameIndexes(1, i);

      $(reference).find('label').each(function(_i,e) {
        let newForValue = $(e).prev('input').attr('id');
        $(e).attr('for', newForValue);

        // change radioType- based on the reference and index
        if($(e).prev('input').attr('type') == 'radio'){
          newForValue = $(e).prev('input').attr('id') + '.' + $(e).prev('input').attr('value');
          $(e).attr('for', newForValue);
          $(e).prev('input').attr('id', newForValue);
          $(e).prev('input').attr('class').split(/\s+/).forEach(function(cls) {
            if (cls.startsWith('radioType-')) {
              $(e).prev('input').removeClass(cls);
              const splitCurrentValue = cls.split('-');
              const newRadioType = splitCurrentValue[0] + '-' + $(reference).attr('data-reference') + `_${i}`;
              $(e).prev('input').addClass(newRadioType);
            }
          });
        }
      });

      // Update indexes of blocks to match with the radioType- indexes
      $(reference).find('div[class*="block-"]').each(function(_i,e) {
        $(e).attr('class').split(/\s+/).forEach(function(cls) {
          if (cls.startsWith('block-')) {
            $(e).removeClass(cls);

            const splitCurrentValue = cls.split('-');
            const newValue = splitCurrentValue[0] + '-' + splitCurrentValue[1] + '-' + $(reference).attr('data-reference') + `_${i}`;
            $(e).addClass(newValue);
          }
      });
      });

    });
  }

  /**
   * Handles the change event for the deliverable type dropdown.
   * Fetches and updates the sub-type options based on the selected deliverable type.
   *
   * @function changeDeliverableType
   * @returns {void}
   */
  function changeDeliverableType() {
    const typeID = $(this).val();

    const $parentDeliverableType = $(this).parents('.evidenceType');
    const $subTypeSelect = $parentDeliverableType.find('select.subTypeSelect');
    
    if (typeID == -1){
      return
    }
    
    $.ajax({
        url: baseURL + '/centerDeliverableSubType.do',
        data: {
          deliverableTypeId: typeID,
          phaseID: phaseID
        },
        beforeSend: function() {
          $(".loading.subtype").fadeIn();
          $subTypeSelect.empty();
          $subTypeSelect.addOption("-1", "Select a sub type...");
        },
        success: function(data) {
          $.each(data.deliverableSubTypes, function(i,type) {
            $subTypeSelect.addOption(type.id, type.name);
          });
        },
        complete: function() {
          $(".loading.subtype").fadeOut();
          $subTypeSelect.select2();
        }
    });
  
  }

  /**
   * This function tracks the options selected in all instances of the evidence select elements
   * and disables the options that are already selected in other instances to prevent duplication.
   * 
   * @function trackOptionsSelectedInAllInstances
   * @returns {void}
   */
  function trackOptionsSelectedInAllInstances() {
    const $listBlock = $(`.referenceList${nameReference}`);
    const selectedValues = new Set();

    // Collect the selected values from all select elements within the reference list
    $listBlock.find('select.evidence option:selected').each(function() {
      selectedValues.add(this.value);
    });

    // Disable options in all select elements that are already selected in other instances to prevent duplication
    $listBlock.find('select.evidence').each(function() {
      const $select = $(this);
      $select.find('option').each(function() {
        const $option = $(this);
        const isSelectedAndNotCurrent = selectedValues.has($option.val()) && !$option.is(':selected');
        if (isSelectedAndNotCurrent) {
          $option.prop('disabled', true);
        } else {
          $option.prop('disabled', false);
        }
      });

      // Reinitialize select2 to reflect changes
      if ($select.data('select2')) {
        $select.select2('destroy');
      }
      $select.select2({
        width: '100%',
        templateResult: formatList,
        templateSelection: formatList
      });
    });
  }

  return {
    init: init
  }

}


//

function initTestDropdown() {
  const dropdowns = document.querySelectorAll('.allianceOrganizations-institutions');

  $.ajax({
    url: `${baseURL}/getInstitutionsService.do`,
    method: 'GET',
    dataType: 'json',
    success: function(data) {
      console.log('Data received:', data);
      const options = data.institutions.map(item => {
        return { label: item.name, value: item.id };
      });
      
      // Apply data to all dropdowns with the class
      dropdowns.forEach(dropdown => {
        dropdown.data = options;
        
        // Set initial value if available
        const initialValue = dropdown.getAttribute("data-value");
        if (initialValue) {
          dropdown.value = parseInt(initialValue);
        }
        
        // Listen for value changes
        dropdown.addEventListener('valueChange', (event) => {
          console.log('Selected value:', event.detail);
        });
      });

      console.log('Data loaded successfully for all dropdowns');
    },
    error: function(xhr, status, error) {
      console.error('Error loading data:', error);
      console.error('Response text:', xhr.responseText);
      console.error('Status code:', xhr.status);
    }
  });
}