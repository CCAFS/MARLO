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

import org.cgiar.ccafs.marlo.data.dao.IpLiaisonInstitutionDAO;
import org.cgiar.ccafs.marlo.data.model.IpLiaisonInstitution;

import java.util.List;
import java.util.Map;

import com.google.inject.Inject;

public class IpLiaisonInstitutionMySQLDAO implements IpLiaisonInstitutionDAO {

  private StandardDAO dao;

  @Inject
  public IpLiaisonInstitutionMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteIpLiaisonInstitution(long ipLiaisonInstitutionId) {
    IpLiaisonInstitution ipLiaisonInstitution = this.find(ipLiaisonInstitutionId);
    return dao.delete(ipLiaisonInstitution);
  }

  @Override
  public boolean existIpLiaisonInstitution(long ipLiaisonInstitutionID) {
    IpLiaisonInstitution ipLiaisonInstitution = this.find(ipLiaisonInstitutionID);
    if (ipLiaisonInstitution == null) {
      return false;
    }
    return true;

  }

  @Override
  public IpLiaisonInstitution find(long id) {
    return dao.find(IpLiaisonInstitution.class, id);

  }

  @Override
  public List<IpLiaisonInstitution> findAll() {
    String query = "from " + IpLiaisonInstitution.class.getName();
    List<IpLiaisonInstitution> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public IpLiaisonInstitution findByIpProgram(long ipProgramID) {
    String query = "from " + IpLiaisonInstitution.class.getName() + " where ip_program=" + ipProgramID;
    List<IpLiaisonInstitution> list = dao.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public List<IpLiaisonInstitution> getLiaisonInstitutionsCrpsIndicator() {
    String query = "from " + IpLiaisonInstitution.class.getName() + " where where id not in(1)";
    List<IpLiaisonInstitution> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public List<Map<String, Object>> getLiaisonInstitutionSynthesisByMog() {
    String query = "select id FROM liaison_institutions li where id  in (2,3,4,5,6,7,8,9,10)";
    return dao.findCustomQuery(query);
  }

  @Override
  public long save(IpLiaisonInstitution ipLiaisonInstitution) {
    if (ipLiaisonInstitution.getId() == null) {
      dao.save(ipLiaisonInstitution);
    } else {
      dao.update(ipLiaisonInstitution);
    }


    return ipLiaisonInstitution.getId();
  }


}