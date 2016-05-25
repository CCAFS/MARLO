[#ftl] 
<div id="loginFormContainer">
  [#if !config.production]
  <div class="loginForm instructions">
    [#-- @s.text name="home.login.message.nonCgiar" / --]
    <p>Thank you for being part of the <strong>exclusive group of testers</strong>; you are in the right place! Please enter your credentials below.</p>
  </div>
  [/#if]
  [@s.form method="POST" namespace="/" action="login" cssClass="loginForm"]
    [@s.fielderror cssClass="fieldError" fieldName="loginMessage"/]
    <div class="crpGroup">
      
    </div>
    [@customForm.input name="user.email" i18nkey="home.login.email" required=true /]
    [@customForm.input name="user.password" i18nkey="home.login.password" required=true type="password" /]
    [@s.submit key="home.login.button" name="login" /]      
  [/@s.form]
</div><!-- End loginFormContainer -->