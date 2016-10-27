$(document).ready(init);
function init() {

  $(".yes-button-label").on("click", function() {
    $(".no-button-label").removeClass("radio-checked");
    $(this).addClass("radio-checked");
    $(".selectHeadquater").slideDown("slow");
  });

  $(".no-button-label").on("click", function() {
    $(".yes-button-label").removeClass("radio-checked");
    $(this).addClass("radio-checked");
    $(".selectHeadquater").slideUp("slow");
  });

}