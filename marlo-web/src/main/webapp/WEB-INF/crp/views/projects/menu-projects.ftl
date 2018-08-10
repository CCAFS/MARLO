[#ftl]
[#assign isCrpProject = (action.isProjectCrpOrPlatform(project.id))!false ]
[#assign isCenterProject = (action.isProjectCenter(project.id))!false ]

[#assign isGlobalUnitProject = (centerGlobalUnit && isCenterProject) || (!centerGlobalUnit && isCrpProject) /]

[#if !((project.projectInfo.isProjectEditLeader())!false)]
  [#assign menus= [
    { 'title': 'General Information', 'show': true,
      'items': [
      { 'slug': 'description',  'name': 'projects.menu.description',  'action': 'description',  'active': true  },
      { 'slug': 'partners',  'name': 'projects.menu.partners',  'action': 'partners',  'active': true  },
      { 'slug': 'budgetByPartners',  'name': 'Budget',  'action': 'budgetByPartners',  'active': true  },
      { 'slug': 'budgetByFlagships',  'name': 'projects.menu.budgetByFlagships',  'action': 'budgetByFlagship',  'active': true , 'show': action.getCountProjectFlagships(project.id) && !reportingActive && isCrpProject}
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
    { 'title': 'Outcomes', 'show': isCrpProject,
      'items': [
      { 'slug': 'contributionsCrpList',  'name': 'projects.menu.contributionsCrpList',  'action': 'contributionsCrpList',  'active': true, 'show':!phaseOne  && ((!project.projectInfo.administrative)!false) , "showCheck": isGlobalUnitProject},
      { 'slug': 'projectOutcomes',  'name': 'projects.menu.projectOutcomes',  'action': 'outcomesPandR',  'active': true, 'show':  phaseOne && !project.projectInfo.administrative , "showCheck": isGlobalUnitProject},
      { 'slug': 'ccafsOutcomes',  'name': 'projects.menu.ccafsOutcomes',  'action': 'ccafsOutcomes',  'active': true, 'show': phaseOne && !project.projectInfo.administrative , "showCheck": isGlobalUnitProject },
      { 'slug': 'projectStudies',  'name': 'projects.menu.expectedStudies',  'action': 'studies',  'active': true, 'show': !reportingActive , "showCheck": isGlobalUnitProject },
      { 'slug': 'projectStudies',  'name': 'projects.menu.studies',           'action': 'studies',  'active': true, 'show': reportingActive , "showCheck": isGlobalUnitProject }
      ]
    },
    { 'title': 'Outputs', 'show': true,
      'items': [
      { 'slug': 'overviewByMogs',  'name': 'projects.menu.overviewByMogs',  'action': 'outputs',  'active': true, 'show' : phaseOne && isCrpProject , "showCheck": isGlobalUnitProject},
      { 'slug': 'deliverableList',  'name': 'projects.menu.deliverables',  'action': 'deliverableList',  'active': true , "showCheck": isGlobalUnitProject },
      { 'slug': 'innovations',  'name': 'projects.menu.innovations',  'action': 'innovationsList',  'active': true,'show': reportingActive && isCrpProject , "showCheck": isGlobalUnitProject },
      { 'slug': 'highlights',  'name': 'Project Highlights',  'action': 'highlights',  'active': true ,'show': reportingActive && isCrpProject, "showCheck": isGlobalUnitProject }
      ]
    },
    { 'title': 'Activities', 'show': action.hasSpecificities(action.crpActivitesModule()),
      'items': [
      { 'slug': 'activities',  'name': 'projects.menu.activities',  'action': 'activities',  'active': true  ,'show': true, "showCheck": isGlobalUnitProject }
      ]
    },
    { 'title': 'Budget', 'show': true,
      'items': [
      { 'slug': 'budgetByPartners',  'name': 'projects.menu.budgetByPartners',  'action': 'budgetByPartners',  'active': true, 'show':true, "showCheck": isGlobalUnitProject },
      { 'slug': 'budgetByCoAs',  'name': 'projects.menu.budgetByCoAs',  'action': 'budgetByCoAs', 'show': action.canEditBudgetByCoAs(project.id) && !project.projectInfo.administrative && !reportingActive && !phaseOne, 'active': isCrpProject , "showCheck": isGlobalUnitProject },
      { 'slug': 'budgetByFlagships',  'name': 'projects.menu.budgetByFlagships',  'action': 'budgetByFlagship',  'active': true, 'show': action.getCountProjectFlagships(project.id) && !reportingActive && isCrpProject, "showCheck": isGlobalUnitProject},
      { 'slug': 'leverages',  'name': 'Leverages',  'action': 'leverages',  'active': true, 'show': reportingActive && action.hasSpecificities("crp_leverages_module") && isCrpProject, "showCheck": isGlobalUnitProject}
      ]
    }
    
  ]/]
[/#if]


[#assign submission = (action.isProjectSubmitted(projectID))!false /]
[#assign canSubmit = (action.hasPersmissionSubmit(projectID))!false /]
[#-- assign completed = (action.isCompleteProject(projectID))!false /--]
[#assign canUnSubmit = ((action.hasPersmissionUnSubmit(projectID))!false)/]

[#assign sectionsForChecking = [] /]

[#-- Menu--]
<nav id="secondaryMenu" class="">
  <p>[@s.text name="projects.menu.project" /]<br />
    <small> 
    [#-- Global Unit Acronym --]
    ${(project.projectInfo.phase.crp.acronym)!}
    [#-- Project Type --]
    [#if (project.projectInfo.administrative)!false]Program Management [#else] Research Project [/#if]
    </small>
  </p> 
  <ul>
    [#assign sectionsChecked = 0 /]
    [#list menus as menu]
      [#if menu.show]
      <li>
        <ul><p class="menuTitle">${menu.title}</p>
          [#list menu.items as item]
            [#assign submitStatus = (action.getProjectSectionStatus(item.action, projectID))!false /]
            [#assign hasDraft = (action.getAutoSaveFilePath(project.class.simpleName, item.action, project.id))!false /]
            [#if (item.show)!true ]
              <li id="menu-${item.action}" class="${hasDraft?string('draft', '')} [#if item.slug == currentStage]currentSection[/#if] [#if (item.showCheck)!true] ${submitStatus?string('submitted','toSubmit')} [/#if] ${(item.active)?string('enabled','disabled')}">
                <a href="[@s.url action="${crpSession}/${item.action}"][@s.param name="projectID" value=projectID /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" onclick="return ${item.active?string}" class="action-${crpSession}/${item.action}">
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

[#-- Sections for checking (Using by JS) --]
<span id="sectionsForChecking" style="display:none">[#list sectionsForChecking as item]${item}[#if item_has_next],[/#if][/#list]</span>

[#-- Open for Project Leaders --]
[#if !reportingActive && canSwitchProject && (action.isCompletePreProject(project.id) || projectEditLeader) && !crpClosed && !centerGlobalUnit]
  [#if !submission]
  <div class="grayBox text-center">
    [@customForm.yesNoInput name="project.projectInfo.isProjectEditLeader()" label="project.isOpen" editable=true inverse=false cssClass="projectEditLeader text-center" /]  
  </div>
  <br />
  [/#if]
[#else]
  [#if !projectEditLeader]
    <p class="text-justify note"><small>All sections need to be completed (green check mark) for the Project Leader to be able to enter the project details.</small></p>
  [/#if]
[/#if]

[#if !centerGlobalUnit]
  [#-- Submition message --]
  [#if !submission && completed && !canSubmit]
    <p class="text-center" style="display:block">The Project can be submitted now by the project leader.</p>
  [/#if]
  
  [#-- Check button --]
  [#if canEdit && !completed && !submission  && projectEditLeader]
    <p class="projectValidateButton-message text-center">Check for missing fields.<br /></p>
    <div id="validateProject-${projectID}" class="projectValidateButton ${(project.type)!''}">[@s.text name="form.buttons.check" /]</div>
    <div id="progressbar-${projectID}" class="progressbar" style="display:none"></div>
  [/#if]
  
  [#-- Submit button --]
  [#if canEdit]
    [#assign showSubmit=(canSubmit && !submission && completed)]
    <a id="submitProject-${projectID}" class="projectSubmitButton" style="display:${showSubmit?string('block','none')}" href="[@s.url action="${crpSession}/submit"][@s.param name='projectID']${projectID}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" >
      [@s.text name="form.buttons.submit" /]
    </a>
  [/#if]
  
  [#-- Unsubmit button --]
  [#if (canUnSubmit && submission) && canEditPhase && !crpClosed ]
    <a id="submitProject-${projectID}" class="projectUnSubmitButton" href="[@s.url action="${crpSession}/unsubmit"][@s.param name='projectID']${projectID}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" >
      [@s.text name="form.buttons.unsubmit" /]
    </a>
  [/#if]
  
  [#-- Justification --]
  <div id="unSubmit-justification" title="[@s.text name="form.buttons.unsubmit" /] justification" style="display:none"> 
    <div class="dialog-content"> 
        [@customForm.textArea name="justification-unSubmit" i18nkey="saving.justification" required=true className="justification"/]
    </div>
  </div>
[/#if]

[#-- Discard Changes Popup --]
[#include "/WEB-INF/global/macros/discardChangesPopup.ftl"]

[#-- Project Submit JS --]
[#assign customJS = [ "${baseUrlMedia}/js/projects/projectSubmit.js?20180530" ] + customJS  /]
