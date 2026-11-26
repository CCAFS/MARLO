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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;

/** A servlet response double for the tests of the local PDF engine: records what the service wrote to it. */
final class PdfResponses {

  /** What the servlet response double saw. */
  static final class Recorded {

    final ByteArrayOutputStream body = new ByteArrayOutputStream();
    boolean committed;
    String contentType;
    String disposition;
    Integer errorStatus;
    String errorMessage;
    IOException failOnWrite;
    int contentLength = -1;
  }

  static final class ClientAbortException extends IOException {

    private static final long serialVersionUID = 1L;

    ClientAbortException() {
      super("peer closed");
    }
  }

  static HttpServletResponse response(final Recorded recorded) {
    final ServletOutputStream stream = new ServletOutputStream() {

      public boolean isReady() {
        return true;
      }

      public void setWriteListener(WriteListener listener) {
      }

      @Override
      public void write(int b) throws IOException {
        if (recorded.failOnWrite != null) {
          throw recorded.failOnWrite;
        }
        recorded.body.write(b);
      }

      @Override
      public void write(byte[] b, int off, int len) throws IOException {
        if (recorded.failOnWrite != null) {
          throw recorded.failOnWrite;
        }
        recorded.body.write(b, off, len);
      }
    };
    return (HttpServletResponse) Proxy.newProxyInstance(PdfResponses.class.getClassLoader(),
      new Class<?>[] {HttpServletResponse.class}, new InvocationHandler() {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
          String name = method.getName();
          if ("setContentType".equals(name)) {
            recorded.contentType = (String) args[0];
          } else if ("setHeader".equals(name) && "Content-Disposition".equals(args[0])) {
            recorded.disposition = (String) args[1];
          } else if ("setContentLength".equals(name)) {
            recorded.contentLength = (Integer) args[0];
          } else if ("sendError".equals(name)) {
            recorded.errorStatus = (Integer) args[0];
            recorded.errorMessage = args.length > 1 ? (String) args[1] : null;
            recorded.committed = true;
          } else if ("isCommitted".equals(name)) {
            return recorded.committed;
          } else if ("getOutputStream".equals(name)) {
            return stream;
          }
          if (method.getReturnType() == boolean.class) {
            return false;
          }
          return null;
        }
      });
  }

  private PdfResponses() {
  }
}
