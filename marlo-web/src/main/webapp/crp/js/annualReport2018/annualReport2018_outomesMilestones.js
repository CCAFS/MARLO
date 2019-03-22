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
    var $block = $(this).parents('.synthesisMilestone').find('.milestonesEvidence');

    if(optionSelected == 2 || optionSelected == 3 || optionSelected == 4) {
      $block.slideDown();
    } else {
      $block.slideUp();
    }
  });

}