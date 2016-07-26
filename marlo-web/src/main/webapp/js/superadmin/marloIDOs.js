$(document).ready(init);

function init() {

  /* Declaring Events */
  attachEvents();

  $('.yes-button-label').on('click', function() {
    yesnoEventCrossCutting(true, $(this));
  });
  $('.no-button-label').on('click', function() {
    yesnoEventCrossCutting(false, $(this));
  });

}

function yesnoEventCrossCutting(value,item) {
  $t = item.parent().find('input.onoffswitch-radio');
  var array = $t.attr('name').split('.');
  var $aditional = $t.parents('.srfIdo').find('.aditional-' + array[array.length - 1]);
  if(value == true) {
    item.siblings().removeClass('radio-checked');
    item.addClass('radio-checked');
    $aditional.slideDown("slow");
    $t.val(value);
  } else {
    var locElements = $t.parents('.locationLevel').find('.locElement').length;
    if(locElements > 0) {
      // Do something
    } else {
      item.siblings().removeClass('radio-checked');
      item.addClass('radio-checked');
      $aditional.slideUp("slow");
      $t.val(value);
    }
  }
}

function attachEvents() {

  $('.addIdo').on('click', addIdo);

  $('.addSubIdo').on('click', addSubIdo);

  $('.remove-element').on('click', removeElement);

  $('.blockTitle.closed').on('click', function() {
    if($(this).hasClass('closed')) {
      $('.blockContent').slideUp();
      $('.blockTitle').addClass('closed');
    }
    $(this).next().slideToggle();
    $(this).removeClass('closed');
  });
}

function addIdo() {
  var $itemsList = $(this).parent().find('.idos-list');
  var $item = $("#srfIdo-template").clone(true).removeAttr("id");
  $item.find('.blockTitle').trigger('click');
  $itemsList.append($item);
  $item.slideDown('slow');
  updateIndexes();
  $item.trigger('addComponent');
}

function addSubIdo() {
  var $itemsList = $(this).parent().parent().find('.subIdosList');
  var $item = $("#srfSubIdo-template").clone(true).removeAttr("id");

  $itemsList.append($item);
  $item.slideDown('slow');
  updateIndexes();
  $item.trigger('addComponent');
}

function removeElement() {
  $item = $(this).parent();
  $item.hide('slow', function() {
    $item.remove();
    updateIndexes();
    $(document).trigger('removeComponent');
  });
}

function updateIndexes() {
  $('.idos-list .srfIdo').each(function(i,ido) {
    // Updating indexes
    $(ido).setNameIndexes(1, i);
    $(ido).find('.srfSubIdo').each(function(subIdoIndex,subIdo) {
      // Updating indexes
      $(subIdo).setNameIndexes(2, subIdoIndex);
    });
  });
}
