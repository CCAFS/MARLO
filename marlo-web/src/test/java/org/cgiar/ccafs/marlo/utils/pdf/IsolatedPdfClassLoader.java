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

package org.cgiar.ccafs.marlo.utils.pdf;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads the classes of this package afresh (child first, so their static state starts empty) and records every
 * classpath resource they open. Resources can be hidden to stand in for a WAR entry that is missing. Everything outside
 * the package comes from the test's own class loader.
 * <p>
 * The PDF engine opens its resources with {@code Class.getResourceAsStream}, which for a class in an unnamed module
 * asks that class's loader; that is the only point this loader observes, and it changes nothing else.
 */
final class IsolatedPdfClassLoader extends ClassLoader {

  private static final String PACKAGE = "org.cgiar.ccafs.marlo.utils.pdf.";

  private final Set<String> hidden = ConcurrentHashMap.newKeySet();
  private final List<String> requested = Collections.synchronizedList(new ArrayList<String>());

  IsolatedPdfClassLoader() {
    super(IsolatedPdfClassLoader.class.getClassLoader());
  }

  @Override
  public InputStream getResourceAsStream(String name) {
    requested.add(name);
    if (hidden.contains(name)) {
      return null;
    }
    return getParent().getResourceAsStream(name);
  }

  /** From now on, {@code name} (as the class loader sees it: no leading slash) is not found. */
  void hide(String name) {
    hidden.add(name);
  }

  @Override
  protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    if (!name.startsWith(PACKAGE) || name.indexOf('.', PACKAGE.length()) >= 0) {
      return super.loadClass(name, resolve);
    }
    synchronized (getClassLoadingLock(name)) {
      Class<?> loaded = findLoadedClass(name);
      if (loaded == null) {
        byte[] bytes;
        try (InputStream in = getParent().getResourceAsStream(name.replace('.', '/') + ".class")) {
          if (in == null) {
            throw new ClassNotFoundException(name);
          }
          bytes = in.readAllBytes();
        } catch (IOException e) {
          throw new ClassNotFoundException(name, e);
        }
        loaded = defineClass(name, bytes, 0, bytes.length);
      }
      if (resolve) {
        resolveClass(loaded);
      }
      return loaded;
    }
  }

  /** The resource names opened so far, in order. */
  List<String> requested() {
    synchronized (requested) {
      return new ArrayList<String>(requested);
    }
  }
}
