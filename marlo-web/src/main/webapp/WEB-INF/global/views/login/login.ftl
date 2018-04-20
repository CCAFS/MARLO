[#ftl]
[#assign title = "Welcome to MARLO" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrl}/global/js/login/login.js?20181104"] /]
[#assign currentSection = "home" /]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<section> 
  <div class="container loginPage">
    <div class="form-group row">
      <div class="col-md-10 col-center">
        <h3 class="">[@s.text name="login.marloTitle" /]</h3>
        <p class="text-justify login-description">[@s.text name="login.marloDescription" /]  </p>
        [#if !config.production]
          <br />
          <div class="note alert alert-warning" role="alert">
            <p class="text-justify">[@s.text name="login.testersMessage2"/]</p>
            <p class="text-justify">[@s.text name="login.testersMessage3"/]</p>
          </div>
        [/#if]
      </div>
      <div class="row">
        <div class=" col-md-offset-3 col-xs-7 col-sm-6 col-sm-6 col-md-5 col-center">
          [#include "/WEB-INF/global/pages/loginForm.ftl" /]
        </div>
      </div>
    </div>
    [#-- CRPS --]
    <br>
    <div class="row left-margin">
      <div class="col-md-12 col-center">
        <label>Participating CRPs:</label>
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
            <p>Not CRPs loaded</p>
          [/#if]
          </ul>
        </div>
      </div>
    </div>
    [#-- CENTERS --]
    <br>
    <div class="row left-margin">
      <div class="col-md-offset-3 col-md-2">
        <label>Centers:</label>
        <div class="login-logos-container">
          <ul>
          [#attempt] 
            [#assign centerList = action.getCrpCategoryList("4") /]
          [#recover]
            [#assign centerList = [] /]
          [/#attempt]
          [#if centerList?has_content]
            [#list centerList as center]
              [@crpItem element=center /]
            [/#list]
          [#else]
            <p>Not Centers loaded</p>
          [/#if]
          </ul>
        </div>
      </div>
      [#-- PLATFORMS --]
      <div class="col-md-4">
        <label>Platforms:</label>
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
              <p>Not Platforms loaded</p>
            [/#if]
          </ul>
        </div>
      </div>
    </div>  
  </div>
</section>


[#macro crpItem element]
  <li id="crp-${element.acronym}" title="${element.login?string('', 'Coming soon...')}" >
    <img class="${element.login?string('animated bounceIn', '')}" src="${baseUrl}/global/images/crps/${element.acronym}.png" alt="${element.name}" />
  </li>
[/#macro]

[#include "/WEB-INF/global/pages/footer.ftl" /]