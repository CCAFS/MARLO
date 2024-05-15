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

import org.cgiar.ccafs.marlo.data.dao.DisaggregatedTargetCrpMilestoneDAO;
import org.cgiar.ccafs.marlo.data.model.DisaggregatedTargetCrpMilestone;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class DisaggregatedTargetCrpMilestoneMySQLDAO extends AbstractMarloDAO<DisaggregatedTargetCrpMilestone, Long> implements DisaggregatedTargetCrpMilestoneDAO {


  @Inject
  public DisaggregatedTargetCrpMilestoneMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteDisaggregatedTargetCrpMilestone(long disaggregatedTargetCrpMilestoneId) {
    DisaggregatedTargetCrpMilestone disaggregatedTargetCrpMilestone = this.find(disaggregatedTargetCrpMilestoneId);
    disaggregatedTargetCrpMilestone.setActive(false);
    this.update(disaggregatedTargetCrpMilestone);
  }

  @Override
  public boolean existDisaggregatedTargetCrpMilestone(long disaggregatedTargetCrpMilestoneID) {
    DisaggregatedTargetCrpMilestone disaggregatedTargetCrpMilestone = this.find(disaggregatedTargetCrpMilestoneID);
    if (disaggregatedTargetCrpMilestone == null) {
      return false;
    }
    return true;

  }

  @Override
  public DisaggregatedTargetCrpMilestone find(long id) {
    return super.find(DisaggregatedTargetCrpMilestone.class, id);

  }

  @Override
  public List<DisaggregatedTargetCrpMilestone> findAll() {
    String query = "from " + DisaggregatedTargetCrpMilestone.class.getName() + " where is_active=1";
    List<DisaggregatedTargetCrpMilestone> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public DisaggregatedTargetCrpMilestone save(DisaggregatedTargetCrpMilestone disaggregatedTargetCrpMilestone) {
    if (disaggregatedTargetCrpMilestone.getId() == null) {
      super.saveEntity(disaggregatedTargetCrpMilestone);
    } else {
      disaggregatedTargetCrpMilestone = super.update(disaggregatedTargetCrpMilestone);
    }


    return disaggregatedTargetCrpMilestone;
  }


}