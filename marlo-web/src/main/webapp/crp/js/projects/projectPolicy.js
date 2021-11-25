var caseStudiesName;
var $elementsBlock;
var policyAjaxURL = '/qaAssessmentStatus.do?year=2021&indicatorTypeID=2&crpID=';
var policyArrName = 'fullItemsAssessmentStatus';

$(document).ready(init);

function init() {

  // Add Events
  attachEvents();

  // Add select2
  addSelect2();

  // Add Geographic Scope
  $('select.elementType-repIndGeographicScope ').on("addElement removeElement", function (event, id, name) {
    setGeographicScope(this);
  });
  setGeographicScope($('form select.elementType-repIndGeographicScope')[0]);

  // This function enables launch the pop up window
  popups();

  // Add amount format
  $('input.currencyInput').currencyInput();

  $('.ccRelevanceBlock input:radio').on('change', function () {
    var $commentBox = $(this).parents('.ccRelevanceBlock').find('.ccCommentBox');
    if (this.value != 3) {
      $commentBox.slideDown();
    } else {
      $commentBox.slideUp();
    }
  });

  $('.policyContributingCRP select').on('change', checkContributingCRP);
  checkContributingCRP();
}

function checkContributingCRP() {
  var actualContributingCRP = $('.policyContributingCRP').find('.list .relationElement');

  actualContributingCRP.each((index, item) => {
    actualContributingCRPSpan = $(item).find('span.elementName').html();
    var crpName = actualContributingCRPSpan.substr(0, actualContributingCRPSpan.indexOf(' '));
    var actualCRP = $('#actualCRP').html();

    if (crpName == actualCRP) {
      $(item).find('.removeElement').css('display', 'none');
    }
  });
}

function attachEvents() {
  if ($('#actualPhase').html() == 'true') {
    loadQualityAssessmentStatus(policyAjaxURL, policyArrName);
  }

  $('select.elementType-repIndPolicyType').on("addElement removeElement", function (event, id, name) {
    // Other Please Specify
    if (id == 4) {
      if (event.type == "addElement") {
        $('.block-pleaseSpecify').slideDown();
      }
      if (event.type == "removeElement") {
        $('.block-pleaseSpecify').slideUp();
      }
    }
  });

  // On change policyInvestimentTypes
  $('select.policyInvestimentTypes').on('change', function () {
    if (this.value == 3) {
      $('.block-budgetInvestment').slideDown();
    } else {
      $('.block-budgetInvestment').slideUp();
    }
  });

  $('select.maturityLevel').on('change', function () {
    var id = this.value;
    if ((id == 4) || (id == 5)) {
      $('.evidences-block .requiredTag').slideDown();
    } else {
      $('.evidences-block .requiredTag').slideUp();
    }
    if (id == 3) {
      $('.block-researchMaturity').slideDown();
    }
    else {
      $('.block-researchMaturity').slideUp();
    }
  });

  //On change radio buttons
  $('input[class*="radioType-"]').on('change', onChangeRadioButton);

  //View sub-IDOs popup
  $(".selectSubIDO").on("click", function () {
    currentSubIdo = $(this).parents(".subIdo");
    $("#subIDOs-graphic").dialog({
      autoOpen: false,
      resizable: false,
      closeText: "",
      width: '85%',
      modal: true,
      height: $(window).height() * 0.90,
      show: {
        effect: "blind",
        duration: 500
      },
      hide: {
        effect: "fadeOut",
        duration: 500
      }
    });
    $("#subIDOs-graphic").dialog("open");
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
    if (x[0] == $('#policyID').html()) {
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
          status = 'Policy was Quality Assessed on ' + date;
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

        if (x[1] == 'quality_assessed') {
          var pMessageTag = document.createElement('p');
          var textMessage = document.createTextNode('As this item has already been Quality Assessed, no changes are recommended');

          pMessageTag.classList.add('messageQAInfo');
          pMessageTag.appendChild(textMessage);
          container.appendChild(pMessageTag);
        }
      }
    }
  });
}

function addSelect2() {
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