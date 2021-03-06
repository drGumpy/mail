package wysyłka;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

public class Generate extends JFrame {
	private static final long serialVersionUID = 1L;

	private static String mailText=""; 
	
	private static JTextField filePath = new JTextField(50);
	
	private static SendMail sender;
	
	private static boolean test = true;
	
	static String deviceTable(DeviceData data) {
		String row= "<tr>";
		row+= "    <td>"+data.device.model+"</td>";
		row+= "    <td>"+data.device.producent+"</td>";
		row+= "    <td>"+data.deviceSerial+"</td>";
		row+= "    <td>"+data.calibrationDate+"</td>";
		return row;
	}
	
	static void txt(Mail info){
		String device="<table style=\"width:100%\">"
				+ "<tr>" + 
				"    <td>Model</td>" + 
				"    <td>Producent</td>" + 
				"    <td>Numer seryjny</td>" +
				"    <td>Data wzorcowania</td>" +
				"  </tr>";
		for(int i=0; i<info.devices.size() ; i++){
			device+=deviceTable(info.devices.get(i));
					//info.devices.get(i)+"<br\\>";
		}
		device+="</table>";
		String mail= mailText.replaceAll("<Urządzenia>", device);
		if(!test)
			sender.send(info.user.mail, mail);
		else
			sender.send(MailData.getFrom(), mail);
	}
	
	private static String gethtml(){
		String html="";
		try {
			Scanner scan = new Scanner(new File("C:\\Users\\Laboratorium\\Documents\\Przypominanie e-mail\\pap.html"));
			while(scan.hasNextLine()){
				html+= scan.nextLine();
			}
			scan.close();
			return html;
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null,
				"Brak szablonu w żądanej lokalizacji",
				"Brak szablonu",
				JOptionPane.ERROR_MESSAGE);
			return "";
		}
	}
	
	private static void getMailText() throws Exception{
		try {	
			Scanner scan = new Scanner(
					new File("C:\\Users\\Laboratorium\\Documents\\Przypominanie e-mail\\Szablon.txt"), "UTF-8");
			mailText="";
			while(scan.hasNextLine())
				mailText+=scan.nextLine()+"<br\\>";
			scan.close();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null,
        		    "Brak szablonu w żądanej lokalizacji",
        		    "Brak szablonu",
        		    JOptionPane.ERROR_MESSAGE);
			throw new Exception();
		}
		String html = gethtml();
		if(html.equals("")){
			throw new Exception();
		}else{
			mailText= html.replaceAll("<mail>", mailText);
		}
	}
	
	private static JPanel file(){
		JButton b= new JButton("zmień");
	    JPanel jp = new JPanel();
	    jp.setPreferredSize(new Dimension(650, 50));
	    jp.setMinimumSize(jp.getPreferredSize());
	    jp.setLayout(new GridBagLayout());
	    GridBagConstraints c = new GridBagConstraints();
	    c.gridx=1;
	    jp.setBorder(new TitledBorder("plik z danymi"));
	    jp.add(b, c);
	    filePath.setText("C:\\Users\\Laboratorium\\Desktop\\Laboratorium.ods");
	    filePath.setEditable(false);
	    c.gridx=2;
	    jp.add(filePath);
	    b.addActionListener(new ActionListener(){
	    public void actionPerformed(ActionEvent e) {
	    	JFileChooser c = new JFileChooser();
	        c.showOpenDialog(c);
	        String path =c.getSelectedFile().toString();
	        if(path.substring(path.length()-4, path.length()).equals(".ods"))
	        	filePath.setText(c.getSelectedFile().toString()); 
	        else{
	        	JOptionPane.showMessageDialog(null,
	        		    "Plik z danymu musi miec rozszerzenie .ods",
	        		    "Błędny plik",
	        		    JOptionPane.ERROR_MESSAGE);
	        	filePath.setText("C:\\Users\\Laboratorium\\Desktop\\Laboratorium.ods");
	        	}
	    	}
	    });
	    return jp; 
	}
	
	private void VerificateMail(ArrayList<Mail> data) throws Exception{
		ArrayList <String> errors =new ArrayList <String>();
		MailVerificate.start();
		for(int i=0; i<data.size(); i++){
			if(data.get(i).user.mail.equals("")){
				errors.add(data.get(i).user.name+": brak adresu eMail");
				//data.remove(i);
				//i--;
				continue;
			}
			String error = MailVerificate.mailCheck(data.get(i).user.mail);
			if(!error.equals("")){
				errors.add(data.get(i).user.name+": "+ error);
				data.remove(i);
				i--;
			}
		}
		if(!errors.isEmpty()){
			String error ="Błąd w mailach:\n";
			JTabbedPane tabbedPane = new JTabbedPane();
			for(int i=0; i<errors.size();i++){
				error+=errors.get(i)+"\n";
				if((i+1)%10==0){
					tabbedPane.addTab((i+1)/10+"", new JTextArea(error));
					error="Błąd w mailach:\n";
				}
			}
			Object[] options = {"Kontynuj",
                    "Anuluj wysyłanie"};
			int n = JOptionPane.showOptionDialog(Generate.this, tabbedPane,
					"Błędne adresy eMail", JOptionPane.YES_NO_OPTION,
					JOptionPane.ERROR_MESSAGE, null, options,options[1]);
			if(n==JOptionPane.NO_OPTION)
				throw new Exception();
		}
	}
	
	void sendMail(){
		File file = new File(filePath.getText());
		Find find = new Find(file);
		ArrayList<Mail> data = find.get_data();
		try {
			VerificateMail(data);
		} catch (Exception e) {
			return;
		}
		try {
			getMailText();
		} catch (Exception e) {
			return;
		}
		sender= new SendMail(MailData.getProperties(), MailData.getUsername(),
				MailData.getPassword(), MailData.getFrom());
		for(int i=0; i<data.size(); i++){
			txt(data.get(i));
		}
		System.out.println(mailText);
		JOptionPane.showMessageDialog(null,
    		    "Wysyłka wykonana",
    		    "Mail",
    		    JOptionPane.ERROR_MESSAGE);
		this.dispose();
	}
	
	public Generate(){
    	//JPanel jp = new JPanel();
    	setLayout(new GridBagLayout());
    	GridBagConstraints c = new GridBagConstraints();
    	c.anchor =GridBagConstraints.PAGE_START;
    	c.gridy=0;
    	add(file(),c);
    	JButton b= new JButton("wyślij");
    	c.gridy=1;
    	add(b,c);
    	b.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent e) {
    			sendMail();
    		}
	    });
    }
	
	public static void run(){
		SwingUtilities.invokeLater(new Runnable(){
			Generate f = new Generate();
			public void run(){
				f.setTitle("Przypomnienie o wzorcowaniu");
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setSize(700,150);
				f.setVisible(true);
			}
		});
	}
	
	public static void main(String[] args) {
		run();
	}
	
}
