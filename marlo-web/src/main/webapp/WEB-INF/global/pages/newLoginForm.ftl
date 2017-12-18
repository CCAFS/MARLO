[#ftl] 
<div id="loginFormContainer">
  [#if !config.production]
  [#-- COMMENTED BY REMAKE
  <div class="loginForm instructions">
    [#-- @s.text name="home.login.message.nonCgiar" / -] [#--THIS PART WAS COMMENTED -]
    <p>[@s.text name="login.testersMessage"/]</p>
  </div>
   --]
  [/#if]
  [#-- Form --]
  [@s.form method="POST" namespace="/" action="login"]
    [#-- Login error message --]
    [#-- COMMENTED BY REMAKE
    [@s.fielderror cssClass="fieldError" fieldName="loginMessage"/]
     --]
     <div class="crps-select hidden">
      <div class="name-type-container">
        <span class="selection-bar-title">CRPS:</span>
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
            <p>Not CRPs loaded</p>
          [/#if]
        </ul>
      </div>
      [#-- 
      <div class="name-type-container">
        <span class="selection-bar-title">Centers:</span>
      </div>
      <div class="selection-bar-options">
        <ul>
          [#if centersList?has_content]
            [#list centersList as center]
              [@availableItems element=center /]
            [/#list]
          [#else]
            <p>Not Centers loaded</p>
          [/#if]
        </ul>
      </div>
      <div class="name-type-container">
        <span class="selection-bar-title">Platforms:</span>
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
            <p>Not Platforms loaded</p>
          [/#if]
        </ul>
      </div>
       --]
     </div>
     
     [#-- End crps select --]
     [#-- Trick for IE z-index --]
     <div style="position:relative;">
     <div class="loginForm" style="z-index: 1000">
        [#-- Email --]
        <div class="row">
          <div class="col-sm-12">
            <div class="login-input-container" id="login-email">
              <input id="user.email" class="login-input" type="text" name="user.email" value="" required/>
              <label for="user.email">Email</label>
            </div>
            <p class="invalidEmail hidden">Please enter a valid email</p>
            [#-- CRP Session --]
            <input type="hidden" id="crp-input" name="crp" value="${(crpSession)!}" />
            [#-- Type Session --]
            <input type="hidden" id="type-input" name="type" />
            
            [#-- Image --]
            <div class="form-group text-center hidden" >
            [#-- src="${baseUrl}/global/images/crps/${(element.acronym)!'default'}.png" --]
              <img id="crpSelectedImage"  width="300px" src="${baseUrl}/global/images/crps/${(element.acronym)!'default'}.png" alt="${(element.name)!}" />
            </div>
            [#-- Welcome info --]
            <div class="row">
              <div class="col-sm-10 welcome-message-container hidden" >
                <span class="login-input-container welcome-message">Welcome:</span>
                <br>
                <span class="login-input-container username"><i class="glyphicon glyphicon-triangle-left"></i><span></span></span>
              </div>
            </div>
          </div>
        </div>
        [#-- Password --]
        <div class="row" >
          <div class="col-sm-10">
            <div class="login-input-container hidden" id="login-password" >
              <input id="user.password" class="login-input" type="password" name="user.password" tabindex=1 required/>
              <label for="user.password">Password</label>
            </div>
          </div>
        </div>
        [#-- Submit button --]
        <div class="row">
          <div class="col-sm-12">
            <div class="login-button-container">
              [@s.submit key="Next" name="next" cssClass="login-form-button" role="button "/]
            </div>
          </div>
        </div>
        <div class="row login-back-container hidden">
          <div class="col-xs-6 col-center">
            <p class="loginBack">Log in with another user</p>
          </div>
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
  <li id="crp-${element.acronym}" class="option ${element.login?string('enabled', 'disabled')} hidden" title="">
    <img class="animated bounceIn" src="${baseUrl}/global/images/crps/${element.acronym}.png" alt="${element.name}" tabindex=0/>
  </li>
[/#macro]