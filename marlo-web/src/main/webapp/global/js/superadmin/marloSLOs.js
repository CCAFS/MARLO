$(document).ready(init);
var select;
var countriesContent = $(".countriesContent");

function init() {
  attachEvents();
  select = $(".countriesList");
  $('.removeCountry').on('click', removeCountry);

  $('select').select2({
    templateResult: formatState
  });

  addUser = addUserItem;
}

function formatState(state) {
  if(!state.id) {
    return state.text;
  }
  var $state =
      $('<span><i class="flag-icon flag-icon-' + state.element.value.toLowerCase() + '"></i> ' + state.text + '</span>');
  return $state;
}

function attachEvents() {
  select = $(".countriesList ");
  select.on('change', function() {
    countrySelected = select.find("option:selected");
    if(countrySelected.val() != -1 && countrySelected.val() != null) {
      differences();
    }
  });

  $('.glyphicon-remove').on('click', function() {
    var $parent = $(this).parent();
    var $block = $parent.parent().parent();
    $parent.hide(function() {
      $parent.remove();
      checkItems($block);
      updateCountriesIndex();
    });
  });
}

function addCountry(countrySelected) {
  var $item = $('#country-template').clone(true).removeAttr('id');
  $item.find('input.isoAlpha2').val(countrySelected.val());
  $item.find('.country-title').html("<i></i> " + countrySelected.text());
  $item.find('i').attr('class', 'flag-icon flag-icon-' + countrySelected.val().toLowerCase());
  countriesContent.append($item);
  $item.show("slow");
  updateCountriesIndex();
}

function removeCountry() {
  var $item = $(this).parents('.country');
  $item.hide(1000, function() {
    $item.remove();
    updateCountriesIndex();
  });
}

function differences() {
  countrySelected = select.find("option:selected");
  if(countriesContent.find('input[value=' + countrySelected.val() + ']').exists()) {
    var notyOptions = jQuery.extend({}, notyDefaultOptions);
    notyOptions.text = 'This country has been added';
    notyOptions.type = 'alert';
    noty(notyOptions);
  } else {
    addCountry(countrySelected);
  }
}

function updateCountriesIndex() {
  var name = "loggedCrp.siteIntegrations";
  $(".countriesContent").find('.country').each(function(i,item) {
    var customName = name + '[' + i + ']';
    $(item).attr('id', 'country-' + i);
    $(item).find('input.id').attr('name', customName + '.id');
    $(item).find('input.isoAlpha2').attr('name', customName + '.locElement.isoAlpha2');
    updateUsersIndex(item, customName);
  });
}

function addUserItem(composedName,userId) {
  $usersList = $elementSelected.parent().parent().find(".items-list");
  var $li = $("#user-template").clone(true).removeAttr("id");
  $li.find('.name').html(escapeHtml(composedName));
  $li.find('.user').val(userId);
  $usersList.find("ul").append($li);
  $li.show('slow');
  checkItems($usersList);
  updateCountriesIndex();
  dialog.dialog("close");
}

function checkItems(block) {
  var items = $(block).find('li').length;
  if(items == 0) {
    $(block).find('p').fadeIn();
  } else {
    $(block).find('p').fadeOut();
  }
}

function updateUsersIndex(item,name) {
  $(item).find('li').each(function(indexUser,userItem) {
    var customName = name + '.siteLeaders[' + indexUser + ']';
    $(userItem).find('.user').attr('name', customName + '.user.id');
    $(userItem).find('.id').attr('name', customName + '.id');
  });
}
