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

import org.cgiar.ccafs.marlo.data.manager.CrossCuttingDimensionManager;
import org.cgiar.ccafs.marlo.data.model.dto.CrossCuttingDimensionDTO;
import org.cgiar.ccafs.marlo.data.model.dto.CrossCuttingDimensionTableDTO;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class CrossCuttingDimensionManagerImpl implements CrossCuttingDimensionManager {


  @Inject
  public CrossCuttingDimensionManagerImpl() {

  }

  @Override
  public CrossCuttingDimensionDTO loadCrossCuttingDimensionByPMU(Long liaisonInstitution) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public CrossCuttingDimensionTableDTO loadTableByLiaisonAndPhase(Long liaisonInstitution, Long phaseId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void saveCrossCuttingDimensions(CrossCuttingDimensionDTO crossCuttingDimensions) {
    // TODO Auto-generated method stub

  }

}
