$(document).ready(init);

function init() {
  var categories = $('select.impactsCategoriesSelect');
  if ($('#actualPhase').html() == 'true') {
    categories.find('option[value="3"]').prop('disabled', true);
  }

  // Init Select2 plugin
  $('.impactsCategoriesSelect').select2({
    width: "100%",
    minimumResultsForSearch: Infinity
  });
}