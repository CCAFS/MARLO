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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist;

import org.cgiar.ccafs.marlo.rest.errors.ErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ENH-LOGGING-STANDARDIZATION-001-T17: the collaborators of a REST v2 item (the managers and the mapper) as JDK
 * proxies, so that an item can be run for real, without a database, against the entities a test builds. A call that
 * the test did not expect fails loudly instead of answering null.
 */
final class Fakes {

  /**
   * What a fake answers to a call.
   */
  interface Answer {

    Object answer(Object[] args) throws Throwable;
  }

  /**
   * A collaborator under construction.
   */
  static final class Fake<T> {

    private final Class<T> type;
    private final Map<String, Answer> answers = new HashMap<String, Answer>();
    private final List<String> calls = new ArrayList<String>();

    private Fake(Class<T> type) {
      this.type = type;
    }

    /**
     * @return the fake
     */
    @SuppressWarnings("unchecked")
    T build() {
      return (T) Proxy.newProxyInstance(this.type.getClassLoader(), new Class<?>[] {this.type},
        (proxy, method, args) -> {
          if (method.getDeclaringClass() == Object.class) {
            switch (method.getName()) {
              case "equals":
                return proxy == args[0];
              case "hashCode":
                return System.identityHashCode(proxy);
              default:
                return this.type.getSimpleName();
            }
          }
          this.calls.add(method.getName());
          Answer answer = this.answers.get(method.getName());
          if (answer == null) {
            throw new UnsupportedOperationException(
              "unexpected call to " + this.type.getSimpleName() + "." + method.getName());
          }
          return answer.answer(args == null ? new Object[0] : args);
        });
    }

    /**
     * The names of the methods that were called, in order.
     */
    List<String> calls() {
      return this.calls;
    }

    /**
     * Answers a call with what the answer computes from its arguments.
     */
    Fake<T> on(String method, Answer answer) {
      this.answers.put(method, answer);
      return this;
    }

    /**
     * Answers every call of the method with the same value.
     */
    Fake<T> returning(String method, Object value) {
      return this.on(method, args -> value);
    }
  }

  /**
   * The field errors of a response body, as {@code field: message}.
   */
  static List<String> fieldErrors(Object body) {
    List<String> errors = new ArrayList<String>();
    List<FieldErrorDTO> fieldErrors = ((ErrorDTO) body).getFieldErrors();
    if (fieldErrors != null) {
      for (FieldErrorDTO fieldError : fieldErrors) {
        errors.add(fieldError.getField() + ": " + fieldError.getMessage());
      }
    }
    return errors;
  }

  /**
   * Starts a fake of a manager or mapper interface.
   */
  static <T> Fake<T> fake(Class<T> type) {
    return new Fake<T>(type);
  }

  /**
   * An item whose collaborators are all null, as its single constructor leaves them. The test then sets the ones the
   * call under test uses with {@link RestErrorHarness#inject(Object, String, Object)}.
   */
  static <T> T instance(Class<T> type) {
    try {
      Constructor<?> constructor = type.getConstructors()[0];
      return type.cast(constructor.newInstance(new Object[constructor.getParameterCount()]));
    } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
      throw new AssertionError("Cannot create " + type.getName(), e);
    }
  }

  private Fakes() {
  }
}
