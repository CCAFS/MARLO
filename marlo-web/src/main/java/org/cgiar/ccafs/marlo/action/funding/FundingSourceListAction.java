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
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
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


  private CrpManager crpManager;
  private ProjectManager projectManager;

  private long fundingSourceID;


  private String justification;


  @Inject
  public FundingSourceListAction(APConfig config, FundingSourceManager fundingSourceManager, CrpManager crpManager,
    ProjectManager projectManager) {
    super(config);
    this.fundingSourceManager = fundingSourceManager;
    this.crpManager = crpManager;
    this.projectManager = projectManager;
  }

  @Override
  public String add() {
    FundingSource fundingSource = new FundingSource();
    fundingSource.setCreatedBy(this.getCurrentUser());
    fundingSource.setModifiedBy(this.getCurrentUser());
    fundingSource.setModificationJustification("New expected project bilateral cofunded created");
    fundingSource.setActive(true);
    fundingSource.setActiveSince(new Date());
    // project.setCrp(loggedCrp);

    fundingSourceID = fundingSourceManager.saveFundingSource(fundingSource);

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
      this
        .addActionMessage(this.getText("deleting.success", new String[] {this.getText("fundingSource").toLowerCase()}));
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

      if (this.canAccessSuperAdmin() || this.canAcessCrpAdmin()) {
        myProjects = loggedCrp.getFundingSources().stream().filter(p -> p.isActive()).collect(Collectors.toList());
        myProjects
          .addAll(fundingSourceManager.findAll().stream().filter(c -> c.getCrp() == null).collect(Collectors.toList()));
      } else {
        allProjects = loggedCrp.getFundingSources().stream().filter(p -> p.isActive()).collect(Collectors.toList());
        allProjects
          .addAll(fundingSourceManager.findAll().stream().filter(c -> c.getCrp() == null).collect(Collectors.toList()));
        // myProjects = projectManager.getUserProjects(this.getCurrentUser().getId(), loggedCrp.getAcronym());
        // Collections.sort(myProjects, (p1, p2) -> p1.getId().compareTo(p2.getId()));
        myProjects = allProjects;
        // allProjects.removeAll(myProjects);
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