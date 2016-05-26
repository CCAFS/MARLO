[#ftl]
<ol class="breadcrumb">
  [#if breadCrumb?has_content] 
    [#list breadCrumb as item]
      <li class="[#if !item_has_next]active[/#if]">
        [#if item.action?has_content]
          [#if item.param?exists]
            <a href="${baseUrl}/${item.nameSpace}/${item.action}.do?${item.param}" >[@s.text name="breadCrumb.menu.${item.label}" /]</a>
          [#else]
            <a href="${baseUrl}/${item.nameSpace}/${item.action}.do" >[@s.text name="breadCrumb.menu.${item.label}" /]</a>
          [/#if]
        [#else]
          [@s.text name="breadCrumb.menu.${item.label}" /]</a>
        [/#if]
      </li> 
    [/#list]
  [/#if]
  <div class="usersInfo">
    [#assign users = action.getUsersOnline()]
    
    <button type="button" class="btn btn-xs btn-default" title="[#list users as us]${(us.firstName)!} ${(us.lastName)!}[/#list]">Users Online : ${online}</button>
  </div>
</ol>
