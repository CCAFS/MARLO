$(document).ready(init);

function init() {
  // Add User - Override function from userManagement.js
  addUser = addUserItem;

// Numeric inputs
  $('input.keyOutputContribution').numericInput();
  $('input.outcomeContribution').numericInput();

  $('input.keyOutputContribution').percentageInput();
  $('input.outcomeContribution').percentageInput();

  $('form select').select2({
    width: '100%'
  });

// Add a new cluster
  $('.addCluster').on('click', addCluster);
// Remove cluster
  $('.removeCluster').on('click', removeCluster);
// Remove person
  $('.remove-userItem').on('click', removePerson);
// Add a new Key Output
  $('.addKeyOutput').on('click', addKeyOutput);
// Remove key output
  $('.removeKeyOutput').on('click', removeKeyOutput);
// Remove outcome
  $('.removeOutcome').on('click', removeOutcome);

  updateClustersIndex();

  $(".keyOutputInput").on("change keyup", changeTitle);

  $(".keyOutputContribution").on("change keyup", changeKOcontribution);

  // verify key Output contribution
  $(".keyOutputContribution").on("change keyup", function() {
    var list = $(this).parents(".cluster").find(".keyOutputsItems-list");
    var percentageRemaining = verifyKoContribution(list);
    if(percentageRemaining < 0) {
      $(list).find(".keyOutputContribution").removeClass('fieldChecked');
      $(list).find(".keyOutputContribution").addClass('fieldError');

    } else {
      $(list).find(".keyOutputContribution").removeClass('fieldError');
      $(list).find(".keyOutputContribution").addClass('fieldChecked');
    }
  });

  // verify Outcome contribution
  $(".outcomeContribution").on("change keyup", function() {
    var list = $(this).parents(".keyOutputItem ").find(".outcomesWrapper ");
    var percentageRemaining = verifyOutcomeContribution(list);
    console.log(percentageRemaining);
    if(percentageRemaining < 0) {
      $(list).find(".outcomeContribution ").removeClass('fieldChecked');
      $(list).find(".outcomeContribution ").addClass('fieldError');

    } else {
      $(list).find(".outcomeContribution ").removeClass('fieldError');
      $(list).find(".outcomeContribution ").addClass('fieldChecked');
    }
  });

  // Select outcomes
  $(".outcomeList").on("change", function() {
    var option = $(this).find("option:selected");
    var validation = $(this).parents(".outcomesWrapper").find("input[value=" + option.val() + "]");
    console.log(validation);
    if(option.val() != "-1") {
      if(validation.exists()) {
        option.parent().val(-1);
        option.parent().trigger("change.select2");
        // Show message
        var text = option.html() + ' already exists in this list';
        notify(text);
      } else {
        addOutcome(option);
      }
    }
  });

  $('.blockTitle').on('click', function() {
    if($(this).hasClass('closed')) {
      $(this).parent().find('.blockTitle').removeClass('opened').addClass('closed');
      $(this).removeClass('closed').addClass('opened');
    } else {
      $(this).removeClass('opened').addClass('closed');
    }
    $(this).next().slideToggle('slow', function() {
      $(this).find('textarea').autoGrow();
    });
  });
}

// CLUSTERS

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
    updateKeyOtuputsIndex(item, customName);
  });
}

// USERS-LEADERS
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

  // Update component event
  $(document).trigger('updateComponent');
}

// KEY OUTPUTS

// change title
function changeTitle() {
  var $blockTitle = $(this).parents(".keyOutputItem ").find(".koTitle");
  $blockTitle.html($(this).val());
  if($blockTitle.html() == "" || $blockTitle.html() == " ") {
    $blockTitle.html("New Key OutPut");
  }
}

// change key output contribution on title box
function changeKOcontribution() {
  var $blockTitle = $(this).parents(".keyOutputItem ").find(".koContribution-percentage");
  if($(this).val() < 0) {
    $blockTitle.html("0%");
  } else {
    $blockTitle.html($(this).val() + "%");
  }
  if($(this).val() == "") {
    $blockTitle.html("0%");
  }
}

function addKeyOutput() {
  var contribution;
  var $list = $(this).parent().parent().find('.keyOutputsItems-list');
  var $item = $('#keyOutput-template').clone(true).removeAttr("id");
  if(verifyKoContribution($list) <= 0) {
    contribution = 0;
  } else {
    contribution = verifyKoContribution($list);
  }
  $item.find(".keyOutputContribution").val(contribution);
  $item.find("span .koContribution-percentage").html(contribution + '%');
  $list.append($item);
  $item.find("select").select2({
      templateResult: formatState,
      templateSelection: formatState,
      width: '100%'
  });
  $item.show('slow');
  updateClustersIndex();
}

function removeKeyOutput() {
  var $item = $(this).parents('.keyOutputItem');
  var $parent = $item.parent();
  $item.hide(function() {
    $item.remove();
    updateClustersIndex();
  });
}

function updateKeyOtuputsIndex(item,clustersName) {
  var name = $("#keyOutputName").val();
  $(item).find('.keyOutputsItems-list .keyOutputItem').each(function(indexKeyOuput,keyOutputItem) {
    var customName = clustersName + '.' + name + '[' + indexKeyOuput + ']';
    $(keyOutputItem).find('.keyOutputInput').attr('name', customName + '.keyOutput');
    $(keyOutputItem).find('.id').attr('name', customName + '.id');
    $(keyOutputItem).find('.keyOutputContribution').attr('name', customName + '.contribution');

    updateOutcomesIndex(keyOutputItem, customName);
  });

  // Update component event
  $(document).trigger('updateComponent');
}

function verifyKoContribution(list) {
  var contribution = 0;
  var val = 0;
  list.find(".keyOutputItem ").each(function(i,e) {
    if($(e).find(".id ").val()) {
      // Existente
    } else {
      // nuevo
      if($(e).find(".keyOutputContribution ").val()) {
        val = parseInt($(e).find(".keyOutputContribution ").val());
        contribution = contribution + val;
      } else {
        val = 0;
        contribution = contribution + val;
      }
    }
  });
  var newContribution = 100 - contribution;

  return newContribution;
}

// OUTCOMES BY CoA

function addOutcome(option) {
  var contribution;
  var $list = $(option).parents('.blockContent').find(".outcomesWrapper");
  var $item = $('#outcomeByCluster-template').clone(true).removeAttr("id");
  if(verifyOutcomeContribution($list) <= 0) {
    contribution = 0;
  } else {
    contribution = verifyOutcomeContribution($list);
  }
  var v = $(option).text().length > 80 ? $(option).text().substr(0, 100) + ' ... ' : $(option).text();
  $item.find(".outcomeStatement").attr("title", $(option).text()).tooltip();
  $item.find(".outcomeStatement").html(v);
  $item.find(".outcomeId").val(option.val());
  $item.find(".outcomeContribution").val(contribution);
  $list.append($item);
  $item.show('slow');
  updateClustersIndex();
}

function removeOutcome() {
  console.log("holiClose");
  var $item = $(this).parents('.outcomeByClusterItem');
  var $parent = $item.parent();
  $item.hide(function() {
    $item.remove();
    updateClustersIndex();
  });
}

function updateOutcomesIndex(item,keyOutputName) {
  var name = $("#outcomesName").val();
  $(item).find('.outcomesWrapper .outcomeByClusterItem').each(function(indexOutcome,outcomeItem) {
    var customName = keyOutputName + '.' + name + '[' + indexOutcome + ']';
    $(outcomeItem).find('.outcomeContribution ').attr('name', customName + '.contribution');
    $(outcomeItem).find('.outcomeId').attr('name', customName + '.crpProgramOutcome.id');
    $(outcomeItem).find('.elementId').attr('name', customName + '.id');
  });

  // Update component event
  $(document).trigger('updateComponent');
}

function verifyOutcomeContribution(list) {
  var contribution = 0;
  var val = 0;
  list.find(".outcomeByClusterItem").each(function(i,e) {
    if($(e).find(".elementId ").val()) {
      // Existente
    } else {
      // nuevo
      if($(e).find(".outcomeContribution  ").val()) {
        val = parseInt($(e).find(".outcomeContribution").val());
        contribution = contribution + val;
      } else {
        val = 0;
        contribution = contribution + val;
      }
    }
  });
  var newContribution = 100 - contribution;
  return newContribution;
}

function checkItems(block) {
  console.log(block);
  var items = $(block).find('li').length;
  if(items == 0) {
    $(block).parent().find('p.inf').fadeIn();
  } else {
    $(block).parent().find('p.inf').fadeOut();
  }
}

function formatState(state) {
  console.log(state.text);
  if(state.id != "-1") {
    var text = state.text.split(/:(.+)?/);
    var $state = $("<span><strong>" + text[0] + ":</strong> " + text[1] + "</span>");
    return $state;
  } else {
    var $state = $("<span>" + state.text + "</span>");
    return $state;
  }

};

function notify(text) {
  var notyOptions = jQuery.extend({}, notyDefaultOptions);
  notyOptions.text = text;
  notyOptions.type = 'alert';
  noty(notyOptions);
}