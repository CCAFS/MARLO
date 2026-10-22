[#ftl]

[#-- A2-2613: the form header's actions, beside the title. projectInnovations.js keeps driving them through
     #modalCounterShared, #sharedClusterModal and the .copyButton / .urlInput pair inside .generalInnovationsOptions. --]
[#macro innovationHeaderActions element]
  <span class="inf-head__actions generalInnovationsOptions">
    [#-- Shared Clusters --]
    <button type="button" class="dlf-btn dlf-btn--ghost inf-headBtn" data-toggle="modal" data-target="#sharedClusterModal">
      <svg class="dlf-icon" width="13" height="13" viewBox="0 0 16 16" fill="none" aria-hidden="true"><circle cx="4" cy="8" r="2" stroke="currentColor" stroke-width="1.5"></circle><circle cx="12" cy="3.6" r="2" stroke="currentColor" stroke-width="1.5"></circle><circle cx="12" cy="12.4" r="2" stroke="currentColor" stroke-width="1.5"></circle><path d="M5.8 7 10.2 4.6M5.8 9l4.4 2.4" stroke="currentColor" stroke-width="1.5"></path></svg>
      [@s.text name="projectInnovations.redesign.share" /]
      <span class="inf-headBtn__count" id="modalCounterShared">0</span>
    </button>

    [#-- Copy --]
    <button type="button" class="dlf-btn dlf-btn--ghost inf-headBtn copyButton">
      <svg class="dlf-icon" width="13" height="13" viewBox="0 0 16 16" fill="none" aria-hidden="true"><rect x="5" y="2.2" width="8.4" height="9.6" rx="1.6" stroke="currentColor" stroke-width="1.4"></rect><path d="M3 5v7.6c0 .9.7 1.6 1.6 1.6H10" stroke="currentColor" stroke-width="1.4" stroke-linecap="round"></path></svg>
      [@s.text name="projectInnovations.redesign.copyLink" /]
    </button>
    [#local summaryPDF = "${baseUrl}/summaries/${crpSession}/projectInnovationSummary.do?innovationID=${(element.id)!}&cycle=Reporting&year=${(actualPhase.year)!}"]
    [@customForm.input name="innovation.projectExpectedStudyInfo.link" i18nkey="study.general.link" className="form-control input-sm urlInput" value="${summaryPDF}" editable=editable display=false readOnly=true/]
  </span>

  [#-- Shared Cluster Modal --]
  <div class="modal fade sharedClusterMessage" id="sharedClusterModal" tabindex="-1" role="dialog" aria-labelledby="sharedClusterModalLabel" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
          <h4 class="modal-title" id="sharedClusterModalLabel">[@s.text name="projectInnovations.sharedProjects.title" /]</h4>
        </div>
        <div class="modal-body">
          [#-- Projects shared --]
          <h5 class="headTitle">[@s.text name="projectInnovations.sharedProjects.title" /]</h5>
          [@customForm.elementsListComponent name="innovation.sharedInnovations" elementType="project" elementList=(innovation.sharedInnovations)![] label="projectInnovations.sharedProjects"  listName="myProjects" keyFieldName="id" displayFieldName="composedName" required=false /]
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">[@s.text name="projectInnovations.sharedProjects.close" /]</button>
        </div>
      </div>
    </div>
  </div>
[/#macro]

[#-- A2-2613: Annual Report year and the PRMS mapping in one card. projectInnovations.js keeps the PRMS counter
     (.prmsContents__amount, #modalCounterPRMS, .prmsContents__no__amount) and the mapped list current. --]
[#macro innovationReportingStrip element]
  <div class="dlf-card inf-strip">
    [#-- Year --]
    <div class="inf-strip__year">
      [@customForm.select name="innovation.projectInnovationInfo.year" i18nkey="policy.year" listName="getInnovationsYears(${innovationID})" header=false required=true editable=editable /]
      <span class="dlf-hint inf-strip__hint" data-inf-year-hint data-phase-year="${(actualPhase.year?c)!}"
        data-current="[@s.text name="projectInnovations.redesign.year.current"][@s.param]${(actualPhase.name)!} ${(actualPhase.year?c)!}[/@s.param][/@s.text]"
        data-previous="[@s.text name="projectInnovations.redesign.year.previous" /]"></span>
    </div>

    [#-- PRMS --]
    <div class="inf-strip__prms">
      <img class="inf-strip__logo" src="${baseUrlCdn}/global/images/cgiar_logo_black.png" width="32" height="32" alt="CGIAR" />
      <span class="inf-strip__prmsText prmsContents">
        <span class="inf-strip__eyebrow">[@s.text name="projectInnovations.redesign.prms.title" /]</span>
        <span class="inf-strip__count prmsContents__amount" style="display: none;">[@s.text name="projectInnovations.redesign.prms.mapped" /] <span id="modalCounterPRMS">0</span> [@s.text name="projectInnovations.redesign.prms.mappedUnit" /]</span>
        <span class="inf-strip__count prmsContents__no__amount">[@s.text name="projectInnovations.redesign.prms.none" /]</span>
      </span>
      <span class="inf-strip__status"><span class="inf-strip__statusDot" aria-hidden="true"></span>[@s.text name="projectInnovations.redesign.prms.status" /]</span>
      [#if editable]
        <button type="button" class="dlf-btn inf-strip__map btnPRMSInnovations" data-toggle="modal" data-target="#prmsInnovationsModal">
          <svg class="dlf-icon" width="13" height="13" viewBox="0 0 16 16" fill="none" aria-hidden="true"><rect x="2.4" y="5" width="8.6" height="8.6" rx="1.6" stroke="currentColor" stroke-width="1.5"></rect><path d="M5.6 2.4h8v8" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"></path></svg>
          [@s.text name="projectInnovations.redesign.prms.map" /]
        </button>
      [/#if]
    </div>
  </div>

  [#-- PRMS Innovations List Modal --]
  <div class="modal fade" id="prmsInnovationsModal" tabindex="-1" role="dialog" aria-labelledby="prmsInnovationsModalLabel">
    <div class="modal-dialog modal-lg" style="width: 1100px;">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" >&times;</button>
          <h4 class="modal-title" id="prmsInnovationsModalLabel">[@s.text name="projectInnovations.prmsInnovations.title" /]</h4>
        </div>
        <div class="modal-body" style="padding: 24px; display: flex; flex-direction: column; gap: 6px;">
          [#-- Note - Green --]
          <div class="note">
            <p>[@s.text name="projectInnovations.prmsInnovations.helpText.readText" /]</p>
          </div>

          [#-- Note - Yellow - Warning No save information --]
          <label class="note note--3 warningUnsaveInformation" style="display: none;"><span class="glyphicon glyphicon-exclamation-sign"></span> <span class="contentInformation" >The mapped innovations changes are not save yet. Please save all the changes made after close the modal.</span></label>

          [@tablePRMSInnovation list=prmsInnovationList selected=element.prmsInnovations /]

          <hr style="background: #CCC; width: 100%;" />

          <div>
            <label>[@s.text name="projectInnovations.prmsInnovations.additionalNotes" /]:</label>
            [@s.text name="projectInnovations.prmsInnovations.additionalNotes.items" /]
          </div>

        </div>
      </div>
    </div>
  </div>

  [#-- Reference List of Mapped PRMS Innovations --]
  <div id="referenceMappedPRMSInnovations" style="display: none;">
  [#if element.prmsInnovations?has_content]
    [#list element.prmsInnovations as prmsInnovation]
      [@hiddenMapPRMSInnovations element=prmsInnovation name="innovation.prmsInnovations" index=prmsInnovation_index template=false editable=editable /]
    [/#list]
  [/#if]
  </div>

  [#-- Template hiddenMapPRMSInnovations --]
  <div id="hiddenMapPRMSInnovations" style="display: none;">
    [@hiddenMapPRMSInnovations element={} name="innovation.prmsInnovations" index=-1 template=true editable=editable /]
  </div>
[/#macro]

[#macro innovationGeneral element name index=-1 template=false ]
  [#local isProgressActive = action.isProgressActive() ]
  [#-- "project" / "cluster" / "team": an imported library does not see the template's sectionNoun --]
  [#local sectionNoun = (action.getText("projects.menu.status.noun"))!"project" /]
  [#-- A2-2613: the tab drawn as the design's cards over the deliverable form redesign (.dlf). Every field keeps the
       customForm markup, names and classes projectInnovations.js, fieldsValidation.js and the feedback module read. --]
  <div id="general" class="dlf-sections inf-general">
    [#-- Texts the redesign scripts write into the list controls (deliverableFormRedesign.js, innovationFormRedesign.js) --]
    <span hidden data-dlf-strings
      data-add-region="[@s.text name="project.deliverable.redesign.add.region" /]"
      data-add-country="[@s.text name="project.deliverable.redesign.add.country" /]"
      data-add-lead="[@s.text name="projectInnovations.redesign.add.lead" /]"
      data-add-external="[@s.text name="projectInnovations.redesign.add.external" /]"
      data-add-indicator="[@s.text name="projectInnovations.redesign.add.indicator" /]"
      data-of-max="[@s.text name="projectInnovations.redesign.ofMax" /]"
      data-contacts="[@s.text name="projectInnovations.redesign.contacts" /]"
      data-selected="[@s.text name="projectInnovations.redesign.selected" /]"></span>

    [#-- Definition of innovation --]
    <div class="dlf-note" role="note">
      <span class="dlf-note__icon" aria-hidden="true">i</span>
      <p class="dlf-note__text">[@s.text name="projectInnovations.generalInformation.help" /]</p>
    </div>

    [#-- ─── Basic information ─────────────────────────────────────────────── --]
    <section class="dlf-card" id="inf-basics">
      <h2 class="dlf-card__title">[@s.text name="projectInnovations.redesign.section.basics" /]</h2>

      [#-- Title --]
      <div class="dlf-field">
        [@customForm.input name="innovation.projectInnovationInfo.title" type="text" i18nkey="projectInnovations.title"  placeholder="" className="limitWords-30" help="projectInnovations.title.helpText" helpIcon=false required=true editable=editable isMainTitle=true isWidthFull=true /]
      </div>

      [#-- Short Title --]
      <div class="dlf-field">
        [@customForm.input name="innovation.projectInnovationInfo.shortTitle" type="text" i18nkey="projectInnovations.shortTitle"  placeholder="" className="limitWords-15" help="projectInnovations.shortTitle.helpText" helpIcon=false required=true editable=editable isMainTitle=true isWidthFull=true /]
      </div>

      [#-- Narrative --]
      <div class="dlf-field">
        [@customForm.textArea name="innovation.projectInnovationInfo.narrative"  i18nkey="projectInnovations.narrative"  placeholder="" className="limitWords-500" help="projectInnovations.narrative.helpText" helpIcon=false required=true editable=editable isMainTitle=true isWidthFull=true /]
      </div>

      [#-- Innovation Bundle: the question and its helper on the left, the answer as a segmented control --]
      [#local isInnovationBundle = ((element.projectInnovationInfo?has_content)&&(element.projectInnovationInfo.innovationBundle?has_content && element.projectInnovationInfo.innovationBundle?c == "true")) /]
      [#local bundleDefinition = action.getText("projectInnovations.innovationBundle.definition") /]
      <div class="inf-questionBox">
      <div class="inf-question">
        <div class="inf-question__text">
          <span class="inf-question__label">[@s.text name="projectInnovations.innovationBundle" /][@customForm.req required=editable /]
            <span class="inf-question__ask">[@s.text name="projectInnovations.innovationBundle.helpText" /]</span>
          </span>
          [#if bundleDefinition?has_content && bundleDefinition != "projectInnovations.innovationBundle.definition"]
            <span class="dlf-hint">${bundleDefinition}</span>
          [/#if]
        </div>
        <div class="inf-segmented" role="radiogroup" aria-label="[@s.text name="projectInnovations.innovationBundle" /]">
          [@customForm.radioFlat id="isInnovationBundle-determined" name="innovation.projectInnovationInfo.innovationBundle" i18nkey="projectInnovations.innovationBundle.innovationOption" value="false" checked=((element.projectInnovationInfo??)&&(element.projectInnovationInfo.innovationBundle??)&&(!isInnovationBundle)) cssClass="radioType-isInnovationBundle" cssClassLabel="radio-label" editable=editable /]
          [@customForm.radioFlat id="isInnovationBundle-undetermined" name="innovation.projectInnovationInfo.innovationBundle" i18nkey="projectInnovations.innovationBundle.bundleOption" value="true" checked=((element.projectInnovationInfo??)&&(element.projectInnovationInfo.innovationBundle??)&&(isInnovationBundle)) cssClass="radioType-isInnovationBundle" cssClassLabel="radio-label" editable=editable /]
        </div>
      </div>
      [#-- innovationFormRedesign.js shows it once Bundled innovation is chosen in this visit --]
      <div class="inf-questionBox__note" data-inf-bundle-note hidden>
        <svg class="dlf-icon" width="14" height="14" viewBox="0 0 16 16" fill="none" aria-hidden="true"><circle cx="8" cy="8" r="6.4" stroke="currentColor" stroke-width="1.4"></circle><path d="M8 7.3v3.6" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"></path><circle cx="8" cy="5.1" r=".95" fill="currentColor"></circle></svg>
        [@s.text name="projectInnovations.redesign.bundleNote" /]
      </div>
      </div>
    </section>

    [#-- ─── Classification ────────────────────────────────────────────────── --]
    <section class="dlf-card" id="inf-classification">
      <h2 class="dlf-card__title">[@s.text name="projectInnovations.redesign.section.classification" /]</h2>

      [#-- Innovation nature --]
      <div class="dlf-field">
        [@customForm.select name="innovation.projectInnovationInfo.repIndInnovationNature.id" label="" i18nkey="projectInnovations.innovationNature" listName="innovationNatureList" keyFieldName="id" displayFieldName="composedName" required=true help="projectInnovations.innovationNature.helpText" helpIcon=false className="innovationNatureSelect" editable=editable isMainTitle=true /]
        [#-- Other Innovation Nature --]
        [#local isNatureFour = (element.projectInnovationInfo.repIndInnovationNature.id == 4)!false]
        <div class="inf-other natureFourBlock" style="display:${isNatureFour?string('block','none')}">
          [@customForm.input name="innovation.projectInnovationInfo.otherInnovationNature"  type="text" i18nkey="projectInnovations.otherInnovationNature" helpIcon=false required=!isProgressActive editable=editable  /]
        </div>
      </div>

      [#-- Innovation Type --]
      <div class="dlf-field dlf-divided">
        [@customForm.select name="innovation.projectInnovationInfo.repIndInnovationType.id" label="" i18nkey="projectInnovations.innovationType" listName="innovationTypeList" keyFieldName="id" displayFieldName="name" required=true help="projectInnovations.innovationType.helpText" helpIcon=false className="innovationTypeSelect" editable=editable isMainTitle=true isWidthFull=true /]

        [#-- Informative text - Compatibility with PRMS (projectInnovations.js shows the line of the selected type) --]
        [#local innovationTypeValue = (element.projectInnovationInfo?has_content && element.projectInnovationInfo.repIndInnovationType?has_content)?then(element.projectInnovationInfo.repIndInnovationType.id, -1) /]
        [#local programName = action.getText("projectInnovations.prmsEquivalent.program") /]
        <div class="prmsEquivalentBlock inf-prmsLine">
          [#list innovationTypeList as type]
            [#local isVisible = (type.id == innovationTypeValue) /]
            <p style="display:${isVisible?string('flex','none')}">
              <svg class="dlf-icon" width="14" height="14" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M2.6 5.4h9.6M9.6 2.8l2.6 2.6-2.6 2.6M13.4 10.6H3.8M6.4 8l-2.6 2.6 2.6 2.6" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round"></path></svg>
              <span class="prmsEquivalentText" data-id="${type.id}">
                [#if (type.prmsNameEquivalent?has_content)]
                  [@s.text name="projectInnovations.prmsEquivalent.equals"][@s.param]<b>${type.name}</b>[/@s.param][@s.param]<b>${programName}</b>[/@s.param][@s.param]<b>${type.prmsNameEquivalent}</b>[/@s.param][/@s.text]
                [#else]
                  [@s.text name="projectInnovations.prmsEquivalent.none"][@s.param]<b>${type.name}</b>[/@s.param][@s.param]<b>${programName}</b>[/@s.param][/@s.text]
                [/#if]
              </span>
            </p>
          [/#list]
        </div>

        [#-- Other Innovation Type --]
        [#local isTypeSix = (element.projectInnovationInfo.repIndInnovationType.id == 6)!false]
        <div class="inf-other typeSixBlock" style="display:${isTypeSix?string('block','none')}">
          [@customForm.input name="innovation.projectInnovationInfo.otherInnovationType"  type="text" i18nkey="projectInnovations.otherInnovation" helpIcon=false required=!isProgressActive editable=editable  /]
        </div>
      </div>

      [#-- Value chain(s), behind innovation_value_chains_active (on for AICCRA-FSRP IV only, see its migration) --]
      [#if (action.isValueChainsActive())!false]
      [#-- Value chain(s) (A2-2613): one checkbox per catalog entry, posted at its catalog position. "Not value-chain
           specific" sits after an "or" and is exclusive of the rest (innovationFormRedesign.js); "Other" asks for the
           value chain in a text. listname lets fieldsValidation.js mark the group when it is missing. --]
      [#local selectedValueChainIds = [] /]
      [#list (element.valueChains)![] as elementValueChain]
        [#if (elementValueChain.valueChain.id)??][#local selectedValueChainIds = selectedValueChainIds + [elementValueChain.valueChain.id] /][/#if]
      [/#list]
      [#local hasOtherValueChain = false /]
      [#list (valueChainList)![] as valueChain]
        [#if valueChain.other && selectedValueChainIds?seq_contains(valueChain.id)][#local hasOtherValueChain = true /][/#if]
      [/#list]
      <div class="dlf-field dlf-divided inf-vc" listname="innovation.valueChains" data-inf-value-chains
        data-note="[@s.text name="projectInnovations.valueChains.exclusiveNote" /]"
        data-unavailable="[@s.text name="projectInnovations.valueChains.unavailable" /]"
        data-clear-first="[@s.text name="projectInnovations.valueChains.clearFirst" /]"
        data-selected="[@s.text name="projectInnovations.redesign.selected" /]">
        <div class="inf-vc__head">
          <label class="inf-vc__label">[@s.text name="projectInnovations.valueChains" /][@customForm.req required=editable /]<span class="inf-count" data-inf-vc-count></span></label>
          <div class="commentNumberContainer">
            <div class="numberOfCommentsBubble"><p></p></div>
            <img src="${baseUrlCdn}/global/images/comment.png" class="qaComment" name="innovation.valueChains" fieldID="" description="">
          </div>
        </div>
        <p class="dlf-hint inf-vc__help">[@s.text name="projectInnovations.valueChains.help" /]</p>
        [#if editable]
          <div class="inf-vc__group" role="group" aria-label="[@s.text name="projectInnovations.valueChains" /]">
            [#list (valueChainList)![] as valueChain]
              [#if !valueChain.notSpecific][@valueChainChip valueChain=valueChain index=valueChain_index checked=selectedValueChainIds?seq_contains(valueChain.id) /][/#if]
            [/#list]
            <span class="inf-vc__or" aria-hidden="true">[@s.text name="projectInnovations.valueChains.or" /]</span>
            [#list (valueChainList)![] as valueChain]
              [#if valueChain.notSpecific][@valueChainChip valueChain=valueChain index=valueChain_index checked=selectedValueChainIds?seq_contains(valueChain.id) /][/#if]
            [/#list]
          </div>
          <p class="inf-vc__note" data-inf-vc-note hidden></p>
          [#-- innovationFormRedesign.js shows it when fieldsValidation.js marked the value chains as missing --]
          <p class="inf-vc__error" role="alert" data-inf-vc-error hidden>
            <svg class="dlf-icon" width="13" height="13" viewBox="0 0 16 16" fill="none" aria-hidden="true"><circle cx="8" cy="8" r="6.4" fill="currentColor"></circle><path d="M8 4.6v4.2" stroke="#fff" stroke-width="1.7" stroke-linecap="round"></path><circle cx="8" cy="11.1" r="1" fill="#fff"></circle></svg>
            [@s.text name="projectInnovations.valueChains.missing" /]
          </p>
        [#else]
          <p class="inf-vc__readOnly">
            [#list ((valueChainList)![])?filter(v -> selectedValueChainIds?seq_contains(v.id)) as valueChain]<span class="inf-vc__value">${valueChain.name}</span>[#else]<span class="inf-vc__empty">[@s.text name="projectInnovations.valueChains.none" /]</span>[/#list]
          </p>
        [/#if]
        <div class="inf-other" data-inf-vc-other style="display:${hasOtherValueChain?string('block','none')}">
          [@customForm.input name="innovation.projectInnovationInfo.otherValueChain" type="text" i18nkey="projectInnovations.otherValueChain" placeholder="projectInnovations.otherValueChain.placeholder" className="limitWords-10" helpIcon=false required=true editable=editable /]
        </div>
      </div>
      [/#if]

      [#-- Modal with Table Comparative between innovation type and innovation type in PRMS --]
      <div class="modal fade" id="innovationTypeComparisonModal" tabindex="-1" role="dialog" aria-labelledby="innovationTypeComparisonModalLabel" aria-hidden="true">
        <div class="modal-dialog" role="document" style="width: 1000px;">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title" id="innovationTypeComparisonModalLabel">[@s.text name="projectInnovations.innovationTypeModal.title" /]</h5>
              <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            <div class="modal-body">
              [#-- Note - Green --]
              <div class="note--2">
                <p>[@s.text name="projectInnovations.innovationTypeModal.note" /]</p>
              </div>
              <table class="table">
                <thead>
                  <tr>
                    <th>[@s.text name="projectInnovations.innovationTypeModal.columnInnovation" /]</th>
                    <th>[@s.text name="projectInnovations.innovationTypeModal.columnPRMS" /]</th>
                  </tr>
                </thead>
                <tbody>
                  [#list innovationTypeList as type]
                  <tr>
                    <td>${type.name}</td>
                    <td>${type.prmsNameEquivalent!'No equivalent'}</td>
                  </tr>
                  [/#list]
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </section>

    [#-- ─── Geographic scope ──────────────────────────────────────────────── --]
    [#local geographicScopeElement = (element.geographicScopes)![] ]
    [#local isRegional =      findElementID(geographicScopeElement,  action.reportingIndGeographicScopeRegional) /]
    [#local isNational =      findElementID(geographicScopeElement,  action.reportingIndGeographicScopeNational) /]
    [#local hasSpecifiedOutputCountries = ((element.projectInnovationInfo??) && (element.projectInnovationInfo.hasSpecifiedOutputCountries??))?then(element.projectInnovationInfo.hasSpecifiedOutputCountries,"") /]
    <section class="dlf-card geographicScopeBlock" id="dlf-geographicScope">
      <h2 class="dlf-card__title">[@s.text name="projectInnovations.geographicScope" /][@customForm.req required=(editable && reportingActive) /]</h2>
      <span class="inf-fieldLabel">[@s.text name="projectInnovations.geographicImpact" /]</span>

      <div class="geographicScopeInput">
        [#local existGeographicScope = element.geographicScopes?? && element.geographicScopes[0]??  /]
        [#if existGeographicScope]
          [#local geographicScopeID = (element.geographicScopes[0].id!"")?string /]
          [@customForm.input name="innovation.geographicScopes[0].id" editable=false display=false value=geographicScopeID /]
        [/#if]

        [#-- Geographic Scope: one radio card per scope --]
        <div class="inf-radioCards" role="radiogroup" aria-label="[@s.text name="projectInnovations.geographicScope" /]">
          [#list geographicScopeList as geoScope]
            [#local isChecked = ((existGeographicScope) && (element.geographicScopes[0].repIndGeographicScope??) && (geoScope.id == element.geographicScopes[0].repIndGeographicScope.id)) /]
            [@customForm.radioFlat id="geoScope-${geoScope.id}" name="innovation.geographicScopes[0].repIndGeographicScope.id" label="${geoScope.name}" value="${geoScope.id}" checked=isChecked cssClass="radioType-geographicScopes" cssClassLabel="radio-label" editable=editable disabled=!editable /]
          [/#list]
        </div>

        <div class="geographicScopeMessage" style="display:${(isNational)?string('block','none')}">
          [@customForm.labelText name="projectInnovations.geographicScopeTopic" text="projectInnovations.geographicScopeTopic" /]
        </div>

        [#-- National scope --]
        <div class="dlf-field nationalBlock" style="display:${(isNational)?string('block','none')}">
          [@customForm.select name="innovation.countriesIds" label="" i18nkey="projectInnovations.country" listName="countries" keyFieldName="isoAlpha2"  displayFieldName="name" value="innovation.countriesIds" multiple=true required=!isProgressActive className="countriesSelect" disabled=!editable /]
        </div>

        [#-- Regional scope --]
        <div class="dlf-field regionalBlock" style="display:${(isRegional)?string('block','none')}">
          [#local geographicCountrySpecific = (element.projectInnovationInfo?? && element.projectInnovationInfo.hasSpecifiedOutputCountries??)?then(element.projectInnovationInfo.hasSpecifiedOutputCountries, false) /]
          [@customForm.elementsListComponent name="innovation.regions" elementType="locElement" elementList=innovation.regions label="projectInnovations.region"  listName="regions" keyFieldName="id" displayFieldName="composedName" required=true /]

          <div class="inf-question inf-question--inline">
            <div class="inf-question__text">
              <span class="inf-question__label">[@s.text name="projectInnovations.geographicCountrySpecific" /][@customForm.req required=editable /]</span>
            </div>
            <div class="inf-segmented" role="radiogroup" aria-label="[@s.text name="projectInnovations.geographicCountrySpecific" /]">
              [@customForm.radioFlat id="hasSpecifiedOutputCountries-yes" name="innovation.projectInnovationInfo.hasSpecifiedOutputCountries" i18nkey="projectInnovations.hasSpecifiedOutputCountries.yes" value="true" checked=((element.projectInnovationInfo??) && (element.projectInnovationInfo.hasSpecifiedOutputCountries??) && (hasSpecifiedOutputCountries))  cssClass="radioType-hasSpecifiedOutputCountries" cssClassLabel="radio-label-yes" editable=editable /]
              [@customForm.radioFlat id="hasSpecifiedOutputCountries-no" name="innovation.projectInnovationInfo.hasSpecifiedOutputCountries" i18nkey="projectInnovations.hasSpecifiedOutputCountries.no" value="false" checked=((element.projectInnovationInfo??) && (element.projectInnovationInfo.hasSpecifiedOutputCountries??) && (!hasSpecifiedOutputCountries)) cssClass="radioType-hasSpecifiedOutputCountries" cssClassLabel="radio-label-no" editable=editable /]
            </div>
          </div>
          <div class="block-hasSpecifiedOutputCountries" style="display:${(geographicCountrySpecific)?string('block','none')}">
            [@customForm.labelText name="projectInnovations.geographicScopeTopic" text="projectInnovations.geographicScopeTopic" /]
            [@customForm.select name="innovation.countriesIds" label="" i18nkey="projectInnovations.countries" listName="countries" keyFieldName="isoAlpha2"  displayFieldName="name" value="innovation.countriesIds" multiple=true required=!isProgressActive className="countriesSelect" disabled=!editable /]
          </div>
        </div>
      </div>
    </section>

    [#-- ─── Partners & contributors ───────────────────────────────────────── --]
    <section class="dlf-card contributionsCenters" id="inf-partners">
      <h2 class="dlf-card__title">[@s.text name="projectInnovations.contributingCenters.title" /]</h2>
      <div class="dlf-note dlf-note--compact" role="note">
        <span class="dlf-note__icon" aria-hidden="true">i</span>
        <p class="dlf-note__text">[@s.text name="study.generalInformation.ppapartner.note"][@s.param]<a href="[@s.url namespace="/projects" action='${crpSession}/partners'][@s.param name='projectID']${(projectID)!}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" target="_blank">[@s.text name="projectInnovations.redesign.partners.link" /]</a>[/@][/@]</p>
      </div>

      [#-- Lead Centers / Implementing partners --]
      <div class="dlf-field inf-leads">
        [@customForm.elementsListComponent name="innovation.centers" i18nkey="innovation.centers" elementType="institution" elementList=innovation.centers label="projectInnovations.contributingCenters"  listName="centers" keyFieldName="id" displayFieldName="composedName" required=!isProgressActive /]
      </div>

      [#-- Define organizationRoles list for checkbox options --]
      [#local organizationRoles = [
        {"name": "Scaling"},
        {"name": "Demand"},
        {"name": "Innovation"},
        {"name": "Other"}
      ] /]

      [#-- External Contributing partners, sortable, with their roles --]
      <div class="dlf-field dlf-divided top-five-contributing inf-externals" data-inf-sort-hint="[@s.text name="projectInnovations.redesign.externals.sortHint" /]">
        [@customForm.elementsListComponent name="innovation.contributingOrganizations" i18nkey="innovation.contributingOrganizations" help="projectInnovations.contributingOrganizations.help" helpIcon=false isNote=false maxLimit=5 elementType="institution" elementList=innovation.contributingOrganizations label="projectInnovations.contributingOrganizations"  listName="contributingPartnerList" keyFieldName="id" displayFieldName="nameWithCountry" hasInnerCheckbox=true argsInnerCheckbox={"listCheckbox": organizationRoles, "subtitleElement": "Organization", "subtitleCheckbox": "Role"} orderById=true  /]
      </div>
    </section>

    [#-- ─── Contribution ──────────────────────────────────────────────────── --]
    <section class="dlf-card" id="inf-contribution">
      <h2 class="dlf-card__title">[@s.text name="projectInnovations.redesign.section.contribution" /]</h2>

      [#-- Link to Performance Indicators: innovationFormRedesign.js draws the code pills and the add label --]
      <div class="dlf-field inf-indicators">
        [@customForm.elementsListComponent name="innovation.crpOutcomes" elementType="crpOutcome" elementList=(innovation.crpOutcomes)![] label="innovation.outcomes" helpIcon=false listName="crpOutcomes" keyFieldName="id" displayFieldName="composedName" required=!isProgressActive isMainTitle=true /]
        <a class="dlf-link dlf-link--end" href="[@s.url namespace=namespace action="${crpSession}/contributionsCrpList"][@s.param name='projectID']${projectID?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" target="_blank">
          [@s.text name="project.deliverable.redesign.indicators.notListed"][@s.param]${sectionNoun}[/@s.param][/@s.text]
        </a>
      </div>

      [#-- Link to other PRMS innovation reported --]
      [#if action.hasSpecificities('innovation_link_to_other_reported_in_prms_active')]
        <div class="dlf-field dlf-divided">
          [@customForm.input name="innovation.projectInnovationInfo.otherPrmsInnovationReported" type="text" i18nkey="projectInnovations.otherPrmsInnovationReported"  placeholder="" className="limitWords-30" help="projectInnovations.otherPrmsInnovationReported.helpText" helpIcon=false required=false editable=editable isMainTitle=true isWidthFull=true /]
        </div>
      [/#if]

      [#-- Contributing cNE/Platforms --]
      [#if !action.isAiccra()]
        <div class="dlf-field dlf-divided">
          [@customForm.elementsListComponent name="innovation.crps" elementType="globalUnit" elementList=innovation.crps label="projectInnovations.contributing"  listName="crpList" keyFieldName="id" displayFieldName="composedName" required=false /]
        </div>
      [/#if]

      [#if !action.isAiccra()]
        [#-- Sub IDOs (maxLimit=3 -Requested for AR2019) --]
        <div class="dlf-field dlf-divided">
          [@customForm.elementsListComponent name="innovation.subIdos" elementType="srfSubIdo" elementList=(innovation.subIdos)![] label="innovation.subIDOs" helpIcon=false listName="subIdos" maxLimit=3 keyFieldName="id" displayFieldName="description" required=!isProgressActive hasPrimary=true/]
        </div>
      [/#if]
    </section>

    [#-- ─── Responsible institution (Contact person) ───────────────────────── --]
    [#-- #dlf-partners: the deliverable form's partner box and person cards (deliverableForm.css, deliverableFormRedesign.js) --]
    <section class="dlf-card stageProcessOne" id="dlf-partners">
      <h2 class="dlf-card__title">[@s.text name="projectInnovations.communications.contacts" /]</h2>
      <div id="addPartnerText" class="dlf-hint inf-card__lead">
        [@s.text name="projectInnovations.communications.contacts.help" /]
        <a class="popup" href="[@s.url action='${crpSession}/partnerSave' namespace="/projects"][@s.param name='projectID']${(projectID)!}[/@s.param][@s.param name='context'](${(actionName)!}: ID-${(innovation.id)!})[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
          [@s.text name="projectInnovations.communications.contacts.help2" /]
        </a>
      </div>
      <div class="dlf-field">
        <label for="">[@customForm.text name="projectInnovations.communications.indicateResponsablePartner" readText=!editable/]</label>
        <div class="projectInnovationsPartners dlf-partnerBox">
          [@deliverableMacros.deliverablePartnerMacro element=(element.partnerships[0])!{} name="innovation.partnerships" index=0 defaultType=2 /]
        </div>
      </div>
    </section>

  </div>
[/#macro]

[#-- A2-2613: the Alliance Alignment tab left the form, but the save replaces the innovation info and the Alliance
     levers with what the form posts. Its stored answers are posted back unchanged so a save keeps them as they are
     (the tab's own macro below forced lever 3 on every save). --]
[#macro innovationAllianceStored element]
  [#if editable]
    [#list (element.allianceLevers)![] as elementLever]
      [#if (elementLever.allianceLever.id)??]
        <input type="hidden" name="innovation.allianceLevers[${elementLever_index}].id" value="${(elementLever.id?c)!}" />
        <input type="hidden" name="innovation.allianceLevers[${elementLever_index}].allianceLever.id" value="${elementLever.allianceLever.id?c}" />
      [/#if]
    [/#list]
    [#local info = (element.projectInnovationInfo)! /]
    [#if (info.otherAllianceLever)??]<input type="hidden" name="innovation.projectInnovationInfo.otherAllianceLever" value="${info.otherAllianceLever}" />[/#if]
    [#if (info.intellectualPropertyInstitution.id)??]<input type="hidden" name="innovation.projectInnovationInfo.intellectualPropertyInstitution.id" value="${info.intellectualPropertyInstitution.id?c}" />[/#if]
    [#if (info.otherIntellectualProperty)??]<input type="hidden" name="innovation.projectInnovationInfo.otherIntellectualProperty" value="${info.otherIntellectualProperty}" />[/#if]
    [#if (info.hasLegalRestrictions)??]<input type="hidden" name="innovation.projectInnovationInfo.hasLegalRestrictions" value="${info.hasLegalRestrictions?c}" />[/#if]
    [#if (info.hasAssetPotential)??]<input type="hidden" name="innovation.projectInnovationInfo.hasAssetPotential" value="${info.hasAssetPotential?c}" />[/#if]
    [#if (info.hasFurtherDevelopment)??]<input type="hidden" name="innovation.projectInnovationInfo.hasFurtherDevelopment" value="${info.hasFurtherDevelopment?c}" />[/#if]
  [/#if]
[/#macro]

[#macro innovationAlliance element name index=-1 template=false]
  
  [#local customName = "${name}"/]

  <div id="alliance" class="borderBox clearfix">
    [#-- Alliance Research Theme --]
    <div class="form-group radioToCheckbox ">
      [@customForm.labelText name="innovation.allianceLevers" text="projectInnovations.alliance.researchTheme" required=true isMainTitle=true /]
      <label>[@s.text name="projectInnovations.alliance.researchTheme.subtitle" /]</label>
      [#if allianceLeverList?has_content]
          [#list allianceLeverList as lever]
            [#if lever.description?has_content]
              [#local customLabel = "${lever.name} : ${lever.description}" /]
            [#else]
              [#local customLabel = "${lever.name}" /]
            [/#if]

            [#local isOther = (lever.name == "Other")!false /]

            [#local isChecked = false /]

            <div class="containerRadioToCheckbox ${isOther?then('containerRadioToCheckbox--other','')}">
              [#if element.allianceLevers?has_content]
                [#list element.allianceLevers as elementLever]
                  [#if (elementLever.allianceLever.id??) && (elementLever.allianceLever.id == lever.id)]
                    <div class="form-group hiddenIdReference">
                        [@customForm.input name="${customName}.allianceLevers[${elementLever_index}].id" editable=false display=false value="${elementLever.id!-1}" /]
                    </div>
                    [#local isChecked = true /]
                    [#break /]
                  [/#if]

                [/#list]
              [/#if]
 
              [@customForm.checkBoxFlat id="lever-${lever.id}" name="${customName}.allianceLevers[${lever_index}].allianceLever.id" label="${customLabel}" value="${lever.id}" checked=((isChecked)||(lever.id == 3)) editable=editable disabled=(lever.id == 3) /]
              [#if isOther]
                <div class="form-group inputOther">
                  [@customForm.input name="${customName}.projectInnovationInfo.otherAllianceLever" placeholder="Other" editable=editable showTitle=false /]
                </div>
              [/#if]
            </div>
          [/#list]
        [#else]
        <p>No information available</p>
      [/#if]
    </div>
    [#-- Intellectual property rights --]
    <div class="form-group">
      <label class="label--2" style="width:100%">[@s.text name="projectInnovations.alliance.intellectualProperty" /]:</label>
      <label class="note--2">
        <p>[@s.text name="projectInnovations.alliance.intellectualProperty.subtitle" /]</p>
      </label>
      [#-- Intellectual property rights - Owner --]
      <div class="form-group">
        [@customForm.select name="${customName}.projectInnovationInfo.intellectualPropertyInstitution.id" i18nkey="projectInnovations.alliance.intellectualProperty.description" listName="intellectualInstitutionsList" keyFieldName="id" className="innovationPropertyRightsSelect" displayFieldName="customName" required=false editable=editable /]
        [#local otherIntellectualProperty = (element.projectInnovationInfo.intellectualPropertyInstitution.id == 4)!false  /]
        <div class="col-md-12 margin-top-10">
          <div class="form-group otherIntellectualProperty" style="display: ${otherIntellectualProperty?then('block','none')}">
            [@customForm.input name="${customName}.projectInnovationInfo.otherIntellectualProperty" type="text" i18nkey="projectInnovations.alliance.intellectualProperty.other" helpIcon=false required=false editable=editable /]
          </div>
        </div>
        
      </div>
      [#-- Intellectual property rights - Legal Restrictions --]
      <div class="form-group">
        <div class="col-md-12">
          <label>
            [@s.text name="projectInnovations.alliance.intellectualProperty.legalRestrictions" /]
          </label> 
        </div>
        [#local innovationIntellectualLegalRestrictions = "innovationIntellectualLegalRestrictions" /]
        [#local showLegalRestrictions = (innovation.projectInnovationInfo.hasLegalRestrictions)! /]    

        <div class="col-md-1">
          [@customForm.radioFlat id="${innovationIntellectualLegalRestrictions}-yes" name="${customName}.projectInnovationInfo.hasLegalRestrictions" label="Yes" value="true" checked=((innovation.projectInnovationInfo??)&&(innovation.projectInnovationInfo.hasLegalRestrictions??) &&(showLegalRestrictions)) cssClass="radioType-${innovationIntellectualLegalRestrictions}" cssClassLabel="radio-label-yes" editable=editable /]
        </div>
        <div class="col-md-1">
          [@customForm.radioFlat id="${innovationIntellectualLegalRestrictions}-no" name="${customName}.projectInnovationInfo.hasLegalRestrictions" label="No" value="false" checked=((innovation.projectInnovationInfo??)&&(innovation.projectInnovationInfo.hasLegalRestrictions??) &&(!showLegalRestrictions)) cssClass="radioType-${innovationIntellectualLegalRestrictions}" cssClassLabel="radio-label-no" editable=editable /]
        </div>
      </div>
      [#-- Intellectual property rights - Commercialization --]
      <div class="form-group">
        <div class="col-md-12">
          <label>
            [@s.text name="projectInnovations.alliance.intellectualProperty.commercialization" /]
          </label> 
        </div>
        [#local innovationIntellectualCommercialization = "innovationIntellectualCommercialization" /]
        [#local showCommercialization = (innovation.projectInnovationInfo.hasAssetPotential)! /]   

        <div class="col-md-1">
          [@customForm.radioFlat id="${innovationIntellectualCommercialization}-yes" name="${customName}.projectInnovationInfo.hasAssetPotential" label="Yes" value="true" checked=((innovation.projectInnovationInfo??)&&(innovation.projectInnovationInfo.hasAssetPotential??) && (showCommercialization)) cssClass="radioType-${innovationIntellectualCommercialization}" cssClassLabel="radio-label-yes" editable=editable /]
        </div>
        <div class="col-md-1">
          [@customForm.radioFlat id="${innovationIntellectualCommercialization}-no" name="${customName}.projectInnovationInfo.hasAssetPotential" label="No" value="false" checked=((innovation.projectInnovationInfo??)&&(innovation.projectInnovationInfo.hasAssetPotential??) && (!showCommercialization))  cssClass="radioType-${innovationIntellectualCommercialization}" cssClassLabel="radio-label-no" editable=editable /]
        </div>
      </div>
      [#-- Intellectual property rights - Further Development --]
      <div class="form-group">
        <div class="col-md-12">
          <label>
            [@s.text name="projectInnovations.alliance.intellectualProperty.furtherDevelopment" /]
          </label> 
        </div>
        [#local innovationIntellectualFurtherDevelopment = "innovationIntellectualFurtherDevelopment" /]
        [#local showFurtherDevelopment = (innovation.projectInnovationInfo.hasFurtherDevelopment)! /]

        <div class="col-md-1">
          [@customForm.radioFlat id="${innovationIntellectualFurtherDevelopment}-yes" name="${customName}.projectInnovationInfo.hasFurtherDevelopment" label="Yes" value="true" checked=((innovation.projectInnovationInfo??)&&(innovation.projectInnovationInfo.hasFurtherDevelopment??)&&(showFurtherDevelopment)) cssClass="radioType-${innovationIntellectualFurtherDevelopment}" cssClassLabel="radio-label-yes" editable=editable /]
        </div>
        <div class="col-md-1">
          [@customForm.radioFlat id="${innovationIntellectualFurtherDevelopment}-no" name="${customName}.projectInnovationInfo.hasFurtherDevelopment" label="No" value="false" checked=((innovation.projectInnovationInfo??)&&(innovation.projectInnovationInfo.hasFurtherDevelopment??)&&(!showFurtherDevelopment)) cssClass="radioType-${innovationIntellectualFurtherDevelopment}" cssClassLabel="radio-label-no" editable=editable /]
        </div>
      </div>
      
    </div>
  </div>
[/#macro]

[#macro innovationOneCGIAR element name index=-1 template=false]
  [#local customName = "${name}"/]

  [#local hasContributionToCGIAR = (element.projectInnovationInfo.hasCgiarContribution)!false ]

  <div id="oneCGIAR" class="dlf-sections inf-cross">

    [#-- reflect a contribution --]
    [#-- 
    <div class="form-group">
      <label class="label--2">[@s.text name="projectInnovations.oneCGIARAligment.contributionToCGIAR" /]:[@customForm.req required=(editable) /]</label>
      <div class="form-group col-md-12">
      
        <div class="col-md-1">
          [@customForm.radioFlat id="optionOneCGIAR-Yes" name="${customName}.projectInnovationInfo.hasCgiarContribution" i18nkey="projectInnovations.oneCGIARAligment.contributionToCGIARYes" value="true" checked=(((element.projectInnovationInfo.hasCgiarContribution??) && (hasContributionToCGIAR))!false) cssClass="radioType-contributionToCGIAR" cssClassLabel="font-normal" editable=editable /]
        </div>
        <div class="col-md-1">
          [@customForm.radioFlat id="optionOneCGIAR-No" name="${customName}.projectInnovationInfo.hasCgiarContribution" i18nkey="projectInnovations.oneCGIARAligment.contributionToCGIARNo" value="false" checked=(((element.projectInnovationInfo.hasCgiarContribution??) && (!hasContributionToCGIAR))!false) cssClass="radioType-contributionToCGIAR" cssClassLabel="font-normal" editable=editable /]
        </div>
      </div>
    </div>


    Innovation importance
    <div class="form-group linkToImpactAreas" style="display:${(((hasContributionToCGIAR?c) == 'true')&& (element.projectInnovationInfo.hasCgiarContribution??))?string('block','none')};" >

      Impact Areas
      <div class="form-group  col-md-12">
        <label class="label--2 col-md-12">[@s.text name="projectInnovations.oneCGIARAligment.impactAreas" /]:[@customForm.req required=true /]</label>
        <label>[@s.text name="projectInnovations.oneCGIARAligment.impactAreas.subtitle" /]</label>
        [@customForm.elementsListComponent name="${customName}.impactAreas" elementType="impactArea" elementList=(element.impactAreas)![] helpIcon=false listName="impactAreaList" keyFieldName="id" displayFieldName="name" required=false showTitle=false /]
      </div>
    </div>

    Reason not provided
    <div class="form-group contributionToCGIARComment col-md-12" style="display:${((element.projectInnovationInfo??)&&((hasContributionToCGIAR?c) == 'false') && (element.projectInnovationInfo.hasCgiarContribution??))?string('block','none')};">
      [@customForm.textArea name="${customName}.projectInnovationInfo.reasonNotCgiarContribution" i18nkey="projectInnovations.oneCGIARAligment.contributionToCGIAR.reasonToNoProvided"  helpIcon=false className="limitWords-200" required=(editable) editable=editable /]
    </div>
    --]

    [#-- A2-2613: the tab drawn as the design's cards (.dlf). The SDG list component and the score radios keep the names
         and classes projectInnovations.js, fieldsValidation.js, the validator and the feedback module read. --]
    [#local evidenceTab = action.getText("projectInnovations.tab.innovationSharing") /]

    [#-- ─── Contribution to SDG targets ────────────────────────────────────── --]
    <section class="dlf-card" id="inf-sdgs">
      [#-- projectInnovations.js draws each goal with its official icon (global/images/sdg/svg/goal-NN.svg) --]
      <div class="dlf-field inf-sdgs" data-inf-sdgs data-icons="${baseUrlCdn}/global/images/sdg/svg/"
        data-add-label="[@s.text name="projectInnovations.redesign.add.sdg" /]"
        data-selected="[@s.text name="projectInnovations.redesign.selected" /]">
        [@customForm.elementsListComponent name="${customName}.sdgs" elementType="sdg" elementList=(element.sdgs)![] label="projectInnovations.oneCGIARAligment.sdgTargets" help="projectInnovations.oneCGIARAligment.sdgTargets.subtitle" helpIcon=false listName="sdgList" keyFieldName="id" displayFieldName="shortName" required=true isMainTitle=true /]
      </div>
    </section>

    [#-- ─── Impact Area scores ─────────────────────────────────────────────── --]
    [#-- The warnings and the evidence status are hints only (innovationFormRedesign.js); the validator is unchanged --]
    <section class="dlf-card inf-ia" id="inf-ia" data-inf-ia
      data-warn-many="[@s.text name="projectInnovations.redesign.ia.warnMany" /]"
      data-warn-zero="[@s.text name="projectInnovations.redesign.ia.warnZero" /]"
      data-evidence-added="[@s.text name="projectInnovations.redesign.ia.evidenceAdded" /]"
      data-evidence-pending="[@s.text name="projectInnovations.redesign.ia.evidencePending" /]">
      <h2 class="dlf-card__title">[@s.text name="projectInnovations.oneCGIARAligment.impactAreasScore" /]</h2>

      <div class="dlf-note inf-iaGuide" role="note">
        <span class="dlf-note__icon" aria-hidden="true">i</span>
        <div class="inf-iaGuide__text">
          <p class="dlf-note__text">[@s.text name="projectInnovations.redesign.ia.intro" /]</p>
          <div class="inf-iaGuide__body" id="inf-ia-guide" hidden>[@s.text name="projectInnovations.redesign.ia.guide" /]</div>
        </div>
        <button type="button" class="inf-iaGuide__toggle" aria-expanded="false" aria-controls="inf-ia-guide" data-inf-ia-guide
          data-show="[@s.text name="projectInnovations.redesign.ia.guide.show" /]"
          data-hide="[@s.text name="projectInnovations.redesign.ia.guide.hide" /]">[@s.text name="projectInnovations.redesign.ia.guide.show" /]</button>
      </div>

      [#if editable]
        <p class="inf-iaWarn" role="status" data-inf-ia-warn hidden></p>
      [/#if]

      [@impactScoreRadioGroup fieldName="genderScore" fieldLabel="projectInnovations.oneCGIARAligment.genderScore" fieldValue=((element.projectInnovationInfo.genderScore)!{}) evidenceTag="gender" evidenceTab=evidenceTab editable=editable /]
      [@impactScoreRadioGroup fieldName="climateChangeScore" fieldLabel="projectInnovations.oneCGIARAligment.climateChangeScore" fieldValue=((element.projectInnovationInfo.climateChangeScore)!{}) evidenceTag="climateChange" evidenceTab=evidenceTab editable=editable /]
      [@impactScoreRadioGroup fieldName="foodSecurityScore" fieldLabel="projectInnovations.oneCGIARAligment.nutritionScore" fieldValue=((element.projectInnovationInfo.foodSecurityScore)!{}) evidenceTag="nutrition" evidenceTab=evidenceTab editable=editable /]
      [@impactScoreRadioGroup fieldName="environmentalScore" fieldLabel="projectInnovations.oneCGIARAligment.environmentScore" fieldValue=((element.projectInnovationInfo.environmentalScore)!{}) evidenceTag="environmental" evidenceTab=evidenceTab editable=editable /]
      [@impactScoreRadioGroup fieldName="povertyScore" fieldLabel="projectInnovations.oneCGIARAligment.povertyScore" fieldValue=((element.projectInnovationInfo.povertyScore)!{}) evidenceTag="poverty" evidenceTab=evidenceTab editable=editable /]
    </section>

  </div>
[/#macro]

[#-- DEPRECATED - Not being used - 30/07/2025 --]
[#macro innovationReadiness element name index=-1 template=false]
  <div id="readiness" class="borderBox clearfix">
    <div class="form-group">

      [#-- Innovation importance --]
      <div class="form-group col-md-12">
        [@customForm.textArea name="innovation.projectInnovationInfo.innovationImportance" i18nkey="projectInnovations.oneCGIARAligment.innovationImportance" help="projectInnovations.oneCGIARAligment.innovationImportance.help" helpIcon=false className="limitWords-200" required=false editable=editable isNote=true /]
      </div>

      [#-- Innovation Readiness reason --]
      <div class="form-group">
        [@customForm.textArea name="${name}.projectInnovationInfo.readinessReason" i18nkey="projectInnovations.readiness.reason" help="projectInnovations.readiness.reason.help" helpIcon=false className="limitWords-80" required=true isNote=true showTitle=true isMainTitle=true editable=editable isWidthFull=true /]
      </div>
    </div>
  </div>
[/#macro]

[#macro innovationBundleComposition element name index=-1 template=false]
  <div id="bundleComposition" class="borderBox clearfix">
    [#-- A2-2613: co-developing institutions (IRI 1.5), behind innovation_co_developing_institutions_active --]
    [#if (action.isCoDevelopersActive())!false]
      [@innovationCoDevelopers element=element /]
    [/#if]
    <div class="form-group">

      [#-- Select an innovation --]
      [#-- Table with all innovations and a selector list --]
      <div class="form-group">
        <div class="form-group">
            <div class="col-md-12" style="display: flex; align-items: center; gap: 15px;">
            <div style="flex: 1;">
              <label for="innovation.bundles" class="label--2">[@s.text name="projectInnovations.bundle.selectInnovation" /]: </label>
              <div class="note" style="align-items: center;">
              <span class="glyphicon glyphicon-question-sign" style="margin-top: 3px; font-size: 24px;"></span><p style="margin: 0;"> [@s.text name="projectInnovations.bundle.selectInnovation.helpText" /]</p>
              </div>
            </div>
            <div class="commentNumberContainer" style="flex-shrink: 0; display: flex; align-items: center; justify-content: center;">
              <div class="numberOfCommentsBubble">
              <p></p>
              </div>
              <img src="${baseUrlCdn}/global/images/comment.png" class="qaComment" name="innovation.bundles" fieldID="" description="">
            </div>
            </div>
        </div>
        [#-- Table --]
        <div class="col-md-12 simpleBox">
          [#if editable]
            [@tableAllInnovationsMacro list=allInnovationList selected=element.bundles clusterName=element.project.acronym /]
          [/#if]
        </div>
        [#-- Innovations selected - bundles --]
        <div class="col-md-12 innovationBundleBlock" listname="innovation.bundles">
          <label>[@s.text name="projectInnovations.bundle.selectInnovation.selected" /]:[@customForm.req required=false /]</label>
          [#-- Innovation selected List - bundles --]
          <div class="innovationBundleList">
            [#if element.bundles?has_content]
              [#list element.bundles as innovation]
                [@innovationSelectedMacro element=innovation name="innovation.bundles" index=innovation_index template=false editable=editable /]
              [/#list]
            [/#if]
          </div>
          <div class="clearfix"></div>
          [#-- Template item --]
          <div style="display:none">
            [@innovationSelectedMacro element={} name="innovation.bundles" index=-1 template=true editable=editable /]
          </div>
        </div>
        <div class="clearfix"></div>
      </div>


      [#-- Add complementary innovation --]
      [#-- A module that creates accordions with inner unique data --]
      <div class="form-group">
        <div style="display: flex; align-items: center; gap: 15px;">
          <div style="flex: 1;">
            <label for="innovation.complementarySolutions" class="label--2">[@s.text name="projectInnovations.bundle.complementaryInnovations" /]:</label>
          </div>
          <div class="commentNumberContainer" style="flex-shrink: 0; display: flex; align-items: center; justify-content: center;">
            <div class="numberOfCommentsBubble">
              <p></p>
            </div>
            <img src="${baseUrlCdn}/global/images/comment.png" class="qaComment" name="innovation.complementarySolutions" fieldID="" description="">
          </div>
        </div>
        <br />
        <div class="col-md-12">
          <div class="complementarySolutionsBlock">
            <div class="complementarySolutionsList">
              [#if element.complementarySolutions?has_content]
                [#list element.complementarySolutions as solution]
                  [#-- Complementary Solutions Macro --]
                  [@complementarySolutionsMacro name="innovation.complementarySolutions" element=solution index=solution_index template=false editable=editable /]
                [/#list]
              [/#if]
            </div>
            [#if editable]
            <div class="addButtonComplementarySolutions bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span>Add a complementary innovation</div>
            <div class="clearfix"></div>
            [/#if]
          </div>
          [#-- Element item Template --]
          <div style="display:none">
            [@complementarySolutionsMacro name="innovation.complementarySolutions" element={} index=-1 template=true  /]
          </div>
        </div>
      </div>
    </div>
  </div>  
[/#macro] 

[#macro innovationSharing element name index=-1 template=false]
  [#local customName = "${name}"/]

  <div id="funding" class="borderBox clearfix">
    <div class="form-group">

      [#-- Innovation Scaling Readiness --]
      <div class="form-group">
        [@scalingMacro name="innovation.projectInnovationInfo.readinessScale" element=(innovation.projectInnovationInfo.readinessScale)!-1 editable=true label="projectInnovations.readiness.scale" helpLabel="projectInnovations.readiness.scale.help" listName=scalingReadinessList class="innovationScaling" /]
      </div>

      [#-- Foresee barriers --]
      <div class="form-group col-md-12">
        <label class="col-md-12">[@s.text name="projectInnovations.sharing.aboutTheTool.foreseeBarriers" /][@customForm.req required=editanle /]</label>
        [#local foreseeBarriersText = "foreseeBarriers" /]
        [#local foreseeBarriers = (element.projectInnovationInfo.foreseeBarriers)! /]  

        <div class="col-md-1">
          [@customForm.radioFlat id="${foreseeBarriersText}-yes" name="${customName}.projectInnovationInfo.foreseeBarriers" label="Yes" value="true" checked=((element.projectInnovationInfo??)&&(element.projectInnovationInfo.foreseeBarriers??) &&(foreseeBarriers)) cssClass="radioType-${foreseeBarriersText}" cssClassLabel="radio-label-yes" editable=editable /]
        </div>
        <div class="col-md-1">
          [@customForm.radioFlat id="${foreseeBarriersText}-no" name="${customName}.projectInnovationInfo.foreseeBarriers" label="No" value="false" checked=((element.projectInnovationInfo??)&&(element.projectInnovationInfo.foreseeBarriers??) &&(!foreseeBarriers)) cssClass="radioType-${foreseeBarriersText}" cssClassLabel="radio-label-no" editable=editable /]
        </div>

        [#local isDisplayKnowledgeToolUsesNarrative = ((element.projectInnovationInfo??)&&(element.projectInnovationInfo.foreseeBarriers??)&&(foreseeBarriers)) || ((!editable)) /]
        [#-- knowledgeToolUsesNarrative --]
        <div class="col-md-12 padding-left-2 block-foreseeBarriers" style="display:${(isDisplayKnowledgeToolUsesNarrative)?then('block','none')};">
          [@customForm.textArea name="${customName}.projectInnovationInfo.knowledgeToolUsesNarrative" i18nkey="projectInnovations.sharing.aboutTheTool.uses" helpIcon=false className="limitWords-500" required=editable editable=editable /]
        </div>
      </div>  

      [#-- knowledgeResultsNarrative --]
      <div class="col-md-12">
        [@customForm.textArea name="${customName}.projectInnovationInfo.knowledgeResultsNarrative" i18nkey="projectInnovations.sharing.aboutTheTool.results" helpIcon=false className="limitWords-500" required=false editable=editable /]
      </div>

      [#-- Knowledge Sharing and Scaling Potential --]
      <div class="form-group">
        <div class="col-md-12">
          <label class="col-md-12 label--2">[@s.text name="projectInnovations.sharing.knowledge" /]:</label>
        </div>
        <div class="col-md-12 padding-left-2">

          [#-- Technical excellence --]
          <label class="label--2 col-md-12 blueLightColor">
            [@s.text name="projectInnovations.sharing.technicalExcellence" /]:
          </label>

          [#local cheaperAlternativesValue = (element.projectInnovationInfo.cheaperAlternatives)!{} /]
          [#local simplerUseValue = (element.projectInnovationInfo.simplerUse)!{} /]
          [#local performBetterValue = (element.projectInnovationInfo.performBetter)!{} /]

          [@customForm.likertScale name="${customName}.projectInnovationInfo.cheaperAlternatives" id="${customName}.projectInnovationInfo.cheaperAlternatives" value=(cheaperAlternativesValue) label="projectInnovations.sharing.technicalExcellence.cheaperAlternatives" required=true editable=editable subLabelLeft="projectInnovations.sharing.technicalExcellence.cheaperAlternatives.subLabel.left" subLabelRight="projectInnovations.sharing.technicalExcellence.cheaperAlternatives.subLabel.right" /]
          [@customForm.likertScale name="${customName}.projectInnovationInfo.simplerUse" id="${customName}.projectInnovationInfo.simplerUse" value=(simplerUseValue) label="projectInnovations.sharing.technicalExcellence.simplerUse" required=true editable=editable subLabelLeft="projectInnovations.sharing.technicalExcellence.simplerUse.subLabel.left" subLabelRight="projectInnovations.sharing.technicalExcellence.simplerUse.subLabel.right" /]
          [@customForm.likertScale name="${customName}.projectInnovationInfo.performBetter" id="${customName}.projectInnovationInfo.performBetter" value=(performBetterValue) label="projectInnovations.sharing.technicalExcellence.performBetter" required=true editable=editable subLabelLeft="projectInnovations.sharing.technicalExcellence.performBetter.subLabel.left" subLabelRight="projectInnovations.sharing.technicalExcellence.performBetter.subLabel.right" /]

          [#-- Demand and Investment --]
          <label class="label--2 col-md-12 blueLightColor">
            [@s.text name="projectInnovations.sharing.demand&Investment" /]:
          </label>
          
          [#local innovationDesirableValue = (element.projectInnovationInfo.innovationDesirable)!{} /]
          [#local innovationCommerciallyValue = (element.projectInnovationInfo.innovationCommercially)!{} /]

          [@customForm.likertScale name="${customName}.projectInnovationInfo.innovationDesirable" id="${customName}.projectInnovationInfo.innovationDesirable" value=(innovationDesirableValue) label="projectInnovations.sharing.demand&Investment.innovationDesirable" required=true editable=editable subLabelLeft="projectInnovations.sharing.demand&Investment.innovationDesirable.subLabel.left" subLabelRight="projectInnovations.sharing.demand&Investment.innovationDesirable.subLabel.right" /]
          [@customForm.likertScale name="${customName}.projectInnovationInfo.innovationCommercially" id="${customName}.projectInnovationInfo.innovationCommercially" value=(innovationCommerciallyValue) label="projectInnovations.sharing.demand&Investment.innovationCommercially" required=true editable=editable subLabelLeft="projectInnovations.sharing.demand&Investment.innovationCommercially.subLabel.left" subLabelRight="projectInnovations.sharing.demand&Investment.innovationCommercially.subLabel.right" /]
          
          [#-- Sustained use --]
          <label class="label--2 col-md-12 blueLightColor">
            [@s.text name="projectInnovations.sharing.sustainedUse" /]:
          </label>

          [#local innovationSupportedValue = (element.projectInnovationInfo.innovationSupported)!{} /]
          [#local evidenceUptakeValue = (element.projectInnovationInfo.evidenceUptake)!{} /]

          [@customForm.likertScale name="${customName}.projectInnovationInfo.innovationSupported" id="${customName}.projectInnovationInfo.innovationSupported" value=(innovationSupportedValue) label="projectInnovations.sharing.sustainedUse.innovationSupported" required=true editable=editable subLabelLeft="projectInnovations.sharing.sustainedUse.innovationSupported.subLabel.left" subLabelRight="projectInnovations.sharing.sustainedUse.innovationSupported.subLabel.right" /]
          [@customForm.likertScale name="${customName}.projectInnovationInfo.evidenceUptake" id="${customName}.projectInnovationInfo.evidenceUptake" value=(evidenceUptakeValue) label="projectInnovations.sharing.sustainedUse.evidenceUptake" required=true editable=editable subLabelLeft="projectInnovations.sharing.sustainedUse.evidenceUptake.subLabel.left" subLabelRight="projectInnovations.sharing.sustainedUse.evidenceUptake.subLabel.right" /]

        </div>
      </div>

      [#-- hasKnowledgePotential --]
      <div class="col-md-12 margin-top-10 form-group">
        [@customForm.labelText name="innovation.hasKnowledgePotential" text="projectInnovations.sharing.aboutTheTool.potential" required=true className="width-100" /]
        [#local hasKnowledgePotentialText = "hasKnowledgePotential" /]
        [#local hasKnowledgePotential = (element.projectInnovationInfo.hasKnowledgePotential.id)!-1 /]    

        <div class="col-md-1">
          [@customForm.radioFlat id="${hasKnowledgePotentialText}-yes" name="${customName}.projectInnovationInfo.hasKnowledgePotential.id" label="Yes" value="1" checked=(hasKnowledgePotential == 1) cssClass="radioType-${hasKnowledgePotentialText}" cssClassLabel="radio-label-yes" editable=editable /]
        </div>
        <div class="col-md-3">
          [@customForm.radioFlat id="${hasKnowledgePotentialText}-yesW" name="${customName}.projectInnovationInfo.hasKnowledgePotential.id" label="Yes with adaptations" value="2" checked=(hasKnowledgePotential == 2) cssClass="radioType-${hasKnowledgePotentialText}" cssClassLabel="radio-label-yes" editable=editable /]
        </div>
        <div class="col-md-1">
          [@customForm.radioFlat id="${hasKnowledgePotentialText}-no" name="${customName}.projectInnovationInfo.hasKnowledgePotential.id" label="No" value="3" checked=(hasKnowledgePotential == 3) cssClass="radioType-${hasKnowledgePotentialText}" cssClassLabel="radio-label-no" editable=editable /]
        </div>

        [#local isDisplayReasonKnowledgePotential = ((element.projectInnovationInfo??)&&(element.projectInnovationInfo.hasKnowledgePotential??)&&((hasKnowledgePotential == 2))) || ((!editable)) /]
        
        [#-- reasonKnowledgePotential --]
        <div class="col-md-12 block-w-${hasKnowledgePotentialText} padding-left-2" style="display:${(isDisplayReasonKnowledgePotential)?then('block','none')};">
          [@customForm.textArea name="${customName}.projectInnovationInfo.reasonKnowledgePotential" i18nkey="projectInnovations.sharing.aboutTheTool.reasonProvided"  helpIcon=false className="limitWords-500" required=(editable) editable=editable /]
        </div>
      </div>
      [#-- knowledge support the outreach --]
      <div class="col-md-12">
        [@customForm.textArea name="${customName}.projectInnovationInfo.knowledgeMethodsAndToolsNarrative" i18nkey="projectInnovations.sharing.aboutTheTool.supportTheOutreach" helpIcon=false className="limitWords-500" required=false editable=editable /]
      </div>

      [#-- About the innovation --]
      [#--        <div class="form-group">
        <label class="label--2 col-md-12 blueLightColor">
          [@s.text name="projectInnovations.sharing.aboutTheTool" /]:
        </label>
        <div class="col-md-12 padding-left-2">
          -- Objetive --
          <div class="col-md-12">
            [@customForm.elementsListComponent name="${customName}.toolCategories" elementType="toolCategory" elementList=(element.toolCategories)![] label="projectInnovations.sharing.aboutTheTool.objetive" listName="toolCategoryList" keyFieldName="id" displayFieldName="name" required=false /]
          </div>
          -- knowledgeToolUsesNarrative 

        </div>
      </div>  --]
      
      [#-- Anticipated users --]
      <div class="form-group col-md-12 block-innovationAnticipatedUsers">
        [@customForm.labelText name="innovation.anticipatedUsers" text="projectInnovations.anticipatedUsers" twoPoints=false required=true isMainTitle=true className="width-100" /]
        [#local areUsersDetermined = (innovation.projectInnovationInfo.areUsersDetermined)! /]
        <div class="col-md-12">
          <div class="col-md-4">
            [@customForm.radioFlat id="anticipatedUsers-determined" name="innovation.projectInnovationInfo.areUsersDetermined" i18nkey="projectInnovations.anticipatedUsers.determined" value="true" checked=((innovation.projectInnovationInfo??)&&(innovation.projectInnovationInfo.areUsersDetermined??)&&(areUsersDetermined)) cssClass="radioType-anticipatedUsers" cssClassLabel="radio-label-yes" editable=editable /]
          </div>
          <div class="col-md-4">
            [@customForm.radioFlat id="anticipatedUsers-undetermined" name="innovation.projectInnovationInfo.areUsersDetermined" i18nkey="projectInnovations.anticipatedUsers.undetermined" value="false" checked=((innovation.projectInnovationInfo??)&&(innovation.projectInnovationInfo.areUsersDetermined??)&&(!areUsersDetermined)) cssClass="radioType-anticipatedUsers" cssClassLabel="radio-label-no" editable=editable /]
          </div>
        </div>
        <div class="col-md-12 block-anticipatedUsers" style="display: ${((innovation.projectInnovationInfo??)&&(innovation.projectInnovationInfo.areUsersDetermined??)&&(areUsersDetermined))?then('block','none')}">
          [#-- Actors --]
          <div class="col-md-12 actorsBlock">
            <label for="innovation.actors">[@s.text name="projectInnovations.anticipatedUsers.actors" /]:[@customForm.req required=true /]</label>
            <div class="note" style="align-items: center;">
              <span class="glyphicon glyphicon-question-sign" style="margin-top: 3px; font-size: 24px;"></span><p style="margin: 0;"> [@s.text name="projectInnovations.anticipatedUsers.actors.help" /]</p>
            </div>
            [#-- list of items --]
            <div class="actorsList">
              [#list (element.actors)![] as actor]
                [@actorsMacro name="innovation.actors" element=actor index=actor_index template=false /]
              [/#list]
            </div>
            [#if editable]
              <div class="addActors bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> Add actor </div>
              <div class="clearfix"></div>
            [/#if]
          </div>
          [#-- Organizations --]
          <div class="col-md-12 organizationsBlock">
          [#-- list of items --]
          [#-- DEPRECATED --]
          [#--
            <label for="innovation.allianceOrganizations">[@s.text name="projectInnovations.anticipatedUsers.organizations" /]:[@customForm.req required=true /]</label>
             <div class="organizationsList">
              [#list (element.allianceOrganizations)![] as organization]
                [@organizationsMacro name="innovation.allianceOrganizations" element=organization index=organization_index template=false /]
              [/#list]
            </div>
            [#if editable]
              <div class="addOrganizations bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> Add organization </div>
              <div class="clearfix"></div>
            [/#if] --]
            [#-- multiselect organizations --]
            <br>
            [#-- data multiselect to be process after load --]
            [#-- Convert allianceOrganizations to a string for data-value --]
            [#local customValueOrganizations = "[]" /]
            [#if (element.allianceOrganizations)?has_content]
              [#local orgList = [] /]
              [#list element.allianceOrganizations as organization]
              
	              [#if organization?? && organization.institution??]
								  [#local orgList = orgList + ['{"id": "' + (organization.id!"") + '", "value": "' + (organization.institution.id!"") + '"}' ] /]
								[/#if]
              
              [/#list]
              [#local customValueOrganizations = "[" + orgList?join(",") + "]" /]
            [/#if]
            <div class="form-group">
              <label for="innovation.allianceOrganizations">[@s.text name="projectInnovations.anticipatedUsers.organizations" /]:[@customForm.req required=false /]</label>
              <br>
              <mal-multiselect name="${customName}.allianceOrganizations" id="${customName}.allianceOrganizations" class="allianceOrganizations-institutions" show-selected-container="true" reference="institution.id" data-value='${customValueOrganizations}'>
              </mal-multiselect>
            </div>
          </div>
          [#-- Element item Template --]
          <div style="display:none">
            [@actorsMacro name="innovation.actors" element={} index=-1 template=true /]
            [#--  [@organizationsMacro name="innovation.allianceOrganizations" element={} index=-1 template=true /]  --]
          </div>

        </div>
          
      </div>

      [#-- Beneficiries --]
      <div class="form-group col-md-12">
        <label for="innovation.actors">[@s.text name="projectInnovations.beneficiaries" /]:[@customForm.req required=true /]</label>
        <div class="note" style="align-items: center;">
          <span class="glyphicon glyphicon-question-sign" style="margin-top: 3px; font-size: 24px;"></span><p style="margin: 0;"> [@s.text name="projectInnovations.beneficiaries.helpText" /]</p>
        </div>
        [@customForm.textArea name="innovation.projectInnovationInfo.beneficiariesNarrative"  showTitle=false placeholder="" className="limitWords-80"  isNote=true helpIcon=false required=true editable=editable isWidthFull=true /]
      </div>
      
      [#-- URLs: Tool website, publications, stories and more  --]
      [#--        <div class="form-group">
        <label class="label--2 col-md-12 blueLightColor">[@s.text name="projectInnovations.sharing.urls" /]:</label>
        #-- hasToolUrl ---
        <div class="col-md-12 padding-left-2">
          [@customForm.labelText name="innovation.hasToolUrl" text="projectInnovations.sharing.urls.tool" /]
          [#local hasToolUrlText = "hasToolUrl" /]
          [#local hasToolUrl = (element.projectInnovationInfo.hasToolUrl)! /]    

          <div class="col-md-1">
            [@customForm.radioFlat id="${hasToolUrlText}-yes" name="${customName}.projectInnovationInfo.hasToolUrl" label="Yes" value="true" checked=((element.projectInnovationInfo??)&&(element.projectInnovationInfo.hasToolUrl??) &&(hasToolUrl)) cssClass="radioType-${hasToolUrlText}" cssClassLabel="radio-label-yes" editable=editable /]
          </div>
          <div class="col-md-1">
            [@customForm.radioFlat id="${hasToolUrlText}-no" name="${customName}.projectInnovationInfo.hasToolUrl" label="No" value="false" checked=((element.projectInnovationInfo??)&&(element.projectInnovationInfo.hasToolUrl??) &&(!hasToolUrl)) cssClass="radioType-${hasToolUrlText}" cssClassLabel="radio-label-no" editable=editable /]
          </div>

          #-- If yes - Evidence/Reference --
          <div class="col-md-12 block-yes-${hasToolUrlText}" style="display:${((element.projectInnovationInfo??)&&(element.projectInnovationInfo.hasToolUrl??) && (hasToolUrl))?then('block','none')};">
            <label class="note--2" style="width:100%"><p class="col-md-12">[@s.text name="projectInnovations.sharing.urls.tool.help" /]</p></label>
            #-- List URLs --
            <div class="form-group">
              <div class="col-md-12">
                <div class="referenceBlock">
                  <div class="referenceListUrl">
                    [#if element.referenceUrls?has_content]
                      [#list element.referenceUrls as referenceUrl]
                        [@customForm.evidence name="innovation.referenceUrls" element=referenceUrl index=referenceUrl_index template=false class="Url" /]
                      [/#list]
                    [/#if]
                  </div>
                  [#if editable]
                  <div class="addButtonReferenceUrl bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span>Add Reference </div>
                  <div class="clearfix"></div>
                  [/#if]
                </div>
                #-- Element item Template --
                <div style="display:none">
                  [@customForm.evidence name="innovation.referenceUrls" element={} index=-1 template=true class="Url" /]
                </div>
              </div>
            </div>
          </div>
            
          #-- If not - reasonNotToolUrl --
          <div class="col-md-12 block-no-${hasToolUrlText}" style="display:${((element.projectInnovationInfo??)&&(element.projectInnovationInfo.hasToolUrl??) && (!hasToolUrl))?then('block','none')};">
            [@customForm.textArea name="${customName}.projectInnovationInfo.reasonNotToolUrl" i18nkey="projectInnovations.sharing.urls.reasonNoProvided"  helpIcon=false className="limitWords-500" required=(editable) editable=editable /]
          </div>
        </div>
      </div>  --]

      [#-- Collaboration with other innovations --]
      [#--        <div class="form-group">
        <label class="label--2 col-md-12 blueLightColor">[@s.text name="projectInnovations.sharing.collaboration" /]:</label>
        <div class="col-md-12 padding-left-2">
          #-- knowledgeCollaboration --
          <div class="col-md-12">
            <label class="note--2"><p>[@s.text name="projectInnovations.sharing.collaboration.help" /]</p></label>
            [@customForm.textArea name="${customName}.projectInnovationInfo.knowledgeCollaboration" i18nkey="projectInnovations.sharing.collaboration.knowledgeCollaboration" helpIcon=false className="limitWords-200" required=false editable=editable /]  
          </div>
          #-- urls Complementary Solutions --
          <div class="col-md-12">
            [@customForm.labelText name="innovation.hasComplementarySolutions" text="projectInnovations.sharing.collaboration.complementarySolutions" /]
            <div class="col-md-12">
              <div class="referenceBlock">
                <div class="referenceListComplementary">
                  [#if element.referenceComplementarySolutions?has_content]
                    [#list element.referenceComplementarySolutions as referenceComplementary]
                      [@customForm.evidence name="innovation.referenceComplementarySolutions" element=referenceComplementary index=referenceComplementary_index template=false class="Complementary" isDeliverable=false /]
                    [/#list]
                  [/#if]
                </div>
                [#if editable]
                <div class="addButtonReferenceComplementary bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span>Add associated innovation</div>
                <div class="clearfix"></div>
                [/#if]
              </div>
              #-- Element item Template --
              <div style="display:none">
                [@customForm.evidence name="innovation.referenceComplementarySolutions" element={} index=-1 template=true  class="Complementary" isDeliverable=false /]
              </div>
            </div>
          </div>
        </div>
      </div>  --]

      [#-- Evidence and Reference --]
      <div class="form-group">
        [@customForm.labelText name="innovation.references" text="projectInnovations.readiness.evidence" helpText="projectInnovations.readiness.evidence.help" required=false isMainTitle=true twoPoints=false classNameLabel="blueLightColor" className="width-100" /]
        <br>
        <label class="note note--3 statusEvidenceInImpactArea"><span class="glyphicon glyphicon-exclamation-sign"></span> <span class="contentInformation" >Problem available</span></label>
        <div class="col-md-12">
          <div class="referenceBlock">
            <div class="referenceListReadiness">
              [#if element.references?has_content]
                [#list element.references as reference]
                  [@customForm.evidence name="innovation.references" element=reference index=reference_index template=false class="Readiness" editable=editable /]
                [/#list]
              [/#if]
            </div>
            [#if editable]
            <div class="addButtonReferenceReadiness bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span>Add evidence </div>
            <div class="clearfix"></div>
            [/#if]
          </div>
          [#-- Element item Template --]
          <div style="display:none">
            [@customForm.evidence name="innovation.references" element={} index=-1 template=true class="Readiness"  /]
          </div>
        </div>
      </div>
      
  </div>
[/#macro]

[#-- One value chain of the innovation form, as a checkbox chip --]
[#-- Co-developing African institution(s) and Institutional level of a bundle (A2-2613, rule 4.2). The options are
     the innovation's contributing external partners: innovationFormRedesign.js keeps them in step with General
     Information (a partner added there is offered here, a removed one leaves with a notice). listname lets
     fieldsValidation.js mark the group when it is missing. --]
[#macro innovationCoDevelopers element]
  [#local partners = [] /]
  [#list (element.contributingOrganizations)![] as partner]
    [#if (partner.institution.id)??][#local partners = partners + [partner.institution] /][/#if]
  [/#list]
  [#local selectedIds = [] /]
  [#list (element.coDevelopers)![] as coDeveloper]
    [#if (coDeveloper.institution.id)??][#local selectedIds = selectedIds + [coDeveloper.institution.id] /][/#if]
  [/#list]
  [#local level = (element.projectInnovationInfo.institutionalLevel)!"" /]
  <section class="dlf-card inf-codev" id="inf-codev" data-inf-codev
    data-count="[@s.text name="projectInnovations.coDevelopers.count" /]"
    data-removed="[@s.text name="projectInnovations.coDevelopers.removed" /]">
    [#-- fieldsValidation.js marks div[listname] and its first label when the institutions are missing --]
    <div class="inf-codev__field" listname="innovation.coDevelopers">
    <div class="inf-codev__head">
      <label class="dlf-card__title inf-codev__label">[@s.text name="projectInnovations.coDevelopers" /][@customForm.req required=editable /]</label>
      <span class="inf-codev__new">[@s.text name="projectInnovations.coDevelopers.new" /]</span>
      <span class="inf-count" data-inf-codev-count></span>
      <div class="commentNumberContainer">
        <div class="numberOfCommentsBubble"><p></p></div>
        <img src="${baseUrlCdn}/global/images/comment.png" class="qaComment" name="innovation.coDevelopers" fieldID="" description="">
      </div>
    </div>
    <p class="dlf-hint">[@s.text name="projectInnovations.coDevelopers.help" /]</p>

    <p class="inf-codev__notice" role="status" data-inf-codev-notice hidden>
      <span data-inf-codev-notice-text></span>
      <button type="button" class="inf-codev__dismiss" data-inf-codev-dismiss aria-label="Dismiss">&times;</button>
    </p>

    [#if editable]
      <div class="inf-codev__empty" data-inf-codev-empty[#if partners?has_content] hidden[/#if]>
        <span>[@s.text name="projectInnovations.coDevelopers.empty" /]</span>
        <button type="button" class="inf-goTab" data-inf-go-partners="#innovationGeneral">[@s.text name="projectInnovations.coDevelopers.goToPartners" /]<svg class="dlf-icon" width="10" height="10" viewBox="0 0 12 12" fill="none" aria-hidden="true"><path d="M4.5 2.5 8 6l-3.5 3.5" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"></path></svg></button>
      </div>
      <div class="inf-vc__group inf-codev__group" role="group" aria-label="[@s.text name="projectInnovations.coDevelopers" /]" data-inf-codev-group>
        [#list partners as institution]
          [@coDeveloperChip institution=institution index=institution_index checked=selectedIds?seq_contains(institution.id) /]
        [/#list]
      </div>
      <p class="inf-vc__error" role="alert" data-inf-codev-error hidden>[@s.text name="projectInnovations.coDevelopers.missing" /]</p>
      [#-- Chip template: innovationFormRedesign.js clones it for a partner added in this visit --]
      <template data-inf-codev-template>[@coDeveloperChip institution={} index=-1 checked=false /]</template>
    [#else]
      <p class="inf-vc__readOnly">
        [#list partners?filter(i -> selectedIds?seq_contains(i.id)) as institution]<span class="inf-vc__value">${(institution.nameWithCountry)!(institution.composedName)!""}</span>[#else]<span class="inf-vc__empty">[@s.text name="projectInnovations.coDevelopers.none" /]</span>[/#list]
      </p>
    [/#if]
    </div>

    [#-- Institutional level --]
    <div class="inf-codev__level">
      <span class="inf-iaRow__label">[@s.text name="projectInnovations.institutionalLevel" /][@customForm.req required=editable /]</span>
      <div class="inf-codev__levels" role="radiogroup" aria-label="[@s.text name="projectInnovations.institutionalLevel" /]">
        [#list ["regional", "national", "joint"] as option]
          [@customForm.radioFlat id="institutionalLevel-${option}" name="innovation.projectInnovationInfo.institutionalLevel" i18nkey="projectInnovations.institutionalLevel.${option}" value=option checked=(level == option) cssClass="radioType-institutionalLevel" cssClassLabel="radio-label" editable=editable canComment=false /]
        [/#list]
        [#if !editable && !level?has_content]<p class="inf-vc__empty">[@s.text name="projectInnovations.valueChains.none" /]</p>[/#if]
      </div>
      <div class="commentNumberContainer">
        <div class="numberOfCommentsBubble"><p></p></div>
        <img src="${baseUrlCdn}/global/images/comment.png" class="qaComment" name="innovation.projectInnovationInfo.institutionalLevel" fieldID="" description="">
      </div>
    </div>
  </section>
[/#macro]

[#macro coDeveloperChip institution index checked]
  <label class="inf-vcChip" data-institution="${(institution.id?c)!""}">
    <input type="checkbox" name="innovation.coDevelopers[${index?c}].institution.id" value="${(institution.id?c)!""}"[#if checked] checked[/#if] />
    <span class="inf-vcChip__box" aria-hidden="true"></span><span class="inf-codev__name">${(institution.nameWithCountry)!(institution.composedName)!""}</span>
  </label>
[/#macro]

[#macro valueChainChip valueChain index checked]
  <label class="inf-vcChip[#if valueChain.notSpecific] inf-vcChip--exclusive[/#if]">
    <input type="checkbox" name="innovation.valueChains[${index}].valueChain.id" value="${valueChain.id?c}"[#if checked] checked[/#if]
      [#if valueChain.notSpecific]data-not-specific[/#if] [#if valueChain.other]data-other[/#if] />
    <span class="inf-vcChip__box" aria-hidden="true"></span>${valueChain.name}
  </label>
[/#macro]

[#function findElementID list id]
  [#list (list)![] as item]
    [#if (item.repIndGeographicScope.id == id)!false][#return true][/#if]
  [/#list]
  [#return false]
[/#function]

[#macro actorsMacro name element index=-1 template=false class=""]
  [#local customName = "${template?string('_TEMPLATE_', '')}${name}[${index}]"]

  [#local sexAgeNotApply = ((element.sexAgeNotApply??) && (element.sexAgeNotApply == true))! /]

  <div id="actorsInnovation-${(template?string('template', ''))}" class="actorsInnovation form-group graySoftBox ${class}">
    [#-- Hidden not saved - id --]
    [@customForm.input name="${customName}.id" value=((element.id)?string)!"" editable=false display=false /]
    [#-- Dropdown Actors - Type --]
    [#if editable]
    <div class="col-md-12 select--flex padding-bottom-1">
      <div class="col-md-6">
        [@customForm.select name="${customName}.actor.id" i18nkey="projectInnovations.anticipatedUsers.actors.typeActor" listName="actorList" keyFieldName="id" displayFieldName="prmsNameEquivalent" required=false editable=true /]
      </div>
      <div class="col-md-6 checkbox-sexAgeNotApply align-content-end">
        [@customForm.checkBoxFlat id="${customName}.sexAgeNotApply" name="${customName}.sexAgeNotApply" label="projectInnovations.anticipatedUsers.actors.sexAgeNotApply" value="true" checked=sexAgeNotApply editable=true cssClass="sexAgeNotApply" /]
      </div>
    </div>
    [#else]
    <div class="col-md-12 select--flex padding-bottom-1">
      <div class="col-md-6">
        [#local actorName = (element.actor.name)!"No provided" /]
        
        <span class="label--2">[@s.text name="projectInnovations.anticipatedUsers.actors.typeActor" /]:</span>
        <span class="col-md-12"><b>${actorName}</b></span>
      </div>
      <div class="col-md-6 checkbox-sexAgeNotApply align-content-end">
        <span class="label--2">Does sex and age disaggregation apply?</span>
        [#if sexAgeNotApply]
          <span class="col-md-12"><b>No</b></span>
        [#else]
          <span class="col-md-12"><b>Yes</b></span>
        [/#if]
      </div>
    </div>
    [/#if]

    [#local isActorOther = ((element.actor??) && (element.actor.name == "Other"))!false /]

    [#-- Other type --]
    <div class="col-md-6 otherType" style="display: ${((isActorOther) && (editable))?then('block','none')}">
      [@customForm.input name="${customName}.other" i18nkey="projectInnovations.anticipatedUsers.actors.otherTypeActor" required=false editable=true showTitle=true value=(element.other)!"" /]
    </div>

    [#-- Checkbox Actors - Genders --]
    <div class="col-md-12">
      <div class="col-md-12">
        [#--          <label class="col-md-12">[@s.text name="projectInnovations.anticipatedUsers.actors.women" /]:</label>
        [#local isWomanWithYouth = ((element.womenYouth??) && (element.womenYouth == true)) /] 
        [#local isWomanNotYouth = ((element.womenNotYouth??) && (element.womenNotYouth == true)) /]  --]

        [#if editable]
        <div class="col-md-10 innerOptions select--flex" style="padding-left: 0;">
          <div class="col-md-2 margin-right-10">
            [@customForm.input name="${customName}.total" i18nkey="projectInnovations.anticipatedUsers.actors.total" required=true editable=true showTitle=true className="input-total"  /]
          </div>
          <div class="col-md-10 block-sexAgeNotApply" style="display: ${(!sexAgeNotApply)?then('block','none')}">
            <div class="col-md-4 margin-right-10">
              [@customForm.input name="${customName}.womenYouthNumber" i18nkey="projectInnovations.anticipatedUsers.actors.whichWomen" required=false editable=true showTitle=true className="input-whichWomen" /]
            </div>
            <div class="col-md-4">
              [@customForm.input name="${customName}.womenNonYouthNumber" i18nkey="projectInnovations.anticipatedUsers.actors.whichYouth" required=false editable=true showTitle=true className="input-whichYouth" /]
            </div>
          </div>
        </div>
        [#-- Message to appear if a value overpass the total with a banish in 10 seconds --]
        <div class="col-md-12 message-overpass-total" style="display: none;">
          <div class="alert alert-warning">
            <strong>Warning!</strong> The value in <b>Of which women</b> or <b>Of which youth</b> exceeds the total. The values will be modified to match the total.
          </div>
        </div>
        [#else]
        <div class="col-md-12">
          <div class="col-md-3">
            <span class="label--2">[@s.text name="projectInnovations.anticipatedUsers.actors.total" /]:</span>
            <span class="col-md-12"><b>${(element.total)!0}</b></span>
          </div>
          <div class="col-md-3">
            <span class="label--2">[@s.text name="projectInnovations.anticipatedUsers.actors.whichWomen" /]:</span>
            <span class="col-md-12"><b>${(element.womenYouthNumber)!0}</b></span>
          </div>
          <div class="col-md-3">
            <span class="label--2">[@s.text name="projectInnovations.anticipatedUsers.actors.whichYouth" /]:</span>
            <span class="col-md-12"><b>${(element.womenNonYouthNumber)!0}</b></span>
          </div>
        </div>
        [/#if]
      </div>
      [#--        <div class="col-md-6">
        <label class="col-md-12">[@s.text name="projectInnovations.anticipatedUsers.actors.men" /]:</label>
        [#local isMenWithYouth = ((element.menYouth??) && (element.menYouth == true)) /] 
        [#local isMenNotYouth = ((element.menNotYouth??) && (element.menNotYouth == true)) /]
        
        [#if editable]
        <div class="col-md-5 innerOptions select--flex">
          <div class="col-md-6">
            [@customForm.checkBoxFlat id="${customName}.menYouth" name="${customName}.menYouth" label="projectInnovations.anticipatedUsers.actors.optionYouth" value="true" checked=isMenWithYouth editable=true cssClass="check-gender" /]
          </div>
          <div class="col-md-6 align-content-end">
            [@customForm.input name="${customName}.menYouthNumber"  className="targetValueNumber numericInput" type="number" required=false editable=true showTitle=false /]
          </div> 
        </div>
        <div class="col-md-7 innerOptions select--flex">
          <div class="col-md-6">
            [@customForm.checkBoxFlat id="${customName}.menNotYouth" name="${customName}.menNotYouth" label="projectInnovations.anticipatedUsers.actors.optionNoYouth" value="true" checked=isMenNotYouth editable=true cssClass="check-gender" /]
          </div>
          <div class="col-md-6 align-content-end">
            [@customForm.input name="${customName}.menNonYouthNumber" className="targetValueNumber numericInput" type="number" required=false editable=true showTitle=false /]
          </div>
        </div>
        [#else]
          <div class="col-md-4">
            <span class="label--2">[@s.text name="projectInnovations.anticipatedUsers.actors.optionYouth" /]:</span>
            <span class="col-md-12"><b>${(element.menYouthNumber)!0}</b></span>
          </div>
          <div class="col-md-4">
            <span class="label--2">[@s.text name="projectInnovations.anticipatedUsers.actors.optionNoYouth" /]:</span>
            <span class="col-md-12"><b>${(element.menNonYouthNumber)!0}</b></span>
          </div>
        [/#if]
      </div>  --]
      [#--        <div class="col-md-4">
        <label>[@s.text name="projectInnovations.anticipatedUsers.actors.noBinary" /]:</label>
        [#local isNonbinaryWithYouth = ((element.nonbinaryYouth??) && (element.nonbinaryYouth == true)) /] 
        [#local isNonbinaryNotYouth = ((element.nonbinaryNotYouth??) && (element.nonbinaryNotYouth == true)) /]
        [@customForm.checkBoxFlat id="${customName}.nonbinaryYouth" name="${customName}.nonbinaryYouth" label="projectInnovations.anticipatedUsers.actors.optionYouth" value="true" checked=isNonbinaryWithYouth editable=true /]
        [@customForm.checkBoxFlat id="${customName}.nonbinaryNotYouth" name="${customName}.nonbinaryNotYouth" label="projectInnovations.anticipatedUsers.actors.optionNoYouth" value="true" checked=isNonbinaryNotYouth editable=true /]
      </div>  --]
    </div>

    [#-- Remove --]
    [#if editable]<div class="removeElement sm removeIcon removeActor ${class}" title="Remove"></div>[/#if]
    <div class="clearfix"></div>
  </div>
[/#macro]

[#--  Organizations Macro  --]
[#--  DEPRECATED - Not being used, a good development :( - 30/07/2025 --]
[#macro organizationsMacro name element index=-1 template=false class=""]
  [#local customName = "${template?string('_TEMPLATE_', '')}${name}[${index}]"]
  
  <div id="organizationsInnovation-${(template?string('template', ''))}" class="organizationsInnovation form-group grayBlueBox ${class}">
    [#-- Hidden not saved - id --]
    [@customForm.input name="${customName}.id" className="indexTag" value=((element.id)?string)!"" editable=false display=false /]
    [#-- "Dropdown Organizations - Type 
    <div class="col-md-12">
      [@customForm.select name="${customName}.institutionType.id" showTitle=false  i18nkey="projectInnovations.organizations" listName="institutionTypeList" keyFieldName="id" displayFieldName="name" required=false editable=true /]
    </div>
    --]
    [#-- Input Organization name --]
    <div class="col-md-12">
      [#if editable]
      <div class="col-md-6">
        [#local organizationId = (element.institution.id)!"" /]
        [@customForm.labelText name="${customName}.institution.id" text="projectInnovations.anticipatedUsers.organizations.name" required=true /]
        [#-- <mal-select name="${customName}.institution.id" id="${customName}.institution.id" data-value="${organizationId}" class="allianceOrganizations-institutions" ></mal-select> --]
        [#-- [@customForm.select name="${customName}.institution.id" label="projectInnovations.anticipatedUsers.organizations.name"  i18nkey="projectInnovations.anticipatedUsers.organizations" listName="institutions" keyFieldName="id" displayFieldName="composedNameType" required=false editable=true /] --]
      </div>
      <div class="col-md-6">
        <div class="col-md-12">
          [@customForm.input name="${customName}.number" className="targetValueNumber" type="number" required=false editable=true i18nkey="projectInnovations.anticipatedUsers.organizations.number" /]
        </div>
      </div>
      [#else]
      <div class="col-md-6">
        [#local organizationName = (element.institution.name)!"" /]
        <span class="label--2">[@s.text name="projectInnovations.anticipatedUsers.organizations.name" /]:</span>
        <span class="col-md-12"><b>${organizationName}</b></span>
      </div>
      <div class="col-md-6">
        <span class="label--2">[@s.text name="projectInnovations.anticipatedUsers.organizations.number" /]:</span>
        <span class="col-md-12"><b>${(element.number)!0}</b></span>
      </div>
      [/#if]
    </div>
    [#-- Checkbox - is a co-development --]
    [#--      <div class="col-md-12">
      [#local isCoDevelopment = ((element.scalingPartner??) && (element.scalingPartner == true)) /]
      [@customForm.checkBoxFlat id="${customName}.scalingPartner" name="${customName}.scalingPartner" label="projectInnovations.anticipatedUsers.organizations.coDevelopment" value="true" checked=isCoDevelopment editable=true /]
    </div>  --]

    [#-- Remove --]
    [#if editable]<div class="removeElement sm removeIcon removeOrganization ${class}" title="Remove"></div>[/#if]
    <div class="clearfix"></div>
  </div>
[/#macro]

[#macro scalingMacro name element editable label="" helpLabel="" listName=[] class=""]
  [#local customName = "${name}"]
  <div id="scalingInnovation" class="scaling form-group ${class}">
    <label for="${customName}" class="label--2">[@s.text name=label /]: [@customForm.req required=(editable) /]</label>
    <div class="note" style="align-items: unset;">
      <span class="glyphicon glyphicon-question-sign" style="margin-top: 3px; font-size: 24px;"></span><p style="margin: 0;"> [@s.text name=helpLabel /]</p>
    </div>

    <div class="scaling__container">
      <div class="scaling__line col-md-11"></div>
      [#if listName?has_content]
        [#list listName as item]
          <div class="col-md-1 scaling__item">
            [@customForm.radioFlat id="${customName}_${item_index}" name="${customName}" label="${item.id-1}" value="${item.id}" checked=((element??) && (element == (item.id)))!false editable=editable cssClass="scalingInnovation__item__value" canComment=false /]
          </div>
        [/#list]
      [/#if]
    </div>
    <div class="scaling__message grayBox">
      [#if element != -1]
        [#local elemInformation = listName?filter(it -> (it.id == element)) /]
        [#if elemInformation?size > 0]
          <h5>[@s.text name=elemInformation[0].name /]</h5>
          <p>[@s.text name=elemInformation[0].description /]</p>
        [/#if]
      [#else]
        <h5>[@s.text name="projectInnovations.readiness.scale.message1" /]</h5>
        <p>[@s.text name="projectInnovations.readiness.scale.message2" /]</p>
      [/#if]
    </div>
    <div class="scaling__hiddenInfo" style="display: none">
      [#if listName?has_content]
        [#list listName as item]
          <div class="scaling__hiddenInfo__item" class="scaling__hiddenInfo__item_${item_index}" id="${item.id}">
            <h5>${item.name}</h5>
            [#if item.description?has_content]
              <p>${item.description!""}</p>
            [#else]
              <p>No descripition available</p>
            [/#if]
          </div>
        [/#list]
      [/#if]
        
    </div>
    <div class="clearfix"></div>
  </div>
[/#macro]

[#-- One Impact Area: the label and the 0 / 1 / 2 answer as a segmented control (the score number is drawn by
     innovationForm.css from the radio value). With a score of 2 the row asks for evidence in the Knowledge tab:
     projectInnovations.js slides the note in, innovationFormRedesign.js fills the evidence status. --]
[#macro impactScoreRadioGroup fieldName fieldLabel fieldValue={} evidenceTag="" evidenceTab="" editable=editable]
  [#local fieldValue = fieldValue!{} /]
  [#local inputName = "innovation.projectInnovationInfo.${fieldName}.id" /]
  [#local isPrincipal = (fieldValue.id?? && fieldValue.id == 3) /]
  <div class="inf-iaRow" id="inf-ia-${fieldName}" data-inf-ia-row data-evidence="${evidenceTag}">
    <div class="inf-iaRow__main">
      <span class="inf-iaRow__label">[@s.text name=fieldLabel /][@customForm.req required=editable /]</span>
      <div class="inf-segmented inf-iaScore" role="radiogroup" aria-label="[@s.text name=fieldLabel /]">
        [#list (impactAreaScores)![] as option]
          [#local optionLabel = editable?then((option.completeDescription)!option.description, option.description) /]
          [@customForm.radioFlat id="${fieldName}-${option.id}" name=inputName value="${option.id}" label=optionLabel checked=(fieldValue.id?? && fieldValue.id == option.id) cssClass="radioType-contributionToCGIAR" cssClassLabel="radio-label" editable=editable /]
        [#else]
          <p class="text-muted">No options available.</p>
        [/#list]
      </div>
    </div>
    <div class="note inf-iaRow__principal" name="${inputName}" style="display:${isPrincipal?string('block','none')};">
      <div class="inf-iaRow__principalInner">
        <svg class="dlf-icon" width="15" height="15" viewBox="0 0 16 16" fill="none" aria-hidden="true"><circle cx="8" cy="8" r="6.4" stroke="currentColor" stroke-width="1.4"></circle><path d="M8 7.3v3.6" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"></path><circle cx="8" cy="5.1" r=".95" fill="currentColor"></circle></svg>
        <span class="inf-iaRow__msg">[@s.text name="innovation.oneCGIAR.tooltip2"][@s.param]<strong>[@s.text name=fieldLabel /]</strong>[/@][/@]</span>
        <span class="inf-evidence" data-inf-evidence></span>
        <button type="button" class="inf-goTab" data-inf-go-tab="#innovationSharing">[@s.text name="projectInnovations.redesign.goTo"][@s.param]${evidenceTab}[/@s.param][/@s.text]<svg class="dlf-icon" width="10" height="10" viewBox="0 0 12 12" fill="none" aria-hidden="true"><path d="M4.5 2.5 8 6l-3.5 3.5" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"></path></svg></button>
      </div>
    </div>
  </div>
[/#macro]

[#macro complementarySolutionsMacro element name index template=false editable=false ]
  [#local customName = "${template?string('_TEMPLATE_', '')}${name}[${index}]"]

  <div id="complementaryInnovation-${template?string('template',index)}" class="complementaryInnovation borderBox" style="display:${template?string('none','block')}">
    [#-- Remove Button --]
    <div class="remove-element removeElement removeComplementaryInnovation" title="Remove"></div>

    [#-- Complementary innovation --]
    <div class="blockTitle closed">
		  [#if element.title?has_content]
				${element.title}
			[#else]
				New Complementary Innovation
			[/#if]
    </div>
    
    <div class="blockContent" style="display:none">
      <hr />
      [#-- ID  --]
      <input type="hidden" name="${customName}.id" value="${(element.id)!}"/>
      [#-- Title  --]
      <div class="form-group">
        [@customForm.input name="${customName}.title" i18nkey="projectInnovations.bundle.complementaryInnovations.title" className="description limitWords-30" required=true /]
      </div>
      <div class="clearfix"></div>
      [#-- Short title  --]
      <div class="form-group">
        [@customForm.input name="${customName}.shortTitle" i18nkey="projectInnovations.bundle.complementaryInnovations.shortTitle" className="description limitWords-15" required=true /]
      </div>
      <div class="clearfix"></div>
      [#-- Short description  --]
      <div class="form-group">
        [@customForm.textArea name="${customName}.shortDescription" i18nkey="projectInnovations.bundle.complementaryInnovations.shortDescription" className="description limitWords-80" required=true /]
      </div>
      <div class="clearfix"></div>
      [#-- Innovation type select --]
      <div class="form-group" listname="${customName}.projectInnovationType.id">
        [@customForm.select name="${customName}.projectInnovationType.id" i18nkey="projectInnovations.bundle.complementaryInnovations.type" listName="innovationTypeList" keyFieldName="id" displayFieldName="name" required=true editable=true /]
      </div>
      <div class="clearfix"></div>
      [#-- Function  --]
      <div class="form-group" listname="${customName}.complementarySolutionFunctions">
        [@customForm.labelText name="${customName}.function" text="projectInnovations.bundle.complementaryInnovations.function" required=true helpText="projectInnovations.bundle.complementaryInnovations.function.helpText" /]
        [#if projectInnovationFunctionList?has_content]
          [#list projectInnovationFunctionList as function]
            <div class="col-md-12">
              [#local functionLabel = function.title!"" /]
              [#local cleanedLabel = functionLabel?replace('\\\\/', '/')?replace('\\/', '/') /]
              [#local cleanedLabel = cleanedLabel?js_string!"" /]


              [#local isChecked = false /]
              [#if element.complementarySolutionFunctions?has_content]
                [#list element.complementarySolutionFunctions as func]
                  [#if func.projectInnovationFunction.id == function.id]
                    [#local isChecked = true /]
                    [#local savedFunctionId = func.id /]
                  [/#if]
                [/#list]
              [/#if]

							[#-- Hidden ID --]
							[#if isChecked && savedFunctionId?has_content]
								<input name="${customName}.complementarySolutionFunctions[${(function.id)!}].id" value="${savedFunctionId}" type="hidden" />
							[/#if]

              [#-- Checkbox Function --]
              [@customForm.checkBoxFlat id="${customName}.complementarySolutionFunctions[${function.id}].projectInnovationFunction.id" isLabelDB=true name="${customName}.complementarySolutionFunctions[${function.id}].projectInnovationFunction.id" label=cleanedLabel editable=true value=function.id checked=isChecked cssClass="check-function" /]
            </div>
          [/#list]
        [/#if]
      </div>
      <div class="clearfix"></div>
    </div>
  </div>
[/#macro]

[#macro tableAllInnovationsMacro list selected={} clusterName="" ]
  [#-- The filter texts: projectInnovations.js builds the filter over the table --]
  <table id="table-all-innovations" class="table table-striped table-hover" width="100%" data-cluster="${clusterName}"
    data-filter-label="[@s.text name="projectInnovations.bundle.filter.label" /]"
    data-filter-all="[@s.text name="projectInnovations.bundle.filter.all" /]"
    data-filter-placeholder="[@s.text name="projectInnovations.bundle.filter.placeholder" /]">
    <thead>
      <tr>
        <th id="tb-id" width="1%" class="no-sort">ID</th>
        <th id="tb-title" width="32%">[@s.text name="projectInnovations.table.title" /]</th>
        <th id="tb-cluster" width="10%">[@s.text name="projectInnovations.table.cluster" /]</th>
        <th id="tb-type" width="20%">[@s.text name="projectInnovations.table.type" /]</th>
        <th id="tb-readinessLevel" width="15%">[@s.text name="projectInnovations.table.readinessLevel" /]</th>
        <th id="tb-year" width="1%">[@s.text name="projectInnovations.table.year" /]</th>
        <th id="tb-actions" class="no-sort" width="15%">Action</th>
        [#-- Hidden column Don't remove it is neccesary for the library --]
        <th id="tb-hidden" style="display: none"></th>
      </tr>
    </thead>
    <tbody>
      [#if list?has_content]
        [#list list as innovation]
          <tr class="innovation-row" data-id="${innovation.id}">
            [#-- Title --]
            [#local title]
              [#if ((innovation.projectInnovationInfo??)&&(innovation.projectInnovationInfo.title?has_content))]
                [@utilities.wordCutter string=(innovation.projectInnovationInfo.title)!"" maxPos=120 /]
              [/#if]
            [/#local]

            [#-- URL --]
            [#local url][@s.url namespace="/projects" action="${(crpSession)!}/innovation"][@s.param name='innovationID']${(innovation.id)!''}[/@s.param][@s.param name='projectID']${(innovation.project.id)!''}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]

            [#-- Boolean scaling readiness available --]
            [#local isScalingReadines = ((innovation.projectInnovationInfo??)&&(innovation.projectInnovationInfo.readinessScale?has_content))!false ]
          
            [#-- Scaling Readiness value --]
            [#local scaleReadiness = (innovation.projectInnovationInfo.readinessScale-1)!"" ]

            [#-- Scaling Innovation Titles --]
            [#local scalingInnovationTitles = ["Idea", "Basic Research", "Formulation", "Proof of Concept", "Controlled Testing","Model/Early Prototype","Semi-Controlled Testing","Prototype","Uncontrolled Testing","Proven Innovation"]]

            [#-- ID --]
            <td class="text-center">${innovation.id!}</td>
            [#-- Title --]
            <td class="text-left">
              <a href="${url!''}" class="title-link" target="_blank" rel="noopener noreferrer" title="View details of innovation">
                ${title!''}
              </a>
            </td>
            [#-- Cluster --]
            <td class="text-center">
              [#if innovation.project??]
                ${innovation.project.acronym!''}
              [/#if]
            </td>
            [#-- Type of Innovation --]
            <td class="text-center">
              [#if innovation.projectInnovationInfo?? && innovation.projectInnovationInfo.repIndInnovationType?? ]
                ${innovation.projectInnovationInfo.repIndInnovationType.name!''}
              [/#if]
            </td>
            [#-- Readiness Level --]
            <td class="text-center">
              [#if isScalingReadines]
                <span class="inno-scale inno-scale-${scaleReadiness}">[@utilities.tableText value=scaleReadiness /]</span>
                <span>${scalingInnovationTitles[scaleReadiness]!""}</span>
              [#else]
                [@utilities.tableText value=(innovation.projectInnovationInfo.readinessLevel.name)!"" /]
              [/#if]
            </td>
            [#-- Year --]
            <td class="text-center">
              [#if innovation.projectInnovationInfo?? && innovation.projectInnovationInfo.year??]
                ${innovation.projectInnovationInfo.year!''}
              [/#if]
            </td>
            [#local innovationSelected = false /]
            [#local textBtn = "Select" /]
            [#if selected?has_content]
              [#list selected as sel]
                [#if sel.selectedInnovation.id == innovation.id]
                  [#local innovationSelected = true /]
                  [#local textBtn = "Selected" /]
                [/#if]
              [/#list]
            [/#if]
            [#-- Actions --]
            <td class="text-center">
              [#-- Select --]
              [#local titleShow] [@s.text name=innovation.projectInnovationInfo.title /] [/#local]
              <button class="btn btn-primary selectInnovationBundle" data-id="${innovation.id!}" data-name="${titleShow!}" [#if innovationSelected]disabled[/#if]>${textBtn!""}</button>

              [#-- Download PDF --]
              <a href="[@s.url namespace="/summaries" action='${(crpSession)!}/projectInnovationSummary'][@s.param name='innovationID']${innovation.id?c}[/@s.param][@s.param name='phaseID']${(innovation.projectInnovationInfo.phase.id)!''}[/@s.param][/@s.url]" target="_blank" rel="noopener noreferrer">
                <img src="${baseUrlCdn}/global/images/pdf.png" height="25" title="[@s.text name="projectsList.downloadPDF" /]" />
              </a>
            </td>

            <td style="display: none"></td>
          </tr>
        [/#list]
      [/#if]
    </tbody>
  </table>
  <div class="clearfix"></div>
[/#macro]

[#macro innovationSelectedMacro element name index template=false editable=false]
  [#local customName = "${template?string('_TEMPLATE_', '')}${name}[${index}]"]
  <div class="innovationBundleItem grayGreenBox" id="innovationBundleItem-${template?string('template',index)}" style="display:${template?string('none','block')}">
      <input type="hidden" name="${customName}.id" value="${element.id!''}" />
      <input type="hidden" id="reference-selected" name="${customName}.selectedInnovation.id" value="${(element.selectedInnovation?? && element.selectedInnovation.id??)?then(element.selectedInnovation.id,'')}" />

      <span  title="Remove" class="removeInnovationBundleItem removeElement sm removeIcon" aria-hidden="true"></span>
      <p><b class="innovationBundleItemID">${(element.selectedInnovation?? && element.selectedInnovation.id??)?then(element.selectedInnovation.id,'')}</b> - <span class="innovationBundleItemName">${(element.selectedInnovation?? && element.selectedInnovation.projectInnovationInfo?? && element.selectedInnovation.projectInnovationInfo.title??)?then(element.selectedInnovation.projectInnovationInfo.title,'')}</span></p>
  </div>
[/#macro]

[#macro tablePRMSInnovation list selected={} ]

  [#-- Scaling Innovation Titles --]
  [#local scalingInnovationTitles = ["Idea", "Basic Research", "Formulation", "Proof of Concept", "Controlled Testing","Model/Early Prototype","Semi-Controlled Testing","Prototype","Uncontrolled Testing","Proven Innovation"]]
  
  <table id="table-prms-innovations" class="table table-striped">
    <thead>
      <tr>
        <th class="text-center">ID</th>
        <th width="65%">Title</th>
        <th width="15%">Readiness level</th>
        <th class="no-sort" width="15%">Action</th>
      </tr>
    </thead>
    <tbody>
      [#list list as innovation]

        [#local title]
          [#if ((innovation.title?has_content))]
            [@utilities.wordCutter string=(innovation.title)!"" maxPos=120 /]
          [#else]
            No provided
          [/#if]
        [/#local]

        [#-- Scaling Readiness value --]
        [#local scaleReadiness = (innovation.readinessLevelId)!"" ]

        [#local isScalingReadines = ((innovation.readinessLevelId?has_content))!false ]

        <tr>
          <td style="padding: 4px;">${innovation.prmsResultId!''}</td>
          <td title="${innovation.title!''}" style="padding: 4px;">${title!''}</td>
          [#-- Readiness Level --]
          <td class="text-center" style="padding: 4px;">
            [#if isScalingReadines]
              <span class="inno-scale inno-scale-${scaleReadiness}">[@utilities.tableText value=scaleReadiness /]</span>
              <span>${scalingInnovationTitles[scaleReadiness]!""}</span>
            [#else]
              [@utilities.tableText value=(innovation.readinessLevel.name)!"" /]
            [/#if]
          </td>
          [#assign textBtn = "Map" /]
          [#assign innovationSelected = false /]
          [#if selected?has_content]
            [#list selected as sel]
              [#if sel.PRMSInnovation.id == innovation.id]
                [#assign innovationSelected = true /]
                [#assign textBtn = "Mapped" /]
              [/#if]
            [/#list]
          [/#if]
          <td class="text-center" style="padding: 4px;">
            <button class="btn btn-primary selectInnovationPRMS" data-id="${innovation.id!''}" data-selected="${innovationSelected?then('true','false')}" style="opacity: ${innovationSelected?then('0.5','1')}">${textBtn!""}</button>
            <a href="${innovation.pdfLink}" target="_blank" rel="noopener noreferrer">
              <img src="${baseUrlCdn}/global/images/pdf.png" height="25" title="[@s.text name="projectsList.downloadPDF" /]" />
            </a>
          </td>
        </tr>
      [/#list]
    </tbody>
  </table>
[/#macro]

[#macro hiddenMapPRMSInnovations element name index template=false editable=false]
  [#local customName = "${template?string('_TEMPLATE_', '')}${name}[${index}]"]
  <div class="innovationMappedItem" id="innovationMappedItem-${template?string('template',index)}" style="display:none;" data-id="${(element.PRMSInnovation?has_content)?then(element.PRMSInnovation.id,'')}">
      <input type="hidden" name="${customName}.id" value="${element.id!''}" class="id-mapped" />
      <input type="hidden" class="reference-mapped" name="${customName}.PRMSInnovation.id" value="${(element.PRMSInnovation?has_content)?then(element.PRMSInnovation.id,'')}" />
  </div>
[/#macro]
