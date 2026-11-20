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

package org.cgiar.ccafs.marlo.action.projects;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.InstitutionTypeManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PartnerRequestManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.ActivityPartner;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.InstitutionType;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Parameter;
import org.cgiar.ccafs.marlo.data.model.PartnerRequest;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.EmailSendDoubles;
import org.cgiar.ccafs.marlo.utils.EmailSendDoubles.EnvelopeConfig;
import org.cgiar.ccafs.marlo.utils.EmailSendDoubles.RecordingSendMail;
import org.cgiar.ccafs.marlo.utils.EmailSendDoubles.Sent;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * CHG-PARTREQ-001-T06: a partner request is saved locally for every Global Unit, an AICCRA one included, and the
 * support mailbox is alerted only after the save (FN-001, FN-002, FN-003, FN-010, NF-001, OPS-001). The manager is a
 * recording double, so a test reads the arguments it received instead of only that it was called.
 */
public class PartnersSaveActionRequestTest {

  private static final String NOTIFY = "notify@unit.test";
  private static final String REQUESTER_EMAIL = "ana@unit.test";
  private static final String PARTNER = "Acme Seeds Ltd";
  private static final String PROJECT_TITLE = "Climate smart villages";

  /** The action with everything session-bound answered by the scenario; it reports the AICCRA unit, as one does. */
  private static final class DrivenAction extends PartnersSaveAction {

    private static final long serialVersionUID = 1L;

    /** The texts of the unit: global.properties, over which the unit's custom file (when it has one) is laid. */
    private final Properties texts = new Properties();

    DrivenAction(PartnerRequestManager manager, RecordingSendMail mail, InstitutionType type, LocElement country,
      String customFile) {
      super(new EnvelopeConfig("pmu@unit.test", NOTIFY, "https://marlo.test"),
        EmailSendDoubles.answering(LocElementManager.class, "getLocElementById", args -> country),
        EmailSendDoubles.answering(InstitutionTypeManager.class, "getInstitutionTypeById", args -> type), null,
        EmailSendDoubles.answering(ProjectManager.class, "getProjectById", args -> {
          Project project = new Project();
          project.setProjectInfo(projectInfo(PROJECT_TITLE));
          return project;
        }), manager, null, null, mail, null, null, null, null);
      this.load("global");
      if (customFile != null) {
        this.load("custom/" + customFile);
      }
    }

    private void load(String name) {
      try (InputStream in = Files.newInputStream(Paths.get("src/main/resources/" + name + ".properties"))) {
        this.texts.load(in);
      } catch (java.io.IOException e) {
        throw new IllegalStateException(e);
      }
    }

    @Override
    public Phase getActualPhase() {
      return null;
    }

    @Override
    public String getBaseUrlCdn() {
      return "https://cdn.test";
    }

    @Override
    public User getCurrentUser() {
      User user = new User();
      user.setId(9L);
      user.setFirstName("Ana");
      user.setLastName("Lopez");
      user.setEmail(REQUESTER_EMAIL);
      return user;
    }

    @Override
    public String getText(String key) {
      return key;
    }

    @Override
    public String getText(String key, String[] args) {
      return new MessageFormat(this.texts.getProperty(key)).format(args);
    }

    @Override
    public boolean isAiccra() {
      return true;
    }
  }

  private final List<PartnerRequest> created = new ArrayList<>();
  private final List<PartnerRequest> originals = new ArrayList<>();
  private RecordingSendMail mail;
  private Logger actionLogger;
  private Level originalLevel;
  private ListAppender<ILoggingEvent> logged;

  private static ProjectInfo projectInfo(String title) {
    ProjectInfo info = new ProjectInfo();
    info.setTitle(title);
    return info;
  }

  private static GlobalUnit unit(boolean notificationsOn) {
    GlobalUnit unit = new GlobalUnit();
    unit.setId(1L);
    unit.setAcronym("AICCRA");
    if (notificationsOn) {
      unit.setCustomParameters(Collections.emptySet());
    } else {
      Parameter parameter = new Parameter();
      parameter.setKey(APConstants.CRP_EMAIL_NOTIFICATIONS);
      CustomParameter off = new CustomParameter();
      off.setParameter(parameter);
      off.setValue("false");
      unit.setCustomParameters(Collections.singleton(off));
    }
    return unit;
  }

  @Before
  public void setUp() {
    this.mail = new RecordingSendMail();
    this.actionLogger = (Logger) LoggerFactory.getLogger(PartnersSaveAction.class);
    this.originalLevel = this.actionLogger.getLevel();
    this.actionLogger.setLevel(Level.TRACE);
    this.logged = new ListAppender<>();
    this.logged.start();
    this.actionLogger.addAppender(this.logged);
  }

  @After
  public void tearDown() {
    this.actionLogger.detachAppender(this.logged);
    this.actionLogger.setLevel(this.originalLevel);
  }

  /** A filled-in request page for the unit, with a manager that answers as the scenario says. */
  private DrivenAction action(GlobalUnit unit, boolean managerFails) throws Exception {
    return this.action(unit, managerFails, null);
  }

  /** The same page, with the texts of the given custom file (null: the global texts only). */
  private DrivenAction action(GlobalUnit unit, boolean managerFails, String customFile) throws Exception {
    PartnerRequestManager manager =
      EmailSendDoubles.answering(PartnerRequestManager.class, "createPartnerRequest", args -> {
        if (managerFails) {
          throw new IllegalStateException("the save was rolled back");
        }
        this.originals.add((PartnerRequest) args[0]);
        this.created.add((PartnerRequest) args[1]);
        return args[1];
      });
    InstitutionType type = new InstitutionType();
    type.setId(7L);
    type.setName("Research institute");
    LocElement country = new LocElement();
    country.setId(30L);
    country.setName("Kenya");
    DrivenAction action = new DrivenAction(manager, this.mail, type, country, customFile);
    Institution partner = new Institution();
    partner.setName(PARTNER);
    partner.setAcronym("ACME");
    partner.setWebsiteLink("https://acme.test");
    partner.setInstitutionType(type);
    ActivityPartner activityPartner = new ActivityPartner();
    activityPartner.setPartner(partner);
    action.setActivityPartner(activityPartner);
    action.setLocationId(30L);
    Field loggedCrp = PartnersSaveAction.class.getDeclaredField("loggedCrp");
    loggedCrp.setAccessible(true);
    loggedCrp.set(action, unit);
    // prepare() loads the types of the page; the name of the chosen one is read from that list
    Field types = PartnersSaveAction.class.getDeclaredField("institutionTypesList");
    types.setAccessible(true);
    types.set(action, Collections.singletonList(type));
    return action;
  }

  @Test
  public void testAiccraUnitSavesTheOriginalAndTheEditableCopyAndAlertsSupport() throws Exception {
    GlobalUnit unit = unit(true);
    assertTrue("the unit of this case reports AICCRA", unit.isAiccra());
    DrivenAction action = this.action(unit, false);
    assertTrue("the action of this case reports AICCRA", action.isAiccra());

    assertEquals("success", action.save());

    assertEquals("the manager must be called once", 1, this.created.size());
    PartnerRequest original = this.originals.get(0);
    PartnerRequest copy = this.created.get(0);
    assertNotSame("the original and the editable copy are two rows", original, copy);
    for (PartnerRequest row : Arrays.asList(original, copy)) {
      assertEquals(PARTNER, row.getPartnerName());
      assertEquals("ACME", row.getAcronym());
      assertEquals("https://acme.test", row.getWebPage());
      assertSame(unit, row.getCrp());
      assertFalse(row.isOffice());
      assertEquals("Kenya", row.getLocElement().getName());
      assertEquals(Long.valueOf(7L), row.getInstitutionType().getId());
    }
    assertTrue(action.isMessageSent());
    assertFalse(action.isRequestFailed());
    assertEquals(1, this.mail.sent.size());
    Sent sent = this.mail.sent.get(0);
    assertEquals(NOTIFY, sent.to);
    assertEquals(NOTIFY, sent.bcc);
    assertEquals("[MARLO - AICCRA] Partner verification - " + PARTNER, sent.subject);
    assertTrue(sent.body, sent.body.contains(REQUESTER_EMAIL));
    assertTrue(sent.body, sent.body.contains("Research institute"));
    assertTrue(sent.body, sent.body.contains("https://marlo.test/superadmin/marloInstitutions.do"));
    assertTrue(sent.html);
  }

  /** The request page opened from project 77: the source names the unit's word and letter, whatever the unit. */
  private void saveFromProject(DrivenAction action) throws Exception {
    action.setProjectID(77);
    action.setContext("Partner request");
    Field page = PartnersSaveAction.class.getDeclaredField("pageRequestName");
    page.setAccessible(true);
    page.set(action, "projects");
    assertEquals("success", action.save());
  }

  @Test
  public void testAiccraUnitsNameTheProjectSourceAsClusterC() throws Exception {
    for (String file : new String[] {"aicrra", "aiccra3"}) {
      this.created.clear();
      this.originals.clear();
      this.saveFromProject(this.action(unit(true), false, file));

      String expected = "Partner request Cluster C77 - " + PROJECT_TITLE;
      assertEquals(file, expected, this.originals.get(0).getRequestSource());
      assertEquals(file, expected, this.created.get(0).getRequestSource());
    }
  }

  @Test
  public void testOtherUnitsNameTheProjectSourceAsProjectP() throws Exception {
    for (String file : new String[] {null, "pabra"}) {
      this.created.clear();
      this.originals.clear();
      this.saveFromProject(this.action(unit(true), false, file));

      String expected = "Partner request Project P77 - " + PROJECT_TITLE;
      assertEquals(String.valueOf(file), expected, this.originals.get(0).getRequestSource());
      assertEquals(String.valueOf(file), expected, this.created.get(0).getRequestSource());
    }
  }

  @Test
  public void testAFailedSaveShowsTheFormAgainAndSendsNothing() throws Exception {
    DrivenAction action = this.action(unit(true), true);

    assertEquals("input", action.save());

    assertTrue(action.isRequestFailed());
    assertFalse(action.isMessageSent());
    assertTrue("a request that was not saved must not be announced", this.mail.sent.isEmpty());
    List<String> errors = new ArrayList<>();
    for (ILoggingEvent event : this.logged.list) {
      if (event.getLevel() == Level.ERROR) {
        errors.add(event.getFormattedMessage());
      }
    }
    assertEquals(Collections.singletonList("Could not save the partner request of the Global Unit 1"), errors);
  }

  @Test
  public void testSwitchOffStoresTheRequestAndSendsNothing() throws Exception {
    DrivenAction action = this.action(unit(false), false);

    assertEquals("success", action.save());

    assertEquals("the request is stored", 1, this.created.size());
    assertTrue(action.isMessageSent());
    assertTrue("the unit's notification switch is off", this.mail.sent.isEmpty());
  }

  @Test
  public void testNoReferenceToTheExternalServiceRemains() throws Exception {
    for (String file : new String[] {"src/main/java/org/cgiar/ccafs/marlo/action/projects/PartnersSaveAction.java",
      "src/main/java/org/cgiar/ccafs/marlo/action/json/project/RequestCountryOfficeAction.java"}) {
      String source = new String(Files.readAllBytes(Paths.get(file)), StandardCharsets.UTF_8);
      assertFalse(file, source.toLowerCase().contains("clarisa"));
      assertFalse(file, source.contains("ExternalPostUtils"));
      assertFalse(file, source.contains("isAiccra()"));
      assertFalse(file, source.contains("sendPartnerRequest"));
    }
  }
}
