$(document).ready(function () {

  // Add select2
  addSelect2();

  // Add Geographic Scope
  $('select.elementType-repIndGeographicScope ').on("addElement removeElement", function (event, id, name) {
    setGeographicScope(this);
  });
  setGeographicScope($('form select.elementType-repIndGeographicScope')[0]);

  // Activate Popup
  popups();

  // Attach Events
  attachEvents();
  AddRequired();
  $('input[name="innovation.projectInnovationInfo.evidenceLink"]').prop('disabled', true);
});

function attachEvents() {
  /**
   * Links Component
   */
  (function () {
    // Events
    $('.addButtonLink').on('click', addItem);
    $('.removeLink.links').on('click', removeItem);
    $('.multiInput').find('span input').on('input', validateURL);

    // Functions
    function addItem() {
      var $list = $(this).parent('.linksBlock').find('.linksList');
      var $element = $('#multiInput-links-template').clone(true).removeAttr("id");
      var $listLength = $list.children().length;
      if ($listLength <= 9) {
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
      var $parent = $(this).parent('.multiInput.links');
      var $addBtn = $(this).parent().parent().parent().find('.addButtonLink');
      $parent.hide(500, function () {
        // Remove DOM element
        $parent.remove();
        // Update indexes
        updateIndexes($addBtn);
      });
    }
    function updateIndexes(list) {
      $(list).parent('.linksBlock').find('.linksList').find('.multiInput').each(function (i, element) {
        $(element).find('.indexTag').text(i + 1);
        $(element).setNameIndexes(1, i);
      });
    }
    function validateURL() {
      var url = this.value;
      var expression = /[-a-zA-Z0-9@:%_\+.~#?&//=]{2,256}\.[a-z]{2,4}\b(\/[-a-zA-Z0-9@:%_\+.~#?&//=]*)?/gi;
      var regex = new RegExp(expression);
      var res = "";
      if (url.match(regex)) {
        res = "Valid URL";
        $(this).css('border', 'none');
        console.log(res);
      } else {
        res = "Invalid URL";
        $(this).css('border', '1px solid red');
        console.log(res);
      }
    }

  })();

  // 
  $('#isClearLeadToAddRequired').on('click', AddRequired);

  // Check the stage of innovation
  $('select.stageInnovationSelect').on('change', function () {
    stageValidations();
  });

  stageValidations();

  // Check the stage of innovation
  $('select.innovationTypeSelect').on('change', function () {
    console.log(this.value)
    var id = this.value;
    if (id == 6) {
      $('.typeSixBlock').slideDown();
      $('.numberInnovations-block').slideUp();
    } else if (id == 1) {
      $('.numberInnovations-block').slideDown();
      $('.typeSixBlock').slideUp();
    } else {
      $('.typeSixBlock').slideUp();
      $('.numberInnovations-block').slideUp();
    }
  });

  $('input.isClearLead').on('change', function () {
    var selected = $('input.isClearLead').is(":checked");

    if (selected == true) {
      $('.lead-organization').slideUp();
    } else {
      $('.lead-organization').slideDown();
    }

  })

  //On change radio buttons
  $('input[class*="radioType-"]').on('change', onChangeRadioButton);

  validateEmptyLinks();
  $('.addButtonLink').on('click', validateEmptyLinks);
  $('.removeLink.links').on('click', validateEmptyLinks);
}

function validateEmptyLinks() {
  var linksList = $('.linksList').children('div').length;

  if ($(this).hasClass('removeElement')) {
    linksList -= 1;
  } else {
    linksList = linksList;
  }
  if ( linksList > 0) {
    $('#warningEmptyLinksTag').hide();
  } else {
    $('#warningEmptyLinksTag').show();
  }
}

function stageValidations() {
  var isStageTwo = $('select[name="innovation.projectInnovationInfo.repIndStageInnovation.id"]').val() == 2;
  var isStageFour = $('select[name="innovation.projectInnovationInfo.repIndStageInnovation.id"]').val() == 4;
  var evidenceLinkTag = $('label[for="innovation.projectInnovationInfo.evidenceLink"]').find('.requiredTag');

  if (isStageFour) {
    $('.stageFourBlock-true').slideDown();
    $('.stageFourBlock-false').slideUp();
  } else {
    $('.stageFourBlock-true').slideUp();
    $('.stageFourBlock-false').slideDown();
  }

  if (isStageTwo) {
    $(evidenceLinkTag).show();
  } else {
    $(evidenceLinkTag).hide();
  }
}

function AddRequired() {
  console.log($('#isClearLeadToAddRequired').is(":checked"));
  if ($('#isClearLeadToAddRequired').is(":checked")) {
    $('.top-five-contributing').find('.requiredTag').show();
  } else {
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
  if (!state.id || (state.id == "-1")) {
    return state.text;
  }
  var result = "<span>" + state.text + "</span>";
  return $(result);
};

function formatStateCountries(state) {
  if (!state.id) {
    return state.text;
  }
  var flag = '<i class="flag-icon flag-icon-' + state.element.value.toLowerCase() + '"></i> ';
  var $state;
  if (state.id != -1) {
    $state = $('<span>' + flag + state.text + '</span>');
  } else {
    $state = $('<span>' + state.text + '</span>');
  }
  return $state;
};