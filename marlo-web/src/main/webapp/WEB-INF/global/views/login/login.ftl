[#ftl]
[#assign title = "Welcome to AICCRA" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrlCdn}/global/js/login/login.js?20230712"] /]
[#assign currentSection = "home" /]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<section> 
  <div class="container loginPage">
    <div class="form-group row">
      <div class="col-md-10 col-center">
        <h3 class="">[@s.text name="login.marloTitle" /]</h3>
        <p class="text-justify login-description">[@s.text name="login.marloDescription" /]</p>
        [#if !config.production]
          <br />
          <div class="note alert alert-warning" role="alert">
            [#-- <p class="text-justify">[@s.text name="login.testersMessage2"/]</p>--]
            <p class="text-justify">[@s.text name="login.testersMessage3"/]</p>
          </div>
        [/#if]
      </div>
      [#-- Login Form --]
      <div class="row">
        <div class=" col-md-offset-3 col-xs-11 col-sm-7 col-md-5 col-center">
          [#include "/WEB-INF/global/pages/loginForm.ftl" /]
        </div>
      </div>
    </div>
    [#if false]
    [#-- Participating CRPS --]
    <br>
    <div class="row left-margin">
      <div class="col-md-12 col-center">
        <label>[@s.text name="login.participating.platforms"/]:</label>    
        <div class="login-logos-container">
          <ul>
          [#attempt] 
            [#assign crpList = action.getCrpCategoryList("1") /]
          [#recover]
            [#assign crpList = [] /]
          [/#attempt]
          [#if crpList?has_content]
            [#list crpList as crp]
              [@crpItem element=crp /]
            [/#list]
          [#else]
            <p>[@s.text name="login.participating.void.crps" /]</p>
          [/#if]
          </ul>
        </div>
      </div>
    </div>
    [#-- CENTERS --]
    <br>
    <div class="row left-margin">
      <div class="col-md-offset-3 col-md-2">
        <label>[@s.text name="login.participating.centers"/]:</label>
        <div class="login-logos-container">
          <ul>
          [#attempt] 
            [#assign centerList = action.getCrpCategoryList("5") /]
          [#recover]
            [#assign centerList = [] /]
          [/#attempt]
          [#if centerList?has_content]
            [#list centerList as center]
              [@crpItem element=center /]
            [/#list]
          [#else]
            <p>[@s.text name="login.participating.void.centers" /]</p>
          [/#if]
          </ul>
        </div>
      </div>
      [/#if]
      [#-- and PLATFORMS --]
      <div class="col-md-4">
        <label>[@s.text name="login.platforms"/]:</label>
        <div class="login-logos-container">
          <ul>
            [#attempt] 
              [#assign platformsList = action.getCrpCategoryList("3") /]
            [#recover]
              [#assign platformsList = [] /]
            [/#attempt]
            [#if platformsList?has_content]
              [#list platformsList as platform]
                [@crpItem element=platform /]
              [/#list]
            [#else]
              <p>[@s.text name="login.participating.void.platforms" /]</p>
            [/#if]
          </ul>
        </div>
      </div>
    </div>  
  </div>
</section>


[#macro crpItem element] 
  [#if element.marlo && element.login]
    [#if element.id != 17 && element.id != 27]
      <li id="crp-${element.acronym}" title="${element.login?string('', 'Coming soon...')}">
        <img class="${element.login?string('animated bounceIn', '')}" src="${baseUrlCdn}/global/images/crps/${element.acronym}.png" alt="${element.name}" />
      </li>
    [/#if]
  [/#if]
[/#macro]

[#include "/WEB-INF/global/pages/footer.ftl" /]
