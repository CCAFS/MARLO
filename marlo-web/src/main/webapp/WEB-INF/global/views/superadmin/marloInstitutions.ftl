[#ftl]
[#assign title = "MARLO Admin" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = [ "flag-icon-css", "datatables.net", "datatables.net-bs"] /]
[#assign customJS = [ 
  "${baseUrlCdn}/global/js/superadmin/marloInstitutions.js?20261120-2" 
  ] 
/]
[#assign customCSS = [ 
  "${baseUrlCdn}/global/css/superadmin/superadmin.css",
  "${baseUrlCdn}/global/css/superadmin/marloInstitutions.css?20261120-1"
  ] 
/]
[#assign currentSection = "superadmin" /]
[#assign currentStage = "institutions" /]

[#assign breadCrumb = [
  {"label":"superadmin", "nameSpace":"", "action":"marloBoard"},
  {"label":"institutions", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#import "/WEB-INF/global/macros/institutionRequestMacro.ftl" as institutionRequest /]
<hr />

<div class="container">
  [#include "/WEB-INF/global/pages/breadcrumb.ftl" /]
</div>
[#-- The generic "saved" notification of generalMessages.ftl is not used here: the outcome of a decision is shown as a note below. --]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities/]

[#assign partnerCount = (partners?size)!0 /]
[#assign officeCount = (countryOfficesList?size)!0 /]

<section class="marlo-content">
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/global/views/superadmin/menu-superadmin.ftl" /]
      </div>
      <div class="col-md-9">
        <div class="prq" id="prqPage"
          data-outcome-done="[@institutionRequest.attrText key='marloRequestInstitution.ui.outcome.done' /]"
          data-outcome-alreadyhandled="[@institutionRequest.attrText key='marloRequestInstitution.ui.outcome.alreadyHandled' /]"
          data-outcome-notfound="[@institutionRequest.attrText key='marloRequestInstitution.ui.outcome.notFound' /]"
          data-outcome-wrongkind="[@institutionRequest.attrText key='marloRequestInstitution.ui.outcome.wrongKind' /]"
          data-outcome-skippedexisting="[@institutionRequest.attrText key='marloRequestInstitution.ui.outcome.skippedExisting' /]"
          data-outcome-notallowed="[@institutionRequest.attrText key='marloRequestInstitution.ui.outcome.notAllowed' /]"
          data-outcome-notauthorized="[@institutionRequest.attrText key='marloRequestInstitution.ui.outcome.notAuthorized' /]"
          data-outcome-sessionexpired="[@institutionRequest.attrText key='marloRequestInstitution.ui.outcome.sessionExpired' /]"
          data-outcome-failed="[@institutionRequest.attrText key='marloRequestInstitution.ui.outcome.failed' /]"
          data-text-managing="[@institutionRequest.attrText key='marloRequestInstitution.ui.similar.managing' /]"
          data-text-required="[@institutionRequest.attrText key='marloRequestInstitution.ui.error.required' /]"
          data-text-selectoffice="[@institutionRequest.attrText key='marloRequestInstitution.ui.offices.error.none' /]"
          data-text-partners-one="[@institutionRequest.attrText key='marloRequestInstitution.ui.summary.partners.one' /]"
          data-text-partners-many="[@institutionRequest.attrText key='marloRequestInstitution.ui.summary.partners.many' /]"
          data-text-offices-one="[@institutionRequest.attrText key='marloRequestInstitution.ui.summary.offices.one' /]"
          data-text-offices-many="[@institutionRequest.attrText key='marloRequestInstitution.ui.summary.offices.many' /]"
          data-text-of="[@institutionRequest.attrText key='marloRequestInstitution.ui.filter.of' /]"
          data-text-filtered-partners="[@institutionRequest.attrText key='marloRequestInstitution.ui.filter.summary.partners' /]"
          data-text-filtered-offices="[@institutionRequest.attrText key='marloRequestInstitution.ui.filter.summary.offices' /]">

          [#-- A div, not a <header>: marlo-redesign.css gives every <header> the brand header's bottom border --]
          <div class="prq-head">
            <div class="prq-head__main">
              <h1 class="prq-head__title">[@s.text name="marloRequestInstitution.ui.title" /]</h1>
              <p class="prq-head__summary" id="prqSummary" aria-live="polite">
                <span data-prq-summary="partners">
                  [#if partnerCount == 1][@s.text name="marloRequestInstitution.ui.summary.partners.one" /][#else][@s.text name="marloRequestInstitution.ui.summary.partners.many"][@s.param]${partnerCount?c}[/@s.param][/@s.text][/#if]
                </span>
                <span class="prq-head__sep" aria-hidden="true">&middot;</span>
                <span data-prq-summary="offices">
                  [#if officeCount == 1][@s.text name="marloRequestInstitution.ui.summary.offices.one" /][#else][@s.text name="marloRequestInstitution.ui.summary.offices.many"][@s.param]${officeCount?c}[/@s.param][/@s.text][/#if]
                </span>
              </p>
            </div>
            <button type="button" class="prq-btn prq-btn--ghost" data-prq-open="allInstitutions" aria-haspopup="dialog">
              [@s.text name="marloRequestInstitution.ui.action.allInstitutions" /]
            </button>
          </div>

          [#-- Search and kind filter: client-side only, applied by marloInstitutions.js to the cards below --]
          <div class="prq-toolbar" data-prq-toolbar>
            <div class="prq-search">
              <svg class="prq-search__icon" width="16" height="16" viewBox="0 0 16 16" fill="none" aria-hidden="true" focusable="false"><circle cx="7" cy="7" r="4.75" stroke="currentColor" stroke-width="1.5"/><path d="M10.6 10.6L14 14" stroke="currentColor" stroke-width="1.5" stroke-linecap="round"/></svg>
              <input type="text" class="prq-input" id="prqSearch" data-prq-search-input autocomplete="off" spellcheck="false" enterkeyhint="search"
                aria-label="[@institutionRequest.attrText key='marloRequestInstitution.ui.filter.search' /]"
                placeholder="[@institutionRequest.attrText key='marloRequestInstitution.ui.filter.search' /]" />
            </div>
            <div class="prq-seg" role="group" aria-label="[@institutionRequest.attrText key='marloRequestInstitution.ui.filter.group' /]">
              <button type="button" class="prq-seg__btn is-on" data-prq-seg="all" aria-pressed="true">[@s.text name="marloRequestInstitution.ui.filter.all" /] <span class="prq-seg__count" data-prq-seg-count="all">${(partnerCount + officeCount)?c}</span></button>
              <button type="button" class="prq-seg__btn" data-prq-seg="partners" aria-pressed="false">[@s.text name="marloRequestInstitution.ui.filter.partners" /] <span class="prq-seg__count" data-prq-seg-count="partners">${partnerCount?c}</span></button>
              <button type="button" class="prq-seg__btn" data-prq-seg="offices" aria-pressed="false">[@s.text name="marloRequestInstitution.ui.filter.offices" /] <span class="prq-seg__count" data-prq-seg-count="offices">${officeCount?c}</span></button>
            </div>
            <button type="button" class="prq-btn prq-btn--ghost prq-toolbar__clear" data-prq-clear hidden>[@s.text name="marloRequestInstitution.ui.filter.clear" /]</button>
          </div>

          [#-- Outcome of the last decision (a redirected accept) and errors: the only message on this screen --]
          <div class="prq-notes" id="prqNotes" role="status" aria-live="polite">
            [#list (actionMessages)![] as message]
              <p class="prq-note prq-note--success"><span class="prq-note__text">${message}</span></p>
            [/#list]
            [#assign failedText = action.getText("marloRequestInstitution.ui.outcome.failed") /]
            [#list (actionErrors)![] as message]
              <p class="prq-note prq-note--[#if message == failedText]danger[#else]warning[/#if]"><span class="prq-note__text">${message}</span></p>
            [/#list]
          </div>

          [#-- Requested institutions --]
          <section class="prq-section" data-prq-section="partners" aria-labelledby="prqPartnersTitle">
            <div class="prq-section__head">
              <h2 class="prq-section__title" id="prqPartnersTitle">[@s.text name="marloRequestInstitution.ui.section.partners" /]</h2>
              <span class="prq-count" data-prq-count="partners">${partnerCount?c}</span>
            </div>
            [@institutionRequest.partnersList partners=partners  canEdit=editable namespace="/marloInstitutions" defaultAction="${(crpSession)!}/marloInstitutions"/]
            <div class="prq-empty prq-empty--filtered" data-prq-nomatch hidden>
              <p class="prq-empty__title">[@s.text name="marloRequestInstitution.ui.filter.empty" /]</p>
              <button type="button" class="prq-btn prq-btn--ghost" data-prq-clear>[@s.text name="marloRequestInstitution.ui.filter.clear" /]</button>
            </div>
          </section>

          [#-- Requested office locations --]
          <section class="prq-section" data-prq-section="offices" aria-labelledby="prqOfficesTitle">
            <div class="prq-section__head">
              <h2 class="prq-section__title" id="prqOfficesTitle">[@s.text name="marloRequestInstitution.ui.section.offices" /]</h2>
              <span class="prq-count" data-prq-count="offices">${officeCount?c}</span>
            </div>
            [@institutionRequest.officesRequest partners=countryOfficesList  canEdit=editable namespace="/marloInstitutions" defaultAction="${(crpSession)!}/marloInstitutions"/]
            <div class="prq-empty prq-empty--filtered" data-prq-nomatch hidden>
              <p class="prq-empty__title">[@s.text name="marloRequestInstitution.ui.filter.empty" /]</p>
              <button type="button" class="prq-btn prq-btn--ghost" data-prq-clear>[@s.text name="marloRequestInstitution.ui.filter.clear" /]</button>
            </div>
          </section>

          [#-- Decision dialogs --]
          [#include "/WEB-INF/global/macros/rejectInstitutionPopup.ftl" /]

          [#-- All institutions dialog --]
          [#include "/WEB-INF/global/macros/allInstitutionsPopup.ftl" /]
        </div>
      </div>
    </div>
  </div>
</section>

[#include "/WEB-INF/global/pages/footer.ftl" /]
