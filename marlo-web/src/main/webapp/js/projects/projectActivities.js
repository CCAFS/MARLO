$(document).ready(console.log("asdad"));

function init() {
  /* Init Select2 plugin */
  $('form select').select2({
    width: "100%"
  });
  console.log("debug");
  $(".addActivity").on("click", addActivity);

}

// Add a new person element
function addActivity() {
  console.log("holi");
  var $list = $(".activitiesOG-content");
  var $item = $("#projectActivity-template").clone(true).removeAttr("id");
  $list.append($item);
  $item.show('slow');
  // checkItems($list);
  // updatePartners();
}