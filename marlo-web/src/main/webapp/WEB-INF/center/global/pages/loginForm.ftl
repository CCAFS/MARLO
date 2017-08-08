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
    <div class="crpGroup form-group animated bounceIn clearfix">
      <h4 class="headTitle text-center">[@s.text name="login.crp" /]</h4>
    </div>
    <div class="secondForm" style="display:block">
      [#-- Email --]
      <div class="form-group">
        [@customForm.input name="user.email" i18nkey="login.email" required=true /]
      </div>
      [#-- Password --]
      <div class="form-group">
        [@customForm.input name="user.password" i18nkey="login.password" required=true type="password" /]
      </div>
      [#-- Login (Submit button) --]
      <div class="center">[@s.submit key="login.button" name="login" /]</div>
    </div>
  [/@s.form]
  <br />
  [#-- Disclaimer --]
  <div class="alert alert-warning" role="alert">[@s.text name="login.disclaimer"/]</div>
</div><!-- End loginFormContainer -->