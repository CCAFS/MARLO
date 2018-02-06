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

import org.cgiar.ccafs.marlo.data.dao.ICenterSectionStatusDAO;
import org.cgiar.ccafs.marlo.data.model.CenterSectionStatus;

import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class CenterSectionStatusDAO extends AbstractMarloDAO<CenterSectionStatus, Long>
  implements ICenterSectionStatusDAO {


  @Inject
  public CenterSectionStatusDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteSectionStatus(long sectionStatusId) {
    CenterSectionStatus sectionStatus = this.find(sectionStatusId);
    super.delete(sectionStatus);
  }

  @Override
  public List<Map<String, Object>> distinctSectionStatus(long programID) {
    String query = "select DISTINCT section_name from center_section_statuses where research_program_id=" + programID;
    return super.findCustomQuery(query);
  }

  @Override
  public List<Map<String, Object>> distinctSectionStatusCapDev(long capDevID) {
    String query = "select DISTINCT section_name from center_section_statuses where capdev_id=" + capDevID;
    return super.findCustomQuery(query);
  }

  @Override
  public List<Map<String, Object>> distinctSectionStatusProject(long projectID) {
    String query = "select DISTINCT section_name from center_section_statuses where project_id=" + projectID;
    return super.findCustomQuery(query);
  }

  @Override
  public boolean existSectionStatus(long sectionStatusID) {
    CenterSectionStatus sectionStatus = this.find(sectionStatusID);
    if (sectionStatus == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterSectionStatus find(long id) {
    return super.find(CenterSectionStatus.class, id);

  }

  @Override
  public List<CenterSectionStatus> findAll() {
    String query = "from " + CenterSectionStatus.class.getName();
    List<CenterSectionStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterSectionStatus> getSectionStatus(long programId, String sectionName) {
    String query = "from " + CenterSectionStatus.class.getName() + " where section_name='" + sectionName
      + "' and research_program_id=" + programId;
    List<CenterSectionStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public CenterSectionStatus getSectionStatusByCapdev(long capdevId, String sectionName, int year) {
    String query = "from " + CenterSectionStatus.class.getName() + " where section_name='" + sectionName
      + "' and capdev_id=" + capdevId + " and year=" + year;
    List<CenterSectionStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public CenterSectionStatus getSectionStatusByDeliverable(long deliverableId, long projectId, String sectionName,
    int year) {
    String query = "from " + CenterSectionStatus.class.getName() + " where section_name='" + sectionName
      + "' and project_id=" + projectId + " and deliverable_id=" + deliverableId + " and year=" + year;
    List<CenterSectionStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public CenterSectionStatus getSectionStatusByOutcome(long programId, long outcomeId, String sectionName, int year) {
    String query = "from " + CenterSectionStatus.class.getName() + " where section_name='" + sectionName
      + "' and research_program_id=" + programId + " and research_outcome_id=" + outcomeId + " and year=" + year;
    List<CenterSectionStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public CenterSectionStatus getSectionStatusByOutput(long programId, long outputId, String sectionName, int year) {
    String query = "from " + CenterSectionStatus.class.getName() + " where section_name='" + sectionName
      + "' and research_program_id=" + programId + " and research_output_id=" + outputId + " and year=" + year;
    List<CenterSectionStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public CenterSectionStatus getSectionStatusByProgram(long programId, String sectionName, int year) {
    String query = "from " + CenterSectionStatus.class.getName() + " where section_name='" + sectionName
      + "' and research_program_id=" + programId + " and year=" + year;
    List<CenterSectionStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public CenterSectionStatus getSectionStatusByProject(long programId, long projectId, String sectionName, int year) {
    String query = "from " + CenterSectionStatus.class.getName() + " where section_name='" + sectionName
      + "' and project_id=" + projectId + " and year=" + year;
    List<CenterSectionStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public CenterSectionStatus getSectionStatusBySupDocs(long deliverableId, long capDevId, String sectionName,
    int year) {
    String query = "from " + CenterSectionStatus.class.getName() + " where section_name='" + sectionName
      + "' and capdev_id=" + capDevId + " and deliverable_id=" + deliverableId + " and year=" + year;
    List<CenterSectionStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public List<CenterSectionStatus> getSectionStatussByUserId(long userId) {
    String query = "from " + CenterSectionStatus.class.getName() + " where user_id=" + userId;
    return super.findAll(query);
  }

  @Override
  public CenterSectionStatus save(CenterSectionStatus sectionStatus) {
    if (sectionStatus.getId() == null) {
      super.saveEntity(sectionStatus);
    } else {
      sectionStatus = super.update(sectionStatus);
    }
    return sectionStatus;
  }


}