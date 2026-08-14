[#ftl]
[#if logged && action.isVisibleTop()]
  [#assign superAdminMenu =[
    { 'slug': 'superadmin',     'name': 'menu.superadmin',    'namespace': '/superadmin',     'action': 'marloBoard', 'visible': action.canAccessSuperAdmin(), 'active': true }

  ]/]

    [#assign superAdminMenu = superAdminMenu + [
      { 'slug': 'admin',     'name': 'menu.admin',    'namespace': '/admin',            'action': '${(crpSession)!}/management',      'icon': 'cog',  'visible': action.canAcessCrpAdmin(),     'active': true }
    ]/]
  <div id="superadminBlock">
    <div class="container">
      <ul>
        [#list superAdminMenu as item]
          [#if item.visible]
          <li id="${item.slug}" class="[#if currentSection?? && currentSection == item.slug ]currentSection[/#if] ${(item.active)?string('enabled','disabled')}">
            <a href="[@s.url namespace=item.namespace action=item.action ][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" onclick="return ${item.active?string}">
              [#if item.icon?has_content]<span class="glyphicon glyphicon-${item.icon}"></span> [/#if][@s.text name=item.name ][@s.param]${(crpSession)!'CRP'}[/@s.param] [/@s.text]
            </a>
          </li>
          [/#if]
        [/#list]
        [#if action.isVisibleTopGUList()]

        [#--
          Global unit switcher.

          The panel is opened by click (see global-unit-switcher.js), not by
          :hover — the pointer used to leave the trigger before reaching the
          menu, which closed it before anything could be selected.
        --]

        [#-- Open phases of the loaded global unit. `phases` only exists on
             tenant pages, and the other units in the list come from the
             session as detached entities, so their phases cannot be counted
             here without a lazy-loading failure. --]
        [#assign guOpenPhases = 0 /]
        [#attempt]
          [#if phases??]
            [#list phases as guPhase]
              [#if (guPhase.editable)!false][#assign guOpenPhases = guOpenPhases + 1 /][/#if]
            [/#list]
          [/#if]
        [#recover]
          [#assign guOpenPhases = 0 /]
        [/#attempt]

        [#assign guTypeCount = (listGlobalUnitTypesUser?size)!0 /]

        <li id="globalUnitSwitcher">
          <button type="button" id="globalUnitSwitcherTrigger" class="guSwitcher__trigger"
            aria-haspopup="dialog" aria-expanded="false" aria-controls="globalUnitSwitcherPanel">
            <span class="guSwitcher__dot" aria-hidden="true"></span>
            <span class="guSwitcher__acronym">${(currentGlobalUnit.acronym)!'--'}</span>
            <svg class="guSwitcher__caret" width="10" height="10" viewBox="0 0 12 12" fill="none" aria-hidden="true"><path d="M2.5 4.5 6 8l3.5-3.5" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round"/></svg>
          </button>

          <div id="globalUnitSwitcherPanel" class="guPanel" role="dialog"
            aria-label="[@s.text name="globalUnitSwitcher.label" /]" hidden>

            <div class="guPanel__current">
              <span class="guPanel__label">[@s.text name="globalUnitSwitcher.currentPortfolio" /]</span>
              <div class="guPanel__currentRow">
                <span class="guPanel__currentName">${(currentGlobalUnit.acronym)!'--'}</span>
                [#if guOpenPhases gt 0]
                  <span class="guBadge guBadge--open">[#if guOpenPhases == 1][@s.text name="globalUnitSwitcher.onePhaseOpen" /][#else][@s.text name="globalUnitSwitcher.phasesOpen"][@s.param]${guOpenPhases?c}[/@s.param][/@s.text][/#if]</span>
                [/#if]
              </div>
              [#if (currentGlobalUnit.name)?has_content]
                <p class="guPanel__currentDesc">${currentGlobalUnit.name}</p>
              [/#if]
            </div>

            <div class="guPanel__switch">
              <span class="guPanel__label">[@s.text name="globalUnitSwitcher.switchTo" /]</span>
              <div class="guPanel__list">
                [#if listGlobalUnitTypesUser??]
                  [#list listGlobalUnitTypesUser as globalUnitType]
                    [#-- The type heading only earns its space when the user
                         belongs to more than one kind of global unit. --]
                    [#if guTypeCount gt 1]
                      <span class="guPanel__group">${globalUnitType.name}</span>
                    [/#if]
                    [#list globalUnitType.globalUnitsList as globalUnit]
                      [#if globalUnit.login]
                        [#assign guAction = "crpDashboard" /]
                        [#if globalUnitType.id == 2][#assign guAction = "centerDashboard" /][/#if]
                        [#assign guIsCurrent = (crpSession?? && crpSession == globalUnit.acronym) /]
                        <a class="guItem[#if guIsCurrent] guItem--current[/#if]"
                          href="[@s.url namespace="/" action="${globalUnit.acronym}/${guAction}" ][@s.param name="edit" value="true"/][/@s.url]"
                          [#if guIsCurrent]aria-current="true"[/#if]>
                          <span class="guItem__text">
                            <span class="guItem__name">${globalUnit.acronym}</span>
                            [#if (globalUnit.name)?has_content]<span class="guItem__desc">${globalUnit.name}</span>[/#if]
                          </span>
                          [#if guIsCurrent]
                            <svg class="guItem__check" width="15" height="15" viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="M3 8.4 6.2 11.6 13 4.8" stroke="currentColor" stroke-width="1.9" stroke-linecap="round" stroke-linejoin="round"/></svg>
                          [/#if]
                        </a>
                      [/#if]
                    [/#list]
                  [/#list]
                [/#if]
              </div>
            </div>
          </div>
        </li>
        [/#if]
        <div class="clearfix"></div>
      </ul>
    </div>
  </div>
[/#if]
