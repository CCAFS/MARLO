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


package org.cgiar.ccafs.marlo.data.dao.impl;

import org.cgiar.ccafs.marlo.data.dao.ICenterTopicDAO;
import org.cgiar.ccafs.marlo.data.model.CenterTopic;

import java.util.List;

import com.google.inject.Inject;

public class CenterTopicDAO implements ICenterTopicDAO {

  private StandardDAO dao;

  @Inject
  public CenterTopicDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteResearchTopic(long researchTopicId) {
    CenterTopic researchTopic = this.find(researchTopicId);
    researchTopic.setActive(false);
    return this.save(researchTopic) > 0;
  }

  @Override
  public boolean existResearchTopic(long researchTopicID) {
    CenterTopic researchTopic = this.find(researchTopicID);
    if (researchTopic == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterTopic find(long id) {
    return dao.find(CenterTopic.class, id);

  }

  @Override
  public List<CenterTopic> findAll() {
    String query = "from " + CenterTopic.class.getName();
    List<CenterTopic> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterTopic> getResearchTopicsByUserId(long userId) {
    String query = "from " + CenterTopic.class.getName() + " where user_id=" + userId;
    return dao.findAll(query);
  }

  @Override
  public long save(CenterTopic researchTopic) {
    if (researchTopic.getId() == null) {
      dao.save(researchTopic);
    } else {
      dao.update(researchTopic);
    }
    return researchTopic.getId();
  }


}