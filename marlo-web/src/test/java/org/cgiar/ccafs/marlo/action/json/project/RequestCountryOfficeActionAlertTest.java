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

package org.cgiar.ccafs.marlo.action.json.project;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Parameter;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.EmailSendDoubles.EnvelopeConfig;
import org.cgiar.ccafs.marlo.utils.EmailSendDoubles.RecordingSendMail;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * CHG-PARTREQ-001-T11b: the new-office alert hands the bare project id to the email, and the unit's own text adds
 * the word and the letter ("project: P77", "cluster: C77"). The action holds no unit check (NF-001).
 */
public class RequestCountryOfficeActionAlertTest {

  private static final class DrivenAction extends RequestCountryOfficeAction {

    private static final long serialVersionUID = 1L;

    DrivenAction(RecordingSendMail mail) {
      super(new EnvelopeConfig("pmu@unit.test", "notify@unit.test", "https://marlo.test"), null, null, null, null,
        null, mail);
    }

    @Override
    public String getBaseUrlCdn() {
      return "https://cdn.test";
    }

    @Override
    public User getCurrentUser() {
      User user = new User();
      user.setFirstName("Ana");
      user.setLastName("Lopez");
      user.setEmail("ana@unit.test");
      return user;
    }
  }

  private static GlobalUnit unit(String acronym, String customFile) {
    GlobalUnit unit = new GlobalUnit();
    unit.setAcronym(acronym);
    unit.setCustomParameters(new HashSet<CustomParameter>());
    if (customFile != null) {
      Parameter parameter = new Parameter();
      parameter.setKey(APConstants.CRP_CUSTOM_FILE);
      CustomParameter custom = new CustomParameter();
      custom.setParameter(parameter);
      custom.setValue(customFile);
      unit.getCustomParameters().add(custom);
    }
    return unit;
  }

  /** Sends the alert of project 77 for the unit and returns the body of the one email recorded. */
  private static String alertBody(GlobalUnit unit) throws Exception {
    RecordingSendMail mail = new RecordingSendMail();
    DrivenAction action = new DrivenAction(mail);
    Field loggedCrp = RequestCountryOfficeAction.class.getDeclaredField("loggedCrp");
    loggedCrp.setAccessible(true);
    loggedCrp.set(action, unit);
    Field projectID = RequestCountryOfficeAction.class.getDeclaredField("projectID");
    projectID.setAccessible(true);
    projectID.set(action, 77L);
    Method send = RequestCountryOfficeAction.class.getDeclaredMethod("sendNewOfficeAlert", String.class,
      String.class, String.class);
    send.setAccessible(true);
    send.invoke(action, "Acme", "Kenya", "KE");
    assertEquals("one alert is sent", 1, mail.sent.size());
    return mail.sent.get(0).body;
  }

  /** The source text the office rows are saved with for project 77, as the unit's texts compose it. */
  private static String requestSource(GlobalUnit unit, String title) throws Exception {
    DrivenAction action = new DrivenAction(new RecordingSendMail());
    Field loggedCrp = RequestCountryOfficeAction.class.getDeclaredField("loggedCrp");
    loggedCrp.setAccessible(true);
    loggedCrp.set(action, unit);
    Field projectID = RequestCountryOfficeAction.class.getDeclaredField("projectID");
    projectID.setAccessible(true);
    projectID.set(action, 77L);
    return action.buildRequestSource(title);
  }

  @Test
  public void testStoredSourceNamesTheClusterForAiccraUnits() throws Exception {
    for (String file : new String[] {"aicrra", "aiccra3"}) {
      assertEquals("Cluster C77 - My title", requestSource(unit("AICCRA", file), "My title"));
    }
  }

  @Test
  public void testStoredSourceNamesTheProjectForOtherUnitsAndToleratesNoTitle() throws Exception {
    assertEquals("Project P77 - My title", requestSource(unit("CCAFS", null), "My title"));
    assertEquals("Project P77 - ", requestSource(unit("CCAFS", null), null));
  }

  @Test
  public void testAiccraUnitsSeeTheRequestingClusterWithItsLetter() throws Exception {
    for (String file : new String[] {"aicrra", "aiccra3"}) {
      String body = alertBody(unit("AICCRA", file));
      assertTrue(file + ": " + body, body.contains("<li>Requested from cluster: C77</li>"));
    }
  }

  @Test
  public void testOtherUnitsSeeTheRequestingProjectWithItsLetter() throws Exception {
    for (String file : new String[] {null, "pabra"}) {
      String body = alertBody(unit("CCAFS", file));
      assertTrue(file + ": " + body, body.contains("<li>Requested from project: P77</li>"));
    }
  }
}
