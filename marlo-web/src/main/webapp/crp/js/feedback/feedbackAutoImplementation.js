var textareaComment, parentID, projectID, phaseID, userID, userCanManageFeedback, userCanLeaveComments, isFeedbackActive, textareaReply, newData;
var sectionName = $('#sectionNameToFeedback').val();
var contributionCRPAjaxURL = `/fieldsBySectionAndParent.do?sectionName=${sectionName}`;
var arrayName = 'fieldsMap';
let fieldID = '';
let qaComments = '';
let nameNewComment = '';
let descriptionComment = '';
fieldsSections = [];

function feedbackAutoImplementation() {
	parentID = $('#parentID').html();
	projectID = $('#projectID').html();
	phaseID = $('#phaseID').html();
	userID = $('#userID').html();
	userCanManageFeedback = $('#userCanManageFeedback').html();
	userCanLeaveComments = $('#userCanLeaveComments').html();
	userCanApproveFeedback = $('#userCanApproveFeedback').html();
	usercanTrackComments = $('#canTrackComments').html();
	console.log('userCanManageFeedback: ' + userCanManageFeedback)
	console.log('userCanLeaveComments: ' + userCanLeaveComments)
	console.log('userCanApproveFeedback: ' + userCanApproveFeedback)
	isFeedbackActive = $('#isFeedbackActive').html();
	attachEventsFeedback();

	// Get section id from URL
	var identificador = window.location.hash.substring(1); // Remove the # symbol


	// If an identifier exists and it corresponds to a section on the page
	if (identificador && $("#" + identificador).length) {
		// Get the section element
		var seccion = $("#" + identificador);

		// Get the exact position of the target element relative to the document
		var seccionOffset = seccion.offset();

		//Validate if the seccion position is visible, could be display:none at that moment because is in other tab
		if (seccionOffset.left === 0 || seccionOffset.top === 0) {
			// Get parent tab-container where info is located
			const $parentTab = $(seccion).closest('.tab-pane').first();
			// Get the tab selector associated to the tab-container id
			const $parentTabSelector = $(`[href="#${$parentTab.attr('id')}"]`).closest('li');

			//Get all the tabs selector available
			const $tabsSelector = $('[role="presentation"]');
			//Remove all possible selector being selected
			$tabsSelector.removeClass("active");
			//Add selected to selector where the message is display
			$parentTabSelector.addClass("active");

			//Get all the tab-container available in HTML
			const $tabsContent = $('.tab-pane');
			//Remove all possible information being display
			$tabsContent.removeClass('in active');
			//Add visualization to information where the message is display
			$parentTab.addClass('in active');

			//Recalculate position of the section where the message is attach
			seccionOffset = seccion.offset();

		}

		// Calculate the coordinates to open the qaPopup centered on the target element
		var popupLeft = seccionOffset.left + (seccion.outerWidth() / 2);
		var popupTop = seccionOffset.top + (seccion.outerHeight() / 2);

		let name = seccion.attr('name');
		nameNewComment = seccion.attr('name');
		let popUpTitle = $(seccion).attr('description');
		let containerQaPopup = $(`div[id^="containerQaPopup-${name}"]`);
		let qaPopup = containerQaPopup.find('.qaPopup')
		let block = $(`div[id^="qaCommentReply-${name}"]`);
		descriptionComment = popUpTitle;
		fieldID = $(seccion).attr('fieldID');
		block.each((index, item) => {
			if ($(item).attr('index') == 0) {
				$(".titleQaPopup").html(`Comment on ${popUpTitle}`);
			}
		});
		$('textarea[id="New comment"]').prev('label').hide();

		loadCommentsByUser(name);

		if (popupLeft < 1000) {
			containerQaPopup.css('left', popupLeft);
		} else {
			containerQaPopup.css('left', popupLeft - 480);
		}

		containerQaPopup.css('top', popupTop + 25);
		// $('.qaPopup').hide().not(qaPopup);
		containerQaPopup.show();

		// Get the height of the browser window
		var windowHeight = $(window).height();

		// Get the top position of the element
		var seccionTop = seccion.offset().top;

		// Calculate scroll position to center section in window
		var scrollToPosition = seccionTop - (windowHeight / 2);

		// Make the scroll smooth to the centered section
		$("html, body").animate({
			scrollTop: scrollToPosition
		}, 1000); // 1000 is the duration of the animation in milliseconds
	}

}

function attachEventsFeedback() {


	if (isFeedbackActive == 'true') {
		getQAComments();
		loadQACommentsIcons(contributionCRPAjaxURL, arrayName);
	}


	$('.track_icon').click(function() {
		var currentSrc = $(this).attr('src');
		let commentID = $(this).attr('commentId');
		let name = $(this).attr('name');

		if (currentSrc === `${baseURL}/global/images/tracking.png`) {
			$(this).fadeToggle(500, function() {
				$(this).attr('src', `${baseURL}/global/images/yellow_tracking.png`);
				$(this).attr('title', `Stop tracking comment`);
				$(this).fadeToggle(500);
				var $newDiv = $("<div>").addClass("customDiv");
				$newDiv.css({
					position: "absolute",
					top: $(this).parent().parent().parent().parent().offset().top - 120,
					left: $(this).parent().parent().parent().parent().offset().left,
					width: $(this).parent().parent().parent().parent().outerWidth(),
					"z-index": 10000
				});


				var $containerAlert = $("<div>").addClass("animated flipInX  viewMore-block containerAlertMarginTracking");
				$containerAlert.html(`
          <div class="containerAlert alert-leftovers alertColorBackgroundInfo" id="containerAlert" >
            <div class="containerLine alertColorInfo"></div>
            <div class="closeAlertTracking">X</div>
            <div class="containerIcon">
              <div class="containerIcon">
                <img class="trackingImg" src="${baseURL}/global/images/icon-info2.png" />         
              </div>
            </div>
            <div class="containerText col-md-12 alertCollapse">
              <p class="alertText">
              You will receive an email once the comment has a reaction.
              </p>
            </div>
          </div>
        `);

				$containerAlert.css({
					width: "100% !important"
				});

				$newDiv.append($containerAlert);
				$("body").prepend($newDiv);

				$(".closeAlertTracking").click(function() {
					$newDiv.fadeOut(1000, function() {
						$(this).remove();
					});
				});

				setTimeout(function() {
					$newDiv.fadeOut(1000, function() {
						$(this).remove();
					});
				}, 4000);
			});
			saveTrackComment(1, commentID, name);
		} else if (currentSrc === `${baseURL}/global/images/yellow_tracking.png`) {
			$(this).fadeToggle(500, function() {
				$(this).attr('src', `${baseURL}/global/images/tracking.png`);
				$(this).attr('title', `Track your comment`);
				$(this).fadeToggle(500);
			});
			saveTrackComment(0, commentID, name);
		}
	});


	// Multiple comments-replies
	$('img.qaComment').on('click', function(event) {
		let name = this.name;
		nameNewComment = this.name;
		let popUpTitle = $(this).attr('description');
		let containerQaPopup = $(`div[id^="containerQaPopup-${name}"]`);
		let block = $(`div[id^="qaCommentReply-${name}"]`);
		descriptionComment = popUpTitle;
		fieldID = $(this).attr('fieldID');

		block.each((index, item) => {
			if ($(item).attr('index') == 0) {
				$(item).find('textarea[id="New comment"]').prev('label').html(`Comment on "${popUpTitle}":`);
				$(".titleQaPopup").html(`Comment on ${popUpTitle}`);
			}
		});
		$('textarea[id="New comment"]').prev('label').hide();

		loadCommentsByUser(name);

		if (event.pageX < 1000) {
			containerQaPopup.css('left', event.pageX);
		} else {
			containerQaPopup.css('left', event.pageX - 500);
		}
		containerQaPopup.css('top', event.pageY + 25);

		// Ocultar otros popups y luego mostrar el popup deseado con una animación de fadeIn
		$('.containerQaPopup').not(containerQaPopup).fadeOut(400);
		containerQaPopup.fadeIn(400);
	});


	$('div.closeComment').on('click', function() {
		let name = $(this).attr('name');
		let qaPopup = $(`div[id^="containerQaPopup-${name}"]`);
		qaPopup.hide();
	});

	$('div.sendCommentContainer').on('click', function() {
		var $sendCommentImg = $(this).find('img.sendComment');
		var originalSrc = $sendCommentImg.attr('src');

		$sendCommentImg.attr('src', `${baseURL}/global/images/cargando.gif`);

		let name = $(this).attr('name');
		sendNewComment(name);

		// Restore the original src after submitting the comment
		$sendCommentImg.attr('src', originalSrc);
	});

	$('img.disagreeCommentBtn').on('click', function() {
		let name = $(this).attr('name');
		let commentID = $(this).attr('commentId');
		let block = $(this).parent().parent().parent();

		hideShowOptionButtons(block, '0');
		saveCommentStatus(0, commentID, name);
		block.find('img.replyCommentBtn').click();
	});

	$('img.agreeCommentBtn').on('click', function() {
		let name = $(this).attr('name');
		let commentID = $(this).attr('commentId');
		let block = $(this).parent().parent().parent();

		hideShowOptionButtons(block, '1');
		saveCommentStatus(1, commentID, name);
		block.find('img.replyCommentBtn').click();
	});

	$('div.deleteCommentBtn').on('click', function() {

		let name = $(this).attr('name');
		let commentID = $(this).attr('commentId');
		let block = $(this).parent().parent().parent();
		let nameCut = name.substring(0, name.length - 3)

		deleteQAComment(commentID, name, this);
		getNumberOfComments(nameCut);
	});

	$('div.containerSentCommentBtn').on('click', function() {

		let name = $(this).attr('name');
		let commentID = $(this).attr('commentId');
		let block = $(this).parent().parent().parent();
		let blockContainer = block.parent().parent();
		let editComment = $(`textarea[commentID="${commentID}"].editCommentReadonly`).val();
		let editCommentReadonly = $(`textarea[commentID="${commentID}"].editCommentReadonly`);
		let senNewComment = blockContainer.find('div[class="sendCommentContainer"]');
		let nweTextarea = blockContainer.find('textarea[id="New comment"]');

		if (editComment != '' && editComment != ' ') {
			showEditComment(block, commentID, 2);
			updateComment(editComment, fieldID, name, this, commentID);
			editCommentReadonly.css('border', '1px solid #ccc');
		} else {
			editCommentReadonly.css('border', '2px solid red');
		}

		nweTextarea.prop('disabled', false);
		senNewComment.css({
			'background-color': '#0b7ba6',
			'pointer-events': 'auto'
		})
	});

	$('div.deleteReplyBtn').on('click', function() {

		let name = $(this).attr('name');
		let commentID = $(this).attr('replyId');
		let block = $(this).parent().parent().parent();

		deleteQAReply(commentID, name, this);
	});

	$('img.clarificationCommentBtn').on('click', function() {
		let name = $(this).attr('name');
		let commentID = $(this).attr('commentId');
		let block = $(this).parent().parent().parent();

		hideShowOptionButtons(block, '2');
		saveCommentStatus(2, commentID, name);
		block.find('img.replyCommentBtn').click();
	});

	$('img.correctCommentBtn').on('click', function() {
		let name = $(this).attr('name');
		let commentID = $(this).attr('commentId');
		let block = $(this).parent().parent().parent();
		let feedback_assesor_input = block.find('.commentContainer').attr('comment');
		let feedback_assesor_name = block.find('.commentContainer').attr('username');
		let feedback_assesor_email = block.find('.commentContainer').attr('email');
		let isTracking = block.find('.commentContainer').attr('isTracking');
		let feedback_comment_reaction = 'Admitted';

		if (isTracking == 'true') {
			sendFeedbackActionEmail(feedback_assesor_input, feedback_assesor_name, feedback_assesor_email, feedback_comment_reaction, this);
		}
		hideShowOptionButtons(block, 1);
		saveCommentStatus(4, commentID, name);
	});

	$('img.dismissCommentBtn').on('click', function() {
		let name = $(this).attr('name');
		let commentID = $(this).attr('commentId');
		let block = $(this).parent().parent().parent();
		let feedback_assesor_input = block.find('.commentContainer').attr('comment');
		let feedback_assesor_name = block.find('.commentContainer').attr('username');
		let feedback_assesor_email = block.find('.commentContainer').attr('email');
		let isTracking = block.find('.commentContainer').attr('isTracking');
		let feedback_comment_reaction = 'Dismissed';

		if (isTracking == 'true') {
			sendFeedbackActionEmail(feedback_assesor_input, feedback_assesor_name, feedback_assesor_email, feedback_comment_reaction, this);
		}
		saveTrackComment(0, commentID, name);
		hideShowOptionButtons(block, '6');
		saveCommentStatus(6, commentID, name);
		block.find('img.replyCommentBtn').click();
	});

	$('.editCommentBtn').on('click', function() {
		let name = $(this).attr('name');
		let commentID = $(this).attr('commentId');
		let block = $(this).parent().parent().parent();
		let blockContainer = block.parent().parent();
		let senNewComment = blockContainer.find('div[class="sendCommentContainer"]');
		let textarea = blockContainer.find('textarea[id="New comment"]');

		showEditComment(block, commentID, 1);
		textarea.prop('disabled', true);
		senNewComment.css({
			'background-color': '#afafaf',
			'pointer-events': 'none'
		})
	});

	$('img.replyCommentBtn').on('click', function() {
		let name = $(this).attr('name');
		let block = $(this).parent().parent().parent();
		let blockContainer = block.parent().parent();
		let senNewComment = blockContainer.find('div[class="sendCommentContainer"]');
		let textarea = blockContainer.find('textarea[id="New comment"]');

		block.find('.replyContainer').css('display', 'flex');
		block.find('.buttonsContainer').hide();
		block.find('.optionsContainer').hide();

		textarea.prop('disabled', true);
		senNewComment.css({
			'background-color': '#afafaf',
			'pointer-events': 'none'
		})
	});

	$('div.sendReplyContainer').on('click', function() {
		let name = $(this).attr('name');
		let commentID = $(this).attr('commentId');
		let block = $(`div[id^="qaCommentReply-${name}"]`);
		let blockContainer = block.parent().parent();
		let textarea = block.find('textarea[id="Reply"]');
		let value = textarea.val();
		let comment = textarea.next().html();
		let cleanComment;
		let feedback_assesor_input = block.find('.commentContainer').attr('comment');
		let feedback_assesor_name = block.find('.commentContainer').attr('username');
		let feedback_assesor_email = block.find('.commentContainer').attr('email');
		let isTracking = block.find('.commentContainer').attr('isTracking');
		let feedback_comment_reaction = block.find('.commentContainer').attr('status');
		let senNewComment = blockContainer.find('div[class="sendCommentContainer"]');
		let nweTextarea = blockContainer.find('textarea[id="New comment"]');

		const statusMapping = {
			'0': 'Disagreed',
			'1': 'Accepted',
			'2': 'Required clarification'
		};

		feedback_comment_reaction = statusMapping[feedback_comment_reaction] || feedback_comment_reaction;

		if (isTracking == 'true') {
			sendFeedbackReactionEmail(feedback_assesor_input, feedback_assesor_name, feedback_assesor_email, feedback_comment_reaction, currentUserName, value, this)
		}

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


		nweTextarea.prop('disabled', false);
		senNewComment.css({
			'background-color': '#0b7ba6',
			'pointer-events': 'auto'
		})
	});

	$('div.addCommentContainer').on('click', function() {
		$(this).hide();
		let name = $(this).attr('name');
		let block = $(`div[id^="qaCommentReply-${name}"]`);
		block.find('.buttonsContainer').hide();
		// let qaPopup = $(`div[id^="qaPopup-${name}"]`);
		let containerQaPopup = $(`div[id^="containerQaPopup-${name}"]`);
		let lastIndex = block.last().attr('index');
		lastIndex = parseInt(lastIndex) + 1;
		let commentReplyBlock = containerQaPopup.siblings('#qaTemplate').find('.containerQaPopup').children()[2];
		let newBlock = $(commentReplyBlock).clone(true).attr('id', `qaCommentReply-${name}[${lastIndex}]`);
		let editCommentReadonly = $(`.editCommentReadonly`);
		let commentReadonly = $(`p.commentReadonly`);

		newBlock.attr('index', `${lastIndex}`);
		newBlock.find('.sendCommentContainer').attr('name', `${name}[${lastIndex}]`);
		newBlock.find('.sendReplyContainer').attr('name', `${name}[${lastIndex}]`);
		newBlock.find('.addCommentContainer').attr('name', `${name}`);
		newBlock.find('.addCommentContainer').attr('index', `${lastIndex}`);
		newBlock.find('.deleteCommentBtn').attr('name', `${name}[${lastIndex}]`);
		newBlock.find('.containerSentCommentBtn').attr('name', `${name}[${lastIndex}]`);

		newBlock.appendTo(qaPopup).hide().show();
		editCommentReadonly.hide();
		commentReadonly.show();
		block.find('.correctCommentBtn').show();
		block.find('.dismissCommentBtn').show();
		block.find('.editCommentBtn').show();
		// block.find('.editCommentBtn').show();
	});

  $(document).off('click', '.deleteActualReplyBtn').on('click', '.deleteActualReplyBtn', function(event) {
         event.preventDefault(); 
         let $clickedButton = $(this); 
         let replyIdToDelete = $clickedButton.data('reply-id');
         let commentNameForContext = $clickedButton.data('comment-name');

         if (!replyIdToDelete || !commentNameForContext) {
             console.error("MARLO Feedback: Missing replyId or commentName for deleting reply. Button:", $clickedButton);
             alert("Error: Could not identify the reply to delete.");
             return;
         }

         if (confirm('Are you sure you want to delete this reply? This action cannot be undone.')) {
             $clickedButton.prop('disabled', true).text('Deleting...');
             deleteQAReply(replyIdToDelete, commentNameForContext, this);
         }
     });

}

function addNewComment() {
	let name = nameNewComment;
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
	newBlock.find('.containerSentCommentBtn').attr('name', `${name}[${lastIndex}]`);

	if (block.last().attr('newComment') != 'true') {
		newBlock.attr('newComment', 'true');
		newBlock.appendTo(qaPopup).hide().show();
	}

}


function sendNewComment(name) {

	let block = $(`div[id^="containerQaPopup-${name}"]`);
	let textarea = block.find('textarea[id="New comment"]');
	let value = textarea.val();
	let comment = textarea.next().html();
	let cleanComment;


	if (value && value != '') {
		cleanComment = value.replaceAll('.<br>.', '');
	}
	// else {
	//   cleanComment = commnet?.replaceAll('.<br>.', '');
	//   console.log(cleanComment)
	// }


	cleanComment = cleanComment ? cleanComment.replaceAll('&nbsp;', ' ') : '';
	if (cleanComment != '' && cleanComment != ' ') {
		textarea.css('border', '1px solid #ccc');
		saveQAComment(cleanComment, fieldID, name);
		$('textarea[name="New comment"]').val('');
		value = '';
		textarea.val('');
		cleanComment = '';
		comment = '';
		textarea.focus();
	} else {
		textarea.css('border', '2px solid red');
	}

}


//function to hide and show input to be able to edit
function showEditComment(block, commentID, option) {

	let editCommentReadonly = $(`textarea[commentID="${commentID}"].editCommentReadonly`);
	let commentReadonly = $(`p[commentID="${commentID}"].commentReadonly`);

	switch (option) {
		case 1:
			commentReadonly.hide();
			editCommentReadonly.show();
			editCommentReadonly.focus();
			block.find('.editCommentBtn').hide();
			block.find('div.deleteCommentBtn').hide();
			block.find('img.agreeCommentBtn').hide();
			block.find('img.disagreeCommentBtn').hide();
			block.find('img.replyCommentBtn ').hide();
			block.find('img.clarificationCommentBtn').hide();
			block.find('.containerSentCommentBtn').show();
			block.find('.correctCommentBtn').hide();
			block.find('.dismissCommentBtn').hide();
			break;
		case 2:
			commentReadonly.show();
			block.find('.correctCommentBtn').show();
			editCommentReadonly.hide();
			block.find('.editCommentBtn').show();
			block.find('.dismissCommentBtn').show();
			break;

	}

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
			block.find('div.deleteCommentBtn').hide();
			block.find('img.clarificationCommentBtn').hide();
			block.find('.correctCommentBtn').hide();
			block.find('.editCommentBtn').hide();
			block.find('.containerSentCommentBtn').hide();
			block.find('.dismissCommentBtn').hide();

			break;
		case '1':
			textarea.prev().find('span.red.requiredTag').hide();
			block.find('img.agreeCommentBtn').hide();
			block.find('.commentContainer').css('background', '#a8eaab');
			block.find('.replyTextContainer').css('background', '#a8eaab');
			block.find('img.disagreeCommentBtn').hide();
			block.find('img.clarificationCommentBtn').hide();
			block.find('div.deleteCommentBtn').hide();
			block.find('.correctCommentBtn').hide();
			block.find('.editCommentBtn').hide();
			block.find('.containerSentCommentBtn').hide();
			block.find('.dismissCommentBtn').hide();

			break;
		case '2':
			textarea.prev().find('span.red.requiredTag').show();
			block.find('img.clarificationCommentBtn').hide();
			block.find('.commentContainer').css('background', '#a4cde8');
			block.find('.replyTextContainer').css('background', '#a4cde8');
			block.find('img.agreeCommentBtn').hide();
			block.find('img.disagreeCommentBtn').hide();
			block.find('div.deleteCommentBtn').hide();
			block.find('.correctCommentBtn').hide();
			block.find('.editCommentBtn').hide();
			block.find('.containerSentCommentBtn').hide();
			block.find('.dismissCommentBtn').hide();

			break;
		case '4':
			block.find('.editCommentBtn').hide();
			block.find('div.deleteCommentBtn').show();
			block.find('.correctCommentBtn').hide();
			block.find('.containerSentCommentBtn').hide();
			block.find('img.agreeCommentBtn').show();
			block.find('img.disagreeCommentBtn').show();
			block.find('img.clarificationCommentBtn').show();
			block.find('.commentTitle').css('font-style', 'normal');
			block.find('.commentTitle').css('font-weight', '600');
			block.find('.commentReadonly').css('font-style', 'normal');
			block.find('.commentReadonly').css('font-weight', '600');
			block.find('.dismissCommentBtn').hide();
			block.find('.containerReactionComment').css('background', '#f0f0f0');

			break;
		case '6':
			block.find('.editCommentBtn').hide();
			block.find('div.deleteCommentBtn').hide();
			block.find('.correctCommentBtn').hide();
			block.find('.containerSentCommentBtn').hide();
			block.find('img.agreeCommentBtn').hide();
			block.find('img.disagreeCommentBtn').hide();
			block.find('img.clarificationCommentBtn').hide();
			block.find('.dismissCommentBtn').hide();
			block.find('.commentContainer').css('background', '#9b99964a');
			block.find('.replyTextContainer').css('background', '#9b99964a');
			block.find('.commentTitle').css('font-style', 'oblique');
			block.find('.commentTitle').css('font-weight', '200');
			block.find('.commentReadonly').css('font-style', 'oblique');
			block.find('.commentReadonly').css('font-weight', '400');

			break;
		case "":
			block.find('img.agreeCommentBtn').hide();
			block.find('img.disagreeCommentBtn').hide();
			block.find('img.clarificationCommentBtn').hide();
			block.find('div.deleteCommentBtn').show();
			block.find('.containerSentCommentBtn').hide();
			block.find('.correctCommentBtn').show();
			block.find('.editCommentBtn').show();
			block.find('.dismissCommentBtn').show();
			block.find('.commentTitle').css('font-style', 'oblique');
			block.find('.commentTitle').css('font-weight', '200');
			block.find('.commentReadonly').css('font-style', 'oblique');
			block.find('.commentReadonly').css('font-weight', '400');
			break;
	}
}

// Multiple comments-replies
// Your file: feedbackAutoImplementation.js
// All comments are in English as requested.

// ... (other global variables like qaComments, userID, userCanManageFeedback, userCanLeaveComments etc.) ...

// IMPORTANT for Step 7 (Handling Reply Statuses with Icons):
// You will need a way to map status IDs to icon images/classes later.
// For now, this function focuses on displaying replies and current status text.
// Also, ensure 'feedback_statuses_list' (or a similar structure you might use for icons)
// is available if needed for determining which icons to show based on current reply status.

function loadCommentsByUser(name) {
    try {
        name = name.replace(/\[[^\]]*\]$/, ''); 
        let qaPopup2 = $(`.qaPopup[id^="qaPopup-${name}"]`);
        qaPopup2.hide(); 

        if (qaComments.length > 0) {
            for (let i = 0; i < qaComments.length; i++) { 
                if (qaComments[i].frontName == name) {
                    let commentsInGroup = qaComments[i]; 
                    let commentKeys = Object.keys(commentsInGroup).filter(key => !isNaN(parseInt(key))); 
                    
                    let commentEmpty = [];
                    let statusArray = false;

                    if (userCanApproveFeedback == 'false') {
                        commentKeys.forEach(function(key) {
                            if (commentsInGroup[key] && commentsInGroup[key].status) { // Check if comment object and status exist
                                commentEmpty.push(commentsInGroup[key].status);
                            }
                        });
                        statusArray = commentEmpty.length > 0 && commentEmpty.every((el) => el == '6');
                    }

                    if (!statusArray) { 
                        qaPopup2.show(); 

                        commentKeys.forEach(function(j) { // Iterate using actual numeric keys from commentKeys
                            let currentCommentData = commentsInGroup[j]; 
                            let $commentBlock = $(`div[id="qaCommentReply-${name}[${j}]"]`); // Use exact ID selector

                            if (j != "0") { 
                                // This assumes the label for "New comment" is only for the very first input box
                                // and subsequent cloned blocks for actual comments shouldn't show it if they reuse that textarea.
                                // However, your main comment display logic later hides textarea[id="New comment"] anyway.
                                // $commentBlock.find('textarea[id="New comment"]').prev('label').hide(); 
                            }

                            // =================================================================================
                            // CRITICAL SECTION FOR FIXING "SECOND COMMENT NOT SHOWING"
                            // =================================================================================
                            if (!$commentBlock.length) { // Use .length to check if jQuery object found elements
                                // THIS IS WHERE THE PROBLEM OF "SECOND COMMENT NOT SHOWING" LIKELY IS.
                                // Your original logic for cloning the #qaTemplate and appending it to the DOM
                                // MUST be correctly implemented here.
                                // The 'console.warn' below will tell you if it's failing to find pre-existing blocks.

                                console.warn(`MARLO Feedback: Comment block 'qaCommentReply-${name}[${j}]' not found in DOM. Attempting to clone.`);
                                
                                // --- YOUR CLONING LOGIC MUST GO HERE ---
                                // This is a placeholder based on your original code structure.
                                // YOU NEED TO VERIFY AND COMPLETE THIS WITH YOUR ACTUAL, WORKING CLONING CODE.
                                let containerQaPopupForCloning = $(`div[id^="containerQaPopup-${name}"]`); // Your popup container
                                let commentReplyBlockTemplate = containerQaPopupForCloning.siblings('#qaTemplate').find('.qaCommentReplyBlock'); // Path to your template element

                                if (!commentReplyBlockTemplate.length) {
                                     console.error("MARLO Feedback: #qaTemplate or .qaCommentReplyBlock within it not found for cloning. Cannot create new comment blocks.");
                                     return; // or continue to next 'j' if appropriate
                                }
                                
                                // Determine where to append the new block
                                let $lastCommentBlockInPopup = qaPopup2.find(`.qaCommentReplyBlock[name^="${name}"]`).last();

                                let $newBlock = commentReplyBlockTemplate.first().clone(true); // Clone the template
                                $newBlock.attr('id', `qaCommentReply-${name}[${j}]`); // Set the specific ID
                                $newBlock.attr('index', j); // Set the index attribute
                                $newBlock.attr('name', `${name}[${j}]`); // It might be useful to set the name attribute on the block itself

                                // Update 'name' attributes for all relevant elements INSIDE $newBlock
                                // This is crucial for your event handlers that use $(this).attr('name')
                                $newBlock.find('.sendCommentContainer').attr('name', `${name}[${j}]`);
                                $newBlock.find('.sendReplyContainer').attr('name', `${name}[${j}]`);
                                // ... and for ALL other elements inside the block that need a specific name for index 'j'
                                // (deleteCommentBtn, containerSentCommentBtn, agreeCommentBtn, etc.)
                                // Example:
                                $newBlock.find('.deleteCommentBtn').attr('name', `${name}[${j}]`);
                                $newBlock.find('.agreeCommentBtn').attr('name', `${name}[${j}]`);
                                // Add all necessary .attr('name', ...) calls here for elements within $newBlock

                                if ($lastCommentBlockInPopup.length) {
                                    $newBlock.insertAfter($lastCommentBlockInPopup);
                                } else {
                                    // If qaPopup2 is the correct container to append to (e.g., the body of the popup)
                                    qaPopup2.append($newBlock); 
                                }
                                $newBlock.show(); // Ensure the new block is visible

                                $commentBlock = $newBlock; // CRITICAL: Update $commentBlock to reference the NEWLY CREATED element!
                                console.log("MARLO Feedback: Cloned and appended new comment block:", $commentBlock.attr('id'));
                                // =================================================================================
                                // END OF CRITICAL CLONING SECTION
                                // =================================================================================
                            }
                            
                            // Setting up the main comment's content
                            $commentBlock.find('textarea[id="New comment"]').hide(); // Hides the main "New comment" input area after a comment is loaded
                            $commentBlock.find('textarea[id="New comment"]').next().next('p.charCount').hide();
                            $commentBlock.find('.commentContainer').show();
                            $commentBlock.find('.commentContainer .commentTitle').html(`Comment by ${currentCommentData.userName} at ${currentCommentData.date}`);
                            $commentBlock.find('.commentContainer p.commentReadonly').html(`${currentCommentData.comment}`);
                            $commentBlock.find('.commentContainer textarea.editCommentReadonly').html(`${currentCommentData.comment}`);
                            $commentBlock.find('.sendCommentContainer').hide(); // This is for the main "New comment" field, should be hidden when displaying a comment

                            // Assigning IDs and attributes
                            $commentBlock.find('.commentContainer').attr({'userName': currentCommentData.userName, 'email': currentCommentData.email, 'comment': currentCommentData.comment, 'isTracking': currentCommentData.isTracking, 'status': currentCommentData.status});
                            $commentBlock.find('.deleteCommentBtn').attr('commentId', currentCommentData.commentId);
                            $commentBlock.find('.containerSentCommentBtn').attr('commentId', currentCommentData.commentId);
                            $commentBlock.find('.sendReplyContainer').attr('commentId', currentCommentData.commentId); 
                            $commentBlock.find('.agreeCommentBtn').attr('commentId', currentCommentData.commentId);
                            $commentBlock.find('.disagreeCommentBtn').attr('commentId', currentCommentData.commentId);
                            $commentBlock.find('.clarificationCommentBtn').attr('commentId', currentCommentData.commentId);
                            $commentBlock.find('.correctCommentBtn').attr('commentId', currentCommentData.commentId);
                            $commentBlock.find('.dismissCommentBtn').attr('commentId', currentCommentData.commentId);
                            $commentBlock.find('.replyCommentBtn').attr('commentId', currentCommentData.commentId); // Button to show reply textarea
                            $commentBlock.find('.editCommentReadonly').attr('commentId', currentCommentData.commentId);
                            $commentBlock.find('.commentReadonly').attr('commentId', currentCommentData.commentId);
                            $commentBlock.find('.editCommentBtn').attr('commentId', currentCommentData.commentId);
                            $commentBlock.find('.commentCheckContainer').attr('commentId', currentCommentData.commentId);
                            $commentBlock.attr('commentId', currentCommentData.commentId);
                            $commentBlock.find('.track_icon').attr('commentId', currentCommentData.commentId).attr('name', `${name}[${j}]`); // Added name to track_icon for context

                            // === JS MODIFICATION (Step 2: Prepare replies display area) ===
                            let $repliesDisplayArea = $commentBlock.find('.repliesDisplayArea');
                            if (!$repliesDisplayArea.length) {
                                console.error("MARLO Feedback: The '.repliesDisplayArea' container was not found in comment block:", $commentBlock.attr('id'));
                            }
                            $repliesDisplayArea.empty();
                            // === END OF JS MODIFICATION (Step 2) ===

                            // === JS MODIFICATION (Step 3 & Refined Step 7: Iterate, display replies, with placeholder for status icons) ===
                            if (currentCommentData.reply && Array.isArray(currentCommentData.reply) && currentCommentData.reply.length > 0) {
                                $repliesDisplayArea.show(); 

                                currentCommentData.reply.forEach(function(individualReply, replyIndex) {
                                    let r_id = individualReply.id;
                                    let r_text = individualReply.text || "(No content)";
                                    let r_userName = individualReply.userName || "Unknown User";
                                    let r_userID = individualReply.userID;
                                    let r_date = individualReply.date || "No date";

                                    let r_statusDisplay = "No status assigned";
                                    if (individualReply.statusName) {
                                        r_statusDisplay = individualReply.statusName;
                                    } else if (typeof individualReply.statusId !== 'undefined' && individualReply.statusId !== null) {
                                        r_statusDisplay = `Status ID: ${individualReply.statusId}`; // Fallback, ideally map ID to name
                                    }

                                    let r_approvalInfo = "";
                                    if (individualReply.approvalUserName && individualReply.approvalDate) {
                                        r_approvalInfo = ` (Approved by: ${individualReply.approvalUserName} on ${individualReply.approvalDate})`;
                                    } else if (individualReply.userApprovalId && individualReply.approvalDate) {
                                        r_approvalInfo = ` (Approved by User ID: ${individualReply.userApprovalId} on ${individualReply.approvalDate})`;
                                    } else if (individualReply.approvalDate) {
                                        r_approvalInfo = ` (Approved on ${individualReply.approvalDate})`;
                                    }

                                    // HTML structure for reply, mimicking comment structure, indented
                                    let replyHtmlString = `
                                        <div class="individual-reply-item" data-reply-id="${r_id}" style="margin-left: 25px; margin-bottom: 15px; padding:10px; border: 1px solid #ddd; background-color: #fdfdfd; border-radius: 3px;">
                                            <div class="reply-commentContainer"> 
                                                <div class="reply-commentTitle" style="font-weight:bold; color: #333; margin-bottom: 5px; font-size:0.95em;">
                                                    Reply by ${r_userName} 
                                                    <span style="font-weight:normal; color: #777; font-size:0.9em;"> - ${r_date}</span>
                                                </div>
                                                <p class="reply-commentReadonly" style="white-space: pre-wrap; margin-bottom: 8px;">${r_text}</p>
                                                
                                                <div class="reply-footer-area" style="font-size:0.9em; display: flex; justify-content: space-between; align-items: center;">
                                                    <span class="reply-status-display"><strong>Status:</strong> ${r_statusDisplay}${r_approvalInfo}</span>
                                                    <span class="reply-status-icons-placeholder" data-reply-id="${r_id}" data-current-status-id="${individualReply.statusId || ''}">
                                                        </span>
                                                </div>
                                            </div>
                                    `; // HTML string continues

                                    if (r_userID == userID) { 
                                        replyHtmlString += `
                                            <div style="margin-top: 8px; text-align: right;">
                                                <button class="deleteActualReplyBtn" 
                                                        data-reply-id="${r_id}" 
                                                        data-comment-name="${name}[${j}]"  // Pass specific comment block name
                                                        style="background-color: #ffdddd; border: 1px solid #ffc0c0; color: #d8000c; padding: 3px 10px; font-size: 0.8em; cursor: pointer; border-radius: 3px;">
                                                    Delete Reply
                                                </button>
                                            </div>
                                        `;
                                    }
                                    replyHtmlString += `</div>`; // Close .individual-reply-item
                                    $repliesDisplayArea.append(replyHtmlString);
                                });
                            } else {
                                $repliesDisplayArea.html('<p style="font-style:italic; color:#6c757d; padding: 8px 0;">No replies for this comment yet.</p>');
                                $repliesDisplayArea.show();
                            }
                            // === END OF JS MODIFICATION (Step 3 & Refined Step 7) ===

                            // === JS MODIFICATION (Step 5: Manage "Add New Reply" section) ===
                            let $replyTextarea = $commentBlock.find('textarea[id="Reply"]');
                            let $replyTextareaContainer = $replyTextarea.parent(); 
                            let $sendNewReplyButtonContainer = $commentBlock.find('div.sendReplyContainer');

                            if (userCanLeaveComments == 'true') { 
                                let currentCommentStatus = String(currentCommentData.status); 
                                let commentAllowsNewReplies = true; 

                                if (currentCommentStatus === '4' || currentCommentStatus === '6') { 
                                    commentAllowsNewReplies = false;
                                }
                                // const openForReplyStatuses = ['0', '1', '2', '']; 
                                // if (!openForReplyStatuses.includes(currentCommentStatus)) {
                                //     commentAllowsNewReplies = false;
                                // }

                                if (commentAllowsNewReplies) {
                                    $replyTextareaContainer.show(); 
                                    $replyTextarea.val('');         
                                    $replyTextarea.prop('disabled', false); 

                                    $sendNewReplyButtonContainer.attr('commentId', currentCommentData.commentId); 
                                    $sendNewReplyButtonContainer.show();
                                    $sendNewReplyButtonContainer.css('pointer-events', 'auto'); 
                                    $sendNewReplyButtonContainer.find('img.sendComment').css('opacity', '1');
                                } else {
                                    $replyTextareaContainer.hide();
                                    $sendNewReplyButtonContainer.hide();
                                }
                            } else {
                                $replyTextareaContainer.hide();
                                $sendNewReplyButtonContainer.hide();
                            }
                            // === END OF JS MODIFICATION (Step 5) ===

                            // === OLD CODE FOR HANDLING A SINGLE REPLY (Should remain commented out or be deleted) ===
                            /*
                            // ... (all the old logic for replyLength, populating .replyTextContainer for a single reply, etc.) ...
                            */
                            // === END OF OLD CODE ===

                            // Your existing logic for comment display based on its own status and permissions continues here
                            if (currentCommentData.userID != userID || usercanTrackComments == 'false' || currentCommentData.status == '6') {
                                $commentBlock.find('.track_icon').hide();
                            } else {
                                $commentBlock.find('.track_icon').show();
                            }

                            if (currentCommentData.isTracking == true) {
                                $commentBlock.find('.track_icon').attr('src', `${baseURL}/global/images/yellow_tracking.png`);
                                $commentBlock.find('.track_icon').attr('title', `Stop tracking comment`);
                            } else {
                                $commentBlock.find('.track_icon').attr('src', `${baseURL}/global/images/tracking.png`);
                                $commentBlock.find('.track_icon').attr('title', `Track your comment`);
                            }
                            
                            if (currentCommentData.status && currentCommentData.status !== '') {
                                $commentBlock.find('.containerReactionComment').show();
                                $commentBlock.find('.containerReactionComment p.reactionComment').html(reactionName(currentCommentData.status) + `${currentCommentData.approvalUserName} at ${currentCommentData.approvalDate}`);
                            } else if (currentCommentData.status === '') { 
                                $commentBlock.find('.containerReactionComment').hide();
                                $commentBlock.find('.commentContainer .commentTitle').html(`[Draft] - Comment by ${currentCommentData.userName} at ${currentCommentData.date}`);
                            } else { 
                                 $commentBlock.find('.containerReactionComment').hide();
                            }
                            
                            if (userCanLeaveComments == 'true') {
                                let btnsContainer = $commentBlock.find('.buttonsContainer'); // This contains options for main comment
                                let addCommentBtnOnBlock = $commentBlock.find('.addCommentContainer'); // The "Add Comment" button on THIS block

                                // Determine if this is the last *actual* comment object in the current group
                                const actualCommentObjectsInGroup = Object.values(commentsInGroup).filter(item => typeof item === 'object' && item.hasOwnProperty('commentId'));
                                let isLastActualCommentInGroup = false;
                                if (actualCommentObjectsInGroup.length > 0 && currentCommentData.commentId === actualCommentObjectsInGroup[actualCommentObjectsInGroup.length - 1].commentId) {
                                    isLastActualCommentInGroup = true;
                                }

                                if (isLastActualCommentInGroup) {
                                    // Show the "Add Comment" button only on the last comment block of the field
                                    // This assumes 'addCommentContainer' is for adding a new MAIN comment to the field
                                    addCommentBtnOnBlock.show(); 
                                    // btnsContainer might also need to be shown if it holds addCommentBtnOnBlock or related UI
                                    btnsContainer.show(); 
                                } else {
                                    addCommentBtnOnBlock.hide();
                                    // Consider if btnsContainer should also be hidden if addCommentBtnOnBlock is its main purpose here
                                    // This logic seems to be for the "Add new comment to field" button
                                }
                            } else {
                                // $commentBlock.find('.editCommentBtn').remove(); 
                                // $('.containerLeftComment').hide(); // Risky global selector
                            }

                            if (userCanApproveFeedback == 'false') {
                                $commentBlock.find('.dismissCommentBtn').hide();
                                $commentBlock.find('.correctCommentBtn').hide();
                            }

                            if (userCanManageFeedback == 'true') {
                                $commentBlock.find('.buttonsContainer').show(); // Ensure action buttons for comment are visible
                                $commentBlock.find('.optionsContainer').css('display', 'flex');
                            } else {
                                $commentBlock.find('img.agreeCommentBtn').remove();
                                $commentBlock.find('img.disagreeCommentBtn').remove();
                                $commentBlock.find('img.clarificationCommentBtn').remove();
                            }

                            if (userCanLeaveComments == 'true' && userCanManageFeedback == 'false') {
                                $commentBlock.find('.buttonsContainer').show();
                                $commentBlock.find('.optionsContainer').css('display', 'flex');
                            }
                            
                            // CRITICAL: Review hideShowOptionButtons.
                            // Ensure its logic for textarea[id="Reply"] and .sendReplyContainer
                            // does not conflict with the Step 5 logic implemented above.
                            hideShowOptionButtons($commentBlock, currentCommentData.status);

                            let editCommentReadonly = $commentBlock.find('.editCommentReadonly');
                            if (editCommentReadonly.css('display') === 'block' && userCanLeaveComments == 'true') {
                                showEditComment($commentBlock, currentCommentData.commentId , 1);
                            }
                        }); // End of commentKeys.forEach (was for let j = 0...)
                    } else { // statusArray is true (all comments dismissed and user cannot approve)
                        // Your original logic for when all comments are dismissed
                        commentKeys.forEach(function(k) { // Iterate using actual numeric keys
                             let blockToHide = $(`div[id="qaCommentReply-${name}[${k}]"]`);
                             blockToHide.hide();
                             if (k == commentKeys[0]) { // If it's the first of the dismissed comments (e.g., "0")
                                 addNewComment(); // Check what addNewComment() does, ensure it's appropriate
                             }
                        });
                    }
                } // End if (qaComments[i].frontName == name)
            } // End for (let i = 0; ...)
        } // End if (qaComments.length > 0)
    } catch (error) {
        console.error("Error in loadCommentsByUser:", error); 
        // Consider if getQAComments() should always be called or if there's more specific error recovery
        getQAComments(); 
    }
}

runaddfeedbackFlexItemsClass = true;
function addfeedbackFlexItemsClass(fieldsMap) {
	if (!runaddfeedbackFlexItemsClass) return;
	fieldsMap.map(field => {
		if ($(`[name="${field.fieldName}"]`).closest('.fieldReference').length == 2) {
			$(`[name="${field.fieldName}"]`).closest('.fieldReference').first().next().remove();
		}
		let fieldReference = $(`[name="${field.fieldName}"]`).closest('.fieldReference').exists() == true ? $(`[name="${field.fieldName}"]`).closest('.fieldReference').last() : $(`[name="${field.fieldName}[]"]`).closest('.fieldReference').last();
		fieldReference.appendTo(fieldReference.prev());
		fieldReference.closest('.feedback-flex-items').next().appendTo(fieldReference.closest('.feedback-flex-items'))
	})
	runaddfeedbackFlexItemsClass = false;
}

function loadQACommentsIcons(ajaxURL, arrayName) {
	$.ajax({
		url: baseURL + ajaxURL,
		async: false,
		success: function(data) {

			fieldsSections = data?.fieldsMap;
			addfeedbackFlexItemsClass(fieldsSections);
			if ((userCanLeaveComments == 'true') || (userCanManageFeedback == 'true' && qaComments.length > 0)) {
				if (data && Object.keys(data).length != 0) {
					newData = data[arrayName].map(function(x) {
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
	data.map(function(field) {
		var commentIcon = $(`img.qaComment[name="${field[1]}"]`);
		commentIcon.attr('fieldID', `${field[0]}`);
		commentIcon.attr('description', `${field[2]}`);
		commentIcon.attr('id', `${field[0]}`);
		let block = $(`div[id^="qaCommentReply-${field[1]}"]`);

		block.each((index, item) => {
			$(item).find('.agreeCommentBtn').attr('name', `${field[1]}[${index}]`);
			$(item).find('.deleteCommentBtn').attr('name', `${field[1]}[${index}]`);
			$(item).find('.containerSentCommentBtn').attr('name', `${field[1]}[${index}]`);
			$(item).find('.deleteReplyBtn').attr('name', `${field[1]}[${index}]`);
			// $(item).find('.sendCommentContainer').attr('name', `${field[1]}[${index}]`);
			$(item).find('.agreeCommentBtn').attr('name', `${field[1]}[${index}]`);
			$(item).find('.disagreeCommentBtn').attr('name', `${field[1]}[${index}]`);
			$(item).find('.clarificationCommentBtn').attr('name', `${field[1]}[${index}]`);
			$(item).find('.correctCommentBtn').attr('name', `${field[1]}[${index}]`)
			$(item).find('.dismissCommentBtn').attr('name', `${field[1]}[${index}]`)
			$(item).find('.replyCommentBtn').attr('name', `${field[1]}[${index}]`);
			$(item).find('.sendReplyContainer').attr('name', `${field[1]}[${index}]`);
			$(item).find('div.addCommentContainer').attr('name', field[1]);
			$(item).find('.track_icon').attr('name', `${field[1]}[${index}]`);

		});

		let qaCommentFinded = qaComments.find(qaComment => qaComment.frontName == field[1]);
		if (qaCommentFinded) {

			let commentEmpty = []
			let statusComments = false;
			getNumberOfComments(qaCommentFinded.frontName);
			Object.keys(qaCommentFinded).map(keycomment => {
				const { status } = qaCommentFinded[keycomment]
				commentEmpty.push(status);
			})
			statusComments = commentEmpty.some((el) => el == '' || el == '0' || el == '1' || el == '2' || el == '4');

			if (statusComments) {
				let allFieldsdone = true;

				Object.keys(qaCommentFinded).map(keycomment => {

					if (qaCommentFinded[keycomment] == qaCommentFinded.frontName) return;
					if (!allFieldsdone) return;
					const { status, reply } = qaCommentFinded[keycomment]
					if (status === "4") allFieldsdone = false;
					if (status === "0" || status === "2" || status === "") allFieldsdone = !!Object.keys(reply).length;
					if (userCanLeaveComments == 'false') {
						if (status === "") allFieldsdone = true;
					}

				})
				commentIcon.attr('src', qaCommentsStatus(allFieldsdone ? 'done' : 'pending'))
			}
			else {
				commentIcon.attr('src', qaCommentsStatus('start'))
			}


		} else {
			commentIcon.attr('src', qaCommentsStatus('start'))
		}

		// const currentqaComments = qaComments.filter(qaCommentsFilter => qaCommentsFilter.frontName == field[1])
		let commentEmpty2 = []
		if (qaCommentFinded) {
			Object.keys(qaCommentFinded).map(keycomment => {
				const { status } = qaCommentFinded[keycomment]
				if (status === "0" || status === "1" || status === "2" || status === "4") commentEmpty2.push(true)
			})
		}
		if (userCanLeaveComments == 'true' || commentEmpty2[0] == true) {
			commentIcon.show();
			commentIcon.parent().css('display', 'flex');
		}
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
	let objectField = fieldsSections.find(field => field.fieldName == name)
	let inputValue = $(`input[name="${objectField.parentFieldDescription}"]`).val()
	var finalAjaxURL = `/saveFeedbackComments.do?sectionName=${sectionName}&parentID=${parentID}&comment=${encodeURIComponent(comment)}&phaseID=${phaseID}&fieldID=${fieldID}&userID=${userID}&projectID=${projectID}&parentFieldDescription=${inputValue}`;

	$.ajax({
		url: baseURL + finalAjaxURL,
		async: false,
		success: function(data) {
			getQAComments();
			loadCommentsByUser(name);
			loadQACommentsIcons(contributionCRPAjaxURL, arrayName);
		}
	});
}

// Update comments 
function updateComment(comment, fieldID, name, reference, commentID) {
	let indexToCute = $(reference).attr("name").substring(0, $(reference).attr("name").length - 3);
	let objectField = fieldsSections.find(field => field.fieldName == indexToCute)
	let inputValue = $(`input[name="${objectField.parentFieldDescription}"]`).val()
	var finalAjaxURL = `/saveFeedbackComments.do?sectionName=${sectionName}&parentID=${parentID}&comment=${encodeURIComponent(comment)}&phaseID=${phaseID}&fieldID=${fieldID}&userID=${userID}&projectID=${projectID}&commentID=${commentID}`;

	$.ajax({
		url: baseURL + finalAjaxURL,
		async: false,
		success: function(data) {
			getQAComments();
			loadCommentsByUser(name);
			loadQACommentsIcons(contributionCRPAjaxURL, arrayName);
		}
	});
}

function saveFeedbackReply(reply, commentID, name) {
	var finalAjaxURL = `/saveFeedbackReply.do?reply=${encodeURIComponent(reply)}&commentID=${commentID}&userID=${userID}`;

	$.ajax({
		url: baseURL + finalAjaxURL,
		async: false,
		success: function(data) {
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
		success: function(data) {
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
		success: function(data) {
			if (data && Object.keys(data).length != 0) {
				qaComments = data['comments'];
			}
		}
	});
}

function deleteQAReply(commentID, name, htmlParent) {
	var finalAjaxURL = `/deleteReply.do?commentID=${commentID}`;
	$.ajax({
		url: baseURL + finalAjaxURL,
		async: false,
		success: function(data) {

			// if (!data?.delete?.delete) return;

			let qaCommentReplyBlock = $(htmlParent).closest('.qaCommentReplyBlock');
			qaCommentReplyBlock.find('.commentContainer').css('background', 'white');
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
		success: function(data) {

			if (!data?.delete?.delete) return;

			let qaPopup = $(htmlParent).closest('.qaPopup');

			// if (qaPopup.find('.qaCommentReplyBlock').length == 1 )parent.find('.addCommentContainer').trigger("click");

			if (qaPopup.find('.qaCommentReplyBlock').length == 1) {
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
		success: function(data) {
			if (data && Object.keys(data).length != 0) {
				newData = data['comments'].map(function(x) {
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
	data.map(function(x) {
		let p = $(`img.qaComment[name="${name}"]`).prev().find('p');
		p.css('display', 'block');
		if (x[0] == x[1] && x[1]) p.css('border', '2px solid #8dc02c');
		if (x[0] != x[1]) p.css('border', '2px solid #ffffff00');
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
		case "4":
			return 'Admitted by ';
		case "6":
			return 'Dismissed by ';
	}
}

function saveTrackComment(status, commentID, name) {

	var finalAjaxURL = `/saveTrackingStatus.do?status=${status}&commentID=${commentID}`;
	$.ajax({
		url: baseURL + finalAjaxURL,
		async: false,
		success: function(data) {
			getQAComments();
			loadCommentsByUser(name);
			// loadQACommentsIcons(contributionCRPAjaxURL, arrayName);
		}
	});
}

function sendFeedbackActionEmail(feedback_assesor_input, feedback_assesor_name, feedback_assesor_email, feedback_comment_reaction, reference) {
	let indexToCute = $(reference).attr("name").substring(0, $(reference).attr("name").length - 3);
	let objectField = fieldsSections.find(field => field.fieldName == indexToCute)
	let inputValue = $(`input[name="${objectField.parentFieldDescription}"]`).val();

	var finalAjaxURL = `/sendFeedbackActionEmail.do?projectID=${projectID}&feedback_assesor_name=${feedback_assesor_name}&feedback_assesor_input=${feedback_assesor_input}&feedback_assesor_email=${feedback_assesor_email}&sectionName=${sectionName}&feedback_comment_reaction=${feedback_comment_reaction}&section_id=${parentID}&parentFieldDescription=${inputValue}&fieldDescription=${descriptionComment}`;
	$.ajax({
		url: baseURL + finalAjaxURL,
		async: false,
		success: function(data) {
		}
	});
}


function sendFeedbackReactionEmail(feedback_assesor_input, feedback_assesor_name, feedback_assesor_email, feedback_comment_reaction, feedback_replay_username, feedback_response, reference) {
	let indexToCute = $(reference).attr("name").substring(0, $(reference).attr("name").length - 3);
	let objectField = fieldsSections.find(field => field.fieldName == indexToCute)
	let inputValue = $(`input[name="${objectField.parentFieldDescription}"]`).val();
	var finalAjaxURL = `/sendFeedbackReactionEmail.do?projectID=${projectID}&feedback_assesor_name=${feedback_assesor_name}&feedback_assesor_input=${feedback_assesor_input}&feedback_assesor_email=${feedback_assesor_email}&sectionName=${sectionName}&feedback_comment_reaction=${feedback_comment_reaction}&feedback_replay_username=${feedback_replay_username}&feedback_response=${feedback_response}&section_id=${parentID}&parentFieldDescription=${inputValue}&fieldDescription=${descriptionComment}&fieldID=${fieldID}`;
	$.ajax({
		url: baseURL + finalAjaxURL,
		async: false,
		success: function(data) {
		}
	});
}

