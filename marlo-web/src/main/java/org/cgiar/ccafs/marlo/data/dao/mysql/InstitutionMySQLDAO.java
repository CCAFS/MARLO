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

import org.cgiar.ccafs.marlo.data.dao.InstitutionDAO;
import org.cgiar.ccafs.marlo.data.model.Institution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;


/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class InstitutionMySQLDAO extends AbstractMarloDAO<Institution, Long> implements InstitutionDAO {


  @Inject
  public InstitutionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteInstitution(long institutionId) {
    Institution institution = this.find(institutionId);
    super.delete(institution);
  }

  @Override
  public boolean existInstitution(long institutionId) {
    Institution institution = this.find(institutionId);
    if (institution == null) {
      return false;
    }
    return true;
  }

  @Override
  public Institution find(long id) {
    return super.find(Institution.class, id);
  }

  @Override
  public List<Institution> findAll() {
    String query = "from " + Institution.class.getName();
    List<Institution> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override

  public List<Institution> findPPAInstitutions(long crpID) {
    StringBuilder query = new StringBuilder();
    query.append("select inst.id from institutions inst INNER JOIN crp_ppa_partners ppa on ppa.institution_id=inst.id");
    query.append(" where ppa.crp_id=");
    query.append(crpID);
    query.append("  and ppa.is_active=1");
    List<Institution> institutions = new ArrayList<>();
    List<Map<String, Object>> queryValue = super.findCustomQuery(query.toString());
    for (Map<String, Object> map : queryValue) {
      institutions.add(this.find(Long.parseLong(map.get("id").toString())));
    }
    return institutions;
  }

  @Override
  public Institution save(Institution institution) {

    if (institution.getId() == null) {
      super.saveEntity(institution);
    } else {
      institution = super.update(institution);
    }
    return institution;
  }

  @Override
  public List<Institution> searchInstitution(String searchValue, int ppaPartner, int onlyPPA, long crpID) {
    StringBuilder query = new StringBuilder();

    query.append("from " + Institution.class.getName());
    query.append(" WHERE ");
    query.append("name like '%" + searchValue + "%' ");
    query.append("OR acronym like '%" + searchValue + "%' ");

    query.append("OR website_link like '%" + searchValue + "%' ");


    query.append("GROUP BY name ");
    query.append("ORDER BY CASE ");
    query.append("WHEN name like '" + searchValue + "%' THEN 0 ");
    query.append("WHEN name like '% %" + searchValue + "% %' THEN 1 ");
    query.append("WHEN name like '%" + searchValue + "' THEN 2 ");
    query.append("WHEN acronym like '" + searchValue + "%' THEN 3 ");
    query.append("WHEN acronym like '% %" + searchValue + "% %' THEN 4 ");
    query.append("WHEN acronym like '%" + searchValue + "' THEN 5 ");

    query.append("WHEN website_link like '%" + searchValue + "' THEN 6 ");
    query.append("WHEN website_link like '%" + searchValue + "' THEN 7 ");
    query.append("WHEN website_link like '%" + searchValue + "' THEN 8 ");
    query.append("ELSE 12 ");
    query.append("END, name, acronym,website_link ");


    List<Institution> institutions = super.findAll(query.toString());
    List<Institution> institutionsAux = new ArrayList<Institution>();

    if (onlyPPA == 1) {

      institutionsAux.addAll(institutions);
      for (Institution institution : institutionsAux) {
        if (institution.getCrpPpaPartners().stream()
          .filter(c -> c.isActive() && c.getCrp().getId().longValue() == crpID).collect(Collectors.toList())
          .isEmpty()) {
          institutions.remove(institution);
        }
      }
    } else {
      if (ppaPartner == 0) {
        institutionsAux.addAll(institutions);
        for (Institution institution : institutionsAux) {
          if (!institution.getCrpPpaPartners().stream()
            .filter(c -> c.isActive() && c.getCrp().getId().longValue() == crpID).collect(Collectors.toList())
            .isEmpty()) {
            institutions.remove(institution);
          }
        }
      }
    }


    return institutions;
  }

}
