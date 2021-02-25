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
  "${baseUrlMedia}/js/annualReport2018/annualReport2018_${currentStage}.js?20200330"
  ]
/]
[#assign customCSS = ["${baseUrlMedia}/css/annualReport/annualReportGlobal.css?20190621"] /]

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
                <li role="presentation" class="active"><a index="0" href="#tab-keyPartnerships" aria-controls="info" role="tab" data-toggle="tab">2.2.1 Highlights of External Partnerships </a></li>
                <li role="presentation" class=""><a index="1" href="#tab-crossPartnerships" aria-controls="info" role="tab" data-toggle="tab">2.2.2 Cross-CGIAR Partnerships</a></li>
              </ul>
              
              [#-- Content --] 
              <div class="tab-content ">
                
                <div id="tab-keyPartnerships" role="tabpanel" class="tab-pane fade in active">
                  [#-- 2.2.1 Highlights of External Partnerships  --]
                  <div class="form-group">
                    [#-- Partnerships summary --]
                    [#if PMU]
                      <div class="form-group">
                        [#-- Word Document Tag --]
                        [@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/]
                        [@customForm.textArea name="${customName}.summary" i18nkey="${customLabel}.summary" help="${customLabel}.summary.help" className="limitWords-300" helpIcon=false required=true editable=editable allowTextEditor=true /]  
                      </div>
                    [#else]
                      <div class="textArea">
                          <label for="">[@customForm.text name="${customLabel}.summary" readText=true /]</label>:
                          <p>[#if (pmuText?has_content)!false]${pmuText?replace('\n', '<br>')}
                               [#else] [@s.text name="global.prefilledByPmu"/] [@utilities.tagPMU label="annualReport.pmuBadge" tooltip="annualReport.pmuBadge.tooltip"/]
                             [/#if]</p>
                      </div>
                    [/#if]
                    <br />
                  </div>
                  
                  [#-- Table 8: Key external partnerships --]
                  [#if PMU]
                    <div class="form-group">
                      [#-- Word Document Tag --]
                      [@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/]

                      [#if true]
                      <div class="form-group btn-group btn-group-sm pull-right" role="group" aria-label="...">
                        <button type="button" class="btn btn-default" data-toggle="modal" data-target="#modal-evidenceF"><span class="glyphicon glyphicon-fullscreen"></span> AR Evidence F</button>
                      </div>
                      [#-- Evidence F: Full List of Current External Partners --]
                      <div class="modal fade" id="modal-evidenceF" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
                        <div class="modal-dialog modal-lg" role="document">
                          <div class="modal-content">
                            <div class="modal-header">
                              <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                              <h4 class="modal-title" id="myModalLabel"> Evidence F: Full List of Current External Partners </h4>
                            </div>
                            <div class="modal-body">
                              [#-- Full table --]
                              <div class="dataTableExport">
                                [@tableEvidenceF  /]
                              </div>
                            </div>
                            <div class="modal-footer">
                              <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                            </div>
                          </div>
                        </div>
                      </div>
                      [/#if]
                                                                  
                      <h4 class="simpleTitle headTitle">[@s.text name="${customLabel}.table7.title" /]</h4>
                      <div class="viewMoreSyntesisTable-block">
                        <table class="table">
                          <thead>
                            <tr>
                              <th>Lead [@s.text name="global.flagship" /] </th>
                              <th>Description of partnership</th>
                              <th>List of key partners</th>
                              <th>Main area of partnership</th>
                              <th class="col-md-1">Include in AR</th>
                            </tr>
                          </thead>
                          <tbody>
                            [#list (flagshipExternalPartnerships?sort_by("id"))![] as item]
                              [#assign crpProgram = (item.reportSynthesisKeyPartnership.reportSynthesis.liaisonInstitution.crpProgram)!]
                              <tr>
                                <td> <span class="programTag" style="border-color:${(crpProgram.color)!'#fff'}">${(crpProgram.acronym)!}</span></td>
                                <td> [@utils.tableText value=(item.description)!"" /] </td>
                                <td> [@utils.tableList list=(item.institutions)![] displayFieldName="institution.composedName" /] </td>
                                <td> [@utils.tableList list=(item.mainAreas)![] displayFieldName="partnerArea.name"  nobr=true /] 
                                  [#-- Other --]
                                  [@utils.tableText value=(item.other)!"" emptyText="" /]
                                </td>
                                <td class="text-center">
                                  [#assign isChecked = ((!reportSynthesis.reportSynthesisKeyPartnership.partnershipIds?seq_contains(item.id))!true) /]
                                  [@customForm.checkmark id="check-${(item.id)!}" name="reportSynthesis.reportSynthesisKeyPartnership.plannedExternalPartnershipsValue" value="${(item.id)!''}" checked=isChecked editable=editable centered=true/]
                                </td>
                              </tr>
                            [/#list]
                          </tbody>
                        </table>
                      </div>
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
                    
                    [#-- Projects Key Partner (ALL) --]
                    <hr />
                    <div class="form-group">
                      <h4 class="simpleTitle headTitle">[@customForm.text name="${customLabel}.projectsPartnerships.title" param="${currentCycleYear}" /] </h4>
                      <div class="viewMoreSyntesis-block">
                        [@projectsKeyPartnershipsTable name="" list=action.projectPartnerships(false) /]
                      </div>
                    </div>
                  [/#if]
               </div>
               
               <div id="tab-crossPartnerships" role="tabpanel" class="tab-pane fade ">
                  [#-- 2.2.2 Cross-CGIAR Partnerships  --]
                    [#-- Summary --]
                    [#if PMU]
                      <div class="form-group">
                        [#-- Word Document Tag --]
                        [@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/]
                    
                        [@customForm.textArea name="${customName}.summaryCgiar" i18nkey="${customLabel}.crossCGIAR.summary" help="${customLabel}.crossCGIAR.summary.help" className="limitWords-300" helpIcon=false required=true editable=editable allowTextEditor=true /]
                      </div>
                      
                      <hr />
                    [#else]
                      [@utilities.tagPMU label="annualReport.pmuBadge" tooltip="annualReport.pmuBadge.tooltip"/]
                    [#-- <div class="form-group">
                       [@utils.tableText value=(reportSynthesis.reportSynthesisKeyPartnership.crossCGIAR)!'' nobr=false emptyText="global.prefilledByPmu" /] 
                    </div> --]
                    [/#if]
                  [#-- Table 9: Internal Cross-CGIAR Collaborations --]
                  <div class="form-group">
                    <br />
                    [#-- Word Document Tag --]
                    [#if PMU][@utilities.tag label="annualReport.docBadge" tooltip="annualReport.docBadge.tooltip"/][/#if]
                    
                    <h4 class="headTitle annualReport-table">[@s.text name="${customLabel}.table8.title" /]</h4>
                    [@customForm.helpLabel name="${customLabel}.table8.help" showIcon=false editable=editable/]
                    
                    [#if PMU]
                      <div class="viewMoreSyntesisTable-block">
                        <table class="table">
                          <thead>
                            <tr>
                              <th></th>
                              <th> Description of collaboration</th>
                              <th> Center, CRP or Platform</th>
                              <th> Value Added</th>
                              <th class="col-md-1"><small> Include in AR </small></th>
                            </tr>
                          </thead>
                          <tbody>
                            [#list (flagshipExternalCollaborations?sort_by("id"))![] as item]
                              [#assign crpProgramColl = (item.reportSynthesisKeyPartnership.reportSynthesis.liaisonInstitution.crpProgram)!]
                              <tr>
                                <td><span class="programTag" style="border-color:${(crpProgramColl.color)!'#fff'}">${(crpProgramColl.acronym)!}</span></td>
                                <td> [@utils.tableText value=(item.description)!"" /] </td>
                                <td> [@utils.tableList list=(item.crps)![] displayFieldName="globalUnit.acronym" nobr=true /] </td>
                                <td> [@utils.tableText value=(item.valueAdded)!"" /] </td>
                                <td class="text-center">
                                  [#assign isChecked = ((!reportSynthesis.reportSynthesisKeyPartnership.collaborationIds?seq_contains(item.id))!true) /]
                                  [@customForm.checkmark id="check-${(item.id)!}" name="reportSynthesis.reportSynthesisKeyPartnership.plannedCollaborationsValue" value="${(item.id)!''}" checked=isChecked editable=editable centered=true/]
                                </td>
                              </tr>
                            [/#list]
                          </tbody>
                        </table>
                      </div>
                    [/#if]
                    
                    <div class="listCrossParnterships">
                      [#if reportSynthesis.reportSynthesisKeyPartnership.collaborations?has_content]
                        [#list reportSynthesis.reportSynthesisKeyPartnership.collaborations as item]
                          [@addCrossCGIARPartnerships element=item name="${customName}.collaborations" index=item_index  isEditable=editable/]
                        [/#list]
                      [/#if]
                    </div>
                    [#if canEdit && editable]
                      <div class="text-right">
                        <div class="addCrossPartnership bigAddButton text-center"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> [@s.text name="annualReport2018.externalPartnerships.addPlatformCollaborationButton"/]</div>
                      </div> 
                    [/#if]
                  </div>
                  
                  [#-- Projects Key Partner (Only CGIAR Institutions) --]
                  <div class="form-group">
                    <br />
                    <hr />
                    <h4 class="simpleTitle headTitle">[@customForm.text name="${customLabel}.projectsPartnerships.keyCgiarPartners" param="${currentCycleYear}" /] </h4>
                    <div class="viewMoreSyntesis-block">
                      [@projectsKeyPartnershipsTable name="" list=action.projectPartnerships(true) /]
                    </div>
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
          [@customForm.input name="${customName}.other" i18nkey="${customLabel}.table7.otherMainArea" className="" required=false editable=editable /] 
        </div>
      </div>
    
      [#-- Partners --]
      <div class="form-group">
        [@customForm.elementsListComponent name="${customName}.institutions" id="${(element.id)!'TEMPLATE'}" elementType="institution" elementList=(element.institutions)![] label="${customLabel}.table7.partners" help=""  listName="partners" keyFieldName="id" displayFieldName="composedName" indexLevel=2 /]
      </div>
      
      [#-- Request partner adition   --]
       <p id="addPartnerText" class="helpMessage">
        [@s.text name="global.addInstitutionMessage" /]
        <a class="popup" href="[@s.url namespace="/projects" action='${crpSession}/partnerSave' ][@s.param name='synthesisID']${(reportSynthesis.id)!}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url]">
          [@s.text name="projectPartners.addPartnerMessage.second" /]
        </a>
       </p>
       
      <br>
    
      [#-- Upload Template --]
      <div class="form-group" style="position:relative" listname="">
        [@customForm.fileUploadAjax 
          fileDB=(element.file)!{} 
          name="${customName}.file.id" 
          label="annualReport2018.externalPartnerships.table7.documentation"
          dataUrl="${baseUrl}/uploadPartnership.do"  
          path=""
          isEditable=editable
          labelClass=""
          required=false
        /]
      </div>
      
  </div>
[/#macro]


[#macro projectsKeyPartnershipsTable name="" list=["",""]]
  <table class="annual-report-table table-border">
    <thead>
      <tr class="subHeader">
        <th class="col-md-1"> Project</th>
        <th class="col-md-3"> Partner </th>
        <th class="col-md-1"> Formal</th>
        <th class="col-md-7"> Responsibilities </th>
      </tr>
    </thead>
    <tbody>
      [#if list?has_content]
        [#list list as item]
          [#list item.partners as partner]
            [#local url][@s.url namespace="/projects" action="${(crpSession)!}/partners"][@s.param name='projectID']${(item.project.id)!}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
            <tr>
              <td class="text-center">
                [#if (item.project.id?has_content)!false]
                  <a href="${url}" target="_blank"> P${item.project.id} </a>
                [#else]
                  <i style="opacity:0.5">PID</i>
                [/#if]
              </td>
              <td class="">
                [@utils.tableText value=(partner.institution.composedName)!"" /] 
              </td>
              <td class="text-center">
                [@utils.tableText value=(partner.hasPartnerships?string('Yes', 'No'))!"" /]
              </td>
               <td class="text-justify">
                [@utils.tableText value=(partner.responsibilities)!"" /] 
              </td>
            </tr>
          [/#list]
        [/#list]
      [#else]
        
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
      [@customForm.elementsListComponent name="${customName}.crps" id="${(element.id)!'TEMPLATE'}" elementType="globalUnit" elementList=(element.crps)![] label="${customLabel}.table8.crp" help=""  listName="globalUnits" keyFieldName="id" displayFieldName="composedName" indexLevel=2 /]
    </div>
    [#-- Description of collaboration --]
    <div class="form-group">
      [@customForm.textArea name="${customName}.description" i18nkey="${customLabel}.table8.description" help="${customLabel}.table8.description.help" helpIcon=false required=true editable=editable allowTextEditor=true /]
    </div>
    [#-- Value added --]
    <div class="form-group">
      [@customForm.input name="${customName}.valueAdded" i18nkey="${customLabel}.table8.value" help="${customLabel}.table8.value.help" helpIcon=false required=false editable=editable /]
    </div>
  </div>
[/#macro]




[#macro tableEvidenceF]
  <div class="table-responsive">
    <table class="table table-bordered">
      <thead>
        <tr>
          <th> ID </th>
          <th> Name of partner organization</th>
          <th> Organization Type</th>
          <th> Partner Headquarter </th>
        </tr>
      </thead>
      <tbody>
        [#list (evidencePartners)![] as institution ]
          <tr>
            <td> ${(institution.id)!""}  </td>
            <td> ${(institution.composedName)!""} </td>
            <td> ${(institution.institutionType.repIndOrganizationType.name)!""} </td>
            <td> 
              [#list (institution.locations)![] as location]
                [#if (location.headquater)!false]
                  ${(location.locElement.name)!""}
                [/#if]
              [/#list]
            </td>
          </tr>
        [/#list]
      </tbody>
    </table>
  </div>
[/#macro]
