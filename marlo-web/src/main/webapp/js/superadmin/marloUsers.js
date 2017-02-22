$(document).ready(init);

function init() {

  /* Declaring Events */
  attachEvents();
  $('form select').select2({
    width: '100%'
  });
}

function attachEvents() {

  $('.blockTitle.closed').on('click', function() {
    if($(this).hasClass('closed')) {
      $('.blockContent').slideUp();
      $('.blockTitle').removeClass('opened').addClass('closed');
      $(this).removeClass('closed').addClass('opened');
    } else {
      $(this).removeClass('opened').addClass('closed');
    }
    $(this).next().slideToggle('slow', function() {
      $(this).find('textarea').autoGrow();
    });
  });

  $(".checkEmail").on("keyup", function() {
    var email = $(this).val();
    $.ajax({
        url: baseURL + "/searchUserByEmail.do",
        type: 'GET',
        data: {
          userEmail: email
        },
        success: function(m) {
          console.log(m);
        },
        error: function(e) {
          console.log(e);
        }
    });
  });

}
