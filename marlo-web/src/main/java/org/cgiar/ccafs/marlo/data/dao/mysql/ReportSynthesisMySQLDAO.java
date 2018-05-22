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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisDAO;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisMySQLDAO extends AbstractMarloDAO<ReportSynthesis, Long> implements ReportSynthesisDAO {


  @Inject
  public ReportSynthesisMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesis(long reportSynthesisId) {
    ReportSynthesis reportSynthesis = this.find(reportSynthesisId);
    reportSynthesis.setActive(false);
    this.update(reportSynthesis);
  }

  @Override
  public boolean existReportSynthesis(long reportSynthesisID) {
    ReportSynthesis reportSynthesis = this.find(reportSynthesisID);
    if (reportSynthesis == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesis find(long id) {
    return super.find(ReportSynthesis.class, id);

  }

  @Override
  public List<ReportSynthesis> findAll() {
    String query = "from " + ReportSynthesis.class.getName() + " where is_active=1";
    List<ReportSynthesis> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesis findSynthesis(long phaseID, long liaisonInstitutionID) {
    String query = "from " + ReportSynthesis.class.getName() + " where is_active=1 and id_phase= " + phaseID
      + " and liaison_institution_id= " + liaisonInstitutionID;
    List<ReportSynthesis> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;

  }

  @Override
  public ReportSynthesis save(ReportSynthesis reportSynthesis) {
    if (reportSynthesis.getId() == null) {
      super.saveEntity(reportSynthesis);
    } else {
      reportSynthesis = super.update(reportSynthesis);
    }


    return reportSynthesis;
  }

  @Override
  public ReportSynthesis save(ReportSynthesis reportSynthesis, String sectionName, List<String> relationsName,
    Phase phase) {
    if (reportSynthesis.getId() == null) {
      super.saveEntity(reportSynthesis, sectionName, relationsName, phase);
    } else {
      reportSynthesis = super.update(reportSynthesis, sectionName, relationsName, phase);
    }
    return reportSynthesis;
  }


}