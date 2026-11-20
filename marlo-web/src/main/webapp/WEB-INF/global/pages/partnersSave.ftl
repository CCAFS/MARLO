[#ftl]
[#assign title][@s.text name="marloRequestCreation.pageTitle" /][/#assign]
[#assign pageLibs = ["jquery", "noty","select2"] /]
[#assign customJS = ["${baseUrlCdn}/global/js/partnersSave.js?20261120"] /]
[#assign customCSS = ["${baseUrlCdn}/global/css/partnersSave.css?20261120"] /]
[#assign includeHeader = "false" /]
[#assign currentSection = "projects" /]
[#assign currentStage = "partners" /]

[#include "/WEB-INF/global/pages/header.ftl" /]

[#-- The error of one field, written by the action's validate(): the text sits under the field and is read with it --]
[#macro prqError name id]
  [#local errors = (action.fieldErrors[name])![] /]
  [#if errors?has_content]
    <p class="prq-error" id="${id}">[#list errors as error]${error}[#sep] [/#list]</p>
  [/#if]
[/#macro]

[#assign nameErrors = (action.fieldErrors["activityPartner.partner.name"])![] /]
[#assign typeErrors = (action.fieldErrors["activityPartner.partner.institutionType.id"])![] /]
[#assign countryErrors = (action.fieldErrors["locationId"])![] /]
[#assign selectedType = (activityPartner.partner.institutionType.id)!-1 /]
[#assign hasFormErrors = (action.hasFieldErrors())!false || (action.missingRequiredFields)!false /]

<main class="prq prq--request" id="prqRequest">
  <section class="prq-card prq-request" aria-labelledby="prqRequestTitle">
    [#if !messageSent]
      <div class="prq-card__head prq-request__head">
        <h1 class="prq-request__title" id="prqRequestTitle">[@s.text name="marloRequestCreation.title" /]</h1>
      </div>

      <div class="prq-request__notes">
        [#if hasFormErrors]
          <p class="prq-note prq-note--warning" role="alert" id="prqFormErrors"><span class="prq-note__text">[@s.text name="saving.fields.required" /]</span></p>
        [/#if]
        [#if (action.requestFailed)!false]
          <p class="prq-note prq-note--danger" role="alert"><span class="prq-note__text">[@s.text name="marloRequestCreation.saveFailed" /]</span></p>
        [/#if]
        <div class="prq-note prq-note--info">
          <p class="prq-note__text">[@s.text name="marloRequestCreation.helpMessage" /]<br />[@s.text name="marloRequestCreation.helpMessage.example" /]</p>
        </div>
      </div>

      [@s.form action="${crpSession}/partnerSave" cssClass="prq-request__form"]
        <div class="prq-request__grid">
          [#-- Partner name --]
          <div class="prq-field prq-field--wide" id="partnerName">
            <label class="prq-label" for="activityPartner.partner.name" id="prqNameLabel">[@s.text name="marloRequestCreation.label.name" /]<span class="prq-req" aria-hidden="true">*</span></label>
            <input type="text" class="prq-input[#if nameErrors?has_content] is-invalid[/#if]" id="activityPartner.partner.name" name="activityPartner.partner.name" value="${(activityPartner.partner.name)!}" autocomplete="off" aria-required="true"[#if nameErrors?has_content] aria-invalid="true" aria-describedby="prqNameError"[/#if] />
            [@prqError name="activityPartner.partner.name" id="prqNameError" /]
          </div>

          [#-- Partner types list --]
          <div class="prq-field prq-field--wide" id="partnerTypes">
            <label class="prq-label" for="activityPartner.partner.institutionType.id" id="prqTypeLabel">[@s.text name="marloRequestCreation.label.type" /]<span class="prq-req" aria-hidden="true">*</span></label>
            <select class="prq-select institutionTypes[#if typeErrors?has_content] is-invalid[/#if]" id="activityPartner.partner.institutionType.id" name="activityPartner.partner.institutionType.id" aria-required="true"[#if typeErrors?has_content] aria-invalid="true" aria-describedby="prqTypeError"[/#if]>
              <option value="-1">[@s.text name="form.select.placeholder" /]</option>
              [#list institutionTypesList as type]
                <option value="${type.id?c}"[#if selectedType == type.id] selected="selected"[/#if]>${(type.name)!''}</option>
              [/#list]
            </select>
            [@prqError name="activityPartner.partner.institutionType.id" id="prqTypeError" /]
            <div hidden>
              [#list institutionTypesList as type]
                <div id="institutionType-${type.id?c}">
                  <strong>${(type.acronym)!} ${(type.name)!'No Name'}</strong><br />
                  <i>${(type.description)!''}</i>
                </div>
              [/#list]
            </div>
          </div>

          [#-- Partner acronym --]
          <div class="prq-field" id="partnerAcronym">
            <label class="prq-label" for="activityPartner.partner.acronym">[@s.text name="marloRequestCreation.label.acronym" /]</label>
            <input type="text" class="prq-input" id="activityPartner.partner.acronym" name="activityPartner.partner.acronym" value="${(activityPartner.partner.acronym)!}" autocomplete="off" />
          </div>

          [#-- Countries list --]
          <div class="prq-field" id="partnerCountry">
            <label class="prq-label" for="locationId" id="prqCountryLabel">[@s.text name="marloRequestCreation.label.country" /]<span class="prq-req" aria-hidden="true">*</span></label>
            <select class="prq-select[#if countryErrors?has_content] is-invalid[/#if]" id="locationId" name="locationId" aria-required="true"[#if countryErrors?has_content] aria-invalid="true" aria-describedby="prqCountryError"[/#if]>
              <option value="-1">[@s.text name="form.select.placeholder" /]</option>
              [#list countriesList as country]
                <option value="${country.id?c}"[#if (locationId!0) == country.id] selected="selected"[/#if]>${(country.name)!''}</option>
              [/#list]
            </select>
            [@prqError name="locationId" id="prqCountryError" /]
          </div>

          [#-- Web page link --]
          <div class="prq-field prq-field--wide" id="partnerPage">
            <label class="prq-label" for="activityPartner.partner.websiteLink">[@s.text name="marloRequestCreation.label.website" /]</label>
            <input type="text" class="prq-input" id="activityPartner.partner.websiteLink" name="activityPartner.partner.websiteLink" value="${(activityPartner.partner.websiteLink)!}" placeholder="[@s.text name="marloRequestCreation.webSiteLink.placeholder" /]" inputmode="url" autocomplete="off" />
          </div>
        </div>

        [#-- Where the request was made from: the identifiers the action reads --]
        [#if projectID??]<input type="hidden" name="projectID" value="${projectID}"/>[/#if]
        [#if fundingSourceID??]<input type="hidden" name="fundingSourceID" value="${fundingSourceID}"/>[/#if]
        [#if expectedID??]<input type="hidden" name="expectedID" value="${expectedID}"/>[/#if]
        [#if capdevID??]<input type="hidden" name="capdevID" value="${capdevID}"/>[/#if]
        [#if powbSynthesisID??]<input type="hidden" name="powbSynthesisID" value="${(powbSynthesisID)!}"/>[/#if]
        [#if synthesisID??]<input type="hidden" name="synthesisID" value="${(synthesisID)!}"/>[/#if]
        <input type="hidden" name="context" value="${(context)!}" />
        <input type="hidden" name="phaseID" value="${(actualPhase.id)!}"/>

        <div class="prq-request__foot">
          [@s.submit type="button" name="save" cssClass="prq-btn prq-btn--primary"][@s.text name="form.buttons.savePartner.request" /][/@s.submit]
        </div>
      [/@s.form]

    [#else]

      <div class="prq-success" role="status">
        <svg class="prq-success__icon" viewBox="0 0 24 24" width="40" height="40" aria-hidden="true" focusable="false"><circle cx="12" cy="12" r="11" fill="none" stroke="currentColor" stroke-width="2"/><path d="M7 12.5l3.2 3.2L17 8.8" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/></svg>
        <h1 class="prq-success__title" id="prqRequestTitle">[@s.text name="marloRequestCreation.success.title" /]</h1>
        <p class="prq-success__text">[@s.text name="marloRequestCreation.success.text" /]</p>
        <div class="prq-success__actions">
          [#if projectID?? && (projectID != 0)]<a href="[@s.url][@s.param name="projectID" value=projectID /][/@s.url]" class="prq-btn prq-btn--ghost">[@s.text name="marloRequestCreation.success.again" /]</a>[/#if]
          [#if fundingSourceID?? && (fundingSourceID != 0)]<a href="[@s.url][@s.param name="fundingSourceID" value=fundingSourceID /][/@s.url]" class="prq-btn prq-btn--ghost">[@s.text name="marloRequestCreation.success.again" /]</a>[/#if]
          <button type="button" class="prq-btn prq-btn--primary" data-prq-close>[@s.text name="marloRequestCreation.success.close" /]</button>
        </div>
      </div>

    [/#if]
  </section>
</main>

  [#-- Importing JavaScript files --]
  [#if globalLibs??]
    [#list globalLibs as libraryName][@components.js_imports libraryName=libraryName/][/#list]
  [/#if]

  [#-- Importing JavaScript files --]
  [#if pageLibs??]
    [#list pageLibs as libraryName][@components.js_imports libraryName=libraryName/][/#list]
  [/#if]
  [#-- import the custom JS and CSS --]
  [#if customJS??][#list customJS as js]<script src="${js}"></script>[/#list][/#if]
[#-- Closes #pageContent, opened by header.ftl. This page does not include
     footer.ftl, which is where every other page closes the wrapper. --]
</div>
</body>