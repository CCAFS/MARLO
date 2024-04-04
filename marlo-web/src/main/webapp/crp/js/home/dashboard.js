// $.fn.dataTableExt.sErrMode = 'throw';
$(document).ready(initDashboard);

let timelineElements;



function initDashboard() {


  $('#newProject').on('click', function(e) {
    $('#decisionTree .addProjectButtons').show(0, function() {
      $(this).addClass('animated flipInX');
    });
  });

  $('.loadingBlock').hide().next().fadeIn(500);

  getTimeline();
  createTimeline2();
  $(".timelineRefresh").hide();
  $(".timeline").show();
  setTimelinePosition();

  $('.buttonRightTimeline').on("click", moveScrollRight);

  $('.buttonLeftTimeline').on("click", moveScrollLeft);

  $('.itemsTablet').on("click", updateTable);

  $('.itemsTablet').hover(updateGif, updateImg);
  
  $('.circleMap').hover(itemMapHover, itemMap);
}

function itemMapHover(){
  let item = $(this).attr('id').split('cluster')[1];
  locateContentDialog(item);
  $('#cluster'+item).css("-webkit-box-shadow", " 0px 0px 10px rgb(0 0 0 / 100%)")
  $('#cluster'+item).css("background-color", " white")
  $('.dialogMap').css("display", "block")
  $('.dialogMap').addClass('animate__animated animate__backInRight')
  $('.dialogMapText').text(contentDialog(item));
}

function itemMap(){
  let item = $(this).attr('id').split('cluster')[1];
  $('#cluster'+item).css("-webkit-box-shadow", " 0px 0px 10px rgb(0 0 0 / 0%)")  
  $('#cluster'+item).css("background-color", " #b3b3b3")  
  $('.dialogMap').css("display", "none")
}

function contentDialog(id){

  switch (id) {
    case '1':
      return "Senegal: Activities led by ILRI"
    case '2':
      return "Ethiopia: Activities led by ILRI"
    case '3':
      return "Ghana: Activities led by IITA"
    case '4':
      return "Kenya: Activities led by ILRI"
    case '5':
      return "Zambia: Activities led by IWMI"
    case '6':
      return "Theme 1: Activities led by ILRI"
    case '7':
      return "Theme 2: Activities led by the Alliance"
    case '8':
      return "West Africa"
    case '9':
      return "Theme 4: Activities led by Columbia University"
    case '10':
      return "Theme 3: Gender and Social Inclusion Leader (Lead by ILRI)"
    case '11':
      return "East and Southern Africa"
    case '12':
      return "Mali: Activities led by AfricaRice"
  }
  
}


function updateImg() {
  let name = $(this).attr("id");
  $('.itemimg'+name).show();
  $('.itemgif'+name).hide();
}

function updateGif() {
  let name = $(this).attr("id");
  $('.itemimg'+name).hide();
  $('.itemgif'+name).show();
}

function locateContentDialog(id){

  switch (id) {
    case '1':
      $('.dialogMap').css("top", "119px")
      $('.dialogMap').css("left", "13px")
      break;
    case '2':
      $('.dialogMap').css("top", "131px")
      $('.dialogMap').css("left", "161px")      
      break;
    case '3':
      $('.dialogMap').css("top", "145px")
      $('.dialogMap').css("left", "49px")      
      break;
    case '4':
      $('.dialogMap').css("top", "165px")
      $('.dialogMap').css("left", "165px")      
      break;
    case '5':
      $('.dialogMap').css("top", "217px")
      $('.dialogMap').css("left", "127px")      
      break;
    case '6':
      $('.dialogMap').css("top", "46px")
      $('.dialogMap').css("left", "94px")     
      break;
    case '7':
      $('.dialogMap').css("top", "75px")
      $('.dialogMap').css("left", "11px")     
      break;
    case '8':
      $('.dialogMap').css("top", "132px")
      $('.dialogMap').css("left", "0px")      
      break;
    case '9':
      $('.dialogMap').css("top", "195px")
      $('.dialogMap').css("left", "76px")      
      break;
    case '10':
      $('.dialogMap').css("top", "218px")
      $('.dialogMap').css("left", "171px")      
      break;
    case '11':
      $('.dialogMap').css("top", "103px")
      $('.dialogMap').css("left", "169px")     
      break;
    case '12':
      $('.dialogMap').css("top", "106px")
      $('.dialogMap').css("left", "49px")     
      break;
  }

  
}

function moveScrollRight() {
  const element = document.getElementById("timelineContainer");
  
  const widthContainer = $('.sectionMap').width();
	const containerSize = widthContainer * 0.8;
	
	element.style.scrollBehavior = "smooth"
  element.scrollLeft += (containerSize+(containerSize* (2/5)));
}

function moveScrollLeft() {
  const element = document.getElementById("timelineContainer");
  
	const widthContainer = $('.sectionMap').width();
	const containerSize = widthContainer * 0.8;
	
	element.style.scrollBehavior = "smooth"
  element.scrollLeft -= (containerSize+(containerSize* (2/5)));
}

const convertDateToAfricanDate = (date) => {
	  const africanOptions = { timeZone: 'Africa/Nairobi', month: 'short', day: 'numeric', year: "numeric", hour: "numeric", minute: "numeric"};
    return new Date(date.toLocaleString('en-US', africanOptions));
}
const convertDateToText = (date, withYear) => {
  return new Date(date).toLocaleString('default', withYear? { timeZone: 'Africa/Nairobi', month: 'short', day: 'numeric', year: "numeric" } : { timeZone: 'Africa/Nairobi', month: 'short', day: 'numeric' });
}

const getAbsoluteDays = (startDate, endDate, restDays)  => {
  const oneDay = 24 * 60 * 60 * 1000;
  const isRestDays = restDays? restDays : 0;
  return Math.round(Math.abs((new Date(startDate) - new Date(endDate)) / oneDay)) - isRestDays;
};

const getFirstAndLastDates = (dates) => {
  const sortDatesByStart = dates.map(date => Date.parse(date.startDate)).sort((a, b) => a - b);
  const sortDatesByEnd = dates.map(date => Date.parse(date.endDate)).sort((a, b) => a - b);
  return {
    firstDate: sortDatesByStart[0],
    lastDate: sortDatesByEnd[sortDatesByEnd.length - 1]
  };
}

function getDateBasedOnASumOfDays(startDate, days) {
  const newDate = new Date(startDate);
  newDate.setDate(newDate.getDate() + days);
  return newDate;
}

function createDivTimes(totalDays, divClass, divIdPrefix){
	let arrayDays = [];
	for(let i=0; i < totalDays+1; i++){
		let newDiv = document.createElement('div');
		newDiv.id = `time_${i}` 
		newDiv.className = divClass;
		newDiv.style.width = setWidth();
		newDiv.innerHTML = `
			<p class="timeNumber">
				${convertDateToText(getDateBasedOnASumOfDays(divIdPrefix,i))}
			</p>
		`;
		arrayDays.push(newDiv);
	}
	return arrayDays;
}

function createDivActivities(activity, id){
	
	const status = setStatus(activity.startDate,activity.endDate);
	const card = document.createElement('div');
	card.className = 'activityCard';
	card.id = `activityCard_${id}`;
	card.innerHTML = `
			<div class="activityCard_container" 
			style="left: ${setDistances(activity.startDate,false,false,activity.endDate)}; 
			width: ${setWidth(getAbsoluteDays(activity.startDate, activity.endDate))}; 
			background: ${setStatusColor(status)}
			" >
			
				<div class="activityCard_content"> 
					<h3 class="activityCard_description">${activity.description}</h3>
			    <div class="activityCard_details">
			    		<div>
			    			<img src=${baseURL +"/global/images/start_date.png"} alt="start_icon" />
			    			<p><b>Start date:</b> ${convertDateToText(activity.startDate,true)}</p>
			    		</div>
			    		<p><b>Status:</b> ${status} </p>
			    		<div>
			    			<img src=${baseURL +"/global/images/end_date.png"} alt="end_icon" />
			    			<p><b>End date:</b> ${convertDateToText(activity.endDate,true)}</p>
			    		</div>
			    </div>
				</div>
			</div>
  `;
  
  return card;
}

const setStatus = (startDate, endDate) => {
	const today = convertDateToAfricanDate(new Date());
  const dateStatus = {
    "Completed": today > new Date(endDate),
    "In progress": today > new Date(startDate) && today < new Date(endDate),
    "Not started": today < new Date(startDate)
  };

  const entries = Object.entries(dateStatus);
  for (let i = 0; i < entries.length; i++) {
    const [status, value] = entries[i];
    if (value) {
      return status;
    }
  }
}

const setStatusColor = (status) => {
	  const colorStatus = {
    "Completed": "#B5D08B",
    "In progress": "#81B8C1",
    "Not started": "#F9C786"
  };
  
  return colorStatus[status];
}

function setWidth(amount) {
	const widthContainer = $('.sectionMap').width();
	const widthInPx = `${widthContainer * 0.8}px`;
	return `calc(${amount !==undefined? (amount === 0? 3: amount)+"*("+widthInPx+" / 5) - 10px)": "calc("+widthInPx+" / 5)"}`;
}

function setDistances(startDate,isToday, isJS,endDate) {
	const { firstDate, lastDate } = getFirstAndLastDates(timelineElements);
	let today = convertDateToAfricanDate(new Date());
	
	console.log(timelineElements);
	today = today.getDate() > lastDate? convertDateToAfricanDate(lastDate): today;
	
	const currentHour = today.getHours();
	const percentageCompletion = (currentHour / 24);
  
  
	const widthContainer = $('.sectionMap').width();
	const widthInPx = `${widthContainer * 0.8}px`;
	const containerSize = widthContainer*0.8;
	
	const isFinalActivity = new Date(lastDate).getTime() === new Date(endDate).getTime();
  
  if(isJS){
		
		if(isToday){
			return (getAbsoluteDays(firstDate,today,1) * ((containerSize/5) + ((containerSize/5)*percentageCompletion)) )
		}
		
		return ((isFinalActivity? getAbsoluteDays(firstDate,startDate)-2:getAbsoluteDays(firstDate,startDate) ) * (containerSize/5))
		
	} else {
		
		if(isToday){
			return `calc(${getAbsoluteDays(firstDate, today,1)} * (${widthInPx} / 5) + ((${widthInPx} / 5)* ${percentageCompletion}) )`;
		} 
  	return `calc((${isFinalActivity? getAbsoluteDays(firstDate,startDate)-2:getAbsoluteDays(firstDate,startDate)}) * (${widthInPx} / 5))`;
	}
}

function setTimelinePosition(){
	let weekStart = new Date();
	weekStart.setDate(weekStart.getDate() - weekStart.getDay())
	
	const timelineContainer = document.getElementById("timelineContainer");
	timelineContainer.scrollLeft += setDistances(weekStart, undefined,true);
	
}



function createTimeline2() {
	const getFirstDate = getFirstAndLastDates(timelineElements).firstDate;
	const getLastDate = getFirstAndLastDates(timelineElements).lastDate;
	const getTotalDays = getAbsoluteDays(getFirstDate,getLastDate);
	
	const listItemTimeline=document.getElementById("listItemTimeline2");
	listItemTimeline.innerHTML = `
	  <div>
	  <div id="timelineDescription">
	  	<div id="timelineDescription_title">
	  		<b>Schedule</b>
	  	</div>
	  	<div id="timelineAlert">
	    	<b>Progress status:</b>
	    	<section id="timelineAlert_container">
	    		<article class="timelineAlert_item">
	    			<div class="timelineAlert_item_color timelineAlert_item_color--1"></div>
	    			<p>Not started</p>
	    		</article>
	    		<article class="timelineAlert_item">
	    			<div class="timelineAlert_item_color timelineAlert_item_color--2"></div>
	    			<p>In progress</p>
	    		</article>
	    		<article class="timelineAlert_item">
	    			<div class="timelineAlert_item_color timelineAlert_item_color--3"></div>
	    			<p>Completed</p>
	    		</article>
	    	</section>
    	</div>
	  </div>
    <div id="timelineContainer">
      <div id="timeline_times">
      	${createDivTimes(getTotalDays,"timebox",getFirstDate).reduce((acc, curr) => acc + curr.outerHTML, '')}
      </div>
      <div id="timeline_activities">
      	${timelineElements.map((elem,id) => `
      		${createDivActivities(elem,id).outerHTML}
      	` ).join('')}
      </div>
      <div id="timeline_today" style="left: ${setDistances(null,true)}"></div>
    </div>
  </div>
	`
}

function updateTable(){
  // console.log(this.attr("id"))
  let nameId =$(this).attr("id");
  // let activeCurrent = $('a#'+nameId).parent().addClass('active');
  $("li.active").removeClass('active');
  $('div.active').removeClass('in active');
  $('div#'+nameId+'_wrapper').parent().addClass('in active');
  $('a#'+nameId).parent().addClass('active');

  $(".itemsTablet").removeClass('itemsActive');
  $(`#${nameId}`).addClass('itemsActive');
  $(`.${nameId}`).addClass('itemsActive');
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
    language:{
        searchPlaceholder: "Search..."
      },
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


//Add styles to the table
var iconSearch = $("<div></div>").addClass("iconSearch");
var divDataTables_filter = $('.dataTables_filter').parent();
iconSearch.append('<img src="' + baseUrl + '/global/images/search_outline.png" alt="Imagen"  style="width: 24px; margin: auto;" >');
iconSearch.prependTo(divDataTables_filter);


var divDataTables_length =$('.dataTables_length').parent();
divDataTables_length.css("position", "absolute");
divDataTables_length.css("bottom", "8px");
divDataTables_length.css("margin-left", "43%");
divDataTables_length.css("z-index", "1");

var windowWidth = $(window).width();


if (windowWidth < 768) {
	
	divDataTables_filter.css({
		"width": "100%",	
	});
	
	divDataTables_length.css({
		"left": "30vw",
		"bottom": "0",
		"margin-top": "4rem",
		"margin-left": "0"
	});
}

if (windowWidth < 440) {
	divDataTables_length.css({
		"left": "18vw",
		"bottom": "0",
		"margin-top": "32px",
		"margin-left": "0"
	})
}

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
      }
    }
  });
}