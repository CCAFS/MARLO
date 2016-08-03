$(document).ready(init);

function init() {
  console.log($(".selectWrapper").find(".locationLevel").length);

  if($(".selectWrapper").find(".locationLevel").length > 0) {
    $(".map").show();
    loadScript();
  } else {
    $(".map").hide();
  }

  /* Declaring Events */
  attachEvents();
}

function attachEvents() {

  $('.selectLocationLevel').on('change', function() {

    if($(".selectWrapper").find(".locationLevel").length <= 0) {
      loadScript();
      $(".map").show();
    }
    var option = $(this).find("option:selected");
    addLocationLevel(option.html());
  });

  $('.selectLocation').on('change', function() {
    var option = $(this).find("option:selected");
    addLocation($(this).parent(), option.html());
  });

  // Remove a location level element-Event
  $(".removeLocationLevel").on("click", removeLocationLevelItem);

// Remove a location element-Event
  $(".removeLocation").on("click", removeLocationItem);

  // Collapsible
  $('.locationLevel-option').on('click', function() {
    var content = $(this).parent().parent().find('.locationLevel-optionContent');
    if($(this).hasClass('closed')) {
      content.slideDown();
      $(this).removeClass('closed').addClass('opened');
    } else {
      $(this).removeClass('opened').addClass('closed');
      content.slideUp();
    }
  });
}

// FUNCTIONS

function addLocationLevel(option) {
  var $list = $('.selectWrapper');
  var $item = $('#locationLevel-template').clone(true).removeAttr("id");
  $item.attr("id", "holi");
  $item.find('.locationLevel-option').html(option);
  $list.append($item);
  // updateAllIndexes();
  $item.show('slow');
}

function addLocation(parent,option) {
  var $list = parent.find(".optionSelect-content");
  var $item = $('#location-template').clone(true).removeAttr("id");
  $item.attr("id", "holi2");
  $item.find('.locationName').html(option);
  $list.append($item);
  // updateAllIndexes();
  $item.show('slow');
}

// Remove a location level element-Function
function removeLocationLevelItem() {
  var $item = $(this).parents('.locationLevel');
  console.log($item);
  $item.hide(function() {
    $item.remove();
  });
  console.log($(".selectWrapper").find(".locationLevel").length);
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
  });
}

function loadScript() {
  var script = document.createElement("script");
  script.src = "https://maps.googleapis.com/maps/api/js?key=" + GOOGLE_API_KEY + "&callback=initMap";
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
  var map = new google.maps.Map(mapDiv, {
      center: new google.maps.LatLng(14.41, -12.52),
      zoom: 2,
      mapTypeId: 'roadmap',
      styles: style
  });
}
