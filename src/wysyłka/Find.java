package wysy³ka;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;


class Device{
	String model;
	String type;
	String producent;
	public String toString(){
		return type+" firmy: "+producent+" model: "+model;
 }
}

//dane klienta
class Client{
	String name;
	String address;
 	String postal_code;
 	String place;
 	String mail="";
 	public String toString(){
 		return address+", "+postal_code+" "+place;
 	}
}

class DeviceData{
	 Device device = new Device();
	 String deviceSerial;
	 String calibrationDate;
	 public String toString(){
		 return device+" o numerze seryjnym: "+deviceSerial+", wzorcowany: "+calibrationDate;
	 }
}

class Mail{
    Client user= new Client();
    ArrayList<DeviceData> devices = new ArrayList<DeviceData>();    
}

public class Find {
	
	private static File file= new File("C:\\Users\\Laboratorium\\Desktop\\Laboratorium.ods");
	    
	//Spis nie wystawionych certyfikatów wzorcowania
	private static ArrayList<Mail> data = new ArrayList<Mail>();
	        
	//Spis danych o klientach
	private static HashMap<String, Client> clientsData =new HashMap<String, Client>();    
	    
	//Spis typów wzorcowanych urz¹dzeñ
	private static HashMap<String, Device> devicesData =new HashMap<String, Device>();
	
	private static String format;
	private static String sheetName;
	
	private static void getFormat(String now){
		format=GetDate.findDate(now);
		sheetName= GetDate.sheetName();
	}
	    
	//Wyszukiwanie nie wsytawionych œwiadectw - brak daty wzorcowania
	private static void mailData() throws IOException{
		final Sheet sheet = SpreadSheet.createFromFile(file).getSheet(sheetName);
	    int d=1;
	    
	    while(!GetDate.formatDate(sheet.getValueAt(2,d).toString()).equals(format)) d++;
	    //wczytywanie zleceñ do pierwszego braku urz¹dzenia do wzorcowania
	    while(GetDate.formatDate(sheet.getValueAt(2,d).toString()).equals(format)){
	    	String name = sheet.getValueAt(4,d).toString();
	    	Mail order;
	    	if(clientsData.containsKey(name)){
	    		int i=0;
	    		do{
	    			order =data.get(i);
	    			i++;
	    		}while(!order.user.name.equals(name));
	    		data.remove(i-1);
	    	}else{
	    		order = new Mail();
	    		order.user.name=name;
	    		clientsData.put(name, order.user);
	    	}
	    	DeviceData device = new DeviceData();
	    	device.deviceSerial = sheet.getValueAt(6,d).toString();
	    	device.calibrationDate = sheet.getValueAt(10,d).toString();
	    	String deviceModel= sheet.getValueAt(5,d).toString();
	    	device.device.model=deviceModel;
	    	if(!devicesData.containsKey(deviceModel))
	    		devicesData.put(deviceModel, device.device);
	    	order.devices.add(device);
	    	data.add(order);
	        d++;
	    }
	}
	    
	    //poszukiwanie klientów zlecaj¹cych wzorcowanie/u¿ytkowaników urz¹dzenia
	private static void findClientData() throws IOException{
		final Sheet sheet = SpreadSheet.createFromFile(file).getSheet("Klienci");
		int i=0;
		String name;
		while(sheet.getValueAt(0,i)!=""){
			name = sheet.getValueAt(0,i).toString();
			if(clientsData.containsKey(name)){
				Client andrzej = new Client();
				andrzej.name=name;
				andrzej.address= sheet.getValueAt(1,i).toString();
				andrzej.postal_code= sheet.getValueAt(2,i).toString();
				andrzej.place= sheet.getValueAt(3,i).toString();
				andrzej.mail=sheet.getValueAt(4,i).toString();
				clientsData.put(andrzej.name, andrzej);
			}
			i++;
		}        
	}
	    
	//wyszukiwanie danych o wzorcowanych urz¹dzeniach
	private static void findDeviceData() throws IOException{
		final Sheet sheet = SpreadSheet.createFromFile(file).getSheet("Urz¹dzenia");
		int i=0;
		String model;
		while(sheet.getValueAt(0,i)!=""){
			model = sheet.getValueAt(0,i).toString();
			if(devicesData.containsKey(model)){
				Device nunczaku = new Device();
				nunczaku.model= model;
				nunczaku.type= sheet.getValueAt(1,i).toString();
				nunczaku.producent= sheet.getValueAt(2,i).toString();
				devicesData.put(nunczaku.model, nunczaku);
			}
			i++;
		}
	}
	
	private static void gatherDevice(ArrayList<DeviceData> devices){
		for(int i=0; i<devices.size();i++){
			String name= devices.get(i).device.model;
			devices.get(i).device=devicesData.get(name);
		}
	}
	
	private static void gather(){
		for(int i=0; i<data.size();i++){
			String name= data.get(i).user.name;
			data.get(i).user=clientsData.get(name);
			gatherDevice(data.get(i).devices);
		}
	}
	
	static ArrayList<Mail> get_data(){
		try{
			run();
		} catch (IOException e) {}
		return data;
	}
	
	static void set_file(File _file){
		file=_file;
	}
	    
	    
	//otrzymanie danych o wzorcowniu
	private static void run() throws IOException{
		getFormat("");
		mailData();
		findClientData();
		findDeviceData();
		gather();
	}
}
