$(document).ready(init);

function init() {

  /* Declaring Events */
  attachEvents();

}

function attachEvents() {

  // Remove an user item
  $('.remove-userItem').on('click', function() {
    var $parent = $(this).parent();
    var $block = $parent.parent().parent();
    $parent.hide(function() {
      $parent.remove();
      checkItems($block);
      updateUsersIndex($block);
    });
  });

  // Remove an program item
  $('.remove-programItem').on('click', function() {
    var $parent = $(this).parent();
    var $block = $parent.parent().parent();
    $parent.hide(function() {
      $parent.remove();
      checkItems($block);
      updateProgramIndex($block, $block.parent().find('.inputName-input').html())
    });
  });

  /* Add User - Override function from userManagement.js */
  addUser = addUserItem;

  $('.addProgram').on('click', function() {
    addProgram($(this));
  });
}

function addUserItem(composedName,userId) {
  $usersList = $elementSelected.parent().find(".items-list");
  var $li = $("#user-template").clone(true).removeAttr("id");
  $li.find('.name').html(escapeHtml(composedName));
  $li.find('.user').val(userId);
  $usersList.find("ul").append($li);
  $li.show('slow');
  checkItems($usersList);
  updateUsersIndex($usersList);
  dialog.dialog("close");
}

function addProgram(element) {
  var $parent = $(element).parents('.program-block')
  var $programList = $parent.find(".items-list");
  // Getting item parameters
  var item = {
      acronym: $.trim($parent.find('.acronym-input').val()),
      name: $.trim($parent.find('.name-input').val()),
      type: $parent.find('.type-input').html(),
      inputName: $parent.find('.inputName-input').html(),
      composedName: function() {
        return this.acronym + ' - ' + this.name;
      }
  }
  if((item.acronym == '') || (item.name == '')) {
    var notyOptions = jQuery.extend({}, notyDefaultOptions);
    notyOptions.text = 'Acronym and name are required';
    noty(notyOptions);
  } else {
    // Create a program item from a template
    var $li = $("#program-template").clone(true).removeAttr("id");
    // Assign parameters to template created
    $li.find('.composedName').html(item.composedName());
    $li.find('.acronym').val(item.acronym);
    $li.find('.name').val(item.name);
    $li.find('.type').val(item.type);
    // Append item into program list
    $programList.find("ul").append($li);
    // Show item
    $li.show('slow');
    // Reset program form
    $parent.find('input:text').val('');
    // Check program items
    checkItems($programList);
    // Update program index
    updateProgramIndex($programList, item.inputName);
  }
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
    var customName = 'loggedCrp.programManagmenTeam[' + i + ']';
    $(item).find('.user').attr('name', customName + '.user.id');
    $(item).find('.role').attr('name', customName + '.role.id');
    $(item).find('.id').attr('name', customName + '.id');
  });
}

function updateProgramIndex(list,name) {
  $(list).find('li').each(function(i,item) {
    var customName = name + '[' + i + ']';
    $(item).find('.acronym').attr('name', customName + '.acronym');
    $(item).find('.name').attr('name', customName + '.name');
    $(item).find('.type').attr('name', customName + '.programType');
    $(item).find('.id').attr('name', customName + '.id');
  });
}