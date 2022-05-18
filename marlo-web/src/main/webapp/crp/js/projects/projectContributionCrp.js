$(document).ready(init);

var milestonesCount, outcomeID, textareaComment, parentID, projectID, phaseID, userID, link, userCanManageFeedback, isFeedbackActive, textareaReply, newData;
var sectionName = 'projectContributionCrp';
var contributionCRPAjaxURL = `/fieldsBySectionAndParent.do?sectionName=${sectionName}`;
var arrayName = 'fieldsMap';
let fieldID = '';
let qaComments = '';
let commentID;

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

  textareaComment = $('textarea[id="Comment on"]');
  textareaReply = $('textarea[id="Reply"]').parent();
  parentID = $('#parentID').html();
  projectID = $('#projectID').html();
  phaseID = $('#phaseID').html();
  userID = $('#userID').html();
  link = window.location.href;
  userCanManageFeedback = $('#userCanManageFeedback').html();
  isFeedbackActive = $('#isFeedbackActive').html();

  // Attaching events functions
  attachEvents();
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

  if (isFeedbackActive == 'true') {
    getQAComments();
    loadQACommentsIcons(contributionCRPAjaxURL, arrayName);
  }

  // Multiple comments-replies
  $('img.qaComment').on('click', function (event) {
    let name = this.name;
    let popUpTitle = $(this).attr('description');
    let qaPopup = $(`div[name^="${name}"]`);
    let block = $(`div[id^="qaCommentReply-${name}"]`);

    fieldID = $(this).attr('fieldID');
    block.find('textarea[id="Comment on"]').prev('label').html(`Comment on "${popUpTitle}":`);
    block.find('div.sendCommentContainer').attr('name', name);
    // $('#agreeCommentBtn').attr('name', name);
    // $('#disagreeCommentBtn').attr('name', name);
    // $('#replyCommentBtn').attr('name', name);
    // $('#sendReplyContainer').attr('name', name);

    loadCommentsByUser(name);

    if (event.pageX < 1000) {
      qaPopup.css('left', event.pageX);
    } else {
      qaPopup.css('left', 'min(100vw - 100px, 78vw)');
    }

    qaPopup.css('top', event.pageY);
    $('.qaPopup').hide().not(qaPopup);
    qaPopup.show();
  });

  $('div.closeComment').on('click', function () {
    let name = $(this).attr('name');
    $(`div[name^="${name}"]`).hide();
  });

  $('div.sendCommentContainer').on('click', function () {
    let name = $(this).attr('name');
    let block = $(`div[id^="qaCommentReply-${name}"]`);
    let comment = block.find('textarea[id="Comment on"]').next().html();
    let cleanComment = comment.replaceAll('.<br>.', '');
    cleanComment = cleanComment.replaceAll('&nbsp;', ' ');

    saveQAComment(cleanComment, fieldID, name);
  });

  $('img.addCommentBlock').on('click', function () {
    let popup = $(this).parent().parent().parent();
    let commentReplyBlock = popup.siblings('#qaTemplate').find('.qaPopup').children()[2];
    let newBlock = $(commentReplyBlock).clone(true).removeAttr('id');
    newBlock.appendTo(popup).hide().show();
  });

  // Single comment-reply
  // $('img.qaComment').on('click', function (event) {
  //   var name = this.name;
  //   var popUpTitle = $(this).attr('description');
  //   fieldID = $(this).attr('fieldID');
  //   textareaComment.prev('label').html(`Comment on "${popUpTitle}":`);
  //   $('#sendCommentContainer').attr('name', name);
  //   $('#agreeCommentBtn').attr('name', name);
  //   $('#disagreeCommentBtn').attr('name', name);
  //   $('#replyCommentBtn').attr('name', name);
  //   $('#sendReplyContainer').attr('name', name);

  //   loadCommentsByUser(name);

  //   if (event.pageX < 1000) {
  //     $('#qaPopup').css('left', event.pageX);
  //   } else {
  //     $('#qaPopup').css('left', 'min(100vw - 100px, 78vw)');
  //   }

  //   $('#qaPopup').css('top', event.pageY);
  //   $('#qaPopup').show();
  // });

  // $('div.closeComment').on('click', () => {
  //   $('#qaPopup').hide();
  // });

  // $('#sendCommentContainer').on('click', function () {
  //   var name = $(this).attr('name');
  //   var comment = textareaComment.next().html();
  //   var cleanComment = comment.replaceAll('.<br>.', '');
  //   cleanComment = cleanComment.replaceAll('&nbsp;', ' ');

  //   saveQAComment(cleanComment, fieldID, name);
  // });

  $('#agreeCommentBtn').on('click', function () {
    var name = $(this).attr('name');

    hideShowOptionButtons(true);
    saveCommentStatus(1, name);
  });

  $('#disagreeCommentBtn').on('click', function () {
    var name = $(this).attr('name');

    hideShowOptionButtons(false);
    saveCommentStatus(0, name);
    $('#replyCommentBtn').click();
  });

  $('#clarificationCommentBtn').on('click', function () {
    var name = $(this).attr('name');

    hideShowOptionButtons('clarification');
    saveCommentStatus(2, name);
    $('#replyCommentBtn').click();
  });

  $('#replyCommentBtn').on('click', function () {
    $('.replyContainer').show();
    $('.optionsContainer').hide();
  });

  $('#sendReplyContainer').on('click', function () {
    var name = $(this).attr('name');
    var comment = $('textarea[id="Reply"]').next().html();
    var cleanComment = comment.replaceAll('.<br>.', '');
    cleanComment = cleanComment.replaceAll('&nbsp;', ' ');

    saveFeedbackReply(cleanComment, name);
  });
}

function hideShowOptionButtons(status) {
  switch (status) {
    case true:
      $('#agreeCommentBtn').hide();
      $('img.disagreeComment').hide();
      $('img.agreeComment').show();
      $('img.clarificationComment').hide();
      $('#disagreeCommentBtn').hide();
      $('#clarificationCommentBtn').hide();
      $('#replyCommentBtn').show();
      break;
    case false:
      $('#disagreeCommentBtn').hide();
      $('img.agreeComment').hide();
      $('img.disagreeComment').show();
      $('img.clarificationComment').hide();
      $('#agreeCommentBtn').hide();
      $('#clarificationCommentBtn').hide();
      $('#replyCommentBtn').show();
      break;
    case 'clarification':
      $('#clarificationCommentBtn').hide();
      $('img.agreeComment').hide();
      $('img.disagreeComment').hide();
      $('img.clarificationComment').show();
      $('#agreeCommentBtn').hide();
      $('#disagreeCommentBtn').hide();
      $('#replyCommentBtn').show();
      break;
    case '':
      $('#agreeCommentBtn').show();
      $('#disagreeCommentBtn').show();
      $('img.agreeComment').hide();
      $('img.disagreeComment').hide();
      break;

    default:
      break;
  }
}

// Multiple comments-replies
function loadCommentsByUser(name) {
  if (qaComments.length > 0) {
    for (let i = 0; i < qaComments.length; i++) {
      if (qaComments[i].frontName == name) {
        let block = $(`div[id^="qaCommentReply-${name}"]`);
        // block.hide();
        // commentID = qaComments[i].commentId;
        // textareaComment.hide();
        // textareaComment.next().next('p.charCount').hide();
        // $('.commentContainer').show();
        // $('.commentContainer .commentTitle').html(`Comment by ${qaComments[i].userName} at ${qaComments[i].date}`);
        // $('.commentContainer p.commentReadonly').html(`${qaComments[i].comment}`);
        // $('#sendCommentContainer').css('display', 'none');

        if (userCanManageFeedback == 'true') {
          // $('.optionsContainer').css('display', 'flex');
          // hideShowOptionButtons(qaComments[i].status);
        }

        if (qaComments[i].reply && qaComments[i].reply != '') {
          // textareaReply.hide();
          // $('.replyContainer').show();
          // $('.replyTextContainer').show();
          // $('.replyTextContainer .replyTitle').html(`Comment by ${qaComments[i].userName_reply} at ${qaComments[i].date_reply}`);
          // $('.replyTextContainer p.replyReadonly').html(`${qaComments[i].reply}`);
          // $('#sendReplyContainer').css('display', 'none');
          // $('#replyCommentBtn').hide();
        } else {
          // textareaReply.show();
          // $('.replyContainer').hide();
          // $('.replyTextContainer').hide();
          // $('#sendReplyContainer').css('display', 'flex');
        }
        break;
      } else {
        // textareaComment.show();
        // textareaComment.next().next('p.charCount').show();
        // $('.commentContainer').hide();
        // textareaComment.val('');
        // $('textarea[id="Reply"]').val('');
        // $('#sendCommentContainer').css('display', 'flex');
        // $('.optionsContainer').css('display', 'none');
        // hideShowOptionButtons('');
        // $('.replyContainer').hide();
      }
    }
  } else {
    // $('.replyContainer').hide();
  }
}

// Single comment-reply
// function loadCommentsByUser(name) {
//   if (qaComments.length > 0) {
//     for (let i = 0; i < qaComments.length; i++) {
//       if (qaComments[i].frontName == name) {
//         if (qaComments[i].comment && qaComments[i].comment != '') {
//           commentID = qaComments[i].commentId;
//           textareaComment.hide();
//           textareaComment.next().next('p.charCount').hide();
//           $('.commentContainer').show();
//           $('.commentContainer .commentTitle').html(`Comment by ${qaComments[i].userName} at ${qaComments[i].date}`);
//           $('.commentContainer p.commentReadonly').html(`${qaComments[i].comment}`);
//           $('#sendCommentContainer').css('display', 'none');

//           if (userCanManageFeedback == 'true') {
//             $('.optionsContainer').css('display', 'flex');
//             hideShowOptionButtons(qaComments[i].status);
//           }

//           if (qaComments[i].reply && qaComments[i].reply != '') {
//             textareaReply.hide();
//             $('.replyContainer').show();
//             $('.replyTextContainer').show();
//             $('.replyTextContainer .replyTitle').html(`Comment by ${qaComments[i].userName_reply} at ${qaComments[i].date_reply}`);
//             $('.replyTextContainer p.replyReadonly').html(`${qaComments[i].reply}`);
//             $('#sendReplyContainer').css('display', 'none');
//             $('#replyCommentBtn').hide();
//           } else {
//             textareaReply.show();
//             $('.replyContainer').hide();
//             $('.replyTextContainer').hide();
//             $('#sendReplyContainer').css('display', 'flex');
//             // $('#replyCommentBtn').show();
//           }
//           break;
//         }
//       } else {
//         textareaComment.show();
//         textareaComment.next().next('p.charCount').show();
//         $('.commentContainer').hide();
//         textareaComment.val('');
//         $('textarea[id="Reply"]').val('');
//         $('#sendCommentContainer').css('display', 'flex');
//         $('.optionsContainer').css('display', 'none');
//         hideShowOptionButtons('');
//         $('.replyContainer').hide();
//       }
//     }
//   } else {
//     $('.replyContainer').hide();
//   }
// }

function loadQACommentsIcons(ajaxURL, arrayName) {
  $.ajax({
    url: baseURL + ajaxURL,
    async: false,
    success: function (data) {
      if (data && Object.keys(data).length != 0) {
        newData = data[arrayName].map(function (x) {
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
    var commentIcon = $(`img.qaComment[name="${x[1]}"]`);
    commentIcon.attr('fieldID', `${x[0]}`);
    commentIcon.attr('description', `${x[2]}`);

    for (let i = 0; i < qaComments.length; i++) {
      if (x[1] == qaComments[i].frontName) {
        if (qaComments[i].comment != '') {
          commentIcon.attr('src', qaCommentsStatus('pending'));
          if (qaComments[i].reply != '') {
            commentIcon.attr('src', qaCommentsStatus('pending'));
          }
          // false
          if (qaComments[i].status == ' ') {
            commentIcon.attr('src', qaCommentsStatus('done'));
          } else if (qaComments[i].status != '') {
            // true
            commentIcon.attr('src', qaCommentsStatus('done'));
          }
        }
      }
    }
    commentIcon.show();
  });
}

function qaCommentsStatus(status) {
  switch (status) {
    case 'start':
      return `${baseURL}/global/images/comment.png`;
    case 'pending':
      return `${baseURL}/global/images/comment_yellow.png`;
    case 'done':
      return `${baseURL}/global/images/comment_green.png`;
    default:
      break;
  }
}

// Multiple comments-replies
function saveQAComment(comment, fieldID, name) {
  var finalAjaxURL = `/saveFeedbackComments.do?sectionName=${sectionName}&parentID=${parentID}&comment=${comment}&phaseID=${phaseID}&fieldID=${fieldID}&userID=${userID}&projectID=${projectID}`;

  $.ajax({
    url: baseURL + finalAjaxURL,
    async: false,
    success: function (data) {
      getQAComments();
      loadCommentsByUser(name);
      showQAComments(newData);
    }
  });
}

// Single comment-reply
// function saveQAComment(comment, fieldID, name) {
//   var finalAjaxURL = `/saveFeedbackComments.do?sectionName=${sectionName}&parentID=${parentID}&comment=${comment}&phaseID=${phaseID}&fieldID=${fieldID}&userID=${userID}`;

//   $.ajax({
//     url: baseURL + finalAjaxURL,
//     async: false,
//     success: function (data) {
//       getQAComments();
//       loadCommentsByUser(name);
//       showQAComments(newData);
//     }
//   });
// }

function saveFeedbackReply(reply, name) {
  var finalAjaxURL = `/saveFeedbackReply.do?reply=${reply}&commentID=${commentID}&userID=${userID}`;

  $.ajax({
    url: baseURL + finalAjaxURL,
    async: false,
    success: function (data) {
      getQAComments();
      loadCommentsByUser(name);
      showQAComments(newData);
    }
  });
}

function saveCommentStatus(status, name) {
  var finalAjaxURL = `/saveCommentStatus.do?status=${status}&commentID=${commentID}&userID=${userID}`;

  $.ajax({
    url: baseURL + finalAjaxURL,
    async: false,
    success: function (data) {
      getQAComments();
      loadCommentsByUser(name);
      showQAComments(newData);
    }
  });
}

function getQAComments() {
  var finalAjaxURL = `/feedbackComments2.do?sectionName=${sectionName}&parentID=${parentID}&phaseID=${phaseID}`;

  $.ajax({
    url: baseURL + finalAjaxURL,
    async: false,
    success: function (data) {
      if (data && Object.keys(data).length != 0) {
        qaComments = data['comments'];
        console.log(qaComments)
      }
    }
  });
}

/** FUNCTIONS * */

function loadMilestonesByYear(i, e) {
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
    success: function (data) {
      for (var i = 0, len = data.crpMilestones.length; i < len; i++) {
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
    success: function (data) {

      $item.find('.crpMilestoneYear').text(data.crpMilestone.year);
      $item.find('.crpMilestoneYearInput').val(data.crpMilestone.year);
      $item.find('.crpMilestoneValue').text(data.crpMilestone.value);
      // $item.find('.targetValue').attr('placeholder', data.crpMilestone.value);
      $item.find('.crpMilestoneTargetUnit').text(data.crpMilestone.targetUnitName);
      $item.find('.crpMilestoneTargetUnitInput').val(data.crpMilestone.targetUnit);
      $item.find('.title').text(data.crpMilestone.title);

      if (data.crpMilestone.targetUnit != -1) {
        $item.find('.milestoneTargetValue').show();
      }

      // Set indexes
      $item.find('.outcomeMilestoneYear').each(function (i, e) {
        $(e).setNameIndexes(1, milestonesCount);
        milestonesCount++;
      });

      // Update milestone
      $list.find('.milestoneYear').each(function (i, e) {
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

  $parent.hide('slow', function () {
    // Remove milestone block
    $parent.remove();

    // Update milestone
    $select.parents('.milestonesYearBlock').find('.milestoneYear').each(function (i, e) {
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
  $parent.hide('slow', function () {
    // Remove milestone block
    $parent.remove();

    // Update Next users list
    updateNextUsers();
  });
}

function updateNextUsers() {
  $("form .nextUser").each(function (i, e) {
    $(e).find('.index').text(i + 1);
    $(e).setNameIndexes(1, i);
  });
}

function formatState(state) {
  if (state.id != "-1") {
    var text = state.text.split(/-(.+)?/);
    var $state = $("<span><strong> Milestone for " + text[0] + "-</strong> " + text[1] + "</span>");
    return $state;
  } else {
    var $state = $("<span>" + state.text + "</span>");
    return $state;
  }

};
