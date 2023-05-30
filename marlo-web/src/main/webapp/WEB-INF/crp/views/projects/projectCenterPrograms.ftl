[#ftl]
[#assign title = "Project Programs" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/projectDescription.js?20230530",  
  "${baseUrlCdn}/global/js/fieldsValidation.js",
  "${baseUrlCdn}/global/js/autoSave.js"
  ] 
/]
[#assign currentSection = "projects" /]
[#assign currentStage = "centerProgram" /]
[#assign hideJustification = true /]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"text":"P${project.id}", "nameSpace":"/projects", "action":"${crpSession}/description", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
  {"label":"centerProgram", "nameSpace":"/projects", "action":""}
] /]


[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#if (!availabePhase)!false]
  [#include "/WEB-INF/crp/views/projects/availability-projects.ftl" /]
[#else]
<section class="container">
    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/projects/menu-projects.ftl" /]
      </div>
      [#-- Center Programs Section Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/crp/views/projects/messages-projects.ftl" /]
      
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
           
          [#-- Select Project Program--]
          <h3 class="headTitle">[@customForm.text name="projectPrograms.title" param="${currentCrp.acronym}" /]</h3>
          <div class="borderBox">
          
            [#-- Project Title --]
            <div class="form-group">
              <div class="input">
                <label for="">[@customForm.text name="project.title" /]:</label>
                <p>${(project.composedName)!}</p>
              </div>
            </div>
            
            <hr />
            
            <div class="form-group row">
              [#-- CENTER Research program --]
              <div class="col-md-6 researchProgram ">
                [@customForm.select name="project.projectInfo.liaisonInstitutionCenter.id" listName="liaisonInstitutions" paramText="${currentCrp.acronym}" keyFieldName="id" displayFieldName="composedName" i18nkey="CIAT Program" className="liaisonInstitutionSelect" help="project.researchProgram.help" required=true editable=editable /]
              </div>
            </div>

            <div class="form-group row simpleBox additionalPrograms">
              <div class="col-md-6" listname="additionalPrograms">
                <h4> ${crpSession} Program(s)</h4>
                <div id="projectFlagshipsBlock" class="${customForm.changedField('project.flagshipValue')}">
                [#list (programFlagships)![] as element]                                  
                  [@customForm.checkmark id="program-${element.id}" name="project.flagshipValue" label="${element.centerComposedName}" value="${element.id}" editable=editable checked=(flagshipIds?seq_contains(element.id))!false cssClass="fpInput getCenterOutcomes" cssClassLabel="font-normal" /]
                  <br />
                [/#list]
                </div>
              </div>
              <div class="col-md-6" listname="additionalOffices">
                <h4> Regional Office(s)</h4>
                [#list (regionFlagships)![] as element]
                  [@customForm.checkmark id="region-${element.id}" name="project.regionsValue" label="${element.name}" value="${element.id}" editable=editable  checked=((regionsIds?seq_contains(element.id))!false) cssClass="rpInput" cssClassLabel="font-normal" /]
                  <br />
                [/#list]
              </div>
            </div>
            
            [#-- CENTER Reserach Outcomes (SA Version) --] 
            <div class="form-group">
              [@customForm.elementsListComponent name="project.centerOutcomes" elementType="centerOutcome" elementList=project.centerOutcomes label="projectDescription.researchOutcomes" listName="centerOutcomes" keyFieldName="id" displayFieldName="listComposedName" required=editable /]
            </div>
            
            
          </div>
          
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/crp/views/projects/buttons-projects.ftl" /]
          <input type="hidden"  name="sharedPhaseID" value="${sharedPhaseID}"/>
          [/@s.form] 
      </div> 
      
    </div> 
</section>
[/#if]
 
<span id="liaisonInstitutionsPrograms" style="display:none">{[#list liaisonInstitutions as li]"${li.id}" : ${(li.crpProgram.id)!-1}[#if li_has_next], [/#if][/#list]}</span>


[#include "/WEB-INF/global/pages/footer.ftl"]



