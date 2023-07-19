[#ftl] 
<div id="loginFormContainer">
  [#if !config.production]
  <div class="loginForm instructions">
    <p>[@s.text name="login.testersMessage"/]</p>
  </div>
  [/#if]
  [#-- Form --]
  [@s.form method="POST" namespace="/" action="login"]
     [#-- Login CRPs select bar --]
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
     [#-- End crps select --]
     
     [#-- Trick to fix z-index bug in IE --]
     <div style="position:relative;">
     <div class="loginForm" style="z-index: 1000">
        [#-- Row for Email, CRP Session (hide input), CRP Image and Welcome info --]
        <div class="row">
          <div class="col-sm-12">
            [#-- Email input --]
            <div class="login-input-container" id="login-email">
              <input id="user.email" class="login-input user-email form-control" type="text" name="user.email" value="" tabindex=0 required/>
              <label for="user.email">[@s.text name="login.email"/]</label>
            </div>
            [#-- CRP Session (hidden input) --]
            <input type="hidden" id="crp-input" name="crp" value="${(crpSession)!}" />
            
            [#-- CRP Image --]
            <div class="form-group text-center hidden" >
              <img id="crpSelectedImage" width="300px" src="${baseUrlCdn}/global/images/crps/${(element.acronym)!'default'}.png" alt="${(element.name)!}" />
            </div>
            [#-- Welcome info --]
            <div class="row">
              <div class="col-sm-10 welcome-message-container hidden" >
                <span class="login-input-container welcome-message">[@s.text name="login.welcome"/]:</span>
                <br>
                <span class="login-input-container username"><i class="glyphicon glyphicon-triangle-left"></i><span></span></span>
              </div>
            </div>
          </div>
        </div>
        <div class="row" >
          <div class="col-sm-12">
            [#-- Password input --]
            <div class="login-input-container hidden" id="login-password" >
              <span class="glyphicon glyphicon-eye-close icon-show-password"></span>
              <input id="user.password" class="login-input user-password form-control" type="password" name="user.password" tabindex=0 required/>
              <label for="user.password">[@s.text name="login.password"/]</label>
            </div>
            [#-- Error messages --]
            <p class="invalidField invalidEmail hidden">[@s.text name="login.error.invalidField.invalidEmail"/]</p>
            <p class="invalidField emailNotFound hidden">[@s.text name="login.error.invalidField.emailNotFound"/]</p>
            <p class="invalidField deniedAccess hidden">[@s.text name="login.error.invalidField.deniedAccess"/]</p>
            <p class="invalidField voidPassword hidden">[@s.text name="login.error.invalidField.voidPassword"/]</p>
            <p class="invalidField incorrectPassword hidden">[@s.text name="login.error.invalidField.incorrectPassword"/]</p>
          </div>
        </div>
        <div class="row">
          <div class="col-xs-12">
            [#-- Terms and conditions checkbox --]
            <div class="terms-container hidden">
              <input type="checkbox" name="user.agree" id="terms" class="terms" value="true" required> [@s.text name="login.agree"/] <a target="_blank" href="[@s.url namespace="/" action='legalInformation'][/@s.url]#termsConditions">[@s.text name="login.terms"/]</a>
            </div>
          </div>
        </div>
        [#-- field recaptcha--]
        <div class="container-recaptcha">
          <div id="recaptcha-container"></div>
        </div>
        [#-- Submit button --]
        <div class="row">
          <div class="col-sm-12">
            <div class="login-button-container">
              [#-- This one to jump to second form and validate user data --]
              [@s.submit key="Next" name="next" cssClass="login-form-button" role="button" disabled=false /]
              [#-- and This one to send login form --]
              [@s.submit name="formSubmit" cssClass="hidden" role="button "/]
            </div>
          </div>
        </div>
        [#-- Login with different user --]
        <div class="login-back-container hidden">
          <p class="loginBack">[@s.text name="login.differentUser"/]</p>
        </div>
     </div>
     </div>
  [/@s.form]
  <br />
  [#-- Disclaimer --]
  <div class="row">
    <div class="col-md-11 col-center">
      <div class="login-disclaimer text-justify">[@s.text name="login.disclaimer"/]</div>
    </div>
  </div>
</div>

[#macro availableItems element]
  <li id="crp-${element.acronym}" class="option ${element.login?string('enabled', 'disabled')}" title="" tabindex="">
    <img class="selection-bar-image animated bounceIn hidden" src="${baseUrlCdn}/global/images/crps/${element.acronym}.png" alt="${element.name}"/>
    <div class="selection-bar-acronym hidden">${element.acronym}</div>
  </li>
[/#macro]