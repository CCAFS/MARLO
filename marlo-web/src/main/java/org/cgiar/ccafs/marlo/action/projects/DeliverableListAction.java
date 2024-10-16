/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning &
 * Outcomes Platform (MARLO).
 * MARLO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * MARLO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with MARLO. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.action.projects;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.action.deliverable.dto.DeliverableSearchSummary;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.DeliverableInfoManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableTypeManager;
import org.cgiar.ccafs.marlo.data.manager.FeedbackQACommentManager;
import org.cgiar.ccafs.marlo.data.manager.FeedbackQACommentableFieldsManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectDeliverableSharedManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableDissemination;
import org.cgiar.ccafs.marlo.data.model.DeliverableFundingSource;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.DeliverableMetadataElement;
import org.cgiar.ccafs.marlo.data.model.DeliverableType;
import org.cgiar.ccafs.marlo.data.model.DeliverableUserPartnership;
import org.cgiar.ccafs.marlo.data.model.DeliverableUserPartnershipPerson;
import org.cgiar.ccafs.marlo.data.model.FeedbackQAComment;
import org.cgiar.ccafs.marlo.data.model.FeedbackQACommentableFields;
import org.cgiar.ccafs.marlo.data.model.FeedbackStatusEnum;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectDeliverableShared;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class DeliverableListAction extends BaseAction {


  private static final long serialVersionUID = -823169163612346982L;
  // Logger
  private final Logger logger = LoggerFactory.getLogger(DeliverableListAction.class);

  // Managers
  private GlobalUnitManager crpManager;
  private DeliverableManager deliverableManager;
  private PhaseManager phaseManager;
  private DeliverableTypeManager deliverableTypeManager;
  private DeliverableInfoManager deliverableInfoManager;
  private ProjectManager projectManager;
  private SectionStatusManager sectionStatusManager;
  private ProjectDeliverableSharedManager projectDeliverableSharedManager;
  private FeedbackQACommentableFieldsManager feedbackQACommentableFieldsManager;
  private FeedbackQACommentManager commentManager;

  // Front-end
  private List<Integer> allYears;
  private long deliverableID;
  private List<Deliverable> deliverables;
  private List<Deliverable> currentDeliverableList;
  private List<Deliverable> previousSharedDeliverableList;
  private List<DeliverableType> deliverablesType;
  private GlobalUnit loggedCrp;
  private Project project;
  private long projectID;


  @Inject
  public DeliverableListAction(APConfig config, ProjectManager projectManager, GlobalUnitManager crpManager,
    DeliverableTypeManager deliverableTypeManager, DeliverableManager deliverableManager, PhaseManager phaseManager,
    DeliverableInfoManager deliverableInfoManager, SectionStatusManager sectionStatusManager,
    ProjectDeliverableSharedManager projectDeliverableSharedManager,
    FeedbackQACommentableFieldsManager feedbackQACommentableFieldsManager, FeedbackQACommentManager commentManager) {
    super(config);
    this.projectManager = projectManager;
    this.sectionStatusManager = sectionStatusManager;
    this.crpManager = crpManager;
    this.deliverableInfoManager = deliverableInfoManager;
    this.deliverableTypeManager = deliverableTypeManager;
    this.deliverableManager = deliverableManager;
    this.phaseManager = phaseManager;
    this.projectDeliverableSharedManager = projectDeliverableSharedManager;
    this.feedbackQACommentableFieldsManager = feedbackQACommentableFieldsManager;
    this.commentManager = commentManager;
  }

  @Override
  public String add() {

    Deliverable deliverable = new Deliverable();

    deliverable.setProject(project);
    deliverable.setCreateDate(new Date());
    deliverable.setPhase(this.getActualPhase());
    deliverableID = deliverableManager.saveDeliverable(deliverable).getId();


    Phase phase = this.getActualPhase();
    phase = phaseManager.getPhaseById(phase.getId());
    DeliverableInfo deliverableInfo = new DeliverableInfo();
    deliverableInfo.setDeliverable(deliverable);
    deliverableInfo.setPhase(phase);
    deliverableInfo.setYear(this.getCurrentCycleYear());
    deliverableInfo.setStatus(Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId()));
    deliverableInfo.setModificationJustification("New expected deliverable created");
    deliverableInfoManager.saveDeliverableInfo(deliverableInfo);

    if (deliverableID > 0) {
      return SUCCESS;
    }

    return INPUT;
  }


  public void addDeliverablePhase(Phase phase, Deliverable deliverable) {
    phase = phaseManager.getPhaseById(phase.getId());
    DeliverableInfo deliverableInfo = new DeliverableInfo();
    deliverableInfo.setDeliverable(deliverable);
    deliverableInfo.setPhase(phase);
    deliverableInfo.setYear(this.getCurrentCycleYear());
    deliverableInfo.setStatus(Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId()));
    deliverableInfo.setModificationJustification("New expected deliverable created");
    deliverableInfoManager.saveDeliverableInfo(deliverableInfo);
    if (phase.getNext() != null) {
      this.addDeliverablePhase(phase.getNext(), deliverable);
    }
  }


  public boolean canEdit(long deliverableID) {
    Deliverable deliverable = deliverableManager.getDeliverableById(deliverableID);
    if (this.isPlanningActive() && !this.isUpKeepActive()) {
      if (deliverable.getDeliverableInfo(this.getActualPhase()).getYear() >= this.getCurrentCycleYear()) {
        return true;
      }
      return false;
    }
    return true;
  }

  @Override
  public String delete() {

    Map<String, Parameter> parameters = this.getParameters();
    deliverableID = Long
      .parseLong(StringUtils.trim(parameters.get(APConstants.PROJECT_DELIVERABLE_REQUEST_ID).getMultipleValues()[0]));

    Deliverable deliverable = deliverableManager.getDeliverableById(deliverableID);
    if (deliverable != null) {
      if (deliverable.getSectionStatuses() != null) {
        for (SectionStatus sectionStatus : deliverable.getSectionStatuses()) {
          sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
        }
      }

      projectID = deliverable.getProject().getId();


      deliverableManager.saveDeliverable(deliverable);
      deliverableManager.deleteDeliverable(deliverableID);
      this.addActionMessage("message:" + this.getText("deleting.success"));
    }
    return SUCCESS;
  }

  @Override
  public List<Integer> getAllYears() {
    return allYears;
  }


  public void getCommentStatuses() {

    try {

      List<String> commentList = null;
      commentList = deliverableManager.getCommentStatusByPhase(this.getActualPhase().getId());

      List<String> commentAnsweredList = null;
      commentAnsweredList = deliverableManager.getAnsweredCommentByPhase(this.getActualPhase().getId());


      for (Deliverable deliverable : project.getDeliverables()) {
        int answeredComments = 0;
        int totalComments = 0;
        try {

          for (String string : commentList) {
            String test = string.replace("|", ";");

            if (test.split(";")[0].equals(deliverable.getId() + "")) {
              totalComments = Integer.parseInt(test.split(";")[1]);
            }
          }

          for (String string : commentAnsweredList) {
            String test = string.replace("|", ";");

            if (test.split(";")[0].equals(deliverable.getId() + "")) {
              answeredComments = Integer.parseInt(test.split(";")[1]);
            }
          }

          deliverable.setCommentStatus(answeredComments + "/" + totalComments);
          if (deliverable.getCommentStatus() == null
            || (deliverable.getCommentStatus() != null && deliverable.getCommentStatus().isEmpty())) {
            deliverable.setCommentStatus(0 + "/" + 0);
          }

        } catch (Exception e) {
          deliverable.setCommentStatus(0 + "/" + 0);
        }
      }

    } catch (Exception e) {
      logger.error("unable to get feedbackcomments info", e);
      e.printStackTrace();
    }
  }


  public void getCommentStatusesOld() {

    try {
      List<FeedbackQACommentableFields> commentableFields = new ArrayList<>();

      // get the commentable fields by sectionName
      if (feedbackQACommentableFieldsManager.findAll() != null) {
        commentableFields = feedbackQACommentableFieldsManager.findAll().stream()
          .filter(f -> f != null && f.getSectionName().equals("deliverable")).collect(Collectors.toList());
      }
      if (project.getDeliverables() != null && !project.getDeliverables().isEmpty() && commentableFields != null
        && !commentableFields.isEmpty()) {


        // Set the comment status in each project outcome

        for (Deliverable deliverable : project.getDeliverables()) {
          int answeredComments = 0;
          int totalComments = 0;
          try {

            for (FeedbackQACommentableFields commentableField : commentableFields) {
              if (commentableField != null && commentableField.getId() != null) {

                if (deliverable != null && deliverable.getId() != null && commentableField != null
                  && commentableField.getId() != null) {
                  List<FeedbackQAComment> comments = commentManager.findAll().stream()
                    .filter(f -> f != null && f.getPhase() != null && f.getPhase().getId() != null
                      && f.getPhase().getId().equals(this.getActualPhase().getId())
                      && f.getParentId() == deliverable.getId()

                      && (f.getFeedbackStatus() != null && f.getFeedbackStatus().getId() != null && (!f
                        .getFeedbackStatus().getId().equals(Long.parseLong(FeedbackStatusEnum.Dismissed.getStatusId()))
                      // &&
                      // !f.getFeedbackStatus().getId().equals(Long.parseLong(FeedbackStatusEnum.Draft.getStatusId()))
                      ))

                      && f.getField() != null && f.getField().getId().equals(commentableField.getId()))
                    .collect(Collectors.toList());
                  if (comments != null && !comments.isEmpty()) {
                    totalComments += comments.size();
                    comments = comments.stream()
                      .filter(f -> f != null && f.getPhase() != null && f.getPhase().getId() != null
                        && f.getPhase().getId().equals(this.getActualPhase().getId())
                        && ((f.getFeedbackStatus() != null && f.getFeedbackStatus().getId()
                          .equals(Long.parseLong(FeedbackStatusEnum.Agreed.getStatusId())))
                          || (f.getFeedbackStatus() != null && f.getReply() != null)))
                      .collect(Collectors.toList());
                    if (comments != null) {
                      answeredComments += comments.size();
                    }
                  }
                }
              }
            }
            deliverable.setCommentStatus(answeredComments + "/" + totalComments);

            if (deliverable.getCommentStatus() == null
              || (deliverable.getCommentStatus() != null && deliverable.getCommentStatus().isEmpty())) {
              deliverable.setCommentStatus(0 + "/" + 0);
            }
          } catch (Exception e) {
            deliverable.setCommentStatus(0 + "/" + 0);

          }
        }

      }
    } catch (Exception e) {
      logger.error("unable to get feedbackcomments info", e);
      e.printStackTrace();
    }
  }


  public List<Deliverable> getCurrentDeliverableList() {
    return currentDeliverableList;
  }

  public long getDeliverableID() {
    return deliverableID;
  }

  public List<Deliverable> getDeliverables() {
    return deliverables;
  }

  /**
   * @param open: Load Ongoing and Extended if true
   * @param closed: Load Cancelled if true, Completed if false
   * @return
   */
  public List<Deliverable> getDeliverables(boolean open, boolean closed) {

    try {
      if (open) {
        if (this.isPlanningActive() && !this.isUpKeepActive()) {
          List<Deliverable> openA = deliverables.stream()
            .filter(a -> a.isActive() && a.getDeliverableInfo(this.getActualPhase()) != null
              && ((a.getDeliverableInfo(this.getActualPhase()).getStatus() == null
                || a.getDeliverableInfo(this.getActualPhase()).getStatus() == Integer
                  .parseInt(ProjectStatusEnum.Ongoing.getStatusId())
                || (a.getDeliverableInfo(this.getActualPhase()).getStatus() == Integer
                  .parseInt(ProjectStatusEnum.Extended.getStatusId())
                  || a.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == 0
                  || a.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == -1))))
            .collect(Collectors.toList());
          return openA;
        } else {
          // Reporting
          List<Deliverable> openA = deliverables.stream()
            .filter(a -> a.isActive() && a.getDeliverableInfo(this.getActualPhase()) != null
              && ((a.getDeliverableInfo(this.getActualPhase()).getStatus() == null
                || a.getDeliverableInfo(this.getActualPhase()).getStatus() == Integer
                  .parseInt(ProjectStatusEnum.Ongoing.getStatusId())
                || (a.getDeliverableInfo(this.getActualPhase()).getStatus() == Integer
                  .parseInt(ProjectStatusEnum.Extended.getStatusId())
                  || a.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == 0))))
            .collect(Collectors.toList());

          if (closed) {
            openA.addAll(deliverables.stream()
              .filter(d -> d.isActive() && d.getDeliverableInfo(this.getActualPhase()) != null
                && d.getDeliverableInfo(this.getActualPhase()).getYear() == this.getCurrentCycleYear()
                && d.getDeliverableInfo(this.getActualPhase()).getStatus() != null
                && d.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
                  .parseInt(ProjectStatusEnum.Complete.getStatusId()))
              .collect(Collectors.toList()));

            openA.addAll(deliverables.stream()
              .filter(d -> d.isActive() && d.getDeliverableInfo(this.getActualPhase()) != null
                && d.getDeliverableInfo(this.getActualPhase()).getNewExpectedYear() != null
                && d.getDeliverableInfo(this.getActualPhase()).getNewExpectedYear().intValue() == this
                  .getCurrentCycleYear()
                && d.getDeliverableInfo(this.getActualPhase()).getStatus() != null
                && d.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
                  .parseInt(ProjectStatusEnum.Complete.getStatusId()))
              .collect(Collectors.toList()));
          }

          openA.sort((p1, p2) -> this.isDeliverableComplete(p1.getId(), this.getActualPhase().getId())
            .compareTo(this.isDeliverableComplete(p2.getId(), this.getActualPhase().getId())));

          HashSet<Deliverable> deliverables = new HashSet<>();
          deliverables.addAll(openA);
          openA.clear();
          openA.addAll(deliverables);
          return openA;
        }


      } else {
        if (this.isPlanningActive()) {
          List<Deliverable> openA = new ArrayList<>();
          if (closed) {
            openA = deliverables.stream()
              .filter(a -> a.isActive() && a.getDeliverableInfo(this.getActualPhase()) != null
                && ((a.getDeliverableInfo(this.getActualPhase()).getStatus() != null
                  && ((a.getDeliverableInfo(this.getActualPhase()).getStatus() == Integer
                    .parseInt(ProjectStatusEnum.Cancelled.getStatusId()))))))
              .collect(Collectors.toList());

          } else {
            openA = deliverables.stream()
              .filter(a -> a.isActive() && a.getDeliverableInfo(this.getActualPhase()) != null
                && ((a.getDeliverableInfo(this.getActualPhase()).getStatus() != null
                  && ((a.getDeliverableInfo(this.getActualPhase()).getStatus() == Integer
                    .parseInt(ProjectStatusEnum.Complete.getStatusId()))))))
              .collect(Collectors.toList());
          }

          return openA;
        } else {
          List<Deliverable> openA = new ArrayList<>();
          if (closed) {
            openA = deliverables.stream()
              .filter(a -> a.isActive() && a.getDeliverableInfo(this.getActualPhase()) != null
                && ((a.getDeliverableInfo(this.getActualPhase()).getStatus() != null
                  && ((a.getDeliverableInfo(this.getActualPhase()).getStatus() == Integer
                    .parseInt(ProjectStatusEnum.Cancelled.getStatusId()))))))
              .collect(Collectors.toList());

          } else {
            openA = deliverables.stream()
              .filter(a -> a.isActive() && a.getDeliverableInfo(this.getActualPhase()) != null
                && ((a.getDeliverableInfo(this.getActualPhase()).getStatus() != null
                  && ((a.getDeliverableInfo(this.getActualPhase()).getStatus() == Integer
                    .parseInt(ProjectStatusEnum.Complete.getStatusId()))))))
              .collect(Collectors.toList());
          }

          if (closed) {
            openA.removeAll(deliverables.stream()
              .filter(d -> d.isActive() && d.getDeliverableInfo(this.getActualPhase()) != null
                && d.getDeliverableInfo(this.getActualPhase()).getYear() == this.getCurrentCycleYear()
                && d.getDeliverableInfo(this.getActualPhase()).getStatus() != null
                && d.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
                  .parseInt(ProjectStatusEnum.Complete.getStatusId()))
              .collect(Collectors.toList()));

            openA.removeAll(deliverables.stream()
              .filter(d -> d.isActive() && d.getDeliverableInfo(this.getActualPhase()) != null
                && d.getDeliverableInfo(this.getActualPhase()).getNewExpectedYear() != null
                && d.getDeliverableInfo(this.getActualPhase()).getNewExpectedYear().intValue() == this
                  .getCurrentCycleYear()
                && d.getDeliverableInfo(this.getActualPhase()).getStatus() != null
                && d.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
                  .parseInt(ProjectStatusEnum.Complete.getStatusId()))
              .collect(Collectors.toList()));
          }


          openA.sort((p1, p2) -> this.isDeliverableComplete(p1.getId(), this.getActualPhase().getId())
            .compareTo(this.isDeliverableComplete(p2.getId(), this.getActualPhase().getId())));
          HashSet<Deliverable> deliverables = new HashSet<>();
          deliverables.addAll(openA);
          openA.clear();
          openA.addAll(deliverables);
          return openA;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      logger.error("unable to get deliverable info", e);
      return new ArrayList<>();
    }
  }

  public List<DeliverableType> getDeliverablesType() {
    return deliverablesType;
  }

  public int getIndexDeliverables(long id) {
    Deliverable activity = new Deliverable();
    activity.setId(id);
    return deliverables.indexOf(activity);

  }


  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }

  public List<Deliverable> getPreviousSharedDeliverableList() {
    return previousSharedDeliverableList;
  }

  public Project getProject() {
    return project;
  }

  public long getProjectID() {
    return projectID;
  }


  /*
   * Copy method from project.getCurrentDeliverables to allow add the shared deliverables to list
   */
  public void loadCurrentDeliverables() {
    try {
      currentDeliverableList = new ArrayList<>();
      currentDeliverableList =
        this.getDeliverables().stream().filter(d -> d.isActive() && d.getDeliverableInfo(this.getActualPhase()) != null
          && !d.getDeliverableInfo().isPrevious()).collect(Collectors.toList());
    } catch (Exception e) {
      logger.error("unable to get shared deliverables", e);
    }

    List<String> deliverables = null;

    try {
      deliverables = deliverableManager.getDuplicatesDeliverablesByPhase(this.getActualPhase().getId());
    } catch (Exception e) {
      logger.error("unable to get duplivated deliverables", e);
    }

    // Load Shared deliverables
    previousSharedDeliverableList = new ArrayList<>();
    try {
      List<ProjectDeliverableShared> deliverableShared = this.projectDeliverableSharedManager
        .getByProjectAndPhase(project.getId(), this.getActualPhase().getId()) != null
          ? this.projectDeliverableSharedManager.getByProjectAndPhase(project.getId(), this.getActualPhase().getId())
            .stream()
            .filter(px -> px.isActive() && px.getDeliverable().isActive()
              && px.getDeliverable().getDeliverableInfo(this.getActualPhase()) != null)
            .collect(Collectors.toList())
          : Collections.emptyList();


      if (deliverableShared != null && !deliverableShared.isEmpty()) {
        for (ProjectDeliverableShared deliverableS : deliverableShared) {
          List<ProjectDeliverableShared> deliverablesTemp = projectDeliverableSharedManager
            .getByDeliverable(deliverableS.getDeliverable().getId(), this.getActualPhase().getId());

          if (deliverablesTemp != null && !deliverablesTemp.isEmpty()) {
            for (ProjectDeliverableShared deliverableTemp : deliverablesTemp) {

              if (deliverableTemp.getDeliverable().getSharedWithProjects() == null
                || (deliverableTemp.getDeliverable().getSharedWithProjects() != null
                  && deliverableTemp.getDeliverable().getSharedWithProjects().isEmpty())) {

                deliverableTemp.getDeliverable().setSharedWithProjects(
                  "" + deliverableTemp.getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym());

              } else {
                if (deliverableTemp.getDeliverable().getSharedWithProjects() != null
                  && deliverableTemp.getProject() != null
                  && deliverableTemp.getProject().getProjecInfoPhase(this.getActualPhase()) != null
                  && deliverableTemp.getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym() != null
                  && (!deliverableTemp.getDeliverable().getSharedWithProjects()
                    .contains(deliverableTemp.getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym()))) {
                  deliverableTemp.getDeliverable()
                    .setSharedWithProjects(deliverableTemp.getDeliverable().getSharedWithProjects() + "; "
                      + deliverableTemp.getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym());
                }
              }
            }
          }

          if (!currentDeliverableList.contains(deliverableS.getDeliverable())
            && !deliverableS.getDeliverable().getDeliverableInfo(this.getActualPhase()).isPrevious()) {
            currentDeliverableList.add(deliverableS.getDeliverable());
          }
        }

        // Previous shared deliverables
        List<ProjectDeliverableShared> prevProjectDeliverables = deliverableShared.stream()
          .filter(d -> d.isActive() && d.getDeliverable() != null
            && d.getDeliverable().getDeliverableInfo(this.getActualPhase()) != null
            && d.getDeliverable().getDeliverableInfo().isPrevious())
          .collect(Collectors.toList());

        if (prevProjectDeliverables != null && !prevProjectDeliverables.isEmpty()) {
          for (ProjectDeliverableShared prevShared : prevProjectDeliverables) {
            if (prevShared != null && prevShared.getDeliverable() != null
              && prevShared.getDeliverable().getId() != null) {

              // Owner
              if (prevShared.getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym() != null) {
                prevShared.getDeliverable()
                  .setOwner(prevShared.getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym());
                prevShared.getDeliverable()
                  .setSharedWithMe(prevShared.getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym());
              } else {
                prevShared.getDeliverable().setOwner(prevShared.getProject().getId() + "");
                prevShared.getDeliverable().setSharedWithMe(prevShared.getProject().getId() + "");
              }

              // Responsible
              String leader = null;
              List<DeliverableUserPartnership> deliverablePartnershipResponsibles =
                prevShared.getDeliverable().getDeliverableUserPartnerships().stream()
                  .filter(dp -> dp.isActive() && dp.getPhase().getId().equals(this.getActualPhase().getId()) && dp
                    .getDeliverablePartnerType().getId().equals(APConstants.DELIVERABLE_PARTNERSHIP_TYPE_RESPONSIBLE))
                  .collect(Collectors.toList());
              if (deliverablePartnershipResponsibles != null && !deliverablePartnershipResponsibles.isEmpty()) {
                if (deliverablePartnershipResponsibles.size() > 1) {
                  Log.warn("There are more than 1 deliverable responsibles for D" + prevShared.getDeliverable().getId()
                    + " " + this.getActualPhase().toString());
                }
                DeliverableUserPartnership responsible = deliverablePartnershipResponsibles.get(0);

                if (responsible != null && responsible.getDeliverableUserPartnershipPersons() != null) {

                  DeliverableUserPartnershipPerson responsibleppp = new DeliverableUserPartnershipPerson();
                  List<DeliverableUserPartnershipPerson> persons = responsible.getDeliverableUserPartnershipPersons()
                    .stream().filter(dp -> dp.isActive()).collect(Collectors.toList());
                  if (!persons.isEmpty()) {
                    responsibleppp = persons.get(0);
                  }

                  if (responsibleppp != null && responsibleppp.getUser() != null
                    && responsibleppp.getUser().getComposedName() != null) {
                    leader = responsibleppp.getUser().getComposedName();
                  }
                }
              }

              // Set deliverable responsible
              if (leader != null) {
                prevShared.getDeliverable().setResponsible(leader);
              }


              previousSharedDeliverableList.add(prevShared.getDeliverable());
            }
          }
        }
      }
    } catch (Exception e) {
      logger.error("unable to get shared deliverables", e);
    }

    // shared with

    if (currentDeliverableList != null && !currentDeliverableList.isEmpty()) {
      List<ProjectDeliverableShared> deliverablesShared = new ArrayList<>();
      try {
        for (Deliverable deliverableTemp : currentDeliverableList) {
          if (deliverableTemp != null && deliverableTemp.getId() != null) {

            deliverableTemp = deliverableManager.getDeliverableById(deliverableTemp.getId());
            deliverablesShared = projectDeliverableSharedManager.getByPhase(this.getActualPhase().getId());
            if (deliverablesShared != null && !deliverablesShared.isEmpty()) {
              deliverablesShared = deliverablesShared.stream().filter(ds -> ds.isActive() && ds.getDeliverable() != null
                && ds.getDeliverable().getProject().getId().equals(projectID)).collect(Collectors.toList());
            }
            // Is duplicated
            if (this.hasSpecificities(APConstants.DUPLICATED_DELIVERABLES_FUNCTIONALITY_ACTIVE)) {
              String DOI = null;
              String handle = null;
              String disseminationURL = null;


              if (deliverableTemp.getDeliverableMetadataElements() != null) {
                deliverableTemp.setMetadataElements(new ArrayList<>(deliverableTemp.getDeliverableMetadataElements()
                  .stream().filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase()))
                  .collect(Collectors.toList())));
              }
              List<DeliverableMetadataElement> deliverableMetadataElements;
              deliverableMetadataElements = deliverableTemp.getMetadataElements(this.getActualPhase());
              try {
                DOI = deliverableMetadataElements.stream()
                  .filter(me -> me != null && me.getMetadataElement() != null && me.getMetadataElement().getId() != null
                    && me.getMetadataElement().getId().longValue() == 36L && me.getPhase().equals(this.getActualPhase())
                    && me.getDeliverable().getId().equals(deliverableID) && !StringUtils.isBlank(me.getElementValue()))
                  .findFirst().orElse(null).getElementValue();
              } catch (Exception e) {
                Log.info(e);
              }
              try {
                handle = deliverableMetadataElements.stream()
                  .filter(me -> me != null && me.getMetadataElement() != null && me.getMetadataElement().getId() != null
                    && me.getMetadataElement().getId().longValue() == 35L && me.getPhase().equals(this.getActualPhase())
                    && me.getDeliverable().getId().equals(deliverableID) && !StringUtils.isBlank(me.getElementValue()))
                  .findFirst().orElse(null).getElementValue();
              } catch (Exception e) {
                Log.info(e);
              }

              // Deliverable dissemination
              DeliverableDissemination deliverableDissemination = new DeliverableDissemination();

              try {
                deliverableDissemination = deliverableTemp.getDissemination(this.getActualPhase());
              } catch (Exception e) {
                Log.info(e);
              }

              if (deliverableDissemination != null && deliverableDissemination.getDisseminationUrl() != null
                && !deliverableDissemination.getDisseminationUrl().isEmpty()) {
                disseminationURL = deliverableDissemination.getDisseminationUrl();
              }
              if (!deliverables.isEmpty()) {
                List<DeliverableSearchSummary> deliverableDTOs = null;
                deliverableDTOs = this.getDuplicatedDeliverableInformationNew(DOI, handle, disseminationURL,
                  deliverableTemp.getId(), deliverables);


                boolean isDuplicated = false;
                if (deliverableDTOs != null && !deliverableDTOs.isEmpty()) {
                  isDuplicated = true;
                } else {
                  isDuplicated = false;
                }
                if (deliverableTemp.getDeliverableInfo(this.getActualPhase()) != null) {
                  deliverableTemp.getDeliverableInfo(this.getActualPhase()).setDuplicated(isDuplicated);
                  deliverableManager.saveDeliverable(deliverableTemp);
                }
              }
            }

            // Owner
            if (deliverableTemp.getProject() != null && !deliverableTemp.getProject().getId().equals(projectID)) {
              deliverableTemp
                .setOwner(deliverableTemp.getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym());
              deliverableTemp
                .setSharedWithMe(deliverableTemp.getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym());
            } else {
              deliverableTemp.setOwner("This Cluster");
              deliverableTemp.setSharedWithMe("Not Applicable");
            }


            // Responsible
            String leader = null;
            List<DeliverableUserPartnership> deliverablePartnershipResponsibles = deliverableTemp
              .getDeliverableUserPartnerships().stream()
              .filter(dp -> dp.isActive() && dp.getPhase().getId().equals(this.getActualPhase().getId())
                && dp.getDeliverablePartnerType().getId().equals(APConstants.DELIVERABLE_PARTNERSHIP_TYPE_RESPONSIBLE))
              .collect(Collectors.toList());
            if (deliverablePartnershipResponsibles != null && !deliverablePartnershipResponsibles.isEmpty()) {
              if (deliverablePartnershipResponsibles.size() > 1) {
                Log.warn("There are more than 1 deliverable responsibles for D" + deliverableTemp.getId() + " "
                  + this.getActualPhase().toString());
              }
              DeliverableUserPartnership responsible = deliverablePartnershipResponsibles.get(0);

              if (responsible != null && responsible.getDeliverableUserPartnershipPersons() != null) {

                DeliverableUserPartnershipPerson responsibleppp = new DeliverableUserPartnershipPerson();
                List<DeliverableUserPartnershipPerson> persons = responsible.getDeliverableUserPartnershipPersons()
                  .stream().filter(dp -> dp.isActive()).collect(Collectors.toList());
                if (!persons.isEmpty()) {
                  responsibleppp = persons.get(0);
                }

                if (responsibleppp != null && responsibleppp.getUser() != null
                  && responsibleppp.getUser().getComposedName() != null) {
                  leader = responsibleppp.getUser().getComposedName();
                }
              }
            }


            // Set deliverable responsible
            if (leader != null) {
              deliverableTemp.setResponsible(leader);
            }

            // Shared with others
            if (deliverablesShared != null && !deliverablesShared.isEmpty()) {
              for (ProjectDeliverableShared deliverableShared : deliverablesShared) {
                if (deliverableShared.getDeliverable().getSharedWithProjects() == null) {
                  deliverableShared.getDeliverable().setSharedWithProjects(
                    "" + deliverableShared.getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym());
                } else {
                  if (deliverableShared.getDeliverable() != null
                    && deliverableShared.getDeliverable().getSharedWithProjects() != null
                    && deliverableShared.getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym() != null
                    && !deliverableShared.getDeliverable().getSharedWithProjects().contains(
                      deliverableShared.getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym())) {
                    deliverableShared.getDeliverable()
                      .setSharedWithProjects(deliverableShared.getDeliverable().getSharedWithProjects() + "; "
                        + deliverableShared.getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym());
                  }
                }
              }
            }
          }
        }
      } catch (Exception e) {
        logger.error("unable to get shared deliverables", e);
      }
    }

    if (currentDeliverableList != null && !currentDeliverableList.isEmpty()) {
      try {
        currentDeliverableList.stream().sorted((d1, d2) -> d1.getId().compareTo((d2.getId())))
          .collect(Collectors.toList());
      } catch (Exception e) {
        logger.error("unable to get shared deliverables", e);
      }
      // deliverables.addAll(currentDeliverableList);
    }


  }

  /*
   * Copy method from project.getCurrentDeliverables to allow add the shared deliverables to list
   */
  public void loadCurrentDeliverablesOld() {
    try {
      currentDeliverableList = new ArrayList<>();
      currentDeliverableList =
        this.getDeliverables().stream().filter(d -> d.isActive() && d.getDeliverableInfo(this.getActualPhase()) != null
          && !d.getDeliverableInfo().isPrevious()).collect(Collectors.toList());
    } catch (Exception e) {
      logger.error("unable to get shared deliverables", e);
    }

    // Load Shared deliverables
    previousSharedDeliverableList = new ArrayList<>();
    try {
      List<ProjectDeliverableShared> deliverableShared = this.projectDeliverableSharedManager
        .getByProjectAndPhase(project.getId(), this.getActualPhase().getId()) != null
          ? this.projectDeliverableSharedManager.getByProjectAndPhase(project.getId(), this.getActualPhase().getId())
            .stream()
            .filter(px -> px.isActive() && px.getDeliverable().isActive()
              && px.getDeliverable().getDeliverableInfo(this.getActualPhase()) != null)
            .collect(Collectors.toList())
          : Collections.emptyList();


      if (deliverableShared != null && !deliverableShared.isEmpty()) {
        for (ProjectDeliverableShared deliverableS : deliverableShared) {
          List<ProjectDeliverableShared> deliverablesTemp = projectDeliverableSharedManager
            .getByDeliverable(deliverableS.getDeliverable().getId(), this.getActualPhase().getId());

          if (deliverablesTemp != null && !deliverablesTemp.isEmpty()) {
            for (ProjectDeliverableShared deliverableTemp : deliverablesTemp) {

              if (deliverableTemp.getDeliverable().getSharedWithProjects() == null
                || (deliverableTemp.getDeliverable().getSharedWithProjects() != null
                  && deliverableTemp.getDeliverable().getSharedWithProjects().isEmpty())) {

                deliverableTemp.getDeliverable().setSharedWithProjects(
                  "" + deliverableTemp.getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym());

              } else {
                if (deliverableTemp.getDeliverable().getSharedWithProjects() != null
                  && deliverableTemp.getProject() != null
                  && deliverableTemp.getProject().getProjecInfoPhase(this.getActualPhase()) != null
                  && deliverableTemp.getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym() != null
                  && (!deliverableTemp.getDeliverable().getSharedWithProjects()
                    .contains(deliverableTemp.getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym()))) {
                  deliverableTemp.getDeliverable()
                    .setSharedWithProjects(deliverableTemp.getDeliverable().getSharedWithProjects() + "; "
                      + deliverableTemp.getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym());
                }
              }
            }
          }

          if (!currentDeliverableList.contains(deliverableS.getDeliverable())
            && !deliverableS.getDeliverable().getDeliverableInfo(this.getActualPhase()).isPrevious()) {
            currentDeliverableList.add(deliverableS.getDeliverable());
          }
        }

        // Previous shared deliverables
        List<ProjectDeliverableShared> prevProjectDeliverables = deliverableShared.stream()
          .filter(d -> d.isActive() && d.getDeliverable() != null
            && d.getDeliverable().getDeliverableInfo(this.getActualPhase()) != null
            && d.getDeliverable().getDeliverableInfo().isPrevious())
          .collect(Collectors.toList());

        if (prevProjectDeliverables != null && !prevProjectDeliverables.isEmpty()) {
          for (ProjectDeliverableShared prevShared : prevProjectDeliverables) {
            if (prevShared != null && prevShared.getDeliverable() != null
              && prevShared.getDeliverable().getId() != null) {

              // Owner
              if (prevShared.getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym() != null) {
                prevShared.getDeliverable()
                  .setOwner(prevShared.getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym());
                prevShared.getDeliverable()
                  .setSharedWithMe(prevShared.getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym());
              } else {
                prevShared.getDeliverable().setOwner(prevShared.getProject().getId() + "");
                prevShared.getDeliverable().setSharedWithMe(prevShared.getProject().getId() + "");
              }

              // Responsible
              String leader = null;
              List<DeliverableUserPartnership> deliverablePartnershipResponsibles =
                prevShared.getDeliverable().getDeliverableUserPartnerships().stream()
                  .filter(dp -> dp.isActive() && dp.getPhase().getId().equals(this.getActualPhase().getId()) && dp
                    .getDeliverablePartnerType().getId().equals(APConstants.DELIVERABLE_PARTNERSHIP_TYPE_RESPONSIBLE))
                  .collect(Collectors.toList());
              if (deliverablePartnershipResponsibles != null && !deliverablePartnershipResponsibles.isEmpty()) {
                if (deliverablePartnershipResponsibles.size() > 1) {
                  Log.warn("There are more than 1 deliverable responsibles for D" + prevShared.getDeliverable().getId()
                    + " " + this.getActualPhase().toString());
                }
                DeliverableUserPartnership responsible = deliverablePartnershipResponsibles.get(0);

                if (responsible != null && responsible.getDeliverableUserPartnershipPersons() != null) {

                  DeliverableUserPartnershipPerson responsibleppp = new DeliverableUserPartnershipPerson();
                  List<DeliverableUserPartnershipPerson> persons = responsible.getDeliverableUserPartnershipPersons()
                    .stream().filter(dp -> dp.isActive()).collect(Collectors.toList());
                  if (!persons.isEmpty()) {
                    responsibleppp = persons.get(0);
                  }

                  if (responsibleppp != null && responsibleppp.getUser() != null
                    && responsibleppp.getUser().getComposedName() != null) {
                    leader = responsibleppp.getUser().getComposedName();
                  }
                }
              }

              // Set deliverable responsible
              if (leader != null) {
                prevShared.getDeliverable().setResponsible(leader);
              }


              previousSharedDeliverableList.add(prevShared.getDeliverable());
            }
          }
        }
      }
    } catch (Exception e) {
      logger.error("unable to get shared deliverables", e);
    }

    // shared with

    if (currentDeliverableList != null && !currentDeliverableList.isEmpty()) {
      List<ProjectDeliverableShared> deliverablesShared = new ArrayList<>();
      try {
        for (Deliverable deliverableTemp : currentDeliverableList) {
          if (deliverableTemp != null && deliverableTemp.getId() != null) {
            deliverableTemp = deliverableManager.getDeliverableById(deliverableTemp.getId());
            deliverablesShared = projectDeliverableSharedManager.getByPhase(this.getActualPhase().getId());
            if (deliverablesShared != null && !deliverablesShared.isEmpty()) {
              deliverablesShared = deliverablesShared.stream().filter(ds -> ds.isActive() && ds.getDeliverable() != null
                && ds.getDeliverable().getProject().getId().equals(projectID)).collect(Collectors.toList());
            }

            // Is duplicated
            if (this.hasSpecificities(APConstants.DUPLICATED_DELIVERABLES_FUNCTIONALITY_ACTIVE)) {
              String DOI = null;
              String handle = null;
              String disseminationURL = null;


              if (deliverableTemp.getDeliverableMetadataElements() != null) {
                deliverableTemp.setMetadataElements(new ArrayList<>(deliverableTemp.getDeliverableMetadataElements()
                  .stream().filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase()))
                  .collect(Collectors.toList())));
              }
              List<DeliverableMetadataElement> deliverableMetadataElements;
              deliverableMetadataElements = deliverableTemp.getMetadataElements(this.getActualPhase());

              try {
                DOI = deliverableMetadataElements.stream()
                  .filter(me -> me != null && me.getMetadataElement() != null && me.getMetadataElement().getId() != null
                    && me.getMetadataElement().getId().longValue() == 36L && me.getPhase().equals(this.getActualPhase())
                    && me.getDeliverable().getId().equals(deliverableID) && !StringUtils.isBlank(me.getElementValue()))
                  .findFirst().orElse(null).getElementValue();
              } catch (Exception e) {
                Log.info(e);
              }

              try {
                handle = deliverableMetadataElements.stream()
                  .filter(me -> me != null && me.getMetadataElement() != null && me.getMetadataElement().getId() != null
                    && me.getMetadataElement().getId().longValue() == 35L && me.getPhase().equals(this.getActualPhase())
                    && me.getDeliverable().getId().equals(deliverableID) && !StringUtils.isBlank(me.getElementValue()))
                  .findFirst().orElse(null).getElementValue();
              } catch (Exception e) {
                Log.info(e);
              }

              // Deliverable dissemination
              DeliverableDissemination deliverableDissemination = new DeliverableDissemination();

              try {
                deliverableDissemination = deliverableTemp.getDissemination(this.getActualPhase());
              } catch (Exception e) {
                Log.info(e);
              }

              if (deliverableDissemination != null && deliverableDissemination.getDisseminationUrl() != null
                && !deliverableDissemination.getDisseminationUrl().isEmpty()) {
                disseminationURL = deliverableDissemination.getDisseminationUrl();
              }

              List<DeliverableSearchSummary> deliverableDTOs = null;
              deliverableDTOs =
                this.getDuplicatedDeliverableInformation(DOI, handle, disseminationURL, deliverableTemp.getId());

              boolean isDuplicated = false;
              if (deliverableDTOs != null && !deliverableDTOs.isEmpty()) {
                isDuplicated = true;
              } else {
                isDuplicated = false;
              }
              if (deliverableTemp.getDeliverableInfo(this.getActualPhase()) != null) {
                deliverableTemp.getDeliverableInfo(this.getActualPhase()).setDuplicated(isDuplicated);
                deliverableManager.saveDeliverable(deliverableTemp);
              }
            }

            // Owner
            if (deliverableTemp.getProject() != null && !deliverableTemp.getProject().getId().equals(projectID)) {
              deliverableTemp
                .setOwner(deliverableTemp.getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym());
              deliverableTemp
                .setSharedWithMe(deliverableTemp.getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym());
            } else {
              deliverableTemp.setOwner("This Cluster");
              deliverableTemp.setSharedWithMe("Not Applicable");
            }

            // Responsible
            String leader = null;
            List<DeliverableUserPartnership> deliverablePartnershipResponsibles = deliverableTemp
              .getDeliverableUserPartnerships().stream()
              .filter(dp -> dp.isActive() && dp.getPhase().getId().equals(this.getActualPhase().getId())
                && dp.getDeliverablePartnerType().getId().equals(APConstants.DELIVERABLE_PARTNERSHIP_TYPE_RESPONSIBLE))
              .collect(Collectors.toList());
            if (deliverablePartnershipResponsibles != null && !deliverablePartnershipResponsibles.isEmpty()) {
              if (deliverablePartnershipResponsibles.size() > 1) {
                Log.warn("There are more than 1 deliverable responsibles for D" + deliverableTemp.getId() + " "
                  + this.getActualPhase().toString());
              }
              DeliverableUserPartnership responsible = deliverablePartnershipResponsibles.get(0);

              if (responsible != null && responsible.getDeliverableUserPartnershipPersons() != null) {

                DeliverableUserPartnershipPerson responsibleppp = new DeliverableUserPartnershipPerson();
                List<DeliverableUserPartnershipPerson> persons = responsible.getDeliverableUserPartnershipPersons()
                  .stream().filter(dp -> dp.isActive()).collect(Collectors.toList());
                if (!persons.isEmpty()) {
                  responsibleppp = persons.get(0);
                }

                if (responsibleppp != null && responsibleppp.getUser() != null
                  && responsibleppp.getUser().getComposedName() != null) {
                  leader = responsibleppp.getUser().getComposedName();
                }
              }
            }

            // Set deliverable responsible
            if (leader != null) {
              deliverableTemp.setResponsible(leader);
            }

            // Shared with others
            for (ProjectDeliverableShared deliverableShared : deliverablesShared) {
              if (deliverableShared.getDeliverable().getSharedWithProjects() == null) {
                deliverableShared.getDeliverable().setSharedWithProjects(
                  "" + deliverableShared.getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym());
              } else {
                if (deliverableShared.getDeliverable() != null
                  && deliverableShared.getDeliverable().getSharedWithProjects() != null
                  && deliverableShared.getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym() != null
                  && !deliverableShared.getDeliverable().getSharedWithProjects()
                    .contains(deliverableShared.getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym())) {
                  deliverableShared.getDeliverable()
                    .setSharedWithProjects(deliverableShared.getDeliverable().getSharedWithProjects() + "; "
                      + deliverableShared.getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym());
                }
              }
            }
          }
        }
      } catch (Exception e) {
        logger.error("unable to get shared deliverables", e);
      }
    }


    if (currentDeliverableList != null && !currentDeliverableList.isEmpty()) {
      try {
        currentDeliverableList.stream().sorted((d1, d2) -> d1.getId().compareTo((d2.getId())))
          .collect(Collectors.toList());
      } catch (Exception e) {
        logger.error("unable to get shared deliverables", e);
      }
      // deliverables.addAll(currentDeliverableList);
    }


  }

  @Override
  public void prepare() throws Exception {
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());
    Phase phase = this.getActualPhase();
    phase = phaseManager.getPhaseById(phase.getId());
    try {
      projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));
      project = projectManager.getProjectById(projectID);

      if (project != null) {

        allYears = project.getProjecInfoPhase(this.getActualPhase()).getAllYears();

        if (deliverableTypeManager.findAll() != null) {
          deliverablesType = new ArrayList<>(deliverableTypeManager.findAll());
        }

        if (project.getDeliverables() != null) {


          List<DeliverableInfo> infos = deliverableInfoManager.getDeliverablesInfoByProjectAndPhase(phase, project);
          deliverables = new ArrayList<>();
          if (infos != null && !infos.isEmpty()) {
            for (DeliverableInfo deliverableInfo : infos) {
              Deliverable deliverable = deliverableInfo.getDeliverable();
              deliverable.setDeliverableInfo(deliverableInfo);
              deliverables.add(deliverable);
            }
          }


          for (Deliverable deliverable : deliverables) {
            deliverable.setResponsiblePartnership(this.responsiblePartner(deliverable));

            // Gets the Deliverable Funding Source Data without the full information.
            List<DeliverableFundingSource> fundingSources =
              new ArrayList<>(deliverable.getDeliverableFundingSources().stream()
                .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList()));
            for (DeliverableFundingSource deliverableFundingSource : fundingSources) {
              deliverableFundingSource.getFundingSource().setFundingSourceInfo(
                deliverableFundingSource.getFundingSource().getFundingSourceInfo(this.getActualPhase()));
            }

            deliverable.setFundingSources(fundingSources);
          }
        }


        this.loadCurrentDeliverables();
      }

      if (this.hasSpecificities(this.feedbackModule())) {
        this.getCommentStatuses();
      }

      if (this.isReportingActive() || this.isUpKeepActive()) {
        deliverables.sort((p1, p2) -> this.isDeliverableComplete(p1.getId(), this.getActualPhase().getId())
          .compareTo(this.isDeliverableComplete(p2.getId(), this.getActualPhase().getId())));

      }
      String[] params = {loggedCrp.getAcronym(), project.getId() + ""};
      this.setBasePermission(this.getText(Permission.PROJECT_DELIVERABLE_LIST_BASE_PERMISSION, params));

    } catch (Exception e) {
      projectID = -1;
    }

  }

  private List<DeliverableUserPartnership> responsiblePartner(Deliverable deliverable) {
    try {
      return deliverable.getDeliverableUserPartnerships().stream()
        .filter(dp -> dp.isActive()
          && dp.getDeliverablePartnerType().getId().equals(APConstants.DELIVERABLE_PARTNERSHIP_TYPE_RESPONSIBLE))
        .collect(Collectors.toList());
    } catch (Exception e) {
      return Collections.emptyList();
    }
  }

  @Override
  public String save() {
    return SUCCESS;
  }

  public void setAllYears(List<Integer> allYears) {
    this.allYears = allYears;
  }

  public void setCurrentDeliverableList(List<Deliverable> currentDeliverableList) {
    this.currentDeliverableList = currentDeliverableList;
  }

  public void setDeliverableID(long deliverableID) {
    this.deliverableID = deliverableID;
  }

  public void setDeliverables(List<Deliverable> deliverables) {
    this.deliverables = deliverables;
  }

  public void setDeliverablesType(List<DeliverableType> deliverablesType) {
    this.deliverablesType = deliverablesType;
  }

  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setPreviousSharedDeliverableList(List<Deliverable> previousSharedDeliverableList) {
    this.previousSharedDeliverableList = previousSharedDeliverableList;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }
}