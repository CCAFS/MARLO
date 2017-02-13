[#ftl]
[#assign title = "Synthesis by Mog" /]
[#assign currentSectionString = "synthesis-${actionName?replace('/','-')}-${liaisonInstitutionID}" /]
[#assign pageLibs = [ "datatables.net", "datatables.net-bs" ] /]
[#assign customJS = [
  "${baseUrl}/js/synthesis/synthesisByCoAs.js",
  "${baseUrl}/js/global/autoSave.js",
  "${baseUrl}/js/global/fieldsValidation.js"
] /]
[#assign customCSS = ["${baseUrl}/css/synthesis/synthesisGlobal.css"] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "synthesisByMog" /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"${currentSection}", "action":"projectsList"},
  {"label":"synthesisByMog", "nameSpace":"${currentSection}", "action":"synthesisByMog"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<section class="container">
  <div class="row">
  
    <div class="col-md-12">
    [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass="pure-form"]
    
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
      <h1 class="contentTitle">[@s.text name="synthesisByMog.title" ][@s.param]${(currentLiaisonInstitution.name)!}[/@s.param][/@s.text]</h1>
      
      [#-- MOGs --]
      <div id="outcomeSynthesisBlock" class="">
      [#if mogs?has_content]
        [#list mogs as mog]
        <div class="borderBox">
        
          [#-- MOG Name --]
          <div class="fullPartBlock">
            <h6 class="title">${mog.getComposedId()} <span class="ipElementId">ID ${mog.id}</span></h6>
            <p>${mog.description}</p>
          </div>
          
          [#assign index = action.getIndex(mog.id,program.id) /]
          <div class="form-group row">
            [#-- Synthesis report for MOG --]
            <div class="col-md-12">
              [@customForm.textArea name="synthesis[${index}].synthesisReport" i18nkey="synthesisByMog.synthesisReport" paramText="${mog.getComposedId()}" className="synthesisReport limitWords-${program.flagshipProgram?string('250','150')}" required=canEdit editable=editable /]
            </div>
            [#-- Gender synthesis report for MOG --]
            <div class="col-md-12">
              [@customForm.textArea name="synthesis[${index}].synthesisGender" i18nkey="synthesisByMog.genderSynthesisReport" paramText="${mog.getComposedId()}" className="genderSynthesisReport limitWords-${program.flagshipProgram?string('150','100')}" required=canEdit editable=editable /]
            </div> 
          </div>
          
          [#-- Synthesis reported by regions --]
          [#if program.flagshipProgram]
          <div class="fullPartBlock">
            <label>[@s.text name="synthesisByMog.RegionalSynthesis" /]:</label> 
            <div class="fullPartBlock synthesisContributions-block viewMoreSyntesis-block">
              <table class="regionalContributions">
                <thead>
                  <tr class="header">
                    <th class="col-regionId">Region</th>
                    <th class="col-mogSynthesis">Synthesis reported</th>
                    <th class="col-mogSynthesis">Gender synthesis reported</th>
                  </tr>
                </thead>
                <tbody>
                [#list action.getRegionalSynthesis(mog.id) as syntesisReport]
                  <tr>
                    <td class="center"> ${(syntesisReport.ipProgam.acronym)!}</td>
                    <td>${(syntesisReport.synthesisReport)!}</td>  
                    <td>${(syntesisReport.synthesisGender)!}</td>
                  </tr>
                [/#list]
                </tbody>
              </table>
              <div class="viewMoreSyntesis"></div>
            </div>
          </div>
          [/#if]
          
          [#-- Projects contributions to this MOG --]
          <div class="fullPartBlock">
            <label>[@s.text name="synthesisByMog.projectContributions" /]:</label> 
            <div class="fullPartBlock synthesisContributions-block viewMoreSyntesis-block">
              <table class="projectContributions">
                <thead>
                  <tr class="header">
                    <th class="col-projectId">Project</th>
                    <th class="col-mogSynthesis">Summary of actual ${currentCycleYear} contribution towards this MOG  </th>
                    <th class="col-mogSynthesis">Summary of the gender and social inclusion dimension of the ${currentCycleYear} outputs</th>
                  </tr>
                </thead>
                <tbody>
                [#list action.getProjectOutputOverviews(mog.id) as projectContribution]
                  <tr>
                    <td class="center"><a href="[@s.url action="outputs" namespace="/projects/${crpSession}"][@s.param name='projectID']${(projectContribution.project.id)!}[/@s.param][@s.param name='edit']true[/@s.param][/@s.url]">P${(projectContribution.project.id)!}</a></td>
                    <td class="">${(projectContribution.briefSummary)!'Prefilled when available'} </td>
                    <td class="">${(projectContribution.summaryGender)!'Prefilled when available'} </td>
                  </tr>
                [/#list]
                </tbody>
              </table>
              <div class="viewMoreSyntesis"></div>
            </div>
          </div>
          
        </div>
        [/#list]
      [#else]
        No MOGs Loaded
      [/#if]
      </div>
      
      [#-- Synthesis Lessons --]
      <div id="lessons" class="borderBox">
        <div class="fullBlock">
          <input type="hidden" name="projectLessons.id" value=${(projectLessons.id)!"-1"} />
          <input type="hidden" name="projectLessons.year" value=${currentCycleYear} />
          <input type="hidden" name="projectLessons.componentName" value="${actionName}">
          [@customForm.textArea name="projectLessons.lessons" i18nkey="synthesisByMog.lessons" paramText="${program.flagshipProgram?string('project/regional', 'project')}" help="synthesisByMog.lessons.help" className="synthesisLessons limitWords-100" required=true editable=editable /]
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
