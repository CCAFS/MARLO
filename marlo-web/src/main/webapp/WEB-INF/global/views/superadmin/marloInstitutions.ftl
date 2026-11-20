[#ftl]
[#assign title = "MARLO Admin" /]
[#assign currentSectionString = "${actionName?replace('/','-')}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = [ "flag-icon-css", "datatables.net", "datatables.net-bs"] /]
[#assign customJS = [ 
  "${baseUrlCdn}/global/js/superadmin/marloInstitutions.js?20261119-1" 
  ] 
/]
[#assign customCSS = [ 
  "${baseUrlCdn}/global/css/superadmin/superadmin.css",
  "${baseUrlCdn}/global/css/superadmin/marloInstitutions.css?20261119"
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
          data-outcome-failed="[@institutionRequest.attrText key='marloRequestInstitution.ui.outcome.failed' /]"
          data-text-managing="[@institutionRequest.attrText key='marloRequestInstitution.ui.similar.managing' /]"
          data-text-required="[@institutionRequest.attrText key='marloRequestInstitution.ui.error.required' /]"
          data-text-selectoffice="[@institutionRequest.attrText key='marloRequestInstitution.ui.offices.error.none' /]"
          data-text-partners-one="[@institutionRequest.attrText key='marloRequestInstitution.ui.summary.partners.one' /]"
          data-text-partners-many="[@institutionRequest.attrText key='marloRequestInstitution.ui.summary.partners.many' /]"
          data-text-offices-one="[@institutionRequest.attrText key='marloRequestInstitution.ui.summary.offices.one' /]"
          data-text-offices-many="[@institutionRequest.attrText key='marloRequestInstitution.ui.summary.offices.many' /]">

          <header class="prq-head">
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
          </header>

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
          <section class="prq-section" aria-labelledby="prqPartnersTitle">
            <div class="prq-section__head">
              <h2 class="prq-section__title" id="prqPartnersTitle">[@s.text name="marloRequestInstitution.ui.section.partners" /]</h2>
              <span class="prq-count" data-prq-count="partners">${partnerCount?c}</span>
            </div>
            [@institutionRequest.partnersList partners=partners  canEdit=editable namespace="/marloInstitutions" defaultAction="${(crpSession)!}/marloInstitutions"/]
          </section>

          [#-- Requested office locations --]
          <section class="prq-section" aria-labelledby="prqOfficesTitle">
            <div class="prq-section__head">
              <h2 class="prq-section__title" id="prqOfficesTitle">[@s.text name="marloRequestInstitution.ui.section.offices" /]</h2>
              <span class="prq-count" data-prq-count="offices">${officeCount?c}</span>
            </div>
            [@institutionRequest.officesRequest partners=countryOfficesList  canEdit=editable namespace="/marloInstitutions" defaultAction="${(crpSession)!}/marloInstitutions"/]
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
