$(document).ready(init);

function init() {

/* Init Select2 plugin */
  $('.impactsCategoriesSelect').select2({
    width: "100%",
    minimumResultsForSearch: Infinity
  });
}