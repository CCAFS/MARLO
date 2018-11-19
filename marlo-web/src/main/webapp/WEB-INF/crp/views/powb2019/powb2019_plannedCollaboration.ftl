[#ftl]
[#assign title = "POWB Synthesis" /]
[#assign currentSectionString = "powb-${actionName?replace('/','-')}-${liaisonInstitutionID}" /]
[#assign pageLibs = [ "select2", "flat-flags", "datatables.net", "datatables.net-bs" ] /]
[#assign customJS = [ "${baseUrlMedia}/js/powb2019/powb2019_plannedCollaboration.js" ] /]
[#assign customCSS = [ "${baseUrlMedia}/css/powb/powbGlobal.css" ] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "plannedCollaborations" /]


[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"powbReport", "nameSpace":"powb", "action":"${crpSession}/adjustmentsChanges"},
  {"label":"plannedCollaborations", "nameSpace":"powb", "action":""}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#-- Helptext --]
[@utilities.helpBox name="collaborationIntegration.help" /]
    
<section class="container">
  [#-- Program (Flagships and PMU) --]
  [#include "/WEB-INF/crp/views/powb2019/submenu-powb2019.ftl" /]
  
  <div class="row">
    [#-- POWB Menu --]
    <div class="col-md-3">
      [#include "/WEB-INF/crp/views/powb2019/menu-powb2019.ftl" /]
    </div> 
    <div class="col-md-9">
      [#-- Section Messages --]
      [#include "/WEB-INF/crp/views/powb2019/messages-powb2019.ftl" /]
      
      [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
        
        [#-- Title --]
        <h3 class="headTitle">[@s.text name="collaborationIntegration.titlepowb2019"][@s.param]${(actualPhase.year)!}[/@s.param][/@s.text]</h3>
       
        <div class="borderBox"> 
          [#if PMU]            
            <div class="form-group">
              [#if PMU][@utilities.tag label="powb.docBadge" tooltip="powb.docBadge.tooltip"/][/#if]
              <h4 class="subTitle headTitle">[@s.text name="collaborationIntegration.listCollaborations.titlepowb2019"][@s.param]${(actualPhase.year)!}[/@s.param][/@s.text]</h4>
              <div class="form-group">
                [@tableOverallCRPCollaborationsMacro name="powbSynthesis.collaboration" crpPrograms=crpPrograms /]
              </div>
                 
            </div>
          [/#if]
          
          [#-- Table 2C: Major planned new collaborations --]
          [#if flagship]
            <div class="form-group">
              <h4 class="subTitle headTitle powb-table">[@s.text name="collaborationIntegration.listCollaborations.titlepowb2019"][@s.param]${(actualPhase.year)!}[/@s.param][/@s.text]</h4>
              [@customForm.helpLabel name="collaborationIntegration.listCollaborations.titlepowb2019.help" paramText="" showIcon=false editable=editable/]
              <div class="listProgramCollaborations">
               [#if powbSynthesis.powbCollaborationGlobalUnitsList?has_content]
                [#list powbSynthesis.powbCollaborationGlobalUnitsList as collaboration]
                  [@flagshipCollaborationMacro element=collaboration name="powbSynthesis.powbCollaborationGlobalUnitsList" index=collaboration_index  isEditable=editable/]
                [/#list]
              [#else]
                [#if !editable] <i>No Collaborations added</i> [/#if]
               [/#if]
              </div>
              [#if canEdit && editable]
              <div class="text-right">
                <div class="addProgramCollaboration bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="form.buttons.addProgramCollaborationOrNonCgiar"/]</div>
              </div> 
              [/#if]
              
              [#-- Request institution adition --]
              [#if editable]
              <br />
              <p id="addPartnerText" class="helpMessage">
                [@s.text name="global.addInstitutionMessage" /]
                <a class="popup" href="[@s.url namespace="/projects" action='${crpSession}/partnerSave' ][@s.param name='powbSynthesisID']${(powbSynthesis.id)!}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
                  [@s.text name="projectPartners.addPartnerMessage.second" /]
                </a>
              </p> 
              [/#if]
              
              [#-- Hidden: Global Unit list for Select2 widget --]
              <ul style="display:none">
                [#list globalUnits as globalUnit]
                  <li id="globalUnit-${globalUnit.id}">
                    <strong>${(globalUnit.acronym)!}</strong>
                    <span class="pull-right"><i>(${globalUnit.globalUnitType.name})</i> </span>
                    <p>${(globalUnit.name)!}</p>
                  </li>
                [/#list]
              </ul>
              
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

[#include "/WEB-INF/global/pages/footer.ftl"]

[#---------------------------------------------- MACROS ----------------------------------------------]

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
    [#-- Type of Collaborator --]
    [#local collaboratorTypes = [
      { "name": "CRP/PTF", "value": "1"},
      { "name": "Non-CGIAR", "value": "2"}
    ] 
    /]
    <div class="form-group">
      <label>[@s.text name="liaisonInstitution.powb.milestone.assessment" /]:[@customForm.req required=editable  /]</label><br />
      [#list collaboratorTypes as cType]
        [@customForm.radioFlat id="${customName}-cType-${cType_index}" name="${customName}.collaboratorType" label="${cType.name}" value="${cType.value}" checked=((element.collaboratorType.value == "${cType.value}")!false) editable=editable cssClass="cTypeRadio" cssClassLabel=""/]
      [/#list]
      
      [#local isCollaboratorTypeSelected = (element.collaboratorType??)!false]
      [#if !editable && !isCollaboratorTypeSelected][@s.text name="form.values.fieldEmpty"/][/#if]
    </div>
    
    [#-- CRP/Platform --] 
    <div class="form-group collaboratorType collaboratorType-1" style="display:${((element.collaboratorType.value == "1")!false)?string("block", "none")}"> 
      [@customForm.select name="${customName}.globalUnit.id" label="" keyFieldName="id"  displayFieldName="acronymValid" i18nkey="powbSynthesis.programCollaboration.globalUnit" listName="globalUnits"  required=true  className="globalUnitSelect" editable=isEditable/]
    </div>
    [#-- Institution --]
    <div class="form-group collaboratorType collaboratorType-2" style="display:${((element.collaboratorType.value == "2")!false)?string("block", "none")}">
      [@customForm.select name="${customName}.institution.id" label="" keyFieldName="id"  displayFieldName="composedName" i18nkey="powbSynthesis.programCollaboration.institution" listName="institutions"  required=true  className="institutionsSelect" editable=isEditable/]
    </div>
    
    [#-- Brief Description --] 
    <div class="form-group"> 
      [@customForm.textArea name="${customName}.brief" i18nkey="powbSynthesis.programCollaboration.brief" help="powbSynthesis.programCollaboration.brief.help" placeholder="" className="" required=true editable=isEditable /]
    </div>
    
  </div>
[/#macro]

[#macro tableOverallCRPCollaborationsMacro name crpPrograms]
[#local customName = "${name}" /]
<div class="">
  <table class="table table-bordered">
    <thead>
      <tr>
        <th class="col-md-1"> [@s.text name="collaborationIntegration.tableFlagshipsOverall.fp" /] </th>
        <th> [@s.text name="collaborationIntegration.tableFlagshipsOverall.collaborator" /] </th>
        <th> Description of collaboration and value added </th>
        <th> [@s.text name="evidenceRelevant.tablePlannedStudies.include" /] </th>
      </tr>
    </thead>
    <tbody>
    [#assign collaborations = [] ]
      [#if globalUnitCollaborations??]
        [#list globalUnitCollaborations as coll]
          [#assign collaborations = collaborations + [coll] ]
          <tr>
            <td><span class="programTag" style="border-color:${(coll.powbSynthesis.liaisonInstitution.crpProgram.color)!'#fff'}" title="${coll.powbSynthesis.liaisonInstitution.crpProgram.composedName}">${coll.powbSynthesis.liaisonInstitution.crpProgram.acronym}</span></td>
            <td class="col-md-3">
              <ul>
              [#if coll.globalUnit??]
                <li>${(coll.globalUnit.composedName)!}</li>
              [/#if]
              [#if coll.institution??]
                <li>${coll.institution.composedName}</li>
              [/#if]
              </ul>
            </td>
            <td class="col-md-8">
              ${(coll.brief?replace('\n', '<br>'))!} 
            </td>
            [#-- Include in POWB --]
            <td class="col-md-1 text-center">
              [#local isCollaborationChecked = ((!powbSynthesis.collaboration.collaborationsIds?seq_contains(coll.id))!true) ]
              [@customForm.checkmark id="coll-${(coll.id)!''}" name="${customName}.collaborationsValue" value="${(coll.id)!''}" checked=isCollaborationChecked editable=editable centered=true/]
            </td>
          </tr>
        [/#list]
      [/#if]
    [#if !(collaborations?has_content)]
      <tr>
        <td colspan="4"> <p class="text-center">No Collaborations added.</p></td>
      </tr>
    [/#if]
    </tbody>
  </table>
</div>
[/#macro]