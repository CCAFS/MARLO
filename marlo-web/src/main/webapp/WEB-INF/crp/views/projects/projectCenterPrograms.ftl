[#ftl]
[#assign title = "Project Programs" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = [
  "${baseUrlMedia}/js/projects/projectDescription.js", 
  "${baseUrl}/global/js/fieldsValidation.js",
  "${baseUrl}/global/js/autoSave.js"
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
                [@customForm.select name="project.projectInfo.researchProgram.id" listName="researchPrograms" paramText="${currentCrp.acronym}" keyFieldName="id" displayFieldName="name" i18nkey="project.researchProgram" className="projectResearchProgram" help="project.researchProgram.help" editable=true /]
              </div>
            </div>

            <div class="form-group row simpleBox">
              <div class="col-md-6 ">
                <h4> Additional Program(s)</h4>
                [#list (programFlagships)![] as element] 
                  [@customForm.checkmark id="program-${element.id}" name="project.flagshipValue" label="${element.composedName}" value="${element.id}" editable=true checked=(flagshipIds?seq_contains(element.id))!false cssClass="fpInput" cssClassLabel="font-normal" /]
                  <br />
                [/#list]
              </div>
              <div class="col-md-6 ">
                <h4> Additional Region(s)</h4>
                [#list (regionFlagships)![] as element]
                  [@customForm.checkmark id="region-${element.id}" name="project.regionsValue" label="${element.composedName}" value="${element.id}" editable=true  checked=((regionsIds?seq_contains(element.id))!false) cssClass="rpInput" cssClassLabel="font-normal" /]
                  <br />
                [/#list]
              </div>
            </div>
            
          </div>
          
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/crp/views/projects/buttons-projects.ftl" /]
          
          [/@s.form] 
      </div> 
    </div> 
</section>
[/#if]


<span id="liaisonInstitutionsPrograms" style="display:none">{[#list liaisonInstitutions as institution]"${institution}" : ${(institution.crpProgram.id)!-1}[#if institution_has_next], [/#if][/#list]}</span>
  
[#include "/WEB-INF/global/pages/footer.ftl"]



