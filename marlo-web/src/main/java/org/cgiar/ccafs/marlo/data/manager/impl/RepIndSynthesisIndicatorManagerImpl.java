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


import org.cgiar.ccafs.marlo.data.dao.RepIndSynthesisIndicatorDAO;
import org.cgiar.ccafs.marlo.data.manager.RepIndSynthesisIndicatorManager;
import org.cgiar.ccafs.marlo.data.model.RepIndSynthesisIndicator;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class RepIndSynthesisIndicatorManagerImpl implements RepIndSynthesisIndicatorManager {


  private RepIndSynthesisIndicatorDAO repIndSynthesisIndicatorDAO;
  // Managers


  @Inject
  public RepIndSynthesisIndicatorManagerImpl(RepIndSynthesisIndicatorDAO repIndSynthesisIndicatorDAO) {
    this.repIndSynthesisIndicatorDAO = repIndSynthesisIndicatorDAO;


  }

  @Override
  public void deleteRepIndSynthesisIndicator(long repIndSynthesisIndicatorId) {

    repIndSynthesisIndicatorDAO.deleteRepIndSynthesisIndicator(repIndSynthesisIndicatorId);
  }

  @Override
  public boolean existRepIndSynthesisIndicator(long repIndSynthesisIndicatorID) {

    return repIndSynthesisIndicatorDAO.existRepIndSynthesisIndicator(repIndSynthesisIndicatorID);
  }

  @Override
  public List<RepIndSynthesisIndicator> findAll() {

    return repIndSynthesisIndicatorDAO.findAll();

  }

  @Override
  public RepIndSynthesisIndicator getRepIndSynthesisIndicatorById(long repIndSynthesisIndicatorID) {

    return repIndSynthesisIndicatorDAO.find(repIndSynthesisIndicatorID);
  }

  @Override
  public RepIndSynthesisIndicator saveRepIndSynthesisIndicator(RepIndSynthesisIndicator repIndSynthesisIndicator) {

    return repIndSynthesisIndicatorDAO.save(repIndSynthesisIndicator);
  }


}
