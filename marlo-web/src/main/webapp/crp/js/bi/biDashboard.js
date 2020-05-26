$(document).ready(init);

function init() {
  //var idReport = $(".tab-pane.fade.active.in").attr('id');
  //executePetition(idReport);
  addEvents();
}

function addEvents(){
  $('.reportSection').each(function( index ) {
    var idReport = $(this).children().first().attr('class');
    executePetition(idReport);
  });
  $('.reportSection a, .reportSection span').on('click', selectBIReport);
  $('.selectedReportBI').on('click', function() {
    $(this).next().slideToggle('slow');
  });

  /*
  $('li[role="presentation"]').on("click", function() {
    var idReport = $(this).children().first().attr('class');
    executePetition(idReport);
  });*/
}

//Peticion to BireportsTokenAction
function executePetition( idReport ) {
  var $inputsContainer = $('#'+idReport+'-contentOptions');
  var data = {
      datasetId: $inputsContainer.find('input[name=datasetId]').val(),
      reportId: $inputsContainer.find('input[name=reportId]').val()
  }

  //Ajax to petition in back to PowerBi
  $.ajax({
    'url': baseURL + '/biReportsTokenAction.do',
    'type': "GET",
    'data': data,
    'dataType': "json",
    beforeSend: function() {
        $(".deliverableDisseminationUrl").addClass('input-loading');
        $('#metadata-output').html("Searching ... " + data.metadataID);
    },
    success: function(metadata) {
      var embedUrl = $inputsContainer.find('input[name=embedUrl]').val();
      var reportId = $inputsContainer.find('input[name=reportId]').val();
      embedPBI(metadata.token, embedUrl, reportId, idReport);
    },
    complete: function() {
      $(".deliverableDisseminationUrl").removeClass('input-loading');
    },
    error: function(e) {
      console.log("error");
    }
  });
}
/*
// Generate the embed token with the bearer generated (JS petition)
function generateToken(baseURL, data, bearerToken) {
  var token = '';
  $.ajax({
    'url': baseURL,
    'type': "POST",
    'data': data,
    'dataType': "json",
    beforeSend: function (xhr) {
      xhr.setRequestHeader("Authorization", "Bearer " + bearerToken);
    },
    success: function (metadata) {
      if (jQuery.isEmptyObject(metadata)) {
        console.log('empty');
      } else {
        console.log('metadata');
        console.log(metadata);
        $.each(metadata, function (key, value) {
          if (key == 'token') {
            token = value;
          }
        });
      }
    },
    complete: function () {
      // console.log(token);
      // Embed Dashboard
      embedPBI(token, embedUrl, reportId);
    },
    error: function () {
      console.log("error");
    }
  });
}*/

// Embed Dashboard
function embedPBI(embedToken, embededURL, dashboardId, contentId) {
  // Get models. models contains enums that can be used.
  var models = window['powerbi-client'].models;
  var permissions = models.Permissions.All;

  // Embed configuration used to describe the what and how to embed.
  // This object is used when calling powerbi.embed.
  // This also includes settings and options such as filters.
  // You can find more information at https://github.com/Microsoft/PowerBI-JavaScript/wiki/Embed-Configuration-Details.

  var config = {
    type: 'report',
    tokenType: models.TokenType.Embed,
    accessToken: embedToken,
    embedUrl: embededURL,
    id: dashboardId,
    permissions: permissions,
    settings: {
      filterPaneEnabled: true,
      navContentPaneEnabled: true
    }
  };

  // Get a reference to the embedded dashboard HTML element
  //$embedContainer =
  var $dashboardContainer = $("#"+contentId+'-contentOptions').children().first();
  var dashboard = powerbi.embed($dashboardContainer.get(0), config);

  // Dashboard.off removes a given event handler if it exists.
  dashboard.off("loaded");

  // Dashboard.on will add an event handler which prints to Log window.
  dashboard.on("loaded", function () {
    console.log("loaded");
    removeNavPanel(contentId);
  });

  dashboard.on("error", function (event) {
    console.log(event.detail);
    dashboard.off("error");
  });

  dashboard.off("tileClicked");
}

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
    values: [value]
  };

  // Get a reference to the embedded report HTML element
  var currentID = $("div[class$='current']").attr("id");
  var embedContainer = $('#dashboardContainer-'+ currentID.split('-')[1])[0];

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

function removeFilterPanel(){
//Get a reference to the embedded report HTML element
  var currentID = $("div[class$='current']").attr("id");
  var embedContainer = $('#dashboardContainer-'+ currentID.split('-')[1])[0];

  // Get a reference to the embedded report.
  report = powerbi.get(embedContainer);

  // Remove the filters currently applied to the report.
  report.removeFilters()
      .then(function () {
          Log.logText("Report filters were removed.");
      })
      .catch(function (errors) {
          Log.log(errors);
      });
}

function removeFilterPanel(){
//Get a reference to the embedded report HTML element
  var currentID = $("div[class$='current']").attr("id");
  var embedContainer = $('#dashboardContainer-'+ currentID.split('-')[1])[0];
  // Get a reference to the embedded report.
  report = powerbi.get(embedContainer);

  // Remove the filters currently applied to the report.
  report.removeFilters()
      .then(function () {
          Log.logText("Report filters were removed.");
      })
      .catch(function (errors) {
          Log.log(errors);
      });
}

function removeNavPanel (contentId) {
//The new settings that you want to apply to the report.
  const newSettings = {
      panes: {
        pageNavigation: {
          visible: false
        }
      }
  };

  // Get a reference to the embedded report HTML element
  var embedContainer = $("#"+contentId+'-contentOptions').children().first()[0];
  console.log(embedContainer.clientWidth);
  console.log(embedContainer.clientHeight);
  console.log($("#"+contentId+'-contentOptions').children().first().clientWidth);
  console.log($("#"+contentId+'-contentOptions').children().first().clientHeight);
  // Get a reference to the embedded report.
  report = powerbi.get(embedContainer);

  // Update the settings by passing in the new settings you have configured.
  report.updateSettings(newSettings)
      .then(function () {
          console.log("Filter pane was removed.");
      })
      .catch(function (error) {
        console.log(errors);
      });
}

function selectBIReport(e) {
  e.preventDefault();
  var $section = $(e.target).parents('.reportSection');
  var $content = $('#' + $section.attr('id') + '-contentOptions');
  $section.siblings().removeClass('current');
  $section.addClass('current');
  $content.siblings().hide();
  $content.fadeIn();
}