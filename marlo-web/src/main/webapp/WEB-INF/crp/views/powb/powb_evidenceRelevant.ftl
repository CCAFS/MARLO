[#ftl]
[#assign title = "POWB Synthesis" /]
[#assign currentSectionString = "powb-${actionName?replace('/','-')}-${liaisonInstitutionID}" /]
[#assign pageLibs = [ ] /]
[#assign customJS = [ ] /]
[#assign customCSS = ["${baseUrlMedia}/css/powb/powbGlobal.css"] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "evidenceRelevant" /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"powbReport", "nameSpace":"powb", "action":"${crpSession}/adjustmentsChanges"},
  {"label":"evidenceRelevant", "nameSpace":"powb", "action":"${crpSession}/evidenceRelevant"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]

[#-- Helptext --]
[@utilities.helpBox name="evidenceRelevant.help" /]
    
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
        <h3 class="headTitle">[@s.text name="evidenceRelevant.title" /]</h3>
        <div class="borderBox">
          [#-- Provide a short narrative for any outcome --]
          <div class="form-group">
            [#-- Change display=true for display=PMU to show just for PMU --]
            [@customForm.textArea name="evidenceRelevant.narrative" help="evidenceRelevant.help" display=true required=true className="limitWords-100" editable=editable /]
          </div>
          <div class="form-group">
            [#-- Title --]
            <h3 class="subTitle headTitle">[@s.text name="evidenceRelevant.table.title" /]</h3>
            <hr />
          </div>
        </div>
        
        [#-- Section Buttons & hidden inputs--]
        [#include "/WEB-INF/crp/views/powb/buttons-powb.ftl" /]
        
      [/@s.form] 
    </div> 
  </div> 
</section>
[#include "/WEB-INF/crp/pages/footer.ftl"]

[#macro tableB]
  <table class="table-plannedStudies" id="table-plannedStudies">
    <thead>
      <tr class="subHeader">
        <th id="tb-fp">[@s.text name="evidenceRelevant.table.fp" /]</th>
        <th id="tb-plannedTopic" >[@s.text name="evidenceRelevant.table.plannedTopic" /]</th>
        <th id="tb-geographicScope">[@s.text name="evidenceRelevant.table.geographicScope" /]</th>
        <th id="tb-relevant">[@s.text name="evidenceRelevant.table.relevant" /]</th>
        <th id="tb-comments">[@s.text name="evidenceRelevant.table.comments" /]</th>
      </tr>
    </thead>
    <tbody>
    [#-- if deliverables?has_content 1--]
      [#-- list deliverables as deliverable 2--]
        <tr>
          [#-- FP --]
          <td class="tb-fp">
            <a href="[#-- @s.url namespace=namespace action=defaultAction][@s.param name='deliverableID']${deliverable.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url--]">
              [#-- F${deliverable.id} --]
              F123
            </a>
          </td>
          [#-- Planned topic of study --]
          <td class="left">
            [#-- if deliverable.deliverableInfo.title?has_content 3--]
                <a href="[@s.url namespace=namespace action=defaultAction] [@s.param name='deliverableID']${deliverable.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]" title="${deliverable.deliverableInfo.title}">
                [#if deliverable.deliverableInfo.title?length < 120] 
                  ${deliverable.deliverableInfo.title}
                [#else] 
                  [@utilities.wordCutter string=deliverable.deliverableInfo.title maxPos=120 /]
                [/#if]
                </a> 
            [#-- else 3--]
              [#if action.canEdit(deliverable.id)]
                <a href="[@s.url namespace=namespace action=defaultAction includeParams='get'] [@s.param name='deliverableID']${deliverable.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url] ">
                  [@s.text name="projectsList.title.none" /]
                </a>
              [#else]
              [@s.text name="projectsList.title.none" /]
              [/#if]
            [#-- [/#if] 3--]
          </td>
          [#-- Deliverable Type --]
          <td >
            ${(deliverable.deliverableInfo.deliverableType.name?capitalize)!'None'}
          </td>
          [#-- Deliverable Year --]
          <td class="text-center">
          [#if deliverable.deliverableInfo.year== -1]
            None
          [#else]
            ${(deliverable.deliverableInfo.year)!'None'}
            [#if deliverable.status?? && deliverable.status==4 && deliverable.newExpectedYear??]
              Extended to ${deliverable.newExpectedYear}
            [/#if]
          [/#if]
            
          </td>
          [#if isReportingActive]
            [#-- Deliverable FAIR compliance --]
            <td class="fair text-center"> 
            [#if deliverable.deliverableInfo.requeriedFair()]
              <span class="[#attempt][#if action.isF(deliverable.id)??][#if action.isF(deliverable.id)] achieved [#else] notAchieved [/#if][/#if][#recover][/#attempt]">F</span>
              <span class="[#attempt][#if action.isA(deliverable.id)??][#if action.isA(deliverable.id)] achieved [#else] notAchieved [/#if][/#if][#recover][/#attempt]">A</span>
              <span class="[#attempt][#if action.isI(deliverable.id)??][#if action.isI(deliverable.id)] achieved [#else] notAchieved [/#if][/#if][#recover][/#attempt]">I</span>
              <span class="[#attempt][#if action.isR(deliverable.id)??][#if action.isR(deliverable.id)] achieved [#else] notAchieved [/#if][/#if][#recover][/#attempt]">R</span>
            [#else]
              <p class="message">Not applicable</p>
            [/#if]
            </td>
          [/#if]
          [#-- Deliverable Status --]
          <td class="text-center">
            [#attempt]
              <div class="status-container">
                <div class="status-indicator ${(deliverable.deliverableInfo.getStatusName(action.getActualPhase()))!'None'}" title="${(deliverable.deliverableInfo.getStatusName(action.getActualPhase()))!'None'}"></div>
                <span class="hidden">${(deliverable.deliverableInfo.getStatusName(action.getActualPhase()))!'None'}</span>
              </div>
            [#recover]
              None
            [/#attempt]
          </td>
          [#-- Deliverable required fields --]
          <td class="text-center">
            [#if isDeliverableComplete]
              <span class="icon-20 icon-check" title="Complete"></span>
            [#else]
              <span class="icon-20 icon-uncheck" title="Required fields still incompleted"></span> 
            [/#if]
            
            [#if isDeliverableNew]
              <a id="removeDeliverable-${deliverable.id}" class="removeDeliverable" href="${baseUrl}/projects/${crpSession}/deleteDeliverable.do?deliverableID=${deliverable.id}" title="">
                <div class="icon-container"><span class="trash-icon glyphicon glyphicon-trash"></span><div>
              </a>
            [/#if]
          </td>
        </tr>
      [#-- [/#list] 2--]
      [#-- [/#if] 1--]
    </tbody>
  </table>
[/#macro]