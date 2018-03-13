[#ftl]
[#macro projectBudgetsByFlagshipMacro element type totalValue=0 tiny=false popupEnabled=true isAreaPMU=false]
  
  [#if !popupEnabled]
    <nobr>US$ <span >${((totalValue)!0)?number?string(",##0.00")}</span></nobr>
  [#else]
    [#if isAreaPMU]
      [#local projects = (action.loadPMUProjects())![] ]
    [#else]
      [#local projects = (action.loadFlagShipBudgetInfoProgram(element.id))![] ]
    [/#if]
    [#if projects?size > 0]
    <a class=" btn btn-default btn-xs" data-toggle="modal" style="border-color: #00BCD4;color: #057584;" data-target="#projectBudgets-${type}-${(element.id)!}">
       [#-- <span class="glyphicon glyphicon-fullscreen" style="color:#b3b3b3"></span>  --]
       <span >${((totalValue)!0)?number?string(",##0.00")}</span>
    </a>
    
    <!-- Modal -->
    <div class="modal fade" id="projectBudgets-${type}-${(element.id)!}" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
      <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
            <h4 class="modal-title" id="myModalLabel">
              [#if type == "W1W2"]
                [@s.text name="expectedProgress.projectBudgetsW1w2" /]
              [#elseif type == "W3BILATERAL"]
                [@s.text name="expectedProgress.projectBudgetsW3Bilateral" /]
              [#elseif type == "CENTERFUNDS"]
                [@s.text name="expectedProgress.projectBudgetsCenterFunds" /]
              [/#if]
            </h4>
            <span class="programTag" style="border-color:${(element.color)!'#fff'}">${(element.composedName)!}</span> 
          </div>
          <div class="modal-body">
            <div class="">
              <table class="table table-bordered">
                <thead>
                  <tr>
                    <th rowspan="2">[@s.text name="project.title" /]</th>
                    [#if type == "W1W2"] <th colspan="2" class="text-center">[@s.text name="project.coreBudget" /]</th>[/#if]
                    [#if type == "W3BILATERAL"] <th colspan="2" class="text-center">[@s.text name="project.w3Budget" /]</th>[/#if]
                    [#if type == "W3BILATERAL"] <th colspan="2" class="text-center">[@s.text name="project.bilateralBudget" /]</th>[/#if]
                    [#if type == "CENTERFUNDS"] <th colspan="2" class="text-center">[@s.text name="project.centerFundsBudget" /]</th>[/#if]
                    
                  </tr>
                  <tr>
                    [#if type == "W1W2"]
                      <th class="text-center"> Total project [@s.text name="project.coreBudget" /] Amount</th> 
                      <th class="text-center">  ${(element.acronym)!} % Contribution and amount </th>
                    [/#if]
                    [#if type == "W3BILATERAL"]
                      <th class="text-center"> Total project [@s.text name="project.w3Budget" /] Amount</th>
                      <th class="text-center"> ${(element.acronym)!} % Contribution and amount</th>
                    [/#if]
                    [#if type == "W3BILATERAL"]
                      <th class="text-center"> Total project [@s.text name="project.bilateralBudget" /] Amount</th>
                      <th class="text-center"> ${(element.acronym)!} % Contribution and amount</th>
                    [/#if]
                    [#if type == "CENTERFUNDS"]
                      <th class="text-center"> Total project [@s.text name="project.centerFundsBudget" /] Amount</th>
                      <th class="text-center"> ${(element.acronym)!} % Contribution and amount</th>
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
                  
                  [#local totalProjectsCenterFunds = 0 /]
                  [#local totalContributionCenterFunds = 0 /]
     
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
                      [#if type == "CENTERFUNDS"]
                        <td class="text-right"> <nobr>US$ ${(project.centenFundsBudget?number?string(",##0.00"))!}</nobr></td>
                        <td class="text-right"> <nobr> <span class="text-primary">${(project.percentageFundsBudget)!}%</span>  (US$ ${(project.totalCenterFunds?number?string(",##0.00"))!})</nobr></td>
                        [#local totalProjectsCenterFunds = totalProjectsCenterFunds + (project.centenFundsBudget)!0 /]
                        [#local totalContributionCenterFunds = totalContributionCenterFunds + (project.totalCenterFunds)!0 /]
                      [/#if]
                    </tr>
                  [/#list]
                </tbody>
                <tfoot>
                  <tr>
                    <td class="col-md-6"> <strong>Total</strong> </td>
                    [#if type == "W1W2"]
                      [#if totalProjectsW1W2 != 0]
                        [#local percentageContributionW1W2 = (totalContributionW1W2/ totalProjectsW1W2) * 100 ]
                      [/#if]
                      <td class="text-right"> <nobr>US$ ${(totalProjectsW1W2?number?string(",##0.00"))!}</nobr></td>
                      <td class="text-right"> <nobr> <span class="text-primary">${((percentageContributionW1W2)!0)?number?string(",##0.00")}%</span> (US$ ${(totalContributionW1W2?number?string(",##0.00"))!}) </nobr></td>
                    [/#if]
                    [#if type == "W3BILATERAL"]
                      [#if totalProjectsW3 != 0]
                        [#local percentageContributionW3 = (totalContributionW3/ totalProjectsW3) * 100 ]
                      [/#if]
                      <td class="text-right"> <nobr>US$ ${(totalProjectsW3?number?string(",##0.00"))!}</nobr></td>
                      <td class="text-right"> <nobr> <span class="text-primary">${((percentageContributionW3)!0)?number?string(",##0.00")}%</span> (US$ ${(totalContributionW3?number?string(",##0.00"))!}) </nobr> </td>
                    [/#if]
                    [#if type == "W3BILATERAL"]
                      [#if totalProjectsBilateral != 0]
                        [#local percentageContributionBilateral = (totalContributionBilateral/ totalProjectsBilateral) * 100 ]
                      [/#if]
                      <td class="text-right"> <nobr>US$ ${(totalProjectsBilateral?number?string(",##0.00"))!} </nobr> </td>
                      <td class="text-right"> <nobr> <span class="text-primary"> ${((percentageContributionBilateral)!0)?number?string(",##0.00")}% </span> (US$ ${(totalContributionBilateral?number?string(",##0.00"))!}) </nobr> </td>
                    [/#if]
                    [#if type == "CENTERFUNDS"]
                      [#if totalProjectsCenterFunds != 0]
                        [#local percentageContributionBilateral = (totalContributionCenterFunds/ totalProjectsCenterFunds) * 100 ]
                      [/#if]
                      <td class="text-right"> <nobr>US$ ${(totalProjectsCenterFunds?number?string(",##0.00"))!} </nobr> </td>
                      <td class="text-right"> <nobr> <span class="text-primary"> ${((percentageContributionBilateral)!0)?number?string(",##0.00")}% </span> (US$ ${(totalContributionCenterFunds?number?string(",##0.00"))!}) </nobr> </td>
                    [/#if]
                  </tr>
                </tfoot>
              </table>
              <div class="text-right">
                
                <strong>
                  [#if type == "W1W2"]Total [@s.text name="project.coreBudget" /][/#if]
                  [#if type == "W3BILATERAL"]Total [@s.text name="project.w3Budget" /]/[@s.text name="project.bilateralBudget" /] [/#if]
                  [#if type == "CENTERFUNDS"]Total [@s.text name="project.centerFundsBudget" /][/#if]
                  : US$ <span >${((totalValue)!0)?number?string(",##0.00")}</span>
                </strong>
              </div>
            </div>
            
          </div>
          <div class="modal-footer"><button type="button" class="btn btn-default" data-dismiss="modal">Close</button></div>
        </div>
      </div>
    </div>
    [#else]
      No Projects
    [/#if]
  [/#if]
[/#macro]