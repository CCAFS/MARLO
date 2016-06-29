[#ftl]
[#assign title = "MARLO Admin" /]
[#assign pageLibs = [] /]
[#assign customJS = [ ] /]
[#assign customCSS = [  ] /]
[#assign currentSection = "superadmin" /]
[#assign currentStage = "marloBoard" /]

[#assign breadCrumb = [
  {"label":"superadmin", "nameSpace":"", "action":"marloBoard"},
  {"label":"marloBoard", "nameSpace":"", "action":""}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<section class="marlo-content">
  <div class="container"> 
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/views/superadmin/menu-superadmin.ftl" /]
      </div>
      <div class="col-md-9">
        [@s.form action=actionName enctype="multipart/form-data" ]
        <h4 class="sectionTitle">Target Units</h4>
        <div class="borderBox items-list">
          <ul>
          [#list targetUnitList as targetUnit]
            <li class="li-item">(${targetUnit.acronym}) ${targetUnit.name}</li>
          [/#list]
          </ul>
          [#if !targetUnitList?has_content]<p class="text-center">There is not target units</p>[/#if]
        </div>
        [/@s.form]
      	
      </div>
    </div>
  </div>
</section>


[#include "/WEB-INF/global/pages/footer.ftl" /]



