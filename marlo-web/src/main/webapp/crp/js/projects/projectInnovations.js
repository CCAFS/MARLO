$(document).ready(function() {

  // Add select2
  addSelect2();


  // Add Geographic Scope
  /*   $('select.elementType-repIndGeographicScope ').on("addElement removeElement", function(event,id,name) {
    setGeographicScope(this);

    $('div.nationalBlock span.selection span.select2-selection--multiple').append('<span class="select2-selection__arrow" role="presentation"><b role="presentation"></b></span>');
  }); */

  $('input.radioType-geographicScopes').on("change", function() {
    setGeographicScope2(this);
    $('select.countriesSelect').each(function(i, element) {
      dynamicMarginToSelectedRender(element);
    });
  });

  $('select.countriesSelect').on('change', function() {
    dynamicMarginToSelectedRender(this);
  });

  $('select.countriesSelect').each(function(i, element) {
    dynamicMarginToSelectedRender(element);
  });

  $('input.radioType-geographicScopes').each(function(i, element) {
    if($(element).is(':checked')) {
      setGeographicScope2(element);
    }
  })
  //setGeographicScope($('form select.elementType-repIndGeographicScope')[0]);

  //$('div.nationalBlock span.selection span.select2-selection--multiple').append('<span class="select2-selection__arrow" role="presentation"><b role="presentation"></b></span>');

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
  /* $('select.elementType-impactArea').on("change", addImageToSelectImpactAreas);
  addImageToSelectImpactAreas(); */

  // Change display message in Scaling
  $('input[name="innovation.projectInnovationInfo.readinessScale"]').on('change', changeDisplayMessageInScaling);
  changeDisplayMessageInScaling();

  // Update the dynamic visualization of the "Alliance" Tab after selecting in Key Contributors
  updateAllianceTab();
  $('select.elementType-institution').on('change',updateAllianceTab);
  $('div.removeElementType-institution').on('click',updateAllianceTab);

  //init partners methods
  deliverablePartnersModule.init();

  // Run initial check
  dynamicStatusCheckedForEvidences();

  // Listen for changes to impact area scores
  $('input[name^="innovation.projectInnovationInfo"][name$=".id"]').on("change", function() {
    dynamicStatusCheckedForEvidences();
  });
  
  // Listen for changes to readiness scale
  $('input[name="innovation.projectInnovationInfo.readinessScale"]').on("change", function() {
    dynamicStatusCheckedForEvidences();
  });

  // Update status when evidence checkboxes change
  $('.referenceListReadiness').on('change', 'input[type="checkbox"]', function() {
    dynamicStatusCheckedForEvidences();
  });
  
  // Also run when evidence items are added or removed
  $('.addButtonReferenceReadiness, .removeButtonReferenceReadiness').on('click', function() {
    // Small delay to ensure DOM is updated
    setTimeout(dynamicStatusCheckedForEvidences, 100);
  });

  //Add display to accordion items 
  $('.blockTitle.closed').on('click', function() {
    if($(this).hasClass('closed')) {
      $('.blockContent').slideUp();
      $('.blockTitle').removeClass('opened').addClass('closed');
      $(this).removeClass('closed').addClass('opened');
    } else {
      $(this).removeClass('opened').addClass('closed');
    }
    $(this).next().slideToggle('slow', function() {
      $(this).find('textarea').autoGrow();
    });
  });

  feedbackAutoImplementation();

  initDropdownOrganization();
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
    $('.actorsList .actorsInnovation').each(function(_i,e) {
      limitedValueFillInput($(e).find('input.input-total'), $(e).find('input.input-whichWomen'));
      limitedValueFillInput($(e).find('input.input-total'), $(e).find('input.input-whichYouth'));
    });
    $('.actorsList .actorsInnovation').each(function(_i,e) {
      $(e).find('select').on('change', function() {
        //get the text of the selected option
        const selectedText = $(this).find('option:selected').text();
        if(selectedText == "Other") {
          $(e).find('.otherType').slideDown();
        } else {
          $(e).find('.otherType').slideUp();
        }
      });
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

      // Add function to make max value of input number
      limitedValueFillInput($newItem.find('input.input-total'), $newItem.find('input.input-whichWomen'));
      limitedValueFillInput($newItem.find('input.input-total'), $newItem.find('input.input-whichYouth'));

      // Show the element
      $newItem.appendTo($listBlock).hide().show(350);
      // Update indexes
      updateIndexes();

      // Also call onAddDataRelatedToCheckboxGender for each checkbox to set up initial state
      $newItem.find('input[type="checkbox"].check-gender').each(function(_i,_e) {
        onAddDataRelatedToCheckboxGender.call(this);
      });

      // Add event listener for select2 to display otherType
      $newItem.find('select').on('change', function() {
        const selectedText = $(this).find('option:selected').text();
        if(selectedText == "Other") {
          $newItem.find('.otherType').slideDown();
        } else {
          $newItem.find('.otherType').slideUp();
        }
      });

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
      const $inputSexAgeNotApply = $blockSexAgeNotApply.find('input[type="number"]');

      if($element.is(':checked')) {
        $blockSexAgeNotApply.slideUp();
        $checkboxSexAgeNotApply.prop('checked', false);
        $inputSexAgeNotApply.val('');
      } else {
        $blockSexAgeNotApply.slideDown();
      }
    }

    function onAddDataRelatedToCheckboxGender() {
      const $checkboxGender = $(this);
      const $relatedInputNumber = $checkboxGender.parents('.innerOptions').find(`input[type="number"]`);

      $relatedInputNumber.on('change', function() {

        const inputValue = $(this).val().trim();
        
        // If input has a value, ensure the checkbox is checked
        if (inputValue !== '') {
          $checkboxGender.prop('checked', true);
        } 
        else {
          $checkboxGender.prop('checked', false);
        }
      });
    }

  })();

  /**
   * Organizations Component
   */
  ( function () {
    // Events
    $('.addOrganizations').on('click', addOrganization);
    $('.removeOrganization').on('click', removeOrganization);

    // Function - KEEP ONLY THIS IMPLEMENTATION
    function addOrganization() {
      const $listBlock = $('.organizationsList');
      const $template = $('#organizationsInnovation-template');

      const $newItem = $template.clone(true).removeAttr('id');
      
      // First handle standard elements
      $newItem.find('input:not([readonly]), select, input[type="checkbox"]:not([readonly])').each(function(_i,e) {
        if(e.name && e.name.includes("_TEMPLATE_")) {
          e.name = (e.name).replace("_TEMPLATE_", "");
          e.id = (e.id).replace("_TEMPLATE_", "");
        }
      });
      
      $newItem.find('label').each(function(_i,e) {
        e.htmlFor = (e.htmlFor).replace("_TEMPLATE_", "");
      });

      // Handle the mal-select component properly using attributes
      const $malSelect = $newItem.find('.allianceOrganizations-institutions');
      
      // Update name and id attributes instead of properties
      $malSelect.attr('name', function(i, oldName) {
        return oldName.replace("_TEMPLATE_", "");
      });
      
      $malSelect.attr('id', function(i, oldId) {
        return oldId.replace("_TEMPLATE_", "");
      });
      
      // Copy data from template component
      $malSelect[0].data = $template.find('.allianceOrganizations-institutions')[0].data;
      
      // Add event listener for valueChange
      $malSelect[0].addEventListener('valueChange', function() {
        // This will handle any inner select elements if they exist
        const $innerSelect = $(this).find('select');
        $innerSelect.each(function(_i,e) {
          e.name = (e.name).replace("_TEMPLATE_", "");
          e.id = (e.id).replace("_TEMPLATE_", "");
        });
      });
      
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

      });
    }

  })();

  /**
   * Accordion Complementary Innovations
   */
  ( function () {
    // Events
    $('.addButtonComplementarySolutions').on('click', addComplementaryInnovation);
    $('.removeComplementaryInnovation').on('click', removeComplementaryInnovation);
    
    // Function

    function addComplementaryInnovation() {
      console.log('Adding complementary innovation');
      const $listBlock = $('.complementarySolutionsList');
      const $template = $('#complementaryInnovation-template');

      // remove select2 data to avoid corruption in clone process
      if ($template.find('select').data('select2')) {
        $template.find('select').select2("destroy");
      }

      const $newItem = $template.clone(true).removeAttr('id');
      $newItem.find('input, select, textarea').each(function(_i,e) {
        e.name = (e.name).replace("_TEMPLATE_", "");
        e.id = (e.id).replace("_TEMPLATE_", "");
      });
      $newItem.find('label').each(function(_i,e) {
        e.htmlFor = (e.htmlFor).replace("_TEMPLATE_", "");
      });

      // Add select2 to select2 library
      $template.find('select').select2();
      $newItem.find('select').select2();

      // Show the element
      $newItem.appendTo($listBlock).hide().show(350);
      // Update indexes
      updateIndexes();
    }
    function removeComplementaryInnovation() {
      var $parent = $(this).parents('.complementaryInnovation');
      $parent.hide(500, function() {
        // Remove DOM element
        $parent.remove();
        // Update indexes
        updateIndexes();
      });
    }
    function updateIndexes() {
      $('.complementarySolutionsList').find('.complementaryInnovation').each(function(i, innovation) {
        $(innovation).setNameIndexes(1, i);
        
        $(innovation).find('label').each(function(_i,e) {
          const newForValue = $(e).prev('input').attr('id');
          $(e).attr('for', newForValue);
        });
      });
    }
  })();


  /**
   * Bundle Innovation Select Innovation
   */
  ( function () {
    // Events
    $('.selectInnovationBundle').on('click', addBundleInnovation);
    $('.removeInnovationBundleItem').on('click', removeBundleInnovation);
    if($('.innovationBundleList').find('.innovationBundleItem').length == 0) {
      $('.innovationBundleList').append('<p><i>No innovations selected</i></p>');
    }
    // Function
    function addBundleInnovation(e) {
      e.preventDefault();

      const $button = $(this);

      const innovationId = $button.attr('data-id');
      const innovationName = $button.attr('data-name');

      console.log('Adding bundle innovation with ID:', innovationId, 'and Name:', innovationName);

      const $listBlock = $('.innovationBundleList');
      const $template = $('#innovationBundleItem-template');

      if($listBlock.find('.innovationBundleItem').length == 0) {
        $listBlock.empty(); // Clear the list message before adding new items
      }

      const $newItem = $template.clone(true).removeAttr('id');

      // add data-id and data-name attributes to the new item
      $newItem.find('.innovationBundleItemID').text(innovationId);
      $newItem.find('.innovationBundleItemName').text(innovationName);

      $newItem.find('input').each(function(_i,e) { 
        if(e.name && e.name.includes("_TEMPLATE_")) {
          e.name = (e.name).replace("_TEMPLATE_", "");
          e.id = (e.id).replace("_TEMPLATE_", "");
        }
      });

      // Update reference hidden input to be save in database
      $newItem.find('input[type="hidden"]#reference-selected').val(innovationId);

      $button.prop('disabled', true); // Disable the button after selection

      // Show the element
      $newItem.appendTo($listBlock).hide().show(350);
      // Update indexes
      updateIndexes();
    }
    function removeBundleInnovation() {
      var $parent = $(this).parents('.innovationBundleItem');
      $parent.hide(500, function() {
        // Remove DOM element
        $parent.remove();
        // Re-enable the select button for the removed innovation
        const innovationId = $parent.find('.innovationBundleItemID').text();
        console.log('Re-enabling button for innovation ID:', innovationId);
        const $selectButton = $('.selectInnovationBundle[data-id="' + innovationId + '"]');
        $selectButton.prop('disabled', false); // Re-enable the button
        // Update indexes
        updateIndexes();
      });
    }
    function updateIndexes() {
      $('.innovationBundleList').find('.innovationBundleItem').each(function(i, innovation) {
        $(innovation).setNameIndexes(1, i);
        
        $(innovation).find('label').each(function(_i,e) {
          const newForValue = $(e).prev('input').attr('id');
          $(e).attr('for', newForValue);
        });
      });

      // If no items left, show a message
      if($('.innovationBundleList').find('.innovationBundleItem').length == 0) {
        $('.innovationBundleList').append('<p><i>No innovations selected</i></p>');
      }
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

  $('input[name="innovation.projectInnovationInfo.hasKnowledgePotential.id"]').on('change', function() {
    var selected = this.value;
    console.log(selected);
    if(selected == 2) {
      $('.block-w-hasKnowledgePotential').slideDown();
    } else {
      $('.block-w-hasKnowledgePotential').slideUp();
    }
  });

  //On change radio buttons
  $('input[class*="radioType-"]').on('change', onChangeRadioButton);

  // On change checkbox buttons in alliance lever display other field
  displayInnerOtherInput();
  $('.containerRadioToCheckbox--other input[type="checkbox"][id*="lever-"]').on('change',displayInnerOtherInput);
  //On change checkbox buttons - alliance
  updateIndexListCheckbox();
  $('input[type="checkbox"][id*="lever-"]').on('change', updateIndexListCheckbox);

  //On change radio buttons - One CGIAR
  //$('input.radioType-contributionToCGIAR').on('change', onDisplayItemsInOneCGIAR);

  //On change radio buttons - Notes in Scores - One CGIAR
  $('input.radioType-contributionToCGIAR').on('change', onDisplayNotesInScores);

  // Use event delegation to handle "Other" checkbox changes for both existing and new elements
  $('div[listname="innovation.contributingOrganizations"]').on('change', 'input[id$="other"]', function() {
    const $element = $(this).closest('.relationElement');
    
    if(this.checked) {
      $element.find('input[type="checkbox"]').not(this).prop("checked", false);
    } else {
      $element.find('input[type="checkbox"]').not(this).prop("checked", false);
    }
  });

  // On change checkbox buttons - Contributing Organizations except "Other"
  $('div[listname="innovation.contributingOrganizations"]').on('change', 'input[type="checkbox"]', function() {
    const $element = $(this).closest('.relationElement');
    const $checkboxOther = $element.find('input[id$="other"]');
    const $checkboxContributingOrganizations = $element.find('input[type="checkbox"]').not($checkboxOther);
    const $checkboxContributingOrganizationsChecked = $checkboxContributingOrganizations.filter(':checked');

    if($checkboxContributingOrganizationsChecked.length > 0) {
      $checkboxOther.prop("checked", false);
    }
  });

  CustomSortableList("div[listname='innovation.contributingOrganizations'] .panel-body ul.list");

  addDataTableAllInnovations();

  changeInformativeTextPRMSEquivalence();
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
      placeholder: "Select a country",
      templateResult: formatStateCountries,
      templateSelection: formatStateCountries,
      dropdownPosition: "above",
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

  // Copy URL Button event
 $(".copyButton").click(function() {
        var $button = $(this); // Get the clicked button
        var $parent = $button.closest(".generalInnovationsOptions"); // Find the closest parent container
        var $input = $parent.find(".urlInput"); // Locate the input field with the URL

        if ($input.length) {
            var textToCopy = $input.val().trim();

            if (textToCopy) {
                // Copy text to clipboard
                var tempInput = $("<input>");
                $("body").append(tempInput);
                tempInput.val(textToCopy).select();
                document.execCommand("copy");
                tempInput.remove();

                // Store original button text
                var originalText = $button.html();

                // Change button text to "Copied!"
                $button.html('<span class="glyphicon glyphicon-ok"></span> Copied to clipboard!').css({
                    "background-color": "#28a745",
                    "color": "#fff",
                    "border-color": "#28a745"
                });

                setTimeout(function() {
                    $button.html(originalText).css({
                        "background-color": "",
                        "color": "",
                        "border-color": ""
                    });
                }, 1000);
            }
        }
    });

function updateAllianceTab() {
  var $selectCenters = $('div[listname="innovation.centers"] select.elementType-institution');

    setTimeout(() => {
      $option = $selectCenters.find('option[disabled]');

        if($option.toArray().some((item) => item.innerHTML.toLowerCase().includes("alliance"))) {
          //remove disabled class alliance tab
          $('#allianceTab').slideDown();
          $('li[role="presentation"]').css('width', "20%");
        } else {
          //add disabled class alliance tab
          $('#allianceTab').slideUp();
          $('li[role="presentation"]').css('width', "25%");
        }

    }, 500);

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

/**
 * Function to add image to the impact areas in the select list.
 * DEPRECATED: This function is no longer used in the codebase.
 * The reason is the section were It was used was removed from the project.
 * Section: old version of One CGIAR Aligment
 */
/* function addImageToSelectImpactAreas() {
  
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
} */

/**
 * Function to display items in the CGIAR section based on the selected radio button.
 * DEPRECATED: This function is no longer used in the codebase.
 * The reason is the section were It was used was removed from the project.
 * Section: old version of One CGIAR Aligment
 */
/* function onDisplayItemsInOneCGIAR(){
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
} */

function onDisplayNotesInScores() {
  const $nameInput = $(this).attr('name');
  const $valueInput = $(this).val();

  if($valueInput == "3") {
    $(`div.note[name="${$nameInput}"]`).slideDown();
  } else {
    $(`div.note[name="${$nameInput}"]`).slideUp();
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
    $newItem.find('input, select, textarea').each(function(_i,e) {
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

function dynamicMarginToSelectedRender(select){
  const $select = $(select);

  if(!$select.length) {
    console.warn('Invalid element passed to dynamicMarginToSelectedRender');
    return;
  }
  
  // Add a delay to ensure DOM elements are fully rendered
  setTimeout(() => {
    const $selectedMultiple = $select.next('.select2-container--default').find('.select2-selection--multiple');
    const $rendered = $select.next('.select2-container--default').find('.select2-selection__rendered');

    if($rendered.children().length > 0){
      $selectedMultiple.css('margin-bottom',`${$rendered.height()+30}px`);
    } else {
      $selectedMultiple.css('margin-bottom','0');
    }
  }, 10); // 100ms delay to allow rendering to complete
}

function dynamicStatusCheckedForEvidences() {
  // Define impact areas to check
  const impactAreas = [
    { name: "genderScore", displayName: "Gender equality", displayReference: "gender" },
    { name: "climateChangeScore", displayName: "Climate change", displayReference: "climateChange" },
    { name: "foodSecurityScore", displayName: "Food security", displayReference: "nutrition" },
    { name: "environmentalScore", displayName: "Environmental health", displayReference: "environmental" },
    { name: "povertyScore", displayName: "Poverty reduction", displayReference: "poverty" },
  ];
  
  // Initialize messages array
  const messages = [];

  // Track which impact areas need evidence
  const areasNeedingEvidence = [];
  
  // Check for score of 2 in any impact area
  impactAreas.forEach(area => {
    const scoreElement = $(`input[name="innovation.projectInnovationInfo.${area.name}.id"]:checked`);
    if (scoreElement.length > 0 && scoreElement.val() == 3) {
      areasNeedingEvidence.push(area.displayReference);
      messages.push(`As a score of 2 has been selected, you are required to provide at least one evidence of the ${area.displayName}, by select the checkbox.`);
    }
  });
  
  // Check the innovation readiness scale value
  const readinessElement = $('input[name="innovation.projectInnovationInfo.readinessScale"]:checked');
  let readinessNeedsEvidence = false;
  
  if (readinessElement.length > 0) {
    const readinessValue = parseInt(readinessElement.val());
    if (readinessValue >= 2) {
      readinessNeedsEvidence = true;
      messages.push(`Provide at least one evidence for innovation readiness level.`);
    }
  }

  const evidences = $('.referenceListReadiness .evidences')

  if (evidences.length > 0) {
    evidences.each(function(index, element) {
      const $element = $(element);

      const $typeCheckboxes = $element.find('input[type="checkbox"]:checked');
      
      $typeCheckboxes.each(function() {
        const checkboxId = $(this).attr('id');
        
        // Remove messages for impact areas that have evidence
        areasNeedingEvidence.forEach((area, index) => {
          if (checkboxId && checkboxId.includes(area)) {
            // Remove this message as evidence is provided
            areasNeedingEvidence.splice(index, 1);
            messages.splice(index, 1);
          }
        });
        
        // Remove readiness message if evidence is provided
        if (readinessNeedsEvidence && checkboxId && checkboxId.includes('innovationReadiness')) {
          // Find and remove the readiness message
          const readinessIndex = messages.findIndex(msg => msg.includes('innovation readiness level.'));
          if (readinessIndex !== -1) {
            messages.splice(readinessIndex, 1);
          }
        }
      });
    });
  }
  
  // Update the status label content
  const statusLabel = $(".statusEvidenceInImpactArea");
  const contentElement = statusLabel.find(".contentInformation");

  
  if (messages.length > 0) {
    // Show label with message list
    statusLabel.show();
    
    // Create list if multiple messages, otherwise show single message
    if (messages.length > 1) {
      contentElement.html("<ul style='margin: 5px 0; padding-left: 20px;'>" + 
                         messages.map(msg => `<li>${msg}</li>`).join("") + 
                         "</ul>");
    } else {
      contentElement.html(`<p>${messages[0]}</p>`);
    }
    
  } else {
    // Hide the label when no conditions are met
    statusLabel.hide();
  }
}

function initDropdownOrganization() {
  const dropdowns = document.querySelectorAll('.allianceOrganizations-institutions');

  $.ajax({
    url: `${baseURL}/getInstitutionsService.do`,
    method: 'GET',
    dataType: 'json',
    success: function(data) {
      //console.log('Data received:', data);
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
          console.log('This is the dropdown:', dropdown);
        });
      });

      //console.log('Data loaded successfully for all dropdowns');
    },
    error: function(xhr, status, error) {
      console.error('Error loading data:', error);
      console.error('Response text:', xhr.responseText);
      console.error('Status code:', xhr.status);
    }
  });

}

function addDataTableAllInnovations() {
  $('#table-all-innovations').each(function(_i,table) {
    // Skip empty tables or tables without proper structure
    if ($(table).find('thead th').length === 0 || $(table).find('tbody').length === 0) {
      console.warn('Skipping DataTables initialization for invalid table structure.');
      return;
    }

    // Prevent re-initialization
    if ($.fn.dataTable.isDataTable(table)) {
      return;
    }

    // Get total number of columns
    const columns = $(table).find('thead th').length;

    const noSortColumns = [];
    $(table).find('thead th.no-sort').each(function() {
      noSortColumns.push($(this).index());
    });

    try {
      $(table).DataTable({
        // DataTables options
        "bPaginate": true,
        "bLengthChange": true,
        "bFilter": true,
        "bSort": true,
        "bAutoWidth": false,
        "iDisplayLength": 10,
        "language": {
          "searchPlaceholder": "Search...",
          "emptyTable": "No innovation entries entered into the system yet."
        },
        "order": columns > 1 ? [[1, 'desc']] : [],
        "columnDefs": [
          { "targets": noSortColumns, "orderable": false }
        ]
      });

      // Add styles to the table
      const $table = $(table);
      const $wrapper = $table.closest('.dataTables_wrapper');

      if ($wrapper.length) {
        const iconSearch = $("<div></div>").addClass("iconSearch");
        const $filter = $wrapper.find('.dataTables_filter');

        if ($filter.length) {
          iconSearch.append('<img src="' + baseUrl + '/global/images/search_outline.png" alt="Search" style="width: 24px; margin: auto;">');
          $filter.parent().prepend(iconSearch);
        }

        const $length = $wrapper.find('.dataTables_length');
        if ($length.length) {
          $length.parent().css({
            "position": "absolute",
            "bottom": "8px",
            "margin-left": "33%",
            "z-index": "1",
            "width": "25%"
          });
        }
      }

      //Add custom select to make personalized filter my project/cluster
      const $firstChildren = $wrapper.children().first();
      if ($firstChildren.length) {
        const $selectContainer = $('<div class="select-container"></div>');
        const $selectLabel = $('<label for="filter-select">Cluster:</label>');
        const $select = $('<select class="form-control select2"></select>');
        
        $selectContainer.css({
          "margin-left": "15px",
          "margin-right": "15px",
          "z-index": "1",
          "position": "absolute",
          "width": "25%",
          "display": "flex"
        });

        $selectLabel.css({
          "margin-right": "5px"
        });

        $select.css({
          "width": "100% !important",
          "height": "30px !important",
          "margin-left": "5px",
  
        });

        ajaxAllClusters($select);

        $selectContainer.append($selectLabel);
        $selectContainer.append($select);

        $select.on('change', function() {
          const selectedValue = $(this).val();
          const table = $('#table-all-innovations').DataTable();
          table.column(2).search(selectedValue ? '^' + selectedValue + '$' : '', true, false).draw();
        });

        $firstChildren.prepend($selectContainer);
      }

    } catch (error) {
      console.error('Error initializing DataTable:', error);
    }
  });
}

function ajaxAllClusters(selectElement) {
  $.ajax({
    url: baseURL + '/projectList.do',
    type: 'GET',
    data: {
      year: currentCrpSession,
      phaseID: phaseID
    },
    success: function(data) {

      $(selectElement).append('<option value="">All Clusters</option>');

      data.projects.forEach(function(cluster) {
        $(selectElement).append(`<option value="${cluster.acronym}">${cluster.acronym}</option>`);
      });

      // Initialize select2 on the new select element
      $(selectElement).select2({
        width: '100%',
        height: '30px',
        placeholder: "Select a cluster",
        allowClear: true
      });
    },
    error: function(xhr, status, error) {
      console.error('Error loading clusters:', error);
    }
  });
}

function limitedValueFillInput(reference, input) {

  if (!$(reference).length || !$(input).length) {
    console.warn('Invalid elements passed to limitedValueFillInput');
    return;
  }

  // Set initial max value based on the reference input
  const initialMaxValue = parseInt($(reference).val(), 10) || 0;
  const $input = $(input);
  $input.attr('max', initialMaxValue);
  // If the current value exceeds the initial max, adjust it
  const currentValue = parseInt($input.val(), 10) || 0;
  if (currentValue > initialMaxValue) {
    $input.val(initialMaxValue);
  }

  $(reference).on('change', function() {
    const maxValue = parseInt($(reference).val(), 10) || 0;
    
    // Set max attribute for number inputs
    $input.attr('max', maxValue);
    
    // If the current value exceeds the new max, adjust it
    const currentValue = parseInt($input.val(), 10) || 0;
    if (currentValue > maxValue) {
      $input.val(maxValue);
    }
  });

  $(input).on('change', function() {
    const maxValue = parseInt($(reference).val(), 10) || 0;

    // If the current value exceeds the new max, adjust it
    const currentValue = parseInt($(input).val(), 10) || 0;
    if (currentValue > maxValue) {
      $(input).val(maxValue);
    }
  });
}

function changeInformativeTextPRMSEquivalence() {
  const $selectInnovationType = $('select[name="innovation.projectInnovationInfo.repIndInnovationType.id"]');

  const $blockPRMSEquivalence = $('.prmsEquivalentBlock');

  $selectInnovationType.on('change', function() {
    const selectedValue = $(this).val();
    if (selectedValue) {
      $blockPRMSEquivalence.show();

      const $textPRMSEquivalence = $blockPRMSEquivalence.find('.prmsEquivalentText');

      $textPRMSEquivalence.each(function() {
        const $this = $(this);
        const typeId = $this.attr('data-id');
        console.log('Type ID:', typeId, 'Selected value:', selectedValue);
        if (typeId == selectedValue) {
          $this.parent().show();
        } else {
          $this.parent().hide();
        }
      });
    } else {
      $blockPRMSEquivalence.hide();
    }
  });
}