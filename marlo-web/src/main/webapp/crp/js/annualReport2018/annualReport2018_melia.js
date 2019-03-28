$(document).ready(init);

function init() {

  // Add Select2
  $('form select').select2({
    width: '100%'
  });

  attachEvents();
}

function attachEvents() {

  // Add a program collaboration
  $('.addEvaluation').on('click', addEvaluation);

  // Remove a program collaboration
  $('.removeEvaluation').on('click', removeEvaluation);

}

function addEvaluation() {
  var $list = $(this).parents("form").find('.listEvaluations');
  var $item = $('#evaluation-template').clone(true).removeAttr("id");
  $list.append($item);

  // Add select
  $item.find('select').select2({
    width: '100%'
  });

  $item.find('textarea').setTrumbowyg();

  $item.show('slow');
  updateIndexes();
}

function removeEvaluation() {
  var $item = $(this).parents('.evaluation');
  $item.hide(function() {
    $item.remove();
    updateIndexes();
  });
}

function updateIndexes() {
  $(".listEvaluations").find(".evaluation").each(function(i,element) {
    $(element).setNameIndexes(1, i);
    $(element).find(".index").html(i + 1);

    // Update actions
    $(element).find(".evaluationAction").each(function(j,evalAction) {
      $(evalAction).setNameIndexes(2, j);
      $(evalAction).find(".index").html(j + 1);
    });

  });
}