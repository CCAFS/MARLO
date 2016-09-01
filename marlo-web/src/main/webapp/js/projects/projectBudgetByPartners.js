$(document).ready(init);

function init() {

  // Setting Numeric Inputs
  $('input.currencyInput, input.percentageInput').numericInput();

  // Setting Currency Inputs
  $('input.currencyInput').currencyInput();

  // Setting Percentage Inputs
  $('input.percentageInput').percentageInput();

  // Attaching events
  attachEvents();
}

function attachEvents() {

}