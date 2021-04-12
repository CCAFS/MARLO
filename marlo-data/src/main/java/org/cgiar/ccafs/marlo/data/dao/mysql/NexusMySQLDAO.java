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

import org.cgiar.ccafs.marlo.data.dao.NexusDAO;
import org.cgiar.ccafs.marlo.data.model.Nexus;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class NexusMySQLDAO extends AbstractMarloDAO<Nexus, Long> implements NexusDAO {


  @Inject
  public NexusMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteNexus(long nexusId) {
    Nexus nexus = this.find(nexusId);
    this.update(nexus);
  }

  @Override
  public boolean existNexus(long nexusID) {
    Nexus nexus = this.find(nexusID);
    if (nexus == null) {
      return false;
    }
    return true;

  }

  @Override
  public Nexus find(long id) {
    return super.find(Nexus.class, id);

  }

  @Override
  public List<Nexus> findAll() {
    String query = "from " + Nexus.class.getName();
    List<Nexus> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public Nexus save(Nexus nexus) {
    if (nexus.getId() == null) {
      super.saveEntity(nexus);
    } else {
      nexus = super.update(nexus);
    }


    return nexus;
  }


}