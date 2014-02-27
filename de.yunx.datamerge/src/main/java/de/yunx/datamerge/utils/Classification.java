package de.yunx.datamerge.utils;

public final class Classification {

	static int LEGAL = 1; // 0000001
	static int COMPANY = 2; // 0000010
	static int NAME1 = 4; // 0000100
	static int NAME2 = 8; // 0001000
	static int PREPOSITION = 16;
	static int CITY = 32;
	static int STREET = 64;
	static int STATE = 128;
	static int COUNTRY = 256;
	static int SPECIFICATION = 512; // german technical...
	static int TITLE = 1024; // prof dr ing etc

	public static String getAsString(int classification) {
		String result = "";

		if ((classification & 1) == 1)
			result += "LEGAL ";
		if ((classification & 2) == 2)
			result += "COMPANY ";
		if ((classification & 4) == 4)
			result += "NAME1 ";
		if ((classification & 8) == 8)
			result += "NAME2 ";
		if ((classification & 16) == 16)
			result += "PREPOSITION ";
		if ((classification & 32) == 32)
			result += "CITY ";
		if ((classification & 64) == 64)
			result += "STREET ";
		if ((classification & 128) == 128)
			result += "STATE ";
		if ((classification & 256) == 256)
			result += "COUNTRY ";
		if ((classification & 512) == 512)
			result += "SPECIFICATION ";
		if ((classification & 1024) == 1024)
			result += "TITLE ";
		if ((classification & 2048) == 2048)
			result += "OTHER ";

		return result.trim();
		
	}
	
	/**
	 * 
	 * @param classification
	 * @return -1 if unknown
	 */
	public static int getAsInt(String classification){
		switch (classification.toLowerCase()) {
		case "legal":
			return LEGAL;
		case "company":
			return COMPANY;
		case "name1":
			return NAME1;
		case "name2":
			return NAME2;
		case "preposition":
			return PREPOSITION;
		case "city":
			return CITY;
		case "street":
			return STREET;
		case "state":
			return STATE;
		case "country":
			return COUNTRY;
		case "specification":
			return SPECIFICATION;
		case "title":
			return TITLE;
		default: return -1;
		}
	}

}
