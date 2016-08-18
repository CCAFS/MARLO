$(document).ready(init);
var map;
var markers = [];
var countID;

function init() {

  $('.latitude, .longitude').numericInput();

  countID = $("form .locElement").length;

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
    } else {
      addLocationLevel(option);
    }

  });

// ADD a location element by select list-Event
  $('.selectLocation').on('change', function() {
    var option = $(this).find("option:selected");
    var content = $(this).parent().find(".optionSelect-content");
    console.log(this);
    if($(content).find("input[value=" + option.val() + "]").exists()) {
    } else {
      addLocationList($(this).parent(), option);
    }

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
    $(this).val(1);
    var parent = $(this).parent().parent();
    if($(this).is(":checked") == true) {
      parent.find(".selectLocation").attr("disabled", true);
      // parent.find(".selectLocation").css("cursor", "not-allowed");
      parent.find("input.form-control").attr("disabled", true);
      parent.find(".locElement").each(function(i,e) {
        $(e).hide("slow");
      })
    } else {
      $(this).val(0);
      parent.find(".selectLocation").attr("disabled", false);
      // parent.find(".selectLocation").css("cursor", "inherit");
      parent.find("input.form-control").attr("disabled", false);
      parent.find(".locElement").each(function(i,e) {
        $(e).show("slow");
      })
    }
    updateIndex();
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
  if(isList === "true") {
    $item.find(".selectLocation").css("display", "block");
  } else {
    $item.find(".coordinates-inputs").css("display", "block");
  }
  $item.find('.locationLevelId').val(idLocationLevel);
  $list.append($item);

  // LocElements options using ajax
  var select = $item.find(".selectLocation ");
  var url = baseURL + "/searchCountryListPL.do";
  var data = {
    parentId: idLocationLevel
  }
  $.ajax({
      url: url,
      type: 'GET',
      dataType: "json",
      data: data
  }).done(function(m) {
    console.log(m);

    select.empty();
    select.append("<option value='-1' >Select a location</option>");
    for(var int = 0; int < m.locElements.length; int++) {
      select.append("<option value='" + m.locElements[int].id + "' >" + m.locElements[int].name + "</option>");
    }

  });
  $item.show('slow');
  updateIndex();
}

// Add a location by select list
function addLocationList(parent,option) {
  if(option.val() != "-1") {
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
}

// Add a location by coordinates inputs
function addLocationForm(parent,latitude,longitude,name) {
  var countryName = "";
  var $list = parent.find(".optionSelect-content");
  var $item = $('#location-template').clone(true).removeAttr("id");
  countID++;
  // Ajax for country name
  $.ajax({
      'url': 'https://maps.googleapis.com/maps/api/geocode/json',
      'data': {
          key: GOOGLE_API_KEY,
          latlng: (latitude + "," + longitude)
      },
      success: function(data) {
        if(data.status == 'OK') {
          $item.find('input.locElementCountry').val(getResultByType(data.results[0], 'country').short_name);
          countryName = getResultByType(data.results[0], 'country').long_name;
        } else {
          console.log(data.status);
        }
      },
      complete: function(data) {
        $item.attr("id", "location-" + (countID));
        $item.find('.locationName').html(
            name + " <label > " + countryName + "(" + latitude + ", " + longitude + ")</label>");
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
        addMarker(map, (countID), parseInt(latitude), parseInt(longitude), name);
        // update indexes
        updateIndex();
      }
  });

}

// Remove a location level element-Function
function removeLocationLevelItem() {
  var $item = $(this).parents('.locationLevel');

  // REMOVE all item of this element
  $item.find(".locElement").each(function(index,item) {
    if($(item).find(".geoLatitude").val() != "" && $(item).find(".geoLongitude").val() != "") {
      var optionValue = $(item).attr("id").split('-');
      var id = optionValue[1];
      removeMarker(id);
    }
  })

  // Remove location level Element
  $item.hide(function() {
    $item.remove();
    updateIndex();
  });
  if($(".selectWrapper").find(".locationLevel").length <= 1) {
    $(".map").hide('slow');
    deleteMarkers();
  }
}

// Remove a location element-Function
function removeLocationItem() {
  var $item = $(this).parents('.locElement');
  if($item.find(".geoLatitude").val() != "" && $item.find(".geoLongitude").val() != "") {
    var optionValue = $item.attr("id").split('-');
    var id = optionValue[1];
    removeMarker(id);
  }
  $item.hide(function() {
    $item.remove();
    updateIndex();
  });

}

// Update indexes
function updateIndex() {
  var name = $("#locationLevelName").val();
  $(".selectWrapper").find('.locationLevel').each(function(i,item) {
    var customName = name + '[' + i + ']';
    $(item).find('.locationLevelId').attr('name', customName + '.id');
    $(item).find('.locationLevelType').attr('name', customName + '.type');
    $(item).find('.allCountries').attr('name', customName + '.allCountries');
    updateLocationIndex(item, customName);
  });
}

function updateLocationIndex(item,locationLevelName) {
  var name = $("#locationName").val();
  $(item).find('.locElement').each(function(indexLoc,locItem) {
    var customName = locationLevelName + '.' + name + '[' + indexLoc + ']';
    $(locItem).find('.locElementId').attr('name', customName + '.id');
    $(locItem).find('.geoLatitude').attr('name', customName + '.latitude');
    $(locItem).find('.geoLongitude').attr('name', customName + '.longitude');
    $(locItem).find('.locElementName').attr('name', customName + '.name');
    $(locItem).find('.locElementCountry').attr('name', customName + '.isoAlpha2');
    $(locItem).find('.geoId').attr('name', customName + '.locGeoposition.id');
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
        var site = $(locItem).find(".locElementName").val();
        var idMarker = $(locItem).attr("id").split("-")[1];
        if(latitude != "" && longitude != "") {
          addMarker(map, (idMarker), parseInt(latitude), parseInt(longitude), site);
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

function addMarker(map,idMarker,latitude,longitude,sites) {

  var contentString = '<div id="infoContent"><input placeholder="name" class="name form-control" type="text" /></div>';

  var infowindow = new google.maps.InfoWindow({
    content: contentString
  });

  var marker = new google.maps.Marker({
      id: idMarker,
      position: {
          lat: latitude,
          lng: longitude
      },
      icon: baseURL + '/images/global/otherSite-marker.png',
      title: sites,
      animation: google.maps.Animation.DROP
  });
  markers[idMarker] = marker;
// To add the marker to the map, call setMap();
  marker.setMap(map);

  marker.addListener('click', function() {
    infowindow.close();
    infowindow.open(map, marker);
  });
}

function deleteMarkers() {
  setAllMap(null);
  markers = [];
}

// Sets the map on all markers in the array.
function setAllMap(map) {
  $.each(markers, function(index,marker) {
    if(marker) {
      marker.setMap(map);
    }
  });
}

// Remove individual marker by id
function removeMarker(id) {
  marker = markers[id];
  marker.setMap(null);
  delete markers[id];
}

// Get short and long country name
function getResultByType(results,type) {
  if(results) {
    for(var i = 0; i < results.address_components.length; i++) {
      var types = results.address_components[i].types;
      for(var typeIdx = 0; typeIdx < types.length; typeIdx++) {
        if(types[typeIdx] == type) {
          return {
              short_name: results.address_components[i].short_name,
              long_name: results.address_components[i].long_name
          };
        }
      }
    }
  } else {
    return undefined;
  }
}
