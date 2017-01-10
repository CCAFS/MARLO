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


package org.cgiar.ccafs.marlo.action.funding;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonUserManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.FundingSourceInstitution;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.Role;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

public class FundingSourceListAction extends BaseAction {


  private static final long serialVersionUID = -8858893084495492581L;


  private Crp loggedCrp;

  private List<FundingSource> myProjects;


  private List<FundingSource> allProjects;

  private FundingSourceManager fundingSourceManager;

  private FundingSourceInstitutionManager fundingSourceInstitutionManager;
  private CrpManager crpManager;
  private RoleManager roleManager;
  private UserManager userManager;
  private LiaisonUserManager liaisonUserManager;
  private InstitutionManager institutionManager;
  private ProjectManager projectManager;

  private long fundingSourceID;


  private String justification;


  @Inject
  public FundingSourceListAction(APConfig config, RoleManager roleManager, FundingSourceManager fundingSourceManager,
    CrpManager crpManager, ProjectManager projectManager, LiaisonUserManager liaisonUserManager,
    InstitutionManager institutionManager, FundingSourceInstitutionManager fundingSourceInstitutionManager) {
    super(config);
    this.fundingSourceManager = fundingSourceManager;
    this.crpManager = crpManager;
    this.roleManager = roleManager;
    this.liaisonUserManager = liaisonUserManager;
    this.fundingSourceInstitutionManager = fundingSourceInstitutionManager;
    this.projectManager = projectManager;
    this.institutionManager = institutionManager;
  }

  @Override
  public String add() {
    FundingSource fundingSource = new FundingSource();
    fundingSource.setCreatedBy(this.getCurrentUser());
    fundingSource.setModifiedBy(this.getCurrentUser());
    fundingSource.setModificationJustification("New expected project bilateral cofunded created");
    fundingSource.setActive(true);
    fundingSource.setActiveSince(new Date());
    fundingSource.setCrp(loggedCrp);
    fundingSource.setCenterType(1);

    // project.setCrp(loggedCrp);

    fundingSourceID = fundingSourceManager.saveFundingSource(fundingSource);


    LiaisonUser user = liaisonUserManager.getLiaisonUserByUserId(this.getCurrentUser().getId(), loggedCrp.getId());
    if (user != null) {
      LiaisonInstitution liaisonInstitution = user.getLiaisonInstitution();
      try {
        if (liaisonInstitution != null && liaisonInstitution.getInstitution() != null) {
          Institution institution = institutionManager.getInstitutionById(liaisonInstitution.getInstitution().getId());

          FundingSourceInstitution fundingSourceInstitution = new FundingSourceInstitution();
          fundingSourceInstitution.setFundingSource(fundingSource);

          fundingSourceInstitution.setInstitution(institution);
          fundingSourceInstitutionManager.saveFundingSourceInstitution(fundingSourceInstitution);
        }
      } catch (Exception e) {

      }


    }
    this.clearPermissionsCache();
    if (fundingSourceID > 0) {
      return SUCCESS;
    }

    return INPUT;
  }


  @Override
  public String delete() {
    FundingSource fundingSource = fundingSourceManager.getFundingSourceById(fundingSourceID);
    System.out.println("fundign id" + fundingSourceID);
    fundingSource.setModifiedBy(this.getCurrentUser());
    fundingSource.setModificationJustification(justification);

    fundingSourceManager.saveFundingSource(fundingSource);


    if (fundingSourceManager.deleteFundingSource(fundingSource.getId())) {
      this.addActionMessage(
        "message:" + this.getText("deleting.success", new String[] {this.getText("fundingSource").toLowerCase()}));
    } else {
      this.addActionError(this.getText("deleting.problem", new String[] {this.getText("fundingSource").toLowerCase()}));
    }

    return SUCCESS;
  }

  public List<FundingSource> getAllProjects() {
    return allProjects;
  }

  public long getFundingSourceID() {
    return fundingSourceID;
  }

  @Override
  public String getJustification() {
    return justification;
  }

  public Crp getLoggedCrp() {
    return loggedCrp;
  }

  public List<FundingSource> getMyProjects() {
    return myProjects;
  }

  @Override
  public void prepare() throws Exception {

    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());

    if (fundingSourceManager.findAll() != null) {

      Role role = roleManager.getRoleById(Long.parseLong(this.getSession().get(APConstants.CRP_PMU_ROLE).toString()));
      boolean isPMU = !role.getUserRoles().stream()
        .filter(c -> c.getUser().getId().longValue() == this.getCurrentUser().getId().longValue())
        .collect(Collectors.toList()).isEmpty();
      if (this.canAccessSuperAdmin() || this.canAcessCrpAdmin() || isPMU

      ) {
        myProjects = loggedCrp.getFundingSources().stream().filter(p -> p.isActive()).collect(Collectors.toList());
        myProjects
          .addAll(fundingSourceManager.findAll().stream().filter(c -> c.getCrp() == null).collect(Collectors.toList()));
      } else {
        allProjects = loggedCrp.getFundingSources().stream().filter(p -> p.isActive()).collect(Collectors.toList());

        myProjects = fundingSourceManager.getFundingSource(this.getCurrentUser().getId(), loggedCrp.getAcronym());


        allProjects.removeAll(myProjects);
      }
    }

  }

  public void setAllProjects(List<FundingSource> allProjects) {
    this.allProjects = allProjects;
  }


  public void setFundingSourceID(long projectID) {
    this.fundingSourceID = projectID;
  }

  @Override
  public void setJustification(String justification) {
    this.justification = justification;
  }

  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setMyProjects(List<FundingSource> myProjects) {
    this.myProjects = myProjects;
  }

}