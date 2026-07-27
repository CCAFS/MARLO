$(document).ready(init);

// currentStep is one of "email", "project" or "password", matching the 3 login screens
var cookieTime, currentStep = "email", hasAccess = false;
var username = $("input[name='user.email']");
var inputPassword = $("input[name='user.password']");
var crpSession = "";
var incorrectPasswordCount = 0;
// Number of crps/centers/platforms actually assigned to the user (data.crps.length from crpByEmail.do),
// NOT the count of <li> elements in the DOM (which always renders every Global Unit with login=true,
// regardless of the current user's access)
var availableCrpsCount = 0;

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

    // Mirror the selected project card (image/acronym) into the step 3 full-width card
    $('#login-selected-project-card').html($(this).html());
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

  // Next / Log in Button
  $("input#login_next").on('click', function(e) {
    e.preventDefault();

    if(currentStep == "email") {
      // Save the email in cookies
      setCookie("username.email", username.val(), cookieTime);
      // Clean bottom red line in input
      $('input.login-input').removeClass("wrongData");
      var email = username.val();
      /* || !isEmail(email) if you want to check if isEmail */
      if(email == "") {
        wrongData("invalidEmail");
      } else {
        showProjectSkeleton();
        loadAvailableItems(email);
      }
    } else if(currentStep == "project") {
      // A project is already selected by default (loadAvailableItems auto-selects one)
      showPasswordStep();
    } else if(inputPassword.val() == "") {
      wrongData("voidPassword");
    } else {
      checkPassword(username.val(), inputPassword.val());
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

  // Return to the email step when click on the user name
  $(".loginForm .login-input-container.username").on('click', function() {
    showEmailStep();
  });

  // Go back to the previous step when click on the "Go back" link
  $('.login-back-container p.loginBack').on('click', function() {
    if(currentStep == "password" && crpSession == "" && availableCrpsCount > 1) {
      showProjectStep();
    } else {
      showEmailStep();
    }
  });

}

// Step 1: Email
function showEmailStep() {
  // refresh variables
  currentStep = "email";
  hasAccess = false;
  availableCrpsCount = 0;

  cleanWrongData();

  // Reset input password
  $(".loginForm #login-password .user-password").val("");

  // Hide the big crp image, the welcome message, and the project/password steps
  $(".crps-select, .project-skeleton, .loginForm .form-group, .loginForm .welcome-message-container").addClass("hidden");
  $("#login-step-project, #login-step-password").addClass("hidden");

  // Hide terms and conditions checkbox
  $('.terms-container').addClass("hidden");

  // Hide the "go back" link
  $('.login-back-container').addClass('hidden');

  // Hide the labels (CRPs,Centers and Platforms)
  $(".crps-select .name-type-container").addClass("hidden");

  // Hide the crps-centers-platforms images or acronyms in selection bar
  $('.selection-bar-options ul .selection-bar-image,' + '.selection-bar-options ul .selection-bar-acronym').addClass(
      "hidden");

  // Change height value according to the first step
  $("#loginFormContainer .loginForm").removeClass("max-size");

  // Show email step
  $("#login-step-email").removeClass("hidden");

  // Change button value to Next
  $("input#login_next").val("Log in");

  // Set focus on email input
  $(".loginForm #login-email .user-email").focus();
}

// Show 2 placeholder cards in the project step while crpByEmail.do is loading,
// avoiding the jarring gray loading block that used to replace the button
function showProjectSkeleton() {
  cleanWrongData();

  $("#login-step-email, #login-step-password").addClass("hidden");
  $("#login-step-project").removeClass("hidden");

  $(".crps-select").addClass("hidden");
  $(".project-skeleton").removeClass("hidden");

  // Hide the "go back" link while the request is in flight
  $('.login-back-container').addClass('hidden');
  $('.terms-container').addClass("hidden");

  $("#loginFormContainer .loginForm").addClass("max-size");
}

// Step 2: Select project (CRP/Center/Platform)
function showProjectStep() {
  currentStep = "project";

  cleanWrongData();

  $("#login-step-email, #login-step-password").addClass("hidden");
  $("#login-step-project").removeClass("hidden");

  // Show the project card grid, hide the loading skeleton
  $(".project-skeleton").addClass("hidden");
  $(".crps-select").removeClass("hidden");

  // Show the "go back" link, hide terms checkbox (shown again on the password step)
  $('.login-back-container').removeClass('hidden');
  $('.terms-container').addClass("hidden");

  $("#loginFormContainer .loginForm").addClass("max-size");

  // Change button value to Login
  $("input#login_next").val("Log in");
}

// Step 3: Password
function showPasswordStep() {
  currentStep = "password";

  cleanWrongData();

  // Show terms and conditions checkbox
  showTermsCheckbox();

  // Echo the email entered in step 1
  $(".login-echoed-email").text(username.val());

  // Mirror the actually selected project card (image/acronym), in case it was
  // auto-selected before its "hidden" class was cleared
  var $activeProjectCard = $('.selection-bar-options ul li.active');
  if($activeProjectCard.length) {
    $('#login-selected-project-card').html($activeProjectCard.html());
  }

  // Change height value to the password step
  $("#loginFormContainer .loginForm:not(.instructions)").addClass("max-size");

  $("#login-step-email, #login-step-project").addClass("hidden");
  $("#login-step-password").removeClass("hidden");

  // Change button value to Login
  $("input#login_next").val("Log in");

  // Set focus on password input
  $(".loginForm #login-password input").focus();

  // Show the "go back" link
  $('.login-back-container').removeClass('hidden');
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
            // Disable the button while the project cards skeleton is shown on step 2
            $("input#login_next").attr("disabled", true);
          },
          success: function(data) {
            // If the user doesn't exists show a predefined message and reset the button value to (next)
            if(data.user == null) {
              showEmailStep();
              wrongData("emailNotFound");
            } else {
              var crpCookie = getCrpCookie();

              // Track the real number of crps/centers/platforms assigned to this user,
              // used by the "Go back" handler to decide whether Step 2 makes sense to show
              availableCrpsCount = data.crps.length;

              //console.log(data.crps[0].acronym);

              if(data.crps[0].acronym == "AICCRA") {
                $('.selection-bar-options ul #crp-' + data.crps[0].acronym).click();
              }
              else {
                // Select the first crp/center/platform available by default
                $('.selection-bar-options ul #crp-' + data.crps[0].acronym + "_").click();
              }

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
                showEmailStep();
                wrongData("deniedAccess");
              }
            }
          },
          complete: function(data) {
            $("input#login_next").attr("disabled", false);
          },
          error: function(data) {
            showEmailStep();
            wrongData("An error has ocurred. Please try again or contact with the MARLO Support team (MARLOSupport@cgiar.org)");
          }
      });
}

// Decide which step to show next (project selection or password), reusing the same
// hasAccess / crpSession / data.crps checks the app already relied on
function secondForm(data) {
  cleanWrongData();

  // Show user name in form (kept for the hidden welcome-message-container)
  $(".welcome-message-container .username span").text(data.user.name);

  // If has a crpSession validate if user has access, if doesn't click the crpSession option
  // If hasn't crpSession and user has multiple projects, show the project selection step
  if(crpSession != '') {

    if(!hasAccess) {
      showPasswordStep();
      wrongData("deniedAccess");
    } else {
      $('.selection-bar-options ul #crp-' + crpSession).click();
      showPasswordStep();
    }

  } else {

    // When user has access to multiple crps, show the project selection step
    if(data.crps.length > 1) {
      showProjectStep();
    } else {
      // Click on the unique loaded crp and go straight to the password step
      $('.selection-bar-options ul #crp-' + data.crps[0].acronym).click();
      showPasswordStep();
    }
  }
}

// Validate login success
function checkPassword(email,password) {
  $
      .ajax({
          url: baseUrl + "/validateUser.do",
          data: {
              userEmail: email,
              userPassword: password,
              agree: $('input#terms').is(':checked')
          },
          beforeSend: function() {
            // If terms and conditions is checked, show a small spinner over the button
            if($('input#terms').is(':checked')) {
              $("input#login_next").addClass("is-loading");
              $("input#login_next").attr("disabled", true);
              $(".login-button-spinner").removeClass("hidden");
            }
          },
          success: function(data) {
            // If login success is false show the error message, if doesn't send form
            if(!data.userFound.loginSuccess) {
              if(data.messageEror == "Invalid CGIAR email or password, please try again") {
                wrongData("incorrectPassword");
                // Code for the recaptcha to appear after 3 password attempts
                incorrectPasswordCount++;
                if (incorrectPasswordCount == 3) {
                  var loginButton = document.getElementById('login_next');
                  $('#recaptcha-container').css("margin-top", "40px")
                  $('.login-form-button').css("margin-top", "-15px")
                  $('#loginFormContainer .loginForm.max-size').css("height", "500px")
                  grecaptcha.render('recaptcha-container', {
                    'sitekey': RECAPTCHAT_SITE_KEY,
                    'callback': function() {
                      loginButton.disabled = false;
                    }
                  });
                  
                }
              } else {
                wrongData("incorrectPassword", data.messageEror);
              }

              // Hide the loading spinner
              $("input#login_next").removeClass("is-loading");
              $(".login-button-spinner").addClass("hidden");
              if (incorrectPasswordCount != 3){
                $("input#login_next").attr("disabled", false);
              }
              $("input#login_next").val("Log in");
            } else {
              $("input#login_formSubmit").click();
            }
          },
          complete: function(data) {
          },
          error: function(data) {
            wrongData("An error has ocurred. Please try again or contact with the MARLO Support team (MARLOSupport@cgiar.org)");
            $("input#login_next").removeClass("is-loading");
            $(".login-button-spinner").addClass("hidden");
            $("input#login_next").attr("disabled", false);
            $("input#login_next").val("Log in");
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
  $("#crpSelectedImage").attr("src", baseUrl + "/data/globalUnitLogo.do?acronym=" + encodeURIComponent(selectedImageAcronym));
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