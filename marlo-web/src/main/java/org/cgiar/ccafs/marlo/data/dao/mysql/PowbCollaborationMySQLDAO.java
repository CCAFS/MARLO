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

import org.cgiar.ccafs.marlo.data.dao.PowbCollaborationDAO;
import org.cgiar.ccafs.marlo.data.model.PowbCollaboration;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class PowbCollaborationMySQLDAO extends AbstractMarloDAO<PowbCollaboration, Long> implements PowbCollaborationDAO {


  @Inject
  public PowbCollaborationMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deletePowbCollaboration(long powbCollaborationId) {
    PowbCollaboration powbCollaboration = this.find(powbCollaborationId);
    powbCollaboration.setActive(false);
    this.update(powbCollaboration);
  }

  @Override
  public boolean existPowbCollaboration(long powbCollaborationID) {
    PowbCollaboration powbCollaboration = this.find(powbCollaborationID);
    if (powbCollaboration == null) {
      return false;
    }
    return true;

  }

  @Override
  public PowbCollaboration find(long id) {
    return super.find(PowbCollaboration.class, id);

  }

  @Override
  public List<PowbCollaboration> findAll() {
    String query = "from " + PowbCollaboration.class.getName() + " where is_active=1";
    List<PowbCollaboration> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public PowbCollaboration save(PowbCollaboration powbCollaboration) {
    if (powbCollaboration.getId() == null) {
      super.saveEntity(powbCollaboration);
    } else {
      powbCollaboration = super.update(powbCollaboration);
    }


    return powbCollaboration;
  }


}