[#ftl] 
<div id="loginFormContainer">
  [#if !config.production]
  <div class="loginForm instructions">
    [#-- @s.text name="home.login.message.nonCgiar" / --]
    <p>Thank you for being part of the <strong>exclusive group of testers</strong>; you are in the right place! Please enter your credentials below.</p>
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
    [@s.submit key="login.button" name="login" /]      
  [/@s.form]
</div><!-- End loginFormContainer -->