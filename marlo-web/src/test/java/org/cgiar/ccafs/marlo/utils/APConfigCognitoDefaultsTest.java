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

package org.cgiar.ccafs.marlo.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.PropertiesPropertySource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Proves CHG-COGNITO-AUTH-001-T03's central claim: the application starts on an environment that has
 * never heard of Cognito, and every Cognito getter answers with an empty string rather than {@code null}
 * or an exception.
 * <p>
 * <b>The property source is deliberately built by stripping every {@code cognito.*} key out of the real
 * tracked {@code marlo-test.properties}.</b> That is T03's <i>Not evidence when</i> clause made
 * executable: <i>"verified on a machine whose properties file was already updated… the whole point is the
 * un-updated case."</i> Handing the context a curated map of only the keys it needs would prove nothing,
 * because it would silently supply whatever `APConfig` happens to require today.
 * <p>
 * The template is read <b>from the source tree by path</b>, not from the classpath, so that this test reads
 * the file a developer edits rather than whatever an earlier build happened to stage. Production loads the
 * same file from the classpath — {@code ApplicationContextConfig} declares
 * {@code @PropertySource("classpath:config/marlo-${spring.profiles.active:dev}.properties")} — and the two
 * copies can drift for a whole build cycle. The side effect is the valuable part: adding an {@code @Value}
 * to {@code APConfig} without adding the key to the tracked template now fails this test instead of
 * failing a deployment.
 */
public class APConfigCognitoDefaultsTest {

  /** The tracked bootstrap template, relative to the module root that Surefire runs in. */
  private static final String TEMPLATE = "src/main/resources/config/marlo-test.properties";

  /**
   * Keys {@code APConfig} requires that the tracked template does not supply. Pre-existing and unrelated
   * to Cognito; see {@code execution.md} PS-5. This list must only ever shrink.
   */
  private static final String[] KNOWN_TEMPLATE_GAPS = {"email.pmu", "clarisa.wos.link2"};

  /**
   * Every property MARLO's own template declares, minus every Cognito key -- i.e. exactly what an
   * environment that predates this task looks like.
   */
  private static Properties propertiesWithoutCognito() throws Exception {
    File template = new File(TEMPLATE);
    assertTrue("cannot find " + template.getAbsolutePath(), template.isFile());

    Properties properties = new Properties();
    try (InputStream in = new FileInputStream(template)) {
      properties.load(in);
    }

    List<String> cognitoKeys = new ArrayList<String>();
    for (String key : properties.stringPropertyNames()) {
      if (key.startsWith("cognito.")) {
        cognitoKeys.add(key);
      }
    }
    // Guards the guard: if the template ever stops carrying the Cognito keys, this test would silently
    // become a no-op that proves nothing about stripping them.
    // CHG-COGNITO-AUTH-001-T15 raised this from 7 to 8: cognito.identity.provider joined the other seven,
    // following the exact same @Value(...:}) + cognitoSetting(...) pattern.
    assertEquals("marlo-test.properties must declare the 8 Cognito keys T03/T15 add", 8, cognitoKeys.size());
    for (String key : cognitoKeys) {
      properties.remove(key);
    }

    // Two placeholders APConfig declares with no default are MISSING from the tracked template, so an
    // environment bootstrapped from it per hard rule 12 fails Spring context startup on them -- before
    // reaching anything Cognito. The gap is pre-existing and outside T03's scope, so it is pinned here
    // rather than quietly repaired in a file this task happens to touch.
    //
    // They are supplied as dummies so this test can measure the question it exists to measure. The
    // assertion below is the point: a NEW @Value added to APConfig without a matching template key will
    // fail here instead of failing a deployment.
    for (String key : KNOWN_TEMPLATE_GAPS) {
      assertTrue("marlo-test.properties now provides '" + key + "'. Remove it from KNOWN_TEMPLATE_GAPS.",
        properties.getProperty(key) == null);
      properties.setProperty(key, "unset-in-template");
    }
    return properties;
  }

  private static AnnotationConfigApplicationContext contextWithout(Properties properties, Class<?> extra) {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
    context.getEnvironment().getPropertySources()
      .addFirst(new PropertiesPropertySource("marlo-test-without-cognito", properties));
    context.register(PlaceholderConfig.class);
    context.register(APConfig.class);
    if (extra != null) {
      context.register(extra);
    }
    context.refresh();
    return context;
  }

  /**
   * The task's stated test: every Cognito getter returns empty -- not {@code null}, not an exception --
   * when the property is absent.
   */
  @Test
  public void everyCognitoGetterReturnsEmptyWhenTheKeyIsAbsent() throws Exception {
    try (AnnotationConfigApplicationContext context = contextWithout(propertiesWithoutCognito(), null)) {
      APConfig config = context.getBean(APConfig.class);

      assertNotNull(config);
      assertEquals("", config.getCognitoUserPoolId());
      assertEquals("", config.getCognitoRegion());
      assertEquals("", config.getCognitoClientId());
      assertEquals("", config.getCognitoClientSecret());
      assertEquals("", config.getCognitoDomain());
      assertEquals("", config.getCognitoCallbackUrl());
      assertEquals("", config.getCognitoJwksUri());
      assertEquals("", config.getCognitoIdentityProvider());
    }
  }

  /**
   * T03's <i>Fails when</i> clause, as a standing regression test rather than a one-off experiment:
   * <i>"the :default suffix is removed from any new @Value -- the context must then fail with
   * BeanCreationException. Prove it once, deliberately."</i>
   * <p>
   * A bean carrying the same placeholder <b>without</b> a default is registered into the same context.
   * If this ever stops throwing, the empty defaults in {@code APConfig} have stopped being load-bearing
   * and design.md 9.3's "phase 0 is inert" guarantee is no longer backed by anything.
   */
  @Test
  public void aPlaceholderWithoutADefaultBreaksTheContext() throws Exception {
    try (AnnotationConfigApplicationContext context =
      contextWithout(propertiesWithoutCognito(), NoDefaultProbe.class)) {
      fail("a ${cognito.*} placeholder with no default must not resolve when the key is absent");
    } catch (BeanCreationException expected) {
      assertTrue("the failure must name the unresolved placeholder, was: " + expected.getMessage(),
        expected.getMessage().contains("cognito.client.id"));
    }
  }

  /**
   * The control for the test above. The identical probe, differing only by the {@code :} that supplies an
   * empty default, must start cleanly. Without this, a green
   * {@code aPlaceholderWithoutADefaultBreaksTheContext} would be consistent with the context failing for
   * some unrelated reason in this hand-built setup.
   */
  @Test
  public void theSamePlaceholderWithAnEmptyDefaultResolvesCleanly() throws Exception {
    try (AnnotationConfigApplicationContext context =
      contextWithout(propertiesWithoutCognito(), WithDefaultProbe.class)) {
      WithDefaultProbe probe = context.getBean(WithDefaultProbe.class);
      assertEquals("", probe.clientId);
    }
  }

  /**
   * Must be a static {@code @Bean}: a {@code BeanFactoryPostProcessor} declared on a non-static method is
   * created too early to post-process reliably.
   */
  @Configuration
  static class PlaceholderConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
      return new PropertySourcesPlaceholderConfigurer();
    }
  }

  /** Carries the placeholder exactly as APConfig must NOT declare it. */
  @Configuration
  static class NoDefaultProbe {

    @Value("${cognito.client.id}")
    String clientId;
  }

  /** Carries the placeholder exactly as APConfig DOES declare it. */
  @Configuration
  static class WithDefaultProbe {

    @Value("${cognito.client.id:}")
    String clientId;
  }
}
