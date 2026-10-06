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

package org.cgiar.ccafs.marlo.rest.errors;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import static org.junit.Assert.assertEquals;

public class ExceptionTranslatorRouteKeyTest {

  private static final String PATTERN = "/{CGIAREntity}/progresstowards/{id}";

  private static RequestAttributes requestWith(Object pattern) {
    Map<String, Object> values = new HashMap<>();
    values.put(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE, pattern);
    return new RequestAttributes() {

      @Override
      public Object getAttribute(String name, int scope) {
        return scope == SCOPE_REQUEST ? values.get(name) : null;
      }

      @Override
      public String[] getAttributeNames(int scope) {
        return new String[0];
      }

      @Override
      public String getSessionId() {
        return null;
      }

      @Override
      public Object getSessionMutex() {
        return null;
      }

      @Override
      public void registerDestructionCallback(String name, Runnable callback, int scope) {
      }

      @Override
      public void removeAttribute(String name, int scope) {
      }

      @Override
      public Object resolveReference(String key) {
        return null;
      }

      @Override
      public void setAttribute(String name, Object value, int scope) {
      }
    };
  }

  @Test
  public void differentIdsOfTheSameEndpointShareOneKey() {
    assertEquals(PATTERN, ExceptionTranslator.routeForKey(requestWith(PATTERN), "/api/AICCRA/progresstowards/7"));
    assertEquals(PATTERN, ExceptionTranslator.routeForKey(requestWith(PATTERN), "/api/AICCRA/progresstowards/8"));
  }

  @Test
  public void fallsBackToThePathWithoutAPattern() {
    assertEquals("/api/x", ExceptionTranslator.routeForKey(requestWith(null), "/api/x"));
    assertEquals("/api/x", ExceptionTranslator.routeForKey(requestWith(""), "/api/x"));
    assertEquals("/api/x", ExceptionTranslator.routeForKey(null, "/api/x"));
  }
}
