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

  /* Declaring Events */
  attachEvents();

  /* Array countries */
  $("input[value='Country']").parents(".locationLevel").find(".locElement").each(function(i,e) {
    countries.push($(e).find(".locElementCountry").val());
  });
  // calculateWidthSelect();
}

function attachEvents() {

// ADD a location level element-Event
  $('.selectLocationLevel').on('change', function() {

    /*
     * if($(".selectWrapper").find(".locationLevel").length <= 0) { var mapDiv = document.getElementById('map');
     * $(".map").show("slow", function() { initMap(); }); }
     */
    var option = $(this).find("option:selected");
    if(option.val() != "-1") {
      if($(".selectWrapper").find("input.locationLevelId[value=" + option.val().split("-")[0] + "]").exists()) {
        var text = option.html() + ' already exists in this list';
        notify(text);
      } else {
        addLocationLevel(option);
      }
    }

  });

// ADD a location element by select list-Event
  $('.selectLocation').on('change', function() {
    var option = $(this).find("option:selected");
    var content = $(this).parent().find(".optionSelect-content");
    if($(content).find("input[value=" + option.val() + "]").exists()) {
      var text = option.html() + ' already exists in this list';
      notify(text);
    } else {
      if(option.parents(".locationLevel").find(".locationLevelName").val() == "Country") {
        // countries.push(option.text());
        // mappingCountries();
      }
      addLocationList($(this).parent(), option);
    }

  });

// ADD a location element by coordinates inputs list-Event
  $('.addLocation').on('click', function() {
    var latitude = $(this).parent().find(".latitude").val();
    var longitude = $(this).parent().find(".longitude").val();
    var name = $(this).parent().find(".name").val();
    if(name != "") {
      $(this).parent().find(".name").removeClass('fieldError');
      if(latitude != "" && latitude != null && longitude != "" && longitude != null) {
        if(isCoordinateValid(latitude, longitude)) {
          console.log("here1");
          addLocationForm($(this).parent().parent(), latitude, longitude, name);
        }
      } else {
        addLocationForm($(this).parent().parent(), "", "", name);
      }
    } else {
      $(this).parent().find(".name").addClass('fieldError');
    }

  });

  // Remove a location level element-Event
  $(".removeLocationLevel").on("click", removeLocationLevelItem);

// Remove a location element-Event
  $(".removeLocation").on("click", removeLocationItem);

  // Checkbox to working in all regions
  $(".allCountries").on("change", checkboxAllCountries);

  // Collapsible
  $('.locationLevel-optionSelect').on(
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

  // Clicking location
  $('.locationName').on('click', function() {
    $(".locations").removeClass("selected");
    var id = $(this).parent().parent().attr("id").split('-')[1];
    var marker = markers[id];
    if(marker) {
      $(this).parent().addClass("selected");
      openInfoWindow(marker);
      map.setCenter(marker.getPosition());
    } else {
      infoWindow.close();
    }
  });

  $("#view1").on("click", changeLayout2);
  $("#view2").on("click", changeLayout);

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

}

// FUNCTIONS

// Changes layout
function changeLayout() {
  var selectWrapper = $("#selectsContent");
  var map = $(".map")
  map.insertAfter("#selectsContent");
  selectWrapper.removeClass("col-md-12").addClass("col-md-6");
  selectWrapper.removeClass("selectWrapper-horizontal");
  map.removeClass("col-md-12").addClass("col-md-6");
  selectWrapper.find(".locationLevel").removeClass("locationLevel-horizontal").addClass("col-md-12");
  selectWrapper.find(".selectWrapper").removeClass("select-horizontal");
  selectWrapper.find(".selectWrapper").css("width", "auto");
  // calculateWidthSelect();
  initMap();
  showMarkers();
}

function changeLayout2() {
  var selectWrapper = $("#selectsContent");
  var map = $(".map")
  selectWrapper.insertAfter(".map");
  selectWrapper.removeClass("col-md-6").addClass("col-md-12");
  selectWrapper.addClass("selectWrapper-horizontal");
  map.removeClass("col-md-6").addClass("col-md-12");
  selectWrapper.find(".locationLevel").removeClass("col-md-12").addClass("locationLevel-horizontal");
  selectWrapper.find(".selectWrapper").addClass("select-horizontal");
  calculateWidthSelect();
  initMap();
  showMarkers();
}

// Change width of selectWrapper
function calculateWidthSelect() {
  var widthSelect = ($("form .locationLevel").length) * 395;
  $(".select-horizontal").css("width", widthSelect + "px");
}

function checkboxAllCountries() {
  $(this).val(true);
  var parent = $(this).parents(".locationLevel");
  if($(this).is(":checked") == true) {

    parent.find(".selectLocation").attr("disabled", true);
    parent.find("input.form-control").attr("disabled", true);
    parent.find(".locElement").each(function(i,e) {
      $(e).hide("slow");
      var id = $(e).attr("id").split('-')[1];
      if(markers[id] != undefined) {
        removeMarker(id);
      }
    })
  } else {
    $(this).val(false);
    parent.find(".selectLocation").attr("disabled", false);
    parent.find("input.form-control").attr("disabled", false);
    parent.find(".locElement").each(
        function(i,e) {
          $(e).show("slow");
          var id = $(e).attr("id").split('-')[1];
          var isList = $(e).parent().parent().parent().find(".isList");
          if(isList.val() == "false") {
            addMarker(map, id, parseFloat($(e).find(".geoLatitude").val()),
                parseFloat($(e).find(".geoLongitude").val()), $(e).find(".locElementName").val());
          }
        })
  }
  updateIndex();
}

function addLocationLevel(option) {
  var $list = $('.selectWrapper');
  var $item = $('#locationLevel-template').clone(true).removeAttr("id");
  $item.find('.locationLevel-option').html(option.html());
  var optionValue = option.val().split('-');
  var idLocationLevel = optionValue[0];
  var isList = optionValue[1];
  var name = optionValue[2];
  if(isList === "true") {
    $item.find(".selectLocation").css("display", "block");
    $item.find(".checkBox").css("display", "block");
  } else {
    $item.find(".coordinates-inputs").css("display", "block");
  }

  $item.find('.locationLevelId').val(idLocationLevel);
  $item.find('.locationLevelName').val(name);
  $item.find('.isList').val(isList);

  // Other layout
  if($list.hasClass("select-horizontal")) {
    $item.removeClass("col-md-12").addClass("locationLevel-horizontal");
    var widthSelect = ($("form .locationLevel").length) * 430;
    $(".select-horizontal").css("width", (widthSelect + 430) + "px");
    $list.prepend($item);
  } else {
    $list.append($item);
    $('.selectWrapper').animate({
      scrollTop: $('.selectWrapper').prop("scrollHeight")
    }, 500);
  }

  // LocElements options using ajax
  var select = $item.find(".selectLocation ");
  var url = baseURL + "/searchCountryListPL.do";
  var data = {
    parentId: idLocationLevel
  };
  $.ajax({
      url: url,
      type: 'GET',
      dataType: "json",
      data: data
  }).done(
      function(m) {
        console.log(m);
        select.empty();
        select.append("<option value='-1' >Select a location</option>");
        for(var i = 0; i < m.locElements.length; i++) {
          select.append("<option value='" + m.locElements[i].id + "-" + m.locElements[i].isoAlpha2 + "' >"
              + m.locElements[i].name + "</option>");
        }
      });
  $item.show('slow');
  updateIndex();
}

// Add a location by select list
function addLocationList(parent,option) {
  var latitude = "";
  var longitude = "";
  var locElementId = $(option).val().split("-")[0];
  var locElementIsoAlpha = $(option).val().split("-")[1];
  /* GET COORDINATES */
  var url = baseURL + "/geopositionByElement.do";
  var data = {
    "locElementID": locElementId
  };

  if(locElementId != "-1") {
    countID++;
    var $list = parent.find(".optionSelect-content");
    var $item = $('#location-template').clone(true).removeAttr("id");
    $.ajax({
        url: url,
        type: 'GET',
        dataType: "json",
        data: data
    }).done(function(m) {
      if(m.geopositions.length != 0) {
        console.log(m);
        latitude = m.geopositions[0].latitude;
        longitude = m.geopositions[0].longitude;
        $item.find('.geoLatitude').val(latitude);
        $item.find('.geoLongitude').val(longitude);
        addMarker(map, (countID), parseFloat(latitude), parseFloat(longitude), option.html(), "true");
      }
    });

    /** Mapping if is country * */
    var locLevelName = $(option).parents(".locationLevel").find(".locationLevelName").val();
    if(locLevelName == "Country") {
      layer.setMap(null);
      countries.push(locElementIsoAlpha); // --------------------------------------------------------------------------------------------------------------------------------------------------------
      mappingCountries();
    }
    $item.attr("id", "location-" + (countID));
    $item.find('.locationName').html(option.html());
    $item.find('.locElementId').val(locElementId);
    $item.find('.locElementName').val(option.html());
    $item.find('.locElementCountry').val(locElementIsoAlpha);
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
  if(latitude == "" && longitude == "") {
    latitude = map.getCenter().lat();
    longitude = map.getCenter().lng();
  }
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
          // ADD country into countries list
          // countries.push(countryName);
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
        // updateAllIndexes();
        $item.show('slow');
        // empty input fields
        parent.find(".latitude").val("");
        parent.find(".longitude").val("");
        parent.find(".name").val("");
        // add marker
        addMarker(map, (countID), parseFloat(latitude), parseFloat(longitude), name, "false");
        // update indexes
        updateIndex();
      }
  });

}

// Remove a location level element-Function
function removeLocationLevelItem() {
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
    calculateWidthSelect();
  });
  /*
   * if($(".selectWrapper").find(".locationLevel").length <= 1) { $(".map").hide('slow'); deleteMarkers(); }
   */
}

// Remove a location element-Function
function removeLocationItem() {
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
    updateIndex();
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
  });
  // Update component event
  $(document).trigger('updateComponent');
}

function updateLocationIndex(item,locationLevelName) {
  var name = $("#locationName").val();
  $(item).find('.locElement').each(function(indexLoc,locItem) {
    var customName = locationLevelName + '.' + name + '[' + indexLoc + ']';
    $(locItem).find('.locElementId').attr('name', customName + '.id');
    $(locItem).find('.geoLatitude').attr('name', customName + '.locGeoposition.latitude');
    $(locItem).find('.geoLongitude').attr('name', customName + '.locGeoposition.longitude');
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
        var isList = $(locItem).parent().parent().parent().find(".isList").val();
        console.log(isList);
        var site = $(locItem).find(".locElementName").val();
        var idMarker = $(locItem).attr("id").split("-")[1];
        if(latitude != "" && longitude != "" && latitude != 0 && longitude != 0) {
          addMarker(map, (idMarker), parseFloat(latitude), parseFloat(longitude), site, isList);
        }
        // ADD country into countries list
        $.ajax({
            'url': 'https://maps.googleapis.com/maps/api/geocode/json',
            'data': {
                key: GOOGLE_API_KEY,
                latlng: (latitude + "," + longitude)
            },
            success: function(data) {
              if(data.status == 'OK') {
                countryName = getResultByType(data.results[0], 'country').long_name;
                // ADD country into countries list
                // countries.push(countryName);
              } else {
                console.log(data.status);
              }
            }
        });
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
  infoWindow = new google.maps.InfoWindow();

  google.maps.event.addListener(infoWindow, 'closeclick', function() {
    $(".locations").removeClass("selected");
  });

  google.maps.event.addListener(map, 'click', function(event) {
    infoWindow.close();
    $(".locations").removeClass("selected");
  });

  google.maps.event.addListener(map, 'rightclick', function(e) {
    openInfoWindowForm(e);
  });

  if(markers.length > 0) {
    map.setCenter(markers[markers.length - 1].getPosition());
    // console.log(markers[markers.length - 1].getPosition());
  }

  mappingCountries();

}

// Map events

function addMarker(map,idMarker,latitude,longitude,sites,isList) {
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
  markers[idMarker] = marker;
  // console.log(markers);
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
  infoWindow.setPosition(e.latLng);
  // Init select2
  $("select").select2();
  if($("select").hasClass("select2-hidden-accessible")) {
    $("select").select2('destroy');
    $('select').select2({
      width: '100%'
    });
    $("select").next().next().remove();
  }
  // Set latLng
  $("#inputFormWrapper").find("input.latitude").val(e.latLng.lat());
  $("#inputFormWrapper").find("input.longitude").val(e.latLng.lng());

  // Events
  formWindowEvents();

}

function formWindowEvents() {
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
            var select = $(".selectList select");
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
                  console.log(m);
                  select.empty();
                  for(var i = 0; i < m.locElements.length; i++) {
                    select.append("<option value='" + m.locElements[i].id + "-" + m.locElements[i].isoAlpha2 + "-"
                        + m.locElements[i].name + "' >" + m.locElements[i].name + "</option>");
                  }
                });
            $("#inputFormWrapper").slideUp();
            $(".yesnoQuestion").slideDown();
          } else {
            $(".yesnoQuestion").slideUp();
            $(".selectLocations").hide();
            $("#inputFormWrapper").slideDown();
            $(".no-button-label").removeClass("radio-checked");
            $(".yes-button-label").addClass("radio-checked");
          }
        }
      });

  // Yes-no button
  $(".no-button-label").on("click", function() {
    $(".yes-button-label").removeClass("radio-checked");
    $(this).addClass("radio-checked");
    $(".selectLocations").slideDown("slow");
  });
  $(".yes-button-label").on("click", function() {
    $(".no-button-label").removeClass("radio-checked");
    $(this).addClass("radio-checked");
    $(".selectLocations").slideUp("slow");
  });

  // Add location button
  $("#addLocationButton").on(
      "click",
      function(e) {
        console.log($(".selectList select").val());
        var $locationLevelSelect = $("#locLevelSelect");
        var locationId = $locationLevelSelect.val().split("-")[0];
        var locationIsList = $locationLevelSelect.val().split("-")[1];
        var locationName = $locationLevelSelect.val().split("-")[2];
        // checking if is list
        if(locationIsList == "true") {
          var $locationSelect = $(".selectList select");
          // Checking if locations select is empty
          if($locationSelect.val() != null) {
            // Checking if the location level exist in the bottom wrapper
            if($(".selectWrapper").find("input.locationLevelId[value='" + locationId + "']").exists()) {
              var locationContent =
                  $(".selectWrapper").find("input.locationLevelId[value='" + locationId + "']").parent().find(
                      ".optionSelect-content");
              console.log(locationContent);
              $.each($locationSelect.val(), function(i,e) {
                var $item = $("#location-template").clone(true).removeAttr("id");
                var locId = e.split("-")[0];
                var locIso = e.split("-")[1];
                var locName = e.split("-")[2];
                $item.find(".lName").html(locName);
                $item.find(".locElementName").val(locName);
                $item.find(".locElementId").val(locId);
                $item.find(".locElementCountry").val(locIso);
                locationContent.append($item);
                $item.show("slow");
                countries.push(locIso);
              });
              infoWindow.close();
              layer.setMap(null);
              mappingCountries();
            } else {
              console.log("no existe");
            }
          }
        } else {

        }
      });
}

function resetInfoWindow() {
  $(".yesnoQuestion").hide();
  $(".selectLocations").hide();
  $("#inputFormWrapper").hide();
  $(".no-button-label").removeClass("radio-checked");
  $(".yes-button-label").addClass("radio-checked");
}

// Open info window for change the country name
function openInfoWindow(marker) {
  var content;
  if(editable && marker.list == "false") {
    content =
        '<div id="infoContent"><label for="nameMapMarker">Change the location name:</label><input placeholder="'
            + marker.name
            + '" id="nameMapMarker" class="nameMap form-control" type="text" /><span class="editLocationName glyphicon glyphicon-ok button-green"></span></div>';
  } else {
    content = '<div id="infoContent"><div class=" form-control">' + marker.name + '</div></div>';
  }
  var markerLatLng = marker.getPosition();
  infoWindow.setContent([
    content
  ].join(''));
  infoWindow.open(map, marker);

  // Edit location name from map
  $(".editLocationName").on('click', function editLocationName() {
    var parent = $(this).parent();
    var newName = parent.find(".nameMap").val();
    var location = parent.parents().find("#location-" + marker.id);
    var markerLatLng = marker.getPosition();

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
                fillOpacity: 0.35
            }
          }
        ]
    };
    console.log(FT_Options);
    layer = new google.maps.FusionTablesLayer(FT_Options);
    layer.setMap(map);
  }

}
