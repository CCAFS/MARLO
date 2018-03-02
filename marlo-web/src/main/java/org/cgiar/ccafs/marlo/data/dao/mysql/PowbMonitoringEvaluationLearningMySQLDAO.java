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


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.PowbMonitoringEvaluationLearningDAO;
import org.cgiar.ccafs.marlo.data.model.PowbMonitoringEvaluationLearning;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class PowbMonitoringEvaluationLearningMySQLDAO extends AbstractMarloDAO<PowbMonitoringEvaluationLearning, Long> implements PowbMonitoringEvaluationLearningDAO {


  @Inject
  public PowbMonitoringEvaluationLearningMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deletePowbMonitoringEvaluationLearning(long powbMonitoringEvaluationLearningId) {
    PowbMonitoringEvaluationLearning powbMonitoringEvaluationLearning = this.find(powbMonitoringEvaluationLearningId);
    powbMonitoringEvaluationLearning.setActive(false);
    this.save(powbMonitoringEvaluationLearning);
  }

  @Override
  public boolean existPowbMonitoringEvaluationLearning(long powbMonitoringEvaluationLearningID) {
    PowbMonitoringEvaluationLearning powbMonitoringEvaluationLearning = this.find(powbMonitoringEvaluationLearningID);
    if (powbMonitoringEvaluationLearning == null) {
      return false;
    }
    return true;

  }

  @Override
  public PowbMonitoringEvaluationLearning find(long id) {
    return super.find(PowbMonitoringEvaluationLearning.class, id);

  }

  @Override
  public List<PowbMonitoringEvaluationLearning> findAll() {
    String query = "from " + PowbMonitoringEvaluationLearning.class.getName() + " where is_active=1";
    List<PowbMonitoringEvaluationLearning> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public PowbMonitoringEvaluationLearning save(PowbMonitoringEvaluationLearning powbMonitoringEvaluationLearning) {
    if (powbMonitoringEvaluationLearning.getId() == null) {
      super.saveEntity(powbMonitoringEvaluationLearning);
    } else {
      powbMonitoringEvaluationLearning = super.update(powbMonitoringEvaluationLearning);
    }


    return powbMonitoringEvaluationLearning;
  }


}