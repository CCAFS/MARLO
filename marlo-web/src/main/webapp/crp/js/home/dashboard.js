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
  createTimeline();
  createTimeline2();

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
  var lastPosition = timelineElements.length;

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
    var options = {timeZone: 'Africa/Nairobi', year: 'numeric', month: 'numeric', day: 'numeric', hour: 'numeric', minute: 'numeric', second: 'numeric'};
    var africaOpciones = { timeZone: 'Africa/Nairobi', day: 'numeric' };
    var africanDate = new Date(new Date().toLocaleString('en-US', options));

    var description = document.createTextNode(data.description);
    var dateMonthStart = new Date(data.startDate).toLocaleString("en-US", { month: "short" });
    var dateDayStart = new Date(data.startDate).getDate()+1;    
    var dateMonthYear =new Date(data.endDate).getFullYear();
    var dateMonthEnd = new Date(new Date(data.endDate).toLocaleString('en-US', options)).toLocaleString("en-US", { month: "short" });
    var dateDayEnd = new Date(data.endDate).toLocaleString('en-US', africaOpciones);
    var date =document.createTextNode(dateMonthEnd+' '+ dateDayEnd+' - '+dateMonthYear)
    newDivTitle.appendChild(description);
    newPTimeLine.appendChild(date);
    var endDate = new Date(new Date(data.endDate).toLocaleString('en-US', options));
    endDate.setDate(endDate.getDate() + 1)

    if(description.length > 120)newDivTitle.style["width"] = '120px';

    //hide alert the days left to finalize activity 
    if(((lastPosition - 1) == index) && endDate < africanDate )$('.timelineAlert').hide();
    
    var closingTime = new Date(africanDate.setHours(africanDate.getHours() + 1));
    
    if(endDate < closingTime){

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
    if(counter == 0 && (endDate > africanDate)){

      let dateDiff = endDate.getTime() - new Date(previusDate).getTime();
      let daysFinalizeActivity = ((endDate.getTime() - africanDate.getTime())/(1000*60*60*24));
      newPorcentTimeLine.className='porcentTimeLine';
      newDivTitle.classList.add('timelineColorAlert');
      newDivPoint.classList.add('timelineBackAlert');

      linePorcent = -(daysFinalizeActivity*100)/(dateDiff/(1000*60*60*24))+100;
      newDivTimeLine.style["margin-top"] = "0";
      newPorcentTimeLine.style["width"] = Math.round(linePorcent)+'%';
      if(linePorcent < 0) newPorcentTimeLine.style["width"] = Math.round(0)+'%';
      newPorcentTimeLine.appendChild(newDivTimeLine);      
      let textAlert ='';
      textAlert = Math.round(daysFinalizeActivity+1)+' day left to finalize the current activity';
      if(Math.round(daysFinalizeActivity+1)>1) textAlert = Math.round(daysFinalizeActivity+1)+' days left to finalize the current activity';
      counter = 1;
      $('.timelineAlertText').text(textAlert);
    }
  })
  $(".timelineRefresh").hide();
  $(".timeline").show();
  // Locate pending activity
  const element = document.querySelector(".scroll-x-containerTimeline");
  element.scrollLeft += 243*(counterActvi-2);
  
}

const convertDateToText = (date, withYear) => {
  return new Date(date).toLocaleString('default', withYear? { month: 'short', day: 'numeric', year: "numeric" } : { month: 'short', day: 'numeric' });
}

const getAbsoluteDays = (startDate, endDate) => {
  const oneDay = 24 * 60 * 60 * 1000;
  return Math.round(Math.abs((new Date(startDate) - new Date(endDate)) / oneDay));
};

const getRemainingDays = (endDate) => {
  const oneDay = 24 * 60 * 60 * 1000;
  const today = new Date();

  return new Date(endDate) - today > 0 ? Math.round(Math.abs((new Date(today) - new Date(endDate)) / oneDay)) : 0;
}

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
	for(let i=0; i < totalDays; i++){
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
			style="left: ${setDistances(activity.startDate)}; 
			width: ${setWidth(getAbsoluteDays(activity.startDate, activity.endDate))}; 
			background: ${setStatusColor(status)}
			" >
			
				<div class="activityCard_content"> 
					<h3 class="activityCard_description">${activity.description}</h3>
			    <div class="activityCard_details">
			    		<p>Start date: ${activity.startDate}</p>
			    		<p>Status: ${status} </p>
			    		<p>End date: ${activity.endDate}</p>
			    </div>
				</div>
			</div>
  `;
  
  return card;
}

const setStatus = (startDate, endDate) => {
	const today = new Date();
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
	return `calc(${amount? (amount === 0? 3: amount)+"*(80vw / 7))": "calc(80vw / 7)"}`;
}

function setDistances(startDate,isToday) {
	const today = new Date();
  const { firstDate } = getFirstAndLastDates(timelineElements);
  
  if(isToday){
		return `calc(${getAbsoluteDays(firstDate, today)} * (80vw / 7))`;
	}

  return `calc(${getAbsoluteDays(firstDate, startDate)} * (80vw / 7))`;

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
	  	<p id="timelineDescription_range">${convertDateToText(getFirstDate,true)} - ${convertDateToText(getLastDate,true)}</p>
	  </div>
    <div id="timelineContainer">
      <div id="timeline_today" style="left: ${setDistances(null,true)}"></div>
      <div id="timeline_times">
      	${createDivTimes(getTotalDays,"timebox",getFirstDate).reduce((acc, curr) => acc + curr.outerHTML, '')}
      </div>
      <div id="timeline_activities">
      	${timelineElements.map((elem,id) => `
      		${createDivActivities(elem,id).outerHTML}
      	` ).join('')}
      </div>

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