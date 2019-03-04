[#ftl]
[#assign title = "Annual Report ${actualPhase.year}" /]
[#assign currentSectionString = "annualReport-${actionName?replace('/','-')}-${synthesisID}" /]
[#assign currentSection = "synthesis" /]
[#assign currentStage = actionName?split('/')[1]/]
[#assign pageLibs = [ ] /]
[#assign customJS = [ "${baseUrlMedia}/js/annualReport/annualReport_${currentStage}.js" ] /]
[#assign customCSS = ["${baseUrlMedia}/css/annualReport/annualReportGlobal.css"] /]

[#assign breadCrumb = [
  {"label":"${currentSection}",   "nameSpace":"",             "action":""},
  {"label":"annualReport",        "nameSpace":"annualReport${annualReport2018?string('2018', '')}", "action":"${crpSession}/crpProgress"},
  {"label":"${currentStage}",     "nameSpace":"annualReport${annualReport2018?string('2018', '')}", "action":"${crpSession}/{currentStage}"}
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
            
            [#-- Number of peer-reviewed publications --]
            <div class="form-group">
              <h4 class="headTitle">[@s.text name="${customLabel}.peerReviewed.title" /]</h4>
              [@numberOfPublications name="peerReviewed" list=["", ""]/]
            </div>
            
            [#-- Full list of publications published --]
            <div class="form-group">
              <h4 class="headTitle">[@s.text name="${customLabel}.fullList.title" /]</h4>
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
                      [@listOfPublications name="fullList" list=[ ] allowPopups=false /]
                    </div>
                  </div>
                </div>
                [#-- Table --]
                [@listOfPublications name="fullList" list=[ ] allowPopups=true /]
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

[#---------------------------------------------------- MACROS ----------------------------------------------------]

[#macro numberOfPublications name list=[] ]
  
  <div class="form-group">
    <table class="table table-bordered">
      <thead>
        <tr>
          <td></td>
          <th class="text-center"> [@s.text name="${customLabel}.${name}.totalAmount" /] </th>
          <th class="text-center col-md-3"> [@s.text name="${customLabel}.${name}.percent" /] </th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td class="tableTitle">[@s.text name="${customLabel}.${name}.publications" /]</td>
          <td></td>
          <td></td>
        </tr>
        <tr>
          <td class="tableTitle">[@s.text name="${customLabel}.${name}.openAccess" /]</td>
          <td></td>
          <td></td>
        </tr>
        <tr>
          <td class="tableTitle">[@s.text name="${customLabel}.${name}.isi" /]</td>
          <td></td>
          <td></td>
        </tr>
      </tbody>
    </table>
  </div>

[/#macro]

[#macro listOfPublications name list=[] allowPopups=false]


  <div class="form-group">
    <table class="table table-bordered">
      <thead>
        <tr>
          <th class="text-center"> [@s.text name="${customLabel}.${name}.author" /] </th>
          <th class="text-center col-md-2"> [@s.text name="${customLabel}.${name}.date" /] </th>
          <th class="text-center"> [@s.text name="${customLabel}.${name}.article" /] </th>
          <th class="text-center"> [@s.text name="${customLabel}.${name}.journal" /] </th>
          [#if !allowPopups]
            <th class="text-center"> [@s.text name="${customLabel}.${name}.volume" /] </th>
            <th class="text-center"> [@s.text name="${customLabel}.${name}.issue" /] </th>
            <th class="text-center"> [@s.text name="${customLabel}.${name}.page" /] </th>
          [/#if]
            <th class="text-center col-md-2"> [@s.text name="${customLabel}.${name}.openAccess" /] </th>
            <th class="text-center"> [@s.text name="${customLabel}.${name}.isi" /] </th>
          [#if !allowPopups]
           <th class="text-center"> [@s.text name="${customLabel}.${name}.identifier" /] </th>
          [/#if]
          [#if allowPopups]
           <th class="col-md-1 text-center"> [@s.text name="${customLabel}.${name}.includeAR" /] </th>
          [/#if]
        </tr>
      </thead>
      <tbody>
        [#if list?has_content]
          [#list list as item]
          <tr>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            [#if !allowPopups]
              <td></td>
              <td></td>
              <td></td>
              <td></td>
            [/#if]
            <td></td>
            <td></td>
            [#if allowPopups]
              <td class="text-center">
                [@customForm.checkmark id="" name="" checked=false editable=editable centered=true/] 
              </td>
            [/#if]
          </tr>
          [/#list]
        [#else]
          <tr>
            [#if allowPopups]
              <td class="text-center" colspan="7"><i>No entries added yet.</i></td>
            [#else]
              <td class="text-center" colspan="10"><i>No entries added yet.</i></td>
            [/#if]
          </tr>
        [/#if]
      </tbody>
    </table>
  </div>
[/#macro]