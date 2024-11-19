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


import org.cgiar.ccafs.marlo.data.dao.QuantificationTypeDAO;
import org.cgiar.ccafs.marlo.data.manager.QuantificationTypeManager;
import org.cgiar.ccafs.marlo.data.model.QuantificationType;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class QuantificationTypeManagerImpl implements QuantificationTypeManager {


  private QuantificationTypeDAO quantificationTypeDAO;
  // Managers


  @Inject
  public QuantificationTypeManagerImpl(QuantificationTypeDAO quantificationTypeDAO) {
    this.quantificationTypeDAO = quantificationTypeDAO;


  }

  @Override
  public void deleteQuantificationType(long quantificationTypeId) {

    quantificationTypeDAO.deleteQuantificationType(quantificationTypeId);
  }

  @Override
  public boolean existQuantificationType(long quantificationTypeID) {

    return quantificationTypeDAO.existQuantificationType(quantificationTypeID);
  }

  @Override
  public List<QuantificationType> findAll() {

    return quantificationTypeDAO.findAll();

  }

  @Override
  public QuantificationType getQuantificationTypeById(long quantificationTypeID) {

    return quantificationTypeDAO.find(quantificationTypeID);
  }

  @Override
  public QuantificationType saveQuantificationType(QuantificationType quantificationType) {

    return quantificationTypeDAO.save(quantificationType);
  }


}
