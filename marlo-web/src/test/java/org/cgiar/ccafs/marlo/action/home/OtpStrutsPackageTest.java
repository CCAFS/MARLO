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

package org.cgiar.ccafs.marlo.action.home;

import org.cgiar.ccafs.marlo.data.manager.CrpUserManager;
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.OtpChallengeManager;
import org.cgiar.ccafs.marlo.data.manager.OtpRateLimitManager;
import org.cgiar.ccafs.marlo.data.manager.ParameterManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.security.otp.ClientIpResolver;
import org.cgiar.ccafs.marlo.security.otp.OtpCodes;
import org.cgiar.ccafs.marlo.security.otp.OtpEligibility;
import org.cgiar.ccafs.marlo.security.otp.OtpKeys;
import org.cgiar.ccafs.marlo.security.otp.OtpRateLimiter;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.time.Clock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Scope;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.junit.XWorkJUnit4TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * CHG-OTP-LOGIN-001-T12 (FN-001, SEC-004, NF-002; design 4, DD-1; P-14, P-21, P-26, P-28): proves the {@code homeOtp}
 * Struts package through the configuration the application really loads ({@code struts-default.xml}, the struts2-json
 * plugin's {@code struts-plugin.xml}, and the real {@code struts.xml} with every include), never through an action
 * built by hand. Struts builds the actions, so their parameters, interceptors and JSON result are the XML's.
 * <p>
 * Two stand-ins, both outside the package under test: the {@link ObjectFactory} hands out the two actions built with
 * an unconfigured secret (the production factory is Spring's), and the {@code i18nFile} interceptor, which needs the
 * Spring graph, becomes a no-op. Every other member of {@code otpUnloggedStack} and of {@code defaultStack} is the
 * real one.
 */
public class OtpStrutsPackageTest extends XWorkJUnit4TestCase {

  /** The action the factory returns for the two OTP classes; a test may replace it before the request runs. */
  private static final Map<Class<?>, Object> ACTIONS = new HashMap<Class<?>, Object>();

  /** Builds the two actions from an unconfigured secret, and swaps the Spring-dependent interceptor. */
  public static class TestObjectFactory extends ObjectFactory {

    private static final long serialVersionUID = 1L;

    /** The production factory is Spring's, which needs no public no-arg constructor: the MARLO actions have none. */
    @Override
    public boolean isNoArgConstructorRequired() {
      return false;
    }

    @Override
    public Interceptor buildInterceptor(InterceptorConfig interceptorConfig, Map<String, String> interceptorRefParams)
      throws ConfigurationException {
      if ("i18nFile".equals(interceptorConfig.getName())) {
        return new AbstractInterceptor() {

          private static final long serialVersionUID = 1L;

          @Override
          public String intercept(ActionInvocation invocation) throws Exception {
            return invocation.invoke();
          }
        };
      }
      return super.buildInterceptor(interceptorConfig, interceptorRefParams);
    }

    @Override
    public Object buildBean(Class clazz, Map<String, Object> extraContext) throws Exception {
      Object prepared = ACTIONS.get(clazz);
      return prepared != null ? prepared : super.buildBean(clazz, extraContext);
    }
  }

  /** Registers {@link TestObjectFactory} as the container's {@link ObjectFactory}. */
  private static final class FactoryProvider implements ConfigurationProvider {

    @Override
    public void destroy() {
      // nothing to release
    }

    @Override
    public void init(Configuration configuration) {
      // nothing to initialise
    }

    @Override
    public void loadPackages() {
      // the packages come from the XML providers
    }

    @Override
    public boolean needsReload() {
      return false;
    }

    @Override
    public void register(ContainerBuilder builder, LocatableProperties props) {
      builder.factory(ObjectFactory.class, Container.DEFAULT_NAME, TestObjectFactory.class, Scope.SINGLETON);
      // Normally read from default.properties and struts.properties, which the plain XWork test container skips.
      props.setProperty("struts.i18n.encoding", "UTF-8");
    }
  }

  /**
   * The struts2-json plugin's own {@code struts-plugin.xml}, found on the classpath. The convention and spring plugins
   * ship a file of the same name that scans the classpath and needs the servlet container, so they are left out.
   */
  private static final class JsonPluginProvider extends StrutsXmlConfigurationProvider {

    JsonPluginProvider() {
      super("struts-plugin.xml");
    }

    @Override
    protected Iterator<URL> getConfigurationUrls(String fileName) throws IOException {
      List<URL> json = new ArrayList<URL>();
      Iterator<URL> all = super.getConfigurationUrls(fileName);
      while (all.hasNext()) {
        URL url = all.next();
        if (url.toString().contains("struts2-json-plugin")) {
          json.add(url);
        }
      }
      assertEquals("exactly one struts2-json-plugin struts-plugin.xml must be on the classpath", 1, json.size());
      return json.iterator();
    }
  }

  private static Object unusable(Class<?> type) {
    return Proxy.newProxyInstance(OtpStrutsPackageTest.class.getClassLoader(), new Class<?>[] {type},
      new InvocationHandler() {

        @Override
        public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args) {
          throw new AssertionError("an unconfigured request must not reach " + type.getSimpleName() + "."
            + method.getName());
        }
      });
  }

  private static APConfig unconfigured() {
    return new APConfig() {

      @Override
      public String getBaseUrl() {
        return "http://localhost:8080";
      }

      @Override
      public String getOtpHmacSecret() {
        return "";
      }

      @Override
      public String getOtpTrustedProxies() {
        return "";
      }
    };
  }

  private static OtpRequestCodeAction requestAction() {
    APConfig config = unconfigured();
    OtpKeys keys = new OtpKeys(config);
    OtpChallengeManager challenges = (OtpChallengeManager) unusable(OtpChallengeManager.class);
    GlobalUnitManager units = (GlobalUnitManager) unusable(GlobalUnitManager.class);
    CustomParameterManager customParameters = (CustomParameterManager) unusable(CustomParameterManager.class);
    OtpEligibility eligibility = new OtpEligibility(keys, (UserManager) unusable(UserManager.class), units,
      (CrpUserManager) unusable(CrpUserManager.class), challenges, customParameters,
      (ParameterManager) unusable(ParameterManager.class));
    return new OtpRequestCodeAction(config, keys,
      new OtpRateLimiter(keys, (OtpRateLimitManager) unusable(OtpRateLimitManager.class)), new OtpCodes(keys),
      eligibility, challenges, units, customParameters,
      (texts, assetsUrl, recipient, code, acronym, unitId) -> {
        throw new AssertionError("an unconfigured request must not send mail");
      }, new ClientIpResolver(""), Clock.systemUTC());
  }

  private static OtpVerifyCodeAction verifyAction() {
    APConfig config = unconfigured();
    OtpKeys keys = new OtpKeys(config);
    OtpChallengeManager challenges = (OtpChallengeManager) unusable(OtpChallengeManager.class);
    GlobalUnitManager units = (GlobalUnitManager) unusable(GlobalUnitManager.class);
    UserManager users = (UserManager) unusable(UserManager.class);
    CrpUserManager crpUsers = (CrpUserManager) unusable(CrpUserManager.class);
    CustomParameterManager customParameters = (CustomParameterManager) unusable(CustomParameterManager.class);
    ParameterManager parameters = (ParameterManager) unusable(ParameterManager.class);
    return new OtpVerifyCodeAction(config, users, units, crpUsers, customParameters, parameters,
      new OtpRateLimiter(keys, (OtpRateLimitManager) unusable(OtpRateLimitManager.class)), new OtpCodes(keys),
      new OtpEligibility(keys, users, units, crpUsers, challenges, customParameters, parameters), challenges,
      new ClientIpResolver(""), Clock.systemUTC());
  }

  /** The raw field: {@code getCrpSession()} derefs the servlet action mapping, which this container has none of. */
  private static Object crpSessionField(Object action) throws Exception {
    java.lang.reflect.Field field = org.cgiar.ccafs.marlo.action.BaseAction.class.getDeclaredField("crpSession");
    field.setAccessible(true);
    return field.get(action);
  }

  private static List<String> interceptorNames(ActionConfig action) {
    List<String> names = new ArrayList<String>();
    for (InterceptorMapping mapping : action.getInterceptors()) {
      names.add(mapping.getName());
    }
    return names;
  }

  private MockHttpServletResponse response;

  @Before
  public void loadRealConfiguration() {
    ACTIONS.clear();
    this.loadConfigurationProviders(new StrutsXmlConfigurationProvider("struts-default.xml"),
      new JsonPluginProvider(), new StrutsXmlConfigurationProvider("struts.xml"),
      new FactoryProvider());
  }

  @After
  public void clear() {
    ACTIONS.clear();
    ActionContext.clear();
  }

  /** Runs {@code /name} through the configured package with the given request parameters, rendering the result. */
  private String run(String name, Map<String, String> parameters) throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/" + name + ".do");
    this.response = new MockHttpServletResponse();
    Map<String, String[]> raw = new HashMap<String, String[]>();
    for (Map.Entry<String, String> entry : parameters.entrySet()) {
      raw.put(entry.getKey(), new String[] {entry.getValue()});
    }
    Map<String, Object> extra = ActionContext.of(new HashMap<String, Object>())
      .withParameters(HttpParameters.create(raw).build()).withSession(new HashMap<String, Object>()).getContextMap();
    extra.put(StrutsStatics.HTTP_REQUEST, request);
    extra.put(StrutsStatics.HTTP_RESPONSE, this.response);
    ActionProxy proxy = this.actionProxyFactory.createActionProxy("/", name, null, extra);
    return proxy.execute();
  }

  private static Map<String, String> params(String... keysAndValues) {
    Map<String, String> map = new LinkedHashMap<String, String>();
    for (int i = 0; i < keysAndValues.length; i += 2) {
      map.put(keysAndValues[i], keysAndValues[i + 1]);
    }
    return map;
  }

  private PackageConfig homeOtp() {
    PackageConfig pack = this.configuration.getPackageConfig("homeOtp");
    assertNotNull("struts-home.xml must declare the package homeOtp (DD-1)", pack);
    return pack;
  }

  // ---- Reachability: P-26 and FN-001 -------------------------------------------------------------------------

  @Test
  public void homeOtpIsRegisteredAndExtendsBothMarloDefaultAndJsonDefault() {
    PackageConfig pack = this.homeOtp();
    List<String> parents = new ArrayList<String>();
    for (PackageConfig parent : pack.getParents()) {
      parents.add(parent.getName());
    }
    assertTrue("P-26: extends=\"marlo-default,json-default\" must load: " + parents,
      parents.contains("marlo-default") && parents.contains("json-default"));
    assertEquals("/", pack.getNamespace());
  }

  @Test
  public void bothActionsAreReachableAtTheirDoRoutesThroughOtpUnloggedStack() throws Exception {
    this.homeOtp();
    Map<String, Class<?>> expected = new LinkedHashMap<String, Class<?>>();
    expected.put("otpRequestCode", OtpRequestCodeAction.class);
    expected.put("otpVerifyCode", OtpVerifyCodeAction.class);
    for (Map.Entry<String, Class<?>> route : expected.entrySet()) {
      ActionConfig action = this.configuration.getRuntimeConfiguration().getActionConfig("/", route.getKey());
      assertNotNull(route.getKey() + " must be reachable in the runtime configuration", action);
      assertEquals(route.getValue().getName(), action.getClassName());
      assertEquals("homeOtp", action.getPackageName());
      List<String> names = interceptorNames(action);
      assertEquals("logErrorStatus must run first, once", "logErrorStatus", names.get(0));
      assertEquals(1, java.util.Collections.frequency(names, "logErrorStatus"));
      assertTrue("i18nFile must run: " + names, names.contains("i18nFile"));
      assertTrue("the standard param/validation pipeline must run: " + names, names.contains("params"));
      assertFalse("no requireUser (pre-authentication): " + names, names.contains("requireUser"));
      assertFalse("validCrp assumes a {crp}/ URL: " + names, names.contains("validCrp"));
      assertFalse("validSessionCrp assumes a {crp}/ URL: " + names, names.contains("validSessionCrp"));
    }
  }

  @Test
  public void aRequestRunsThroughThePackageAndAnswersJson() throws Exception {
    ACTIONS.put(OtpRequestCodeAction.class, requestAction());
    assertEquals("success", this.run("otpRequestCode", params("email", "x@example.org", "globalUnitId", "1")));
    assertTrue(this.response.getContentType(), this.response.getContentType().startsWith("application/json"));
    assertEquals("{\"status\":\"domainNotEnabled\"}", this.response.getContentAsString());

    ACTIONS.put(OtpVerifyCodeAction.class, verifyAction());
    assertEquals("success", this.run("otpVerifyCode", params("code", "123456")));
    assertEquals("{\"status\":\"unavailable\"}", this.response.getContentAsString());
  }

  // ---- SEC-004: only `response` is serialised -----------------------------------------------------------------

  @Test
  public void onlyTheResponseKeysAreSerialisedForAPopulatedAction() throws Exception {
    OtpRequestCodeAction request = requestAction();
    request.getResponse().put("status", "sent");
    request.getResponse().put("destination", "a***@example.org");
    ACTIONS.put(OtpRequestCodeAction.class, request);
    // Render the result of the package with this populated action, without running execute() again.
    ActionProxy proxy = this.actionProxyFactory.createActionProxy("/", "otpRequestCode", null, this.extra());
    ResultConfig resultConfig = proxy.getConfig().getResults().get("success");
    assertNotNull(resultConfig);
    assertEquals("root must be response", "response", resultConfig.getParams().get("root"));
    this.renderSuccess(proxy);
    assertEquals("{\"status\":\"sent\",\"destination\":\"a***@example.org\"}", this.response.getContentAsString());
    for (String forbidden : new String[] {"session", "user", "config", "crp", "texts", "request", "actionErrors"}) {
      assertFalse(forbidden + " must never be serialised: " + this.response.getContentAsString(),
        this.response.getContentAsString().contains("\"" + forbidden + "\""));
    }

    OtpVerifyCodeAction verify = verifyAction();
    verify.getResponse().put("status", "ok");
    verify.getResponse().put("redirect", "http://localhost:8080/x");
    ACTIONS.put(OtpVerifyCodeAction.class, verify);
    ActionProxy verifyProxy = this.actionProxyFactory.createActionProxy("/", "otpVerifyCode", null, this.extra());
    this.renderSuccess(verifyProxy);
    // The JSON writer escapes the slash, which is still valid JSON.
    assertEquals("{\"status\":\"ok\",\"redirect\":\"http:\\/\\/localhost:8080\\/x\"}",
      this.response.getContentAsString());
  }

  private Map<String, Object> extra() {
    this.response = new MockHttpServletResponse();
    Map<String, Object> extra =
      ActionContext.of(new HashMap<String, Object>()).withSession(new HashMap<String, Object>()).getContextMap();
    extra.put(StrutsStatics.HTTP_REQUEST, new MockHttpServletRequest("POST", "/x.do"));
    extra.put(StrutsStatics.HTTP_RESPONSE, this.response);
    return extra;
  }

  /** Executes the configured {@code success} result of the proxy's action, as the invocation would. */
  private void renderSuccess(ActionProxy proxy) throws Exception {
    ActionContext.of(proxy.getInvocation().getInvocationContext().getContextMap()).bind();
    ResultConfig resultConfig = proxy.getConfig().getResults().get("success");
    com.opensymphony.xwork2.Result result = this.container.getInstance(ObjectFactory.class)
      .buildResult(resultConfig, proxy.getInvocation().getInvocationContext().getContextMap());
    this.container.inject(result);
    result.execute(proxy.getInvocation());
  }

  // ---- SEC-004 / RJ-9: refused parameters ---------------------------------------------------------------------

  @Test
  public void aRequestParameterOutsideTheAllowListIsRefusedByAcceptableParameterName() {
    OtpRequestCodeAction request = requestAction();
    for (String accepted : new String[] {"email", "globalUnitId", "agree"}) {
      assertTrue(accepted, request.acceptableParameterName(accepted));
    }
    for (String refused : new String[] {"user.email", "crpSession", "url", "user", "crp", "globalUnit", "code",
      "response", "email.x", "email[0]", "(#a=1)", "session", "agreed", "Email", ""}) {
      assertFalse(refused, request.acceptableParameterName(refused));
    }
    assertFalse(request.acceptableParameterName(null));
    OtpVerifyCodeAction verify = verifyAction();
    assertTrue(verify.acceptableParameterName("code"));
    for (String refused : new String[] {"email", "globalUnitId", "agree", "user.email", "crpSession", "url", "user",
      "crp", "globalUnit", "response", "code.x", "code[0]", "Code", ""}) {
      assertFalse(refused, verify.acceptableParameterName(refused));
    }
    assertFalse(verify.acceptableParameterName(null));
  }

  @Test
  public void theParamsInterceptorOfThePackageNeverBindsARefusedParameter() throws Exception {
    OtpRequestCodeAction request = requestAction();
    ACTIONS.put(OtpRequestCodeAction.class, request);
    this.run("otpRequestCode", params("email", "x@example.org", "crpSession", "hacked", "url", "http://evil.test/",
      "user.email", "y@example.org", "globalUnitId", "1"));
    assertNull("crpSession must not be bound", crpSessionField(request));
    assertEquals("{\"status\":\"domainNotEnabled\"}", this.response.getContentAsString());

    OtpVerifyCodeAction verify = verifyAction();
    ACTIONS.put(OtpVerifyCodeAction.class, verify);
    this.run("otpVerifyCode", params("code", "123456", "crpSession", "hacked", "url", "http://evil.test/"));
    assertNull("crpSession must not be bound", crpSessionField(verify));
    assertNull("url must not be bound", verify.getUrl());
    assertNull("user must not be bound", verify.getUser());
  }

  // ---- RJ-9: the global input result is JSON ------------------------------------------------------------------

  /**
   * Records the field error that the {@code conversionError} interceptor records for a value that fails to convert,
   * before {@code workflow} runs: it answers {@code input} for any action with field errors.
   */
  public static final class ConversionFailingRequestAction extends OtpRequestCodeAction {

    private static final long serialVersionUID = 1L;

    ConversionFailingRequestAction() {
      super(unconfigured(), new OtpKeys(unconfigured()), null, null, null, null, null, null, null,
        new ClientIpResolver(""), Clock.systemUTC());
    }

    @Override
    public void prepare() throws Exception {
      this.addFieldError("globalUnitId", "Invalid field value for field \"globalUnitId\".");
    }
  }

  @Test
  public void anInvalidConversionYieldsTheJsonInputResult() throws Exception {
    ACTIONS.put(OtpRequestCodeAction.class, new ConversionFailingRequestAction());
    assertEquals("input", this.run("otpRequestCode", params("email", "x@example.org", "globalUnitId", "abc")));
    assertTrue(this.response.getContentType(), this.response.getContentType().startsWith("application/json"));
    assertEquals("{\"status\":\"unavailable\"}", this.response.getContentAsString());
  }

  @Test
  public void theGlobalInputResultIsJsonWithRootResponse() {
    ResultConfig input = this.homeOtp().getAllGlobalResults().get("input");
    assertNotNull("homeOtp must declare a global input result (RJ-9)", input);
    assertEquals("json", input.getClassName().contains("JSONResult") ? "json" : input.getClassName());
    assertEquals("response", input.getParams().get("root"));
  }
}
