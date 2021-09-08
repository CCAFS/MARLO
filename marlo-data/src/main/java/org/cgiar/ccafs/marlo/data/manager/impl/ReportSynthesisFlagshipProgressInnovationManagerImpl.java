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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisFlagshipProgressInnovationDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressInnovationManager;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgress;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressInnovation;
import org.cgiar.ccafs.marlo.data.model.User;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisFlagshipProgressInnovationManagerImpl
  implements ReportSynthesisFlagshipProgressInnovationManager {


  private ReportSynthesisFlagshipProgressInnovationDAO reportSynthesisFlagshipProgressInnovationDAO;
  // Managers


  @Inject
  public ReportSynthesisFlagshipProgressInnovationManagerImpl(
    ReportSynthesisFlagshipProgressInnovationDAO reportSynthesisFlagshipProgressInnovationDAO) {
    this.reportSynthesisFlagshipProgressInnovationDAO = reportSynthesisFlagshipProgressInnovationDAO;


  }

  @Override
  public void deleteReportSynthesisFlagshipProgressInnovation(long reportSynthesisFlagshipProgressInnovationId) {

    reportSynthesisFlagshipProgressInnovationDAO
      .deleteReportSynthesisFlagshipProgressInnovation(reportSynthesisFlagshipProgressInnovationId);
  }

  @Override
  public boolean existReportSynthesisFlagshipProgressInnovation(long reportSynthesisFlagshipProgressInnovationID) {

    return reportSynthesisFlagshipProgressInnovationDAO
      .existReportSynthesisFlagshipProgressInnovation(reportSynthesisFlagshipProgressInnovationID);
  }

  @Override
  public List<ReportSynthesisFlagshipProgressInnovation> findAll() {

    return reportSynthesisFlagshipProgressInnovationDAO.findAll();

  }

  @Override
  public ReportSynthesisFlagshipProgressInnovation
    getReportSynthesisFlagshipProgressInnovationById(long reportSynthesisFlagshipProgressInnovationID) {

    return reportSynthesisFlagshipProgressInnovationDAO.find(reportSynthesisFlagshipProgressInnovationID);
  }

  @Override
  public ReportSynthesisFlagshipProgressInnovation
    getReportSynthesisFlagshipProgressInnovationByInnovationAndFlagshipProgress(Long innovationId,
      Long flagshipProgressId) {
    return this.reportSynthesisFlagshipProgressInnovationDAO
      .getReportSynthesisFlagshipProgressInnovationByInnovationAndFlagshipProgress(innovationId.longValue(),
        flagshipProgressId.longValue());
  }

  @Override
  public ReportSynthesisFlagshipProgressInnovation saveReportSynthesisFlagshipProgressInnovation(
    ReportSynthesisFlagshipProgressInnovation reportSynthesisFlagshipProgressInnovation) {

    return reportSynthesisFlagshipProgressInnovationDAO.save(reportSynthesisFlagshipProgressInnovation);
  }

  @Override
  public ReportSynthesisFlagshipProgressInnovation toAnnualReport(ProjectInnovation projectInnovation,
    ReportSynthesisFlagshipProgress flagshipProgress, User user, boolean remove) {
    ReportSynthesisFlagshipProgressInnovation progressInnovation = null;

    if (projectInnovation != null && projectInnovation.getId() != null && flagshipProgress != null
      && flagshipProgress.getId() != null && user != null && user.getId() != null) {

      progressInnovation = this.getReportSynthesisFlagshipProgressInnovationByInnovationAndFlagshipProgress(
        projectInnovation.getId(), flagshipProgress.getId());

      if (progressInnovation == null) {
        // is not added to ar
        if (remove) {
          // we need to add it to ar. if we do not need to add it to ar, there is no need to create one.
          progressInnovation = new ReportSynthesisFlagshipProgressInnovation();
          progressInnovation.setCreatedBy(user);
          progressInnovation.setReportSynthesisFlagshipProgress(flagshipProgress);
          progressInnovation.setProjectInnovation(projectInnovation);
        }
      }

      if (progressInnovation != null) {
        progressInnovation.setModifiedBy(user);
        progressInnovation.setActive(remove);
      }

      progressInnovation = this.saveReportSynthesisFlagshipProgressInnovation(progressInnovation);
    }

    return progressInnovation;
  }

  @Override
  public ReportSynthesisFlagshipProgressInnovation
    toAnnualReport(ReportSynthesisFlagshipProgressInnovation progressInnovation, boolean remove) {
    if (progressInnovation != null && progressInnovation.getId() != null) {
      progressInnovation.setActive(remove);
      progressInnovation = this.saveReportSynthesisFlagshipProgressInnovation(progressInnovation);
    }

    return progressInnovation;
  }
}
