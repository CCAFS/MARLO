[#ftl]
[#assign title = "Impact Pathway - Research Topics" /]
[#assign currentSectionString = "program-${actionName?replace('/','-')}-${programID}" /]
[#assign pageLibs = ["cytoscape","cytoscape-panzoom","select2", "vanilla-color-picker"] /]
[#assign customJS = [
  "${baseUrl}/global/js/usersManagement.js", 
  "${baseUrlMedia}/js/impactPathway/researchTopics.js", 
  "${baseUrl}/global/js/fieldsValidation.js", 
  "${baseUrl}/global/js/autoSave.js"
  ] 
/]
[#assign customCSS = [ 
  "${baseUrlMedia}/css/impactPathway/clusterActivities.css" 
  ] 
/]
[#assign currentSection = "impactPathway" /]
[#assign currentStage = "researchTopics" /]

[#assign breadCrumb = [
  {"label":"impactPathway", "nameSpace":"", "action":"topics"}
]/]
[#assign leadersName = "leaders"/]
[#include "/WEB-INF/center/pages/header.ftl" /]
[#include "/WEB-INF/center/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/utils.ftl" as utils /]
[#--  Research Topics Help Text--] 
[@utils.helpInfos hlpInfo="researchTopics.help" /]
[#--  marlo cluster of activities--]
<section class="marlo-content">
  <div class="container"> 
    [#if researchAreas?has_content]
    <div class="row">
      <div class="col-md-3">
        [#include "/WEB-INF/center//views/impactPathway/menu-impactPathway.ftl" /]
      </div>
      <div class="col-md-9">
        [#-- Impact pathway sub menu --]
        [#include "/WEB-INF/center//views/impactPathway/submenu-impactPathway.ftl" /]
        [#-- Section Messages --]
        [#include "/WEB-INF/center//views/impactPathway/messages-impactPathway.ftl" /]
        
        [#-- Title --]
        <div class="col-md-12">
          <h3 class="subTitle headTitle outcomeListTitle">Research Topics</h3>
          <hr />
        </div><div class="clearfix"></div>
        
        [@s.form action=actionName enctype="multipart/form-data" ]     
          <div id="researchTopics" class="outcomes-list" listname="researchTopics">
          [#if topics?has_content]
            [#list topics as topic]
              [@topicMacro element=topic name="topics" index=topic_index /]
            [/#list]
          [#else]
            [@topicMacro element={} name="topics" index=0 /]
          [/#if]          
          </div>
          <div class="clearfix"></div>
          [#-- Add Research Impact Button --]
          [#if editable]
            <div class="addResearchTopic bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span>[@s.text name="form.buttons.addResearchTopic"/]</div>
          [/#if]
        
          
          [#-- Section Buttons--]
          [#include "/WEB-INF/center//views/impactPathway/buttons-impactPathway.ftl" /]
          
          
        [/@s.form]
      </div>
    </div>
    [#else]
     <p class="text-center borderBox inf">Program impacts are not available in for the selected research area</p>
    [/#if]
  </div>
</section>
[#-- Outcome Template --]
[@topicMacro element={} name="topics" index=-1 template=true /]

[#include "/WEB-INF/center/pages/footer.ftl" /]

[#-- MACROS --]
[#macro topicMacro element name index template=false]
  <div id="researchTopic-${template?string('template','')}" class="borderBox researchTopic" style="display:${template?string('none','block')}">
    [#local customName = "${name}[${index}]" /]
    <div class="invisibleDrag"></div>
    [#-- Remove Button --]
    [#if editable]
    [#if element.id?has_content]
      [#if template || action.centerCanBeDeleted(element.id, element.class.name)!false]
        <span class="glyphicon glyphicon-remove pull-right removeResearchTopic" style="color:#FF0000" aria-hidden="true"></span>
      [#else]
        <span class="glyphicon glyphicon-remove pull-right" style="color:#ccc" aria-hidden="true" title="Can not be deleted"></span>
      [/#if]
    [#else]  
      <span class="glyphicon glyphicon-remove pull-right removeResearchTopic" style="color:#FF0000" aria-hidden="true"></span>
    [/#if] 
    [/#if]
    
    <div class="leftHead">
      <span class="index">${index+1}</span>
    </div>
    <br />
    [#--  Hidden inputs --]
    <input type="hidden" name="${customName}.id" value="${(element.id)!}"/>
    <input type="hidden" name="${customName}.order" value="${(index)!}" class="order-index"/>
    [#-- Research Topic Name --]
    <div class="form-group"> 
      <div class="row">
        <div class="col-sm-12">[@customForm.input name="${customName}.researchTopic" type="text"  i18nkey="researchTopic.name" className="researchTopicInput limitWords-15" required=true editable=editable /]</div>
      </div>  
      
      <div class="row">
        <div class="col-sm-7">[@customForm.input name="${customName}.shortName" i18nkey="researchTopic.shortName" className="limitChar-30" required=false editable=editable /]</div>       
      </div>    
    </div>
  </div>  
[/#macro]


