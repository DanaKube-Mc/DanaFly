package fr.danakube.danafly.util.data;

public interface Reloadable {
	
	public default void onTempflyReload() {
		return;
	}

}
