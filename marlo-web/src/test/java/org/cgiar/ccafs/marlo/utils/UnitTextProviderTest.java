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

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

import com.opensymphony.xwork2.TextProvider;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * CHG-OTP-LOGIN-001-T20: the provider of the code email resolves the custom file of the selected unit over
 * {@code global.properties}, from the real files, and formats with MessageFormat as Struts does.
 */
public class UnitTextProviderTest {

  private static final String[] ARGS = {"BODY", "ASSETS"};

  private static Properties load(String resource) throws IOException {
    Properties properties = new Properties();
    try (InputStream stream = UnitTextProviderTest.class.getResourceAsStream(resource)) {
      properties.load(stream);
    }
    return properties;
  }

  private static String layout(UnitTextProvider texts) {
    return texts.getText("email.layout", ARGS);
  }

  @Test
  public void theCustomFileWinsOverGlobalForTheKeysItDefines() {
    UnitTextProvider texts = UnitTextProvider.forCustomFile("aicrra");

    assertTrue(layout(texts), layout(texts).contains("marlo-logo.png"));
    assertTrue(texts.getText("login.otp.email.subject", new String[] {"MARLO"}).startsWith("[AICCRA]"));
    assertTrue(texts.hasKey("email.layout"));
  }

  @Test
  public void aKeyTheCustomFileDoesNotDefineFallsBackToGlobal() throws IOException {
    String key = "login.otp.email.text";
    assertFalse("precondition: the custom file does not define the key",
      load("/custom/aicrra.properties").containsKey(key));
    assertTrue("precondition: global defines it", load("/global.properties").containsKey(key));

    UnitTextProvider texts = UnitTextProvider.forCustomFile("aicrra");

    assertTrue(texts.hasKey(key));
    assertTrue(texts.getText(key, new String[] {"MARLO", "123456", "5", "x@y.org"}).contains("123456"));
    assertEquals("MARLO", texts.getText("login.otp.email.appName"));
  }

  @Test
  public void aMissingNullOrUnsafeFileNameLeavesTheUnitOnGlobalOnly() {
    for (String name : Arrays.asList(null, "", "  ", "noSuchFile", "../global", "custom/aicrra", "a/b",
      "aicrra.properties", "aic\nrra", "x".repeat(65))) {
      UnitTextProvider texts = UnitTextProvider.forCustomFile(name);

      assertEquals("<" + name + "> resolves the base shell, which is the body", "BODY", layout(texts));
      assertEquals("MARLO", texts.getText("login.otp.email.appName"));
    }
  }

  @Test
  public void aTrimmedNameIsAccepted() {
    assertTrue(layout(UnitTextProvider.forCustomFile(" aicrra ")).contains("marlo-logo.png"));
  }

  @Test
  public void everyShellUnitFileResolvesItsOwnShellAndFallsBackForTheRest() {
    for (String file : new String[] {"aicrra", "aiccra3", "test"}) {
      UnitTextProvider texts = UnitTextProvider.forCustomFile(file);

      assertTrue(file, layout(texts).contains("marlo-logo.png"));
      assertEquals(file, "MARLO", texts.getText("login.otp.email.appName"));
    }
  }

  @Test
  public void aValueIsFormattedWithMessageFormatAsStrutsDoes() {
    UnitTextProvider texts = UnitTextProvider.forCustomFile(null);

    assertEquals("Your X sign-in code", texts.getText("login.otp.email.subject", new String[] {"X"}));
    assertEquals("Your X sign-in code",
      texts.getText("login.otp.email.subject", Collections.singletonList("X")));
    assertEquals("Your X sign-in code", texts.getText("login.otp.email.subject", "d", new String[] {"X"}));
  }

  @Test
  public void anUnknownKeyAnswersTheKeyOrTheDefaultValueLikeStruts() {
    UnitTextProvider texts = UnitTextProvider.forCustomFile("aicrra");

    assertEquals("no.such.key", texts.getText("no.such.key"));
    assertEquals("no.such.key", texts.getText("no.such.key", new String[] {"x"}));
    assertEquals("fallback", texts.getText("no.such.key", "fallback"));
    assertFalse(texts.hasKey("no.such.key"));
    assertFalse(texts.hasKey(null));
    assertNull(texts.getText(null, (String) null));
  }

  @Test
  public void twoProvidersAreIndependentAndHoldNoSharedState() {
    UnitTextProvider aiccra = UnitTextProvider.forCustomFile("aicrra");
    UnitTextProvider global = UnitTextProvider.forCustomFile(null);

    assertTrue(layout(aiccra).contains("marlo-logo.png"));
    assertEquals("a later provider for another unit is not affected by the first", "BODY", layout(global));
    assertTrue("and the first still answers its own shell", layout(aiccra).contains("marlo-logo.png"));
    TextProvider asInterface = aiccra;
    assertTrue(asInterface.hasKey("email.layout"));
  }
}
