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
// Complete name of the user being authenticated (users.first_name + users.last_name, as returned by
// crpByEmail.do). Empty when the record has no name; the password step then echoes only what was typed
var userDisplayName = "";
// True while validateUser.do is in flight. Guards the step navigation ("Go back") and the button
// state, so an attempt that is still running can't be navigated away from or submitted twice
var isSubmitting = false;

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

  // Keep the submit button disabled while the email/username field is empty, so step 1 can't be
  // submitted with nothing typed. "input" (not "change") so it reacts on every keystroke and on paste
  username.on("input", function() {
    updateNextButtonState();
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
      // Clean bottom red line in input
      $('input.login-input').removeClass("wrongData");
      var email = $.trim(username.val());

      // Two-stage validation, so a malformed value is caught here instead of being reported as a
      // missing database record. The field accepts an email OR a username (crpByEmail.do falls back
      // to getUserByUsername), so the email format is only enforced when the value looks like an
      // email attempt: without "@" it is a username and is left for the server to resolve
      if(email == "") {
        wrongData("emailRequired");
      } else if(email.indexOf("@") > -1 && !looksLikeEmail(email)) {
        wrongData("invalidEmail");
      } else {
        // Only remember a value that passed validation
        setCookie("username.email", email, cookieTime);
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

  // Submitting on "Enter" is keyup-based, so it only belongs to an input that also received the
  // matching keydown. A <button> activated with Enter dispatches its click on keydown, and the
  // "Go back" handler moves the focus into an input while going back - the trailing keyup then
  // landed on that freshly focused input and immediately submitted the step the user had just
  // left. Pairing the two events keeps a keyup that started somewhere else from counting here
  $(".loginForm .login-input").on('keydown', function(event) {
    if(event.keyCode === 13) {
      $(this).data("enterStartedHere", true);
    }
  });

  // A key held down while the focus moves away never delivers its keyup here, so drop the flag
  // rather than leaving it armed for an unrelated keyup later on
  $(".loginForm .login-input").on('blur', function() {
    $(this).removeData("enterStartedHere");
  });

  // Accessible "Enter" (keyCode==13) to login
  $(".loginForm .login-input").keyup(function(event) {
    if(event.keyCode === 13) {
      if(!$(this).data("enterStartedHere")) {
        return;
      }
      $(this).removeData("enterStartedHere");

      // The button is disabled while step 1 is empty, and a disabled button is not guaranteed to
      // dispatch a click, so raise the validation message here instead of relying on one
      if(currentStep == "email" && $.trim(username.val()) == "") {
        wrongData("emailRequired");
        return;
      }

      if(isSubmitting) {
        return;
      }

      $("input#login_next").click();
    }
  });

  // Accessible "Enter" (keyCode==13) to select crp,center or platform
  $(".selection-bar-options ul li").keyup(function(event) {
    if(event.keyCode === 13) {
      $(this).click();
    }
  });

  // Go back to the previous step when the "Go back" button is activated. It is a native <button>,
  // so Enter and Space raise this same click event and no extra key handler is needed
  $('.login-back-container .loginBack').on('click', function() {
    // Ignore clicks while a login attempt is still running: navigating away mid-request left the
    // form on another step while the pending response was still about to repaint the button
    if(isSubmitting) {
      return;
    }

    if(currentStep == "password" && crpSession == "" && availableCrpsCount > 1) {
      showProjectStep();
    } else {
      showEmailStep();
    }
  });

  // Reflect the initial state of the email field (it may be prefilled from the cookie) on the button
  updateNextButtonState();

}

// Step 1: Email
function showEmailStep() {
  // refresh variables
  currentStep = "email";
  hasAccess = false;
  availableCrpsCount = 0;
  userDisplayName = "";

  cleanWrongData();

  // Reset input password
  $(".loginForm #login-password .user-password").val("");

  // Hide the big crp image and the project/password steps
  $(".crps-select, .project-skeleton, .loginForm .form-group").addClass("hidden");
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

  // Hide every card and re-show all the groups, so a lookup for a different email
  // starts from a clean list instead of keeping the previous user's options
  $('.selection-bar-options ul li').addClass("hidden");
  $('.crps-select .selection-bar-options').removeClass("hidden");

  // Change height value according to the first step
  $("#loginFormContainer .loginForm").removeClass("max-size");

  // Show email step
  $("#login-step-email").removeClass("hidden");

  // Change button value to Next
  $("input#login_next").val("Log in");

  // Drop any spinner/disabled state left behind by a failed attempt, then re-evaluate the button
  // against the (possibly empty) email field
  clearLoginButtonLoading();
  updateNextButtonState();

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

  clearLoginButtonLoading();
  updateNextButtonState();
}

// Step 3: Password
function showPasswordStep() {
  currentStep = "password";

  cleanWrongData();

  // Show terms and conditions checkbox
  showTermsCheckbox();

  // Echo who is logging in: the complete name when the user record has one, and always what was
  // typed in step 1 - between parentheses when it accompanies the name, on its own otherwise
  if(userDisplayName != "") {
    $(".login-echoed-name").text(userDisplayName);
    $(".login-echoed-username").text(" (" + username.val() + ")");
  } else {
    $(".login-echoed-name").text("");
    $(".login-echoed-username").text(username.val());
  }

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

  clearLoginButtonLoading();
  updateNextButtonState();

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
          // POST so the account identifier is not left in the URL (proxy logs, browser history, Referer)
          type: "POST",
          data: {
            userEmail: email
          },
          beforeSend: function() {
            // Disable the button while the project cards skeleton is shown on step 2
            $("input#login_next").attr("disabled", true);
          },
          success: function(data) {
            // Same guard as in checkPassword: an HTML error page answered with a 200 must not be
            // reported to the user as "account not found"
            if(data == null || data.crps == null) {
              showEmailStep();
              wrongData("serverError");
              return;
            }

            // If the user doesn't exists show a predefined message and reset the button value to (next).
            // An empty crps list is treated the same way: the code below indexes data.crps[0], and
            // throwing there would strand the user on the loading skeleton
            if(data.user == null || data.crps.length == 0) {
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

                // Reveal the card itself. Every Global Unit with login=true is rendered in the DOM,
                // so the ones this user is not assigned to must stay hidden, otherwise they show up
                // as empty bordered cards next to the real options
                $('.selection-bar-options ul #crp-' + data.crps[i].acronym).removeClass("hidden");

                // Always show the logo, no matter how many options the user has. The acronym element
                // is kept in the markup (hidden) as a fallback we may want to bring back
                // Additionally set tabindex to make crp change accessible by keyboard
                $('.selection-bar-options ul #crp-' + data.crps[i].acronym + ' .selection-bar-image').removeClass(
                    "hidden");
                $('.selection-bar-options ul #crp-' + data.crps[i].acronym + ' .selection-bar-image').attr('tabindex',
                    '0');

                // If user has a crp cookie, click it
                if(crpCookie == data.crps[i].acronym) {
                  $('.selection-bar-options ul #crp-' + data.crps[i].acronym).click();
                }
              });

              // Collapse type groups that ended up without any visible card, so their
              // separator and spacing don't leave a gap in the list
              $(".crps-select .selection-bar-options").each(function() {
                if($(this).find("ul li:not(.hidden)").length === 0) {
                  $(this).addClass("hidden");
                } else {
                  $(this).removeClass("hidden");
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
            updateNextButtonState();
          },
          error: function(data) {
            showEmailStep();
            wrongData("serverError");
          }
      });
}

// Decide which step to show next (project selection or password), reusing the same
// hasAccess / crpSession / data.crps checks the app already relied on
function secondForm(data) {
  cleanWrongData();

  // Keep the user's complete name to show it on the password step
  userDisplayName = sanitizeUserName(data.user.name);

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
          // POST so the credentials are not left in the URL (proxy logs, browser history, Referer)
          type: "POST",
          data: {
              userEmail: email,
              userPassword: password,
              agree: $('input#terms').is(':checked')
          },
          beforeSend: function() {
            // Lock the button for the whole request. It used to be locked only when the terms
            // checkbox was checked, which let a second attempt be fired on top of the first one
            isSubmitting = true;
            $("input#login_next").addClass("is-loading");
            $("input#login_next").attr("disabled", true);
            $(".login-button-spinner").removeClass("hidden");
            // Grey out the step navigation for the duration of the request, and disable the button
            // so it also leaves the tab order instead of being a focusable no-op
            $('.login-back-container').addClass("is-busy");
            $('.login-back-container .loginBack').attr("disabled", true);
          },
          success: function(data) {
            // A proxy or the container can answer 200 with an HTML body instead of the expected JSON.
            // Treat any unexpected payload as a server error instead of throwing inside this callback,
            // which is the other way the button used to end up stuck with its spinner
            if(data == null || data.userFound == null) {
              clearLoginButtonLoading();
              updateNextButtonState();
              wrongData("serverError");
              return;
            }

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

              // Hide the loading spinner. Runs after the recaptcha block above, so that
              // updateNextButtonState() sees the incremented counter and keeps the button locked
              clearLoginButtonLoading();
              updateNextButtonState();
            } else {
              // Keep the spinner and the lock while the real form submits and the page navigates away
              $("input#login_formSubmit").click();
            }
          },
          complete: function(data) {
          },
          error: function(data) {
            // Release the button before rendering the message, so a failure inside wrongData() can
            // never again leave the spinner running (this is what the 502 on AD accounts exposed)
            clearLoginButtonLoading();
            updateNextButtonState();
            wrongData("serverError");
          }
      });
}

// Show error message and bottom red line in input
// {type} is the CSS class of one of the <p class="invalidField ..."> elements in loginForm.ftl
// {customMessage} optionally replaces that element's default (i18n) text
function wrongData(type,customMessage) {
  // Only ever show one message: submitting with "Enter" keeps the focus on the input, so its "change"
  // event never fires and a message from the previous attempt used to stay on screen and stack
  cleanWrongData();

  // bottom red line in input
  $('input.login-input').addClass("wrongData");

  // {type} must be a bare CSS class. Passing a whole sentence built an invalid selector
  // (".loginForm p.invalidField.An error has ocurred...") and made jQuery throw, which aborted the
  // rest of the caller - leaving the button stuck with its spinner. Fall back to the generic slot
  // Guard against a non-string too: null coerces to "null", which would select nothing and leave the
  // user with no message at all
  var $invalidField;
  if(typeof type == "string" && /^[A-Za-z][\w-]*$/.test(type)) {
    $invalidField = $('.loginForm p.invalidField.' + type);
  } else {
    $invalidField = $('.loginForm p.invalidField.serverError');
    customMessage = (customMessage != null) ? customMessage : type;
  }

  if(customMessage != null) {
    $invalidField.text(customMessage);
  }
  $invalidField.removeClass("hidden");

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
            "author_name": userDisplayName,
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

// getComposedCompleteName() on the server concatenates first_name and last_name without validating
// them, so an incomplete record arrives here as "null null" or "Kenji null". Drop those tokens and
// return "" when nothing usable is left
function sanitizeUserName(name) {
  if(name == null) {
    return "";
  }

  var parts = String(name).split(/\s+/);
  var cleaned = [];

  $.each(parts, function(i, part) {
    if(part != "" && part.toLowerCase() != "null") {
      cleaned.push(part);
    }
  });

  return cleaned.join(" ");
}

// Hide wrong data line and message in email and password inputs
function cleanWrongData() {
  // Hide input bottom red line
  $('input.login-input').removeClass("wrongData");
  // Hide error message
  $('.loginForm p.invalidField').addClass("hidden");
}

// Clear the in-flight visual state of the submit button. Called on every step change and whenever a
// request settles, so a failed or abandoned attempt can never leave the button spinning forever.
// Deliberately does not touch "disabled" - that belongs to updateNextButtonState()
function clearLoginButtonLoading() {
  isSubmitting = false;
  $("input#login_next").removeClass("is-loading");
  $(".login-button-spinner").addClass("hidden");
  $("input#login_next").val("Log in");
  $('.login-back-container').removeClass("is-busy");
  $('.login-back-container .loginBack').attr("disabled", false);
}

// Permissive check for "looks like a usable email address": a single "@", something before it, and a
// dotted domain after it. Deliberately laxer than isEmail() in utils.js, which caps the top level
// domain at 4 characters and would reject valid addresses such as name@example.africa. Its only job
// is to catch an obviously malformed address ("name@", "name@domain") before the database lookup
function looksLikeEmail(value) {
  return /^[^\s@]+@[^\s@.]+(\.[^\s@.]+)+$/.test(value);
}

// The submit button is shared by the 3 steps: on the email step it stays disabled until something is
// typed, on the other steps it is always available
function updateNextButtonState() {
  // A request is still running, or the recaptcha is on screen and owns the button until its callback
  if(isSubmitting || incorrectPasswordCount >= 3) {
    return;
  }

  if(currentStep == "email") {
    $("input#login_next").attr("disabled", $.trim(username.val()) == "");
  } else {
    $("input#login_next").attr("disabled", false);
  }
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