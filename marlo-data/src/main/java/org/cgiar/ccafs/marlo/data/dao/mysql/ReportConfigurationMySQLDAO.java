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

import org.cgiar.ccafs.marlo.data.dao.ReportConfigurationDAO;
import org.cgiar.ccafs.marlo.data.model.ReportConfiguration;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportConfigurationMySQLDAO extends AbstractMarloDAO<ReportConfiguration, Long>
  implements ReportConfigurationDAO {


  @Inject
  public ReportConfigurationMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportConfiguration(long reportConfigurationId) {
    ReportConfiguration reportConfiguration = this.find(reportConfigurationId);
    this.delete(reportConfiguration);
  }

  @Override
  public boolean existReportConfiguration(long reportConfigurationID) {
    ReportConfiguration reportConfiguration = this.find(reportConfigurationID);
    if (reportConfiguration == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportConfiguration find(long id) {
    return super.find(ReportConfiguration.class, id);

  }

  @Override
  public List<ReportConfiguration> findAll() {
    String query = "from " + ReportConfiguration.class.getName();
    List<ReportConfiguration> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportConfiguration save(ReportConfiguration reportConfiguration) {
    if (reportConfiguration.getId() == null) {
      super.saveEntity(reportConfiguration);
    } else {
      reportConfiguration = super.update(reportConfiguration);
    }

    return reportConfiguration;
  }


}