[#ftl]
[#assign title = "Project Other contributions" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/projectOtherContributions.js", 
  "${baseUrl}/global/js/autoSave.js",
  "${baseUrl}/global/js/fieldsValidation.js"
  ] 
/]
[#assign customCSS = ["${baseUrlMedia}/css/projects/projectOtherContributions.css"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "otherContributions" /]
[#assign hideJustification = true /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"projectOtherContributions", "nameSpace":"/projects", "action":""}
] /]


[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]

<div class="container helpText viewMore-block">
  <div class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/global/images/icon-help.jpg" />
    <p class="col-md-10"> [@s.text name="projectOtherContributions.help" ][/@s.text]</p>
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
        [#include "/WEB-INF/crp/views/projects/messages-projects.ftl" /]
        
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
          
          <h3 class="headTitle">[@s.text name="projectOtherContributions.title" /]</h3>  
          <div id="otherContributions" class="borderBox"> 
          
            [#-- How are contributing to other CCAFS IP --]
            <div class="fullBlock">
             [#if project.projectOtherContributionsList?has_content]
                  [#list project.projectOtherContributionsList as element]
              [@customForm.textArea name="project.projectOtherContributionsList[${element_index}].contribution" className="contribution limitWords-100" i18nkey="projectOtherContributions.contribution" editable=editable  /]  
             <input type="hidden" class="projectIndicatorParent" name="project.projectOtherContributionsList[${element_index}].id" value="${(element.id)!}"  />
           [/#list]
            [/#if]
            </div>
          
            [#-- -- -- REPORTING BLOCK -- -- --]
            [#-- Others impact pathways contributions --]
            [#if reportingActive]
              <div id="otherContributionsBlock">
                [#if project.otherContributionsList?has_content]
                  [#list project.otherContributionsList as element]
                    [@otherContribution element=element name="project.otherContributionsList" index=element_index /] 
                  [/#list]
                [#else]
                  <div class="emptyMessage simpleBox center"><p>There is not other contributions added</p></div>
                [/#if]
              </div>
              [#if editable]<div id="addOtherContribution"><a href="" class="addLink">[@s.text name="projectOtherContributions.addOtherContribution"/]</a></div>[/#if]
              <div class="clearfix"></div>
              <br />
            [/#if]
          
            [#-- Collaborating with other CRPs --]
            [#assign crpsName= "project.ipOtherContribution.crps"/]
            <div class="fullPartBlock">      
              <div class="crpContribution panel tertiary">
                <div class="panel-head"><label for="">[@customForm.text name="projectOtherContributions.collaboratingCRPs" readText=!editable /]</label></div> 
                <div class="panel-body"> 
                  <ul id="contributionsBlock" class="list">
                  [#if  project.crpContributions?has_content]  
                    [#list project.crpContributions as crp] 
                       [@crpContribution element=crp name="project.crpContributions" index=crp_index /]
                    [/#list] 
                  [#else]
                    <p class="emptyText"> [@s.text name="projectOtherContributions.crpsEmpty" /] </p>  
                  [/#if]  
                  </ul>
                  [#if editable]
                    [@customForm.select name="" label="" disabled=!canEdit i18nkey="" listName="crps" keyFieldName="id"  displayFieldName="name" className="crpsSelect" value="" /]
                  [/#if] 
                </div>
              </div> 
            </div>
          
          </div> <!-- End otherContributions --> 
          
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/crp/views/projects/buttons-projects.ftl" /]
          
        
        [/@s.form] 
      </div>
    </div>  
</section>
[/#if]

[#-- CRP Contribution template --]
<ul>
[@crpContribution element={} name="project.crpContributions" index=-1 isTemplate=true /]
</ul>

[#-- Other contribution template --]
[@otherContribution element={} name="project.otherContributionsList" index=-1 template=true /]
        
[#include "/WEB-INF/crp/pages/footer.ftl"]



[#macro otherContribution element name index template=false]
  [#local customName = "${name}[${template?string('-1',index)}]" /]
  [#local contribution = element /]
  <div id="otherContribution-${template?string('template',index)}" class="otherContribution simpleBox" style="display:${template?string('none','block')}">
    <div class="loading" style="display:none"></div>
    [#-- Edit/Back/remove buttons --]
    [#if (editable && canEdit)]<div class="removeElement" title="[@s.text name="projectOtherContributions.removeOtherContribution" /]"></div>[/#if]
    [#-- Other Contribution ID --]
    <input type="hidden" name="${customName}.id" class="otherContributionId" value="${(contribution.id)!-1}"/>
    <div class="fullBlock">
      [#-- Region --]
      <div class="halfPartBlock">
        [@customForm.select name="${customName}.ipProgram.id" className="otherContributionRegion" label="" i18nkey="projectOtherContributions.region" keyFieldName="id"  displayFieldName="composedName" listName="regions"  required=true editable=editable  /]
      </div> 
    </div>
    [#-- Indicator --]
    <div class="fullBlock">
      [@customForm.select name="${customName}.ipIndicator.id" className="otherContributionIndicator" label="" i18nkey="projectOtherContributions.indicators" keyFieldName="id"  displayFieldName="composedName"  listName="otherIndicators" required=true editable=editable  /]
    </div>
    [#-- Describe how you are contributing to the selected outcome --]
    <div class="fullBlock">
      <label>[@customForm.text name="projectOtherContributions.description" param="${currentCycleYear}" readText=!editable /]:[@customForm.req required=editable  /]</label>
      [@customForm.textArea name="${customName}.description" className="otherContributionDescription limitWords-100"  i18nkey="" showTitle=false required=true editable=editable  /]
    </div>
    [#-- Target contribution --]
    <div class="fullBlock">
      <label>[@customForm.text name="projectOtherContributions.target" readText=!editable /]:</label>
      <div class="halfPartBlock">
        [@customForm.input name="${customName}.target" className="otherContributionTarget" i18nkey="" showTitle=false editable=editable  /]
      </div>
    </div>
  </div> 
[/#macro]



[#macro crpContribution element name index isTemplate=false]
<li id="crpOtherContribution-${isTemplate?string('template', index)}" class="crpOtherContribution clearfix" style="display:${isTemplate?string('none','block')}">
  [#local customName = "${name}[${index}]" /]
  [#-- Remove --]
  [#if editable]<span class="listButton remove removeCrpContribution">[@s.text name="form.buttons.remove" /]</span>[/#if]
  
  [#-- Hidden inputs --]
  <input class="id" type="hidden" name="${customName}.crp.id" value="${(element.crp.id)!}" />
  <input class="crpContributionId" type="hidden" name="${customName}.id" value="${(element.id)!}" />
  
  [#-- CRP Title --]
  <div class="form-group"><span class="name crpName">${(element.crp.name)!}</span></div>
  
  [#-- CRP Outcome Collaboration --]
  <div class="form-group col-md-12">
    [@customForm.textArea name="${customName}.collaborationNature" className="crpCollaborationNature limitWords-50" i18nkey="projectOtherContributions.collaborationNature" required=true editable=editable /]  
  </div>
  
   
  <br />
  
  
</li>
[/#macro]