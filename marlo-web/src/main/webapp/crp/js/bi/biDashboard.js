$(document).ready(init);

function init() {

  addEvents();


}

function addEvents() {
  var idReport = $('.reportSection').children().first().attr('class');
  var urlReport= $('.reportSection').children().first().attr('id');

  executePetition(idReport,urlReport);
  $('.reportSection').on('click', function () {
    var idReport = $(this).children().first().attr('class');
    var urlReport= $(this).children().first().attr('id');
    var inputsContainer = $('#' + idReport + '-contentOptions');
    if (!($(inputsContainer).hasClass('loaded'))) {
      executePetition(idReport,urlReport);
    }
  });
  $('.reportSection a, .reportSection span').on('click', selectBIReport);
  // $('.selectedReportBIContainer').on('click', function () {
  //   reportsMenuToggle();
  // });
  // $('.selectedReportBIContainer').hover(function () {
  //   $('#repportsMenu').slideDown("fast");
  //   $('.reportsButtonsIcon').addClass("glyphicon-chevron-down");
  // }, function () {
  //   reportsMenuToggle();
  // });

  $('.setFullScreen').on('click', function () {
    fullScreenDashboard();
  });

  if(userCanLeaveComments = $('#userCanLeaveComments').html() == 'false'){
    $('[has-role-authorization="true"]').hide();
  }

    changeHeightInnerIframe();
  
}

// Toggle width of reports menu
function reportsMenuToggle() {
  $('#repportsMenu').slideToggle("fast");
  $('.reportsButtonsIcon').toggleClass("glyphicon-chevron-down");
}

// Open dashboard in full screen
function fullScreenDashboard() {
  // Get a reference to the embedded report HTML element
  var currentID = $("div[class$='current']").attr("id");
  var embedContainer = $("#" + currentID + '-contentOptions').children().first()[0];

  // Get a reference to the embedded report.
  report = powerbi.get(embedContainer);
  report.updateSettings({})
    // report.updateSettings(newSettings)
    .then(function () {
      report.fullscreen();
      console.log("full Screen Dashboard.");
    })
    .catch(function (error) {
      console.log(errors);
    });
}

// get the embedUrl from the id to pass to the reportName in the widgetInit and reload the page with the information
function executePetition(idReport, urlReport) {
  var url = urlReport.replace("BIreport-", "");
  var inputsContainer = idReport + '-contentOptions';

  pbiwidget.init(inputsContainer, {
     reportName: url
   });

   setReportTitle();
   $(`#${inputsContainer}`).addClass('loaded');
  
}

// Set the report title and description
function setReportTitle() {
  var reportTitle = $("div[class$='current']").attr("report-title");
  $('.headTitle.text-left').text(reportTitle + '');
}

function selectBIReport(e) {
  e.preventDefault();
  var $section = $(e.target).parents('.reportSection');
  var $content = $('#' + $section.attr('id') + '-contentOptions');
  $section.siblings().removeClass('current');
  $section.addClass('current');
  $content.siblings().hide();
  setReportTitle();
  // reportsMenuToggle();
  $content.fadeIn();
}

function changeHeightInnerIframe(){
  let iframeOuter = $("#iframe-dashboardEmbed");
  console.log(iframeOuter);
  let documentIframeOuter = iframeOuter.contents();
  console.log(documentIframeOuter);
  let iframeInner = documentIframeOuter.find("iframe");
  console.log(iframeInner);
  let documentIframeInner = iframeInner.contents();
  console.log(documentIframeInner);

  documentIframeInner.on("click", function(){
    console.log("click");
    let explorationElement = documentIframeInner.find("exploration");
    let ariaLabel = explorationElement.attr("aria-label");
  
    let parentIframeOuter = iframeOuter.parent();

    console.log(ariaLabel);
    console.log(parentIframeOuter);
  
    switch(ariaLabel){
      case "Indicators":
        parentIframeOuter.css("height", "1250px !important");
        break;
      case "Cluster Indicators":
        parentIframeOuter.css("height", "1300px !important");
        break;
      case "ClusterResults":
        parentIframeOuter.css("height", "2500px !important");
        break;
      case "Deliverables":
        parentIframeOuter.css("height", "2150px !important");
        break;
      case "OICRs":
        parentIframeOuter.css("height", "2100px !important");
        break;
      case "Innovations":
        parentIframeOuter.css("height", "1450px !important");
        break;
      default:
        parentIframeOuter.css("height", "1250px !important");
        break;
  
    }
  });
}