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

package org.cgiar.ccafs.marlo.action.crp.admin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.CrpPpaPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonUserManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpPpaPartner;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.inject.Inject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class CrpPpaPartnersAction extends BaseAction {


  private static final long serialVersionUID = -8561096521514225205L;

  // Managers
  private InstitutionManager institutionManager;
  private CrpManager crpManager;
  private CrpPpaPartnerManager crpPpaPartnerManager;
  private LiaisonUserManager liaisonUserManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;

  // Variables
  private List<Institution> institutions;
  private List<Institution> crpInstitutions;
  private Crp loggedCrp;

  @Inject
  public CrpPpaPartnersAction(APConfig config, InstitutionManager institutionManager, CrpManager crpManager,
    CrpPpaPartnerManager crpPpaPartnerManager, LiaisonUserManager liaisonUserManager,
    LiaisonInstitutionManager liaisonInstitutionManager) {
    super(config);
    this.institutionManager = institutionManager;
    this.crpManager = crpManager;
    this.crpPpaPartnerManager = crpPpaPartnerManager;
    this.liaisonUserManager = liaisonUserManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
  }

  public List<Institution> getCrpInstitutions() {
    return crpInstitutions;
  }

  public List<Institution> getInstitutions() {
    return institutions;
  }

  public Crp getLoggedCrp() {
    return loggedCrp;
  }


  @Override
  public void prepare() throws Exception {
    super.prepare();
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());
    String params[] = {loggedCrp.getAcronym()};

    if (loggedCrp.getCrpPpaPartners() != null) {
      loggedCrp.setCrpInstitutionsPartners(new ArrayList<CrpPpaPartner>(
        loggedCrp.getCrpPpaPartners().stream().filter(ppa -> ppa.isActive()).collect(Collectors.toList())));
      loggedCrp.getCrpInstitutionsPartners()
        .sort((p1, p2) -> p1.getInstitution().getName().compareTo(p2.getInstitution().getName()));
      // Fill Managing/PPA Partners with contact persons
      Set<CrpPpaPartner> crpPpaPartners = new HashSet<CrpPpaPartner>(0);
      for (CrpPpaPartner crpPpaPartner : loggedCrp.getCrpInstitutionsPartners()) {
        LiaisonInstitution liaisonInstitution = liaisonInstitutionManager
          .getLiasonInstitutionByInstitutionId(crpPpaPartner.getInstitution().getId(), loggedCrp.getId());
        if (liaisonInstitution != null && liaisonInstitution.isActive()) {
          crpPpaPartner.setContactPoints(liaisonInstitution.getLiaisonUsers().stream()
            .filter(lu -> lu.isActive() && lu.getUser() != null && lu.getUser().isActive() && lu.getCrp() != null
              && lu.getCrp().equals(loggedCrp))
            .sorted((lu1, lu2) -> lu1.getUser().getLastName().compareTo(lu2.getUser().getLastName()))
            .collect(Collectors.toList()));
        }
        crpPpaPartners.add(crpPpaPartner);
      }
      loggedCrp.setCrpPpaPartners(crpPpaPartners);
    }
    institutions = institutionManager.findAll().stream().filter(c -> c.isActive()).collect(Collectors.toList());
    institutions.sort((i1, i2) -> i1.getName().compareTo(i2.getName()));


    this.setBasePermission(this.getText(Permission.CRP_ADMIN_BASE_PERMISSION, params));
    if (this.isHttpPost()) {
      loggedCrp.getCrpInstitutionsPartners().clear();
    }
  }

  @Override
  public String save() {
    if (this.hasPermission("*")) {
      /* Test */
      for (CrpPpaPartner crpPpaPartner : loggedCrp.getCrpInstitutionsPartners()) {
        CrpPpaPartner crpPpaPartner1 = crpPpaPartnerManager.getCrpPpaPartnerById(crpPpaPartner.getId());
        System.out.println(crpPpaPartner1.getInstitution().getComposedName());
        if (crpPpaPartner.getContactPoints() != null) {
          for (LiaisonUser liaisonUser : crpPpaPartner.getContactPoints()) {
            System.out.println("Contact Person: " + liaisonUser.getUser().getId());
          }
        } else {
          System.out.println("No Contact Person");
        }
      }
      Set<CrpPpaPartner> crpPpaPartners = new HashSet<CrpPpaPartner>(0);
      for (CrpPpaPartner crpPpaPartner : loggedCrp.getCrpInstitutionsPartners()) {
        LiaisonInstitution liaisonInstitution = liaisonInstitutionManager
          .getLiasonInstitutionByInstitutionId(crpPpaPartner.getInstitution().getId(), loggedCrp.getId());
        if (liaisonInstitution != null && liaisonInstitution.isActive()) {
          crpPpaPartner.setContactPoints(liaisonInstitution.getLiaisonUsers().stream()
            .filter(lu -> lu.isActive() && lu.getUser() != null && lu.getUser().isActive() && lu.getCrp() != null
              && lu.getCrp().equals(loggedCrp))
            .sorted((lu1, lu2) -> lu1.getUser().getLastName().compareTo(lu2.getUser().getLastName()))
            .collect(Collectors.toList()));
        }
        crpPpaPartners.add(crpPpaPartner);
      }
      loggedCrp.setCrpPpaPartners(crpPpaPartners);
      for (CrpPpaPartner crpPpaPartner : loggedCrp.getCrpInstitutionsPartners()) {
        CrpPpaPartner crpPpaPartner1 = crpPpaPartnerManager.getCrpPpaPartnerById(crpPpaPartner.getId());
        System.out.println("2 " + crpPpaPartner1.getInstitution().getComposedName());
        if (crpPpaPartner.getContactPoints() != null) {
          for (LiaisonUser liaisonUser : crpPpaPartner.getContactPoints()) {
            System.out.println("2 Contact Person: " + liaisonUser.getUser().getId());
          }
        } else {
          System.out.println("2 No Contact Person");
        }
      }
      /* End Test */
      List<CrpPpaPartner> ppaPartnerReview;

      if (crpPpaPartnerManager.findAll() != null) {
        ppaPartnerReview = crpPpaPartnerManager.findAll();

        for (CrpPpaPartner partner : ppaPartnerReview.stream().filter(ppa -> ppa.getCrp().equals(loggedCrp))
          .collect(Collectors.toList())) {
          if (!loggedCrp.getCrpInstitutionsPartners().contains(partner)) {
            crpPpaPartnerManager.deleteCrpPpaPartner(partner.getId());
          }
        }
      }


      for (CrpPpaPartner partner : loggedCrp.getCrpInstitutionsPartners()) {
        if (partner.getId() == null) {
          partner.setCrp(loggedCrp);
          partner.setInstitution(institutionManager.getInstitutionById(partner.getInstitution().getId()));
          partner.setActive(true);
          partner.setCreatedBy(this.getCurrentUser());
          partner.setModifiedBy(this.getCurrentUser());
          partner.setModificationJustification("");
          partner.setActiveSince(new Date());
          crpPpaPartnerManager.saveCrpPpaPartner(partner);
        }
      }

      if (loggedCrp.getCrpPpaPartners() != null) {
        loggedCrp.setCrpInstitutionsPartners(new ArrayList<CrpPpaPartner>(
          loggedCrp.getCrpPpaPartners().stream().filter(ppa -> ppa.isActive()).collect(Collectors.toList())));
      }

      Collection<String> messages = this.getActionMessages();
      if (!messages.isEmpty()) {
        String validationMessage = messages.iterator().next();
        this.setActionMessages(null);
        this.addActionWarning(this.getText("saving.saved") + validationMessage);
      } else {
        this.addActionMessage("message:" + this.getText("saving.saved"));
      }
      messages = this.getActionMessages();
      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }
  }

  public void setCrpInstitutions(List<Institution> crpInstitutions) {
    this.crpInstitutions = crpInstitutions;
  }

  public void setInstitutions(List<Institution> institutions) {
    this.institutions = institutions;
  }

  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

}
