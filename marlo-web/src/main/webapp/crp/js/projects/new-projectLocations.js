$(document).ready(init);
var map;
var style;
var infoWindow = null;
var markers = [];
var countID;
var countries = [];
var layer;

function init() {
// Init select2
  $('form select').select2({
    width: "100%"
  });

  /* Declaring Events */
  attachEvents();

}

function attachEvents() {

  // REGIONAL QUESTION
  $(".button-label").on("click", function() {
    var valueSelected = $(this).hasClass('yes-button-label');
    var $input = $(this).parent().find('input');
    $input.val(valueSelected);
    $(this).parent().find("label").removeClass("radio-checked");
    $(this).addClass("radio-checked");
    $(document).trigger('updateComponent');
  });

  $(".isRegional .button-label").on("click", function() {
    var valueSelected = $(this).hasClass('yes-button-label');
    if(!valueSelected) {
      $(".regionsBox").hide("slow");
      $(".regionsContent").hide("slow");
    } else {
      $(".regionsBox").show("slow");
      $(".regionsContent").show("slow");
    }
  });

  // Region Select
  $("#regionSelect").on("change", function() {
    var option = $(this).find("option:selected");
    if(option.val() != "-1") {
      addRegion(option);
      // Remove option from select
      // option.remove();
      option.prop('disabled', true);
      $('#regionSelect').select2();
      // $(this).trigger("select2:change");
    }
  });
}

//Add Regions
function addRegion(option) {
  var canAdd = true;
  if(option.val() == "-1") {
    canAdd = false;
  }
  var optionValue = option.val().split("-")[0];
  var optionScope = option.val().split("-")[1];

  var $list = $(option).parents("#regionList").find(".list");
  var $item = $("#regionTemplate").clone(true).removeAttr("id");
  var v = $(option).text().length > 20 ? $(option).text().substr(0, 20) + ' ... ' : $(option).text();

  // Check if is already selected
  $list.find('.region').each(function(i,e) {
    if($(e).find('input.rId').val() == optionValue) {
      canAdd = false;
      return;
    }
  });
  if(!canAdd) {
    return;
  }

  // Set region parameters
  $item.find(".name").attr("title", $(option).text());
  $item.find(".name").html(v);
  $item.find(".rId").val(optionValue);
  $item.find(".regionScope").val(optionScope);
  $item.find(".id").val(-1);
  $list.append($item);
  $item.show('slow');
  updateRegionList($list);
  checkRegionList($list);

}

function updateRegionList($list) {

  $($list).find('.region').each(function(i,e) {
    // Set regions indexes
    $(e).setNameIndexes(1, i);
  });
}

function checkRegionList(block) {
  var items = $(block).find('.region').length;
  if(items == 0) {
    $(block).parent().find('p.emptyText').fadeIn();
  } else {
    $(block).parent().find('p.emptyText').fadeOut();
  }
}