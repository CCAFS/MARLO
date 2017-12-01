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

package org.cgiar.ccafs.marlo.action.repository;

import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.model.Institution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named
public class InstitutionsRepository {

  private static Map<String, Object> institutions = new HashMap<String, Object>();

  private final InstitutionManager institutionManager;

  @Inject
  public InstitutionsRepository(InstitutionManager institutionManager) {
    this.institutionManager = institutionManager;

  }


  public Map<String, Object> findAll() {
    System.out.println("Entra");
    List<Institution> institutionsList =
      new ArrayList<>(institutionManager.findAll().stream().filter(i -> i.isActive()).collect(Collectors.toList()));

    for (Institution institution : institutionsList) {

      Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

      institutions.put(String.valueOf(institution.getId()), gson.toJson(institution));
    }
    return institutions;
  }

  public Object get(String id) {
    Object obj = institutions.get(id);
    return obj;
  }

  public Map<String, Object> remove(String id) {
    institutions.remove(id);
    return institutions;
  }

  public Map<String, Object> save(Object institution) {
    String id = String.valueOf(institutions.size() + 1);
    institutions.put(id, institution);
    return institutions;
  }
}
