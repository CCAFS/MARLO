$(document).ready(init);

var markers, inputMilestoneStatus;
var oicrsAjaxURL = '/qaAssessmentStatus.do?year=2021&indicatorTypeID=4&crpID=';
var oicrsArrName = 'fullItemsAssessmentStatus';

function init() {

  // Add Select2
  // $('form select').select2({
  //   width: '100%'
  // });
  if ($('#actualPhaseAR2021').html() == 'true') {
    $('textarea[name*="evidenceLink"]').prop('disabled', true);
  }
  inputMilestoneStatus = $('input.milestoneStatus');
  loadInputMilestoneStatus();
  inputMilestoneStatus.on('change', function () {
    var evidenceTag = $(this).parents('.synthesisMilestone').find('.milestoneEvidence').find('.requiredTag');
    var warningTag = $(this).parents('.synthesisMilestone').find('.linksToEvidence').find('#warningEmptyLinksTag');
    var tag = $(warningTag).next().find('.requiredTag');
    disableEnableWarningTag(this.value, evidenceTag, warningTag, tag);
  });
  attachEvents();

  // Set google charts
  setGoogleCharts();
  disabledUncheckedCheckmarkColor();
  markers = $('select.marker');
  loadMarkers();
  markers.on('change', function () {
    var tag = $(this).parent().parent().parent().next('.conditionalRequire').find('.requiredTag');
    disableEnableRequiredTag(this.value, tag);
  });
}

function loadInputMilestoneStatus() {
  inputMilestoneStatus.each((index, item) => {
    var evidenceTag = $(this).parents('.synthesisMilestone').find('.milestoneEvidence').find('.requiredTag');
    var warningTag = $(item).parents('.synthesisMilestone').find('.linksToEvidence').find('#warningEmptyLinksTag');
    var tag = $(warningTag).next().find('.requiredTag');
    if (item.checked) {
      disableEnableWarningTag(item.value, evidenceTag, warningTag, tag);
    }
  });
}

function disableEnableWarningTag(optionSelected, evidenceTag, warningTag, tag) {
  switch (optionSelected) {
    case '3':
      $(evidenceTag).hide();
      $(warningTag).show();
      $(tag).show();
      break;
    case '5':
      $(evidenceTag).show();
      $(warningTag).hide();
      $(tag).hide();
      break;
    case '6':
      $(evidenceTag).show();
      $(warningTag).show();
      $(tag).show();
      break;
    case '7':
      $(evidenceTag).show();
      $(warningTag).hide();
      $(tag).hide();
      break;

    default:
      break;
  }
}

function loadMarkers() {
  markers.each((index, item) => {
    var tag = $(item).parent().parent().parent().next('.conditionalRequire').find('.requiredTag');
    disableEnableRequiredTag(item.value, tag);
  });
}

function disableEnableRequiredTag(key, tag) {
  switch (key) {
    case '-1':
      $(tag).hide();
      break;

    case '1':
      $(tag).hide();
      break;

    case '2':
      $(tag).show();
      break;

    case '3':
      $(tag).show();
      break;

    case '4':
      $(tag).hide();
      break;

    default:
      break;
  }
}

function disabledUncheckedCheckmarkColor() {
  $('input[id^="disabled-"]').each((index, item) => {
    if ($(item).prop('checked') == false) {
      $(item).closest('.inputContainer').find('.checkmark').css('border', '2px solid #ff0000');
    }
  });
}

function attachEvents() {
  if ($('#actualPhase').html() == 'true') {
    loadQualityAssessmentStatus(oicrsAjaxURL, oicrsArrName);
  }

  // Links Component
  (function () {
    // Events
    $('.addButtonLink').on('click', addItem);
    $('.removeLink.links').on('click', removeItem);
    $('.multiInput').find('span input').on('input', validateURL);
    validateEmptyLinks();

    // Functions
    function addItem() {
      var $list = $(this).parent('.linksBlock').find('.linksList');
      var $element = $(this).parent('.linksBlock').parent().find('#multiInput-links-template').clone(true).removeAttr("id");
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
      var linksList = $(list).parent('.linksBlock').find('.linksList');
      linksList.find('.multiInput').each(function (i, element) {
        $(element).find('.indexTag').text(i + 1);
        $(element).setNameIndexes(3, i);
      });
      if (linksList.children().length != 0) {
        $('#warningEmptyLinksTag').hide();
        validateEmptyLinks();
      } else {
        $('#warningEmptyLinksTag').show();
      }
    }
    function validateURL() {
      var url = this.value;
      var expression = /((([A-Za-z]{3,9}:(?:\/\/)?)(?:[-;:&=\+\$,\w]+@)?[A-Za-z0-9.-]+|(?:www.|[-;:&=\+\$,\w]+@)[A-Za-z0-9.-]+)((?:\/[\+~%\/.\w-_]*)?\??(?:[-\+=&;%@.\w_]*)#?(?:[\w]*))?)/;
      var regex = new RegExp(expression);
      var res = "";
      if (url.match(regex)) {
        res = "Valid URL";
        $(this).css('border', 'none');
        $('#warningEmptyLinksTag').hide();
      } else {
        res = "Invalid URL";
        $(this).css('border', '1px solid red');
        $('#warningEmptyLinksTag').show();
      }
    }
    function validateEmptyLinks() {
      $('.linksList').find('.multiInput span input').map((index, item) => {
        if (item.value != '') {
          $('#warningEmptyLinksTag').hide();
        } else {
          $('#warningEmptyLinksTag').show();
        }
      });
    }

  })();

  // Change main reason
  $('select.milestoneMainReasonSelect').on('change', function () {
    var optionSelected = this.value;
    var $block = $(this).parents('.milestonesEvidence').find('.otherBlock');

    if (optionSelected == 7) {
      $block.slideDown();
    } else {
      $block.slideUp();
    }
  });

  inputMilestoneStatus.on('change', function () {
    var optionSelected = this.value;

    // Milestone Evidence
    var $block = $(this).parents('.synthesisMilestone').find('.milestonesEvidence');
    if (optionSelected == 4 || optionSelected == 5 || optionSelected == 6) {
      $block.slideDown();
    } else {
      $block.slideUp();
    }

    // Extended year
    var $yearBlock = $(this).parents('.synthesisMilestone').find('.extendedYearBlock');
    if (optionSelected == 4) {
      $yearBlock.slideDown();
    } else {
      $yearBlock.slideUp();
    }
  });
}

function loadQualityAssessmentStatus(ajaxURL, arrName) {
  var currentCrpID = $('#actualCrpID').html();

  if (currentCrpID != '-1') {
    var finalAjaxURL = ajaxURL + currentCrpID;

    $.ajax({
      url: baseURL + finalAjaxURL,
      async: false,
      success: function (data) {
        if (data && data.length != 0 && data.length != undefined) {
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
    var isCheckedAR = $(`#isCheckedAR-${x[0]}`).html();
    var element = document.getElementById(`QAStatusIcon-${x[0]}`);
    var status, iconSrc;

    switch (x[1]) {
      case 'pending':
        status = 'Pending';
        iconSrc = baseURL + '/global/images/pending-icon.svg';
        break;
      case 'in_progress':
        status = 'Quality Assessed (Requires 2nd assessment)';
        iconSrc = baseURL + '/global/images/quality-assessed-icon.svg';
        break;
      case 'quality_assessed':
        status = 'Quality Assessed';
        iconSrc = baseURL + '/global/images/quality-assessed-icon.svg';
        $(`#study-${x[0]}`).prop('disabled', true);
        $(`#study-${x[0]}`).next('span').attr('title', 'This item cannot be unchecked because it has been already Quality Assessed');
        break;

      default:
        break;
    }

    if (element && isCheckedAR == '1') {
      var imgTag = document.createElement('img');
      var br = document.createElement('br');
      var spanTag = document.createElement('span');
      var text = document.createTextNode(status);

      element.innerHTML = '';
      imgTag.style.width = '25px';
      imgTag.src = iconSrc;
      element.appendChild(imgTag);
      element.appendChild(br);
      spanTag.appendChild(text);
      element.appendChild(spanTag);
    }
  });
}

function setGoogleCharts() {
  // Chart #14  - OICRs Level of maturity
  createGoogleBarChart('#chart14', {
    title: 'OICRs Level of Maturity',
    titleTextStyle: {
      color: '#5f5e5e',
      fontName: 'Roboto',
      fontSize: 16,
      bold: false
    },
    orientation: 'horizontal',
    hAxis: {
      baseline: 'none',
      textPosition: 'none',
      gridlines: {
        count: 0
      }
    },
    vAxis: {
      baseline: 'none',
      textPosition: 'none',
      gridlines: {
        count: 0
      }
    },
    //pieHole: 0.4,
    chartArea: {
      top: 45,
      width: "80%",
      heigth: "100%"
    },
    colors: [
      '#1773b8', '#e43a74', '#00a0b0', '#f3bd1e', '#373a3b'
    ],
    bar: { groupWidth: '100%' },
    legend: {
      position: "bottom",
      //alignment: 'center',
    },
  });

  // Chart #15 - Policies by Type
  createGoogleBarChart("#chart15", {
    title: "OICRs by Flagship/Module",
    titleTextStyle: {
      color: '#5f5e5e',
      fontName: 'Roboto',
      fontSize: 16,
      bold: false
    },
    chartArea: {
      top: 65,
      left: 55,
      width: '80%',
      heigth: "100%"
    },
    hAxis: {
      baseline: 'none',
      //viewWindowMode: 'pretty',
      //slantedText: true,
      textPosition: 'none',
      gridlines: {
        count: 0
      },
      title: '*Note: Please note that an OICR can contribute to more than one Flagship/Module.'
    },
    vAxis: {
      textStyle: {
        color: '#5f5e5e',
        fontName: 'Roboto'
      }
    },
    legend: {
      position: "none"
    },
    bars: 'horizontal' // Required for Material Bar Charts.
  });
}