$(document).ready(init);

function init() {

  $(".selectLocationLevel").select2({
    placeholder: "Select a location level"
  })

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

// ADD a location level element-Event
  $('.selectLocationLevel').on('change', function() {

    if($(".selectWrapper").find(".locationLevel").length <= 0) {
      loadScript();
      $(".map").show("slow");
    }
    var option = $(this).find("option:selected");
    if($(".selectWrapper").find("input[value=" + option.val().split("-")[0] + "]").exists()) {
      console.log("exists");
    } else {
      addLocationLevel(option);
    }

  });

// ADD a location element-Event
  $('.selectLocation').on('change', function() {
    var option = $(this).find("option:selected");
    addLocation($(this).parent(), option);
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
  var typeLocationLevel = optionValue[1];
  $item.find('.locationLevelId').val(idLocationLevel);
  $item.find('.locationLevelType').val(typeLocationLevel);
  $list.append($item);
  // updateAllIndexes();
  $item.show('slow');
  updateIndex();
}

function addLocation(parent,option) {
  var $list = parent.find(".optionSelect-content");
  var $item = $('#location-template').clone(true).removeAttr("id");
  $item.find('.locationName').html(option.html());
  $item.find('.locationId').val(option.val());
  $list.append($item);
  // updateAllIndexes();
  $item.show('slow');
  updateIndex();
}

// Remove a location level element-Function
function removeLocationLevelItem() {
  var $item = $(this).parents('.locationLevel');
  console.log($item);
  $item.hide(function() {
    $item.remove();
    updateIndex();
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
    updateIndex();
  });

}

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
    $(locItem).find('.locationId').attr('name', customName + '.id');
  });

  // Update component event
  $(document).trigger('updateComponent');
}

// Load script of google services
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
