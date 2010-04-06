/*
  Copyright 2009 by Marcin Szubert
  Licensed under the Academic Free License version 3.0
 */

package cecj.utils;

public class Pair<T> {
	public T first;
	public T second;

	public Pair(T first, T second) {
		this.first = first;
		this.second = second;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Pair)) {
			return false;
		}

		Pair<?> other = (Pair<?>) obj;
		return (first.equals(other.first) && second.equals(other.second));
	}

	@Override
	public int hashCode() {
		return first.hashCode() + second.hashCode();
	}

	@Override
	public String toString() {
		return "<" + first.toString() + ", " + second.toString() + ">";
	}
}
