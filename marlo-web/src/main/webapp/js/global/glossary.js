$(document).ready(init);

function init() {
  var searchInput = $("#search");
  searchInput.on("change keyup", function() {
    searchWord(searchInput.val());
  });
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
