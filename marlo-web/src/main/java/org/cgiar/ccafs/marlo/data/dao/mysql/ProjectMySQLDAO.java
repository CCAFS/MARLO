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
import org.cgiar.ccafs.marlo.utils.PropertiesManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class ProjectMySQLDAO extends AbstractMarloDAO<Project, Long> implements ProjectDAO {

  private ProjectInfoDAO projectInfoDAO;
  private CrpProgramDAO crpProgramDAO;

  @Inject
  public ProjectMySQLDAO(SessionFactory sessionFactory, ProjectInfoDAO projectInfoDAO, CrpProgramDAO crpProgramDAO) {
    super(sessionFactory);
    this.projectInfoDAO = projectInfoDAO;

    this.crpProgramDAO = crpProgramDAO;
  }


  public boolean deleteOnCascade(String tableName, String columnName, Long columnValue, long userID,
    String justification) {

    StringBuilder query = new StringBuilder();

    PropertiesManager manager = new PropertiesManager();

    try {

      // Let's find all the tables that are related to the current table.
      query.append("SELECT * FROM information_schema.KEY_COLUMN_USAGE ");
      query.append("WHERE TABLE_SCHEMA = '");
      query.append(manager.getPropertiesAsString(APConfig.MYSQL_DATABASE));
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
        query.append(manager.getPropertiesAsString(APConfig.MYSQL_DATABASE));
        query.append("' ");
        query.append("AND TABLE_NAME = '");
        query.append(table);
        query.append("' ");
        query.append("AND COLUMN_NAME = 'is_active'");
        List<Map<String, Object>> rsColumnExist = super.findCustomQuery(query.toString());
        if (!rsColumnExist.isEmpty()) {
          query.setLength(0);
          query.append("UPDATE ");
          query.append(table);
          query.append(
            " SET is_active = 0, modified_by = " + userID + ", modification_justification = '" + justification + "' ");
          query.append("WHERE ");
          query.append(column);
          query.append(" = '" + columnValue + "'");

          super.executeUpdateQuery(query.toString());


        }
      }


    } catch (Exception e)

    {

      e.printStackTrace();
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
    return super.find(Project.class, id);

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
  public List<Project> getCompletedProjects(long crpId) {
    StringBuilder query = new StringBuilder();
    query.append(
      "select distinct p.id as projectId,pi.id as info  from projects p inner join projects_info pi on pi.project_id=p.id ");
    query.append("where p.is_active=1 and p.crp_id =");
    query.append(crpId);
    query.append(" and pi.`status` in (" + ProjectStatusEnum.Cancelled.getStatusId() + " , "
      + ProjectStatusEnum.Complete.getStatusId() + " )");
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
  public List<Map<String, Object>> getUserProjects(long userId, String crp) {

    StringBuilder builder = new StringBuilder();
    builder.append("select DISTINCT project_id from user_permission where crp_acronym='" + crp
      + "' and project_id is not null and  permission_id not in (438,462)");
    List<Map<String, Object>> list =
      super.excuteStoreProcedure(" call getPermissions(" + userId + ")", builder.toString());
    return list;
  }

  @Override
  public List<Map<String, Object>> getUserProjectsReporting(long userId, String crp) {

    StringBuilder builder = new StringBuilder();
    builder.append("select DISTINCT project_id from user_permission where crp_acronym='" + crp
      + "' and project_id is not null and  permission_id  in (110,195)");
    List<Map<String, Object>> list =
      super.excuteStoreProcedure(" call getPermissions(" + userId + ")", builder.toString());
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