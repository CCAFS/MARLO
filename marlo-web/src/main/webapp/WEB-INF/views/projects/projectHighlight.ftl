[#ftl] 
[#assign title = "Project Highlight" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${highlight.id}" /]
[#assign pageLibs = ["select2", "blueimp-file-upload"] /]
[#assign customJS = ["${baseUrl}/js/projects/projectHighlight.js", "${baseUrl}/js/global/autoSave.js", "${baseUrl}/js/global/fieldsValidation.js"] /]
[#assign customCSS = ["${baseUrl}/css/projects/projectHighlights.css"] /]
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
    <img class="col-md-2" src="${baseUrl}/images/global/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="projectHighlight.help" /] </p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
</div>
    
<section class="container">
  <div class="row">
    [#-- Project Menu --]
    <div class="col-md-3">
      [#include "/WEB-INF/views/projects/menu-projects.ftl" /]
    </div>
    [#-- Project Section Content --]
    <div class="col-md-9">
    [#-- Section Messages --]
    [#include "/WEB-INF/views/projects/messages-highlight.ftl" /]

      [@s.form action=actionName cssClass="pure-form" method="POST" enctype="multipart/form-data" ]
        
        

        [#-- Back --]
        <small class="pull-right">
          <a href="[@s.url action='${crpSession}/highlights'][@s.param name="projectID" value=project.id /][/@s.url]">
            <span class="glyphicon glyphicon-circle-arrow-left"></span> Back to the project highlights
          </a>
        </small>
        
        [#--  Highlight Information --]
        <h3 class="headTitle">[@s.text name="projectHighlight.information" /]</h3> 
        <div id="highlight-information" class="borderBox clearfix">
          
          [#-- Title --]
          <div class="form-group">
            [@customForm.input name="highlight.title" type="text" i18nkey="highlight.title" editable=editable required=true  /]
          </div> 
          
          <div class="form-group">
            <div class="row">
              [#-- Author --]
              <div class="col-md-6" >
                [@customForm.input name="highlight.author" type="text" i18nkey="highlight.author" editable=editable  required=true  /]
              </div> 
              [#-- Subject --]
              <div class="col-md-6" >
                [@customForm.input name="highlight.subject"  type="text" i18nkey="highlight.subject" help="highlight.subject.help" editable=editable /] 
              </div> 
            </div>
          </div>
    
          <div class="form-group">
            <div class="row">
              [#-- Publisher --]
              <div class="col-md-6" >
                [@customForm.input name="highlight.publisher" type="text" i18nkey="highlight.publisher" help="highlight.publisher.help" editable=editable /]   
              </div>
              [#-- Year --]
              <div class="col-md-6">
                [@customForm.select name="highlight.year" value="${(highlight.year)!currentCycleYear}" i18nkey="highlight.year" listName="allYears" editable=editable header=false stringKey=true required=false  /]
                [#if !editable]${(highlight.year)!}[/#if]
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
                  [#assign hasFile = highlight.file?? && highlight.file.id?? /]
                  <input id="fileID" type="hidden" name="highlight.file.id" value="${(highlight.file.id)!}" />
                  [#-- Input File --]
                  [#if editable]
                  <div class="fileUpload" style="display:${hasFile?string('none','block')}"> <input class="upload" type="file" accept="image/*" name="file" data-url="${baseUrl}/uploadFundingSource.do"></div>
                  [/#if]
                  [#-- Uploaded File --]
                  <p class="fileUploaded textMessage checked" style="display:${hasFile?string('block','none')}">
                    <span class="contentResult">[#if highlight.file??]${(highlight.file.fileName)!('No file name')} [/#if]</span> 
                    [#if editable]<span class="removeIcon"> </span> [/#if]
                  </p>
                </div>
                [#-- Show highlight image --]
                <div id="highlight.image" class="image">
                 [#if hasFile]
                   <img src="${(highlightsImagesUrl)!baseUrl}${(highlight.file.fileName)!'images/global/defaultImage.png'}" width="100%">
                 [#else]
                   <img src="${baseUrl}/${(highlight.file.fileName)!'images/global/defaultImage.png'}" width="100%">
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
                [@customForm.input name="highlight.startDate" className="startDate" type="text" i18nkey="highlight.startDate" editable=editable/]
              </div> 
              [#-- End Date --]
              <div class="col-md-6">
                [@customForm.input name="highlight.endDate" className="endDate" type="text" i18nkey="highlight.endDate" editable=editable/]
              </div>
            </div>
          </div>
          
          [#-- Is global --]
          <div class="form-group">
            <div class="row">
              <div class="col-md-6">
          
                [@customForm.checkbox  name="highlight.global" className="isGlobal" i18nkey="highlight.isGlobal" checked=(highlight.global)!false value="true" editable=editable/]
              </div>
            </div>
          </div>
          
          [#-- Countries --]
          <div class="form-group countriesBlock chosen ${customForm.changedField('highlight.countries')}" style="display:${((highlight.global)!false)?string('none','block')}" >
            [#if editable]
              [@customForm.select name="highlight.countriesIds" label="" i18nkey="highlight.countries" listName="countries" keyFieldName="id"  displayFieldName="name" value="highlight.countriesIds" multiple=true disabled="${(highlight.global?string(1, 0))!0}"/]              
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
            [@customForm.input name="highlight.keywords" type="text" i18nkey="highlight.keywords" help="highlight.keywords.help" editable=editable/]
          </div>
    
          [#-- Description --]
          <div class="form-group">
            [@customForm.textArea name="highlight.description" className="limitWords-300" i18nkey="highlight.descripition" editable=editable/]
          </div>
    
          [#-- Objectives --]
          <div class="form-group">
            [@customForm.textArea name="highlight.objectives" className="limitWords-100" i18nkey="highlight.objectives" editable=editable/]
          </div>
    
          [#-- Result --]
          <div class="form-group">
            [@customForm.textArea name="highlight.results" className="limitWords-300" i18nkey="highlight.results" editable=editable/]
          </div>
    
          [#-- Partners --]
          <div class="form-group">
            [@customForm.textArea name="highlight.partners" className="limitWords-300" i18nkey="highlight.partners" editable=editable/]
          </div>
    
          [#-- Links / resources --]
          <div class="form-group">
            [@customForm.textArea name="highlight.links" i18nkey="highlight.links" editable=editable/]
          </div>
          
        </div>
        
        [#-- Section Buttons & hidden inputs--]
        [#include "/WEB-INF/views/projects/buttons-highlight.ftl" /]
       
      [/@s.form] 
    </div>
    
    
  </div>  
</section>
  
[#-- File upload template --]
[@customForm.inputFile name="file" template=true /] 

[#include "/WEB-INF/global/pages/footer.ftl"]