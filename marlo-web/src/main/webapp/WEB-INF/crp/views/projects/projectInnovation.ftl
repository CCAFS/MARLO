[#ftl]
[#assign title = "Project Innovations" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#-- TODO: Remove unused pageLibs--]
[#assign pageLibs = ["select2","font-awesome", "flat-flags"] /]
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

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#-- Helptext --]
[@utilities.helpBox name="projectInnovations.help" /]

<section class="container">
  <div class="row">
    [#-- Project Menu --]
    <div class="col-md-3">
      [#include "/WEB-INF/crp/views/projects/menu-projects.ftl" /]
    </div>
    [#-- Project Section Content --]
    <div class="col-md-9">
      [#-- Section Messages --]
      [#include "/WEB-INF/crp/views/projects/messages-projects.ftl" /]

      [#-- Back --]
      <small class="pull-right">
        <a href="[@s.url action='${crpSession}/innovationsList'][@s.param name="projectID" value=project.id /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
          <span class="glyphicon glyphicon-circle-arrow-left"></span> [@s.text name="projectInnovations.back" /]
        </a>
      </small>
        
      [@s.form action=actionName cssClass="pure-form" method="POST" enctype="multipart/form-data" ]
        
        [#--  Innovation Title --]
        <h3 class="headTitle">[@s.text name="projectInnovations" /]</h3> 
        <div id="innovations" class="borderBox clearfix">

        <div class="">
          [#-- Title --] 
          <div class="form-group">
            [@customForm.input name="innovation.projectInnovationInfo.title" type="text" i18nkey="projectInnovations.title"  placeholder="" className="limitWords-20" required=true editable=editable /]
          </div>
        
          [#-- Narrative --] 
          <div class="form-group">
            [@customForm.textArea name="innovation.projectInnovationInfo.narrative"  i18nkey="projectInnovations.narrative"  placeholder="" className="limitWords-50" required=true editable=editable /]
          </div>
        
          [#-- Phase of research and Stage of innovation --] 
          <div class="form-group row">
            <div class="col-md-6 ">
              [@customForm.select name="innovation.projectInnovationInfo.repIndPhaseResearchPartnership.id" label=""  i18nkey="projectInnovations.phase" listName="phaseResearchList" keyFieldName="id"  displayFieldName="name"   required=true  className="" editable=editable/]
            </div>
            <div class="col-md-6 ">
              [@customForm.select name="innovation.projectInnovationInfo.repIndStageInnovation.id" label=""  i18nkey="projectInnovations.stage" listName="stageInnovationList" keyFieldName="id"  displayFieldName="name"   required=true  className="stageInnovationSelect" editable=editable/]
              [#assign isStageFour = (innovation.projectInnovationInfo.repIndStageInnovation.id == 4)!false]
            </div>
          </div>
        
          [#-- Geographic scope and innovation type --] 
          <div class="form-group row">
            <div class="col-md-6 ">
              [@customForm.select name="innovation.projectInnovationInfo.repIndGeographicScope.id" label=""  i18nkey="projectInnovations.geographicScope" listName="geographicScopeList" keyFieldName="id"  displayFieldName="name" required=true  className="geographicScopeSelect" editable=editable/]
            </div>
            <div class="col-md-6 ">
              [@customForm.select name="innovation.projectInnovationInfo.repIndInnovationType.id" label=""  i18nkey="projectInnovations.innovationType" listName="innovationTypeList" keyFieldName="id"  displayFieldName="name" required=true  className="" editable=editable/]
            </div>
          </div>
          
          [#assign isRegional = ((innovation.projectInnovationInfo.repIndGeographicScope.id == action.reportingIndGeographicScopeRegional)!false) ]
          [#assign isMultiNational = ((innovation.projectInnovationInfo.repIndGeographicScope.id == action.reportingIndGeographicScopeMultiNational)!false) ]
          [#assign isNational = ((innovation.projectInnovationInfo.repIndGeographicScope.id == action.reportingIndGeographicScopeNational)!false) ]
          [#assign isSubNational = ((innovation.projectInnovationInfo.repIndGeographicScope.id == action.reportingIndGeographicScopeSubNational)!false) ]
        
          [#-- Region (if scope is Region) --] 
          <div class="form-group row regionalBlock" style="display:${isRegional?string('block','none')}">
            <div class="col-md-6 ">
              [@customForm.select name="innovation.projectInnovationInfo.repIndRegion.id" label=""  i18nkey="projectInnovations.region" listName="regionList" keyFieldName="id"  displayFieldName="name" required=true  className="" editable=editable/]
            </div>
            <div class="col-md-6 ">
            </div>
          </div>
        
          [#-- Country(ies) (if scope is Multi-national, National or Sub-National)  --]
          <div class="form-group countriesBlock nationalBlock chosen" style="display:${(isMultiNational || isNational || isSubNational)?string('block','none')}">
            [#if editable]
              [@customForm.select name="innovation.countriesIds" label="" i18nkey="projectInnovations.countries" listName="countries" keyFieldName="isoAlpha2"  displayFieldName="name" value="innovation.countriesIds" required=true  className="countriesIds" multiple=true  /]
            [#else]
              <label>[@s.text name="projectInnovations.countries" /]:</label>
              <div class="select">
              [#if innovation.countries?has_content]
                [#list innovation.countries as element]<p class="checked">${(element.locElement.name)!}</p>[/#list]
              [#else]
                <p>[@s.text name="projectInnovations.countries" /]</p>
              [/#if]
              </div>
            [/#if]
          </div>
          
          [#-- Specify next user organizational type (Only if stage 4) --]
          <div class="form-group stageFourBlock" style="display:${isStageFour?string('block','none')}">
            [@customForm.elementsListComponent name="innovation.organizations" elementType="repIndOrganizationType" elementList=innovation.organizations label="projectInnovations.nextUserOrganizationalType"  listName="organizationTypeList" keyFieldName="id" displayFieldName="name"/]
          </div>
        
          [#-- Specify an Outcome Case Study (Only if stage 4) --]
          <div class="form-group stageFourBlock" style="display:${isStageFour?string('block','none')}">
            [@customForm.select name="innovation.projectInnovationInfo.projectExpectedStudy.id" label=""  i18nkey="projectInnovations.outcomeCaseStudy" listName="expectedStudyList" keyFieldName="id"  displayFieldName="composedName"  multiple=false required=true  className="keyOutput" editable=editable/]
          </div>
        
          [#-- Novel or adaptative research --] 
          <div class="form-group">
            [@customForm.textArea name="innovation.projectInnovationInfo.novel" i18nkey="projectInnovations.novelOrAdaptative"  placeholder="" className="limitWords-100" required=true editable=editable /]
          </div>
        
          [#-- Evidence Link --] 
          <div class="form-group">
            [@customForm.input name="innovation.projectInnovationInfo.evidenceLink"  type="text" i18nkey="projectInnovations.evidenceLink"  placeholder="marloRequestCreation.webSiteLink.placeholder" className="" required=true editable=editable /]
          </div>
        
          [#-- Or Deliverable ID (optional) --]
          <div class="form-group">
            [@customForm.elementsListComponent name="innovation.deliverables" elementType="deliverable" elementList=innovation.deliverables label="projectInnovations.deliverableId"  listName="deliverableList" required=false keyFieldName="id" displayFieldName="composedName"/]
          </div>
        
          [#-- Contributing CRPs/Platforms --]
          <div class="form-group">
            [@customForm.elementsListComponent name="innovation.crps" elementType="globalUnit" elementList=innovation.crps label="projectInnovations.contributing"  listName="crpList" keyFieldName="id" displayFieldName="composedName"/]
          </div>
          
          [#-- Gender Relevance --]
          <div class="form-group">
            <label for="">[@s.text name="projectInnovations.genderRelevance" /]:[@customForm.req required=editable /]</label>
            <div class="simpleBox">
              <div class="form-group">
                [#list focusLevelList as level]
                  <p>[@customForm.radioFlat id="genderLevel-${level.id}" name="innovation.projectInnovationInfo.genderFocusLevel.id" label="${level.name}" value="${level.id}" checked=(innovation.projectInnovationInfo.genderFocusLevel.id == level.id)!false cssClass="" cssClassLabel=""/]</p> 
                [/#list]
              </div>
              [#-- Brief explanation and evidence --] 
              <div class="form-group">
                [@customForm.textArea name="innovation.projectInnovationInfo.genderExplaniation" i18nkey="projectInnovations.genderRelevance.explanation"  placeholder="" className="" required=true editable=editable /]
              </div>
            </div>
          </div>
          
          [#-- Youth Relevance --]
          <div class="form-group">
            <label for="">[@s.text name="projectInnovations.youthRelevance" /]:[@customForm.req required=editable /]</label>
            <div class="simpleBox">
              <div class="form-group">
                [#list focusLevelList as level]
                  <p>[@customForm.radioFlat id="youthLevel-${level.id}" name="innovation.projectInnovationInfo.youthFocusLevel.id" label="${level.name}" value="${level.id}" checked=(innovation.projectInnovationInfo.youthFocusLevel.id == level.id)!false cssClass="" cssClassLabel=""/]</p> 
                [/#list]
              </div>
              [#-- Brief explanation and evidence --] 
              <div class="form-group">
                [@customForm.textArea name="innovation.projectInnovationInfo.youthExplaniation" i18nkey="projectInnovations.youthRelevance.explanation" placeholder="" className="" required=true editable=editable /]
              </div>
            </div>
          </div>
        </div>
      </div>
        
      [#-- Section Buttons & hidden inputs--]
      [#include "/WEB-INF/crp/views/projects/buttons-innovation.ftl" /]
        
      [/@s.form] 
  </div>  
</section>


[#-- Element Macro Template --]
<ul style="display:none">
  [@customForm.listElementMacro name="innovation.organizations" element={} type="repIndOrganizationType" index=-1 template=true /]
  [@customForm.listElementMacro name="innovation.crps" element={} type="globalUnit" index=-1 template=true /]
  [@customForm.listElementMacro name="innovation.deliverables" element={} type="deliverable" index=-1 template=true /]
</ul>

[#include "/WEB-INF/global/pages/footer.ftl"]


[#-- MACROS --]

