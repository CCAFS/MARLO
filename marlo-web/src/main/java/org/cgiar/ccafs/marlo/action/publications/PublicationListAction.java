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


package org.cgiar.ccafs.marlo.action.publications;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.DeliverableInfoManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableLeaderManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonUserManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.DeliverableLeader;
import org.cgiar.ccafs.marlo.data.model.DeliverableProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublicationListAction extends BaseAction {


  /**
   * 
   */
  private static final long serialVersionUID = -5176367401132626314L;


  private final Logger LOG = LoggerFactory.getLogger(PublicationListAction.class);


  private GlobalUnit loggedCrp;
  private long deliverableID;

  private DeliverableInfoManager deliverableInfoManager;
  private GlobalUnitManager crpManager;
  private DeliverableManager deliverableManager;
  private LiaisonUserManager liaisonUserManager;
  private InstitutionManager institutionManager;
  private DeliverableLeaderManager deliverableLeaderManager;
  private PhaseManager phaseManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private String justification;


  @Inject
  public PublicationListAction(APConfig config, GlobalUnitManager crpManager, DeliverableManager deliverableManager,
    InstitutionManager institutionManager, LiaisonUserManager liaisonUserManager,
    DeliverableInfoManager deliverableInfoManager, DeliverableLeaderManager deliverableLeaderManager,
    PhaseManager phaseManager, LiaisonInstitutionManager liaisonInstitutionManager) {

    super(config);
    this.deliverableManager = deliverableManager;
    this.crpManager = crpManager;
    this.liaisonUserManager = liaisonUserManager;
    this.deliverableInfoManager = deliverableInfoManager;
    this.deliverableLeaderManager = deliverableLeaderManager;
    this.institutionManager = institutionManager;
    this.phaseManager = phaseManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
  }


  @Override
  public String add() {
    String params[] = {loggedCrp.getAcronym()};
    if (this.hasPermission(this.generatePermission(Permission.PUBLICATION_ADD, params))) {
      Deliverable deliverable = new Deliverable();

      deliverable.setCrp(loggedCrp);
      deliverable.setCreateDate(new Date());
      deliverable.setIsPublication(true);
      deliverable.setPhase(this.getActualPhase());

      deliverable = deliverableManager.saveDeliverable(deliverable);
      deliverableID = deliverable.getId();

      List<LiaisonInstitution> liaisonInstitutions =
        liaisonInstitutionManager.getLiaisonInstitutionByUserId(this.getCurrentUser().getId(), loggedCrp.getId());
      Set<Institution> institutions = new HashSet<Institution>();
      for (LiaisonInstitution liaisonInstitution : liaisonInstitutions) {
        if (liaisonInstitution.getInstitution() != null) {
          institutions.add(liaisonInstitution.getInstitution());
        }
      }

      Phase phase = this.getActualPhase();
      boolean hasNext = phase.getNext() != null;
      this.saveDeliverableInfo(deliverable, phase);
      this.saveDeliverableLeaders(deliverable, institutions, phase);
      this.clearPermissionsCache();

      if (deliverableID > 0) {
        return SUCCESS;
      }
      return INPUT;
    } else {
      return NOT_AUTHORIZED;
    }

  }

  public boolean canEdit(long deliverableID) {
    String crpAcronymParam[] = {loggedCrp.getAcronym()};
    String publicationParams[] = {loggedCrp.getAcronym(), deliverableID + ""};
    boolean hasPublicationFullPermission =
      this.hasPermission(this.generatePermission(Permission.PUBLICATION_FULL_PERMISSION, crpAcronymParam));
    boolean hasPublicationPermission =
      this.hasPermission(this.generatePermission(Permission.PUBLICATION_PERMISSION, publicationParams));
    return hasPublicationFullPermission || hasPublicationPermission;
  }

  @Override
  public String delete() {
    Map<String, Parameter> parameters = this.getParameters();
    deliverableID = Long
      .parseLong(StringUtils.trim(parameters.get(APConstants.PROJECT_DELIVERABLE_REQUEST_ID).getMultipleValues()[0]));

    Deliverable deliverable = deliverableManager.getDeliverableById(deliverableID);

    if (deliverable != null) {
      for (DeliverableInfo deliverableInfo : deliverable.getDeliverableInfos()) {
        deliverableInfo.setModificationJustification(this.getJustification());
        deliverableInfoManager.saveDeliverableInfo(deliverableInfo);
      }
      deliverableManager.deleteDeliverable(deliverableID);
      this.addActionMessage("message:" + this.getText("deleting.success"));
    }
    return SUCCESS;
  }

  public long getDeliverableID() {
    return deliverableID;
  }

  @Override
  public String getJustification() {
    return justification;
  }

  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }


  public List<Deliverable> getPublications(boolean hasPermission) {
    List<Deliverable> deliverables = new ArrayList<Deliverable>();
    for (Deliverable deliverable : loggedCrp.getDeliverablesList()) {
      boolean isCreator = this.getCurrentUser().getId().equals(deliverable.getCreatedBy().getId());
      if ((isCreator && hasPermission) || this.canEdit(deliverable.getId().longValue()) == hasPermission) {
        deliverable.getDeliverableInfo(deliverable.getPhase());
        deliverables.add(deliverable);
      }
    }
    return deliverables;
  }


  @Override
  public void prepare() throws Exception {
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());
    try {
      loggedCrp.setDeliverablesList(loggedCrp.getDeliverables().stream()
        .filter(c -> c.getIsPublication() != null && c.getIsPublication().booleanValue() && c.isActive()
          && c.getDeliverableInfo(this.getActualPhase()) != null
          && c.getDeliverableInfo().getYear() == this.getActualPhase().getYear())
        .collect(Collectors.toList()));
      for (Deliverable deliverable : loggedCrp.getDeliverablesList()) {
        deliverable.setLeaders(deliverable.getDeliverableLeaders().stream()
          .filter(dl -> dl.getPhase() != null && dl.getPhase().equals(this.getActualPhase()))
          .sorted((l1, l2) -> l1.getInstitution().getName().compareTo(l2.getInstitution().getName()))
          .collect(Collectors.toList()));
        List<DeliverableProgram> deliverablePrograms = deliverable.getDeliverablePrograms().stream()
          .filter(c -> c.getPhase() != null && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList());

        deliverable.setPrograms(deliverablePrograms.stream()
          .filter(c -> c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
          .sorted((f1, f2) -> f1.getCrpProgram().getAcronym().compareTo(f2.getCrpProgram().getAcronym()))
          .collect(Collectors.toList()));
        deliverable.setRegions(deliverablePrograms.stream()
          .filter(c -> c.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
          .sorted((r1, r2) -> r1.getCrpProgram().getAcronym().compareTo(r2.getCrpProgram().getAcronym()))
          .collect(Collectors.toList()));
      }
    } catch (Exception e) {
      LOG.error("unable to update publication", e);
      /**
       * Original code swallows the exception and didn't even log it. Now we at least log it,
       * but we need to revisit to see if we should continue processing or re-throw the exception.
       */
    }

  }

  private void saveDeliverableInfo(Deliverable deliverable, Phase phase) {
    DeliverableInfo deliverableInfo = new DeliverableInfo();
    deliverableInfo.setYear(this.getCurrentCycleYear());
    deliverableInfo.setDeliverable(deliverable);
    deliverableInfo.setPhase(phase);
    deliverableInfo.setModificationJustification("New publication created");
    deliverableInfoManager.saveDeliverableInfo(deliverableInfo);
  }

  private void saveDeliverableLeaders(Deliverable deliverable, Set<Institution> institutionLeaders, Phase phase) {
    if (institutionLeaders != null && !institutionLeaders.isEmpty()) {
      for (Institution institution : institutionLeaders) {
        try {
          DeliverableLeader deliverableLeader = new DeliverableLeader();
          deliverableLeader.setDeliverable(deliverable);
          deliverableLeader.setPhase(phase);
          deliverableLeader.setInstitution(institution);
          deliverableLeaderManager.saveDeliverableLeader(deliverableLeader);
        } catch (Exception e) {
          LOG.error("unable to save deliverableLeader", e);
          /**
           * Original code swallows the exception and didn't even log it. Now we at least log it,
           * but we need to revisit to see if we should continue processing or re-throw the exception.
           */
        }
      }
    }
  }

  public void setDeliverableID(long deliverableID) {
    this.deliverableID = deliverableID;
  }


  @Override
  public void setJustification(String justification) {
    this.justification = justification;
  }


  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

}
