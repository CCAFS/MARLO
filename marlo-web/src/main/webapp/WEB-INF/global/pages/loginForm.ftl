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
    <div class="firstForm crpGroup form-group row" style="display:${(crpSession?has_content)?string('none', 'block')}">
      [#-- CRPs --]
      <div class="col-md-4 animated bounceIn">
        <h4 class="headTitle ">CRPs</h4>
        <ul>
        [#if crpList?has_content]
          [#list crpList as crp]
            <li id="crp-${crp.acronym}" class="loginOption [#if crpSession?? && (crp.acronym == crpSession)]selected[/#if]"><img src="${baseUrl}/images/global/crps/${crp.acronym}.png" alt="${crp.acronym}" /></li>
          [/#list]
        [#else]
          <p>Not CRPs loaded</p>
        [/#if]
        </ul>
      </div>
      [#-- Platforms --]
      <div class="col-md-4 animated bounceIn">
        <h4 class="headTitle ">Platforms</h4>
        <ul>
        [#assign platformsList = [
          {"acronym": "bigData"},
          {"acronym": "breeding"}, 
          {"acronym": "genebank"}
        ] /]
        [#if platformsList?has_content]
          [#list platformsList as crp]
            <li id="crp-${crp.acronym}" class="loginOption [#if crpSession?? && (crp.acronym == crpSession)]selected[/#if]"><img src="${baseUrl}/images/global/crps/${crp.acronym}.png" alt="${crp.acronym}" /></li>
          [/#list]
        [#else]
          <p>Not Platforms loaded</p>
        [/#if]
        </ul> 
      </div>
      [#-- Centers --]
      <div class="col-md-4 animated bounceIn">
        <h4 class="headTitle ">Centers</h4>
        <ul>
        [#assign centersList = [{"acronym": "ciat"}] /]
        [#if centersList?has_content]
          [#list centersList as crp]
            <li id="crp-${crp.acronym}" class="loginOption [#if crpSession?? && (crp.acronym == crpSession)]selected[/#if]"><img src="${baseUrl}/images/global/crps/${crp.acronym}.png" alt="${crp.acronym}" /></li>
          [/#list]
        [#else]
          <p>Not Centers loaded</p>
        [/#if]
        </ul>
      </div>
      
    </div>
    <div class="secondForm" style="display:${(crpSession?has_content)?string('block', 'none')}">
      <div class="form-group row">
        <div class="col-md-2"></div>
        <div class="col-md-8">
          <div class="row">
            <br />
            [#-- Image --]
            <div class="col-md-6 text-center">
              [#-- Go back --]
              <img id="crpSelectedImage"  width="100%" src="${baseUrl}/images/global/crps/${(crpSession)!'default'}.png" alt="${(crpSession)!}" />
              <br />
              <br />
              <a class="goBackToSelect" href="#">Select another (CRP, Center or Platform)</a>
            </div>
            [#-- Form --]
            <div class="col-md-6">
              [#-- CRP Session --]
              <input type="hidden" id="crp" name="crp" value="${(crpSession)!}" />
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
          </div>
          <br />
        </div>
        <div class="col-md-2"></div>
      </div>
    </div>
  [/@s.form]
  <br />
  [#-- Disclaimer --]
  <div class="alert alert-warning" role="alert">[@s.text name="login.disclaimer"/]</div>
  
</div><!-- End loginFormContainer -->