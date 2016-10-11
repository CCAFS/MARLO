$(document).ready(init);

function init() {
  verifyContributions();
  // Add User - Override function from userManagement.js
  addUser = addUserItem;

// Numeric inputs
  $('input.keyOutputContribution , input.outcomeContribution').numericInput();
  $('form input.keyOutputContribution , form input.outcomeContribution').percentageInput();

  $('form select').select2({
      templateResult: formatState,
      templateSelection: formatState,
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

  // Select outcomes
  $(".outcomeList").on("change", function() {
    var option = $(this).find("option:selected");
    if(option.val() != "-1") {
      addOutcome(option);
    }
    // Remove option from select
    option.remove();
    $(this).trigger("change.select2");
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
      $(this).find(".errorTag").hide();
      console.log($(this).find(".errorTag"));
      $(this).find(".errorTag").css("left", $(this).find(".outcomesWrapper").outerWidth());
      $(this).find(".errorTag").fadeIn(2000);
    });
  });

// verify contributions
  $(".keyOutputContribution , .outcomeContribution").on("change keyup", verifyContributions);

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
  var v = $(this).val().length > 70 ? $(this).val().substr(0, 70) + ' ... ' : $(this).val();
  $blockTitle.attr("title", $(this).val()).tooltip();
  $blockTitle.html(v);
  if($blockTitle.html() == "" || $blockTitle.html() == " ") {
    $blockTitle.html("New Key OutPut");
  }
}

// change key output contribution on title box
function changeKOcontribution() {
  var $blockTitle = $(this).parents(".keyOutputItem ").find(".koContribution-percentage");
  if($(this).val() < 0) {
    $blockTitle.html("0%");
  } else if($(this).val() > 100) {
    $blockTitle.html("100%");
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
  $item.find(".blockTitle").removeClass("closed").addClass("opened");
  $item.find(".blockContent").css("display", "block");
  if(verifyKoContribution($list) <= 0) {
    contribution = 0;
  } else {
    contribution = verifyKoContribution($list);
  }
  $item.find(".keyOutputContribution").val(contribution);
  $item.find("span .koContribution-percentage").html(contribution + '%');
  $list.append($item);

  // Initialize
  $($item).find('input.keyOutputContribution').percentageInput();
  $item.find("select").select2({
      templateResult: formatState,
      templateSelection: formatState,
      width: '100%'
  });
  $item.show('slow');
  updateClustersIndex();
  verifyContributions();
  checkKeyoutputs($list);
}

function removeKeyOutput() {
  var $item = $(this).parents('.keyOutputItem');
  var $parent = $item.parent();
  $item.hide(function() {
    $item.remove();
    updateClustersIndex();
    verifyContributions();
    checkKeyoutputs($parent);
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

    // nuevo
    if($(e).find(".keyOutputContribution ").val()) {
      val = parseFloat($(e).find(".keyOutputContribution ").val());
      console.log(val);
      contribution = contribution + val;
    } else {
      val = 0;
      contribution = contribution + val;
    }
    console.log(contribution);
  });
  var newContribution = 100 - contribution;
  return newContribution.toFixed(2);
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

  // Initialize
  $($item).find('input.outcomeContribution').percentageInput();
  $item.show('slow');
  updateClustersIndex();
  verifyContributions();
  checkOutcomes($list);
}

function removeOutcome() {
  console.log("holiClose");
  var $item = $(this).parents('.outcomeByClusterItem');
  var $list = $item.parent();
  var value = $item.find(".outcomeId").val();
  var name = $item.find(".outcomeStatement").attr("title");
  var $parent = $item.parent();
  var $select = $parent.parent().parent().parent().find(".outcomeList");
  $item.hide(function() {
    $item.remove();
    updateClustersIndex();
    checkOutcomes($list);
  });
// Add outcome option again
  $select.addOption(value, name);
  $select.trigger("change.select2");
  verifyContributions();

}

function updateOutcomesIndex(item,keyOutputName) {
  var name = $("#outcomesName").val();
  $(item).find('.outcomesWrapper .outcomeByClusterItem').each(function(indexOutcome,outcomeItem) {
    var customName = keyOutputName + '.' + name + '[' + indexOutcome + ']';
    $(outcomeItem).find('.outcomeContribution ').attr('name', customName + '.contribution');
    $(outcomeItem).find('.outcomeId').attr('name', customName + '.crpProgramOutcome.id');
  });

  // Update component event
  $(document).trigger('updateComponent');
}

function verifyOutcomeContribution(list) {
  var contribution = 0;
  var val = 0;
  list.find(".outcomeByClusterItem").each(function(i,e) {
    // nuevo
    if($(e).find(".outcomeContribution  ").val()) {
      val = parseFloat($(e).find(".outcomeContribution").val());
      contribution = contribution + val;
    } else {
      val = 0;
      contribution = contribution + val;
    }

  });
  var newContribution = 100 - contribution;
  return newContribution.toFixed(2);
}

function checkItems(block) {
  var items = $(block).find('li').length;
  if(items == 0) {
    $(block).parent().find('p.inf').fadeIn();
  } else {
    $(block).parent().find('p.inf').fadeOut();
  }
}

function checkKeyoutputs(block) {
  var items = $(block).find('.keyOutputItem').length;
  if(items == 0) {
    $(block).find('p.alertKeyoutput').fadeIn();
  } else {
    $(block).find('p.alertKeyoutput').fadeOut();
  }
}

function checkOutcomes(block) {
  var items = $(block).find('.outcomeByClusterItem').length;
  if(items == 0) {
    $(block).find('p.alertOutcome').fadeIn();
  } else {
    $(block).find('p.alertOutcome').fadeOut();
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

function verifyContributions() {
  var contentCluster = $(".clusterList");
  contentCluster.find(".cluster").each(function(i,e) {
    // verify Key output contributions
    var keyOutputList = $(e).find(".keyOutputsItems-list");
    var percentageRemaining = verifyKoContribution(keyOutputList);
    if(percentageRemaining < 0) {
      $(keyOutputList).find(".keyOutputContribution").removeClass('fieldChecked');
      $(keyOutputList).find(".keyOutputContribution").addClass('fieldError');
      $(keyOutputList).find(".koContribution-title").addClass('errorText');
    } else {
      $(keyOutputList).find(".koContribution-title").removeClass('errorText');
      $(keyOutputList).find(".keyOutputContribution").removeClass('fieldError');
      $(keyOutputList).find(".keyOutputContribution").addClass('fieldChecked');
    }
    if(percentageRemaining > 0) {
      $(keyOutputList).find(".keyOutputContribution").removeClass('fieldChecked');
      $(keyOutputList).find(".keyOutputContribution").addClass('fieldError');
      $(keyOutputList).find(".koContribution-title").addClass('errorText');
    }

    // verify Outcome contributions
    keyOutputList.find(".keyOutputItem ").each(function(i1,e1) {
      var outcomeList = $(e1).find(".outcomesWrapper");
      var outcomeRemaining = verifyOutcomeContribution(outcomeList);
      if(outcomeRemaining < 0) {
        $(outcomeList).find(".outcomeContribution ").removeClass('fieldChecked');
        $(outcomeList).find(".outcomeContribution ").addClass('fieldError');

      } else {
        $(outcomeList).find(".outcomeContribution ").removeClass('fieldError');
        $(outcomeList).find(".outcomeContribution ").addClass('fieldChecked');
      }
      if(outcomeRemaining > 0) {
        $(outcomeList).find(".outcomeContribution").removeClass('fieldChecked');
        $(outcomeList).find(".outcomeContribution").addClass('fieldError');
      }
    });
  });
}