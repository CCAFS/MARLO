[#ftl]
[#assign title = "MARLO Study" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-${(expectedStudy.id)!}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2" ,"blueimp-file-upload", "flag-icon-css" ] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/projectStudy.js?20210127",
  "${baseUrlCdn}/global/js/fieldsValidation.js"
] /]
[#assign customCSS = [
  "${baseUrlMedia}/css/projects/projectStudies.css?20230106"
  ] 
/]
[#assign currentSection = "additionalReporting" /]

[#assign breadCrumb = [
  {"label":"publicationsList", "nameSpace":"publications", "action":"${crpSession}/publicationsList"},
  {"label":"publication", "nameSpace":"publications", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/studiesTemplates.ftl" as studies /]
[#import "/WEB-INF/global/macros/utils.ftl" as utils /]

[#assign customName = "study" /]

    
<section class="container">
  <div class="col-md-1"></div>
  [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
  <div class="col-md-10">
    [#-- Messages --]
    [#include "/WEB-INF/crp/views/publications/messages-publications.ftl" /]
    
    [#-- Back --]
    <small class="pull-right">
      <a href="[@s.url action='${crpSession}/studiesList'][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
        <span class="glyphicon glyphicon-circle-arrow-left"></span> Back to Studies list
      </a>
    </small>
    <br />
    
    <h3 class="headTitle"> Study Information</h3> 
    <div class="">
      [@studies.studyMacro element=expectedStudy name="expectedStudy" index=0  fromProject=false/]
    </div>
 
    [#-- Section Buttons & hidden inputs--]
    [#include "/WEB-INF/crp/views/studies/buttons-studies.ftl" /]
  </div>
  [/@s.form]
  <div class="col-md-1"></div>
</section>


[#include "/WEB-INF/global/pages/footer.ftl"]