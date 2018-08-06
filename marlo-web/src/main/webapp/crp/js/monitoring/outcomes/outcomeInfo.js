$(document).ready(init);

function init() {

  /* Declaring Events */
  attachEvents();

  /* Init Select2 plugin */
  $('form select').select2();

  $('form input.initialBaseLine').integerInput();

}

function attachEvents() {

  // Add a Milestone
  $('.addEvidence').on('click', addEvidence);
  // Remove a Milestone
  $('.removeEvidence').on('click', removeEvidence);

  $(".link").on("keyup", checkUrl);
}

/**
 * Milestone Functions
 */

function checkUrl() {
  var input = $(this);
  var inputData = $.trim(input.val());
  var uri = new Uri(inputData);
  $(input).removeClass("fieldError");
  if(inputData != "") {
    if(uri.protocol() == "http" || uri.protocol() == "https") {
      $(input).removeClass("fieldError");
    } else {
      $(input).addClass("fieldError");
    }
  }
}

function addEvidence() {
  console.log(this);
  var $list = $(this).parents(".tab-pane.active").find('.evidenceList');
  var $item = $('#evidence-template').clone(true).removeAttr("id");
  console.log($item);
  $list.append($item);
  $item.show('slow');
  updateAllIndexes();
  // Hide empty message
  $('.tab-pane.active .evidenceList p.message').hide();
}

function removeEvidence() {
  var $list = $(this).parents(".tab-pane.active").find('.evidenceList');
  var $item = $(this).parents('.evidence');
  $item.hide(function() {
    $item.remove();
    updateAllIndexes();
  });
}

/**
 * General Function
 */

function updateAllIndexes() {
  $('form .outcomeTab').each(function(i,outcome) {

    $(outcome).setNameIndexes(1, i);

    // Update evidences
    $(outcome).find('.evidence').each(function(i,evidence) {

      $(evidence).setNameIndexes(2, i);

    });
  });

  // Update component event
  $(document).trigger('updateComponent');

}
