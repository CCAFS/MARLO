
$(document).ready(init);

function init() {
  // Setting ID to Date-picker input



  //reportId = $('input[name=reportID]').val();
  //datasetId = $('input[name=datasetId]').val();

  //console.log(embedUrl);
  //console.log(reportId);
  //console.log(datasetId);
  //setTokenData();
  var idReport = $(".tab-pane.fade.active.in").attr('id');
  console.log(idReport);
  executePetition(idReport);
  addEvents();
}

function addEvents(){
  $('li[role="presentation"]').on("click", function() {
    var idReport = $(this).children().first().attr('class');
    //console.log(reportDbId);
    //$('input#embeUrl-'+reportDbId).val()
    //embedUrl = $('input#embeUrl-'+reportDbId).val();
    //reportId = $('input#reportID-'+reportDbId).val();
    //datasetId = $('input#datasetId-'+reportDbId).val();
    //console.log(embedUrl);
    //console.log(reportId);
    //console.log(datasetId);
    //console.log($('input#reportName-'+reportDbId).val());
    executePetition(idReport);
  });
}

//Peticion to BireportsTokenAction
function executePetition( idReport ) {
  console.log("EP BiReports");
  var $inputsContainer = $('#'+idReport);
  console.log('#'+idReport);
  console.log($inputsContainer);
  var data = {
      datasetId: $inputsContainer.find('input[name=datasetId]').val(),
      reportId: $inputsContainer.find('input[name=reportId]').val()
  }

  //Ajax to service
  $.ajax({
    'url': 'https://localhost:8443/marlo-web/biReportsTokenAction.do',
    'type': "GET",
    'data': data,
    'dataType': "json",
    beforeSend: function() {
        $(".deliverableDisseminationUrl").addClass('input-loading');
        $('#metadata-output').html("Searching ... " + data.metadataID);
    },
    success: function(metadata) {
      console.log("BireportsTokenAction");

      var embedUrl = $inputsContainer.find('input[name=embedUrl]').val();
      var reportId = $inputsContainer.find('input[name=reportId]').val();
      console.log(embedUrl);
      console.log(reportId);
      console.log(metadata.token);
      embedPBI(metadata.token, embedUrl, reportId);
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
// Function that requests to the Api the bearer token necessary for the embed token
function generateBearerToken(baseURL, data) {
  var token = '';
  $.ajax({
    'url': baseURL,
    'type': "POST",
    "headers": {
      "Content-Type": ["application/x-www-form-urlencoded", "application/x-www-form-urlencoded"],
    },
    'data': data,
    success: function (metadata) {
      if (jQuery.isEmptyObject(metadata)) {
        console.log('empty');
      } else {
        $.each(metadata, function (key, value) {
          if (key == 'access_token') {
            token = value;
          }
        });
        // Generate the embed token with the bearer generated
        generateToken(baseUrl, tokenData, token);
      }
    },
    complete: function () {
      // console.log(token);
    },
    error: function () {
      console.log("error");
    }
  });
}

// Generate the embed token with the bearer generated
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
function embedPBI(embedToken, embededURL, dashboardId) {
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
  //var reportDbId = $('input[name=id]').val();
  var $dashboardContainer = $(".tab-pane.fade.active.in").children().first();
  console.log($dashboardContainer);
  var dashboard = powerbi.embed($dashboardContainer.get(0), config);

  // Dashboard.off removes a given event handler if it exists.
  dashboard.off("loaded");

  // Dashboard.on will add an event handler which prints to Log window.
  dashboard.on("loaded", function () {
    console.log("loaded");
  });

  dashboard.on("error", function (event) {
    console.log(event.detail);
    dashboard.off("error");
  });

  dashboard.off("tileClicked");

}

/*
// Embed Dashboard
function embed(userRoles) {
  var url = baseUrlAzure.replace("{tenantID}", tenantID);
  tokenData.identities[0].roles = userRoles;
  generateBearerToken(url, bearerData)
}*/

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
  var embedContainer = $embedContainer[0];

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
  var embedContainer = $embedContainer[0];

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