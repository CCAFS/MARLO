[#ftl]
<ul id="breadcrumb">
  [#if breadCrumb?has_content] 
    [#list breadCrumb as item]
      <li>
        [#if item.action?has_content]
          [#if item.param?exists]
            <a href="${baseUrl}/${item.nameSpace}/${item.action}.do?${item.param}" >
          [#else]
            <a href="${baseUrl}/${item.nameSpace}/${item.action}.do" >
          [/#if]
        [#else]
          <a href="#" />
        [/#if]
        [@s.text name="breadCrumb.menu.${item.label}" /]
        </a>[#if item_has_next]>[/#if]
      </li> 
    [/#list]
  [/#if]
</ul>
