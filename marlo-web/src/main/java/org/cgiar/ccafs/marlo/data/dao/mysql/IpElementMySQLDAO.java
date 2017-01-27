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

import com.google.inject.Inject;

public class IpElementMySQLDAO implements IpElementDAO {

  private StandardDAO dao;

  @Inject
  public IpElementMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteIpElement(long ipElementId) {
    IpElement ipElement = this.find(ipElementId);
    ipElement.setActive(false);
    return this.save(ipElement) > 0;
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
    return dao.find(IpElement.class, id);

  }

  @Override
  public List<IpElement> findAll() {
    String query = "from " + IpElement.class.getName() + " where is_active=1";
    List<IpElement> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

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


    List<Map<String, Object>> rList = dao.findCustomQuery(query.toString());
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

    List<Map<String, Object>> rList = dao.findCustomQuery(query.toString());
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
  public long save(IpElement ipElement) {
    if (ipElement.getId() == null) {
      dao.save(ipElement);
    } else {
      dao.update(ipElement);
    }


    return ipElement.getId();
  }


}