$(document).ready(init);

function init() {

  /* Declaring Events */
  attachEvents();

  /* Overwritten event from global.js */
  yesnoEvent = yesnoEventLocations;

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
      if(item.type == 'crpUser') {
        updateProgramManagementTeamIndexes($block);
      }
      if(item.type == 'programUser') {
        updateProgramIndexes($block.parents('.items-list'));
      }
    });
  });

  // Key up Acronym
  $('input.acronym').on('keyup', function() {
    var currentValue = this.value;
    var matches = 0;
    $(this).removeClass('fieldError');
    $('form input.acronym').each(function(i,e) {
      if(currentValue == e.value) {
        matches++
      }
    });
    if(matches > 1) {
      $(this).addClass('fieldError');
      var notyOptions = jQuery.extend({}, notyDefaultOptions);
      notyOptions.text = 'Abbreviations/Acronyms cannot be repeated';
      noty(notyOptions);
    }
  });

  // Add program
  $('.addProgram').on('click', function() {
    addProgram($(this));
  });

  // Remove an program item
  $('.remove-activityItem2').on('click', function() {
    var $parent = $(this).parent();
    var $block = $parent.parent().parent();
    var leaders = $parent.find('.userItem').length;
    var removeThis = function() {
      $parent.hide(function() {
        $parent.remove();
        checkItems($block, 'programMessage');
        updateProgramIndexes($block);
      });
    }

    // Alert PopUp
    noty({
        text: 'Are you sure you want to delete this activity?',
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
                  removeThis();
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

  });

// switch coordinates
  $('.yes-button-label').on('click', function() {
    yesnoEventLocations(true, $(this));
  });
  $('.no-button-label').on('click', function() {
    yesnoEventLocations(false, $(this));
  });

}

function addUserItem(composedName,userId) {
  var $usersList = $elementSelected.parent().parent().find(".items-list");
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
  if(item.type == 'crpUser') {
    updateProgramManagementTeamIndexes($usersList);
  }
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
  // Append item into program list
  $programList.find(".flagships-list").append($li);
  // Show item
  $li.show('slow');
  // Check program items
  checkItems($programList, 'programMessage');
  // Update program index
  updateProgramIndexes($programList);

}

function checkItems(block,target) {
  var items = $(block).find('> ul li').length;
  if(items == 0) {
    $(block).find('p.' + target).fadeIn();
  } else {
    $(block).find('p.' + target).fadeOut();
  }
}

function updateProgramManagementTeamIndexes(list) {
  $(list).find('li').each(function(i,item) {
    var customName = 'loggedCrp.programManagmenTeam[' + i + '].';
    updateUserItemIndex(item, customName);
  });
}

function updateProgramIndexes(list) {
  $(list).find('.program').each(function(index,item) {
    var programName = 'activities' + '[' + index + '].';

    $(item).find('.index').text(index + 1);

    $(item).find('.title').attr('title', programName + 'title');
    $(item).find('.id').attr('title', programName + 'id');

  });
}

function updateUserItemIndex(element,name) {
  $(element).find('input.user').attr('name', name + 'user.id');
  $(element).find('input.role').attr('name', name + 'role.id');
  $(element).find('input.id').attr('name', name + 'id');
}

function yesnoEventLocations(value,item) {
  var $t = item.parent().find('input.onoffswitch-radio');
  // AHORA
  if(value == true) {
    item.siblings().removeClass('radio-checked');
    item.addClass('radio-checked');
    $t.val(value);
  } else {
    $("#dialog-confirm").dialog({
        resizable: false,
        height: 120,
        closeText: "",
        modal: true,
        buttons: {
            "Yes": function() {
              item.siblings().removeClass('radio-checked');
              item.addClass('radio-checked');
              $t.val(value);
              $(this).dialog("close");

            },
            Cancel: function() {
              $(this).dialog("close");
            }
        }
    });
  }
}
