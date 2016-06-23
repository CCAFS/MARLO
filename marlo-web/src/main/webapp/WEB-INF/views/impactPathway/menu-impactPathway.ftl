[#ftl]
[#assign items= [
  { 'slug': 'outcomes',           'name': 'impactPathway.menu.hrefOutcomes',               'action': 'outcomes',         'active': true },
  { 'slug': 'clusterActivities',  'name': 'impactPathway.menu.hrefCOA',  'action': 'clusterActivities',  'active': true }
]/]

<nav id="secondaryMenu">
  <ul>
    <li><p>[@s.text name="impactPathway.menu.title"/]</p>
      <ul>
        [#list items as item]
          <li class="[#if item.slug == currentStage]currentSection[/#if] ${(item.active)?string('enabled','disabled')}">
            <a href="[@s.url action="${crpSession}/${item.action}"][@s.param name="crpProgramID" value=crpProgramID /][@s.param name="edit" value="true"/][/@s.url]" onclick="return ${item.active?string}">
              [@s.text name=item.name/]
            </a>
          </li>
        [/#list] 
      </ul>
    </li>
  </ul> 
</nav>