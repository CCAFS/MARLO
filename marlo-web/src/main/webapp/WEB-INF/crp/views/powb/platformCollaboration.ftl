[#ftl]
[#assign title = "POWB Report" /]
[#assign currentSectionString = "powb-${actionName?replace('/','-')}-${liaisonInstitutionID}" /]
[#assign pageLibs = [ ] /]
[#assign customJS = [ ] /]
[#assign customCSS = ["${baseUrlMedia}/css/powb/powbGlobal.css"] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "platformCollaboration" /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"powbReport", "nameSpace":"powb", "action":"${crpSession}/summaryHighlight"},
  {"label":"platformCollaboration", "nameSpace":"powb", "action":"${crpSession}/platformCollaboration"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]

[#-- Helptext --]
[@utilities.helpBox name="platformCollaboration.help" /]
    
<section class="container">
  [#-- Program (Flagships and PMU) --]
  [#include "/WEB-INF/crp/views/powb/submenu-powb.ftl" /]
  
  <div class="row">
    [#-- POWB Menu --]
    <div class="col-md-3">
      [#include "/WEB-INF/crp/views/powb/menu-powb.ftl" /]
    </div> 
    <div class="col-md-9">
      [#-- Section Messages --]
      [#include "/WEB-INF/crp/views/powb/messages-powb.ftl" /]
      
      [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
         
        [#-- Title --]
        <h3 class="headTitle">[@s.text name="platformCollaboration.title" /]</h3>
        <div class="borderBox">
          <h5 class="sectionSubTitle">[@s.text name="platformCollaboration.platformsCollaborations" /]</h5>
          <div class="collaborationBlock">
            <div class="items-list">
              [#list 1..2 as element]
                [@platformCollaboration element=element name="liaisonInstitution.powb.platformsCollaborations" index=element_index /]
              [/#list]
            </div>
            [#-- Add Item --]
            [#if editable] 
              <div class="text-center">
                <div class="dddCollaboration bigAddButton"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="form.buttons.addFlagshipProgram" /]</div>
              </div>
            [/#if]
          </div>
          
          
          [#-- Other Flagships platforms Collaborations --]
          <h5 class="sectionSubTitle">[@s.text name="platformCollaboration.otherCollaborations" /]</h5>
          <div class="form-group">
            <table class="">
              <thead>
                <tr>
                  <th>Platform Name</th>
                  <th>Platform Description</th>
                  <th>Flagship</th>
                </tr>
              </thead>
              <tbody>
                [#list 2..3 as platform]
                <tr>
                  <td>Big Data</td>
                  <td>Lorem ipsum dolor sit amet, consectetur adipisicing elit. A earum delectus placeat reiciendis ducimus aspernatur animi vel corporis incidunt numquam odit esse natus illo ipsum pariatur blanditiis eius totam sed?</td>
                  <td>F${platform}</td>
                </tr>
                [/#list]
              </tbody>
            </table>
          </div>
        </div>
        
        [#-- Section Buttons & hidden inputs--]
        [#include "/WEB-INF/crp/views/powb/buttons-powb.ftl" /]
        
      [/@s.form] 
    </div> 
  </div> 
</section>
[#include "/WEB-INF/crp/pages/footer.ftl"]

[#macro platformCollaboration element name index ]
  [#local customName = "${name}[${index}]"]
  <div class="simpleBox">
    <div class="form-group row">
      <div class="col-md-8">
        [@customForm.input name="${customName}.name" i18nkey="${name}.name" required=true className="" editable=editable /]
      </div>
      <div class="col-md-4">
        [@customForm.input name="${customName}.flagship" i18nkey="${name}.flagship" required=true className="" editable=editable && PMU /]
      </div>
    </div>
    <div class="form-group">
      [@customForm.textArea name="${customName}.description" i18nkey="${name}.description" required=true className="limitWords-100" editable=editable /]
    </div>
  </div>
[/#macro]