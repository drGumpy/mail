package wysyłka;

import java.util.ArrayList;
import java.util.HashSet;

public class MailVerificate {
	
	private static HashSet<Character> domianAllowed = new HashSet<Character>();
	private static HashSet<Character> localAllowed = new HashSet<Character>();
	
	private static String doubleDots(String data, String coments){
		String[] separate  = data.split("\\.");
		for(int i=0; i<separate.length; i++){
			if(separate[i].length()==0)
				return "powójny znak '.' "+coments;
			}
		return "";
	}
	
	private static void domian(ArrayList<String> data, String domian){
		domian= domian.replaceAll("\\+", "");
		char[] characters = domian.toCharArray();
		for(int i=0; i<characters.length; i++){
			if(!domianAllowed.contains(characters[i])){
				data.add("nieprawidłowe znaki w nazwie domeny");
				break;
			}
		}
		if(characters[0]=='-')
			data.add("znak '-' na początku nazwy domeny");
		if(characters[characters.length-1]=='-')
			data.add("znak '-' na końcu nazwy domeny");
		if(characters[characters.length-1]=='.')
			data.add("znak '.' na początku nazwy domeny");
		if(characters[characters.length-1]=='.')
			data.add("znak '.' na końcu nazwy domeny");
		String dots=doubleDots(domian, "w nazwzie domeny");
		if(!dots.equals(""))
			data.add(dots);
	}
	
	private static void local(ArrayList<String> data, String local){
		local= local.replaceAll("\\+", "");
		char[] characters = local.toCharArray();
		for(int i=0; i<characters.length; i++){
			if(!localAllowed.contains(characters[i]))
				data.add("nieprawidłowe znaki w nazwie lokalnej");
		}
		if(characters[characters.length-1]=='.')
			data.add("znak '.' na początku nazwy lokalnej");
		if(characters[characters.length-1]=='.')
			data.add("znak '.' na końcu nazwy lokalnej");
		String dots=doubleDots(local, "w nawzie lokalnej");
		if(!dots.equals(""))
			data.add(dots);
	}

	private	static void localChar(){
		localAllowed.addAll(domianAllowed);
		localAllowed.add('!');
		localAllowed.add('#');
		localAllowed.add('$');
		localAllowed.add('%');
		localAllowed.add('&');
		localAllowed.add('\'');
		localAllowed.add('*');
		localAllowed.add('+');
		localAllowed.add('-');
		localAllowed.add('/');
		localAllowed.add('=');
		localAllowed.add('?');
		localAllowed.add('^');
		localAllowed.add('{');
		localAllowed.add('|');
		localAllowed.add('}');
		localAllowed.add('~');
	}
	
	private static void domianChar(){
		domianAllowed.add((char)'-');
		domianAllowed.add('.');
		for(int i=48; i<58; i++){
			domianAllowed.add((char)i);
		}
		for(int i=0; i<26; i++){
			domianAllowed.add((char)('a'+i));
			domianAllowed.add((char)('A'+i));
		}
	}
	
	static void start(){
		domianChar();
		localChar();
	}
	
	private static String errorCheck(ArrayList<String> data){
		if(data.isEmpty()) return "";
		String answ=data.get(0);
		for(int i=1; i<data.size(); i++){
			answ+= ", "+ data.get(i);
		}
		return answ;
	}
	
	static String mailCheck(String mail){
		ArrayList<String> data = new ArrayList<String>();
		String[] separate = mail.split("\\s+");
		if(separate.length>1)
			return "mail zawiera spacje";
		separate = mail.split("@");
		if(separate.length!=2)
			return "zły format (nieprawiłowa ilość znaków @)";
		domian(data, separate[1]);
		local(data, separate[0]);
		return errorCheck(data);
	}
}
