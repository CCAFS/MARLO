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

import org.cgiar.ccafs.marlo.data.dao.FileDBDAO;
import org.cgiar.ccafs.marlo.data.model.FileDB;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class FileDBMySQLDAO extends AbstractMarloDAO<FileDB, Long> implements FileDBDAO {


  @Inject
  public FileDBMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteFileDB(long fileDBId) {
    FileDB fileDB = this.find(fileDBId);

    super.delete(fileDB);
  }

  @Override
  public boolean existFileDB(long fileDBID) {
    FileDB fileDB = this.find(fileDBID);
    if (fileDB == null) {
      return false;
    }
    return true;

  }

  @Override
  public FileDB find(long id) {
    return super.find(FileDB.class, id);

  }

  @Override
  public List<FileDB> findAll() {
    String query = "from " + FileDB.class.getName() + " where is_active=1";
    List<FileDB> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public FileDB save(FileDB fileDB) {
    if (fileDB.getId() == null) {
      super.saveEntity(fileDB);
    } else {
      fileDB = super.update(fileDB);
    }


    return fileDB;
  }


}