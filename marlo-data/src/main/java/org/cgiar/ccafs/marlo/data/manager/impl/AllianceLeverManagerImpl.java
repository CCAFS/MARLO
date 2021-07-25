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


import org.cgiar.ccafs.marlo.data.dao.AllianceLeverDAO;
import org.cgiar.ccafs.marlo.data.manager.AllianceLeverManager;
import org.cgiar.ccafs.marlo.data.model.AllianceLever;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class AllianceLeverManagerImpl implements AllianceLeverManager {


  private AllianceLeverDAO allianceLeverDAO;
  // Managers


  @Inject
  public AllianceLeverManagerImpl(AllianceLeverDAO allianceLeverDAO) {
    this.allianceLeverDAO = allianceLeverDAO;


  }

  @Override
  public void deleteAllianceLever(long allianceLeverId) {

    allianceLeverDAO.deleteAllianceLever(allianceLeverId);
  }

  @Override
  public boolean existAllianceLever(long allianceLeverID) {

    return allianceLeverDAO.existAllianceLever(allianceLeverID);
  }

  @Override
  public List<AllianceLever> findAll() {

    return allianceLeverDAO.findAll();

  }

  @Override
  public AllianceLever getAllianceLeverById(long allianceLeverID) {

    return allianceLeverDAO.find(allianceLeverID);
  }

  @Override
  public AllianceLever saveAllianceLever(AllianceLever allianceLever) {

    return allianceLeverDAO.save(allianceLever);
  }


}
