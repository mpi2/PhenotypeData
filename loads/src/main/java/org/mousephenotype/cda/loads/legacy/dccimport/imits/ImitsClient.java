package org.mousephenotype.cda.loads.legacy.dccimport.imits;

/**
 * Generic interface to get either a URL or file backed imits client
 */
public interface ImitsClient {

	String getGeneByColonyId(String colonyId, String center);

	String getAlleleSymbolByColonyId(String colonyId);

	String getStrainByColonyId(String colonyId, String center);

	String getStrainNameByColonyId(String colonyId, String center);

	String getColonyIdByEsCell(String specimenID, String center);

	String getProjectByEsCell(String specimenID, String center);

	String getAlleleMGIIDByEscell(String esCell);

	String getGeneByEscell(String colonyID);
}
