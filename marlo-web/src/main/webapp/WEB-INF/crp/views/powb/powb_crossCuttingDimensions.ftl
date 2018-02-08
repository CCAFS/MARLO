[#ftl]
[#assign title = "POWB Synthesis" /]
[#assign currentSectionString = "powb-${actionName?replace('/','-')}-${liaisonInstitutionID}" /]
[#assign pageLibs = [ ] /]
[#assign customJS = [ ] /]
[#assign customCSS = ["${baseUrlMedia}/css/powb/powbGlobal.css"] /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = "crossCuttingDimensions" /]

[#assign breadCrumb = [
  {"label":"${currentSection}", "nameSpace":"", "action":""},
  {"label":"powbReport", "nameSpace":"powb", "action":"${crpSession}/adjustmentsChanges"},
  {"label":"crossCuttingDimensions", "nameSpace":"powb", "action":"${crpSession}/crossCuttingDimensions"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/crp/pages/header.ftl" /]
[#include "/WEB-INF/crp/pages/main-menu.ftl" /]

[#-- Helptext --]
[@utilities.helpBox name="crossCuttingDimensions.help" /]
    
<section class="container">
  [#-- Program (Flagships and PMU) --]
  [#include "/WEB-INF/crp/views/powb/submenu-powb.ftl" /]
  
  <div class="row">
    [#-- POWB Menu --]
    <div class="col-md-3">
      [#include "/WEB-INF/crp/views/powb/menu-powb.ftl" /]
    </div> 
    <div class="col-md-9">
      [#-- Section Messages --]
      [#include "/WEB-INF/crp/views/powb/messages-powb.ftl" /]
      
      [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
        
        [#-- Title --]
        <h3 class="headTitle">[@s.text name="crossCuttingDimensions.title" /]</h3>
        <div class="borderBox">
          
          [#-- Briefly summarize the main areas of work in 2018 relevant to cross-cutting dimensions --] 
          <div class="form-group">
              [@customForm.textArea name="powbSynthesis.crossCutting.summarize" i18nkey="liaisonInstitution.powb.summarizeCorssCutting"  help="liaisonInstitution.powb.summarizeCorssCutting.help" paramText="${(actualPhase.year)!}" required=true className="limitWords-100" editable=editable && PMU /]
          </div>
        
          [#-- Open Data and Intellectual Assets --] 
          <div class="form-group">
            [@customForm.textArea name="powbSynthesis.crossCutting.assets" i18nkey="liaisonInstitution.powb.openDataIntellectualAssests" help="liaisonInstitution.powb.openDataIntellectualAssests.help" paramText="${(actualPhase.year)!}" required=true className="limitWords-100" editable=editable && PMU /]
          </div>
          
          [#-- Table C: Cross-cutting Aspect of Expected Deliverables (OPTIONAL) --]
          <div class="form-group">
            <h4 class="subTitle headTitle">[@s.text name="crossCuttingDimensions.tableC.title" /]</h4>
            <hr />
            [@tableHMacro /]
          </div>
        
        </div>
        
        [#-- Section Buttons & hidden inputs--]
        [#if PMU]
          [#include "/WEB-INF/crp/views/powb/buttons-powb.ftl" /]
        [/#if]
        
      [/@s.form] 
    </div> 
  </div> 
</section>
[#include "/WEB-INF/crp/pages/footer.ftl"]


[#---------------------------------------------- MACROS ----------------------------------------------]

[#macro tableHMacro ]
  [#assign crossCuttingDimesions = [ 
    { "title": "Gender",
      "principal": "5",
      "significant": "10",
      "notTargeted": "50"
    },
    { "title": "Youth",
      "principal": "25",
      "significant": "30",
      "notTargeted": "45"
    
    },
    { "title": "CapDev",
      "principal": "18",
      "significant": "6",
      "notTargeted": "45"
    
    }
    ]
  /]

  <div class="">
    <table class="table table-bordered">
      <thead>
        <tr>
          <th> [@s.text name="crossCuttingDimensions.tableC.crossCutting" /] </th>
          <th> [@s.text name="crossCuttingDimensions.tableC.principal" /] </th>
          <th> [@s.text name="crossCuttingDimensions.tableC.significant" /] </th>
          <th> [@s.text name="crossCuttingDimensions.tableC.notTargeted" /] </th>
          <th> [@s.text name="crossCuttingDimensions.tableC.overall" /] </th>
        </tr>
      </thead>
      <tbody>
        [#list crossCuttingDimesions as cc]
          <tr>
            <td class="row">${cc.title}</td>
            <td class="text-center"> <span class="animated flipInX">${cc.principal}%</span> </td>
            <td class="text-center"> <span class="animated flipInX">${cc.significant}%</span> </td>
            <td class="text-center"> <span class="animated flipInX">${cc.notTargeted}%</span> </td>
            [#if cc_index == 0]<th rowspan="${crossCuttingDimesions?size}" class="text-center"> <h3 class="animated flipInX">250</span> </h3>[/#if]
          </tr>
        [/#list]
      </tbody>
    </table>
  </div>
[/#macro]