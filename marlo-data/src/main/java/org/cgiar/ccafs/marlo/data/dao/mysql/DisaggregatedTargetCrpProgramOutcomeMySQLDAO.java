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

import org.cgiar.ccafs.marlo.data.dao.DisaggregatedTargetCrpProgramOutcomeDAO;
import org.cgiar.ccafs.marlo.data.model.DisaggregatedTargetCrpProgramOutcome;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class DisaggregatedTargetCrpProgramOutcomeMySQLDAO extends
  AbstractMarloDAO<DisaggregatedTargetCrpProgramOutcome, Long> implements DisaggregatedTargetCrpProgramOutcomeDAO {


  @Inject
  public DisaggregatedTargetCrpProgramOutcomeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDisaggregatedTargetCrpProgramOutcome(long disaggregatedTargetCrpProgramOutcomeId) {
    DisaggregatedTargetCrpProgramOutcome disaggregatedTargetCrpProgramOutcome =
      this.find(disaggregatedTargetCrpProgramOutcomeId);
    disaggregatedTargetCrpProgramOutcome.setActive(false);
    this.update(disaggregatedTargetCrpProgramOutcome);
  }

  @Override
  public boolean existDisaggregatedTargetCrpProgramOutcome(long disaggregatedTargetCrpProgramOutcomeID) {
    DisaggregatedTargetCrpProgramOutcome disaggregatedTargetCrpProgramOutcome =
      this.find(disaggregatedTargetCrpProgramOutcomeID);
    if (disaggregatedTargetCrpProgramOutcome == null) {
      return false;
    }
    return true;

  }

  @Override
  public DisaggregatedTargetCrpProgramOutcome find(long id) {
    return super.find(DisaggregatedTargetCrpProgramOutcome.class, id);

  }

  @Override
  public List<DisaggregatedTargetCrpProgramOutcome> findAll() {
    String query = "from " + DisaggregatedTargetCrpProgramOutcome.class.getName() + " where is_active=1";
    List<DisaggregatedTargetCrpProgramOutcome> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<DisaggregatedTargetCrpProgramOutcome>
    getDisaggregatedTargetCrpProgramOutcomeByOutcome(long crpProgramOutcomeID) {
    String query = "from " + DisaggregatedTargetCrpProgramOutcome.class.getName()
      + " where is_active=1 and crp_program_outcome_id=" + crpProgramOutcomeID;
    List<DisaggregatedTargetCrpProgramOutcome> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public List<DisaggregatedTargetCrpProgramOutcome> getDisaggregatedTargetCrpProgramOutcomeByPhase(long phaseId) {
    String query =
      "from " + DisaggregatedTargetCrpProgramOutcome.class.getName() + " where is_active=1 and id_phase=" + phaseId;
    List<DisaggregatedTargetCrpProgramOutcome> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public DisaggregatedTargetCrpProgramOutcome
    save(DisaggregatedTargetCrpProgramOutcome disaggregatedTargetCrpProgramOutcome) {
    if (disaggregatedTargetCrpProgramOutcome.getId() == null) {
      super.saveEntity(disaggregatedTargetCrpProgramOutcome);
    } else {
      disaggregatedTargetCrpProgramOutcome = super.update(disaggregatedTargetCrpProgramOutcome);
    }


    return disaggregatedTargetCrpProgramOutcome;
  }


}