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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.FileDBDAO;
import org.cgiar.ccafs.marlo.data.manager.FileDBManager;
import org.cgiar.ccafs.marlo.data.model.FileDB;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class FileDBManagerImpl implements FileDBManager {


  private FileDBDAO fileDBDAO;
  // Managers


  @Inject
  public FileDBManagerImpl(FileDBDAO fileDBDAO) {
    this.fileDBDAO = fileDBDAO;


  }

  @Override
  public void deleteFileDB(long fileDBId) {

    fileDBDAO.deleteFileDB(fileDBId);
  }

  @Override
  public boolean existFileDB(long fileDBID) {

    return fileDBDAO.existFileDB(fileDBID);
  }

  @Override
  public List<FileDB> findAll() {

    return fileDBDAO.findAll();

  }

  @Override
  public FileDB getFileDBById(long fileDBID) {

    return fileDBDAO.find(fileDBID);
  }

  @Override
  public FileDB saveFileDB(FileDB fileDB) {

    return fileDBDAO.save(fileDB);
  }


}
