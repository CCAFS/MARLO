var milestonesCount;

$(document).ready(function() {

  milestonesCount = $('form .milestoneYear').length;

  $('form select').select2({
    width: '100%'
  });

  $('.milestonesYearSelect select').on('change', function() {
    var $item = $('#milestoneYear-template').clone(true).removeAttr('id');
    var $list = $(this).parents('.milestonesYearBlock').find(".milestonesYearList");
    var year = ($list.parents('.tab-pane').attr('id')).split('-')[1];
    var title = $(this).find('option:selected').text();
    var milestonId = $(this).find('option:selected').val();

    $item.find('.title').text(title);
    $item.find('.crpMilestoneId').val(milestonId);
    $item.find('.year').val(year);

    $item.find('select').select2({
      width: '100%'
    });

    $list.append($item);

    $item.show('slow');

    milestonesCount

    $item.setNameIndexes(1, milestonesCount);
    milestonesCount++;

    // updateMilestonesIndex();

  });

  $('.removeProjectMilestone').on('click', function() {
    var $parent = $(this).parent();
    $parent.hide('slow', function() {
      $parent.remove();
      // updateMilestonesIndex();
    });
  });

});

function updateMilestonesIndex() {
  $('form .milestoneYear').each(function(i,e) {
    $(e).setNameIndexes(1, i);
  });
}