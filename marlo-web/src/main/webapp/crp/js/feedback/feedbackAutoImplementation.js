var textareaComment, parentID, projectID, phaseID, userID, userCanManageFeedback, userCanLeaveComments, isFeedbackActive, textareaReply, newData;
var sectionName = $('#sectionNameToFeedback').val();
var contributionCRPAjaxURL = `/fieldsBySectionAndParent.do?sectionName=${sectionName}`;
var arrayName = 'fieldsMap';
let fieldID = '';
let qaComments = '';
fieldsSections = [];

function feedbackAutoImplementation (){
  console.log('%c feedback auto implementation init', 'background: white; color: black');
  parentID = $('#parentID').html();
  projectID = $('#projectID').html();
  phaseID = $('#phaseID').html();
  userID = $('#userID').html();
  userCanManageFeedback = $('#userCanManageFeedback').html();
  userCanLeaveComments = $('#userCanLeaveComments').html();
  console.log('can comment', userCanLeaveComments, 'can reply', userCanManageFeedback);
  isFeedbackActive = $('#isFeedbackActive').html();
  attachEventsFeedback();
}

function attachEventsFeedback() {

    
  if (isFeedbackActive == 'true') {
    getQAComments();
    loadQACommentsIcons(contributionCRPAjaxURL, arrayName);
  }

  // Multiple comments-replies
  $('img.qaComment').on('click', function (event) {
    console.log(this);
    let name = this.name;
    console.log(this.name);
    let popUpTitle = $(this).attr('description');
    let qaPopup = $(`div[id^="qaPopup-${name}"]`);
    let block = $(`div[id^="qaCommentReply-${name}"]`);

    fieldID = $(this).attr('fieldID');

    block.each((index, item) => {
      if ($(item).attr('index') == 0) {
        $(item).find('textarea[id="New comment"]').prev('label').html(`Comment on "${popUpTitle}":`);
      }

     
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
    let textarea = block.find('textarea[id="New comment"]');
    let value = textarea.val();
    let comment = textarea.next().html();
    let cleanComment;

    if (value && value != '') {
      cleanComment = value.replaceAll('.<br>.', '');
    } else {
      cleanComment = comment?.replaceAll('.<br>.', '');
    }
   
    cleanComment = cleanComment ? cleanComment.replaceAll('&nbsp;', ' ') : '';

    if (cleanComment != '' && cleanComment != ' ') {
      textarea.css('border', '1px solid #ccc');
      saveQAComment(cleanComment, fieldID, name, this);
    } else {
      textarea.css('border', '2px solid red');
    }
  });

  $('img.disagreeCommentBtn').on('click', function () {
    let name = $(this).attr('name');
    let commentID = $(this).attr('commentId');
    let block = $(this).parent().parent().parent();

    hideShowOptionButtons(block, '0');
    saveCommentStatus(0, commentID, name);
    block.find('img.replyCommentBtn').click();
  });

  $('img.agreeCommentBtn').on('click', function () {
    let name = $(this).attr('name');
    let commentID = $(this).attr('commentId');
    let block = $(this).parent().parent().parent();

    hideShowOptionButtons(block, '1');
    saveCommentStatus(1, commentID, name);
  });

  $('div.deleteCommentBtn').on('click', function () {

    let name = $(this).attr('name');
    let commentID = $(this).attr('commentId');
    let block = $(this).parent().parent().parent();

    deleteQAComment(commentID, name, this);
  });

  $('div.deleteReplyBtn').on('click', function () {

    let name = $(this).attr('name');
    let commentID = $(this).attr('replyId');
    let block = $(this).parent().parent().parent();

    deleteQAReply(commentID, name, this);
  });

  $('img.clarificationCommentBtn').on('click', function () {
    let name = $(this).attr('name');
    let commentID = $(this).attr('commentId');
    let block = $(this).parent().parent().parent();

    hideShowOptionButtons(block, '2');
    saveCommentStatus(2, commentID, name);
    block.find('img.replyCommentBtn').click();
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
    newBlock.find('.deleteCommentBtn').attr('name', `${name}[${lastIndex}]`);
    
    newBlock.appendTo(qaPopup).hide().show();
  });
}

function hideShowOptionButtons(block, status) {
    let textarea = block.find('textarea[id="Reply"]');
  
    switch (status) {
      case '0':
        textarea.prev().find('span.red.requiredTag').show();
        // block.find('.buttonsContainer').hide();
        block.find('img.disagreeCommentBtn').hide();
        block.find('.commentContainer').css('background', '#e8a9a4');
        block.find('.replyTextContainer').css('background', '#e8a9a4');
        block.find('img.agreeCommentBtn').hide();
        block.find('div.deleteCommentBtn').hide();
        block.find('img.clarificationCommentBtn').hide();
        break;
      case '1':
        textarea.prev().find('span.red.requiredTag').hide();
        block.find('img.agreeCommentBtn').hide();
        block.find('.commentContainer').css('background', '#a8eaab');
        block.find('.replyTextContainer').css('background', '#a8eaab');
        block.find('img.disagreeCommentBtn').hide();
        block.find('img.clarificationCommentBtn').hide();
        block.find('div.deleteCommentBtn').hide();

        break;
      case '2':
        textarea.prev().find('span.red.requiredTag').show();
        // block.find('.buttonsContainer').hide();
        block.find('img.clarificationCommentBtn').hide();
        block.find('.commentContainer').css('background', '#a4cde8');
        block.find('.replyTextContainer').css('background', '#a4cde8');
        block.find('img.agreeCommentBtn').hide();
        block.find('img.disagreeCommentBtn').hide();
        block.find('div.deleteCommentBtn').hide();

        break;
      case '' || ' ':
        block.find('img.agreeCommentBtn').show();
        block.find('img.disagreeCommentBtn').show();
        block.find('img.clarificationCommentBtn').show();
        block.find('div.deleteCommentBtn').show();
        break;
      default:
        break;
    }
}
  
  // Multiple comments-replies
  function loadCommentsByUser(name) {
    try {

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
                block.find('textarea[id="New comment"]').prev().hide();
              }

              if (!block.exists()) {
                 block = $(`div[id^="qaPopup-${name}["]`).find('.qaCommentReplyBlock')
              }
              // div[id^="qaCommentReply-deliverable.deliverableInfo.title[0]"]
              console.log("load ids")

              block.find('textarea[id="New comment"]').hide();
              block.find('textarea[id="New comment"]').next().next('p.charCount').hide();
              block.find('.commentContainer').show();
              block.find('.commentContainer .commentTitle').html(`Comment by ${qaComments[i][j].userName} at ${qaComments[i][j].date}`);
              block.find('.commentContainer p.commentReadonly').html(`${qaComments[i][j].comment}`);
              block.find('.sendCommentContainer').hide();
              if (qaComments[i][j].userID != userID) block.find('.deleteCommentBtn').remove();
              if (qaComments[i][j].reply.userID == userID) block.find('.deleteReplyBtn').show();
              block.find('.deleteCommentBtn').attr('commentId', qaComments[i][j].commentId);
              block.find('.deleteReplyBtn').attr('replyId', qaComments[i][j].reply.id);
              block.find('.sendReplyContainer').attr('commentId', qaComments[i][j].commentId);
              block.find('.agreeCommentBtn').attr('commentId', qaComments[i][j].commentId);
              block.find('.disagreeCommentBtn').attr('commentId', qaComments[i][j].commentId);
              block.find('.clarificationCommentBtn').attr('commentId', qaComments[i][j].commentId);
              block.find('.replyCommentBtn').attr('commentId', qaComments[i][j].commentId);

              
              if(qaComments[i][j].status) {               
                  block.find('.containerReactionComment').show();
                  block.find('.containerReactionComment p.reactionComment').html(reactionName(qaComments[i][j].status)+`${qaComments[i][j].approvalUserName} at ${qaComments[i][j].approvalDate}`);                 
              }else{
                block.find('.containerReactionComment').hide();
              }
              //////////////////////////////////////////////////////////////////
              //waiting for endpoint to show clarificationCommentBtn
              if(false){
                block.find('.optionsContainer .clarificationCommentBtn').remove();
              }
              ///////////////////////////////////////////////////////////////
              
              if (userCanLeaveComments == 'true') {
                let btnsContainer = block.find('.buttonsContainer');
                let addBtn = block.find('.addCommentContainer');
                const index = commentsLength - 2;
  
                if (addBtn.attr('index') == index) {
                  btnsContainer.show();
                  addBtn.show();
  
                  let blockDup = $(`div[id="qaCommentReply-${name}[${j + 1}]"]`);
  
                  if (blockDup.length != 0) {
                    btnsContainer.hide();
                    addBtn.hide();
                  }
                } else {
                  btnsContainer.hide();
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
                // block.find('.buttonsContainer').hide();
                block.find('.replyCommentBtn').hide();
                block.find('.sendReplyContainer').hide();
              } else {
                if (qaComments[i][j].status && qaComments[i][j].status != '') {
                  if (qaComments[i][j].status == '1') {
                    block.find('textarea[id="Reply"]').parent().show();
                    block.find('.replyContainer').hide();
                    block.find('.replyTextContainer').hide();
                    block.find('.replyCommentBtn').show();
                    block.find('.sendReplyContainer').show();
                  } else {
                    block.find('textarea[id="Reply"]').parent().show();
                    block.find('.replyContainer').css('display', 'flex');
                    block.find('.replyTextContainer').hide();
                    block.find('.replyCommentBtn').hide();
                    block.find('.sendReplyContainer').show();
                  }
                } else {
                  block.find('.replyCommentBtn').hide();
                }
              }
            }
          }
        }
      }
    }
    } catch (error) {
      console.log(error)
      getQAComments();
    }
  }

  runaddfeedbackFlexItemsClass = true;
  function addfeedbackFlexItemsClass(fieldsMap){
    if (!runaddfeedbackFlexItemsClass) return;
    fieldsMap.map(field=>{
      if ($(`[name="${field.fieldName}"]`).closest('.fieldReference').length ==2) {
        $(`[name="${field.fieldName}"]`).closest('.fieldReference').first().next().remove();
      }
      let fieldReference = $(`[name="${field.fieldName}"]`).closest('.fieldReference').exists() == true ?  $(`[name="${field.fieldName}"]`).closest('.fieldReference').last()  : $(`[name="${field.fieldName}[]"]`).closest('.fieldReference').last() ;
      fieldReference.appendTo(fieldReference.prev());
      fieldReference.closest('.feedback-flex-items').next().appendTo(fieldReference.closest('.feedback-flex-items'))
    })
    runaddfeedbackFlexItemsClass = false;
  }
  
  function loadQACommentsIcons(ajaxURL, arrayName) {
    $.ajax({
      url: baseURL + ajaxURL,
      async: false,
      success: function (data) {
        
        fieldsSections = data?.fieldsMap;
        addfeedbackFlexItemsClass(fieldsSections);
        if ((userCanLeaveComments == 'true') || (userCanManageFeedback == 'true' && qaComments.length > 0)) {
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
      }
    });
  }
  
  function showQAComments(data) {
    data.map(function (field) {
      var commentIcon = $(`img.qaComment[name="${field[1]}"]`);
      commentIcon.attr('fieldID', `${field[0]}`);
      commentIcon.attr('description', `${field[2]}`);

      
     let block = $(`div[id^="qaCommentReply-${field[1]}"]`);

     block.each((index, item) => {
        
  
      $(item).find('.deleteCommentBtn').attr('name', `${name}[${index}]`);
      $(item).find('.deleteReplyBtn').attr('name', `${name}[${index}]`);
      $(item).find('.sendCommentContainer').attr('name', `${name}[${index}]`);
      $(item).find('.agreeCommentBtn').attr('name', `${name}[${index}]`);
      $(item).find('.disagreeCommentBtn').attr('name', `${name}[${index}]`);
      $(item).find('.clarificationCommentBtn').attr('name', `${name}[${index}]`);
      $(item).find('.replyCommentBtn').attr('name', `${name}[${index}]`);
      $(item).find('.sendReplyContainer').attr('name', `${name}[${index}]`);
      $(item).find('div.addCommentContainer').attr('name', name);
       
      });

      let qaCommentFinded = qaComments.find(qaComment => qaComment.frontName == field[1]);
      if (qaCommentFinded) {
        let allFieldsdone = true;
        getNumberOfComments(qaCommentFinded.frontName);
        Object.keys(qaCommentFinded).map(keycomment=>{
          if (qaCommentFinded[keycomment] == qaCommentFinded.frontName) return ;
          if (qaCommentFinded[keycomment].status === "") allFieldsdone = false;
        })
        commentIcon.attr('src', qaCommentsStatus(allFieldsdone ? 'done' : 'pending'))
      }

      const currentqaComments = qaComments.filter(qaCommentsFilter => qaCommentsFilter.frontName == field[1])
      if (userCanLeaveComments == 'true' || currentqaComments.length>=1) {
        commentIcon.show();
        commentIcon.parent().css('display', 'flex');
      }
   
            

      // for (let i = 0; i < qaComments.length; i++) {
        
      //   if (x[1] == qaComments[i].frontName) {
      //     getNumberOfComments(x[1]);
      //     let commentsLength = Object.keys(qaComments[i]).length;
      //     commentIcon.attr('src', qaCommentsStatus('done'));
      //     // for (let j = 0; j < commentsLength; j++) {
      //     //   if (qaComments[i][j] != undefined) {
      //     //     if (qaComments[i][j].comment != '') {
      //     //       commentIcon.attr('src', qaCommentsStatus('pending'));
  
      //     //       if (qaComments[i][j].status != '') {
      //     //         console.log(qaComments)
      //     //         if (qaComments[i][j].status == '1') {
      //     //           commentIcon.attr('src', qaCommentsStatus('done'));
      //     //         } else {
      //     //           if (Object.keys(qaComments[i][j].reply).length != 0) {
      //     //             commentIcon.attr('src', qaCommentsStatus('done'));
      //     //           } else {
      //     //             commentIcon.attr('src', qaCommentsStatus('pending'));
      //     //           }
      //     //         }
      //     //       } else {
      //     //         commentIcon.attr('src', qaCommentsStatus('pending'));
      //     //       }
      //     //     }
      //     //   }
      //     // }
      //     commentIcon.show();
      //     commentIcon.parent().css('display', 'flex');
      //   }
      // }
      // if (userCanLeaveComments == 'true') {
      //   commentIcon.show();
      //   commentIcon.parent().css('display', 'flex');
      // }

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
  function saveQAComment(comment, fieldID, name, reference) {
    let indexToCute = $(reference).attr("name").substring(0,$(reference).attr("name").length-3);
    let objectField = fieldsSections.find(field => field.fieldName == indexToCute)
    let inputValue = $(`input[name="${objectField.parentFieldDescription}"]`).val()
    var finalAjaxURL = `/saveFeedbackComments.do?sectionName=${sectionName}&parentID=${parentID}&comment=${comment}&phaseID=${phaseID}&fieldID=${fieldID}&userID=${userID}&projectID=${projectID}&parentFieldDescription=${inputValue}`;
  
    $.ajax({
      url: baseURL + finalAjaxURL,
      async: false,
      success: function (data) {
        getQAComments();
        loadCommentsByUser(name);
        loadQACommentsIcons(contributionCRPAjaxURL, arrayName);
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
        loadQACommentsIcons(contributionCRPAjaxURL, arrayName);
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
        loadQACommentsIcons(contributionCRPAjaxURL, arrayName);
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
          getNameBtns();
        }
      }
    });
  }

  function deleteQAReply(commentID, name, htmlParent) {
    var finalAjaxURL = `/deleteReply.do?commentID=${commentID}`;
    $.ajax({
      url: baseURL + finalAjaxURL,
      async: false,
      success: function (data) {

        // if (!data?.delete?.delete) return;

        let qaCommentReplyBlock = $(htmlParent).closest('.qaCommentReplyBlock');
        qaCommentReplyBlock.find('.commentContainer').css('background','white');
        qaCommentReplyBlock.find('.replyContainer').find('.replyTextContainer').hide();

        qaCommentReplyBlock.find('.agreeCommentBtn').show();
        qaCommentReplyBlock.find('.disagreeCommentBtn').show();
        qaCommentReplyBlock.find('.clarificationCommentBtn').show();
        qaCommentReplyBlock.find('.deleteCommentBtn').show();

        getQAComments();
        loadCommentsByUser(name);
        loadQACommentsIcons(contributionCRPAjaxURL, arrayName);

      }
    });
  }
  
  function deleteQAComment(commentID, name, htmlParent) {
    var finalAjaxURL = `/deleteComment.do?commentID=${commentID}`;
    $.ajax({
      url: baseURL + finalAjaxURL,
      async: false,
      success: function (data) {

        if (!data?.delete?.delete) return;

        let qaPopup = $(htmlParent).closest('.qaPopup');

        // if (qaPopup.find('.qaCommentReplyBlock').length == 1 )parent.find('.addCommentContainer').trigger("click");
        
        if (qaPopup.find('.qaCommentReplyBlock').length == 1 ) {
          qaPopup.find('.qaCommentReplyBlock').last().find('.sendCommentContainer').show();
          qaPopup.find('.qaCommentReplyBlock').last().find('.textArea').find('textarea').show();
          qaPopup.find('.qaCommentReplyBlock').last().find('.commentContainer').hide();
          qaPopup.find('.qaCommentReplyBlock').last().find('.buttonsContainer').hide();
          qaPopup.find('.qaCommentReplyBlock').last().find('.addCommentContainer').hide();
          qaPopup.find('.qaCommentReplyBlock').last().find('.charCount').show();
          qaPopup.find('.qaCommentReplyBlock').last().find('textarea').val('');      
          getQAComments();
          loadCommentsByUser(name);
          loadQACommentsIcons(contributionCRPAjaxURL, arrayName);
           return;
        }
        qaPopup.find('.qaCommentReplyBlock').last().remove();
        qaPopup.find('.qaCommentReplyBlock').last().find('.addCommentContainer').show();

        getQAComments();
        loadCommentsByUser(name);
        loadQACommentsIcons(contributionCRPAjaxURL, arrayName);
        

      }
    });
  }

  function getNumberOfComments(name) {
    var finalAjaxURL = `/getCommentStatus.do?sectionName=${sectionName}&parentID=${parentID}&phaseID=${phaseID}&fieldDescription=${name}`;
  
    $.ajax({
      url: baseURL + finalAjaxURL,
      async: false,
      success: function (data) {
        if (data && Object.keys(data).length != 0) {
          newData = data['comments'].map(function (x) {
            var arr = [];
            arr.push(x.answeredComments);
            arr.push(x.totalComments);
            return arr;
          });
          loadNumberOfComments(name, newData);
        }
      }
    });
  }
  
  function loadNumberOfComments(name, data) {
    data.map(function (x) {
      let p = $(`img.qaComment[name="${name}"]`).prev().find('p');
      p.css('display', 'block');
      if(x[0] == x[1]) p.css('border', '2px solid #8dc02c');
      if(x[0] != x[1]) p.css('border', '2px solid #ffffff00');
      p.html(`${x[0]}/${x[1]}`);
    });
  }


  function reactionName(status) {
    switch (status) {
      case "0":
        return 'Disagreed by ';
      case "1":
        return 'Accepted by ';
      case "2":
        return 'Required clarification by ';
    }
  }

  function getNameBtns() {

    console.log($('img.qaComment'));

    $('img.qaComment').each((index, item) =>{
      console.log(item)
    })

  }