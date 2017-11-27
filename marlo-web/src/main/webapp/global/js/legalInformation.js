$(document).ready(init);

function init() {
  $('p').each(function(i,e) {
    var html = urlifyComplete($(e).html());
    $(e).html(html);
  });
}
