var crpContributionName, otherContributionsName;
var $contributionsBlock;
$(document).ready(init);

function init() {
  $contributionsBlock = $('ul#contributionsBlock');
  crpContributionName = $('#crpsName').val();
  otherContributionsName = $('#otherContributionsName').val();

  addSelect2();
  attachEvents();
  initItemListEvents();

}

function attachEvents() {
  // Remove a next user event
  // $('.removeElement').on('click', removeOtherContribution);
  // Add new next user event
  // $('#addOtherContribution .addLink').on('click', addOtherContribution);

  // Change a region or flagship
  // $('.otherContributionFlagship, .otherContributionRegion').on('change', changeRegionFlagship);

  // Disabled values that is not number
  $('.otherContributionTarget').on("keydown", function(e) {
    isNumber(e);
  });
}

function changeRegionFlagship(e) {
  var $parent = $(e.target).parents('.otherContribution');
  var $indicatorsSelect = $parent.find('select.otherContributionIndicator');
  var data = {
      flagshipID: $parent.find('select.otherContributionFlagship').val(),
      regionID: $parent.find('select.otherContributionRegion').val(),
      projectID: $('#projectID').val()
  }
  $.ajax({
      url: baseURL + '/iPIndicatorsByIPPrograms.do',
      data: data,
      beforeSend: function() {
        $parent.find('.loading').fadeIn('slow');
        $indicatorsSelect.empty();
        $indicatorsSelect.addOption(-1, 'Select an indicator');
      },
      success: function(result) {
        $.each(result.IPElementsList, function(i,element) {
          $indicatorsSelect.addOption(element.id, element.description);
        });
        $parent.find('.indicatorsFound').html('(' + result.IPElementsList.length + ' indicators found)');
      },
      error: function() {
        $parent.find('.indicatorsFound').html('(0 indicators found)');
      },
      complete: function(info) {
        $parent.find('.loading').fadeOut('slow');
      }
  });
}

function addOtherContribution(e) {
  e.preventDefault();
  $('#otherContributionsBlock').find('.emptyMessage').fadeOut();
  var $clone = $("#otherContribution-template").clone(true).removeAttr("id");
  $clone.find('select').select2();
  $clone.appendTo($('#otherContributionsBlock')).hide().show('slow');
  setOtherContributionsIndexes();
}

function removeOtherContribution(e) {
  e.preventDefault();
  $(e.target).parent().hide('slow', function() {
    $(this).remove();
    setOtherContributionsIndexes();
  });
}

function setOtherContributionsIndexes() {
  $('form .otherContribution').each(setOtherContributionIndex);
}

function setOtherContributionIndex(i,element) {
  var name = otherContributionsName + '[' + i + '].';
  $(element).find(".otherContributionId").attr("name", name + "id");
  $(element).find(".otherContributionRegion").attr("name", name + "region");
  $(element).find(".otherContributionFlagship").attr("name", name + "flagship");
  $(element).find(".otherContributionIndicator").attr("name", name + "indicators");
  $(element).find(".otherContributionDescription").attr("name", name + "description");
  $(element).find(".otherContributionTarget").attr("name", name + "target");
}

// Items list functions
function initItemListEvents() {
  $('select.crpsSelect').on('change', function(e) {
    addItemList($(this).find('option:selected'));
  });
  $('ul li .remove').on('click', function(e) {
    removeItemList($(this).parents('li'));
  });

  setInitialList();
}

function setInitialList() {
  $("div.crpContribution").each(function(index,element) {
    // Getting previously selected by project partner
    var $select = $(element).find('select');
    $(element).find('li input.id').each(function(i_id,id) {
      $select.find('option[value=' + $(id).val() + ']').remove();
    });
    $select.trigger("liszt:updated");
  });
}

function removeItemList($item) {
  // Adding to select list
  var $select = $item.parents('.panel').find('select');
  $select.append(setOption($item.find('.id').val(), $item.find('.name').text()));
  $select.trigger("liszt:updated");
  // Removing from list
  $item.hide("slow", function() {
    $item.remove();
    setIndexes();
  });
}

function addItemList($option) {
  var $select = $option.parent();
  var $li = $("#crpOtherContribution-template").clone(true).removeAttr("id");
  $li.find('.id').val($option.val());
  $li.find('.name').html($option.text());
  $li.appendTo($contributionsBlock).hide().show('slow');
  $option.remove();
  $select.trigger("liszt:updated");
  // setIndexes();
  $contributionsBlock.find('.emptyText').fadeOut();
}

function setIndexes() {
  $contributionsBlock.find('li').each(function(i,item) {
    var elementName = crpContributionName + '[' + i + ']';
    $(item).find('.id').attr('name', elementName + '.crp.id');
    $(item).find('.crpContributionId').attr('name', elementName + '.id');
    $(item).find('.crpCollaborationNature').attr('name', elementName + '.natureCollaboration');
    $(item).find('.crpCollaborationAchieved').attr('name', elementName + '.explainAchieved');

  });
}

function addSelect2() {
  $('form select').select2();
}
