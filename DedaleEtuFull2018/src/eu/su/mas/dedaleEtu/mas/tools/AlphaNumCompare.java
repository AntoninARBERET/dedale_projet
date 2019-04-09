package eu.su.mas.dedaleEtu.mas.tools;

public class AlphaNumCompare {
	public static boolean isFirst(String str1, String str2){
		int cpt=0;
		while(cpt<str1.length() && cpt<str2.length()){
			if(str1.charAt(cpt)>str2.charAt(cpt)){
				return true;
			}
			else if(str1.charAt(cpt)<str2.charAt(cpt)){
				return false;
			}
		}
		if(str1.length()<str2.length()){
			return true;
		}else{
			return false;
		}
	}
}
