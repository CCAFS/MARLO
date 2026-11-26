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
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.EmailSendDoubles;
import org.cgiar.ccafs.marlo.utils.EmailSendDoubles.RecordingSendMail;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * CHG-PARTROUTE-001-T02: the partner request goes to CLARISA or is stored in MARLO depending on the stored switch of
 * the submitting Global Unit (FN-001…FN-006, FN-010, SEC-002, OPS-001). The CLARISA call is the overridable seam of
 * the action, replaced here by a double that keeps the arguments it received. Every expected value is a literal.
 */
public class PartnersSaveActionClarisaModeTest {

  private static final String HOST = "https://clarisa.test";
  private static final String USERNAME = "clarisa-user-7f3";
  private static final String PASSWORD = "s3cr3t-pw-91x";
  private static final String REQUESTER_EMAIL = "ana@unit.test";

  private static long NEXT_ROW_ID = 100L;

  /** Answers the CLARISA getters (the shared test config answers none) and what the MARLO-mode alert reads. */
  private static final class ClarisaConfig extends APConfig {

    private final String host;
    private final String username;
    private final String password;

    ClarisaConfig(String host, String username, String password) {
      this.host = host;
      this.username = username;
      this.password = password;
    }

    @Override
    public String getBaseUrl() {
      return "https://marlo.test";
    }

    @Override
    public String getClarisaAPIHost() {
      return this.host;
    }

    @Override
    public String getClarisaAPIPassword() {
      return this.password;
    }

    @Override
    public String getClarisaAPIUsername() {
      return this.username;
    }

    @Override
    public String getEmail_pmu() {
      return "pmu@unit.test";
    }

    @Override
    public String getEmailNotification() {
      return "notify@unit.test";
    }
  }

  /** The action with the session-bound parts answered, and the CLARISA call replaced by a recording double. */
  private static final class DrivenAction extends PartnersSaveAction {

    private static final long serialVersionUID = 1L;

    private final Properties texts = new Properties();
    final transient List<String> urls = new ArrayList<>();
    final transient List<String> bodies = new ArrayList<>();
    transient int status = 201;
    transient IOException failure;
    transient boolean sessionSaysClarisa;

    DrivenAction(ClarisaConfig config, PartnerRequestManager manager, RecordingSendMail mail, InstitutionType type,
      LocElement country, String customFile) {
      super(config, EmailSendDoubles.answering(LocElementManager.class, "getLocElementById", args -> country),
        EmailSendDoubles.answering(InstitutionTypeManager.class, "getInstitutionTypeById", args -> type), null,
        EmailSendDoubles.answering(ProjectManager.class, "getProjectById", args -> {
          Project project = new Project();
          ProjectInfo info = new ProjectInfo();
          info.setTitle("Climate smart villages");
          project.setProjectInfo(info);
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
      } catch (IOException e) {
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
      user.setUsername("ana.lopez");
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
    public boolean hasSpecificities(String specificity) {
      return this.sessionSaysClarisa;
    }

    @Override
    protected int postToClarisa(String url, String json) throws IOException {
      this.urls.add(url);
      this.bodies.add(json);
      if (this.failure != null) {
        throw this.failure;
      }
      return this.status;
    }
  }

  private final List<Object[]> stored = new ArrayList<>();
  private RecordingSendMail mail;
  private Logger actionLogger;
  private Level originalLevel;
  private ListAppender<ILoggingEvent> logged;

  @Before
  public void setUp() {
    this.mail = new RecordingSendMail();
    // The appender sits on the action's logger only, so the lines of APConfig or of other classes are not counted
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

  private static CustomParameter row(String key, String value, boolean active) {
    Parameter parameter = new Parameter();
    parameter.setKey(key);
    CustomParameter row = new CustomParameter();
    row.setParameter(parameter);
    row.setValue(value);
    row.setActive(active);
    // The rows of a set are told apart by id, so each needs its own
    row.setId(NEXT_ROW_ID++);
    return row;
  }

  private static GlobalUnit unit(long id, String acronym, CustomParameter... rows) {
    GlobalUnit unit = new GlobalUnit();
    unit.setId(id);
    unit.setAcronym(acronym);
    Set<CustomParameter> parameters = new HashSet<>();
    Collections.addAll(parameters, rows);
    unit.setCustomParameters(parameters);
    return unit;
  }

  private static GlobalUnit clarisaUnit(long id, String acronym) {
    return unit(id, acronym, row(APConstants.PARTNER_REQUESTS_TO_CLARISA, "true", true));
  }

  private DrivenAction action(GlobalUnit unit, ClarisaConfig config, String customFile) throws Exception {
    PartnerRequestManager manager =
      EmailSendDoubles.answering(PartnerRequestManager.class, "createPartnerRequest", args -> {
        this.stored.add(args);
        return args[1];
      });
    InstitutionType type = new InstitutionType();
    type.setId(7L);
    type.setName("Research institute");
    LocElement country = new LocElement();
    country.setId(30L);
    country.setName("Kenya");
    country.setIsoAlpha2("KE");
    DrivenAction action = new DrivenAction(config, manager, this.mail, type, country, customFile);
    Institution partner = new Institution();
    partner.setName("Acme Seeds Ltd");
    partner.setAcronym("ACME");
    partner.setWebsiteLink("https://acme.test");
    partner.setInstitutionType(type);
    ActivityPartner activityPartner = new ActivityPartner();
    activityPartner.setPartner(partner);
    action.setActivityPartner(activityPartner);
    action.setLocationId(30L);
    this.set(action, "loggedCrp", unit);
    this.set(action, "institutionTypesList", Collections.singletonList(type));
    return action;
  }

  private DrivenAction action(GlobalUnit unit) throws Exception {
    return this.action(unit, new ClarisaConfig(HOST, USERNAME, PASSWORD), null);
  }

  private void set(DrivenAction action, String name, Object value) throws Exception {
    Field field = PartnersSaveAction.class.getDeclaredField(name);
    field.setAccessible(true);
    field.set(action, value);
  }

  /** The page was opened from project 77. */
  private String saveFromProject(DrivenAction action) throws Exception {
    action.setProjectID(77);
    action.setContext("Partner request");
    this.set(action, "pageRequestName", "projects");
    return action.save();
  }

  private void assertNothingStoredNorAlerted(String why) {
    assertTrue(why + ": the manager must not be called", this.stored.isEmpty());
    assertTrue(why + ": the support mailbox must not be alerted", this.mail.sent.isEmpty());
  }

  /** The one event of the action's logger, asserted to be alone, free of request data and without a throwable. */
  private ILoggingEvent onlyEvent() {
    assertEquals("exactly one event of the action's logger", 1, this.logged.list.size());
    ILoggingEvent event = this.logged.list.get(0);
    assertNull("no throwable is attached", event.getThrowableProxy());
    String line = event.getFormattedMessage();
    for (String secret : new String[] {REQUESTER_EMAIL, USERNAME, PASSWORD, "Authorization", "Acme", "ana.lopez"}) {
      assertFalse("the line must not carry " + secret + ": " + line, line.contains(secret));
    }
    return event;
  }

  // ---- D1: wrong branch ------------------------------------------------------------------------------------------

  @Test
  public void testClarisaModeSendsOnceAndStoresNothing() throws Exception {
    DrivenAction action = this.action(clarisaUnit(45L, "AICCRA"));

    assertEquals("success", this.saveFromProject(action));

    assertEquals("the seam is called once", 1, action.urls.size());
    this.assertNothingStoredNorAlerted("CLARISA mode");
    assertTrue(action.isMessageSent());
    assertFalse(action.isRequestFailed());
    assertTrue(action.isClarisaMode());
    ILoggingEvent event = this.onlyEvent();
    assertEquals(Level.INFO, event.getLevel());
    assertEquals("CLARISA accepted the partner request of the Global Unit 45 with status 201",
      event.getFormattedMessage());
  }

  @Test
  public void testMarloModeAiccraUnitStoresOnceAndNeverCallsClarisa() throws Exception {
    DrivenAction action =
      this.action(unit(45L, "AICCRA", row(APConstants.PARTNER_REQUESTS_TO_CLARISA, "false", true)));

    assertEquals("success", action.save());

    assertTrue("the seam is never called", action.urls.isEmpty());
    assertEquals("the manager is called once", 1, this.stored.size());
    assertEquals("one support alert", 1, this.mail.sent.size());
    assertTrue(action.isMessageSent());
    assertFalse(action.isClarisaMode());
  }

  // ---- D2: payload -----------------------------------------------------------------------------------------------

  @Test
  public void testPayloadCarriesEveryFieldAndTheUnitsOwnSourceText() throws Exception {
    DrivenAction action = this.action(clarisaUnit(45L, "AICCRA"), new ClarisaConfig(HOST, USERNAME, PASSWORD),
      "aicrra");

    assertEquals("success", this.saveFromProject(action));

    assertEquals(Collections.singletonList("https://clarisa.test/api/partner-requests/create"), action.urls);
    JSONObject json = new JSONObject(action.bodies.get(0));
    assertEquals("no other field is sent", 11, json.length());
    assertEquals("Acme Seeds Ltd", json.getString("name"));
    assertEquals("ACME", json.getString("acronym"));
    assertEquals("https://acme.test", json.getString("websiteLink"));
    assertEquals(7, json.getInt("institutionTypeCode"));
    assertEquals("KE", json.getString("hqCountryIso"));
    assertEquals("Partner request Cluster C77 - Climate smart villages", json.getString("requestSource"));
    assertEquals("aiccra", json.getString("misAcronym"));
    assertEquals(9, json.getInt("userId"));
    assertEquals("ana@unit.test", json.getString("externalUserMail"));
    assertEquals("ana.lopez", json.getString("externalUserName"));
    assertEquals("Request made from MARLO", json.getString("externalUserComments"));
  }

  @Test
  public void testPayloadAcronymIsTheLowerCaseAcronymOfTheUnit() throws Exception {
    DrivenAction action = this.action(clarisaUnit(47L, "AICCRA_III"));

    assertEquals("success", action.save());

    assertEquals("aiccra_iii", new JSONObject(action.bodies.get(0)).getString("misAcronym"));
  }

  @Test
  public void testPayloadSourceIsTheGlobalTextForAUnitWithoutAnOverride() throws Exception {
    DrivenAction action = this.action(clarisaUnit(5L, "PABRA"), new ClarisaConfig(HOST, USERNAME, PASSWORD), null);

    assertEquals("success", this.saveFromProject(action));

    JSONObject json = new JSONObject(action.bodies.get(0));
    assertEquals("Partner request Project P77 - Climate smart villages", json.getString("requestSource"));
    assertEquals("pabra", json.getString("misAcronym"));
  }

  @Test
  public void testPayloadSourceFallsBackWhenThePageHadNoItem() throws Exception {
    DrivenAction action = this.action(clarisaUnit(45L, "AICCRA"));

    assertEquals("success", action.save());

    assertEquals("Request made from MARLO", new JSONObject(action.bodies.get(0)).getString("requestSource"));
  }

  // ---- D3: failure -----------------------------------------------------------------------------------------------

  private void assertFailsVisibly(DrivenAction action, int seamCalls, String why) {
    assertEquals(why, "input", action.save());
    assertTrue(why, action.isRequestFailed());
    assertFalse(why, action.isMessageSent());
    assertEquals(why + ": seam calls", seamCalls, action.urls.size());
    this.assertNothingStoredNorAlerted(why);
  }

  @Test
  public void testRejectionIsAVisibleFailureWithOneWarning() throws Exception {
    DrivenAction action = this.action(clarisaUnit(45L, "AICCRA"));
    action.status = 400;

    this.assertFailsVisibly(action, 1, "status 400");

    assertTrue(action.isClarisaMode());
    ILoggingEvent event = this.onlyEvent();
    assertEquals(Level.WARN, event.getLevel());
    assertEquals("CLARISA did not accept the partner request of the Global Unit 45, status 400",
      event.getFormattedMessage());
  }

  @Test
  public void testConnectionFailureIsAVisibleFailureLoggedByClassName() throws Exception {
    DrivenAction action = this.action(clarisaUnit(45L, "AICCRA"));
    action.failure = new IOException("refused for " + REQUESTER_EMAIL + " with " + PASSWORD);

    this.assertFailsVisibly(action, 1, "IOException");

    ILoggingEvent event = this.onlyEvent();
    assertEquals(Level.ERROR, event.getLevel());
    assertEquals("Could not send the partner request of the Global Unit 45 to CLARISA: IOException",
      event.getFormattedMessage());
  }

  @Test
  public void testTimeoutIsAVisibleFailureAndItsMessageStaysOutOfTheLog() throws Exception {
    DrivenAction action = this.action(clarisaUnit(45L, "AICCRA"));
    action.failure = new SocketTimeoutException("Read timed out for " + REQUESTER_EMAIL + " with " + PASSWORD
      + " as " + USERNAME + " Authorization: Basic abc");

    this.assertFailsVisibly(action, 1, "timeout");

    ILoggingEvent event = this.onlyEvent();
    assertEquals(Level.ERROR, event.getLevel());
    assertEquals("Could not send the partner request of the Global Unit 45 to CLARISA: SocketTimeoutException",
      event.getFormattedMessage());
  }

  @Test
  public void testMissingConfigurationFailsWithoutCallingClarisa() throws Exception {
    ClarisaConfig[] configs = {new ClarisaConfig("", USERNAME, PASSWORD), new ClarisaConfig(HOST, "  ", PASSWORD),
      new ClarisaConfig(HOST, USERNAME, ""), new ClarisaConfig(null, USERNAME, PASSWORD)};
    for (int i = 0; i < configs.length; i++) {
      this.logged.list.clear();
      DrivenAction action = this.action(clarisaUnit(45L, "AICCRA"), configs[i], null);

      this.assertFailsVisibly(action, 0, "configuration case " + i);

      ILoggingEvent event = this.onlyEvent();
      assertEquals(Level.ERROR, event.getLevel());
      assertEquals("The CLARISA connection is not configured; the partner request of the Global Unit 45 was not sent",
        event.getFormattedMessage());
    }
  }

  @Test
  public void testUnitWithoutAcronymFailsWithoutCallingClarisa() throws Exception {
    DrivenAction action = this.action(clarisaUnit(45L, null));

    this.assertFailsVisibly(action, 0, "no acronym");

    ILoggingEvent event = this.onlyEvent();
    assertEquals(Level.ERROR, event.getLevel());
    assertEquals("The Global Unit 45 has no acronym; the partner request was not sent to CLARISA",
      event.getFormattedMessage());
  }

  @Test
  public void testNoUnitStoresNothingSendsNothingAndLogsOnce() throws Exception {
    DrivenAction action = this.action(null);

    this.assertFailsVisibly(action, 0, "no unit");

    assertFalse("the page shows the saved-failure text", action.isClarisaMode());
    ILoggingEvent event = this.onlyEvent();
    assertEquals(Level.ERROR, event.getLevel());
    assertEquals("The partner request has no Global Unit; nothing was stored or sent", event.getFormattedMessage());
  }

  // ---- D11: the stored parameters decide, not the session ---------------------------------------------------------

  @Test
  public void testStoredFalseWinsOverATrueInTheSession() throws Exception {
    DrivenAction action =
      this.action(unit(1L, "AICCRA", row(APConstants.PARTNER_REQUESTS_TO_CLARISA, "false", true)));
    action.sessionSaysClarisa = true;

    assertEquals("success", action.save());

    assertTrue("the seam is never called", action.urls.isEmpty());
    assertEquals("the request is stored", 1, this.stored.size());
    assertFalse(action.isClarisaMode());
  }

  @Test
  public void testAnythingButTrueInTheStoredValueIsMarloMode() throws Exception {
    for (String value : new String[] {"TRUE ", "yes", "", null}) {
      this.stored.clear();
      DrivenAction action =
        this.action(unit(1L, "AICCRA", row(APConstants.PARTNER_REQUESTS_TO_CLARISA, value, true)));

      assertEquals("success", action.save());

      assertTrue("[" + value + "] never reaches CLARISA", action.urls.isEmpty());
      assertEquals("[" + value + "] is stored", 1, this.stored.size());
      assertFalse(action.isClarisaMode());
    }
  }

  @Test
  public void testATrueOnAnInactiveRowIsMarloMode() throws Exception {
    DrivenAction action =
      this.action(unit(1L, "AICCRA", row(APConstants.PARTNER_REQUESTS_TO_CLARISA, "true", false)));

    assertEquals("success", action.save());

    assertTrue(action.urls.isEmpty());
    assertEquals(1, this.stored.size());
    assertFalse(action.isClarisaMode());
  }

  @Test
  public void testIncompleteRowsAreSkippedAndAMissingRowIsMarloMode() throws Exception {
    CustomParameter withoutParameter = new CustomParameter();
    withoutParameter.setValue("true");
    withoutParameter.setActive(true);
    withoutParameter.setId(900L);
    CustomParameter withoutKey = row(null, "true", true);
    DrivenAction action = this.action(unit(1L, "AICCRA", withoutParameter, withoutKey,
      row("some_other_key", "true", true), row(APConstants.PARTNER_REQUESTS_TO_CLARISA, "true", true)));

    assertEquals("success", action.save());

    assertEquals("the one complete active row turns CLARISA mode on", 1, action.urls.size());
    assertTrue(this.stored.isEmpty());

    this.stored.clear();
    DrivenAction noRow =
      this.action(unit(1L, "AICCRA", withoutParameter, withoutKey, row("some_other_key", "true", true)));
    assertEquals("success", noRow.save());
    assertTrue(noRow.urls.isEmpty());
    assertEquals(1, this.stored.size());
  }

  // ---- isClarisaMode: what the page sees --------------------------------------------------------------------------

  @Test
  public void testThePageSeesTheModeThatHandledTheSubmit() throws Exception {
    DrivenAction clarisaFailure = this.action(clarisaUnit(45L, "AICCRA"));
    clarisaFailure.status = 500;
    assertEquals("input", clarisaFailure.save());
    assertTrue("after a CLARISA failure", clarisaFailure.isClarisaMode());
    assertTrue(clarisaFailure.isRequestFailed());

    DrivenAction marloFailure = this.action(unit(45L, "AICCRA"));
    this.set(marloFailure, "partnerRequestManager", EmailSendDoubles.answering(PartnerRequestManager.class,
      "createPartnerRequest", args -> {
        throw new IllegalStateException("the save was rolled back");
      }));
    assertEquals("input", marloFailure.save());
    assertFalse("after a MARLO failure", marloFailure.isClarisaMode());
    assertTrue(marloFailure.isRequestFailed());
  }
}
