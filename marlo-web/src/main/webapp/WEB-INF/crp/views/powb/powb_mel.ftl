[#ftl]
[#assign title = "POWB Synthesis" /]
[#assign currentSectionString = "powb-${actionName?replace('/','-')}-${powbSynthesisID}" /]
[#assign pageLibs = [ ] /]
[#assign customJS = ["${baseUrlMedia}/js/powb/powb_monitoringLearning.js"] /]
[#assign customCSS = ["${baseUrlMedia}/css/powb/powbGlobal.css"] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "monitoringLearning" /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"powbReport", "nameSpace":"powb", "action":"${crpSession}/adjustmentsChanges"},
  {"label":"monitoringLearning", "nameSpace":"powb", "action":""}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#-- Helptext --]
[@utilities.helpBox name="monitoringLearning.help" /]
    
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
        <h3 class="headTitle">[@s.text name="monitoringLearning.title" /]</h3>
        <div class="borderBox">
          [#-- Please highlight any areas of interest for evaluation, review ... --]
          [#if PMU]
          <div class="form-group margin-panel">
            [#-- Change display=true for display=PMU to show just for PMU --]
            [@customForm.textArea name="powbSynthesis.powbMonitoringEvaluationLearning.highlight" i18nkey="monitoringLearning.areasOfInterest" help="monitoringLearning.areasOfInterest.help" display=true required=true className="limitWords-100" paramText="${(actualPhase.year)!}" editable=editable /]
          </div>
          [/#if]
          
          [#-- Table Flagships planned Monitoring, Evaluation and Learning Exercises --]
          [#if PMU]
          <div class="form-group margin-panel">
            <h4 class="subTitle headTitle">[@s.text name="monitoringLearning.table.title" /]</h4>
            <hr />
            [@tableFlagshipsPlannedMacro /]
          </div>
          [/#if]
          
          [#-- Planned Monitoring Evaluation, and Learning Exercises --]
          [#if Flagship]
          <div class="form-group margin-panel">
            <div class="evidence-plannedStudies-header row">
              <h4 class="subTitle headTitle col-md-9">[@s.text name="monitoringLearning.plannedStudies" /]</h4>
            </div>
            <div class="expectedStudies-list" listname="list-exercises">
            [#if powbSynthesis.powbMonitoringEvaluationLearning.exercises?has_content]
              [#list powbSynthesis.powbMonitoringEvaluationLearning.exercises as exercise]
                [@flagshipsPlannedMELMacro element=exercise name="powbSynthesis.powbMonitoringEvaluationLearning.exercises"  index=exercise_index isEditable=editable/]
              [/#list]
            [#else]
              [#-- if !editable]<p>[@s.text name="monitoringLearning.plannedStudies.empty" /]</p>[/#if--]
            [/#if]
            </div>
            
            [#if canEdit && editable]
            <div class="text-right">
              <div class="addExpectedStudy bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="form.buttons.addPlannedStudyExercise"/]</div>
            </div> 
            [/#if]
          </div>
          [/#if]
          
        </div>
        
        [#-- Section Buttons & hidden inputs--]
        [#include "/WEB-INF/crp/views/powb/buttons-powb.ftl" /]
        
      [/@s.form] 
    </div> 
  </div> 
</section>

[#-- Planned Study Template --]
[@flagshipsPlannedMELMacro element={} name="powbSynthesis.powbMonitoringEvaluationLearning.exercises"  index=-1 template=true/]

[#include "/WEB-INF/global/pages/footer.ftl"]

[#macro flagshipsPlannedMELMacro element name index template=false isEditable=true]
  [#local customName = "${name}[${index}]" /]
  <div id="expectedStudy-${template?string('template', index)}" class="expectedStudy borderBox form-group" style="position:relative; display:${template?string('none','block')}">
    
    [#-- Index --]
    <div class="leftHead"><span class="index">${index+1}</span></div>
    [#-- Remove Button --]
    [#if isEditable]<div class="removeExpectedStudy removeElement" title="Remove Planned topic of study"></div>[/#if]
    [#-- Hidden inputs --]
    <input type="hidden" name="${customName}.id" value="${(element.id)!}"/> 
    <br />
    
    
    <div class="form-group row"> 
      [#-- Study/Learning exercise --] 
      <div class="col-xs-12">
        [@customForm.input name="${customName}.exercise" i18nkey="monitoringLearning.plannedStudies.studyLearningExercise" placeholder="" className="limitWords-100" required=true editable=isEditable/]
      </div>
    </div>
    [#-- Comments --] 
    <div class="form-group"> 
      [@customForm.textArea name="${customName}.comments" i18nkey="monitoringLearning.plannedStudies.comments"  placeholder="" className="limitWords-100" required=true editable=isEditable /]
    </div>
    
    <div class="clearfix"></div>
  </div>
[/#macro]

[#macro tableFlagshipsPlannedMacro]
  <table class="table-plannedStudies table-border-powb" id="table-plannedStudies">
    <thead>
      <tr class="subHeader">
        <th id="tb-fp" width="11%">[@s.text name="monitoringLearning.table.fp" /]</th>
        <th id="tb-plannedTopic" width="45%">[@s.text name="monitoringLearning.table.plannedStudies"][@s.param]${(actualPhase.year)!}[/@s.param][/@s.text]</th>
        <th id="tb-comments" width="44%">[@s.text name="monitoringLearning.table.comments" /]</th>
      </tr>
    </thead>
    <tbody>
    [#if flagshipExercises?has_content]
      [#list flagshipExercises as flagshipExercise]
        <tr>
          [#-- FP --]
          <td class="tb-fp text-center">
            <span class="programTag" style="border-color:${(flagshipExercise.powbMonitoringEvaluationLearning.powbSynthesis.liaisonInstitution.crpProgram.color)!'#fff'}">
              ${flagshipExercise.powbMonitoringEvaluationLearning.powbSynthesis.liaisonInstitution.crpProgram.acronym}
            </span>
          </td>
          [#-- Planned Study Exercise --]
          <td>
            [#if flagshipExercise.exercise?has_content]${(flagshipExercise.exercise)!''}[#else]<i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>[/#if]
          </td>
          [#-- Comments --]
          <td class="comments"> 
            [#if flagshipExercise.comments?has_content]${(flagshipExercise.comments)!''}[#else]<i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>[/#if]
          </td>
        </tr>
      [/#list]
    [#else]
      <tr>
        <td class="text-center" colspan="3">
          <i style="opacity:0.5">[@s.text name="global.prefilledWhenAvailable"/]</i>
        </td>
      </tr>
    [/#if]
    </tbody>
  </table>
[/#macro]