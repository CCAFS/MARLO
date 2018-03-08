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


import org.cgiar.ccafs.marlo.data.dao.PowbMonitoringEvaluationLearningExerciseDAO;
import org.cgiar.ccafs.marlo.data.manager.PowbMonitoringEvaluationLearningExerciseManager;
import org.cgiar.ccafs.marlo.data.model.PowbMonitoringEvaluationLearningExercise;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class PowbMonitoringEvaluationLearningExerciseManagerImpl implements PowbMonitoringEvaluationLearningExerciseManager {


  private PowbMonitoringEvaluationLearningExerciseDAO powbMonitoringEvaluationLearningExerciseDAO;
  // Managers


  @Inject
  public PowbMonitoringEvaluationLearningExerciseManagerImpl(PowbMonitoringEvaluationLearningExerciseDAO powbMonitoringEvaluationLearningExerciseDAO) {
    this.powbMonitoringEvaluationLearningExerciseDAO = powbMonitoringEvaluationLearningExerciseDAO;


  }

  @Override
  public void deletePowbMonitoringEvaluationLearningExercise(long powbMonitoringEvaluationLearningExerciseId) {

    powbMonitoringEvaluationLearningExerciseDAO.deletePowbMonitoringEvaluationLearningExercise(powbMonitoringEvaluationLearningExerciseId);
  }

  @Override
  public boolean existPowbMonitoringEvaluationLearningExercise(long powbMonitoringEvaluationLearningExerciseID) {

    return powbMonitoringEvaluationLearningExerciseDAO.existPowbMonitoringEvaluationLearningExercise(powbMonitoringEvaluationLearningExerciseID);
  }

  @Override
  public List<PowbMonitoringEvaluationLearningExercise> findAll() {

    return powbMonitoringEvaluationLearningExerciseDAO.findAll();

  }

  @Override
  public PowbMonitoringEvaluationLearningExercise getPowbMonitoringEvaluationLearningExerciseById(long powbMonitoringEvaluationLearningExerciseID) {

    return powbMonitoringEvaluationLearningExerciseDAO.find(powbMonitoringEvaluationLearningExerciseID);
  }

  @Override
  public PowbMonitoringEvaluationLearningExercise savePowbMonitoringEvaluationLearningExercise(PowbMonitoringEvaluationLearningExercise powbMonitoringEvaluationLearningExercise) {

    return powbMonitoringEvaluationLearningExerciseDAO.save(powbMonitoringEvaluationLearningExercise);
  }


}
