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
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableTypeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableDissemination;
import org.cgiar.ccafs.marlo.data.model.DeliverableType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class DeliverableListAction extends BaseAction {


  private static final long serialVersionUID = -823169163612346982L;


  private Crp loggedCrp;

  // Managers
  private ProjectManager projectManager;


  private DeliverableTypeManager deliverableTypeManager;
  private DeliverableManager deliverableManager;

  private SectionStatusManager sectionStatusManager;
  private CrpManager crpManager;


  // Front-end
  private List<Deliverable> deliverables;

  private List<DeliverableType> deliverablesType;
  private long projectID;

  private long deliverableID;
  private Project project;
  private List<Integer> allYears;

  @Inject
  public DeliverableListAction(APConfig config, ProjectManager projectManager, CrpManager crpManager,
    DeliverableTypeManager deliverableTypeManager, DeliverableManager deliverableManager,
    SectionStatusManager sectionStatusManager) {
    super(config);
    this.projectManager = projectManager;
    this.sectionStatusManager = sectionStatusManager;
    this.crpManager = crpManager;
    this.deliverableTypeManager = deliverableTypeManager;
    this.deliverableManager = deliverableManager;
  }


  @Override
  public String add() {

    Deliverable deliverable = new Deliverable();
    deliverable.setYear(this.getCurrentCycleYear());
    deliverable.setCreatedBy(this.getCurrentUser());
    deliverable.setModifiedBy(this.getCurrentUser());
    deliverable.setModificationJustification("New expected deliverable created");
    deliverable.setActive(true);
    deliverable.setActiveSince(new Date());
    deliverable.setProject(project);
    deliverable.setCreateDate(new Date());


    deliverableID = deliverableManager.saveDeliverable(deliverable);

    if (deliverableID > 0) {
      return SUCCESS;
    }

    return INPUT;
  }

  public boolean canEdit(long deliverableID) {
    Deliverable deliverable = deliverableManager.getDeliverableById(deliverableID);
    if (this.isPlanningActive()) {
      if (deliverable.getYear() >= this.getCurrentCycleYear()) {
        return true;
      }
      return false;
    }
    return true;
  }


  @Override
  public String delete() {

    Map<String, Object> parameters = this.getParameters();
    deliverableID =
      Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.PROJECT_DELIVERABLE_REQUEST_ID))[0]));


    Deliverable deliverable = deliverableManager.getDeliverableById(deliverableID);

    for (SectionStatus sectionStatus : deliverable.getSectionStatuses()) {
      sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
    }

    projectID = deliverable.getProject().getId();

    if (deliverable != null) {
      deliverableManager.deleteDeliverable(deliverableID);
      this.addActionMessage("message:" + this.getText("deleting.success"));
    }
    return SUCCESS;
  }

  public List<Integer> getAllYears() {
    return allYears;
  }

  public long getDeliverableID() {
    return deliverableID;
  }

  public List<Deliverable> getDeliverables() {
    return deliverables;
  }

  public List<Deliverable> getDeliverables(boolean open) {

    try {
      if (open) {

        List<Deliverable> openA = deliverables.stream()
          .filter(a -> a.isActive()
            && ((a.getStatus() == null || a.getStatus() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId())
              || (a.getStatus() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())
                || a.getStatus().intValue() == 0))))
          .collect(Collectors.toList());
        return openA;

      } else {

        List<Deliverable> openA = deliverables.stream()
          .filter(a -> a.isActive()
            && ((a.getStatus() != null && (a.getStatus() == Integer.parseInt(ProjectStatusEnum.Complete.getStatusId())
              || (a.getStatus() == Integer.parseInt(ProjectStatusEnum.Cancelled.getStatusId()))))))
          .collect(Collectors.toList());
        return openA;
      }
    } catch (Exception e) {
      e.printStackTrace();
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

  public Project getProject() {
    return project;
  }

  public long getProjectID() {
    return projectID;
  }


  public Boolean isA(long deliverableID) {
    Deliverable deliverableBD = deliverableManager.getDeliverableById(deliverableID);
    this.loadDissemination(deliverableBD);
    if (deliverableBD.getDissemination().getIsOpenAccess() != null
      && deliverableBD.getDissemination().getIsOpenAccess().booleanValue()) {
      return true;
    }

    if (deliverableBD.getDissemination().getIsOpenAccess() == null) {
      return null;
    }
    return false;
  }


  public Boolean isF(long deliverableID) {


    Deliverable deliverableBD = deliverableManager.getDeliverableById(deliverableID);
    this.loadDissemination(deliverableBD);
    if (deliverableBD.getDissemination().getAlreadyDisseminated() != null
      && deliverableBD.getDissemination().getAlreadyDisseminated().booleanValue()) {
      return true;
    }
    if (deliverableBD.getDissemination().getAlreadyDisseminated() == null) {
      return null;
    }

    return false;

  }

  public Boolean isI(long deliverableID) {
    if (deliverableID == 661) {
      System.out.println("holi");
    }

    Deliverable deliverableBD = deliverableManager.getDeliverableById(deliverableID);
    this.loadDissemination(deliverableBD);
    if (deliverableBD.getDissemination().getAlreadyDisseminated() != null
      && deliverableBD.getDissemination().getAlreadyDisseminated().booleanValue()) {


      String channel = deliverableBD.getDissemination().getDisseminationChannel();
      String link = deliverableBD.getDissemination().getDisseminationUrl();
      if (channel == null || channel.equals("-1")) {
        return false;
      }
      if (link == null || link.equals("-1") || link.isEmpty()) {
        return false;
      }

      switch (channel) {
        case "cgspace":
          if (!this.validURL(link)) {
            return false;
          }
          if (!link.contains("cgspace.cgiar.org")) {
            return false;
          }
          break;
        case "dataverse":
          if (!link.contains("dataverse.harvard.edu")) {
            if (!this.validURL(link)) {
              return false;
            }
            return false;
          }
          break;
        default:
          return false;

      }
      return true;
    }
    if (deliverableBD.getDissemination().getAlreadyDisseminated() == null) {
      return null;
    }

    return false;

  }

  public void loadDissemination(Deliverable deliverableBD) {

    if (deliverableBD.getDeliverableDisseminations() != null) {
      deliverableBD.setDisseminations(new ArrayList<>(deliverableBD.getDeliverableDisseminations()));
      if (deliverableBD.getDeliverableDisseminations().size() > 0) {
        deliverableBD.setDissemination(deliverableBD.getDisseminations().get(0));
      } else {
        deliverableBD.setDissemination(new DeliverableDissemination());
      }

    }
  }

  @Override
  public void prepare() throws Exception {
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());

    try {
      projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));
      project = projectManager.getProjectById(projectID);

      if (project != null) {

        allYears = project.getAllYears();

        if (deliverableTypeManager.findAll() != null) {
          deliverablesType = new ArrayList<>(deliverableTypeManager.findAll());
        }

        if (project.getDeliverables() != null) {
          deliverables =
            new ArrayList<>(project.getDeliverables().stream().filter(d -> d.isActive()).collect(Collectors.toList()));
        }
      }

      String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
      this.setBasePermission(this.getText(Permission.PROJECT_DELIVERABLE_LIST_BASE_PERMISSION, params));

    } catch (Exception e) {
      projectID = -1;
    }

  }


  @Override
  public String save() {
    return SUCCESS;
  }

  public void setAllYears(List<Integer> allYears) {
    this.allYears = allYears;
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

  public void setProject(Project project) {
    this.project = project;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  public boolean validURL(String URL) {
    try {
      java.net.URL url = new java.net.URL(URL);
      URLConnection conn = url.openConnection();
      conn.connect();
      return true;
    } catch (MalformedURLException e) {
      return false;
    } catch (IOException e) {
      return false;
    }
  }
}
