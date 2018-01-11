[#ftl]
[#assign items= [
  { 'slug': 'coordination',       'name': 'admin.menu.hrefCoordination', 'action': 'coordination',       'active': true },
  { 'slug': 'strategicObjectives',  'name': 'admin.menu.hrefObjectives',   'action': 'objectives',  'active': true },
  { 'slug': 'researchManagement',      'name': 'admin.menu.hrefManagement',       'action': 'researchManagement',      'active': true }
]/]

<nav id="secondaryMenu">
  <p>[@s.text name="centerAdmin.menu.title"/]</p>
  <ul>
    <li>
      <ul>
        [#list items as item]
          <li id="${item.slug}" class="[#if item.slug == currentStage]currentSection[/#if] ${(item.active)?string('enabled','disabled')}">
            <a href="[@s.url action="${centerSession}/${item.action}"][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" onclick="return ${item.active?string}">
              [@s.text name=item.name/]
            </a>
          </li>
        [/#list] 
      </ul>
    </li>
  </ul> 
</nav>

<div class="clearfix"></div>