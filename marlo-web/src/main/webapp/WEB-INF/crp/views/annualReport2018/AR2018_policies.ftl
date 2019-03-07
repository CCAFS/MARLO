[#ftl]
[#assign title = "Annual Report ${actualPhase.year}" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ "datatables.net", "datatables.net-bs", "components-font-awesome", "trumbowyg" ] /]
[#assign customJS = [ 
  "https://www.gstatic.com/charts/loader.js",
  "${baseUrlMedia}/js/annualReport2018/annualReport2018_${currentStage}.js",
  "${baseUrlMedia}/js/annualReport/annualReportGlobal.js"
] /]
[#assign customCSS = ["${baseUrlMedia}/css/annualReport/annualReportGlobal.css"] /]

[#assign breadCrumb = [
  {"label":"${currentSection}",   "nameSpace":"",             "action":""},
  {"label":"annualReport",        "nameSpace":"annualReport${annualReport2018?string('2018', '')}", "action":"${crpSession}/crpProgress"},
  {"label":"table2.${currentStage}",     "nameSpace":"annualReport${annualReport2018?string('2018', '')}", "action":"${crpSession}/{currentStage}"}
]/]

[#import "/WEB-INF/global/macros/utils.ftl" as utilities /]
[#include "/WEB-INF/global/pages/header.ftl" /]
[#include "/WEB-INF/global/pages/main-menu.ftl" /]

[#assign customName= "reportSynthesis" /]
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
          <div class="borderBox">
          
            
              [#-- Table 2: Condensed list of policy contributions --]
              <div class="form-group">
                [#assign guideSheetURL = "https://drive.google.com/file/d/1GYLsseeZOOXF9zXNtpUtE1xeh2gx3Vw2/view" /]
                <small class="pull-right"><a href="${guideSheetURL}" target="_blank"> <img src="${baseUrl}/global/images/icon-file.png" alt="" /> #I1 Policies  -  Guideline </a> </small>
              </div>
              <br />
              <div class="form-group row">
              <div class="col-md-9">
                [#-- Chart 6 - Organizations designing/promulgating the policy --]
                <div id="chart6" class="chartBox simpleBox">
                  <ul class="chartData" style="display:none">
                    [#assign organizationTypeByStudiesDTOs = [
                      { 
                        "projectExpectedStudies": 1,
                        "repIndOrganizationType": {
                          "name": "Name"
                        },
                        "projectExpectedStudies": [1, 2, 3]
                      },
                      { 
                        "projectExpectedStudies": 1,
                        "repIndOrganizationType": {
                          "name": "Name 2"
                        },
                        "projectExpectedStudies": [1, 2, 3, 4]
                      },
                      { 
                        "projectExpectedStudies": 1,
                        "repIndOrganizationType": {
                          "name": "Name 3"
                        },
                        "projectExpectedStudies": [1, 2, 3, 4, 5]
                      }
                    ] /]
                    <li>
                      <span>[@s.text name="${customLabel}.indicatorI1.chart1.0" /]</span>
                      <span>[@s.text name="${customLabel}.indicatorI1.chart1.1" /]</span>
                      <span class="json">{"role":"style"}</span>
                      <span class="json">{"role":"annotation"}</span>
                    </li>
                    [#list (organizationTypeByStudiesDTOs)![] as data]
                      [#if (data.projectExpectedStudies?has_content)!false]
                      <li>
                        <span>${(data.repIndOrganizationType.name)!}</span>
                        <span class="number">${data.projectExpectedStudies?size}</span>
                        <span>#4285f4</span>
                        <span>${data.projectExpectedStudies?size}</span>
                      </li>
                      [/#if]
                    [/#list]
                  </ul>
                </div> 
              </div>
              [#assign totalPolicies = 12 /]
              <div class="col-md-3">
                <div class="simpleBox numberBox">
                  <label for="">[@s.text name="${customLabel}.indicatorI1.totalPolicies" /]</label><br />
                  <span class="animated infinite bounce">${(totalPolicies?number?string(",##0"))!0}</span>
                </div>
              </div>
            </div>
            
            <div class="form-group row">
              <div class="col-md-9">
              [#-- Chart 7 - CGIAR Cross-cutting markers --]
                <div id="chart7" class="chartBox simpleBox">
                  <ul class="chartData" style="display:none">
                    [#assign levelsList = ["Not Targeted", "Significant Objective", "Principal Objective"] /]
                    [#assign crossCuttingM = [
                      { 
                        "name": "Gender",
                        "cclevels": [3, 4, 5]
                      },
                      { 
                        "name": "Youth",
                        "cclevels": [4, 5, 3]
                      },
                      { 
                        "name": "Climate Change",
                        "cclevels": [5, 3, 4]
                      }
                    ] /]
                    <li>
                      <span>[@s.text name="${customLabel}.indicatorI1.chart1.0" /]</span>
                      <span>[@s.text name="${customLabel}.indicatorI1.chart1.1" /]</span>
                      <span class="json">{"role":"style"}</span>
                      <span class="json">{"role":"annotation"}</span>
                    </li>
                    [#list (crossCuttingM)![] as data]
                      [#if (data?has_content)!false]
                      <li>
                        <span>${(data.name)!}</span>
                        <span class="number">
                          [#list (data.cclevels)![] as levels]
                            ${(levels)}
                          [/#list]
                        </span>
                        <span>#4285f4</span>
                        <span></span>
                      </li>
                      [/#if]
                    [/#list]
                  </ul>
                </div> 
               </div>
            </div>
              
              <div class="form-group">
              [#-- Modal Large --]
                <button type="button" class="pull-right btn btn-link btn-sm" data-toggle="modal" data-target="#tableA-bigger"> 
                  <span class="glyphicon glyphicon-fullscreen"></span> See Full Table 2
                </button>
                <div id="tableA-bigger" class="modal fade bs-example-modal-lg " tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel">
                  <div class="modal-dialog modal-lg bigger" role="document">
                    <div class="modal-content">
                      <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                      </div>
                      [@table2ListOfPolicies list=[] allowPopups=false/]
                    </div>
                  </div>
                </div>
                [#-- Table --]
                [@table2ListOfPolicies list=[] allowPopups=true/]
              </div>
          
          </div>
          [#-- Section Buttons & hidden inputs--]
          [#include "/WEB-INF/crp/views/annualReport2018/buttons-AR2018.ftl" /]
        [/@s.form] 
      </div> 
    </div>
  [/#if] 
</section>
[#include "/WEB-INF/global/pages/footer.ftl"]


[#------------------------------------  MACROS  ------------------------------------------]


[#macro table2ListOfPolicies list=[] allowPopups=true id=""]

  <table id="tableA" class="table table-bordered">
    <thead>
      <tr>
        <th class="text-center" rowspan="2">[@s.text name="${customLabel}.table2.name" /]</th>
        <th class="text-center" rowspan="2">[@s.text name="${customLabel}.table2.maturity" /]</th>
        <th class="col-md-2 text-center" rowspan="2">[@s.text name="${customLabel}.table2.subIDOs" /]</th>
        [#if !allowPopups]
          <th class="text-center" colspan="4">[@s.text name="${customLabel}.table2.crossCutting" /]</th>
          <th class="text-center" rowspan="2">[@s.text name="${customLabel}.table2.type" /]</th>
          <th class="text-center" rowspan="2">[@s.text name="${customLabel}.table2.whose" /]</th>
          <th class="text-center" rowspan="2">[@s.text name="${customLabel}.table2.geoScope" /]</th>
        [/#if]
        [#if allowPopups]
          <th class="col-md-1 text-center" rowspan="2">[@s.text name="${customLabel}.table2.includeAR" /]</th>
        [/#if]
      </tr>
      [#if !allowPopups]
      <tr>
          <th class="text-center"> Gender </th>
          <th class="text-center"> Youth </th>
          <th class="text-center"> CapDev</th>
          <th class="text-center"> Climate Change</th>
        </tr>
      [/#if]
    </thead>
    <tbody>
     [#if list?has_content]
      <tr>
        <td>
        </td>
        <td>
        </td>
        [#if !allowPopups]
          <td>
          </td>
          <td>
          </td>
          <td>
          </td>
          <td>
          </td>
          <td>
          </td>
          <td>
          </td>
          <td>
          </td>
        [/#if]
        <td>
        </td>
        [#if allowPopups]
          <td class="text-center">
            [@customForm.checkmark id="" name="" checked=false editable=editable centered=true/] 
          </td>
        [/#if]
      </tr>
      [#else]
        [#if allowPopups]
          <td class="text-center" colspan="4"><i>No entries added yet.</i></td>
        [#else]
          <td class="text-center" colspan="10"><i>No entries added yet.</i></td>
        [/#if]
      [/#if]
    </tbody>
  </table>

[/#macro]