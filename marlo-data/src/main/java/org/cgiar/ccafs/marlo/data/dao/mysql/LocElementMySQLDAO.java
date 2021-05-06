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

import org.cgiar.ccafs.marlo.data.dao.LocElementDAO;
import org.cgiar.ccafs.marlo.data.model.LocElement;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.type.StandardBasicTypes;

@Named
public class LocElementMySQLDAO extends AbstractMarloDAO<LocElement, Long> implements LocElementDAO {

  @Inject
  public LocElementMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteLocElement(long locElementId) {
    LocElement locElement = this.find(locElementId);
    locElement.setActive(false);
    this.save(locElement);
  }

  @Override
  public boolean existLocElement(long locElementID) {
    LocElement locElement = this.find(locElementID);
    if (locElement == null) {
      return false;
    }
    return true;

  }

  @Override
  public LocElement find(long id) {
    return super.find(LocElement.class, id);

  }

  @Override
  public List<LocElement> findAll() {
    String query = "from " + LocElement.class.getName() + " where is_active=1";
    List<LocElement> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<LocElement> findAllLocationMap(Long phase) {
    StringBuilder q = new StringBuilder();

    q.append(
      "select LocElement.id as id,LocElement.name as name,LocElement.isoAlpha2 as isoAlpha2,LocElement.isoNumeric as isoNumeric from (select le.id as id,le.name as name,le.iso_alpha_2 as isoAlpha2,le.iso_numeric as isoNumeric "
        + ",(select count(1) from deliverable_locations WHERE loc_element_id=le.id ) as deliverables "
        + ",(select count(1) from project_locations WHERE loc_element_id=le.id ) as projects "
        + ",(select count(1) from project_innovation_countries WHERE id_country=le.id ) as innovations "
        + ",(select count(1) from project_expected_study_countries WHERE loc_element_id=le.id ) as studies "
        + ",(select count(1) from project_partner_locations ppl  "
        + "INNER JOIN institutions_locations il ON il.id=ppl.institution_loc_id WHERE il.loc_element_id=le.id ) as partners "
        + ",(select count(1) from funding_source_locations  WHERE loc_element_id=le.id ) as fundingsources "
        + "from loc_elements le where le.element_type_id=2) as LocElement where LocElement.deliverables+LocElement.projects+LocElement.innovations+studies+partners+fundingsources>0 ");
    Query query = this.getSessionFactory().getCurrentSession().createSQLQuery(q.toString()).addScalar("id",
      StandardBasicTypes.LONG);
    query.setResultTransformer(new AliasToBeanResultTransformer(LocElement.class));
    List<LocElement> result = query.list();
    return result;
  }

  @Override
  public LocElement findISOCode(String ISOcode) {
    String query = "from " + LocElement.class.getName() + " where iso_alpha_2='" + ISOcode + "'";
    List<LocElement> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public List<LocElement> findLocElementByParent(Long parentId) {
    String query = "from " + LocElement.class.getName() + " where parent_id='" + parentId + "'";
    List<LocElement> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public LocElement findNumericISOCode(Long ISOcode) {
    String query = "from " + LocElement.class.getName() + " where iso_numeric=" + ISOcode;
    List<LocElement> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public LocElement save(LocElement locElement) {
    if (locElement.getId() == null) {
      super.saveEntity(locElement);
    } else {
      locElement = super.update(locElement);
    }
    return locElement;
  }

}