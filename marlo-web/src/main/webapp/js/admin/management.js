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

  // Add program
  $('.addProgram').on('click', function() {
    addProgram($(this));
  });

  // Remove an program item
  $('.remove-programItem').on('click', function() {
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
        text: 'Please be aware you may have outcomes and cluster of activities associated to this Flagship.',
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
  // Assign parameters to template created
  $li.find('.type').val(item.type);
  // Set color picker
  $li.find('.color-picker').colorPicker();
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
    var programName = 'flagshipsPrograms' + '[' + index + '].';
    $(item).find('.index').text(index + 1);

    $(item).find('.acronym').attr('name', programName + 'acronym');
    $(item).find('.name').attr('name', programName + 'name');
    $(item).find('.color-picker input').attr('name', programName + 'color');
    $(item).find('.type').attr('name', programName + 'programType');
    $(item).find('.id').attr('name', programName + 'id');

    // Program Leaders
    $(item).find('.usersBlock li').each(function(i,leader) {
      var leaderName = programName + 'leaders[' + i + '].';
      updateUserItemIndex(leader, leaderName);
    });
  });
}

function updateUserItemIndex(element,name) {
  $(element).find('input.user').attr('name', name + 'user.id');
  $(element).find('input.role').attr('name', name + 'role.id');
  $(element).find('input.id').attr('name', name + 'id');
}

function yesnoEventLocations(value,item) {
  $t = item.parent().find('input.onoffswitch-radio');
  // AHORA
  if(value == true) {
    item.siblings().removeClass('radio-checked');
    item.addClass('radio-checked');
    $t.val(value);
  } else {
    $("#dialog-confirm").dialog({
        resizable: false,
        height: 120,
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