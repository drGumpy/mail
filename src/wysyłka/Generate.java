package wysy³ka;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Generate {
	static String path= "C:/Users/Laboratorium/Desktop/test/";
	
	static void txt(Mail info){
		try{
		    PrintWriter writer = new PrintWriter(path+info.user.name+".txt", "UTF-8");
		    writer.println(info.user);
		    writer.println("mail " + info.user.mail);
		    writer.println("");
		    writer.println("<teœæ maila>");
		    writer.println("");
		    writer.println("wzorcowali Pañstwo u nas nastêpuj¹ce przyrz¹dy:");
		    for(int i=0; i<info.devices.size() ; i++){
		    	writer.println(info.devices.get(i));
		    }
		    
		    writer.close();
		} catch (IOException e) {}
	}
	
	public static void main(String[] args) {
		ArrayList<Mail> data = Find.get_data();
		for(int i=0; i<data.size(); i++){
			txt(data.get(i));
		}
	}
}
