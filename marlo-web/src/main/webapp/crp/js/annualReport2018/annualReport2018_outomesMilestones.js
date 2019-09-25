$(document).ready(init);

function init() {

  // Add Select2
  $('form select').select2({
    width: '100%'
  });

  attachEvents();
}

function attachEvents() {

  // Change main reason
  $('select.milestoneMainReasonSelect').on('change', function() {
    var optionSelected = this.value;
    var $block = $(this).parents('.milestonesEvidence').find('.otherBlock');

    if(optionSelected == 7) {
      $block.slideDown();
    } else {
      $block.slideUp();
    }
  });

  $('input.milestoneStatus').on('change', function() {
    var optionSelected = this.value;

    // Milestone Evidence
    var $block = $(this).parents('.synthesisMilestone').find('.milestonesEvidence');
    if(optionSelected == 4 || optionSelected == 5 || optionSelected == 6) {
      $block.slideDown();
    } else {
      $block.slideUp();
    }

    // Extended year
    var $yearBlock = $(this).parents('.synthesisMilestone').find('.extendedYearBlock');
    if(optionSelected == 4) {
      $yearBlock.slideDown();
    } else {
      $yearBlock.slideUp();
    }
  });

}