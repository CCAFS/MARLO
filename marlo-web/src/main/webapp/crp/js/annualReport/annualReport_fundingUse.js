$(document).ready(init);

function init() {
  // Setting Percentage Inputs
  $('form input.percentageInput').percentageInput();

  // Add Select2
  $('form select').select2({
    width: '100%'
  });

}