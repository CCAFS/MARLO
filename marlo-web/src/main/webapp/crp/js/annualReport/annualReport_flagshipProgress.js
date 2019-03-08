$(document).ready(init);

function init() {

  // Add Select2
  $('form select').select2({
    width: '100%'
  });


  setTextFieldHeight();



}

//html text fields
function setTextFieldHeight() {
 var chart = $('.chartCount').text();
 console.log(chart);

}