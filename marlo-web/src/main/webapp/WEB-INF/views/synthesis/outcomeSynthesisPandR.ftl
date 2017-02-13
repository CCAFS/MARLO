[#ftl]
[#assign canEdit = true /]
[#assign editable = true /]


[#assign title = "Outcome Synthesis" /]
[#assign currentSectionString = "synthesis-${actionName?replace('/','-')}-${liaisonInstitutionID}" /]
[#assign pageLibs = [ "datatables.net", "datatables.net-bs" ] /]
[#assign customJS = [
  "${baseUrl}/js/synthesis/outcomeSynthesis.js",
  "${baseUrl}/js/global/autoSave.js",
  "${baseUrl}/js/global/fieldsValidation.js"
] /]
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
         
          [#-- Title --]
          <h1 class="contentTitle">[@s.text name="synthesis.outcomeSynthesisPandR.title" ][@s.param]${(currentLiaisonInstitution.name)!}[/@s.param][/@s.text]</h1>
          
          [#-- Outcomes 2019 --]
          <div id="outcomeSynthesisBlock" class="">
            [#list midOutcomes as midOutcome]
            <div class="borderBox"> 
              <div class="fullPartBlock">
                <h6 class="title">${(midOutcome.composedId)!} <span class="ipElementId">ID ${midOutcome.id}</span></h6>
                <p>${midOutcome.description}</p>
              </div>
              <div class="fullPartBlock">
                <label>[@s.text name="synthesis.outcomeSynthesisPandR.indicators" /]:</label> 
                [#if midOutcome.indicators?has_content]
                
                [#list midOutcome.indicators as indicator]
                  [#assign flagshipIndicator = (indicator.parent)!indicator /]
                   [#assign index = action.getIndex(flagshipIndicator.id,midOutcome.id,program.id) /]
               
                  <div class="simpleBox">
                    <div class="fullPartBlock">
                      <div class="grayBox"><p>${flagshipIndicator.description}</p></div>
                    </div>
                    [#-- Achieved target in current reporting period --]
                    <div class="form-group row">
                      <div class="col-md-4">[@customForm.input name="synthesis[${index}].achievedText" type="text" i18nkey="synthesis.outcomeSynthesisPandR.targetAchieved" className="isNumeric" help="form.message.numericValue" required=canEdit editable=editable /]</div>
                      <div class="col-md-4"></div>
                      <div class="col-md-4">[@customForm.input name="synthesis[${index}].achievedExpectedText" type="text" i18nkey="synthesis.outcomeSynthesisPandR.targetAchievedExpected" className="isNumeric" help="form.message.numericValue" required=canEdit editable=false /]</div>
                    </div>
                    
                    [#-- Synthesis of annual progress towards this indicator --]
                    <div class="form-group">
                      [@customForm.textArea name="synthesis[${index}].synthesisAnual" i18nkey="synthesis.outcomeSynthesisPandR.progressIndicator" className="progressIndicator limitWords-250" required=canEdit editable=editable /]
                    </div>
                    
                    [#-- Synthesis of annual progress gender and social inclusion contribution towards this indicator --]
                    <div class="form-group">
                      [@customForm.textArea name="synthesis[${index}].synthesisGender" i18nkey="synthesis.outcomeSynthesisPandR.genderProgressIndicator" className="genderProgressIndicator limitWords-200" required=canEdit editable=editable /]
                    </div>
                    
                    [#-- Explain any discrepancy  --]
                    [#if program.regionalProgram]
                    <div class="form-group">
                      [@customForm.textArea name="synthesis[${index}].discrepancy" i18nkey="synthesis.outcomeSynthesisPandR.discrepancy" className="discrepancy limitWords-100" editable=editable /]
                    </div>
                    [/#if]
                    
                    [#-- Regions/Global contributions --]
                    [#if program.flagshipProgram]
                    <label>[@s.text name="synthesis.outcomeSynthesisPandR.regionalContributions" /]:</label> 
                    <div class="fullPartBlock">
                      <div class="fullPartBlock synthesisContributions-block viewMoreSyntesis-block">
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
                        <div class="viewMoreSyntesis"></div>
                      </div>
                    </div>
                    [/#if]
                    
                    [#-- Project Contributions --]
                    [#if program.flagshipProgram]
                      <label>[@s.text name="synthesis.outcomeSynthesisPandR.globalProjectContributions" /]:</label> 
                    [#else]
                      <label>[@s.text name="synthesis.outcomeSynthesisPandR.projectContributions" /]:</label> 
                    [/#if]
                    [#if (action.getProjectIndicators(currentCycleYear, flagshipIndicator.id,midOutcome.id))?has_content]
                    <div class="fullPartBlock synthesisContributions-block viewMoreSyntesis-block">
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
                        [#list action.getProjectIndicators(currentCycleYear, flagshipIndicator.id,midOutcome.id) as projectIndicator]
                          <tr>
                            <td class="center"><a href="[@s.url action="ccafsOutcomes" namespace="/projects"][@s.param name='projectID']${(projectIndicator.projectId)!}[/@s.param][@s.param name='edit']true[/@s.param][/@s.url]">P${(projectIndicator.projectId)!}</a></td>
                            <td class="center" title="${(projectIndicator.target)!''}" >[@utilities.wordCutter string=(projectIndicator.target)!'Prefilled when available' maxPos=25 /]</td>
                            <td class="center" title="${(projectIndicator.archived)!''}" >[@utilities.wordCutter string=(projectIndicator.archived)!'Prefilled when available' maxPos=25 /]</td>
                            <td class="">${(projectIndicator.narrativeTargets)!'Prefilled when available'} </td>
                            <td class="">${(projectIndicator.narrativeGender)!'Prefilled when available'} </td>
                          </tr>
                        [/#list]
                        </tbody>
                      </table>
                      <div class="viewMoreSyntesis"></div>
                    </div>
                    [#else]
                      <p>There is not project contributing to this indicator</p>
                    [/#if]
                  </div>
                [/#list]
                [#else]
                  <p>No Outcomes Loaded</p>
                [/#if]
              </div>
            </div>
            [/#list]
          </div>
          
          [#-- Synthesis Lessons --]
          <div id="lessons" class="borderBox">
            <div class="fullBlock">
              <input type="hidden" name="projectLessons.id" value=${(projectLessons.id)!"-1"} />
              <input type="hidden" name="projectLessons.year" value=${currentCycleYear} />
              <input type="hidden" name="projectLessons.componentName" value="${actionName}">
              [@customForm.textArea name="projectLessons.lessons" i18nkey="synthesis.outcomeSynthesisPandR.lessons" paramText="${program.flagshipProgram?string('project/regional', 'project')}" help="synthesis.outcomeSynthesisPandR.lessons.help" className="synthesisLessons limitWords-100" required=true editable=editable /]
            </div> 
          </div>  
        </div>
        
        [#-- Section Buttons & hidden inputs--]
        [#include "/WEB-INF/views/synthesis/buttons-synthesis.ftl" /]
        
      [/@s.form]
      
    </div>
  </div>
</section> 


[#include "/WEB-INF/global/pages/footer.ftl"]
