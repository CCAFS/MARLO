[#ftl]
[#assign canEdit = true /]
[#assign editable = true /]
[#assign midOutcomes = [] /]
[#assign program = {
  'flagshipProgram' : true
} /]
[#assign liaisonInstitutionID = 2 /]
[#assign liaisonInstitutions = [
  { 'id': 1, 'acronym': 'F1', 'name': 'Priorities and Policies for CSA'},
  { 'id': 2, 'acronym': 'F2', 'name': 'Climate-Smart Technologies and Practices'},
  { 'id': 3, 'acronym': 'F3', 'name': 'Low emissions development'},
  { 'id': 4, 'acronym': 'F4', 'name': 'Climate services and safety nets'}
] /]
[#assign currentLiaisonInstitution = liaisonInstitutions[1] /]
[#assign midOutcomes = [
  { 'id': 1, 'composedId': 'Outcome 2022', 'description': '10 policy decisions taken (in part) based on engagement and information dissemination by CCAFS.'}
] /]

[#assign outcomeMilestones = [
  { 'id': 1, 'description': 'Diagnosis on subnational policy and institutional frameworks analysis focusing on different options that can support the adoption of preferred CSA practices'}
] /]

[#assign projectMilestoneContributions = [
  { 'id': 1, 'projectId': '56', 'target': '2', 'achieved': '2', 'narrativeTargets': 'Lorem ipsum dolor sit amet, consectetur adipisicing elit. Quisquam error eveniet quam praesentium quasi impedit cumque odio culpa omnis eos ab aspernatur expedita aperiam sunt illum adipisci nesciunt! Modi quas!'},
  { 'id': 2, 'projectId': '22', 'target': '14', 'achieved': '11', 'narrativeTargets': 'Lorem ipsum dolor sit amet, consectetur adipisicing elit. Quisquam error eveniet quam praesentium quasi impedit cumque odio culpa omnis eos ab aspernatur expedita aperiam sunt illum adipisci nesciunt! Modi quas!'},
  { 'id': 3, 'projectId': '1', 'target': '1', 'achieved': '1', 'narrativeTargets': 'Lorem ipsum dolor sit amet, consectetur adipisicing elit. Quisquam error eveniet quam praesentium quasi impedit cumque odio culpa omnis eos ab aspernatur expedita aperiam sunt illum adipisci nesciunt! Modi quas!'},
  { 'id': 4, 'projectId': '16', 'target': '', 'achieved': '', 'narrativeTargets': 'Lorem ipsum dolor sit amet, consectetur adipisicing elit. Quisquam error eveniet quam praesentium quasi impedit cumque odio culpa omnis eos ab aspernatur expedita aperiam sunt illum adipisci nesciunt! Modi quas!'},
  { 'id': 5, 'projectId': '23', 'target': '8', 'achieved': '2', 'narrativeTargets': 'Lorem ipsum dolor sit amet, consectetur adipisicing elit. Quisquam error eveniet quam praesentium quasi impedit cumque odio culpa omnis eos ab aspernatur expedita aperiam sunt illum adipisci nesciunt! Modi quas!'}
] /]


[#assign title = "Outcome Synthesis" /]
[#assign currentSectionString = "synthesis-${actionName?replace('/','-')}-${liaisonInstitutionID}" /]
[#assign pageLibs = [ "datatables.net", "datatables.net-bs" ] /]
[#assign customJS = ["${baseUrl}/js/synthesis/outcomeSynthesis.js"] /]
[#assign customCSS = ["${baseUrl}/css/synthesis/synthesisGlobal.css"] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "outcomeSynthesis" /]

[#assign breadCrumb = [
  {"label":"synthesis", "nameSpace":"/synthesis", "action":"crpContributors"},
  {"label":"outcomeSynthesis", "nameSpace":"", "action":""}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<section class="container">
  <div class="row"> 
    <div class="col-md-12">
      [@s.form action="outcomeSynthesis" method="POST" enctype="multipart/form-data" cssClass="pure-form"]
      
      [#-- Program (Regions and Flagships) --]
      <ul id="liaisonInstitutions" class="horizontalSubMenu">
        [#list liaisonInstitutions as institution]
          [#assign isActive = (institution.id == liaisonInstitutionID)/]
          [#assign isCompleted = false /]
          [#assign hasPermission = (action.hasSynthesisPermission('update', institution.id))!false/]
          <li class="${isActive?string('active','')} ${hasPermission?string('canEdit','')}">
            <a href="[@s.url][@s.param name ="liaisonInstitutionID"]${institution.id}[/@s.param][@s.param name ="edit"]true[/@s.param][/@s.url]">${institution.acronym}: ${institution.name}</a>
            [#if isCompleted] <p class="synthesisCompleted"> <img src="${baseUrl}/images/global/icon-check-tiny${isActive?string('-white','')}.png" /> </p> [/#if]
          </li>
        [/#list]
      </ul>
       
      [#-- Title --]
      <h3 class="headTitle">[@s.text name="synthesis.outcomeSynthesis.title" ][@s.param]${(currentLiaisonInstitution.acronym)!}[/@s.param][/@s.text]</h3>
      
      [#-- Outcomes 2019 --]
      <div id="outcomeSynthesisBlock" class="">
        [#list midOutcomes as midOutcome]
        <div class="borderBox"> 
          <div class="fullPartBlock">
            <h6 class="title">${(currentLiaisonInstitution.acronym)!} - ${midOutcome.composedId} <span class="ipElementId">ID ${midOutcome.id}</span></h6>
            <p>${midOutcome.description}</p>
          </div>
          
          [#-- Milestones --]
          <div class="fullPartBlock">
            <h4 class="subHeadTitle">Milestones:</h4> 
            
            [#-- Outcome Miliestones --]
            [#list outcomeMilestones as indicator]
              [#assign flagshipIndicator = (indicator.parent)!indicator /]
              [#assign index = (action.getIndex(flagshipIndicator.id,midOutcome.id,program.id))!-1 /]
           
              <div class="simpleBox">
                <div class="fullPartBlock"><p>${flagshipIndicator.description}</p></div>
                [#-- Achieved target in current reporting period --]
                <div class="fullPartBlock">
                  <div class="thirdPartBlock">[@customForm.input name="synthesis[${index}].achievedText" type="text" i18nkey="synthesis.outcomeSynthesis.targetAchieved" className="isNumeric" help="form.message.numericValue" required=canEdit editable=editable /]</div>
                  <div class="thirdPartBlock"></div>
                  <div class="thirdPartBlock">[@customForm.input name="synthesis[${index}].achievedExpectedText" type="text" i18nkey="synthesis.outcomeSynthesis.targetAchievedExpected" className="isNumeric" help="form.message.numericValue" required=canEdit editable=false /]</div>
                </div>
                
                [#-- Synthesis of annual progress towards this indicator --]
                <div class="fullPartBlock">
                  [@customForm.textArea name="synthesis[${index}].synthesisAnual" i18nkey="synthesis.outcomeSynthesis.progressIndicator" className="progressIndicator limitWords-250" required=canEdit editable=editable /]
                </div>
                
                [#-- Synthesis of annual progress gender and social inclusion contribution towards this indicator --]
                <div class="fullPartBlock">
                  [@customForm.textArea name="synthesis[${index}].synthesisGender" i18nkey="synthesis.outcomeSynthesis.genderProgressIndicator" className="genderProgressIndicator limitWords-200" required=canEdit editable=editable /]
                </div>
                
                [#-- Project Contributions --]
                <label>[@s.text name="synthesis.outcomeSynthesis.projectContributions" /]:</label> 
                [#-- Project milestone contributions --> action.getProjectIndicators(reportingYear, flagshipIndicator.id,midOutcome.id)--]
                [#if (projectMilestoneContributions)?has_content]
                <div class="fullPartBlock synthesisContributions-block viewMoreSynthesis-block">
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
                      	<td class="center"><a href="[@s.url action="ccafsOutcomes" namespace="/reporting/projects"][@s.param name='projectID']${(projectIndicator.projectId)!}[/@s.param][@s.param name='edit']true[/@s.param][/@s.url]">P${(projectIndicator.projectId)!}</a></td>
                      	<td class="center" title="${(projectIndicator.target)!''}" >[@utilities.wordCutter string=(projectIndicator.target)!'Prefilled when available' maxPos=25 /]</td>
                      	<td class="center" title="${(projectIndicator.archived)!''}" >[@utilities.wordCutter string=(projectIndicator.archived)!'Prefilled when available' maxPos=25 /]</td>
                        <td class="">${(projectIndicator.narrativeTargets)!'Prefilled when available'} </td> 
                      </tr>
                    [/#list]
                  	</tbody>
                  </table>
                  <div class="viewMore"></div>
                </div>
                [#else]
                  <p>There is not project contributing to this indicator</p>
                [/#if]
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


[#include "/WEB-INF/global/pages/footer.ftl"]
