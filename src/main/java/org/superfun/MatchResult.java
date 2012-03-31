package org.superfun;

/**
 * @author Mike Brock
 */
public class MatchResult {
  private final boolean matches;

  // intentionally not using a Map here since the expected parm-value pair size is
  // almost always going to be very small (1-5 elements typically)
  private final String[] parmNames;
  private final String[] values;

  private MatchResult(boolean matches, String[] parmNames, String[] values) {
    this.matches = matches;
    this.parmNames = parmNames;
    this.values = values;
  }

  private static final String[] EMPTY_STRING_ARRAY = new String[0];

  private static final MatchResult DEFAULT_FALSE_RESULT =
          of(false, EMPTY_STRING_ARRAY, EMPTY_STRING_ARRAY);

  private static final MatchResult DEFAULT_TRUE_RESULT =
          of(true, EMPTY_STRING_ARRAY, EMPTY_STRING_ARRAY);

  static MatchResult defaultTrueResult() {
    return DEFAULT_TRUE_RESULT;
  }

  static MatchResult defaultFalseResult() {
    return DEFAULT_FALSE_RESULT;
  }

  static MatchResult of(boolean matches, String[] parmNames, String[] values) {
    return new MatchResult(matches, parmNames, values);
  }

  public int parameterCount() {
    return parmNames.length;
  }

  public boolean matches() {
    return matches;
  }

  public String getValue(final String parm) {
    if (parm == null) return null;

    for (int i = 0; i < parmNames.length; i++) {
      if (parm.equals(parmNames[i])) return values[i];
    }

    return null;
  }

  public String[] getParmNames() {
    return parmNames;
  }

  public String[] getValues() {
    return values;
  }
}
