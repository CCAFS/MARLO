$(document).ready(init);

function init() {

  /* Declaring Events */
  attachEvents();

  /* Overwritten event from global.js */
  yesnoEvent = yesnoEventLocations;

  /* Numeric Inputs */
  $('.locationLatitude-input, .locationLongitude-input').numericInput();

  /* Init Select2 plugin */
  $('form select').select2({
      width: "100%",
      templateResult: formatState
  });

  /* Coordinates verification */
  $('.locationLatitude-input, .locationLongitude-input').on("keyup", function(e) {
    var $parent = $(this).parent().parent().parent();
    var lat = $parent.find('.locationLatitude-input').val();
    var lng = $parent.find('.locationLongitude-input').val();

    if(isCoordinateValid(lat, lng)) {
      $parent.find('.locationLatitude-input, .locationLongitude-input').removeClass('fieldError');
    } else {
      $parent.find('.locationLatitude-input, .locationLongitude-input').addClass('fieldError');
    }
  });
}

function attachEvents() {

  // Location Levels events
  $('.addLocationLevel').on('click', addLocationLevel);
  $('.removeLocationLevel').on('click', removeLocationLevel);

  // Location Elements events
  $('.addLocElement').on('click', addLocElement);
  $(".countriesList ").on('change', addLocElementCountry);
  $('.removeLocElement').on('click', removeLocElement);

  // Switch coordinates
  $('.yes-button-label').on('click', function() {
    yesnoEventLocations(true, $(this));
  });
  $('.no-button-label').on('click', function() {
    yesnoEventLocations(false, $(this));
  });
  
  // Default locations levels
  $('.elementTypeCheck').on('change', function(){
    $(this).parents('.defaultLocation').find('input:checkbox').val($(this).is(":checked"));
  });

}

/**
 * Location Levels Functions
 */

function addLocationLevel() {
  var $item, $list;
  if($(this).classParam('type') == "location") {
    $item = $('#locationLevel-location-template').clone(true).removeAttr('id');
    $list = $(".locations-list");
  } else {
    $item = $('#locationLevel-scope-template').clone(true).removeAttr('id');
    $list = $(".scopes-list");
  }

  $item.find('select').select2({
      width: "100%",
      templateResult: formatState
  });

  $list.append($item);
  updateLocationsIndexes();
  $item.show('slow');
}

function removeLocationLevel() {
  var $parent = $(this).parent();
  $parent.hide('slow', function() {
    $parent.remove();
    updateLocationsIndexes();
  });
}

/**
 * Location Element Funtions
 */

function addLocElementCountry() {
  var countrySelected = $(this).find("option:selected");
  var contryISO = countrySelected.val();
  var countryName = countrySelected.text();
  if(contryISO == "-1"){
    return
  }
  
  var $item = $('#locElement-scope-template').clone(true).removeAttr('id');
  var $list = $(this).parents('.locationLevel').find(".items-list ul");
  
  
  // Fill item values
  $item.find('span.name').text(countryName);
  $item.find('span.coordinates').text("");
  $item.find('input.locElementName').val(countryName);
  $item.find('input.locElementCountry').val(contryISO);
  
  // Add Flag
  var $flag = $item.find('.flag-icon');
  var flag = '<i class="flag-sm flag-sm-' + contryISO.toUpperCase() + '"></i>';
  $flag.html(flag);
  // Remove coordinates span
  $item.find('.coordinates').remove();
  // Adding item to the list
  $list.append($item);
  // Update Locations Indexes
  updateLocationsIndexes();
  // Show item
  $item.show('slow');
  // Remove message
  $list.parent().find('p.message').hide();
  
  // Reset select
  $(this).val('-1');
  $(this).trigger('select2:change');
}

function addLocElement() {
  var $item = $('#locElement-location-template').clone(true).removeAttr('id');
  var $list = $(this).parents('.locationLevel').find(".items-list ul");
  var $inputs = $(this).parent().parent();
  var itemObject = {
      lat: $inputs.find('.locationLatitude-input').val(),
      lng: $inputs.find('.locationLongitude-input').val(),
      name: $inputs.find('.locationName-input').val(),
      countryName: '',
      latlng: function() {
        return this.lat + "," + this.lng
      },
      composedLatLng: function() {
        return "(" + this.lat + ", " + this.lng + ")"
      }
  }
  if((itemObject.lat && itemObject.lng && itemObject.name) == "") {
    var notyOptions = jQuery.extend({}, notyDefaultOptions);
    notyOptions.text = 'You must fill all fields for adding a new location.';
    noty(notyOptions);
  } else {
    // Getting country name by Google GeoCoding REST API
    $.ajax({
        'url': 'https://maps.googleapis.com/maps/api/geocode/json',
        'data': {
            key: GOOGLE_API_KEY,
            latlng: itemObject.latlng()
        },
        success: function(data) {
          if(data.status == 'OK') {
            $item.find('input.locElementCountry').val(getResultByType(data.results[0], 'country').short_name);
            itemObject.countryName = getResultByType(data.results[0], 'country').long_name;
          } else {
            console.log(data.status);
          }
        },
        complete: function(data) {
          // Fill item values
          $item.find('span.name').text(itemObject.name);
          $item.find('span.coordinates').text(itemObject.countryName + " " + itemObject.composedLatLng());
          $item.find('input.locElementName').val(itemObject.name);
          $item.find('input.geoLat').val(itemObject.lat);
          $item.find('input.geoLng').val(itemObject.lng);
          // Adding item to the list
          $list.append($item);
          // Update Locations Indexes
          updateLocationsIndexes();
          // Show item
          $item.show('slow');
          // Remove message
          $list.parent().find('p.message').hide();
          // Reset Inputs
          $inputs.find('input:text').val('');
        }
    });

  }

}

function removeLocElement() {
  var $parent = $(this).parent();
  $parent.hide('slow', function() {
    $parent.remove();
    updateLocationsIndexes();
  });
}

/**
 * General Functions
 */

function updateLocationsIndexes() {
  $(".locations-list").find('.locationLevel').each(updateLocLevelIndexes);
  $(".scopes-list").find('.locationLevel').each(updateLocLevelIndexes);
}

function updateLocLevelIndexes(index,location){
  $(location).find('> .leftHead .index').text(index + 1);
  $(location).setNameIndexes(1, index);

  // Updating radio buttons label attributes
  var uniqueId = "hasCoordinates-" + index;
  // Yes Button
  $(location).find('input.yes-button-input').attr('id', "yes-button-" + uniqueId);
  $(location).find('label.yes-button-label').attr('for', "yes-button-" + uniqueId);
  // No Button
  $(location).find('input.no-button-input').attr('id', "no-button-" + uniqueId);
  $(location).find('label.no-button-label').attr('for', "no-button-" + uniqueId);
  
  // Loc Element
  $(location).find('.locElement').each(function(i,element) {
    $(element).setNameIndexes(2, i);
  });
}

function yesnoEventLocations(value,item) {
  $t = item.parent().find('input.onoffswitch-radio');
  var array = $t.attr('name').split('.');
  var $aditional = $t.parents('.locationLevel').find('.aditional-' + array[array.length - 1]);
  // AHORA
  if(value == true) {
    item.siblings().removeClass('radio-checked');
    item.addClass('radio-checked');
    $aditional.slideDown("slow");
    $t.val(value);
  } else {
    var locElements = $t.parents('.locationLevel').find('.locElement').length;
    if(locElements > 0) {
      noty({
          text: 'If you want to proceed with this action, '
              + locElements
              + ' locations  will be removed from the system. Please be aware you may have projects associated to these locations.',
          type: 'confirm',
          dismissQueue: true,
          layout: 'center',
          theme: 'relax',
          modal: true,
          buttons: [
              {
                  addClass: 'btn btn-primary',
                  text: 'Proceed',
                  onClick: function($noty) {
                    item.siblings().removeClass('radio-checked');
                    item.addClass('radio-checked');
                    $aditional.slideUp("slow");
                    $t.val(value);
                    $noty.close();
                  }
              }, {
                  addClass: 'btn btn-danger',
                  text: 'Cancel',
                  onClick: function($noty) {
                    $noty.close();
                  }
              }
          ]
      });

    } else {
      item.siblings().removeClass('radio-checked');
      item.addClass('radio-checked');
      $aditional.slideUp("slow");
      $t.val(value);
    }
  }
}

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

/**
 * Countries List Format
 */

function formatState(state) {
  if(!state.id) {
    return state.text;
  }
  var $state =
      $('<span><i class="flag-sm flag-sm-' + state.element.value.toUpperCase() + '"></i> ' + state.text + '</span>');
  return $state;
};