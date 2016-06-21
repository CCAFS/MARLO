$(document).ready(init);

function init() {
  // Add User - Override function from userManagement.js
  addUser = addUserItem;

// Add a new cluster
  $('.addCluster').on('click', addCluster);
// Remove cluster
  $('.removeCluster').on('click', removeCluster);
// Remove person
  $('.remove-userItem').on('click', removePerson);

  updateIndex();
}

function addCluster() {
  var $list = $('.clusterList');
  var $item = $('#cluster-template').clone(true).removeAttr("id");
  $list.append($item);
  $item.show('slow');
  updateIndex();
}

function removeCluster() {
  var $list = $(this).parents('.clusterList');
  var $item = $(this).parents('.cluster');
  $item.hide(1000, function() {
    $item.remove();
    updateIndex();
  });
}

function removePerson() {
  var $item = $(this).parents('li');
  console.log($item);
  $item.hide(function() {
    $item.remove();
    updateIndex();
  });

}

function addUserItem(composedName,userId) {
  $usersList = $elementSelected.parent().parent().find(".leaders");
  var $li = $("#user-template").clone(true).removeAttr("id");
  var item = {
      name: escapeHtml(composedName),
      id: userId
  }
  if($usersList.find('input[value=' + item.id + ']').exists()) {
    console.log("ya existe el usuario");
  } else {
    console.log(item.name);
    $li.find('.name').html(item.name);
    $li.find('.user').val(item.id);
    $usersList.append($li);
    $li.show('slow');
  }

  dialog.dialog("close");
  updateIndex();
}

function updateIndex() {
  $(".clusterList").find('.cluster').each(function(i,item) {

    $(item).attr('id', 'cluster-' + i);
    $(item).find('.userItem').each(function(iUser,userItem) {
      $(userItem).attr('name', 'cluster[' + i + '].leader[' + iUser + ']institution.id');
    });

  });
}