[#ftl]
[#if !((project.projectInfo.isProjectEditLeader())!false)]
  [#assign menus= [
    { 'title': 'General Information', 'show': true,
      'items': [
      { 'slug': 'description',  'name': 'projects.menu.description',  'action': 'description',  'active': true  },
      { 'slug': 'partners',  'name': 'projects.menu.partners',  'action': 'partners',  'active': true  },
      { 'slug': 'budgetByPartners',  'name': 'Budget',  'action': 'budgetByPartners',  'active': true  }
      ]
    }
    
  ]/]
[#else] 
  [#assign menus= [
    { 'title': 'General Information', 'show': true,
      'items': [
      { 'slug': 'description',  'name': 'projects.menu.description',  'action': 'description',  'active': true  },
      { 'slug': 'partners',  'name': 'projects.menu.partners',  'action': 'partners',  'active': true  },
      { 'slug': 'locations',  'name': 'projects.menu.locations',  'action': 'locations',  'active': true  }
      ]
    },
    { 'title': 'Outcomes', 'show': !project.projectInfo.administrative,
      'items': [
      { 'slug': 'contributionsCrpList',  'name': 'projects.menu.contributionsCrpList',  'action': 'contributionsCrpList',  'active': true, 'show':!phaseOne },
      { 'slug': 'projectOutcomes',  'name': 'projects.menu.projectOutcomes',  'action': 'outcomesPandR',  'active': true, 'show':  phaseOne  },
      { 'slug': 'ccafsOutcomes',  'name': 'projects.menu.ccafsOutcomes',  'action': 'ccafsOutcomes',  'active': true, 'show': phaseOne },
      { 'slug': 'otherContributions',  'name': 'projects.menu.otherContributions',  'action': 'otherContributions',  'active': phaseOne, 'show': reportingActive  },
      { 'slug': 'caseStudies',  'name': 'Outcome Case Studies',  'action': 'caseStudies',  'active': false, 'show': reportingActive }
      ]
    },
    { 'title': 'Outputs', 'show': true,
      'items': [
      { 'slug': 'overviewByMogs',  'name': 'projects.menu.overviewByMogs',  'action': 'outputs',  'active': true, 'show' : phaseOne },
      { 'slug': 'deliverableList',  'name': 'projects.menu.deliverables',  'action': 'deliverableList',  'active': true  },
      { 'slug': 'highlights',  'name': 'Project Highlights',  'action': 'highlights',  'active': false ,'show': reportingActive }
      ]
    },
    { 'title': 'Activities', 'show': action.hasSpecificities(action.crpActivitesModule()),
      'items': [
      { 'slug': 'activities',  'name': 'projects.menu.activities',  'action': 'activities',  'active': true  ,'show': true }
      ]
    },
    { 'title': 'Budget', 'show': true,
      'items': [
      { 'slug': 'budgetByPartners',  'name': 'projects.menu.budgetByPartners',  'action': 'budgetByPartners',  'active': true, 'show':true },

      { 'slug': 'budgetByCoAs',  'name': 'projects.menu.budgetByCoAs',  'action': 'budgetByCoAs', 'show': action.canEditBudgetByCoAs(project.id) && !project.projectInfo.administrative && !reportingActive && !phaseOne, 'active': true  },
      { 'slug': 'leverages',  'name': 'Leverages',  'action': 'leverages',  'active': false, 'show': reportingActive && action.hasSpecificities("crp_leverages_module")}

      ]
    }
    
  ]/]
[/#if]


[#assign submission = (action.isProjectSubmitted(projectID))!false /]
[#assign canSubmit = (action.hasPersmissionSubmit(projectID))!false /]
[#assign completed = (action.isCompleteProject(projectID))!false /]
[#assign canUnSubmit = (action.hasPersmissionUnSubmit(projectID))!false /]

[#assign sectionsForChecking = [] /]

[#-- Phase Select 
<div class="form-group">
  [@customForm.select name="actualPhase" className="phaseSelect" value="actualPhase.id"   i18nkey="actual.cycle"   listName="phases" keyFieldName="id"  displayFieldName="composedName" /]
</div>
--]

[#-- Menu--]
<nav id="secondaryMenu" class="">
  <p>Project Menu <br /><small> [#if project.projectInfo.administrative]Program Management [#else] Research Project [/#if]</small> </p> 
  <ul>
    [#list menus as menu]
      [#if menu.show]
      <li>
        <ul><p class="menuTitle">${menu.title}</p>
          [#list menu.items as item]
            [#assign submitStatus = (action.getProjectSectionStatus(item.action, projectID))!false /]
            [#assign hasDraft = (action.getAutoSaveFilePath(project.class.simpleName, item.action, project.id))!false /]
            [#if (item.show)!true ]
              <li id="menu-${item.action}" class="[#if item.slug == currentStage]currentSection[/#if] ${submitStatus?string('submitted','toSubmit')} ${(item.active)?string('enabled','disabled')}">
                <a href="[@s.url action="${crpSession}/${item.action}"][@s.param name="projectID" value=projectID /][@s.param name="edit" value="true"/][/@s.url]" onclick="return ${item.active?string}" class="action-${crpSession}/${item.action}">
                  [#-- Name --]
                  [@s.text name=item.name/]
                  [#-- Draft Tag 
                  [#if hasDraft][@s.text name="message.fieldsCheck.draft" ][@s.param]section[/@s.param][/@s.text][/#if]
                  --]
                </a>
              </li>
              [#if item.active]
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

[#-- Sections for checking (Using by JS) --]
<span id="sectionsForChecking" style="display:none">[#list sectionsForChecking as item]${item}[#if item_has_next],[/#if][/#list]</span>

[#-- Open for Project Leaders --]
[#if !reportingActive && canSwitchProject && (action.isCompletePreProject(project.id) || project.projectInfo.isProjectEditLeader()) && !crpClosed]
  [#if !submission]
  <div class="grayBox text-center">
    [@customForm.yesNoInput name="project.projectInfo.isProjectEditLeader()" label="project.isOpen" editable=true inverse=false cssClass="projectEditLeader text-center" /]  
  </div>
  <br />
  [/#if]
[#else]
  [#if !((project.projectInfo.isProjectEditLeader())!false)]
    <p class="text-justify note"><small>All sections need to be completed (green check mark) for the Project Leader to be able to enter the project details.</small></p>
  [/#if]
[/#if]





  [#-- Submition message --]
  [#if !submission && completed && !canSubmit]
    <p class="text-center" style="display:block">The Project can be submitted now by the project leader.</p>
  [/#if]
  
  
  
 
  

  [#-- Check button --]
  [#if canEdit && !completed && !submission]
    <p class="projectValidateButton-message text-center">Check for missing fields.<br /></p>
    <div id="validateProject-${projectID}" class="projectValidateButton ${(project.type)!''}">[@s.text name="form.buttons.check" /]</div>
    <div id="progressbar-${projectID}" class="progressbar" style="display:none"></div>
  [/#if]

 


 [#-- Submit button --]
  [#if canEdit]
    [#assign showSubmit=(canSubmit && !submission && completed)]
    <a id="submitProject-${projectID}" class="projectSubmitButton" style="display:${showSubmit?string('block','none')}" href="[@s.url action="${crpSession}/submit"][@s.param name='projectID']${projectID}[/@s.param][/@s.url]" >
      [@s.text name="form.buttons.submit" /]
    </a>
  [/#if]
  
  [#-- Unsubmit button --]
  [#if (canUnSubmit && submission) && !crpClosed && !reportingActive]
    <a id="submitProject-${projectID}" class="projectUnSubmitButton" href="[@s.url action="${crpSession}/unsubmit"][@s.param name='projectID']${projectID}[/@s.param][/@s.url]" >
      [@s.text name="form.buttons.unsubmit" /]
    </a>
  [/#if]

[#-- Justification --]
<div id="unSubmit-justification" title="[@s.text name="form.buttons.unsubmit" /] justification" style="display:none"> 
  <div class="dialog-content"> 
      [@customForm.textArea name="justification-unSubmit" i18nkey="saving.justification" required=true className="justification"/]
  </div>  
</div>

[#-- Discard Changes Popup --]
[#include "/WEB-INF/global/macros/discardChangesPopup.ftl"]

[#-- Project Submit JS --]
[#assign customJS = [ "${baseUrlMedia}/js/projects/projectSubmit.js" ] + customJS  /]
