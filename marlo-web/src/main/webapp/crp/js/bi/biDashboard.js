// Bearer token generator data
var baseUrlAzure;
var tenantID;
var clientId;
var clientSecret;
var resource;

// Embed token generator data
var baseUrl;
var reportId;
var datasetId;

// Embed Data
var embedUrl;
var $embedContainer;
$(document).ready(init);

//Peticion to BireportsTokenAction
function executePetition() {
  console.log("EP BiReports");
var data = {
    datasetId: "9bd72c88-3162-4a6b-a4cc-8422e61e9eeb",
    reportId: "50e6f7be-fef1-43cd-9983-4008f47f4a4d"
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
    console.log(metadata);
  },
  complete: function() {
    $(".deliverableDisseminationUrl").removeClass('input-loading');
  },
  error: function(e) {
    console.log("error");
    console.log(e.responseText);
  }
});}

//Peticion example for deliverable
function executePetitionDeliverable() {
  console.log("EP MetadataByLink");
  var data = {
      pageID: "harvardDataverse",
      metadataID: "https://dataverse.harvard.edu/dataset.xhtml?persistentId=doi:10.7910/DVN/0ZEXKC",
      phaseID: 101
  }

//Ajax to service
  $.ajax({
    'url': 'https://localhost:8443/marlo-web/metadataByLink.do',
    'type': "GET",
    'data': data,
    'dataType': "json",
    beforeSend: function() {
      $(".deliverableDisseminationUrl").addClass('input-loading');
      $('#metadata-output').html("Searching ... " + data.metadataID);
    },
    success: function(algo) {
      console.log("executePetitionDeliverable");
      metadata = algo.metadata;
      console.log(metadata);
    },
    complete: function() {
      $(".deliverableDisseminationUrl").removeClass('input-loading');
    },
    error: function() {
      console.log("error");
      $('#metadata-output').empty().append("Invalid URL for searching metadata");
    }
});
}

function init() {
  // Setting ID to Date-picker input
  $embedContainer =$(".tab-pane.fade.in.active").children().first();
  console.log($embedContainer);
  embedUrl = $('input[name=embeUrl]').val();
  reportId = $('input[name=reportID]').val();
  datasetId = $('input[name=datasetId]').val();
  console.log(embedUrl);
  console.log(reportId);
  console.log(datasetId);
  console.log($('input[name=reportName]').val());
  tokenData = {
      "datasets": [
        {
          "id": datasetId
        }
      ],
      "reports": [
        {
          "id": reportId
        }
      ],
      "identities": [
        {
          "username": "MarloEmbedApp",
          "roles": [
            ""
          ],
          "datasets": [
            datasetId
          ]
        }
      ]
    }
  embed(["CCAFS"]);

  executePetition();
  executePetitionDeliverable();

  addEvents();
}

function addEvents(){
  $('li[role="presentation"]').on("click", function() {
    var reportDbId = $(this).children().first().attr('index');
    $embedContainer = $("#dashboardContainer-"+reportDbId);

    console.log(reportDbId);
    //$('input#embeUrl-'+reportDbId).val()
    embedUrl = $('input#embeUrl-'+reportDbId).val();
    reportId = $('input#reportID-'+reportDbId).val();
    datasetId = $('input#datasetId-'+reportDbId).val();
    console.log(embedUrl);
    console.log(reportId);
    console.log(datasetId);
    console.log($('input#reportName-'+reportDbId).val());
    tokenData = {
        "datasets": [
          {
            "id": datasetId
          }
        ],
        "reports": [
          {
            "id": reportId
          }
        ],
        "identities": [
          {
            "username": "MarloEmbedApp",
            "roles": [
              ""
            ],
            "datasets": [
              datasetId
            ]
          }
        ]
      }
    if(reportDbId =="1"){
      embed(["CCAFS"]);
    }else{
      embed(["PIM"]);
    }
  });
}

baseUrl = 'https://api.powerbi.com/v1.0/myorg/GenerateToken';
baseUrlAzure = 'https://login.microsoftonline.com/{tenantID}/oauth2/token';
tenantID = '6afa0e00-fa14-40b7-8a2e-22a7f8c357d5'

// Bearer token generator Data
clientId = "a30f2154-8314-4d82-8131-97c1cdfaf6fe";
clientSecret = "T69q-Krzgbu.YypNmQWDMJh=Jl?m7m6J";
resource = "https://analysis.windows.net/powerbi/api";

// Embed token generator Data
//reportId = "50e6f7be-fef1-43cd-9983-4008f47f4a4d";
//datasetId = "9bd72c88-3162-4a6b-a4cc-8422e61e9eeb";

// Embed Data
//embedUrl = "https://app.powerbi.com/reportEmbed?reportId=50e6f7be-fef1-43cd-9983-4008f47f4a4d&groupId=37376d13-3df2-4447-aaa5-49c047533b4f&config=eyJjbHVzdGVyVXJsIjoiaHR0cHM6Ly93YWJpLW5vcnRoLWV1cm9wZS1yZWRpcmVjdC5hbmFseXNpcy53aW5kb3dzLm5ldC8ifQ%3D%3D";

// Json data to generate embed token
var tokenData = {
  "datasets": [
    {
      "id": datasetId
    }
  ],
  "reports": [
    {
      "id": reportId
    }
  ],
  "identities": [
    {
      "username": "MarloEmbedApp",
      "roles": [
        ""
      ],
      "datasets": [
        datasetId
      ]
    }
  ]
}

// Json data to generate bearer token
var bearerData = {
  "grant_type": "client_credentials",
  "client_id": clientId,
  "client_secret": clientSecret,
  "resource": resource
}

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
        /*
        console.log('metadata');
        console.log(metadata);
        */
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
}

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
  //var $dashboardContainer = $('#dashboardContainer');
  var $dashboardContainer = $embedContainer;

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

// Embed Dashboard
function embed(userRoles) {
  var url = baseUrlAzure.replace("{tenantID}", tenantID);
  tokenData.identities[0].roles = userRoles;
  generateBearerToken(url, bearerData)
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
// test
embed(["CCAFS"]);