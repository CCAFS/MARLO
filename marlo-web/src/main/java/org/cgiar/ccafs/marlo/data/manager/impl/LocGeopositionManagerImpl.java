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


import org.cgiar.ccafs.marlo.data.dao.LocGeopositionDAO;
import org.cgiar.ccafs.marlo.data.manager.LocGeopositionManager;
import org.cgiar.ccafs.marlo.data.model.LocGeoposition;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class LocGeopositionManagerImpl implements LocGeopositionManager {


  private LocGeopositionDAO locGeopositionDAO;
  // Managers


  @Inject
  public LocGeopositionManagerImpl(LocGeopositionDAO locGeopositionDAO) {
    this.locGeopositionDAO = locGeopositionDAO;


  }

  @Override
  public void deleteLocGeoposition(long locGeopositionId) {

    locGeopositionDAO.deleteLocGeoposition(locGeopositionId);
  }

  @Override
  public boolean existLocGeoposition(long locGeopositionID) {

    return locGeopositionDAO.existLocGeoposition(locGeopositionID);
  }

  @Override
  public List<LocGeoposition> findAll() {

    return locGeopositionDAO.findAll();

  }

  @Override
  public LocGeoposition getLocGeopositionById(long locGeopositionID) {

    return locGeopositionDAO.find(locGeopositionID);
  }

  @Override
  public LocGeoposition saveLocGeoposition(LocGeoposition locGeoposition) {

    return locGeopositionDAO.save(locGeoposition);
  }


}
