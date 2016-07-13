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


import org.cgiar.ccafs.marlo.data.dao.SrfSloIndicatorTargetDAO;
import org.cgiar.ccafs.marlo.data.manager.SrfSloIndicatorTargetManager;
import org.cgiar.ccafs.marlo.data.model.SrfSloIndicatorTarget;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class SrfSloIndicatorTargetManagerImpl implements SrfSloIndicatorTargetManager {


  private SrfSloIndicatorTargetDAO srfSloIndicatorTargetDAO;
  // Managers


  @Inject
  public SrfSloIndicatorTargetManagerImpl(SrfSloIndicatorTargetDAO srfSloIndicatorTargetDAO) {
    this.srfSloIndicatorTargetDAO = srfSloIndicatorTargetDAO;


  }

  @Override
  public boolean deleteSrfSloIndicatorTarget(long srfSloIndicatorTargetId) {

    return srfSloIndicatorTargetDAO.deleteSrfSloIndicatorTarget(srfSloIndicatorTargetId);
  }

  @Override
  public boolean existSrfSloIndicatorTarget(long srfSloIndicatorTargetID) {

    return srfSloIndicatorTargetDAO.existSrfSloIndicatorTarget(srfSloIndicatorTargetID);
  }

  @Override
  public List<SrfSloIndicatorTarget> findAll() {

    return srfSloIndicatorTargetDAO.findAll();

  }

  @Override
  public SrfSloIndicatorTarget getSrfSloIndicatorTargetById(long srfSloIndicatorTargetID) {

    return srfSloIndicatorTargetDAO.find(srfSloIndicatorTargetID);
  }

  @Override
  public long saveSrfSloIndicatorTarget(SrfSloIndicatorTarget srfSloIndicatorTarget) {

    return srfSloIndicatorTargetDAO.save(srfSloIndicatorTarget);
  }


}
