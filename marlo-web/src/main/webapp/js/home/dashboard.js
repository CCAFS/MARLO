//$.fn.dataTableExt.sErrMode = 'throw';
$(document).ready(initDashboard);

function initDashboard() {
  // Set timeline dates completion
  //setCompletionDates();

  $('#newProject').on('click', function(e) {
    $('#decisionTree .addProjectButtons').show(0, function() {
      $(this).addClass('animated flipInX');
    });
  });

  $('.loadingBlock').hide().next().fadeIn(500);

  // Initialize tabs
  //initTabs();

  // Initialize datatable of projects
  //initDatatable();

}

function setCompletionDates() {
  var today = new Date();
  $('#timeline li.li').each(function(i,element) {
    var timelineDate = new Date($(element).find('.dateText').text());
    timelineDate.setTime(timelineDate.getTime() + (timelineDate.getTimezoneOffset() / 60) * 3600000);
    $(element).find('.date').text(timelineDate.toDateString()).addClass('animated flipInX');
    var isOpen = $(element).find('.isOpen').text() === "true";
    if(!isOpen) {
      timelineDate.setTime(timelineDate.getTime() + (24 * 3600000));
    }
    if(today >= timelineDate) {
      $(element).addClass('complete');
    }
  });
}

function workflowModal() {
  $("#showPandRWorkflowDialog").dialog({
      modal: true,
      width: 700,
      height: 770,
      buttons: {
        Ok: function() {
          $(this).dialog("close");
        }
      }
  });
  return false;
}

var graphStarted = false;
function initTabs() {
  $("#dashboard-tabs").tabs({
    activate: function(event,ui) {
      if(ui.newTab.index() == 1) {
        if(!graphStarted) {
          callCytos(baseURL + "/json/prePlanningIpGraph.do", "ipGraph-content");
          graphStarted = true;
        }
      }
    }
  });
}

function initDatatable() {
  $('#projects-table').dataTable({
      "aLengthMenu": [
          [
              5, 10
          ], [
              5, 10
          ]
      ],
      "iDisplayLength": 5
  });
  $("#activities-table").dataTable({
      "aLengthMenu": [
          [
              5, 10
          ], [
              5, 10
          ]
      ],
      "iDisplayLength": 5
  });

  $("#deadlineDates table").dataTable();
}

function initSlidr() {
  slidr.create('slider', {
      breadcrumbs: true,
      keyboard: true,
      overflow: true,
      pause: false,
      theme: '#444',
      touch: true
  }).start();
}
