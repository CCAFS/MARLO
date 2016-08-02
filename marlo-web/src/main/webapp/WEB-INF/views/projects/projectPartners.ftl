[#ftl]
[#assign title = "Project Partners" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = ["${baseUrl}/js/projects/projectPartners.js", "${baseUrl}/js/global/usersManagement.js"] /] [#-- "${baseUrl}/js/global/autoSave.js" --]
[#assign currentSection = "projects" /]
[#assign currentStage = "partners" /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"projectPartners", "nameSpace":"/projects", "action":""}
] /]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<div class="container">
  <div class="helpMessage"><img src="${baseUrl}/images/global/icon-help.png" /><p> [@s.text name="projectPartners.help" /] </p></div> 
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
        [#include "/WEB-INF/views/projects/messages-projects.ftl" /]
      
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
           
          <h3 class="headTitle">[@s.text name="projectPartners.title" /]</h3>
          [#-- Listing Partners  --]
          <div class="loadingBlock"></div>
          <div id="projectPartnersBlock" class="simpleBox" style="display:none">
            [#if project.partners?has_content]
              [#list project.partners as projectPartner]
                [@projectPartnerMacro element=projectPartner name="project.partners[${projectPartner_index}]" index=projectPartner_index /]
              [/#list]
            [#else]
              [#if !editable]
                <p class="center">[@s.text name="projectPartners.empty" /]
                <a href="[@s.url][@s.param name ="projectID"]${project.id}[/@s.param][@s.param name="edit"]true[/@s.param][/@s.url]">[@s.text name="form.buttons.clickHere" /]</a> [@s.text name="projectPartners.switchEditingMode" /]
                </p>
              [/#if]
            [/#if] 
            [#if (editable && canEdit)]
              <div class="addProjectPartner bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="projectPartners.addProjectPartner" /]</div>
            [/#if]
          </div> 
           
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/views/projects/buttons-projects.ftl" /]
         
        [/@s.form] 
      </div>
    </div>  
</section>

[#-- Hidden Parameters Interface --]
  <input id="partners-name" type="hidden" value="project.partners" />
  
  [#-- Single partner TEMPLATE from partnersTemplate.ftl --]
  [@projectPartnerMacro element={} name="project.partners" isTemplate=true /]
  
  [#-- Contact person TEMPLATE from partnersTemplate.ftl --]
  [@contactPersonMacro element={} name="contactPerson" isTemplate=true /]
  
  [#-- PPA list Template --]
  <ul style="display:none">
    <li id="ppaListTemplate" class="clearfix">
      <input class="id" type="hidden" name="" value="" />
      <span class="name"></span> 
      [#if editable]<span class="listButton remove">[@s.text name="form.buttons.remove" /]</span>[/#if] 
    </li>
  </ul>
  
  [#-- allPPAInstitutions --]
  <input type="hidden" id="allPPAInstitutions" value="[[#if allPPAInstitutions??][#list allPPAInstitutions as item]${item.id}[#if item_has_next],[/#if][/#list][/#if]]"/>
  
  [#-- Can update PPA Partners --]
  <input type="hidden" id="canUpdatePPAPartners" value="${(action.hasPermission("ppa") || project.bilateralProject)?string}"/>
  
  [#-- Project PPA Partners --]
  <select id="projectPPAPartners" style="display:none">
  [#if project.PPAPartners??]
    [#list project.PPAPartners as ppaPartner]<option value="${ppaPartner.institution.id}">${ppaPartner.institution.getComposedName()}</option>[/#list]
  [/#if]  
  </select>
  
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
<div id="contactChangeType-dialog" title="Change person type" style="display:none">
  <ul class="messages"></ul>
</div>

[#-- Partner person relations dialog --]
<div id="relations-dialog" title="Leading Activities/Deliverables" style="display:none">
</div>

[#-- Search users Interface --]
[#import "/WEB-INF/global/macros/usersPopup.ftl" as usersForm/]
[@usersForm.searchUsers/]
  
[#include "/WEB-INF/global/pages/footer.ftl"]

[#------------------------------------------------------            ------------------------------------------------------]
[#----------------------------------------------------     MACROS     ----------------------------------------------------]
[#------------------------------------------------------            ------------------------------------------------------]

[#macro projectPartnerMacro element name index=-1 isTemplate=false]
  [#local isLeader = (element.leader)!false/]
  [#local isCoordinator = (element.coordinator)!false/]
  
  <div id="projectPartner-${isTemplate?string('template',(projectPartner.id)!)}" class="projectPartner expandableBlock borderBox ${(isLeader?string('leader',''))!} ${(isCoordinator?string('coordinator',''))!}" style="display:${isTemplate?string('none','block')}">
    [#-- Remove link for all partners --]
    [#if editable && (isTemplate)]
      <div class="removeLink"><div id="removePartner" class="removePartner removeElement removeLink" title="[@s.text name="projectPartners.removePartner" /]"></div></div>
    [/#if]
    
    [#-- Partner Title --]
    <div class="blockTitle closed">
      <span class="index">${index+1}</span> <strong class="type"> ${(isLeader?string('(Leader)',''))!} ${(isCoordinator?string('(Coordinator)',''))!}</strong> ${(element.institution.composedName)!'Institution Name'}
    </div>
    
    <div class="blockContent" style="display:none">
      <hr />
      
      [#-- Filters --]
      [#if editable && isTemplate]
        <div class="filters-link">[@s.text name="projectPartners.filters" /]</div>
        <div class="filters-content">
          [#-- Partner type list --]
          <div class="halfPartBlock partnerTypeName chosen">
            [#-- Name attribute is not needed, we just need to load the value, not save it it. --]
            [@customForm.select name="" label="" disabled=!editable i18nkey="projectPartners.partnerType" listName="intitutionTypes" keyFieldName="id"  displayFieldName="name" className="partnerTypes" value="${(element.institution.type.id)!-1}" /]
          </div>
          [#-- Country list --]
          <div class="halfPartBlock countryListBlock chosen">
            [#-- Name attribute is not needed, we just need to load the value, not save it it. --]
            [@customForm.select name="" label="" disabled=!editable i18nkey="projectPartners.country" listName="countries" keyFieldName="id"  displayFieldName="name" className="countryList" value="'${(element.institution.country.id)!-1}'" /]
          </div>
        </div> 
      [/#if]
      
      [#-- Institution / Organization --]
      <div class="form-group">
      [#if ((editable && isTemplate) || (editable && !element.institution??))]
        [@customForm.select name="${name}[${index}].institution" value="${(element.institution.id)!-1}" className="institutionsList" required=true  i18nkey="projectPartners.partner.name" listName="allInstitutions" keyFieldName="id"  displayFieldName="composedName" /]
      [/#if]
      </div>
      
      [#-- Indicate which PPA Partners for second level partners --]
      [#if (editable || ((!editable && element.projectPartnerContributors?has_content)!false)) && (!project.bilateralProject)]
        [#assign showPPABlock][#if (element.institution.PPA)!true]none[#else]block[/#if][/#assign]
        <div class="ppaPartnersList panel tertiary" style="display:${showPPABlock}">
          <div class="panel-head">[@customForm.text name="projectPartners.indicatePpaPartners" readText=!editable /]</div> 
          <div class="panel-body">
            [#if !(element.projectPartnerContributors?has_content) && !editable]
              <p>[@s.text name="projectPartners.noSelectedCCAFSPartners" /] </p>
            [/#if]
            <ul class="list"> 
            [#if element.projectPartnerContributors?has_content]
              [#list element.projectPartnerContributors as ppaPartner]
                <li class="clearfix [#if !ppaPartner_has_next]last[/#if]">
                  <input class="id" type="hidden" name="${name}.partnerContributors[${ppaPartner_index}].institution.id" value="${(ppaPartner.institution.id)!-1}" />
                  <span class="name">${(ppaPartner.institution.composedName)!}</span> 
                  [#if editable]<span class="listButton remove">[@s.text name="form.buttons.remove" /]</span>[/#if]
                </li>
              [/#list]
            [/#if]
            </ul>
            [#if editable]
              [@customForm.select name="" label="" disabled=!canEdit i18nkey="" listName="" keyFieldName="id"  displayFieldName="composedName" className="ppaPartnersSelect" value="" /]
            [/#if] 
          </div>
        </div>
      [/#if]
      
      [#-- Contacts person(s)  --]
      <div class="contactsPerson panel tertiary">
        <div class="panel-head">[@s.text name="projectPartners.projectPartnerContacts" /]</div>
        <div class="fullPartBlock">
        [#if element.projectPartnerPersons?has_content]
          [#list element.projectPartnerPersons as partnerPerson]
            [@contactPersonMacro element=partnerPerson name="${name}[${index}].partnerPersons" index=partnerPerson_index partnerIndex=index /]
          [/#list]
        [#else]
           [@contactPersonMacro element={} name="${name}[0].partnerPersons" index=0 partnerIndex=index /]
        [/#if]  
        [#if (editable && canEdit)]
          <div class="addContact"><a href="" class="addLink">[@s.text name="projectPartners.addContact"/]</a></div> 
        [/#if]
        </div>
      </div>
      
    </div>
  
  </div>
[/#macro]

[#macro contactPersonMacro element name index=-1 partnerIndex=-1 isTemplate=false]
  <div id="contactPerson-${isTemplate?string('template',(element.id)!)}" class="contactPerson simpleBox ${(element.type)!}" style="display:${isTemplate?string('none','block')}">
    [#-- Remove link for all partners --]
    [#if editable]
      <div class="removePerson removeElement" title="[@s.text name="projectPartners.removePerson" /]"></div>
    [/#if]
    <div class="leftHead">
      <span class="index"></span>
    </div>
    <input id="id" class="partnerPersonId" type="hidden" name="${name}.id" value="${(contact.id)!-1}" />
    [#local canEditLeader=(editable && action.hasPermission("leader"))!false /]
    [#local canEditCoordinator=(editable && action.hasPermission("coordinator"))!false /]
     
    [#-- Contact type --]
    <div class="form-group">
      <div class="partnerPerson-type halfPartBlock clearfix">
        [#if canEditLeader]
          [@customForm.select name="${name}.type" className="partnerPersonType" disabled=!canEdit i18nkey="projectPartners.personType" stringKey=true listName="partnerPersonTypes" value="'${(element.type)!'CP'}'" editable=canEditLeader required=true /]
        [#else]
          <h6><label class="readOnly">[@s.text name="projectPartners.personType" /]:</label></h6>
          <div class="select"><p>[@s.text name="projectPartners.types.${(element.type)!'none'}"/]</p></div>
          <input type="hidden" name="${name}.type" class="partnerPersonType" value="${(element.type)!-1}" />
        [/#if]
      </div>
      
      [#-- Contact Email --]
      <div class="partnerPerson-email userField halfPartBlock clearfix">
        [#assign canEditEmail=!((action.getActivitiesLedByUser((element.id)!-1)!false)?has_content) && canEditLeader/]
        <input type="hidden" class="canEditEmail" value="${canEditEmail?string}" />
        [#-- Contact Person information is going to come from the users table, not from project_partner table (refer to the table project_partners in the database) --] 
        [@customForm.input name="partner-${partnerIndex}-person-${index}" value="${(element.user.composedName?html)!}" className="userName" type="text" disabled=!canEdit i18nkey="projectPartners.contactPersonEmail" required=true readOnly=true editable=editable && canEditEmail /]
        <input class="userId" type="hidden" name="${name}.user" value="${(element.user.id)!'-1'}" />   
        [#if editable && canEditEmail]<div class="searchUser button-blue button-float">[@s.text name="form.buttons.searchUser" /]</div>[/#if]
      </div> 
    </div>
    
    [#-- Responsibilities --]
    <div class="form-group partnerResponsabilities chosen"> 
      [@customForm.textArea name="${name}.responsibilities" className="resp" i18nkey="projectPartners.responsabilities" required=!project.bilateralProject editable=editable /]
      <div class="clearfix"></div>
    </div>
    
    
  </div>
[/#macro]



