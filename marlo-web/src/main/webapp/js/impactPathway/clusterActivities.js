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

  updateClustersIndex();
}

// Clusters

function addCluster() {
  var $list = $('.clusterList');
  var $item = $('#cluster-template').clone(true).removeAttr("id");
  $list.append($item);
  $item.show('slow');
  updateClustersIndex();
}

function removeCluster() {
  var $list = $(this).parents('.clusterList');
  var $item = $(this).parents('.cluster');
  $item.hide(1000, function() {
    $item.remove();
    updateClustersIndex();
  });
}

function updateClustersIndex() {
  var name = $("#clusterName").val();
  $(".clusterList").find('.cluster').each(function(i,item) {

    var customName = name + '[' + i + ']';
    $(item).find('span.index').html(i + 1);
    $(item).find('.outcome-statement').attr('name', customName + '.description');
    $(item).find('.cluterId').attr('name', customName + '.id');

    updateUsersIndex(item, customName);
  });
}

// Users-leaders
function removePerson() {
  var $item = $(this).parents('li');
  var $parent = $item.parent();
  $item.hide(function() {
    $item.remove();
    checkItems($parent);
    updateClustersIndex();
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
  } else {
    $li.find('.name').html(item.name);
    $li.find('.user').val(item.id);
    $usersList.append($li);
    $li.show('slow');
  }
  checkItems($usersList);
  dialog.dialog("close");
  updateClustersIndex();
}

function updateUsersIndex(item,clustersName) {
  var name = $("#leaderName").val();
  $(item).find('li').each(function(indexUser,userItem) {
    var customName = clustersName + '.' + name + '[' + indexUser + ']';
    $(userItem).find('.user').attr('name', customName + '.user.id');
    $(userItem).find('.role').attr('name', customName + '.role.id');
    $(userItem).find('.id').attr('name', customName + '.id');
  });

}

function checkItems(block) {
  console.log(block);
  var items = $(block).find('li').length;
  if(items == 0) {
    $(block).parent().find('p').fadeIn();
  } else {
    $(block).parent().find('p').fadeOut();
  }
}