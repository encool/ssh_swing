import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;

import org.apache.commons.configuration.XMLConfiguration;

import ch.ethz.ssh2.Session;


public class utility {
	static ConfigUtility cu=new ConfigUtility();
	static SessionPool sessionpool=new SessionPool();
	static String ipaddress;
	static String usernamestring;
	static String pswstring;
	public static JPanel createComponent(XMLConfiguration config,int index){
		cu.setXmlcof(config);
		ipaddress=cu.getIpByindex(index);
		usernamestring=cu.getUserByindex(index);
		pswstring=cu.getPswByindex(index);
		Runnable run=new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Session s=sessionpool.getSesByIP(ipaddress, usernamestring, pswstring);
			}
			
		};
		SwingUtilities.invokeLater(run);
		//Session s=sessionpool.getSesByIP(ipaddress, "root", "space");
		JPanel panel=new JPanel();
		panel.setLayout(new BorderLayout());
		//Box boxpanel=new Box(BoxLayout.Y_AXIS);
		
		JPanel npanel=new JPanel();
		npanel.setLayout(new BorderLayout());
		
		JPanel leftpanel=new JPanel();
		JPanel rightpanel=new JPanel();
		JLabel ip=new JLabel("ip ");
		JLabel username=new JLabel("user");
		JLabel password=new JLabel("password");
		JButton restartbutton=createButton("restart");
		ActionListener listener=new MyActionListener(config,0,restartbutton);
		restartbutton.addActionListener(listener);
//		restartbutton.addActionListener(new ActionListener(){
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				// TODO Auto-generated method stub
//			//	config.getString("");
//			}
//			
//		});
		JButton statusbutton=createButton("status");
		JButton stopbutton=createButton("stop");
		JButton startbutton=createButton("start");
		JButton deletebutton=createButton("delete");
		JButton connectbutton=createButton("connect");
		
		JEditorPane console=new JEditorPane();
		JScrollPane scollpane=new JScrollPane(console);	
		//console.setPreferredSize(new Dimension(300,300));
		scollpane.setPreferredSize(new Dimension(300,300));
		//scollpane.add(console);
		
		JTextField editip=new JTextField();
		editip.setText(ipaddress);
		editip.setColumns(8);
		editip.setBorder(new BevelBorder(1,Color.LIGHT_GRAY,Color.gray));
		
		JTextField editusername=new JTextField();
		editusername.setText(usernamestring);
		editusername.setColumns(8);
		editusername.setBorder(new BevelBorder(1,Color.LIGHT_GRAY,Color.gray));
		
		JTextField editpassword=new JTextField();
		editpassword.setText(pswstring);
		editpassword.setColumns(8);
		editpassword.setBorder(new BevelBorder(1,Color.LIGHT_GRAY,Color.gray));
		
		Box box=new Box(BoxLayout.X_AXIS);
		GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.fill=GridBagConstraints.HORIZONTAL;
        c.insets=new Insets(1,1,1,1);
        leftpanel.setLayout(gridbag);

        gridbag.setConstraints(username, c);
        leftpanel.add(username);
       

        gridbag.setConstraints(editusername, c);
        leftpanel.add(editusername);     
        
  
        gridbag.setConstraints(password, c);
        leftpanel.add(password);
        c.insets=new Insets(1,1,1,5);
        addconstrains(leftpanel,gridbag,editpassword,c);
        c.insets=new Insets(1,1,1,1);
        c.gridheight=1;
        addconstrains(leftpanel,gridbag,restartbutton,c);
        c.gridwidth = GridBagConstraints.REMAINDER; 
        addconstrains(leftpanel,gridbag,statusbutton,c);
        c.gridwidth=1;
        //c.weightx=1;
        addconstrains(leftpanel,gridbag,ip,c);
        c.gridwidth=2;
        addconstrains(leftpanel,gridbag,editip,c);
        c.gridwidth=1;
        //c.weightx=1;
        c.insets=new Insets(1,1,1,5);
        addconstrains(leftpanel,gridbag,connectbutton,c);
        c.insets=new Insets(1,1,1,1);
        //c.weightx=1;
        
        addconstrains(leftpanel,gridbag,startbutton,c);
        addconstrains(leftpanel,gridbag,stopbutton,c);
        npanel.add(leftpanel,BorderLayout.WEST);
        //boxpanel.add(box);
        //boxpanel.add(scollpane);
        panel.add(npanel,BorderLayout.NORTH);
        panel.add(scollpane,BorderLayout.CENTER);
        

		//leftpanel.setLayout(mgr)
		return panel;
		
	}
	static JButton createButton(String str){
		JButton button=new JButton(str);
		button.setFocusPainted(false);
		return button;
	}
	public static String getIpAddrByIndex(int index){
		return "1.1.1.1";
	}
	static void addconstrains(JComponent ipanel,GridBagLayout lay,JComponent com1,GridBagConstraints c){
		lay.setConstraints(com1, c);
		ipanel.add(com1);
		
	}

}
