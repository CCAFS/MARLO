$(document).ready(init);

function init() {

  addEvents();


}

function addEvents() {
  var $firstReport = $('.reportSection').children().first();
  var idReport = $firstReport.attr('class');
  var urlReport = $firstReport.attr('id');

  // No .reportSection is rendered when the instance has no BI report configured. Skip the
  // initial load in that case so the handlers below still get registered.
  if (idReport && urlReport) {
    executePetition(idReport, urlReport);
  }
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

/*   if(userCanLeaveComments = $('#userCanLeaveComments').html() == 'false'){
    $('[has-role-authorization="true"]').hide();
  } */

  $(window).on('message', function (e) {
    var currentVisible = $(".loaded iframe").filter((i, el) => $(el).closest('.loaded').css('display') !== 'none');
    currentVisible.height(e.originalEvent.data.currentHeight+40);
    $(".loaded").height("auto")
  });
  
}

// Toggle width of reports menu
function reportsMenuToggle() {
  $('#repportsMenu').slideToggle("fast");
  $('.reportsButtonsIcon').toggleClass("glyphicon-chevron-down");
}

// Open dashboard in full screen
function fullScreenDashboard() {
  // Get a reference to the embedded report HTML element
  var currentID = $('.reportSection.current').attr('id');
  if (!currentID) {
    return;
  }
  var embedContainer = $("#" + currentID + '-contentOptions').children().first()[0];

  // Get a reference to the embedded report.
  report = powerbi.get(embedContainer);
  report.updateSettings({})
    // report.updateSettings(newSettings)
    .then(function () {
      report.fullscreen();
    })
    .catch(function (error) {
      console.log(error);
    });
}

// get the embedUrl from the id to pass to the reportName in the widgetInit and reload the page with the information
function executePetition(idReport, urlReport) {
  if (!urlReport) {
    return;
  }
  var url = urlReport.replace("BIreport-", "");
  var inputsContainer = idReport + '-contentOptions';

  pbiwidget.init(inputsContainer, {
     reportName: url,
     autoSize: true,
   });

   setReportTitle();
   $(`#${inputsContainer}`).addClass('loaded');
  
}

// Set the report title and description
function setReportTitle() {
  // Match the class itself rather than an attribute ending in "current": [class$='current']
  // only matches while "current" is the last class in the attribute string, so any script that
  // appends another class to the tab makes this miss. Combined with the old `reportTitle + ''`
  // that rendered the literal string "undefined" on screen (A2-2428).
  var reportTitle = $('.reportSection.current').attr('report-title');
  $('.headTitle.text-left').text(reportTitle || '');
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