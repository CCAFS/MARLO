$(document).ready(init);

function init() {

  /* Declaring Events */
  attachEvents();

  /* Select2 */
  $('.program-block').find('.countriesSelect').select2({
      placeholder: "Select a country",
      templateResult: formatState,
      templateSelection: formatState,
      width: '100%'
  });

  /* Color picker widget */
  $('form .color-picker').colorPicker();

}

function attachEvents() {

  // Add User - Override function from userManagement.js
  addUser = addUserItem;

  // Remove an user item
  $('.remove-userItem').on('click', function() {
    var $parent = $(this).parent();
    var $block = $parent.parent().parent();
    var item = {
        type: $parent.parents('.usersBlock').find('.usersType').text(),
        role: $parent.parents('.usersBlock').find('.usersRole').text()
    }

    $parent.hide(function() {
      $parent.remove();
      checkItems($block, 'usersMessage');
      if(item.type == 'programUser') {
        updateProgramIndexes($block.parents('.items-list'));
      }
    });
  });

  // Add program
  $('.addProgram').on('click', function() {
    addProgram($(this));
  });

  // Remove an program item
  $('.remove-programItem').on('click', function() {
    var $parent = $(this).parent();
    var $block = $parent.parent().parent();
    $parent.hide(function() {
      $parent.remove();
      checkItems($block, 'programMessage');
      updateProgramIndexes($block);

    });
  });

}

function addUserItem(composedName,userId) {
  $usersList = $elementSelected.parent().parent().find(".items-list");
  var $li = $("#user-template").clone(true).removeAttr("id");
  var item = {
      name: escapeHtml(composedName),
      id: userId,
      type: $elementSelected.parents('.usersBlock').find('.usersType').text(),
      role: $elementSelected.parents('.usersBlock').find('.usersRole').text()
  }
  $li.find('.name').html(item.name);
  $li.find('.user').val(item.id);
  $li.find('.role').val(item.role);

  $usersList.find("ul").append($li);
  $li.show('slow');
  checkItems($usersList, 'usersMessage');

  if(item.type == 'programUser') {
    updateProgramIndexes($usersList.parents('.items-list'));
  }
  dialog.dialog("close");
}

function addProgram(element) {
  var $parent = $(element).parents('.program-block')
  var $programList = $parent.find(".items-list");
  // Getting item parameters
  var item = {
      type: $parent.find('.type-input').html(),
      inputName: $parent.find('.inputName-input').html(),
  }
  // Create a program item from a template
  var $li = $("#program-template").clone(true).removeAttr("id");
  // Assign parameters to template created
  $li.find('.type').val(item.type);
  $li.find('.countriesSelect').select2({
      placeholder: "Select a country",
      templateResult: formatState,
      templateSelection: formatState,
      width: '100%'
  });
  // Append item into program list
  $programList.find(".regions-list").append($li);
  // Show item
  $li.show('slow');
  // Set color picker
  $li.find('.color-picker').colorPicker();
  // Check program items
  checkItems($programList, 'programMessage');
  // Update program index
  updateProgramIndexes($programList);

}

function formatState(state) {
  if(!state.id) {
    return state.text;
  }
  var $state =
      $('<span> <i class="flag-sm flag-sm-' + state.element.value.toUpperCase() + '"></i>  ' + state.text + '</span>');
  return $state;
};

function checkItems(block,target) {
  var items = $(block).find('> ul li').length;
  if(items == 0) {
    $(block).find('p.' + target).fadeIn();
  } else {
    $(block).find('p.' + target).fadeOut();
  }
}

function updateProgramIndexes(list) {
  $(list).find('.program').each(function(i,item) {
    var programName = 'regionsPrograms' + '[' + i + '].';
    $(item).find('.acronym-input').attr('name', programName + 'acronym');
    $(item).find('.name-input').attr('name', programName + 'name');
    $(item).find('.color-picker input').attr('name', programName + 'color');
    $(item).find('.type').attr('name', programName + 'programType');
    $(item).find('.id').attr('name', programName + 'id');
    $(item).find('.countriesSelect').attr('name', programName + 'selectedCountries');

    // Program Leaders
    $(item).find('.usersBlock li').each(function(i,leader) {
      var leaderName = programName + 'leaders[' + i + '].';
      updateUserItemIndex(leader, leaderName);
    });
  });
}

function updateUserItemIndex(element,name) {
  $(element).find('.user').attr('name', name + 'user.id');
  $(element).find('.role').attr('name', name + 'role.id');
  $(element).find('.id').attr('name', name + 'id');
}