$(document).ready(init);

var milestonesCount, outcomeID, parentID, phaseID, userID;
var sectionName = 'projectContributionCrp';
var contributionCRPAjaxURL = `/fieldsBySectionAndParent.do?sectionName=${sectionName}`;
var arrayName = 'fieldsMap';
let fieldID = '';
let qaComments = '';

function init() {
  milestonesCount = $('form .outcomeMilestoneYear').length;
  outcomeID = $('#outcomeId').val();

  // Set Select2 widget to already saved data
  $('form select').select2();
  $('form .milestonesYearSelect select').select2({
      templateResult: formatState,
      templateSelection: formatState,
      width: '100%'
  });

  // Numeric inputs
  $('input.targetValue').numericInput();

  // Load Milestones ones
  $('form .milestonesYearSelect').each(loadMilestonesByYear);

  parentID = $('#parentID').html();
  phaseID = $('#phaseID').html();
  userID = $('#userID').html();

  // Attaching events functions
  attachEvents();

  $('img.qaComment').on('click', function (event) {
    var name = this.name;
    var popUpTitle = $(this).attr('description');
    fieldID = $(this).attr('fieldID');
    $('textarea[id="Comment on"]').prev('label').html(`Comment on "${popUpTitle}":`);
    $('.sendCommentContainer').attr('name', name);

    loadCommentsByUser(name);

    if (event.pageX < 1000) {
      $('#qaPopup').css('left', event.pageX);  
    } else {
      $('#qaPopup').css('left', 'min(100vw - 100px, 78vw)');  
    }

    $('#qaPopup').css('top', event.pageY);
    $('#qaPopup').show();
  });

  $('div.closeComment').on('click', () => {
    $('#qaPopup').hide();
  });
}

function attachEvents() {
  // Add a milestone
  $('.milestonesYearSelect select').on('change', addMilestone);

  // Remove a milestone
  $('.removeProjectMilestone').on('click', removeMilestone);

  // Add a next user
  $('.addNextUser').on('click', addNextUser);

  // Remove a next user
  $('.removeNextUser').on('click', removeNextUser);

  loadQACommentsIcons(contributionCRPAjaxURL, arrayName);
  getQAComments();

  $('.sendCommentContainer').on('click', function () {
    var name = $(this).attr('name');
    var comment = $('textarea[id="Comment on"]').next().html();
    var cleanComment = comment.replaceAll('.<br>.', '');
    cleanComment = cleanComment.replaceAll('&nbsp;', ' ');

    saveQAComment(cleanComment, fieldID, name);
  });
}

function loadCommentsByUser(name) {
  for (let i = 0; i < qaComments.length; i++) {
    if(qaComments[i].frontName == name) {
      if (qaComments[i].comment && qaComments[i].comment != '') {
        $('textarea[id="Comment on"]').hide();
        $('textarea[id="Comment on"]').next().next('p.charCount').hide();
        $('.commentContainer').show();
        $('.commentContainer .commentTitle').html(`Comment by ${qaComments[i].userName} at ${qaComments[i].date}`);
        $('.commentContainer p.commentReadonly').html(`${qaComments[i].comment}`);
        $('textarea[id="Comment on"]').attr('readonly','readonly');
        $('.sendCommentContainer').css('display','none');
        $('.optionsContainer').css('display','flex');
        break;
      }
    } else {
      $('textarea[id="Comment on"]').show();
      $('textarea[id="Comment on"]').next().next('p.charCount').show();
      $('.commentContainer').hide();
      $('textarea[id="Comment on"]').val('');
      $('textarea[id="Comment on"]').removeAttr('readonly');
      $('.sendCommentContainer').css('display','flex');
      $('.optionsContainer').css('display','none');
    }
  }
}

function loadQACommentsIcons(ajaxURL, arrayName) {
  $.ajax({
    url: baseURL + ajaxURL,
    async: false,
    success: function (data) {
      if (data && Object.keys(data).length != 0) {
        var newData = data[arrayName].map(function (x) {
          var arr = [];
          arr.push(x.fieldID);
          arr.push(x.fieldName);
          arr.push(x.description);
          return arr;
        });
        showQAComments(newData);
      }
    }
  });
}

function showQAComments(data) {
  data.map(function (x) {
    var commentIcon = $(`img[name="${x[1]}"]`);
    commentIcon.attr('fieldID', `${x[0]}`);
    commentIcon.attr('description', `${x[2]}`);
    commentIcon.show();
  });
}

function saveQAComment(comment, fieldID, name) {
  var finalAjaxURL = `/saveFeedbackComments.do?sectionName=${sectionName}&parentID=${parentID}&comment=${comment}&phaseID=${phaseID}&fieldID=${fieldID}&userID=${userID}`;

  $.ajax({
    url: baseURL + finalAjaxURL,
    async: false,
    success: function (data) {
      getQAComments();
      loadCommentsByUser(name);
    }
  });
}

function getQAComments() {
  var finalAjaxURL = `/feedbackComments.do?sectionName=${sectionName}&parentID=${parentID}&phaseID=${phaseID}`;
  
  $.ajax({
    url: baseURL + finalAjaxURL,
    async: false,
    success: function (data) {
      if (data && Object.keys(data).length != 0) {
        qaComments = data['comments'];
      }
    }
  });
}

/** FUNCTIONS * */

function loadMilestonesByYear(i,e) {
  var $parent = $(e).parents('.tab-pane');
  var $select = $(e).find('select');
  var selectedIds = ($(e).find('.milestonesSelectedIds').text()).split(',');

  // Getting Milestones list milestonesYear.do?year=2017&outcomeID=33
  $.ajax({
      url: baseURL + '/milestonesYear.do',
      data: {
          year: currentCycleYear,
          outcomeID: outcomeID,
          phaseID: phaseID,
          ignoreNewer: true,
      },
      success: function(data) {
        for(var i = 0, len = data.crpMilestones.length; i < len; i++) {
          $select.addOption(data.crpMilestones[i].id, data.crpMilestones[i].description);
        }

        // Clear options
        $select.clearOptions(selectedIds);

        $select.trigger("change.select2");
      }
  });

}

function addMilestone() {
  var $item = $('#milestoneYear-template').clone(true).removeAttr('id');
  var $list = $(this).parents('.milestonesYearBlock').find(".milestonesYearList");
  // var year = ($list.parents('.tab-pane').attr('id')).split('-')[1];
  var title = $(this).find('option:selected').text();
  var milestonId = $(this).find('option:selected').val();

  // Set the milestone parameters
  $item.find('.crpMilestoneId').val(milestonId);

  // Set Select2 widget
  $item.find('select').select2({
    width: '100%'
  });

  $list.find('.emptyMessage').hide();

  // Add the milestone to the list
  $list.append($item);

  // Show the milestone
  $item.show('slow');

  // Remove option from select
  $(this).find('option:selected').remove();
  $(this).trigger("change.select2");

  // Get extra information from ajax service milestoneInformation.do?milestoneID=3
  $.ajax({
      url: baseURL + '/milestoneInformation.do',
      data: {
          milestoneID: milestonId,
          phaseID: phaseID
      },
      success: function(data) {

        $item.find('.crpMilestoneYear').text(data.crpMilestone.year);
        $item.find('.crpMilestoneYearInput').val(data.crpMilestone.year);
        $item.find('.crpMilestoneValue').text(data.crpMilestone.value);
        // $item.find('.targetValue').attr('placeholder', data.crpMilestone.value);
        $item.find('.crpMilestoneTargetUnit').text(data.crpMilestone.targetUnitName);
        $item.find('.crpMilestoneTargetUnitInput').val(data.crpMilestone.targetUnit);
        $item.find('.title').text(data.crpMilestone.title);

        if(data.crpMilestone.targetUnit != -1) {
          $item.find('.milestoneTargetValue').show();
        }

        // Set indexes
        $item.find('.outcomeMilestoneYear').each(function(i,e) {
          $(e).setNameIndexes(1, milestonesCount);
          milestonesCount++;
        });

        // Update milestone
        $list.find('.milestoneYear').each(function(i,e) {
          $(e).find('.index').text(i + 1);
        });

      }
  });
}

function removeMilestone() {
  var $parent = $(this).parent();
  var $select = $parent.parents('.milestonesYearBlock').find('.milestonesYearSelect select');
  var value = $parent.find('.crpMilestoneId').val();
  var name = $parent.find('.crpMilestoneYear').text() + " - " + $parent.find('.title').text();

  $parent.hide('slow', function() {
    // Remove milestone block
    $parent.remove();

    // Update milestone
    $select.parents('.milestonesYearBlock').find('.milestoneYear').each(function(i,e) {
      $(e).find('.index').text(i + 1);
    });

    // Add milestone option again
    $select.addOption(value, name);
    $select.trigger("change.select2");
  });
}

function addNextUser() {
  var $item = $('#nextUser-template').clone(true).removeAttr('id');
  var $list = $(this).parents('.nextUsersBlock').find(".nextUsersList");

  // Add the milestone to the list
  $list.append($item);

  // Show the milestone
  $item.show('slow');

  // Update Next users list
  updateNextUsers();

}

function removeNextUser() {
  var $parent = $(this).parent();
  $parent.hide('slow', function() {
    // Remove milestone block
    $parent.remove();

    // Update Next users list
    updateNextUsers();
  });
}

function updateNextUsers() {
  $("form .nextUser").each(function(i,e) {
    $(e).find('.index').text(i + 1);
    $(e).setNameIndexes(1, i);
  });
}

function formatState(state) {
  if(state.id != "-1") {
    var text = state.text.split(/-(.+)?/);
    var $state = $("<span><strong> Milestone for " + text[0] + "-</strong> " + text[1] + "</span>");
    return $state;
  } else {
    var $state = $("<span>" + state.text + "</span>");
    return $state;
  }

};
