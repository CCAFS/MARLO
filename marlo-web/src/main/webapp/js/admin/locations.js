$(document).ready(init);

function init() {

  /* Declaring Events */
  attachEvents();

  /* Overwritten event from global.js */
  yesnoEvent = yesnoEventLocations;
}

function attachEvents() {

  // Location Levels events
  $('.addLocationLevel').on('click', addLocationLevel);
  $('.removeLocationLevel').on('click', removeLocationLevel);

  // Location Elements events

}

/**
 * Location Levels events
 */

function addLocationLevel() {
  var $item = $('#locationLevel-template').clone(true).removeAttr('id');
  var $list = $(".locations-list");
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
 * General Functions
 */

function updateLocationsIndexes() {
  $(".locations-list").find('.locationLevel').each(function(i,location) {
    var locationName = "locationsLevels" + "[" + i + "].";
    $(location).find('> .leftHead .index').text(i + 1);
    $(location).find('.locationLevelId').attr('name', locationName + "id");
    $(location).find('.locationName').attr('name', locationName + "name");
    $(location).find('input.onoffswitch-radio').attr('name', locationName + "hasCoordinates");

    // Updating radio buttons label attributes
    var uniqueId = "hasCoordinates-" + i;
    // Yes Button
    $(location).find('input.yes-button-input').attr('id', "yes-button-" + uniqueId);
    $(location).find('label.yes-button-label').attr('for', "yes-button-" + uniqueId);
    // No Button
    $(location).find('input.no-button-input').attr('id', "no-button-" + uniqueId);
    $(location).find('label.no-button-label').attr('for', "no-button-" + uniqueId);

    $(location).find('.locElement').each(function(i,element) {
      var elementName = locationName + "locElements" + "[" + i + "].";

      $(element).find('.locationName').attr('name', elementName + "name");

    });
  });
}

function yesnoEventLocations(target) {
  $t = $(target);
  var isChecked = ($t.val() === "true");
  $t.siblings().removeClass('radio-checked');
  $t.next().addClass('radio-checked');
  var array = $t.attr('name').split('.');
  var $aditional = $t.parents('.locationLevel').find('.aditional-' + array[array.length - 1]);
  if($t.hasClass('inverse')) {
    isChecked = !isChecked;
  }
  if(isChecked) {
    $aditional.slideDown("slow");
  } else {
    $aditional.slideUp("slow");
    $aditional.find('input:text,textarea').val('');
  }
}
