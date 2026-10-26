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

import java.lang.reflect.Proxy;
import java.util.Map;

import com.opensymphony.xwork2.LocalizedTextProvider;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * A validator adds a missing field only when its text is not empty, so a text key without a value must come back as
 * the key, as it does on the page actions; it used to come back null and the missing field was lost.
 */
public class ValidateProjectSectionActionTextTest {

  private static final Map<String, String> TEXTS = Map.of("activity.statusProgress", "Activity {0} Progress");

  private static ValidateProjectSectionAction action() {
    LocalizedTextProvider texts = (LocalizedTextProvider) Proxy.newProxyInstance(
      LocalizedTextProvider.class.getClassLoader(), new Class<?>[] {LocalizedTextProvider.class},
      (proxy, method, args) -> "findDefaultText".equals(method.getName()) ? TEXTS.get(args[0]) : null);
    return new ValidateProjectSectionAction(null, null, null, null, null, texts, null, null, null);
  }

  @Test
  public void aKeyWithATextAnswersTheText() {
    assertEquals("Activity {0} Progress", action().getText("activity.statusProgress"));
  }

  @Test
  public void aKeyWithoutATextAnswersTheKey() {
    assertEquals("project.locationsData", action().getText("project.locationsData"));
  }

  @Test
  public void aKeyWithoutATextAnswersTheKeyWithArguments() {
    assertEquals("project.locationsData", action().getText("project.locationsData", new String[] {"1"}));
  }
}
