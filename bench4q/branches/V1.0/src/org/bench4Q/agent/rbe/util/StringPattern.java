package org.bench4Q.agent.rbe.util;

public abstract class StringPattern {
	int start, end; // Last match.

	// Find a match in the given string.
	// Return index of first character of pattern, if the
	// pattern is found.
	// Returns -1 if no pattern is found.

	// Search whole string.
	public int find(String s) {
		return (find(s, 0, s.length() - 1));
	}

	// Search starting at index start.
	public int find(String s, int start) {
		return (find(s, start, s.length() - 1));
	}

	// Search from index start to end, inclusive.
	// Note that the ENTIRE pattern must fit between start and end,
	// not just begin matching before end.
	public abstract int find(String s, int start, int end);

	// See if pattern matches first part of string.
	public boolean match(String s) {
		return (match(s, 0, s.length() - 1));
	}

	// See if pattern matches at index pos.
	public boolean match(String s, int pos) {
		return (matchWithin(s, pos, s.length() - 1));
	}

	// See if pattern matches exactly characters pos to end, inclusive.
	public boolean match(String s, int pos, int end) {
		int saveStart = start;
		int saveEnd = this.end;

		if (matchWithin(s, pos, end)) {
			if (this.end == end) {
				return (true);
			}
			this.start = saveStart;
			this.end = saveEnd;
		}

		return (false);
	}

	// Find a complete match starting at pos and stopping before or at end.
	public abstract boolean matchWithin(String s, int pos, int end);

	// Returns the index of the last charcter that matched the
	// pattern.
	public int end() {
		return (end);
	}

	// Returns the index of the first character that last matched
	// the pattern.
	public int start() {
		return (start);
	}

	// Minimum and maximum lengths.
	protected abstract int minLength();

	protected abstract int maxLength();
}
