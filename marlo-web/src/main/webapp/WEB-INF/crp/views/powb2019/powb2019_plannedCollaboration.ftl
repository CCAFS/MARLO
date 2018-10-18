[#ftl]
[#assign title = "POWB Synthesis" /]
[#assign currentSectionString = "powb-${actionName?replace('/','-')}-${liaisonInstitutionID}" /]
[#assign pageLibs = [ "select2", "flat-flags", "datatables.net", "datatables.net-bs" ] /]
[#assign customJS = [ "${baseUrlMedia}/js/powb2019/powb2019_plannedCollaboration.js" ] /]
[#assign customCSS = [ "${baseUrlMedia}/css/powb/powbGlobal.css" ] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "plannedCollaborations" /]


[#assign concurrenceEnabled = false /]

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
                [@tableOverallCRPCollaborationsMacro name="powbSynthesis.powbCollaborationGlobalUnitsList" crpPrograms=crpPrograms /]
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
               [/#if]
              </div>
              [#if canEdit && editable]
              <div class="text-right">
                <div class="addProgramCollaboration bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="form.buttons.addProgramCollaborationOrNonCgiar"/]</div>
              </div> 
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

    <div class="form-group row"> 
      [#-- CRP/Platform --] 
      <div class="col-md-5">
        [@customForm.select name="${customName}.globalUnit.id" label="" keyFieldName="id"  displayFieldName="acronymValid" i18nkey="powbSynthesis.programCollaboration.globalUnit" listName="globalUnits"  required=true  className="globalUnitSelect" editable=isEditable/]
      </div>
      <div class="col-md-1 text-center">
        <i>- Or -</i>
      </div>
      [#-- Institution --]
      <div class="col-md-6">
        [@customForm.select name="${customName}.institution.id" label="" keyFieldName="id"  displayFieldName="composedName" i18nkey="powbSynthesis.programCollaboration.institution" listName="institutionsList"  required=true  className="institutionsSelect" editable=isEditable/]
      </div>
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
        <th> CRP or non-CGIAR collaborator </th>
        <th> Description of collaboration and value added </th>
        <th> [@s.text name="evidenceRelevant.tablePlannedStudies.include" /] </th>
      </tr>
    </thead>
    <tbody>
    [#assign collaborations = [] ]
    [#list crpPrograms as crpProgram]
      [#if crpProgram.synthesis.powbCollaborationGlobalUnitsList??]
        [#list crpProgram.synthesis.powbCollaborationGlobalUnitsList as coll]
          [#assign collaborations = collaborations + [coll] ]
          <tr>
            <td><span class="programTag" style="border-color:${(crpProgram.color)!'#fff'}" title="${crpProgram.composedName}">${crpProgram.acronym}</span></td>
            <td class="col-md-3"> 
              <strong>${(coll.globalUnit.acronym)!}</strong><br />
              <i>${(coll.globalUnit.globalUnitType.name)!}</i>
            </td>
            <td class="col-md-8">
              ${(coll.brief?replace('\n', '<br>'))!} 
            </td>
            [#-- Include in POWB --]
            <td class="col-md-1 text-center">
              [#local isCollaborationChecked = ((!powbSynthesis.powbCollaborationGlobalUnitsList.plannedCollabortionsIds?seq_contains(coll.id))!true) ]
              [@customForm.checkmark id="coll-${(coll.id)!''}" name="${customName}.plannedCollabortions" value="${(coll.id)!''}" checked=isCollaborationChecked editable=editable centered=true/]
            </td>
          </tr>
        [/#list]
      [/#if]
    [/#list]
    [#if !(collaborations?has_content)]
      <tr>
        <td colspan="4"> <p class="text-center">No Collaborations added.</p></td>
      </tr>
    [/#if]
    </tbody>
  </table>
</div>
[/#macro]