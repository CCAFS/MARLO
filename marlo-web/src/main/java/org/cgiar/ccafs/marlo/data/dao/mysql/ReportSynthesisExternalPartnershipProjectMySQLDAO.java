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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisExternalPartnershipProjectDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisExternalPartnershipProject;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisExternalPartnershipProjectMySQLDAO extends AbstractMarloDAO<ReportSynthesisExternalPartnershipProject, Long> implements ReportSynthesisExternalPartnershipProjectDAO {


  @Inject
  public ReportSynthesisExternalPartnershipProjectMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisExternalPartnershipProject(long reportSynthesisExternalPartnershipProjectId) {
    ReportSynthesisExternalPartnershipProject reportSynthesisExternalPartnershipProject = this.find(reportSynthesisExternalPartnershipProjectId);
    reportSynthesisExternalPartnershipProject.setActive(false);
    this.update(reportSynthesisExternalPartnershipProject);
  }

  @Override
  public boolean existReportSynthesisExternalPartnershipProject(long reportSynthesisExternalPartnershipProjectID) {
    ReportSynthesisExternalPartnershipProject reportSynthesisExternalPartnershipProject = this.find(reportSynthesisExternalPartnershipProjectID);
    if (reportSynthesisExternalPartnershipProject == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisExternalPartnershipProject find(long id) {
    return super.find(ReportSynthesisExternalPartnershipProject.class, id);

  }

  @Override
  public List<ReportSynthesisExternalPartnershipProject> findAll() {
    String query = "from " + ReportSynthesisExternalPartnershipProject.class.getName() + " where is_active=1";
    List<ReportSynthesisExternalPartnershipProject> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisExternalPartnershipProject save(ReportSynthesisExternalPartnershipProject reportSynthesisExternalPartnershipProject) {
    if (reportSynthesisExternalPartnershipProject.getId() == null) {
      super.saveEntity(reportSynthesisExternalPartnershipProject);
    } else {
      reportSynthesisExternalPartnershipProject = super.update(reportSynthesisExternalPartnershipProject);
    }


    return reportSynthesisExternalPartnershipProject;
  }


}