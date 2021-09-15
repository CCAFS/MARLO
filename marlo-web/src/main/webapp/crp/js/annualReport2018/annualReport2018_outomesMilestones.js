$(document).ready(init);

var markers, inputMilestoneStatus;

function init() {

  // Add Select2
  // $('form select').select2({
  //   width: '100%'
  // });
  
  inputMilestoneStatus = $('input.milestoneStatus');
  loadInputMilestoneStatus();
  inputMilestoneStatus.on('change', function() {
    var tag = $(this).parents('.synthesisMilestone').find('.linksToEvidence').find('#warningEmptyLinksTag');
    disableEnableWarningTag(this.value, tag);
  });
  attachEvents();

  // Set google charts
  setGoogleCharts();
  disabledUncheckedCheckmarkColor();
  markers = $('select.marker');
  loadMarkers();
  markers.on('change', function() {
    var tag = $(this).parent().parent().parent().next('.conditionalRequire').find('.requiredTag');
    disableEnableRequiredTag(this.value, tag);
  });
}

function loadInputMilestoneStatus() {
  inputMilestoneStatus.each((index, item) => {
    var tag = $(item).parents('.synthesisMilestone').find('.linksToEvidence').find('#warningEmptyLinksTag');
    if (item.checked) {
      disableEnableWarningTag(item.value, tag);
    }
  });
}

function disableEnableWarningTag(optionSelected, tag) {
  if (optionSelected == '6') {
    $(tag).show();
  } else {
    $(tag).hide();
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

  // Change main reason
  $('select.milestoneMainReasonSelect').on('change', function() {
    var optionSelected = this.value;
    var $block = $(this).parents('.milestonesEvidence').find('.otherBlock');

    if(optionSelected == 7) {
      $block.slideDown();
    } else {
      $block.slideUp();
    }
  });

  inputMilestoneStatus.on('change', function() {
    var optionSelected = this.value;

    // Milestone Evidence
    var $block = $(this).parents('.synthesisMilestone').find('.milestonesEvidence');
    if(optionSelected == 4 || optionSelected == 5 || optionSelected == 6) {
      $block.slideDown();
    } else {
      $block.slideUp();
    }

    // Extended year
    var $yearBlock = $(this).parents('.synthesisMilestone').find('.extendedYearBlock');
    if(optionSelected == 4) {
      $yearBlock.slideDown();
    } else {
      $yearBlock.slideUp();
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
        baseline:'none',
        textPosition: 'none',
        gridlines: {
          count: 0
        }
      },
      vAxis: {
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
        baseline:'none',
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