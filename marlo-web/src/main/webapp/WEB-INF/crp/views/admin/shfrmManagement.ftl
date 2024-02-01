[#ftl]
[#assign title = "SHFRM Management" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2", "flag-icon-css"] /]
[#assign customJS = [ 
  "${baseUrlMedia}/js/admin/shfrmManagement.js",
  "${baseUrlCdn}/global/js/fieldsValidation.js",
  "${baseUrlCdn}/global/js/usersManagement.js?20230927"
  ] 
/]
[#assign customCSS = [ "${baseUrlMedia}/css/admin/crpPhases.css" ] /]
[#assign currentSection = "admin" /]
[#assign currentStage = "shfrmManagement" /]
[#assign hideJustification = true /]

[#assign breadCrumb = [
  {"label":"superadmin", "nameSpace":"", "action":"adminManagement"},
  {"label":"shfrmManagement", "nameSpace":"", "action":""}
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
            <h3 class="headTitle">[@s.text name="shfrmManagement.title" /]</h3>
          </div>
          [#-- Listing Partners  --]
          
            [#-- Partners list --]
            <div id="projectPartnersBlock" class="simpleBox" listname="priorityActions">
              [#if priorityActions?has_content]
                [#list priorityActions as priorityAction]
                  [@projectPartnerMacro element=priorityAction!{} name="priorityActions[${priorityAction_index}]" index=priorityAction_index opened=(priorityActions?size = 1)/]
                [/#list]
              [#else]
                [@projectPartnerMacro element={} name="priorityActions[0]" index=0 opened=true defaultPerson=true /]

              [/#if] 
                <div class="addProjectPartner bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="shfrmManagement.priorityActions.add" /]</div>
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
[@projectPartnerMacro element={} name="priorityActions[-1]" isTemplate=true /]

[#-- Contact person TEMPLATE from partnersTemplate.ftl --]
[@contactPersonMacro element={} name="priorityActions[-1].shfrmSubActions[-1]" isTemplate=true /]

[#-- PPA list Template --]
<ul style="display:none">
  <li id="ppaListTemplate" class="clearfix">
    <input type="hidden"            name="project.partners[-1].partnerContributors[-1].id" />
    <input type="hidden"            name="project.partners[-1].partnerContributors[-1].projectPartnerContributor.id" />
    <input class="id" type="hidden" name="project.partners[-1].partnerContributors[-1].projectPartnerContributor.institution.id" value="" />
    <span class="name"></span> 
    [#if editable]<span class="listButton remove">[@s.text name="form.buttons.remove" /]</span>[/#if] 
  </li>
</ul>

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
  [#local isLeader = (element.leader)!false/]
  [#local isCoordinator = (element.coordinator)!false/]
  [#local isPPA = (action.isPPA(element.institution))!false /]
  [#local allowSubDep = ((element.subDepartment?has_content)!false) || ((element.institution.institutionType.subDepartmentActive)!false) ]
  
  <div id="projectPartner-${isTemplate?string('template',(projectPartner.id)!)}" class="projectPartner expandableBlock borderBox ${(isLeader?string('leader',''))!} ${(isCoordinator?string('coordinator',''))!}" style="display:${isTemplate?string('none','block')}">
    [#-- Loading --]
    <div class="loading" style="display:none"></div>
    [#-- TODO: Please improve this validation at backend side --]

    [#-- Remove link for all partners --]
    [#if editable ] [#--&& (isTemplate) --]
      <div class="removeLink"><div id="removePartner" class="removePartner removeElement removeLink" title="[@s.text name="projectPartners.removePartner" /]"></div></div>
    [/#if]
    
    [#-- Partner Title --]
    <div class="blockTitle ${opened?string('opened', 'closed')}">
      [#-- Title --]
      <span class="${customForm.changedField('${name}.id')}"> <span class="index_number">${index+1}</span>. <span class="priorityActionTitle">${(element.shfrmPriorityAction.title)!'Priority Action'}</span> </span>
      
      [#-- Tags --]
      <div class="partnerTags pull-right">
        <span class="label label-success type-leader" style="display:${(isLeader?string('inline','none'))!'none'}">Leader</span>
        <span class="label label-default type-coordinator" style="display:${(isCoordinator?string('inline','none'))!'none'}">Coordinator</span>
        <span class="index ${isPPA?string('ppa','')}">${isPPA?string('Managing Partner','Partner')} </span>
      </div>
      
      [#-- Contacts --]
      [#if (element.partnerPersons)?? ] <br /> 
        <small>[#list element.partnerPersons as partnerPerson]
          [#if partnerPerson.user?? && partnerPerson.user.firstName??]
            [${(partnerPerson.user.composedCompleteName)!}]
          [/#if]
        [/#list]</small> 
      [/#if]
      <div class="clearfix"></div>
    </div>
    
    <div class="blockContent" style="display:${opened?string('block','none')}">
      <hr />
            
      [#-- Action name  --]
      <input id="id" class="partnerPersonId" type="hidden" name="${name}.id" value="${(element.id)!}" />

      <div class="form-group">
        [@customForm.input name="${name}.name" i18nkey="shfrmManagement.priorityActions.title" className="name limitWords-100" required=true /]
      </div>
      <div class="clearfix"></div>
      [#-- Action description  --]
      <div class="form-group">
        [@customForm.input name="${name}.description" i18nkey="shfrmManagement.priorityActions.description" className="description limitWords-100" required=true /]
      </div>
      <div class="clearfix"></div>      
                 
      
      [#-- Sub Actions  --]
      <div class="contactsPerson panel tertiary">
        <h5 class="sectionSubTitle">[@s.text name="shfrmManagement.subActions.add" /] <small>[@customForm.req required=true /]</small></h5>
        <div class="fullPartBlock" listname="${name}.shfrmSubActions">
        [#if element.shfrmSubActions?has_content]
          [#list element.shfrmSubActions as subAction]
            [@contactPersonMacro element=subAction name="${name}.subActions[$subAction_index}]" index=subAction_index partnerIndex=index institutionID=(element.institution.id)! /]
          [/#list]
        [#else]
          [#if isPPA || defaultPerson]
            [@contactPersonMacro element={} name="${name}subActions[0]" index=0 partnerIndex=index institutionID=(element.institution.id)!-1 /]
          [#else]
            <p class="noContactMessage">[@s.text name="shfrmManagement.subActionsEmpty" /]</p>
          [/#if]
        [/#if]  
        [#if (editable && canEdit)]
          <div class="addContact bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span>[@s.text name="shfrmManagement.subActions.add"/]</div>
        [/#if]
        [#--  
        <br>
        <div class="addProjectPartner bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="shfrmManagement.priorityActions.add" /]</div>
--]
        </div>
      </div>
      
    </div>
    
    [#-- Deliverables --]
    [#if !isTemplate] 
    <div class="pull-right">
      [@popUps.relationsMacro element=element /]
    </div>
    [/#if]
  
  </div>
[/#macro]

[#macro contactPersonMacro element name index=-1 partnerIndex=-1 isTemplate=false institutionID=-1]
  <div id="contactPerson-${isTemplate?string('template',(element.id)!)}" class="contactPerson simpleBox ${(element.contactType)!}" style="display:${isTemplate?string('none','block')}" listname="partner-${partnerIndex}-person-${index}">
    [#-- Remove link for all partners --]
    [#if editable && action.canBeDeleted((element.id)!-1,(element.class.name)!)]
      <div class="removePerson removeElement" title="[@s.text name="projectPartners.removePerson" /]"></div>
    [/#if]
    <div class="leftHead">
      <span class="index"></span>
    </div>
    <input id="id" class=subActionnId" type="hidden" name="${name}.id" value="${(element.id)!}" />
    [#local isPPA = (action.isPPA(element.projectPartner.institution))!false /]
    
    [#if customForm.changedField('${name}.id') != '']
      <span class="label label-info pull-right">Added/Updated</span> 
    [/#if]
    <div class="">
      <div class="form-group row">
        [#-- Sub action name --]
        <div class="col-md-4 partnerPerson-type ${customForm.changedField('${name}.contactType')}">
            [@customForm.input name="${name}.name" className="partnerPersonType" editable=editable i18nkey="shfrmManagement.subActions.title" value="${(element.name)!}" /]
        </div>
        [#-- Sub action description --]
        <div class="col-md-8 userField" >
          
          [@customForm.input name="${name}.description" value="${(element.description)!}" type="text" i18nkey="shfrmManagement.subActions.description" required=true editable=editable /]
          <input class="userId" type="hidden" name="${name}.description" value="${(element.description)!}" />   
        </div>
      </div>
           
    </div> 
        
  </div>
[/#macro]
