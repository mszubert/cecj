/*
  Copyright 2009 by Marcin Szubert
  Licensed under the Academic Free License version 3.0
 */

package cecj.utils;

/**
 * 
 * @author Marcin Szubert
 * 
 * @param <T>
 */
public interface EquivalenceComparator<T> {
	boolean equal(T o1, T o2);
}
