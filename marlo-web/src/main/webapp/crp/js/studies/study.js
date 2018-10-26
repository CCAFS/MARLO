$(document).ready(init);

function init() {

  // Select2
  $('select').select2({
    width: '100%'
  });

  // Set visible publications questions
  $('.publicationMetadataBlock').show();

  // Attaching events
  attachEvents();
}

function attachEvents() {

  /** Lead Partners * */

  $(".leadPartnersSelect").on("change", function() {
    var option = $(this).find("option:selected");
    if(option.val() != "-1") {
      addItem(option);
    }
    // Remove option from select
    option.remove();
    $(this).trigger("change.select2");
  });
  $(".removeLeadPartner").on("click", removeItem);

  // Validate if funding source exists in select
  $("form .leadPartner").each(function(i,e) {
    var options = $(".leadPartnersSelect option");
    options.each(function(iOption,eOption) {
      if($(e).find(".fId").val() == $(eOption).val()) {
        $(eOption).remove();
      }
    });
  });

}

function addItem(option) {
  var canAdd = true;
  console.log(option.val());
  if(option.val() == "-1") {
    canAdd = false;
  }

  var $list = $(option).parents(".select").parents("#leadPartnerList").find(".list");
  var $item = $("#leadPartner-template").clone(true).removeAttr("id");
  var v = $(option).text().length > 80 ? $(option).text().substr(0, 80) + ' ... ' : $(option).text();

  // Check if is already selected
  $list.find('.leadPartner').each(function(i,e) {
    if($(e).find('input.fId').val() == option.val()) {
      canAdd = false;
      return;
    }
  });
  if(!canAdd) {
    return;
  }

  // Set parameters
  $item.find(".name").attr("title", $(option).text()).tooltip();
  $item.find(".name").html(v);
  $item.find(".fId").val(option.val());
  $list.append($item);
  $item.show('slow');
  updateItems($list);
  checkLeadItems($list);

  // Reset select
  $(option).val("-1");
  $(option).trigger('change.select2');

}

function removeItem() {
  var $list = $(this).parents('.list');
  var $item = $(this).parents('.leadPartner');
  var value = $item.find(".fId").val();
  var name = $item.find(".name").attr("title");
  console.log(name + "-" + value);
  var $select = $(".leadPartnersSelect");
  $item.hide(800, function() {
    $item.remove();
    checkLeadItems($list);
    updateItems($list);
  });
// Add funding source option again
  $select.addOption(value, name);
  $select.trigger("change.select2");
}

function updateItems($list) {
  $($list).find('.leadPartner').each(function(i,e) {
    // Set funding sources indexes
    $(e).setNameIndexes(1, i);
  });
}

function checkLeadItems(block) {
  console.log(block);
  var items = $(block).find('.leadPartner').length;
  console.log(items);
  if(items == 0) {
    $(block).parent().find('p.emptyText').fadeIn();
  } else {
    $(block).parent().find('p.emptyText').fadeOut();
  }
}
