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

import org.cgiar.ccafs.marlo.data.dao.GeneralAcronymDAO;
import org.cgiar.ccafs.marlo.data.model.GeneralAcronym;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class GeneralAcronymMySQLDAO extends AbstractMarloDAO<GeneralAcronym, Long> implements GeneralAcronymDAO {


  @Inject
  public GeneralAcronymMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteGeneralAcronym(long generalAcronymId) {
    GeneralAcronym generalAcronym = this.find(generalAcronymId);
    generalAcronym.setActive(false);
    this.update(generalAcronym);
  }

  @Override
  public boolean existGeneralAcronym(long generalAcronymID) {
    GeneralAcronym generalAcronym = this.find(generalAcronymID);
    if (generalAcronym == null) {
      return false;
    }
    return true;

  }

  @Override
  public GeneralAcronym find(long id) {
    return super.find(GeneralAcronym.class, id);

  }


  @Override
  public List<GeneralAcronym> findAll() {
    String query = "from " + GeneralAcronym.class.getName() + " where is_active=1";
    List<GeneralAcronym> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }


  @Override
  public List<GeneralAcronym> findByAcronym(String acronym) {
    String query = "from " + GeneralAcronym.class.getName() + " where is_active=1 and acronym='" + acronym + "'";
    List<GeneralAcronym> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public GeneralAcronym save(GeneralAcronym generalAcronym) {
    if (generalAcronym.getId() == null) {
      super.saveEntity(generalAcronym);
    } else {
      generalAcronym = super.update(generalAcronym);
    }


    return generalAcronym;
  }


}