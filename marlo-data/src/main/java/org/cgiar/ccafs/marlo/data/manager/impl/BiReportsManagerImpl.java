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


import org.cgiar.ccafs.marlo.data.dao.BiReportsDAO;
import org.cgiar.ccafs.marlo.data.manager.BiReportsManager;
import org.cgiar.ccafs.marlo.data.model.BiReports;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Luis Benavides - CIAT/CCAFS
 */
@Named
public class BiReportsManagerImpl implements BiReportsManager {


  private BiReportsDAO biReportsDAO;
  // Managers


  @Inject
  public BiReportsManagerImpl(BiReportsDAO biReportsDAO) {
    this.biReportsDAO = biReportsDAO;


  }

  @Override
  public void deleteBiReports(long biReportsId) {

    biReportsDAO.deleteBiReports(biReportsId);
  }

  @Override
  public boolean existBiReports(long biReportsID) {

    return biReportsDAO.existBiReports(biReportsID);
  }

  @Override
  public List<BiReports> findAll() {

    return biReportsDAO.findAll();

  }

  @Override
  public BiReports getBiReportsById(long biReportsID) {

    return biReportsDAO.find(biReportsID);
  }

  @Override
  public BiReports saveBiReports(BiReports biReports) {

    return biReportsDAO.save(biReports);
  }


}
