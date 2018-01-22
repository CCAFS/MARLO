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

import org.cgiar.ccafs.marlo.data.dao.IpElementDAO;
import org.cgiar.ccafs.marlo.data.model.IpElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class IpElementMySQLDAO extends AbstractMarloDAO<IpElement, Long> implements IpElementDAO {


  @Inject
  public IpElementMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteIpElement(long ipElementId) {
    IpElement ipElement = this.find(ipElementId);
    ipElement.setActive(false);
    this.save(ipElement);
  }

  @Override
  public boolean existIpElement(long ipElementID) {
    IpElement ipElement = this.find(ipElementID);
    if (ipElement == null) {
      return false;
    }
    return true;

  }

  @Override
  public IpElement find(long id) {
    return super.find(IpElement.class, id);

  }

  @Override
  public List<IpElement> findAll() {
    String query = "from " + IpElement.class.getName() + " where is_active=1";
    List<IpElement> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<IpElement> getIPElementByProgramIDSynthesis(long programID) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT e.id, e.description,  ");
    query.append("et.id as 'element_type_id', et.name as 'element_type_name', ");
    query.append("pro.id as 'program_id', pro.acronym as 'program_acronym' ");
    query.append("FROM ip_elements e ");
    query.append("INNER JOIN ip_element_types et ON e.element_type_id = et.id ");
    query.append("INNER JOIN ip_programs pro ON e.ip_program_id = pro.id ");
    query.append("WHERE pro.id = ");
    query.append(programID + " and et.id =4  ");
    query.append(" GROUP BY e.id");
    query.append(" ORDER BY et.id, pro.type_id ");

    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());

    List<IpElement> ipElements = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        IpElement ipElement = this.find(Long.parseLong(map.get("id").toString()));
        ipElements.add(ipElement);
      }
    }

    return ipElements;
  }

  @Override
  public List<IpElement> getIPElementListForOutcomeSynthesis(long programID, long type) {

    StringBuilder query = new StringBuilder();
    query.append("SELECT e.*");
    query.append("FROM ip_elements e ");
    query.append("INNER JOIN ip_element_types et ON e.element_type_id = et.id ");
    query.append("INNER JOIN ip_programs pro ON e.ip_program_id = pro.id ");
    query.append("WHERE pro.id = ");
    query.append(programID);
    query.append(" AND et.id = ");
    query.append(type);
    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());

    List<IpElement> ipElements = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        IpElement ipElement = this.find(Long.parseLong(map.get("id").toString()));
        ipElements.add(ipElement);
      }
    }

    return ipElements;
  }

  @Override
  public List<IpElement> getIPElementListForSynthesisRegion(long programId) {

    StringBuilder query = new StringBuilder();
    query.append("SELECT e.id, e.description, ");
    query.append("et.id as 'element_type_id', et.name as 'element_type_name', ");
    query.append("pro.id as 'program_id', pro.acronym as 'program_acronym', ");
    query.append("iep.id as 'parent_id', iep.description as 'parent_description' ");
    query.append("FROM ip_elements e ");
    query.append("INNER JOIN ip_element_types et ON e.element_type_id = et.id ");
    query.append("INNER JOIN ip_programs pro ON e.ip_program_id = pro.id ");
    query.append("INNER JOIN ip_project_contributions ipc ON e.id = ipc.mog_id ");
    query.append("INNER JOIN ip_elements iep ON iep.id = ipc.midOutcome_id ");
    query.append("WHERE project_id in (select project_id from project_focuses where program_id=" + programId + ")   ");

    query.append(" AND ipc.is_active = TRUE ");
    query.append("GROUP BY id order by  pro.acronym  ");


    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());

    List<IpElement> ipElements = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        IpElement ipElement = this.find(Long.parseLong(map.get("id").toString()));
        ipElements.add(ipElement);
      }
    }

    return ipElements;
  }

  @Override
  public List<IpElement> getIPElementsByParent(int parentId, int relationTypeID) {

    StringBuilder query = new StringBuilder();
    query.append("SELECT e.*");
    query.append("FROM ip_elements e ");
    query.append("INNER JOIN ip_relationships r ON e.id = r.child_id ");
    query.append("INNER JOIN ip_element_types et ON e.element_type_id = et.id ");
    query.append("INNER JOIN ip_programs pro ON e.ip_program_id = pro.id ");
    query.append("WHERE r.relation_type_id = ");
    query.append(relationTypeID);
    query.append(" AND r.parent_id = ");
    query.append(parentId);
    query.append(" GROUP BY e.id ");


    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    List<IpElement> ipElements = new ArrayList<>();
    if (rList != null) {
      for (Map<String, Object> map : rList) {
        IpElement ipElement = this.find(Long.parseLong(map.get("id").toString()));
        ipElements.add(ipElement);
      }
    }

    return ipElements;

  }

  @Override
  public List<IpElement> getIPElementsRelated(int ipElementID, int relationTypeID) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT e.* ");
    query.append("FROM ip_elements e ");
    query.append("INNER JOIN ip_relationships r ON e.id = r.parent_id AND r.child_id = ");
    query.append(ipElementID + " ");
    query.append("INNER JOIN ip_element_types et ON e.element_type_id = et.id ");
    query.append("INNER JOIN ip_programs pro ON e.ip_program_id = pro.id ");
    query.append("WHERE r.relation_type_id = ");
    query.append(relationTypeID);
    query.append(" GROUP BY e.id ");

    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    List<IpElement> ipElements = new ArrayList<>();
    if (rList != null) {
      for (Map<String, Object> map : rList) {
        IpElement ipElement = this.find(Long.parseLong(map.get("id").toString()));
        ipElements.add(ipElement);
      }
    }

    return ipElements;


  }

  @Override
  public IpElement save(IpElement ipElement) {
    if (ipElement.getId() == null) {
      super.saveEntity(ipElement);
    } else {
      ipElement = super.update(ipElement);
    }


    return ipElement;
  }


}