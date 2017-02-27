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
    <div class="crpGroup form-group animated bounceIn clearfix">
      <div class="alert alert-warning" role="alert"> We are currently experiencing technical difficulties. As a result, MARLO users with CGIAR credentials may not be able to login to the system. The MARLO technical team is working on resolving the issue. Please check back to see if the situation has been resolved. Thank you!</div>
      
      <h4 class="headTitle text-center">[@s.text name="login.crp" /]</h4>
      <ul>
      [#if crpList?has_content]
        [#list crpList as crp]
          <li id="crp-${crp.acronym}" class="[#if crpSession?? && (crp.acronym == crpSession)]selected[/#if]"><img src="${baseUrl}/images/global/crps/${crp.acronym}.png" alt="${crp.acronym}" /></li>
        [/#list]
      [#else]
        <p>Not CRPs loaded</p>
      [/#if]
      </ul>
      <input type="hidden" id="crp" name="crp" value="${(crpSession)!}" />
    </div>
    <div class="secondForm" style="display:${(crpSession?has_content)?string('block', 'none')}">
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