[#ftl] 
[#assign title = "Project Highlight" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${highlight.id}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2", "blueimp-file-upload"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/projectHighlight.js?20220111a", 
  "${baseUrlCdn}/global/js/autoSave.js", 
  "${baseUrlCdn}/global/js/fieldsValidation.js"
  ] 
/]
[#assign customCSS = ["${baseUrlMedia}/css/projects/projectHighlights.css"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "highlights" /]
[#assign hideJustification = true /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"projectHighlights", "nameSpace":"/projects", "action":"${(crpSession)!}/highlights", "param" : "projectID=${projectID}"},
  {"label":"projectHighlight", "nameSpace":"/projects", "action":""}
] /]


[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]



<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrlCdn}/global/images/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="projectHighlight.help" /] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>

[#if (!availabePhase)!false]
  [#include "/WEB-INF/crp/views/projects/availability-projects.ftl" /]
[#else]
<section class="container">
  <div class="row">
    [#-- Project Menu --]
    <div class="col-md-3">
      [#include "/WEB-INF/crp/views/projects/menu-projects.ftl" /]
    </div>
    [#-- Project Section Content --]
    <div class="col-md-9">
    [#-- Section Messages --]
    [#include "/WEB-INF/crp/views/projects/messages-highlight.ftl" /]

      [@s.form action=actionName cssClass="pure-form" method="POST" enctype="multipart/form-data" ]
        
        

        [#-- Back --]
        <small class="pull-right">
          <a href="[@s.url action='${crpSession}/highlights'][@s.param name="projectID" value=project.id /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
            <span class="glyphicon glyphicon-circle-arrow-left"></span> Back to the project highlights
          </a>
        </small>
        
        [#--  Highlight Information --]
        <h3 class="headTitle">[@s.text name="projectHighlight.information" /]</h3> 
        <div id="highlight-information" class="borderBox clearfix">
          <div id="isEditable" style="display: none;">${(editable!false)?c}</div>

          [#-- Title --]
          <div class="form-group">
            [@customForm.input name="highlight.projectHighlightInfo.title" type="text" i18nkey="highlight.title" editable=editable required=true  /]
          </div> 
          
          <div class="form-group">
            <div class="row">
              [#-- Author --]
              <div class="col-md-6" >
                [@customForm.input name="highlight.projectHighlightInfo.author" type="text" i18nkey="highlight.author" editable=editable  required=true  /]
              </div> 
              [#-- Subject --]
              <div class="col-md-6" >
                [@customForm.input name="highlight.projectHighlightInfo.subject"  type="text" i18nkey="highlight.subject" help="highlight.subject.help" editable=editable /] 
              </div> 
            </div>
          </div>
    
          <div class="form-group">
            <div class="row">
              [#-- Publisher --]
              <div class="col-md-6" >
                [@customForm.input name="highlight.projectHighlightInfo.publisher" type="text" i18nkey="highlight.publisher" help="highlight.publisher.help" editable=editable /]   
              </div>
              [#-- Year --]
              <div class="col-md-6">
                <label for="">[@s.text name="highlight.year" /]:</label>
                [#assign highlightYear = (highlight.projectHighlightInfo.year)!actualPhase.year ]
                <p>${highlightYear}</p>
                <input type="hidden" name="highlight.projectHighlightInfo.year" value="${highlightYear}" />
              </div>
            </div>
          </div>
         
          <br />
          <div class="form-group">
            <div class="row"> 
              [#-- Types --]
              <div class="col-md-6 highlightsTypes ${customForm.changedField('highlight.types')}">
                <label for="highlight.types">[@s.text name="highlight.types" /]<span class="red">*</span></label>
                <div class="checkboxGroup">
                [#if editable]
                  [@s.fielderror cssClass="fieldError" fieldName="highlight.typesIds"/]
                  [@s.checkboxlist name="highlight.typesids" list="highlightsTypes" value="highlight.typesids" itemKey="id"  cssClass="" /]
                [#else]
                  [#if highlight.typesIds?has_content]
                    [#list highlight.typesIds as element]<p class="checked">${element.description}</p>[/#list]
                  [#else]
                    <div class="select"><p>Field is empty</p></div>
                  [/#if]
                [/#if]
                </div>
              </div>
              
              [#-- Image --]
              <div class="col-md-6 imageBlock">
                [#-- Upload highlight image --]
                <div class="form-group fileUploadContainer">
                  <label for="highlight.image">[@customForm.text name="highlight.image" readText=!editable /]:</label>
                  [#assign hasFile = highlight.projectHighlightInfo.file?? && highlight.projectHighlightInfo.file.id?? /]
                  <input id="fileID" type="hidden" name="highlight.projectHighlightInfo.file.id" value="${(highlight.projectHighlightInfo.file.id)!}" />
                  [#-- Input File --]
                  [#if editable]
                  <div class="fileUpload" style="display:${hasFile?string('none','block')}"> <input class="upload" type="file" accept="image/*" name="file" data-url="${baseUrl}/uploadFundingSource.do"></div>
                  [/#if]
                  [#-- Uploaded File --]
                  <p class="fileUploaded textMessage checked" style="display:${hasFile?string('block','none')}">
                    <span class="contentResult">[#if highlight.projectHighlightInfo.file??]${(highlight.projectHighlightInfo.file.fileName)!('No file name')} [/#if]</span> 
                    [#if editable]<span class="removeIcon"> </span> [/#if]
                  </p>
                </div>
                [#-- Show highlight image --]
                <div id="highlight.image" class="image">
                 [#if hasFile]
                   <img src="${(highlightsImagesUrl)!baseUrl}${(highlight.projectHighlightInfo.file.fileName)!'global/images/defaultImage.png'}" width="100%">
                 [#else]
                   <img src="${baseUrl}/${(highlight.projectHighlightInfo.file.fileName)!'global/images/defaultImage.png'}" width="100%">
                 [/#if]
                </div>
                <div class="clear"></div>
              </div>
              
            </div>
          </div>
          <br />
          
          <div class="form-group">
            <div class="row">
              [#-- Start Date --]
              <div class="col-md-6">
                [@customForm.input name="highlight.projectHighlightInfo.startDate" className="startDate" type="text" i18nkey="highlight.startDate" editable=editable/]
              </div> 
              [#-- End Date --]
              <div class="col-md-6">
                [@customForm.input name="highlight.projectHighlightInfo.endDate" className="endDate" type="text" i18nkey="highlight.endDate" editable=editable/]
              </div>
            </div>
          </div>
          
          [#-- Is global --]
          <div class="form-group">
            <div class="row">
              <div class="col-md-6">
                <div id="isGlobalValue" style="display: none;">${(highlight.projectHighlightInfo.global!false)?c}</div>
                [@customForm.checkbox  name="highlight.projectHighlightInfo.global" className="isGlobal" i18nkey="highlight.isGlobal" checked=(highlight.projectHighlightInfo.global)!false value="true" editable=editable/]
              </div>
            </div>
          </div>
          
          [#-- Countries --]
          <div class="form-group countriesBlock chosen ${customForm.changedField('highlight.countries')}" style="display:${((highlight.projectHighlightInfo.global)!false)?string('none','block')}" >
            [#if editable]
              [@customForm.select name="highlight.countriesIds" label="" i18nkey="highlight.countries" listName="countries" keyFieldName="id"  displayFieldName="name" value="highlight.countriesIds" multiple=true /]              
            [#else]
              <label>[@s.text name="highlight.countries" /]:</label>
              <div class="select">
              [#if highlight.countries?has_content]
                [#list highlight.countries as element]
                 <p class="checked">${element.locElement.name}</p>
                [/#list]
              [#else]
                <p>Field is empty</p>
              [/#if]
              </div>
            [/#if]
          </div>
    
          [#-- Keywords --]
          <div class="form-group">
            [@customForm.input name="highlight.projectHighlightInfo.keywords" type="text" i18nkey="highlight.keywords" help="highlight.keywords.help" editable=editable/]
          </div>
    
          [#-- Description --]
          <div class="form-group">
            [@customForm.textArea name="highlight.projectHighlightInfo.description" className="limitWords-300" i18nkey="highlight.descripition" editable=editable/]
          </div>
    
          [#-- Objectives --]
          <div class="form-group">
            [@customForm.textArea name="highlight.projectHighlightInfo.objectives" className="limitWords-100" i18nkey="highlight.objectives" editable=editable/]
          </div>
    
          [#-- Result --]
          <div class="form-group">
            [@customForm.textArea name="highlight.projectHighlightInfo.results" className="limitWords-300" i18nkey="highlight.results" editable=editable/]
          </div>
    
          [#-- Partners --]
          <div class="form-group">
            [@customForm.textArea name="highlight.projectHighlightInfo.partners" className="limitWords-300" i18nkey="highlight.partners" editable=editable/]
          </div>
    
          [#-- Links / resources --]
          <div class="form-group">
            [@customForm.textArea name="highlight.projectHighlightInfo.links" i18nkey="highlight.links" editable=editable/]
          </div>
          
        </div>
        
        [#-- Section Buttons & hidden inputs--]
        [#include "/WEB-INF/crp/views/projects/buttons-highlight.ftl" /]
       
      [/@s.form] 
    </div>
    
    
  </div>  
</section>
[/#if]
  
[#-- File upload template --]
[@customForm.inputFile name="file" template=true /] 

[#include "/WEB-INF/global/pages/footer.ftl"]