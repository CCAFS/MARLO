[#ftl] 
<div id="loginFormContainer">
  [#if !config.production]
  <div class="loginForm instructions">
    [#-- @s.text name="home.login.message.nonCgiar" / --]
    <p>[@s.text name="login.testersMessage"/]</p>
  </div>
  [/#if]
  [@s.form method="POST" namespace="/" action="login" cssClass="loginForm"]
    [#-- Login error message --]
    [@s.fielderror cssClass="fieldError" fieldName="loginMessage"/]
    [#-- CRPs --]
    <div class="crpGroup form-group clearfix">
      <label for="crp">[@s.text name="login.crp" /]:<span class="red">*</span></label>
      <ul>
        [#list crpList as crp]
          <li id="crp-${crp.acronym}"><img src="${baseUrl}/images/global/crps/${crp.acronym}.png" alt="${crp.acronym}" /></li>
        [/#list]
      </ul>
      <input type="hidden" id="crp" name="crp" value="-1" />
    </div>
    [#-- Email --]
    [@customForm.input name="user.email" i18nkey="login.email" required=true /]
    [#-- Password --]
    [@customForm.input name="user.password" i18nkey="login.password" required=true type="password" /]
    [#-- Login (Submit button) --]
    <div class="center">[@s.submit key="login.button" name="login" /]</div>
  [/@s.form]
  <br />
  [#-- Disclaimer --]
  <div class="alert alert-warning" role="alert">[@s.text name="login.disclaimer"/]</div>
</div><!-- End loginFormContainer -->