$(document).ready(function() {

  var $partnershipsTable = $('table.partnershipsTable_');

  var table = $partnershipsTable.DataTable({
      "bPaginate": true, // This option enable the table pagination
      "bLengthChange": false, // This option disables the select table size option
      "bFilter": false, // This option enable the search
      "bSort": true, // this option enable the sort of contents by columns
      "bAutoWidth": false, // This option enables the auto adjust columns width
      "iDisplayLength": 5, // Number of rows to show on the table
      "order": [
        [
            0, 'desc'
        ]
      ],
      aoColumnDefs: [
          {
              bSortable: true,
              aTargets: [
                -1
              ]
          }, {
              sType: "natural",
              aTargets: [
                0
              ]
          }
      ]
  });

  // Add Select2
  $('form select').select2({
      width: '100%',
      templateResult: formatSelect2Result
  });

  attachEvents();
});

function attachEvents() {

  setViewMores();

  addFlagshipAutoComplete();

  // Add a program collaboration
  $('.addProgramCollaboration').on('click', addProgramCollaboration);

  // Remove a program collaboration
  $('.removeProgramCollaboration').on('click', removeProgramCollaboration);

  // Update Efforts Country Question
  $('.updateEffostornCountry').on('keyup', updateEffostornCountry);

}

function updateEffostornCountry() {
  var pmuValue = ""
  $('textarea.updateEffostornCountry').each(function(i,input) {
    if(input.value) {
      pmuValue += $(input).parents('.regionBox').find('.regionTitle').text() + "\n";
      pmuValue += input.value + "\n \n";
    }
  });

  $('#pmuValue').val(pmuValue);
}

function addProgramCollaboration() {
  var $list = $(this).parents("form").find('.listProgramCollaborations');
  var $item = $('#flagshipCollaboration-template').clone(true).removeAttr("id");
  $list.append($item);

  // Add select
  $item.find('select').select2({
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
  var $item = $('#globalUnit-' + item.id).clone();
  return $item;
}

/** viewMoreSyntesis functions * */

function expandViewMoreSyntesisBlock() {

  var blockHeight = $(this).parent().find('table').height() + $(this).height();
  var defaultHeigth = 250;

  if($(this).hasClass("closed")) {
    $(this).parent().css({
      height: blockHeight + 8
    });
    $(this).html('View less');
    $(this).addClass("opened").removeClass("closed");
  } else if($(this).hasClass("opened")) {
    $(this).parent().css({
      height: defaultHeigth
    });
    $(this).html('View More');
    $(this).addClass("closed").removeClass("opened");
  }
}

function setViewMores() {
  var defaultHeigth = 250;
  $('.viewMoreSyntesis-block').each(function(i,element) {
    var $viewMoreButton = $(element).find('.viewMoreSyntesis');

    if($(element).height() < defaultHeigth) {
      $viewMoreButton.remove();
    } else {
      $(element).css({
        "height": defaultHeigth
      })
      $viewMoreButton.addClass("closed");
      $viewMoreButton.html('View More');
    }
    // Show the block if is hidden
    $(element).show();
    // Add Event
    $viewMoreButton.on('click', expandViewMoreSyntesisBlock);
  });
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
      url: baseURL + '/crpProgramsGlobalUnit.do',
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