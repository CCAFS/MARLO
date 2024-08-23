[#ftl]
[#assign title = "SHFRM Management" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2", "flag-icon-css", "datatables.net", "datatables.net-bs"] /]
[#assign customJS = [ 
  "${baseUrlMedia}/js/admin/allianceLeversManagement.js",
  "${baseUrlCdn}/global/js/fieldsValidation.js",
  "${baseUrlCdn}/global/js/usersManagement.js?20230927"
  ] 
/]
[#assign customCSS = [ "${baseUrlMedia}/css/admin/crpPhases.css", "${baseUrlMedia}/css/annualReport/annualReportGlobal.css?20240213"] /]
[#assign currentSection = "admin" /]
[#assign currentStage = "allianceLeversManagement" /]
[#assign hideJustification = true /]

[#assign breadCrumb = [
  {"label":"superadmin", "nameSpace":"", "action":"allianceLeversManagement"},
  {"label":"allianceLeversManagement", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]

[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/crp/macros/relationsPopupMacro.ftl" as popUps /]

    
[#if (!availabePhase)!false]
  [#include "/WEB-INF/crp/views/projects/availability-projects.ftl" /]
[#else]
<section class="container">

    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/admin/menu-admin.ftl" /]
      </div>
      [#-- Project Section Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
          <div class="containerTitlePartners">
            <h4 class="sectionTitle">[@s.text name="allianceLeversManagement.primaryLevers" /]</h4>
          </div>
          [#-- Listing Partners  --]
          
            [#-- Priority actions list --]
            <div id="projectPartnersBlock" class="simpleBox" listname="primaryLevers">
              [#if primaryLevers?has_content]
                [#list primaryLevers as primaryLever]
                  [@projectPartnerMacro element=primaryLever!{} name="primaryLevers[${primaryLever_index}]" index=primaryLever_index opened=(primaryLevers?size = 1)/]
                [/#list]
              [#else]
                [@projectPartnerMacro element={} name="primaryLevers[0]" index=0 opened=true defaultPerson=true /]

              [/#if] 
                <div class="addProjectPartner bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="allianceLeversManagement.primaryLevers.add" /]</div>
            </div> 
            <br>
            
            [#-- Related levers list --]
            <h4 class="sectionTitle">[@s.text name="allianceLeversManagement.relatedLevers" /]</h4>
            <div id="projectPartnersBlock2" class="simpleBox" listname="relatedLevers">
              [#if relatedLevers?has_content]
                [#list relatedLevers as relatedLever]
                  [@projectPartnerMacro2 element=relatedLever!{} name="relatedLevers[${relatedLever_index}]" index=relatedLever_index opened=(relatedLevers?size = 1)/]
                [/#list]
              [#else]
                [@projectPartnerMacro2 element={} name="relatedLevers[0]" index=0 opened=true defaultPerson=true /]

              [/#if] 
                <div class="addProjectPartner2 bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="allianceLeversManagement.relatedLevers.add" /]</div>
            </div> 
                        
          [#-- Section Buttons--]
          <div class="buttons">
            <div class="buttons-content">
              [@s.submit type="button" name="save" cssClass="button-save"]<span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span> [@s.text name="form.buttons.save" /][/@s.submit]
            </div>
          </div>
          
         
        [/@s.form] 
      </div>
    </div>  
</section>
[/#if]

[#-- Single partner TEMPLATE from partnersTemplate.ftl --]
[@projectPartnerMacro element={} name="primaryLevers[-1]" isTemplate=true /]

[#-- Contact person TEMPLATE from partnersTemplate.ftl --]
[@contactPersonMacro element={} name="primaryLevers[-1].primaryStrategicOutcomes[-1]" isTemplate=true /]

[#-- Single partner TEMPLATE from partnersTemplate.ftl --]
[@projectPartnerMacro2 element={} name="relatedLevers[-1]" isTemplate=true /]

[#-- Contact person TEMPLATE from partnersTemplate.ftl --]
[@contactPersonMacro2 element={} name="relatedLevers[-1].relatedLeverSdgContribution[-1]" isTemplate=true /]

[#-- Project roles descriptions --]
<span class="contactPersonRole-PC" style="display:none">[@s.text name="projectPartners.contactPersonRolePC" /]</span>
<span class="contactPersonRole-PL" style="display:none">[@s.text name="projectPartners.contactPersonRolePL" /]</span>
<span class="contactPersonRole-CP" style="display:none">[@s.text name="projectPartners.contactPersonRoleCP" /]</span>

[#-- allPPAInstitutions --]
<input type="hidden" id="allPPAInstitutions" value="[[#if allPPAInstitutions??][#list allPPAInstitutions as item]${item.id}[#if item_has_next],[/#if][/#list][/#if]]"/>
  
[#-- Remove Partner Dialog --]
<div id="partnerRemove-dialog" title="Remove partner" style="display:none">
  <p>[@s.text name="projectPartners.partnerCannotBeDeleted" /]</p>
  <ul class="messages"></ul>
</div>

[#-- Remove partner person leader dialog --]
<div id="contactRemove-dialog" title="Remove person" style="display:none">
  <p>[@s.text name="projectPartners.personCannotBeDeleted" /]</p>
  <ul class="messages"></ul>
</div>

[#-- Change partner person email dialog --]
<div id="contactChange-dialog" title="Change contact person" style="display:none">
  <ul class="messages"></ul>
</div>

[#-- Change partner person type dialog --]
<div id="contactChangeType-dialog" title="Change contact person’s role" style="display:none">
  <ul class="messages"></ul>
</div>

[#-- Partner person relations dialog --]
<div id="relations-dialog" title="Leading Activities/Deliverables" style="display:none">
</div>

  
[#include "/WEB-INF/global/pages/footer.ftl"]

[#------------------------------------------------------            ------------------------------------------------------]
[#----------------------------------------------------     MACROS     ----------------------------------------------------]
[#------------------------------------------------------            ------------------------------------------------------]

[#macro projectPartnerMacro element name index=-1 opened=false defaultPerson=false isTemplate=false]
  
  <div id="projectPartner-${isTemplate?string('template',(projectPartner.id)!)}" class="projectPartner expandableBlock borderBox" style="display:${isTemplate?string('none','block')}">
    [#-- Loading --]
    <div class="loading" style="display:none"></div>
    [#-- TODO: Please improve this validation at backend side --]

    [#-- Remove link for all partners --]
    [#if isTemplate || action.canBeDeleted((element.id)!-1,(element.class.name)!)!false]
      <div class="removeLink"><div id="removePartner" class="removePartner removeElement removeLink" title="[@s.text name="projectPartners.removePartner" /]"></div></div>
    [/#if]
    
    [#-- Partner Title --]
    <div class="blockTitle ${opened?string('opened', 'closed')}">
      
    [#-- Title --]
      <span class="${customForm.changedField('${name}.id')}"> <span class="index_number">${index+1}</span>. <span class="primaryLeverTitle">${(element.name)!'Primary Alliance Lever'}</span> </span>            
    </div>
    

    <div class="blockContent" style="display:${opened?string('block','none')}">
      <hr />
            
      [#-- Action name  --]
      <input id="id" class="partnerPersonId" type="hidden" name="${name}.id" value="${(element.id)!}" />

      <div class="form-group">
        [@customForm.input name="${name}.name" i18nkey="allianceLeversManagement.primaryLevers.title" className="name" required=true /]
      </div>
      <div class="clearfix"></div>
      [#-- Action description  --]
      <div class="form-group">
        [@customForm.input name="${name}.description" i18nkey="allianceLeversManagement.primaryLevers.description" className="description" required=true /]
      </div>
      <div class="clearfix"></div>      
                 
      
      [#-- Sub Actions  --]
      <div class="contactsPerson panel tertiary">
        <h5 class="sectionSubTitle">[@s.text name="allianceLeversManagement.strategicOutcomes" /] <small>[@customForm.req required=true /]</small></h5>
        <div class="fullPartBlock" listname="${name}.primaryStrategicOutcomes">
          [#if element.primaryStrategicOutcomes?has_content]
            [#list element.primaryStrategicOutcomes as subAction]
              [@contactPersonMacro element=subAction name="${name}.primaryStrategicOutcomes[${subAction_index}]" index=subAction_index partnerIndex=index institutionID=(element.institution.id)! /]
            [/#list]
          [#else]            
              <p class="noContactMessage">[@s.text name="allianceLeversManagement.strategicOutcomesEmpty" /]</p>
          [/#if]  
          [#if (editable && canEdit)]
            <div class="addContact bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span>[@s.text name="allianceLeversManagement.strategicOutcomes.add"/]</div>
          [/#if]

        </div>
      </div>
      
    </div>
       

  </div>
[/#macro]

[#macro contactPersonMacro element name index=-1 partnerIndex=-1 isTemplate=false institutionID=-1]
  <div id="contactPerson-${isTemplate?string('template',(element.id)!)}" class="contactPerson simpleBox ${(element.contactType)!}" style="display:${isTemplate?string('none','block')}" listname="partner-${partnerIndex}-person-${index}">
    [#-- Remove link for all partners --]
    [#if editable]
      <div class="removePerson removeElement" title="[@s.text name="projectPartners.removePerson" /]"></div>
    [/#if]
    <div class="leftHead">
      <span class="index"></span>
    </div>
    <input id="id" class=subActionnId" type="hidden" name="${name}.id" value="${(element.id)!}" />
    
    [#if customForm.changedField('${name}.id') != '']
      <span class="label label-info pull-right">Added/Updated</span> 
    [/#if]
    <div class="">
      <div class="form-group row">
        [#-- Sub action name --]
        <div class="col-md-4 partnerPerson-type ${customForm.changedField('${name}.contactType')}">
            [@customForm.input name="${name}.name" className="partnerPersonType" editable=editable i18nkey="allianceLeversManagement.strategicOutcomes.title" value="${(element.name)!}" /]
        </div>
        [#-- Sub action description --]
        <div class="col-md-8 userField" >         
          [@customForm.input name="${name}.description" value="${(element.description)!}" type="text" i18nkey="allianceLeversManagement.strategicOutcomes.description" required=true editable=editable /]
        </div>
      </div>
           
    </div> 
        
  </div>
[/#macro]

[#macro projectPartnerMacro2 element name index=-1 opened=false defaultPerson=false isTemplate=false]
  
  <div id="projectPartner-${isTemplate?string('template',(projectPartner.id)!)}" class="projectPartner expandableBlock borderBox" style="display:${isTemplate?string('none','block')}">
    [#-- Loading --]
    <div class="loading" style="display:none"></div>
    [#-- TODO: Please improve this validation at backend side --]

    [#-- Remove link for all partners --]
    [#if isTemplate || action.canBeDeleted((element.id)!-1,(element.class.name)!)!false]
      <div class="removeLink"><div id="removePartner" class="removePartner removeElement removeLink" title="[@s.text name="projectPartners.removePartner" /]"></div></div>
    [/#if]
    
    [#-- Partner Title --]
    <div class="blockTitle ${opened?string('opened', 'closed')}">
      
    [#-- Title --]
      <span class="${customForm.changedField('${name}.id')}"> <span class="index_number">${index+1}</span>. <span class="primaryLeverTitle">${(element.name)!'Related Alliance Lever'}</span> </span>            
    </div>
    

    <div class="blockContent" style="display:${opened?string('block','none')}">
      <hr />
            
      [#-- Action name  --]
      <input id="id" class="partnerPersonId" type="hidden" name="${name}.id" value="${(element.id)!}" />

      <div class="form-group">
        [@customForm.input name="${name}.name" i18nkey="allianceLeversManagement.relatedLevers.title" className="name" required=true /]
      </div>
      <div class="clearfix"></div>
      [#-- Action description  --]
      <div class="form-group">
        [@customForm.input name="${name}.description" i18nkey="allianceLeversManagement.relatedLevers.description" className="description" required=true /]
      </div>
      <div class="clearfix"></div>      
                 
      
      [#-- Sub Actions  --]
      <div class="contactsPerson panel tertiary">
        <h5 class="sectionSubTitle">[@s.text name="allianceLeversManagement.sdgContribution" /] <small>[@customForm.req required=true /]</small></h5>
        <div class="fullPartBlock" listname="${name}.relatedLeverSdgContributions">
          [#if element.relatedLeverSdgContributions?has_content]
            [#list element.relatedLeverSdgContributions as subAction]
              [@contactPersonMacro element=subAction name="${name}.relatedLeverSdgContribution[${subAction_index}]" index=subAction_index partnerIndex=index institutionID=(element.institution.id)! /]
            [/#list]
          [#else]            
              <p class="noContactMessage">[@s.text name="allianceLeversManagement.sdgContributionEmpty" /]</p>
          [/#if]  
          [#if (editable && canEdit)]
            <div class="addContact bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span>[@s.text name="allianceLeversManagement.sdgContribution.add"/]</div>
          [/#if]

        </div>
      </div>
      
    </div>
       

  </div>
[/#macro]

[#macro contactPersonMacro2 element name index=-1 partnerIndex=-1 isTemplate=false institutionID=-1]
  <div id="contactPerson-${isTemplate?string('template',(element.id)!)}" class="contactPerson simpleBox ${(element.contactType)!}" style="display:${isTemplate?string('none','block')}" listname="partner-${partnerIndex}-person-${index}">
    [#-- Remove link for all partners --]
    [#if editable]
      <div class="removePerson removeElement" title="[@s.text name="projectPartners.removePerson" /]"></div>
    [/#if]
    <div class="leftHead">
      <span class="index"></span>
    </div>
    <input id="id" class=subActionnId" type="hidden" name="${name}.id" value="${(element.id)!}" />
    
    [#if customForm.changedField('${name}.id') != '']
      <span class="label label-info pull-right">Added/Updated</span> 
    [/#if]
    <div class="">
      <div class="form-group row">
        [#-- Sub action name --]
        <div class="col-md-4 partnerPerson-type ${customForm.changedField('${name}.contactType')}">
            [@customForm.input name="${name}.name" className="partnerPersonType" editable=editable i18nkey="allianceLeversManagement.sdgContribution.title" value="${(element.name)!}" /]
        </div>
        [#-- Sub action description --]
        <div class="col-md-8 userField" >         
          [@customForm.input name="${name}.description" value="${(element.description)!}" type="text" i18nkey="allianceLeversManagement.sdgContribution.description" required=true editable=editable /]
        </div>
      </div>
           
    </div> 
        
  </div>
[/#macro]
