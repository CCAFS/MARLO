$(document).ready(init);

var milestonesCount, outcomeID, textareaComment, parentID, projectID, phaseID, userID, link, userCanManageFeedback, userCanLeaveComments, isFeedbackActive, textareaReply, newData;
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
  projectID = $('#projectID').html();
  phaseID = $('#phaseID').html();
  userID = $('#userID').html();
  link = window.location.href;
  userCanManageFeedback = $('#userCanManageFeedback').html();
  userCanLeaveComments = $('#userCanLeaveComments').html();
  console.log('can comment', userCanLeaveComments, 'can reply', userCanManageFeedback);
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
    let qaPopup = $(`div[id^="qaPopup-${name}"]`);
    let block = $(`div[id^="qaCommentReply-${name}"]`);

    fieldID = $(this).attr('fieldID');

    block.each((index, item) => {
      if ($(item).attr('index') == 0) {
        $(item).find('textarea[id="Comment"]').prev('label').html(`Comment on "${popUpTitle}":`);
      }
      
      $(item).find('.sendCommentContainer').attr('name', `${name}[${index}]`);
      $(item).find('.agreeCommentBtn').attr('name', `${name}[${index}]`);
      $(item).find('.disagreeCommentBtn').attr('name', `${name}[${index}]`);
      $(item).find('.clarificationCommentBtn').attr('name', `${name}[${index}]`);
      $(item).find('.replyCommentBtn').attr('name', `${name}[${index}]`);
      $(item).find('.sendReplyContainer').attr('name', `${name}[${index}]`);
      $(item).find('div.addCommentContainer').attr('name', name);
    });

    loadCommentsByUser(name);

    if (event.pageX < 1000) {
      qaPopup.css('left', event.pageX);
    } else {
      qaPopup.css('left', 'min(100vw - 100px, 71vw)');
    }

    qaPopup.css('top', event.pageY);
    $('.qaPopup').hide().not(qaPopup);
    qaPopup.show();
  });

  $('div.closeComment').on('click', function () {
    let name = $(this).attr('name');
    let qaPopup = $(`div[id^="qaPopup-${name}"]`);
    qaPopup.hide();
  });

  $('div.sendCommentContainer').on('click', function () {
    let name = $(this).attr('name');
    let block = $(`div[id^="qaCommentReply-${name}"]`);
    let textarea = block.find('textarea[id="Comment"]');
    let value = textarea.val();
    let comment = textarea.next().html();
    let cleanComment;

    if (value && value != '') {
      cleanComment = value.replaceAll('.<br>.', '');
    } else {
      cleanComment = comment.replaceAll('.<br>.', '');
    }

    cleanComment = cleanComment.replaceAll('&nbsp;', ' ');

    if (cleanComment != '' && cleanComment != ' ') {
      textarea.css('border', '1px solid #ccc');
      saveQAComment(cleanComment, fieldID, name);
    } else {
      textarea.css('border', '2px solid red');
    }
  });

  $('img.disagreeCommentBtn').on('click', function () {
    let name = $(this).attr('name');
    let commentID = $(this).attr('commentId');
    let block = $(this).parent().parent();

    hideShowOptionButtons(block, '0');
    saveCommentStatus(0, commentID, name);
    $('img.replyCommentBtn').click();
  });

  $('img.agreeCommentBtn').on('click', function () {
    let name = $(this).attr('name');
    let commentID = $(this).attr('commentId');
    let block = $(this).parent().parent();

    hideShowOptionButtons(block, '1');
    saveCommentStatus(1, commentID, name);
  });

  $('img.clarificationCommentBtn').on('click', function () {
    let name = $(this).attr('name');
    let commentID = $(this).attr('commentId');
    let block = $(this).parent().parent();

    hideShowOptionButtons(block, '2');
    saveCommentStatus(2, commentID, name);
    $('img.replyCommentBtn').click();
  });

  $('img.replyCommentBtn').on('click', function () {
    let block = $(this).parent().parent().parent();

    block.find('.replyContainer').css('display', 'flex');
    block.find('.buttonsContainer').hide();
    block.find('.optionsContainer').hide();
  });

  $('div.sendReplyContainer').on('click', function () {
    let name = $(this).attr('name');
    let commentID = $(this).attr('commentId');
    let block = $(`div[id^="qaCommentReply-${name}"]`);
    let textarea = block.find('textarea[id="Reply"]');
    let value = textarea.val();
    let comment = textarea.next().html();
    let cleanComment;
    
    if (value && value != '') {
      cleanComment = value.replaceAll('.<br>.', '');
    } else {
      cleanComment = comment.replaceAll('.<br>.', '');
    }

    cleanComment = cleanComment.replaceAll('&nbsp;', ' ');
    
    if (cleanComment != '' && cleanComment != ' ') {
      textarea.css('border', '1px solid #ccc');
      saveFeedbackReply(cleanComment, commentID, name);
    } else {
      textarea.css('border', '2px solid red');
    }
  });

  $('div.addCommentContainer').on('click', function () {
    $(this).hide();
    let name = $(this).attr('name');
    let block = $(`div[id^="qaCommentReply-${name}"]`);
    block.find('.buttonsContainer').hide();
    let qaPopup = $(`div[id^="qaPopup-${name}"]`);
    let lastIndex = block.last().attr('index');
    lastIndex = parseInt(lastIndex) + 1;
    let commentReplyBlock = qaPopup.siblings('#qaTemplate').find('.qaPopup').children()[2];
    let newBlock = $(commentReplyBlock).clone(true).attr('id', `qaCommentReply-${name}[${lastIndex}]`);

    newBlock.attr('index', `${lastIndex}`);
    newBlock.find('.sendCommentContainer').attr('name', `${name}[${lastIndex}]`);
    newBlock.find('.sendReplyContainer').attr('name', `${name}[${lastIndex}]`);
    newBlock.find('.addCommentContainer').attr('name', `${name}`);
    newBlock.find('.addCommentContainer').attr('index', `${lastIndex}`);
    newBlock.appendTo(qaPopup).hide().show();
  });
}

function hideShowOptionButtons(block, status) {
  let textarea = block.find('textarea[id="Reply"]');

  switch (status) {
    case '0':
      textarea.prev().find('span.red.requiredTag').show();
      block.find('img.disagreeCommentBtn').hide();
      block.find('.commentContainer').css('background', '#e8a9a4');
      block.find('.replyTextContainer').css('background', '#e8a9a4');
      block.find('img.agreeCommentBtn').hide();
      block.find('img.clarificationCommentBtn').hide();
      break;
    case '1':
      textarea.prev().find('span.red.requiredTag').hide();
      block.find('img.agreeCommentBtn').hide();
      block.find('.commentContainer').css('background', '#a8eaab');
      block.find('.replyTextContainer').css('background', '#a8eaab');
      block.find('img.disagreeCommentBtn').hide();
      block.find('img.clarificationCommentBtn').hide();
      break;
    case '2':
      textarea.prev().find('span.red.requiredTag').show();
      block.find('img.clarificationCommentBtn').hide();
      block.find('.commentContainer').css('background', '#a4cde8');
      block.find('.replyTextContainer').css('background', '#a4cde8');
      block.find('img.agreeCommentBtn').hide();
      block.find('img.disagreeCommentBtn').hide();
      break;
    case '' || ' ':
      block.find('img.agreeCommentBtn').show();
      block.find('img.disagreeCommentBtn').show();
      block.find('img.clarificationCommentBtn').show();
      break;
    default:
      break;
  }
}

// Multiple comments-replies
function loadCommentsByUser(name) {
  // Removes the last index in brackets, i.e: [0]
  name = name.replace(/\[[^\]]*\]$/, '');

  if (qaComments.length > 0) {
    for (let i = 0; i < qaComments.length; i++) {
      if (qaComments[i].frontName == name) {
        let commentsLength = Object.keys(qaComments[i]).length;

        for (let j = 0; j < commentsLength; j++) {
          if (qaComments[i][j] !== undefined) {
            let block = $(`div[id^="qaCommentReply-${name}[${j}]"]`);

            if (j != 0) {
              block.find('textarea[id="Comment"]').prev().hide();
            }

            block.find('textarea[id="Comment"]').hide();
            block.find('textarea[id="Comment"]').next().next('p.charCount').hide();
            block.find('.commentContainer').show();
            block.find('.commentContainer .commentTitle').html(`Comment by ${qaComments[i][j].userName} at ${qaComments[i][j].date}`);
            block.find('.commentContainer p.commentReadonly').html(`${qaComments[i][j].comment}`);
            block.find('.sendCommentContainer').hide();
            block.find('.sendReplyContainer').attr('commentId', qaComments[i][j].commentId);
            block.find('.agreeCommentBtn').attr('commentId', qaComments[i][j].commentId);
            block.find('.disagreeCommentBtn').attr('commentId', qaComments[i][j].commentId);
            block.find('.clarificationCommentBtn').attr('commentId', qaComments[i][j].commentId);
            block.find('.replyCommentBtn').attr('commentId', qaComments[i][j].commentId);

            if (userCanLeaveComments == 'true') {
              let btnsContainer = block.find('.buttonsContainer');
              let addBtn = block.find('.addCommentContainer');
              const index = commentsLength - 2;

              if (addBtn.attr('index') == index) {
                btnsContainer.show();
                addBtn.show();
                let blockDup = $(`div[id="qaCommentReply-${name}[${j + 1}]"]`);

                if (blockDup.length != 0) {
                  addBtn.hide();
                }
              } else {
                addBtn.hide();
              }
            }

            if (userCanManageFeedback == 'true') {
              block.find('.buttonsContainer').show();
              block.find('.optionsContainer').css('display', 'flex');
            }
            
            hideShowOptionButtons(block, qaComments[i][j].status);

            let replyLength = Object.keys(qaComments[i][j].reply).length;

            if (replyLength !== 0) {
              block.find('textarea[id="Reply"]').parent().hide();
              block.find('.replyContainer').css('display', 'flex');
              block.find('.replyTextContainer').show();
              block.find('.replyTextContainer .replyTitle').html(`Reply by ${qaComments[i][j].reply['userName']} at ${qaComments[i][j].reply['date']}`);
              block.find('.replyTextContainer p.replyReadonly').html(`${qaComments[i][j].reply['text']}`);
              block.find('.buttonsContainer').hide();
              block.find('.replyCommentBtn').hide();
              block.find('.sendReplyContainer').hide();
              // block.find('.addCommentContainer').show();
            } else {
              if (qaComments[i][j].status && qaComments[i][j].status != '') {
                if (qaComments[i][j].status == '1') {
                  block.find('textarea[id="Reply"]').parent().show();
                  block.find('.replyContainer').hide();
                  block.find('.replyTextContainer').hide();
                  block.find('.replyCommentBtn').show();
                  block.find('.sendReplyContainer').show();
                  // block.find('.addCommentContainer').hide();
                } else {
                  block.find('textarea[id="Reply"]').parent().show();
                  block.find('.replyContainer').css('display', 'flex');
                  block.find('.replyTextContainer').hide();
                  block.find('.replyCommentBtn').hide();
                  block.find('.sendReplyContainer').show();
                  // block.find('.addCommentContainer').hide();
                }
              } else {
                // block.find('textarea[id="Reply"]').parent().show();
                // block.find('.replyContainer')..css('display', 'flex');
                // block.find('.replyTextContainer').hide();
                block.find('.replyCommentBtn').hide();
                // block.find('.sendReplyContainer').show();
                // block.find('.addCommentContainer').show();
              }
            }
          }
        }
      }
    }
  }
}

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
        let commentsLength = Object.keys(qaComments[i]).length;

        for (let j = 0; j < commentsLength; j++) {
          if (qaComments[i][j] != undefined) {
            if (qaComments[i][j].comment != '') {
              commentIcon.attr('src', qaCommentsStatus('pending'));

              if (qaComments[i][j].status != '') {
                if (qaComments[i][j].status == '0' || qaComments[i][j].status == '1') {
                  commentIcon.attr('src', qaCommentsStatus('done'));
                } else {
                  commentIcon.attr('src', qaCommentsStatus('pending'));
                }
              } else {
                commentIcon.attr('src', qaCommentsStatus('pending'));
              }
            }
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

function saveFeedbackReply(reply, commentID, name) {
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

function saveCommentStatus(status, commentID, name) {
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
