$(document).ready(function() {

  $('select').select2({
    width: '100%'
  });

  $('.milestonesYearSelect select').on('change', function() {
    var $item = $('#milestoneYear-template').clone(true).removeAttr('id');
    var $list = $(this).parents('.milestonesYearBlock').find(".milestonesYearList");
    $list.append($item);
  });

});
