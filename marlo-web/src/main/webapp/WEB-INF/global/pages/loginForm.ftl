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
    <div class="firstForm  form-group row" style="display:${(crpSession?has_content)?string('none', 'block')}">
      <ul class="nav nav-tabs" role="tablist">
        <li role="presentation" class="active"><a href="#crps" aria-controls="home" role="tab" data-toggle="tab">CRPs</a></li>
        <li role="presentation"><a href="#platforms" aria-controls="profile" role="tab" data-toggle="tab">Platforms</a></li>
        <li role="presentation"><a href="#centers" aria-controls="messages" role="tab" data-toggle="tab">Centers</a></li> 
      </ul>
      
      <div class="crpGroup tab-content">
      
        [#-- CRPs --]
        <div role="tabpanel" id="crps" class="tab-pane active col-md-12 animated bounceIn">
           
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
        <div id="platforms" class="tab-pane col-md-12 animated bounceIn">
          
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
        <div id="centers" class="tab-pane col-md-12 animated bounceIn">
           
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
    </div>
    <div class="secondForm" style="display:${(crpSession?has_content)?string('block', 'none')}">
      <div class="form-group row">
        
        <div class="col-md-12">
          <div class="row">
            <br />
             
            [#-- Form --]
            <div class="col-md-12 text-center">
              [#-- Image --]
              <img id="crpSelectedImage"  width="100%" src="${baseUrl}/images/global/crps/${(crpSession)!'default'}.png" alt="${(crpSession)!}" />
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
              <br />
              <br />
              [#-- Go back --]
              <a class="goBackToSelect" href="#">Select another (CRP, Center or Platform)</a>
            </div>
          </div>
          <br />
        </div>
        
      </div>
    </div>
  [/@s.form]
  <br />
  [#-- Disclaimer --]
  <div class="alert alert-warning" role="alert">[@s.text name="login.disclaimer"/]</div>
  
</div><!-- End loginFormContainer -->