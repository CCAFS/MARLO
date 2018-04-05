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
        [#if listGlobalUnitTypes??]
          [#assign typesSize = 100 / listGlobalUnitTypes?size ]
          [#list listGlobalUnitTypes as globalUnitType]
            <li id="${globalUnitType.id}" role="presentation" style="width:${typesSize}%;" class="type-${globalUnitType.id} [#if (typeSession == globalUnitType.id)!false]active[/#if]">
              <a href="#globalUnit-${globalUnitType.id}" role="tab" data-toggle="tab"> ${globalUnitType.name}</a>
            </li>
          [/#list]
        [/#if]
      </ul>
      
      <div class="crpGroup tab-content">
        [#list listGlobalUnitTypes as globalUnitType]
          <div role="tabpanel" id="globalUnit-${globalUnitType.id}" class="tab-pane type-${globalUnitType.id} [#if (typeSession == globalUnitType.id)!false]class="active"[/#if] col-sm-12">
            <ul>
            [#if globalUnitType.globalUnitsList?has_content]
              [#list globalUnitType.globalUnitsList as globalUnit]
                [#if globalUnit.login][@crpItem element=globalUnit /][/#if]
              [/#list]
            [#else]
              <p>Not ${globalUnitType.name} loaded</p>
            [/#if]
            </ul>
          </div>
        [/#list]
      </div>
      
    </div>
    <div class="secondForm" style="display:${(crpSession?has_content)?string('block', 'none')}">
      <div class="row">
        [#-- Form --]
        <div class="col-sm-12">
          [#-- Image --]
          <div class="form-group text-center">
            <img id="crpSelectedImage"  width="300px" src="${baseUrl}/global/images/crps/${(crpSession)!'default'}.png" alt="${(crpSession)!}" />
          </div>
          [#-- CRP Session --]
          <input type="hidden" id="crp-input" name="crp" value="${(crpSession)!}" />
          [#-- Global Unit Session --]
          <input type="hidden" id="globalUnit-input" name="globalUnit" value="${(currentCrp.id)!}" />
          [#-- Type Session --]
          <input type="hidden" id="type-input" name="type" value="${(typeSession)!}" />
          [#-- Email --]
          <div class="form-group text-left">
            [@customForm.input name="user.email" i18nkey="login.email" required=true /]
          </div>
          [#-- Password --]
          <div class="form-group text-left">
            [@customForm.input name="user.password" i18nkey="login.password" required=true type="password" /]
          </div>
          [#-- Login (Submit button) --]
          <div class="text-center">
            [@s.submit key="login.button" name="login" /]
          </div>
         
          <br />
          [#-- Go back --]
          <div class="text-center">
            <a class="goBackToSelect" href="#"><span class="glyphicon glyphicon-menu-down"></span> Select another (CRP, Center or Platform)</a>
          </div>
        </div>
      </div>
    </div>
  [/@s.form]
  <br />
  [#-- Disclaimer --]
  <div class="alert alert-warning" role="alert">[@s.text name="login.disclaimer"/]</div>
  
</div><!-- End loginFormContainer -->

[#macro crpItem element]
  <li id="crp-${element.acronym}" class="loginOption globalUnitID-${element.id} ${element.login?string('enabled', 'disabled')} [#if crpSession?? && (element.acronym == crpSession)]selected[/#if]" title="${element.login?string('', 'Coming soon...')}">
    <img class="${element.login?string('animated bounceIn', '')} " src="${baseUrl}/global/images/crps/${element.acronym}.png" alt="${element.name}" />
  </li>
[/#macro]