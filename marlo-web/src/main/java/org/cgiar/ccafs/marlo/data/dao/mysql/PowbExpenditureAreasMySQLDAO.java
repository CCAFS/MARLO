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

import org.cgiar.ccafs.marlo.data.dao.PowbExpenditureAreasDAO;
import org.cgiar.ccafs.marlo.data.model.PowbExpenditureAreas;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class PowbExpenditureAreasMySQLDAO extends AbstractMarloDAO<PowbExpenditureAreas, Long> implements PowbExpenditureAreasDAO {


  @Inject
  public PowbExpenditureAreasMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deletePowbExpenditureAreas(long powbExpenditureAreasId) {
    PowbExpenditureAreas powbExpenditureAreas = this.find(powbExpenditureAreasId);
    powbExpenditureAreas.setActive(false);
    this.save(powbExpenditureAreas);
  }

  @Override
  public boolean existPowbExpenditureAreas(long powbExpenditureAreasID) {
    PowbExpenditureAreas powbExpenditureAreas = this.find(powbExpenditureAreasID);
    if (powbExpenditureAreas == null) {
      return false;
    }
    return true;

  }

  @Override
  public PowbExpenditureAreas find(long id) {
    return super.find(PowbExpenditureAreas.class, id);

  }

  @Override
  public List<PowbExpenditureAreas> findAll() {
    String query = "from " + PowbExpenditureAreas.class.getName() + " where is_active=1";
    List<PowbExpenditureAreas> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public PowbExpenditureAreas save(PowbExpenditureAreas powbExpenditureAreas) {
    if (powbExpenditureAreas.getId() == null) {
      super.saveEntity(powbExpenditureAreas);
    } else {
      powbExpenditureAreas = super.update(powbExpenditureAreas);
    }


    return powbExpenditureAreas;
  }


}