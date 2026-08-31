[#ftl]
[#macro relationsMacro element labelText=true  tag=""]
  [#local className = ((element.class.name)?split('.')?last)!''/]
  [#local composedID = "${className}-${(element.id)!}"]
   [#-- 
  [#local deliverablesProject = (action.getDeliverableRelationsProject(element.id, element.class.name,(element.project.id)!-1))! /]
  [#local deliverablesImpact = (action.getDeliverableRelationsImpact(element.id, element.class.name))! /]
  [#local deliverablesPartner = (action.getDeliverablesLedByPartner(element.id))! /]
  --]
  
  [#if tag == "shfrm"]
    [#local shfrmDeliverables = (action.getShfrmActionDeliverablesRelation(element.id))! /]
    [#local deliverables = (shfrmDeliverables)![] /]
  [#else]

    [#if className == "ProjectPartner"]
      [#local deliverablesPartner = (action.getDeliverablesLedByPartner(element.id))! /]
      [#local deliverables = deliverablesPartner /]
    [#elseif ((className == "ProjectOutcome") && (tag == "")) || (className == "ProjectBudget")]
      [#local deliverablesProject = (action.getDeliverableRelationsProject(element.id, element.class.name,(element.project.id)!-1))! /]
      [#local deliverables = deliverablesProject /]
    [#else]
      [#local deliverablesImpact = (action.getDeliverableRelationsImpact(element.id, element.class.name))! /]
      [#local deliverablesPartner = (action.getDeliverablesLedByPartner(element.id))! /]
      [#local deliverables = ((deliverablesImpact)!deliverablesPartner)! /]
    [/#if]

  [/#if]

  [#local projects = (action.getProjectRelationsImpact(element.id, element.class.name))! /]

  [#-- News buttons --]
  [#if !action.isAiccra()]
    [#local policies = (action.getPolicyContributingByPartner(element.id))![] /]
  [/#if]
  
  [#if tag == "expectedOutcomes"]
    [#local evidencies = (action.getexpectedCrpOutcomes(element.id))![] /]
  [#else]
    [#local evidencies = (action.getStudyContributingByPartner(element.id))![] /]
  [/#if]

  [#if tag == "innovationOutcomes"]
    [#local innovations = (action.getInnovationProjectOutcomes(element.id))![] /]
  [#else]
    [#local innovations = (action.getInnovationContributingByPartner(element.id))![] /]
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
              <table class="table table-striped table-hover deliverableList" width="100%">
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
                 [#if className=="ProjectOutcome"]
                    Deliverables that are contributing to ${(element.project.acronym)!} Outcome Contribution
                 [#else]
                  Deliverables that are contributing to this [@s.text name="global.${className}" /]
                  [/#if]
                [/#if]
                
               

                <br />
                <small>${elementTitle}</small>
              </h4>
            </div>
            <div class="modal-body">
              [#-- Deliverables table --]
              <table class="table table-striped table-hover deliverableList" id="deliverables" width="100%">
                <thead>
                  <tr>
                    <th id="ids">[@s.text name="projectsList.projectids" /]</th>
                    <th id="deliverableTitles" >[@s.text name="project.deliverableList.deliverableName" /]</th>
                                       
                    [#if tag != "shfrm"]
                        <th id="deliverableType">[@s.text name="project.deliverableList.subtype" /]</th>
                        <th id="deliverableType">[@s.text name="project.deliverableList.owner" /]</th>
                        <th id="deliverableType">[@s.text name="project.deliverableList.sharedW" /]</th>
                    [/#if]

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
                        <td>
                        [#if (d.tagTitle?has_content)]
                          ${(d.tagTitle)!}
                        [#else]
                          ${(d.deliverableInfo.title)!'Untitled'}
                        [/#if]
                        </td>                       
                        
                        [#if tag != "shfrm"]
                          <td>${(d.deliverableInfo.deliverableType.name?capitalize)!'-'}</td>
                          <td>${(d.owner)!'-'}</td>
                          <td class="col-md-2"> ${(d.sharedWithProjects)!'-'} </td>
                        [/#if]

                        <td>${(d.deliverableInfo.getStatusName(action.getActualPhase()))!'None'}</td>
                        [#-- Deliverable Year --]
                          <td class="text-center">
                            [#if d.deliverableInfo.year== -1]
                              None
                            [#else]
                              [#if ((d.deliverableInfo.status == 4 || d.deliverableInfo.status==3 || d.deliverableInfo.status==5)!false )
                                      && ((d.deliverableInfo.newExpectedYear != -1)!false)]
                                ${d.deliverableInfo.newExpectedYear} (Extended from 
                                ${(d.deliverableInfo.year)!'None'})
                              [#else]
                                ${(d.deliverableInfo.year)!'None'}
                              [/#if]
                                                            
                            [/#if]
                
                          </td>
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
              <table class="table table-striped table-hover" width="100%">
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
              <table class="table table-striped table-hover innovationList" id="innovation" width="100%">
                <thead>
                  <tr>
                    <th id="ids">[@s.text name="projectsList.projectids" /]</th>
                    <th id="innovationTitles" >[@s.text name="project.innovationList.innovationName" /]</th>
                    <th class="innovationOwner">[@s.text name="project.innovationList.owner" /]</th>
                    <th id="innovationYear">[@s.text name="project.innovationList.year" /]</th>
                    <th></th>
                  </tr>
                </thead>
                <tbody>
                  [#list innovations as i]
                    [#local innovationUrl][@s.url namespace="/projects" action="${(crpSession)!}/innovation"][@s.param name='innovationID']${i.id?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
                    <tr>
                      <th scope="row">${i.id}</th>
                      <td class="col-md-6">${(i.projectInnovationInfo.title)!'Untitled'}</td>
                      <td class="col-md-6">[#if i.project.id == (element.project.id)!1] This Cluster [#else]${(i.project.acronym)!''}[/#if]</td>
                      <td>${(i.projectInnovationInfo.year)}</td>
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
        <span class="icon-20 evidences"></span> <strong>${evidencies?size}</strong> [#if labelText] OICRs [/#if]
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
              <table class="table table-striped table-hover evidencieList" id="evidencies" width="100%">
                <thead>
                  <tr>
                    <th id="ids">[@s.text name="projectsList.projectids" /]</th>
                    <th id="evidencyTitles" >[@s.text name="project.evidenceList.evidenceName" /]</th>
                    <th id="evidencyOwner" >[@s.text name="project.evidenceList.owner" /]</th>
                    <th id="evidencyOwner" >[@s.text name="studiesList.column.status" /]</th>
                    <th id="evidencyYears" >[@s.text name="project.evidenceList.year" /]</th>
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
                      <td class="col-md-6">[#if e.project.id == (element.project.id)!1] This Cluster[#else]${(e.project.acronym)!''}[/#if]</td>
                      <td class="col-md-6">${(e.projectExpectedStudyInfo.statusName)!''}</td>
                      <td class="col-md-6">${(e.projectExpectedStudyInfo.year)!}</td>
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
              <table class="table table-striped table-hover" width="100%">
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

[#-- 
  Activity titles (admin > activities management): informative popup listing the clusters(projects) that are using an
  activity title, with no phase or year restriction, so the admin can see where the activity is reported.
  It only displays information, it does not decide whether the activity title can be deleted.
  One row per logical activity: activities replicate forward, so the phases of a same activity are collapsed into a
  span instead of repeating the row once per phase.
  @param element the ActivityTitle being rendered.
  @param relations the list returned by action.getActivityTitleRelations(element.id).
--]
[#macro activityTitleRelationsMacro element relations labelText=true]
  [#if relations?has_content]
    [#local composedID = "ActivityTitle-${(element.id)!}" /]
    [#local activityTitle = (element.title)!'' /]
    [#-- Distinct clusters, so the button count and the table agree on what is being counted --]
    [#local clusterIds = [] /]
    [#list relations as relation]
      [#if !clusterIds?seq_contains(relation.clusterId)]
        [#local clusterIds = clusterIds + [relation.clusterId] /]
      [/#if]
    [/#list]

    <div id="${composedID}" class="form-group elementRelations ActivityTitle">
      [#-- Button --]
      <button type="button" class="btn btn-default btn-xs" data-toggle="modal" data-target="#modal-clusters-${composedID}">
        <span class="icon-20 project"></span> <strong>${clusterIds?size}</strong> [#if labelText][@s.text name="activityManagement.relations.button" /][/#if]
      </button>

      [#-- Modal --]
      <div class="modal fade" id="modal-clusters-${composedID}" tabindex="-1" role="dialog" aria-labelledby="label-clusters-${composedID}">
        <div class="modal-dialog modal-lg" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
              <h4 class="modal-title" id="label-clusters-${composedID}">
                [@s.text name="activityManagement.relations.title" /]
                <br />
                <small>${activityTitle}</small>
              </h4>
            </div>
            <div class="modal-body">
              <p class="infoText">[@s.text name="activityManagement.relations.help" /]</p>
              [#-- 
                Per column filters. relationsModalDataTables.js reads data-filter-columns and builds one select per
                listed column, labelling it with that column header, so nothing has to be repeated here.
              --]
              [#assign labelAll][@s.text name="activityManagement.relations.filter.all" /][/#assign]
              <div class="relationsModalFilters" data-filter-columns="1,4" data-label-all="${labelAll}"></div>
              [#-- 
                Clusters table. data-order and data-page-length are read natively by DataTables, which merges the
                table data attributes over the shared init options, so the list opens grouped by cluster.
              --]
              <table class="table table-striped table-hover activityClustersList" width="100%"
                data-page-length="10" data-order='[[0, "asc"]]'>
                <thead>
                  <tr>
                    <th>[@s.text name="projectsList.projectids" /]</th>
                    <th>[@s.text name="projectsList.projectTitles" /]</th>
                    <th>[@s.text name="activityManagement.relations.column.activity" /]</th>
                    <th>[@s.text name="activityManagement.relations.column.phases" /]</th>
                    <th>[@s.text name="activityManagement.relations.column.status" /]</th>
                    <th></th>
                  </tr>
                </thead>
                <tbody>
                  [#list relations as relation]
                    [#local clusterUrl][@s.url namespace="/clusters" action="${(crpSession)!}/activities"][@s.param name='projectID']${relation.clusterId?c}[/@s.param][#include "/WEB-INF/global/pages/urlGlobalParams.ftl" /][/@s.url][/#local]
                    [#-- 
                      The activity own title is a copy of the activity title for AICCRA, so showing it would just
                      repeat the modal header. The description is what the cluster actually reported.
                    --]
                    [#local description = (relation.activityDescription)!'' /]
                    [#if description?length > 160]
                      [#local description = description?substring(0, 160) + "..." /]
                    [/#if]
                    [#if relation.reportedInCurrentPhase]
                      [#local statusClass = "is-reported" /]
                      [#assign statusLabel][@s.text name="activityManagement.relations.status.reported" /][/#assign]
                    [#else]
                      [#local statusClass = "is-removed" /]
                      [#assign statusLabel][@s.text name="activityManagement.relations.status.removed" /][/#assign]
                    [/#if]
                    <tr>
                      <th scope="row">C${relation.clusterId?c}</th>
                      [#-- 
                        Filtered column: plain text only, so the select offers one option per cluster.
                      --]
                      <td>[#if (relation.clusterTitle)?has_content]${relation.clusterTitle}[#else][@s.text name="projectsList.title.none" /][/#if]</td>
                      <td>
                        [#if (relation.composedId)?has_content]<strong>${relation.composedId}</strong>[/#if]
                        [#if description?has_content]<br /><span class="activityDescriptionText">${description}</span>[/#if]
                      </td>
                      [#-- 
                        Every phase the activity lives in, listed explicitly so gaps are visible. Not a filtered
                        column: each combination would be its own filter option. Long lists are trimmed and the
                        full list stays in the tooltip.
                      --]
                      [#local shownPhases = relation.phaseLabels /]
                      [#local hiddenPhases = 0 /]
                      [#if shownPhases?size > 4]
                        [#local hiddenPhases = shownPhases?size - 4 /]
                        [#local shownPhases = shownPhases[0..3] /]
                      [/#if]
                      <td title="${relation.phaseLabels?join(', ')}">
                        ${shownPhases?join(', ')}[#if hiddenPhases > 0] <span class="phasesMore">+${hiddenPhases} [@s.text name="activityManagement.relations.phasesMore" /]</span>[/#if]
                      </td>
                      [#-- 
                        Plain text on purpose: this column is filtered, and DataTables filters against the raw cell
                        content, so any markup here would break an exact match.
                      --]
                      <td class="relationsStatus ${statusClass}">${statusLabel}</td>
                      <td><a href="${clusterUrl}" target="_blank"><span class="glyphicon glyphicon-new-window"></span></a></td>
                    </tr>
                  [/#list]
                </tbody>
              </table>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-default" data-dismiss="modal">[@s.text name="form.buttons.close" /]</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  [/#if]
[/#macro]
