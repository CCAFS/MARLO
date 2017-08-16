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

import org.cgiar.ccafs.marlo.data.dao.ICenterMonitoringtOutcomeEvidenceDAO;
import org.cgiar.ccafs.marlo.data.model.CenterMonitoringOutcomeEvidence;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class CenterMonitoringOutcomeEvidenceDAO extends AbstractMarloDAO<CenterMonitoringOutcomeEvidence, Long>
  implements ICenterMonitoringtOutcomeEvidenceDAO {


  @Inject
  public CenterMonitoringOutcomeEvidenceDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public boolean deleteMonitorignOutcomeEvidence(long monitorignOutcomeEvidenceId) {
    CenterMonitoringOutcomeEvidence monitorignOutcomeEvidence = this.find(monitorignOutcomeEvidenceId);
    monitorignOutcomeEvidence.setActive(false);
    return this.save(monitorignOutcomeEvidence) > 0;
  }

  @Override
  public boolean existMonitorignOutcomeEvidence(long monitorignOutcomeEvidenceID) {
    CenterMonitoringOutcomeEvidence monitorignOutcomeEvidence = this.find(monitorignOutcomeEvidenceID);
    if (monitorignOutcomeEvidence == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterMonitoringOutcomeEvidence find(long id) {
    return super.find(CenterMonitoringOutcomeEvidence.class, id);

  }

  @Override
  public List<CenterMonitoringOutcomeEvidence> findAll() {
    String query = "from " + CenterMonitoringOutcomeEvidence.class.getName();
    List<CenterMonitoringOutcomeEvidence> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterMonitoringOutcomeEvidence> getMonitorignOutcomeEvidencesByUserId(long userId) {
    String query = "from " + CenterMonitoringOutcomeEvidence.class.getName() + " where user_id=" + userId;
    return super.findAll(query);
  }

  @Override
  public long save(CenterMonitoringOutcomeEvidence monitorignOutcomeEvidence) {
    if (monitorignOutcomeEvidence.getId() == null) {
      super.saveEntity(monitorignOutcomeEvidence);
    } else {
      super.update(monitorignOutcomeEvidence);
    }
    return monitorignOutcomeEvidence.getId();
  }


}