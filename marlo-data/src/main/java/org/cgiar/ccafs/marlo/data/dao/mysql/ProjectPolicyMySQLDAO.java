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

import org.cgiar.ccafs.marlo.data.dao.ProjectPolicyDAO;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PolicyHomeDTO;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.utils.ListResultTransformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named
public class ProjectPolicyMySQLDAO extends AbstractMarloDAO<ProjectPolicy, Long> implements ProjectPolicyDAO {

	@Inject
	public ProjectPolicyMySQLDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public void deleteProjectPolicy(long projectPolicyId) {
		ProjectPolicy projectPolicy = this.find(projectPolicyId);
		projectPolicy.setActive(false);
		this.update(projectPolicy);
	}

	@Override
	public boolean existProjectPolicy(long projectPolicyID) {
		ProjectPolicy projectPolicy = this.find(projectPolicyID);
		if (projectPolicy == null) {
			return false;
		}
		return true;

	}

	@Override
	public ProjectPolicy find(long id) {
		return super.find(ProjectPolicy.class, id);

	}

	@Override
	public List<ProjectPolicy> findAll() {
		String query = "from " + ProjectPolicy.class.getName() + " where is_active=1";
		List<ProjectPolicy> list = super.findAll(query);
		if (list.size() > 0) {
			return list;
		}
		return null;

	}

	@Override
	public List<PolicyHomeDTO> getPoliciesByProjectAndPhaseHome(long phaseId, long projectId) {
		String query = "select pp.id as policyId, ppi.year as expectedYear, pr.id as projectId, "
				+ "coalesce(ppi.repIndPolicyInvestimentType.name, 'None') as policyType, "
				+ "(case when coalesce(ppi.title, '') = '' then 'Not Provided' else ppi.title end) as policyTitle,"
				+ "coalesce(ppi.repIndStageProcess.name, 'Not Defined') as policyLevel "
				+ "from ProjectPolicy pp, ProjectPolicyInfo ppi, Phase ph, Project pr "
				+ "where ppi.projectPolicy = pp and pp.active = true and "
				+ "pp.project = pr and pr.id = :projectId and pr.active = true and "
				+ "ppi.phase = ph and ph.id = :phaseId and ppi.year = ph.year";

		Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);

		createQuery.setParameter("phaseId", phaseId);
		createQuery.setParameter("projectId", projectId);

		createQuery.setResultTransformer(
				(ListResultTransformer) (tuple, aliases) -> new PolicyHomeDTO(((Number) tuple[0]).longValue(),
						((Number) tuple[1]).longValue(), ((Number) tuple[2]).longValue(), (String) tuple[3],
						(String) tuple[4], (String) tuple[5]));
		createQuery.setFlushMode(FlushMode.COMMIT);

		List<PolicyHomeDTO> policies = createQuery.list();

		return policies;
	}

	@Override
	public List<ProjectPolicy> getProjectPolicyByPhase(Phase phase) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT DISTINCT  ");
		query.append("pp.id as id ");
		query.append("FROM ");
		query.append("project_policies AS pp ");
		query.append("INNER JOIN project_policy_info AS ppi ON pp.id = ppi.project_policy_id ");
		query.append("WHERE pp.is_active = 1 AND ");
		query.append("ppi.id_phase =" + phase.getId());

		List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
		List<ProjectPolicy> projectPolicies = new ArrayList<>();

		if (rList != null) {
			for (Map<String, Object> map : rList) {
				ProjectPolicy projectPolicy = this.find(Long.parseLong(map.get("id").toString()));
				projectPolicies.add(projectPolicy);
			}
		}

		return projectPolicies.stream().collect(Collectors.toList());
	}

	@Override
	public Boolean isPolicyExcluded(Long policyId, Long phaseId) {
		StringBuilder query = new StringBuilder();
		query.append("select is_policy_excluded(" + policyId.longValue() + "," + phaseId.longValue()
				+ ") as isPolicyExcluded");
		List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
		if (rList != null) {
			for (Map<String, Object> map : rList) {
				if (Long.parseLong(map.get("isPolicyExcluded").toString()) == 0) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public ProjectPolicy save(ProjectPolicy projectPolicy) {
		if (projectPolicy.getId() == null) {
			super.saveEntity(projectPolicy);
		} else {
			projectPolicy = super.update(projectPolicy);
		}

		return projectPolicy;
	}

	@Override
	public ProjectPolicy save(ProjectPolicy projectPolicy, String section, List<String> relationsName, Phase phase) {
		if (projectPolicy.getId() == null) {
			super.saveEntity(projectPolicy, section, relationsName, phase);
		} else {
			projectPolicy = super.update(projectPolicy, section, relationsName, phase);
		}
		return projectPolicy;
	}

}