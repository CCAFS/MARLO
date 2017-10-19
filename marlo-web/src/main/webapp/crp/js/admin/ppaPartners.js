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
  
  // Add User - Override function from userManagement.js
  addUser = addUserItem;

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
    $(item).setNameIndexes(1, i);
    
    $(item).find('.userItem').each(function(ui,user){
      $(user).setNameIndexes(2, ui);
    });
  });
}

function addUserItem(composedName,userId) {
  $usersList = $elementSelected.parents('.ppaPartner').find(".items-list");
  var $li = $("#user-template").clone(true).removeAttr("id");
  var item = {
      name: escapeHtml(composedName),
      id: userId,
      type: $elementSelected.parents('.usersBlock').find('.usersType').text(),
      role: $elementSelected.parents('.usersBlock').find('.usersRole').text()
  }
  $li.find('.name').html(item.name);
  $li.find('.user').val(item.id);

  $usersList.find("ul").append($li);
  $li.show('slow');
  
  updateIndex();
  dialog.dialog("close");
}