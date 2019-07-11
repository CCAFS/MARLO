$(document).ready(init);

function init() {

  $('.usersTable').DataTable({
      dom: 'Bfrtip',
      buttons: [
          {
              extend: 'copy',
              title: 'Data export'
          }, {
              extend: 'csv',
              title: 'Data_export_' + getDateString()
          }
      ]
  });

  // Add guest user module
  guestUsersModule.init();

  // Attaching events
  attachEvents();
}

function attachEvents() {

}

var guestUsersModule =
    (function() {
      var $userEmail = $('input.userEmail');
      var $firstName = $('input.userFirstName');
      var $lastName = $('input.userLastName');
      var $selectedGlobalUnitID = $('input.selectedGlobalUnitID');
      var $message = $('#guestUserMessage');
      var $userNameBlock = $('.firstLastName');
      var $submitButton = $('button[name="save"]');
      var timer = null;
      var userHasAccess = false;

      function init() {
        events();

        validateForm();
      }

      function events() {
        $userEmail.on("keyup", function() {
          $userEmail.addClass('input-loading');
          disabledSubmitButton(true);
          if(timer) {
            clearTimeout(timer); // cancel the previous timer.
            timer = null;
          }
          timer = setTimeout(findUserEmail, 1500);
        });

        $firstName.on("change keyup", validateForm);
        $lastName.on("change keyup", validateForm);
      }

      function getUserEmail() {
        return $.trim($userEmail.val());
      }

      function getCRPAcronym() {
        return $.trim($selectedGlobalUnitID.val());
      }

      function findUserEmail() {
        var email = getUserEmail();
        if(validateEmail(email)) {
          $.ajax({
              url: baseUrl + "/crpByEmail.do",
              data: {
                userEmail: email
              },
              beforeSend: function() {
                $message.hide();
                userHasAccess = false;
              },
              success: function(data) {
                console.log(data);
                if(data.user == null) {
                } else {
                  $.each(data.crps, function(i,crp) {
                    if(crp.acronym == getCRPAcronym()) {
                      $message.text(data.user.name + " has already access to " + getCRPAcronym() + "").fadeIn();
                      userHasAccess = true;
                    }
                  });
                }
              },
              complete: function(data) {
                validateForm();
              },
              error: function(data) {
              }
          });
        } else {
          validateForm();
        }

      }

      function validateForm() {
        var email = getUserEmail();
        var firstName = $.trim($firstName.val());
        var lastName = $.trim($lastName.val());
        var isValid = false;

        $userEmail.removeClass('input-loading');

        if(!userHasAccess) {
          if(validateCGIAR()) {
            isValid = true;
          } else {
            if(validateEmail(email) && firstName && lastName) {
              isValid = true;
            }
          }
        }

        console.log("Validate Form", isValid);

        disabledSubmitButton(!isValid);
      }

      function disabledSubmitButton(state) {
        $submitButton.prop("disabled", state);
        if(state) {
          $submitButton.addClass("disabled");
        } else {
          $submitButton.removeClass("disabled");
        }
      }

      function validateCGIAR() {
        var email = getUserEmail();
        if(validateEmail(email) && email.indexOf("@cgiar.org") !== -1) {
          $userNameBlock.slideUp();
          return true;
        } else {
          $userNameBlock.slideDown();
          return false;
        }
      }

      function validateEmail(email) {
        var re =
            /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return re.test(email);
      }

      return {
        init: init
      }
    })();