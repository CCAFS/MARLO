$(document).ready(init);
var map;
var style;
var infoWindow = null;
var markers = [];
var countID;
var countries = [];
var layer;

function init() {
  loadScript();
  countID = $("form .locElement").length;

  /* Declaring Events */
  attachEvents();

  /* Array countries */
  $("input[value='Country']").parents(".locationLevel").find(".locElement").each(function(i,e) {
    countries.push($(e).find(".locElementCountry").val());
  });
}

function attachEvents() {

  // Remove a location level element-Event
  $(".removeLocationLevel").on("click", removeLocationLevelItem);

// Remove a location element-Event
  $(".removeLocation").on("click", removeLocationItem);

  // Clicking location
  $('.locationName').on('click', function() {
    $(".locations").removeClass("selected");
    var id = $(this).parent().parent().attr("id").split('-')[1];
    var marker = markers[id].gElement;
    if(marker) {
      $(this).parent().addClass("selected");
      openInfoWindow(marker);
      map.setCenter(marker.getPosition());
    } else {
      infoWindow.close();
    }
  });

  $('.projectLocationsWrapper .button-label').on('click', function() {
    var $t = $(this).parent().find('input.onoffswitch-radio');
    var value = ($(this).hasClass('yes-button-label'));
    var $thisLabel = $(this);
    $thisLabel.siblings().removeClass('radio-checked');
    $thisLabel.addClass('radio-checked');

  });

  $("input[name='project.locationGlobal']").on("change", function() {
    var $this = $(this);
    if($this.val() == "true") {
      $this.removeAttr("checked");
      $this.val("false");
    } else {
      $this.attr("checked", true);
      $this.val("true");
    }
  });

// Yes-no button
  $(".no-button-label").on("click", function() {
    $(this).parent().find(".yes-button-label").removeClass("radio-checked");
    $(this).addClass("radio-checked");
    $(this).parent().find("input").val(false);
    checkAllCountries(this);
  });
  $(".yes-button-label").on("click", function() {
    $(this).parent().find(".no-button-label").removeClass("radio-checked");
    $(this).addClass("radio-checked");
    $(this).parent().find("input").val(true);
    checkAllCountries(this);
  });

}

// FUNCTIONS

function checkAllCountries($this) {
  var parent = $($this).parents(".locationLevel");
  console.log(parent);
  if($($this).parent().find("input").val() == "true") {
    parent.find(".locElement").each(function(i,e) {
      $(e).hide("slow");
      var id = $(e).attr("id").split('-')[1];
      if(markers[id] != undefined) {
        removeMarker(id);
      }
    });
    var url = baseURL + "/searchCountryListPL.do";
    var data = {
      parentId: 10
    };
    $.ajax({
        url: url,
        type: 'GET',
        dataType: "json",
        data: data
    }).done(function(m) {
      console.log(m);
      $.each(m.locElements, function(i,e) {
        addMarker(map, e.id, e.lat, e.lng, e.name, "true", 2);
      });
    });
    if($(parent).find(".locationLevelName").val() == "Climate Smart Village Sites") {
      $("#locLevelSelect").find("option[value='10-true-Climate Smart Village Sites']").remove();
    }
  } else {
    // Delete all
    $.each(markers, function(i,e) {
      if(typeof e === 'undefined') {
      } else {
        if(e.type == 2) {
          removeMarker(e.gElement.id);
        }
      }
    });
    // Add current
    parent.find(".locElement").each(
        function(i,e) {
          $(e).show("slow");
          var id = $(e).attr("id").split('-')[1];
          var locLevelName = $(e).parent().parent().parent().find(".locationLevelName");
          if(locLevelName.val() == "Climate Smart Village Sites") {
            addMarker(map, id, parseFloat($(e).find(".geoLatitude").val()),
                parseFloat($(e).find(".geoLongitude").val()), $(e).find(".locElementName").val(), "true", 2);
          }
        });
    if($(parent).find(".locationLevelName").val() == "Climate Smart Village Sites") {
      $("#locLevelSelect").append(
          "<option value='10-true-Climate Smart Village Sites'>Climate Smart Village Sites</option>");
    }
  }
  updateIndex();
}

// Remove a location level element-Function
function removeLocationLevelItem() {
  var globalList = $(this).parents('.locationLevel').parents("#selectsContent");
  var $item = $(this).parents('.locationLevel');
  if(markers != []) {
    // REMOVE all item of this element
    $item.find(".locElement").each(function(index,item) {
      if($(item).find(".geoLatitude").val() != "" && $(item).find(".geoLongitude").val() != "") {
        var optionValue = $(item).attr("id").split('-');
        var id = optionValue[1];
        if(markers[id] != undefined) {
          removeMarker(id);
        }
      }
      layer.setMap(null);
      /* Remove of countries array */

      var i = countries.indexOf($(item).find(".locElementCountry").val());
      if(i > -1) {
        countries.splice(i, 1);
      }
      mappingCountries();
    });
  }

  // Remove location level Element
  $item.hide(function() {
    $item.remove();
    updateIndex();
    checkItems(globalList);
  });
}

// Remove a location element-Function
function removeLocationItem() {
  var globalList = $(this).parents("#selectsContent");
  var list = $(this).parents(".optionSelect-content");
  var $item = $(this).parents('.locElement');
  if($item.find(".geoLatitude").val() != "" && $item.find(".geoLongitude").val() != "") {
    var optionValue = $item.attr("id").split('-');
    var id = optionValue[1];
    if(markers[id] == undefined) {

    } else {
      removeMarker(id);
    }
  }
  $item.hide(function() {
    $item.remove();
    if($(list).find(".locElement").length == 0) {
      $(list).parents(".locationLevel").remove();
    }
    updateIndex();
    checkItems(globalList);
  });
  layer.setMap(null);
  /* Remove of countries array */

  var index = countries.indexOf($item.find(".locElementCountry").val());
  if(index > -1) {
    countries.splice(index, 1);
  }
  mappingCountries();
}

function updateIndex() {
  $('.selectWrapper ').find('.locationLevel').each(function(i,e) {
    $(e).setNameIndexes(1, i);
    $.each($(e).find(".locElement"), function(index,element) {
      $(element).setNameIndexes(2, index);
    });
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
        var isList = $(locItem).parent().parent().parent().find(".isList").val();
        var site = $(locItem).find(".locElementName").val();
        var idMarker = $(locItem).attr("id").split("-")[1];
        if(latitude != "" && longitude != "" && latitude != 0 && longitude != 0) {
          if($(item).find(".locationLevelName").val() == "Climate Smart Village Sites") {
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

// Initialization Google Map API
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
  if(editable) {
    var centerControl = new CenterControl(centerControlDiv, map);
  }

  centerControlDiv.index = 1;
  map.controls[google.maps.ControlPosition.TOP_CENTER].push(centerControlDiv);

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

function CenterControl(controlDiv,map) {

  // Set CSS for the control border.
  var controlUI = document.createElement('div');
  controlUI.style.backgroundColor = '#668fda';
  controlUI.style.border = '2px solid #668fda';
  controlUI.style.borderRadius = '3px';
  controlUI.style.boxShadow = '0 2px 6px rgba(0,0,0,.3)';
  controlUI.style.cursor = 'pointer';
  controlUI.style.marginTop = '10px';
  controlUI.style.marginBottom = '22px';
  controlUI.style.textAlign = 'center';
  controlUI.title = 'Click to add location';
  controlDiv.appendChild(controlUI);

  // Set CSS for the control interior.
  var controlText = document.createElement('div');
  controlText.style.color = 'white';
  controlText.style.fontFamily = 'Roboto,Arial,sans-serif';
  controlText.style.fontSize = '12px';
  controlText.style.lineHeight = '25px';
  controlText.style.paddingLeft = '5px';
  controlText.style.paddingRight = '5px';
  controlText.innerHTML = '<span class="glyphicon glyphicon-plus"></span> <b>Add new Location</b>';
  controlUI.appendChild(controlText);

  // Setup the click event listeners: simply set the map to Chicago.
  controlUI.addEventListener('click', function() {
    var latLng = new google.maps.LatLng(map.getCenter().lat(), map.getCenter().lng());
    console.log(latLng);
    console.log(map.getCenter());
    openInfoWindowForm(latLng);
  });

}

// Map events

function addMarker(map,idMarker,latitude,longitude,sites,isList,locType) {
  // Close info window
  infoWindow.close();
  var drag;
  if(editable && isList == "false") {
    drag = true;
  } else {
    drag = false;
  }

  var $item = $("#location-" + idMarker);
  var marker = new google.maps.Marker({
      id: idMarker,
      draggable: drag,
      position: {
          lat: latitude,
          lng: longitude
      },
      icon: baseURL + '/images/global/otherSite-marker.png',
      name: sites,
      animation: google.maps.Animation.DROP,
      list: isList
  });
  markers[idMarker] = {
      gElement: marker,
      type: locType
  };
// To add the marker to the map, call setMap();
  marker.setMap(map);
  map.setCenter(marker.getPosition());

  // MARKER EVENTS
  marker.addListener('click', function() {
    $(".locations").removeClass("selected");
    openInfoWindow(marker);
    $item.find(".locations").addClass("selected");
  });

  marker.addListener('drag', function() {

    var markerLatLng = marker.getPosition();
    var latitude = markerLatLng.lat();
    var longitude = markerLatLng.lng();
    $item.find("input.geoLongitude").val(longitude);
    $item.find("input.geoLatitude").val(latitude);
    $item.find("span.lPos").html(" (" + latitude.toFixed(4) + ", " + longitude.toFixed(4) + ")");
    $item.find(".locations").addClass("selected");
    // update Infowindow
    $(".editableLoc").find(".latMap").attr("value", latitude);
    $(".editableLoc").find(".lngMap").attr("value", longitude);
  });

  marker.addListener('dragend', function() {
    // GET Isoalpha
    $.ajax({
        'url': 'https://maps.googleapis.com/maps/api/geocode/json',
        'data': {
            key: GOOGLE_API_KEY,
            latlng: ($item.find("input.geoLatitude").val() + "," + $item.find("input.geoLongitude").val())
        },
        success: function(data) {
          if(data.status == 'OK') {
            $item.find('input.locElementCountry').val(getResultByType(data.results[0], 'country').short_name);
          } else {
            console.log(data.status);
          }
        }
    });
    $item.find(".locations").removeClass("selected");
    // Update component event
    $(document).trigger('updateComponent');
  });

}

// Delete markers
function deleteMarkers() {
  setAllMap(null);
  markers = [];
}

// Sets the map on all markers in the array.
function setAllMap(map) {
  $.each(markers, function(index,marker) {
    if(marker.gElement) {
      marker.gElement.setMap(map);
    }
  });
}

// Remove individual marker by id
function removeMarker(id) {
  marker = markers[id].gElement;
  marker.setMap(null);
  delete markers[id];
}

// Removes the markers from the map, but keeps them in the array.
function clearMarkers() {
  setAllMap(null);
}

// Shows any markers currently in the array.
function showMarkers() {
  setAllMap(map);
}

// open info window with the form
function openInfoWindowForm(e) {

  resetInfoWindow();
  var content;
  content = $("#infoWrapper").html();
  infoWindow.setContent([
    content
  ].join(''));
  infoWindow.open(map);
  infoWindow.setPosition(e);
  /** Type* */
  infoWindow.type = {
    "data": "form"
  };
  // Init select2
  $("select").select2();
  if($("select").hasClass("select2-hidden-accessible")) {
    $("select").select2('destroy');
    $('select').select2({
        width: '100%',
        placeholder: function() {
          $(this).data('placeholder');
        }
    });
    $("select").next().next().remove();
  }
  // Set latLng
  $("#inputFormWrapper").find("input.latitude").val(e.lat());
  $("#inputFormWrapper").find("input.longitude").val(e.lng());

  // Events
  formWindowEvents();

}

function formWindowEvents() {

  // $("#inputFormWrapper").find('.latitude, .longitude').numericInput();
  $("#inputFormWrapper").find('.latitude, .longitude').on("keyup", function(e) {
    var $parent = $(this).parent().parent();
    var lat = $parent.find('.latitude').val();
    var lng = $parent.find('.longitude').val();
    if(isCoordinateValid(lat, lng)) {
      $parent.find('.latitude, .longitude').removeClass('fieldError');
      var position = new google.maps.LatLng(lat, lng);
      map.setCenter(position);
      infoWindow.setPosition(position);
    } else {
      $parent.find('.latitude, .longitude').addClass('fieldError');
    }
  });

  /* prevent enter key to inputs */

  $('input').on("keypress", function(event) {

    if(event.keyCode === 10 || event.keyCode === 13) {
      event.preventDefault();
    }
  });

// Events
  $("#locLevelSelect").on(
      "change",
      function() {
        var option = $(this).find("option:selected");
        if(option.val() == "-1") {
          $("#addLocationButton").hide("slow");
          resetInfoWindow();
        } else {
          $("#addLocationButton").show("slow");
          if(option.val().split("-")[1] == "true") {
            // LocElements options using ajax
            var select = $("#countriesCmvs");
            var url = baseURL + "/searchCountryListPL.do";
            var data = {
              parentId: option.val().split("-")[0]
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
                  if($("#locLevelSelect").val().split("-")[2] == "Country") {
                    setCountryDefault();
                  }
                });
            $("#inputFormWrapper").slideUp();
            $(".selectLocations").slideDown();
          } else {
            $(".selectLocations").slideUp();
            $("#inputFormWrapper").slideDown();
          }
        }
      });

  // Add location button
  $("#addLocationButton").on("click", function(e) {

    var $locationLevelSelect = $("#locLevelSelect");
    var locationId = $locationLevelSelect.val().split("-")[0];
    var locationIsList = $locationLevelSelect.val().split("-")[1];
    var locationName = $locationLevelSelect.val().split("-")[2];
    var $locationSelect = $("#countriesCmvs");
    // checking if is list
    if(locationIsList == "true") {
      // Checking if locations select is empty
      if($locationSelect.val() != null) {
        // Checking if the location level exist in the bottom wrapper
        if($(".selectWrapper").find("input.locationLevelId[value='" + locationId + "']").exists()) {
          addCountryIntoLocLevel(locationId, $locationSelect, locationName)
        } else {
          addLocLevel(locationName, locationId, locationIsList, $locationSelect, locationIsList);
        }
      }
    } else {
      if($("#inputFormWrapper").find(".name").val().trim() == "") {
        $("#inputFormWrapper").find(".name").addClass("fieldError");
        console.log("no name");
      } else {
        $("#inputFormWrapper").find(".name").removeClass("fieldError");
        if($("#inputFormWrapper").find(".fieldError").exists()) {

        } else {
          // Checking if the location level exist in the bottom wrapper
          if($(".selectWrapper").find("input.locationLevelId[value='" + locationId + "']").exists()) {
            addLocByCoordinates(locationId, $locationSelect, locationName)
          } else {
            addLocLevel(locationName, locationId, locationIsList, $locationSelect, locationIsList);
          }
        }
      }

    }

  });

  // Cancel button
  $("#cancelButton").on("click", function(e) {
    infoWindow.close();
  });
}

// Set default country to countries select
function setCountryDefault() {
// Ajax for country name
  $.ajax({
      'url': 'https://maps.googleapis.com/maps/api/geocode/json',
      'data': {
          key: GOOGLE_API_KEY,
          latlng: (infoWindow.getPosition().lat() + "," + infoWindow.getPosition().lng())
      },
      success: function(data) {
        if(data.status == 'OK') {
          var country = getResultByType(data.results[0], 'country').short_name;
          var $countrySelect = $("#countriesCmvs");
          console.log($countrySelect.find("option." + country).val());
          $countrySelect.val([
            $countrySelect.find("option." + country).val()
          ]);
          $countrySelect.select2().trigger("change");
        } else {
          console.log(data.status);
        }
      },
  });
}

// Adding location level with locElements
function addLocLevel(locationName,locationId,locationIsList,$locationSelect,locationIsList) {
  var $locationItem = $("#locationLevel-template").clone(true).removeAttr("id");
  $locationItem.find(".locLevelName").html(locationName);
  $locationItem.find("input.locationLevelId").val(locationId);
  $locationItem.find("input.locationLevelName").val(locationName);
  $locationItem.find("input.isList").val(locationIsList);
  $(".selectWrapper").append($locationItem);
  $locationItem.show("slow");
  updateIndex();
  if(locationIsList == "true") {
    if(locationName == "Country") {
    } else {
      $locationItem.find(".allCountriesQuestion").show();
      $locationItem.find("span.question").html($("span.qCmvSites").text());
    }
    addCountryIntoLocLevel(locationId, $locationSelect, locationName);
  } else {
    addLocByCoordinates(locationId, $locationSelect, locationName);
  }
}

function addLocByCoordinates(locationId,$locationSelect,locationName) {
  var countryName = "";
  var $list =
      $(".selectWrapper").find("input.locationLevelId[value='" + locationId + "']").parent().find(
          ".optionSelect-content");
  var $item = $('#location-template').clone(true).removeAttr("id");
  countID++;
  var latitude = $("#inputFormWrapper").find(".latitude").val();
  var longitude = $("#inputFormWrapper").find(".longitude").val();
  console.log(latitude);
  console.log(longitude);
  var name = $("#inputFormWrapper").find("input.name").val();
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
        } else {
          console.log(data.status);
        }
      },
      complete: function(data) {
        $item.attr("id", "location-" + (countID));
        $item.find('.locationName').html(
            '<span class="lName">' + name + '</span><span class="lPos"> (' + parseFloat(latitude).toFixed(4) + ', '
                + parseFloat(longitude).toFixed(4) + ' )</span> ');
        $item.find('.geoLatitude').val(latitude);
        $item.find('.geoLongitude').val(longitude);
        $item.find('.locElementName').val(name);
        $item.find('.locElementId').val(-1);
        $list.append($item);
        $item.show('slow');
        // add marker
        addMarker(map, (countID), parseFloat(latitude), parseFloat(longitude), name, "false", 1);
        // update indexes
        updateIndex();
        checkItems($list.parents("#selectsContent"));
      }
  });
}

// Adding locElement into location level(Country and CSVS)
function addCountryIntoLocLevel(locationId,$locationSelect,locationName) {
  var locationContent =
      $(".selectWrapper").find("input.locationLevelId[value='" + locationId + "']").parent().find(
          ".optionSelect-content");
  $.each($locationSelect.val(), function(i,e) {
    var $item = $("#location-template").clone(true).removeAttr("id");
    var locId = e.split("-")[0];
    var locIso = e.split("-")[1];
    var locName = e.split("-")[2];
    console.log(e);
    // Check if the item doesn't exists into the list
    if(locationContent.find("input.locElementId[value='" + locId + "']").exists()) {
      notify(locName + " already exists into the " + locationContent.parent().parent().find(".locationLevelName").val()
          + " list")
    } else {
      /* GET COORDINATES */
      var url = baseURL + "/geopositionByElement.do";
      var data = {
        "locElementID": locId
      };
      countID++;
      $.ajax({
          url: url,
          type: 'GET',
          dataType: "json",
          data: data
      }).done(function(m) {
        console.log(m);
        if(m.geopositions.length != 0) {
          latitude = m.geopositions[0].latitude;
          longitude = m.geopositions[0].longitude;
          $item.find('.geoLatitude').val(latitude);
          $item.find('.geoLongitude').val(longitude);
          addMarker(map, (countID), parseFloat(latitude), parseFloat(longitude), locName, "true", 2);
          var latLng = new google.maps.LatLng(latitude, longitude);
          console.log(latLng);
          map.setCenter(latLng);
        }
      });
      $item.attr("id", "location-" + (countID));
      $item.find(".lName").html(locName);
      $item.find(".locElementName").val(locName);
      $item.find(".locElementId").val(locId);

      // If is a country
      if(locationName == "Country") {
        countries.push(locIso);
        $item.find(".locElementCountry").val(locIso);
      }
      locationContent.append($item);
      $item.show("slow");
    }
  });
  updateIndex();
  checkItems(locationContent.parents("#selectsContent"));
  infoWindow.close();
  if(locationName == "Country") {
    layer.setMap(null);
    mappingCountries();
  }

}

function resetInfoWindow() {
  $(".selectLocations").hide();
  $("#inputFormWrapper").hide();
}

// Open info window
function openInfoWindow(marker) {
  var contentItem;
  // Check if the location is editable
  if(editable && marker.list == "false") {
    contentItem = $("#informationWrapper");
    console.log((contentItem).find(".nameMap"));
    $(contentItem).find(".nameMap").attr("value", marker.name);
    $(contentItem).find(".latMap").attr("value", marker.getPosition().lat());
    $(contentItem).find(".lngMap").attr("value", marker.getPosition().lng());
  } else {
    contentItem = $("#notEditableInfoWrapper");
    $(contentItem).find(".nameMap").text(marker.name);
    $(contentItem).find(".latMap").text(marker.getPosition().lat());
    $(contentItem).find(".lngMap").text(marker.getPosition().lng());
  }
  $(contentItem).find(".latMap").parents(".latitudeWrapper").show();
  $(contentItem).find(".lngMap").parents(".longitudeWrapper").show();
  var locationLevel = $(contentItem).parent().find("#location-" + marker.id).parents(".locationLevel");
  $(contentItem).find(".infoLocName").text($(locationLevel).find(".locLevelName").text());
  var content = contentItem.html();
  var markerLatLng = marker.getPosition();
  infoWindow.setContent([
    content
  ].join(''));
  infoWindow.open(map, marker);

  /** Type* */
  infoWindow.type = {
    "data": "info"
  };

  // Edit location name from map
  $("#changeLocation").on('click', function editLocationName() {
    console.log(this);
    var parent = $(this).parent().parent();
    console.log(parent);
    var newName = parent.find(".nameMap").val().trim();
    var location = parent.parents(".projectLocationsWrapper").find("#location-" + marker.id);
    console.log(location);

    // Change data marker and inputs form
    if(newName != "") {
      marker.name = newName;
      location.find(".lName").html(newName);
      location.find(".locElementName").val(newName);
      // Update component event
      $(document).trigger('updateComponent');
    }
    // Close infowindow
    infoWindow.close();
    $("#location-" + marker.id).find(".locations").removeClass("selected");

  });
  /** Events latitude and longitude * */
  $($("#inputFormWrapper").find(".latMap , .lngMap")).on("keyup", function(e) {
    var $item = $("#location-" + marker.id);
    var $parent = $(this).parent().parent();
    var lat = $parent.find('.latMap').val();
    var lng = $parent.find('.lngMap').val();
    if(isCoordinateValid(lat, lng)) {
      $parent.find('.latMap, .lngMap').removeClass('fieldError');
      var position = new google.maps.LatLng(lat, lng);
      map.panTo(position);
      marker.setPosition(position);
      // Set values into hidden inputs
      $item.find("input.geoLatitude").val(lat);
      $item.find("input.geoLongitude").val(lng);
      $item.find("span.lPos").html(" (" + lat + ", " + lng + ")");
      $(document).trigger('updateComponent');
    } else {
      $parent.find('.latMap, .lngMap').addClass('fieldError');
    }
  });

  $("#okInfo").on("click", function() {
    infoWindow.close();
  });

}

// Open info window for countries
function openInfoWindowCountries(country) {
  var contentItem;
  // Check if the location is editable
  contentItem = $("#notEditableInfoWrapper");
  $(contentItem).find(".nameMap").text(country.row.Name.value);
  $(contentItem).find(".latMap").parents(".latitudeWrapper").hide();
  $(contentItem).find(".lngMap").parents(".longitudeWrapper").hide();
// $(contentItem).find(".latMap").text(country.latLng.lat().toFixed(4));
// $(contentItem).find(".lngMap").text(country.latLng.lng().toFixed(4));

  var locationLevel =
      $(contentItem).parent().find("input.locElementCountry[value='" + country.row.ISO_2DIGIT.value + "']").parents(
          ".locationLevel");
  $(contentItem).find(".infoLocName").text($(locationLevel).find(".locLevelName").text());

  var content = contentItem.html();
  var countryLatLng = country.latLng;
  infoWindow.setContent([
    content
  ].join(''));

  infoWindow.setPosition(country.latLng);
  infoWindow.open(map);

  /** Type* */
  infoWindow.type = {
    "data": "info"
  };

  $("#okInfo").on("click", function() {
    infoWindow.close();
  });
}

function checkItems(block) {
  console.log(block);
  var items = $(block).find('.locElement').length;
  console.log(items);
  if(items == 0) {
    $(block).find('p.inf').fadeIn();
  } else {
    $(block).find('p.inf').fadeOut();
  }
}

// Edit location name from map function

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

function notify(text) {
  var notyOptions = jQuery.extend({}, notyDefaultOptions);
  notyOptions.text = text;
  notyOptions.type = 'alert';
  noty(notyOptions);
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
