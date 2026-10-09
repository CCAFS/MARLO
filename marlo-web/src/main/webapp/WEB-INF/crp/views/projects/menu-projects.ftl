[#ftl]
[#assign isCrpProject = (action.isProjectCrpOrPlatform(project.id))!false ]
[#assign isCenterProject = (action.isProjectCenter(project.id))!false ]
[#assign isGlobalUnitProject = (centerGlobalUnit && isCenterProject) || (!centerGlobalUnit && isCrpProject) /]
[#if project.projectInfo?has_content && project.projectInfo.clusterType?has_content && project.projectInfo.clusterType.id?has_content && project.projectInfo.clusterType.id == 1]
  [#assign isCountryCluster = true]
[#else]
   [#assign isCountryCluster = false]
[/#if]
[#if project.projectInfo?has_content && project.projectInfo.clusterType?has_content && project.projectInfo.clusterType.id?has_content && project.projectInfo.clusterType.id == 4]
  [#assign isRegionalCluster = true]
[#else]
   [#assign isRegionalCluster = false]
[/#if]
[#if !((project.projectInfo.isProjectEditLeader())!false)]
  [#assign menus= [
    { 'title': 'General Information', 'show': true,
      'items': [
      { 'slug': 'description',  'name': 'projects.menu.description',  'action': 'description',  'active': true  },
      { 'slug': 'partners',  'name': 'projects.menu.partners',  'action': 'partners',  'active': true  },
      { 'slug': 'budgetByPartners',  'name': 'Budget',  'action': 'budgetByPartners',  'active': false, 'show': false  },
      { 'slug': 'budgetByFlagships',  'name': 'projects.menu.budgetByFlagships',  'action': 'budgetByFlagship',  'active': false , 'show': false  }
      [#--  { 'slug': 'budgetByFlagships',  'name': 'projects.menu.budgetByFlagships',  'action': 'budgetByFlagship',  'active': true , 'show': action.getCountProjectFlagships(project.id) && !reportingActive && isCrpProject}  --]
      ]
    }
    
  ]/]
[#else]
  [#assign menus= [
    { 'title': '${currentCrp.acronym} Mapping', 'show': centerGlobalUnit && isCrpProject,
      'items': [
      { 'slug': 'centerProgram',  'name': 'projects.menu.centerProgram',  'action': 'centerProgram',  'active': true, "showCheck": true  }
      ]
    },
    { 'title': 'General Information', 'show': true,
      'items': [
      { 'slug': 'description',  'name': 'projects.menu.description',  'action': 'description',  'active': true, "showCheck": isGlobalUnitProject},
      { 'slug': 'partners',  'name': 'projects.menu.partners',  'action': 'partners',  'active': true, "showCheck": isGlobalUnitProject },
      { 'slug': 'locations',  'name': 'projects.menu.locations',  'action': 'locations',  'active': true, "showCheck": isGlobalUnitProject  }
      ]
    },
    { 'title': 'Indicators', 'show': isCrpProject,
      'items': [
      { 'slug': 'contributionsCrpList',  'name': 'projects.menu.contributionsCrpList',  'action': 'contributionsCrpList',  'active': true, 'show':!phaseOne  && ((!project.projectInfo.administrative)!false) , "showCheck": isGlobalUnitProject},
      { 'slug': 'contributionsLP6',  'name': 'projects.menu.contributionLP6',  'action': 'contributionsLP6',  'active': true, 'show': action.hasSpecificities('crp_lp6_active') && reportingActive && ((action.getProjectLp6ContributionValue(project.id, actualPhase.id))!false), "showCheck": isGlobalUnitProject},
      { 'slug': 'projectOutcomes',  'name': 'projects.menu.projectOutcomes',  'action': 'outcomesPandR',  'active': true, 'show':  phaseOne && !project.projectInfo.administrative , "showCheck": isGlobalUnitProject},
      { 'slug': 'ccafsOutcomes',  'name': 'projects.menu.ccafsOutcomes',  'action': 'ccafsOutcomes',  'active': true, 'show': phaseOne && !project.projectInfo.administrative , "showCheck": isGlobalUnitProject },
      { 'slug': 'projectPolicies',  'name': 'projects.menu.policies',           'action': 'policies',  'active': true, 'show': (reportingActive || upKeepActive) && !aiccra, "showCheck": isGlobalUnitProject, "development": false},
      { 'slug': 'projectStudies',  'name': 'projects.menu.expectedStudies',  'action': 'studies',  'active': true, 'show': !centerGlobalUnit && !reportingActive && Aiccra , "showCheck": isGlobalUnitProject },
      { 'slug': 'projectStudies',  'name': 'projects.menu.studies',           'action': 'studies',  'active': true, 'show': reportingActive && Aiccra, "showCheck": isGlobalUnitProject, "development": false }
      ]
    },
    { 'title': 'Outputs', 'show': true,
      'items': [
      { 'slug': 'overviewByMogs',  'name': 'projects.menu.overviewByMogs',  'action': 'outputs',  'active': true, 'show' : phaseOne && isCrpProject , "showCheck": isGlobalUnitProject},
      { 'slug': 'deliverableList',  'name': 'projects.menu.deliverables',  'action': 'deliverableList',  'active': true , "showCheck": isGlobalUnitProject, "development": false },
      { 'slug': 'innovations',  'name': 'projects.menu.innovations',  'action': 'innovationsList',  'active': action.hasSpecificities("innovation_section_active"),'show': action.hasSpecificities("innovation_section_active") &&((reportingActive || upKeepActive) && isCrpProject) , "showCheck": isGlobalUnitProject, "development": false },
      { 'slug': 'highlights',  'name': 'Project Highlights',  'action': 'highlights',  'active': true ,'show': reportingActive && isCrpProject && action.hasSpecificities("crp_view_highlights") && !aiccra, "showCheck": isGlobalUnitProject }
      ]
    },
    { 'title': 'Activities', 'show': action.hasSpecificities(action.crpActivitesModule()),
      'items': [
      { 'slug': 'activities',  'name': 'projects.menu.activities',  'action': 'activities',  'active': true  ,'show': true, "showCheck": isGlobalUnitProject }
      ]
    },
    { 'title': 'Budget', 'show': false,
      'items': [
      { 'slug': 'budgetByPartners',  'name': 'projects.menu.budgetByPartners',  'action': 'budgetByPartners',  'active': false, 'show':false, "showCheck": false },      
      { 'slug': 'budgetByFlagships',  'name': 'projects.menu.budgetByFlagships',  'action': 'budgetByFlagship',  'active': false, 'show': action.getCountProjectFlagships(project.id) && !reportingActive && isCrpProject, "showCheck": false},
      { 'slug': 'leverages',  'name': 'Leverages',  'action': 'leverages',  'active': true, 'show': reportingActive && action.hasSpecificities("crp_leverages_module") && isCrpProject, "showCheck": isGlobalUnitProject}
      ]
    },
    { 'title': 'Safeguards', 'show':(UpKeepActive || reportingActive) && !project.projectInfo.administrative && (isCountryCluster || isRegionalCluster),
      'items': [
      { 'slug': 'safeguards',  'name': 'projects.menu.safeguards',  'action': 'safeguards',  'active': true  ,'show': (UpKeepActive || reportingActive) && !project.projectInfo.administrative && (isCountryCluster || isRegionalCluster), "showCheck": true, "development": false }
      ]
    },
    { 'title': 'Feedback', 'show': action.hasSpecificities(action.feedbackModule()),
      'items': [
      { 'slug': 'feedback',  'name': 'projects.menu.feedback',  'action': 'feedback',  'active': true  ,'show': action.hasSpecificities(action.feedbackModule()), "showCheck": false }
      ]
    }
    
  ]/]
[/#if]

[#assign submission = (action.isProjectSubmitted(projectID))!false /]
[#assign canSubmit = (action.hasPersmissionSubmit(projectID))!false /]
[#assign canUnSubmit = ((action.hasPersmissionUnSubmit(projectID))!false)/]


[#assign sectionsForChecking = [] /]


[#-- Menu--]
<nav id="secondaryMenu" class="clusterMenu">
  <p>[@s.text name="projects.menu.project" /]<br />
    <small> 
    [#-- Global Unit Acronym --]
    ${(project.projectInfo.phase.crp.acronym)!}
    [#-- Project Type --]
    [#if (project.projectInfo.administrative)!false][@s.text name="project.Management" /] [#else] [@s.text name="project.Research" /] [/#if]
    </small>
  </p> 
  <ul>
    [#assign sectionsChecked = 0 /]
    [#-- Display-only completeness counters for the status card below the menu. They
         count only the rows that carry a status badge (showCheck), so sections such as
         Feedback never weigh on "X of Y sections". sectionsChecked / completed keep
         their own rules: the submit flow depends on them. --]
    [#assign trackedTotal = 0 /]
    [#assign trackedDone = 0 /]
    [#list menus as menu]
      [#if menu.show]
      <li>
        <ul><p class="menuTitle">${menu.title}</p>
          [#list menu.items as item]
            [#if (item.showCheck)!true]
            [#assign submitStatus = false /]
              [#if item.action?has_content && projectID?has_content]
                  [#assign submitStatus = (action.getProjectSectionStatus(item.action, projectID))!false /]
              [#else]
                  [#assign submitStatus = false /]
              [/#if]
            [/#if]
            [#assign hasDraft = (action.getAutoSaveFilePath(project.class.simpleName, item.action, project.id))!false /]
            [#if (item.show)!true ]
              [#assign isTracked = ((item.showCheck)!true) && item.active /]
              [#if isTracked]
                [#assign trackedTotal = trackedTotal + 1 /]
                [#if submitStatus][#assign trackedDone = trackedDone + 1 /][/#if]
              [/#if]
              <li id="menu-${item.action}" [#if isTracked]data-tracked="true"[/#if] class="${hasDraft?string('draft', '')} [#if item.slug == currentStage]currentSection[/#if] [#if (item.hasBackground)!false]hasBackground[/#if] [#if (item.showCheck)!true] ${submitStatus?string('submitted','toSubmit')} [/#if] ${(item.active)?string('enabled','disabled')}">
                <a href="[@s.url action="${crpSession}/${item.action}"][@s.param name="projectID" value=projectID /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" onclick="return ${item.active?string}" class="action-${crpSession}/${item.action}">
                  [#if (item.icon?has_content)!false][@menuIcon name="${item.icon}" width="15px" height="15px" /][/#if]
                  [#-- Name --]
                  [@s.text name=item.name/]
                  [#if (item.development)!false][@utils.underConstruction title="global.underConstruction" width="20px" height="20px" /][/#if]
                </a>
              </li>
              [#if item.active]
                [#if submitStatus][#assign sectionsChecked = sectionsChecked + 1 /][/#if]
                [#assign sectionsForChecking = sectionsForChecking + ["${item.action}"] /]
              [/#if]
            [/#if]
          [/#list] 
        </ul>
      </li>
      [/#if]
    [/#list]
  </ul> 
</nav>

<div class="clearfix"></div>


[#assign projectEditLeader = (project.projectInfo.isProjectEditLeader())!false /]
[#assign completed = (sectionsChecked == sectionsForChecking?size) &&  projectEditLeader/]
[#assign completedPreProject = (sectionsChecked == sectionsForChecking?size) /]
[#assign trackedMissing = trackedTotal - trackedDone /]
[#assign trackedPct = (trackedTotal > 0)?then(((trackedDone * 100) / trackedTotal)?round, 0) /]
[#-- "project" / "cluster" / ..., per global unit, for the status card copy. --]
[#assign menuNoun = (action.getText("projects.menu.status.noun"))!"project" /]

[#-- Sections for checking (Using by JS) --]
<span id="sectionsForChecking" style="display:none">[#list sectionsForChecking as item]${item}[#if item_has_next],[/#if][/#list]</span>

[#-- Status card (A2-2440): completeness, "Check for missing fields" and the submit
     flow, drawn as the second sidebar card of the design. The ids and classes
     projectSubmit.js binds to (validateProject-, progressbar-, submitProject-,
     .projectEditLeader, #unSubmit-justification) are unchanged. --]
<div class="clusterMenu-status" data-tracked-total="${trackedTotal}">

  [#-- Completeness --]
  [#if trackedTotal > 0]
  <div class="clusterMenu-status__progress">
    <span class="clusterMenu-status__progressHead">
      <span class="clusterMenu-status__label">[@s.text name="projects.menu.status.completeness"][@s.param]${menuNoun?cap_first}[/@s.param][/@s.text]</span>
      <span class="clusterMenu-status__count" data-cluster-done data-template="[@s.text name="projects.menu.status.sectionsDone"][@s.param]{0}[/@s.param][@s.param]{1}[/@s.param][/@s.text]">[@s.text name="projects.menu.status.sectionsDone"][@s.param]${trackedDone}[/@s.param][@s.param]${trackedTotal}[/@s.param][/@s.text]</span>
    </span>
    <span class="clusterMenu-status__bar" role="progressbar" aria-valuemin="0" aria-valuemax="${trackedTotal}" aria-valuenow="${trackedDone}" aria-label="[@s.text name="projects.menu.status.completeness"][@s.param]${menuNoun?cap_first}[/@s.param][/@s.text]">
      <span class="clusterMenu-status__barFill" data-cluster-bar style="width:${trackedPct}%"></span>
    </span>
  </div>
  [/#if]

  [#-- Open for Project Leaders --]
  [#if !reportingActive && canSwitchProject && ( completedPreProject || projectEditLeader) && !crpClosed && !centerGlobalUnit]
    [#if !submission]
    <div class="clusterMenu-status__toggle">
      [@customForm.yesNoInput name="project.projectInfo.isProjectEditLeader()" label="project.isOpen" editable=true inverse=false cssClass="projectEditLeader" /]
    </div>
    [/#if]
  [#else]
    [#if !projectEditLeader]
      <p class="clusterMenu-status__note">[@s.text name="projects.menu.status.presetNote" /]</p>
    [/#if]
  [/#if]

  [#if !centerGlobalUnit]
    [#-- Submission message --]
    [#if !submission && completed && !canSubmit]
      [#if action.isAiccra()]
        <p class="clusterMenu-status__note">[@s.text name="projects.menu.status.completedAiccra" /]</p>
      [#else]
        <p class="clusterMenu-status__note">[@s.text name="projects.menu.status.completedLeader" /]</p>
      [/#if]
    [/#if]

    [#-- Check button --]
    [#if canEdit && !completed && !submission  && projectEditLeader]
      <div id="validateProject-${projectID}" class="projectValidateButton clusterMenu-status__check ${(project.type)!''}" role="button" tabindex="0" data-label-again="[@s.text name="projects.menu.status.checkAgain" /]">
        <svg width="15" height="15" viewBox="0 0 16 16" fill="none" aria-hidden="true"><circle cx="8" cy="8" r="6.4" stroke="currentColor" stroke-width="1.5"></circle><path d="M5.2 8.2 7.1 10l3.7-4" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round"></path></svg>
        <span class="clusterMenu-status__checkLabel">[@s.text name="projects.menu.status.check" /]</span>
      </div>
      <div id="progressbar-${projectID}" class="progressbar clusterMenu-status__checking" style="display:none"></div>
      [#-- What the check found, in words. projectSubmit.js fills it; empty until then. --]
      <div class="clusterMenu-results" data-cluster-results role="status" aria-live="polite"
        data-text-ok="[@s.text name="projects.menu.status.allGood"][@s.param]${menuNoun}[/@s.param][/@s.text]"
        data-text-missing="[@s.text name="projects.menu.status.sectionMissing" /]"></div>
    [/#if]

    [#assign enableUnsubmitButton = !upKeepActive ]

    [#if action.canAccessSuperAdmin()]

      [#-- Submit button: SuperAdmin can always submit, complete or not (override). --]
      [#assign showSubmit=(!submission)]
      [#if enableUnsubmitButton && showSubmit]
        <div class="clusterMenu-status__admin">
          <span class="clusterMenu-status__adminCaption">
            <svg width="12" height="12" viewBox="0 0 16 16" fill="none" aria-hidden="true"><rect x="3.2" y="7" width="9.6" height="6.6" rx="1.6" stroke="currentColor" stroke-width="1.5"></rect><path d="M5.6 7V5.2a2.4 2.4 0 0 1 4.8 0V7" stroke="currentColor" stroke-width="1.5"></path></svg>
            [@s.text name="projects.menu.status.superAdminOnly" /]
          </span>
          <a id="submitProject-${projectID}" class="projectSubmitButton clusterMenu-status__submit" href="[@s.url action="${crpSession}/submit"][@s.param name='projectID']${projectID}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" >
            <svg width="14" height="14" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M8 10.5V2.8M5 5.6 8 2.6l3 3" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"></path><path d="M3 9.5v3.2h10V9.5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"></path></svg>
            [@s.text name="projects.menu.status.submit"][@s.param]${menuNoun}[/@s.param][/@s.text]
          </a>
          <span class="clusterMenu-status__submitNote" data-cluster-submit-note
            data-text-ready="[@s.text name="projects.menu.status.submitReady"][@s.param]${menuNoun}[/@s.param][/@s.text]"
            data-text-pending="[@s.text name="projects.menu.status.submitPending"][@s.param]{0}[/@s.param][/@s.text]">
            [#if trackedMissing > 0]
              [@s.text name="projects.menu.status.submitPending"][@s.param]${trackedMissing}[/@s.param][/@s.text]
            [#else]
              [@s.text name="projects.menu.status.submitReady"][@s.param]${menuNoun}[/@s.param][/@s.text]
            [/#if]
          </span>
        </div>
      [/#if]

    [#else]

      [#-- Submit button: hidden until every section is complete; the check reveals it. --]
      [#if enableUnsubmitButton && canEdit]
        [#assign showSubmit=(canSubmit && !submission && completed)]
        <a id="submitProject-${projectID}" class="projectSubmitButton clusterMenu-status__submit" style="display:${showSubmit?string('flex','none')}" href="[@s.url action="${crpSession}/submit"][@s.param name='projectID']${projectID}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" >
          <svg width="14" height="14" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M8 10.5V2.8M5 5.6 8 2.6l3 3" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"></path><path d="M3 9.5v3.2h10V9.5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"></path></svg>
          [@s.text name="projects.menu.status.submit"][@s.param]${menuNoun}[/@s.param][/@s.text]
        </a>
      [/#if]

    [/#if]

    [#-- Unsubmit button --]
    [#if enableUnsubmitButton && (canUnSubmit && submission) && canEditPhase && !crpClosed ]
      <a id="submitProject-${projectID}" class="projectUnSubmitButton clusterMenu-status__unsubmit" href="[@s.url action="${crpSession}/unsubmit"][@s.param name='projectID']${projectID}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" >
        [@s.text name="form.buttons.unsubmit" /]
      </a>
    [/#if]
  [/#if]
</div>

[#if !centerGlobalUnit]
  [#-- Justification --]
  <div id="unSubmit-justification" title="[@s.text name="form.buttons.unsubmit" /] justification" style="display:none"> 
    <div class="dialog-content"> 
        [@customForm.textArea name="justification-unSubmit" i18nkey="saving.justification" required=true className="justification"/]
    </div>
  </div>
[/#if]

  [#-- AICCRA Doc report --]
  [#if !config.production && action.canAccessSuperAdmin() && false]
    <br><br>
    <div class="text-center">
      [#assign documentLink][@s.url namespace="/projects" action="${crpSession}/progressReportProcessSummary"][@s.param name='projectID']${projectID}[/@s.param][@s.param name='phaseID']${actualPhase.id}[/@s.param][/@s.url][/#assign]
      <a class="btn btn-default" href="${documentLink}" target="_blank">
       <img src="${baseUrlCdn}/global/images/icons/file-doc.png" alt="" /> Generate Progress Summary
       [@utils.underConstruction title="global.underConstruction" width="20px" height="20px" /]
      </a>
    </div>
  [/#if]

[#-- Discard Changes Popup --]
[#include "/WEB-INF/global/macros/discardChangesPopup.ftl"]

[#-- Project Submit JS --]
[#assign customJS = [ "${baseUrlMedia}/js/projects/projectSubmit.js?20261009-2" ] + customJS  /]

[#macro menuIcon name="" show=true width="" height="" ]
  <span style="display:${show?string('inline','none')};">
    <img src="${baseUrlCdn}/global/images/${name}.png" width="${width!'10px'}" height="${height!'10px'}" />
  </span>
[/#macro]
