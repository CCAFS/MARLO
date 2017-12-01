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

package org.cgiar.ccafs.marlo.rest;

import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.model.Crp;

import java.util.List;

import javax.inject.Inject;

import com.opensymphony.xwork2.ModelDriven;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is an alternative Controller that uses Entities instead of DTOs. Please see the getModelFromEntity method to see
 * how the response is serialized back to JSON.
 * Note that I didn't convert this to using Jackson library, as after call with Hector we decided to go down the DTO
 * approach.
 * 
 * @author GrantL
 */
public class CrpEntityController implements ModelDriven<Object> {

  @SuppressWarnings("unused")
  private static final long serialVersionUID = 1L;

  private static final Logger LOG = LoggerFactory.getLogger(CrpEntityController.class);

  private final CrpManager crpManager;

  private Crp crp = new Crp();

  private Long id;


  private List<Crp> crps;


  @Inject
  public CrpEntityController(CrpManager crpManager) {
    this.crpManager = crpManager;
  }


  // POST /orders
  public HttpHeaders create() {
    LOG.debug("Create new crp {}", crp);
    crpManager.saveCrp(crp);
    return new DefaultHttpHeaders("create");
  }

  // DELETE /orders/1
  public HttpHeaders destroy() {
    LOG.debug("Delete crp with id: {}", id);
    crpManager.deleteCrp(id);
    return new DefaultHttpHeaders("destroy");
  }

  // GET /orders/1/edit
  public HttpHeaders edit() {
    return new DefaultHttpHeaders("edit");
  }

  // GET /orders/new
  public String editNew() {
    crp = new Crp();
    return "editNew";
  }

  public Crp getCrp() {
    return crp;
  }

  public List<Crp> getCrps() {
    return crps;
  }

  public Long getId() {
    return id;
  }

  @Override
  public Object getModel() {
    Object jsonObject = this.getModelFromEntity();
    return jsonObject;
  }

  /**
   * This method returns a json serialized object back to the client.
   * I Decided against using Annotations on the entity as we would have two types of JSON annotations on our entity,
   * GSON and JsonLib annotations. The best thing to do if we decide to serialize our entities is to standardize on a
   * single JSON serialization framework. Jackson would be the best choice as it is supported by the struts2 rest plugin
   * and most developers consider it to be the best of breed. If we switch to a spring based web service solution,
   * Jackson is well supported also.
   * 
   * @return
   */
  public Object getModelFromEntity() {
    JsonConfig jsonConfig = new JsonConfig();
    /**
     * This requires us to exclude all the bidirectional relations back to the Crp in order to avoid infinite recursion.
     */
    jsonConfig.setExcludes(new String[] {"crpUsers", "roles", "crpPrograms", "crpsSitesIntegrations", "locElementTypes",
      "liasonUsers", "liaisonInstitutions", "customParameters", "parameters", "crpSubIdosContributions",
      "fundingSources", "crpTargetUnits", "deliverables", "crpLocElementTypes", "deliverablesList", "genderTypes",
      "partnerRequest", "createdBy", "modifiedBy", "crpPpaPartners", "locElements", "programManagmenTeam",
      "crpInstitutionsPartners", "siteIntegrations", "locationElementTypes", "locationCustomElementTypes", "projects",
      "targetUnits", "customLevels"});
    jsonConfig.setIgnoreDefaultExcludes(false);
    jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);


    if (crps != null) {
      JSONArray jsonArray = JSONArray.fromObject(crps, jsonConfig);
      return jsonArray;
    }
    JSONObject jsonObject = JSONObject.fromObject(crp, jsonConfig);
    return jsonObject;
  }


  // GET /orders
  public HttpHeaders index() {
    crps = crpManager.findAll();
    return new DefaultHttpHeaders("index").disableCaching();
  }

  public void setCrp(Crp crp) {
    this.crp = crp;
  }

  public void setCrps(List<Crp> crps) {
    this.crps = crps;
  }

  public void setId(Long id) {
    this.id = id;
  }

  // GET /orders/1
  public HttpHeaders show() {
    crp = crpManager.getCrpById(this.getId());

    return new DefaultHttpHeaders("show");
  }

  // PUT /orders/1
  public HttpHeaders update() {
    crpManager.saveCrp(crp);
    return new DefaultHttpHeaders("update");
  }


}
