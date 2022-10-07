// $.fn.dataTableExt.sErrMode = 'throw';
$(document).ready(initDashboard);

let timelineElements;

// console.log(baseURL)


function initDashboard() {


  $('#newProject').on('click', function(e) {
    $('#decisionTree .addProjectButtons').show(0, function() {
      $(this).addClass('animated flipInX');
    });
  });

  $('.loadingBlock').hide().next().fadeIn(500);

  getTimeline();
  createTimeline();

  $('.buttonRightTimeline').on("click", moveScrollRight);

  $('.buttonLeftTimeline').on("click", moveScrollLeft);

  
}

function moveScrollRight() {
  const element = document.querySelector(".scroll-x-containerTimeline");
  element.scrollLeft += 200;
}

function moveScrollLeft() {
  const element = document.querySelector(".scroll-x-containerTimeline");
  element.scrollLeft -= 200;
}

function createTimeline() {
  var counter = 0;
  var counterActvi = 0;
  var previusDate;
  var linePorcent;

  // iterate timeline elements
  timelineElements.forEach(function(data,index){
    var listItemTimeline=document.getElementById("listItemTimeline");
    var newDiv= document.createElement("div")    
    newDiv.className='infTimelineTimeline';
    listItemTimeline.appendChild(newDiv);
    var newDivTitle= document.createElement("span")    
    newDivTitle.className='titleTimeline';
    newDiv.appendChild(newDivTitle)
    var newDivPoint= document.createElement("div") 
    newDivPoint.className='timeline-pointTimeline';
    newDiv.appendChild(newDivPoint)
    var newPorcentTimeLine= document.createElement("div") 
    newDiv.appendChild(newPorcentTimeLine)
    var newDivTimeLine= document.createElement("div") 
    newDivTimeLine.className='timeline-line';
    newDiv.appendChild(newDivTimeLine)
    var newPTimeLine= document.createElement("p") 
    newPTimeLine.className='dateTimeline';
    newDiv.appendChild(newPTimeLine)

    var description = document.createTextNode(data.description);
    var dateMonthStart = new Date(data.startDate).toLocaleString("en-US", { month: "short" });
    var dateDayStart = new Date(data.startDate).getDate()+1;
    var dateMonthEnd =new Date(data.endDate).toLocaleString("en-US", { month: "short" });
    var dateDayEnd = new Date(data.endDate).getDate()+1;
    var date =document.createTextNode(dateMonthStart+' '+ dateDayStart+' - '+dateMonthEnd+' '+ dateDayEnd)
    newDivTitle.appendChild(description);
    newPTimeLine.appendChild(date);
    var endDate = new Date(data.endDate);
    endDate.setDate(endDate.getDate() + 1)
    
    // Define the color of elements
    if(endDate < new Date()){

      var newImgTimeLine= document.createElement("img");
      newImgTimeLine.className='imgTimeline';
      newImgTimeLine.setAttribute("src",baseURL +"/global/images/icon-check-tiny-white.png")
      newDivPoint.appendChild(newImgTimeLine);
      newDivTitle.classList.add('timelineColorSuccess');
      newDivPoint.classList.add('timelineBackSuccess');
      newDivTimeLine.classList.add('timelineBackSuccess');
      previusDate=data.endDate;
      counterActvi =counterActvi+1      

    }
    // Define the color and percentage of the bar
    if(counter == 0 && (endDate > new Date())){

      let dateDiff = endDate.getTime() - new Date(data.startDate).getTime();
      let daysFinalizeActivity = ((endDate.getTime() - new Date().getTime())/(1000*60*60*24));
      newPorcentTimeLine.className='porcentTimeLine';
      newDivTitle.classList.add('timelineColorAlert');
      newDivPoint.classList.add('timelineBackAlert');

      linePorcent = -(daysFinalizeActivity*100)/(dateDiff/(1000*60*60*24))+100;
      newDivTimeLine.style["margin-top"] = "0";
      newPorcentTimeLine.style["width"] = Math.round(linePorcent)+'%';
      if(linePorcent < 0) newPorcentTimeLine.style["width"] = Math.round(0)+'%';
      newPorcentTimeLine.appendChild(newDivTimeLine);      
      $('.timelineAlertText').text(Math.round(daysFinalizeActivity+1)+' days left until the end of the activity');
      counter = 1;
      // console.log(linePorcent)
    }
  })
  // Locate pending activity
  const element = document.querySelector(".scroll-x-containerTimeline");
  element.scrollLeft += 243*(counterActvi-2);
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
      closeText: "",
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

function timeline() {
  var today = new Date();
  var dd = today.getDate();
  var mm = today.getMonth();
  var yyyy = today.getFullYear();

  if(dd < 10) {
    dd = '0' + dd
  }
  if(mm < 10) {
    mm = '0' + mm
  }

  today = new Date(yyyy, mm, dd);
  // today = new Date("12/11/2016");

  var timelineStart = 1;
  var current = [];
  var state = 0;

  $(".infoActions").each(function(i,e) {
    var startDate = new Date($(e).find(".startDate").html());
    if($(e).find(".endDate").html().length == 0) {
      var endDate = startDate;
    } else {
      var endDate = new Date($(e).find(".endDate").html());
    }

    if(today >= startDate && today <= endDate) {
      current[i] = 1;
      state = 1;
    } else {
      current[i] = startDate + "/" + endDate;
    }
  });

  if(state != 1) {
    for(var i = 0; i < current.length; i++) {
      var resta = new Date(current[i].split("/")[1]) - today;
      var dias = resta / (1000 * 60 * 60 * 24);
      if(dias < 0) {
        current[i] = 0;
      } else {
        current[i] = 1;
        break;
      }
    }
  }
  timelineStart = current.indexOf(1) + 1;

  $().timelinr({
      orientation: 'horizontal',
      // value: horizontal | vertical, default to horizontal
      containerDiv: '#timeline',
      // value: any HTML tag or #id, default to #timeline
      datesDiv: '#dates',
      // value: any HTML tag or #id, default to #dates
      datesSelectedClass: 'selected',
      // value: any class, default to selected
      datesSpeed: 'normal',
      // value: integer between 100 and 1000 (recommended) or 'slow', 'normal' or 'fast'; default to normal
      issuesDiv: '#issues',
      // value: any HTML tag or #id, default to #issues
      issuesSelectedClass: 'selected',
      // value: any class, default to selected
      issuesSpeed: 'fast',
      // value: integer between 100 and 1000 (recommended) or 'slow', 'normal' or 'fast'; default to fast
      issuesTransparency: 0.1,
      // value: integer between 0 and 1 (recommended), default to 0.2
      issuesTransparencySpeed: 500,
      // value: integer between 100 and 1000 (recommended), default to 500 (normal)
      prevButton: '.leftControl',
      // value: any HTML tag or #id, default to #prev
      nextButton: '.rigthControl',
      // value: any HTML tag or #id, default to #next
      arrowKeys: 'false',
      // value: true/false, default to false
      startAt: timelineStart,
      // value: integer, default to 1 (first)
      autoPlay: 'false',
      // value: true | false, default to false
      autoPlayDirection: 'forward',
      // value: forward | backward, default to forward
      autoPlayPause: 2000
  });
}

$('table.projectsList').dataTable({
    "bPaginate": true, // This option enable the table pagination
    "bLengthChange": true, // This option disables the select table size option
    "bFilter": true, // This option enable the search
    "bSort": true, // this option enable the sort of contents by columns
    "bAutoWidth": false, // This option enables the auto adjust columns width
    "iDisplayLength": 5, // Number of rows to show on the table
    "pagingType": "simple",
    "fnDrawCallback": function() {
      // This function locates the add activity button at left to the filter box
      var table = $(this).parent().find("table");
      if($(table).attr("id") == "currentActivities") {
        $("#currentActivities_filter").prepend($("#addActivity"));
      }
    },
    aoColumnDefs: [
        {
            bSortable: false,
            aTargets: [

            ]
        }, {
            sType: "natural",
            aTargets: [
              0
            ]
        }
    ]
});

$('a#impact[data-toggle="tab"]').on('shown.bs.tab', function(e) {
  e.target // newly activated tab
  e.relatedTarget // previous active tab
  var url = baseURL + "/impactPathway/impactPathwayFullGraph.do";
  var data = {
    crpID: currentCrpID
  }
  ajaxService(url, data, "impactGraphic", true, true, 'concentric', true);
})

// Impact pathway full screen

$("#fullscreen").on("click", function() {
  $("#impactGraphic-content").dialog({
      resizable: false,
      closeText: "",
      width: '90%',
      modal: true,
      height: $(window).height() * 0.80,
      show: {
          effect: "blind",
          duration: 500
      },
      hide: {
          effect: "fadeOut",
          duration: 500
      },
      open: function(event,ui) {
        var dataFull = {
          crpID: currentCrpID
        }
        var url = baseURL + "/impactPathway/impactPathwayFullGraph.do";
        ajaxService(url, dataFull, "impactGraphic-fullscreen", true, true, 'breadthfirst', false);
      }
  });

});

function getTimeline() {
  var finalAjaxURL = `/getTimelineInformation.do`;

  $.ajax({
    url: baseURL + finalAjaxURL,
    async: false,
    success: function (data) {
      if (data && Object.keys(data).length != 0) {
        timelineElements = data['information'];
        // console.log(data['information'])
      }
    }
  });
}
