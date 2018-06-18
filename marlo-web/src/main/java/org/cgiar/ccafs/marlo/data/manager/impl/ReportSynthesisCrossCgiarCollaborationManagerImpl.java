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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisCrossCgiarCollaborationDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrossCgiarCollaborationManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCgiarCollaboration;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisCrossCgiarCollaborationManagerImpl implements ReportSynthesisCrossCgiarCollaborationManager {


  private ReportSynthesisCrossCgiarCollaborationDAO reportSynthesisCrossCgiarCollaborationDAO;
  // Managers


  @Inject
  public ReportSynthesisCrossCgiarCollaborationManagerImpl(ReportSynthesisCrossCgiarCollaborationDAO reportSynthesisCrossCgiarCollaborationDAO) {
    this.reportSynthesisCrossCgiarCollaborationDAO = reportSynthesisCrossCgiarCollaborationDAO;


  }

  @Override
  public void deleteReportSynthesisCrossCgiarCollaboration(long reportSynthesisCrossCgiarCollaborationId) {

    reportSynthesisCrossCgiarCollaborationDAO.deleteReportSynthesisCrossCgiarCollaboration(reportSynthesisCrossCgiarCollaborationId);
  }

  @Override
  public boolean existReportSynthesisCrossCgiarCollaboration(long reportSynthesisCrossCgiarCollaborationID) {

    return reportSynthesisCrossCgiarCollaborationDAO.existReportSynthesisCrossCgiarCollaboration(reportSynthesisCrossCgiarCollaborationID);
  }

  @Override
  public List<ReportSynthesisCrossCgiarCollaboration> findAll() {

    return reportSynthesisCrossCgiarCollaborationDAO.findAll();

  }

  @Override
  public ReportSynthesisCrossCgiarCollaboration getReportSynthesisCrossCgiarCollaborationById(long reportSynthesisCrossCgiarCollaborationID) {

    return reportSynthesisCrossCgiarCollaborationDAO.find(reportSynthesisCrossCgiarCollaborationID);
  }

  @Override
  public ReportSynthesisCrossCgiarCollaboration saveReportSynthesisCrossCgiarCollaboration(ReportSynthesisCrossCgiarCollaboration reportSynthesisCrossCgiarCollaboration) {

    return reportSynthesisCrossCgiarCollaborationDAO.save(reportSynthesisCrossCgiarCollaboration);
  }


}
