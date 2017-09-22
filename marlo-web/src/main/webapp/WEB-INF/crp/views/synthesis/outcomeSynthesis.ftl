[#ftl]
[#assign canEdit = true /]
[#assign editable = true /]

[#assign title = "Outcome Synthesis" /]
[#assign currentSectionString = "synthesis-${actionName?replace('/','-')}-${liaisonInstitutionID}" /]
[#assign pageLibs = [ "datatables.net", "datatables.net-bs" ] /]
[#assign customJS = ["${baseUrlMedia}/js/synthesis/outcomeSynthesis.js"] /]
[#assign customCSS = ["${baseUrlMedia}/css/synthesis/synthesisGlobal.css"] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "outcomeSynthesis" /]

[#assign breadCrumb = [
  {"label":"synthesis", "nameSpace":"/synthesis", "action":"crpContributors"},
  {"label":"outcomeSynthesis", "nameSpace":"", "action":""}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]

<section class="container">
  <div class="row"> 
    <div class="col-md-12">
      [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass="pure-form"]
      
      [#-- Title --]
      <h3 class="headTitle text-center">[@s.text name="synthesis.outcomeSynthesis.title" ][@s.param]${(currentLiaisonInstitution.name)!}[/@s.param][/@s.text]</h3>
       
      [#-- Program (Regions and Flagships) --]
      <ul id="liaisonInstitutions" class="horizontalSubMenu text-center">
        [#list liaisonInstitutions as institution]
          [#assign isActive = (institution.id == liaisonInstitutionID)/]
          [#assign isCompleted = false /]
          [#assign hasPermission = (action.hasSynthesisPermission('update', institution.id))!false/]
          <li class="${isActive?string('active','')} ${hasPermission?string('canEdit','')}">
            <a href="[@s.url][@s.param name ="liaisonInstitutionID"]${institution.id}[/@s.param][@s.param name ="edit"]true[/@s.param][/@s.url]">${institution.acronym}: ${institution.name}</a>
            [#if isCompleted] <p class="synthesisCompleted"> <img src="${baseUrl}/global/images/icon-check-tiny${isActive?string('-white','')}.png" /> </p> [/#if]
          </li>
        [/#list]
      </ul>
      
      [#-- Outcomes 2019 --]
      <div id="outcomeSynthesisBlock" class="">
        [#list midOutcomes as midOutcome]
        <div class="outcomeSynthesis borderBox"> 
          [#-- Outcome title --]
          <h6 class="title">${(currentLiaisonInstitution.acronym)!} - ${midOutcome.composedId} <span class="ipElementId">ID ${midOutcome.id}</span></h6>
          <p>${midOutcome.description}</p>
           
          [#-- Milestones --]
          <div class="col-md-12"> 
            [#-- Outcome Miliestones --]
            [#list outcomeMilestones as milestone]
              [#assign flagshipIndicator = (indicator.parent)!milestone /]
              [#assign index = (action.getIndex(flagshipIndicator.id,midOutcome.id,program.id))!-1 /]
           
              <div class="form-group simpleBox">
                [#-- Milestone title --]
                <div class="form-group grayBox">
                  <h4 class="subHeadTitle"> Milestone for ${milestone.year}:</h4> 
                  <p>${milestone.description}</p>
                </div>
               
                [#-- Achieved target in current reporting period --]
                [#assign showMilestoneValue = milestone.srfTargetUnit??  && milestone.srfTargetUnit.id?? && (milestone.srfTargetUnit.id != -1) /]
                <div class="row form-group milestoneTargetValue" style="display:${showMilestoneValue?string('block', 'none')}">
                  <div class="col-md-4">
                    [@customForm.input name="synthesis[${index}].achievedExpectedText" type="text" i18nkey="synthesis.outcomeSynthesis.targetAchievedExpected" className="isNumeric" help="form.message.numericValue" required=canEdit editable=false /]
                  </div>
                  <div class="col-md-4">
                    <div class="select">
                      <label for="">[@s.text name="projectOutcomeMilestone.expectedUnit" /]:</label>
                      <div class="selectList"><p class="crpMilestoneTargetUnit">${(milestone.srfTargetUnit.name)!}</p></div> 
                    </div>
                  </div> 
                  <div class="col-md-4">
                    [@customForm.input name="synthesis[${index}].achievedText" type="text" i18nkey="synthesis.outcomeSynthesis.targetAchieved" className="isNumeric" help="form.message.numericValue" required=canEdit editable=editable /]
                  </div>
                </div>
                
                [#-- Synthesis of annual progress towards this milestone --]
                <div class="fullPartBlock">
                  [@customForm.textArea name="synthesis[${index}].synthesisAnual" i18nkey="synthesis.outcomeSynthesis.progressIndicator" className="progressIndicator limitWords-250" required=canEdit editable=editable /]
                </div>
                
                [#-- Project Contributions --]
                <label>[@s.text name="synthesis.outcomeSynthesis.projectContributions" /]:</label> 
                [#if (projectMilestoneContributions)?has_content]
                <div class="fullPartBlock synthesisContributions-block viewMoreSyntesis-block">
                  <table class="projectContributions">
                    <thead>
                      <tr class="header">
                        <th class="col-projectId">Project</th>
                        <th class="col-expected">Target Expected</th>
                        <th class="col-achieved">Target Achieved</th> 
                        <th class="col-synthesis">Narrative of achieved targets, including evidence</th> 
                      </tr>
                  	</thead>
                  	<tbody>
                    [#list projectMilestoneContributions as projectIndicator]
                      <tr>
                      	<td class="center"><a href="[@s.url action="${crpSession}/contributionsCrpList" namespace="/projects"][@s.param name='projectID']${(projectIndicator.projectId)!}[/@s.param][@s.param name='edit']true[/@s.param][/@s.url]">P${(projectIndicator.projectId)!}</a></td>
                      	<td class="center" title="${(projectIndicator.target)!''}" >[@utilities.wordCutter string=(projectIndicator.target)!'Prefilled when available' maxPos=25 /]</td>
                      	<td class="center" title="${(projectIndicator.achieved)!''}" >[@utilities.wordCutter string=(projectIndicator.achieved)!'Prefilled when available' maxPos=25 /]</td>
                        <td class="">${(projectIndicator.narrativeTargets)!'Prefilled when available'} </td> 
                      </tr>
                    [/#list]
                  	</tbody>
                  </table>
                  <div class="viewMoreSyntesis closed"></div>
                </div>
                [#else]
                  <p>There is not project contributing to this milestone</p>
                [/#if]
                <br />
              </div>
            [/#list]
          </div>
           
        </div>
        [/#list]
      </div>
      
      [#-- Synthesis Lessons --]
      <div id="lessons" class="borderBox">
        <div class="fullBlock">
          <input type="hidden" name="projectLessons.id" value=${(projectLessons.id)!"-1"} />
          <input type="hidden" name="projectLessons.year" value=${reportingYear} />
          <input type="hidden" name="projectLessons.componentName" value="${actionName}">
          [@customForm.textArea name="projectLessons.lessons" i18nkey="synthesis.outcomeSynthesis.lessons" paramText="${program.flagshipProgram?string('project/regional', 'project')}" help="synthesis.outcomeSynthesis.lessons.help" className="synthesisLessons limitWords-100" required=true editable=editable /]
        </div>
        <br />
      </div>
      
      [#-- Hidden inputs --]
      <input type="hidden" name="liaisonInstitutionID" value="${liaisonInstitutionID}"  /> 
       
      [/@s.form] 
      
    </div>
  </div>
</section> 


[#include "/WEB-INF/crp/pages/footer.ftl"]
