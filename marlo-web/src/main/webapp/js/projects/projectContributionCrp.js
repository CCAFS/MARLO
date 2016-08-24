var milestonesCount;

$(document).ready(function() {

  milestonesCount = $('form .milestoneYear').length;

  $('form select').select2({
    width: '100%'
  });

  $('form .milestonesYearSelect').each(function(i,e) {

    var $select = $(e).find('select');
    var selectedIds = ($(e).find('.milestonesSelectedIds').text()).split(',');

    $select.clearOptions(selectedIds);
  });

  $('.milestonesYearSelect select').on('change', function() {
    var $item = $('#milestoneYear-template').clone(true).removeAttr('id');
    var $list = $(this).parents('.milestonesYearBlock').find(".milestonesYearList");
    var year = ($list.parents('.tab-pane').attr('id')).split('-')[1];
    var title = $(this).find('option:selected').text();
    var milestonId = $(this).find('option:selected').val();

    // Set the milestone parameters
    $item.find('.title').text(title);
    $item.find('.crpMilestoneId').val(milestonId);
    $item.find('.year').val(year);

    // Set Select2 widget
    $item.find('select').select2({
      width: '100%'
    });

    // Add the milestone to the list
    $list.append($item);

    // Show the milestone
    $item.show('slow');

    // Remove option from select
    $(this).find('option:selected').remove();
    $(this).trigger("change.select2");

    // Set indexes
    $item.setNameIndexes(1, milestonesCount);
    milestonesCount++;

    // Update milestone
    $list.find('.milestoneYear').each(function(i,e) {
      $(e).find('.index').text(i + 1);
    });

    // Get extra information from ajax service milestoneInformation.do?milestoneID=3
    $.ajax({
        url: baseURL + '/milestoneInformation.do',
        data: {
          milestoneID: milestonId
        },
        success: function(data) {
          console.log(data);
        }
    });

  });

  $('.removeProjectMilestone').on('click', function() {
    var $parent = $(this).parent();
    var $select = $parent.parents('.milestonesYearBlock').find('.milestonesYearSelect select');
    var value = $parent.find('.crpMilestoneId').val();
    var name = $parent.find('.title').text();

    $parent.hide('slow', function() {
      // Remove milestone block
      $parent.remove();

      // Update milestone
      $parent.parents('.milestonesYearBlock').find('.milestoneYear').each(function(i,e) {
        $(e).find('.index').text(i + 1);
      });

      // Add milestone option again
      $select.addOption(value, name);
      $select.trigger("change.select2");
    });
  });

});
