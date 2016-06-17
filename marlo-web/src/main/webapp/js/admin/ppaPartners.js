$(document).ready(init);
var partnerSelect, partnerContent;

function init() {
  partnerSelect = $("#ccafs_ppaPartners_");
  partnerSelect.attr("data-live-search", "true")
  partnerContent = $("#partnerContent");
  /* Declaring Events */
  attachEvents();
}

function attachEvents() {
  differences();
  updateIndex();
  removePartner();
  var partner = "";
  // Getting event of Select
  partnerSelect.on('changed.bs.select', function() {
    partner = partnerSelect.find("option:selected");
    if(partner[0].value != -1 && partner[0].value != null) {
      addPartner(partner);
      differences();
    }
  });

}

// Add partner item
function addPartner(partner) {
  var $item = $('#institution-template').clone(true).removeAttr('id');
  $item.find('input.institutionId').val(partner.val());
  $item.find('.title').html(partner.text());
  partnerContent.append($item);
  $item.show("slow");
  updateIndex();
}

// Remove partner item
function removePartner() {
  $(".delete").on('click', function() {
    var institution = $(this).parents(".institution");
    partnerSelect.find("option[value='" + institution.find("input").val() + "'] ").attr("disabled", false);
    institution.hide(1000, function() {
      institution.remove();
      updateIndex();
      differences();
    });
  });
}

// this function verify that the option selected don't exists in the parnerContent
function differences() {
  partner = partnerSelect.find("option:selected");
  if(partnerContent.find('input[value=' + partner.val() + ']').exists()) {
    partner.attr("disabled", true);
  }
  partnerSelect.selectpicker('refresh');
}

// Update index and position of property name
function updateIndex() {
  $(partnerContent).find('.institution').each(function(i,item) {
    console.log($(this));
    $(item).find('.institutionId').attr('name', 'loggedCrp.crpInstitutionsPartners[' + i + '].institution.id');
  });
}