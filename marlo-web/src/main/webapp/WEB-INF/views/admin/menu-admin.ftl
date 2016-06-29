[#ftl]
[#assign items= [
  { 'slug': 'management',       'name': 'CRPAdmin.menu.hrefProgramManagement', 'action': 'management',       'active': true },
  { 'slug': 'regionalMapping',  'name': 'CRPAdmin.menu.hrefRegionalMapping',   'action': 'regionalMapping',  'active': action.hasProgramnsRegions() },
  { 'slug': 'ppaPartners',      'name': 'CRPAdmin.menu.hrefPPAPartners',       'action': 'ppaPartners',      'active': true },
  { 'slug': 'siteIntegration',  'name': 'CRPAdmin.menu.hrefSiteIntegration',   'action': 'siteIntegration',  'active': true },
  { 'slug': 'locations',        'name': 'CRPAdmin.menu.hrefLocations',          'action': 'locations',        'active': true }
]/]

<nav id="secondaryMenu">
  <ul>
    <li><p>[@s.text name="CRPAdmin.menu.title"/]</p>
      <ul>
        [#list items as item]
          <li id="${item.slug}" class="[#if item.slug == currentStage]currentSection[/#if] ${(item.active)?string('enabled','disabled')}">
            <a href="[@s.url action="${crpSession}/${item.action}"][@s.param name="edit" value="true"/][/@s.url]" onclick="return ${item.active?string}">
              [@s.text name=item.name/]
            </a>
          </li>
        [/#list] 
      </ul>
    </li>
  </ul> 
</nav>

<div class="clearfix"></div>