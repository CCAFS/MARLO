$(document).ready(init);
var slideIndex = 1;
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
  dots[slideIndex - 1].className += " active";
}