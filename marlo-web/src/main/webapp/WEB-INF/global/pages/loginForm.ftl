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
          [#-- CRP Image (kept for compatibility with existing JS, not shown in the card layout) --]
          <div class="form-group text-center hidden">
            <img id="crpSelectedImage" width="300px" src="${baseUrl}/data/globalUnitLogo.do?acronym=${(element.acronym)!'default'}" alt="${(element.name)!}" />
          </div>
        </div>

        [#-- Step 3: Password --]
        <div class="login-step hidden" id="login-step-password">
          <p class="login-headline">[@s.text name=loginHeadlineKey/]</p>
          <div class="selected-project-container">
            <p class="login-field-label">[@s.text name="login.selectedProject"/]</p>
            <ul class="selected-project-card-list">
              <li id="login-selected-project-card" class="selected-project-card"></li>
            </ul>
          </div>
          [#-- Complete name of the user (when the record has one) followed by what was typed in step 1 --]
          <div class="login-echoed-email-container">
            <p class="login-field-label">[@s.text name="login.loggingInAs"/]</p>
            <p class="login-echoed-email">
              <span class="login-echoed-name"></span><span class="login-echoed-username"></span>
            </p>
          </div>
          <p class="login-field-label">[@s.text name="login.password"/]:</p>
          <div class="login-input-container" id="login-password">
            <span class="glyphicon glyphicon-eye-close icon-show-password"></span>
            <input id="user.password" class="login-input user-password form-control" type="password" name="user.password" placeholder="${passwordPlaceholder}" tabindex=0 required/>
          </div>
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

        [#-- Terms and conditions checkbox --]
        <div class="terms-container hidden">
          <input type="checkbox" name="user.agree" id="terms" class="terms" value="true" required> [@s.text name="login.agree"/] <a target="_blank" href="[@s.url namespace="/" action='legalInformation'][/@s.url]#termsConditions">[@s.text name="login.terms"/]</a>
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
