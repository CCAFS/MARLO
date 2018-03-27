[#ftl] 
<div id="loginFormContainer">
  [#if !config.production]
  <div class="loginForm instructions">
    <p>[@s.text name="login.testersMessage"/]</p>
  </div>
  [/#if]
  [#-- Form --]
  [@s.form method="POST" namespace="/" action="login"]
     <div class="crps-select hidden">
      <div class="name-type-container type-CRP hidden">
        <span class="selection-bar-title">[@s.text name="login.crps"/]:</span>
      </div>
      <div class="selection-bar-options">
        <ul>
          [#attempt] 
            [#assign crpList = action.getCrpCategoryList("1") /]
          [#recover]
            [#assign crpList = [] /]
          [/#attempt]
          [#if crpList?has_content]
            [#list crpList as crp]
              [@availableItems element=crp /]
            [/#list]
          [#else]
            <p>[@s.text name="login.error.crps"/]</p>
          [/#if]
        </ul>
      </div>
      <div class="name-type-container type-Center hidden">
        <span class="selection-bar-title">[@s.text name="login.centers"/]:</span>
      </div>
      <div class="selection-bar-options">
        <ul>
          [#attempt] 
            [#assign centerList = action.getCrpCategoryList("2") /]
          [#recover]
            [#assign centerList = [] /]
          [/#attempt]
          [#if centerList?has_content]
            [#list centerList as center]
              [@availableItems element=center /]
            [/#list]
          [#else]
            <p>[@s.text name="login.error.centers"/]</p>
          [/#if]
        </ul>
      </div>
      <div class="name-type-container type-Platform hidden">
        <span class="selection-bar-title">[@s.text name="login.platforms"/]:</span>
      </div>
      <div class="selection-bar-options">
        <ul>
          [#attempt] 
            [#assign platformsList = action.getCrpCategoryList("3") /]
          [#recover]
            [#assign platformsList = [] /]
          [/#attempt]
          [#if platformsList?has_content]
            [#list platformsList as platform]
              [@availableItems element=platform /]
            [/#list]
          [#else]
            <p>[@s.text name="login.error.platforms"/]</p>
          [/#if]
        </ul>
      </div>
     </div>
     [#-- End crps select --]
     [#-- Check if the inputs can be replaced with the macro from forms.ftl --]
     [#-- Trick for IE z-index --]
     <div style="position:relative;">
     <div class="loginForm" style="z-index: 1000">
        [#-- Email --]
        <div class="row">
          <div class="col-sm-12">
            <div class="login-input-container" id="login-email">
              <input id="user.email" class="login-input user-email form-control" type="text" name="user.email" value="" required/>
              <label for="user.email">[@s.text name="login.email"/]</label>
            </div>
            [#-- CRP Session --]
            <input type="hidden" id="crp-input" name="crp" value="${(crpSession)!}" />
            
            [#-- Image --]
            <div class="form-group text-center hidden" >
              <img id="crpSelectedImage" width="300px" src="${baseUrl}/global/images/crps/${(element.acronym)!'default'}.png" alt="${(element.name)!}" />
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
        [#-- Password --]
        <div class="row" >
          <div class="col-sm-12">
            <div class="login-input-container hidden" id="login-password" >
              <input id="user.password" class="login-input user-password form-control" type="password" name="user.password" tabindex=1 required/>
              <label for="user.password">[@s.text name="login.password"/]</label>
            </div>
            <p class="invalidField invalidEmail hidden">[@s.text name="login.error.invalidField.invalidEmail"/]</p>
            <p class="invalidField emailNotFound hidden">[@s.text name="login.error.invalidField.emailNotFound"/]</p>
            <p class="invalidField deniedAccess hidden">[@s.text name="login.error.invalidField.deniedAccess"/]</p>
            <p class="invalidField voidPassword hidden">[@s.text name="login.error.invalidField.voidPassword"/]</p>
            <p class="invalidField incorrectPassword hidden">[@s.text name="login.error.invalidField.incorrectPassword"/]</p>
          </div>
        </div>
        [#-- Terms and conditions checkbox --]
        <div class="row">
          <div class="col-xs-12">
            <div class="terms-container hidden">
              <input type="checkbox" name="user.agree" id="terms" class="terms" value="true" checked required> I agree to the <a target="_blank" href="[@s.url namespace="/" action='legalInformation'][/@s.url]#termsConditions">Terms and Conditions</a>
            </div>
          </div>
        </div>
        [#-- Submit button --]
        <div class="row">
          <div class="col-sm-12">
            <div class="login-button-container">
              [@s.submit key="Next" name="next" cssClass="login-form-button" role="button" disabled=false /]
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
</div><!-- End loginFormContainer -->

[#macro availableItems element]
  <li id="crp-${element.acronym}" class="option ${element.login?string('enabled', 'disabled')}" title="">
    <img class="selection-bar-image animated bounceIn hidden" src="${baseUrl}/global/images/crps/${element.acronym}.png" alt="${element.name}" tabindex=0/>
    <div class="selection-bar-acronym hidden">${element.acronym}</div>
  </li>
[/#macro]