$(document).ready(init);

function init() {
  attachEvents();
}

function attachEvents() {

  $('.addSlo').on('click', addSlo);

  $('.addIndicator').on('click', addIndicator);

  $('.addTargets').on('click', addTarget);

  $('.addCrossCuttingIssue').on('click', addCrossCuttingIssue);

  $('.remove-element').on('click', removeElement);

  $('.confirmDelete').on('click', function(e) {
    if (!confirm($(this).data('confirm'))) {
      e.preventDefault();
    }
  });

  $('.blockTitle.closed').on('click', function() {
    if($(this).hasClass('closed')) {
      $('.blockContent').slideUp();
      $('.blockTitle').removeClass('opened').addClass('closed');
      $(this).removeClass('closed').addClass('opened');
    } else {
      $(this).removeClass('opened').addClass('closed');
    }
    $(this).next().slideToggle();
  });

}

function addSlo() {
  var $itemsList = $(this).parent().find('.slos-list');
  var $item = $("#srfSlo-template").clone(true).removeAttr("id");
  $item.find('.blockTitle').trigger('click');
  $itemsList.append($item);
  $item.slideDown('slow');
  updateIndexes();
  $item.trigger('addComponent');
}

function addIndicator() {
  var $itemsList = $(this).parent().parent().find('.srfIndicators-list');
  var $item = $("#srfSloIndicator-template").clone(true).removeAttr("id");

  $itemsList.append($item);
  $item.slideDown('slow');
  updateIndexes();
  $item.trigger('addComponent');
}

function addTarget() {
  var $itemsList = $(this).parent().parent().find('.targetsList');
  var $item = $("#targetIndicator-template").clone(true).removeAttr("id");

  $itemsList.append($item);
  $item.slideDown('slow');
  updateIndexes();
  $item.trigger('addComponent');
}

function addCrossCuttingIssue() {
  var $itemsList = $(this).parent().find('.issues-list');
  var $item = $("#srfCCIssue-template").clone(true).removeAttr("id");

  $itemsList.append($item);
  $item.slideDown('slow');
  updateIndexes();
  $item.trigger('addComponent');
}

function removeElement() {
  var $item;
  if ($(this).hasClass('targetRemove')) {
    $item = $(this).closest('.targetsIndicator');
  } else {
    $item = $(this).parent();
  }
  $item.hide('slow', function() {
    $item.remove();
    updateIndexes();
    $(document).trigger('removeComponent');
  });
}

function updateIndexes() {
  $('.slos-list .srfSlo').each(function(i, slo) {
    $(slo).setNameIndexes(1, i);
    $(slo).find('.srfSloIndicator').each(function(indicatorIndex, indicator) {
      $(indicator).setNameIndexes(2, indicatorIndex);
      $(indicator).find('.targetsIndicator').each(function(targetIndex, target) {
        $(target).setNameIndexes(3, targetIndex);
      });
    });
  });

  $('.issues-list .srfCCIssue').each(function(issueIndex, issue) {
    $(issue).setNameIndexes(1, issueIndex);
  });
}
