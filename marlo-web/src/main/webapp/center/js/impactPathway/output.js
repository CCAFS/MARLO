$(document).ready(init);

function init() {

  // Add Select2
  $('form select').select2({
    width: '100%'
  });

  attachEvents();
}

function attachEvents() {

  // Add User - Override function from userManagement.js
  addUser = addUserItem;

  // Remove an user item
  $('.remove-userItem').on('click', removeUser);

  // Add a partner
  $('.partnerSelect select').on('change', addPartner);

  // Remove a partner
  $('.removePartner').on('click', removePartner);
  
//Add a next User
  $('.addNextUser').on('click', addNextUser);
  
// Remove a next User
  $('.removeNextUser').on('click', removeNextUser);
  
//When change of beneficiary type
  $(".typeSelect").on("change", function() {
    var option = $(this).find("option:selected");
    var $select = $(this).parents(".nextUser").find(".subTypeSelect");
    var url = baseURL + "/nextUserByType.do";
    var data = {
      "nextUserID": option.val()
    }
    // remove options
    $select.find("option").each(function(i,e) {
      $(e).remove();
    });
    $.ajax({
        url: url,
        type: 'GET',
        dataType: "json",
        data: data
    }).done(function(m) {
      $select.addOption("-1", "Select an option");
      for(var i = 0; i < m.nextUsers.length; i++) {
        // Add beneficiary option
        $select.addOption(m.nextUsers[i].id, m.nextUsers[i].name);
      }
      $select.trigger("change.select2");
    });
  });

}

function addPartner() {
  var $item = $('#partner-template').clone(true).removeAttr('id');
  var $list = $(this).parents('.parntersBlock').find(".partnersList");
  // var year = ($list.parents('.tab-pane').attr('id')).split('-')[1];
  var title = $(this).find('option:selected').text();
  var partnerInstitutionId = $(this).find('option:selected').val();

  // Set the partner parameters
  $item.find('.title').text(title);
  $item.find('.partnerInstitutionId').val(partnerInstitutionId);

  // Add the partner to the list
  $list.append($item);

  // Show the new partner
  $item.show('slow');

  // Update partners list
  updateIndexes();

  // Remove option from select
  $(this).find('option:selected').remove();
  $(this).trigger("change.select2");

}

function removePartner() {
  var $parent = $(this).parent();
  var $select = $parent.parents('.parntersBlock').find('.partnerSelect select');
  var value = $parent.find('.partnerInstitutionId').val();
  var name = $parent.find('.title').text();

  $parent.hide('slow', function() {
    // Remove partner block
    $parent.remove();

    // Update partners list
    updateIndexes();

    // Add partner option again to select
    $select.addOption(value, name);
    $select.trigger("change.select2");
  });
}

function addUserItem(composedName,userId) {
  $usersList = $elementSelected.parent().parent().find(".items-list");
  var $li = $("#user-template").clone(true).removeAttr("id");
  var item = {
      name: escapeHtml(composedName),
      id: userId
  }
  $li.find('.name').html(item.name);
  $li.find('.user').val(item.id);

  $usersList.find("ul").append($li);
  $li.show('slow');
  checkItems($usersList, 'usersMessage');

  // Update partners list
  updateIndexes();

  dialog.dialog("close");
}

function removeUser() {
  var $parent = $(this).parent();
  var $block = $parent.parent().parent();

  $parent.hide(function() {
    $parent.remove();
    checkItems($block, 'usersMessage');
    // Update partners list
    updateIndexes();
  });
}

function checkItems(block,target) {
  var items = $(block).find('> ul li').length;
  if(items == 0) {
    $(block).find('p.' + target).fadeIn();
  } else {
    $(block).find('p.' + target).fadeOut();
  }
}

function updateIndexes() {
  // Update partner indexes
  $('.parntersBlock').find('.partner').each(function(index,element) {
    $(element).setNameIndexes(1, index);

    // Update contacts indexes
    $(element).find('.userItem').each(function(i,e) {
      $(e).setNameIndexes(2, i);
    });
  });
  
//Update beneficiaries
  $(".nextUsers-list").find(".nextUser").each(function(i,e) {
    console.log($(e));
    $(e).setNameIndexes(1, i);
  });
}

/**
 * next users function
 */
function addNextUser() {
  console.log("holi");
  var $list = $(this).parents("form").find('.nextUsers-list');
  console.log($list);
  var $item = $('#nextUser-template').clone(true).removeAttr("id");
  $list.append($item);
  $item.show('slow');
  checkNextUsersItems($list);
  updateIndexes();
}

function removeNextUser() {
  var $list = $(this).parents('.nextUsers-list');
  var $item = $(this).parents('.nextUser');
  $item.hide(function() {
    $item.remove();
    checkNextUsersItems($list);
    updateIndexes();
  });
}

function checkNextUsersItems(block) {
  var items = $(block).find('.nextUser ').length;
  if(items == 0) {
    $(block).parent().find('p.message').fadeIn();
  } else {
    $(block).parent().find('p.message').fadeOut();
  }
}