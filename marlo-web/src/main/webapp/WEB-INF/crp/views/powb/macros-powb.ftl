[#ftl]
[#macro projectBudgetsByFlagshipMacro element type tiny=false popupEnabled=true]
  
  [#if !popupEnabled]
    <nobr>US$ <span >[#if type == "W1W2"]${element.w1?number?string(",##0.00")}[#elseif type == "W3BILATERAL"]${element.w3?number?string(",##0.00")}[/#if]</span></nobr>
  [#else]
    [#local projects = (action.loadFlagShipBudgetInfoProgram(element.id))![] ]
    [#if projects?size > 0]
    <a class=" btn btn-default btn-xs" data-toggle="modal" data-target="#projectBudgets-${type}-${element.id}">
       US$ <span >[#if type == "W1W2"]${element.w1?number?string(",##0.00")}[#elseif type == "W3BILATERAL"]${element.w3?number?string(",##0.00")}[/#if]</span>
    </a>
    
    <!-- Modal -->
    <div class="modal fade" id="projectBudgets-${type}-${element.id}" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
      <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
            <h4 class="modal-title" id="myModalLabel">
              [#if type == "W1W2"]
                [@s.text name="expectedProgress.projectBudgetsW1w2" /]
              [#elseif type == "W3BILATERAL"]
                [@s.text name="expectedProgress.projectBudgetsW3Bilateral" /]
              [/#if]
            </h4>
            <span class="programTag" style="border-color:${(element.color)!'#fff'}">${element.composedName}</span> 
          </div>
          <div class="modal-body">
            <div class="">
              <table class="table table-bordered">
                <thead>
                  <tr>
                    <th rowspan="2">[@s.text name="project.title" /]</th>
                    [#if type == "W1W2"] <th colspan="2" class="text-center">[@s.text name="project.coreBudget" /]</th>[/#if]
                    [#if type == "W3BILATERAL"] <th colspan="2" class="text-center">W3</th>[/#if]
                    [#if type == "W3BILATERAL"] <th colspan="2" class="text-center">Bilateral</th>[/#if]
                  </tr>
                  <tr>
                    [#if type == "W1W2"]
                      <th class="text-center"> Total project [@s.text name="project.coreBudget" /] Amount</th> 
                      <th class="text-center">  ${element.acronym} % Contribution and amount </th>
                    [/#if]
                    [#if type == "W3BILATERAL"]
                      <th class="text-center"> Total project[@s.text name="project.w3Budget" /] Amount</th>
                      <th class="text-center"> ${element.acronym} % Contribution and amount</th>
                    [/#if]
                    [#if type == "W3BILATERAL"]
                      <th class="text-center"> Total project[@s.text name="project.bilateralBudget" /] Amount</th>
                      <th class="text-center"> ${element.acronym} % Contribution and amount</th>
                    [/#if]
                  </tr>
                </thead>
                <tbody>
                  [#local totalProjectsW1W2 = 0 /]
                  [#local totalContributionW1W2 = 0 /]
                  
                  [#local totalProjectsW3 = 0 /]
                  [#local totalContributionW3 = 0 /]
                  
                  [#local totalProjectsBilateral = 0 /]
                  [#local totalContributionBilateral = 0 /]
                  
                  [#list projects as project]
                    [#local pURL][@s.url namespace="/projects" action="${(crpSession)!}/budgetByPartners"][@s.param name='projectID']${project.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
                    <tr>
                      <td class="col-md-6"> <a href="${pURL}" target="_blank">${(project.composedName)!}</a></td>
                      [#if type == "W1W2"]
                        <td class="text-right"> <nobr>US$ ${(project.coreBudget?number?string(",##0.00"))!}</nobr></td> 
                        <td class="text-right"> <nobr> <span class="text-primary"> ${(project.percentageW1)!}% </span> (US$ ${(project.totalW1?number?string(",##0.00"))!})</nobr></td>
                        [#local totalProjectsW1W2 = totalProjectsW1W2 + (project.coreBudget)!0 /]
                        [#local totalContributionW1W2 = totalContributionW1W2 + (project.totalW1)!0 /]
                      [/#if]
                      [#if type == "W3BILATERAL"]
                        <td class="text-right"> <nobr>US$ ${(project.w3Budget?number?string(",##0.00"))!}</nobr></td>
                        <td class="text-right"> <nobr> <span class="text-primary">${(project.percentageW3)!}%</span> (US$ ${(project.totalW3?number?string(",##0.00"))!})</nobr></td>
                        [#local totalProjectsW3 = totalProjectsW3 + (project.w3Budget)!0 /]
                        [#local totalContributionW3 = totalContributionW3 + (project.totalW3)!0 /]
                      [/#if]
                      [#if type == "W3BILATERAL"]
                        <td class="text-right"> <nobr>US$ ${(project.bilateralBudget?number?string(",##0.00"))!}</nobr></td>
                        <td class="text-right"> <nobr> <span class="text-primary">${(project.percentageBilateral)!}%</span>  (US$ ${(project.totalBilateral?number?string(",##0.00"))!})</nobr></td>
                        [#local totalProjectsBilateral = totalProjectsBilateral + (project.bilateralBudget)!0 /]
                        [#local totalContributionBilateral = totalContributionBilateral + (project.totalBilateral)!0 /]
                      [/#if]
                    </tr>
                  [/#list]
                </tbody>
                <tfoot>
                  <tr>
                    <td class="col-md-6"> <strong>Total</strong> </td>
                    [#if type == "W1W2"]
                      [#local percentageContributionW1W2 = (totalContributionW1W2/ totalProjectsW1W2) * 100 ]
                      <td class="text-right"> <nobr>US$ ${(totalProjectsW1W2?number?string(",##0.00"))!}</nobr></td>
                      <td class="text-right"> <nobr> <span class="text-primary">${percentageContributionW1W2?number?string(",##0.00")}%</span> (US$ ${(totalContributionW1W2?number?string(",##0.00"))!}) </nobr></td>
                    [/#if]
                    [#if type == "W3BILATERAL"]
                      [#local percentageContributionW3 = (totalContributionW3/ totalProjectsW3) * 100 ]
                      <td class="text-right"> <nobr>US$ ${(totalProjectsW3?number?string(",##0.00"))!}</nobr></td>
                      <td class="text-right"> <nobr> <span class="text-primary">${percentageContributionW3?number?string(",##0.00")}%</span> (US$ ${(totalContributionW3?number?string(",##0.00"))!}) </nobr> </td>
                    [/#if]
                    [#if type == "W3BILATERAL"]
                      [#local percentageContributionBilateral = (totalContributionBilateral/ totalProjectsBilateral) * 100 ]
                      <td class="text-right"> <nobr>US$ ${(totalProjectsBilateral?number?string(",##0.00"))!} </nobr> </td>
                      <td class="text-right"> <nobr> <span class="text-primary"> ${percentageContributionBilateral?number?string(",##0.00")}% </span> (US$ ${(totalContributionBilateral?number?string(",##0.00"))!}) </nobr> </td>
                    [/#if]
                  </tr>
                </tfoot>
              </table>
              <div class="text-right">
                [#if type == "W3BILATERAL"]
                  <strong>Total W3/Bilateral : US$ <span >${element.w3?number?string(",##0.00")}</span></strong>
                [/#if]
                [#if type == "W1W2"]
                  <strong>Total W1W2 : US$ <span >${element.w1?number?string(",##0.00")}</span></strong>
                [/#if]
              </div>
            </div>
            
          </div>
          <div class="modal-footer"><button type="button" class="btn btn-default" data-dismiss="modal">Close</button></div>
        </div>
      </div>
    </div>
    [/#if]
  [/#if]
[/#macro]