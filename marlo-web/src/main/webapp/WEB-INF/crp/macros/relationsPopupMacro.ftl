[#ftl]
[#macro relationsMacro element labelText=true  tag=""]
  [#local className = ((element.class.name)?split('.')?last)!''/]
  [#local composedID = "${className}-${(element.id)!}"]
  [#local deliverablesProject = (action.getDeliverableRelationsProject(element.id, element.class.name,(element.project.id)!-1))! /]
  [#local deliverablesImpact = (action.getDeliverableRelationsImpact(element.id, element.class.name))! /]
  [#local deliverablesPartner = (action.getDeliverablesLedByPartner(element.id))! /]

  [#if className == "ProjectPartner"]
    [#local deliverables = deliverablesPartner /]
  [#elseif ((className == "ProjectOutcome") && (tag == "")) || (className == "ProjectBudget")]
    [#local deliverables = deliverablesProject /]
  [#else]
    [#local deliverables = ((deliverablesImpact)!deliverablesPartner)! /]
  [/#if]
  [#local projects = (action.getProjectRelationsImpact(element.id, element.class.name))! /]

  [#-- News buttons --]
  [#local policies = (action.getPolicyContributingByPartner(element.id))![] /]
  [#local innovations = (action.getInnovationContributingByPartner(element.id))![] /]
  [#local evidencies = (action.getStudyContributingByPartner(element.id))![] /]
  
  [#if tag == "expectedOutcomes"]
    [#local evidencies = (action.getexpectedProjectOutcomes(element.id))![] /]
  [/#if]
  [#if tag == "innovationOutcomes"]
    [#local innovations = (action.getInnovationProjectOutcomes(element.id))![] /]
  [/#if]
  [#-- News buttons --]

  [#local elementTitle = (element.keyOutput)!((element.title)!((element.description)!'')) /]
  [#if (deliverables?has_content) ||  (projects?has_content) || (policies?has_content) || (innovations?has_content) || (evidencies?has_content)]
  <div id="${composedID}" class="form-group elementRelations ${className}">
    [#if projects?has_content]
      [#-- Button --]
      <button type="button" class="btn btn-default btn-xs" data-toggle="modal" data-target="#modal-projects-${composedID}">
        <span class="icon-20 project"></span> <strong>${projects?size}</strong> [#if labelText][@s.text name="global.Project" /](s)[/#if]
      </button>

      [#-- Modal --]
      <div class="modal fade" id="modal-projects-${composedID}" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
        <div class="modal-dialog modal-lg" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
              <h4 class="modal-title" id="myModalLabel">

                [@s.text name="global.projects" /] that are contributing to this [@s.text name="global.${className}" /]
                <br />
                <small>${elementTitle}</small>
              </h4>
            </div>
            <div class="modal-body">
              [#-- Projects table --]
              <table class="table">
                <thead>
                  <tr>
                    <th id="ids">[@s.text name="projectsList.projectids" /]</th>
                    <th id="projectTitles" >[@s.text name="projectsList.projectTitles" /]</th>
                    <th id="projectLeader" >[@s.text name="projectsList.projectLeader" /]</th>
                    <th></th>
                  </tr>
                </thead>
                <tbody>
                  [#list projects as p]
                    [#if action.isAiccra()]
                    [#--
                      [#local projectUrl][@s.url namespace="/projects" action="${(crpSession)!}/description"][@s.param name='projectID']${p.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
                      --]
                      [#local existRelation = (action.hasProjectOutcomeRelationImpact(action.getActualPhase().id, p.id, element.id))!/]

                      [#if existRelation]
                        [#local projectOutcome = (action.getProjectOutcomeRelationImpact(action.getActualPhase().id, p.id, element.id))!/]
                        [#local projectUrl][@s.url namespace="/clusters" action="${(crpSession)!}/contributionCrp"][@s.param name='projectOutcomeID']${projectOutcome.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
                      [#else]
                        [#local projectUrl][@s.url namespace="/clusters" action="${(crpSession)!}/description"][@s.param name='projectID']${p.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
                      [/#if]
                    [#--
                      [#local projectOutcome = (action.getProjectOutcomeRelationImpact(action.getActualPhase().id, p.id, element.id))!/]
                      [#local projectUrl][@s.url namespace="/projects" action="${(crpSession)!}/contributionCrp"][@s.param name='projectOutcomeID']${projectOutcome.id}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
                    --]
                    [#else]
                      [#local projectUrl][@s.url namespace="/clusters" action="${(crpSession)!}/description"][@s.param name='projectID']${p.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
                    [/#if]
                    <tr>
                      <th scope="row">C${p.id}</th>
                      <td>${(p.projectInfo.title)!'Untitled'}</td>
                      <td class="">[#if p.getLeader(action.getActualPhase())?has_content]${(p.getLeader(action.getActualPhase()).institution.acronym)!p.getLeader(action.getActualPhase()).institution.name}[#else][@s.text name="projectsList.title.none" /][/#if]</td>
                      <td> <a href="${projectUrl}" target="_blank"><span class="glyphicon glyphicon-new-window"></span></a>  </td>
                    </tr>
                  [/#list]
                </tbody>
              </table>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
          </div>
        </div>
      </div>
    [/#if]

    [#if deliverables?has_content]
      [#-- Button --]
      <button type="button" class="btn btn-default btn-xs" data-toggle="modal" data-target="#modal-deliverables-${composedID}">
        <span class="icon-20 deliverable"></span> <strong>${deliverables?size}</strong> [#if labelText] Deliverable(s)[/#if]
      </button>

      [#-- Modal --]
      <div class="modal fade" id="modal-deliverables-${composedID}" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
        <div class="modal-dialog modal-lg" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
              <h4 class="modal-title" id="myModalLabel">

                 [#if className=="ProjectBudget"]
                Deliverables funded by this funding source in this [@s.text name="global.Project" /]
                [#else]
                  Deliverables that are contributing to this [@s.text name="global.${className}" /]
                [/#if]

                <br />
                <small>${elementTitle}</small>
              </h4>
            </div>
            <div class="modal-body">
              [#-- Deliverables table --]
              <table class="table">
                <thead>
                  <tr>
                    <th id="ids">[@s.text name="projectsList.projectids" /]</th>
                    <th id="deliverableTitles" >[@s.text name="project.deliverableList.deliverableName" /]</th>
                    <th id="deliverableType">[@s.text name="project.deliverableList.subtype" /]</th>
                    <th id="deliverableType">[@s.text name="project.deliverableList.status" /]</th>
                    <th id="deliverableYear">[@s.text name="project.deliverableList.year" /]</th>
                    <th></th>
                  </tr>
                </thead>
                <tbody>
                  [#list deliverables as d]
                    [#if action.isAiccra()]
                      [#local deliverableUrl][@s.url namespace="/clusters" action="${(crpSession)!}/deliverable"][@s.param name='deliverableID']${d.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
                    [#else]
                      [#local deliverableUrl][@s.url namespace="/projects" action="${(crpSession)!}/deliverable"][@s.param name='deliverableID']${d.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
                    [/#if]
                      <tr>
                        <th scope="row">D${d.id}</th>
                        <td class="col-md-6">
                        [#if (d.tagTitle?has_content)]${(d.tagTitle)!}[/#if]
                        ${(d.deliverableInfo.title)!'Untitled'}</td>
                        <td>${(d.deliverableInfo.deliverableType.name?capitalize)!'-'}</td>
                        <td>${(d.deliverableInfo.getStatusName(action.getActualPhase()))!'None'}</td>
                        <td>${(d.deliverableInfo.year)!'none'}</td>
                        <td> <a href="${deliverableUrl}" target="_blank"><span class="glyphicon glyphicon-new-window"></span></a>  </td>
                      </tr>
                    [/#list]
                </tbody>
              </table>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
          </div>
        </div>
      </div>
    [/#if]

    [#-- policies --]
    [#if policies?has_content]
      [#-- Button --]
      <button type="button" class="btn btn-default btn-xs" data-toggle="modal" data-target="#modal-policies-${composedID}">
        <span class="icon-20 policies"></span> <strong>${policies?size}</strong> [#if labelText] Policy(ies)[/#if]
      </button>

      [#-- Modal --]
      <div class="modal fade" id="modal-policies-${composedID}" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
        <div class="modal-dialog modal-lg" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
              <h4 class="modal-title" id="myModalLabel">

                 [#if className=="ProjectBudget"]
                Policies funded by this funding source in this [@s.text name="global.Project" /]
                [#else]
                  Policies that are contributing to this [@s.text name="global.${className}" /]
                [/#if]

                <br />
                <small>${elementTitle}</small>
              </h4>
            </div>
            <div class="modal-body">
              [#-- Policies table --]
              <table class="table">
                <thead>
                  <tr>
                    <th id="ids">[@s.text name="projectsList.projectids" /]</th>
                    <th id="policyTitles" >[@s.text name="project.projectPolicyList.policyName" /]</th>
                   [#--<th id="policyType">[@s.text name="project.projectPolicyList.type" /]</th>--]
                    <th></th>
                  </tr>
                </thead>
                <tbody>
                  [#list policies as p]
                    [#local policyUrl][@s.url namespace="/projects" action="${(crpSession)!}/policy"][@s.param name='policyID']${p.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
                    <tr>
                      <th scope="row">${p.id}</th>
                      <td class="col-md-6">${(p.projectPolicyInfo.title)!'Untitled'}</td>
                       [#--<td>${(p.projectPolicyInfo.policyType.name?capitalize)!'none'}</td>--]
                      <td> <a href="${policyUrl}" target="_blank"><span class="glyphicon glyphicon-new-window"></span></a>  </td>
                    </tr>
                    [/#list]
                </tbody>
              </table>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
          </div>
        </div>
      </div>
    [/#if]

    [#-- innovations --]
    [#if innovations?has_content]
      [#-- Button --]
      <button type="button" class="btn btn-default btn-xs" data-toggle="modal" data-target="#modal-innovations-${composedID}">
        <span class="icon-20 innovations"></span> <strong>${innovations?size}</strong> [#if labelText] Innovation(s)[/#if]
      </button>

      [#-- Modal --]
      <div class="modal fade" id="modal-innovations-${composedID}" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
        <div class="modal-dialog modal-lg" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
              <h4 class="modal-title" id="myModalLabel">

                 [#if className=="ProjectBudget"]
                Innovations funded by this funding source in this [@s.text name="global.Project" /]
                [#else]
                  Innovations that are contributing to this [@s.text name="global.${className}" /]
                [/#if]

                <br />
                <small>${elementTitle}</small>
              </h4>
            </div>
            <div class="modal-body">
              [#-- innovations table --]
              <table class="table">
                <thead>
                  <tr>
                    <th id="ids">[@s.text name="projectsList.projectids" /]</th>
                    <th id="innovationTitles" >[@s.text name="project.innovationList.innovationName" /]</th>
                    [#--<th id="innovationType">[@s.text name="project.innovationList.type" /]</th>--]
                    [#--<th id="innovationRole" >[@s.text name="project.innovationList.role" /]</th>--]
                    <th></th>
                  </tr>
                </thead>
                <tbody>
                  [#list innovations as i]
                    [#local innovationUrl][@s.url namespace="/projects" action="${(crpSession)!}/innovation"][@s.param name='innovationID']${i.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
                    <tr>
                      <th scope="row">${i.id}</th>
                      <td class="col-md-6">${(i.projectInnovationInfo.title)!'Untitled'}</td>
                      [#--<td>${(i.innovationInfo.innovationType.name?capitalize)!'none'}</td>
                      <td class="col-md-6">${(i.projectInnovationInfo.title)!'Untitled'}</td>--]
                      <td> <a href="${innovationUrl}" target="_blank"><span class="glyphicon glyphicon-new-window"></span></a>  </td>
                    </tr>
                    [/#list]
                </tbody>
              </table>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
          </div>
        </div>
      </div>
    [/#if]

    [#-- Evidencies --]
    [#if evidencies?has_content]
      [#-- Button --]
      <button type="button" class="btn btn-default btn-xs" data-toggle="modal" data-target="#modal-evidencies-${composedID}">
        <span class="icon-20 evidences"></span> <strong>${evidencies?size}</strong> [#if labelText] OICRs / MELIAs[/#if]
      </button>

      [#-- Modal --]
      <div class="modal fade" id="modal-evidencies-${composedID}" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
        <div class="modal-dialog modal-lg" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
              <h4 class="modal-title" id="myModalLabel">

                 [#if className=="ProjectBudget"]
                Evidencies funded by this funding source in this [@s.text name="global.Project" /]
                [#else]
                  Evidencies that are contributing to this [@s.text name="global.${className}" /]
                [/#if]

                <br />
                <small>${elementTitle}</small>
              </h4>
            </div>
            <div class="modal-body">
              [#-- Evidencies table --]
              <table class="table">
                <thead>
                  <tr>
                    <th id="ids">[@s.text name="projectsList.projectids" /]</th>
                    <th id="evidencyTitles" >[@s.text name="project.evidenceList.evidenceName" /]</th>
                    [#--<th id="evidencyType">[@s.text name="project.evidenceList.type" /]</th>--]
                    <th></th>
                  </tr>
                </thead>
                <tbody>
                  [#list evidencies as e]
                    [#local evidenceUrl][@s.url namespace="/projects" action="${(crpSession)!}/study"][@s.param name='expectedID']${e.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
                    <tr>
                      <th scope="row">${e.id}</th>
                      <td class="col-md-6">${(e.projectExpectedStudyInfo.title)!'Untitled'}</td>
                      [#--<td>${(e.studyInfo.studyType.name?capitalize)!'none'}</td>--]
                      <td> <a href="${evidenceUrl}" target="_blank"><span class="glyphicon glyphicon-new-window"></span></a>  </td>
                    </tr>
                    [/#list]
                </tbody>
              </table>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
          </div>
        </div>
      </div>
    [/#if]

  </div>
  [/#if]
[/#macro]

[#macro deliverablesMissingActivities deliverables labelText=true ]
    <button type="button" class="btn btn-default btn-xs" data-toggle="modal" data-target="#modal-project-deliverables">
      <span class="icon-20 project"></span> <strong>${deliverables?size}</strong> [#if labelText][@s.text name="deliverable(s) without activity" /][/#if]
    </button>

    <div class="form-group elementRelations">
    <div class="modal fade" id="modal-project-deliverables" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
      <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
            <h4 class="modal-title" id="myModalLabel">
              These deliverables are not linked to any activity
              <br />
            </h4>
          </div>
            <div class="modal-body">
              [#-- Evidencies table --]
              <table class="table">
                <thead>
                  <tr>
                    <th id="ids">[@s.text name="projectsList.projectids" /]</th>
                    <th id="delverableTitles" >[@s.text name="project.deliverableList.deliverableName" /]</th>
                    <th id="delverableTitles" >[@s.text name="project.deliverableList.year" /]</th>
                    <th></th>
                  </tr>
                </thead>
                <tbody>
                  [#list deliverables as d]
                    [#local deliverableUrl][@s.url namespace="/clusters" action="${(crpSession)!}/deliverable"][@s.param name='deliverableID']${d.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
                    <tr>
                      <th class="deliverableId" scope="row">D${d.id}</th>
                      <td class="col-md-12">${(d.deliverableInfo.title)!'Untitled'}</td>
                      <td class="col-md-12">${(d.deliverableInfo.year)!'-'}</td>
                      [#--<td>${(e.studyInfo.studyType.name?capitalize)!'none'}</td>--]
                      <td> <a href="${deliverableUrl}" target="_blank"><span class="glyphicon glyphicon-new-window"></span></a>  </td>
                    </tr>
                    [/#list]
                </tbody>
              </table>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
      </div>
    </div>

  </div>
[/#macro]
