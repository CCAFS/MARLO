[#ftl]
[#assign title = "Output" /]
[#assign currentSectionString = "program-${actionName?replace('/','-')}-${outputID}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/impactPathway/output.js", 
  "${baseUrl}/global/js/fieldsValidation.js", 
  "${baseUrl}/global/js/autoSave.js"
  ] 
/]
[#assign customCSS = [
  "${baseUrl}/global/css/customDataTable.css",
  "${baseUrlMedia}/css/impactPathway/outputList.css"
  ] 
/]
[#assign currentSection = "impactPathway" /]
[#assign currentStage = "outputs" /]
[#assign currentSubStage = "mainInformation" /]

[#assign breadCrumb = [
  {"label":"impactPathway", "nameSpace":"impactPathway", "action":"programImpacts"},
  {"label":"outputsList", "nameSpace":"${centerSession}/impactPathway", "action":"outputsList"},
  {"label":"output", "nameSpace":"", "action":""}
]/]
[#assign leadersName = "leaders"/]
[#include "/WEB-INF/center/pages/header.ftl" /]
[#include "/WEB-INF/center/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/utils.ftl" as utils /]
[#import "/WEB-INF/center/views/impactPathway/outputListTemplate.ftl" as outcomesList /]
[#--  Research Otcomes Help Text--] 
[@utils.helpInfos hlpInfo="researchOutputs.help" /]
[#assign outputCustomName= "output" /]

[#--  marlo cluster of activities--]
<section class="marlo-content">
  <div class="container"> 
    
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/center/views/impactPathway/menu-impactPathway.ftl" /]
      </div>
      <div class="col-md-9">
        [#-- Impact pathway sub menu --]
        [#include "/WEB-INF/center/views/impactPathway/submenu-impactPathway-output.ftl" /]
        [#-- Section Messages --]
        [#include "/WEB-INF/center/views/impactPathway/messages-impactPathway-output.ftl" /]
        
         

        <span id="programSelected" class="hidden">${(selectedProgram.id)!}</span>
        
        
        
        [#-- Back --]
        <h5 class="pull-right">
          <a href="[@s.url action='${centerSession}/outputsList'][@s.param name="programID" value=programID /][@s.param name="outcomeID" value=outcomeID /][@s.param name="edit" value=true /][/@s.url]">
            <span class="glyphicon glyphicon-circle-arrow-left"></span> Back to the outputs list
          </a>
        </h5>
        
        [@s.form action=actionName enctype="multipart/form-data" ]
        
        
        
        <h3 class="headTitle"> Output General Information </h3>
        <div class="borderBox">
          <!--h5 class="sectionSubTitle"> Output description</h5-->
          
          [#-- Output Name --]
          <div class="form-group">
            [@customForm.textArea name="${outputCustomName}.title"  i18nkey="output.name" required=true className="output-name limitWords-50" editable=editable /]
            
            <div class="row">
              <div class="col-sm-7">[@customForm.input name="${outputCustomName}.shortName" i18nkey="output.shortName" className="limitChar-30" required=false editable=editable /]</div>       
            </div> 
            
          </div> 
          
          [#-- Research topic --]
          <div class="form-group">
              <label for="">Research Topic:</label><p>${selectedResearchTopic.researchTopic}</p>
          </div>
          
          [#-- Outcome --]
          <div class="form-group">
              <label for="">Outcome:</label><p>${selectedResearchOutcome.description}</p>
          </div>
          
          [#-- Contact Person --]
          [#if contacPersons?has_content]
            <label for="">Contact Person(s):  </label>
            [#list contacPersons as contacPerson]
            <p> <span class="glyphicon glyphicon-user"></span> ${contacPerson.user.composedCompleteName}</p>
            [/#list]
          [/#if] 
        </div>
        
        <h3 class="headTitle"> Next Users </h3>
        <div class="borderBox nextUsers-list" listname="${outputCustomName}.nextUsers">
          [#if output.nextUsers?has_content]
            [#list output.nextUsers as nextUser]
            [@nextUserMacro nextUser=nextUser name="${outputCustomName}.nextUsers" index=nextUser_index /]
            [/#list]
            [#else]
            [@nextUserMacro nextUser={} name="${outputCustomName}.nextUsers" index=0 /]
          [/#if] 
        </div>
        [#if editable] 
        <div class="text-right">
          <div class="addNextUser button-blue text-right"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="Add a new Next User"/]</div>
        </div>
        [/#if]
        [#-- Section Buttons--]
        [#include "/WEB-INF/center/views/impactPathway/buttons-impactPathway-output.ftl" /]   
        [/@s.form]
        
      </div>
    </div>
    
  </div>
</section>

[#-- Macros --]
[@nextUserMacro nextUser={} name="output.nextUsers" index=-1 template=true/]

[#include "/WEB-INF/center/pages/footer.ftl" /]

[#macro nextUserMacro nextUser name index template=false]
  [#assign nextUserCustomName = "${name}[${index}]" /]
  <div id="nextUser-${template?string('template', index)}" class="nextUser borderBox form-group" style="position:relative; display:${template?string('none','block')}">
    [#-- Remove Button --]
    [#if editable]
    <div class="removeNextUser removeIcon" title="Remove assumption"></div>
    [/#if]
    <input type="hidden" class="nextUserId" name="${nextUserCustomName}.id" value="${(nextUser.id)!}"/>
  
  [#-- Type select --]
  <div class="col-md-6">
  [@customForm.select name="${nextUserCustomName}.nextuserType.nextuserType.id" label=""  i18nkey="Type" listName="nextuserTypes" keyFieldName="id"  displayFieldName="name"  multiple=false required=true  className="typeSelect form-control input-sm " editable=editable/]
  </div>   

  [#-- SubType select --]
  <div class="col-md-6">
  [@customForm.select name="${nextUserCustomName}.nextuserType.id" label=""  i18nkey="Subtype" listName="${nextUserCustomName}.nextuserType.nextuserType.nextuserTypes" keyFieldName="id"  displayFieldName="name"  multiple=false required=true  className="subTypeSelect form-control input-sm " editable=editable/]
  </div>
  
  <div class="clearfix"></div>
  </div>
[/#macro]