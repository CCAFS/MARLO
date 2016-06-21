$(document).ready(init);

function init() {

  /* Declaring Events */
  attachEvents();

  /* Init Select2 plugin */
  $('select').select2({
    templateResult: formatState
  });

  /* Override function from userManagement.js */
  addUser = addUserItem;

}

function formatState(state) {
  if(!state.id) {
    return state.text;
  }
  var $state =
      $('<span><i class="flag-sm flag-sm-' + state.element.value.toUpperCase() + '"></i> ' + state.text + '</span>');
  return $state;
};

function attachEvents() {

  var select = $("#ccafs_siteIntegration_");
  select.on('change', function() {
    countrySelected = select.find("option:selected");
    if(countrySelected.val() != -1 && countrySelected.val() != null) {
      console.log(countrySelected);
    }
  });

  // Remove an item
  $('.glyphicon-remove').on('click', function() {
    var $parent = $(this).parent();
    var $block = $parent.parent().parent();
    $parent.hide(function() {
      $parent.remove();
      checkItems($block);
      updateUsersIndex($block, $block.parent().find('.inputName-input').text());
    });
  });
}

function addCountry() {
  var $item = $('#country-template').clone(true).removeAttr('id');
  $item.find('input.institutionId').val(partner.val());
  $item.find('.title').html(partner.text());
  partnerContent.append($item);
  $item.show("slow");
  updateIndex();
}

function addUserItem(composedName,userId) {
  $usersList = $elementSelected.parent().find(".items-list");
  var $li = $("#user-template").clone(true).removeAttr("id");
  $li.find('.name').html(escapeHtml(composedName));
  $li.find('.user').val(userId);
  $usersList.find("ul").append($li);
  $li.show('slow');
  checkItems($usersList);
  updateUsersIndex($usersList, $elementSelected.find('.inputName-input').text());
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

function updateUsersIndex(list,name) {
  $(list).find('li').each(function(i,item) {
    var customName = name + '[' + i + ']';
    $(item).find('.user').attr('name', customName + '.user.id');
    $(item).find('.role').attr('name', customName + '.role.id');
    $(item).find('.id').attr('name', customName + '.id');
  });
}
