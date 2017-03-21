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

package org.cgiar.ccafs.marlo.action.rest;

import org.cgiar.ccafs.marlo.action.repository.InstitutionsRepository;

import com.google.inject.Inject;
import com.opensymphony.xwork2.ModelDriven;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class InstitutionController implements ModelDriven<Object> {


  private String id;
  private Object institutions;


  private InstitutionsRepository repository;

  @Inject
  public InstitutionController(InstitutionsRepository repository) {
    this.repository = repository;
    institutions = repository.findAll();
  }


  public String getId() {
    return id;
  }


  public Object getInstitutions() {
    return institutions;
  }


  @Override
  public Object getModel() {
    if (institutions == null) {
      return institutions;
    } else {
      return institutions;
    }
  }


  public InstitutionsRepository getRepository() {
    return repository;
  }


  public HttpHeaders index() {
    return new DefaultHttpHeaders("index").disableCaching();
  }


  public void setId(String id) {
    this.id = id;
  }


  public void setInstitutions(Object institutions) {
    this.institutions = institutions;
  }

  public void setRepository(InstitutionsRepository repository) {
    this.repository = repository;
  }


  public HttpHeaders show() {
    institutions = repository.get(this.getId());
    return new DefaultHttpHeaders("show").disableCaching();
  }

}
