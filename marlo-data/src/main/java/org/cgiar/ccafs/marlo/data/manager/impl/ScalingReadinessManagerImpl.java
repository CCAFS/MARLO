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


import org.cgiar.ccafs.marlo.data.dao.ScalingReadinessDAO;
import org.cgiar.ccafs.marlo.data.manager.ScalingReadinessManager;
import org.cgiar.ccafs.marlo.data.model.ScalingReadiness;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ScalingReadinessManagerImpl implements ScalingReadinessManager {


  private ScalingReadinessDAO scalingReadinessDAO;
  // Managers


  @Inject
  public ScalingReadinessManagerImpl(ScalingReadinessDAO scalingReadinessDAO) {
    this.scalingReadinessDAO = scalingReadinessDAO;


  }

  @Override
  public void deleteScalingReadiness(long scalingReadinessId) {

    scalingReadinessDAO.deleteScalingReadiness(scalingReadinessId);
  }

  @Override
  public boolean existScalingReadiness(long scalingReadinessID) {

    return scalingReadinessDAO.existScalingReadiness(scalingReadinessID);
  }

  @Override
  public List<ScalingReadiness> findAll() {

    return scalingReadinessDAO.findAll();

  }

  @Override
  public ScalingReadiness getScalingReadinessById(long scalingReadinessID) {

    return scalingReadinessDAO.find(scalingReadinessID);
  }

  @Override
  public ScalingReadiness saveScalingReadiness(ScalingReadiness scalingReadiness) {

    return scalingReadinessDAO.save(scalingReadiness);
  }


}
