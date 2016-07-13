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


import org.cgiar.ccafs.marlo.data.dao.SrfSloIndicatorDAO;
import org.cgiar.ccafs.marlo.data.manager.SrfSloIndicatorManager;
import org.cgiar.ccafs.marlo.data.model.SrfSloIndicator;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class SrfSloIndicatorManagerImpl implements SrfSloIndicatorManager {


  private SrfSloIndicatorDAO srfSloIndicatorDAO;
  // Managers


  @Inject
  public SrfSloIndicatorManagerImpl(SrfSloIndicatorDAO srfSloIndicatorDAO) {
    this.srfSloIndicatorDAO = srfSloIndicatorDAO;


  }

  @Override
  public boolean deleteSrfSloIndicator(long srfSloIndicatorId) {

    return srfSloIndicatorDAO.deleteSrfSloIndicator(srfSloIndicatorId);
  }

  @Override
  public boolean existSrfSloIndicator(long srfSloIndicatorID) {

    return srfSloIndicatorDAO.existSrfSloIndicator(srfSloIndicatorID);
  }

  @Override
  public List<SrfSloIndicator> findAll() {

    return srfSloIndicatorDAO.findAll();

  }

  @Override
  public SrfSloIndicator getSrfSloIndicatorById(long srfSloIndicatorID) {

    return srfSloIndicatorDAO.find(srfSloIndicatorID);
  }

  @Override
  public long saveSrfSloIndicator(SrfSloIndicator srfSloIndicator) {

    return srfSloIndicatorDAO.save(srfSloIndicator);
  }


}
