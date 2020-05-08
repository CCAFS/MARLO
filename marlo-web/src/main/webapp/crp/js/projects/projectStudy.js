var caseStudiesName;
var $elementsBlock;

$(document).ready(init);

function init() {

	// Add Events
	attachEvents();

	// Add select2
	addSelect2();

	// Add Geographic Scope
	$('select.elementType-repIndGeographicScope ').on(
			"addElement removeElement", function(event, id, name) {
				setGeographicScope(this);
			});
	setGeographicScope($('form select.elementType-repIndGeographicScope')[0]);

	// Add file uploads
	setFileUploads();

	// This function enables launch the pop up window
	popups();

	// Add amount format
	$('input.currencyInput').currencyInput();

	// Numeric field
	$('input.numericInput').numericInput();

	$('.ccRelevanceBlock input:radio').on(
			'change',
			function() {
				var $commentBox = $(this).parents('.ccRelevanceBlock').find(
						'.ccCommentBox');
				var id = this.value;
				console.log("CC", id);
				if ((id == "1") || (id == "4")) {
					$commentBox.slideUp();
				} else {
					$commentBox.slideDown();
				}
			});
}

function attachEvents() {

  //Expected Year
  $('select.statusSelect').on('change', function() {
    console.log(this.value);
    if(this.value == 4) {
      $('.block-extendedYear').slideDown();
      $('.block-year').slideUp();
    } else {
      $('.block-extendedYear').slideUp();
      $('.block-year').slideDown();
    }
  });

	// On change studyType
	$('select.studyType').on(
			'change',
			function() {

				// Clean indicator #3 option
				$('input.radioType-studyIndicatorThree:checked').prop(
						'checked', false).trigger('change');
			});

	// On change radio buttons
	$('input[class*="radioType-"]').on('change', onChangeRadioButton);

	// On change policyInvestimentTypes
	$('select.policyInvestimentTypes').on('change', function() {
		console.log(this.value);
		if (this.value == 3) {
			$('.block-budgetInvestment').slideDown();
		} else {
			$('.block-budgetInvestment').slideUp();
		}
	});

	// On change stage of process
	$('select.stageProcess, input.radioType-studyIndicatorThree').on(
			'change',
			function() {
				var isPolicy = $('input.radioType-studyIndicatorThree:checked')
						.val() == "true";
				var stageProcessOne = ($('select.stageProcess').val() == 1);

				if (isPolicy && stageProcessOne) {
					// $('.stageProcessOne span.requiredTag').slideUp();
				} else {
					// $('.stageProcessOne span.requiredTag').slideDown();
				}
			});

	// SRF Targets validation
	$('input.radioType-targetsOption').on('change', function() {
		var showComponent = $(this).val() == "targetsOptionYes";
		if (showComponent) {
			$('.srfTargetsComponent').slideDown(); // Show
		} else {
			$('.srfTargetsComponent').slideUp(); // Hide
		}
	});

	// Other CrossCutting Component validation
	$('input.radioType-otherCrossCuttingOption').on('change', function() {
		var showComponent = $(this).val() == "Yes";
		if (showComponent) {
			// $('.otherCrossCuttingOptionsComponent').slideDown(); // Show
		} else {
			// $('.otherCrossCuttingOptionsComponent').slideUp(); // Hide
		}
	});

	// Public link
	$('input.radioType-optionPublic').on('change', function() {
		var showComponent = $(this).val() == "true";
		if (showComponent) {
			$('.optionPublicComponent').slideDown(); // Show
		} else {
			$('.optionPublicComponent').slideUp(); // Hide
		}
	});

	// Copy URL Button event
	$('.copyButton').on('click', function() {
		var $parent = $(this).parents(".optionPublicComponent");
		var $input = $parent.find('.urlInput');
		var $message = $parent.find('.message');
		$input.select();
		if (document.execCommand("copy")) {
			$message.fadeIn(400, function() {
				$message.fadeOut(300);
			});
		}
		console.log($input.val());
	});

	/**
	 * Links Component
	 */
	(function() {
		// Events
		$('.addButtonLink').on('click', addItem);
		$('.removeLink').on('click', removeItem);

		// Functions
		function addItem() {
			var $list = $(this).parents('.linksBlock').find('.linksList');
			var $element = $('#studyLink-template').clone(true)
					.removeAttr("id");
			// Remove template tag
			$element.find('input, textarea').each(function(i, e) {
				e.name = (e.name).replace("_TEMPLATE_", "");
				e.id = (e.id).replace("_TEMPLATE_", "");
			});
			// Show the element
			$element.appendTo($list).hide().show(350);
			// Update indexes
			updateIndexes();
		}
		function removeItem() {
			var $parent = $(this).parents('.studyLink');
			$parent.hide(500, function() {
				// Remove DOM element
				$parent.remove();
				// Update indexes
				updateIndexes();
			});
		}
		function updateIndexes() {
			$('.linksList').find('.studyLink').each(function(i, element) {
				$(element).find('.indexTag').text(i + 1);
				$(element).setNameIndexes(1, i);
			});
		}

	})();

	/**
	 * Qualification Component
	 */
	(function() {
		// Events
		$('.addStudyQualification').on('click', addItem);
		$('.removeQuantification').on('click', removeItem);

		// Functions
		function addItem() {
			var $list = $(this).parents('.quantificationsBlock').find(
					'.quantificationsList');
			var $element = $('#quantification-template').clone(true)
					.removeAttr("id");
			// Remove template tag
			$element.find('input, textarea').each(function(i, e) {
				e.name = (e.name).replace("_TEMPLATE_", "");
				e.id = (e.id).replace("_TEMPLATE_", "");
			});
			// Show the element
			$element.appendTo($list).hide().show(350);
			// Update indexes
			updateIndexes();
		}
		function removeItem() {
			var $parent = $(this).parents('.quantification');
			$parent.hide(500, function() {
				// Remove DOM element
				$parent.remove();
				// Update indexes
				updateIndexes();
			});
		}
		function updateIndexes() {
			$('.quantificationsList')
					.find('.quantification')
					.each(
							function(i, element) {
								$(element).find('span.index').text(i + 1);
								$(element).setNameIndexes(1, i);

								// Update Radios
								$(element)
										.find('.radioFlat ')
										.each(
												function(j, item) {
													var radioName = 'quantificationRadio-'
															+ i + '-' + j;
													$(item).find('input').attr(
															'id', radioName);
													$(item).find('label').attr(
															'for', radioName);
												});

							});
		}

	})();
	//On change radio buttons
	$('input[class*="radioType-"]').on('change', onChangeRadioButton);
}

function onChangeRadioButton() {
	var thisValue = this.value === "true";
	var radioType = $(this).classParam('radioType');
	if (thisValue) {
		$('.block-' + radioType).slideDown();
	} else {
		$('.block-' + radioType).slideUp();
	}
}

function addSelect2() {

	var isNew = $('.evidenceBlock').classParam('isNew') == "true";
	var hasEvidenceTag = $('input.radioType-tags:checked').val();

	if (isNew) {
		// Adjust status
		// it was disable in 6/11/2019
		// $('select.statusSelect option[value="4"]').prop('disabled', true); //
		// Extended
		// $('select.statusSelect option[value="5"]').prop('disabled', true); //
		// Cancelled

		// Suggest Evidence tag
		if (!hasEvidenceTag) {
			$('input.radioType-tags[value="1"]').prop("checked", true); // New
			// tag
		}
	} else {

	}

	if (reportingActive) {
		// Adjust status

	} else {
		// Adjust status
		$('select.statusSelect option[value="3"]').prop('disabled', true);
		$('select.statusSelect option[value="4"]').prop('disabled', true);
		$('select.statusSelect option[value="5"]').prop('disabled', true);
	}

	$('form select').select2({
		width : '100%'
	});
}

function onChangeRadioButton() {
	  var thisValue = this.value === "true";
	  var radioType = $(this).classParam('radioType');
	  if (thisValue) {
	    $('.block-' + radioType).slideDown();
	  } else {
	    $('.block-' + radioType).slideUp();
	  }
}

/**
 * File upload (blueimp-tmpl)
 */
function setFileUploads() {

	var containerClass = ".fileUploadContainer";
	var $uploadBlock = $(containerClass);
	var $fileUpload = $uploadBlock.find('.upload');

	$fileUpload.fileupload({
		dataType : 'json',
		start : function(e) {
			var $ub = $(e.target).parents(containerClass);
			$ub.addClass('blockLoading');
		},
		stop : function(e) {
			var $ub = $(e.target).parents(containerClass);
			$ub.removeClass('blockLoading');
		},
		done : function(e, data) {
			var r = data.result;
			console.log(r);
			if (r.saved) {
				var $ub = $(e.target).parents(containerClass);
				$ub.find('.textMessage .contentResult').html(r.fileFileName);
				$ub.find('.textMessage').show();
				$ub.find('.fileUpload').hide();
				// Set file ID
				$ub.find('input.fileID').val(r.fileID);
				// Set file URL
				$ub.find('.fileUploaded a').attr('href',
						r.path + '/' + r.fileFileName)
			}
		},
		fail : function(e, data) {
			var $ub = $(e.target).parents(containerClass);
			$ub.animateCss('shake');
		},
		progressall : function(e, data) {
			var $ub = $(e.target).parents(containerClass);
			var progress = parseInt(data.loaded / data.total * 100, 10);
			$ub.find('.progress').fadeIn(100);
			$ub.find('.progress .progress-bar').width(progress + '%');
			if (progress == 100) {
				$ub.find('.progress').fadeOut(1000, function() {
					$ub.find('.progress .progress-bar').width(0);
				});
			}
		}
	});

	// Prepare data
	$fileUpload.bind('fileuploadsubmit', function(e, data) {

	});

	// Remove file event
	$uploadBlock.find('.removeIcon').on('click', function() {
		var $ub = $(this).parents(containerClass);
		$ub.find('.textMessage .contentResult').html("");
		$ub.find('.textMessage').hide();
		$ub.find('.fileUpload').show();
		$ub.find('input.fileID').val('');
		// Clear URL
		$ub.find('.fileUploaded a').attr('href', '');
	});

}
