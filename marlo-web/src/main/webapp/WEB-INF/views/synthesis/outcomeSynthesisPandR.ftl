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
  { 'id': 1, 'year': '2017', 'description': 'Diagnosis on subnational policy and institutional frameworks analysis focusing on different options that can support the adoption of preferred CSA practices'},
  { 'id': 2, 'year': '2017', 'srfTargetUnit': {'id':1, 'name':'Country profiles'}, 'description': '10 country profiles in SSA and South Asia developped strategic engagement with subnational government, capacity building and training plan co-developed with ACSAA workshops on climate smart local development planning.'}
] /]

[#assign projectMilestoneContributions = [
  { 'id': 1, 'projectId': '56', 'target': '2', 'achieved': '2', 'narrativeTargets': 'Lorem ipsum dolor sit amet, consectetur adipisicing elit. Quisquam error eveniet quam praesentium quasi impedit cumque odio culpa omnis eos ab aspernatur expedita aperiam sunt illum adipisci nesciunt! Modi quas!'},
  { 'id': 2, 'projectId': '22', 'target': '5', 'achieved': '1', 'narrativeTargets': 'Lorem ipsum dolor sit amet, consectetur adipisicing elit. Quisquam error eveniet quam praesentium quasi impedit cumque odio culpa omnis eos ab aspernatur expedita aperiam sunt illum adipisci nesciunt! Modi quas!'},
  { 'id': 3, 'projectId': '1', 'target': '1', 'achieved': '1', 'narrativeTargets': 'Lorem ipsum dolor sit amet, consectetur adipisicing elit. Quisquam error eveniet quam praesentium quasi impedit cumque odio culpa omnis eos ab aspernatur expedita aperiam sunt illum adipisci nesciunt! Modi quas!'},
  { 'id': 4, 'projectId': '16',  'target': '3', 'achieved': '1', 'narrativeTargets': 'Lorem ipsum dolor sit amet, consectetur adipisicing elit. Quisquam error eveniet quam praesentium quasi impedit cumque odio culpa omnis eos ab aspernatur expedita aperiam sunt illum adipisci nesciunt! Modi quas!'},
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
            [#assign hasPermission = (action.hasSynthesisPermission('update', institution.id))!false /]
            <li class="${isActive?string('active','')} ${hasPermission?string('canEdit','')}">
              <a href="[@s.url][@s.param name ="liaisonInstitutionID"]${institution.id}[/@s.param][@s.param name ="edit"]true[/@s.param][/@s.url]">${institution.name}</a>
              [#if isCompleted] <p class="synthesisCompleted"> <img src="${baseUrl}/images/global/icon-check-tiny${isActive?string('-white','')}.png" /> </p> [/#if]
            </li>
          [/#list]
        </ul>
        
        <div class="fullContent">
          [#-- Informing user that he/she doesn't have enough privileges to edit. See GrantProjectPlanningAccessInterceptor--]
          [#if submission?has_content]
            <p class="projectSubmitted">[@s.text name="submit.projectSubmitted" ][@s.param]${(submission.dateTime?date)?string.full}[/@s.param][/@s.text]</p>
          [#elseif !canEdit ]
            <p class="readPrivileges">[@s.text name="saving.read.privileges"][@s.param]${title}[/@s.param][/@s.text]</p>
          [/#if]
          [#-- Title --]
          <h1 class="contentTitle">[@s.text name="reporting.synthesis.outcomeSynthesis.title" ][@s.param]${(currentLiaisonInstitution.name)!}[/@s.param][/@s.text]</h1>
          
          [#-- Outcomes 2019 --]
          <div id="outcomeSynthesisBlock" class="">
            [#list midOutcomes as midOutcome]
            <div class="borderBox"> 
              <div class="fullPartBlock">
                <h6 class="title">${(midOutcome.composedId)!} <span class="ipElementId">ID ${midOutcome.id}</span></h6>
                <p>${midOutcome.description}</p>
              </div>
              <div class="fullPartBlock">
                <h6>[@s.text name="reporting.synthesis.outcomeSynthesis.indicators" /]:</h6> 
                
                [#list midOutcome.indicators as indicator]
                  [#assign flagshipIndicator = (indicator.parent)!indicator /]
                   [#assign index = action.getIndex(flagshipIndicator.id,midOutcome.id,program.id) /]
               
                  <div class="simpleBox">
                    <div class="fullPartBlock">
                      <p>${flagshipIndicator.description}</p>
                    </div>
                    [#-- Achieved target in current reporting period --]
                    <div class="fullPartBlock">
                      <div class="thirdPartBlock">[@customForm.input name="synthesis[${index}].achievedText" type="text" i18nkey="reporting.synthesis.outcomeSynthesis.targetAchieved" className="isNumeric" help="form.message.numericValue" required=canEdit editable=editable /]</div>
                      <div class="thirdPartBlock"></div>
                      <div class="thirdPartBlock">[@customForm.input name="synthesis[${index}].achievedExpectedText" type="text" i18nkey="reporting.synthesis.outcomeSynthesis.targetAchievedExpected" className="isNumeric" help="form.message.numericValue" required=canEdit editable=false /]</div>
                    </div>
                    
                    [#-- Synthesis of annual progress towards this indicator --]
                    <div class="fullPartBlock">
                      [@customForm.textArea name="synthesis[${index}].synthesisAnual" i18nkey="reporting.synthesis.outcomeSynthesis.progressIndicator" className="progressIndicator limitWords-250" required=canEdit editable=editable /]
                    </div>
                    
                    [#-- Synthesis of annual progress gender and social inclusion contribution towards this indicator --]
                    <div class="fullPartBlock">
                      [@customForm.textArea name="synthesis[${index}].synthesisGender" i18nkey="reporting.synthesis.outcomeSynthesis.genderProgressIndicator" className="genderProgressIndicator limitWords-200" required=canEdit editable=editable /]
                    </div>
                    
                    [#-- Explain any discrepancy  --]
                    [#if midOutcome.regionalProgramType]
                    <div class="fullPartBlock">
                      [@customForm.textArea name="synthesis[${index}].discrepancy" i18nkey="reporting.synthesis.outcomeSynthesis.discrepancy" className="discrepancy limitWords-100" editable=editable /]
                    </div>
                    [/#if]
                    
                    [#-- Regions/Global contributions --]
                    [#if midOutcome.flagshipProgramType]
                    <h6>[@s.text name="reporting.synthesis.outcomeSynthesis.regionalContributions" /]:</h6> 
                    <div class="fullPartBlock">
                      <div class="fullPartBlock synthesisContributions-block viewMore-block">
                        <table class="regionalContributions">
                          <thead>
                            <tr class="header">
                              <th class="col-projectId">Region</th>
                              <th class="col-expected">Target Expected</th>
                              <th class="col-achieved">Target Achieved</th>
                              <th class="col-synthesis">Synthesis reported</th>
                              <th class="col-synthesis">Gender synthesis reported</th>
                            </tr>
                          </thead>
                          <tbody>
                          [#list action.getRegionalSynthesis(flagshipIndicator.id,midOutcome.id) as syntesisReport]
                            <tr>
                              <td class="center">${(syntesisReport.ipprogram.acronym)!'Prefilled when available'}</td>
                              <td class="center">${(syntesisReport.achievedExpected)!'Prefilled when available'}</td>  
                              <td class="center">${(syntesisReport.achieved)!'Prefilled when available'}</td>
                              <td >${(syntesisReport.synthesisAnual)!'Prefilled when available'}</td>
                              <td> ${(syntesisReport.synthesisGender)!'Prefilled when available'}</td>
                            </tr>
                          [/#list]
                          </tbody>
                        </table>
                        <div class="viewMore"></div>
                      </div>
                    </div>
                    [/#if]
                    
                    [#-- Project Contributions --]
                    [#if midOutcome.flagshipProgramType]
                      <h6>[@s.text name="reporting.synthesis.outcomeSynthesis.globalProjectContributions" /]:</h6> 
                    [#else]
                      <h6>[@s.text name="reporting.synthesis.outcomeSynthesis.projectContributions" /]:</h6> 
                    [/#if]
                    [#if (action.getProjectIndicators(currentReportingYear, flagshipIndicator.id,midOutcome.id))?has_content]
                    <div class="fullPartBlock synthesisContributions-block viewMore-block">
                      <table class="projectContributions">
                        <thead>
                          <tr class="header">
                            <th class="col-projectId">Project</th>
                            <th class="col-expected">Target Expected</th>
                            <th class="col-achieved">Target Achieved</th> 
                            <th class="col-synthesis">Narrative of achieved targets, including evidence</th>
                            <th class="col-synthesis">Narrative of achieved annual gender and social inclusion contribution</th>
                          </tr>
                        </thead>
                        <tbody>
                        [#list action.getProjectIndicators(currentReportingYear, flagshipIndicator.id,midOutcome.id) as projectIndicator]
                          <tr>
                            <td class="center"><a href="[@s.url action="ccafsOutcomes" namespace="/reporting/projects"][@s.param name='projectID']${(projectIndicator.projectId)!}[/@s.param][@s.param name='edit']true[/@s.param][/@s.url]">P${(projectIndicator.projectId)!}</a></td>
                            <td class="center" title="${(projectIndicator.target)!''}" >[@utilities.wordCutter string=(projectIndicator.target)!'Prefilled when available' maxPos=25 /]</td>
                            <td class="center" title="${(projectIndicator.archived)!''}" >[@utilities.wordCutter string=(projectIndicator.archived)!'Prefilled when available' maxPos=25 /]</td>
                            <td class="">${(projectIndicator.narrativeTargets)!'Prefilled when available'} </td>
                            <td class="">${(projectIndicator.narrativeGender)!'Prefilled when available'} </td>
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
              [#if editable] 
              <div class="buttons clearfix">
                [@s.submit type="button" name="save"][@s.text name="form.buttons.save" /][/@s.submit]
                [@s.submit type="button" name="cancel"][@s.text name="form.buttons.cancel" /][/@s.submit]
              </div>
              [/#if]
            </div>
            [/#list]
          </div>
          
          [#-- Synthesis Lessons --]
          <div id="lessons" class="borderBox">
            [#if (!editable && canEdit)]
              <div class="editButton"><a href="[@s.url][@s.param name ="liaisonInstitutionID"]${liaisonInstitutionID}[/@s.param][@s.param name="edit"]true[/@s.param][/@s.url]#lessons">[@s.text name="form.buttons.edit" /]</a></div>
            [#elseif canEdit]
              <div class="viewButton"><a href="[@s.url][@s.param name ="liaisonInstitutionID"]${liaisonInstitutionID}[/@s.param][/@s.url]#lessons">[@s.text name="form.buttons.unedit" /]</a></div>
            [/#if]
            <div class="fullBlock">
              <input type="hidden" name="projectLessons.id" value=${(projectLessons.id)!"-1"} />
              <input type="hidden" name="projectLessons.year" value=${currentReportingYear} />
              <input type="hidden" name="projectLessons.componentName" value="${actionName}">
              [@customForm.textArea name="projectLessons.lessons" i18nkey="reporting.synthesis.outcomeSynthesis.lessons" paramText="${program.flagshipProgram?string('project/regional', 'project')}" help="reporting.synthesis.outcomeSynthesis.lessons.help" className="synthesisLessons limitWords-100" required=true editable=editable /]
            </div>
            <br />
            [#if editable] 
            <div class="buttons clearfix">
              [@s.submit type="button" name="save"][@s.text name="form.buttons.save" /][/@s.submit]
              [@s.submit type="button" name="cancel"][@s.text name="form.buttons.cancel" /][/@s.submit]
            </div>
            [/#if] 
          </div>
            <input type="hidden" name="liaisonInstitutionID" value="${liaisonInstitutionID}"  /> 
          [#-- Log history --]
          [#if editable]
            
          [#else]
            [#-- Display Log History --]
            [#if history??][@log.logList list=history /][/#if] 
          [/#if]
        </div>
      [/@s.form]
      
    </div>
  </div>
</section> 


[#include "/WEB-INF/global/pages/footer.ftl"]
