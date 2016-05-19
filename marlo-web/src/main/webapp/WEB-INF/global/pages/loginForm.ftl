[#ftl] 
<div id="loginFormContainer">
  [#if !config.production]
  <div class="loginForm instructions">
    [#-- @s.text name="home.login.message.nonCgiar" / --]
    <p>Thank you for being part of the <strong>exclusive group of testers</strong>; you are in the right place! Please enter your credentials below.</p>
    <p>Should you not be part of the group of testers, please go to the temporary CCAFS P&R by clicking <a href="http://davinci.ciat.cgiar.org/ip">here</a></p>
  </div>
  [/#if]
  [@s.fielderror cssClass="fieldError" fieldName="loginMessage"/]
  [@s.form method="POST" namespace="/" action="login" cssClass="loginForm pure-form"]
    [@customForm.input name="user.email" i18nkey="home.login.email" required=true /]
    [@customForm.input name="user.password" i18nkey="home.login.password" required=true type="password" /]
    [@s.submit key="home.login.button" name="login" /]      
  [/@s.form]
</div><!-- End loginFormContainer -->