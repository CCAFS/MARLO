$(document).ready(init);

function init() {

  /* Declaring Events */
  attachEvents();
  $('form select').select2({
    width: "100%"
  });
  // Add options
  $(".cgiarUser").addOption("false", "No");
  $(".cgiarUser").addOption("true", "Yes");
  $(".isActive").addOption("false", "No");
  $(".isActive").addOption("true", "Yes");
  $(".autosave").addOption("false", "No");
  $(".autosave").addOption("true", "Yes");
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
    if(validateEmail(email)) {
      $.ajax({
          url: baseURL + "/searchUserByEmail.do",
          type: 'GET',
          data: {
            userEmail: email
          },
          success: function(m) {
            console.log(m);
            if(m.userFound.newUser == false) {
              $(".infoService").css("color", "green");
              $(".infoService").text("Found user.");
              enableFields(true);
              updateData(m.userFound);
            } else {
              enableFields(false);
              $(".infoService").css("color", "rgb(136, 72, 9)");
              $(".infoService").text("This user doesn't exists, you can to create a new user.");
              var user = {
                  id: "",
                  name: "",
                  lastName: "",
                  email: m.userFound.email,
                  username: "",
                  cgiar: "false",
                  active: "false",
                  autosave: "false"
              };
              updateData(user);
            }
          },
          error: function(e) {
            console.log(e);
          }
      });
    } else {
      enableFields(true);
      $(".infoService").css("color", "red");
      $(".infoService").text("Please, write a valid email.");
    }
  });

}

function updateData(user) {
  // Empty fields
  // User data
  $(".userId").val(user.id);
  $(".userFirstName").val(user.name);
  $(".userLastName").val(user.lastName);
  $(".userEmail").val(user.email);
  $(".userUsername").val(user.username);
  $(".userPassword").val();
  // Configuration
  console.log(user.cgiar);
  $(".cgiarUser").val(user.cgiar.toString()).trigger("change");
  $(".isActive").val(user.active.toString()).trigger("change");
  $(".autosave").val(user.autosave.toString()).trigger("change");

}

function enableFields(state) {
  // User data
  $(".userId").attr("disabled", state);
  $(".userFirstName").attr("disabled", state);
  $(".userLastName").attr("disabled", state);
  $(".userEmail").attr("disabled", state);
  $(".userUsername").attr("disabled", state);
  $(".userPassword").attr("disabled", state);
  // Configuration
  $(".cgiarUser").attr("disabled", state);
  $(".isActive").attr("disabled", state);
  $(".autosave").attr("disabled", state);
}

function validateEmail(email) {
  var re =
      /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
  return re.test(email);
}
