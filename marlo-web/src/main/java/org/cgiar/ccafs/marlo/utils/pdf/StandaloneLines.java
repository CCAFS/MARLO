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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Standalone-line handling of Handlebars.js 4.7.8 (<code>WhitespaceControl</code>), applied to the template source
 * before Handlebars.java compiles it. A line that holds only a block tag (open, close, else), a comment or a partial,
 * plus whitespace, is removed with its line break, and <code>~</code> strips all adjacent whitespace. Handlebars.java's
 * own <code>prettyPrint</code> differs from Handlebars.js on edge cases (empty blocks, a close tag at the end of the
 * template, chained <code>else if</code>), so the algorithm is ported instead: same predicates, same order, same
 * <code>leftStripped</code> / <code>rightStripped</code> guards. Only content between tags changes; tags stay as they
 * are. A template with a raw block (<code>{{{{</code>), an unbalanced block or an inverse block with an else is
 * returned untouched, and the caller is told why.
 */
final class StandaloneLines {

  /**
   * The JavaScript <code>\s</code> class. Java's own <code>\s</code> lacks the no-break space, the byte order mark and
   * the Unicode spaces that JavaScript counts. The patterns end with <code>\z</code> where JavaScript has
   * <code>$</code>: without the <code>m</code> flag that is the end of the text only, while Java's <code>$</code> also
   * matches before a final line break.
   */
  private static final String WS =
    "[\\t\\n\\u000B\\f\\r \\u00A0\\u1680\\u2000-\\u200A\\u2028\\u2029\\u202F\\u205F\\u3000\\uFEFF]";

  private static final Pattern PREV_LINE = Pattern.compile("\\r?\\n" + WS + "*?\\z");

  private static final Pattern PREV_LINE_ROOT = Pattern.compile("(\\A|\\r?\\n)" + WS + "*?\\z");

  private static final Pattern NEXT_LINE = Pattern.compile("\\A" + WS + "*?\\r?\\n");

  private static final Pattern NEXT_LINE_ROOT = Pattern.compile("\\A" + WS + "*?(\\r?\\n|\\z)");

  private static final Pattern LEADING = Pattern.compile("\\A" + WS + "+");

  private static final Pattern TRAILING = Pattern.compile(WS + "+\\z");

  private static final Pattern LEADING_LINE = Pattern.compile("\\A[ \\t]*\\r?\\n?");

  private static final Pattern TRAILING_BLANKS = Pattern.compile("[ \\t]+\\z");

  /** A node of a program: content, a tag, a comment or partial, or a block. */
  private static class Node {

    boolean stripOpen;

    boolean stripClose;
  }

  private static final class Content extends Node {

    final String original;

    String value;

    boolean leftStripped;

    boolean rightStripped;

    /** Characters removed from the start and from the end of the original text. */
    int trimLeft;

    int trimRight;

    Content(String text) {
      this.original = text;
      this.value = text;
    }
  }

  /** A mustache, kept as it is. */
  private static final class Tag extends Node {

    final String text;

    Tag(String text) {
      this.text = text;
    }
  }

  /** A comment or a partial: it can be a standalone line. */
  private static final class Inline extends Node {

    final String text;

    Inline(String text) {
      this.text = text;
    }
  }

  private static final class Program {

    final List<Node> body = new ArrayList<Node>();

    boolean chained;
  }

  private static final class Block extends Node {

    String openText;

    Program program = new Program();

    Program inverse;

    String inverseText;

    boolean inverseStripOpen;

    boolean inverseStripClose;

    String closeText;

    boolean closeStripOpen;

    boolean closeStripClose;
  }

  /** Thrown when the template is outside what this port reads; the caller keeps the source. */
  private static final class Unsupported extends RuntimeException {

    private static final long serialVersionUID = 1L;

    Unsupported(String reason) {
      super(reason, null, false, false);
    }
  }

  /** Why a template is left as it is: the reasons {@link #strip(String, List)} reports. */
  static final String RAW_BLOCK = "raw block ({{{{)";

  static final String UNTERMINATED = "unterminated tag";

  static final String UNBALANCED = "unbalanced block tags";

  static final String INVERSE_ELSE = "inverse block with else, which Handlebars.java cannot compile";

  static final String MERGE = "stripped text would merge into a tag that has no ~";

  /** Tag kinds. */
  private static final int OPEN = 1;

  private static final int CLOSE = 2;

  private static final int ELSE = 3;

  private static final int ELSE_CHAIN = 4;

  private static final int COMMENT = 5;

  private static final int PARTIAL = 6;

  private static final int PLAIN = 7;

  private static final int PARTIAL_BLOCK = 8;

  private static final class Scanner {

    final String src;

    int pos;

    int kind;

    String tagText;

    boolean tildeOpen;

    boolean tildeClose;

    Scanner(String src) {
      this.src = src;
    }

    /** The next piece: content (kind 0) or a tag. Returns false at the end. */
    boolean next() {
      if (pos >= src.length()) {
        return false;
      }
      int tag = findTag(pos);
      if (tag < 0) {
        tagText = src.substring(pos);
        kind = 0;
        pos = src.length();
        return true;
      }
      if (tag > pos) {
        tagText = src.substring(pos, tag);
        kind = 0;
        pos = tag;
        return true;
      }
      readTag();
      return true;
    }

    /** Start of the next real mustache at or after <code>from</code>: a <code>{{</code> not escaped by a backslash. */
    private int findTag(int from) {
      int at = from;
      while (true) {
        at = src.indexOf("{{", at);
        if (at < 0) {
          return -1;
        }
        int slashes = 0;
        while (at - slashes - 1 >= from && src.charAt(at - slashes - 1) == '\\') {
          slashes++;
        }
        if (slashes % 2 == 1) {
          at += 2;
          continue;
        }
        return at;
      }
    }

    private void readTag() {
      int start = pos;
      if (src.startsWith("{{{{", start)) {
        throw new Unsupported(RAW_BLOCK);
      }
      int body = start + 2;
      tildeOpen = src.startsWith("~", body);
      int at = tildeOpen ? body + 1 : body;
      int end;
      if (src.startsWith("!--", at)) {
        int close = src.indexOf("--", at + 3);
        while (close >= 0 && !(src.startsWith("}}", close + 2) || src.startsWith("~}}", close + 2))) {
          close = src.indexOf("--", close + 1);
        }
        if (close < 0) {
          throw new Unsupported(UNTERMINATED);
        }
        tildeClose = src.startsWith("~", close + 2);
        end = close + 2 + (tildeClose ? 3 : 2);
        kind = COMMENT;
      } else if (src.startsWith("!", at)) {
        int close = src.indexOf("}}", at);
        if (close < 0) {
          throw new Unsupported(UNTERMINATED);
        }
        end = close + 2;
        tildeClose = src.charAt(close - 1) == '~';
        kind = COMMENT;
      } else if (src.startsWith("{", at)) {
        int close = src.indexOf("}}}", at);
        if (close < 0) {
          throw new Unsupported(UNTERMINATED);
        }
        end = close + 3;
        tildeClose = src.charAt(close - 1) == '~';
        kind = PLAIN;
      } else {
        end = tagEnd(at);
        tildeClose = src.charAt(end - 3) == '~';
        kind = classify(at, end - (tildeClose ? 3 : 2));
      }
      tagText = src.substring(start, end);
      pos = end;
    }

    /** End (exclusive) of a mustache whose head starts at <code>from</code>, skipping string literals and brackets. */
    private int tagEnd(int from) {
      int at = from;
      while (at < src.length()) {
        char c = src.charAt(at);
        if (c == '"' || c == '\'') {
          at++;
          while (at < src.length() && src.charAt(at) != c) {
            at += src.charAt(at) == '\\' ? 2 : 1;
          }
        } else if (c == '[') {
          at = src.indexOf(']', at);
          if (at < 0) {
            throw new Unsupported(UNTERMINATED);
          }
        } else if (src.startsWith("}}", at)) {
          return at + 2;
        }
        at++;
      }
      throw new Unsupported(UNTERMINATED);
    }

    private int classify(int from, int to) {
      String head = src.substring(from, Math.max(from, to));
      char first = head.isEmpty() ? ' ' : head.charAt(0);
      if (first == '#' || first == '*') {
        return head.startsWith("#>") ? PARTIAL_BLOCK : OPEN;
      } else if (first == '^' && !head.substring(1).trim().isEmpty()) {
        return OPEN;
      } else if (first == '/') {
        return CLOSE;
      } else if (first == '>') {
        return PARTIAL;
      } else if (first == '^') {
        return ELSE;
      }
      String trimmed = head.trim();
      if (trimmed.equals("else")) {
        return ELSE;
      }
      if (trimmed.startsWith("else") && trimmed.length() > 4 && Character.isWhitespace(trimmed.charAt(4))) {
        return ELSE_CHAIN;
      }
      return PLAIN;
    }
  }

  /**
   * The template with its standalone lines stripped as Handlebars.js does. When the port cannot (a raw block, tags
   * that do not balance, a merge that has no safe form) the source is returned as it is, which leaves every standalone
   * line in the HTML, and the reason is added to <code>fallbacks</code> (never template text).
   */
  static String strip(String source, List<String> fallbacks) {
    if (source == null || source.indexOf("{{") < 0) {
      return source;
    }
    try {
      Scanner scanner = new Scanner(source);
      Program root = new Program();
      parse(scanner, root);
      if (scanner.kind == CLOSE || scanner.kind == ELSE || scanner.kind == ELSE_CHAIN) {
        fallbacks.add(UNBALANCED);
        return source;
      }
      program(root, true);
      List<Object> pieces = new ArrayList<Object>();
      emit(root, pieces);
      return join(pieces);
    } catch (Unsupported e) {
      fallbacks.add(e.getMessage());
      return source;
    }
  }

  /**
   * Reads statements into <code>into</code> until the end or a tag that ends the program (close, else); that tag is
   * left in <code>scanner</code> (kind, tagText) for the caller.
   */
  private static void parse(Scanner scanner, Program into) {
    while (scanner.next()) {
      if (scanner.kind == 0) {
        into.body.add(new Content(scanner.tagText));
      } else if (scanner.kind == CLOSE || scanner.kind == ELSE || scanner.kind == ELSE_CHAIN) {
        return;
      } else if (scanner.kind == OPEN || scanner.kind == PARTIAL_BLOCK) {
        into.body.add(block(scanner));
      } else if (scanner.kind == COMMENT || scanner.kind == PARTIAL) {
        Inline inline = new Inline(scanner.tagText);
        inline.stripOpen = scanner.tildeOpen;
        inline.stripClose = scanner.tildeClose;
        into.body.add(inline);
      } else {
        Tag tag = new Tag(scanner.tagText);
        tag.stripOpen = scanner.tildeOpen;
        tag.stripClose = scanner.tildeClose;
        into.body.add(tag);
      }
    }
    scanner.kind = -1;
  }

  /** Reads a block whose opening tag is the scanner's current tag, through its close tag. */
  private static Block block(Scanner scanner) {
    Block block = new Block();
    block.openText = scanner.tagText;
    block.stripOpen = scanner.tildeOpen;
    block.stripClose = scanner.tildeClose;
    parse(scanner, block.program);
    if (scanner.kind == ELSE || scanner.kind == ELSE_CHAIN) {
      block.inverseText = scanner.tagText;
      block.inverseStripOpen = scanner.tildeOpen;
      block.inverseStripClose = scanner.tildeClose;
      block.inverse = new Program();
      if (scanner.kind == ELSE_CHAIN) {
        if (block.openText.startsWith("{{^") || block.openText.startsWith("{{~^")) {
          throw new Unsupported(INVERSE_ELSE);
        }
        block.inverse.chained = true;
        Block inner = chainedBlock(scanner);
        block.inverse.body.add(inner);
        block.closeText = inner.closeText;
        block.closeStripOpen = inner.closeStripOpen;
        block.closeStripClose = inner.closeStripClose;
        return block;
      }
      parse(scanner, block.inverse);
    }
    if (scanner.kind != CLOSE) {
      throw new Unsupported(UNBALANCED);
    }
    if (block.inverse != null && (block.openText.startsWith("{{^") || block.openText.startsWith("{{~^"))) {
      // Handlebars.js swaps the two programs of such a block; Handlebars.java cannot compile it at all
      throw new Unsupported(INVERSE_ELSE);
    }
    block.closeText = scanner.tagText;
    block.closeStripOpen = scanner.tildeOpen;
    block.closeStripClose = scanner.tildeClose;
    return block;
  }

  /** <code>{{else if y}}</code>: a block of its own that shares the close tag of the block that holds it. */
  private static Block chainedBlock(Scanner scanner) {
    Block inner = new Block();
    inner.openText = scanner.tagText;
    inner.stripOpen = scanner.tildeOpen;
    inner.stripClose = scanner.tildeClose;
    parse(scanner, inner.program);
    if (scanner.kind == ELSE || scanner.kind == ELSE_CHAIN) {
      inner.inverseText = scanner.tagText;
      inner.inverseStripOpen = scanner.tildeOpen;
      inner.inverseStripClose = scanner.tildeClose;
      inner.inverse = new Program();
      if (scanner.kind == ELSE_CHAIN) {
        inner.inverse.chained = true;
        Block deeper = chainedBlock(scanner);
        inner.inverse.body.add(deeper);
        inner.closeText = deeper.closeText;
        inner.closeStripOpen = deeper.closeStripOpen;
        inner.closeStripClose = deeper.closeStripClose;
        return inner;
      }
      parse(scanner, inner.inverse);
    }
    if (scanner.kind != CLOSE) {
      throw new Unsupported(UNBALANCED);
    }
    inner.closeText = scanner.tagText;
    inner.closeStripOpen = scanner.tildeOpen;
    inner.closeStripClose = scanner.tildeClose;
    return inner;
  }

  // -------------------------------------------------------------------------------------------------------------
  // WhitespaceControl
  // -------------------------------------------------------------------------------------------------------------

  /** The flags a statement hands to its program: <code>openStandalone</code>, <code>closeStandalone</code>. */
  private static final class Flags {

    boolean open;

    boolean close;

    boolean openStandalone;

    boolean closeStandalone;

    boolean inlineStandalone;
  }

  private static void program(Program program, boolean isRoot) {
    List<Node> body = program.body;
    for (int i = 0; i < body.size(); i++) {
      Node current = body.get(i);
      Flags strip = accept(current);
      if (strip == null) {
        continue;
      }
      boolean prevWs = isPrevWhitespace(body, i, isRoot);
      boolean nextWs = isNextWhitespace(body, i, isRoot);
      boolean openStandalone = strip.openStandalone && prevWs;
      boolean closeStandalone = strip.closeStandalone && nextWs;
      boolean inlineStandalone = strip.inlineStandalone && prevWs && nextWs;
      if (strip.close) {
        omitRight(body, i, true);
      }
      if (strip.open) {
        omitLeft(body, i, true);
      }
      if (inlineStandalone) {
        omitRight(body, i, false);
        omitLeft(body, i, false);
      }
      if (openStandalone) {
        Block block = (Block) current;
        omitRight(block.program != null ? block.program.body : block.inverse.body, -1, false);
        omitLeft(body, i, false);
      }
      if (closeStandalone) {
        Block block = (Block) current;
        omitRight(body, i, false);
        omitLeft(block.inverse != null ? block.inverse.body : block.program.body, Integer.MIN_VALUE, false);
      }
    }
  }

  private static Flags accept(Node node) {
    if (node instanceof Block) {
      return blockStatement((Block) node);
    }
    if (node instanceof Inline) {
      Flags flags = new Flags();
      flags.inlineStandalone = true;
      flags.open = node.stripOpen;
      flags.close = node.stripClose;
      return flags;
    }
    if (node instanceof Tag) {
      Flags flags = new Flags();
      flags.open = node.stripOpen;
      flags.close = node.stripClose;
      return flags;
    }
    return null;
  }

  private static Flags blockStatement(Block block) {
    program(block.program, false);
    if (block.inverse != null) {
      program(block.inverse, false);
    }
    Program program = block.program;
    Program inverse = block.inverse;
    Program firstInverse = inverse;
    Program lastInverse = inverse;
    if (inverse != null && inverse.chained) {
      firstInverse = ((Block) inverse.body.get(0)).program;
      Block last = (Block) lastInverse.body.get(lastInverse.body.size() - 1);
      lastInverse = last.program;
    }
    Flags strip = new Flags();
    strip.open = block.stripOpen;
    strip.close = block.closeStripClose;
    strip.openStandalone = isNextWhitespace(program.body, -1, false);
    strip.closeStandalone = isPrevWhitespace((firstInverse != null ? firstInverse : program).body,
      (firstInverse != null ? firstInverse : program).body.size(), false);
    if (block.stripClose) {
      omitRight(program.body, -1, true);
    }
    if (inverse != null) {
      if (block.inverseStripOpen) {
        omitLeft(program.body, Integer.MIN_VALUE, true);
      }
      if (block.inverseStripClose) {
        omitRight(firstInverse.body, -1, true);
      }
      if (block.closeStripOpen) {
        omitLeft(lastInverse.body, Integer.MIN_VALUE, true);
      }
      if (isPrevWhitespace(program.body, program.body.size(), false)
        && isNextWhitespace(firstInverse.body, -1, false)) {
        omitLeft(program.body, Integer.MIN_VALUE, false);
        omitRight(firstInverse.body, -1, false);
      }
    } else if (block.closeStripOpen) {
      omitLeft(program.body, Integer.MIN_VALUE, true);
    }
    return strip;
  }

  private static boolean isPrevWhitespace(List<Node> body, int index, boolean isRoot) {
    Node prev = index - 1 >= 0 ? body.get(index - 1) : null;
    Node sibling = index - 2 >= 0 ? body.get(index - 2) : null;
    if (prev == null) {
      return isRoot;
    }
    if (prev instanceof Content) {
      Pattern pattern = sibling != null || !isRoot ? PREV_LINE : PREV_LINE_ROOT;
      return pattern.matcher(((Content) prev).original).find();
    }
    return false;
  }

  private static boolean isNextWhitespace(List<Node> body, int index, boolean isRoot) {
    Node next = index + 1 < body.size() ? body.get(index + 1) : null;
    Node sibling = index + 2 < body.size() ? body.get(index + 2) : null;
    if (next == null) {
      return isRoot;
    }
    if (next instanceof Content) {
      Pattern pattern = sibling != null || !isRoot ? NEXT_LINE : NEXT_LINE_ROOT;
      return pattern.matcher(((Content) next).original).find();
    }
    return false;
  }

  /** <code>index</code> -1 is the first node (JavaScript <code>i == null</code>), otherwise the node after it. */
  private static void omitRight(List<Node> body, int index, boolean multiple) {
    int at = index == -1 ? 0 : index + 1;
    Node node = at < body.size() ? body.get(at) : null;
    if (!(node instanceof Content)) {
      return;
    }
    Content current = (Content) node;
    if (!multiple && current.rightStripped) {
      return;
    }
    String before = current.value;
    current.value = (multiple ? LEADING : LEADING_LINE).matcher(current.value).replaceFirst("");
    current.trimLeft += before.length() - current.value.length();
    current.rightStripped = !current.value.equals(before);
  }

  /** <code>Integer.MIN_VALUE</code> is the last node (JavaScript <code>i == null</code>), otherwise the node before. */
  private static boolean omitLeft(List<Node> body, int index, boolean multiple) {
    int at = index == Integer.MIN_VALUE ? body.size() - 1 : index - 1;
    Node node = at >= 0 && at < body.size() ? body.get(at) : null;
    if (!(node instanceof Content)) {
      return false;
    }
    Content current = (Content) node;
    if (!multiple && current.leftStripped) {
      return false;
    }
    String before = current.value;
    current.value = (multiple ? TRAILING : TRAILING_BLANKS).matcher(current.value).replaceFirst("");
    current.trimRight += before.length() - current.value.length();
    current.leftStripped = !current.value.equals(before);
    return current.leftStripped;
  }

  /** The tags and contents in template order; a tag is a String, a content is its {@link Content}. */
  private static void emit(Program program, List<Object> out) {
    for (Node node : program.body) {
      if (node instanceof Content) {
        out.add(node);
      } else if (node instanceof Tag) {
        out.add(((Tag) node).text);
      } else if (node instanceof Inline) {
        out.add(((Inline) node).text);
      } else {
        Block block = (Block) node;
        out.add(block.openText);
        if (block.inverse != null && block.inverse.chained) {
          emit(block.program, out);
          // the chained block holds the close tag the whole chain shares
          emit(block.inverse, out);
        } else {
          emit(block.program, out);
          if (block.inverse != null) {
            out.add(block.inverseText);
            emit(block.inverse, out);
          }
          out.add(block.closeText);
        }
      }
    }
  }

  /**
   * Writes the pieces, keeping the lexing of every boundary what it was in the original. Handlebars.js strips the AST;
   * here stripped text becomes source again, and two characters that were apart can meet. Content that ends with
   * <code>{</code> or <code>\</code> right before a tag would make <code>{{{</code> or an escape, and content that
   * starts with <code>}</code> right after a tag would extend its closing braces. The first can only happen after a
   * <code>~</code> strip, so the trailing whitespace is kept and the tag's own <code>~</code> removes it when
   * Handlebars.java compiles; the second gets an empty comment between the tag and the text.
   */
  private static String join(List<Object> pieces) {
    StringBuilder out = new StringBuilder();
    for (int i = 0; i < pieces.size(); i++) {
      Object piece = pieces.get(i);
      if (piece instanceof String) {
        out.append((String) piece);
        continue;
      }
      Content content = (Content) piece;
      String text = content.value;
      if (i > 0 && content.trimLeft > 0 && text.startsWith("}")) {
        out.append("{{!}}");
      }
      Object next = i + 1 < pieces.size() ? pieces.get(i + 1) : null;
      if (content.trimRight > 0 && (text.endsWith("{") || text.endsWith("\\"))) {
        if (!(next instanceof String) || !((String) next).startsWith("{{~")) {
          throw new Unsupported(MERGE);
        }
        text = text + content.original.substring(content.original.length() - content.trimRight);
      }
      out.append(text);
    }
    return out.toString();
  }

  private StandaloneLines() {
  }
}
