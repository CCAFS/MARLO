$(document).ready(function() {

  // Add select2
  addSelect2();


  // Add Geographic Scope
  $('select.elementType-repIndGeographicScope ').on("addElement removeElement", function(event,id,name) {
    setGeographicScope(this);
    dynamicMarginToSelectedRender();
  });
  setGeographicScope($('form select.elementType-repIndGeographicScope')[0]);

  dynamicMarginToSelectedRender();

  $('select.countriesSelect').on('change', dynamicMarginToSelectedRender);

  // Activate Popup
  popups();

  // Attach Events
  attachEvents();
  AddRequired();

  // Change counter value of Shared Cluster
  counterSharedCluster();

  //init partners methods
  deliverablePartnersModule.init();

  // Add image to SDG Targets
  $('select.elementType-sdgTarget').on("change", addImageToSelectSDGTargets);
  addImageToSelectSDGTargets();
  // Add image to Impact Areas
  $('select.elementType-impactArea').on("change", addImageToSelectImpactAreas);
  addImageToSelectImpactAreas();




  feedbackAutoImplementation();
});

function attachEvents() {

  /**
   * Actors Component
   */

  ( function () {
    // Events
    $('.addActors').on('click', addActor);
    $('.removeActor').on('click', removeActor);

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
      // Add select2 to select2 library
      $template.find('select').select2();
      $newItem.find('select').select2();

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

    function updateIndexes() {}

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
      if ($template.find('select').data('select2')) {
        $template.find('select').select2("destroy");
      }

      const $newItem = $template.clone(true).removeAttr('id');
      $newItem.find('input, select').each(function(_i,e) {
        e.name = (e.name).replace("_TEMPLATE_", "");
        e.id = (e.id).replace("_TEMPLATE_", "");
      });
      // Add select2 to select2 library
      $template.find('select').select2();
      $newItem.find('select').select2();

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
      $('organizationsList').find('.organizationsInnovation').each(function(i, organization) {
        $(organization).setNameIndexes(1, i);

      });
    }

  })();

  // 
  $('#isClearLeadToAddRequired').on('click', AddRequired);

  // Check the stage of innovation
  $('select.stageInnovationSelect').on('change', function() {
    var isStageFour = this.value == 4;
    if(isStageFour) {
      $('.stageFourBlock-true').slideDown();
      $('.stageFourBlock-false').slideUp();
    } else {
      $('.stageFourBlock-true').slideUp();
      $('.stageFourBlock-false').slideDown();
    }
  });

  // Check the stage of innovation
  $('select.innovationTypeSelect').on('change', function() {
    console.log(this.value)
    var id = this.value;
    if(id == 6) {
      $('.typeSixBlock').slideDown();
      $('.numberInnovations-block').slideUp();
    } else if(id == 1) {
      $('.numberInnovations-block').slideDown();
      $('.typeSixBlock').slideUp();
    } else {
      $('.typeSixBlock').slideUp();
      $('.numberInnovations-block').slideUp();
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

}
function AddRequired(){
  console.log($('#isClearLeadToAddRequired').is(":checked"));
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

  $('form select.countriesIds').select2({
      maximumSelectionLength: 0,
      placeholder: "Select a country(ies)",
      templateResult: formatStateCountries,
      templateSelection: formatStateCountries,
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

function dynamicMarginToSelectedRender(){
  const $selectedMultiple = $('.select2-selection--multiple');
  const $rendered = $('ul.select2-selection__rendered');

  if($rendered.children().length > 0){
    $selectedMultiple.css('margin-bottom',`${$rendered.height()+30}px`);
  } else {
    $selectedMultiple.css('margin-bottom','0');

  }

}

function addImageToSelectSDGTargets() {

  const $listRender = $('div[listname="innovation.sdgTargets"] .panel-body li.relationElement');

  $listRender.each(function(index, element) {
    const $elementVisualization = $(element);
    const $elementId = $(element).find('input[type="hidden"].elementRelationID').val();

    if($elementVisualization.find('.sdgImage').length == 0) {
      
      $.ajax({
        url: baseURL + '/getSdgImage.do',
        async: true,
        data: {
          requestID: Number.parseInt(Number.parseInt($elementId))
        },
        success: function(data) {
          console.log(data);
  
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
            console.log(data);
    
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

    updateIndexes();

  }

  function addPartnerItem() {
    var $listBlock = $('.projectInnovationsPartners');
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
    $('.projectInnovationsPartners .deliverablePartnerItem').each(function (i, partner) {

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