[#ftl]
[#assign title = "Project Locations" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2", "blueimp-file-upload"] /]
[#assign customJS = [
  "https://unpkg.com/leaflet@1.9.4/dist/leaflet.js",
  "${baseUrlMedia}/js/projects/projectLocations.js?20261112-2",
  "${baseUrlCdn}/global/js/fieldsValidation.js"
  ]
/]
[#assign customCSS = [
  "https://unpkg.com/leaflet@1.9.4/dist/leaflet.css",
  "${baseUrlMedia}/css/projects/projectLocations.css?20261112-2" ] /]
[#assign currentSection = "projects" /]
[#assign currentStage = "locations" /]
[#assign hideJustification = true /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"text":"C${project.id}", "nameSpace":"/projects", "action":"${crpSession}/description", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
  {"label":"projectLocations", "nameSpace":"/projects", "action":""}
] /]

[#-- A2-2735 redesign. The posted field names below are the contract ProjectLocationAction.save() reads:
     anything this page stops posting is deleted on save. projectLocations.js keeps the indexes contiguous. --]
[#assign otherLocations = action.hasSpecificities('crp_other_locations') /]
[#-- The Yes / No questions and the locations list are editable only with crp_other_locations, as before --]
[#assign scopeEditable = editable && otherLocations /]
[#-- A SuperAdmin can still edit a closed phase, so the read-only note follows editable, not the phase alone --]
[#assign phaseClosed = !editable && !((actualPhase.editable)!true) /]
[#assign phaseLabel = "${(actualPhase.name)!} ${(actualPhase.year?c)!}"?trim /]
[#assign globalValue = ((project.projectInfo.locationGlobal)?string('true', 'false'))!'' /]
[#assign regionalValue = ((project.projectInfo.locationRegional)?string('true', 'false'))!'' /]
[#-- The shared yes/no macro keeps the whole switch, marked and inert, in read-only --]
[#global deliverableFormRedesign = true /]
[#assign hasSuggestions =(project.regionFS?has_content || project.countryFS?has_content)!false /]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

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
        <input class="projectInfo" type="hidden" name="project.id" value="${project.id?c}" />
        <div class="lc[#if !editable] is-readonly[/#if]" data-lc-root data-editable="${editable?string}" data-scope-editable="${scopeEditable?string}"
          data-has-suggestions="${hasSuggestions?string}">

          [#-- Header: title and the section summary --]
          <div class="lc-head">
            <h1 class="lc-head__title">[@s.text name="projectLocations.title" /]</h1>
            <span class="lc-head__summary" data-lc-summary></span>
          </div>

          [#-- Instructions, or the closed-phase note --]
          [#if phaseClosed]
            <div class="lc-note lc-note--closed" role="note">
              <svg class="lc-note__lock" width="18" height="18" viewBox="0 0 16 16" fill="none" aria-hidden="true"><rect x="3.2" y="7" width="9.6" height="6.6" rx="1.6" stroke="currentColor" stroke-width="1.5"></rect><path d="M5.6 7V5.2a2.4 2.4 0 0 1 4.8 0V7" stroke="currentColor" stroke-width="1.5"></path></svg>
              <p class="lc-note__text">[@s.text name="projectLocations.redesign.closed"][@s.param]${phaseLabel}[/@s.param][/@s.text]</p>
            </div>
          [#else]
            <div class="lc-note" role="note">
              <span class="lc-note__icon" aria-hidden="true">i</span>
              <p class="lc-note__text">[#if reportingActive][@s.text name="projectLocations.help2" /][#else][@s.text name="projectLocations.help1" /][/#if]</p>
            </div>
          [/#if]

          [#-- Geographic scope --]
          <section class="lc-card" aria-labelledby="lc-scope-title">
            <h2 class="lc-card__title" id="lc-scope-title">[@s.text name="projectLocations.redesign.geoScope" /]</h2>

            [@scopeQuestion id="lc-q-global" name="project.projectInfo.locationGlobal" label="projectLocations.globalDimension" value=globalValue /]

            <div class="lc-dimGroup">
              [@scopeQuestion id="lc-q-regional" name="project.projectInfo.locationRegional" label="projectLocations.regionalDimension" value=regionalValue
                note=(scopeEditable)?then("projectLocations.regionsNote", "") /]

              [#if otherLocations]
              [#-- Regions picker: only while the regional answer is Yes --]
              <div class="lc-regions" data-lc-regions[#if regionalValue != "true"] hidden[/#if]>
                <span class="lc-regions__head">
                  <label class="lc-label" for="lc-region-select">[@s.text name="projectLocations.redesign.regions" /]</label>
                  <span class="lc-help">[@s.text name="projectLocations.unStandard" /]</span>
                </span>
                <div class="lc-chips" id="regionList" listname="project.projectRegions">
                  [#if project.projectRegions?has_content]
                    [#list project.projectRegions as region]
                      [@regionChip element=region index=region_index /]
                    [/#list]
                  [/#if]
                  <span class="lc-chips__empty" data-lc-regions-empty[#if project.projectRegions?has_content] hidden[/#if]>[#if editable][@s.text name="projectLocations.redesign.regions.none" /][#else][@s.text name="projectLocations.redesign.notProvided" /][/#if]</span>
                </div>
                [#if editable]
                  <span class="lc-selectWrap lc-selectWrap--add">
                    <select id="lc-region-select" class="lc-select lc-select--add" data-lc-region-add>
                      <option value="">[@s.text name="projectLocations.redesign.regions.add" /]</option>
                      [#if scopeRegionLists?has_content]
                      <optgroup label="[@s.text name="projectLocations.redesign.regions.scopeGroup"][@s.param]${(loggedCrp.acronym)!}[/@s.param][/@s.text]">
                        [#list scopeRegionLists as region]
                        <option value="${(region.id?c)!}-true" data-scope="true">${(region.name)!}</option>
                        [/#list]
                      </optgroup>
                      [/#if]
                      [#if regionLists?has_content]
                      <optgroup label="[@s.text name="projectLocations.unStandard" /]">
                        [#list regionLists as region]
                        <option value="${(region.id?c)!}-false" data-scope="false">${(region.name)!}</option>
                        [/#list]
                      </optgroup>
                      [/#if]
                    </select>
                    <svg class="lc-selectWrap__caret" width="11" height="11" viewBox="0 0 12 12" fill="none" aria-hidden="true"><path d="M2.5 4.5 6 8l3.5-3.5" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"></path></svg>
                  </span>
                [/#if]
              </div>
              [/#if]
            </div>
          </section>

          [#-- AR4D activities related to the evaluation of CSA options (crp_location_csv_activities, off for AICCRA) --]
          [#if reportingActive && action.hasSpecificities("crp_location_csv_activities")]
          <section class="lc-card lc-card--csv">
            <div class="form-group">
              [@customForm.yesNoInput label="projectLocations.activitiesCSV" name="project.projectInfo.activitiesCSV" editable=editable inverse=false cssClass="isCSV" /]
            </div>
            <div class="csvActivitiesBox simpleBox" style="display:${(project.projectInfo.activitiesCSV?string("block","none"))!"none"};">
              <div class="row">
                <div class="col-md-9">
                  <label for="">[@s.text name="projectLocations.activitiesCSV.instructions" /]:</label>
                </div>
                <div class="col-md-3 right">
                  <a href="${baseUrlCdn}/global/documents/Guidelines_CSA_evlautions_template _Reporting_2020.pdf" target="__BLANK">
                    <img src="${baseUrlCdn}/global/images/pdf.png" height="20" alt="" />
                    [[@s.text name="projectLocations.activitiesCSV.guideline" /]]
                  </a>
                </div>
              </div>
              <div class="row justify-content-md-center">
                <div class="col-md-2"></div>
                <div class="col-md-4">
                   <label for="">[@s.text name="projectLocations.activitiesCSV.download" /]:</label>
                   <div class="form-group">
                     <a href="${baseUrlCdn}/global/documents/CSA_Evaluation_template_2020.xlsm" download><img src="${baseUrlCdn}/global/images/download-excel.png" height="70" alt="" /></a>
                   </div>
                </div>
                <div class="col-md-5 " style="position:relative" listname="project.projectInfo.activitiesCSVFile">
                  [@customForm.fileUploadAjax
                    fileDB=(project.projectInfo.activitiesCSVFile)!{}
                    name="project.projectInfo.activitiesCSVFile.id"
                    label="projectLocations.activitiesCSV.upload"
                    image=true
                    imgUrl="upload-excel.png"
                    imgClass="csvUpload"
                    dataUrl="${baseUrl}/uploadProjectLocationActivitiesCSV.do"
                    path="${(action.getPath())!}"
                    isEditable=editable
                    labelClass=""
                    required=true
                  /]
                </div>
              </div>
            </div>
          </section>
          [/#if]

          [#-- Suggested locations: aggregated from the funding sources. Checked or unchecked, never removed. --]
          [#if hasSuggestions]
          <section class="lc-card" aria-labelledby="lc-fs-title">
            <div class="lc-card__head">
              <h2 class="lc-card__title" id="lc-fs-title">[@s.text name="projectLocations.suggestedLocations" /]</h2>
              <p class="lc-card__intro">[@s.text name="projectLocations.note" /][#if editable] [@s.text name="projectLocations.redesign.suggested.instructions" /][/#if]</p>
            </div>
            <div class="lc-fsGrid">
              [#if project.regionFS?has_content]
                [#list project.regionFS as location]
                  [@suggestedLocation element=location name="project.regionFS" index=location_index isRegion=true /]
                [/#list]
              [/#if]
              [#if project.countryFS?has_content]
                [#list project.countryFS as location]
                  [@suggestedLocation element=location name="project.countryFS" index=location_index isRegion=false /]
                [/#list]
              [/#if]
            </div>
            <span class="lc-empty-ro" data-lc-fs-none hidden>[@s.text name="projectLocations.redesign.notProvided" /]</span>
          </section>
          [/#if]

          [#-- Locations list, with the map beside it --]
          [#if otherLocations]
          <section class="lc-card lc-card--locations" id="lc-locations" aria-labelledby="lc-list-title" data-lc-locations>
            <div class="lc-card__bar">
              <span class="lc-card__head">
                <h2 class="lc-card__title" id="lc-list-title">[@s.text name="projectLocations.locationsList" /]</h2>
                <span class="lc-card__meta" data-lc-count></span>
              </span>
              [#if scopeEditable]
                <button type="button" class="lc-btn lc-btn--primary" data-lc-add-start>
                  <svg width="13" height="13" viewBox="0 0 14 14" fill="none" aria-hidden="true"><path d="M7 2.6v8.8M2.6 7h8.8" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"></path></svg>
                  [@s.text name="projectLocations.redesign.addLocation" /]
                </button>
              [/#if]
            </div>

            <div class="lc-alert lc-alert--error" role="alert" data-lc-error hidden>
              <svg width="16" height="16" viewBox="0 0 16 16" fill="none" aria-hidden="true"><circle cx="8" cy="8" r="7" fill="currentColor"></circle><path d="M8 4.4v4.2" stroke="#fff" stroke-width="1.7" stroke-linecap="round"></path><circle cx="8" cy="11.3" r="1" fill="#fff"></circle></svg>
              <span>[@s.text name="projectLocations.redesign.error.required" /]</span>
            </div>
            <div class="lc-alert lc-alert--info" data-lc-global-note hidden>
              <span class="lc-alert__icon" aria-hidden="true">i</span>
              <span>[@s.text name="projectLocations.redesign.globalNote" /]</span>
            </div>

            [#if scopeEditable]
              [@addLocationPanel /]
            [/#if]

            <div class="lc-split">
              [#-- The groups carry the posted project.locationsData; fieldsValidation.js flags this listname --]
              <div class="lc-groups" id="selectsContent" listname="project.locationsData" data-lc-groups>
                [#if project.locationsData?has_content]
                  [#list project.locationsData as locationLevel]
                    [@locationGroup element=locationLevel index=locationLevel_index /]
                  [/#list]
                [/#if]
                <div class="lc-empty" data-lc-empty hidden>
                  <span class="lc-empty__title" data-lc-empty-title
                    data-text-default="[@s.text name="projectLocations.redesign.empty.title" /]"
                    data-text-global="[@s.text name="projectLocations.redesign.empty.titleGlobal" /]">[@s.text name="projectLocations.redesign.empty.title" /]</span>
                  <span class="lc-empty__text">[@s.text name="projectLocations.redesign.empty.text" /]</span>
                  [#if scopeEditable]
                    <button type="button" class="lc-btn lc-btn--ghost lc-btn--sm" data-lc-add-start>[@s.text name="projectLocations.redesign.addLocation" /]</button>
                  [/#if]
                </div>
                <span class="lc-empty-ro" data-lc-empty-ro hidden>[@s.text name="projectLocations.redesign.notProvided" /]</span>
              </div>

              <div class="lc-mapCol">
                [#-- Leaflet 1.9.4 + OpenStreetMap, created when it first scrolls into view (projectLocations.js) --]
                <div class="lc-map" id="lc-map" role="region" aria-label="[@s.text name="projectLocations.redesign.map.label" /]" data-lc-map></div>
                <span class="lc-mapCol__foot">
                  <span class="lc-legend"><span class="lc-legend__pin" aria-hidden="true"></span>[@s.text name="projectLocations.redesign.map.legendSite" /]</span>
                  <span data-lc-map-caption></span>
                </span>
              </div>
            </div>

            [#if scopeEditable]
              <button type="button" class="lc-addCard" data-lc-add-start>
                <svg width="14" height="14" viewBox="0 0 14 14" fill="none" aria-hidden="true"><path d="M7 2.6v8.8M2.6 7h8.8" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"></path></svg>
                [@s.text name="projectLocations.redesign.addNewLocation" /]
              </button>
            [/#if]
          </section>
          [/#if]

          [#-- Save bar: save state and the section buttons --]
          <div class="lc-saveBar">
            <div class="lc-saveBar__info">
              [#if editable]
              <span class="lc-saveBar__state" data-lc-save-state>
                <span class="lc-saveBar__dot" aria-hidden="true"></span>
                <span data-lc-save-text>[@s.text name="projectLocations.redesign.save.clean" /]</span>
              </span>
              [/#if]
            </div>
            <div class="lc-saveBar__actions">
              [#include "/WEB-INF/crp/views/projects/buttons-projects.ftl" /]
            </div>
          </div>

        </div>
        [/@s.form]
      </div>
    </div>
</section>
[/#if]

[#-- Templates the script clones. Their inputs carry no name until projectLocations.js places them in the form. --]
<div id="lc-templates" hidden>
  [@locationGroup element={} index=-1 template=true /]
  [@locationRow element={} isList=false template=true /]
  <span class="lc-chip" data-lc-region-template>
    <span class="lc-chip__name"></span>
    <input type="hidden" data-lc-field=".id" value="-1" />
    <input type="hidden" class="rId" data-lc-field="" value="" />
    <input type="hidden" class="regionScope" data-lc-field=".scope" value="" />
    <button type="button" class="lc-chip__remove" data-lc-region-remove>
      <svg width="10" height="10" viewBox="0 0 12 12" fill="none" aria-hidden="true"><path d="M2.5 2.5l7 7M9.5 2.5l-7 7" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"></path></svg>
    </button>
  </span>
</div>

[#-- Copy the script builds at runtime, so it stays i18n-keyed --]
<span id="lc-i18n" hidden
  data-yes="[@s.text name="projectLocations.redesign.answer.yes" /]"
  data-no="[@s.text name="projectLocations.redesign.answer.no" /]"
  data-none="[@s.text name="projectLocations.redesign.answer.none" /]"
  data-summary-global="[@s.text name="projectLocations.redesign.summary.global" /]"
  data-summary-regional="[@s.text name="projectLocations.redesign.summary.regional" /]"
  data-countries-one="[@s.text name="projectLocations.redesign.summary.countriesOne" /]"
  data-countries-other="[@s.text name="projectLocations.redesign.summary.countriesOther" /]"
  data-count-none="[@s.text name="projectLocations.redesign.count.none" /]"
  data-count-one="[@s.text name="projectLocations.redesign.count.one" /]"
  data-count-other="[@s.text name="projectLocations.redesign.count.other" /]"
  data-whole-country="[@s.text name="projectLocations.redesign.meta.wholeCountry" /]"
  data-from-list="[@s.text name="projectLocations.redesign.meta.fromList" /]"
  data-suggested="[@s.text name="projectLocations.redesign.meta.suggested" /]"
  data-fs-badge="[@s.text name="projectLocations.redesign.fundingSource" /]"
  data-fs-locked="[@s.text name="projectLocations.redesign.fsLockedTitle" /]"
  data-remove="[@s.text name="projectLocations.redesign.remove" /]"
  data-level-hint="[@s.text name="projectLocations.redesign.add.levelHint" /]"
  data-level-hint-list="[@s.text name="projectLocations.redesign.add.levelHintList" /]"
  data-level-hint-coord="[@s.text name="projectLocations.redesign.add.levelHintCoord" /]"
  data-list-loading="[@s.text name="projectLocations.redesign.add.loading" /]"
  data-list-empty="[@s.text name="projectLocations.redesign.add.listEmpty" /]"
  data-already-added="[@s.text name="projectLocations.redesign.add.alreadyAdded" /]"
  data-coord-hint="[@s.text name="projectLocations.redesign.add.coordHint" /]"
  data-coord-error="[@s.text name="projectLocations.redesign.add.coordError" /]"
  data-selected="[@s.text name="projectLocations.redesign.add.selected" /]"
  data-confirm-one="[@s.text name="projectLocations.redesign.add.confirmOne" /]"
  data-confirm-many="[@s.text name="projectLocations.redesign.add.confirmMany" /]"
  data-caption-placing="[@s.text name="projectLocations.redesign.map.captionPlacing" /]"
  data-caption-none="[@s.text name="projectLocations.redesign.map.captionNone" /]"
  data-caption-one="[@s.text name="projectLocations.redesign.map.captionOne" /]"
  data-caption-other="[@s.text name="projectLocations.redesign.map.captionOther" /]"
  data-save-clean="[@s.text name="projectLocations.redesign.save.clean" /]"
  data-save-one="[@s.text name="projectLocations.redesign.save.one" /]"
  data-save-other="[@s.text name="projectLocations.redesign.save.other" /]"
  data-save-locked="[@s.text name="projectLocations.redesign.save.lockedDraft" /]"
></span>

[#include "/WEB-INF/global/pages/footer.ftl"]

[#-- ------------------------------------------------------------------------------------------------
     Macros
     ------------------------------------------------------------------------------------------------ --]

[#-- A Yes / No question: the shared customForm.yesNoInputDeliverable segmented switch (the one the redesigned
     deliverable and innovation forms use), with the question text beside it. Read-only keeps the switch whole
     and inert (deliverableFormRedesign). --]
[#macro scopeQuestion id name label value note=""]
  <div class="lc-dim" data-lc-question="${name}">
    <div class="lc-dim__row">
      <span class="lc-dim__text">
        <span class="lc-dim__label" id="${id}">[@s.text name=label /]</span>
        [#if note?has_content]<span class="lc-help">[@s.text name=note /]</span>[/#if]
      </span>
      <div class="lc-dim__switch" role="radiogroup" aria-labelledby="${id}">
        [@customForm.yesNoInputDeliverable name=name value=value editable=scopeEditable
          yesLabel=action.getText("projectLocations.redesign.answer.yes") noLabel=action.getText("projectLocations.redesign.answer.no") /]
      </div>
    </div>
  </div>
[/#macro]

[#-- A saved region. Saved regions post their element (or element type) id; new ones post locElementId or
     locElementTypeId, which saveRegions() reads. --]
[#macro regionChip element index]
  [#local customName = "project.projectRegions[${index}]" /]
  [#local isType = !(element.locElement?has_content) /]
  [#local regionName = (isType?then((element.locElementType.name)!'', (element.locElement.name)!''))!'' /]
  [#local regionScope = (isType?then((element.locElementType.scope?c)!'', (element.locElement.locElementType.scope?c)!''))!'' /]
  [#local regionId = (isType?then((element.locElementType.id?c)!'', (element.locElement.id?c)!''))!'' /]
  <span class="lc-chip" data-lc-region data-key="${regionId}-${regionScope}">
    <span class="lc-chip__name">${regionName}</span>
    <input class="id" type="hidden" name="${customName}.id" data-lc-field=".id" value="${(element.id?c)!}" />
    <input class="regionScope" type="hidden" name="${customName}.scope" data-lc-field=".scope" value="${regionScope}" />
    [#if isType]
      <input class="rId" type="hidden" name="${customName}.locElementType.id" data-lc-field=".locElementType.id" value="${regionId}" />
    [#else]
      <input class="rId" type="hidden" name="${customName}.locElement.id" data-lc-field=".locElement.id" value="${regionId}" />
    [/#if]
    [#if editable]
      <button type="button" class="lc-chip__remove" data-lc-region-remove aria-label="[@s.text name="projectLocations.redesign.remove"][@s.param]${regionName}[/@s.param][/@s.text]">
        <svg width="10" height="10" viewBox="0 0 12 12" fill="none" aria-hidden="true"><path d="M2.5 2.5l7 7M9.5 2.5l-7 7" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"></path></svg>
      </button>
    [/#if]
  </span>
[/#macro]

[#-- A funding-source suggestion: a checkbox that drives the posted .selected flag --]
[#macro suggestedLocation element name index isRegion]
  [#local customName = "${name}[${index}]" /]
  [#local isType = !(element.locElement??) /]
  [#local locName = (isType?then((element.locElementType.name)!'', (element.locElement.name)!''))!'' /]
  [#local selected = (element.selected)!false /]
  <label class="lc-fs[#if selected] is-on[/#if]" data-lc-fs data-fs-kind="${isRegion?then('region', 'country')}"
    data-name="${locName}" data-iso="${(element.locElement.isoAlpha2)!}"
    data-key="${isType?then('t', 'e')}${(isType?then((element.locElementType.id?c)!'', (element.locElement.id?c)!''))!}"
    [#if !editable && !selected] hidden[/#if]>
    <input type="checkbox" class="lc-fs__check" data-lc-fs-check[#if selected] checked[/#if][#if !editable] disabled[/#if] />
    <span class="lc-fs__box" aria-hidden="true"><svg width="11" height="11" viewBox="0 0 12 12" fill="none"><path d="M2.6 6.2 5 8.5l4.4-5" stroke="#fff" stroke-width="1.9" stroke-linecap="round" stroke-linejoin="round"></path></svg></span>
    <span class="lc-fs__body">
      <span class="lc-fs__name">${locName}</span>
      [#if element.fundingSources?has_content]
        <span class="lc-fs__meta">
          [#list element.fundingSources as fs]
            <a class="lc-fs__link" target="_blank" rel="noopener noreferrer" href="[@s.url namespace="/fundingSources" action="${(crpSession)!}/fundingSource"][@s.param name='fundingSourceID']${fs.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">FS${fs.id?c}[#if (fs.fundingSourceInfo.financeCode?has_content)!false] · ${fs.fundingSourceInfo.financeCode}[/#if][#if (fs.fundingSourceInfo.title?has_content)!false] · ${fs.fundingSourceInfo.title}[/#if]</a>[#if action.hasSpecificities('crp_fs_w1w2_cofinancing') && (fs.w1w2)!false] <span class="lc-fs__cofin">(Co-Financing)</span>[/#if]
          [/#list]
        </span>
      [/#if]
    </span>
    <span class="lc-tag">[#if isRegion][@s.text name="projectLocations.redesign.level.region" /][#else][@s.text name="projectLocations.redesign.level.country" /][/#if]</span>
    [#if isType]
      <input type="hidden" class="elementID" name="${customName}.locElementType.id" value="${(element.locElementType.id?c)!}" />
      <input type="hidden" class="locScope" name="${customName}.scope" value="${(element.locElementType.scope?c)!}" />
    [#else]
      <input type="hidden" class="elementID" name="${customName}.locElement.id" value="${(element.locElement.id?c)!}" />
      <input type="hidden" class="locScope" name="${customName}.scope" value="${(element.locElement.locElementType.scope?c)!}" />
    [/#if]
    <input type="hidden" class="recommended-location" name="${customName}.selected" value="${selected?string}" data-lc-fs-value />
  </label>
[/#macro]

[#-- A location level (Country, Province, ...) with its locations --]
[#macro locationGroup element index template=false]
  [#local customName = "project.locationsData[${index}]" /]
  [#local isList = (element.list)!false /]
  <div class="lc-group" data-lc-group[#if template] data-lc-group-template[/#if] data-level-id="${(element.id?c)!}" data-level-name="${(element.name)!}" data-is-list="${isList?string}">
    <span class="lc-group__head">
      <span class="lc-group__name" data-lc-group-name>${(element.name)!}</span>
      <span class="lc-group__count" data-lc-group-count></span>
    </span>
    <span class="lc-group__inputs">
      <input class="locationLevelId" type="hidden"[#if !template] name="${customName}.id"[/#if] data-lc-field=".id" value="${(element.id?c)!}" />
      <input class="locationLevelName" type="hidden"[#if !template] name="${customName}.name"[/#if] data-lc-field=".name" value="${(element.name)!}" />
      <input class="isList" type="hidden"[#if !template] name="${customName}.isList"[/#if] data-lc-field=".isList" value="${isList?string}" />
    </span>
    <div class="lc-group__rows" data-lc-rows>
      [#if !template && element.locElements?has_content]
        [#list element.locElements as location]
          [@locationRow element=location name="${customName}.locElements[${location_index}]" isList=isList /]
        [/#list]
      [/#if]
    </div>
  </div>
[/#macro]

[#-- One location. Coordinates of 0 were never real, so they post empty as before. --]
[#macro locationRow element isList name="" template=false]
  [#local lat = (element.locGeoposition.latitude)!0 /]
  [#local lon = (element.locGeoposition.longitude)!0 /]
  [#local hasCoords = (lat != 0 && lon != 0) /]
  <div class="lc-row" data-lc-row[#if template] data-lc-row-template[/#if] data-name="${(element.name)!}" data-key="${(element.id?c)!}"
    [#if hasCoords] data-lat="${lat?c}" data-lon="${lon?c}"[/#if]>
    <span class="lc-row__icon" aria-hidden="true" data-lc-row-icon></span>
    [#-- Shows the location on the map: a site centres on its point, a country fits its extent --]
    <button type="button" class="lc-row__body" data-lc-row-focus>
      <span class="lc-row__name" data-lc-row-name>${(element.name)!}</span>
      <span class="lc-row__meta" data-lc-row-meta></span>
    </button>
    [#if editable && otherLocations]
      <button type="button" class="lc-iconBtn lc-iconBtn--danger" data-lc-row-remove
        aria-label="[@s.text name="projectLocations.redesign.remove"][@s.param]${(element.name)!}[/@s.param][/@s.text]">
        <svg width="14" height="14" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M3 4.4h10M6.4 4.4V3h3.2v1.4M4.4 4.4l.6 8.6h6l.6-8.6" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round"></path></svg>
      </button>
    [/#if]
    <input type="hidden" class="locElementId"[#if !template] name="${name}.id"[/#if] data-lc-field=".id" value="${(element.id?c)!}" />
    <input type="hidden" class="locElementName"[#if !template] name="${name}.name"[/#if] data-lc-field=".name" value="${(element.name)!}" />
    <input type="hidden" class="locElementCountry"[#if !template] name="${name}.locElement.isoAlpha2"[/#if] data-lc-field=".locElement.isoAlpha2" value="${(element.isoAlpha2)!}" />
    <input type="hidden" class="geoId"[#if !template] name="${name}.locGeoposition.id"[/#if] data-lc-field=".locGeoposition.id" value="${(element.locGeoposition.id?c)!}" />
    <input type="hidden" class="geoLatitude"[#if !template] name="${name}.locGeoposition.latitude"[/#if] data-lc-field=".locGeoposition.latitude" value="${hasCoords?then(lat?c, '')}" />
    <input type="hidden" class="geoLongitude"[#if !template] name="${name}.locGeoposition.longitude"[/#if] data-lc-field=".locGeoposition.longitude" value="${hasCoords?then(lon?c, '')}" />
  </div>
[/#macro]

[#-- Inline add panel. The levels come from crp_loc_element_types; list levels load their options from
     searchCountryListPL.do, the others take a name and coordinates. --]
[#macro addLocationPanel]
  <div class="lc-add" data-lc-add hidden>
    <span class="lc-add__head">
      <span class="lc-add__title">[@s.text name="projectLocations.addingNewLocation" /]</span>
      <button type="button" class="lc-iconBtn" data-lc-add-cancel aria-label="[@s.text name="projectLocations.redesign.add.cancelLabel" /]">
        <svg width="11" height="11" viewBox="0 0 12 12" fill="none" aria-hidden="true"><path d="M2.5 2.5l7 7M9.5 2.5l-7 7" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"></path></svg>
      </button>
    </span>
    <div class="lc-add__grid">
      <div class="lc-field">
        <label class="lc-label" for="lc-level-select">[@s.text name="projectLocations.redesign.add.level" /]</label>
        <span class="lc-selectWrap">
          <select id="lc-level-select" class="lc-select" data-lc-level>
            <option value="">[@s.text name="projectLocations.redesign.add.levelPlaceholder" /]</option>
            [#list locationsLevels as locLevels]
              [#list locLevels.locations as level]
                <option value="${level.id?c}" data-list="${level.list?string}" data-name="${level.name}">${level.name}</option>
              [/#list]
            [/#list]
          </select>
          <svg class="lc-selectWrap__caret" width="11" height="11" viewBox="0 0 12 12" fill="none" aria-hidden="true"><path d="M2.5 4.5 6 8l3.5-3.5" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"></path></svg>
        </span>
        <span class="lc-help" data-lc-level-hint>[@s.text name="projectLocations.redesign.add.levelHint" /]</span>
      </div>
      <div class="lc-field lc-add__target">
        <div class="lc-add__placeholder" data-lc-add-none>[@s.text name="projectLocations.redesign.add.chooseLevel" /]</div>
        <div class="lc-add__list" data-lc-add-list hidden>
          <span class="lc-label" id="lc-pick-label">[@s.text name="projectLocations.selectLocations" /]</span>
          <span class="lc-picks" role="group" aria-labelledby="lc-pick-label" data-lc-picks></span>
        </div>
        <div class="lc-add__coords" data-lc-add-coords hidden>
          <div class="lc-coordGrid">
            <span class="lc-field">
              <label class="lc-label" for="lc-add-name">[@s.text name="projectLocations.locationName" /]</label>
              <input id="lc-add-name" type="text" class="lc-input" data-lc-add-name autocomplete="off" placeholder="[@s.text name="projectLocations.redesign.add.namePlaceholder" /]" />
            </span>
            <span class="lc-field">
              <label class="lc-label" for="lc-add-lat">[@s.text name="projectLocations.latitude" /]</label>
              <input id="lc-add-lat" type="text" inputmode="decimal" class="lc-input" data-lc-add-lat autocomplete="off" placeholder="14.1520" />
            </span>
            <span class="lc-field">
              <label class="lc-label" for="lc-add-lon">[@s.text name="projectLocations.longitude" /]</label>
              <input id="lc-add-lon" type="text" inputmode="decimal" class="lc-input" data-lc-add-lon autocomplete="off" placeholder="-16.0726" />
            </span>
          </div>
          <span class="lc-help lc-add__coordNote" data-lc-coord-note>
            <svg width="13" height="13" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M8 14.2s4.6-4.4 4.6-8A4.6 4.6 0 0 0 3.4 6.2c0 3.6 4.6 8 4.6 8Z" stroke="currentColor" stroke-width="1.4"></path><circle cx="8" cy="6.2" r="1.6" stroke="currentColor" stroke-width="1.4"></circle></svg>
            <span data-lc-coord-note-text>[@s.text name="projectLocations.redesign.add.coordHint" /]</span>
          </span>
        </div>
      </div>
    </div>
    <div class="lc-add__foot">
      <span class="lc-add__summary" data-lc-add-summary></span>
      <button type="button" class="lc-btn lc-btn--ghost" data-lc-add-cancel>[@s.text name="projectLocations.redesign.add.cancel" /]</button>
      <button type="button" class="lc-btn lc-btn--primary" data-lc-add-confirm disabled>[@s.text name="projectLocations.redesign.add.confirmOne" /]</button>
    </div>
  </div>
[/#macro]
