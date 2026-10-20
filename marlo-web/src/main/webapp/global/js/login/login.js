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
// CHG-COGNITO-AUTH-001-T12: true when crpByEmail.do reported user.isCgiarUser for the account
// currently in step 1/2 (set in secondForm()). Combined with cognitoEnabled (below) to compose
// `mode = user.isCgiarUser && card.cognitoEnabled` once step 3 is reached (FN-001, design.md 5.2)
var isCgiarUser = false;
// Per-unit Cognito flag (T10) of the Global Unit card currently selected. Never a single scalar
// on the user: a mixed-membership account can see a different value per card (MIG-001). Set on
// card selection - the click handler below - which also fires from the auto-skip path in
// secondForm() when crps.length == 1
var cognitoEnabled = false;
// Numeric id (not acronym) of the selected Global Unit card, read from its data-global-unit-id
// attribute (written in loadAvailableItems). Needed both by the Cognito redirect
// (cognitoLogin.do requires a Long globalUnitId) and by PS-20's validateUser.do post - the
// acronym #crp-input already carries is not what either endpoint expects
var selectedGlobalUnitId = null;
// CHG-COGNITO-AUTH-001-T12 fix (audit Issue 1): a clone of #login-password, captured in init()
// AFTER the eye-icon toggle is bound below so the clone carries that handler too. The COGNITO
// branch of showPasswordStep() .remove()s the real #login-password from the document; this
// template is what restores it if the same page later needs the LOCAL branch again. Never
// inserted directly - always re-cloned, so it can restore the field more than once per page load
var passwordFieldTemplate;
// CHG-OTP-LOGIN-001-T14: per-unit "Email me a sign-in code" flag (crpByEmail.do otpEnabled) of the Global Unit
// card currently selected. False unless the server said exactly true, so every flow below behaves as it did
// before this feature whenever the specificity is off
var otpEnabled = false;
// CHG-PWD-SWITCH-001-T06 / UI-001: whether the selected unit offers the password. Anything but the server saying
// exactly false reads as true, so a cached page against an older server (no attribute) keeps showing the password
var passwordEnabled = true;
// Incremented on every OTP request/verify call and on "use a different email". A response whose number is no
// longer the latest belongs to an attempt the user has left and is dropped untouched (UI-004)
var otpRequestSeq = 0;
var otpRequestInFlight = false, otpVerifyInFlight = false;
// True once verification succeeded and the redirect has started: any later verify answer is ignored
var otpRedirecting = false;
var otpCooldownTimer = null, otpCooldownLeft = 0;
var OTP_COOLDOWN_SECONDS = 30;
// CHG-OTP-LOGIN-001-T17 / UI-006: what step 3 shows on the LOCAL branch. "direct" is today's password field with
// "Log in" (the code is not offered); "choice", "code" and "password" only exist when otpEnabled === true
var methodView = "direct";
var OTP_REQUEST_TIMEOUT_MS = 20000;

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

    // CHG-COGNITO-AUTH-001-T12/PS-20: capture this card's numeric Global Unit id and its
    // per-unit Cognito flag (both written as data-* attributes in loadAvailableItems), so
    // showPasswordStep() can compose `mode` and the Cognito control / validateUser.do post know
    // which Global Unit was actually selected
    selectedGlobalUnitId = $(this).attr('data-global-unit-id') || null;
    cognitoEnabled = $(this).attr('data-cognito-enabled') === 'true';
    otpEnabled = $(this).attr('data-otp-enabled') === 'true';
    passwordEnabled = $(this).attr('data-password-enabled') !== 'false';

    // Mirror the selected project card (image/acronym) into the step 3 full-width card(s) -
    // .selected-project-card matches the mirror in both the password step and the cgiar step
    $('.selected-project-card').html($(this).html());
  });

  // Hide wrong data line and message in email and password inputs
  $('input.login-input').on("change", function(e) {
    cleanWrongData();
  });

  // BUG-LOGIN-TERMS-001-FN-006: #terms is not a .login-input, so the binding above never reaches it.
  // Found by walking the real flow: the user ticks the box -- correcting the exact thing the message
  // asks for -- and the message stays on screen, because nothing clears it until the next attempt
  // repaints it. Scoped deliberately: only when the box becomes CHECKED, and only while the terms
  // message is the one showing, so toggling the checkbox cannot wipe an unrelated message such as
  // incorrectPassword
  $('input#terms').on("change", function() {
    if($(this).is(':checked') && !$('.loginForm p.invalidField.termsRequired').hasClass("hidden")) {
      cleanWrongData();
    }
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
    } else if(!termsAccepted()) {
      // BUG-LOGIN-TERMS-001-FN-001: deliberately BEFORE checkPassword(). That function's beforeSend takes
      // the button lock, and the terms refusal used to arrive only after validateUser.do had answered - at
      // which point the lock was held and the success branch had nothing left to release it. Reported
      // after voidPassword so an empty password still names itself first, the behaviour that ships today
      return;
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

      // CHG-OTP-LOGIN-001-T17 / UI-006: Enter never submits a method that is not open
      if(methodNotPassword()) {
        return;
      }

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

  // CHG-COGNITO-AUTH-001-T12 fix (audit finding 6): captured here, not right after the eye-icon
  // binding above. jQuery's .clone(true) copies only handlers already bound to the source node at
  // clone time, and #login-password's .user-password input is also targeted (via .login-input) by
  // the keydown/blur/keyup bindings just above this line - capturing before them left every
  // restored field missing the "Accessible Enter to login" relay. Benign today only because native
  // implicit form submission still clicks input#login_next on Enter; a later edit relying on the
  // keyup handler itself would silently break, but only after a COGNITO visit.
  // INVARIANT: this capture must stay below every binding that targets .form-control, .login-input
  // or .icon-show-password - moving it back above any of them reopens this gap.
  passwordFieldTemplate = $("#login-password").clone(true);

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
    if(isSubmitting || otpRequestInFlight || otpVerifyInFlight) {
      return;
    }

    if(currentStep == "password" && crpSession == "" && availableCrpsCount > 1) {
      showProjectStep();
    } else {
      showEmailStep();
    }
  });

  // CHG-COGNITO-AUTH-001-T12: the CGIAR sign-in control (FN-001 COGNITO branch). A real
  // <button type="button">, so it is keyboard-reachable and activates on both Enter and Space for
  // free, and it never submits the login form - it navigates to cognitoLogin.do instead. The
  // redirect is always user-initiated (design.md 5.5): nothing here fires without this click.
  // CHG-OTP-LOGIN-001-T21 / UI-006: the same handler serves the "Sign in with CGIAR" control of the method choice,
  // which a CGIAR account on a Cognito unit sees next to "Email me a sign-in code"
  $('#login-cgiar-button, #login-method-cgiar-button').on('click', function() {
    // BUG-LOGIN-TERMS-001-FN-002: this control is type="button", so the HTML5 "required" that used to sit
    // on #terms never fired for it and the redirect carried &agree=false to a server that refuses it under
    // the generic "cognitoFailed" category (SEC-006) - a dead end the user could not diagnose, one page
    // load away from the step they were on. CognitoLoginAction's own guard STAYS (SEC-001): this endpoint
    // is unauthenticated, so the client check is an affordance for the user, never a control
    if(!termsAccepted()) {
      return;
    }

    window.location.href = baseUrl + "/cognitoLogin.do"
        + "?email=" + encodeURIComponent($.trim(username.val()))
        + "&globalUnitId=" + encodeURIComponent(selectedGlobalUnitId)
        + "&agree=" + $('input#terms').is(':checked');
  });

  // CHG-OTP-LOGIN-001-T14: the OTP controls. All of them are inert unless showPasswordStep() revealed the
  // request button, which only happens when the selected unit reported otpEnabled === true
  $('#login-otp-button').on('click', function() {
    requestOtpCode();
  });
  $('#login-otp-resend').on('click', function() {
    requestOtpCode();
  });
  $('#login-otp-verify').on('click', function() {
    verifyOtpCode();
  });
  // CHG-PWD-SWITCH-001-T06 / FN-010: the way back to step 1 from the "use your email address" view
  $('#login-otp-different, #login-method-username-different').on('click', function() {
    // A verified code is already redirecting: leaving now would strand the user on the email step
    if(otpRedirecting) {
      return;
    }
    showEmailStep();
  });
  // Digits only, six at most. Enter verifies here instead of falling through to the form's implicit submit,
  // whose first submit button is the (hidden) "Log in" one
  $('#login-otp-code').on('input', function() {
    var digits = this.value.replace(/\D/g, '').slice(0, 6);
    if(digits !== this.value) {
      this.value = digits;
    }
    $('#login-otp-error').addClass("hidden");
  }).on('keydown', function(event) {
    if(event.keyCode === 13) {
      event.preventDefault();
      verifyOtpCode();
    }
  });

  // CHG-OTP-LOGIN-001-T17 / UI-006: the method choice. While the choice or the code panel is open, Enter on a
  // control that is not a button (the terms checkbox is the one that can hold the focus) would run the form's
  // implicit submission and post login.do with an empty password, so it is stopped here. Buttons keep their own
  // Enter activation
  $('.loginForm').on('keydown', function(event) {
    if(event.keyCode === 13 && methodNotPassword() && !$(event.target).is('button, a')) {
      event.preventDefault();
    }
  });
  $('#login-method-code-button').on('click', function() {
    fillMethodInfo();
    showMethodView("code");
  });
  $('#login-method-password-button').on('click', function() {
    showMethodView("password");
  });
  $('#login-method-code-back, #login-method-password-back-button').on('click', function() {
    // The code request owns the panel while it is in flight
    if(otpRequestInFlight || otpRedirecting) {
      return;
    }
    cleanWrongData();
    showMethodView("choice");
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
  isCgiarUser = false;
  cognitoEnabled = false;
  passwordEnabled = true;
  selectedGlobalUnitId = null;
  resetOtpState();
  resetMethodView();

  cleanWrongData();

  // Reset input password (a no-op once the COGNITO branch of showPasswordStep() has .remove()'d
  // this field for the current attempt)
  $(".loginForm #login-password .user-password").val("");

  // Hide the big crp image and the project/password/cgiar steps
  $(".crps-select, .project-skeleton, .loginForm .form-group").addClass("hidden");
  $("#login-step-project, #login-step-password, #login-step-cgiar").addClass("hidden");

  // Restore the shared submit button and hide the Cognito-only control, in case the previous
  // attempt reached the COGNITO branch of showPasswordStep()
  $("#login-cgiar-button").addClass("hidden");
  $("input#login_next").removeClass("hidden");

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

  $("#login-step-email, #login-step-password, #login-step-cgiar").addClass("hidden");
  $("#login-step-project").removeClass("hidden");

  $(".crps-select").addClass("hidden");
  $(".project-skeleton").removeClass("hidden");

  // Hide the "go back" link while the request is in flight
  $('.login-back-container').addClass('hidden');
  $('.terms-container').addClass("hidden");

  $("#loginFormContainer .loginForm").addClass("max-size");

  // Restore the shared submit button and hide the Cognito-only control, exactly as
  // showEmailStep()/showProjectStep() do - unreachable today (this step only follows step 1,
  // which already resets both), kept for symmetry so a future caller of this function does not
  // reintroduce the gap those two functions were fixed to close
  $("input#login_next").removeClass("hidden");
  $("#login-cgiar-button").addClass("hidden");
}

// Step 2: Select project (CRP/Center/Platform)
function showProjectStep() {
  currentStep = "project";

  // CHG-OTP-LOGIN-001-T14: an OTP answer still in flight belongs to the step just left, so drop it (UI-004).
  // Both flags are false whenever OTP is off, so this changes nothing there
  otpRequestSeq++;
  otpRequestInFlight = false;
  otpVerifyInFlight = false;
  otpRefreshButtons();
  resetMethodView();

  cleanWrongData();

  $("#login-step-email, #login-step-password, #login-step-cgiar").addClass("hidden");
  $("#login-step-project").removeClass("hidden");

  // Show the project card grid, hide the loading skeleton
  $(".project-skeleton").addClass("hidden");
  $(".crps-select").removeClass("hidden");

  // Show the "go back" link, hide terms checkbox (shown again on the password/cgiar step)
  $('.login-back-container').removeClass('hidden');
  $('.terms-container').addClass("hidden");

  $("#loginFormContainer .loginForm").addClass("max-size");

  // Change button value to Login, and restore it in case the previous attempt reached the
  // COGNITO branch of showPasswordStep(), which hides it in favor of #login-cgiar-button
  $("input#login_next").val("Log in");
  $("input#login_next").removeClass("hidden");
  $("#login-cgiar-button").addClass("hidden");
  $("#login-otp-request-container").addClass("hidden");

  clearLoginButtonLoading();
  updateNextButtonState();
}

// Step 3: branches by mode = user.isCgiarUser && card.cognitoEnabled (FN-001, design.md 5.2).
// isCgiarUser is captured in secondForm() from crpByEmail.do's user.isCgiarUser; cognitoEnabled
// and selectedGlobalUnitId are captured on card selection (the click handler above), including
// the auto-skip path in secondForm() when crps.length == 1, which clicks the sole card before
// calling this function - so both values are already current by the time mode is composed below
function showPasswordStep() {
  // CHG-COGNITO-AUTH-001-T12 fix (audit Issue 2): mode must never default to LOCAL just because
  // no card was ever explicitly clicked. login.js's own auto-select selector for a non-AICCRA
  // first card carries a stray trailing underscore and matches no element (pre-existing, not
  // fixed here - see the report), and updateNextButtonState() does not require an active card
  // before enabling "Log in" on the project step - so a multi-unit CGIAR user could otherwise
  // reach here with selectedGlobalUnitId still null and silently compose LOCAL in front of a
  // possibly-migrated account. Re-derive from the actually active card first; if there is still
  // none, refuse to guess and send the user back to pick one.
  // CHG-COGNITO-AUTH-001-T12 fix (audit finding 5): ":not(.hidden)" is required, not decorative.
  // showEmailStep() hides every card but never strips ".active", and a re-lookup for a different
  // email leaves a PREVIOUS user's selected card ".active" and hidden at the same time. Without
  // this filter, that stale card would be silently adopted here (wrong Cognito flag, wrong
  // globalUnitId posted to validateUser.do) instead of correctly finding nothing and bouncing to
  // step 2. loadAvailableItems() only ever un-hides the CURRENT lookup's cards, so ":not(.hidden)"
  // is an exact discriminator for "belongs to this user's current session", not just "was once
  // clicked".
  // Scoped to crpSession == '': harmless defensive coding, kept for clarity even though
  // loadAvailableItems() only calls secondForm() when hasAccess || crpSession == "" (so inside
  // secondForm(), crpSession != '' already implies hasAccess === true, and the !hasAccess
  // branch that also calls showPasswordStep() is unreachable) - this gate is not load-bearing
  // justification for that path, just an explicit statement of the intended scope.
  if(selectedGlobalUnitId == null && crpSession == '') {
    var $activeCard = $('.selection-bar-options ul li.active:not(.hidden)');
    if($activeCard.length) {
      selectedGlobalUnitId = $activeCard.attr('data-global-unit-id') || null;
      cognitoEnabled = $activeCard.attr('data-cognito-enabled') === 'true';
      otpEnabled = $activeCard.attr('data-otp-enabled') === 'true';
      passwordEnabled = $activeCard.attr('data-password-enabled') !== 'false';
    }
  }

  if(selectedGlobalUnitId == null && crpSession == '') {
    showProjectStep();
    wrongData("selectProject");
    return;
  }

  currentStep = "password";

  cleanWrongData();

  // Show terms and conditions checkbox - shared by both branches below. design.md 5.2/5.4: the
  // CGIAR block carries the same terms checkbox as the password step, never a duplicate of it
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
  // auto-selected before its "hidden" class was cleared. .selected-project-card matches the
  // mirror in both the password step and the cgiar step, so this reaches whichever shows next
  var $activeProjectCard = $('.selection-bar-options ul li.active');
  if($activeProjectCard.length) {
    $('.selected-project-card').html($activeProjectCard.html());
  }

  // Change height value to the password step
  $("#loginFormContainer .loginForm:not(.instructions)").addClass("max-size");

  // Change button value to Login (applies to input#login_next regardless of branch; COGNITO
  // hides that button below, so the value is moot there but left set for a clean restore)
  $("input#login_next").val("Log in");

  var mode = (isCgiarUser && cognitoEnabled) ? "COGNITO" : "LOCAL";

  if(mode == "COGNITO" && otpEnabled !== true) {
    // FN-001: the password input must not be rendered, focusable, or present in the submitted
    // form. .remove() - not .hide(), not disabled - a hidden or disabled input still ships in
    // FormData; this is the exact defect Judgment Day found in revision 1 (tasks.md T12)
    $("#login-password").remove();

    $("#login-step-email, #login-step-project, #login-step-password").addClass("hidden");
    $("#login-step-cgiar").removeClass("hidden");

    // The shared "Log in" button submits a password this branch does not have; it is replaced by
    // the dedicated Cognito control, which navigates to cognitoLogin.do instead of submitting
    $("input#login_next").addClass("hidden");
    $("#login-cgiar-button").removeClass("hidden");
    $("#login-otp-request-container").addClass("hidden");
    // CHG-OTP-LOGIN-001-T17 / UI-006: Cognito alone is a single method, shown directly, with no choice
    resetMethodView();

    // CHG-COGNITO-AUTH-001-T12 fix (audit Issue 3): hiding input#login_next above can leave focus
    // on <body> when it held focus, so the Cognito control mirrors the LOCAL arm's own focus call
    // below rather than leaving the user to tab in from the top of the document
    $("#login-cgiar-button").focus();
  } else if(mode == "COGNITO") {
    // CHG-OTP-LOGIN-001-T21 / UI-006: a CGIAR account on a Cognito unit whose code sign-in is on for this email
    // (otpEnabled) chooses between "Sign in with CGIAR" and "Email me a sign-in code". The choice and the code panel
    // are the ones the LOCAL branch uses; the password is not one of the methods, so its input stays out of the
    // document exactly as on the single-method branch above (FN-001: not rendered, not focusable, not submitted)
    $("#login-password").remove();

    $("#login-step-email, #login-step-project, #login-step-cgiar").addClass("hidden");
    $("#login-step-password").removeClass("hidden");

    $("#login-cgiar-button").addClass("hidden");
    $("input#login_next").addClass("hidden");
    $("#login-otp-request-container").removeClass("hidden");

    setChoiceAlternative(true);
    showMethodView("choice");
  } else {
    // The choice offers the password next to the code on this branch (UI-006)
    setChoiceAlternative(false);

    $("#login-step-email, #login-step-project, #login-step-cgiar").addClass("hidden");
    $("#login-step-password").removeClass("hidden");

    $("#login-cgiar-button").addClass("hidden");
    $("input#login_next").removeClass("hidden");
    // CHG-OTP-LOGIN-001-T14 / UI-001: the additive code option, only when the unit enables it. Since T17 the
    // request button is the "Send code" control of the code panel, which stays closed until it is chosen
    $("#login-otp-request-container").toggleClass("hidden", otpEnabled !== true);

    // CHG-COGNITO-AUTH-001-T12 fix (audit Issue 1): a prior visit to the COGNITO branch above
    // .remove()d #login-password from the document. Restore it from the template captured in
    // init() before this arm can rely on it - #login-password is #login-step-password's last
    // child, so append() puts it back in its original position. Re-cloned every time (never the
    // template node itself) so the same page can restore it more than once
    if($("#login-password").length === 0) {
      $("#login-step-password").append(passwordFieldTemplate.clone(true));
      // Reassigning inputPassword is not optional: :142/:746's references to the stale detached
      // node are what makes the LOCAL step permanently unusable after a COGNITO visit otherwise
      inputPassword = $("input[name='user.password']");
    }

    // CHG-OTP-LOGIN-001-T17 / UI-006: with the code enabled, step 3 always opens on the method choice (and
    // moves the focus into it). Otherwise it is today's password field, shown directly
    if(passwordEnabled === false && otpEnabled === true) {
      // CHG-PWD-SWITCH-001-T06 / UI-001, UI-002: the password is off for this unit, so the code is the only method
      // and is shown directly. The password input leaves the document exactly as on the COGNITO arms (FN-001), and
      // there is no other way to choose, so the "Choose another way to sign in" link goes too
      $("#login-password").remove();
      fillMethodInfo();
      showMethodView("code");
      $("#login-method-code-back").addClass("hidden");
    } else if(passwordEnabled === false) {
      // CHG-PWD-SWITCH-001-T06 / FN-010: the code is the only method but this identifier cannot receive one (a
      // username, or an email that is not one). No password field and no dead end: say what to type and offer step 1
      showUsernameOnlyView();
    } else if(otpEnabled === true) {
      showMethodView("choice");
    } else {
      resetMethodView();
      // Set focus on password input (LOCAL only - the Cognito control manages its own
      // focus/activation and is not part of this form's tab-to-submit flow)
      $(".loginForm #login-password input").focus();
    }
  }

  clearLoginButtonLoading();
  updateNextButtonState();

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

                // CHG-COGNITO-AUTH-001-T12/PS-20: store this Global Unit's numeric id and its
                // per-unit Cognito flag (T10) on its card, so selecting it later (the click
                // handler above) can compose `mode` and know which unit to authenticate against,
                // without a second round trip to crpByEmail.do
                $('.selection-bar-options ul #crp-' + data.crps[i].acronym).attr({
                  'data-global-unit-id': data.crps[i].id,
                  'data-cognito-enabled': data.crps[i].cognitoEnabled ? 'true' : 'false',
                  'data-otp-enabled': data.crps[i].otpEnabled === true ? 'true' : 'false',
                  // CHG-PWD-SWITCH-001-T06: absent from an older server's answer, which means "on"
                  'data-password-enabled': data.crps[i].passwordEnabled === false ? 'false' : 'true'
                });

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

              // If the user previously accepted the terms and conditions, check the box by default.
              // BUG-LOGIN-TERMS-001-FN-005: .prop(), not .attr(). jQuery's .attr('checked', ...) writes the
              // CONTENT attribute, which stops driving the rendered state once the element's dirty-value
              // flag is set - that is, once the user has clicked the checkbox even once. Since
              // showEmailStep() never resets #terms either, a "Go back" plus a second lookup could leave a
              // user whose record says agree=true looking at an unticked box this line believed it had
              // ticked. "=== true" keeps a null agree_terms from rendering as accepted
              $('input#terms').prop('checked', data.user.agree === true);

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

  // CHG-COGNITO-AUTH-001-T12: one half of `mode = user.isCgiarUser && card.cognitoEnabled`
  // (FN-001). The other half, cognitoEnabled, is captured per-card on selection below
  isCgiarUser = !!data.user.isCgiarUser;

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
  // CHG-COGNITO-AUTH-001-PS-20: the guard on validateUser.do (T11) narrows to the selected
  // Global Unit only when it is supplied; without it, a mixed-membership CGIAR user (MIG-001)
  // is refused on every membership instead of just the migrated one. Sent only when known, so an
  // attempt that somehow reaches this function without a card ever being selected does not send
  // an empty value that fails Struts' Long conversion
  var postData = {
    userEmail: email,
    userPassword: password,
    agree: $('input#terms').is(':checked')
  };
  if(selectedGlobalUnitId) {
    postData.globalUnitId = selectedGlobalUnitId;
  }

  $
      .ajax({
          url: baseUrl + "/validateUser.do",
          // POST so the credentials are not left in the URL (proxy logs, browser history, Referer)
          type: "POST",
          data: postData,
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
              // Keep the spinner and the lock while the real form submits and the page navigates away.
              // BUG-LOGIN-TERMS-001-FN-003: but a submit that constraint validation refuses NEVER FIRES,
              // and this branch is the one holding the lock - so a refusal here left the button disabled
              // with its spinner, "Go back" disabled and isSubmitting stuck true, recoverable only by
              // reloading the page. The terms guard above makes that specific case unreachable; this check
              // makes the whole CLASS unreachable, for whatever required control this form grows next.
              // closest("form") rather than an id: the form's id is generated by the Struts theme
              var loginFormElement = $("input#login_formSubmit").closest("form")[0];
              if(loginFormElement && typeof loginFormElement.checkValidity == "function"
                  && !loginFormElement.checkValidity()) {
                clearLoginButtonLoading();
                updateNextButtonState();
                wrongData("serverError");
                return;
              }
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

  // bottom red line in input. BUG-LOGIN-TERMS-001: not for "termsRequired" - neither the email nor the
  // password input is what is wrong there, and underlining both sends the user to check fields they have
  // already filled in correctly
  if(type != "termsRequired") {
    $('input.login-input').addClass("wrongData");
  }

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
  } else if(type == "termsRequired") {
    // BUG-LOGIN-TERMS-001: the checkbox, not the email field. By step 3 the email input sits inside a step
    // carrying .hidden, which is display:none (global.css) - focusing a display:none element is a no-op,
    // so the fallback below would leave the message on screen with nothing pointing at the control to act
    // on. On the COGNITO branch the password input does not exist at all, making this the only target
    $('input#terms').focus();
  } else {
    username.focus();
  }

  // BUG-LOGIN-TERMS-001: "termsRequired" names a control the user has not completed yet, not an outcome
  // worth reporting. postMessageToSlack below is a SYNCHRONOUS XMLHttpRequest (utils.js), so routing the
  // most frequent slip this form can produce through it would block the UI thread on a webhook round trip
  // and bury the real notifications underneath it
  if(type == "termsRequired") {
    return;
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

// BUG-LOGIN-TERMS-001-FN-001/FN-002: the terms gate, shared by both step-3 branches. It replaces the HTML5
// "required" that used to sit on #terms - see loginForm.ftl for why that attribute could not work on either
// one. Returns true when the attempt may proceed; shows the message, focuses the checkbox and returns false
// otherwise. One helper, two call sites, so the branches cannot drift apart
function termsAccepted() {
  if($('input#terms').is(':checked')) {
    return true;
  }
  wrongData("termsRequired");
  return false;
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

// ---------------------------------------------------------------------------------------------------------------
// CHG-OTP-LOGIN-001-T14: e-mailed sign-in code (UI-001 .. UI-005). Everything below is reachable only from the
// OTP controls, which stay hidden unless the selected Global Unit reported otpEnabled === true
// ---------------------------------------------------------------------------------------------------------------

// Text of one i18n message rendered (hidden) by loginForm.ftl
function otpCopy(key) {
  return $('#login-otp-copy [data-otp-copy="' + key + '"]').text();
}

// Show exactly one OTP message: inside the code step once it is open, otherwise under the password step
function otpShowError(key) {
  // T22: on a unit whose password is switched off "use your password" would point at a method that is refused there
  if(key === "unavailable" && passwordEnabled === false) {
    key = "unavailablePasswordOff";
  }
  cleanWrongData();
  var $target = (currentStep == "otp") ? $('#login-otp-error') : $('.loginForm p.invalidField.otpRequestError');
  $target.text(otpCopy(key)).removeClass("hidden");
}

// One place that derives every OTP button's state, so a request in flight can never leave one stuck
function otpRefreshButtons() {
  var busy = otpRequestInFlight || otpVerifyInFlight || otpRedirecting;

  $('#login-otp-button').attr("disabled", otpRequestInFlight)
      .text(otpRequestInFlight ? otpCopy("sending") : otpCopy("sendCode"));
  // T17: "Choose another way to sign in" is unavailable while the code request owns the panel
  $('.login-method-back-container .loginBack').attr("disabled", otpRequestInFlight || otpRedirecting);

  $('#login-otp-verify').attr("disabled", busy).text(otpVerifyInFlight ? otpCopy("verifying") : otpCopy("verify"));

  var resendText = otpCopy("resend");
  if(otpRequestInFlight) {
    resendText = otpCopy("sending");
  } else if(otpCooldownLeft > 0) {
    resendText = otpCopy("resendIn").replace("__N__", function() {
      return String(otpCooldownLeft);
    });
  }
  $('#login-otp-resend').attr("disabled", busy || otpCooldownLeft > 0).text(resendText);
  $('#login-otp-different').attr("disabled", otpRedirecting);
}

function stopOtpCooldown() {
  if(otpCooldownTimer !== null) {
    clearInterval(otpCooldownTimer);
    otpCooldownTimer = null;
  }
  otpCooldownLeft = 0;
}

// Resend countdown. A restart replaces any timer still running
function startOtpCooldown() {
  stopOtpCooldown();
  otpCooldownLeft = OTP_COOLDOWN_SECONDS;
  otpCooldownTimer = setInterval(function() {
    otpCooldownLeft--;
    if(otpCooldownLeft <= 0) {
      stopOtpCooldown();
    }
    otpRefreshButtons();
  }, 1000);
  otpRefreshButtons();
}

// Back to a clean slate: called by showEmailStep(), which is also what "use a different email" runs. The
// sequence bump is what makes a response still in flight stale
function resetOtpState() {
  otpRequestSeq++;
  otpRequestInFlight = false;
  otpVerifyInFlight = false;
  otpRedirecting = false;
  otpEnabled = false;
  stopOtpCooldown();
  $("#login-step-otp").addClass("hidden");
  $("#login-otp-request-container").addClass("hidden");
  $("#login-otp-code").val("");
  $("#login-otp-status").text("");
  $("#login-otp-error").addClass("hidden").text("");
  otpRefreshButtons();
}

// CHG-OTP-LOGIN-001-T17 / UI-006: true while step 3 shows the method choice or the code panel, the two views in
// which no password is being entered and Enter must not submit anything
function methodNotPassword() {
  return currentStep == "password" && (methodView == "choice" || methodView == "code" || methodView == "username");
}

// Fills the code panel's sentence with the masked destination. Used by the choice click and by the code-only view
function fillMethodInfo() {
  $('#login-method-info').text(otpCopy("methodInfo").replace("__DEST__", function() {
    return otpMaskedEmail(username.val());
  }));
}

// CHG-PWD-SWITCH-001-T06 / FN-010: step 3 for an identifier the code cannot be sent to when the password is off.
// Leaves only the message and the "use a different email" action; no password input, no "Log in", no terms
function showUsernameOnlyView() {
  methodView = "username";
  $("#login-password").remove();
  $("#login-method-choice, #login-method-code, #login-method-password-back").addClass("hidden");
  $("#login-password-label, input#login_next, #login-otp-request-container, .terms-container").addClass("hidden");
  $("#login-method-username").removeClass("hidden");
  $("#login-method-username-different").focus();
}

// Browser-side twin of OtpDomainPolicy.mask(): first character, "***", then the domain, from the typed email.
// Anything without exactly one "@" in the middle shows just "***", like the server
function otpMaskedEmail(email) {
  var typed = $.trim(email == null ? "" : String(email)).toLowerCase();
  var at = typed.indexOf("@");
  if(at <= 0 || at !== typed.lastIndexOf("@") || at === typed.length - 1) {
    return "***";
  }
  return typed.charAt(0) + "***" + typed.substring(at);
}

// CHG-OTP-LOGIN-001-T21 / UI-006: the other way the choice offers next to the code. "Continue with your password" for
// the LOCAL branch, "Sign in with CGIAR" for a CGIAR account on a Cognito unit (then the "External user" caption of
// the password step does not apply either)
function setChoiceAlternative(cognito) {
  $("#login-method-password-container, #login-external-user-label").toggleClass("hidden", cognito);
  $("#login-method-cgiar-container").toggleClass("hidden", !cognito);
}

// Leaves step 3 in its plain state: no choice, no code panel, the password field shown. Does not touch
// "Log in" or the focus, which every caller already owns
function resetMethodView() {
  methodView = "direct";
  setChoiceAlternative(false);
  $("#login-method-choice, #login-method-code, #login-method-password-back, #login-method-username")
      .addClass("hidden");
  $("#login-password-label, #login-password").removeClass("hidden");
  $("#login-method-code-back").removeClass("hidden");
}

// Opens one view of step 3 when the code is enabled: "choice", "code" or "password". Only the opened view's
// controls are shown, and the focus moves to the first of them
function showMethodView(view) {
  methodView = view;
  var passwordOpen = (view == "password");
  $("#login-method-username").addClass("hidden");
  // The code-only view hides the way back to the choice after opening; every other opening shows it again
  $("#login-method-code-back").removeClass("hidden");
  $("#login-method-choice").toggleClass("hidden", view != "choice");
  $("#login-method-code").toggleClass("hidden", view != "code");
  $("#login-password-label, #login-password").toggleClass("hidden", !passwordOpen);
  $("input#login_next").toggleClass("hidden", !passwordOpen);
  $("#login-method-password-back").toggleClass("hidden", !passwordOpen);

  if(view == "choice") {
    $("#login-method-code-button").focus();
  } else if(view == "code") {
    $("#login-otp-button").focus();
  } else {
    $(".loginForm #login-password input").focus();
  }
}

// Opens (or refreshes, on a resend) the code step
function showOtpStep(destination) {
  currentStep = "otp";

  cleanWrongData();

  $("#login-step-email, #login-step-project, #login-step-password, #login-step-cgiar").addClass("hidden");
  $("#login-step-otp").removeClass("hidden");
  $("input#login_next, #login-cgiar-button, #login-otp-request-container").addClass("hidden");
  $('.terms-container, .login-back-container, #login-method-password-back').addClass("hidden");

  $("#login-otp-status").text(otpCopy("sent").replace("__DEST__", function() {
    return destination == null ? "" : String(destination);
  }));
  $("#login-otp-error").addClass("hidden").text("");
  $("#login-otp-code").val("").focus();

  startOtpCooldown();
}

// "Email me a sign-in code" and "Resend code"
function requestOtpCode() {
  if(otpRequestInFlight || otpVerifyInFlight || otpRedirecting) {
    return;
  }
  // Same gate as the other buttons, before any request. The server refuses it too (termsRequired)
  if(!termsAccepted()) {
    return;
  }

  var seq = ++otpRequestSeq;
  var stepAtStart = currentStep;
  otpRequestInFlight = true;
  cleanWrongData();
  otpRefreshButtons();

  // A non-JSON answer, a network error and a timeout are all "unavailable"
  var failedRequest = function() {
    if(seq !== otpRequestSeq) {
      return;
    }
    // Same drop rule as the success path: a password attempt or another step owns the page now
    if(currentStep !== stepAtStart || isSubmitting) {
      otpRequestInFlight = false;
      otpRefreshButtons();
      return;
    }
    otpRequestInFlight = false;
    otpShowError("unavailable");
    otpRefreshButtons();
  };

  $.ajax({
    url: baseUrl + "/otpRequestCode.do",
    type: "POST",
    dataType: "json",
    timeout: OTP_REQUEST_TIMEOUT_MS,
    data: {
      email: $.trim(username.val()),
      globalUnitId: selectedGlobalUnitId,
      agree: $('input#terms').is(':checked')
    },
    success: function(data) {
      if(seq !== otpRequestSeq) {
        return;
      }
      if(data == null || typeof data.status != "string") {
        failedRequest();
        return;
      }
      otpRequestInFlight = false;
      // A password attempt started meanwhile (or any other step change) owns the page now
      if(currentStep !== stepAtStart || isSubmitting) {
        otpRefreshButtons();
        return;
      }
      if(data.status == "sent") {
        showOtpStep(data.destination);
      } else if(data.status == "termsRequired") {
        wrongData("termsRequired");
      } else if(data.status == "domainNotEnabled" || data.status == "rateLimited") {
        otpShowError(data.status);
      } else {
        otpShowError("unavailable");
      }
      otpRefreshButtons();
    },
    error: function(jqXHR) {
      // Browser console logging is limited to the HTTP status
      if(window.console && jqXHR) {
        console.log("otpRequestCode.do status " + jqXHR.status);
      }
      failedRequest();
    }
  });
}

// "Verify code"
function verifyOtpCode() {
  if(otpRequestInFlight || otpVerifyInFlight || otpRedirecting) {
    return;
  }

  var code = $.trim($('#login-otp-code').val());
  if(!/^\d{6}$/.test(code)) {
    otpShowError("codeRequired");
    $('#login-otp-code').focus();
    return;
  }

  var seq = ++otpRequestSeq;
  otpVerifyInFlight = true;
  cleanWrongData();
  otpRefreshButtons();

  var failedVerify = function() {
    if(seq !== otpRequestSeq || otpRedirecting) {
      return;
    }
    otpVerifyInFlight = false;
    otpShowError("unavailable");
    otpRefreshButtons();
  };

  $.ajax({
    url: baseUrl + "/otpVerifyCode.do",
    type: "POST",
    dataType: "json",
    timeout: OTP_REQUEST_TIMEOUT_MS,
    data: {
      code: code
    },
    success: function(data) {
      if(seq !== otpRequestSeq || otpRedirecting) {
        return;
      }
      if(data == null || typeof data.status != "string") {
        failedVerify();
        return;
      }

      // "failed" carries the generic login redirect the server picked, so it navigates like "ok" does
      if((data.status == "ok" || data.status == "failed") && typeof data.redirect == "string" && data.redirect != "") {
        otpRedirecting = true;
        otpRefreshButtons();
        window.location.href = data.redirect;
        return;
      }

      otpVerifyInFlight = false;
      if(data.status == "mismatch") {
        otpShowError("mismatch");
        $('#login-otp-code').val("").focus();
      } else if(data.status == "exhausted" || data.status == "invalid") {
        // The code is spent: let the user ask for a new one at once. The server still enforces its own bucket
        otpShowError(data.status);
        stopOtpCooldown();
      } else if(data.status == "rateLimited") {
        otpShowError("rateLimited");
      } else {
        otpShowError("unavailable");
      }
      otpRefreshButtons();
    },
    error: function(jqXHR) {
      if(window.console && jqXHR) {
        console.log("otpVerifyCode.do status " + jqXHR.status);
      }
      failedVerify();
    }
  });
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