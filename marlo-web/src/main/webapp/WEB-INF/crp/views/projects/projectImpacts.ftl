[#ftl]
[#assign title = "Project Impacts" /]
[#assign currentSectionString = "project-${actionName?replace('/','-')}-${projectID}-phase-${(actualPhase.id)!}" /]
[#assign pageLibs = ["select2"] /]
[#assign customJS = [ 
  "${baseUrlMedia}/js/projects/projectImpacts.js?20211013a", 
  "${baseUrlCdn}/global/js/autoSave.js",
  "${baseUrlCdn}/global/js/fieldsValidation.js"
  ] 
/]
[#assign customCSS = [
  ""
  ] 
/]

[#assign breadCrumb = [
  {"label":"projectsList", "nameSpace":"/projects", "action":"${(crpSession)!}/projectsList"},
  {"text":"P${project.id}", "nameSpace":"/projects", "action":"${crpSession}/description", "param": "projectID=${project.id?c}&edit=true&phaseID=${(actualPhase.id)!}"},
  {"label":"projectImpacts", "nameSpace":"/projects", "action":""}
] /]

[#assign currentSection = "projects" /]
[#assign currentStage = "covid19" /]
[#assign hideJustification = true /]
[#assign isCrpProject = (action.isProjectCrpOrPlatform(project.id))!false ]
[#assign isCenterProject = (action.isProjectCenter(project.id))!false ]
[#assign impactCategory = (action.getCategoryById(actualProjectImpact.projectImpactCategoryId))! ]
[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

<section class="container">
    <div class="row">
      [#-- Project Menu --]
      <div class="col-md-3">
        [#include "/WEB-INF/crp/views/projects/menu-projects.ftl" /]
      </div>
      [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
        <div class="col-md-9">
          <h3 class="headTitle">[@s.text name="projects.impacts.covid19Title" /]</h3>
          <div id="projectImpactCovid19" class="borderBox">

            <div class="form-group">        
              [@customForm.textArea name="actualProjectImpact.answer" i18nkey="projects.impacts.covid19ImpactQuestion${actualPhase.year}" placeholder="" help="projects.impacts.covid19ImpactHelp" className="project-title limitWords-300" helpIcon=false required=true editable=editable /]
            </div>

            [#if editable]
            <div class="form-group">  
              [#-- project category --]
              [@customForm.select name="actualProjectImpact.projectImpactCategoryId" className="impactsCategoriesSelect" i18nkey="projects.impacts.covid19CategoryTitle"  disabled=!editable  listName="projectImpactsCategories" keyFieldName="id"  displayFieldName="composedName" required=false editable=editable required=true/]
            </div>

            [#else]
              [@customForm.select name="" className="impactsCategoriesSelect" value="ejemplo" i18nkey="projects.impacts.covid19CategoryTitle"  disabled=!editable  listName="" keyFieldName="id"   displayFieldName="composedName" required=false editable=false required=true/]
               [#if actualProjectImpact.projectImpactCategoryId?has_content]
                  [@s.text name="${(impactCategory.name)!} - ${(impactCategory.description)!}" /]
                  [#else]
                  [@s.text name="form.values.fieldEmpty" /]
               [/#if]
            [/#if]
           
          
            [#if actualPhase.year = 2021]
              </br>
              [#list historyProjectImpacts as historicProject]
                <label>[@s.text name="projects.impacts.covid19.answer" /]</label>
                <div>${historicProject.answer}</div>
              [/#list]  
            [/#if]

          </div>  
        </div>
        [#-- Section Buttons & hidden inputs--]
        [#include "/WEB-INF/crp/views/projects/buttons-projects.ftl" /]
      [/@s.form]  
    </div>
    <span id="actualPhase" style="display: none">${action.isSelectedPhaseAR2021()?c}</span>
</section>

[#include "/WEB-INF/global/pages/footer.ftl"]
