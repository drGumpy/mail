package wysyłka;

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
		return type+" model: "+model;
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
		String[] date = calibrationDate.split("÷");
		if(date.length==1)
			return device+" o numerze seryjnym: "+deviceSerial+
					", wzorcowany w dniu "+calibrationDate;
		else
			return device+" o numerze seryjnym: "+deviceSerial+
					", wzorcowany w dniach "+calibrationDate;
	 }
}

class Mail{
    Client user= new Client();
    ArrayList<DeviceData> devices = new ArrayList<DeviceData>();    
}

public class Find {
	
	Find(File file){
		this.file=file;
	}
	
	private static File file= 
			new File("C:\\Users\\Laboratorium\\Desktop\\Laboratorium.ods");
	    
	//Spis nie wystawionych certyfikatów wzorcowania
	private ArrayList<Mail> data = new ArrayList<Mail>();
	        
	//Spis danych o klientach
	private HashMap<String, Client> clientsData =new HashMap<String, Client>();    
	    
	//Spis typów wzorcowanych urządzeń
	private HashMap<String, Device> devicesData =new HashMap<String, Device>();
	
	private HashMap <String, Mail> devices = new HashMap<String, Mail>();
	
	private String format;
	private String sheetName;
	
	private void getFormat(String now){
		format=GetDate.findDate(now);
		sheetName= GetDate.sheetName();
	}
	
	private void findRepeat(int d, String sheetName, Sheet sheet) throws IOException{
		while(sheet.getValueAt(5,d)!=""){
			String deviceSerial =sheet.getValueAt(6,d).toString();
			if(devices.containsKey(deviceSerial)){
				Mail order = devices.get(deviceSerial);
				if(!order.user.name.equals(sheet.getValueAt(4,d).toString())){
					d++;
					continue;
					}
				ArrayList<DeviceData> device = order.devices;
				int n =device.size();
				for(int i=0; i<n; i++){
					if(device.get(i).deviceSerial.equals(deviceSerial))
						if(device.get(i).device.model.
								equals(sheet.getValueAt(5,d))){
							device.remove(i);
							break;
						}
				}
				devices.remove(deviceSerial);	
			}
			d++;
		}
		if(!sheetName.equals("Zlecenia"))
			findRepeat(1 ,"Zlecenia",
					SpreadSheet.createFromFile(file).getSheet("Zlecenia"));
	}
	    
	//Wyszukiwanie nie wsytawionych świadectw - brak daty wzorcowania
	private void mailData() throws IOException{
		final Sheet sheet = SpreadSheet.createFromFile(file).getSheet(sheetName);
	    int d=1;
	    
	    while(!GetDate.formatDate(sheet.getValueAt(2,d).toString()).equals(format)) d++;
	    //wczytywanie zleceń do pierwszego braku urządzenia do wzorcowania
	    while(GetDate.formatDate(sheet.getValueAt(2,d).toString()).equals(format)){
	    	String name = sheet.getValueAt(4,d).toString();
	    	Mail order;
	    	boolean flag = true;
	    	if(clientsData.containsKey(name)){
	    		int i=0;
	    		flag = false;
	    		while(!data.get(i).user.name.equals(name)) i++;
	    		order =data.get(i);
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
	    	if(flag)
	    		data.add(order);
	    	devices.put(device.deviceSerial, order);
	        d++;
	    }
	    findRepeat(d, sheetName, sheet);
	}
	    
	    //poszukiwanie klientów zlecających wzorcowanie
	private void findClientData() throws IOException{
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
	    
	//wyszukiwanie danych o wzorcowanych urządzeniach
	private void findDeviceData() throws IOException{
		final Sheet sheet = SpreadSheet.createFromFile(file).getSheet("Urządzenia");
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
	
	private void gatherDevice(ArrayList<DeviceData> devices){
		for(int i=0; i<devices.size();i++){
			String name= devices.get(i).device.model;
			devices.get(i).device=devicesData.get(name);
		}
	}
	
	private void gather(){
		for(int i=0; i<data.size();i++){
			String name= data.get(i).user.name;
			data.get(i).user=clientsData.get(name);
			gatherDevice(data.get(i).devices);
		}
	}
	
	ArrayList<Mail> get_data(){
		try{
			run();
		} catch (IOException e) {}
		return data;
	}
	
	static void set_file(File _file){
		file=_file;
	}
	    
	    
	//otrzymanie danych o wzorcowniu
	private void run() throws IOException{
		getFormat("");
		mailData();
		findClientData();
		findDeviceData();
		gather();
	}
}
