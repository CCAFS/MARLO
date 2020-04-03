/*****************************************************************
 * \ * This file is part of Managing Agricultural Research for Learning &
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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.QAToken;

import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.QATokenAuthManager;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.QATokenAuth;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.NewQATokenAuthDTO;
import org.cgiar.ccafs.marlo.rest.dto.QATokenAuthDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.QATokenMapper;
import org.cgiar.ccafs.marlo.utils.MD5Convert;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author Luis Benavides - CIAT/CCAFS
 */
@Configuration
@PropertySource("classpath:global.properties")
@Named
public class QATokenItem<T> {

  private static final int SECONDS_EXPIRATION = 60;

  // Managers and mappers
  private QATokenAuthManager qATokenManager;
  private GlobalUnitManager globalUnitManager;
  private QATokenMapper qATokenMapper;

  @Inject
  public QATokenItem(QATokenAuthManager qATokenManager, GlobalUnitManager globalUnitManager,
    QATokenMapper qATokenMapper) {
    this.qATokenManager = qATokenManager;
    this.globalUnitManager = globalUnitManager;
    this.qATokenMapper = qATokenMapper;
  }

  /**
   * Create a QA TokenAuth
   * 
   * @param name
   * @param username
   * @param email
   * @param smoCode
   * @param User
   * @return a QATokenAuth
   */
  private QATokenAuth createToken(String name, String username, String email, String smoCode, User user) {
    Calendar c = Calendar.getInstance();
    String textBeforeMD5 = null;
    QATokenAuth qATokenAuth = new QATokenAuth();

    Date currentDate = c.getTime();
    c.add(Calendar.SECOND, SECONDS_EXPIRATION);
    Date expirationDate = c.getTime();

    textBeforeMD5 = user.getId().toString() + username.trim() + currentDate;

    qATokenAuth.setCreatedAt(currentDate);
    qATokenAuth.setUpdatedAt(currentDate);
    qATokenAuth.setCrpId(smoCode.trim());
    qATokenAuth.setToken(MD5Convert.stringToMD5(textBeforeMD5));
    qATokenAuth.setExpirationDate(expirationDate);
    qATokenAuth.setUsername(username.trim());
    qATokenAuth.setEmail(email.trim());
    qATokenAuth.setName(name.trim());
    qATokenAuth.setAppUser(user.getId());

    qATokenManager.saveQATokenAuth(qATokenAuth);

    return qATokenAuth;
  }

  /**
   * Validate format email
   * 
   * @param email
   * @return a true if is valid else false
   */
  private boolean emailIsValid(String email) {
    Pattern patternEmail =
      Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

    Matcher mather = patternEmail.matcher(email);

    return mather.find();
  }

  /**
   * Generate a QA TokenAuth
   * 
   * @param NewQATokenAuthDTO
   * @param User
   * @return a QATokenAuthDTO with the QATokenItem
   */
  public ResponseEntity<QATokenAuthDTO> getToken(NewQATokenAuthDTO newQATokenAuthDTO, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    QATokenAuth qATokenAuth = null;
    String name = newQATokenAuthDTO.getName();
    String username = newQATokenAuthDTO.getUsername();
    String email = newQATokenAuthDTO.getEmail();
    String smoCode = newQATokenAuthDTO.getSmocode();

    if (this.valuesIsEmpty(newQATokenAuthDTO)) {
      fieldErrors.add(new FieldErrorDTO("getToken", "newQATokenAuthDTO", "One or more of the values are empty"));
    } else {
      GlobalUnit crpSmo = this.globalUnitManager.findGlobalUnitBySMOCode(smoCode.trim());

      if (crpSmo == null) {
        fieldErrors.add(new FieldErrorDTO("getToken", "smoCode", smoCode + " is an invalid smo code"));
      } else {

        Set<CrpUser> lstUser = user.getCrpUsers();

        if (!lstUser.stream().anyMatch(crp -> crp.getCrp().getAcronym().equalsIgnoreCase(crpSmo.getAcronym()))) {
          fieldErrors.add(new FieldErrorDTO("getToken", "smoCode", smoCode + " is a smo code entity not autorized"));
        } else {
          if (!this.emailIsValid(email)) {
            fieldErrors.add(new FieldErrorDTO("getToken", "email", email + " is an invalid email"));
          } else {
            qATokenAuth = this.createToken(name, username, email, smoCode, user);
          }

        }
      }
    }
    // Validate all fields
    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return Optional.ofNullable(qATokenAuth).map(this.qATokenMapper::QATokenAuthToQATokenAuthDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

  }

  private Boolean valuesIsEmpty(NewQATokenAuthDTO newQATokenAuthDTO) {
    if (newQATokenAuthDTO.getName().isEmpty() || newQATokenAuthDTO.getUsername().isEmpty()
      || newQATokenAuthDTO.getEmail().isEmpty() || newQATokenAuthDTO.getSmocode().isEmpty()) {
      return true;
    }
    return false;
  }

}
