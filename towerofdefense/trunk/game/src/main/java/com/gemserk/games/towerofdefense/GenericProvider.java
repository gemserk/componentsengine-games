package com.gemserk.games.towerofdefense;

public interface GenericProvider {

	<T> T get();

	<T> T get(Object ...objects);


}