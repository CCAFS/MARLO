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
package org.cgiar.ccafs.marlo.data.service.impl;


import org.cgiar.ccafs.marlo.data.dao.ICenterTopicDAO;
import org.cgiar.ccafs.marlo.data.model.CenterTopic;
import org.cgiar.ccafs.marlo.data.service.ICenterTopicManager;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CenterTopicManager implements ICenterTopicManager {


  private ICenterTopicDAO researchTopicDAO;

  // Managers


  @Inject
  public CenterTopicManager(ICenterTopicDAO researchTopicDAO) {
    this.researchTopicDAO = researchTopicDAO;


  }

  @Override
  public boolean deleteResearchTopic(long researchTopicId) {

    return researchTopicDAO.deleteResearchTopic(researchTopicId);
  }

  @Override
  public boolean existResearchTopic(long researchTopicID) {

    return researchTopicDAO.existResearchTopic(researchTopicID);
  }

  @Override
  public List<CenterTopic> findAll() {

    return researchTopicDAO.findAll();

  }

  @Override
  public CenterTopic getResearchTopicById(long researchTopicID) {

    return researchTopicDAO.find(researchTopicID);
  }

  @Override
  public List<CenterTopic> getResearchTopicsByUserId(Long userId) {
    return researchTopicDAO.getResearchTopicsByUserId(userId);
  }

  @Override
  public long saveResearchTopic(CenterTopic researchTopic) {

    return researchTopicDAO.save(researchTopic);
  }


}
