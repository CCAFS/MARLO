$(document).ready(init);

function init() {

  attachEvents();

  // initTabs();

}

function attachEvents() {

  $('.isNumeric').on("keydown", isNumber);

  $('.showIndicatorDesc').on('click', showIndicatorDesc);

}

function initTabs() {
  $("#crpIndicatorsTabs").tabs();
}

function showIndicatorDesc(e) {
  e.preventDefault();
  var descID = $(this).attr('id').split('-')[1];
  $('#indicatorDesc-' + descID).slideToggle();
}