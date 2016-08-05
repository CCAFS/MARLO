[#ftl]
[#assign items= [
  { 'slug': 'description',  'name': 'projects.menu.description',  'action': 'description',  'active': true  },
  { 'slug': 'partners',  'name': 'projects.menu.partners',  'action': 'partners',  'active': true  },
  { 'slug': 'locations',  'name': 'projects.menu.locations',  'action': 'locations',  'active': true  }
]/]


[#assign submission = (action.submission)! /]
[#assign canSubmit = (action.hasPersmissionSubmit())!false /]
[#assign completed = (action.isCompleteProject(projectID))!false /]


[#-- Menu--]
<nav id="secondaryMenu" class="">
  <ul>
    <li><p>Project Menu <br /></brq><small>([@s.text name="project.type.${(project.type?lower_case)!'none'}" /])</small></p>
      <ul>
        [#list items as item]
          [#assign submitStatus = (action.getProjectSectionStatus(item.action, projectID))!false /]
          <li id="menu-${item.action}" class="[#if item.slug == currentStage]currentSection[/#if] [#if canEdit]${submitStatus?string('submitted','toSubmit')}[/#if] ${(item.active)?string('enabled','disabled')}">
            <a href="[@s.url action="${crpSession}/${item.action}"][@s.param name="projectID" value=projectID /][@s.param name="edit" value="true"/][/@s.url]" onclick="return ${item.active?string}">
              [@s.text name=item.name/]
            </a>
          </li>
        [/#list] 
      </ul>
    </li>
  </ul> 
</nav>

<div class="clearfix"></div>
