$(document).ready(init);
var map;

function init() {

  $('.latitude, .longitude').numericInput();

  // validate latitude and longitude
  $('.latitude, .longitude').on("keyup", function(e) {
    var $parent = $(this).parent().parent();
    var lat = $parent.find('.latitude').val();
    var lng = $parent.find('.longitude').val();

    if(isCoordinateValid(lat, lng)) {
      $parent.find('.latitude, .longitude').removeClass('fieldError');
    } else {
      $parent.find('.latitude, .longitude').addClass('fieldError');
    }
  });

  $(".selectLocationLevel").select2({
    placeholder: "Select a location level"
  })

  // validate if regions list is empty
  if($(".selectWrapper").find(".locationLevel").length > 0) {
    $(".map").show('slow', function() {
      loadScript();
    });

  } else {
    $(".map").hide();
  }

  /* Declaring Events */
  attachEvents();
}

function attachEvents() {

// ADD a location level element-Event
  $('.selectLocationLevel').on('change', function() {

    if($(".selectWrapper").find(".locationLevel").length <= 0) {

      $(".map").show("slow", function() {
      });
    }
    var option = $(this).find("option:selected");
    if($(".selectWrapper").find("input[value=" + option.val().split("-")[0] + "]").exists()) {
      console.log("exists");
    } else {
      addLocationLevel(option);
    }

  });

  // Select location ajax
  $('.selectLocation').on('click', function() {
    var parent = $(this).parent().parent();
    var url = baseURL + "/searchCountryListPL.do";
    var data = {
      parentId: parent.find(".locationLevelId").val()
    }
    console.log(data);
    $.ajax({
        url: url,
        type: 'GET',
        dataType: "json",
        data: data
    }).done(function(m) {
      console.log(m);
    });

  });

// ADD a location element by select list-Event
  $('.selectLocation').on('change', function() {
    var option = $(this).find("option:selected");
    addLocationList($(this).parent(), option);
  });

// ADD a location element by coordinates inputs list-Event
  $('.addLocation').on('click', function() {
    var latitude = $(this).parent().find(".latitude").val();
    var longitude = $(this).parent().find(".longitude").val();
    var name = $(this).parent().find(".name").val();
    if(latitude != "" && latitude != null && longitude != "" && longitude != null && name != "" && name != null) {
      if(isCoordinateValid(latitude, longitude)) {
        addLocationForm($(this).parent().parent(), latitude, longitude, name);
      }
    }
  });

  // Remove a location level element-Event
  $(".removeLocationLevel").on("click", removeLocationLevelItem);

// Remove a location element-Event
  $(".removeLocation").on("click", removeLocationItem);

  // Checkbox to working in all regions
  $(".allCountries").on("change", function() {
    console.log("holi")
  });

  // Collapsible
  $('.locationLevel-option').on(
      'click',
      function() {
        var content = $(this).parent().parent().find('.locationLevel-optionContent');
        if($(this).hasClass('closed')) {
          content.slideDown();
          $(this).parent().find(".collapsible").removeClass("glyphicon glyphicon-chevron-down").addClass(
              "glyphicon glyphicon-chevron-up");
          $(this).removeClass('closed').addClass('opened');
        } else {
          $(this).parent().find(".collapsible").removeClass("glyphicon glyphicon-chevron-up").addClass(
              "glyphicon glyphicon-chevron-down");
          $(this).removeClass('opened').addClass('closed');
          content.slideUp();
        }
      });
}

// FUNCTIONS

function addLocationLevel(option) {
  var $list = $('.selectWrapper');
  var $item = $('#locationLevel-template').clone(true).removeAttr("id");
  $item.find('.locationLevel-option').html(option.html());
  var optionValue = option.val().split('-');
  var idLocationLevel = optionValue[0];
  var isList = optionValue[1];
  console.log(isList);
  if(isList === "true") {
    $item.find(".selectLocation").css("display", "block");
  } else {
    $item.find(".coordinates-inputs").css("display", "block");
  }
  $item.find('.locationLevelId').val(idLocationLevel);
  $list.append($item);
  // updateAllIndexes();
  $item.show('slow');
  updateIndex();
}

// Add a location by select list
function addLocationList(parent,option) {
  var $list = parent.find(".optionSelect-content");
  var $item = $('#location-template').clone(true).removeAttr("id");
  $item.find('.locationName').html(option.html());
  $item.find('.locElementId').val(option.val());
  $item.find('.locElementName').val(option.html());
  $list.append($item);
  // updateAllIndexes();
  $item.show('slow');
  updateIndex();
}

// Add a location by coordinates inputs
function addLocationForm(parent,latitude,longitude,name) {
  var $list = parent.find(".optionSelect-content");
  var $item = $('#location-template').clone(true).removeAttr("id");
  $item.find('.locationName').html(name + " <label > (" + latitude + ", " + longitude + ") </label>");
  $item.find('.geoLatitude').val(latitude);
  $item.find('.geoLongitude').val(longitude);
  $item.find('.locElementName').val(name);
  $list.append($item);
  // updateAllIndexes();
  $item.show('slow');
  // empty input fields
  parent.find(".latitude").val("");
  parent.find(".longitude").val("");
  parent.find(".name").val("");
  // add marker
  addMarker(map, parseInt(latitude), parseInt(longitude), name);
  // update indexes
  updateIndex();
}

// Remove a location level element-Function
function removeLocationLevelItem() {
  var $item = $(this).parents('.locationLevel');
  $item.hide(function() {
    $item.remove();
    updateIndex();
  });
  if($(".selectWrapper").find(".locationLevel").length <= 1) {
    $(".map").hide('slow');
  }
}

// Remove a location element-Function
function removeLocationItem() {
  var $item = $(this).parents('.locElement');
  console.log($item);
  $item.hide(function() {
    $item.remove();
    updateIndex();
  });

}

// Update index
function updateIndex() {
  var name = $("#locationLevelName").val();
  $(".selectWrapper").find('.locationLevel').each(function(i,item) {
    var customName = name + '[' + i + ']';
    $(item).find('.locationLevelId').attr('name', customName + '.id');
    $(item).find('.locationLevelType').attr('name', customName + '.type');
    updateLocationIndex(item, customName);
  });
}

function updateLocationIndex(item,locationLevelName) {
  var name = $("#locationName").val();
  $(item).find('.locElement').each(function(indexLoc,locItem) {
    console.log(locItem);
    var customName = locationLevelName + '.' + name + '[' + indexLoc + ']';
    $(locItem).find('.locElementId').attr('name', customName + '.id');
    $(locItem).find('.geoLatitude').attr('name', customName + '.latitude');
    $(locItem).find('.geoLongitude').attr('name', customName + '.longitude');
    $(locItem).find('.locElementName').attr('name', customName + '.name');
  });

  // Update component event
  $(document).trigger('updateComponent');
}

// Load script of google services
function loadScript() {
  var script = document.createElement("script");
  script.src = "https://maps.googleapis.com/maps/api/js?key=" + GOOGLE_API_KEY + "&callback=initMap";
  // function after load script
  script.onload = script.onreadystatechange = function() {
    $(".selectWrapper").find(".locationLevel").each(function(index,item) {
      $(item).find(".locElement").each(function(i,locItem) {
        var latitude = $(locItem).find(".geoLatitude").val();
        var longitude = $(locItem).find(".geoLongitude").val();
        var site = $(locItem).find(".locElementName").val()
        if(latitude != "" && longitude != "") {
          addMarker(map, parseInt(latitude), parseInt(longitude), site);
        }
      });
    });
  }
  document.body.appendChild(script);
}

// Initialization Google Map API
function initMap() {

  var style = [
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
      zoom: 2,
      mapTypeId: 'roadmap',
      styles: style
  });
}

// Map events

function addMarker(map,latitude,longitude,sites) {

  var marker = new google.maps.Marker({
      position: {
          lat: latitude,
          lng: longitude
      },
      title: sites
  });

// To add the marker to the map, call setMap();
  marker.setMap(map);
}