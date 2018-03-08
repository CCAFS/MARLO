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


import org.cgiar.ccafs.marlo.data.dao.PowbMonitoringEvaluationLearningDAO;
import org.cgiar.ccafs.marlo.data.manager.PowbMonitoringEvaluationLearningManager;
import org.cgiar.ccafs.marlo.data.model.PowbMonitoringEvaluationLearning;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class PowbMonitoringEvaluationLearningManagerImpl implements PowbMonitoringEvaluationLearningManager {


  private PowbMonitoringEvaluationLearningDAO powbMonitoringEvaluationLearningDAO;
  // Managers


  @Inject
  public PowbMonitoringEvaluationLearningManagerImpl(PowbMonitoringEvaluationLearningDAO powbMonitoringEvaluationLearningDAO) {
    this.powbMonitoringEvaluationLearningDAO = powbMonitoringEvaluationLearningDAO;


  }

  @Override
  public void deletePowbMonitoringEvaluationLearning(long powbMonitoringEvaluationLearningId) {

    powbMonitoringEvaluationLearningDAO.deletePowbMonitoringEvaluationLearning(powbMonitoringEvaluationLearningId);
  }

  @Override
  public boolean existPowbMonitoringEvaluationLearning(long powbMonitoringEvaluationLearningID) {

    return powbMonitoringEvaluationLearningDAO.existPowbMonitoringEvaluationLearning(powbMonitoringEvaluationLearningID);
  }

  @Override
  public List<PowbMonitoringEvaluationLearning> findAll() {

    return powbMonitoringEvaluationLearningDAO.findAll();

  }

  @Override
  public PowbMonitoringEvaluationLearning getPowbMonitoringEvaluationLearningById(long powbMonitoringEvaluationLearningID) {

    return powbMonitoringEvaluationLearningDAO.find(powbMonitoringEvaluationLearningID);
  }

  @Override
  public PowbMonitoringEvaluationLearning savePowbMonitoringEvaluationLearning(PowbMonitoringEvaluationLearning powbMonitoringEvaluationLearning) {

    return powbMonitoringEvaluationLearningDAO.save(powbMonitoringEvaluationLearning);
  }


}
