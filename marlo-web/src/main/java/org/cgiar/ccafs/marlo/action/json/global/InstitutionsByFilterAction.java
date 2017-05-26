package org.cgiar.ccafs.marlo.action.json.global;


import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.InstitutionType;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class InstitutionsByFilterAction extends BaseAction {

  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(InstitutionsByFilterAction.class);
  private static final long serialVersionUID = 2773512217474650692L;

  // Model
  private List<Institution> institutions;
  private long countryID;
  private long institutionTypeID;

  // Managers
  private InstitutionManager institutionManager;

  @Inject
  public InstitutionsByFilterAction(APConfig config, InstitutionManager institutionManager) {
    super(config);
    this.institutionManager = institutionManager;
  }

  @Override
  public String execute() throws Exception {
    InstitutionType type = new InstitutionType();
    type.setId(institutionTypeID);

    LocElement country = new LocElement();
    country.setId(countryID);

    institutions = institutionManager.findAll().stream()
      .filter(c -> c.getInstitutionType().getId().longValue() == type.getId()).collect(Collectors.toList());
    LOG.info("The list of institutions by country='{}' and type='{}' was loaded.", countryID, institutionTypeID);
    return SUCCESS;
  }

  public List<Institution> getInstitutions() {
    return institutions;
  }


  @Override
  public void prepare() throws Exception {
    String _countryID, _typeID;

    // If there is a country ID take its values
    _countryID = StringUtils.trim(this.getRequest().getParameter(APConstants.COUNTRY_REQUEST_ID));
    try {
      countryID = (_countryID != null) ? Integer.parseInt(_countryID) : -1;
    } catch (NumberFormatException e) {
      LOG.warn("There was an exception trying to convert to int the parameter {}", _countryID);
      countryID = -1;
    }

    // If there is a partner type ID take its values
    _typeID = StringUtils.trim(this.getRequest().getParameter(APConstants.INSTITUTION_TYPE_REQUEST_ID));
    try {
      institutionTypeID = (_typeID != null) ? Integer.parseInt(_typeID) : -1;
    } catch (NumberFormatException e) {
      LOG.warn("There was an exception trying to convert to int the parameter {}", _typeID);
      institutionTypeID = -1;
    }
  }

}