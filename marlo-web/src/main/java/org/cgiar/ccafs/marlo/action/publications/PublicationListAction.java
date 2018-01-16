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
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableInfoManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableLeaderManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonUserManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.DeliverableLeader;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
  private Crp loggedCrp;
  private long deliverableID;
  private DeliverableInfoManager deliverableInfoManager;
  private CrpManager crpManager;
  private DeliverableManager deliverableManager;
  private LiaisonUserManager liaisonUserManager;
  private InstitutionManager institutionManager;
  private DeliverableLeaderManager deliverableLeaderManager;

  @Inject
  public PublicationListAction(APConfig config, CrpManager crpManager, DeliverableManager deliverableManager,
    InstitutionManager institutionManager, LiaisonUserManager liaisonUserManager,
    DeliverableInfoManager deliverableInfoManager, DeliverableLeaderManager deliverableLeaderManager) {

    super(config);
    this.deliverableManager = deliverableManager;
    this.crpManager = crpManager;
    this.liaisonUserManager = liaisonUserManager;
    this.deliverableInfoManager = deliverableInfoManager;
    this.deliverableLeaderManager = deliverableLeaderManager;
    this.institutionManager = institutionManager;
  }


  @Override
  public String add() {
    String params[] = {loggedCrp.getAcronym()};
    if (this.hasPermission(this.generatePermission(Permission.PUBLICATION_ADD, params))) {
      Deliverable deliverable = new Deliverable();

      deliverable.setCreatedBy(this.getCurrentUser());
      deliverable.setActive(true);
      deliverable.setActiveSince(new Date());
      deliverable.setCrp(loggedCrp);
      deliverable.setCreateDate(new Date());
      deliverable.setIsPublication(true);
      deliverable.setPhase(this.getActualPhase());

      deliverable = deliverableManager.saveDeliverable(deliverable);
      deliverableID = deliverable.getId();

      deliverableID = deliverableManager.saveDeliverable(deliverable).getId();
      DeliverableInfo deliverableInfo = new DeliverableInfo();
      deliverableInfo.setYear(this.getCurrentCycleYear());
      deliverableInfo.setModifiedBy(this.getCurrentUser());
      deliverableInfo.setDeliverable(deliverable);
      deliverableInfo.setPhase(this.getActualPhase());
      deliverableInfo.setModificationJustification("New publication created");
      deliverableInfoManager.saveDeliverableInfo(deliverableInfo);
      LiaisonUser user = liaisonUserManager.getLiaisonUserByUserId(this.getCurrentUser().getId(), loggedCrp.getId());
      if (user != null) {
        LiaisonInstitution liaisonInstitution = user.getLiaisonInstitution();
        try {
          if (liaisonInstitution != null && liaisonInstitution.getInstitution() != null) {
            Institution institution =
              institutionManager.getInstitutionById(liaisonInstitution.getInstitution().getId());

            DeliverableLeader deliverableLeader = new DeliverableLeader();
            deliverableLeader.setDeliverable(deliverable);

            deliverableLeader.setInstitution(institution);
            deliverableLeaderManager.saveDeliverableLeader(deliverableLeader);
          }
        } catch (Exception e) {
          LOG.error("unable to save deliverableLeader", e);
          /**
           * Original code swallows the exception and didn't even log it. Now we at least log it,
           * but we need to revisit to see if we should continue processing or re-throw the exception.
           */
        }


      }
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
    String params[] = {loggedCrp.getAcronym()};
    String paramDeliverableID[] = {loggedCrp.getAcronym(), deliverableID + ""};
    return this.hasPermission(this.generatePermission(Permission.PUBLICATION_FULL_PERMISSION, params))
      || this.hasPermission(this.generatePermission(Permission.PUBLICATION_INSTITUTION, paramDeliverableID));
  }

  @Override
  public String delete() {

    // Map<String, Object> parameters = this.getParameters();
    Map<String, Parameter> parameters = this.getParameters();
    deliverableID =
      // Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.PROJECT_DELIVERABLE_REQUEST_ID))[0]));
      Long
        .parseLong(StringUtils.trim(parameters.get(APConstants.PROJECT_DELIVERABLE_REQUEST_ID).getMultipleValues()[0]));


    Deliverable deliverable = deliverableManager.getDeliverableById(deliverableID);


    if (deliverable != null) {
      deliverableManager.deleteDeliverable(deliverableID);
      this.addActionMessage("message:" + this.getText("deleting.success"));
    }
    return SUCCESS;


  }

  public long getDeliverableID() {
    return deliverableID;
  }

  public Crp getLoggedCrp() {
    return loggedCrp;
  }


  public List<Deliverable> getPublications(boolean permission) {

    List<Deliverable> deliverables = new ArrayList<Deliverable>();

    for (Deliverable deliverable : loggedCrp.getDeliverablesList()) {
      if (this.canEdit(deliverable.getId().longValue()) == permission) {
        deliverables.add(deliverable);
      }
    }
    return deliverables;
  }


  @Override
  public void prepare() throws Exception {
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());
    try {

      loggedCrp.setDeliverablesList(loggedCrp.getDeliverables().stream()
        .filter(c -> c.getIsPublication() != null && c.getIsPublication().booleanValue() && c.isActive())
        .collect(Collectors.toList()));
      for (Deliverable deliverable : loggedCrp.getDeliverablesList()) {
        deliverable.setLeaders(deliverable.getDeliverableLeaders().stream().collect(Collectors.toList()));
        deliverable.setPrograms(deliverable.getDeliverablePrograms().stream()
          .filter(c -> c.getIpProgram().getIpProgramType().getId().intValue() == 4).collect(Collectors.toList()));
        deliverable.setRegions(deliverable.getDeliverablePrograms().stream()
          .filter(c -> c.getIpProgram().getIpProgramType().getId().intValue() == 5).collect(Collectors.toList()));
      }
    } catch (Exception e) {
      LOG.error("unable to update deliverable", e);
      /**
       * Original code swallows the exception and didn't even log it. Now we at least log it,
       * but we need to revisit to see if we should continue processing or re-throw the exception.
       */
    }

  }


  public void setDeliverableID(long deliverableID) {
    this.deliverableID = deliverableID;
  }

  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }
}
