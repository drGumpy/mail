package wysy³ka;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GetDate {
	
	private static String sheetName;
	
	static String formatDate(String data){
		Date date;
		try {
			date = new SimpleDateFormat("EEE MMM dd hh:mm:ss z yyyy", Locale.US).parse(data);
			return new SimpleDateFormat("MM.yyyy").format(date);
		} catch (ParseException e) {
			return data;
		}
	}
	
	static String currentDate(){
		 Date date = new Date();
		 return new SimpleDateFormat("MM.yyyy").format(date);
	}
	
	static String findDate(String data){
		Date date = new Date();
		try {
			date = new SimpleDateFormat("MM.yyyy", Locale.US).parse(data);
		} catch (ParseException e) {
		}
		int month= Integer.parseInt(new SimpleDateFormat("MM").format(date));
		int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(date));
		if(month==12){
			month=01;
			sheetName="Zlecenia";
		}
		else{
			month++;
			year--;
			sheetName="Archiwum "+year;
		}
		
		return String.format("%02d", month)+"."+year;
	}
	
	static String sheetName(){
		return sheetName;
	}
	 public static void main(String[] args) {
		 System.out.println(findDate(""));
		 
	 }
}
