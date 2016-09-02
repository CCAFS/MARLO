$(document).ready(init);

function init() {
  /* Init Select2 plugin */
  $('form select').select2({
    width: "100%"
  });
  console.log("debug");
  $(".addActivity").on("click", addActivity);

  $('.blockTitle').on('click', function() {
    if($(this).hasClass('closed')) {
      $(this).parent().find('.blockTitle').removeClass('opened').addClass('closed');
      $(this).removeClass('closed').addClass('opened');
    } else {
      $(this).removeClass('opened').addClass('closed');
    }
    $(this).next().slideToggle('slow', function() {
      $(this).find('textarea').autoGrow();
    });
  });

}

// Add a new activity element
function addActivity() {
  var $list = $(".activitiesOG-content");
  var $item = $("#projectActivity-template").clone(true).removeAttr("id");
  $list.append($item);
  $item.show('slow');
  // checkItems($list);
  // updatePartners();
}
