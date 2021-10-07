$(document).ready(init);

// The email input view is equal to isSecondForm=false
// The password input view is equal to isSecondForm=true
var cookieTime, isSecondForm = false, hasAccess = false;
var username = $("input[name='user.email']");
var inputPassword = $("input[name='user.password']");
var crpSession = "";

function init() {
  initJreject();

  cookieTime = 100;

  setCrpSession();

  var input = $('.form-control');

  // Bottom animated line on input focus
  input.on('focus', function(ev) {
    input.parent().addClass('is-focused');
  });

  // Hide bottom animated line when input loses focus
  input.on('blur', function(ev) {
    input.parent().removeClass('is-focused');
  });

  // Verify user email session cookie
  if(verifyCookie("username.email")) {
    username.val(getCookie("username.email"));
  } else {
    username.val(getCookie(""));
  }

  // On select a CRP, Center or Platform load image in login and set crp cookie
  $('.selection-bar-options ul li.enabled').on('click', function() {
    var selectedImageAcronym = $(this).attr('id').split('-')[1];
    loadSelectedImage(selectedImageAcronym);

    // Hidden input that contains the selected crp id
    $("input#crp-input").val(selectedImageAcronym);
    setCRPCookie();
  });

  // Hide wrong data line and message in email and password inputs
  $('input.login-input').on("change", function(e) {
    cleanWrongData();
  });

  // Set focus on email input on page load
  $(".loginForm #login-email .user-email").focus();

  // When user selects a crp, change active class to the selected crp
  $(".selection-bar-options ul li").on('click', function() {
    $(".selection-bar-options ul li").removeClass('active');
    $(this).addClass('active');
  });

  //show password
  $('.icon-show-password').click(function () {
    var inputPass = document.getElementById("user.password");
    var icon = document.querySelector(".icon-show-password");
    if (inputPass.type === "password") {
      inputPass.type = "text";
      icon.classList.remove("glyphicon-eye-close");
      icon.classList.add("glyphicon-eye-open");
    } else {
      inputPass.type = "password";
      icon.classList.remove("glyphicon-eye-open");
      icon.classList.add("glyphicon-eye-close");
    }
  });

  // Next Button
  $("input#login_next").on('click', function(e) {
    e.preventDefault();

    // Save the email in cookies
    setCookie("username.email", username.val(), cookieTime);
    // Clean bottom red line in input
    $('input.login-input').removeClass("wrongData");
    var email = username.val();
    /* || !isEmail(email) if you want to check if isEmail */
    if(email == "") {
      wrongData("invalidEmail");
    } else if(!isSecondForm) {
      loadAvailableItems(email);
    } else if(inputPassword.val() == "") {
      wrongData("voidPassword");
    } else {
      checkPassword(email, inputPassword.val());
    }
  });

  // Control page scroll when user is scrolling in the crps select bar
  $(".crps-select").on('mouseover', function() {
    $('html, body').disableScroll();
  });

  $(".crps-select").on('mouseleave', function() {
    $('html, body').enableScroll();
  });

  // Accessible "Enter" (keyCode==13) to login
  $(".loginForm .login-input").keyup(function(event) {
    if(event.keyCode === 13) {
      $("input#login_next").click();
    }
  });

  // Accessible "Enter" (keyCode==13) to select crp,center or platform
  $(".selection-bar-options ul li").keyup(function(event) {
    if(event.keyCode === 13) {
      $(this).click();
    }
  });

  // Return to the first form (email input) when click on the user name
  $(".loginForm .login-input-container.username").on('click', function() {
    firstForm();
  });

  // Return to the first form (email input) when click on the bottom message in form
  $('.login-back-container p.loginBack').on('click', function() {
    $(".loginForm .login-input-container.username").click();
  });

}

// First form (email input)
function firstForm() {
  // refresh variables
  isSecondForm = false;
  hasAccess = false;

  cleanWrongData();

  // Reset input password
  $(".loginForm #login-password .user-password").val("");

  // Hide the big crp image, the welcome message, and the input password (jquery selectors in order)
  $(".crps-select, .loginForm .form-group," + " .loginForm .welcome-message-container, " + ".loginForm #login-password")
      .addClass("hidden");

  // Hide terms and conditions checkbox
  $('.terms-container').addClass("hidden");

  // Hide the "login with different user" button
  $('.login-back-container').addClass('hidden');

  // Hide the labels (CRPs,Centers and Platforms)
  $(".crps-select .name-type-container").addClass("hidden");

  // Hide the crps-centers-platforms images or acronyms in selection bar
  $('.selection-bar-options ul .selection-bar-image,' + '.selection-bar-options ul .selection-bar-acronym').addClass(
      "hidden");

  // Change height value according to the first form
  $("#loginFormContainer .loginForm").removeClass("max-size");

  // Show email input
  $(".loginForm #login-email").removeClass("hidden");

  // Change button value to Next
  $("input#login_next").val("Next");

}

// Returns true if the {nameCookie} exists
function verifyCookie(nameCookie) {
  if(getCookie(nameCookie) != "") {
    return true;
  } else {
    return false;
  }
}

// Get the value of CRP cookie if exists else returns false
function getCrpCookie() {
  // Verify "crp"
  if(verifyCookie("CRP") && (getCookie("CRP") != "undefined")) {
    var crpSelected = getCookie("CRP");
    return crpSelected;
  }
  return false;
}

function setCRPCookie() {
  var crpInput = $('input#crp-input').val();
  // Create crp cookie
  setCookie("CRP", crpInput, cookieTime);
}

// Find if the url contains a crp/center/platform name, to set a crpSession
// crpSession is when has a preselected crp (and requested page is 401.ftl)
function setCrpSession() {
  // get all crps/centers/platforms available
  var availableList = [];
  var listItems = $('.crps-select .selection-bar-options ul li');
  $.each(listItems, function(i) {
    availableList.push(listItems[i].id.split('-')[1]);
  });
  // get url split by '/' and compare if any item of the available list match with any of the path
  var path = window.location.pathname.split("/");
  $.each(path, function(i) {
    $.each(availableList, function(j) {
      if(path[i] == availableList[j]) {
        crpSession = path[i];
      }
    });
  });
}

// With user email or username gets his name, if previously was accepted terms and conditions and his available list of
// crps
function loadAvailableItems(email) {
  $
      .ajax({
          url: baseUrl + "/crpByEmail.do",
          data: {
            userEmail: email
          },
          beforeSend: function() {
            // Add the animated gif in button and remove the next text
            $("input#login_next").addClass("login-loadingBlock");
            $("input#login_next").attr("disabled", true);
            $("input#login_next").val("");
          },
          success: function(data) {
            // If the user doesn't exists show a predefined message and reset the button value to (next)
            if(data.user == null) {
              wrongData("emailNotFound");
              $("input#login_next").val("Next");
            } else {
              var crpCookie = getCrpCookie();

              // Select the first crp/center/platform available by default
              $('.selection-bar-options ul #crp-' + data.crps[0].acronym).click();

              // Do for each available crp
              $.each(data.crps, function(i) {
                // If has crpSession, so is a redirect link (401.ftl) and if match with any of available crp,
                // the user has access to that crp
                if(crpSession != "" && crpSession == data.crps[i].acronym) {
                  hasAccess = true;
                }

                // Show the title of the crp type (i.e. for CCAFS, type is equals to "CRP" or for BigData, type is
                // equals to
                // "Platform")
                // in the select bar
                $(".crps-select .name-type-container.type-" + data.crps[i].idType).removeClass("hidden");

                // If the user has access to less than 7 crps, show images in select bar, if doesn't, show acronyms
                // boxes
                // Additionally set tabindex to make crp change accessible by keyboard
                if(data.crps.length < 7) {
                  $('.selection-bar-options ul #crp-' + data.crps[i].acronym + ' .selection-bar-image').removeClass(
                      "hidden");
                  $('.selection-bar-options ul #crp-' + data.crps[i].acronym + ' .selection-bar-image').attr(
                      'tabindex', '0');
                } else {
                  $('.selection-bar-options ul #crp-' + data.crps[i].acronym + ' .selection-bar-acronym').removeClass(
                      "hidden");
                  $('.selection-bar-options ul #crp-' + data.crps[i].acronym + ' .selection-bar-acronym').attr(
                      'tabindex', '0');
                }

                // If user has a crp cookie, click it
                if(crpCookie == data.crps[i].acronym) {
                  $('.selection-bar-options ul #crp-' + data.crps[i].acronym).click();
                }
              });

              // If the user previously accepted the terms and conditions, check the box by default
              if(data.user.agree) {
                $('input#terms').attr('checked', true);
              } else {
                $('input#terms').attr('checked', false);
              }

              // If user has access to the crpSession or crpSession is void, change to secondForm, if doesn't denied
              // access
              if(hasAccess || crpSession == "") {
                secondForm(data);
              } else {
                wrongData("deniedAccess");
                $("input#login_next").val("Next");
              }
            }
          },
          complete: function(data) {
            $("input#login_next").removeClass("login-loadingBlock");
            $("input#login_next").attr("disabled", false);
          },
          error: function(data) {
            wrongData("An error has ocurred. Please try again or contact with the MARLO Support team (MARLOSupport@cgiar.org)");
            $("input#login_next").removeClass("login-loadingBlock");
            $("input#login_next").attr("disabled", false);
          }
      });
}

// Second Form (password input)
function secondForm(data) {
  // Submit Button control, just send the form when is in the second form
  isSecondForm = true;

  // Show terms and conditions checkbox
  showTermsCheckbox();

  cleanWrongData();

  // Show user name in form
  $(".welcome-message-container .username span").text(data.user.name);

  // Change height value to secondForm
  $("#loginFormContainer .loginForm:not(.instructions)").addClass("max-size");

  // Hide email input
  $(".loginForm #login-email").addClass("hidden");

  // Show crp image, welcome message and input password
  $(".loginForm .form-group, " + ".loginForm .welcome-message-container, " + ".loginForm #login-password").removeClass(
      "hidden");

  // Change button value to Login
  $("input#login_next").val("Login");

  // Set focus on password input
  $(".loginForm #login-password input").focus();

  // Show the back to email button
  $('.login-back-container').removeClass('hidden');

  // If has a crpSession validate if user has access, if doesn't click the crpSession option
  // If hasn't crpSession show the side select bar
  if(crpSession != '') {

    if(!hasAccess) {
      wrongData("deniedAccess");
    } else {
      $('.selection-bar-options ul #crp-' + crpSession).click();
    }

  } else {

    // When user has access to multiple crps, show the side bar
    if(data.crps.length > 1) {
      $(".crps-select").removeClass("hidden");
      // Move crps select side bar to left
      $(".crps-select").addClass('show-select-bar');
    } else {
      // Click on the unique loaded crp
      $('.selection-bar-options ul #crp-' + data.crps[0].acronym).click();
    }
  }
}

// Validate login success
function checkPassword(email, password) {
  $.ajax({
    url: baseUrl + "/validateUser.do",
    type:"POST",
    data: {
      userEmail: email,
      userPassword: password,
      agree: $('input#terms').is(':checked')
    },
    beforeSend: function () {
      // If terms and conditions is checked, show loading gif
      if ($('input#terms').is(':checked')) {
        $("input#login_next").addClass("login-loadingBlock");
        $("input#login_next").attr("disabled", true);
        $("input#login_next").val("");
      }
    },
    success: function (data) {
      // If login success is false show the error message, if doesn't send form
      if (!data.userFound.loginSuccess) {
        if (data.messageEror == "Invalid CGIAR email or password, please try again") {
          wrongData("incorrectPassword");
        } else {
          wrongData("incorrectPassword", data.messageEror);
        }

        // Hide the loading gif
        $("input#login_next").removeClass("login-loadingBlock");
        $("input#login_next").attr("disabled", false);
        $("input#login_next").val("Login");
      } else {
        $("input#login_formSubmit").click();
      }
    },
    complete: function (data) {
    },
    error: function (data) {
      wrongData("An error has ocurred. Please try again or contact with the MARLO Support team (MARLOSupport@cgiar.org)");
    }
  });
}

// Show error message and bottom red line in input
// if has a custom message show them, but if is a default type (i.e. incorrectPassword, etc.), show them
function wrongData(type,customMessage) {
  // bottom red line in input
  $('input.login-input').addClass("wrongData");
  $invalidField = $('.loginForm p.invalidField.' + type);
  if(customMessage != null) {
    $invalidField.text(customMessage);
    $invalidField.removeClass("hidden");
  } else {
    $invalidField.removeClass("hidden");
  }

  // Set focus on the wrong field
  if(type == "voidPassword" || type == "incorrectPassword") {
    inputPassword.focus();
  } else {
    username.focus();
  }

  var slackMessage = {
      "text": "MARLO Login Notification",
      "attachments": [
        {
            "color": "#e74c3c",
            "author_name": $('.login-input-container.username span').text(),
            "text": $invalidField.text(),
            "fields": [
                {
                    "title": "CGIAR Entity",
                    "value": $('input#crp-input').val(),
                    "short": true
                }, {
                    "title": "Username/Email",
                    "value": $('input.user-email').val(),
                    "short": true
                }
            ],
            "footer": window.location.href,
        }
      ]
  };
  postMessageToSlack(JSON.stringify(slackMessage));
}

// Hide wrong data line and message in email and password inputs
function cleanWrongData() {
  // Hide input bottom red line
  $('input.login-input').removeClass("wrongData");
  // Hide error message
  $('.loginForm p.invalidField').addClass("hidden");
}

// Show terms and conditions checkbox
function showTermsCheckbox() {
  $('.terms-container').removeClass("hidden");

}

// Show the image of the selected crp (big image)
function loadSelectedImage(selectedImageAcronym) {
  $("#crpSelectedImage").attr("src", baseUrl + "/global/images/crps/" + selectedImageAcronym + ".png");
}

function setCookie(cname,cvalue,mins) {
  var d = new Date();
  d.setTime(d.getTime() + (mins * 60 * 1000));
  var expires = "expires=" + d.toUTCString();
  document.cookie = cname + "=" + cvalue + "; " + expires;
}

function initJreject() {
  $.reject({
      reject: {
          msie: false,
          msie5: true,
          msie6: true,
          msie7: true, // Microsoft Internet Explorer
          firefox: false,
          firefox1: true,
          firefox2: true,
          firefox3: true, // Mozilla firefox
          opera: false,
          opera7: true,
          opera8: true,
          opera9: true, // Opera
          safari: false,
          safari2: true,
          safari3: true,
          safari4: true
      // Safari
      }, // Reject all renderers for demo

      closeCookie: true, // Set cookie to remmember close for this session
      display: [
          'firefox', 'chrome', 'opera', 'msie', 'safari'
      ]
  });
}

// "Disable and enable scroll on page" anonymous function, above an example of how to use them
$.fn.disableScroll = function() {
  window.oldScrollPos = $(window).scrollTop();

  $(window).on('scroll.scrolldisabler', function(event) {
    $(window).scrollTop(window.oldScrollPos);
    event.preventDefault();
  });
};

$.fn.enableScroll = function() {
  $(window).off('scroll.scrolldisabler');
};