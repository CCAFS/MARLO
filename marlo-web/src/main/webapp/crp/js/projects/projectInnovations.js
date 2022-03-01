var multiInputInnovations;
var innovationAjaxURL = '/qaAssessmentStatus.do?year=2021&indicatorTypeID=1&crpID=';
var innovationArrName = 'fullItemsAssessmentStatus';

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
  $('input[name="innovation.projectInnovationInfo.evidenceLink"]').css('display', 'none');
  $('input[name="innovation.projectInnovationInfo.evidenceLink"]').siblings('p').css('display', 'none');
  $('.innovationContributingCRP select').on('change', checkContributingCRP);
  checkContributingCRP();
  multiInputInnovations = $('.multiInput').find('span input');
  checkHyperlinks();
});

function checkContributingCRP() {
  var actualContributingCRP = $('.innovationContributingCRP').find('.list .relationElement');

  actualContributingCRP.each((index, item) => {
    actualContributingCRPSpan = $(item).find('span.elementName').html();
    var crpName = actualContributingCRPSpan.substr(0, actualContributingCRPSpan.indexOf(' '));
    var actualCRP = $('#actualCRP').html();

    if (crpName == actualCRP) {
      $(item).find('.removeElement').css('display', 'none');
    }
  });
}

function checkHyperlinks() {
  multiInputInnovations.each((index, item) => {
    validateURL(item);
  });
}

function validateURL(item) {
  multiInputInnovations = $('.multiInput').find('span input');
  var url = item.value;
  var expression = /((([A-Za-z]{3,9}:(?:\/\/)?)(?:[-;:&=\+\$,\w]+@)?[A-Za-z0-9.-]+|(?:www.|[-;:&=\+\$,\w]+@)[A-Za-z0-9.-]+)((?:\/[\+~%\/.\w-_]*)?\??(?:[-\+=&;%@.\w_]*)#?(?:[\w]*))?)/i;
  var regex = new RegExp(expression);
  var res = "";

  if (url) {
    if (url.match(regex)) {
      for (let i = 0; i < multiInputInnovations.length; i++) {
        for (let j = 0; j < multiInputInnovations.length; j++) {
          // prevents the element from comparing with itself
          if (i !== j) {
            // check if elements' values are equal
            if (multiInputInnovations[i].value === multiInputInnovations[j].value) {
              // duplicate element present 
              res = "Invalid URL"; 
              // console.log(res);
              $(multiInputInnovations[i]).css('border', '1px solid red');
              $(multiInputInnovations[j]).css('border', '1px solid red');
            } else {
              if (url !== multiInputInnovations[i].value) {
                res = "Valid URL";
                // console.log(res);
                $(multiInputInnovations[i]).css('border', 'none');
                $(item).css('border', 'none');
              }
            }
          }
        }
      }
    } else {
      res = "Invalid URL";
      $(item).css('border', '1px solid red');
    }
  } else {
    res = "Empty URL";
    $(item).css('border', '1px solid red');
  }
}

function attachEvents() {
  if ($('#actualPhase').html() == 'true') {
    loadQualityAssessmentStatus(innovationAjaxURL, innovationArrName);
  }

  /**
   * Links Component
   */
  (function () {
    // Events
    $('.addButtonLink').on('click', addItem);
    $('.removeLink.links').on('click', removeItem);
    $('.multiInput').find('span input').on('input', function () {
      validateURL(this);
    });

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

function loadQualityAssessmentStatus(ajaxURL, arrName) {
  var currentCrpID = $('#actualCrpID').html();

  if (currentCrpID != '-1') {

    var finalAjaxURL = ajaxURL + currentCrpID;

    $.ajax({
      url: baseURL + finalAjaxURL,
      async: false,
      success: function (data) {
        if (data && Object.keys(data).length != 0) {
          var newData = data[arrName].map(function (x) {
            var arr = [];

            arr.push(x.id);
            arr.push(x.assessmentStatus);
            arr.push(x.updatedAt);

            return arr;
          });
          updateQualityAssessmentStatusData(newData);
        }
      }
    });
  }
}

function updateQualityAssessmentStatusData(data) {
  data.map(function (x) {
    if (x[0] == $('#innovationID').html()) {
      var container = document.getElementsByClassName('containerTitleElementsProject')[0];
      var element = document.getElementById('qualityAssessedIcon');
      var date, status, statusClass;

      switch (x[1]) {
        case 'pending':
          status = 'Pending assessment';
          statusClass = 'pending-mode';
          break;
        case 'pending_crp':
          status = 'Pending CRP response';
          statusClass = 'pending-mode';
          break;
        case 'in_progress':
          status = 'Quality Assessed (Requires 2nd assessment)';
          statusClass = 'qualityAssessed-mode';
          break;
        case 'quality_assessed':
          date = new Date((x[2].split('T')[0])).toDateString();
          status = 'Innovation was Quality Assessed on ' + date;
          statusClass = 'qualityAssessed-mode';
          break;

        default:
          break;
      }

      if (element) {
        var pTag = document.createElement('p');
        var text = document.createTextNode(status);

        element.innerHTML = '';
        element.classList.remove('pendingForReview-mode');
        element.classList.add(statusClass);
        pTag.style.margin = '0';
        pTag.appendChild(text);
        element.appendChild(pTag);

        if (x[1] == 'quality_assessed' || x[1] == 'pending') {
          var pMessageTag = document.createElement('p');
          if (x[1] == 'quality_assessed') {
            var textMessage = document.createTextNode('As this item has already been Quality Assessed, no changes are recommended');
          } else {
            var textMessage = document.createTextNode('As this item is being assessed by the SMO, no changes are recommended');
          }

          pMessageTag.classList.add('messageQAInfo');
          pMessageTag.appendChild(textMessage);
          container.appendChild(pMessageTag);
        }
      }
    }
  });
}

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

function stageValidations() {
  var isStageTwo = $('select[name="innovation.projectInnovationInfo.repIndStageInnovation.id"]').val() == 2;
  var isStageThree = $('select[name="innovation.projectInnovationInfo.repIndStageInnovation.id"]').val() == 3;
  var isStageFour = $('select[name="innovation.projectInnovationInfo.repIndStageInnovation.id"]').val() == 4;
  var evidenceLinkTag = $('label[for="innovation.projectInnovationInfo.evidenceLink"]').find('.requiredTag');
  var innovationGeneticNumber = $('input[name="innovation.projectInnovationInfo.innovationNumber"]');
  var oldInnovationGeneticNumber = $('#oldInnovationNumber').html();

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

  if (isStageThree || isStageFour) {
    innovationGeneticNumber.val("1");
    innovationGeneticNumber.prop('disabled', true);
  } else {
    innovationGeneticNumber.val(oldInnovationGeneticNumber);
    innovationGeneticNumber.prop('disabled', false);
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