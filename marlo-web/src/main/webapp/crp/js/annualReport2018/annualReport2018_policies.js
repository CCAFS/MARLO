$(document).ready(init);

var policiesAjaxURL = '/qaAssessmentStatus.do?year=2021&indicatorTypeID=2&crpID=';
var policiesArrName = 'fullItemsAssessmentStatus';

function init() {
  // Attaching events
  attachEvents();

  // Set google charts
  setGoogleCharts();
}

function attachEvents() {
  if ($('#actualPhase').html() == 'true') {
    loadQualityAssessmentStatus(policiesAjaxURL, policiesArrName);
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
        $(`#policy-${x[0]}`).prop('disabled', true);
        $(`#policy-${x[0]}`).next('span').attr('title', 'This item cannot be unchecked because it has been already Quality Assessed');
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
  // Chart #7 - Policies Stage of Maturity
  createGoogleBarChart('#chart7', {
      title: 'Policies Stage of Maturity',
      titleTextStyle: {
          color: '#5f5e5e',
          fontName: 'Roboto',
          fontSize: 16,
          bold: false
      },
      orientation: 'horizontal',
      hAxis: {
        baseline:'none',
        textPosition: 'none',
        gridlines: {
          count: 0
        }
      },
      vAxis: {
        //baseline:'none',
        baseline:'none',
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
      bar: {groupWidth: '100%'},
      legend: {
        position: "bottom",
        //alignment: 'center',
      },
  });

  // Chart #6 - Policies by Type
  createGoogleBarChart("#chart6", {
      title: "Policies by Type",
      titleTextStyle: {
          color: '#5f5e5e',
          fontName: 'Roboto',
          fontSize: 16,
          bold: false
      },
      chartArea: {
          top: 65,
          right: 0,
          width: '70%',
          heigth: "100%"
      },
      hAxis: {
        baseline:'none',
        //viewWindowMode: 'pretty',
        //slantedText: true,
        textPosition: 'none',
        gridlines: {
          count: 0
        }
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

};