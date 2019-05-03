package eu.su.mas.dedaleEtu.mas.tools;



public class AlphaNumCompare {
	
	//Compare les deux chaines de charactere
	public static boolean isFirst(String str1, String str2){
		String lows1 = str1.toLowerCase();
		String lows2 = str2.toLowerCase();
		int cpt=0;
		
		while(cpt<lows1.length() && cpt<lows2.length()) {
			if((int)lows1.charAt(cpt)<(int)lows2.charAt(cpt)) {
				return true;
			}
			else if((int)lows1.charAt(cpt)>(int)lows2.charAt(cpt)) {
				return false;
			}
			cpt++;
		}
		if(lows1.length()<(int)lows2.length()) {
			return true;
		}
		else if(lows1.length()>(int)lows2.length()) {
			return false;
		}
		return false;
		
	}
}
