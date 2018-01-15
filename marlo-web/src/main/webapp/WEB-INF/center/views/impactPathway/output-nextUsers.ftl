[#ftl]
[#assign title = "Output Next-users" /]
[#assign currentSectionString = "program-${actionName?replace('/','-')}-${programID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["datatables.net", "datatables.net-bs","select2"] /]
[#assign customJS = [
  "${baseUrl}/global/js/usersManagement.js", 
  "${baseUrlMedia}/js/impactPathway/output.js", 
  "${baseUrl}/global/js/fieldsValidation.js"
  ] 
/]
[#assign customCSS = [
  "${baseUrl}/global/css/customDataTable.css",
  "${baseUrlMedia}/css/impactPathway/outputList.css"] 
/]
[#assign currentSection = "impactPathway" /]
[#assign currentStage = "outputs" /]

[#assign breadCrumb = [
  {"label":"impactPathway", "nameSpace":"impactPathway", "action":"programImpacts"},
  {"label":"outputsList", "nameSpace":"${centerSession}/impactPathway", "action":"outputsList"},
  {"label":"output", "nameSpace":"", "action":""}
]/]
[#assign leadersName = "leaders"/]
[#include "/WEB-INF/center/pages/header.ftl" /]
[#include "/WEB-INF/center/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/utils.ftl" as utils /]
[#--  marlo cluster of activities--]
<section class="marlo-content">
  <div class="container"> 
    
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/center/views/impactPathway/menu-impactPathway.ftl" /]
      </div>
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/center/views/impactPathway/messages-impactPathway-output.ftl" /]
        [#-- Impact pathway sub menu --]
        [#include "/WEB-INF/center/views/impactPathway/submenu-impactPathway-output.ftl" /]

        <span id="programSelected" class="hidden">${(selectedProgram.id)!}</span>
        [#-- Impact pathway sub menu --]
        <div class="simpleBox col-md-12">
            <label for="">Research Topic:  </label>
            <p>${selectedResearchTopic.researchTopic}</p>
        </div>
        
        <div class="clearfix"></div>
        
        [@s.form action=actionName enctype="multipart/form-data" ]
        
        <div class="borderBox">
          <h5 class="sectionSubTitle">Next-Users</h5>
          [#-- Next User Types --]
          [#if outputNextUsers?has_content]
             <div class="simpleBox col-md-12">
            <label for="">Type:<span class="red">*</span></label>
            <select name="outputNextUsers" id="outputNextUsers">
              <option value="-1" >Select an option</option>
                [#list outputNextUsers as nextUser]
                  <option value="${nextUser.id}">${nextUser.name}</option>
                [/#list]
            </select>
          </div>
          
          
           [#-- Select Next User Sub Types --]
           [#if outputNextSubUsers?has_content]
           [#-- Sub Types --]
           <div class="simpleBox col-md-12">
            <label for="">Sub Type:<span class="red">*</span></label>
            <select name="outputNextSubUsers" id="outputNextSubUsers">
              <option value="-1" >Select an option</option>
                [#list outputNextSubUsers as subType]
                  <option value="${nextSubUser.id}">${nextSubUser.name}</option>
                [/#list]
            </select>
          </div>
           [#-- Next User Sub Types --]
             <div class="simpleBox col-md-12">
            <label for="">Sub Type:<span class="red">*</span></label>
            <select name="outputNextSubUsers" id="outputNextSubUsers">
              <option value="-1" >Select an option</option>
                [#list outputNextSubUsers as subType]
                  <option value="${nextSubUser.id}">${nextSubUser.name}</option>
                [/#list]
            </select>
          </div>
           [#-- Next User Sub Types --]
            <div class="simpleBox col-md-12">
            <label for="">Sub Type:<span class="red">*</span></label>
            <select name="outputNextSubUsers" id="outputNextSubUsers">
              <option value="-1" >Select an option</option>
                [#list outputNextSubUsers as nextSubUser]
                  <option value="${nextSubUser.id}">${nextSubUser.name}</option>
                [/#list]
            </select>
          </div>
            [/#if] 
          [/#if] 
        </div>
          
        [#-- Section Buttons--]
        [#include "/WEB-INF/center/views/impactPathway/buttons-impactPathway-output.ftl" /]
        
        [#-- Hidden inputs --]
        <input type="hidden" name="typeID" value="${typeID}" />
          
        [/@s.form]
        
      </div>
    </div>
    
  </div>
</section>

[#include "/WEB-INF/center/pages/footer.ftl" /]
