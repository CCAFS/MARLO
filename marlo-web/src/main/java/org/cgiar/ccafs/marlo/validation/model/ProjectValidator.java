/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.validation.model;

import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;


/**
 * @author Christian Garcia
 */
@Named
public class ProjectValidator extends BaseValidator {

  private final InstitutionManager institutionManager;

  @Inject
  public ProjectValidator(InstitutionManager institutionManager) {
    this.institutionManager = institutionManager;
  }


  public boolean isValidLeader(ProjectPartner leader) {
    if (leader == null) {
      return false;
    }
    if (leader.getInstitution() != null) {


      leader.setInstitution(institutionManager.getInstitutionById(leader.getInstitution().getId()));
      // For bilateral projects the leader must be a PPA institution

      return true;
    }
    return false;

  }

  public boolean isValidLeaderResponsabilities(String leaderResponsabilities) {
    return (this.isValidString(leaderResponsabilities)) ? true : false;
  }

  public boolean isValidLiaisonInstitution(LiaisonInstitution liaisonInstitution) {
    return (liaisonInstitution != null) ? true : false;
  }

  public boolean isValidOwner(User owner) {
    if (owner != null) {
      return true;
    }
    return false;
  }

  public boolean isValidPersonResponsibilities(String responsibilities) {
    return (this.isValidString(responsibilities) && this.wordCount(responsibilities) <= 100);
  }

  public boolean isValidPPAPartners(List<ProjectPartner> ppaPartners) {
    return false;
  }

  public boolean isValidProjectWorkplanName(String workplanName) {
    return (this.isValidString(workplanName)) ? true : false;
  }


  public boolean isValidStartDate(Date startDate) {
    return (startDate != null) ? true : false;
  }

  public boolean isValidStatus(String status) {
    return (this.isValidString(status)) ? true : false;
  }

  public boolean isValidSummary(String summary) {
    return (this.isValidString(summary) && this.wordCount(summary) <= 150) ? true : false;
  }

  public boolean isValidTargetNarrative(String targetNarrative) {
    return (this.isValidString(targetNarrative) && this.wordCount(targetNarrative) <= 100);
  }

  public boolean isValidTargetValueNull(String targetValue) {
    return (this.isValidString(targetValue));
  }

  public boolean isValidTargetValueNumber(String targetValue) {
    return (this.isValidNumber(targetValue));
  }

  public boolean isValidTitle(String title) {
    return (this.isValidString(title) && this.wordCount(title) <= 20) ? true : false;
  }


}
