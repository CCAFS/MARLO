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

import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class PhaseMySQLDAO extends AbstractMarloDAO<Phase, Long> implements PhaseDAO {


  @Inject
  public PhaseMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deletePhase(long phaseId) {
    Phase phase = this.find(phaseId);

    super.delete(phase);
  }

  @Override
  public boolean existPhase(long phaseID) {
    Phase phase = this.find(phaseID);
    if (phase == null) {
      return false;
    }
    return true;

  }

  @Override
  public Phase find(long id) {
    return super.find(Phase.class, id);

  }

  @Override
  public List<Phase> findAll() {
    String query = "from " + Phase.class.getName() + " ";
    List<Phase> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public Phase findCycle(String cylce, int year, boolean upkeep, long crpId) {
    String query = "from " + Phase.class.getName() + " where description='" + cylce + "' and year=" + year
      + " and upkeep=" + upkeep + " and global_unit_id=" + crpId;
    List<Phase> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public Phase findPreviousPhase(long phaseId) {
    String query = "from " + Phase.class.getName() + " where next_phase=" + phaseId;
    List<Phase> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }


  @Override
  public Phase getActivePhase(long globalUnitId) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT MAX(id) id FROM phases ");
    query.append("WHERE editable = 1 AND visible = 1 ");
    query.append("AND global_unit_id = ");
    query.append(globalUnitId);
    List<Map<String, Object>> list = super.findCustomQuery(query.toString());

    List<Phase> phases = new ArrayList<Phase>();
    for (Map<String, Object> map : list) {
      String id = map.get("id").toString();
      long phaseId = Long.parseLong(id);
      Phase phase = this.find(phaseId);
      phases.add(phase);
    }
    return phases.get(0);
  }


  @Override
  public Phase save(Phase phase) {
    if (phase.getId() == null) {
      super.saveEntity(phase);
    } else {
      phase = super.update(phase);
    }


    return phase;
  }


}