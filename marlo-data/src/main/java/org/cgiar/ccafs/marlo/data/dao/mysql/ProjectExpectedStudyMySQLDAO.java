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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyDAO;
import org.cgiar.ccafs.marlo.data.dto.StudyHomeDTO;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.RepIndOrganizationType;
import org.cgiar.ccafs.marlo.utils.ListResultTransformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.AliasToEntityMapResultTransformer;

@Named
public class ProjectExpectedStudyMySQLDAO extends AbstractMarloDAO<ProjectExpectedStudy, Long>
		implements ProjectExpectedStudyDAO {

	@Inject
	public ProjectExpectedStudyMySQLDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public void deleteProjectExpectedStudy(long projectExpectedStudyId) {
		ProjectExpectedStudy projectExpectedStudy = this.find(projectExpectedStudyId);
		projectExpectedStudy.setActive(false);
		this.save(projectExpectedStudy);
	}

	@Override
	public boolean existProjectExpectedStudy(long projectExpectedStudyID) {
		ProjectExpectedStudy projectExpectedStudy = this.find(projectExpectedStudyID);
		if (projectExpectedStudy == null) {
			return false;
		}
		return true;

	}

	@Override
	public ProjectExpectedStudy find(long id) {
		return super.find(ProjectExpectedStudy.class, id);

	}

	@Override
	public List<ProjectExpectedStudy> findAll() {
		String query = "from " + ProjectExpectedStudy.class.getName() + " where is_active=1";
		List<ProjectExpectedStudy> list = super.findAll(query);
		if (list.size() > 0) {
			return list;
		}
		return null;

	}

	@Override
	public List<ProjectExpectedStudy> getAllStudiesByPhase(long phaseId) {
		String query = "SELECT DISTINCT pes.id AS id FROM ProjectExpectedStudy pes, ProjectExpectedStudyInfo pesi "
				+ "where pesi.projectExpectedStudy = pes and pes.active = true AND pesi.phase.id = :phaseId";

		Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
		createQuery.setParameter("phaseId", phaseId);
		createQuery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
		createQuery.setFlushMode(FlushMode.COMMIT);

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> rList = createQuery.list();
		List<ProjectExpectedStudy> projectExpectedStudies = new ArrayList<>();

		if (rList != null) {
			for (Map<String, Object> map : rList) {
				ProjectExpectedStudy projectExpectedStudy = this.find(Long.parseLong(map.get("id").toString()));
				projectExpectedStudies.add(projectExpectedStudy);
			}
		}

		return projectExpectedStudies;
	}

	@Override
	public List<ProjectExpectedStudy> getStudiesByOrganizationType(RepIndOrganizationType repIndOrganizationType,
			Phase phase) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT DISTINCT  ");
		query.append("s.id as id ");
		query.append("FROM ");
		query.append("project_expected_studies AS s ");
		query.append("INNER JOIN project_expected_study_info AS si ON si.project_expected_study_id = s.id ");
		query.append("INNER JOIN rep_ind_organization_types ot ON ot.id = si.rep_ind_organization_type_id ");
		query.append("WHERE s.is_active = 1 AND ");
		query.append("s.`year` =" + phase.getYear() + " AND ");
		query.append("si.`id_phase` =" + phase.getId() + " AND ");
		query.append("si.`is_contribution` = 1 AND ");
		query.append("ot.`id` =" + repIndOrganizationType.getId());

		List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
		List<ProjectExpectedStudy> projectExpectedStudies = new ArrayList<>();

		if (rList != null) {
			for (Map<String, Object> map : rList) {
				ProjectExpectedStudy projectExpectedStudy = this.find(Long.parseLong(map.get("id").toString()));
				projectExpectedStudies.add(projectExpectedStudy);
			}
		}

		return projectExpectedStudies;

	}

	@Override
	public List<ProjectExpectedStudy> getStudiesByPhase(Phase phase) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT DISTINCT  ");
		query.append("s.id as id ");
		query.append("FROM ");
		query.append("project_expected_studies AS s ");
		query.append("INNER JOIN project_expected_study_info AS si ON si.project_expected_study_id = s.id ");
		query.append("WHERE s.is_active = 1 AND ");
		query.append("si.`is_contribution` = 1 AND ");
		query.append("s.`year` =" + phase.getYear() + " AND ");
		query.append("si.`id_phase` =" + phase.getId());

		List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
		List<ProjectExpectedStudy> projectExpectedStudies = new ArrayList<>();

		if (rList != null) {
			for (Map<String, Object> map : rList) {
				ProjectExpectedStudy projectExpectedStudy = this.find(Long.parseLong(map.get("id").toString()));
				projectExpectedStudies.add(projectExpectedStudy);
			}
		}

		return projectExpectedStudies;
	}

	@Override
	public List<StudyHomeDTO> getStudiesByProjectAndPhaseHome(long phaseId, long projectId) {
		String query = "select pes.id as studyId, pesi.year as expectedYear, pr.id as projectId, "
				+ "coalesce(st.name, 'Not Provided') as studyType, "
				+ "(case when coalesce(pesi.title, '') = '' then 'Not Provided' else pesi.title end) as studyTitle, "
				+ "coalesce(riss.name, 'Not Defined') as studyMaturity " + "from project_expected_studies pes "
				+ "join project_expected_study_info pesi on pesi.project_expected_study_id = pes.id "
				+ "join phases ph on ph.id = pesi.id_phase " + "join projects pr on pes.project_id = pr.id "
				+ "left join study_types st on pesi.study_type_id = st.id "
				+ "left join rep_ind_stage_studies riss on pesi.rep_ind_stage_study_id = riss.id "
				+ "where ph.id = :phaseId and pr.id = :projectId and pesi.year = ph.year and pes.is_active";

		Query createQuery = this.getSessionFactory().getCurrentSession().createSQLQuery(query);

		createQuery.setParameter("phaseId", phaseId);
		createQuery.setParameter("projectId", projectId);

		createQuery.setResultTransformer(
				(ListResultTransformer) (tuple, aliases) -> new StudyHomeDTO(((Number) tuple[0]).longValue(),
						((Number) tuple[1]).longValue(), ((Number) tuple[2]).longValue(), (String) tuple[3],
						(String) tuple[4], (String) tuple[5]));
		createQuery.setFlushMode(FlushMode.COMMIT);

		List<StudyHomeDTO> studies = createQuery.list();

		return studies;
	}

	@Override
	public List<Map<String, Object>> getUserStudies(long userId, String crp) {
		List<Map<String, Object>> list = new ArrayList<>();
		StringBuilder builder = new StringBuilder();
		builder.append("select DISTINCT project_id from user_permission where permission_id in "
				+ "(select id from permissions where permission = 'crp:{0}:studies:{1}:canEdit')");
		if (super.getTemTableUserId() == userId) {
			list = super.findCustomQuery(builder.toString());
		} else {
			list = super.excuteStoreProcedure(" call getPermissions(" + userId + ")", builder.toString());
		}
		return list;
	}

	@Override
	public Boolean isStudyExcluded(Long projectExpectedStudyId, Long phaseId, Long typeStudy) {
		StringBuilder query = new StringBuilder();
		if (typeStudy.longValue() == 1) {
			query.append("select is_study_excluded(" + projectExpectedStudyId.longValue() + "," + phaseId.longValue()
					+ ") as isStudyExcluded");
		} else {
			query.append("select is_melia_excluded(" + projectExpectedStudyId.longValue() + "," + phaseId.longValue()
					+ ") as isStudyExcluded");
		}

		List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
		if (rList != null) {
			for (Map<String, Object> map : rList) {
				if (Long.parseLong(map.get("isStudyExcluded").toString()) == 0) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public ProjectExpectedStudy save(ProjectExpectedStudy projectExpectedStudy) {
		if (projectExpectedStudy.getId() == null) {
			super.saveEntity(projectExpectedStudy);
		} else {
			projectExpectedStudy = super.update(projectExpectedStudy);
		}

		return projectExpectedStudy;
	}

	@Override
	public ProjectExpectedStudy save(ProjectExpectedStudy projectExpectedStudy, String section,
			List<String> relationsName, Phase phase) {
		if (projectExpectedStudy.getId() == null) {
			super.saveEntity(projectExpectedStudy, section, relationsName, phase);
		} else {
			projectExpectedStudy = super.update(projectExpectedStudy, section, relationsName, phase);
		}
		return projectExpectedStudy;
	}

}