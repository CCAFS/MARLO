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

import org.cgiar.ccafs.marlo.data.dao.PolicyMilestoneDAO;
import org.cgiar.ccafs.marlo.data.model.PolicyMilestone;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named
public class PolicyMilestoneMySQLDAO extends AbstractMarloDAO<PolicyMilestone, Long> implements PolicyMilestoneDAO {


  @Inject
  public PolicyMilestoneMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deletePolicyMilestone(long policyMilestoneId) {
    PolicyMilestone policyMilestone = this.find(policyMilestoneId);
    this.delete(policyMilestone);
  }

  @Override
  public boolean existPolicyMilestone(long policyMilestoneID) {
    PolicyMilestone policyMilestone = this.find(policyMilestoneID);
    if (policyMilestone == null) {
      return false;
    }
    return true;

  }

  @Override
  public PolicyMilestone find(long id) {
    return super.find(PolicyMilestone.class, id);

  }

  @Override
  public List<PolicyMilestone> findAll() {
    String query = "from " + PolicyMilestone.class.getName();
    List<PolicyMilestone> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public PolicyMilestone findByCrpMilestonePolicyAndPhase(long crpMilestoneId, long policyId, long phaseId) {
    String query = "from " + PolicyMilestone.class.getName() + " where policy_id=" + policyId + " and crp_milestone_id="
      + crpMilestoneId + " and id_phase=" + phaseId;

    List<PolicyMilestone> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }

    return null;
  }

  @Override
  public List<PolicyMilestone> getAllPolicyMilestonesByPolicy(long policyId) {
    String query = "select pm from PolicyMilestone pm where pm.policy.id = :policyId order by pm.phase.id";

    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("policyId", policyId);

    List<PolicyMilestone> result = super.findAll(createQuery);

    return result;
  }

  @Override
  public PolicyMilestone save(PolicyMilestone policyMilestone) {
    if (policyMilestone.getId() == null) {
      super.saveEntity(policyMilestone);
    } else {
      policyMilestone = super.update(policyMilestone);
    }


    return policyMilestone;
  }
}