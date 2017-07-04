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

/**
 * 
 */
package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.ICenterLeaderDAO;
import org.cgiar.ccafs.marlo.data.model.CenterLeader;

import java.util.List;

import com.google.inject.Inject;


/**
 * Modified by @author nmatovu last on Oct 10, 2016
 */
public class CenterLeaderDAO implements ICenterLeaderDAO {

  private StandardDAO dao;

  @Inject
  public CenterLeaderDAO(StandardDAO dao) {
    this.dao = dao;
  }


  @Override
  public boolean deleteResearchLeader(long researchLeaderId) {
    CenterLeader researchLeader = this.find(researchLeaderId);
    researchLeader.setActive(false);
    return this.save(researchLeader) > 0;
  }


  @Override
  public boolean existResearchLeader(long researchLeaderId) {
    CenterLeader researchLeader = this.find(researchLeaderId);
    if (researchLeader == null) {
      return false;
    }
    return true;
  }


  @Override
  public CenterLeader find(long researchLeaderId) {
    return dao.find(CenterLeader.class, researchLeaderId);
  }

  @Override
  public List<CenterLeader> findAll() {
    String query = "from " + CenterLeader.class.getName() + " where is_active=1";
    List<CenterLeader> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public long save(CenterLeader researchLeader) {
    if (researchLeader.getId() == null) {
      dao.save(researchLeader);
    } else {
      dao.update(researchLeader);
    }
    return researchLeader.getId();
  }

}
