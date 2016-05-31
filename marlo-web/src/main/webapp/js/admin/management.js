$(document).ready(init);

function init() {

  /* Declaring Events */
  attachEvents();

  /* Override function from userManagement.js */
  addUser = addUserItem;

}

function attachEvents() {

  // Remove an item
  $('.glyphicon-remove').on('click', function() {
    var $parent = $(this).parent();
    var $block = $parent.parent().parent();
    $parent.hide(function() {
      $parent.remove();
      checkItems($block);
      updateUsersIndex($block);
      updateProgramIndex($block)
    });
  });

  $('.addProgram').on('click', function() {
    addProgram($(this));
  });
}

function addUserItem(composedName,userId) {
  var $usersList = $(".users.items-list");
  var $li = $("#user-template").clone(true).removeAttr("id");
  $li.find('.name').html(escapeHtml(composedName));
  $li.find('.id').val(userId);
  $usersList.find("ul").append($li);
  $li.show('slow');
  checkItems($usersList);
  updateUsersIndex($usersList);
  dialog.dialog("close");
}

function addProgram(element) {
  var $parent = $(element).parents('.program-block')
  var $programList = $parent.find(".items-list");
  var $li = $("#program-template").clone(true).removeAttr("id");
  var item = {
      acronym: $parent.find('.acronym-input').val(),
      name: $parent.find('.name-input').val(),
      type: $parent.find('.type-input').html(),
      composedName: function() {
        return this.acronym + ' - ' + this.name;
      }
  }
  console.log(item);
  $li.find('.composedName').html(item.composedName());
  $li.find('.acronym').val(item.acronym);
  $li.find('.name').val(item.name);
  $li.find('.type').val(item.type);
  $programList.find("ul").append($li);
  $li.show('slow');

  $parent.find('input:text').val('');
  checkItems($programList);
  updateProgramIndex($programList)
}

function checkItems(block) {
  var items = $(block).find('li').length;
  if(items == 0) {
    $(block).find('p').fadeIn();
  } else {
    $(block).find('p').fadeOut();
  }
}

function updateUsersIndex(list) {
  $(list).find('li').each(function(i,item) {
    var customName = 'programManagmentTeam[' + i + ']';
    $(item).find('.id').attr('name', customName + '.id');
  });
}

function updateProgramIndex(list) {
  $(list).find('li').each(function(i,item) {
    var customName = 'programs[' + i + ']';
    $(item).find('.acronym').attr('name', customName + '.acronym');
    $(item).find('.name').attr('name', customName + '.name');
    $(item).find('.type').attr('name', customName + '.type');
    $(item).find('.id').attr('name', customName + '.id');
  });
}