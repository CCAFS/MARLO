[#ftl]
[#if !((project.projectEditLeader)!false)]
  [#assign menus= [
    { 'title': 'General Information',
      'items': [
      { 'slug': 'description',  'name': 'projects.menu.description',  'action': 'description',  'active': true  },
      { 'slug': 'partners',  'name': 'projects.menu.partners',  'action': 'partners',  'active': true  },
      { 'slug': 'budget',  'name': 'Budget',  'action': 'locations',  'active': false  }
      ]
    }
    
  ]/]
[#else]
  [#assign menus= [
    { 'title': 'General Information',
      'items': [
      { 'slug': 'description',  'name': 'projects.menu.description',  'action': 'description',  'active': true  },
      { 'slug': 'partners',  'name': 'projects.menu.partners',  'action': 'partners',  'active': true  },
      { 'slug': 'locations',  'name': 'projects.menu.locations',  'action': 'locations',  'active': true  }
      ]
    },
    { 'title': 'Outcomes',
      'items': [
      { 'slug': '',  'name': 'Project Contribution to CRP Outcomes',  'action': '',  'active': false  },
      { 'slug': '',  'name': 'Other Contributions',  'action': '',  'active': false  },
      { 'slug': '',  'name': 'Outcome Case Studies',  'action': '',  'active': false, 'show': reportingActive }
      ]
    },
    { 'title': 'Outputs',
      'items': [
      { 'slug': '',  'name': 'Deliverables',  'action': '',  'active': false  },
      { 'slug': '',  'name': 'Project Highlights',  'action': '',  'active': false ,'show': reportingActive }
      ]
    },
    { 'title': 'Activities',
      'items': [
      { 'slug': '',  'name': 'Activities',  'action': '',  'active': false  }
      ]
    },
    { 'title': 'Budget',
      'items': [
      { 'slug': '',  'name': 'Budget by Partners',  'action': '',  'active': false  },
      { 'slug': '',  'name': 'Budget by CoAs',  'action': '',  'active': false  },
      { 'slug': '',  'name': 'Leverages',  'action': '',  'active': false, 'show': reportingActive }
      ]
    }
    
  ]/]
[/#if]


[#assign submission = (action.submission)! /]
[#assign canSubmit = (action.hasPersmissionSubmit())!false /]
[#assign completed = (action.isCompleteProject(projectID))!false /]


[#-- Menu--]
<nav id="secondaryMenu" class="">
  <p>Project Menu <br /><small>([@s.text name="project.type.${(project.type?lower_case)!'none'}" /])</small></p>
  <ul>
    [#list menus as menu]
    <li>
      <ul><p class="menuTitle">${menu.title}</p>
        [#list menu.items as item]
          [#assign submitStatus = (action.getProjectSectionStatus(item.action, projectID))!false /]
          [#if (item.show)!true ]
          <li id="menu-${item.action}" class="[#if item.slug == currentStage]currentSection[/#if] [#if canEdit]${submitStatus?string('submitted','toSubmit')}[/#if] ${(item.active)?string('enabled','disabled')}">
            <a href="[@s.url action="${crpSession}/${item.action}"][@s.param name="projectID" value=projectID /][@s.param name="edit" value="true"/][/@s.url]" onclick="return ${item.active?string}" style="border-color:${(project.liaisonInstitution.crpProgram.color)!}">
              [@s.text name=item.name/]
            </a>
          </li>
          [/#if]
        [/#list] 
      </ul>
    </li>
    [/#list]
  </ul> 
</nav>

<div class="clearfix"></div>

[#-- Open for Project Leaders --]
[#if action.hasPermission("projectSwitch") ]
<div class="grayBox">
  [@customForm.yesNoInput name="project.projectEditLeader" label="project.isOpen" editable=editable inverse=false cssClass="text-left" /]
</div>
[/#if]

[#-- Project Submit JS --]
[#assign customJS = [ "${baseUrl}/js/projects/projectSubmit.js" ] + customJS  /]
