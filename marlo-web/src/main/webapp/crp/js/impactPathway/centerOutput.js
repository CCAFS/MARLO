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

// Add a next User
  $('.addNextUser').on('click', addNextUser);

// Remove a next User
  $('.removeNextUser').on('click', removeNextUser);

  $(".outputSelect").on("change", addOutput);
  $(".removeOutput").on("click", removeOutput);

// When change of beneficiary type
  $(".typeSelect").on("change", function() {
    var option = $(this).find("option:selected");
    var $select = $(this).parents(".nextUser").find(".subTypeSelect");
    var url = baseURL + "/nextUserByType.do";
    var data = {
        "nextUserID": option.val(),
        phaseID: phaseID
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

  var outputSelect = $(".outputSelect");
  $(".outputList").find(".outputs").each(function(i,e) {
    var idOutput = $(e).find("input.outputId");
    var outputOption = outputSelect.find("option[value='"+idOutput.val()+"']");
    console.log("idOut= "+idOutput.val()+" outputOpt= "+outputOption);
    outputOption.prop('disabled', true);
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

// Update beneficiaries
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

/** FUNCTIONS Outputs* */

// Add a new funding source element
function addOutput() {
  var $select = $(this);
  var option = $select.find("option:selected");
  if(option.val() != "-1") {
    $.ajax({
        url: baseURL + "/outcomeInfo.do",
        type: 'GET',
        data: {
          outcomeID: option.val()
        },
        success: function(m) {
          console.log("data");
          console.log(m);
          var $list = $(".outputList");
          var $item = $("#output-template").clone(true).removeAttr("id");
          $item.find("span.index").text("OC" + m.outcomeInfo.id);
          console.log('opttext'+option.text());
          $item.find("div.oStatement").text(option.text());
          $item.find(".outputId").val(m.outcomeInfo.id);
          $list.append($item);
          $item.show('slow');
          updateOutputs();
          checkOutputItems($list);
          $select.find(option).remove();
          $select.val("-1").trigger("change");
        },
        error: function(e) {
          console.log(e);
        }
    });
  }
}

// Remove Funding source element
function removeOutput() {
  var $list = $(this).parents('.outputList');
  var $item = $(this).parents('.outputs');
  var $select = $(".outputSelect");
  $select.addOption($item.find("input.outputId").val(), $item.find("div.oStatement").text());
  $item.hide(1000, function() {
    $item.remove();
    checkOutputItems($list);
    updateOutputs();
  });

}

function updateOutputs() {
  $(".outputList").find('.outputs').each(function(i,e) {
    // Set indexes
    $(e).setNameIndexes(1, i);
  });
}

function checkOutputItems(block) {
  var items = $(block).find('.outputs').length;
  if(items == 0) {
    $(block).find('p.outputInf').fadeIn();
  } else {
    $(block).find('p.outputInf').fadeOut();
  }
}

function checkOutputsToRemove() {
  $(".outputList").find(".outputs").each(function(i,e) {
    var option = $(".outputSelect").find("option[value='" + $(e).find(".outputId").val() + "']");
    option.remove();
  });
}