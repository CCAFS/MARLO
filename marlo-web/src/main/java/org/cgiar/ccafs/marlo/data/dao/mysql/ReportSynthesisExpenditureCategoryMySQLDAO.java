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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisExpenditureCategoryDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisExpenditureCategory;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisExpenditureCategoryMySQLDAO extends AbstractMarloDAO<ReportSynthesisExpenditureCategory, Long> implements ReportSynthesisExpenditureCategoryDAO {


  @Inject
  public ReportSynthesisExpenditureCategoryMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisExpenditureCategory(long reportSynthesisExpenditureCategoryId) {
    ReportSynthesisExpenditureCategory reportSynthesisExpenditureCategory = this.find(reportSynthesisExpenditureCategoryId);
    reportSynthesisExpenditureCategory.setActive(false);
    this.update(reportSynthesisExpenditureCategory);
  }

  @Override
  public boolean existReportSynthesisExpenditureCategory(long reportSynthesisExpenditureCategoryID) {
    ReportSynthesisExpenditureCategory reportSynthesisExpenditureCategory = this.find(reportSynthesisExpenditureCategoryID);
    if (reportSynthesisExpenditureCategory == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisExpenditureCategory find(long id) {
    return super.find(ReportSynthesisExpenditureCategory.class, id);

  }

  @Override
  public List<ReportSynthesisExpenditureCategory> findAll() {
    String query = "from " + ReportSynthesisExpenditureCategory.class.getName() + " where is_active=1";
    List<ReportSynthesisExpenditureCategory> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisExpenditureCategory save(ReportSynthesisExpenditureCategory reportSynthesisExpenditureCategory) {
    if (reportSynthesisExpenditureCategory.getId() == null) {
      super.saveEntity(reportSynthesisExpenditureCategory);
    } else {
      reportSynthesisExpenditureCategory = super.update(reportSynthesisExpenditureCategory);
    }


    return reportSynthesisExpenditureCategory;
  }


}