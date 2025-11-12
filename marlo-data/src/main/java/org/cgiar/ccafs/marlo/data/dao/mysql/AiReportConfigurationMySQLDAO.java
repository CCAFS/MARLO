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

import org.cgiar.ccafs.marlo.data.dao.AiReportConfigurationDAO;
import org.cgiar.ccafs.marlo.data.model.AiReportConfiguration;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class AiReportConfigurationMySQLDAO extends AbstractMarloDAO<AiReportConfiguration, Long> implements AiReportConfigurationDAO {


  @Inject
  public AiReportConfigurationMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteAiReportConfiguration(long aiReportConfigurationId) {
    AiReportConfiguration aiReportConfiguration = this.find(aiReportConfigurationId);
    aiReportConfiguration.setActive(false);
    this.update(aiReportConfiguration);
  }

  @Override
  public boolean existAiReportConfiguration(long aiReportConfigurationID) {
    AiReportConfiguration aiReportConfiguration = this.find(aiReportConfigurationID);
    if (aiReportConfiguration == null) {
      return false;
    }
    return true;

  }

  @Override
  public AiReportConfiguration find(long id) {
    return super.find(AiReportConfiguration.class, id);

  }

  @Override
  public List<AiReportConfiguration> findAll() {
    String query = "from " + AiReportConfiguration.class.getName() + " where is_active=1";
    List<AiReportConfiguration> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public AiReportConfiguration save(AiReportConfiguration aiReportConfiguration) {
    if (aiReportConfiguration.getId() == null) {
      super.saveEntity(aiReportConfiguration);
    } else {
      aiReportConfiguration = super.update(aiReportConfiguration);
    }


    return aiReportConfiguration;
  }


}