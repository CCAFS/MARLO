[#ftl]
[#assign title = "Cluster Partners" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2", "flag-icon-css"] /]
[#assign customJS = [
  "${baseUrlCdn}/global/js/fieldsValidation.js",
  "${baseUrlCdn}/global/js/usersManagement.js?20260925",
  "${baseUrlMedia}/js/projects/projectPartners.js?20261009-5"
  ]
/]
[#assign customCSS = ["${baseUrlMedia}/css/projects/projectPartners.css?20261009-4"] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "partners" /]
[#assign hideJustification = true /]

[#if !action.isAiccra()]
  [#assign breadCrumb = [
    {"label":"projectsList", "nameSpace":"projects", "action":"${(crpSession)!}/projectsList"},
    {"text":"P${project.id}", "nameSpace":"projects", "action":"${crpSession}/description", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
    {"label":"projectPartners", "nameSpace":"projects", "action":""}
  ] /]
[#else]
  [#assign breadCrumb = [
    {"label":"projectsList", "nameSpace":"clusters", "action":"${(crpSession)!}/projectsList"},
    {"text":"C${project.id}", "nameSpace":"clusters", "action":"${crpSession}/description", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
    {"label":"projectPartners", "nameSpace":"clusters", "action":""}
  ] /]
[/#if]

[#assign partnerRespRequired = action.hasSpecificities('crp_nonPPAPartner_resp_required') ]
[#-- The specificities ProjectPartnersValidator gates its rules on. projectPartners.js mirrors
     the same rules to flag what each partner still misses, so it needs the same switches. --]
[#assign partnerOfficeRequired = action.hasSpecificities('crp_partners_office') ]
[#assign managingContactsRequired = action.hasSpecificities('crp_managing_partners_contact_persons') ]
[#assign projectEditLeader = (project.projectInfo.isProjectEditLeader())!false ]
[#assign permissionLeader = action.hasPermission("leader") ]
[#assign permissionCoordinator = action.hasPermission("coordinator") ]
[#assign canAddContacts = (editable && canEdit)!false ]
[#-- "project" / "cluster" / "team", per global unit --]
[#assign sectionNoun = (action.getText("projects.menu.status.noun"))!"project" /]
[#assign contactRoles = ["PL", "PC", "CP"] /]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/crp/macros/relationsPopupMacro.ftl" as popUps /]

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
        <div class="ptn" data-editable="${editable?string}">

          [#-- Header: title, video tutorial and the section summary --]
          <div class="ptn-head">
            <div class="ptn-head__main">
              <span class="ptn-head__titleRow">
                <h1 class="ptn-head__title">[@s.text name="projectPartners.title" /]</h1>
                <a class="ptn-chip ptn-chip--link" target="_blank" rel="noopener noreferrer" href="https://drive.google.com/file/d/1WSvnbRH94ddzCF-pR0n6tt1scb-WA3vk/view">
                  <svg width="13" height="13" viewBox="0 0 16 16" fill="none" aria-hidden="true"><rect x="1.8" y="3" width="12.4" height="10" rx="2" stroke="currentColor" stroke-width="1.4"></rect><path d="M6.6 6v4l3.4-2-3.4-2Z" fill="currentColor"></path></svg>
                  [@s.text name="projectPartners.redesign.videoTutorial" /]
                </a>
              </span>
              <span class="ptn-head__summary" data-ptn-summary></span>
            </div>
          </div>

          [#-- Instructions, plus the legend behind "View more" --]
          <div class="ptn-note" role="note">
            <span class="ptn-note__icon" aria-hidden="true">i</span>
            <div class="ptn-note__body">
              <p class="ptn-note__text">
                [#if projectEditLeader][#if reportingActive][@s.text name="projectPartners.help3" /][#else][@s.text name="projectPartners.help2" ][@s.param][@s.text name="global.managementLiaison" /][/@s.param][/@s.text][/#if][#else][@s.text name="projectPartners.help1" /][/#if]
              </p>
              <div class="ptn-note__extra" id="ptn-note-extra" data-ptn-note-extra hidden></div>
              <div class="ptn-legend" id="ptn-legend" hidden>
                <span class="ptn-legend__item"><span class="ptn-tag ptn-tag--leader">[@s.text name="projectPartners.types.PL" /]</span><span class="ptn-legend__text">[@s.text name="projectPartners.redesign.legend.leader"][@s.param]${sectionNoun}[/@s.param][/@s.text]</span></span>
                <span class="ptn-legend__item"><span class="ptn-tag ptn-tag--coordinator">[@s.text name="projectPartners.types.PC" /]</span><span class="ptn-legend__text">[@s.text name="projectPartners.redesign.legend.coordinator" /]</span></span>
                <span class="ptn-legend__item"><span class="ptn-tag ptn-tag--managing">[@s.text name="projectPartners.redesign.tag.managing" /]</span><span class="ptn-legend__text">[@s.text name="projectPartners.redesign.legend.managing" /]</span></span>
                <span class="ptn-legend__item"><span class="ptn-tag ptn-tag--partner">[@s.text name="projectPartners.redesign.tag.partner" /]</span><span class="ptn-legend__text">[@s.text name="projectPartners.redesign.legend.partner"][@s.param]${sectionNoun}[/@s.param][/@s.text]</span></span>
              </div>
            </div>
            <button type="button" class="ptn-link ptn-note__more" aria-expanded="false" aria-controls="ptn-note-extra ptn-legend" data-ptn-legend-toggle
              data-label-more="[@s.text name="projectPartners.redesign.viewMore" /]" data-label-less="[@s.text name="projectPartners.redesign.viewLess" /]">[@s.text name="projectPartners.redesign.viewMore" /]</button>
          </div>

          [#-- Listing Partners  --]
          <div class="loadingBlock"></div>
          <div class="ptn-content" style="display:none">

            [#-- Toolbar: search, partner-type filter, expand all, add partner --]
            <div class="ptn-toolbar">
              <span class="ptn-search">
                <svg width="15" height="15" viewBox="0 0 16 16" fill="none" aria-hidden="true"><circle cx="7" cy="7" r="4.6" stroke="currentColor" stroke-width="1.5"></circle><path d="M10.6 10.6 14 14" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"></path></svg>
                <input type="text" id="partnersSearch" class="ptn-input partnersSearch" placeholder="[@s.text name='projectPartners.redesign.search.placeholder' /]" aria-label="[@s.text name='projectPartners.redesign.search.label' /]" autocomplete="off" />
              </span>
              <span class="ptn-seg" role="group" aria-label="[@s.text name='projectPartners.redesign.filter.label' /]">
                <button type="button" class="ptn-seg__btn is-on" data-ptn-filter="all" aria-pressed="true">[@s.text name="projectPartners.redesign.filter.all" /] <span class="ptn-seg__count" data-ptn-count="all"></span></button>
                <button type="button" class="ptn-seg__btn" data-ptn-filter="mp" aria-pressed="false">[@s.text name="projectPartners.redesign.filter.managing" /] <span class="ptn-seg__count" data-ptn-count="mp"></span></button>
                <button type="button" class="ptn-seg__btn" data-ptn-filter="partner" aria-pressed="false">[@s.text name="projectPartners.redesign.filter.partners" /] <span class="ptn-seg__count" data-ptn-count="partner"></span></button>
              </span>
              <span class="ptn-toolbar__end">
                <button type="button" class="ptn-link" data-ptn-expand-all
                  data-label-expand="[@s.text name="projectPartners.redesign.expandAll" /]" data-label-collapse="[@s.text name="projectPartners.redesign.collapseAll" /]">[@s.text name="projectPartners.redesign.expandAll" /]</button>
                [#if (editable && canEdit)]
                  <button type="button" class="ptn-btn ptn-btn--primary addProjectPartner">
                    <svg width="13" height="13" viewBox="0 0 14 14" fill="none" aria-hidden="true"><path d="M7 2.6v8.8M2.6 7h8.8" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"></path></svg>
                    [@s.text name="projectPartners.addProjectPartner" /]
                  </button>
                [/#if]
              </span>
            </div>

            [#-- Partners list --]
            <div id="projectPartnersBlock" class="ptn-list" listname="project.partners">
              [#if project.partners?has_content]
                [#list project.partners as projectPartner]
                  [@projectPartnerMacro element=projectPartner!{} name="project.partners[${projectPartner_index}]" index=projectPartner_index opened=(project.partners?size = 1)/]
                [/#list]
              [/#if]
            </div>

            [#if (editable && canEdit)]
              <button type="button" class="ptn-addCard addProjectPartner">
                <svg width="14" height="14" viewBox="0 0 14 14" fill="none" aria-hidden="true"><path d="M7 2.6v8.8M2.6 7h8.8" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"></path></svg>
                [@s.text name="projectPartners.addProjectPartner" /]
              </button>
            [/#if]

            [#-- Shown by projectPartners.js when no partner matches the search or the filter --]
            <div class="ptn-empty partnersSearch-empty" style="display:none">
              <span class="ptn-empty__title">[@s.text name="projectPartners.redesign.empty.title" /]</span>
              <span class="ptn-empty__text">[@s.text name="projectPartners.redesign.empty.text" /]</span>
              <button type="button" class="ptn-btn ptn-btn--ghost ptn-btn--sm" data-ptn-clear-filters>[@s.text name="projectPartners.redesign.empty.clear" /]</button>
            </div>
            [#-- No partner at all yet --]
            <div class="ptn-empty ptn-empty--none" style="display:${(project.partners?has_content)?string('none','flex')}">
              <span class="ptn-empty__title">[@s.text name="projectPartners.redesign.noPartners" /]</span>
            </div>

            [#-- Request partner addition --]
            [#if editable]
            <p id="addPartnerText" class="ptn-foot">
              [@s.text name="projectPartners.addPartnerMessage.first" /]
              <a class="popup" href="[@s.url action='${crpSession}/partnerSave'][@s.param name='projectID']${project.id?c}[/@s.param][/@s.url]">
                [@s.text name="projectPartners.addPartnerMessage.second" /]
              </a>
            </p>
            [/#if]

          </div>

          [#-- Save bar: save state, the shared last-edit message and the section buttons --]
          <div class="ptn-saveBar">
            <div class="ptn-saveBar__info">
              [#if editable]
              <span class="ptn-saveBar__state" data-ptn-save-state>
                <span class="ptn-saveBar__dot" aria-hidden="true"></span>
                <span data-ptn-save-text>[@s.text name="projectPartners.redesign.save.clean" /]</span>
              </span>
              [/#if]
              <span class="ptn-toast" data-ptn-toast role="status" aria-live="polite" hidden>
                <span data-ptn-toast-text></span>
                <button type="button" class="ptn-toast__undo" data-ptn-toast-undo hidden>[@s.text name="projectPartners.redesign.toast.undo" /]</button>
              </span>
            </div>
            <div class="ptn-saveBar__actions">
              [#-- Section Buttons & hidden inputs--]
              [#include "/WEB-INF/crp/views/projects/buttons-projects.ftl" /]
            </div>
          </div>

        </div>
        [/@s.form]
      </div>
    </div>
</section>
[/#if]

[#-- Hidden Parameters Interface --]
<input id="partners-name" type="hidden" value="project.partners" />
<input id="partnerRespRequired" type="hidden" value="${partnerRespRequired?string}" />
<input id="partnerOfficeRequired" type="hidden" value="${partnerOfficeRequired?string}" />
<input id="managingContactsRequired" type="hidden" value="${managingContactsRequired?string}" />
<input id="projectEditLeader" type="hidden" value="${projectEditLeader?string}" />
<input id="permissionLeader" type="hidden" value="${(permissionLeader && canAddContacts)?string}" />
<input id="permissionCoordinator" type="hidden" value="${(permissionCoordinator && canAddContacts)?string}" />

[#-- Copy the script builds at runtime, so it stays i18n-keyed. --]
<span id="ptn-i18n" hidden
  data-summary="[@s.text name="projectPartners.redesign.summary" /]"
  data-no-country="[@s.text name="projectPartners.redesign.noCountry" /]"
  data-no-contacts="[@s.text name="projectPartners.redesign.contacts.none" /]"
  data-contacts-one="[@s.text name="projectPartners.redesign.contacts.one" /]"
  data-contacts-other="[@s.text name="projectPartners.redesign.contacts.other" /]"
  data-contacts-leader="[@s.text name="projectPartners.redesign.contacts.leader" /]"
  data-people-one="[@s.text name="projectPartners.redesign.people.one" /]"
  data-people-other="[@s.text name="projectPartners.redesign.people.other" /]"
  data-linked-via="[@s.text name="projectPartners.redesign.linkedVia" /]"
  data-match="[@s.text name="projectPartners.redesign.match" /]"
  data-missing="[@s.text name="projectPartners.redesign.missing" /]"
  data-missing-title="[@s.text name="projectPartners.redesign.missingTitle" /]"
  data-issue-org="[@s.text name="projectPartners.redesign.issue.organization" /]"
  data-issue-resp="[@s.text name="projectPartners.redesign.issue.resp" /]"
  data-issue-resp-long="[@s.text name="projectPartners.redesign.issue.respLong" /]"
  data-issue-country="[@s.text name="projectPartners.redesign.issue.country" /]"
  data-issue-linked="[@s.text name="projectPartners.redesign.issue.linked" /]"
  data-issue-contact="[@s.text name="projectPartners.redesign.issue.contact" /]"
  data-tag-managing="[@s.text name="projectPartners.redesign.tag.managing" /]"
  data-tag-partner="[@s.text name="projectPartners.redesign.tag.partner" /]"
  data-expand="[@s.text name="projectPartners.redesign.toggle.expand" /]"
  data-collapse="[@s.text name="projectPartners.redesign.toggle.collapse" /]"
  data-remove="[@s.text name="projectPartners.redesign.remove.title" /]"
  data-remove-blocked-leader="[@s.text name="projectPartners.redesign.remove.blocked.leader" /]"
  data-remove-blocked-linked="[@s.text name="projectPartners.redesign.remove.blocked.linked" /]"
  data-remove-blocked-ppa="[@s.text name="projectPartners.redesign.remove.blocked.ppa" /]"
  data-remove-blocked-activities="[@s.text name="projectPartners.redesign.remove.blocked.activities" /]"
  data-remove-confirm="[@s.text name="projectPartners.redesign.remove.confirm"][@s.param]{0}[/@s.param][@s.param]${sectionNoun}[/@s.param][/@s.text]"
  data-remove-confirm-contacts="[@s.text name="projectPartners.redesign.remove.confirmContacts" /]"
  data-remove-confirm-deliverables="[@s.text name="projectPartners.redesign.remove.confirmDeliverables" /]"
  data-person-remove-confirm="[@s.text name="projectPartners.redesign.person.removeConfirm" /]"
  data-person-remove-blocked-leader="[@s.text name="projectPartners.redesign.person.removeBlocked.leader" /]"
  data-person-remove-blocked-activities="[@s.text name="projectPartners.redesign.person.removeBlocked.activities" /]"
  data-add-leader="[@s.text name="projectPartners.redesign.group.add.PL" /]"
  data-replace-leader="[@s.text name="projectPartners.redesign.group.replace.PL" /]"
  data-replace-leader-title="[@s.text name="projectPartners.redesign.group.replace.title" /]"
  data-replace-confirm="[@s.text name="projectPartners.redesign.replace.done"][@s.param]{0}[/@s.param][@s.param]${sectionNoun}[/@s.param][/@s.text]"
  data-duplicate-contact="[@s.text name="projectPartners.redesign.person.duplicate" /]"
  data-draft-required="[@s.text name="projectPartners.redesign.draft.required" /]"
  data-draft-missing="[@s.text name="projectPartners.redesign.draft.missing" /]"
  data-draft-ready="[@s.text name="projectPartners.redesign.draft.ready" /]"
  data-draft-field-org="[@s.text name="projectPartners.redesign.draft.field.organization" /]"
  data-draft-field-resp="[@s.text name="projectPartners.redesign.draft.field.responsibilities" /]"
  data-draft-field-country="[@s.text name="projectPartners.redesign.draft.field.country" /]"
  data-draft-field-linked="[@s.text name="projectPartners.redesign.draft.field.linked" /]"
  data-resp-counter="[@s.text name="projectPartners.redesign.resp.counter" /]"
  data-resp-over="[@s.text name="projectPartners.redesign.resp.over" /]"
  data-resp-required="[@s.text name="projectPartners.redesign.resp.required" /]"
  data-linked-missing="[@s.text name="projectPartners.redesign.linked.missing" /]"
  data-linked-note="[@s.text name="projectPartners.redesign.linked.note" /]"
  data-linked-none="[@s.text name="projectPartners.redesign.linked.none" /]"
  data-country-filled="[@s.text name="projectPartners.redesign.country.filled"][@s.param]{0}[/@s.param][@s.param]${sectionNoun}[/@s.param][/@s.text]"
  data-removed="[@s.text name="projectPartners.redesign.toast.removed" /]"
  data-unsaved-one="[@s.text name="projectPartners.redesign.save.unsavedOne" /]"
  data-unsaved-other="[@s.text name="projectPartners.redesign.save.unsavedOther" /]"
  data-check-missing="[@s.text name="projectPartners.redesign.check.missing" /]"
  data-save-clean="[@s.text name="projectPartners.redesign.save.clean" /]"
  data-country-add="[@s.text name="projectPartners.redesign.country.add" /]"
  data-country-remove="[@s.text name="projectPartners.redesign.country.remove" /]"
  data-org-placeholder="[@s.text name="projectPartners.redesign.org.placeholder" /]"
></span>

[#-- Single partner TEMPLATE from partnersTemplate.ftl --]
[@projectPartnerMacro element={} name="project.partners[-1]" isTemplate=true /]

[#-- Contact person TEMPLATE from partnersTemplate.ftl --]
[@contactPersonMacro element={} name="project.partners[-1].partnerPersons[-1]" isTemplate=true /]

[#-- Country Element Template --]
[@locElementMacro element={} name="project.partners[-1].selectedLocations" index=-1 isTemplate=true /]

[#-- PPA list Template: one linked managing partner (projectPartnerContributor) --]
<ul style="display:none">
  <li id="ppaListTemplate" class="clearfix">
    <input type="hidden"            name="project.partners[-1].partnerContributors[-1].id" />
    <input type="hidden"            name="project.partners[-1].partnerContributors[-1].projectPartnerContributor.id" />
    <input class="id" type="hidden" name="project.partners[-1].partnerContributors[-1].projectPartnerContributor.institution.id" value="" />
    <span class="name"></span>
  </li>
</ul>

[#-- Project roles descriptions --]
<span class="contactPersonRole-PC" style="display:none">[@s.text name="projectPartners.contactPersonRolePC" /]</span>
<span class="contactPersonRole-PL" style="display:none">[@s.text name="projectPartners.contactPersonRolePL" /]</span>
<span class="contactPersonRole-CP" style="display:none">[@s.text name="projectPartners.contactPersonRoleCP" /]</span>

[#-- allPPAInstitutions --]
<input type="hidden" id="allPPAInstitutions" value="[[#if allPPAInstitutions??][#list allPPAInstitutions as item]${item.id}[#if item_has_next],[/#if][/#list][/#if]]"/>

[#-- Can update PPA Partners --]
<input type="hidden" id="canUpdatePPAPartners" value="${(action.hasPermission("ppa") || !projectEditLeader)?string}"/>

[#-- Project PPA Partners --]
<select id="projectPPAPartners" style="display:none">
[#if project.PPAPartners??]
  [#list project.PPAPartners as ppaPartner]<option value="${ppaPartner.institution.id}">${ppaPartner.institution.composedName}</option>[/#list]
[/#if]
</select>

[#-- Change partner person email dialog --]
<div id="contactChange-dialog" title="Change contact person" style="display:none">
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
            <p  class="text-center col-md-12">[@s.text name="projectPartners.redesign.requestOffice.sent" /]</p>
            <br />
            [#-- Buttons --]
            <div class="text-center">
              <button class="btn btn-danger" type="button" data-dismiss="modal" aria-label="Close">[@s.text name="projectPartners.redesign.requestOffice.close" /]</button>
            </div>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="requestButton btn btn-primary"> <span class="glyphicon glyphicon-send"></span> [@s.text name="projectPartners.redesign.requestOffice.request" /]</button>
      </div>
    </div>
  </div>
</div>

[#--  allInstitutions list --]
<ul style="display:none">
[#list allInstitutions as inst]
  <li id="instID-${inst.id}">
    <span class="composedName">${inst.composedName}</span>
    <span class="acronym">${(inst.acronym)!}</span>
    <span class="name">${(inst.name)!}</span>
    <span class="allowSubDepart">${inst.institutionType.subDepartmentActive?string}</span>
  </li>
[/#list]
</ul>

[#include "/WEB-INF/global/pages/footer.ftl"]

[#------------------------------------------------------            ------------------------------------------------------]
[#----------------------------------------------------     MACROS     ----------------------------------------------------]
[#------------------------------------------------------            ------------------------------------------------------]

[#-- One partner card (A2-2440 redesign). The classes and the input names projectPartners.js,
     fieldsValidation.js and ProjectPartnerAction's binding depend on are unchanged:
     .projectPartner, .blockTitle, .blockContent, .partnerId, .institutionsList,
     .countries-list, .ppaPartnersList, .contactsPerson, .contactPerson and every
     project.partners[i]... name. --]
[#macro projectPartnerMacro element name index=-1 opened=false defaultPerson=false isTemplate=false]
  [#local isLeader = (element.leader)!false/]
  [#local isCoordinator = (element.coordinator)!false/]
  [#local isPPA = (action.isPPA(element.institution))!false /]
  [#local allowSubDep = ((element.subDepartment?has_content)!false) || ((element.institution.institutionType.subDepartmentActive)!false) ]
  [#local acronym = (element.institution.acronym)!'' /]
  [#local instName = (element.institution.name)!'' /]
  [#if !acronym?has_content][#local acronym = instName /][#local instName = '' /][/#if]
  [#local isNewPartner = (editable && isTemplate) || (editable && !element.institution??) || (editable && ((element.institution.id?number == -1)!false)) /]
  [#-- TODO: Please improve this validation at backend side --]
  [#local canRemoveCIAT = true /]
  [#if centerGlobalUnit && isCenterProject && ((element.institution.id == 46)!false)]
    [#local canRemoveCIAT = false /]
  [/#if]
  [#local cardId = isTemplate?string('template', (element.id?c)!(index?c)) /]

  <div id="projectPartner-${isTemplate?string('template',(projectPartner.id)!)}" class="projectPartner ptn-card ${isPPA?string('is-ppa','')} ${(isLeader?string('leader',''))!} ${(isCoordinator?string('coordinator',''))!} ${opened?string('is-open','')}" style="display:${isTemplate?string('none','block')}">
    [#-- Loading --]
    <div class="loading" style="display:none"></div>

    [#-- New partner header: only shown while the card is a draft (see projectPartners.js) --]
    <div class="ptn-draftHead">
      <span class="ptn-draftHead__icon" aria-hidden="true"><svg width="13" height="13" viewBox="0 0 14 14" fill="none"><path d="M7 2.6v8.8M2.6 7h8.8" stroke="#fff" stroke-width="1.8" stroke-linecap="round"></path></svg></span>
      <span class="ptn-draftHead__text">
        <span class="ptn-draftHead__title">[@s.text name="projectPartners.redesign.draft.title" /]</span>
        <span class="ptn-draftHead__help">[@s.text name="projectPartners.redesign.draft.help" /]</span>
      </span>
      <button type="button" class="ptn-iconBtn" data-ptn-draft-cancel aria-label="[@s.text name="projectPartners.redesign.draft.cancelLabel" /]">
        <svg width="13" height="13" viewBox="0 0 14 14" fill="none" aria-hidden="true"><path d="M3 3l8 8M11 3l-8 8" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"></path></svg>
      </button>
    </div>

    [#-- Partner header (collapsed view) --]
    <div class="blockTitle ptn-card__head ${opened?string('opened', 'closed')}">
      <button type="button" class="ptn-card__caret" data-ptn-toggle aria-expanded="${opened?string}" aria-controls="ptn-body-${cardId}">
        <svg width="10" height="10" viewBox="0 0 12 12" fill="none" aria-hidden="true"><path d="M4 2.5 7.5 6 4 9.5" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"></path></svg>
      </button>
      <button type="button" class="ptn-card__summary" data-ptn-toggle tabindex="-1">
        <span class="ptn-card__nameRow">
          <span class="ptn-card__acr ${customForm.changedField('${name}.id')}">${acronym}</span>
          <span class="ptn-card__name" title="${instName}">[#if instName?has_content]&mdash; ${instName}[/#if]</span>
          [#-- Full composed name: what PartnerObject reads as the institution name --]
          <span class="partnerTitle" hidden>${(element.institution.composedName)!''}</span>
        </span>
        <span class="ptn-card__meta">
          <span class="ptn-meta" data-ptn-countries>
            <svg width="12" height="12" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M8 14.4s4.6-4.1 4.6-7.6a4.6 4.6 0 0 0-9.2 0c0 3.5 4.6 7.6 4.6 7.6Z" stroke="currentColor" stroke-width="1.5"></path><circle cx="8" cy="6.8" r="1.6" stroke="currentColor" stroke-width="1.4"></circle></svg>
            <span data-ptn-countries-text></span>
          </span>
          <span class="ptn-meta">
            <span class="ptn-avatars" data-ptn-avatars aria-hidden="true"></span>
            <span data-ptn-contacts-text></span>
          </span>
          <span class="ptn-meta" data-ptn-linked hidden>
            <svg width="12" height="12" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M6.6 9.4 9.4 6.6M5.4 7.8 4 9.2a2.2 2.2 0 0 0 3.1 3.1l1.4-1.4M10.6 8.2 12 6.8a2.2 2.2 0 0 0-3.1-3.1L7.5 5.1" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"></path></svg>
            <span data-ptn-linked-text></span>
          </span>
          <span class="ptn-match" data-ptn-match hidden></span>
          [#-- Searchable text the card does not show: contact names and emails --]
          <span class="ptn-card__haystack" data-ptn-haystack hidden></span>
        </span>
      </button>
      <span class="ptn-card__tags">
        <span class="ptn-tag ${isPPA?string('ptn-tag--managing','ptn-tag--partner')}" data-ptn-type>[#if isPPA][@s.text name="projectPartners.redesign.tag.managing" /][#else][@s.text name="projectPartners.redesign.tag.partner" /][/#if]</span>
      </span>
      <span class="ptn-card__status">
        <span class="ptn-issues" data-ptn-issues hidden></span>
        [#if !isTemplate]
          <span class="ptn-card__relations" data-ptn-relations>[@popUps.relationsMacro element=element /]</span>
        [/#if]
      </span>
      [#if editable && canRemoveCIAT]
        <button type="button" class="ptn-iconBtn ptn-iconBtn--danger removePartner" title="[@s.text name="projectPartners.removePartner" /]" aria-label="[@s.text name="projectPartners.removePartner" /]">
          <svg width="15" height="15" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M3 4.4h10M6.4 4.4V3h3.2v1.4M4.4 4.4l.6 8.6h6l.6-8.6" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round"></path></svg>
        </button>
      [/#if]
    </div>

    [#-- Inline remove confirmation --]
    <div class="ptn-confirm" role="alertdialog" aria-live="assertive" hidden>
      <span class="ptn-confirm__text" data-ptn-confirm-text></span>
      <button type="button" class="ptn-btn ptn-btn--ghost ptn-btn--sm" data-ptn-confirm-cancel>[@s.text name="projectPartners.redesign.remove.keep" /]</button>
      <button type="button" class="ptn-btn ptn-btn--danger ptn-btn--sm" data-ptn-confirm-ok>[@s.text name="projectPartners.redesign.remove.do" /]</button>
    </div>

    <div class="blockContent ptn-card__body" id="ptn-body-${cardId}" style="display:${opened?string('block','none')}">
      <input id="id" class="partnerId" type="hidden" name="${name}.id" value="${(element.id)!}" />
      <input id="id" class="phaseId" type="hidden" name="${name}.phase.id" value="${(element.phase.id)!}" />

      [#-- Institution / Organization --]
      [#if isNewPartner]
      <div class="ptn-field ptn-field--org partnerName">
        <span class="ptn-label">[@s.text name="projectPartners.partner.name" /]<span class="ptn-req" aria-hidden="true">*</span></span>
        <p class="fieldErrorInstitutions"></p>
        [@customForm.select name="${name}.institution.id" value="${(element.institution.id)!}" className="institutionsList" required=true header=false showTitle=false i18nkey="projectPartners.partner.name" listName="" keyFieldName="id"  displayFieldName="composedName"  /]
        <span class="ptn-error" data-ptn-org-error hidden>[@s.text name="projectPartners.redesign.draft.orgError" /]</span>
      </div>
      [#else]
        <input type="hidden" name="${name}.institution.id" class="institutionsList" value="${(element.institution.id)!}"/>
      [/#if]

      [#-- Everything else stays locked on a new partner until its organization is chosen --]
      <div class="ptn-card__fields">

        [#-- Sub department input, only for goverment institutions --]
        <div class="ptn-field subDepartment" style="display:${allowSubDep?string('block','none')}">
          [@customForm.input name="${name}.subDepartment" className="subDepartment" i18nkey="projectPartners.subDepartment"  editable=editable /]
        </div>

        <div class="ptn-grid">
          [#-- Responsibilities --]
          <div class="ptn-field ptn-field--resp">
          [#if projectEditLeader]
            [#local respName = "${name}.responsibilities" /]
            [#local respLabel][#if editable][@s.text name="projectPartners.responsabilities" /][#else][@s.text name="projectPartners.responsabilities.readText" /][/#if][/#local]
            <label class="ptn-label" for="resp-${cardId}">${respLabel}[#if partnerRespRequired && editable]<span class="ptn-req" aria-hidden="true">*</span>[/#if]</label>
            [@s.fielderror cssClass="fieldError" fieldName="${respName}"/]
            [#if editable]
              <textarea id="resp-${cardId}" name="${respName}" rows="5" class="ptn-textarea resp ${partnerRespRequired?string('required','optional')}" placeholder="[@s.text name="projectPartners.redesign.resp.placeholder" /]">${(element.responsibilities)!}</textarea>
              <span class="ptn-field__foot">
                <span class="ptn-error" data-ptn-resp-error></span>
                <span class="ptn-counter" data-ptn-resp-counter></span>
              </span>
            [#else]
              <input type="hidden" name="${respName}" value="${(element.responsibilities)!}" />
              <p class="ptn-readText">[#if (element.responsibilities?has_content)!false]${element.responsibilities}[#else]<span class="ptn-muted">[@s.text name="form.values.fieldEmpty" /]</span>[/#if]</p>
            [/#if]
          [/#if]
          </div>

          <div class="ptn-col">
            [#-- Country office(s) --]
            <div class="ptn-field ptn-field--countries">
              <span class="ptn-label">[@s.text name="projectPartners.redesign.countryOffices" /][#if partnerOfficeRequired]<span class="ptn-req" aria-hidden="true">*</span>[/#if]</span>
              <div class="countries-list items-list ptn-chips" listname="${name}.selectedLocations">
                <ul class="ptn-chips__list">
                  [#if (element.selectedLocations?has_content)!false]
                    [#list element.selectedLocations as locElement]
                      [@locElementMacro element=locElement!{} name="${name}.selectedLocations" index=locElement_index /]
                    [/#list]
                  [/#if]
                </ul>
                [#if editable]
                  <span class="ptn-chips__add">
                    [@customForm.select name="" showTitle=false i18nkey="location.select.country" listName="${name}.institution.locations" header=true keyFieldName="locElement.isoAlpha2" displayFieldName="composedName" value="id" placeholder="projectPartners.redesign.country.add" className="countriesList ptn-chips__select"/]
                  </span>
                [#elseif !(element.selectedLocations?has_content)!true]
                  <span class="ptn-muted">[@s.text name="projectPartners.redesign.noCountry" /]</span>
                [/#if]
              </div>
              <span class="ptn-help">
                <span data-ptn-country-note></span>
                [#if editable]
                  [@s.text name="projectPartners.redesign.country.missing" /]
                  [#if !action.isAiccra()]
                    <a href="#" data-toggle="modal" data-target="#requestModal">[@s.text name="projectPartners.redesign.country.request" /]</a>
                  [#else]
                    [@s.text name="projectPartners.redesign.country.email" /] <a href="mailto:MARLOSupport@cgiar.org">MARLOSupport@cgiar.org</a>.
                  [/#if]
                [/#if]
              </span>
            </div>

            [#-- Indicate which managing partners this (second level) partner is linked through --]
            [#if (editable || ((!editable && element.partnerContributors?has_content)!false))]
              <div class="ptn-field ptn-field--linked ppaPartnersList" listname="${name}.partnerContributors" style="display:${(isPPA || isTemplate)?string('none','block')}">
                <span class="ptn-label">[@customForm.text name="projectPartners.indicatePpaPartners" readText=!editable /][#if editable]<span class="ptn-req" aria-hidden="true">*</span>[/#if]</span>
                [#-- Toggle chips are drawn by projectPartners.js from #projectPPAPartners;
                     the selected ones are the hidden <li> rows below, which are what is posted. --]
                <div class="ptn-toggles" data-ptn-linked-options role="group"></div>
                <ul class="list" hidden>
                [#if element.partnerContributors?has_content]
                  [#list element.partnerContributors as ppaPartner]
                    <li class="clearfix">
                      <input type="hidden" name="${name}.partnerContributors[${ppaPartner_index}].id" value="${(ppaPartner.id)!}" />
                      <input type="hidden" name="${name}.partnerContributors[${ppaPartner_index}].projectPartnerContributor.id" value="${(ppaPartner.projectPartnerContributor.id)!}"/>
                      <input class="id" type="hidden" name="${name}.partnerContributors[${ppaPartner_index}].projectPartnerContributor.institution.id"  value="${(ppaPartner.projectPartnerContributor.institution.id)!}"/>
                      <span class="name">${(ppaPartner.projectPartnerContributor.institution.composedName)!}</span>
                    </li>
                  [/#list]
                [/#if]
                </ul>
                <span class="ptn-help" data-ptn-linked-note></span>
              </div>
            [/#if]
          </div>
        </div>

        [#-- Contacts person(s), grouped by role --]
        <div class="contactsPerson ptn-contacts">
          <div class="ptn-contacts__head">
            <span class="ptn-label">[@s.text name="projectPartners.projectPartnerContacts" /]<span class="ptn-req requiredTag" aria-hidden="true" style="display:${isPPA?string('inline','none')}">*</span></span>
            <span class="ptn-muted" data-ptn-people></span>
          </div>
          <div class="ptn-contacts__none" data-ptn-no-contacts hidden>[@s.text name="projectPartners.redesign.contacts.empty" /]</div>
          <div class="fullPartBlock ptn-groups" listname="${name}.partnerPersons">
            [#list contactRoles as role]
              [@contactGroupMacro element=element name=name role=role index=index isTemplate=isTemplate defaultPerson=(isPPA || defaultPerson) /]
            [/#list]
          </div>
        </div>

      </div>

      [#-- New partner footer: only shown while the card is a draft --]
      <div class="ptn-draftFoot">
        <span class="ptn-draftFoot__note" data-ptn-draft-note></span>
        <button type="button" class="ptn-btn ptn-btn--ghost" data-ptn-draft-cancel>[@s.text name="projectPartners.redesign.draft.cancel" /]</button>
        <button type="button" class="ptn-btn ptn-btn--primary" data-ptn-draft-commit>[@s.text name="projectPartners.redesign.draft.commit"][@s.param]${sectionNoun}[/@s.param][/@s.text]</button>
      </div>
    </div>
  </div>
[/#macro]

[#-- One role group of a partner's contact people: heading, role tooltip, add button and
     the members, two per row. Every person keeps the same inputs as before; only
     where it sits on the page changed. --]
[#macro contactGroupMacro element name role index isTemplate defaultPerson]
  [#local members = [] /]
  [#if (element.partnerPersons?has_content)!false]
    [#list element.partnerPersons as partnerPerson]
      [#local personType = (partnerPerson.contactType)!'CP' /]
      [#if !contactRoles?seq_contains(personType)][#local personType = 'CP' /][/#if]
      [#if personType == role][#local members = members + [{"person": partnerPerson, "index": partnerPerson_index}] /][/#if]
    [/#list]
  [/#if]
  [#local canAddRole = canAddContacts && ((role == "PL" && permissionLeader) || (role == "PC" && permissionCoordinator) || role == "CP") /]
  [#local roleKey = "projectPartners.types.${role}" /]
  <div class="ptn-group ptn-group--${role}" data-ptn-group="${role}">
    <div class="ptn-group__head">
      <span class="ptn-group__dot" aria-hidden="true"></span>
      <span class="ptn-group__label">[@s.text name=roleKey /]</span>
      <span class="ptn-group__count" data-ptn-group-count>${members?size}</span>
      <span class="ptn-tip">
        <button type="button" class="ptn-tip__btn" aria-expanded="false" aria-label="[@s.text name="projectPartners.redesign.group.about"][@s.param][@s.text name=roleKey /][/@s.param][/@s.text]">
          <svg width="15" height="15" viewBox="0 0 16 16" fill="none" aria-hidden="true"><circle cx="8" cy="8" r="6.4" stroke="currentColor" stroke-width="1.4"></circle><path d="M8 7.3v3.6" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"></path><circle cx="8" cy="5.1" r=".95" fill="currentColor"></circle></svg>
        </button>
        <span class="ptn-tip__body" role="tooltip" hidden>
          <span class="ptn-tip__title">[@s.text name=roleKey /]</span>
          <span class="ptn-tip__text">[@s.text name="projectPartners.contactPersonRole${role}" /]</span>
        </span>
      </span>
      [#if canAddRole]
        <button type="button" class="ptn-btn ptn-btn--soft ptn-btn--sm ptn-group__add" data-ptn-add-contact="${role}">
          <svg width="11" height="11" viewBox="0 0 14 14" fill="none" aria-hidden="true"><path d="M7 2.6v8.8M2.6 7h8.8" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"></path></svg>
          <span data-ptn-add-label>[@s.text name="projectPartners.redesign.group.add.${role}" /]</span>
        </button>
      [/#if]
    </div>
    <p class="ptn-group__empty" data-ptn-group-empty [#if members?has_content]hidden[/#if]>[@s.text name="projectPartners.redesign.group.empty.${role}"][@s.param]${sectionNoun}[/@s.param][/@s.text]</p>
    <div class="ptn-group__members">
      [#list members as member]
        [@contactPersonMacro element=member.person name="${name}.partnerPersons[${member.index}]" index=member.index partnerIndex=index institutionID=(element.institution.id)! /]
      [/#list]
    </div>
  </div>
[/#macro]

[#macro contactPersonMacro element name index=-1 partnerIndex=-1 isTemplate=false institutionID=-1]
  [#local personType = (element.contactType)!'CP' /]
  [#if !["PL", "PC", "CP"]?seq_contains(personType)][#local personType = 'CP' /][/#if]
  [#local firstName = (element.user.firstName)!'' /]
  [#local lastName = (element.user.lastName)!'' /]
  [#local initials = ((firstName?has_content)?then(firstName?substring(0,1), '') + (lastName?has_content)?then(lastName?substring(0,1), ''))?upper_case /]
  [#local email = (element.user.email)!'' /]
  [#local canRemove = editable && ((action.canBeDeleted((element.id)!-1,(element.class.name)!))!true) /]
  [#if (element.contactType == "PL")!false]
    [#local canEditContactType = (editable && permissionLeader)!false /]
  [#elseif (element.contactType == "PC")!false]
    [#local canEditContactType = (editable && permissionCoordinator)!false /]
  [#else]
    [#local canEditContactType = editable || isTemplate /]
  [/#if]
  [#local canEditEmail = editable && (isTemplate || !(element.id??)) /]

  <div id="contactPerson-${isTemplate?string('template',(element.id)!)}" class="contactPerson ptn-person ${personType} ${customForm.changedField('${name}.id')}" style="display:${isTemplate?string('none','flex')}" listname="partner-${partnerIndex}-person-${index}">
    <input id="id" class="partnerPersonId" type="hidden" name="${name}.id" value="${(element.id)!}" />
    <input type="hidden" class="partnerPersonType" name="${name}.contactType" value="${personType}" />
    <input type="hidden" class="canEditEmail" value="${canEditEmail?string}" />
    <input type="hidden" class="canEditContactType" value="${canEditContactType?string}" />

    <span class="ptn-avatar" aria-hidden="true" data-ptn-initials>${initials}</span>
    <span class="ptn-person__body userField">
      [#-- Display name, kept as the input the users popup writes into. Not bound server-side. --]
      <input type="hidden" class="userName" name="partner-${partnerIndex}-person-${index}" id="partner-${partnerIndex}-person-${index}" value="${(element.user.composedName)!}" />
      <input class="userId" type="hidden" name="${name}.user.id" value="${(element.user.id)!}" />
      <span class="ptn-person__name" data-ptn-person-name>${(element.user.composedCompleteName)!''}</span>
      <a class="ptn-person__email" data-ptn-person-email href="mailto:${email}">${email}</a>
      [@s.fielderror cssClass="fieldError" fieldName="${name}.contactType"/]
    </span>

    [#if canRemove]
      <button type="button" class="ptn-iconBtn ptn-iconBtn--danger ptn-iconBtn--sm removePerson" aria-label="[@s.text name="projectPartners.redesign.person.remove"][@s.param]${firstName} ${lastName}[/@s.param][/@s.text]" title="[@s.text name="projectPartners.removePerson" /]">
        <svg width="10" height="10" viewBox="0 0 14 14" fill="none" aria-hidden="true"><path d="M3 3l8 8M11 3l-8 8" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"></path></svg>
      </button>
    [/#if]

    [#-- IFPRI Partner Division partnerDivision  --]
    [#local showIfpriDivision = (institutionID == 89)!false /]
    [#if action.hasSpecificities('crp_division_fs')]
    <div class="ptn-person__extra divisionBlock division-IFPRI" style="display:${showIfpriDivision?string('block','none')}">
      [@customForm.select name="${name}.partnerDivision.id" value="${(element.partnerDivision.id)!-1}" i18nkey="projectPartners.division" className="divisionField setSelect2" listName="divisions" keyFieldName="id" displayFieldName="composedName" required=true editable=editable /]
    </div>
    [/#if]

    [#if !isTemplate]
      [#-- Activities leading and Deliverables with responsibilities --]
      <div class="contactTags ptn-person__extra">
        [#if (element.id??)!false ]
          [#local activitiesLedByUserList = action.getActivitiesData(element.id)]
          [#if activitiesLedByUserList?size>0]
            <button type="button" class="tag activities ptn-relTag">[@s.text name="projectPartners.personActivities"][@s.param]${activitiesLedByUserList?size}[/@s.param][/@s.text]</button>
            <div class="activitiesList"  style="display:none">
              <h3>Activities</h3>
              <ul>
              [#list activitiesLedByUserList as activity]
                <li>${activity.title}  <a target="_blank" href="[@s.url namespace=namespace action='${crpSession}/activities' ][@s.param name='projectID']${project.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]#projectActivity-${activity.id}"><img class="external-link" src="${baseUrlCdn}/global/images/external-link.png" alt="" /></a></li>
              [/#list]
              </ul>
            </div>
          [/#if]
          [#local deliverablesLedByUserList = action.getDeliverablesLedByUser(element.user.id)]
          [#if deliverablesLedByUserList?size>0]
            <button type="button" class="tag deliverables ptn-relTag">[@s.text name="projectPartners.personDeliverables"][@s.param]${deliverablesLedByUserList?size}[/@s.param][/@s.text]</button>
            <div class="deliverablesList" style="display:none">
              <h3>Deliverables</h3>
              <ul>
              [#list deliverablesLedByUserList as deliverable]
                <li>${deliverable.title}  <a target="_blank" href="[@s.url namespace=namespace action='${crpSession}/deliverable' ][@s.param name='deliverableID']${deliverable.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]"><img class="external-link" src="${baseUrlCdn}/global/images/external-link.png" alt="" /></a></li>
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
  [#assign locElementName = "${name}[${index}]" ]
  <li id="locElement-${isTemplate?string('template', index)}" class="locElement ptn-chip ptn-chip--country" style="display:${isTemplate?string('none','inline-flex')}">
    <span class="flag-icon ptn-chip__flag" aria-hidden="true"><i class="flag-icon flag-icon-${(element.locElement.isoAlpha2?lower_case)!}"></i></span>
    <span class="name">${(element.composedName)!'{name}'}</span>
    <span class="ptn-chip__badge" data-ptn-auto hidden>[@s.text name="projectPartners.redesign.country.fromLocation" /]</span>
    [#if editable]
      <button type="button" class="removeLocElement ptn-chip__remove" aria-label="[@s.text name="projectPartners.redesign.country.remove"][@s.param]${(element.composedName)!''}[/@s.param][/@s.text]">
        <svg width="9" height="9" viewBox="0 0 14 14" fill="none" aria-hidden="true"><path d="M3 3l8 8M11 3l-8 8" stroke="currentColor" stroke-width="2" stroke-linecap="round"></path></svg>
      </button>
    [/#if]
    [#-- Hidden inputs --]
    <input type="hidden" class="locElementCountry" name="${locElementName}.locElement.isoAlpha2" value="${(element.locElement.isoAlpha2)!}" />
  </li>
[/#macro]
