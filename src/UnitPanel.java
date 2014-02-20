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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import org.apache.commons.configuration.XMLConfiguration;

import ch.ethz.ssh2.Session;


public class UnitPanel extends JPanel implements ActionListener{

	/**
	 * 
	 */
	XMLConfiguration config;
	int index;
	ConfigUtility cu=new ConfigUtility();
	private String ipaddress;
	private String usernamestring;
	private String pswstring;
	Session ss;
	Thread thread;//获取session的线程 变成field是为了取出session
	
	JTextField editpassword;
	JTextField editusername;
	JTextField editip;
	
	JButton connectbutton;
	
	JTextArea console=new JTextArea(25,60);
	JScrollPane scollpane;
	
	TerminalHandle terminalhandle;
	
	private static final long serialVersionUID = 1L;
	UnitPanel(XMLConfiguration config,int index){
		super();
		this.config=config;
		this.index=index;
		init();
	}
	void init(){
		
		console.setEditable(false);
		console.setBackground(new Color(249,249,249));
		
		cu.setXmlcof(config);
		cu.setXmlcof(config);
		ipaddress=cu.getIpByindex(index);
		usernamestring=cu.getUserByindex(index);
		pswstring=cu.getPswByindex(index);

		//Session s=sessionpool.getSesByIP(ipaddress, "root", "space");
//		JPanel panel=new JPanel();

		this.setLayout(new BorderLayout());
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
		statusbutton.addActionListener(this);
		JButton stopbutton=createButton("stop");
		JButton startbutton=createButton("start");
		JButton deletebutton=createButton("delete");
		connectbutton=createButton("connect");
		connectbutton.addActionListener(this);
		
		

		scollpane=new JScrollPane(console);	
		scollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		//console.setPreferredSize(new Dimension(300,300));
		scollpane.setBorder(new TitledBorder(new BevelBorder(1, Color.LIGHT_GRAY, Color.gray),"console"));
		//scollpane.setPreferredSize(new Dimension(400,400));
		//scollpane.add(console);
		
		editip=new JTextField();
		editip.setText(ipaddress);
		editip.setColumns(8);
		editip.setBorder(new BevelBorder(1,Color.LIGHT_GRAY,Color.gray));
		
		editusername=new JTextField();
		editusername.setText(usernamestring);
		editusername.setColumns(8);
		editusername.setBorder(new BevelBorder(1,Color.LIGHT_GRAY,Color.gray));
		
		editpassword=new JTextField();
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
        this.add(npanel,BorderLayout.NORTH);
        this.add(scollpane,BorderLayout.CENTER);

        

		//leftpanel.setLayout(mgr)

		
	
	}
	JButton createButton(String str){
		JButton button=new JButton(str);
		button.setFocusPainted(false);
		return button;
	}
	void addconstrains(JComponent ipanel,GridBagLayout lay,JComponent com1,GridBagConstraints c){
		lay.setConstraints(com1, c);
		ipanel.add(com1);
		
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getActionCommand().equals(new String("connect"))){
			setUneditable();
			thread=new OpenSessionThread(editip.getText(), editusername.getText(), editpassword.getText(),this);
			thread.start();
		}
	
		//System.out.println(e.getActionCommand());
	}
	void notified(boolean issuccess){
		if(issuccess){
			getSessionFromThread();
			connectbutton.setText("disconnect");
			terminalhandle=new TerminalHandle(console,ss,console.getColumns(),console.getRows());
		}else{
			setEditable();
		}
		
	}
	Session getSessionFromThread(){
		ss=((OpenSessionThread) thread).getSs();
		return ss;
	}
	void setUneditable(){
		editusername.setEditable(false);
		editip.setEditable(false);
		editpassword.setEditable(false);
	}
	void setEditable(){
		editusername.setEditable(true);
		editip.setEditable(true);
		editpassword.setEditable(true);
	}

}
