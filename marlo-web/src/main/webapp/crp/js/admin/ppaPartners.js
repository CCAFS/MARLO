$(document).ready(init);
var $partnerSelect, partnerContent;

function init() {
  $partnerSelect = $("form select");
  partnerContent = $("#partnerContent");

  /* Select2 */
  $partnerSelect.select2(searchInstitutionsOptionsData({
      includePPA: true,
      projectPreSetting: 0
  }));
  $partnerSelect.parent().find("span.select2-selection__placeholder").text(placeholderText);

  /* Declaring Events */
  attachEvents();
}

function attachEvents() {

  // Remove partner item
  $(".delete").on('click', removePartner);

  
  // Getting event of Select
  $partnerSelect.on('change', function(e) {
    var $partner = $(this); 

    if($partner.val() == -1) {
      return
    }
    
    // Check if already exist
    if(partnerContent.find('input[value=' + $partner.val() + ']').exists()) {
      var notyOptions = jQuery.extend({}, notyDefaultOptions);
      notyOptions.text = $("#institutionArray-" + $partner.val()).text() + ' already exists in this list';
      notyOptions.type = 'alert';
      noty(notyOptions);
    } else {
      addPartner($partner);
    }
    
    // Reset select
    $partnerSelect.val('-1');
    $partnerSelect.trigger('select2:change')
    
  });

  updateIndex();
}

// Add partner item
function addPartner(partner) {
  var $item = $('#institution-template').clone(true).removeAttr('id');
  $item.find('input.institutionId').val($(partner).val());
  $item.find('.title').html($("#institutionArray-"+$(partner).val()).text());
  partnerContent.append($item);
  $item.show("slow");
  updateIndex();
}

function removePartner() {
  var $institution = $(this).parents(".institution");
  $institution.hide(500, function() {
    $institution.remove();
    updateIndex();
  });
}

// Update index and position of property name
function updateIndex() {
  $(partnerContent).find('.institution').each(function(i,item) {
    $(item).find('.institutionId').attr('name', 'loggedCrp.crpInstitutionsPartners[' + i + '].institution.id');
    $(item).find('.id').attr('name', 'loggedCrp.crpInstitutionsPartners[' + i + '].id');
  });
}