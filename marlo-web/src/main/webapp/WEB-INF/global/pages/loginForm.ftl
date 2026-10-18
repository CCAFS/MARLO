[#ftl]
[#-- Placeholder shows the expected format, the visible label above the field names it (login.emailLabel) --]
[#assign emailPlaceholder][@s.text name="login.emailPlaceholder"/][/#assign]
[#assign passwordPlaceholder][@s.text name="login.password"/][/#assign]
[#-- Accessible name of the "Go back" control, longer than the visible label it contains --]
[#assign goBackAccessibleName][@s.text name="login.goBack.accessibleName"/][/#assign]
[#-- Headline key: pages including this form may override it (e.g. the 401 unauthorized access page) --]
[#assign loginHeadlineKey = (loginHeadlineKey)!"login.headline" /]
<div id="loginFormContainer">
  [#-- [#if !config.production]
  <div class="login-testers-note">
    <p>[@s.text name="login.testersMessage"/]</p>
    <p>[@s.text name="login.testersMessage3"/]</p>
  </div>
  [/#if] --]
  [#-- Form --]
  [@s.form method="POST" namespace="/" action="login"]
     [#-- Trick to fix z-index bug in IE --]
     <div style="position:relative;">
     <div class="loginForm" style="z-index: 1000">

        [#-- Step 1: Email --]
        <div class="login-step" id="login-step-email">
          <p class="login-headline">[@s.text name=loginHeadlineKey/]</p>
          <p class="login-subtext">[@s.text name="login.emailHint"/]</p>
          <p class="login-field-label">[@s.text name="login.emailLabel"/]</p>
          <div class="login-input-container" id="login-email">
            <input id="user.email" class="login-input user-email form-control" type="text" name="user.email" value="" placeholder="${emailPlaceholder}" tabindex=0 required/>
          </div>
        </div>

        [#-- CRP Session (hidden input) --]
        <input type="hidden" id="crp-input" name="crp" value="${(crpSession)!}" />

        [#-- Step 2: Project selection --]
        <div class="login-step hidden" id="login-step-project">
          <p class="login-headline">[@s.text name=loginHeadlineKey/]</p>
          <p class="login-subtext">[@s.text name="login.selectProject"/]</p>
          [#-- Login CRPs select bar, re-skinned as project cards --]
          <div class="crps-select hidden">
            [#if listGlobalUnitTypes??]
              [#list listGlobalUnitTypes as globalUnitType]
                [#if globalUnitType.globalUnitsList?has_content]
                <div class="name-type-container type-${globalUnitType.id} hidden">
                  <span class="selection-bar-title">${globalUnitType.name!}s:</span>
                </div>
                <div class="selection-bar-options">
                  <ul>
                  [#list globalUnitType.globalUnitsList as globalUnit]
                    [#if globalUnit.login][@availableItems element=globalUnit /][/#if]
                  [/#list]
                  </ul>
                </div>
                [/#if]
              [/#list]
            [/#if]
          </div>
          [#-- Skeleton placeholder cards shown while crpByEmail.do is loading --]
          <div class="project-skeleton hidden">
            <div class="selection-bar-options">
              <ul>
                <li class="skeleton-card"></li>
                <li class="skeleton-card"></li>
                <li class="skeleton-card"></li>
                <li class="skeleton-card"></li>
              </ul>
            </div>
          </div>
          [#-- CRP Image (kept for compatibility with existing JS, not shown in the card layout).
               No src here on purpose: loadSelectedImage() in login.js sets it once a Global Unit is actually
               selected. A src built at render time asked globalUnitLogo.do for the acronym of a unit nobody
               had chosen yet, and the browser fetched it even though the container is hidden. --]
          <div class="form-group text-center hidden">
            <img id="crpSelectedImage" width="300px" alt="" />
          </div>
        </div>

        [#-- Step 3: Password --]
        <div class="login-step hidden" id="login-step-password">
          <p class="login-headline">[@s.text name=loginHeadlineKey/]</p>
          [#-- CHG-COGNITO-AUTH-001-T18 / FN-001: names the authentication method for this block, the
               way #login-step-cgiar announces itself through its own labelled control. Uses the
               headline/subtext pair steps 1 and 2 already use (loginForm.ftl:25, :38), so it reads as
               this step's heading rather than as a field label of the card below it. Placed before
               #login-password, which login.js restores with append() and must stay the last child --]
          <p class="login-subtext" id="login-external-user-label">[@s.text name="login.externalUser"/]</p>
          <div class="selected-project-container">
            <p class="login-field-label">[@s.text name="login.selectedProject"/]</p>
            <ul class="selected-project-card-list">
              <li class="selected-project-card"></li>
            </ul>
          </div>
          [#-- Complete name of the user (when the record has one) followed by what was typed in step 1 --]
          <div class="login-echoed-email-container">
            <p class="login-field-label">[@s.text name="login.loggingInAs"/]</p>
            <p class="login-echoed-email">
              <span class="login-echoed-name"></span><span class="login-echoed-username"></span>
            </p>
          </div>
          [#-- CHG-OTP-LOGIN-001-T17 / UI-006: the method choice and the code panel. Both stay hidden and inert
               unless login.js opens them, which it does when otpEnabled === true, on the LOCAL branch and (T21) for
               a CGIAR account on a Cognito unit, where "Sign in with CGIAR" replaces the password method. The
               password label and #login-password below are the existing markup, reused as the password panel.
               Placed before the label on purpose: #login-password must stay this step's last child, which
               login.js relies on to restore it with append() --]
          <div class="login-method-choice hidden" id="login-method-choice">
            <p class="login-field-label" id="login-method-heading">[@s.text name="login.otp.method.heading"/]</p>
            <div class="login-button-container">
              <button type="button" id="login-method-code-button" class="login-form-button">[@s.text name="login.otp.method.code"/]</button>
            </div>
            <div class="login-button-container" id="login-method-password-container">
              <button type="button" id="login-method-password-button" class="login-form-button login-form-button-secondary">[@s.text name="login.otp.method.password"/]</button>
            </div>
            [#-- CHG-OTP-LOGIN-001-T21: the other way of a CGIAR account on a Cognito unit. Same handler as
                 #login-cgiar-button, which stays the single-method control when the code is not offered --]
            <div class="login-button-container hidden" id="login-method-cgiar-container">
              <button type="button" id="login-method-cgiar-button" class="login-form-button login-form-button-secondary">[@s.text name="login.cgiarSignIn"/]</button>
            </div>
          </div>
          <div class="login-method-code hidden" id="login-method-code">
            <p class="login-method-info" id="login-method-info"></p>
            [#-- The T14 request button, moved here as the panel's "Send code": same id, same handler --]
            <div class="login-button-container hidden" id="login-otp-request-container">
              <button type="button" id="login-otp-button" class="login-form-button">[@s.text name="login.otp.method.send"/]</button>
            </div>
            <div class="login-method-back-container">
              <button type="button" id="login-method-code-back" class="loginBack">[@s.text name="login.otp.method.back"/]</button>
            </div>
          </div>
          [#-- CHG-PWD-SWITCH-001-T06 / FN-010: the code is the only method but the identifier typed in step 1 cannot
               receive one. Hidden and inert unless login.js opens it. Before #login-password-label on purpose, so
               #login-password stays this step's last child --]
          <div class="login-method-username hidden" id="login-method-username">
            <p class="login-method-info" id="login-method-username-info" role="status">[@s.text name="login.otp.usernameOnly"/]</p>
            <div class="text-center">
              <button type="button" id="login-method-username-different" class="btn btn-link" aria-describedby="login-method-username-info">[@s.text name="login.otp.differentEmail"/]</button>
            </div>
          </div>
          <p class="login-field-label" id="login-password-label">[@s.text name="login.password"/]:</p>
          <div class="login-input-container" id="login-password">
            <span class="glyphicon glyphicon-eye-close icon-show-password"></span>
            <input id="user.password" class="login-input user-password form-control" type="password" name="user.password" placeholder="${passwordPlaceholder}" tabindex=0 required/>
          </div>
        </div>

        [#-- Step 3 (COGNITO branch): CHG-COGNITO-AUTH-001-T12 / FN-001. A CGIAR user whose
             selected Global Unit has the Cognito specificity enabled sees this single "Sign in
             with CGIAR" control instead of a password field. The selected-project card and the
             echoed identity are the same reused markup/classes as #login-step-password
             (design.md 5.2: "both reused") so login.js's existing mirroring code
             (secondForm/click handler, showPasswordStep) targets both blocks without change.
             The password input is never rendered here -- it exists only inside
             #login-step-password, and login.js additionally .remove()s it on this branch as the
             DOM-removal measure design.md 5.2 requires (not .hide(), not disabled). The terms
             checkbox and the CGIAR control itself live outside this block (.terms-container,
             .login-button-container below), exactly as the password step's do, so no control is
             ever duplicated between the two branches --]
        <div class="login-step hidden" id="login-step-cgiar">
          <p class="login-headline">[@s.text name=loginHeadlineKey/]</p>
          <div class="selected-project-container">
            <p class="login-field-label">[@s.text name="login.selectedProject"/]</p>
            <ul class="selected-project-card-list">
              <li class="selected-project-card"></li>
            </ul>
          </div>
          [#-- Complete name of the user (when the record has one) followed by what was typed in step 1 --]
          <div class="login-echoed-email-container">
            <p class="login-field-label">[@s.text name="login.loggingInAs"/]</p>
            <p class="login-echoed-email">
              <span class="login-echoed-name"></span><span class="login-echoed-username"></span>
            </p>
          </div>
        </div>

        [#-- CHG-OTP-LOGIN-001-T14 / UI-002, UI-003: the e-mailed sign-in code step. Inert and hidden until
             login.js opens it, which only happens after a successful otpRequestCode.do answer for a Global
             Unit whose crpByEmail.do entry reported otpEnabled === true. The code input has no "name" and is
             not a .login-input, so it is never submitted with this form and the existing Enter/keyup relays
             never reach it. Every string comes from global.properties --]
        <div class="login-step hidden" id="login-step-otp">
          <p class="login-headline">[@s.text name="login.otp.title"/]</p>
          <p class="login-subtext" id="login-otp-status" role="status" aria-live="polite"></p>
          <label class="login-field-label" for="login-otp-code">[@s.text name="login.otp.codeLabel"/]</label>
          <div class="login-input-container">
            <input id="login-otp-code" class="form-control" type="text" inputmode="numeric" autocomplete="one-time-code" maxlength="6" pattern="[0-9]*" aria-describedby="login-otp-error" autocapitalize="off" spellcheck="false"/>
          </div>
          <p class="invalidField hidden" id="login-otp-error" role="alert"></p>
          <div class="login-button-container">
            <button type="button" id="login-otp-verify" class="login-form-button">[@s.text name="login.otp.verify"/]</button>
          </div>
          <div class="text-center">
            <button type="button" id="login-otp-resend" class="btn btn-link" disabled="disabled">[@s.text name="login.otp.resend"/]</button>
          </div>
          <div class="text-center">
            <button type="button" id="login-otp-different" class="btn btn-link">[@s.text name="login.otp.differentEmail"/]</button>
          </div>
        </div>

        [#-- Copy read by login.js (text only, never HTML). Hidden and inert; __DEST__ and __N__ are replaced
             client-side so the messages stay whole i18n strings --]
        <div class="hidden" id="login-otp-copy" aria-hidden="true">
          <span data-otp-copy="sent">[@s.text name="login.otp.sent"][@s.param]__DEST__[/@s.param][/@s.text]</span>
          <span data-otp-copy="sending">[@s.text name="login.otp.sending"/]</span>
          <span data-otp-copy="verifying">[@s.text name="login.otp.verifying"/]</span>
          <span data-otp-copy="resend">[@s.text name="login.otp.resend"/]</span>
          <span data-otp-copy="resendIn">[@s.text name="login.otp.resendIn"][@s.param]__N__[/@s.param][/@s.text]</span>
          <span data-otp-copy="sendCode">[@s.text name="login.otp.method.send"/]</span>
          <span data-otp-copy="methodInfo">[@s.text name="login.otp.method.info"][@s.param]__DEST__[/@s.param][/@s.text]</span>
          <span data-otp-copy="verify">[@s.text name="login.otp.verify"/]</span>
          <span data-otp-copy="codeRequired">[@s.text name="login.otp.error.codeRequired"/]</span>
          <span data-otp-copy="mismatch">[@s.text name="login.otp.error.mismatch"/]</span>
          <span data-otp-copy="exhausted">[@s.text name="login.otp.error.exhausted"/]</span>
          <span data-otp-copy="invalid">[@s.text name="login.otp.error.invalid"/]</span>
          <span data-otp-copy="rateLimited">[@s.text name="login.otp.error.rateLimited"/]</span>
          <span data-otp-copy="unavailable">[@s.text name="login.otp.error.unavailable"/]</span>
          <span data-otp-copy="unavailablePasswordOff">[@s.text name="login.otp.error.unavailablePasswordOff"/]</span>
          <span data-otp-copy="domainNotEnabled">[@s.text name="login.otp.error.domainNotEnabled"/]</span>
        </div>

        [#-- Error messages. login.js shows exactly one of these at a time, selecting it by its
             second CSS class --]
        <p class="invalidField emailRequired hidden">[@s.text name="login.error.invalidField.emailRequired"/]</p>
        <p class="invalidField invalidEmail hidden">[@s.text name="login.error.invalidField.invalidEmail"/]</p>
        <p class="invalidField emailNotFound hidden">[@s.text name="login.error.invalidField.emailNotFound"/]</p>
        [#-- Generic slot for unexpected failures (request errors, proxy/gateway errors) --]
        <p class="invalidField serverError hidden">[@s.text name="login.error.invalidField.serverError"/]</p>
        <p class="invalidField deniedAccess hidden">[@s.text name="login.error.invalidField.deniedAccess"/]</p>
        <p class="invalidField voidPassword hidden">[@s.text name="login.error.invalidField.voidPassword"/]</p>
        <p class="invalidField incorrectPassword hidden">[@s.text name="login.error.invalidField.incorrectPassword"/]</p>
        [#-- CHG-COGNITO-AUTH-001-T12 fix (audit Issue 2): shown when "Log in" is pressed on step 2
             with no card ever selected, so mode cannot be composed safely --]
        <p class="invalidField selectProject hidden">[@s.text name="login.error.invalidField.selectProject"/]</p>
        [#-- BUG-LOGIN-TERMS-001-UI-001: raised by login.js before any request or redirect is started, so
             the terms gate never locks the button. It joins this block on purpose -- cleanWrongData()
             clears every .invalidField here, which is what keeps exactly one login message on screen --]
        <p class="invalidField termsRequired hidden">[@s.text name="login.error.invalidField.termsRequired"/]</p>

        [#-- CHG-COGNITO-AUTH-001-T22 (V-6): the ONLY two categories a refused Cognito login may ever
             render, selected server-side by LoginAction/CognitoLoginAction's own isCognitoUnavailable()/
             isCognitoFailed() booleans -- never by echoing the authError parameter's own value. Every
             other rejection reason (cognitoNotEligible, inactive, invalidUserCrp, route C's own
             no-message branch) collapses into the "cognitoFailed" outcome server-side, before this
             template ever runs (SEC-005, SEC-006) --]
        <p class="invalidField cognitoUnavailable${(cognitoUnavailable!false)?then('', ' hidden')}">[@s.text name="login.error.cognitoUnavailable"/]</p>
        <p class="invalidField cognitoFailed${(cognitoFailed!false)?then('', ' hidden')}">[@s.text name="login.error.cognitoFailed"/]</p>

        [#-- CHG-OTP-LOGIN-001-T14: shown on the password step when "Email me a sign-in code" is refused
             before the code step opens. Same cleanWrongData() lifecycle as the messages above --]
        <p class="invalidField otpRequestError hidden" role="alert"></p>

        [#-- Terms and conditions checkbox.
             BUG-LOGIN-TERMS-001-UI-002: deliberately NO "required" here. HTML5 constraint validation
             cannot express this gate on either branch. On LOCAL the "Log in" click is preventDefault()ed
             (login.js), so the attribute never fired on the click the user makes; it fired instead on the
             programmatic submit of #login_formSubmit at the end of the validateUser.do round trip, which
             aborted the submission while the button lock taken in beforeSend was still held, with nothing
             left to release it. On COGNITO #login-cgiar-button is type="button" and never submits this
             form, so the attribute could not fire at all. Its message is also browser-supplied, so it
             could never be i18n-keyed. The gate now lives in login.js's termsAccepted(), shared by both
             branches. Do not put it back --]
        <div class="terms-container hidden">
          <input type="checkbox" name="user.agree" id="terms" class="terms" value="true"> [@s.text name="login.agree"/] <a target="_blank" href="[@s.url namespace="/" action='legalInformation'][/@s.url]#termsConditions">[@s.text name="login.terms"/]</a>
        </div>

        [#-- field recaptcha--]
        <div class="container-recaptcha">
          <div id="recaptcha-container"></div>
        </div>

        [#-- Submit button --]
        <div class="login-button-container">
          [#-- This one to jump to next step / validate user data --]
          [@s.submit key="login.logIn" name="next" cssClass="login-form-button" role="button" disabled=false /]
          <span class="login-button-spinner hidden"></span>
          [#-- and This one to send login form --]
          [@s.submit name="formSubmit" cssClass="hidden" role="button "/]
          [#-- CHG-COGNITO-AUTH-001-T12: the COGNITO-branch control (FN-001). A real
               <button type="button">, so Enter and Space both activate it for free and it never
               submits #login-step-password's form -- it navigates to cognitoLogin.do instead
               (design.md 5.5). Reuses .login-form-button, styled only as a descendant of
               .login-button-container (customLogin.css), so it must live in this container --]
          <button type="button" id="login-cgiar-button" class="login-form-button hidden">
            [@s.text name="login.cgiarSignIn"/]
          </button>
        </div>

        [#-- CHG-OTP-LOGIN-001-T17 / UI-006: "Choose another way to sign in" of the password panel (the code
             panel carries its own inside #login-step-password). Hidden unless login.js opens the password panel
             of the method choice. The T14 request button now lives in the code panel above --]
        <div class="login-method-back-container hidden" id="login-method-password-back">
          <button type="button" id="login-method-password-back-button" class="loginBack">[@s.text name="login.otp.method.back"/]</button>
        </div>

        [#-- Go back to the previous step. A real <button> so it is in the tab order, exposes a
             button role to screen readers and is activated by Enter/Space for free.
             type="button" keeps it from submitting the login form --]
        <div class="login-back-container hidden">
          <button type="button" id="login-go-back" class="loginBack" aria-label="${goBackAccessibleName}">[@s.text name="login.goBack"/]</button>
        </div>
     </div>
     </div>
  [/@s.form]

  [#-- Support contact --]
  <div class="login-support">
    <p>[@s.text name="login.supportContact"/] <a href="mailto:MarloSupport@cgiar.org">MarloSupport@cgiar.org</a></p>
  </div>
  [#-- Terms acknowledgement --]
  <p class="login-disclaimer">[@s.text name="login.byContinuing"/] <a target="_blank" href="[@s.url namespace="/" action='legalInformation'][/@s.url]#termsConditions">[@s.text name="login.terms"/].</a></p>
</div>

[#-- Every Global Unit with login=true is rendered here, but a card starts hidden and is only
     revealed by login.js for the units the current user is actually assigned to (data.crps) --]
[#macro availableItems element]
  <li id="crp-${element.acronym}" class="option hidden ${element.login?string('enabled', 'disabled')}" title="${element.name}" tabindex="">
    <img class="selection-bar-image animated bounceIn hidden" src="${baseUrl}/data/globalUnitLogo.do?acronym=${element.acronym}" alt="${element.name}"/>
    <div class="selection-bar-acronym hidden">${element.acronym}</div>
  </li>
[/#macro]
