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


package org.cgiar.ccafs.marlo.data.dao;

import org.cgiar.ccafs.marlo.data.model.CrossCuttingDimensions;
import org.cgiar.ccafs.marlo.data.model.dto.CrossCuttingDimensionTableDTO;

public interface CrossCuttingDimensionsDAO {


  /**
   * find a cross cutting dimension by its id
   * 
   * @param id - cross cutting dimension id
   * @author JULIANRODRIGUEZ
   * @return a cross cutting dimension object
   */
  public CrossCuttingDimensions find(Long id);

  /**
   * find a cross cutting dimension by the liaison institution and the phase
   * 
   * @author JULIANRODRIGUEZ
   * @param liaisonInstitutionId
   * @param phaseId
   * @return a cross cutting dimension object
   */
  public CrossCuttingDimensions findCrossCutting(Long liaisonInstitutionId, Long phaseId);


  /**
   * load Table C from POWB by liaison institution and phase
   * 
   * @param liaisonInstitution
   * @param phaseId
   * @return object with table c fields
   */
  public CrossCuttingDimensionTableDTO getTableC(Long liaisonInstitution, Long phaseId);


  /**
   * save a new cross cutting dimension into the database
   * 
   * @author JULIANRODRIGUEZ
   * @param crossCutting - the new object
   * @return - a stored cross cutting dimension object
   */
  public CrossCuttingDimensions save(CrossCuttingDimensions crossCutting);

}
