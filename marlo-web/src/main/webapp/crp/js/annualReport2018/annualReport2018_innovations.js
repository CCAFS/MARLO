$(document).ready(init);

var innovationsAjaxURL = '/qaAssessmentStatus.do?year=2021&indicatorTypeID=1&crpID=';
var innovationsArrName = 'fullItemsAssessmentStatus';

function init() {
  // Attaching events
  attachEvents();

  // Load Google Charts
  setGoogleCharts();
}

function attachEvents() {
  if ($('#actualPhase').html() == 'true') {
    loadQualityAssessmentStatus(innovationsAjaxURL, innovationsArrName);
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
        $(`#innovation-${x[0]}`).prop('disabled', true);
        $(`#innovation-${x[0]}`).next('span').attr('title', 'This item cannot be unchecked because it has been already Quality Assessed');
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

  // Chart #8 - Innovations by type
  createGooglePieChart('#chart8', {
    title: 'Innovations by type',
    titleTextStyle: {
      color: '#5f5e5e',
      fontName: 'Roboto',
      fontSize: 16,
      bold: false
    },
    pieHole: 0.3,
    chartArea: {
      right: 0,
      bottom: 0,
      width: '80%',
      height: '85%'
    },
    legend: {
      alignment: 'center',
      // position: 'bottom'
    }
  });

  // Chart #9 - Innovations by stage
  createGoogleBarChart('#chart9', {
    title: "Innovations by stage",
    titleTextStyle: {
      color: '#5f5e5e',
      fontName: 'Roboto',
      fontSize: 16,
      bold: false
    },
    chartArea: {
      right: 0,
      bottom: 0,
      width: '80%',
      height: '90%'
    },
    hAxis: {
      baseline: 'none',
      textPosition: 'none',
      gridlines: {
        count: 0
      }
    },
    legend: {
      position: "none"
    },
    vAxis: {
      textStyle: {
        color: '#8c8c8c',
        fontName: 'Roboto'
      }
    },
    // colors: '#27ae60',
    bars: 'horizontal' // Required for Material Bar Charts.
  });

}