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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.Collator;
import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Formatter;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.PathExpression;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.ValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;

/**
 * Handlebars engine configured like the reports microservice (<code>pdf.module.ts</code>): the same 17 helpers with
 * JavaScript semantics (loose equality, truthiness, number conversion and printing), the built-in <code>if</code> and
 * <code>unless</code> block helpers with Handlebars.js 4.7.8 semantics, a map-only value resolver and JavaScript number
 * printing for rendered values.
 * <p>
 * Value model used by the helpers: JavaScript <code>undefined</code> is the Java <code>null</code> (a missing key, an
 * omitted argument); JavaScript <code>null</code> is {@link #JS_NULL}, which the value resolver returns for a map key
 * that is present with a null value. Handlebars.java parses a literal <code>null</code> written inside a template as a
 * lookup of the name <code>null</code>, so the value resolver answers that name with {@link #JS_NULL} when the map has
 * no key of that name, and the helper receives JavaScript <code>null</code> as in Handlebars.js. A name missing from
 * the current item is <code>undefined</code>: the engine does not walk the parent contexts (see
 * {@link ScopedContext}), the data variables <code>@index</code>, <code>@key</code>, <code>@first</code> and
 * <code>@last</code> stay visible in nested scopes, and <code>null</code> elements of lists and maps are
 * {@link #JS_NULL}; <code>@odd</code> and <code>@even</code> read as undefined, as in Handlebars.js. Parity is
 * guaranteed for the supported template subset (design.md section 7.1). The constructs outside it behave differently
 * or are unverified (a data key or path segment named <code>null</code>, <code>if</code> / <code>unless</code> without
 * an argument, block parameters, partials, <code>../</code> paths) and are listed by {@link ReportTemplateSubset}.
 */
final class HandlebarsReportHelpers {

  /**
   * JavaScript <code>null</code>. It is an empty list so that Handlebars' own truthiness test (<code>if</code>,
   * <code>unless</code>, <code>each</code>, <code>with</code>) treats it as empty; the formatter prints it as nothing.
   */
  static final class JsNull extends AbstractList<Object> {

    @Override
    public Object get(int index) {
      throw new IndexOutOfBoundsException();
    }

    @Override
    public int size() {
      return 0;
    }

    @Override
    public String toString() {
      return "";
    }
  }

  /** The Handlebars <code>options</code> object as JavaScript sees it (prints as <code>[object Object]</code>). */
  static final class OptionsObject {

    @Override
    public String toString() {
      return "[object Object]";
    }
  }

  /**
   * Value resolver: maps only (no JavaBean or method access), a null map value resolves to {@link #JS_NULL}. A name
   * the map does not have resolves to <code>null</code> (JavaScript <code>undefined</code>), which is what
   * Handlebars.java's own composite resolver makes of {@link ValueResolver#UNRESOLVED} when it wraps a resolver.
   */
  private static final class ReportValueResolver implements ValueResolver {

    @Override
    public Set<Map.Entry<String, Object>> propertySet(Object context) {
      return MapValueResolver.INSTANCE.propertySet(context);
    }

    @Override
    public Object resolve(Object context) {
      Object value = MapValueResolver.INSTANCE.resolve(context);
      return value == UNRESOLVED ? null : value;
    }

    @Override
    public Object resolve(Object context, String name) {
      if (context instanceof Map) {
        Map<?, ?> map = (Map<?, ?>) context;
        Object value = map.get(name);
        // a key present with a null value is JavaScript null; a template literal null is parsed as the name "null"
        if (value == null && (map.containsKey(name) || NULL_LITERAL.equals(name))) {
          return JS_NULL;
        }
        return value;
      }
      return null;
    }
  }

  /**
   * Handlebars.js (without compat mode) looks a name up in the current context only; parents are reached through
   * <code>../</code> and <code>@root</code>. Handlebars.java walks every ancestor for a name missing from the current
   * item, so inside <code>{{#each}}</code> or <code>{{#with}}</code> an absent field would read the same name of an
   * enclosing context. This context confines the lookup of a plain name to itself: the item, its extension map and
   * the data variables. A data variable (<code>@index</code> and the like) is the exception: it is inherited from the
   * enclosing block, so its lookup goes up the parent chain. Every child context is of this class
   * (<code>newChildContext</code>), so <code>../name</code> (which calls <code>get</code> on the parent) behaves as
   * before. Known limit: inside a block that declares block
   * parameters (<code>{{#each x as |i|}}</code>) Handlebars.java builds the item context from its own package-private
   * class, so a plain name missing from the item still reads the enclosing scope there; the parameters themselves
   * resolve correctly. MARLO's templates declare none.
   */
  private static final class ScopedContext extends Context {

    /** Stops the lookup of a path that partly resolved, as Handlebars.java does with its own marker. */
    private static final Object PARTIAL = new Object();

    static ScopedContext root(Object model) {
      ScopedContext root = new ScopedContext(model);
      root.extendedContext = new ScopedContext(new HashMap<String, Object>());
      root.data = new HashMap<String, Object>();
      root.data.put(Context.PARTIALS, new HashMap<String, Object>());
      LinkedList<Object> inlinePartials = new LinkedList<Object>();
      inlinePartials.push(new HashMap<String, Object>());
      root.data.put(Context.INLINE_PARTIALS, inlinePartials);
      root.data.put(Context.INVOCATION_STACK, new LinkedList<Object>());
      root.data.put("root", model);
      root.resolver = RESOLVER;
      return root;
    }

    ScopedContext(Object model) {
      super(model);
    }

    @Override
    public Object get(List<PathExpression> path) {
      PathExpression head = path.get(0);
      if (isAbsentDataVariable(head)) {
        return null;
      }
      if (head.local() || isDataVariable(head)) {
        return dataVariable(head, super.get(path));
      }
      PathChain chain = new PathChain(path);
      Object value = chain.eval(resolver, this, model());
      if (value == null && extendedContext != null) {
        value = chain.eval(resolver, extendedContext, extendedContext.model());
      }
      if (value == null) {
        value = chain.eval(resolver, this, data);
      }
      return dataVariable(head, value == PARTIAL ? null : value);
    }

    @Override
    protected Context newChildContext(Object model) {
      return new ScopedContext(model);
    }

    /**
     * The context a block helper renders its body in when the item it sits in is <code>null</code>. Handlebars.js calls
     * the helper with an empty object as <code>this</code> and adds no level: <code>../</code> still reaches the
     * parent of the item. A plain child of the item would add a level, so this context takes the item's own parent,
     * extension map (where <code>each</code> keeps <code>@index</code> and the like) and data. Returns
     * <code>null</code> when the item is not one of this engine's contexts (block parameter blocks).
     */
    static Context nullItemBody(Context item) {
      if (!(item instanceof ScopedContext) || item.parent() == null) {
        return null;
      }
      ScopedContext body = new ScopedContext(new HashMap<String, Object>());
      body.parent = item.parent();
      body.extendedContext = ((ScopedContext) item).extendedContext;
      body.data = ((ScopedContext) item).data;
      body.resolver = ((ScopedContext) item).resolver;
      return body;
    }

    /**
     * Handlebars.java invents <code>@odd</code> and <code>@even</code>; Handlebars.js has neither, so they read as
     * undefined.
     */
    private static boolean isAbsentDataVariable(PathExpression head) {
      String name = head.toString();
      return "@odd".equals(name) || "@even".equals(name);
    }

    /**
     * A data variable (<code>@index</code>, <code>@key</code>, <code>@first</code>, <code>@last</code>) belongs to the
     * enclosing block, not to the item, so a child context (<code>with</code>, <code>if</code> or a helper block
     * rendering a new item) does not hold it: the lookup goes up the parent chain, as in Handlebars.js, where the
     * data frame is inherited. <code>@root</code> is held by every context and is resolved here.
     */
    private static boolean isDataVariable(PathExpression head) {
      String name = head.toString();
      return name.startsWith("@") && !"@root".equals(name);
    }

    /** Handlebars.java stores <code>@first</code> and <code>@last</code> as text; JavaScript has booleans. */
    private static Object dataVariable(PathExpression head, Object value) {
      if (value instanceof String) {
        String name = head.toString();
        if ("@first".equals(name) || "@last".equals(name)) {
          return Boolean.valueOf(!((String) value).isEmpty());
        }
      }
      return value;
    }

    /** The chain of a path lookup, as <code>Context</code> keeps it private. */
    private static final class PathChain implements PathExpression.Chain {

      private final List<PathExpression> path;

      private int index;

      PathChain(List<PathExpression> path) {
        this.path = path;
      }

      Object eval(ValueResolver resolver, Context context, Object value) {
        index = 0;
        Object result = next(resolver, context, value);
        if (result == null) {
          return index > 1 ? PARTIAL : null;
        }
        return result;
      }

      @Override
      public Object next(ValueResolver resolver, Context context, Object value) {
        if (value != null && index < path.size()) {
          return path.get(index++).eval(resolver, context, value, this);
        }
        return value;
      }

      @Override
      public List<PathExpression> path() {
        return path.subList(index, path.size());
      }
    }
  }

  private static final String NULL_LITERAL = "null";

  static final JsNull JS_NULL = new JsNull();

  static final OptionsObject OPTIONS = new OptionsObject();

  /** The 17 helpers registered by <code>pdf.module.ts</code>. */
  static final List<String> HELPER_NAMES = Collections.unmodifiableList(Arrays.asList("safeEmpty",
    "percentage", "mathOperator", "stringContentCompare", "inList", "yesNoHelper", "conditionalAttribiute",
    "safeString", "ifEquals", "ifNotEquals", "ifCond", "or", "typeof", "json", "parseJSON", "ifAnd", "ifOr"));

  private static final ValueResolver RESOLVER = new ReportValueResolver();

  private static final Pattern DECIMAL = Pattern
    .compile("[+-]?(?:Infinity|(?:\\d+\\.?\\d*|\\.\\d+)(?:[eE][+-]?\\d+)?)");

  private static final Pattern RADIX = Pattern.compile("0(?:[xX]([0-9a-fA-F]+)|[oO]([0-7]+)|[bB]([01]+))");

  private static final String JS_WHITESPACE =
    "\\u0009\\u000A\\u000B\\u000C\\u000D\\u0020\\u00A0\\u1680\\u2000-\\u200A\\u2028\\u2029\\u202F\\u205F\\u3000\\uFEFF";

  private static final Pattern LEADING_WHITESPACE = Pattern.compile("^[" + JS_WHITESPACE + "]+");

  private static final Pattern TRAILING_WHITESPACE = Pattern.compile("[" + JS_WHITESPACE + "]+$");

  /** English collation at base sensitivity (<code>localeCompare(..., 'en', {sensitivity: 'base'})</code>). */
  private static final Collator COLLATOR = newCollator();

  private static final ObjectMapper JSON_PARSER =
    new ObjectMapper().enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);

  /** Builds the engine: the 17 helpers plus JavaScript printing of numbers, lists, maps and null. */
  static Handlebars newHandlebars() {
    Handlebars handlebars = new Handlebars();
    handlebars.with(new Formatter() {

      @Override
      public Object format(Object value, Chain next) {
        if (value instanceof Number || value instanceof List || value instanceof Map || value instanceof Boolean
          || value instanceof OptionsObject) {
          return display(value);
        }
        return next.format(value);
      }
    });
    registerHelpers(handlebars);
    return handlebars;
  }

  /**
   * Context over a data map with the map value resolver only, whose name lookup stays in the current scope (see
   * {@link ScopedContext}). The data is copied with its JavaScript <code>null</code> list elements and map values
   * replaced by {@link #JS_NULL}; the argument is not modified. Use it to apply a compiled template.
   */
  static Context newContext(Map<String, Object> data) {
    return ScopedContext.root(withJsNulls(data));
  }

  /**
   * Copy of a parsed JSON value in which every <code>null</code> list element or map value, at any depth, is
   * {@link #JS_NULL}, so that it reaches the helpers as JavaScript <code>null</code> and not as <code>undefined</code>.
   * Other values are returned as they are.
   */
  static Object withJsNulls(Object value) {
    return withJsNulls(value, new IdentityHashMap<Object, Object>());
  }

  private static Object withJsNulls(Object value, Map<Object, Object> copies) {
    if (value == null) {
      return JS_NULL;
    }
    if (value instanceof JsNull || !(value instanceof List || value instanceof Map)) {
      return value;
    }
    Object known = copies.get(value);
    if (known != null) {
      return known;
    }
    if (value instanceof List) {
      List<Object> list = new ArrayList<Object>(((List<?>) value).size());
      copies.put(value, list);
      for (Object element : (List<?>) value) {
        list.add(withJsNulls(element, copies));
      }
      return list;
    }
    Map<Object, Object> map = new LinkedHashMap<Object, Object>();
    copies.put(value, map);
    for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
      map.put(entry.getKey(), withJsNulls(entry.getValue(), copies));
    }
    return map;
  }

  /**
   * Applies a template to a data map using the map value resolver only.
   */
  static String apply(Template template, Map<String, Object> data) throws IOException {
    return template.apply(newContext(data));
  }

  /**
   * Java's default English rules ignore spaces, tabs, line breaks and hyphens, which ICU (JavaScript) compares as
   * letters; they are made significant again. Space and no-break space stay equal, as in ICU at base sensitivity.
   */
  private static Collator newCollator() {
    Collator collator = Collator.getInstance(Locale.ENGLISH);
    try {
      collator = new RuleBasedCollator(((RuleBasedCollator) collator).getRules()
        + "& '_' < '\u0020' ; '\u00a0' < '\u0009' < '\n' < '\r' < '-'");
    } catch (ParseException e) {
      throw new IllegalStateException("The collation rules of the report helpers do not parse", e);
    }
    collator.setStrength(Collator.PRIMARY);
    collator.setDecomposition(Collator.CANONICAL_DECOMPOSITION);
    return collator;
  }

  private static Object at(Object[] args, int index) {
    return index < args.length ? args[index] : null;
  }

  /** Arguments as JavaScript sees them: the positional values followed by the options object. */
  private static Object[] jsArgs(Object context, Options options) {
    Object[] args = new Object[options.params.length + 2];
    args[0] = context;
    System.arraycopy(options.params, 0, args, 1, options.params.length);
    args[args.length - 1] = OPTIONS;
    return args;
  }

  /** A block helper declared with <code>declared</code> JavaScript parameters, the last one being the options. */
  private static void requireBlock(Object[] args, int declared) {
    if (args.length != declared) {
      throw new IllegalArgumentException("options.fn is not a function");
    }
  }

  /**
   * Body of a block helper written <code>options.fn(this)</code> in JavaScript. <code>this</code> is the item the block
   * sits in, not the first argument of the helper (which Handlebars.java passes as <code>context</code>, and which may
   * be missing). Handlebars.js calls a helper with an empty object as <code>this</code> when the current context is
   * <code>null</code> or <code>undefined</code> (a <code>null</code> element of a list), so that is the context the
   * body sees.
   */
  private static String body(Options options) throws IOException {
    if (isNullish(options.context.model())) {
      Context scope = ScopedContext.nullItemBody(options.context);
      return scope == null ? options.fn(new HashMap<String, Object>()).toString() : options.fn(scope).toString();
    }
    return options.fn().toString();
  }

  /** The <code>options.inverse(this)</code> counterpart of {@link #body}. */
  private static String otherwise(Options options) throws IOException {
    if (isNullish(options.context.model())) {
      Context scope = ScopedContext.nullItemBody(options.context);
      return scope == null ? options.inverse(new HashMap<String, Object>()).toString()
        : options.inverse(scope).toString();
    }
    return options.inverse().toString();
  }

  private static Object branch(boolean condition, Options options) throws IOException {
    return condition ? body(options) : otherwise(options);
  }

  /**
   * Handlebars.js 4.7.8 <code>if</code>: the branch runs unless the value is JavaScript-falsy or an empty array;
   * <code>includeZero</code> keeps a (non-NaN) zero. A block written without any argument cannot be told from one
   * written over <code>this</code> (Handlebars.java hands the current context to the helper), so only a surplus
   * argument is rejected, as <code>#if requires exactly one argument</code>.
   */
  private static boolean ifCondition(Object value, Options options) {
    boolean empty = !truthy(value) && !isZero(value) || value instanceof List && ((List<?>) value).isEmpty();
    if (truthy((Object) options.hash("includeZero"))) {
      return !empty;
    }
    return truthy(value) && !empty;
  }

  private static boolean isZero(Object value) {
    return value instanceof Number && toDouble((Number) value) == 0;
  }

  private static void registerHelpers(Handlebars handlebars) {
    registerValueHelpers(handlebars);
    registerConditionHelpers(handlebars);
    registerDataHelpers(handlebars);
  }

  private static void registerValueHelpers(Handlebars handlebars) {
    handlebars.registerHelper("safeEmpty", new Helper<Object>() {

      @Override
      public Object apply(Object context, Options options) throws IOException {
        String value = body(options);
        if (value.isEmpty()) {
          return new Handlebars.SafeString("Not Provided");
        }
        return value;
      }
    });
    handlebars.registerHelper("percentage", new Helper<Object>() {

      @Override
      public Object apply(Object context, Options options) throws IOException {
        double value = stringToNumber(body(options)) * 100;
        return new Handlebars.SafeString(numberToString(value) + "%");
      }
    });
    handlebars.registerHelper("mathOperator", new Helper<Object>() {

      @Override
      public Object apply(Object context, Options options) {
        Object[] args = jsArgs(context, options);
        double left = parseFloat(jsToString(at(args, 0)));
        double right = parseFloat(jsToString(at(args, 2)));
        String operator = jsToString(at(args, 1));
        if ("+".equals(operator)) {
          return left + right;
        } else if ("-".equals(operator)) {
          return left - right;
        } else if ("*".equals(operator)) {
          return left * right;
        } else if ("/".equals(operator)) {
          return left / right;
        }
        return null;
      }
    });
    handlebars.registerHelper("stringContentCompare", new Helper<Object>() {

      @Override
      public Object apply(Object context, Options options) throws IOException {
        Object[] args = jsArgs(context, options);
        if (args.length != 3) {
          throw new IllegalArgumentException("#stringContentCompare requires exactly one two arguments");
        }
        if (!(args[0] instanceof String && args[1] instanceof String)) {
          throw new IllegalArgumentException("#stringContentCompare requires the two arguments provided being strings");
        }
        return branch(COLLATOR.compare((String) args[0], (String) args[1]) == 0, options);
      }
    });
    handlebars.registerHelper("inList", new Helper<Object>() {

      @Override
      public Object apply(Object context, Options options) {
        Object[] args = jsArgs(context, options);
        Object list = at(args, 1);
        String[] items;
        if (isNullish(list)) {
          items = new String[] {""};
        } else if (list instanceof String) {
          items = ((String) list).split(",", -1);
        } else {
          throw new IllegalArgumentException("(list ?? \"\").split is not a function");
        }
        Object value = at(args, 0);
        if (isNullish(value)) {
          throw new IllegalArgumentException(
            "Cannot read properties of " + jsToString(value) + " (reading 'toString')");
        }
        return Arrays.asList(items).contains(jsToString(value));
      }
    });
    handlebars.registerHelper("yesNoHelper", new Helper<Object>() {

      @Override
      public Object apply(Object context, Options options) {
        Object[] args = jsArgs(context, options);
        Object value = at(args, 0);
        if (args.length == 2) {
          if (isNullish(value)) {
            return at(args, 1);
          }
          return truthy(value) ? "Yes" : "No";
        }
        return truthy(value) ? "Yes" : "No";
      }
    });
    handlebars.registerHelper("conditionalAttribiute", new Helper<Object>() {

      @Override
      public Object apply(Object context, Options options) {
        Object[] args = jsArgs(context, options);
        Object chosen = looseEquals(at(args, 0), at(args, 1)) ? at(args, 3) : at(args, 4);
        return jsToString(at(args, 2)) + "=\"" + jsToString(chosen) + "\"";
      }
    });
    handlebars.registerHelper("safeString", new Helper<Object>() {

      @Override
      public Object apply(Object context, Options options) {
        return new Handlebars.SafeString(jsToString(at(jsArgs(context, options), 0)));
      }
    });
  }

  private static void registerConditionHelpers(Handlebars handlebars) {
    handlebars.registerHelper("if", new Helper<Object>() {

      @Override
      public Object apply(Object context, Options options) throws IOException {
        Object[] args = jsArgs(context, options);
        if (args.length != 2) {
          throw new IllegalArgumentException("#if requires exactly one argument");
        }
        return branch(ifCondition(args[0], options), options);
      }
    });
    handlebars.registerHelper("unless", new Helper<Object>() {

      @Override
      public Object apply(Object context, Options options) throws IOException {
        Object[] args = jsArgs(context, options);
        if (args.length != 2) {
          throw new IllegalArgumentException("#unless requires exactly one argument");
        }
        return branch(!ifCondition(args[0], options), options);
      }
    });
    handlebars.registerHelper("ifEquals", new Helper<Object>() {

      @Override
      public Object apply(Object context, Options options) throws IOException {
        Object[] args = jsArgs(context, options);
        requireBlock(args, 3);
        return branch(looseEquals(args[0], args[1]), options);
      }
    });
    handlebars.registerHelper("ifNotEquals", new Helper<Object>() {

      @Override
      public Object apply(Object context, Options options) throws IOException {
        Object[] args = jsArgs(context, options);
        requireBlock(args, 3);
        return branch(!looseEquals(args[0], args[1]), options);
      }
    });
    handlebars.registerHelper("ifCond", new Helper<Object>() {

      @Override
      public Object apply(Object context, Options options) throws IOException {
        Object[] args = jsArgs(context, options);
        requireBlock(args, 4);
        return branch(condition(args[0], args[1], args[2]), options);
      }
    });
    handlebars.registerHelper("or", new Helper<Object>() {

      @Override
      public Object apply(Object context, Options options) {
        Object[] args = jsArgs(context, options);
        for (int i = 0; i < args.length - 1; i++) {
          if (truthy(args[i])) {
            return Boolean.TRUE;
          }
        }
        return Boolean.FALSE;
      }
    });
  }

  private static void registerDataHelpers(Handlebars handlebars) {
    handlebars.registerHelper("typeof", new Helper<Object>() {

      @Override
      public Object apply(Object context, Options options) {
        return typeOf(at(jsArgs(context, options), 0));
      }
    });
    handlebars.registerHelper("json", new Helper<Object>() {

      @Override
      public Object apply(Object context, Options options) {
        StringBuilder out = new StringBuilder();
        return stringify(at(jsArgs(context, options), 0), "", out) ? out.toString() : null;
      }
    });
    handlebars.registerHelper("parseJSON", new Helper<Object>() {

      @Override
      public Object apply(Object context, Options options) {
        String text = jsToString(at(jsArgs(context, options), 0));
        try {
          return withJsNulls(JSON_PARSER.readValue(text, Object.class));
        } catch (IOException | RuntimeException e) {
          return new ArrayList<Object>();
        }
      }
    });
    handlebars.registerHelper("ifAnd", new Helper<Object>() {

      @Override
      public Object apply(Object context, Options options) throws IOException {
        Object[] args = jsArgs(context, options);
        boolean all = true;
        for (int i = 0; i < args.length - 1; i++) {
          all = all && truthy(args[i]);
        }
        return branch(all, options);
      }
    });
    handlebars.registerHelper("ifOr", new Helper<Object>() {

      @Override
      public Object apply(Object context, Options options) throws IOException {
        Object[] args = jsArgs(context, options);
        boolean any = false;
        for (int i = 0; i < args.length - 1; i++) {
          any = any || truthy(args[i]);
        }
        return branch(any, options);
      }
    });
  }

  /** <code>ifCond</code> body: the operator is compared with <code>===</code> to the string literals. */
  private static boolean condition(Object left, Object operator, Object right) {
    if (!(operator instanceof String)) {
      return false;
    }
    String op = (String) operator;
    if ("==".equals(op)) {
      return looseEquals(left, right);
    } else if ("===".equals(op)) {
      return strictEquals(left, right);
    } else if ("!=".equals(op)) {
      return !looseEquals(left, right);
    } else if ("!==".equals(op)) {
      return !strictEquals(left, right);
    } else if ("&&".equals(op)) {
      return truthy(left) && truthy(right);
    } else if ("||".equals(op)) {
      return truthy(left) || truthy(right);
    } else if ("<".equals(op) || "<=".equals(op) || ">".equals(op) || ">=".equals(op)) {
      return relational(left, op, right);
    }
    return false;
  }

  // ---------------------------------------------------------------------------------------------------------------
  // JavaScript semantics
  // ---------------------------------------------------------------------------------------------------------------

  /** Value printed by a mustache for the types whose Java text differs from the JavaScript one. */
  static String display(Object value) {
    if (value == null || value instanceof JsNull) {
      return "";
    }
    return jsToString(value);
  }

  static boolean isNullish(Object value) {
    return value == null || value instanceof JsNull;
  }

  private static boolean isObject(Object value) {
    return !isNullish(value) && !(value instanceof Boolean) && !(value instanceof Number)
      && !(value instanceof String);
  }

  static String typeOf(Object value) {
    if (value == null) {
      return "undefined";
    } else if (value instanceof Boolean) {
      return "boolean";
    } else if (value instanceof Number) {
      return "number";
    } else if (value instanceof String) {
      return "string";
    }
    return "object";
  }

  static boolean truthy(Object value) {
    if (isNullish(value)) {
      return false;
    } else if (value instanceof Boolean) {
      return (Boolean) value;
    } else if (value instanceof Number) {
      double number = toDouble((Number) value);
      return number != 0 && !Double.isNaN(number);
    } else if (value instanceof String) {
      return !((String) value).isEmpty();
    }
    return true;
  }

  static double toDouble(Number value) {
    if (value instanceof Float) {
      return Double.parseDouble(value.toString());
    }
    return value.doubleValue();
  }

  /** JavaScript <code>String(value)</code>. */
  static String jsToString(Object value) {
    if (value == null) {
      return "undefined";
    } else if (value instanceof JsNull) {
      return "null";
    } else if (value instanceof Number) {
      return numberToString(toDouble((Number) value));
    } else if (value instanceof List) {
      StringBuilder joined = new StringBuilder();
      boolean first = true;
      for (Object element : (List<?>) value) {
        if (!first) {
          joined.append(',');
        }
        first = false;
        joined.append(isNullish(element) ? "" : jsToString(element));
      }
      return joined.toString();
    } else if (value instanceof Map || value instanceof OptionsObject) {
      return "[object Object]";
    }
    return value.toString();
  }

  /** JavaScript <code>ToPrimitive</code>: objects become their string form. */
  private static Object toPrimitive(Object value) {
    return isObject(value) ? jsToString(value) : value;
  }

  /** JavaScript <code>Number(value)</code>. */
  static double jsToNumber(Object value) {
    if (value == null) {
      return Double.NaN;
    } else if (value instanceof JsNull) {
      return 0;
    } else if (value instanceof Boolean) {
      return (Boolean) value ? 1 : 0;
    } else if (value instanceof Number) {
      return toDouble((Number) value);
    }
    return stringToNumber(jsToString(value));
  }

  /** JavaScript <code>Number(string)</code>. */
  static double stringToNumber(String text) {
    String trimmed = TRAILING_WHITESPACE.matcher(LEADING_WHITESPACE.matcher(text).replaceFirst("")).replaceFirst("");
    if (trimmed.isEmpty()) {
      return 0;
    }
    if (DECIMAL.matcher(trimmed).matches()) {
      return Double.parseDouble(trimmed);
    }
    Matcher radix = RADIX.matcher(trimmed);
    if (radix.matches()) {
      if (radix.group(1) != null) {
        return new BigInteger(radix.group(1), 16).doubleValue();
      } else if (radix.group(2) != null) {
        return new BigInteger(radix.group(2), 8).doubleValue();
      }
      return new BigInteger(radix.group(3), 2).doubleValue();
    }
    return Double.NaN;
  }

  /** JavaScript <code>parseFloat(string)</code>: the longest numeric prefix, else NaN. */
  static double parseFloat(String text) {
    String trimmed = LEADING_WHITESPACE.matcher(text).replaceFirst("");
    Matcher prefix = DECIMAL.matcher(trimmed);
    if (prefix.lookingAt()) {
      return Double.parseDouble(prefix.group());
    }
    return Double.NaN;
  }

  /** JavaScript <code>a == b</code>. */
  static boolean looseEquals(Object left, Object right) {
    boolean leftNullish = isNullish(left);
    boolean rightNullish = isNullish(right);
    if (leftNullish || rightNullish) {
      return leftNullish && rightNullish;
    }
    if (left instanceof Boolean) {
      return looseEquals(Double.valueOf((Boolean) left ? 1 : 0), right);
    }
    if (right instanceof Boolean) {
      return looseEquals(left, Double.valueOf((Boolean) right ? 1 : 0));
    }
    if (left instanceof Number && right instanceof Number) {
      return toDouble((Number) left) == toDouble((Number) right);
    }
    if (left instanceof String && right instanceof String) {
      return left.equals(right);
    }
    if (left instanceof Number && right instanceof String) {
      return toDouble((Number) left) == stringToNumber((String) right);
    }
    if (left instanceof String && right instanceof Number) {
      return stringToNumber((String) left) == toDouble((Number) right);
    }
    if (isObject(left) && isObject(right)) {
      return left == right;
    }
    return looseEquals(toPrimitive(left), toPrimitive(right));
  }

  /** JavaScript <code>a === b</code>. */
  static boolean strictEquals(Object left, Object right) {
    if (left == null || right == null) {
      return left == right;
    }
    if (left instanceof Number && right instanceof Number) {
      return toDouble((Number) left) == toDouble((Number) right);
    }
    if (left instanceof String && right instanceof String || left instanceof Boolean && right instanceof Boolean) {
      return left.equals(right);
    }
    return left == right;
  }

  /** JavaScript <code>&lt;</code>, <code>&lt;=</code>, <code>&gt;</code>, <code>&gt;=</code>. */
  private static boolean relational(Object left, String operator, Object right) {
    Object leftPrimitive = toPrimitive(left);
    Object rightPrimitive = toPrimitive(right);
    if (leftPrimitive instanceof String && rightPrimitive instanceof String) {
      int order = ((String) leftPrimitive).compareTo((String) rightPrimitive);
      return compareOrder(order, operator);
    }
    double x = jsToNumber(leftPrimitive);
    double y = jsToNumber(rightPrimitive);
    if ("<".equals(operator)) {
      return x < y;
    } else if ("<=".equals(operator)) {
      return x <= y;
    } else if (">".equals(operator)) {
      return x > y;
    }
    return x >= y;
  }

  private static boolean compareOrder(int order, String operator) {
    if ("<".equals(operator)) {
      return order < 0;
    } else if ("<=".equals(operator)) {
      return order <= 0;
    } else if (">".equals(operator)) {
      return order > 0;
    }
    return order >= 0;
  }

  /** JavaScript <code>String(number)</code>: shortest round-trip digits, ECMAScript layout. */
  static String numberToString(double value) {
    if (Double.isNaN(value)) {
      return "NaN";
    } else if (value == 0) {
      return "0";
    } else if (Double.isInfinite(value)) {
      return value > 0 ? "Infinity" : "-Infinity";
    } else if (value < 0) {
      return "-" + numberToString(-value);
    }
    BigDecimal exact = new BigDecimal(value);
    BigDecimal shortest = exact;
    for (int precision = 1; precision <= 17; precision++) {
      BigDecimal rounded = exact.round(new MathContext(precision, RoundingMode.HALF_EVEN));
      if (rounded.doubleValue() == value) {
        shortest = rounded;
        break;
      }
    }
    shortest = shortest.stripTrailingZeros();
    String digits = shortest.unscaledValue().toString();
    int k = digits.length();
    int n = k - shortest.scale();
    if (k <= n && n <= 21) {
      return digits + zeros(n - k);
    } else if (0 < n && n <= 21) {
      return digits.substring(0, n) + "." + digits.substring(n);
    } else if (-6 < n && n <= 0) {
      return "0." + zeros(-n) + digits;
    }
    int exponent = n - 1;
    String sign = exponent < 0 ? "-" : "+";
    String mantissa = k == 1 ? digits : digits.charAt(0) + "." + digits.substring(1);
    return mantissa + "e" + sign + Math.abs(exponent);
  }

  private static String zeros(int count) {
    StringBuilder text = new StringBuilder();
    for (int i = 0; i < count; i++) {
      text.append('0');
    }
    return text.toString();
  }

  /**
   * JavaScript <code>JSON.stringify(value, null, 2)</code>.
   *
   * @return false when JavaScript yields <code>undefined</code> (nothing was written)
   */
  static boolean stringify(Object value, String indent, StringBuilder out) {
    if (value == null) {
      return false;
    }
    if (value instanceof JsNull) {
      out.append("null");
    } else if (value instanceof Boolean) {
      out.append(value);
    } else if (value instanceof Number) {
      double number = toDouble((Number) value);
      out.append(Double.isNaN(number) || Double.isInfinite(number) ? "null" : numberToString(number));
    } else if (value instanceof List) {
      stringifyList((List<?>) value, indent, out);
    } else if (value instanceof Map) {
      stringifyMap((Map<?, ?>) value, indent, out);
    } else {
      quote(value.toString(), out);
    }
    return true;
  }

  private static void stringifyList(List<?> list, String indent, StringBuilder out) {
    if (list.isEmpty()) {
      out.append("[]");
      return;
    }
    String inner = indent + "  ";
    out.append("[\n");
    boolean first = true;
    for (Object element : list) {
      if (!first) {
        out.append(",\n");
      }
      first = false;
      out.append(inner);
      if (element == null) {
        out.append("null");
      } else {
        stringify(element, inner, out);
      }
    }
    out.append('\n').append(indent).append(']');
  }

  private static void stringifyMap(Map<?, ?> map, String indent, StringBuilder out) {
    if (map.isEmpty()) {
      out.append("{}");
      return;
    }
    String inner = indent + "  ";
    out.append("{\n");
    boolean first = true;
    for (Map.Entry<?, ?> entry : map.entrySet()) {
      if (!first) {
        out.append(",\n");
      }
      first = false;
      out.append(inner);
      quote(String.valueOf(entry.getKey()), out);
      out.append(": ");
      if (entry.getValue() == null) {
        out.append("null");
      } else {
        stringify(entry.getValue(), inner, out);
      }
    }
    out.append('\n').append(indent).append('}');
  }

  private static void quote(String text, StringBuilder out) {
    out.append('"');
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      if (c == '"') {
        out.append("\\\"");
      } else if (c == '\\') {
        out.append("\\\\");
      } else if (c == '\b') {
        out.append("\\b");
      } else if (c == '\f') {
        out.append("\\f");
      } else if (c == '\n') {
        out.append("\\n");
      } else if (c == '\r') {
        out.append("\\r");
      } else if (c == '\t') {
        out.append("\\t");
      } else if (c < 0x20 || isLoneSurrogate(text, i)) {
        out.append(String.format("\\u%04x", (int) c));
      } else {
        out.append(c);
      }
    }
    out.append('"');
  }

  private static boolean isLoneSurrogate(String text, int index) {
    char c = text.charAt(index);
    if (Character.isHighSurrogate(c)) {
      return index + 1 >= text.length() || !Character.isLowSurrogate(text.charAt(index + 1));
    }
    return Character.isLowSurrogate(c) && (index == 0 || !Character.isHighSurrogate(text.charAt(index - 1)));
  }

  private HandlebarsReportHelpers() {
  }
}
