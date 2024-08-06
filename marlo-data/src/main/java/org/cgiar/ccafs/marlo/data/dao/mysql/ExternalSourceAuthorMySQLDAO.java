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

import org.cgiar.ccafs.marlo.data.dao.ExternalSourceAuthorDAO;
import org.cgiar.ccafs.marlo.data.model.ExternalSourceAuthor;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

@Named
public class ExternalSourceAuthorMySQLDAO extends AbstractMarloDAO<ExternalSourceAuthor, Long>
  implements ExternalSourceAuthorDAO {

  @Inject
  public ExternalSourceAuthorMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteExternalSourceAuthor(long externalSourceAuthorId) {
    ExternalSourceAuthor externalSourceAuthor = this.find(externalSourceAuthorId);
    // externalSourceAuthor.setActive(false);
    this.delete(externalSourceAuthor);
  }

  @Override
  public boolean existExternalSourceAuthor(long externalSourceAuthorID) {
    ExternalSourceAuthor externalSourceAuthor = this.find(externalSourceAuthorID);
    if (externalSourceAuthor == null) {
      return false;
    }
    return true;

  }

  @Override
  public ExternalSourceAuthor find(long id) {
    return super.find(ExternalSourceAuthor.class, id);

  }

  @Override
  public List<ExternalSourceAuthor> findAll() {
    String query = "from " + ExternalSourceAuthor.class.getName();
    List<ExternalSourceAuthor> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ExternalSourceAuthor>
    findExternalSourceAuthorFromExternalSource(long deliverableMetadataExternalSourceId) {
    String query = "select distinct esa from ExternalSourceAuthor esa "
      + "where deliverableMetadataExternalSources.id = :deliverableMetadataExternalSourceId";
    Query<ExternalSourceAuthor> createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("deliverableMetadataExternalSourceId", deliverableMetadataExternalSourceId);
    List<ExternalSourceAuthor> result = super.findAll(createQuery);

    return result;
  }

  @Override
  public ExternalSourceAuthor save(ExternalSourceAuthor externalSourceAuthor) {
    if (externalSourceAuthor.getId() == null) {
      super.saveEntity(externalSourceAuthor);
    } else {
      externalSourceAuthor = super.update(externalSourceAuthor);
    }

    return externalSourceAuthor;
  }
}