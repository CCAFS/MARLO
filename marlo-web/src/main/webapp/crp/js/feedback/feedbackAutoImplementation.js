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
			block.find('.commentTitle').css('font-style', 'oblique');
			block.find('.commentTitle').css('font-weight', '200');
			block.find('.commentReadonly').css('font-style', 'oblique');
			block.find('.commentReadonly').css('font-weight', '400');
			break;
	}
}

// Multiple comments-replies
function loadCommentsByUser(name) {
	try {
		// Removes the last index in brackets, i.e: [0]
		name = name.replace(/\[[^\]]*\]$/, '');

		// These two lines are used to hide the body where the comments will be displayed since the body is initialized with a display none.
		let qaPopup2 = $(`.qaPopup[id^="qaPopup-${name}"]`);
		qaPopup2.hide();

		if (qaComments.length > 0) {
			for (let i = 0; i < qaComments.length; i++) {
				if (qaComments[i].frontName == name) {
					let commentsLength = Object.keys(qaComments[i]).length;
					let commentEmpty = []
					let statusArray = false;

					if (userCanApproveFeedback == 'false') {
						for (let j = 0; j < commentsLength; j++) {
							if (qaComments[i][j] !== undefined) {
								commentEmpty.push(qaComments[i][j].status);
							}
						}
						statusArray = commentEmpty.every((el) => el == '6');
					}


					if (!statusArray) {
						// These two lines are used to display the body where the comments will be displayed since the body is initialized with a display none.
						let qaPopup2 = $(`.qaPopup[id^="qaPopup-${name}"]`);
						qaPopup2.show();

						for (let j = 0; j < commentsLength; j++) {
							if (qaComments[i][j] !== undefined) {
								let block = $(`div[id^="qaCommentReply-${name}[${j}]"]`);

								if (j != 0) {
									block.find('textarea[id="New comment"]').prev().hide();
								}



								if (!block.exists()) {
									let qaPopup = $(`div[id^="containerQaPopup-${name}"]`);
									let commentReplyBlock = qaPopup.siblings('#qaTemplate').find('.qaPopup').children()[2];
									let lastBlock = $(`div[id^="qaPopup-${name}["]`).find('.qaCommentReplyBlock').last();
									let newBlock = $(commentReplyBlock).clone(true).attr('id', `qaCommentReply-${name}[${j}]`);


									newBlock.attr('index', `${j}`);
									newBlock.find('.sendCommentContainer').attr('name', `${name}[${j}]`);
									newBlock.find('.sendReplyContainer').attr('name', `${name}[${j}]`);
									newBlock.find('.addCommentContainer').attr('name', `${name}`);
									newBlock.find('.addCommentContainer').attr('index', `${j}`);
									newBlock.find('.deleteCommentBtn').attr('name', `${name}[${j}]`);
									newBlock.find('.containerSentCommentBtn').attr('name', `${name}[${j}]`);

									if (block.last().attr('newComment') != 'true') {
										newBlock.attr('newComment', 'true');
										newBlock.insertAfter(lastBlock).hide().show();
									}
									// block = $(`div[id^="qaPopup-${name}["]`).find('.qaCommentReplyBlock');
									block = newBlock;
								}



								block.find('textarea[id="New comment"]').hide();
								block.find('textarea[id="New comment"]').next().next('p.charCount').hide();
								block.find('.commentContainer').show();
								block.find('.commentContainer .commentTitle').html(`Comment by ${qaComments[i][j].userName} at ${qaComments[i][j].date}`);
								block.find('.commentContainer p.commentReadonly').html(`${qaComments[i][j].comment}`);
								block.find('.commentContainer textarea.editCommentReadonly').html(`${qaComments[i][j].comment}`);
								block.find('.sendCommentContainer').hide();
								if (qaComments[i][j].userID != userID) block.find('.deleteCommentBtn').remove();
								if (qaComments[i][j].reply.userID == userID && qaComments[i][j].status != '6') block.find('.deleteReplyBtn').show();
								if (userCanApproveFeedback == 'false' && userCanManageFeedback == 'true' && userCanLeaveComments == 'true' && qaComments[i][j].userID != userID) block.find('.editCommentBtn').remove();
								if (userCanManageFeedback == 'false') {
								}
								block.find('.commentContainer').attr('userName', qaComments[i][j].userName).attr('email', qaComments[i][j].email).attr('comment', qaComments[i][j].comment).attr('isTracking', qaComments[i][j].isTracking).attr('status', qaComments[i][j].status);
								block.find('.deleteCommentBtn').attr('commentId', qaComments[i][j].commentId);
								block.find('.containerSentCommentBtn').attr('commentId', qaComments[i][j].commentId);
								block.find('.deleteReplyBtn').attr('replyId', qaComments[i][j].reply.id);
								block.find('.sendReplyContainer').attr('commentId', qaComments[i][j].commentId);
								block.find('.agreeCommentBtn').attr('commentId', qaComments[i][j].commentId);
								block.find('.disagreeCommentBtn').attr('commentId', qaComments[i][j].commentId);
								block.find('.clarificationCommentBtn').attr('commentId', qaComments[i][j].commentId);
								block.find('.correctCommentBtn').attr('commentId', qaComments[i][j].commentId);
								block.find('.dismissCommentBtn').attr('commentId', qaComments[i][j].commentId);
								block.find('.replyCommentBtn').attr('commentId', qaComments[i][j].commentId);
								block.find('.editCommentReadonly').attr('commentId', qaComments[i][j].commentId);
								block.find('.commentReadonly').attr('commentId', qaComments[i][j].commentId);
								block.find('.editCommentBtn').attr('commentId', qaComments[i][j].commentId);
								block.find('.commentCheckContainer').attr('commentId', qaComments[i][j].commentId);
								block.attr('commentId', qaComments[i][j].commentId);
								block.find('.track_icon').attr('commentId', qaComments[i][j].commentId);


								if (qaComments[i][j].userID != userID || usercanTrackComments == 'false' || qaComments[i][j].status == '6') {
									block.find('.track_icon').hide();
								} else {
									block.find('.track_icon').show();
								}

								if (qaComments[i][j].isTracking == true) {
									block.find('.track_icon').attr('src', `${baseURL}/global/images/yellow_tracking.png`);
									block.find('.track_icon').attr('title', `Stop tracking comment`);
								} else {
									block.find('.track_icon').attr('src', `${baseURL}/global/images/tracking.png`);
								}






								if (qaComments[i][j].status) {
									block.find('.containerReactionComment').show();
									block.find('.containerReactionComment p.reactionComment').html(reactionName(qaComments[i][j].status) + `${qaComments[i][j].approvalUserName} at ${qaComments[i][j].approvalDate}`);
								} if (qaComments[i][j].status == '') {
									block.find('.containerReactionComment').hide();
									block.find('.commentContainer .commentTitle').html(`[Draft] - Comment by ${qaComments[i][j].userName} at ${qaComments[i][j].date}`);
								}

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
								} else {
									// let commentReadonly = $(`div[commentID="${qaComments[i][j].commentId}"].qaCommentReplyBlock`);
									let commentReadonly = $('.containerLeftComment');
									block.find('.editCommentBtn').remove();
									commentReadonly.hide();
									// if(qaComments[i][j].status == '') {
									// commentReadonly.hide();
									// }
								}

								if (userCanApproveFeedback == 'false') {
									let commentReadonly = $(`div[commentID="${qaComments[i][j].commentId}"].qaCommentReplyBlock`);
									let commentCheckContainer = $(`div[commentID="${qaComments[i][j].commentId}"].commentCheckContainer`);
									let deleteCommentBtn = $(`div[commentID="${qaComments[i][j].commentId}"].deleteCommentBtn`);

									if (qaComments[i][j].status == '6') {

										if (j == (commentsLength - 2)) {
											commentReadonly.show();
											commentCheckContainer.hide()
											block.find('.addCommentContainer').show();
											deleteCommentBtn.remove();
										}
										else {
											commentReadonly.hide();
										}
									}
									block.find('.dismissCommentBtn').hide();
									block.find('.correctCommentBtn').hide();
								}

								if (userCanManageFeedback == 'true') {
									block.find('.buttonsContainer').show();
									block.find('.optionsContainer').css('display', 'flex');
								} else {
									block.find('img.agreeCommentBtn').remove();
									block.find('img.disagreeCommentBtn').remove();
									block.find('img.clarificationCommentBtn').remove();
								}

								if (userCanLeaveComments == 'true' && userCanManageFeedback == 'false') {
									block.find('.buttonsContainer').show();
									block.find('.optionsContainer').css('display', 'flex');
								}


								hideShowOptionButtons(block, qaComments[i][j].status);

								//EXTREME CASE: Close popup in an edit operation
								//If a comment it's being edit and the user close the popup, it would be able to edit
								let editCommentReadonly = block.find('.editCommentReadonly');

								//Validates if a edit textarea is display
								if (editCommentReadonly.css('display') === 'block' && userCanLeaveComments == 'true') {
									showEditComment(block, qaComments[i][j].commentId , 1);
								}
								
								//

								let replyLength = Object.keys(qaComments[i][j].reply).length;

								if (replyLength !== 0) {
									block.find('textarea[id="Reply"]').parent().hide();
									block.find('.replyContainer').css('display', 'flex');
									block.find('.replyTextContainer').show();
									block.find('.replyTextContainer .replyTitle').html(`Reply by ${qaComments[i][j].reply['userName']} at ${qaComments[i][j].reply['date']}`);
									block.find('.replyTextContainer p.replyReadonly').html(`${qaComments[i][j].reply['text']}`);
									block.find('.replyCommentBtn').hide();
									block.find('.sendReplyContainer').hide();
								} else {
									if (qaComments[i][j].status && qaComments[i][j].status != '') {
										if (qaComments[i][j].status == '1') {
											block.find('textarea[id="Reply"]').parent().show();
											block.find('.replyContainer').css('display', 'flex');
											block.find('.replyTextContainer').hide();
											block.find('.replyCommentBtn').hide();
											block.find('.sendReplyContainer').show();
										} if (qaComments[i][j].status == '4') {
											block.find('textarea[id="Reply"]').parent().hide();
											block.find('.replyContainer').hide();
											block.find('.replyTextContainer').hide();
											block.find('.replyCommentBtn').hide();
											block.find('.sendReplyContainer').show();
										} if (qaComments[i][j].status == '0') {
											block.find('textarea[id="Reply"]').parent().show();
											block.find('.replyContainer').css('display', 'flex');
											block.find('.replyTextContainer').hide();
											block.find('.replyCommentBtn').hide();
											block.find('.sendReplyContainer').show();
										} if (qaComments[i][j].status == '2') {
											block.find('textarea[id="Reply"]').parent().show();
											block.find('.replyContainer').css('display', 'flex');
											block.find('.replyTextContainer').hide();
											block.find('.replyCommentBtn').hide();
											block.find('.sendReplyContainer').show();
										} if (qaComments[i][j].status == '6') {
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
					} else {
						for (let j = 0; j < commentsLength; j++) {
							if (qaComments[i][j] !== undefined) {
								let block = $(`div[id^="qaCommentReply-${name}[${j}]"]`);
								block.hide();
								if (j == 0) {
									addNewComment()
								}
							}
						}
					}
				} else {
					// These two lines are used to hide the body where the comments will be displayed since the body is initialized with a display none.
					//let qaPopup2 = $(`.qaPopup[id^="qaPopup-${name}"]`);
					//qaPopup2.hide();
				}
			}
		}
	} catch (error) {
		console.log(error)
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

