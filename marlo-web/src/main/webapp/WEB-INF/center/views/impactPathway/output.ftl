[#ftl]
[#assign title = "Output" /]
[#assign currentSectionString = "program-${actionName?replace('/','-')}-${outputID}-phase-${(actualPhase.id)!}" /]
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
          <a href="[@s.url action='${centerSession}/outputsList'][@s.param name="programID" value=programID /][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
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
          
          [#-- Outcome Contributions--]
        <h3 class="headTitle"> Outcome Contributions </h3>
        <div class="borderBox nextUsers-list" listname="${outputCustomName}.nextUsers">
        
          <div class="form-group">      
            <div class="output panel tertiary">
              <div class="panel-body" listname="output.outcomeList"> 
                <ul id="outputsBlock" class="list outputList">
                [#if  output.outcomes?has_content]  
                  [#list output.outcomes as outcome]
                     [@outcomesMacro element=outcome name="output.outcomes" index=outcome_index isTemplate=false/]
                  [/#list] 
                [#else]
                  <p class="text-center outputInf"> [@s.text name="projectDescription.notOutputs" /] </p>  
                [/#if]  
                </ul>
                [#if editable]
                  <select name="" class="outputSelect">
                    <option value="-1">Select an option...</option>
                    [#list outcomes as outcome]
                      <optgroup  label="${(outcome.researchTopic.researchTopic)!}">
                        [#list outcome.outcomes as op]<option value="${(op.id)!}">${(op.composedName)!}</option>[/#list]
                      </optgroup>
                    [/#list]
                  </select>
                [/#if] 
              </div>              
            </div>
          </div>
          
        </div>  
          
        <h3 class="headTitle"> Next Users </h3>
        <div class="borderBox nextUsers-list" listname="${outputCustomName!''}.nextUsers">
          [#if output.nextUsers?has_content]
            [#list output.nextUsers as nextUser]
              [@nextUserMacro nextUser=nextUser name="output.nextUsers" index=nextUser_index /]
            [/#list]
          [#else]
              [@nextUserMacro nextUser={} name="output.nextUsers" index=0 /]
          [/#if] 
        </div>
        [#if editable] 
        <div class="text-right">
          <div class="addNextUser button-blue text-right"><span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span> [@s.text name="Add a new Next User"/]</div>
          <hr />
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
[@outcomesMacro element={} name="output.outcomes"  index=-1 isTemplate=true /]

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

[#macro outcomesMacro element name index=-1 isTemplate=false]  
  [#assign outputCustomName = "${name}[${index}]" /]
  <li id="output-${isTemplate?string('template',(element.id)!)}" class="outputs  borderBox row "  style="display:${isTemplate?string('none','block')}">
  <input type="hidden" name="${outputCustomName}.id" value="${(element.id)!}"/>
  <input type="hidden" class="outputId" name="${outputCustomName}.centerOutcome.id" value="${(element.centerOutcome.id)!}"/>
    [#if editable] [#--&& (isTemplate) --]
      <div class="removeLink">
        <div id="" class="removeOutput removeElement removeLink" title="[@s.text name='projectDecription.removeOutput' /]"></div>
      </div>
    [/#if]
    <div class="leftHead">
      <span class="index">OC${(element.centerOutcome.id)!}</span>
    </div>
    [#-- Output Title --]
    <div class="blockTitle closed form-group" style="margin-top:30px;">
      <label for="">Outcome statement:</label>
      <div class="oStatement">
        [#if element.centerOutcome?? && element.centerOutcome.description?has_content]
        ${(element.centerOutcome.description)!'New Output'}
        [#else]
        No Output
        [/#if]
      </div>
        
      <div class="clearfix"></div>
    </div>
  </li>
[/#macro]