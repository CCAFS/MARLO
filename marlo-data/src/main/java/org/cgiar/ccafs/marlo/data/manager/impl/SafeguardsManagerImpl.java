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


import org.cgiar.ccafs.marlo.data.dao.SafeguardsDAO;
import org.cgiar.ccafs.marlo.data.manager.SafeguardsManager;
import org.cgiar.ccafs.marlo.data.model.Safeguards;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class SafeguardsManagerImpl implements SafeguardsManager {


  private SafeguardsDAO safeguardsDAO;
  // Managers


  @Inject
  public SafeguardsManagerImpl(SafeguardsDAO safeguardsDAO) {
    this.safeguardsDAO = safeguardsDAO;


  }

  @Override
  public void deleteSafeguards(long safeguardsId) {

    safeguardsDAO.deleteSafeguards(safeguardsId);
  }

  @Override
  public boolean existSafeguards(long safeguardsID) {

    return safeguardsDAO.existSafeguards(safeguardsID);
  }

  @Override
  public List<Safeguards> findAll() {

    return safeguardsDAO.findAll();

  }

  @Override
  public Safeguards getSafeguardsById(long safeguardsID) {

    return safeguardsDAO.find(safeguardsID);
  }

  @Override
  public Safeguards saveSafeguards(Safeguards safeguards) {

    return safeguardsDAO.save(safeguards);
  }


}
