[#ftl]
[#assign title = "Cluster Description" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/projectDescription.js?20261112",
  "${baseUrlCdn}/global/js/autoSave.js?20210616",
  "${baseUrlCdn}/global/js/fieldsValidation.js"
  ]
/]
[#assign customCSS = [
  "${baseUrlMedia}/css/projects/projectDescription.css?20261112"
  ]
/]

[#assign currentStage = "description" /]
[#assign currentSection = "projects" /]
[#assign hideJustification = true /]
[#assign isCrpProject = (action.isProjectCrpOrPlatform(project.id))!false ]
[#assign isCenterProject = (action.isProjectCenter(project.id))!false ]
[#assign isNewCenterTypeProject = (action.isNewCenterType(project.id))!false ]

[#if !action.isAiccra()]
  [#assign breadCrumb = [
    {"label":"projectsList", "nameSpace":"${currentSection}", "action":"${(crpSession)!}/projectsList"},
    {"text":"P${project.id}", "nameSpace":"${currentSection}", "action":"${crpSession}/description", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
    {"label":"projectDescription", "nameSpace":"${currentSection}", "action":""}
  ] /]
[#else]
  [#assign breadCrumb = [
    {"label":"projectsList", "nameSpace":"clusters", "action":"${(crpSession)!}/projectsList"},
    {"text":"C${project.id}", "nameSpace":"clusters", "action":"${crpSession}/description", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
    {"label":"projectDescription", "nameSpace":"clusters", "action":""}
  ] /]
[/#if]

[#-- What the signed-in user can change here. The status stays editable on its own (editStatus) when the rest of
     the section is not, and the Title only for administrators. Admins and SuperAdmins keep editing the Title
     whenever the page can be saved (the same condition buttons-projects.ftl shows Save on); a read-only page offers
     no Save, so an open box there would only lose what is typed in it. --]
[#assign isAdministrative = (project.projectInfo.administrative)!false /]
[#assign pageCanSave = ((editable || action.canModifiedProjectStatus()) && !(transaction??))!false /]
[#assign canEditTitle = (pageCanSave && (action.isAdmin() || action.canAccessSuperAdmin()))!false /]
[#assign canEditStatus = (editable || editStatus)!false /]
[#assign canEditFlagships = (editable && action.hasPermission("flagships"))!false /]
[#assign canEditRegions = (editable && action.hasPermission("regions"))!false /]
[#assign isReadOnly = !editable && !canEditStatus /]
[#-- "project" / "cluster" / "team", per global unit --]
[#assign sectionNoun = (action.getText("projects.menu.status.noun"))!"project" /]
[#assign projectCode = action.isAiccra()?then("C", "P") + project.id?c /]
[#assign phaseLabel = (actualPhase.composedName)!"" /]
[#assign challengesYear = (project.projectInfo.endDate?date?string('yyyy'))!((actualPhase.year?c)!"") /]

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
        <div class="pdn[#if isReadOnly] is-readonly[/#if]" data-phase-year="${(actualPhase.year?c)!}">

          [#-- Header --]
          <div class="pdn-head">
            <h1 class="pdn-head__title">[@s.text name="projectDescription.title" /]</h1>
            <span class="pdn-chip">${projectCode}</span>
          </div>

          [#-- Instructions --]
          [#if !isReadOnly]
          <div class="pdn-note" role="note">
            <span class="pdn-note__icon" aria-hidden="true">i</span>
            <p class="pdn-note__text">
              [#if (project.projectInfo.isProjectEditLeader())!false]
                [#if (reportingActive)!false]
                  [@s.text name="projectDescription.help3" ][@s.param][@s.text name="global.managementLiaison" /][/@s.param][/@s.text]
                [#else]
                  [@s.text name="projectDescription.help2" ][@s.param][@s.text name="global.managementLiaison${isCenterProject?string('Center', '')}" /][/@s.param][/@s.text]
                [/#if]
              [#else]
                [@s.text name="projectDescription.help1" /]
              [/#if]
            </p>
          </div>
          [/#if]

          <div id="projectDescription" class="pdn-sections">

            [#-- ── Identification ── --]
            <section class="pdn-card" aria-labelledby="pdn-identification-title">
              <div class="pdn-card__head">
                <h2 class="pdn-card__title" id="pdn-identification-title">[@s.text name="projectDescription.redesign.card.identification" /]</h2>
                [#if editable && !canEditTitle && isCrpProject && !action.hasPermission("managementLiaison")]
                  <span class="pdn-card__aside">
                    <svg width="12" height="12" viewBox="0 0 16 16" fill="none" aria-hidden="true"><rect x="3.2" y="7" width="9.6" height="6.6" rx="1.6" stroke="currentColor" stroke-width="1.5"></rect><path d="M5.6 7V5.2a2.4 2.4 0 0 1 4.8 0V7" stroke="currentColor" stroke-width="1.5"></path></svg>
                    [@s.text name="projectDescription.redesign.lockNote"][@s.param][@s.text name="project.liaisonInstitution" /][/@s.param][/@s.text]
                  </span>
                [/#if]
              </div>

              [#-- Project Title --]
              <div class="pdn-field[#if !canEditTitle] pdn-field--locked[/#if]">
                [@customForm.textArea name="project.projectInfo.title" i18nkey="project.title" required=true className="project-title limitWords-30" editable=canEditTitle /]
              </div>

              [#if isCrpProject]
              <div class="pdn-grid pdn-grid--2">
                [#-- Management liaison --]
                <div class="pdn-field">
                  [#if editable && action.hasPermission("managementLiaison")]
                    [@customForm.select name="project.projectInfo.liaisonInstitution.id" className="liaisonInstitutionSelect" i18nkey="project.liaisonInstitution" disabled=!editable listName="liaisonInstitutionLabels" required=true editable=true /]
                  [#else]
                    [#assign currentLiaisonId = (project.projectInfo.liaisonInstitution.id?c)!"" /]
                    [#assign currentLiaisonLabel = (liaisonInstitutionLabels[currentLiaisonId])!((project.projectInfo.liaisonInstitution.composedName)!"") /]
                    <label class="readOnly">[@s.text name="project.liaisonInstitution" /]</label>
                    [#if currentLiaisonLabel?has_content]
                      [#-- The labels read "[Group] Name": the group shows as a tag before the name --]
                      [#if currentLiaisonLabel?starts_with("[") && currentLiaisonLabel?contains("]")]
                        [#assign liaisonTag = currentLiaisonLabel?keep_after("[")?keep_before("]") /]
                        [#assign liaisonName = currentLiaisonLabel?keep_after("]")?trim /]
                      [#else]
                        [#assign liaisonTag = "" /]
                        [#assign liaisonName = currentLiaisonLabel /]
                      [/#if]
                      <p class="pdn-locked">[#if liaisonTag?has_content]<span class="pdn-tag">${liaisonTag}</span>[/#if]${liaisonName}</p>
                    [#else]
                      <p class="pdn-empty">[@s.text name="form.values.fieldEmpty" /]</p>
                    [/#if]
                  [/#if]
                </div>

                [#-- Cluster type: hidden for the administrative cluster --]
                [#if !isAdministrative]
                  [#assign currentClusterTypeId = (project.projectInfo.clusterType.id?c)!"-1" /]
                  <div class="pdn-field">
                    <span class="pdn-label" id="pdn-clusterType-label">[@s.text name="project.clusterType" /][#if editable]<span class="requiredTag">*</span>[/#if]</span>
                    [#if editable]
                      [@s.fielderror cssClass="fieldError" fieldName="project.projectInfo.clusterType.id"/]
                      [#-- One value for the form, the segmented control only sets it. "-1" keeps the validator's
                           "no type selected" rule: radios with nothing checked would post nothing at all. --]
                      <input type="hidden" name="project.projectInfo.clusterType.id" value="${currentClusterTypeId}" data-pdn-type-value />
                    [/#if]
                    [#if clusterTypes?has_content]
                      <div class="pdn-seg projectprojectInfoclusterTypeid ${customForm.changedField('project.projectInfo.clusterType.id')}" role="radiogroup" aria-labelledby="pdn-clusterType-label"[#if !editable] aria-readonly="true"[/#if]>
                        [#list clusterTypes as clusterType]
                          [#assign isCurrentType = (clusterType.id?c == currentClusterTypeId) /]
                          <button type="button" class="pdn-seg__btn[#if isCurrentType] is-on[/#if]" role="radio" aria-checked="${isCurrentType?string}" data-pdn-type="${clusterType.id?c}"[#if !editable] disabled[/#if]>${(clusterType.name)!}</button>
                        [/#list]
                      </div>
                    [#else]
                      <p class="pdn-empty">[@s.text name="form.values.fieldEmpty" /]</p>
                    [/#if]
                  </div>
                [/#if]

                [#if action.hasSpecificities('previous_project_id_field_active') ]
                  <div class="pdn-field">
                    [@customForm.input name="project.projectInfo.previousProjectId" i18nkey="project.previousId" required=false className="previousID" editable=editable && action.hasPermission("title") /]
                  </div>
                [/#if]
              </div>
              [/#if]

              [#if isCenterProject]
              <div class="pdn-grid pdn-grid--2">
                [#-- CENTER Research program --]
                <div class="pdn-field researchProgram">
                  [#if editable]
                    [@customForm.select name="project.projectInfo.liaisonInstitution.id"
                      listName="liaisonInstitutions"
                      paramText="${currentCrp.acronym}"
                      keyFieldName="id"
                      displayFieldName="composedName"
                      i18nkey="project.researchProgram"
                      className="liaisonInstitutionSelect"
                      help="project.researchProgram.help"
                      required=true
                      editable=true /]
                  [#else]
                    <label class="readOnly">[@s.text name="project.researchProgram" /]</label>
                    <p class="pdn-locked">${(project.projectInfo.liaisonInstitution.composedName)!action.getText("label.not.available")}</p>
                  [/#if]
                </div>
                [#if action.hasSpecificities('previous_project_id_field_active') ]
                  <div class="pdn-field">
                    [@customForm.input name="project.projectInfo.previousProjectId" i18nkey="project.previousId" required=false className="previousID" editable=editable && action.hasPermission("title") /]
                  </div>
                [/#if]
              </div>
              [/#if]

              [#-- Dates --]
              <div class="pdn-grid pdn-grid--2">
                <div class="pdn-field">
                  [@customForm.input name="project.projectInfo.startDate" className="startDate" i18nkey="project.startDate" type="text" disabled=!editable  required=true editable=editable && action.hasPermission("startDate")  /]
                </div>
                <div class="pdn-field">
                  [@customForm.input name="project.projectInfo.endDate" className="endDate"  i18nkey="project.endDate" type="text" disabled=!editable required=true editable=editable && action.hasPermission("endDate")  /]
                  <p class="pdn-fieldError" data-pdn-order-error hidden>[@s.text name="projectDescription.redesign.endDate.beforeStart" /]</p>
                  <p class="pdn-fieldError" data-pdn-end-error hidden>[@s.text name="projectDescription.redesign.endDate.invalid"][@s.param]${(actualPhase.year?c)!}[/@s.param][/@s.text]</p>
                  <p class="pdn-hint" data-pdn-duration hidden></p>
                </div>
              </div>
            </section>

            [#-- ── Narrative ── --]
            <section class="pdn-card" aria-labelledby="pdn-narrative-title">
              <h2 class="pdn-card__title" id="pdn-narrative-title">[@s.text name="projectDescription.redesign.card.narrative" /]</h2>

              [#-- Project Summary --]
              <div class="pdn-field">
                [@customForm.textArea name="project.projectInfo.summary"  i18nkey="project.summary" required=true className="project-description limitWords-250" editable=editable && action.hasPermission("summary") /]
              </div>

              [#-- Project Challenges, causes and proposed solutions. The guidance opens on demand: it is long and the
                   same every phase. projectDescription.js moves the panel under the field's label. --]
              <div class="pdn-field pdn-field--guide">
                [@customForm.textArea name="project.projectInfo.challengesSolutions"  i18nkey="project.challengesAndSolutions" required=true className="project-description limitWords-250" editable=editable /]
                [#if editable]
                  <button type="button" class="pdn-link pdn-guide__toggle" aria-expanded="false" aria-controls="pdn-challenges-guide"
                    data-label-show="[@s.text name="projectDescription.redesign.guidance.show" /]" data-label-hide="[@s.text name="projectDescription.redesign.guidance.hide" /]">[@s.text name="projectDescription.redesign.guidance.show" /]</button>
                  <div class="pdn-guide" id="pdn-challenges-guide" hidden>[@s.text name="project.challengesAndSolutions.helpText"][@s.param]${challengesYear}[/@s.param][/@s.text]</div>
                [/#if]
              </div>
            </section>

            [#-- ── Status: what the reporting cycle asks the cluster to confirm ── --]
            [#assign statusFocus = (reportingActive && canEditStatus)!false /]
            <section class="pdn-card" aria-labelledby="pdn-status-title">
              <div class="pdn-card__head pdn-card__head--start">
                <h2 class="pdn-card__title" id="pdn-status-title">[@s.text name="projectDescription.redesign.card.status" /]</h2>
                [#if statusFocus]
                  <span class="pdn-badge">[@s.text name="projectDescription.redesign.status.review"][@s.param]${phaseLabel}[/@s.param][/@s.text]</span>
                [/#if]
              </div>
              [#if statusFocus]
                <p class="pdn-card__lead">[@s.text name="projectDescription.redesign.status.reviewNote"][@s.param]${sectionNoun}[/@s.param][/@s.text]</p>
              [/#if]
              <div class="pdn-grid pdn-grid--2">
                <div class="pdn-field pdn-field--status">
                  [#if canEditStatus]
                    [@customForm.select name="project.projectInfo.status" value="${(project.projectInfo.status)!}" i18nkey="project.status" className="description_project_status" listName="projectStatuses" header=false required=true editable=true /]
                  [#else]
                    [#assign statusText = "" /]
                    [#list projectStatuses as statusId, statusName]
                      [#if statusId?number == ((project.projectInfo.status)!-1)]
                        [#assign statusText = statusName /]
                      [/#if]
                    [/#list]
                    <label class="readOnly">[@s.text name="project.status" /]</label>
                    [#if statusText?has_content]
                      <p class="pdn-status" data-status="${(project.projectInfo.status)!}"><span class="pdn-status__dot" aria-hidden="true"></span>${statusText}</p>
                    [#else]
                      <p class="pdn-empty">[@s.text name="form.values.fieldEmpty" /]</p>
                    [/#if]
                  [/#if]
                </div>
              </div>
              <div id="statusDescription" class="pdn-subpanel" style="display:${((project.projectInfo.statusJustificationRequired)!false)?string('block','none')}">
                [@customForm.textArea name="project.projectInfo.statusJustification" i18nkey="project.statusJustification" required=true className="project-statusJustification limitWords-100" editable=canEditStatus /]
              </div>
            </section>

            [#-- ── Results framework: flagships / components and regional programs ── --]
            [#if !isAdministrative]
            [#if (programFlagships)?has_content || (regionFlagships)?has_content]
            <section class="pdn-card" aria-labelledby="pdn-framework-title">
              <div class="pdn-card__intro">
                <h2 class="pdn-card__title" id="pdn-framework-title">[@s.text name="projectDescription.redesign.card.framework" /]</h2>
                [#if regionFlagships?has_content]
                  <p class="pdn-card__lead">[@customForm.text name="projectDescription.projectWorkingWithRegions${isCenterProject?string('Center','')}" readText=!editable /]</p>
                [#else]
                  <p class="pdn-card__lead">[@customForm.text name="projectDescription.projectWorking${isCenterProject?string('Center','')}" readText=!editable /]</p>
                [/#if]
              </div>

              <div id="projectWorking" class="pdn-framework[#if !(programFlagships?has_content && regionFlagships?has_content)] pdn-framework--single[/#if]">
                [#-- Flagships: only shown (and required) when there are elements in the list --]
                [#if (programFlagships)?has_content]
                  <div id="projectFlagshipsBlock" listname="project.flagshipValue" class="pdn-group ${customForm.changedField('project.flagshipValue')}">
                    <label class="pdn-group__title">[@s.text name="projectDescription.flagships${isCenterProject?string('Center','')}" /][@customForm.req required=canEditFlagships /]</label>
                    [#if canEditFlagships]
                      [@s.fielderror cssClass="fieldError" fieldName="project.flagshipValue"/]
                      [#assign anyLocked = false /]

                      [#-- Contributions allowed to this flagship --]
                      [#list (programFlagships)![] as element]
                        [#assign outcomesContributions = (action.getContributionsOutcome(project.id, element.id))![] /]
                        [#assign clustersContributions = (action.getClusterOutcome(project.id, element.id))![] /]
                        [#assign totalContributions = outcomesContributions?size + clustersContributions?size /]
                        [#if (totalContributions != 0)]
                          [#assign anyLocked = true /]
                          [#-- Locked: it stays selected while it has contributions. The hidden input keeps it in
                               the saved list; without it a save would unlink the flagship. --]
                          <div class="pdn-check is-on is-locked checkDisable">
                            <span class="pdn-check__box" aria-hidden="true"></span>
                            <span class="pdn-check__text">
                              [@programName element=element /]
                              <span class="pdn-check__caption">
                                <svg width="11" height="11" viewBox="0 0 16 16" fill="none" aria-hidden="true"><rect x="3.2" y="7" width="9.6" height="6.6" rx="1.6" stroke="currentColor" stroke-width="1.6"></rect><path d="M5.6 7V5.2a2.4 2.4 0 0 1 4.8 0V7" stroke="currentColor" stroke-width="1.6"></path></svg>
                                [@s.text name="projectDescription.redesign.flagships.locked" /]
                              </span>
                            </span>
                            [#if outcomesContributions?size > 0][@outcomesRelationsPopup element outcomesContributions clustersContributions /][/#if]
                            <input type="hidden" class="defaultChecked" name="project.flagshipValue" value="${element.id}"/>
                          </div>
                        [#else]
                          [#assign isChecked = ((flagshipIds?seq_contains(element.id))!false) /]
                          <label class="pdn-check[#if isChecked] is-on[/#if]" for="projectFp-${element.id}">
                            <input id="projectFp-${element.id}" class="pdn-check__input checkbox-input fpInput ${isCenterProject?string('getCenterOutcomes','')}" type="checkbox" name="project.flagshipValue" value="${element.id}"[#if isChecked] checked[/#if] />
                            <span class="pdn-check__box" aria-hidden="true"></span>
                            <span class="pdn-check__text">[@programName element=element /]</span>
                          </label>
                        [/#if]
                      [/#list]
                      [#-- Printed after the list so the contributions are read once; CSS places it under the title. --]
                      [#if anyLocked]
                        <p class="pdn-hint pdn-group__note">[@s.text name="projectDescription.redesign.flagships.lockedNote" /]</p>
                      [/#if]
                    [#else]
                      [#-- No permission: the saved selection travels back unchanged --]
                      <input type="hidden" name="project.flagshipValue" value="${(project.flagshipValue)!}"/>
                      [@readOnlyChoices list=(programFlagships)![] selectedIds=(flagshipIds)![] /]
                    [/#if]
                  </div>
                [#else]
                  <input type="hidden" name="project.flagshipValue" value="${(project.flagshipValue)!}"/>
                [/#if]

                [#-- Regions --]
                [#if regionFlagships?has_content]
                  <div id="projectRegionsBlock" listname="project.regionsValue" class="pdn-group ${customForm.changedField('project.regionsValue')}">
                    <label class="pdn-group__title">[@s.text name="projectDescription.regions${isCenterProject?string('Center','')}" /][@customForm.req required=canEditRegions /]</label>
                    [#if canEditRegions]
                      [@s.fielderror cssClass="fieldError" fieldName="project.regionsValue"/]
                      [#list (regionFlagships)![] as element]
                        [#assign isChecked = ((regionsIds?seq_contains(element.id))!false) /]
                        <label class="pdn-check[#if isChecked] is-on[/#if]" for="projectRegion-${element.id}">
                          <input id="projectRegion-${element.id}" class="pdn-check__input checkbox-input checkboxInput rpInput" type="checkbox" name="project.regionsValue" value="${element.id}"[#if isChecked] checked[/#if] />
                          <span class="pdn-check__box" aria-hidden="true"></span>
                          <span class="pdn-check__text">[@programName element=element regional=true /]</span>
                        </label>
                      [/#list]
                      [#if isCrpProject]
                        [#assign noRegional = (project.projectInfo.noRegional)!false /]
                        <span class="pdn-group__divider" aria-hidden="true"></span>
                        <label class="pdn-check[#if noRegional] is-on[/#if]" for="projectNoRegional">
                          <input id="projectNoRegional" class="pdn-check__input checkbox-input checkboxInput" type="checkbox" name="project.projectInfo.noRegional" value="true"[#if noRegional] checked[/#if] />
                          <span class="pdn-check__box" aria-hidden="true"></span>
                          <span class="pdn-check__text">
                            <span class="pdn-check__name">[@s.text name="project.noRegional" /]</span>
                            <span class="pdn-check__caption">[@s.text name="projectDescription.redesign.noRegional.hint" /]</span>
                          </span>
                        </label>
                      [/#if]
                    [#else]
                      [#if (project.projectInfo.getNoRegional())!false ]
                        <input type="hidden" name="project.projectInfo.noRegional" value="true" />
                      [/#if]
                      <input type="hidden" name="project.regionsValue" value="${(project.regionsValue)!}"/>
                      [@readOnlyChoices list=(regionFlagships)![] selectedIds=(regionsIds)![] regional=true /]
                      [#if isCrpProject]
                        <span class="pdn-group__divider" aria-hidden="true"></span>
                        <span class="pdn-check is-static[#if (project.projectInfo.getNoRegional())!false] is-on[/#if]">
                          <span class="pdn-check__box" aria-hidden="true"></span>
                          <span class="pdn-check__text"><span class="pdn-check__name">[@s.text name="project.noRegional" /]</span></span>
                        </span>
                      [/#if]
                    [/#if]
                  </div>
                [/#if]
              </div>
            </section>
            [#else]
              <input type="hidden" name="project.flagshipValue" value="${(project.flagshipValue)!}"/>
            [/#if]
            [/#if]

            [#if isCenterProject]
            [#-- CENTER Reserach Outcomes (SA Version) --]
            <section class="pdn-card">
              <div class="pdn-field">
                [@customForm.elementsListComponent name="project.centerOutcomes" elementType="centerOutcome" elementList=project.centerOutcomes label="projectDescription.researchOutcomes" listName="centerOutcomes" keyFieldName="id" displayFieldName="listComposedName" required=editable /]
              </div>
            </section>
            [/#if]

          </div>

          [#-- Save bar: save state and the shared last-edit message on the left, the buttons on the right.
               projectDescription.js moves the message out of the buttons include and tracks unsaved changes. --]
          <div class="pdn-saveBar">
            <div class="pdn-saveBar__info">
              [#if canEditStatus]
                <span class="pdn-saveBar__state" role="status" aria-live="polite">
                  <span class="pdn-saveBar__dot" aria-hidden="true"></span>
                  <span data-pdn-save-state
                    data-saved="[@s.text name="projectDescription.redesign.save.clean" /]"
                    data-unsaved="[@s.text name="projectDescription.redesign.save.dirty" /]">[@s.text name="projectDescription.redesign.save.clean" /]</span>
                </span>
              [/#if]
            </div>
            <div class="pdn-saveBar__actions">
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

[#-- Copy the script builds at runtime, so it stays i18n-keyed. --]
<span id="pdn-i18n" hidden
  data-duration="[@s.text name="projectDescription.redesign.duration"][@s.param]{0}[/@s.param][@s.param]{1}[/@s.param][/@s.text]"
  data-duration-years="[@s.text name="projectDescription.redesign.duration.years"][@s.param]{0}[/@s.param][/@s.text]"
  data-duration-months="[@s.text name="projectDescription.redesign.duration.months"][@s.param]{0}[/@s.param][/@s.text]"
  data-placeholder="[@s.text name="projectDescription.redesign.date.placeholder" /]"
  data-prev-year="[@s.text name="projectDescription.redesign.date.prevYear" /]"
  data-next-year="[@s.text name="projectDescription.redesign.date.nextYear" /]"></span>

<span id="liaisonInstitutionsPrograms" style="display:none">{[#list liaisonInstitutions as li]"${li.id}" : ${(li.crpProgram.id)!-1}[#if li_has_next], [/#if][/#list]}</span>


[#-- Cluster of activity list template --]
<ul style="display:none">
  <li id="cpListTemplate" class="clusterActivity clearfix">
    <span class="listButton remove pull-right">[@s.text name="form.buttons.remove" /]</span>
    <input class="id" type="hidden" name="project.clusterActivities[-1].crpClusterOfActivity.id" value="" />
    <input class="cid" type="hidden" name="project.clusterActivities[-1].id" value="" />
    <span class="name"></span>
    <div class="clearfix"></div>
    <ul class="leaders"></ul>
  </li>
</ul>

[#-- project scope template --]
<ul style="display:none">
  <li id="projecScope-template" class="projecScope clearfix">
    <span class="listButton remove pull-right">[@s.text name="form.buttons.remove" /]</span>
    <input class="id" type="hidden" name="project.scopes[-1].locElementType.id" value="" />
    <input class="cid" type="hidden" name="project.scopes[-1].id" value="" />
    <span class="name"></span>
    <div class="clearfix"></div>
    <ul class="leaders"></ul>
  </li>
</ul>

[#-- Remove project contribution popup --]
<div id="removeContribution-dialog" title="Remove  [@s.text name="global.clusterOfActivities" /]" style="display:none">
  <ul class="messages"><li>[@s.text name="projectDescription.removeCoADialog" /]</li></ul>
</div>


[@customForm.inputFile name="fileReporting" template=true /]

[#include "/WEB-INF/global/pages/footer.ftl"]


[#-- A flagship / region as the design's "CODE · Name" line --]
[#macro programName element regional=false]
  [#if isCrpProject || isNewCenterTypeProject || regional]
    [#local programAcronym = (element.acronym)!"" /]
    [#local programTitle = (element.name)!"" /]
  [#else]
    [#local programAcronym = "" /]
    [#local programTitle = (element.centerComposedName)!((element.name)!"") /]
  [/#if]
  <span class="pdn-check__name">[#if programAcronym?has_content]<span class="pdn-check__code">${programAcronym}</span>[#if programTitle?has_content] · [/#if][/#if]${programTitle}</span>
[/#macro]

[#-- Read-only choice list: every option shown, the saved ones marked, nothing reacts --]
[#macro readOnlyChoices list selectedIds regional=false]
  [#list list as element]
    [#local isChecked = (selectedIds?seq_contains(element.id))!false /]
    <span class="pdn-check is-static[#if isChecked] is-on[/#if]">
      <span class="pdn-check__box" aria-hidden="true"></span>
      <span class="pdn-check__text">[@programName element=element regional=regional /]</span>
    </span>
  [/#list]
[/#macro]

[#macro outcomesRelationsPopup  element outcomesContributions clustersContributions]
  [#-- Button --]
  <button type="button" class="pdn-countPill" data-toggle="modal" data-target="#modal-outcomesContributions-${element.id}"
    aria-label="[@s.text name="projectDescription.redesign.contributions.open"][@s.param]${outcomesContributions?size}[/@s.param][@s.param]${(element.acronym)!((element.name)!"")}[/@s.param][/@s.text]">
    ${outcomesContributions?size}
    <svg width="11" height="11" viewBox="0 0 12 12" fill="none" aria-hidden="true"><path d="M4.5 2.5 8 6l-3.5 3.5" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"></path></svg>
  </button>

  [#-- Modal --]
  <div class="modal fade pdn-modal" id="modal-outcomesContributions-${element.id}" tabindex="-1" role="dialog" aria-labelledby="modal-outcomesContributions-${element.id}-title">
    <div class="modal-dialog modal-lg" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-label="[@s.text name="projectDescription.redesign.contributions.close" /]"><span aria-hidden="true">&times;</span></button>
          <h4 class="modal-title" id="modal-outcomesContributions-${element.id}-title">
            [@s.text name="projectDescription.redesign.contributions.title"][@s.param]${sectionNoun}[/@s.param][/@s.text]
            <small>${(element.composedName)!}</small>
          </h4>
        </div>
        <div class="modal-body">
          [#-- Outcomes table --]
          [#if outcomesContributions?has_content]
          <table class="table">
            <thead>
              <tr>
                <th scope="col">[@s.text name="projectDescription.redesign.contributions.outcome" /]</th>
                <th scope="col">[@s.text name="projectDescription.redesign.contributions.targetUnit" /]</th>
                <th scope="col">[@s.text name="projectDescription.redesign.contributions.year" /]</th>
                <th scope="col"><span class="sr-only">[@s.text name="projectDescription.redesign.contributions.openLink" /]</span></th>
              </tr>
            </thead>
            <tbody>
              [#list outcomesContributions as oc]
                [#local projectUrl][@s.url namespace="/projects" action="${(crpSession)!}/contributionsCrpList"][@s.param name='projectID']${projectID}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
                <tr>
                  <td>${(oc.description)!'None'}</td>
                  <td>${(oc.value)!} ${(oc.srfTargetUnit.name)!'None'}</td>
                  <td>${(oc.year)!'None'}</td>
                  <td><a href="${projectUrl}" target="_blank" rel="noopener noreferrer" aria-label="[@s.text name="projectDescription.redesign.contributions.openLink" /]"><span class="glyphicon glyphicon-new-window" aria-hidden="true"></span></a></td>
                </tr>
              [/#list]
            </tbody>
          </table>
          [/#if]

          [#-- Cluters of Activities table --]
          [#if clustersContributions?has_content]
          <table class="table">
            <thead>
              <tr>
                <th scope="col">[@s.text name="global.clusterOfActivities" /]</th>
              </tr>
            </thead>
            <tbody>
              [#list clustersContributions as cc]
                <tr>
                  <td>${(cc.description)!'None'}</td>
                </tr>
              [/#list]
            </tbody>
          </table>
          [/#if]
        </div>
        <div class="modal-footer">
          <button type="button" class="pdn-btn pdn-btn--ghost" data-dismiss="modal">[@s.text name="projectDescription.redesign.contributions.close" /]</button>
        </div>
      </div>
    </div>
  </div>
[/#macro]
