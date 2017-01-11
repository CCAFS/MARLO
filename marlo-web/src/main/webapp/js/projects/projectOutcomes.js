var $targetValue;

$(document).ready(function() {

  $targetValue = $('.projectIndicatorTarget, .projectIndicatorAchievedTarget');

  // Check for numeric value already inserted
  $targetValue.on("keyup", function(e) {
    var isEmpty = (e.target.value == "");
    var isRequired = $(e.target).hasClass("required");
    var isNumeric = $.isNumeric(e.target.value);
    var hasMissFields = $('.hasMissingFields').exists();
    var valueError = (!isNumeric && isRequired && hasMissFields);
    if(valueError) {
      $(e.target).addClass("fieldError").attr("title", "This field require a numeric value");
    } else {
      $(e.target).removeClass("fieldError").attr("title", "");
    }
  });
  $targetValue.trigger("keyup");

});