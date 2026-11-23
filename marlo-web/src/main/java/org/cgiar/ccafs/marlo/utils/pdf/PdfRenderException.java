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

/**
 * A PDF report could not be produced by {@link LocalPdfReportService} (CHG-LPRE-NF-003, CHG-LPRE-NF-005, design
 * sections 7.1 and 7.5). The message is safe to log and never holds template or payload text: callers may write it to
 * a log, but they must show the user their own generic text.
 */
public class PdfRenderException extends Exception {

  private static final long serialVersionUID = 1L;

  public PdfRenderException(String message) {
    super(message);
  }

  public PdfRenderException(String message, Throwable cause) {
    super(message, cause);
  }
}
