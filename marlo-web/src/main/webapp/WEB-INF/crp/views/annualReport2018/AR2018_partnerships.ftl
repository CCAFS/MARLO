[#ftl]
[#assign title = "Annual Report ${actualPhase.year}" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ "select2", "blueimp-file-upload", "trumbowyg", "datatables.net", "datatables.net-bs" ] /]
[#assign customJS = [ 
  "https://www.gstatic.com/charts/loader.js",
  "https://cdn.datatables.net/buttons/1.3.1/js/dataTables.buttons.min.js",
  "//cdn.datatables.net/buttons/1.3.1/js/buttons.html5.min.js",
  "//cdn.datatables.net/buttons/1.3.1/js/buttons.print.min.js",
  "${baseUrlMedia}/js/annualReport/annualReportGlobal.js",
  "${baseUrlMedia}/js/annualReport2018/annualReport2018_${currentStage}.js"
  ] 
/]
[#assign customCSS = ["${baseUrlMedia}/css/annualReport/annualReportGlobal.css"] /]

[#assign breadCrumb = [
  {"label":"${currentSection}",   "nameSpace":"",             "action":""},
  {"label":"annualReport",        "nameSpace":"annualReport${annualReport2018?string('2018', '')}", "action":"${crpSession}/crpProgress"},
  {"label":"${currentStage}",     "nameSpace":"annualReport${annualReport2018?string('2018', '')}", "action":"${crpSession}/{currentStage}"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#assign customName= "reportSynthesis.reportSynthesisKeyPartnership" /]
[#assign customLabel= "annualReport2018.${currentStage}" /]

[#-- Helptext --]
[@utilities.helpBox name="${customLabel}.help" /]
    
<section class="container">
  [#if !reportingActive]
    <div class="borderBox text-center">Annual Report is availbale only at Reporting cycle</div>
  [#else]
    [#-- Program (Flagships and PMU) --]
    [#include "/WEB-INF/crp/views/annualReport2018/submenu-AR2018.ftl" /]
    
    <div class="row">
      [#-- POWB Menu --]
      <div class="col-md-3">[#include "/WEB-INF/crp/views/annualReport2018/menu-AR2018.ftl" /]</div>
      [#-- POWB Content --]
      <div class="col-md-9">
        [#-- Section Messages --]
        [#include "/WEB-INF/crp/views/annualReport2018/messages-AR2018.ftl" /]
        
        [@s.form action=actionName method="POST" enctype="multipart/form-data" cssClass=""]
          [#-- Title --]
          <h3 class="headTitle">[@s.text name="${customLabel}.title" /]</h3>
          <div class="">
          
            <div class="bootstrapTabs">
              [#-- Tabs --] 
              <ul class="nav nav-tabs" role="tablist"> 
                <li role="presentation" class="active"><a index="1" href="#tab-keyPartnerships" aria-controls="info" role="tab" data-toggle="tab">2.2.1 Highlights of External Partnerships </a></li>
                <li role="presentation" class=" "><a index="2" href="#tab-crossPartnerships" aria-controls="info" role="tab" data-toggle="tab">2.2.2 Cross-CGIAR Partnerships</a></li>
              </ul>
              
              [#-- Content --] 
              <div class="tab-content ">
                
                <div id="tab-keyPartnerships" role="tabpanel" class="tab-pane fade in active">
                  [#-- 2.2.1 Highlights of External Partnerships  --]
                  <div class="form-group">
                    <h5 class="sectionSubTitle">[@s.text name="${customLabel}.highlights.title" /]</h5>
                    [#-- Partnerships summary --]
                    [#if PMU]
                      <div class="form-group">
                        [@customForm.textArea name="${customName}.summary" i18nkey="${customLabel}.summary" help="${customLabel}.summary.help" className="limitWords-300" helpIcon=false required=true editable=editable allowTextEditor=true /]
                      </div>
                    [#else]
                      <div class="textArea">
                          <label for="">[@customForm.text name="${customLabel}.summary" readText=true /]</label>:
                          <p>[#if (pmuText?has_content)!false]${pmuText?replace('\n', '<br>')}[#else] [@s.text name="global.prefilledByPmu"/] [/#if]</p>
                      </div>
                    [/#if]
                    <br />
                  </div>
                
                  
                  [#-- Table 8: Key external partnerships --]
                  [#if PMU]
                    <div class="form-group">
                      <h4 class="simpleTitle headTitle">[@s.text name="${customLabel}.table7.title" /]</h4>
                      <table class="table">
                        <thead>
                          <tr>
                            <th>Lead FP</th>
                            <th>Description of partnership</th>
                            <th>List of key partners</th>
                            <th>Main area of partnership</th>
                            <th class="col-md-1">Include in AR</th>
                          </tr>
                        </thead>
                        <tbody>
                          [#list (flagshipExternalPartnerships)![] as item]
                            [#assign crpProgram = (item.reportSynthesisKeyPartnership.reportSynthesis.liaisonInstitution.crpProgram)!]
                            <tr>
                              <td> <span class="programTag" style="border-color:${(crpProgram.color)!'#fff'}">${(crpProgram.acronym)!}</span></td>
                              <td> [@utils.tableText value=(item.description)!"" /] </td>
                              <td> [@utils.tableList list=(item.mainAreas)![] displayFieldName="title" /] </td>
                              <td> [@utils.tableList list=(item.institutions)![] displayFieldName="title" /] </td>
                              <td class="text-center">
                                [#assign isChecked = ((!reportSynthesis.reportSynthesisKeyPartnership.selectedExternalPartnerships?seq_contains(item.id))!true) /]
                                [@customForm.checkmark id="check-${(item.id)!}" name="reportSynthesis.reportSynthesisKeyPartnership.plannedExternalPartnershipsValue" value="${(item.id)!''}" checked=isChecked editable=editable centered=true/]
                              </td>
                            </tr>
                          [/#list]
                        </tbody>
                      </table>
                    </div>
                  [#else]
                    <div class="form-group">
                      <h4 class="simpleTitle headTitle">[@s.text name="${customLabel}.table7.title" /]</h4>
                      <div class="listKeyPartnerships">
                        [#if reportSynthesis.reportSynthesisKeyPartnership.partnerships?has_content]
                          [#list reportSynthesis.reportSynthesisKeyPartnership.partnerships as item]
                            [@addKeyExternalPartnership element=item name="${customName}.partnerships" index=item_index isEditable=editable/]
                          [/#list]
                        [/#if]
                      </div>
                      [#if canEdit && editable]
                        <div class="text-right">
                          <div class="addKeyPartnership bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="annualReport2018.externalPartnerships.addPartnershipButton"/]</div>
                        </div> 
                      [/#if]
                      <br />
                    </div>
                    
                    [#-- Projects Key Partnerships --]
                    <div class="form-group">
                      <h4 class="simpleTitle headTitle">[@customForm.text name="${customLabel}.projectsPartnerships.title" param="${currentCycleYear}" /] (${projectKeyPartnerships?size})</h4>
                      <div class="viewMoreSyntesis-block">
                        [@projectsKeyPartnershipsTable name="${customName}.projectsPartnerships" list=projectKeyPartnerships /]
                      </div>
                    </div>
                  [/#if]
               </div>
               
               <div id="tab-crossPartnerships" role="tabpanel" class="tab-pane fade  ">
                  [#-- 2.2.2 Cross-CGIAR Partnerships  --]
                  <div class="form-group">
                    <h5 class="sectionSubTitle">[@s.text name="${customLabel}.crossCGIAR.title" /]</h5>
                    [#-- Summary --]
                    [#if PMU]
                      <div class="form-group">
                        [@customForm.textArea name="${customName}.crossCGIAR.summary" i18nkey="${customLabel}.crossCGIAR.summary" help="${customLabel}.crossCGIAR.summary.help" className="limitWords-300" helpIcon=false required=true editable=editable allowTextEditor=true /]
                      </div>
                    [#else]
                      <div class="textArea">
                        <label for="">[@customForm.text name="${customLabel}.crossCGIAR.summary" readText=true /]:</label>
                        <p>[#if (pmuText?has_content)!false]${pmuText?replace('\n', '<br>')}[#else] [@s.text name="global.prefilledByPmu"/] [/#if]</p>
                      </div>
                    [/#if]
                    <br />
                  </div>
                  
                  [#-- Table 8: Internal Cross-CGIAR Collaborations --]
                  <div class="form-group">
                    <h4 class="headTitle annualReport-table">[@s.text name="${customLabel}.table8.title" /]</h4>
                    [@customForm.helpLabel name="${customLabel}.table8.help" showIcon=false/]
                    <div class="listCrossParnterships">
                    [#if crossCGIARp?has_content]
                      [#list crossCGIARp as item]
                        [@addCrossCGIARPartnerships element=item name="${customName}.externalPartnerships" index=item_index  isEditable=editable/]
                      [/#list]
                    [/#if]
                    </div>
                    [#if canEdit && editable]
                      <div class="text-right">
                        <div class="addCrossPartnership bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="annualReport2018.externalPartnerships.addPlatformCollaborationButton"/]</div>
                      </div> 
                    [/#if]
                  </div>
                  
               </div>
                
              </div>
              [#-- Section Buttons & hidden inputs --]
              [#include "/WEB-INF/crp/views/annualReport2018/buttons-AR2018.ftl" /]
            [/@s.form]
          </div>
        </div>
      </div>
    </div>
  [/#if] 
</section>

[#--  Key Partnerships Template --]
[@addKeyExternalPartnership element={} name="${customName}.partnerships" index=-1 template=true /]
[@addCrossCGIARPartnerships element={} name="${customName}.collaborations" index=-1 template=true /]

[#include "/WEB-INF/global/pages/footer.ftl"]

[#---------------------------------------------------- MACROS ----------------------------------------------------]

[#macro addKeyExternalPartnership element name index template=false isEditable=true]
    [#local customName = "${name}[${index}]" /]
    <div id="keyPartnerships-${template?string('template', index)}" class="keyPartnership borderBox form-group" style="position:relative; display:${template?string('none','block')}">
      
      [#-- Index --]
      <div class="leftHead blue sm"><span class="index">${index+1}</span></div>
    
      [#-- Remove Button --]
      [#if isEditable]<div class="removeKeyPartnership removeElement sm" title="Remove"></div>[/#if]
      [#-- Hidden inputs --]
      <input type="hidden" name="${customName}.id" value="${(element.id)!}"/> 
      <br />
      
      [#-- 
      <div class="form-group">
        <label for="">[@s.text name="annualReport2018.externalPartnerships.table7.lead" /]:</label>
        [#local crpProgram = (reportSynthesis.liaisonInstitution.crpProgram)!{} /]
        ${(crpProgram.acronym)!}
      </div>
       --]
      
      [#-- Description --]
      <div class="form-group">
        [@customForm.input name="${customName}.description" i18nkey="${customLabel}.table7.description" helpIcon=false className="limitWords-30" required=true editable=editable /]
      </div>

      [#-- Main area of partnership --]
      <div class="form-group row">
        <div class="col-md-6">
          [@customForm.elementsListComponent name="${customName}.mainAreas" id="${(element.id)!'TEMPLATE'}" elementType="partnerArea" elementList=(element.mainAreas)![] label="${customLabel}.table7.mainArea" help=""  listName="mainAreasSel" keyFieldName="id" displayFieldName="name"  indexLevel=2 /]
        </div>
        [#local otherArea = false /]  
        [#list (element.mainAreas)![] as item] 
          [#if (item.partnerArea.id == 6)!false][#local otherArea = true /][#break][/#if]  
        [/#list]  
        <div class="col-md-6 block-pleaseSpecify" style="display:${otherArea?string('block', 'none')}"> 
          [@customForm.input name="${customName}.otherPartnershipMainArea" i18nkey="${customLabel}.table7.otherMainArea" className="" required=false editable=editable /] 
        </div>
      </div>
    
      [#-- Partners --]
      <div class="form-group">
        [@customForm.elementsListComponent name="${customName}.institutions" id="${(element.id)!'TEMPLATE'}" elementType="institution" elementList=(element.institutions)![] label="${customLabel}.table7.partners" help=""  listName="partners" keyFieldName="id" displayFieldName="composedName" indexLevel=2 /]
      </div>
    
      [#-- Upload Template --]
      <div class="form-group" style="position:relative" listname="">
        [#if true]
          [@customForm.fileUploadAjax 
            fileDB=(element.file)!{} 
            name="${customName}.file.id" 
            label="annualReport2018.externalPartnerships.table7.documentation"
            dataUrl="${baseUrl}/uploadPartnership.do"  
            path="${(action.getPath())!}"
            isEditable=editable
            labelClass=""
            required=false
          /]
        [#else]
          <p><i> Once you have save this new Key Partnership you will be able to upload the documentation.</i></p>
        [/#if]
        
      </div>
    
  </div>

[/#macro]


[#macro projectsKeyPartnershipsTable name="" list=["",""]]
  <table class="annual-report-table table-border">
    <thead>
      <tr class="subHeader">
        <th id="tb-projectId">[@s.text name="${customLabel}.projectsPartnerships.id" /]</th>
        <th id="tb-phase">[@s.text name="${customLabel}.projectsPartnerships.mainArea" /]</th>
      </tr>
    </thead>
    <tbody>
      [#if list?has_content]
        [#list list as item]
          [#local url][@s.url namespace="/projects" action="${(crpSession)!}/partners"][@s.param name='projectID']${(item.project.id)!}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
          <tr>
            <td class="text-center">
              [#if (item.project.id?has_content)!false]
                <a href="${url}" target="_blank"> P${item.project.id} </a>
              [#else]
                <i style="opacity:0.5">PID</i>
              [/#if]
            </td>
            <td class="text-justify">
              [#if (item.lessons?has_content)!false]
                ${item.lessons}
              [#else]
                <i style="opacity:0.5">From Projects</i>
              [/#if]
            </td>           
          </tr>
        [/#list]
      [#else]
        <tr>
          <td class="text-center" colspan="6"><i>No entries added yet.</i></td>
        </tr>
      [/#if]
    </tbody>
  </table>
[/#macro]


[#macro addCrossCGIARPartnerships element name index template=false isEditable=true]
  [#local customName = "${name}[${index}]" /]
  <div id="crossPartnerships-${template?string('template', index)}" class="crossPartnership borderBox form-group" style="position:relative; display:${template?string('none','block')}">

    [#-- Index --]
    <div class="leftHead blue sm"><span class="index">${index+1}</span></div>
    [#-- Remove Button --]
    [#if isEditable]<div class="removeCrossPartnership removeElement sm" title="Remove"></div>[/#if]
    [#-- Hidden inputs --]
    <input type="hidden" name="${customName}.id" value="${(element.id)!}"/> 
    <br />

    [#-- CRP/Platform --] 
    <div class="form-group">
      [@customForm.select name="${customName}.crp" label="" keyFieldName="id" displayFieldName="acronym" i18nkey="${customLabel}.table8.crp" listName="globalUnitList"  required=true  className="globalUnitSelect" editable=isEditable/]
    </div>
    
    [#-- Description of collaboration --]
    <div class="form-group">
      [@customForm.textArea name="${customName}.description" i18nkey="${customLabel}.table8.description" help="${customLabel}.table8.description.help" helpIcon=false required=true editable=editable allowTextEditor=true /]
    </div>
    
    [#-- Value added --]
    <div class="form-group">
      [@customForm.input name="${customName}.value" i18nkey="${customLabel}.table8.value" help="${customLabel}.table8.value.help" helpIcon=false required=true editable=editable /]
    </div>
    
  </div>
[/#macro]