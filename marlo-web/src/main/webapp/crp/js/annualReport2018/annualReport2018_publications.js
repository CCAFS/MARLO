$(document).ready(init);

var publicationsAjaxURL = '/qaAssessmentStatus.do?year=2021&indicatorTypeID=3&crpID=';
var publicationsArrName = 'fullItemsAssessmentStatus';

function init() {
  // Attaching events
  attachEvents();

  // Init Google Charts
  setGoogleCharts();

}

function attachEvents() {
  if ($('#actualPhase').html() == 'true' && $('#isSubmitted').html() == 'true') {
    loadQualityAssessmentStatus(publicationsAjaxURL, publicationsArrName);
  }
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
    var isCheckedAR = $(`#isCheckedAR-${x[0]}`).html();
    var element = document.getElementById(`QAStatusIcon-${x[0]}`);
    var status, iconSrc;

    switch (x[1]) {
      case 'pending':
        status = 'Pending assessment';
        iconSrc = baseURL + '/global/images/pending-icon.svg';
        break;
      case 'pending_crp':
        status = 'Pending CRP response';
        iconSrc = baseURL + '/global/images/pending-icon.svg';
        break;
      case 'in_progress':
        status = 'Quality Assessed (Requires 2nd assessment)';
        iconSrc = baseURL + '/global/images/quality-assessed-icon.svg';
        break;
      case 'quality_assessed':
        status = 'Quality Assessed';
        iconSrc = baseURL + '/global/images/quality-assessed-icon.svg';
        $(`#deliverable-${x[0]}`).prop('disabled', true);
        $(`#deliverable-${x[0]}`).next('span').attr('title', 'This item cannot be unchecked because it has been already Quality Assessed');
        break;
      case 'automatically_validated':
        status = 'Automatically Validated';
        iconSrc = baseURL + '/global/images/autochecked-icon.svg';
        $(`#deliverable-${x[0]}`).prop('disabled', true);
        $(`#deliverable-${x[0]}`).next('span').attr('title', 'This item cannot be unchecked because it has been already Automatically Validated');
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

  // Chart #10 - Number of peer reviewed articles by Open Access status
  createGooglePieChart('#chart10', {
    title: 'Number of peer reviewed articles by Open Access status',
    titleTextStyle: {
      color: '#5f5e5e',
      fontName: 'Roboto',
      fontSize: 16,
      bold: false
    },
    pieHole: 0.4,
    chartArea: {
      top: 70,
      width: '100%'
    },
    colors: '#e67e22',
    legend: {
      alignment: 'center'
    },
    slices: {
      0: {
        color: '#f68212'
      },
      1: {
        color: '#999fa3'
      }
    }
  });

  // Chart #11 - Number of peer reviewed articles by ISI status
  createGooglePieChart('#chart11', {
    title: 'Number of peer reviewed articles by ISI status',
    titleTextStyle: {
      color: '#5f5e5e',
      fontName: 'Roboto',
      fontSize: 16,
      bold: false,
      alignment: 'center'
    },
    pieHole: 0.4,
    chartArea: {
      top: 70,
      width: '100%'
    },
    colors: '#e67e22',
    legend: {
      alignment: 'center'
    },
    slices: {
      0: {
        color: '#16a085'
      },
      1: {
        color: '#999fa3'
      }
    }
  });

};