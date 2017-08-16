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

import org.cgiar.ccafs.marlo.data.dao.InstitutionLocationDAO;
import org.cgiar.ccafs.marlo.data.model.InstitutionLocation;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class InstitutionLocationMySQLDAO extends AbstractMarloDAO<InstitutionLocation, Long> implements InstitutionLocationDAO {


  @Inject
  public InstitutionLocationMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public boolean deleteInstitutionLocation(long institutionLocationId) {
    InstitutionLocation institutionLocation = this.find(institutionLocationId);

    return super.delete(institutionLocation);
  }

  @Override
  public boolean existInstitutionLocation(long institutionLocationID) {
    InstitutionLocation institutionLocation = this.find(institutionLocationID);
    if (institutionLocation == null) {
      return false;
    }
    return true;

  }

  @Override
  public InstitutionLocation find(long id) {
    return super.find(InstitutionLocation.class, id);

  }

  @Override
  public List<InstitutionLocation> findAll() {
    String query = "from " + InstitutionLocation.class.getName() + " ";
    List<InstitutionLocation> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public InstitutionLocation findByLocation(long locationID, long institutionID) {
    String query = "from " + InstitutionLocation.class.getName() + "  where institution_id=" + institutionID
      + " and loc_element_id= " + locationID;
    List<InstitutionLocation> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;

  }

  @Override
  public long save(InstitutionLocation institutionLocation) {
    if (institutionLocation.getId() == null) {
      super.saveEntity(institutionLocation);
    } else {
      super.update(institutionLocation);
    }


    return institutionLocation.getId();
  }


}