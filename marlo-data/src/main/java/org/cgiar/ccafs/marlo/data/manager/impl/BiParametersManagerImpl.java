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

import org.cgiar.ccafs.marlo.data.dao.BiParametersDAO;
import org.cgiar.ccafs.marlo.data.manager.BiParametersManager;
import org.cgiar.ccafs.marlo.data.model.BiParameters;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Luis Benavides - CIAT/CCAFS
 */
@Named
public class BiParametersManagerImpl implements BiParametersManager {

  private BiParametersDAO biParametersDAO;
  // Managers

  @Inject
  public BiParametersManagerImpl(BiParametersDAO biParametersDAO) {
    this.biParametersDAO = biParametersDAO;

  }

  @Override
  public void deleteBiParameters(long biParametersId) {

    biParametersDAO.deleteBiParameters(biParametersId);
  }

  @Override
  public boolean existBiParameters(long biParametersID) {

    return biParametersDAO.existBiParameters(biParametersID);
  }

  @Override
  public List<BiParameters> findAll() {

    return biParametersDAO.findAll();

  }

  @Override
  public BiParameters getBiParametersById(long biParametersID) {

    return biParametersDAO.find(biParametersID);
  }

  @Override
  public BiParameters saveBiParameters(BiParameters biParameters) {
    return biParametersDAO.save(biParameters);
  }

}
