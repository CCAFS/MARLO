$(document).ready(init);

function init() {

  var searchInput = $("#search");
  searchInput.on("change keyup", function() {
    searchWord(searchInput.val());
  });

  $(".tag").on("click", function() {
    // event.preventDefault();
    index(this);
  });

}

function index(element) {
  var divs = $("#content div[id^='" + $(element).html().toLowerCase() + "']");
  var div = divs[0];
  console.log(div);
  $('html,body').animate({
    scrollTop: ($(div).offset().top) - 15
  }, 'slow');
}

function searchWord(input) {
  var word = input;
  var message = $("#message");
  var content = $("#content");
  content.find(".word").each(function(i,e) {
    $(e).hide();
  })
  var divs = $("#content div:icontains('" + word + "')");
  if(divs.length <= 0) {
    message.show();
  } else {
    console.log(divs.length);
    divs.show();
    message.hide();
  }
}
