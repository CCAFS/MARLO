$(document).ready(init);

function init() {

  // Add Select2
  $('form select.globalUnitSelect').select2({
      width: '100%',
      templateResult: formatSelect2Result
  });

  attachEvents();
}

function attachEvents() {

  addFlagshipAutoComplete();

  // Add a program collaboration
  $('.addProgramCollaboration').on('click', addProgramCollaboration);

  // Remove a program collaboration
  $('.removeProgramCollaboration').on('click', removeProgramCollaboration);

}

function addProgramCollaboration() {
  var $list = $(this).parents("form").find('.listProgramCollaborations');
  var $item = $('#flagshipCollaboration-template').clone(true).removeAttr("id");
  $list.append($item);

  // Add select
  $item.find('select.globalUnitSelect').select2({
      width: '100%',
      templateResult: formatSelect2Result
  });

  // Add auto complete
  var $autoCompleteInput = $item.find('.globalUnitPrograms');

  // Clear Program
  $autoCompleteInput.val('');

  $autoCompleteInput.autocomplete({
      source: searchFlagships,
      select: selectFlagship,
      minLength: 0
  }).autocomplete("instance")._renderItem = renderItem;

  $item.show('slow');
  updateIndexes();
}

function removeProgramCollaboration() {
  var $item = $(this).parents('.flagshipCollaboration');
  $item.hide(function() {
    $item.remove();
    updateIndexes();
  });
}

function updateIndexes() {
  $(".listProgramCollaborations").find(".flagshipCollaboration").each(function(i,element) {
    $(element).setNameIndexes(1, i);
    $(element).find(".index").html(i + 1);

    // Update labels
    $(element).find('.radio-input').each(function(j,input) {
      $(input).next().attr('for', "raioLabel-" + i + "-" + j);
      $(input).attr('id', "raioLabel-" + i + "-" + j);
    });

  });
}

function formatSelect2Result(item) {
  if(item.loading) {
    return item.text;
  }
  console.log(item.id);
  var $item = $('#globalUnit-' + item.id).clone();
  return $item;
}

/**
 * This function initialize the Flagships auto complete
 * 
 * @returns
 */
function addFlagshipAutoComplete() {

  $('select.globalUnitSelect').on('change', function() {
    var $autoCompleteInput = $(this).parents('.flagshipCollaboration').find('.globalUnitPrograms');

    // Clear Program
    $autoCompleteInput.val('');

    $autoCompleteInput.autocomplete({
        source: searchFlagships,
        select: selectFlagship,
        minLength: 0
    }).autocomplete("instance")._renderItem = renderItem;

  });
}

function searchFlagships(request,response) {
  var selectedUnit = $(this.element).parents('.flagshipCollaboration').find('select.globalUnitSelect').val();
  $.ajax({
      url: baseURL + '/annualCrpProgramByCrp.do',
      data: {
          phaseID: phaseID,
          crpID: selectedUnit
      },
      success: function(data) {
        response(data.crpPrograms);
      }
  });
}

function selectFlagship(event,ui) {
  $(this).val(ui.item.acronym + " - " + ui.item.description);
  return false;
}

function renderItem(ul,item) {
  return $("<li>").append("<div>" + item.acronym + " - " + item.description + "</div>").appendTo(ul);
}
