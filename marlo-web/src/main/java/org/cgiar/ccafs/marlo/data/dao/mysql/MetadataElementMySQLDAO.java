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

import org.cgiar.ccafs.marlo.data.dao.MetadataElementDAO;
import org.cgiar.ccafs.marlo.data.model.MetadataElement;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class MetadataElementMySQLDAO extends AbstractMarloDAO implements MetadataElementDAO {


  @Inject
  public MetadataElementMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public boolean deleteMetadataElement(long metadataElementId) {
    MetadataElement metadataElement = this.find(metadataElementId);
    return this.save(metadataElement) > 0;
  }

  @Override
  public boolean existMetadataElement(long metadataElementID) {
    MetadataElement metadataElement = this.find(metadataElementID);
    if (metadataElement == null) {
      return false;
    }
    return true;

  }

  @Override
  public MetadataElement find(long id) {
    return super.find(MetadataElement.class, id);

  }

  @Override
  public List<MetadataElement> findAll() {
    String query = "from " + MetadataElement.class.getName();
    List<MetadataElement> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(MetadataElement metadataElement) {
    if (metadataElement.getId() == null) {
      super.save(metadataElement);
    } else {
      super.update(metadataElement);
    }


    return metadataElement.getId();
  }


}