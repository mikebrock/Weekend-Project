package org.superfun;


import static org.superfun.MatchResult.defaultFalseResult;

/**
 * @author Mike Brock
 */
public class Matcher {
  private final int tokenReferences;
  private final MatchPattern.Segment[] segments;

  private Matcher(int tokenReferences, MatchPattern.Segment[] segments) {
    this.tokenReferences = tokenReferences;
    this.segments = segments;
  }

  static Matcher of(int tokenReferences, MatchPattern.Segment[] segments) {
    return new Matcher(tokenReferences, segments);
  }

  public MatchResult match(final String toMatch) {
    MatchPattern.Segment currentSegment;
    int offset = 0;
    int segmentIndex = 0;
    int tokenIndex = 0;

    final String[] keys = new String[tokenReferences];
    final String[] vals = new String[tokenReferences];

    currentSegment = segments[0];

    if (currentSegment.isStartingWildcard()) {
      offset = 1;
    }

    do {
      if (currentSegment.isParameter()) {
        keys[tokenIndex] = currentSegment.getPattern();

        int nextOffset = calculateOffsetToNextSegment(toMatch, offset, segmentIndex);
        if (nextOffset == Integer.MIN_VALUE) {
          return defaultFalseResult();
        }

        vals[tokenIndex++] = toMatch.substring(currentSegment.getStart(offset), nextOffset);

        segmentIndex++;
        offset = nextOffset + 1;
      }
      else if ((offset = calculateOffset(toMatch, offset, currentSegment)) == Integer.MIN_VALUE) {
        return defaultFalseResult();
      }

      if (++segmentIndex < segments.length) {
        currentSegment = segments[segmentIndex];
      }
      else {
        break;
      }
    }
    while (true);

    if (tokenReferences == 0)
      return MatchResult.defaultTrueResult();
    else
      return MatchResult.of(true, keys, vals);

  }

  public int calculateOffsetToNextSegment(final String toMatch, final int offset, final int segmentIndex) {
    if (segmentIndex + 1 < segments.length) {
      final int _offset = calculateOffset(toMatch, offset, segments[segmentIndex + 1]);
      if (_offset == Integer.MIN_VALUE) {
        return _offset;
      }
      else {
        return _offset - 1;
      }
    }
    else {
      return toMatch.length();
    }
  }

  public static int calculateOffset(final String toMatch, int offset, MatchPattern.Segment to) {
    final int idx = toMatch.indexOf(to.getPattern(), offset);
    if (idx < 0) {
      return Integer.MIN_VALUE;
    }
    else {
      return idx + to.getPattern().length();
    }
  }
}

