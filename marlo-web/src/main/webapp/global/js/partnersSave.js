$(document).ready(init);
var slideIndex = 1;
function init() {

  /* Init Select2 plugin */
  $('form select').select2({
    width: '100%'
  });

  $("select.institutionTypes").select2({
    templateResult: function(state) {
      if (state.id == -1){
        return
      }
      var $state = $("<span>" + $('#institutionType-' + state.id).html() + "</span>");
      return $state;
    }
  });

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

  // Slide info
  showSlides(slideIndex);

}

function currentSlide(n) {
  showSlides(slideIndex = n);
}

function showSlides(n) {
  var i;
  var slides = document.getElementsByClassName("mySlides");
  var dots = document.getElementsByClassName("dot");
  if(n > slides.length) {
    slideIndex = 1
  }
  if(n < 1) {
    slideIndex = slides.length
  }
  for(i = 0; i < slides.length; i++) {
    slides[i].style.display = "none";
  }
  for(i = 0; i < dots.length; i++) {
    dots[i].className = dots[i].className.replace(" active", "");
  }
  slides[slideIndex - 1].style.display = "block";
  if(typeof dots[slideIndex - 1] !== 'undefined'){
    dots[slideIndex - 1].className += " active";
  }
}