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

  setViewMore();
  $('.viewMoreSyntesis').on('click', expandViewMoreSyntesisBlock);

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
  $item.find('select').select2({
      width: '100%',
      templateResult: formatSelect2Result
  });

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
  console.log(item);
  if(item.loading) {
    return item.text;
  }
  var $item = $('#globalUnit-' + item.id).clone();
  return $item;
}

/** viewMoreSyntesis functions * */

function expandViewMoreSyntesisBlock() {

  var blockHeight = $(this).parent().find('table').height() + $(this).height();
  var defaultHeigth = 300;

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

function setViewMore() {
  var defaultHeigth = 300;
  $('.viewMoreSyntesis-block').each(function(i,element) {
    if($(element).height() < defaultHeigth) {
      $(element).find('.viewMoreSyntesis').remove();
    } else {
      $(element).css({
        "height": defaultHeigth
      })
      $(element).find('.viewMoreSyntesis').addClass("closed");
      $(element).find('.viewMoreSyntesis').html('View More');
    }
  });
}
