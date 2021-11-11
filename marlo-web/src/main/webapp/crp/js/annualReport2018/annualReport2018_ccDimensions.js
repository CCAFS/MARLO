var capdevTabLoaded;
var capDevAjaxURL = '/qaAssessmentStatus.do?year=2021&indicatorTypeID=6&crpID=';
var capDevArrName = 'fullItemsAssessmentStatus';
var container;

$(document).ready(function() {
  capdevTabLoaded = ($('input[name="indexTab"]').val() == 3);

  $('a[data-toggle="tab"]').on('shown.bs.tab', function(e) {
    if(!capdevTabLoaded && e.target.hash == "#tab-capdev") {
      loadCapdev();
    }
  });

  if(capdevTabLoaded) {
    loadCapdev();
  }

  attachEvents();
});

function attachEvents() {
  container = document.getElementsByClassName('containerTitleStatusMessage')[0];
  if ($('#actualPhase').html() == 'true' && $('#isSubmitted').html() == 'true') {
    loadQualityAssessmentStatus(capDevAjaxURL, capDevArrName);
  }
  
  $('#qaStatus-button').on('click', function(){
    updateQAStatus($(this));
  });
}

function updateQAStatus(element){
  let $stat = $('input.onoffswitch-radio');

  if($stat.val() == 'true'){
    element.removeClass('includeARButton');
    element.addClass('removeARButton');
    element.html('Remove from QA');
    $stat.val('false');
    container.style.width = '78.6%';
  } else {
    element.removeClass('removeARButton');
    element.addClass('includeARButton');
    element.html('Include in QA');
    $stat.val('true');
    container.style.width = '81.6%';
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
    var isCheckedAR = $('#isCheckedAR').html();
    var element = document.getElementById('containerQAStatus');
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
        status = 'Capacity Development was Quality Assessed on ' + date;
        statusClass = 'qualityAssessed-mode';
        break;

      default:
        break;
    }

    if (element && isCheckedAR == 'true') {
      var pTag = document.createElement('p');
      var text = document.createTextNode(status);
      
      
      element.innerHTML = '';
      element.classList.remove('pendingForReview-mode');
      element.classList.add(statusClass);
      pTag.appendChild(text);
      element.appendChild(pTag);
      
      if (x[1] == 'quality_assessed') {
        var containerElements = document.getElementsByClassName('containerTitleElements')[0];
        
        var removeARBtn = document.getElementsByClassName('removeARButton')[0];
        var pMessageTag = document.createElement('p');
        var textMessage = document.createTextNode('As this item has already been Quality Assessed, no changes are recommended');

        containerElements.style.marginBottom = '0';
        containerElements.style.justifyContent = 'center';
        container.style.marginLeft = '0';
        removeARBtn.style.display = 'none';
        element.style.backgroundPosition = '555px';
        pMessageTag.classList.add('messageQAInfo');
        pMessageTag.appendChild(textMessage);
        container.appendChild(pMessageTag);
      } 
    }
  });
}

function loadCapdev() {
  // Redraw table
  // tableDatatableViewmore.draw();
  // Numeric fields
  $('.numericInput').numericInput();
  // Set charts
  setGoogleCharts();

  capdevTabLoaded = true;
}

function setGoogleCharts() {
  // Chart #12 - Trainees in short-term
  createGoogleBarChart('#chart12', {
      title: 'Trainees in short-term',
      titleTextStyle: {
          color: '#5f5e5e',
          fontName: 'Roboto',
          fontSize: 16,
          bold: false
      },
      //pieHole: 0.4,
      /*
      chartArea: {
          top: 70,
          width: '100%'
      },
      */
      hAxis: {
        baseline:'none',
        textPosition: 'none',
        gridlines: {
          count: 0
        }
      },
      vAxis: {
        //baseline:'none',
        gridlines: {
          count: 0
        }
      },
      //colors: '#e67e22',
      series: {
        0:{color:'#F3BD29'},
        1:{color:'#F3BD27'}
      },
      legend: { position: 'bottom',
        alignment: 'center',
        maxLines: 3
      },
      isStacked: true
      /*
      slices: {
          0: {
            color: '#22a6b3'
          },
          1: {
            color: '#7ed6df'
          }
      },*/

  });

  // Chart #13 - Trainees in long-term
  createGoogleBarChart('#chart13', {
      title: 'Trainees in long-term',
      titleTextStyle: {
          color: '#5f5e5e',
          fontName: 'Roboto',
          fontSize: 16,
          bold: false
      },
      //pieHole: 0.4,
      /*
      chartArea: {
          top: 70,
          width: 400
      },*/
      //colors: '#7ed6df',

      hAxis: {
        baseline:'none',
        textPosition: 'none',
        gridlines: {
          count: 0
        }
      },
      vAxis: {

        gridlines: {
          count: 0
        }
      },
      series: {
        0:{
          color:'#00A0B0',
          annotations: {
            stem: {
              length: 5
            },
          }
        },
        1:{
          color:'#AB2E56',
          annotations: {
            stem: {
              length: 22
            },
          }
        }
      },
      legend: {
        position: 'bottom',
        alignment: 'center',
        maxLines: 3
      },
      isStacked: true
      /*
      slices: {
          0: {
            color: '#22a6b3'
          },
          1: {
            color: '#7ed6df'
          }
      }*/
  });

}