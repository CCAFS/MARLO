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
     <div class="crps-select" style="display:none">
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
     </div>
     [#-- End crps select --]
     [#-- Trick for IE z-index --]
     <div style="position:relative;">
     <div class="loginForm" style="z-index: 1000">
        [#-- Email --]
        <div class="row">
          <div class="col-sm-12">
            <div class="login-input-container" id="login-email">
              <input class="login-input" type="text" name="login-email" required/>
              <label for="login-email">Email</label>
            </div>
            [#-- Image --]
            <div class="form-group text-center" style="display:none">
            [#-- src="${baseUrl}/global/images/crps/${(element.acronym)!'default'}.png" --]
              <img id="crpSelectedImage"  width="300px" src="${baseUrl}/global/images/crps/${(element.acronym)!'A4NH'}.png" alt="${(element.name)!}" />
            </div>
            [#-- Welcome info --]
            <div class="row">
              <div class="col-sm-10 welcome-message-container" style="display:none">
                <span class="login-input-container welcome-message">Welcome:</span>
                <br>
                <span class="login-input-container username"><i class="glyphicon glyphicon-triangle-left"></i>John Doe</span>
              </div>
            </div>
          </div>
        </div>
        [#-- Password --]
        <div class="row" >
          <div class="col-sm-10">
            <div class="login-input-container" id="login-password" style="display:none">
              <input class="login-input" type="password" name="login-password" tabindex=2 required/>
              <label for="login-password">Password</label>
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
  <li id="crp-${element.acronym}" class="option ${element.login?string('enabled', 'disabled')}" title="${element.login?string('', 'Coming soon...')}">
    <img class="${element.login?string('animated bounceIn', '')}" src="${baseUrl}/global/images/crps/${element.acronym}.png" alt="${element.name}" tabindex=1/>
  </li>
[/#macro]