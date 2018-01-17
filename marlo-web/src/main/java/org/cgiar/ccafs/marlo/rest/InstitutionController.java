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

import org.cgiar.ccafs.marlo.data.manager.InstitutionLocationManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.InstitutionDTO;
import org.cgiar.ccafs.marlo.rest.dto.mapper.InstitutionMapper;

import java.util.ArrayList;
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
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class InstitutionController implements ModelDriven<Object> {

  private static final Logger LOG = LoggerFactory.getLogger(InstitutionController.class);

  private Long id;

  private InstitutionDTO institutionDTO = new InstitutionDTO();

  private List<InstitutionDTO> institutionDTOs;

  private final InstitutionManager institutionManager;

  private final UserManager userManager;

  private final InstitutionMapper institutionMapper;

  private final InstitutionLocationManager institutionLocationManager;

  @Inject
  public InstitutionController(InstitutionManager institutionManager, UserManager userManager,
    InstitutionMapper institutionMapper, InstitutionLocationManager institutionLocationManager) {
    this.institutionManager = institutionManager;
    this.userManager = userManager;
    this.institutionMapper = institutionMapper;
    this.institutionLocationManager = institutionLocationManager;
  }

  // POST /institutions
  public HttpHeaders create() {
    LOG.debug("Create new institution {}", institutionDTO);

    Institution newInstitution = institutionMapper.institutionDTOToInstitution(institutionDTO);


    /**
     * TODO find out why Institution entities implement IAuditLog but doesn't have any audit fields.
     */
    // These audit fields should be automatically created via a Hibernate post-insert/update listener!
    // newInstitution.setCreatedBy(this.getCurrentUser());
    // This should not be a non-nullable field in the database, as when created it should be null.
    // newInstitution.setModifiedBy(this.getCurrentUser());
    // newInstitution.setActiveSince(new Date());


    newInstitution = institutionManager.saveInstitution(newInstitution);
    /**
     * Unfortunately we are not using hibernate cascade update which means we need to save each
     * of the locations after saving the institution.
     */
    newInstitution.getInstitutionsLocations()
      .forEach(institutionLocation -> institutionLocationManager.saveInstitutionLocation(institutionLocation));

    /**
     * convert it to a DTO so that the client can see the generated ids.
     */
    institutionDTO = institutionMapper.institutionToInstitutionDTO(newInstitution);

    return new DefaultHttpHeaders("create");
  }


  // DELETE /institutions/1
  public HttpHeaders destroy() {
    LOG.debug("Delete institution with id: {}", id);
    institutionManager.deleteInstitution(id);
    return new DefaultHttpHeaders("destroy");
  }

  /**
   * Struts rest plugin allows you to use GET methods to update an entity. But to keep things simple
   * we will force the user to use the PUT method instead.
   * 
   * @return
   */
  // GET /institutions/1/edit
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
  // GET /institutions/new
  public String editNew() {
    LOG.warn("Deliberately not implementing this method, to create an entity use POST instead.");
    return "editNew";
  }

  // TODO put this in a common class, or eliminate the need for it by making the audit fields get updated by hibernate.
  private User getCurrentUser() {
    Subject subject = SecurityUtils.getSubject();
    Long principal = (Long) subject.getPrincipal();
    User user = userManager.getUser(principal);
    return user;

  }


  public Long getId() {
    return id;
  }

  public List<InstitutionDTO> getInstitutions() {
    return institutionDTOs;
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
    if (institutionDTOs != null) {
      return institutionDTOs;
    }
    return institutionDTO;
  }


  public HttpHeaders index() {
    List<Institution> institutions = institutionManager.findAll();
    institutionDTOs = new ArrayList<>();
    // Create institutionDTOs
    for (Institution institution : institutions) {

      InstitutionDTO institutionDTO = institutionMapper.institutionToInstitutionDTO(institution);
      institutionDTOs.add(institutionDTO);
    }

    return new DefaultHttpHeaders("index").disableCaching();
  }


  public void setId(Long id) {
    this.id = id;
  }


  public void setInstitutionDTOs(List<InstitutionDTO> institutionDTOs) {
    this.institutionDTOs = institutionDTOs;
  }

  public HttpHeaders show() {

    Institution institution = institutionManager.getInstitutionById(this.getId());

    if (institution == null) {
      LOG.error("Unable to find Institution with id : " + this.getId());
      return null;
    }

    institutionDTO = institutionMapper.institutionToInstitutionDTO(institution);

    return new DefaultHttpHeaders("show");
  }

  // PUT /institutions/1
  public HttpHeaders update() {

    /**
     * We have two options here either we expect the REST client to give us a complete representation of the Institution
     * and
     * save that directly (after perhaps some validation - not sure if we can use JSR 303 bean validation) or we need to
     * retrieve the entity, update it and then save it. If we want to update directly, it is
     * important that we have all the fields in the entity listed in the InstitutionDTO (and the mapper maps them to the
     * Institution)
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
    Institution existingInstitution = institutionManager.getInstitutionById(institutionDTO.getId());

    existingInstitution = institutionMapper.updateInstitutionFromInstitutionDto(institutionDTO, existingInstitution);

    // Auditing information should be done in a hibernate post-update/insert listener.
    // existingInstitution.setModifiedBy(this.getCurrentUser());

    // Now update the existingInstitution with the updated values.
    existingInstitution = institutionManager.saveInstitution(existingInstitution);
  }

}
