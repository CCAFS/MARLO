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


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisCrossCgiarCollaborationDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCgiarCollaboration;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisCrossCgiarCollaborationMySQLDAO extends AbstractMarloDAO<ReportSynthesisCrossCgiarCollaboration, Long> implements ReportSynthesisCrossCgiarCollaborationDAO {


  @Inject
  public ReportSynthesisCrossCgiarCollaborationMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisCrossCgiarCollaboration(long reportSynthesisCrossCgiarCollaborationId) {
    ReportSynthesisCrossCgiarCollaboration reportSynthesisCrossCgiarCollaboration = this.find(reportSynthesisCrossCgiarCollaborationId);
    reportSynthesisCrossCgiarCollaboration.setActive(false);
    this.update(reportSynthesisCrossCgiarCollaboration);
  }

  @Override
  public boolean existReportSynthesisCrossCgiarCollaboration(long reportSynthesisCrossCgiarCollaborationID) {
    ReportSynthesisCrossCgiarCollaboration reportSynthesisCrossCgiarCollaboration = this.find(reportSynthesisCrossCgiarCollaborationID);
    if (reportSynthesisCrossCgiarCollaboration == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisCrossCgiarCollaboration find(long id) {
    return super.find(ReportSynthesisCrossCgiarCollaboration.class, id);

  }

  @Override
  public List<ReportSynthesisCrossCgiarCollaboration> findAll() {
    String query = "from " + ReportSynthesisCrossCgiarCollaboration.class.getName() + " where is_active=1";
    List<ReportSynthesisCrossCgiarCollaboration> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisCrossCgiarCollaboration save(ReportSynthesisCrossCgiarCollaboration reportSynthesisCrossCgiarCollaboration) {
    if (reportSynthesisCrossCgiarCollaboration.getId() == null) {
      super.saveEntity(reportSynthesisCrossCgiarCollaboration);
    } else {
      reportSynthesisCrossCgiarCollaboration = super.update(reportSynthesisCrossCgiarCollaboration);
    }


    return reportSynthesisCrossCgiarCollaboration;
  }


}