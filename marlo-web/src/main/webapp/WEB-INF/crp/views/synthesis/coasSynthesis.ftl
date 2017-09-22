[#ftl]
[#assign canEdit = true /]
[#assign editable = true /]
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
[#assign clusterActivities = [
  { 'id': 1, 'composedId': 'CoA 2.1', 'description': 'Participatory evaluation of CSA technologies and practices in Climate-Smart Villages (Learning Platform - LP2)'}
] /]

[#assign keyOutputsByCoA = [
  { 'id': 1, 'description': 'On-farm tested, and evaluated and up-scalable gender sensitive and specific CSA options, including transformative options, and models of integrated crop-livestock-tree systems for increasing resilience.'},
  { 'id': 2, 'description': 'Improved understanding of farmers and stakeholders perceptions along the value chain of CSA options, and assessments of the conditions for success and failure of interventions.'},
  { 'id': 3, 'description': 'Simulation of CSA options under different climate and socio-economic scenarios for informed decision-making (together with FP1)'}
] /]

[#assign deliverablesCompleted = [
  { 'id': 339,  'projectId': '2', 'subType':'Data Portal/tool/model Code/computer Software', 'title': 'Methodology to develop recommendations and provide CB on gender inclusion in CC policies and institutions' },
  { 'id': 424,  'projectId': '2', 'subType':'Research Workshop Report', 'title': 'Policy recommendations to second NAMA' },
  { 'id': 1429, 'projectId': '2', 'subType':'Data Portal/tool/model Code/computer Software', 'title': 'Soil carbon dynamics model (joint with WLE)' },
  { 'id': 1430, 'projectId': '2', 'subType':'Journal Article (peer Reviewed)', 'title': 'Description of soil carbon dynamic model (joint with WLE)' },
  { 'id': 1461, 'projectId': '2', 'subType':'Discussion Paper/working Paper/white Paper', 'title': 'Scientific evidence generated by CIP in the formulation of management plans by subnational government' },
  { 'id': 1432, 'projectId': '2', 'subType':'Policy Brief/policy Note/briefing Paper', 'title': 'Final synthesis of recommendations in NAP' }
 
 ] /]

[#assign activities = [
   { 'id': 29, 'projectId': '2', 'title': 'Influencing gender-inclusive climate change policies for Latin American countries.' },
   { 'id': 30, 'projectId': '2', 'title': 'Promoting the development of a High Andes-oriented agricultural NAMA in Peru' },
   { 'id': 403, 'projectId': '2', 'title': '(BILATERAL): Sustainable development and land use-based alternatives to enhance mitigation and adaptation capacities in Amazon.' },
   { 'id': 6, 'projectId': '2', 'title': 'Support development and implementation of the coffee and livestock  NAMA in Costa Rica' },
   { 'id': 25, 'projectId': '2', 'title': 'Preparation training of Latin American agricultural COP delegates' }
] /]




[#assign title = "Outcome By CoAs" /]
[#assign currentSectionString = "synthesis-${actionName?replace('/','-')}-${liaisonInstitutionID}" /]
[#assign pageLibs = [ "datatables.net", "datatables.net-bs" ] /]
[#assign customJS = ["${baseUrlMedia}/js/synthesis/synthesisByCoAs.js"] /]
[#assign customCSS = ["${baseUrlMedia}/css/synthesis/synthesisGlobal.css"] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "outcomeCoAs" /]

[#assign breadCrumb = [
  {"label":"synthesis", "nameSpace":"/synthesis", "action":"crpContributors"},
  {"label":"outcomeCoas", "nameSpace":"", "action":""}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]

<section class="container">
  <div class="row"> 
    <div class="col-md-12">
      [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass="pure-form"]
      
      [#-- Title --]
      <h3 class="headTitle text-center">[@s.text name="synthesis.synthesisByCoAs.title" ][@s.param]${(currentLiaisonInstitution.name)!}[/@s.param][/@s.text]</h3>
       
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
      
      [#-- Cluster of activities --]
      <div id="synthesisByCoAsBlock" class="">
        [#list clusterActivities as clusterActivity]
        <div class="synthesisByCoAs borderBox"> 
          [#-- Outcome title --]
          <h6 class="title">
           ${(currentLiaisonInstitution.acronym)!} - ${clusterActivity.composedId} -  ${clusterActivity.description} 
           <span class="ipElementId">ID ${clusterActivity.id}</span>
          </h6>
           
          [#-- keyOutputs --]
          <div class="col-md-12"> 
            [#-- Coa Key Outputs --]
            [#list keyOutputsByCoA as keyOutput]
              [#assign flagshipIndicator = (indicator.parent)!keyOutput /]
              [#assign index = (action.getIndex(flagshipIndicator.id,clusterActivity.id,program.id))!-1 /]
           
              <div class="form-group simpleBox">
                [#-- keyOutput title --]
                <div class="form-group grayBox">
                  <h4 class="subHeadTitle"> Key Output:</h4> 
                  
                  
                  <div class="row">
                    <div class="col-md-7">
                      <p>${keyOutput.description}</p>
                    </div>
                    <div class="indicators col-md-5">
                      <div class="indicator"><strong>5</strong> projects working on this key output </div>
                      <div class="indicator"><strong>${deliverablesCompleted?size}</strong> deliverables completed in ${reportingYear}</div>
                      <div class="indicator"><strong>${activities?size}</strong> envolved activities ${reportingYear}</div>
                    </div>
                  </div>
                </div>
               
                [#-- Achieved target in current reporting period --]
                [#assign showkeyOutputValue = keyOutput.srfTargetUnit??  && keyOutput.srfTargetUnit.id?? && (keyOutput.srfTargetUnit.id != -1) /]
                <div class="row form-group keyOutputTargetValue" style="display:${showkeyOutputValue?string('block', 'none')}">
                  <div class="col-md-4">
                    [@customForm.input name="synthesis[${index}].achievedExpectedText" type="text" i18nkey="synthesis.synthesisByCoAs.targetAchievedExpected" className="isNumeric" help="form.message.numericValue" required=canEdit editable=false /]
                  </div>
                  <div class="col-md-4">
                    <div class="select">
                      <label for="">[@s.text name="projectOutcomekeyOutput.expectedUnit" /]:</label>
                      <div class="selectList"><p class="crpkeyOutputTargetUnit">${(keyOutput.srfTargetUnit.name)!}</p></div> 
                    </div>
                  </div> 
                  <div class="col-md-4">
                    [@customForm.input name="synthesis[${index}].achievedText" type="text" i18nkey="synthesis.synthesisByCoAs.targetAchieved" className="isNumeric" help="form.message.numericValue" required=canEdit editable=editable /]
                  </div>
                </div>
                
                [#-- Synthesis of annual progress towards this keyOutput --]
                <div class="fullPartBlock">
                  [@customForm.textArea name="synthesis[${index}].synthesisAnual" i18nkey="synthesis.synthesisByCoAs.progressIndicator" className="progressIndicator limitWords-250" required=canEdit editable=editable /]
                </div>
                
                [#-- Deliverables completed in reportingYear --]
                <label>Deliverables completed in ${reportingYear}:</label> 
                [#if (deliverablesCompleted)?has_content]
                <div class="fullPartBlock synthesisContributions-block viewMoreSyntesis-block">
                  <table class="projectContributions greenTableHead">
                    <thead>
                      <tr class="header">
                        <th class="">Project ID</th>
                        <th class="">Deliverable ID</th>
                        <th class="">Sub Type</th>
                        <th class="">Title</th>
                        <th class="">FAIR</th>
                      </tr>
                    </thead>
                    <tbody>
                    [#list deliverablesCompleted as deliverable]
                      <tr>
                        <td class="center"><a href="[@s.url action="${crpSession}/description" namespace="/projects"][@s.param name='projectID']${(deliverable.projectId)!}[/@s.param][@s.param name='edit']true[/@s.param][/@s.url]">P${(deliverable.projectId)!}</a></td>
                        <td class="center"><a href="[@s.url action="${crpSession}/deliverable" namespace="/projects"][@s.param name='projectID']${(deliverable.projectId)!}[/@s.param][@s.param name='edit']true[/@s.param][@s.param name='deliverableID']${(deliverable.id)!}[/@s.param][/@s.url]">D${(deliverable.id)!}</a></td>
                        <td class="center">${deliverable.subType}</td>
                        <td class="">${deliverable.title}</td>
                        <td class="fair">
                        <span class=" ">F</span>
                        <span class=" ">A</span>
                        <span class=" ">I</span>
                        <span class=" ">R</span>
                      </td>
                      </tr>
                    [/#list]
                    </tbody>
                  </table>
                  <div class="viewMoreSyntesis closed"></div>
                </div>
                [#else]
                  <p>No deliverables contributing to this key output</p>
                [/#if]
                <br />
                
                [#-- Envolved activities --]
                <label> Envolved activities in ${reportingYear}:</label> 
                [#if (activities)?has_content]
                <div class="fullPartBlock synthesisContributions-block viewMoreSyntesis-block">
                  <table class="projectContributions">
                    <thead>
                      <tr class="header">
                        <th class="">Project ID</th>
                        <th class="">Activity ID</th>
                        <th class="">Title</th>
                      </tr>
                    </thead>
                    <tbody>
                    [#list activities as activity]
                      <tr>
                        <td class="center"><a href="[@s.url action="${crpSession}/activities" namespace="/projects"][@s.param name='projectID']${(activity.projectId)!}[/@s.param][@s.param name='edit']true[/@s.param][/@s.url]">P${(activity.projectId)!}</a></td>
                        <td class="center">A${(activity.id)!}</td>
                        <td class="">${(activity.title)!}</td>
                      </tr>
                    [/#list]
                    </tbody>
                  </table>
                  <div class="viewMoreSyntesis closed"></div>
                </div>
                [#else]
                  <p>No activities contributing to this key output</p>
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
          [@customForm.textArea name="projectLessons.lessons" i18nkey="synthesis.synthesisByCoAs.lessons" paramText="${program.flagshipProgram?string('project/regional', 'project')}" help="synthesis.synthesisByCoAs.lessons.help" className="synthesisLessons limitWords-100" required=true editable=editable /]
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