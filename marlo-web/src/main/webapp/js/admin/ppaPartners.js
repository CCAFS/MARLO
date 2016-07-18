$(document).ready(init);
var partnerSelect, partnerContent;

function init() {
  partnerSelect = $("select");
  partnerSelect.attr("data-live-search", "true")
  partnerContent = $("#partnerContent");
  /* Declaring Events */
  attachEvents();
}

function attachEvents() {
  updateIndex();
  removePartner();
  var partner = "";
  // Getting event of Select
  partnerSelect.on('changed.bs.select', function() {
    partner = partnerSelect.find("option:selected");
    if(partner[0].value != -1 && partner[0].value != null) {
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
    });
  });
}

// this function verify that the option selected don't exists in the parnerContent
function differences() {
  partner = partnerSelect.find("option:selected");
  if(partnerContent.find('input[value=' + partner.val() + ']').exists()) {
    var notyOptions = jQuery.extend({}, notyDefaultOptions);
    notyOptions.text = partner.text() + ' already exists in this list';
    notyOptions.type = 'alert';
    noty(notyOptions);
  } else {
    addPartner(partner);
  }
  console.log(partnerSelect.find("option[value= '-1' ]").text());
  partnerSelect.selectpicker('val', '-1');
}

// Update index and position of property name
function updateIndex() {
  $(partnerContent).find('.institution').each(function(i,item) {
    $(item).find('.institutionId').attr('name', 'loggedCrp.crpInstitutionsPartners[' + i + '].institution.id');
    $(item).find('.id').attr('name', 'loggedCrp.crpInstitutionsPartners[' + i + '].id');
  });
}