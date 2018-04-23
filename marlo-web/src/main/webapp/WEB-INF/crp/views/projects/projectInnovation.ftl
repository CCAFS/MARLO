[#ftl]
[#assign title = "Project Innovations" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#-- TODO: Remove unused pageLibs--]
[#assign pageLibs = ["select2","font-awesome","dropzone","blueimp-file-upload","jsUri"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/projectInnovations.js",
  "${baseUrl}/global/js/autoSave.js",
  "${baseUrl}/global/js/fieldsValidation.js"
] /]
[#assign customCSS = ["${baseUrlMedia}/css/projects/projectInnovations.css"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "innovations" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"text":"P${project.id}", "nameSpace":"/projects", "action":"${crpSession}/description", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
  {"label":"innovationsList", "nameSpace":"/projects", "action":"${(crpSession)!}/innovations" ,"param":"projectID=${projectID}"},
  {"label":"innovationInformation", "nameSpace":"/projects", "action":""}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utils /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/global/images/icon-help.jpg" />
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
          <a href="[@s.url action='${crpSession}/innovations'][@s.param name="projectID" value=project.id /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
            <span class="glyphicon glyphicon-circle-arrow-left"></span> Back to the project innovations
          </a>
        </small>
        
        [#--  Innovation Title --]
        <h3 class="headTitle">[@s.text name="projectInnovations" /]</h3> 
        <div id="innovations" class="borderBox clearfix">

        <div class="simpleBox">
          [#-- Title --] 
          <div class="form-group">
            [@customForm.input name="title" value="${(tittle)!}" type="text" i18nkey="projectInnovations.title"  placeholder="" className="limitWords-20" required=true editable=editable /]
          </div>
        
          [#-- Narrative --] 
          <div class="form-group">
            [@customForm.textArea name="" value="${(narrative)!}" i18nkey="projectInnovations.narrative"  placeholder="" className="limitWords-50" required=true editable=editable /]
          </div>
        
          [#-- Phase of research and Stage of innovation --] 
          <div class="form-group row">
            <div class="col-md-6 ">
              [@customForm.select name="" label=""  i18nkey="projectInnovations.phase" listName="deliverableTypeParent" keyFieldName="id"  displayFieldName="name"  multiple=false required=true  className=" form-control input-sm" editable=editable/]
            </div>
            <div class="col-md-6 ">
              [@customForm.select name="" label=""  i18nkey="projectInnovations.stage" listName="deliverableSubTypes" keyFieldName="id"  displayFieldName="name"  multiple=false required=true  className=" form-control input-sm" editable=editable/]
            </div>
          </div>
        
          [#-- Geographic scope and innovation type --] 
          <div class="form-group row">
            <div class="col-md-6 ">
              [@customForm.select name="" label=""  i18nkey="projectInnovations.geographicScope" listName="deliverableTypeParent" keyFieldName="id"  displayFieldName="name"  multiple=false required=true  className=" form-control input-sm" editable=editable/]
            </div>
            <div class="col-md-6 ">
              [@customForm.select name="" label=""  i18nkey="projectInnovations.innovationType" listName="deliverableSubTypes" keyFieldName="id"  displayFieldName="name"  multiple=false required=true  className=" form-control input-sm" editable=editable/]
            </div>
          </div>
        
          [#-- Region (if scope is Region) --] 
          <div class="form-group row">
            <div class="col-md-6 ">
              [@customForm.select name="" label=""  i18nkey="projectInnovations.region" listName="deliverableTypeParent" keyFieldName="id"  displayFieldName="name"  multiple=false required=true  className=" form-control input-sm" editable=editable/]
            </div>
            <div class="col-md-6 ">
            </div>
          </div>
        
          [#-- Country(ies) (if scope is Multi-national, National or Sub-National)  --]
          <div class="form-group countriesBlock chosen ${customForm.changedField('highlight.countries')}" style="display:${((highlight.global)!false)?string('none','block')}" >
            [#if editable]
              [@customForm.select name="highlight.countriesIds" label="" i18nkey="projectInnovations.countries" listName="countries" keyFieldName="id"  displayFieldName="name" value="highlight.countriesIds" multiple=true disabled="${(highlight.global?string(1, 0))!0}"/]
            [#else]
              <label>[@s.text name="projectInnovations.countries" /]:</label>
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
          
          [#-- Specify next user organizational type (Only if stage 4) --]
          <div class="panel tertiary">
           <div class="panel-head"><label for=""> [@customForm.text name="projectInnovations.nextUserOrganizationalType" readText=!editable /]:[@customForm.req required=editable /]</label></div>
            <div id="fundingSourceList" class="panel-body" listname="deliverable.fundingSources"> 
              <ul class="list">
              [#if deliverable.fundingSources?has_content]
                [#list deliverable.fundingSources as element]
                  <li class="fundingSources clearfix">
                    [#if editable]<div class="removeFundingSource removeIcon" title="Remove funding source"></div>[/#if] 
                    <input class="id" type="hidden" name="deliverable.fundingSources[${element_index}].id" value="${(element.id)!}" />
                    <span class="name">
                      <span class="description">${(element.fundingSource.fundingSourceInfo.title)!}</span><br />
                    </span>
                    <div class="clearfix"></div>
                  </li>
                [/#list]
                <p style="display:none;" class="emptyText"> [@s.text name="project.deliverable.fundingSource.empty" /]</p>   
              [#else]
                <p class="emptyText"> [@s.text name="project.deliverable.fundingSource.empty" /]</p> 
              [/#if]
              </ul>
              [#if editable ]
                [@customForm.select name="deliverable.fundingSource.id" label=""  showTitle=false  i18nkey="" listName="fundingSources" keyFieldName="id"  displayFieldName="composedName"  header=true required=true  className="fundingSource" editable=editable/]
              [/#if] 
            </div>
          </div>
          
          [#-- Selected options List --]
          <div style="display:none">
            [#if fundingSources?has_content]
              [#list fundingSources as element]
                <span id="">
                  <span class="description">${(element.fundingSourceInfo.title)!}</span><br />
                </span>
              [/#list]
            [/#if]
          </div>
        
          [#-- Specify an Outcome Case Study (Only if stage 4) --]
          <div class="form-group">
          [@customForm.select name="" label=""  i18nkey="projectInnovations.outcomeCaseStudy" listName="keyOutputs" keyFieldName="id"  displayFieldName="composedName"  multiple=false required=true  className="keyOutput" editable=editable/]
          </div>
        
          [#-- Novel or adaptative research --] 
          <div class="form-group">
            [@customForm.textArea name="" value="${(narrative)!}" i18nkey="projectInnovations.novelOrAdaptative"  placeholder="" className="limitWords-100" required=true editable=editable /]
          </div>
        
          [#-- Evidence Link --] 
          <div class="form-group">
            [@customForm.input name="" value="${(title)!}" type="text" i18nkey="projectInnovations.evidenceLink"  placeholder="https://example.com" className="" required=false editable=editable /]
          </div>
        
          [#-- Or Deliverable ID (optional) --]
          <div class="panel tertiary">
           <div class="panel-head"><label for=""> [@customForm.text name="projectInnovations.nextUserOrganizationalType" readText=!editable /]:[@customForm.req required=editable /]</label></div>
            <div id="fundingSourceList" class="panel-body" listname="deliverable.fundingSources"> 
              <ul class="list">
              [#if deliverable.fundingSources?has_content]
                [#list deliverable.fundingSources as element]
                  <li class="fundingSources clearfix">
                    [#if editable]<div class="removeFundingSource removeIcon" title="Remove funding source"></div>[/#if] 
                    <input class="id" type="hidden" name="deliverable.fundingSources[${element_index}].id" value="${(element.id)!}" />
                    <span class="name">
                        <span>${(element.fundingSourceInfo.id)!} - </span>
                      <span class="description">${(element.fundingSource.fundingSourceInfo.title)!}</span><br />
                    </span>
                    <div class="clearfix"></div>
                  </li>
                [/#list]
                <p style="display:none;" class="emptyText"> [@s.text name="project.deliverable.fundingSource.empty" /]</p>   
              [#else]
                <p class="emptyText"> [@s.text name="project.deliverable.fundingSource.empty" /]</p> 
              [/#if]
              </ul>
              [#if editable ]
                [@customForm.select name="deliverable.fundingSource.id" label=""  showTitle=false  i18nkey="" listName="fundingSources" keyFieldName="id"  displayFieldName="composedName"  header=true required=true  className="fundingSource" editable=editable/]
              [/#if] 
            </div>
          </div>
          
          [#-- Selected options List --]
          <div style="display:none">
            [#if fundingSources?has_content]
              [#list fundingSources as element]
                <span id="">
                  <span>${(element.fundingSourceInfo.id)!} - </span>
                  <span class="description">${(element.fundingSourceInfo.title)!}</span><br />
                </span>
              [/#list]
            [/#if]
          </div>
        
          [#-- Contributing CRPs/Platforms --]
          <div class="form-group countriesBlock chosen ${customForm.changedField('highlight.countries')}" style="display:${((highlight.global)!false)?string('none','block')}" >
            [#if editable]
              [@customForm.select name="highlight.countriesIds" label="" i18nkey="projectInnovations.contributing" listName="countries" keyFieldName="id"  displayFieldName="name" value="highlight.countriesIds" multiple=true disabled="${(highlight.global?string(1, 0))!0}"/]
            [#else]
              <label>[@s.text name="projectInnovations.contributing" /]:</label>
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
          
          [#-- Selected options List --]
          <div class="row" style="display:none">
            [#if fundingSources?has_content]
              [#list fundingSources as element]
              <div class="col-md-3">
                <span id="">${name!}</span>
              </div>
              [/#list]
            [/#if]
          </div>
        
          [#-- Gender Relevance --]
          <div class="relevance-container">
            <ul>
              <li>
                [@customForm.radioButtonGroup label="projectInnovations.genderRelevance" name=""  listName="" i18nkey="projectInnovations.genderRelevance.notTargeted" value="" required=true checked="" editable=editable /]
              </li>
            </ul>
        
            [#-- brief explanation and evidence --] 
            <div class="form-group">
              [@customForm.textArea name="" value="${(narrative)!}" i18nkey="projectInnovations.genderRelevance.explanation"  placeholder="" className="limitWords-100" required=true editable=editable /]
            </div>
          </div>
        
        </div>
      [/@s.form] 
    </div>
  </div>  
</section>
[/#if]

[#include "/WEB-INF/global/pages/footer.ftl"]

[#macro locElementMacro element name index isTemplate=false ]
  <li id="locElement-${isTemplate?string('template', index)}" class="locElement userItem" style="display:${isTemplate?string('none','block')}">
    [#assign locElementName = "${name}[${index}]" ]
    [#-- Remove Button --]
    [#if editable]<div class="removeLocElement removeIcon" title="Remove Location"></div>[/#if] 
    
    [#-- Location Name --]
    <span class="flag-icon"><i class="flag-sm flag-sm-${(element.locElement.isoAlpha2?upper_case)!}"></i></span> <span class="name">${(element.composedName)!'{name}'}</span><br />
    
    [#-- Hidden inputs --]
    <input type="hidden" class="locElementCountry" name="${locElementName}.locElement.isoAlpha2" value="${(element.locElement.isoAlpha2)!}" /> 
  </li>
[/#macro]
