[#ftl]
[#assign title = "Project Highlight" /]
[#assign globalLibs = ["jquery", "noty", "autoSave", "select2"] /]
[#assign customJS = ["${baseUrl}/js/global/utils.js", "${baseUrl}/js/projects/projectHighlight.js"] /]
[#assign currentSection = cycleName?lower_case /]
[#assign currentCycleSection = "projects" /]
[#assign currentStage = "outputs" /]
[#assign currentSubStage = "highlights" /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"${currentSection}", "action":"projectsList"},
  {"label":"projects", "nameSpace":"${currentSection}", "action":"projectsList"},
  {"label":"projectOutputs", "nameSpace":"${currentSection}/projects", "action":"outputs", "param":"projectID=${project.id}"},
  {"label":"projectHighlights", "nameSpace":"${currentSection}/projects", "action":"highlights", "param":"projectID=${project.id}"}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/forms.ftl" as customForm/]

[#import "/WEB-INF/global/macros/usersPopup.ftl" as usersForm/]
[#import "/WEB-INF/global/macros/logHistory.ftl" as log/]
[#import "/WEB-INF/projects/macros/projectDeliverablesTemplate.ftl" as deliverableTemplate/]
    
<section class="content">
  <div class="helpMessage">
    [#-- TODO: Change help text --]
    <img src="${baseUrl}/images/global/icon-help.png" /> <p>[@s.text name="reporting.projectHighlight.help" /]</p>
  </div>
  [#include "/WEB-INF/projects/projectsSubMenu.ftl" /]
  
  [@s.form action="highlight" cssClass="pure-form" method="POST" enctype="multipart/form-data" ]
  <article class="halfContent" id="projectHighlight">  
    [#include "/WEB-INF/projects/dataSheet.ftl" /]
    <br />
    [#-- Informing user that he-she does not have enough privileges to edit. See GrantProjectPlanningAccessInterceptor --]  
    [#if submission?has_content]
      <p class="projectSubmitted">[@s.text name="submit.projectSubmitted" ][@s.param]${(submission.dateTime?date)?string.full}[/@s.param][/@s.text]</p>
    [#elseif !canEdit ]
      <p class="readPrivileges">
        [@s.text name="saving.read.privileges"][@s.param][@s.text name=title /][/@s.param][/@s.text]
      </p>
    [/#if]
    [#--  Highlight Information --] 
    <div id="highlight-information" class="borderBox clearfix">
      [#if !editable && canEdit]
        <div class="editButton"><a href="[@s.url][@s.param name ="highlightID"]${highlight.id}[/@s.param][@s.param name="edit"]true[/@s.param][/@s.url]">[@s.text name="form.buttons.edit" /]</a></div>
      [#else]
        [#if canEdit && !newProject]
          <div class="viewButton"><a href="[@s.url][@s.param name ="highlightID"]${highlight.id}[/@s.param][/@s.url]">[@s.text name="form.buttons.unedit" /]</a></div>
        [/#if]
      [/#if]
      <h1 class="contentTitle">[@s.text name="reporting.projectHighlight.information" /] </h1>  
      [#-- Title --]
      <div class="fullBlock">
        [@customForm.input name="highlight.title" type="text" i18nkey="reporting.projectHighlight.title" editable=editable required=true  /]
      </div> 
      
      <div class="fullPartBlock">
        [#-- Author --]
        <div class="halfPartBlock" >
          [@customForm.input name="highlight.author" type="text" i18nkey="reporting.projectHighlight.author" editable=editable  required=true  /]
        </div>
      
        [#-- Subject --]
        <div class="halfPartBlock" >
          [@customForm.input name="highlight.subject"  type="text" i18nkey="reporting.projectHighlight.subject" help="reporting.projectHighlight.subject.help" editable=editable /] 
        </div> 
      </div>

      <div class="fullPartBlock">
        [#-- Publisher --]
        <div class="halfPartBlock" >
          [@customForm.input name="highlight.publisher" type="text" i18nkey="reporting.projectHighlight.publisher" help="reporting.projectHighlight.publisher.help" editable=editable /]   
        </div>
        [#-- Year --]
        <div class="halfPartBlock">
          [@customForm.select name="highlight.year" value="${(highlight.year)!currentReportingYear}" i18nkey="reporting.projectHighlight.year" listName="allYears" editable=editable stringKey=true required=true  /]
          [#if !editable]${(highlight.year)!}[/#if]
        </div>
      </div>
     
      <br />
      <div class="fullBlock">
        [#-- Types --]
        <div class="halfPartBlock highlightsTypes">
          <h6><label for="highlight.types">[@s.text name="reporting.projectHighlight.types" /]<span class="red">*</span></label></h6>
          <div class="checkboxGroup">
          [#if editable]
            [@s.fielderror cssClass="fieldError" fieldName="highlight.typesIds"/]
            [@s.checkboxlist name="highlight.typesids" list="highlightsTypes" value="highlight.typesids" itemKey="id"  cssClass="checkbox" /]
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
        <div class="halfPartBlock imageBlock">
          <h6><label for="highlight.image">[@customForm.text name="reporting.projectHighlight.image" readText=!editable /]:</label></h6>
          <div class="browseInput fileUpload">
            [#if editable]
              [#if highlight.photo?has_content]
                <p> 
                  [#if editable]<span id="remove-file" class="remove"></span>[#else]<span class="file"></span>[/#if] 
                  <a href="${(highlightsImagesUrl)!baseUrl}${(highlight.photo)!'images/global/defaultImage.png'}">${(highlight.photo)!}</a>  
                  <input type="hidden" name="highlight.photo" value="${highlight.photo}" /> 
                </p>
              [#else]
                [@customForm.inputFile name="file" /]
              [/#if] 
            [/#if]  
          </div>
          <div id="highlight.image" class="image">
           [#if highlight.photo?has_content]
             <img src="${(highlightsImagesUrl)!baseUrl}${(highlight.photo)!'images/global/defaultImage.png'}" width="100%">
           [#else]
             <img src="${baseUrl}/${(highlight.photo)!'images/global/defaultImage.png'}" width="100%">
           [/#if]
          </div>
          <div class="clear"></div>
        </div>
      </div>
      <br />
      
      <div class="fullPartBlock">
        [#-- Start Date --]
        <div class="halfPartBlock">
          [@customForm.input name="highlight.startDateText" className="startDate" type="text" i18nkey="reporting.projectHighlight.startDate" editable=editable/]
        </div>
  
        [#-- End Date --]
        <div class="halfPartBlock">
          [@customForm.input name="highlight.endDateText" className="endDate" type="text" i18nkey="reporting.projectHighlight.endDate" editable=editable/]
        </div>
      </div>
      
      [#-- Is global --]
      <div class="fullBlock">
        <div class="halfPartBlock">
          [@customForm.checkbox  name="highlight.isGlobal" className="isGlobal" i18nkey="reporting.projectHighlight.isGlobal" checked=(highlight.isGlobal)!false value="true" editable=editable/]
        </div>
      </div>
      
      [#-- Countries --]
      <div class="fullBlock countriesBlock chosen" style="display:${((highlight.isGlobal)!false)?string('none','block')}">
        [#if editable]
          [@customForm.select name="highlight.countriesIds" label="" i18nkey="reporting.projectHighlight.countries" listName="countries" keyFieldName="id"  displayFieldName="name" value="highlight.countriesIds" multiple=true disabled="${(highlight.global?string(1, 0))!0}"/]              
        [#else]
          <h6>[@s.text name="reporting.projectHighlight.countries" /]</h6>
          <div class="select">
          [#if highlight.countries?has_content]
            [#list highlight.countries as element]
             <p class="checked">${element.name}</p>
            [/#list]
          [#else]
            <p>Field is empty</p>
          [/#if]
          </div>
        [/#if]
      </div>

      [#-- Keywords --]
      <div class="fullBlock">
        [@customForm.input name="highlight.keywords" type="text" i18nkey="reporting.projectHighlight.keywords" help="reporting.projectHighlight.keywords.help" editable=editable/]
      </div>

      [#-- Description --]
      <div class="fullBlock">
        [@customForm.textArea name="highlight.description" className="limitWords-300" i18nkey="reporting.projectHighlight.descripition" editable=editable/]
      </div>

      [#-- Objectives --]
      <div class="fullBlock">
        [@customForm.textArea name="highlight.objectives" className="limitWords-100" i18nkey="reporting.projectHighlight.objectives" editable=editable/]
      </div>

      [#-- Result --]
      <div class="fullBlock">
        [@customForm.textArea name="highlight.results" className="limitWords-300" i18nkey="reporting.projectHighlight.results" editable=editable/]
      </div>

      [#-- Partners --]
      <div class="fullBlock">
        [@customForm.textArea name="highlight.partners" className="limitWords-300" i18nkey="reporting.projectHighlight.partners" editable=editable/]
      </div>

      [#-- Links / resources --]
      <div class="fullBlock">
        [@customForm.textArea name="highlight.links" i18nkey="reporting.projectHighlight.links" editable=editable/]
      </div>
      
    </div>
    
    [#if editable]
      <input id="minDateValue" value="${startYear?c}-01-01" type="hidden"/>
      <input id="maxDateValue" value="${endYear?c}-12-31" type="hidden"/> 
      <input name="projectID" type="hidden" value="${project.id?c}" />
      <input name="highlightID"type="hidden" value="${highlight.id}">
      <div class="">
        <div class="buttons">
          [@s.submit type="button" name="save"][@s.text name="form.buttons.save" /][/@s.submit]
          [@s.submit type="button" name="next"][@s.text name="form.buttons.next" /][/@s.submit]
          [@s.submit type="button" name="cancel"][@s.text name="form.buttons.cancel" /][/@s.submit]
        </div>
      </div>
    [#else]
      [#-- Display Log History --]
      [#if history??][@log.logList list=history /][/#if]   
    [/#if]
  </article>
  [/@s.form] 
     
</section> 

[#-- File upload template --]
[@customForm.inputFile name="file" template=true /] 

[#include "/WEB-INF/global/pages/footer.ftl"]