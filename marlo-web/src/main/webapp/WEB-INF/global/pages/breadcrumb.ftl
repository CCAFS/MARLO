[#ftl]
[#if breadCrumb??]
[#-- A phase with no id used to render phaseID with an empty value, which every action then had to reject, so the
     param is only added to the links when there is an id to send. --]
[#assign phaseQuery = "" /]
[#if (actualPhase.id)?has_content][#assign phaseQuery = "phaseID=" + actualPhase.id?c /][/#if]
<ol class="breadcrumb">
  [#if breadCrumb?has_content] 
    [#list breadCrumb as item]
      <li class="[#if !item_has_next]active[/#if]">
        [#if item.action?has_content]
          [#if item.param?exists]
            <a href="${baseUrl}/${item.nameSpace}/${item.action}.do?${item.param}[#if phaseQuery?has_content]&${phaseQuery}[/#if]" >[#if item.label?exists][@s.text name="breadCrumb.menu.${item.label}" /][#else][@s.text name="${item.text}" /][/#if]</a>
          [#else]
            <a href="${baseUrl}/${item.nameSpace}/${item.action}.do[#if phaseQuery?has_content]?${phaseQuery}[/#if]" >[#if item.label?exists][@s.text name="breadCrumb.menu.${item.label}" /][#else][@s.text name="${item.text}" /][/#if]</a>
          [/#if]
        [#else]
          [@s.text name="breadCrumb.menu.${item.label}" /]</a>
        [/#if]
      </li> 
    [/#list]
  [/#if]
  
  <div class="usersInfo">
    [#-- 
      [#assign users = action.getUsersOnline()]
      <button type="button" class="btn btn-xs btn-default" title="[#list users as us]${(us.user.firstName)!} ${(us.user.lastName)!} - ${(us.section)!} <br/> [/#list]">Users Online : ${online}</button>
    --]
    
    [#-- Users Online --]
    <span id="usersOnline" class="""> <span>0</span> user(s) online on this section </span>
    
    [#-- Channel name --]
    <span id="currentSectionString" style="display:none">${(currentSectionString)!'none'}</span>

    [#-- Mouse Pointer Template --]
    <span id="mouse-template" style="display:none;position:absolute;top:0;left:0;">
     | <small style="vertical-align: top;">{sessionID}</small> 
    </span>
    
    [#-- User Badge Template --]
    <span id="user-badge-template" class="user-badge" style="display:none">{}</span>
    
  </div>
</ol>
[/#if]