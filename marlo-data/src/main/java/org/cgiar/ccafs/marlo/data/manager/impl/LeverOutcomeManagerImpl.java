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


import org.cgiar.ccafs.marlo.data.dao.LeverOutcomeDAO;
import org.cgiar.ccafs.marlo.data.manager.LeverOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.LeverOutcome;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class LeverOutcomeManagerImpl implements LeverOutcomeManager {


  private LeverOutcomeDAO leverOutcomeDAO;
  // Managers


  @Inject
  public LeverOutcomeManagerImpl(LeverOutcomeDAO leverOutcomeDAO) {
    this.leverOutcomeDAO = leverOutcomeDAO;


  }

  @Override
  public void deleteLeverOutcome(long leverOutcomeId) {

    leverOutcomeDAO.deleteLeverOutcome(leverOutcomeId);
  }

  @Override
  public boolean existLeverOutcome(long leverOutcomeID) {

    return leverOutcomeDAO.existLeverOutcome(leverOutcomeID);
  }

  @Override
  public List<LeverOutcome> findAll() {

    return leverOutcomeDAO.findAll();

  }

  @Override
  public LeverOutcome getLeverOutcomeById(long leverOutcomeID) {

    return leverOutcomeDAO.find(leverOutcomeID);
  }

  @Override
  public LeverOutcome saveLeverOutcome(LeverOutcome leverOutcome) {

    return leverOutcomeDAO.save(leverOutcome);
  }


}
