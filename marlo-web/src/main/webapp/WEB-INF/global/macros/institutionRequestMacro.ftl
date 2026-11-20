[#ftl]
[#-- Review cards of the superadmin Institutions screen (CHG-PARTREQ-001, the .prq scope of its stylesheet). --]
[#-- Every value that can be null goes through (x)!: a request may have no type, no country or no requester. --]

[#-- Text of a key, escaped for an HTML attribute --]
[#macro attrText key][@s.text name=key escapeHtml=true /][/#macro]

[#-- "3 Dec 2026"; empty when there is no date --]
[#function shortDate value=""]
  [#if value?has_content]
    [#return value?datetime?string("d MMM yyyy") /]
  [/#if]
  [#return "" /]
[/#function]

[#-- "First Last"; no email, which a screen must not print for the requester --]
[#function personName user={}]
  [#local name = (((user.firstName)!"") + " " + ((user.lastName)!""))?trim /]
  [#return name /]
[/#function]

[#-- A label and its value inside a card --]
[#macro readRow labelKey value="" muted=false]
  <div class="prq-ro__row">
    <dt class="prq-ro__label">[@s.text name=labelKey /]</dt>
    <dd class="prq-ro__value[#if muted] prq-ro__value--muted[/#if]">
      [#if value?has_content]${value}[#else][@s.text name="marloRequestInstitution.ui.notProvided" /][/#if]
    </dd>
  </div>
[/#macro]

[#-- Who asked, from where and when: shared by the partner and the office cards --]
[#macro requestDetails request]
  <dl class="prq-ro">
    [#local requester = personName((request.createdBy)!{}) /]
    [@readRow labelKey="marloRequestInstitution.ui.requester" value=requester muted=!requester?has_content /]
    [@readRow labelKey="marloRequestInstitution.ui.source" value=(request.requestSource)!"" /]
    [@readRow labelKey="marloRequestInstitution.ui.unit" value=(request.crp.acronym)!"" /]
    [@readRow labelKey="marloRequestInstitution.ui.date" value=shortDate((request.activeSince)!"") /]
    [#if (request.externalUserMail)?has_content]
      [@readRow labelKey="marloRequestInstitution.ui.apiRequester" value=((request.externalUserName)!"") + " (" + request.externalUserMail + ")" /]
      [@readRow labelKey="marloRequestInstitution.ui.apiComments" value=(request.externalUserComments)!"" /]
    [/#if]
  </dl>
[/#macro]

[#macro flagCountry request]
  [#local iso = ((request.locElement.isoAlpha2)!"")?lower_case /]
  [#if iso?has_content]<span class="flag-icon flag-icon-${iso}" aria-hidden="true"></span> [/#if]${(request.locElement.name)!""}
[/#macro]

[#-- Field-level changes of an edited request: only the fields whose value differs from the original (UI-003) --]
[#macro changedFields partner]
  [#local orig = (partner.partnerRequest)!{} /]
  [#local changes = [] /]
  [#if ((orig.partnerName)!"") != ((partner.partnerName)!"")]
    [#local changes = changes + [{"key":"name", "from":((orig.partnerName)!""), "to":((partner.partnerName)!"")}] /]
  [/#if]
  [#if ((orig.acronym)!"") != ((partner.acronym)!"")]
    [#local changes = changes + [{"key":"acronym", "from":((orig.acronym)!""), "to":((partner.acronym)!"")}] /]
  [/#if]
  [#if ((orig.institutionType.id)!0) != ((partner.institutionType.id)!0)]
    [#local changes = changes + [{"key":"type", "from":((orig.institutionType.name)!""), "to":((partner.institutionType.name)!"")}] /]
  [/#if]
  [#if ((orig.locElement.id)!0) != ((partner.locElement.id)!0)]
    [#local changes = changes + [{"key":"country", "from":((orig.locElement.name)!""), "to":((partner.locElement.name)!"")}] /]
  [/#if]
  [#if ((orig.webPage)!"") != ((partner.webPage)!"")]
    [#local changes = changes + [{"key":"webPage", "from":((orig.webPage)!""), "to":((partner.webPage)!"")}] /]
  [/#if]
  [#if changes?has_content || (partner.modificationJustification)?has_content]
  <section class="prq-diff" aria-label="[@attrText key='marloRequestInstitution.ui.changes.title' /]">
    <h4 class="prq-diff__title">[@s.text name="marloRequestInstitution.ui.changes.title" /]</h4>
    [#if changes?has_content]
      <ul class="prq-diff__list">
        [#list changes as change]
          <li class="prq-diff__row">
            <span class="prq-diff__field">[@s.text name="marloRequestInstitution.ui.field.${change.key}" /]</span>
            <span class="prq-diff__cell">
              <span class="prq-diff__tag">[@s.text name="marloRequestInstitution.ui.original" /]</span>
              [#if change.from?has_content]<del class="prq-diff__old">${change.from}</del>[#else]<span class="prq-diff__none">[@s.text name="marloRequestInstitution.ui.notProvided" /]</span>[/#if]
            </span>
            <span class="prq-diff__cell">
              <span class="prq-diff__tag">[@s.text name="marloRequestInstitution.ui.edited" /]</span>
              [#if change.to?has_content]<ins class="prq-diff__new">${change.to}</ins>[#else]<span class="prq-diff__none">[@s.text name="marloRequestInstitution.ui.notProvided" /]</span>[/#if]
            </span>
          </li>
        [/#list]
      </ul>
    [/#if]
    [#if (partner.modificationJustification)?has_content]
      <p class="prq-diff__why"><span class="prq-diff__tag">[@s.text name="marloRequestInstitution.ui.field.justification" /]</span> ${partner.modificationJustification}</p>
    [/#if]
  </section>
  [/#if]
[/#macro]

[#-- Inline edit form of one partner request. The input names are the ones editPartnerRequest reads. --]
[#macro editForm partner]
  [#local id = partner.id?c /]
  <form class="prq-form" id="prq-edit-${id}" data-prq-form="edit" data-request-id="${id}" hidden novalidate>
    <input type="hidden" name="requestID" value="${id}" />
    <div class="prq-form__grid">
      <div class="prq-field prq-field--wide">
        <label class="prq-label" for="prq-edit-name-${id}">[@s.text name="marloRequestInstitution.ui.field.name" /] <span class="prq-req" aria-hidden="true">*</span></label>
        <input type="text" class="prq-input" id="prq-edit-name-${id}" name="institutionName" value="${(partner.partnerName)!""}" aria-required="true" aria-describedby="prq-edit-name-${id}-error" />
        <p class="prq-error" id="prq-edit-name-${id}-error" hidden>[@s.text name="marloRequestInstitution.ui.error.required" /]</p>
      </div>
      <div class="prq-field">
        <label class="prq-label" for="prq-edit-acronym-${id}">[@s.text name="marloRequestInstitution.ui.field.acronym" /]</label>
        <input type="text" class="prq-input" id="prq-edit-acronym-${id}" name="institutionAcronym" value="${(partner.acronym)!""}" />
      </div>
      <div class="prq-field">
        <label class="prq-label" for="prq-edit-type-${id}">[@s.text name="marloRequestInstitution.ui.field.type" /] <span class="prq-req" aria-hidden="true">*</span></label>
        <select class="prq-select" id="prq-edit-type-${id}" name="institutionTypeID" aria-required="true" aria-describedby="prq-edit-type-${id}-error">
          <option value="-1">[@s.text name="marloRequestInstitution.ui.field.select" /]</option>
          [#list (institutionTypesList)![] as type]
            <option value="${type.id?c}"[#if ((partner.institutionType.id)!0) == type.id] selected[/#if]>${(type.name)!""}</option>
          [/#list]
        </select>
        <p class="prq-error" id="prq-edit-type-${id}-error" hidden>[@s.text name="marloRequestInstitution.ui.error.required" /]</p>
      </div>
      <div class="prq-field">
        <label class="prq-label" for="prq-edit-country-${id}">[@s.text name="marloRequestInstitution.ui.field.country" /] <span class="prq-req" aria-hidden="true">*</span></label>
        <select class="prq-select" id="prq-edit-country-${id}" name="countryID" aria-required="true" aria-describedby="prq-edit-country-${id}-error">
          <option value="-1">[@s.text name="marloRequestInstitution.ui.field.select" /]</option>
          [#list (countriesList)![] as country]
            <option value="${(country.isoAlpha2)!""}"[#if ((partner.locElement.isoAlpha2)!"") == ((country.isoAlpha2)!"")] selected[/#if]>${(country.name)!""}</option>
          [/#list]
        </select>
        <p class="prq-error" id="prq-edit-country-${id}-error" hidden>[@s.text name="marloRequestInstitution.ui.error.required" /]</p>
      </div>
      <div class="prq-field prq-field--wide">
        <label class="prq-label" for="prq-edit-web-${id}">[@s.text name="marloRequestInstitution.ui.field.webPage" /]</label>
        <input type="text" class="prq-input" id="prq-edit-web-${id}" name="institutionWebPage" value="${(partner.webPage)!""}" />
      </div>
      <div class="prq-field prq-field--wide">
        <label class="prq-label" for="prq-edit-why-${id}">[@s.text name="marloRequestInstitution.ui.field.justification" /] <span class="prq-req" aria-hidden="true">*</span></label>
        <textarea class="prq-textarea" id="prq-edit-why-${id}" name="justification" rows="3" aria-required="true" aria-describedby="prq-edit-why-${id}-error">${(partner.modificationJustification)!""}</textarea>
        <p class="prq-error" id="prq-edit-why-${id}-error" hidden>[@s.text name="marloRequestInstitution.ui.error.required" /]</p>
      </div>
    </div>
    <div class="prq-form__foot">
      <button type="button" class="prq-btn prq-btn--ghost" data-prq-action="cancel-edit">[@s.text name="marloRequestInstitution.ui.dialog.cancel" /]</button>
      <button type="submit" class="prq-btn prq-btn--primary" data-prq-action="save-edit">[@s.text name="marloRequestInstitution.ui.action.save" /]</button>
    </div>
  </form>
[/#macro]

[#-- Empty state of a list; it stays in the page (hidden) while the list has cards so a decision can reveal it --]
[#macro emptyState titleKey show=true]
  <div class="prq-empty" data-prq-empty[#if !show] hidden[/#if]>
    <p class="prq-empty__title">[@s.text name=titleKey /]</p>
    <p class="prq-empty__text">[@s.text name="marloRequestInstitution.ui.empty.hint" /]</p>
  </div>
[/#macro]

[#macro partnersList partners={} canEdit=false  namespace="/" defaultAction=""]
  [#if partners?has_content]
    <ul class="prq-list" data-prq-list="partners">
      [#list partners as partner]
        [#local id = partner.id?c /]
        <li id="partnerRequestItem-${id}" class="prq-card prq-card--partner partnerRequestItem" data-request-id="${id}" data-prq-name="${(partner.partnerName)!""}">
          <div class="loading" style="display:none"></div>
          <div class="prq-card__head">
            <div class="prq-card__title">
              <h3 class="prq-card__name">${(partner.partnerInfo)!""}</h3>
              <p class="prq-card__meta">
                [#if (partner.institutionType.name)?has_content]${partner.institutionType.name}[#else]<span class="prq-muted">[@s.text name="marloRequestInstitution.ui.unknown" /]</span>[/#if]
                <span class="prq-card__dot" aria-hidden="true"></span>
                [#if (partner.locElement.name)?has_content][@flagCountry request=partner /][#else]<span class="prq-muted">[@s.text name="marloRequestInstitution.ui.unknown" /]</span>[/#if]
              </p>
              [#if (partner.webPage)?has_content]
                <p class="prq-card__link"><a href="${partner.webPage}" target="_blank" rel="noopener noreferrer">${partner.webPage}</a></p>
              [/#if]
            </div>
            <div class="prq-card__badges">
              <span class="prq-badge prq-badge--pending">[@s.text name="marloRequestInstitution.ui.badge.pending" /]</span>
              [#if (partner.modified)!false]<span class="prq-badge prq-badge--edited">[@s.text name="marloRequestInstitution.ui.badge.edited" /]</span>[/#if]
            </div>
          </div>

          <div class="prq-card__body">
            [#if (partner.modified)!false][@changedFields partner=partner /][/#if]
            [@requestDetails request=partner /]
            [#-- Similar institutions: filled by marloInstitutions.js --]
            <div class="prq-note prq-note--info prq-similar sameness" data-prq-similar hidden>
              <div class="prq-note__body">
                <p class="prq-note__title">[@s.text name="marloRequestInstitution.ui.similar" /]</p>
                <ul class="prq-similar__list"></ul>
              </div>
            </div>
          </div>

          [@editForm partner=partner /]

          <div class="prq-card__foot" data-prq-actions>
            <button type="button" class="prq-btn prq-btn--ghost" data-prq-action="edit" aria-expanded="false" aria-controls="prq-edit-${id}">[@s.text name="marloRequestInstitution.ui.action.edit" /]</button>
            <button type="button" class="prq-btn prq-btn--danger" data-prq-action="reject" data-request-id="${id}">[@s.text name="marloRequestInstitution.ui.action.reject" /]</button>
            [#-- Accept: a POST form; the confirmation dialog is attached to it by marloInstitutions.js --]
            <form class="acceptRequestForm prq-inlineForm" method="post" data-prq-confirm="prqAcceptDialog" action="[@s.url namespace="" action="superadmin/addPartner"][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
              <input type="hidden" name="requestID" value="${id}" />
              <button type="submit" class="prq-btn prq-btn--primary" data-prq-action="accept">[@s.text name="marloRequestInstitution.ui.action.accept" /]</button>
            </form>
          </div>
        </li>
      [/#list]
    </ul>
  [/#if]
  [@emptyState titleKey="marloRequestInstitution.ui.empty.partners" show=!partners?has_content /]
[/#macro]

[#macro officesRequest partners={} canEdit=false  namespace="/" defaultAction=""]
  [#if partners?has_content]
    <ul class="prq-list" data-prq-list="offices">
      [#list partners as partner]
        [#local institutionId = partner.institution.id?c /]
        [#local customName = "countryOfficePOJO" /]
        <li id="officesRequestItem-${institutionId}" class="prq-card prq-card--office officesRequestItem" data-institution-id="${institutionId}" data-prq-name="${(partner.institution.composedName)!""}">
          <div class="loading" style="display:none"></div>
          <form class="prq-officeForm" data-prq-form="offices" novalidate>
            <input type="hidden" class="institutionID" name="${customName}.institution.id" value="${institutionId}" />
            <div class="prq-card__head">
              <div class="prq-card__title">
                <h3 class="prq-card__name">${(partner.institution.composedName)!""}</h3>
                [#if (partner.institution.websiteLink)?has_content]
                  <p class="prq-card__link"><a href="${partner.institution.websiteLink}" target="_blank" rel="noopener noreferrer">${partner.institution.websiteLink}</a></p>
                [/#if]
              </div>
              <div class="prq-card__badges">
                <span class="prq-badge prq-badge--pending">[@s.text name="marloRequestInstitution.ui.badge.pending" /]</span>
                <span class="prq-badge prq-badge--count">[@s.text name="marloRequestInstitution.ui.offices.requestedCount"][@s.param]${(partner.partnerRequest?size)!0}[/@s.param][/@s.text]</span>
              </div>
            </div>

            <div class="prq-card__body">
              [#if (partner.institution.institutionsLocations)?has_content]
                <div class="prq-existing">
                  <p class="prq-existing__label">[@s.text name="marloRequestInstitution.ui.offices.existing" /]</p>
                  <ul class="prq-chips">
                    [#list partner.institution.institutionsLocations as location]
                      <li class="prq-chip">${(location.locElement.name)!""}</li>
                    [/#list]
                  </ul>
                </div>
              [/#if]
              <fieldset class="prq-fieldset">
                <legend class="prq-fieldset__legend">[@s.text name="marloRequestInstitution.ui.offices.requested" /]</legend>
                <p class="prq-fieldset__help">[@s.text name="marloRequestInstitution.ui.offices.select" /]</p>
                <ul class="prq-offices">
                  [#list partner.partnerRequest as officeRequest]
                    [#local officeId = officeRequest.id?c /]
                    <li class="prq-office officeCountryRequest">
                      <label class="prq-office__pick" for="officeRequest-${officeId}">
                        <input type="checkbox" class="prq-checkbox officeRequest" name="${customName}.partnerRequest.id" id="officeRequest-${officeId}" value="${officeId}" />
                        <span class="prq-office__country">[@flagCountry request=officeRequest /]</span>
                      </label>
                      [@requestDetails request=officeRequest /]
                    </li>
                  [/#list]
                </ul>
              </fieldset>
              <p class="prq-error" id="prq-offices-error-${institutionId}" data-prq-offices-error hidden>[@s.text name="marloRequestInstitution.ui.offices.error.none" /]</p>
            </div>

            <div class="prq-card__foot" data-prq-actions>
              <button type="button" class="prq-btn prq-btn--danger" data-prq-action="reject-offices" data-institution-id="${institutionId}">[@s.text name="marloRequestInstitution.ui.action.rejectSelected" /]</button>
              <button type="button" class="prq-btn prq-btn--primary" data-prq-action="accept-offices" data-institution-id="${institutionId}">[@s.text name="marloRequestInstitution.ui.action.acceptSelected" /]</button>
            </div>
          </form>
        </li>
      [/#list]
    </ul>
  [/#if]
  [@emptyState titleKey="marloRequestInstitution.ui.empty.offices" show=!partners?has_content /]
[/#macro]
