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
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.response.CrpDTO;
import org.cgiar.ccafs.marlo.rest.response.mapper.CrpMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.opensymphony.xwork2.ModelDriven;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Controller converts CrpDTOs to and from JSON Objects and delegates to the CrpManager.
 * 
 * @author GrantL
 */
public class CrpController implements ModelDriven<Object> {

  @SuppressWarnings("unused")
  private static final long serialVersionUID = 1L;

  private static final Logger LOG = LoggerFactory.getLogger(CrpController.class);

  private final CrpManager crpManager;

  private final UserManager userManager;

  private CrpDTO crpDTO = new CrpDTO();

  private Long id;

  private List<CrpDTO> crpDTOs;

  @Inject
  public CrpController(CrpManager crpManager, UserManager userManager) {
    this.crpManager = crpManager;
    this.userManager = userManager;
  }


  // POST /crps
  public HttpHeaders create() {
    LOG.debug("Create new crp {}", crpDTO);

    CrpMapper crpMapper = new CrpMapper();
    Crp newCrp = crpMapper.crpDTOtoCrp(crpDTO);

    // These audit fields should be automatically created via a Hibernate post-insert/update listener!
    newCrp.setCreatedBy(this.getCurrentUser());
    // This should not be a non-nullable field in the database, as when created it should be null.
    newCrp.setModifiedBy(this.getCurrentUser());
    newCrp.setActiveSince(new Date());


    newCrp = crpManager.saveCrp(newCrp);
    return new DefaultHttpHeaders("create");
  }

  // DELETE /orders/1
  public HttpHeaders destroy() {
    LOG.debug("Delete crp with id: {}", id);
    crpManager.deleteCrp(id);
    return new DefaultHttpHeaders("destroy");
  }

  /**
   * Struts rest plugin allows you to use GET methods to update an entity. But to keep things simple
   * we will force the user to use the PUT method instead.
   * 
   * @return
   */
  // GET /orders/1/edit
  public HttpHeaders edit() {
    LOG.warn("Deliberately not implementing this method, to update an entity use PUT instead.");
    return new DefaultHttpHeaders("edit");
  }

  /**
   * Struts rest plugin allows you to use GET methods to create an entity. This seems weird so we will force the user to
   * use the POST method instead.
   * 
   * @return
   */
  // GET /orders/new
  public String editNew() {
    LOG.warn("Deliberately not implementing this method, to create an entity use POST instead.");
    return "editNew";
  }

  public CrpDTO getCrp() {
    return crpDTO;
  }

  public List<CrpDTO> getCrps() {
    return crpDTOs;
  }

  private User getCurrentUser() {
    Subject subject = SecurityUtils.getSubject();
    Long principal = (Long) subject.getPrincipal();
    User user = userManager.getUser(principal);
    return user;

  }

  public Long getId() {
    return id;
  }

  @Override
  public Object getModel() {
    Object jsonObject;
    try {
      jsonObject = this.getModelFromDTO();
    } catch (JsonProcessingException e) {
      // TODO Auto-generated catch block
      LOG.error("error occurred when serializing the model", e);
      return null;
    }
    return jsonObject;
  }


  /**
   * Converts the DTO to a JSONObject or JSONArray.
   * 
   * @return
   * @throws JsonProcessingException
   */
  public Object getModelFromDTO() throws JsonProcessingException {
    if (crpDTOs != null) {
      return crpDTOs;
    }
    return crpDTO;
  }

  // GET /crps
  public HttpHeaders index() {
    List<Crp> crps = crpManager.findAll();
    CrpMapper crpMapper = new CrpMapper();
    crpDTOs = new ArrayList<>();
    // Create crpDTOs
    for (Crp crp : crps) {

      CrpDTO crpDTO = crpMapper.crpToCrpDTO(crp);
      crpDTOs.add(crpDTO);
    }

    return new DefaultHttpHeaders("index").disableCaching();
  }

  public void setCrpDTO(CrpDTO crpDTO) {
    this.crpDTO = crpDTO;
  }

  public void setCrpDTOs(List<CrpDTO> crpDTOs) {
    this.crpDTOs = crpDTOs;
  }

  public void setId(Long id) {
    this.id = id;
  }

  // GET /crps/1
  public HttpHeaders show() {
    Crp crp = crpManager.getCrpById(this.getId());

    CrpMapper crpMapper = new CrpMapper();
    crpDTO = crpMapper.crpToCrpDTO(crp);

    return new DefaultHttpHeaders("show");
  }

  // PUT /crps/1
  public HttpHeaders update() {

    /**
     * We have two options here either we expect the REST client to give us a complete representation of the Crp and
     * save that directly (after perhaps some validation - not sure if we can use JSR 303 bean validation) or we need to
     * retrieve the entity, update it and then save it. If we want to update directly, it is
     * important that we have all the fields in the entity listed in the CrpDTO (and the mapper maps them to the Crp)
     * otherwise they will be set to null. We may need to refactor how our audited fields (createdBy, activeSince etc)
     * are being generated if we don't want our users to have to include them in the request payload.
     * Note that if we chose to use this approach the client does not need to add OneToMany references but ManyToOne
     * fields will need to include the id of the referenced entity otherwise they would be set to null.
     * For the purpose of simplicity, we will choose the simpler option and that is fetch the entity and then update
     * those fields that we capture in the DTO.
     */
    // updateEntityDirectlyWithoutFetching();
    this.updateEntityAfterFetching();

    return new DefaultHttpHeaders("update");
  }

  /**
   * This approach may be considered better if we are only exposing a few fields for the user to update. It does
   * require an extra SQL read to fetch the entity before we update it however.
   */
  public void updateEntityAfterFetching() {
    Crp existingCrp = crpManager.getCrpById(crpDTO.getId());

    CrpMapper crpMapper = new CrpMapper();
    existingCrp = crpMapper.crpDTOtoCrp(crpDTO, existingCrp);

    // Auditing information should be done in a hibernate post-update/insert listener.
    existingCrp.setModifiedBy(this.getCurrentUser());

    // Now update the existingCrp with the updated values.
    existingCrp = crpManager.saveCrp(existingCrp);
  }

  /**
   * A more efficient implementation but more risky as it requires exposing more fields to the client.
   */
  public void updateEntityDirectlyWithoutFetching() {
    CrpMapper crpMapper = new CrpMapper();

    Crp updatedCrp = crpMapper.crpDTOtoCrp(crpDTO);

    updatedCrp = crpManager.saveCrp(updatedCrp);
  }


}
