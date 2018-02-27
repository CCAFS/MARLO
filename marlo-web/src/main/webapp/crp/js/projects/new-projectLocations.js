$(document).ready(init);
var map;
var style;
var infoWindow = null;
var markers = [];
var countID;
var countries = [];
var layer;

function init() {
// Init select2
  $('form select').select2({
    width: "100%"
  });

  //Google Maps
  loadScript();

  /* Declaring Events */
  attachEvents();

}

function attachEvents() {

  // REGIONAL QUESTION
  $(".button-label").on("click", function() {
    var valueSelected = $(this).hasClass('yes-button-label');
    var $input = $(this).parent().find('input');
    $input.val(valueSelected);
    $(this).parent().find("label").removeClass("radio-checked");
    $(this).addClass("radio-checked");
    $(document).trigger('updateComponent');
  });

  $(".isRegional .button-label").on("click", function() {
    var valueSelected = $(this).hasClass('yes-button-label');
    if(!valueSelected) {
      $(".regionsBox").hide("slow");
      $(".regionsContent").hide("slow");
    } else {
      $(".regionsBox").show("slow");
      $(".regionsContent").show("slow");
    }
  });

  // Region Select
  $("#regionSelect").on("change", function() {
    var option = $(this).find("option:selected");
    if(option.val() != "-1") {
      addRegion(option);
      // Remove option from select
      // option.remove();
      option.prop('disabled', true);
      $('#regionSelect').select2();
      // $(this).trigger("select2:change");
    }
  });

//Events
  $("#locLevelSelect").on("change",function() {
    var option = $(this).find("option:selected");
    if(option.val() == "-1") {
      $("#addLocationButton").hide("slow");
      resetInfoWindow();
    } else {
      $("#addLocationButton").show("slow");
      if(option.val().split("-")[1] == "true") {
        // If is a country change button text
        if(option.val().split("-")[2] === "Country") {
          $("#addLocationButton").text("Add country(ies)");
        } else {
          $("#addLocationButton").text("Drop pin");
        }
        // LocElements options using ajax
        var select = $("#countriesCmvs");
        var url = baseURL + "/searchCountryListPL.do";
        var data = {
            parentId: option.val().split("-")[0],
            phaseID: phaseID
        };
        $.ajax({
            url: url,
            type: 'GET',
            dataType: "json",
            data: data
        }).done(
            function(m) {
              select.empty();
              for(var i = 0; i < m.locElements.length; i++) {
                select.append("<option class='" + m.locElements[i].isoAlpha2 + "' value='" + m.locElements[i].id
                    + "-" + m.locElements[i].isoAlpha2 + "-" + m.locElements[i].name + "' >"
                    + m.locElements[i].name + "</option>");
              }
            });
        $("#inputFormWrapper").slideUp();
        $(".selectLocations").slideDown();
        console.log(option.val());

      } else {
        $(".selectLocations").slideUp();
        $("#inputFormWrapper").slideDown();
      }
    }
  });
}

//Add Regions
function addRegion(option) {
  var canAdd = true;
  if(option.val() == "-1") {
    canAdd = false;
  }
  var optionValue = option.val().split("-")[0];
  var optionScope = option.val().split("-")[1];

  var $list = $(option).parents("#regionList").find(".list");
  var $item = $("#regionTemplate").clone(true).removeAttr("id");
  var v = $(option).text().length > 20 ? $(option).text().substr(0, 20) + ' ... ' : $(option).text();

  // Check if is already selected
  $list.find('.region').each(function(i,e) {
    if($(e).find('input.rId').val() == optionValue) {
      canAdd = false;
      return;
    }
  });
  if(!canAdd) {
    return;
  }

  // Set region parameters
  $item.find(".name").attr("title", $(option).text());
  $item.find(".name").html(v);
  $item.find(".rId").val(optionValue);
  $item.find(".regionScope").val(optionScope);
  $item.find(".id").val(-1);
  $list.append($item);
  $item.show('slow');
  updateRegionList($list);
  checkRegionList($list);

}

function removeRegion() {
  var $list = $(this).parents('.list');
  var $item = $(this).parents('.region');
  var value = $item.find(".rId").val();
  var scope = $item.find(".regionScope").val();
  var name = $item.find(".name").attr("title");

  var $select = $(".regionsSelect");
  $item.hide(300, function() {
    $item.remove();
    checkRegionList($list);
    updateRegionList($list);
  });
  var option = $select.find("option[value='" + value + "-" + scope + "']");
  console.log(option);
  option.prop('disabled', false);
  $('#regionSelect').select2();
}

function updateRegionList($list) {

  $($list).find('.region').each(function(i,e) {
    // Set regions indexes
    $(e).setNameIndexes(1, i);
  });
}

function checkRegionList(block) {
  var items = $(block).find('.region').length;
  if(items == 0) {
    $(block).parent().find('p.emptyText').fadeIn();
  } else {
    $(block).parent().find('p.emptyText').fadeOut();
  }
}

//Load script of google services
function loadScript() {
  var script = document.createElement("script");
  script.src = "https://maps.googleapis.com/maps/api/js?key=" + GOOGLE_API_KEY + "&callback=initMap";
  // function after load script
  script.onload = script.onreadystatechange = function() {

    $(".selectWrapper").find(".locationLevel").each(function(index,item) {
      $(item).find(".locElement").each(function(i,locItem) {
        var latitude = $(locItem).find(".geoLatitude").val();
        var longitude = $(locItem).find(".geoLongitude").val();
        var isList = $(locItem).parent().parent().parent().find(".isList").val();
        var site = $(locItem).find(".locElementName").val();
        var idMarker = $(locItem).attr("id").split("-")[1];
        if(latitude != "" && longitude != "" && latitude != 0 && longitude != 0) {
          if($(item).find("input.locationLevelId").val() == "10") {
            addMarker(map, (idMarker), parseFloat(latitude), parseFloat(longitude), site, isList, 2);
          } else {
            addMarker(map, (idMarker), parseFloat(latitude), parseFloat(longitude), site, isList, 1);
          }
        }
        // ADD country into countries list
// $.ajax({
// 'url': 'https://maps.googleapis.com/maps/api/geocode/json',
// 'data': {
// key: GOOGLE_API_KEY,
// latlng: (latitude + "," + longitude)
// },
// success: function(data) {
// if(data.status == 'OK') {
// countryName = getResultByType(data.results[0], 'country').long_name;
// // ADD country into countries list
// // countries.push(countryName);
// } else {
// console.log(data.status);
// }
// }
// });
      });

    });
  }
  document.body.appendChild(script);
}

//Initialization Google Map API
function initMap() {

  style = [
      {
          "featureType": "water",
          "stylers": [
              {
                "visibility": "on"
              }, {
                "color": "#b5cbe4"
              }
          ]
      }, {
          "featureType": "landscape",
          "stylers": [
            {
              "color": "#efefef"
            }
          ]
      }, {
          "featureType": "road.highway",
          "elementType": "geometry",
          "stylers": [
            {
              "color": "#83a5b0"
            }
          ]
      }, {
          "featureType": "road.arterial",
          "elementType": "geometry",
          "stylers": [
            {
              "color": "#bdcdd3"
            }
          ]
      }, {
          "featureType": "road.local",
          "elementType": "geometry",
          "stylers": [
            {
              "color": "#ffffff"
            }
          ]
      }, {
          "featureType": "poi.park",
          "elementType": "geometry",
          "stylers": [
            {
              "color": "#e3eed3"
            }
          ]
      }, {
          "featureType": "administrative",
          "stylers": [
              {
                "visibility": "on"
              }, {
                "lightness": 33
              }
          ]
      }, {
        "featureType": "road"
      }, {
          "featureType": "poi.park",
          "elementType": "labels",
          "stylers": [
              {
                "visibility": "on"
              }, {
                "lightness": 20
              }
          ]
      }, {}, {
          "featureType": "road",
          "stylers": [
            {
              "lightness": 20
            }
          ]
      }
  ];
  var mapDiv = document.getElementById('map');
  map = new google.maps.Map(mapDiv, {
      center: new google.maps.LatLng(14.41, -12.52),
      zoom: 3,
      mapTypeId: 'roadmap',
      styles: style
  });
  var centerControlDiv = document.createElement('div');
  if(editable && $("span.has_otherLoc").text() == "true") {
    var centerControl = new CenterControl(centerControlDiv, map);
    centerControlDiv.index = 1;
    map.controls[google.maps.ControlPosition.TOP_CENTER].push(centerControlDiv);
  }

  infoWindow = new google.maps.InfoWindow();

  google.maps.event.addListener(infoWindow, 'closeclick', function() {
    $(".locations").removeClass("selected");
  });

  google.maps.event.addListener(map, 'click', function(event) {
    // infoWindow.close();
    $(".locations").removeClass("selected");
  });

  map.addListener('center_changed', function() {
    // 3 seconds after the center of the map has changed, pan back to the
    // marker.
    if(typeof (infoWindow.type) != "undefined") {
      if(infoWindow.type.data === "form") {
        infoWindow.setPosition(map.getCenter());
        var latitude = infoWindow.getPosition().lat()
        var longitude = infoWindow.getPosition().lng()
        $("#inputFormWrapper").find(".latitude").val(latitude);
        $("#inputFormWrapper").find(".longitude").val(longitude);
      }
    }
  });

// google.maps.event.addListener(map, 'rightclick', function(e) {
// openInfoWindowForm(e);
// });

  if(markers.length > 0) {
    map.setCenter(markers[markers.length - 1].getPosition());
  }

  mappingCountries();

}

function mappingCountries() {
  var query = "";
  layer = new google.maps.FusionTablesLayer();
  if(countries.length > 0) {

    $.each(countries, function(i,c) {
      if(i == countries.length - 1) {
        query = query + "'" + c + "'";
      } else {
        query = query + "'" + c + "',";
      }
    });
    console.log(query);
    var FT_Options = {
        suppressInfoWindows: true,
        query: {
            select: 'kml_4326',
            from: 2597535,
            where: "'ISO_2DIGIT' IN (" + query + ") "
        },
        styles: [
          {
            polygonOptions: {
                fillColor: "#2E2EFE",
                fillOpacity: 0.15
            }
          }
        ]
    };
    layer = new google.maps.FusionTablesLayer(FT_Options);
    layer.setMap(map);
    google.maps.event.addListener(layer, 'click', function(e) {
      openInfoWindowCountries(e);
    });
  }

}