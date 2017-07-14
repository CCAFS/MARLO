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
package org.cgiar.ccafs.marlo.data.manager.impl;

import org.cgiar.ccafs.marlo.data.dao.ICenterLeaderDAO;
import org.cgiar.ccafs.marlo.data.manager.ICenterLeaderManager;
import org.cgiar.ccafs.marlo.data.model.CenterLeader;

import java.util.List;

import com.google.inject.Inject;


/**
 * Modified by @author nmatovu last on Oct 10, 2016
 */
public class CenterLeaderManager implements ICenterLeaderManager {

  private ICenterLeaderDAO researchLeaderDAO;

  @Inject
  public CenterLeaderManager(ICenterLeaderDAO researchLeaderDAO) {
    this.researchLeaderDAO = researchLeaderDAO;
  }

  @Override
  public boolean deleteResearchLeader(long researchLeaderId) {

    return researchLeaderDAO.deleteResearchLeader(researchLeaderId);
  }


  @Override
  public boolean existResearchLeader(long researchLeaderId) {

    return researchLeaderDAO.existResearchLeader(researchLeaderId);
  }

  @Override
  public List<CenterLeader> findAll() {

    return researchLeaderDAO.findAll();
  }


  @Override
  public CenterLeader getResearchLeaderById(long researchLeaderId) {

    return researchLeaderDAO.find(researchLeaderId);
  }

  @Override
  public long saveResearchLeader(CenterLeader researchLeader) {

    return researchLeaderDAO.save(researchLeader);
  }


}
