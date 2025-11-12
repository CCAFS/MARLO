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


import org.cgiar.ccafs.marlo.data.dao.AiReportConfigurationDAO;
import org.cgiar.ccafs.marlo.data.manager.AiReportConfigurationManager;
import org.cgiar.ccafs.marlo.data.model.AiReportConfiguration;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class AiReportConfigurationManagerImpl implements AiReportConfigurationManager {


  private AiReportConfigurationDAO aiReportConfigurationDAO;
  // Managers


  @Inject
  public AiReportConfigurationManagerImpl(AiReportConfigurationDAO aiReportConfigurationDAO) {
    this.aiReportConfigurationDAO = aiReportConfigurationDAO;


  }

  @Override
  public void deleteAiReportConfiguration(long aiReportConfigurationId) {

    aiReportConfigurationDAO.deleteAiReportConfiguration(aiReportConfigurationId);
  }

  @Override
  public boolean existAiReportConfiguration(long aiReportConfigurationID) {

    return aiReportConfigurationDAO.existAiReportConfiguration(aiReportConfigurationID);
  }

  @Override
  public List<AiReportConfiguration> findAll() {

    return aiReportConfigurationDAO.findAll();

  }

  @Override
  public AiReportConfiguration getAiReportConfigurationById(long aiReportConfigurationID) {

    return aiReportConfigurationDAO.find(aiReportConfigurationID);
  }

  @Override
  public AiReportConfiguration saveAiReportConfiguration(AiReportConfiguration aiReportConfiguration) {

    return aiReportConfigurationDAO.save(aiReportConfiguration);
  }


}
