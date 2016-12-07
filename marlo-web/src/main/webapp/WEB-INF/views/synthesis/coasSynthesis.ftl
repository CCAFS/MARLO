[#ftl]
[#assign title = "Synthesis by MOG" /]
[#assign globalLibs = ["jquery", "dataTable", "noty","autoSave"] /]
[#assign customJS = ["${baseUrl}/js/global/utils.js", "${baseUrl}/js/synthesis/synthesisByMog.js"] /]
[#assign customCSS = ["${baseUrl}/css/libs/dataTables/jquery.dataTables-1.9.4.css", "${baseUrl}/css/global/customDataTable-flat.css"] /]
[#assign currentSection = reportingCycle?string('reporting','planning') /]
[#assign currentCycleSection = "synthesisByMog" /]
[#assign currentStage = "synthesisByMog" /]
[#assign currentSubStage = "synthesisByMog" /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"${currentSection}", "action":"projectsList"},
  {"label":"synthesisByMog", "nameSpace":"${currentSection}", "action":"synthesisByMog"}
]/]

[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]
[#import "/WEB-INF/global/macros/forms.ftl" as customForm /]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities/]

<section class="content">
 
  <article class="fullBlock clearfix" id="">
    [@s.form action="synthesisByMog" method="POST" enctype="multipart/form-data" cssClass="pure-form"]
    
    [#-- Program (Regions and Flagships) --]
    <ul id="liaisonInstitutions" class="horizontalSubMenu">
      [#list liaisonInstitutions as institution]
        [#assign isActive = (institution.id == liaisonInstitutionID)/]
        [#assign isCompleted = false /]
        [#assign hasPermission = action.hasSynthesisPermission('update', institution.id)/]
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
      <h1 class="contentTitle">[@s.text name="reporting.synthesis.synthesisByMog.title" ][@s.param]${(currentLiaisonInstitution.name)!}[/@s.param][/@s.text]</h1>
      
      [#-- MOGs --]
      <div id="outcomeSynthesisBlock" class="">
        [#list mogs as mog]
        <div class="borderBox">
          [#-- Button for edit this section --]
          [#if (!editable && canEdit)]
            <div class="editButton"><a href="[@s.url][@s.param name ="liaisonInstitutionID"]${liaisonInstitutionID}[/@s.param][@s.param name="edit"]true[/@s.param][/@s.url]">[@s.text name="form.buttons.edit" /]</a></div>
          [#elseif canEdit]
              <div class="viewButton"><a href="[@s.url][@s.param name ="liaisonInstitutionID"]${liaisonInstitutionID}[/@s.param][/@s.url]">[@s.text name="form.buttons.unedit" /]</a></div>
          [/#if]
      
          [#-- MOG Name --]
          <div class="fullPartBlock">
            <h6 class="title">${mog.getComposedId()} <span class="ipElementId">ID ${mog.id}</span></h6>
            <p>${mog.description}</p>
          </div>
          
             [#assign index = action.getIndex(mog.id,program.id) /]
          [#-- Synthesis report for MOG --]
          <div class="fullPartBlock">
            [@customForm.textArea name="synthesis[${index}].synthesisReport" i18nkey="reporting.synthesis.synthesisByMog.synthesisReport" paramText="${mog.getComposedId()}" className="synthesisReport limitWords-${program.flagshipProgram?string('250','150')}" required=canEdit editable=editable /]
          </div>
          [#-- Gender synthesis report for MOG --]
          <div class="fullPartBlock">
            [@customForm.textArea name="synthesis[${index}].synthesisGender" i18nkey="reporting.synthesis.synthesisByMog.genderSynthesisReport" paramText="${mog.getComposedId()}" className="genderSynthesisReport limitWords-${program.flagshipProgram?string('150','100')}" required=canEdit editable=editable /]
          </div>
          
          [#-- Synthesis reported by regions --]
          [#if program.flagshipProgram]
          <div class="fullPartBlock">
            <h6>[@s.text name="reporting.synthesis.synthesisByMog.RegionalSynthesis" /]:</h6> 
            <div class="fullPartBlock synthesisContributions-block viewMore-block">
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
              <div class="viewMore"></div>
            </div>
          </div>
          [/#if]
          
          [#-- Projects contributions to this MOG --]
          <div class="fullPartBlock">
            <h6>[@s.text name="reporting.synthesis.synthesisByMog.projectContributions" /]:</h6> 
            <div class="fullPartBlock synthesisContributions-block viewMore-block">
              <table class="projectContributions">
                <thead>
                  <tr class="header">
                    <th class="col-projectId">Project</th>
                    <th class="col-mogSynthesis">Summary of actual ${currentReportingYear} contribution towards this MOG  </th>
                    <th class="col-mogSynthesis">Summary of the gender and social inclusion dimension of the ${currentReportingYear} outputs</th>
                  </tr>
                </thead>
                <tbody>
                [#list action.getProjectOutputOverviews(mog.id) as projectContribution]
                  <tr>
                    <td class="center"><a href="[@s.url action="outputs" namespace="/reporting/projects"][@s.param name='projectID']${(projectContribution.projectID)!}[/@s.param][@s.param name='edit']true[/@s.param][/@s.url]">P${(projectContribution.projectID)!}</a></td>
                    <td class="">${(projectContribution.briefSummary)!'Prefilled when available'} </td>
                    <td class="">${(projectContribution.summaryGender)!'Prefilled when available'} </td>
                  </tr>
                [/#list]
                </tbody>
              </table>
              <div class="viewMore"></div>
            </div>
          </div>
          [#if editable]
            <div class="buttons">
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
          [@customForm.textArea name="projectLessons.lessons" i18nkey="reporting.synthesis.synthesisByMog.lessons" paramText="${program.flagshipProgram?string('project/regional', 'project')}" help="reporting.synthesis.synthesisByMog.lessons.help" className="synthesisLessons limitWords-100" required=true editable=editable /]
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
  </article>
  
</section> 


[#include "/WEB-INF/global/pages/footer.ftl"]
