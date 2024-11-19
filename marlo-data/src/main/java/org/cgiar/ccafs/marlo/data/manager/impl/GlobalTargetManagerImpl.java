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


import org.cgiar.ccafs.marlo.data.dao.GlobalTargetDAO;
import org.cgiar.ccafs.marlo.data.manager.GlobalTargetManager;
import org.cgiar.ccafs.marlo.data.model.GlobalTarget;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class GlobalTargetManagerImpl implements GlobalTargetManager {


  private GlobalTargetDAO globalTargetDAO;
  // Managers


  @Inject
  public GlobalTargetManagerImpl(GlobalTargetDAO globalTargetDAO) {
    this.globalTargetDAO = globalTargetDAO;


  }

  @Override
  public void deleteGlobalTarget(long globalTargetId) {

    globalTargetDAO.deleteGlobalTarget(globalTargetId);
  }

  @Override
  public boolean existGlobalTarget(long globalTargetID) {

    return globalTargetDAO.existGlobalTarget(globalTargetID);
  }

  @Override
  public List<GlobalTarget> findAll() {

    return globalTargetDAO.findAll();

  }

  @Override
  public List<GlobalTarget> findAllByImpactArea(long impactAreaId) {

    return globalTargetDAO.findAllByImpactArea(impactAreaId);

  }

  @Override
  public GlobalTarget getGlobalTargetById(long globalTargetID) {

    return globalTargetDAO.find(globalTargetID);
  }

  @Override
  public GlobalTarget saveGlobalTarget(GlobalTarget globalTarget) {

    return globalTargetDAO.save(globalTarget);
  }


}
