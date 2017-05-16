[#ftl]
[#assign title = "Project Partners" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}" /]
[#assign pageLibs = ["select2", "flat-flags"] /]
[#assign customJS = ["${baseUrl}/js/global/fieldsValidation.js", "${baseUrl}/js/global/usersManagement.js", "${baseUrl}/js/projects/projectPartners.js", "${baseUrl}/js/global/autoSave.js"] /]  
[#assign customCSS = ["${baseUrl}/css/projects/projectPartners.css"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "partners" /]
[#assign hideJustification = true /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"label":"projectPartners", "nameSpace":"/projects", "action":""}
] /]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]


<div class="container helpText viewMore-block">
  <div style="display:none" class="helpMessage infoText">
    <img class="col-md-2" src="${baseUrl}/images/global/icon-help.jpg" />
    <p class="col-md-10">[#if project.projectEditLeader] [#if reportingActive] [@s.text name="projectPartners.help3" /] [#else] [@s.text name="projectPartners.help2" ] [@s.param][@s.text name="global.managementLiaison" /][/@s.param] [/@s.text] [/#if]  [#else] [@s.text name="projectPartners.help1" /] [/#if]</p>
  </div> 
  <div style="display:none" class="viewMore closed"></div>
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
          <div style="display:none">
            [#-- Other fields --]
            [#if project.projectEditLeader]
            <div class="${(!action.isProjectNew(project.id) || reportingActive)?string('simpleBox','')} ${reportingActive?string('fieldFocus','')}">
              [#-- -- -- REPORTING BLOCK -- -- --]
              [#if reportingActive]
              <br />
              <div class="fullBlock">
                [@customForm.textArea name="project.overall" i18nkey="projectPartners.partnershipsOverall" className="limitWords-100" required=!project.bilateralProject editable=editable /]
              </div>
              [/#if]
              
              [#-- Lessons and progress --]
              [#if !action.isProjectNew(project.id)]
                <div id="lessons" class="">
                  [#-- Lessons learnt from last planning/reporting cycle --]
                  [#if (project.projectComponentLessonPreview.lessons?has_content)!false]
                  <div class="fullBlock">
                    <label>[@customForm.text name="projectPartners.previousLessons.${reportingActive?string('reporting','planning')}" param="${reportingActive?string(reportingYear,planningYear-1)}" /]:</label>
                    <div class="textArea limitWords-100"><p>${project.projectComponentLessonPreview.lessons}</p></div>
                  </div>
                  [/#if]
                  [#-- Planning/Reporting lessons --]
                  <div class="fullBlock">
                    <input type="hidden" name="project.projectComponentLesson.id" value=${(project.projectComponentLesson.id)!"-1"} />
                    <input type="hidden" name="project.projectComponentLesson.year" value=${reportingActive?string(reportingYear,planningYear)} />
                    <input type="hidden" name="project.projectComponentLesson.componentName" value="${actionName}">
                    [@customForm.textArea name="project.projectComponentLesson.lessons" i18nkey="projectPartners.lessons.${reportingActive?string('reporting','planning')}" className="limitWords-100" required=!project.bilateralProject editable=editable /]
                  </div>
                </div>
              [/#if]
            </div>
            [/#if]
          
          
            [#-- Partners list --]
            <div id="projectPartnersBlock" class="simpleBox" listname="project.partners">
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
            
            [#-- Request partner adition --]
            [#if editable && project.projectEditLeader]
            <p id="addPartnerText" class="helpMessage">
              [@s.text name="projectPartners.addPartnerMessage.first" /]
              <a class="popup" href="[@s.url action='${crpSession}/partnerSave'][@s.param name='projectID']${project.id?c}[/@s.param][/@s.url]">
                [@s.text name="projectPartners.addPartnerMessage.second" /]
              </a>
            </p> 
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
[@projectPartnerMacro element={} name="project.partners[-1]" isTemplate=true /]

[#-- Contact person TEMPLATE from partnersTemplate.ftl --]
[@contactPersonMacro element={} name="project.partners[-1].partnerPersons[-1]" isTemplate=true /]

[#-- Country Element Template --]
  [@locElementMacro element={} name="project.partners[-1].selectedLocations" index=-1 isTemplate=true /]

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

[#-- allPPAInstitutions --]
<input type="hidden" id="allPPAInstitutions" value="[[#if allPPAInstitutions??][#list allPPAInstitutions as item]${item.id}[#if item_has_next],[/#if][/#list][/#if]]"/>


[#-- Can update PPA Partners --]
<input type="hidden" id="canUpdatePPAPartners" value="${(action.hasPermission("ppa") || !project.projectEditLeader)?string}"/>

[#-- Project PPA Partners --]
<select id="projectPPAPartners" style="display:none">
[#if project.PPAPartners??]
  [#list project.PPAPartners as ppaPartner]<option value="${ppaPartner.institution.id}">${ppaPartner.institution.composedName}</option>[/#list]
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

[#-- Request partners --]
<div class="modal fade" id="requestModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="loading" style="display:none"></div>
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="exampleModalLabel"></h4>
      </div>
      <div class="modal-body">
        <form>
          <div class="form-group">
            <input type="hidden" name="projectID" value="${(project.id)!}"/>
            <input type="hidden" class="institution_id" name="institutionID" value="" />
            [@customForm.select name="countriesID" i18nkey="location.select.country" listName="countries" header=true keyFieldName="isoAlpha2" displayFieldName="name" value="id" multiple=true placeholder="Select a country..." className="countriesRequest"/]
          </div>
        </form>
        
        <div class="messageBlock" style="display:none">
          <div class="notyMessage">
            <h1 class="text-center brand-success"><span class="glyphicon glyphicon-ok-sign"></span></h1>
            <p  class="text-center col-md-12">
              Your request was sent to the MARLO Support team <br />
              You will receive a confirmation message as soon as it has been processed.
            </p>
            <br />
            [#-- Buttons --]
            <div class="text-center">
              <button class="btn btn-danger" type="button" class="close" data-dismiss="modal" aria-label="Close">Close</button>
            </div>
          </div>
        </div>
      </div>
      <div class="modal-footer"> 
        <button type="button" class="requestButton btn btn-primary"> <span class="glyphicon glyphicon-send"></span> Request</button>
      </div>
    </div>
  </div>
</div>
  
[#include "/WEB-INF/global/pages/footer.ftl"]

[#------------------------------------------------------            ------------------------------------------------------]
[#----------------------------------------------------     MACROS     ----------------------------------------------------]
[#------------------------------------------------------            ------------------------------------------------------]

[#macro projectPartnerMacro element name index=-1 isTemplate=false]
  [#local isLeader = (element.leader)!false/]
  [#local isCoordinator = (element.coordinator)!false/]
  [#local isPPA = (action.isPPA(element.institution))!false /]
  
  <div id="projectPartner-${isTemplate?string('template',(projectPartner.id)!)}" class="projectPartner expandableBlock borderBox ${(isLeader?string('leader',''))!} ${(isCoordinator?string('coordinator',''))!}" style="display:${isTemplate?string('none','block')}">
    [#-- Loading --]
    <div class="loading" style="display:none"></div>
    [#-- Remove link for all partners --]
    [#if editable ] [#--&& (isTemplate) --]
      <div class="removeLink"><div id="removePartner" class="removePartner removeElement removeLink" title="[@s.text name="projectPartners.removePartner" /]"></div></div>
    [/#if]
    
    [#-- Partner Title --]
    <div class="blockTitle closed">
      [#-- Title --]
      <span class="${customForm.changedField('${name}.id')}"> <span class="index_number">${index+1}</span>. <span class="partnerTitle">${(element.institution.composedName)!'New Project Partner'}</span> </span>

      [#-- Tags --]
      <div class="partnerTags pull-right">
        <span class="label label-success type-leader" style="display:${(isLeader?string('inline','none'))!'none'}">Leader</span>
        <span class="label label-default type-coordinator" style="display:${(isCoordinator?string('inline','none'))!'none'}">Coordinator</span>
        <span class="index ${isPPA?string('ppa','')}">${isPPA?string('Managing / PPA Partner','Partner')} </span>
      </div>
      
      [#-- Contacts --]
      [#if (element.partnerPersons)?? ] <br /> 
        <small>[#list element.partnerPersons as partnerPerson][${(partnerPerson.user.composedCompleteName)!}] [/#list]</small> 
      [/#if]
      <div class="clearfix"></div>
    </div>
    
    <div class="blockContent" style="display:none">
      <hr />
      <input id="id" class="partnerId" type="hidden" name="${name}.id" value="${(element.id)!}" />
      
      [#-- Filters  
      [#if ((editable && isTemplate) || (editable && !element.institution??) || (editable && element.institution.id?number == -1))]
        <div class="filters-link"> <span class="glyphicon glyphicon-filter"></span> <span>[@s.text name="projectPartners.filters" /]</span></div>
        <div class="filters-content">
          [#-- Partner type list  
          <div class="col-md-6 partnerTypeName chosen">
            [#-- Name attribute is not needed, we just need to load the value, not save it it.  
            [@customForm.select name="" label="" disabled=!editable i18nkey="projectPartners.partnerType" listName="intitutionTypes" keyFieldName="id"  displayFieldName="name" className="partnerTypes" value="${(element.institution.type.id)!}" /]
          </div>
          --]
          [#-- Country list  
          <div class="col-md-6 countryListBlock chosen">
            [#-- Name attribute is not needed, we just need to load the value, not save it it. 
            [@customForm.select name="" label="" disabled=!editable i18nkey="projectPartners.country" listName="countries" keyFieldName="id"  displayFieldName="name" className="countryList" value="'${(element.institution.country.id)!}'" /]
          </div> 
          <div class="clearfix"></div>
        </div> 
      [/#if]
      --]
      
      [#-- Institution / Organization --]
      [#if ((editable && isTemplate) || (editable && !element.institution??) || (editable && element.institution.id?number == -1))]
      <div class="form-group partnerName">
        <p class="fieldErrorInstitutions"></p>
          [#-- list=allInstitutions --]
          [@customForm.select name="${name}.institution.id" value="${(element.institution.id)!}" className="institutionsList" required=true header=false i18nkey="projectPartners.partner.name" listName="" keyFieldName="id"  displayFieldName="composedName"  /]
      </div>
      [#else]
        <input type="hidden" name="${name}.institution.id" class="institutionsList" value="${(element.institution.id)!}"/>
      [/#if]
      
      [#-- Responsibilities --]
      [#if project.projectEditLeader]
      <div class="form-group partnerResponsabilities chosen"> 
        [@customForm.textArea name="${name}.responsibilities" className="resp limitWords-100" i18nkey="projectPartners.responsabilities" required=true editable=editable /]
        <div class="clearfix"></div>
      </div>
      [/#if]
      
      [#--Select country office  (if applicable)  --] 
      <h5 class="sectionSubTitle">[@s.text name="projectPartners.countryOffices${action.hasSpecificities('crp_partners_office')?string('.required','')}" /] <small>[@customForm.req required=action.hasSpecificities('crp_partners_office') /]</small></h5>
      <div class="countries-list items-list simpleBox" listname="${name}.selectedLocations">
        <ul class="">
          [#if (element.selectedLocations?has_content)!false]
            [#list element.selectedLocations as locElement]
              [@locElementMacro element=locElement name="${name}.selectedLocations" index=locElement_index /]
            [/#list]
          [#else] 
            <p class="message text-center">No country office added</p>
          [/#if]
        </ul>
        <div class="clearfix"></div> 
        [#-- Add Location Element --]
        [#if editable]
          <hr />
          <div class="form-group">
            [@customForm.select name="" showTitle=false i18nkey="location.select.country" listName="${name}.institution.locations" header=true keyFieldName="locElement.isoAlpha2" displayFieldName="composedName" value="id" placeholder="Select a country..." className="countriesList"/]
            <div class="note">
              If you don't find the country office you are looking for, request to have it added by
              <a href="#" class="" data-toggle="modal" data-target="#requestModal">clicking here</a>
            </div>
          </div>
        [/#if]
      </div>
     
      
      [#-- Indicate which PPA Partners for second level partners --]
      [#if (editable || ((!editable && element.partnerContributors?has_content)!false))]
        [#assign showPPABlock][#if isPPA || isTemplate]none[#else]block[/#if][/#assign]
        <div class="ppaPartnersList panel tertiary" listname="${name}.partnerContributors" style="display:${showPPABlock}">
          <h5 class="sectionSubTitle">[@customForm.text name="projectPartners.indicatePpaPartners" readText=!editable /] <small>[@customForm.req required=editable /]</small></h5>
          <div class="panel-body">
            [#if !(element.partnerContributors?has_content) && !editable]
              <p>[@s.text name="projectPartners.noSelectedCCAFSPartners" /] </p>
            [/#if]
            <ul class="list"> 
            [#if element.partnerContributors?has_content]
              [#list element.partnerContributors as ppaPartner]
                <li class="clearfix [#if !ppaPartner_has_next]last[/#if]">
                  <input type="hidden" name="${name}.partnerContributors[${ppaPartner_index}].id" value="${(ppaPartner.id)!}" />
                  <input type="hidden" name="${name}.partnerContributors[${ppaPartner_index}].projectPartnerContributor.id" value="${(ppaPartner.projectPartnerContributor.id)!}"/>
                  <input class="id" type="hidden" name="${name}.partnerContributors[${ppaPartner_index}].projectPartnerContributor.institution.id"  value="${(ppaPartner.projectPartnerContributor.institution.id)!}"/>
                  <span class="name">${(ppaPartner.projectPartnerContributor.institution.composedName)!}</span> 
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
        <h5 class="sectionSubTitle">[@s.text name="projectPartners.projectPartnerContacts" /] <small>[@customForm.req required=isPPA /]</small></h5>
        <div class="fullPartBlock" listname="${name}.partnerPersons">
        [#if element.partnerPersons?has_content]
          [#list element.partnerPersons as partnerPerson]
            [@contactPersonMacro element=partnerPerson name="${name}.partnerPersons[${partnerPerson_index}]" index=partnerPerson_index partnerIndex=index /]
          [/#list]
        [#else]
           [@contactPersonMacro element={} name="${name}.partnerPersons[0]" index=0 partnerIndex=index /]
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
  <div id="contactPerson-${isTemplate?string('template',(element.id)!)}" class="contactPerson simpleBox ${(element.contactType)!}" style="display:${isTemplate?string('none','block')}" listname="partner-${partnerIndex}-person-${index}">
    [#-- Remove link for all partners --]
    [#if editable]
      <div class="removePerson removeElement" title="[@s.text name="projectPartners.removePerson" /]"></div>
    [/#if]
    <div class="leftHead">
      <span class="index"></span>
    </div>
    <input id="id" class="partnerPersonId" type="hidden" name="${name}.id" value="${(element.id)!}" />
    [#local canEditLeader=(editable && action.hasPermission("leader"))!false /]
    [#local canEditCoordinator=(editable && action.hasPermission("coordinator"))!false /]
    
    [#if (element.contactType == "PL")!false]
      [#local canEditContactType = (editable && action.hasPermission("leader"))!false /]
    [#elseif (element.contactType == "PC")!false]
      [#local canEditContactType = (editable && action.hasPermission("coordinator"))!false /]
    [#else]
      [#local canEditContactType = editable || isTemplate /]
    [/#if]
    
    [#if customForm.changedField('${name}.id') != '']
      <span class="label label-info pull-right">Added/Updated</span> 
    [/#if]
    <div class="form-group">
      <div class="row">
          [#-- Contact type --]
          <div class="col-md-4 partnerPerson-type ${customForm.changedField('${name}.contactType')}">
            [#if canEditContactType]
              [@customForm.select name="${name}.contactType" className="partnerPersonType" disabled=!canEdit i18nkey="projectPartners.personType" stringKey=true header=false listName="partnerPersonTypes" value="'${(element.contactType)!'CP'}'" required=true /]
            [#else]
              <label class="readOnly">[@s.text name="projectPartners.personType" /]:</label>
              <div class="select"><p>[@s.text name="projectPartners.types.${(element.contactType)!'none'}"/]</p></div>
              <input type="hidden" name="${name}.contactType" class="partnerPersonType" value="${(element.contactType)!}" />
            [/#if]
          </div>
          [#-- Contact Email --]
          <div class="col-md-8 partnerPerson-email userField" >
            [#attempt]
              [#assign canEditEmail=!((action.getActivitiesLedByUser((element.id)!-1)!false)?has_content) && canEditContactType/]
            [#recover]
              [#assign canEditEmail=true /]
            [/#attempt]
            <input type="hidden" class="canEditEmail" value="${canEditEmail?string}" />
            [#-- Contact Person information is going to come from the users table, not from project_partner table (refer to the table project_partners in the database) --] 
            [#assign partnerClass = "${name}.user.id"?string?replace("\\W+", "", "r") /]
            [#assign changeFieldEmail = customForm.changedField('${name}.user.id') /]
            [@customForm.input name="partner-${partnerIndex}-person-${index}" value="${(element.user.composedName?html)!}" className='userName ${partnerClass} ${changeFieldEmail}' type="text" disabled=!canEdit i18nkey="projectPartners.contactPersonEmail" required=true readOnly=true editable=editable && canEditEmail /]
            <input class="userId" type="hidden" name="${name}.user.id" value="${(element.user.id)!}" />   
            [#if editable && canEditEmail]<div class="searchUser button-blue button-float">[@s.text name="form.buttons.searchUser" /]</div>[/#if]
          </div>
      </div>
    </div> 
    
    [#if !isTemplate]
      [#-- Activities leading and Deliverables with responsibilities --]
      <div class="contactTags fullPartBlock clearfix">
        [#if (element.id??)!false ]
          [#if action.getActivitiesLedByUser(element.id)?has_content]
            <div class="tag activities">[@s.text name="projectPartners.personActivities"][@s.param]${action.getActivitiesLedByUser(element.id)?size}[/@s.param][/@s.text]</div>
            <div class="activitiesList"  style="display:none">
              <h3>Activities</h3>
              <ul>
              [#list action.getActivitiesLedByUser(element.id) as activity]
                <li>${activity.title}  <a target="_blank" href="[@s.url namespace=namespace action='${crpSession}/activities' ][@s.param name='projectID']${project.id?c}[/@s.param][/@s.url]#projectActivity-${activity.id}"><img class="external-link" src="${baseUrl}/images/global/external-link.png" /></a></li>
              [/#list]
              </ul>
            </div>
          [/#if]
          [#if action.getDeliverablesLedByUser(element.id)?has_content]
            <div class="tag deliverables">[@s.text name="projectPartners.personDeliverables"][@s.param]${action.getDeliverablesLedByUser(element.id)?size}[/@s.param][/@s.text]</div>
            <div class="deliverablesList" style="display:none">
              <h3>Deliverables</h3>
              <ul>
              [#list action.getDeliverablesLedByUser(element.id) as deliverable]
                <li>${deliverable.title}  <a target="_blank" href="[@s.url namespace=namespace action='${crpSession}/deliverable' ][@s.param name='deliverableID']${deliverable.id}[/@s.param][/@s.url]"><img class="external-link" src="${baseUrl}/images/global/external-link.png" /></a></li>
              [/#list]
              </ul>
            </div>
          [/#if]
        [/#if]
      </div>
    [/#if]
    
  </div>
[/#macro]

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
