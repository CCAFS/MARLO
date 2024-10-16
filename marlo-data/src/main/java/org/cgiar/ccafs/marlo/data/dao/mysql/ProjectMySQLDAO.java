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

import org.cgiar.ccafs.marlo.data.dao.CrpProgramDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectInfoDAO;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class ProjectMySQLDAO extends AbstractMarloDAO<Project, Long> implements ProjectDAO {

  private ProjectInfoDAO projectInfoDAO;
  private CrpProgramDAO crpProgramDAO;
  private APConfig apConfig;

  private Logger LOG = LoggerFactory.getLogger(ProjectMySQLDAO.class);

  @Inject
  public ProjectMySQLDAO(SessionFactory sessionFactory, ProjectInfoDAO projectInfoDAO, CrpProgramDAO crpProgramDAO,
    APConfig apConfig) {
    super(sessionFactory);
    this.projectInfoDAO = projectInfoDAO;

    this.crpProgramDAO = crpProgramDAO;
    this.apConfig = apConfig;
  }


  public boolean deleteOnCascade(String tableName, String columnName, Long columnValue, long userID,
    String justification) {

    StringBuilder query = new StringBuilder();


    try {

      // Let's find all the tables that are related to the current table.
      query.append("SELECT * FROM information_schema.KEY_COLUMN_USAGE ");
      query.append("WHERE TABLE_SCHEMA = '");
      query.append(apConfig.getMysqlDatabase());
      query.append("' ");
      query.append("AND REFERENCED_TABLE_NAME = '");
      query.append(tableName);
      query.append("' ");
      query.append("AND REFERENCED_COLUMN_NAME = '");
      query.append(columnName);
      query.append("' ");
      //

      List<Map<String, Object>> rsReferences = super.findCustomQuery(query.toString());


      String table, column;

      for (Map<String, Object> map : rsReferences) {
        table = map.get("TABLE_NAME").toString();
        column = map.get("COLUMN_NAME").toString();

        query.setLength(0);
        query.append("SELECT COUNT(*) FROM information_schema.COLUMNS ");
        query.append("WHERE TABLE_SCHEMA = '");
        query.append(apConfig.getMysqlDatabase());
        query.append("' ");
        query.append("AND TABLE_NAME = '");
        query.append(table);
        query.append("' ");
        query.append("AND COLUMN_NAME = 'is_active'");
        List<Map<String, Object>> rsColumnExist = super.findCustomQuery(query.toString());
        if (!rsColumnExist.isEmpty()) {
          try {
            query.setLength(0);
            query.append("UPDATE ");
            query.append(table);
            query.append(" SET is_active = 0, modified_by = " + userID + ", modification_justification = '"
              + justification + "' ");
            query.append("WHERE ");
            query.append(column);
            query.append(" = '" + columnValue + "'");

            super.executeUpdateQuery(query.toString());
          } catch (Exception e) {

            // Adding logging, but really we should re-throw the exception.
            LOG.error("Unable to execute query: " + query, e);

          }


        }
      }


    } catch (Exception e)

    {
      // Adding logging, but really we should re-throw the exception.
      LOG.error("Unable to execute query: " + query, e);
      return false;
    }

    return true;

  }

  @Override
  public void deleteProject(Project project) {

    this.deleteOnCascade("projects", "id", project.getId(), project.getModifiedBy().getId(),
      project.getModificationJustification());
    project.setActive(false);
    this.save(project);
  }

  @Override
  public boolean existProject(long projectID) {
    Project project = this.find(projectID);
    if (project == null) {
      return false;
    }
    return true;

  }

  @Override
  public Project find(long id) {
    Project project = super.find(Project.class, id);

    return project;

  }


  @Override
  public List<Project> findAll() {
    String query = "from " + Project.class.getName() + " where is_active=1";
    List<Project> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }


  @Override
  public Integer findAllQuantity() {
    StringBuilder query = new StringBuilder();
    query.append("select count(*) as count from projects p where is_active =1 ");

    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    int result = 0;
    if (rList != null) {
      for (Map<String, Object> map : rList) {
        result = Integer.parseInt(map.get("count").toString());
      }
    }

    return result;
  }

  @Override
  public List<Project> getActiveProjectsByPhase(Phase phase, int year, String[] projectStatuses) {
    StringBuilder query = new StringBuilder();
    query.append(
      "select distinct p.id as projectId,pi.id as info from projects p inner join projects_info pi on pi.project_id=p.id");
    query.append(" where p.is_active");
    query.append(" and pi.is_active");
    if (year != 0) {
      query.append(" and YEAR(pi.start_date) <= " + year);
      query.append(" and YEAR(pi.end_date) >= " + year);
    }
    query.append(" and pi.id_phase =" + phase.getId());
    if (projectStatuses != null && projectStatuses.length > 0) {
      int size = projectStatuses.length;
      if (size == 1) {
        query.append(" and pi.status = " + projectStatuses[0]);
      } else {

        for (int i = 0; i < size; i++) {
          projectStatuses[i] = "pi.status=" + projectStatuses[i];
        }
        query.append(" and (");
        query.append(String.join(" or ", projectStatuses));
        query.append(" )");
      }
    }
    query.append(" ORDER BY p.id ASC");
    List<Map<String, Object>> list = super.findCustomQuery(query.toString());

    List<Project> projects = new ArrayList<Project>();
    for (Map<String, Object> map : list) {
      Project project = this.find(Long.parseLong(map.get("projectId").toString()));
      project.setProjectInfo(projectInfoDAO.find(Long.parseLong(map.get("info").toString())));
      projects.add(project);
    }
    return projects;
  }

  @Override
  public List<Project> getCompletedProjects(long crpId, long phaseID) {
    StringBuilder query = new StringBuilder();
    query.append(
      "select distinct p.id as projectId,pi.id as info  from projects p inner join projects_info pi on pi.project_id=p.id inner join global_unit_projects gup on gup.project_id = p.id ");
    query.append("where p.is_active=1 and gup.`origin`=1 and gup.global_unit_id=");
    query.append(crpId);
    query.append(" and pi.`status` in (" + ProjectStatusEnum.Cancelled.getStatusId() + " , "
      + ProjectStatusEnum.Complete.getStatusId() + " ) and pi.id_phase=" + phaseID);
    List<Map<String, Object>> list = super.findCustomQuery(query.toString());

    List<Project> projects = new ArrayList<Project>();
    for (Map<String, Object> map : list) {
      Project project = this.find(Long.parseLong(map.get("projectId").toString()));
      project.setProjectInfo(projectInfoDAO.find(Long.parseLong(map.get("info").toString())));
      projects.add(project);
    }
    return projects;

  }

  @Override
  public List<Project> getNoPhaseProjects(long crpId, Phase phase) {

    StringBuilder builder = new StringBuilder();
    builder.append("select distinct  p.* from projects p inner JOIN project_phases ph ");
    builder.append("on ph.project_id=p.id ");
    builder.append("inner join phases fa on fa.id=ph.id_phase ");
    builder.append("inner join global_unit_projects gup on gup.project_id = p.id ");
    builder.append("where p.is_active=1 and gup.global_unit_id=" + crpId + " ");
    builder.append("and (select COUNT('x') from project_phases ph2 where ph2.id_phase in (" + phase.getId()
      + ") and ph2.project_id=p.id) =0 ");
    builder.append("and fa.`year`<" + phase.getYear() + " ;");
    List<Project> list = new ArrayList<>();
    List<Map<String, Object>> maps = super.findCustomQuery(builder.toString());
    for (Map<String, Object> map : maps) {
      Project project = this.find(Long.parseLong(map.get("id").toString()));
      project.getProjectInfoLast(phase);
      if (project.getProjectInfo().getStatus().intValue() == Integer
        .parseInt(ProjectStatusEnum.Ongoing.getStatusId())) {

        project.getProjectInfo().setStatusName(ProjectStatusEnum.Complete.getStatus());
      }

      list.add(project);
    }
    return list;

  }

  @Override
  public List<CrpProgram> getPrograms(long projectID, int type, long idPhase) {
    StringBuilder builder = new StringBuilder();
    builder.append(" select pr.* from project_focuses pf  inner join crp_programs pr on pf.program_id=pr.id");
    builder.append(" where pf.project_id= " + projectID + " and pf.id_phase=" + idPhase + " and pr.program_type=" + type
      + " and pf.is_active=1");
    List<CrpProgram> list = new ArrayList<>();
    List<Map<String, Object>> maps = super.findCustomQuery(builder.toString());
    for (Map<String, Object> map : maps) {
      list.add(crpProgramDAO.find(Long.parseLong(map.get("id").toString())));

    }
    return list;
  }


  @Override
  public List<Project> getProjectWebPageList(Long globalunit_id) {
    List<Project> projectList = new ArrayList<Project>();
    StringBuilder query = new StringBuilder();
    query.append("SELECT projects.* FROM projects ");
    query.append("INNER JOIN project_webpage ON projects.id=project_webpage.project_id ");
    query.append("INNER JOIN global_unit_projects gup on gup.project_id = projects.id ");
    query.append("WHERE project_webpage.is_active=1 and gup.origin=1 and gup.global_unit_id=" + globalunit_id);
    List<Map<String, Object>> list = super.findCustomQuery(query.toString());
    for (Map<String, Object> map : list) {
      Project project = this.find(Long.parseLong(map.get("id").toString()));
      projectList.add(project);
    }
    return projectList;
  }


  @Override
  public List<Map<String, Object>> getUserProjects(long userId, String crp) {
    List<Map<String, Object>> list = new ArrayList<>();
    StringBuilder builder = new StringBuilder();
    builder.append("select DISTINCT project_id from user_permission where crp_acronym='" + crp
      + "' and project_id is not null and  permission LIKE '%project%'");

    if (super.getTemTableUserId() == userId) {
      list = super.findCustomQuery(builder.toString());
    } else {
      list = super.excuteStoreProcedure(" call getPermissions(" + userId + ")", builder.toString());
    }

    return list;
  }


  @Override
  public Project save(Project project) {
    if (project.getId() == null) {
      super.saveEntity(project);
    } else {
      project = super.update(project);
    }


    return project;
  }

  @Override
  public Project save(Project project, String sectionName, List<String> relationsName) {
    if (project.getId() == null) {
      project = super.saveEntity(project, sectionName, relationsName);
    } else {
      project = super.update(project, sectionName, relationsName);
    }


    return project;
  }


  @Override
  public Project save(Project project, String sectionName, List<String> relationsName, Phase phase) {
    if (project.getId() == null) {
      project = super.saveEntity(project, sectionName, relationsName, phase);
    } else {
      project = super.update(project, sectionName, relationsName, phase);
    }


    return project;
  }

}