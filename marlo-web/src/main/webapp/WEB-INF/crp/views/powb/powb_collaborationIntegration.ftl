[#ftl]
[#assign title = "POWB Synthesis" /]
[#assign currentSectionString = "powb-${actionName?replace('/','-')}-${liaisonInstitutionID}" /]
[#assign pageLibs = [ "select2", "flat-flags", "datatables.net", "datatables.net-bs" ] /]
[#assign customJS = [ "${baseUrlMedia}/js/powb/powb_collaborationIntegration.js" ] /]
[#assign customCSS = [ "${baseUrlMedia}/css/powb/powbGlobal.css" ] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "collaborationIntegration" /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"powbReport", "nameSpace":"powb", "action":"${crpSession}/adjustmentsChanges"},
  {"label":"collaborationIntegration", "nameSpace":"powb", "action":"${crpSession}/collaborationIntegration"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]

[#-- Helptext --]
[@utilities.helpBox name="collaborationIntegration.help" /]
    
<section class="container">
  [#-- Program (Flagships and PMU) --]
  [#include "/WEB-INF/crp/views/powb/submenu-powb.ftl" /]
  
  <div class="row">
    [#-- POWB Menu --]
    <div class="col-md-3">
      [#include "/WEB-INF/crp/views/powb/menu-powb.ftl" /]
    </div> 
    <div class="col-md-9">
      [#-- Section Messages --]
      [#include "/WEB-INF/crp/views/powb/messages-powb.ftl" /]
      
      [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
        
        [#-- Title --]
        <h3 class="headTitle">[@s.text name="collaborationIntegration.title"][@s.param]${(actualPhase.year)!}[/@s.param][/@s.text]</h3>
        <div class="borderBox">
          
          [#-- 2.3.1  New Key External Partnerships  --] 
          <div class="form-group">
            [@customForm.textArea  name="powbSynthesis.collaboration.keyExternalPartners" i18nkey="powbSynthesis.collaborationIntegration.partnerships" help="powbSynthesis.collaborationIntegration.partnerships.help" paramText="${actualPhase.year}" required=true className="limitWords-100" editable=editable /]
          </div>
          
          [#-- Project Partnerships --]
          [#if flagship]
            <h4 class="subTitle headTitle">[@s.text name="collaborationIntegration.tableFlagshipPartnerships.title"][@s.param]${(actualPhase.year)!}[/@s.param][/@s.text]</h4>
            <hr />
            [@tableFlagshipPartnershipsMacro list=(action.loadProjects(liaisonInstitution.crpProgram.id))![]  /]
          [/#if]
          
          [#-- Table: New Key External Partnerships --]
          [#if PMU]
          <div class="form-group">
            <h4 class="subTitle headTitle">[@s.text name="collaborationIntegration.tableKeyExternal.title"][@s.param]${(actualPhase.year)!}[/@s.param][/@s.text]</h4>
            [@tableFlagshipsOverallMacro list=crpPrograms item=1 /]
          </div>
          [/#if]
        </div>
        <div class="borderBox"> 
          [#-- 2.3.2  New Contribution to and from Platforms --] 
          <div class="form-group">
            [@customForm.textArea  name="powbSynthesis.collaboration.cotributionsPlatafforms" i18nkey="powbSynthesis.collaborationIntegration.platformsContributions" help="powbSynthesis.collaborationIntegration.platformsContributions.help" paramText="${actualPhase.year}" required=true className="limitWords-100" editable=editable /]
          </div>
          
          
          [#-- 2.3.3  New Cross-CRP Interactions --] 
          <div class="form-group">
            [@customForm.textArea  name="powbSynthesis.collaboration.crossCrp" i18nkey="powbSynthesis.collaborationIntegration.crpInteractions" help="powbSynthesis.collaborationIntegration.crpInteractions.help" paramText="${actualPhase.year}" required=true className="limitWords-100" editable=editable /]
          </div>
          
          [#-- Table: Flagships - Contribution to and from Platforms & Flagships - New Cross-CRP Interactions--]
          [#if PMU]
          <div class="form-group">
            <h4 class="subTitle headTitle">
              Flagships - [@s.text name="collaborationIntegration.tableContributionOtherPlatforms.title"][@s.param]${(actualPhase.year)!}[/@s.param][/@s.text] & [@s.text name="collaborationIntegration.tableCrossCRPInteractions.title"][@s.param]${(actualPhase.year)!}[/@s.param][/@s.text]
            </h4>
            [@tableFlagshipsOverallMacro list=crpPrograms item=2 /]
          </div>
          [/#if]
          
          [#-- Collaborations among Programs and between the Program and Platforms --]
          [#if flagship]
          <div class="form-group">
            <h4 class="subTitle headTitle">[@s.text name="collaborationIntegration.listCollaborations.title"][@s.param]${(actualPhase.year)!}[/@s.param][/@s.text]</h4>
            <div class="listProgramCollaborations">
             [#if powbSynthesis.powbCollaborationGlobalUnitsList?has_content]
              [#list powbSynthesis.powbCollaborationGlobalUnitsList as collaboration]
                [@flagshipCollaborationMacro element=collaboration name="powbSynthesis.powbCollaborationGlobalUnitsList" index=collaboration_index  isEditable=editable/]
              [/#list]
             [/#if]
            </div>
            [#if canEdit && editable]
            <div class="text-right">
              <div class="addProgramCollaboration bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="form.buttons.addProgramCollaboration"/]</div>
            </div> 
            [/#if]
            
            <ul style="display:none">
              [#list globalUnits as globalUnit]
                <li id="globalUnit-${globalUnit.id}">${globalUnit.composedName} <span><i>(${globalUnit.globalUnitType.name})</i> </span></li>
              [/#list]
            </ul>
          </div>
          [/#if]
          
          [#if PMU]
            [@tableOverallCRPCollaborationsMacro crpPrograms=crpPrograms /]
          [/#if]
          
        </div>
        <div class="borderBox">
          [#-- 2.3.4  Expected Efforts on Country Coordination --] 
          <div class="form-group">
            [@customForm.textArea  name="powbSynthesis.collaboration.effostornCountry" i18nkey="powbSynthesis.collaborationIntegration.expectedEfforts" help="powbSynthesis.collaborationIntegration.expectedEfforts.help" paramText="${actualPhase.year}" required=true className="limitWords-100" editable=editable /]
          </div>
          <br />
          
          [#-- Table: Flagships - Expected Efforts on Country Coordination --]
          [#if PMU]
          <div class="form-group">
            <h4 class="subTitle headTitle">[@s.text name="collaborationIntegration.tableExpectedEfforts.title"][@s.param]${(actualPhase.year)!}[/@s.param][/@s.text]</h4>
            [@tableFlagshipsOverallMacro list=crpPrograms item=3/]
          </div>
          [/#if]
          
          [#-- Table: CGIAR Country Coordination--]
          [#if flagship]
          <div class="form-group">
            <h4 class="subTitle headTitle">[@s.text name="collaborationIntegration.tableCountryContribution.title"][@s.param]${(actualPhase.year)!}[/@s.param][/@s.text]</h4>
            [@tableCountryContributionsMacro /]
          </div>
          [/#if]
          
        </div>
        
        [#-- Section Buttons & hidden inputs--]
        [#include "/WEB-INF/crp/views/powb/buttons-powb.ftl" /]
        
      [/@s.form] 
    </div> 
  </div> 
</section>

[#--  Program collaboration Template --]
[@flagshipCollaborationMacro element={} name="powbSynthesis.powbCollaborationGlobalUnitsList" index=-1 template=true /]

[#include "/WEB-INF/crp/pages/footer.ftl"]

[#---------------------------------------------- MACROS ----------------------------------------------]

[#macro tableFlagshipsOverallMacro list item ]
  <div class="">
    <table class="table table-bordered">
      <thead>
        <tr>
          <th class="col-md-1"> [@s.text name="collaborationIntegration.tableFlagshipsOverall.fp" /] </th>
          [#if item ==2 ]
            <th> [@s.text name="collaborationIntegration.tableFlagshipsOverall.narrative" /] </th>
            <th> [@s.text name="collaborationIntegration.tableFlagshipsOverall.narrative" /] </th>
          [#else]
            <th> [@s.text name="collaborationIntegration.tableFlagshipsOverall.narrative" /] </th>
          [/#if]
        </tr>
      </thead>
      <tbody>
        [#if list??]
          [#list list as li]
            <tr>
              <td><span class="programTag" style="border-color:${(li.color)!'#fff'}" title="${li.composedName}">${li.acronym}</span></td>              
              [#if item ==1]  
                <td>[#if (li.collaboration.keyExternalPartners?has_content)!false]${li.collaboration.keyExternalPartners?replace('\n', '<br>')}[#else]<i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>[/#if]</td>
              [/#if]
              [#if item ==2 ]  
                <td>[#if (li.collaboration.cotributionsPlatafforms?has_content)!false]${li.collaboration.cotributionsPlatafforms?replace('\n', '<br>')}[#else]<i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>[/#if]</td>
                <td>[#if (li.collaboration.crossCrp?has_content)!false]${li.collaboration.crossCrp?replace('\n', '<br>')}[#else]<i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>[/#if]</td>
              [/#if]
              [#if item ==3]  
                <td>[#if (li.collaboration.effostornCountry?has_content)!false]${li.collaboration.effostornCountry?replace('\n', '<br>')}[#else]<i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>[/#if]</td>
              [/#if]
            </tr>
          [/#list]
        [/#if]
      </tbody>
    </table>
  </div>
[/#macro]

[#macro tableFlagshipPartnershipsMacro list ]
  <div class="">
    <table class="table table-bordered partnershipsTable">
      <thead>
        <tr>
          <th class="col-md-1"> [@s.text name="collaborationIntegration.tableFlagshipPartnerships.project" /] </th>
          <th> [@s.text name="collaborationIntegration.tableFlagshipPartnerships.narrative" /] </th>
        </tr>
      </thead>
      <tbody>
        [#if list??]
          [#list list as project]
            [#local pURL][@s.url namespace="/projects" action="${(crpSession)!}/partners"][@s.param name='projectID']${project.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
            <tr>
              <td> <a href="${pURL}" target="_blank" title="${(project.composedName)!}">P${project.id}</a> </td>              
              <td>
                [#if (project.projectInfo.newPartnershipsPlanned?has_content)!false]
                  ${project.projectInfo.newPartnershipsPlanned?replace('\n', '<br>')}
                [#else]
                  <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
                [/#if]
              </td>
            </tr>
          [/#list]
        [/#if]
      </tbody>
    </table>
  </div>
[/#macro]

[#macro tableCountryContributionsMacro ]

  [#assign locElements = siteIntegrations /]
  <div class="">
    <table class="table table-bordered">
      <thead>
        <tr>
          <th class="col-md-2"> [@s.text name="collaborationIntegration.tableCountryContribution.cgiarCountry" /] </th>
          <th> [@s.text name="collaborationIntegration.tableCountryContribution.fundingSources" /] </th>
          <th> [@s.text name="collaborationIntegration.tableCountryContribution.projects" /] </th>
        </tr>
      </thead>
      <tbody>
        [#if locElements??]
          [#list locElements as locElement]
            <tr>
              <td> <i class="flag-sm flag-sm-${(locElement.locElement.isoAlpha2?upper_case)!}"></i> ${locElement.locElement.name} </td>              
              <td class="col-md-5">
                [#if (locElement.fundingSources?has_content)!false]
                  [#list locElement.fundingSources as fundingSource]
                    [#local fURL][@s.url namespace="/fundingSources" action="${(crpSession)!}/fundingSource"][@s.param name='fundingSourceID']${fundingSource.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
                    <a href="${fURL}" target="_blanck">FS${fundingSource.id}</a>, 
                  [/#list]
                [#else]
                  <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
                [/#if]
              </td>
              <td class="col-md-5">
                [#if (locElement.projects?has_content)!false]
                  [#list locElement.projects as project]
                    [#local pURL][@s.url namespace="/projects" action="${(crpSession)!}/locations"][@s.param name='projectID']${project.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
                    <a href="${pURL}" target="_blank">P${project.id}</a>,
                  [/#list]
                [#else]
                  <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
                [/#if]
              </td>
              
            </tr>
          [/#list]
        [/#if]
      </tbody>
    </table>
  </div>
[/#macro]

[#macro flagshipCollaborationMacro element name index template=false isEditable=true]
  [#local customName = "${name}[${index}]" /]
  <div id="flagshipCollaboration-${template?string('template', index)}" class="flagshipCollaboration borderBox form-group" style="position:relative; display:${template?string('none','block')}">

    [#-- Index --]
    <div class="leftHead blue sm"><span class="index">${index+1}</span></div>
    [#-- Remove Button --]
    [#if isEditable]<div class="removeProgramCollaboration removeElement sm" title="Remove"></div>[/#if]
    [#-- Hidden inputs --]
    <input type="hidden" name="${customName}.id" value="${(element.id)!}"/> 
    <br />

    <div class="form-group row"> 
      [#-- CRP/Platform --] 
      <div class="col-md-4">
        [@customForm.select name="${customName}.globalUnit.id" label="" keyFieldName="id"  displayFieldName="acronymValid" i18nkey="powbSynthesis.programCollaboration.globalUnit" listName="globalUnits"  required=true  className="" editable=isEditable/]
      </div>
      [#-- Flagship/Module --]
      <div class="col-md-8">
        [@customForm.input name="${customName}.flagship" i18nkey="powbSynthesis.programCollaboration.program" required=true className="" editable=isEditable /]
      </div>
    </div>
    
    [#-- Collaboration type --]
    <div class="form-group">
      <label>[@s.text name="powbSynthesis.programCollaboration.collaborationType" /] [@customForm.req required=editable  /]</label><br />
      [@customForm.radioFlat id="${customName}-type-1" name="${customName}.collaborationType" label="Contribution to"     value="1" checked=(element.collaborationType == "1")!false editable=isEditable cssClass="" cssClassLabel=""/]
      [@customForm.radioFlat id="${customName}-type-2" name="${customName}.collaborationType" label="Service needed from" value="2" checked=(element.collaborationType == "2")!false editable=isEditable cssClass="" cssClassLabel=""/]
      [@customForm.radioFlat id="${customName}-type-3" name="${customName}.collaborationType" label="Both"                value="3" checked=(element.collaborationType == "3")!false editable=isEditable cssClass="" cssClassLabel=""/]
      
      [#local collaborationTypeSelected = ((element.collaborationType == "1")!false) || ((element.collaborationType == "2")!false) || ((element.collaborationType == "3")!false)]
      [#if !editable && !collaborationTypeSelected][@s.text name="form.values.fieldEmpty"/][/#if]
    </div>
    
    [#-- Brief Description --] 
    <div class="form-group"> 
      [@customForm.textArea name="${customName}.brief" i18nkey="powbSynthesis.programCollaboration.brief"  placeholder="" className="limitWords-100" required=true editable=isEditable /]
    </div>
    
  </div>
[/#macro]

[#macro tableOverallCRPCollaborationsMacro crpPrograms]
<div class="">
  <table class="table table-bordered">
    <thead>
      <tr>
        <th class="col-md-1"> [@s.text name="collaborationIntegration.tableFlagshipsOverall.fp" /] </th>
        <th> Collaboration Program </th>
        <th> Brief description </th>
      </tr>
    </thead>
    <tbody>
    [#list crpPrograms as crpProgram]
      [#if crpProgram.synthesis.powbCollaborationGlobalUnitsList??]
        [#list crpProgram.synthesis.powbCollaborationGlobalUnitsList as collaboration]
          <tr>
            <td><span class="programTag" style="border-color:${(crpProgram.color)!'#fff'}" title="${crpProgram.composedName}">${crpProgram.acronym}</span></td>
            <td> ${(collaboration.globalUnit.composedName)!collaboration.globalUnit.acronym}</td>
            <td> ${(collaboration.brief)!} </td>
          </tr>
        [/#list]
      [/#if]
    [/#list]
    </tbody>
  </table>
</div>
[/#macro]