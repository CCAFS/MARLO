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

//Function to set a value for the acronym filter, value is an array
function filterAcronym(value) {
  // Build the filter you want to use. For more information, See Constructing
  // Filters in https://github.com/Microsoft/PowerBI-JavaScript/wiki/Filters.
  const filter = {
    $schema: "http://powerbi.com/product/schema#basic",
    target: {
      table: "project_submission",
      hierarchy: "acronym Hierarchy",
      hierarchyLevel: "acronym"
    },
    operator: "In",
    values: value
  };

  // Get a reference to the embedded report HTML element
  var currentID = $("div[class$='current']").attr("id");
  var embedContainer = $("#" + currentID + '-contentOptions').children().first()[0];

  // Get a reference to the embedded report.
  report = powerbi.get(embedContainer);

  // Set the filter for the report.
  // Pay attention that setFilters receives an array.
  report.setFilters([filter])
    .then(function () {
      console.log("Report filter was set.");
    })
    .catch(function (errors) {
      console.log(errors);
    });
}

//Function to remove the filter panel
function removeFilterPanel(contentId) {
  var newSettings = {
    panes: {
      filters: {
        visible: false
      }
    }
  };
  //Get a reference to the embedded report HTML element
  var embedContainer = $("#" + contentId + '-contentOptions').children().first()[0];
  $('.dashboardContainer').height(250);

  // Get a reference to the embedded report.
  report = powerbi.get(embedContainer);

  // Remove the filters currently applied to the report.
  report.updateSettings(newSettings)
    .then(function () {
      console.log("Report filters were removed.");
    })
    .catch(function (errors) {
      console.log(errors);
    });
}


//Function to remove the navPanel
function removeNavPanel(contentId) {

  // Power bi models.
  var models = window['powerbi-client'].models;

  //The new settings that you want to apply to the report.
  const newSettings = {
    panes: {
      pageNavigation: {
        visible: true
      }
    },
    layoutType: models.LayoutType.Custom,
    customLayout: {
      displayOption: models.DisplayOption.FitToPage,
      // displayOption: models.DisplayOption.FitToWidth,
      pagesLayout: {}
    }
  };


  // Get a reference to the embedded report HTML element
  var embedContainer = $("#" + contentId + '-contentOptions').children().first()[0];


  // Get a reference to the embedded report.
  report = powerbi.get(embedContainer);

  // Update the settings by passing in the new settings you have configured.
  report.updateSettings(newSettings)
    .then(function () {
      console.log("Dashboard was updated.");
    })
    .catch(function (error) {
      console.log(errors);
    });

  // Update height of iframe container depending on dashboard page height
  updateReportHeight(contentId, report, models);

  report.on("buttonClicked", function () {
    updateReportHeight(contentId, report,models);
  });
}

function updateReportHeight(contentId, report, models) {
  var reportId = contentId.split('BIreport-')[1];
  report.getPages().then(function (pages) {
    pages[0].hasLayout(models.LayoutType.MobilePortrait).then(function (hasLayout) {
      pages.forEach(page => {
        if (page.isActive) {
          $("#dashboardContainer-" + reportId).css("height", (page.defaultSize.height));
        }
      });
    })
  });
}

function selectBIReport(e) {
  e.preventDefault();
  var $section = $(e.target).parents('.reportSection');
  var $content = $('#' + $section.attr('id') + '-contentOptions');
  console.log($content);
  $section.siblings().removeClass('current');
  $section.addClass('current');
  $content.siblings().hide();
  setReportTitle();
  // reportsMenuToggle();
  $content.fadeIn();
}