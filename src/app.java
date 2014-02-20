
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;





public class app { 

	/**
	 * @param args
	 */
	static JTabbedPane tabp;
	//标题卡上右键菜单
	static JPopupMenu popupmenu;
	static int delPaneIndex;
	static JFrame f;
	public static void init(){
		try {
			tabp=new JTabbedPane();
			//计数选项卡数
			int i=0;
			//初始化popupmenu
			popupmenu=new JPopupMenu();
			Action deletePane=new MyDeletePaneAction("删除",tabp);
			popupmenu.add(deletePane);
			XMLConfiguration config = null;
			f = new JFrame("GridBag Layout Example");
			JPanel jpanel=new JPanel(new BorderLayout());
			JPanel uppanel=new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
			//uppanel.setAlignmentX(FlowLayout.LEFT);
			config = new XMLConfiguration("conf/myconfig.xml");
			String str="components.component("+Integer.toString(i)+").name";
			String s=config.getString(str);
			
			while(s!=null){
				JButton tmpbutton=new JButton(new MyAddPaneAction(s,config,i,tabp));
				tmpbutton.setContentAreaFilled(false);
				tmpbutton.setFocusPainted(false);
				//tmpbutton.setBorderPainted(false);
				uppanel.add(tmpbutton);
				if(i<1){
					JPanel panel=new UnitPanel(config, i);
					tabp.add(panel,config.getString("components.component("+Integer.toString(i)+").name"));
				}
				
			
				i++;
				str="components.component("+Integer.toString(i)+").name";
				s=config.getString(str);
			}
			
			//选项卡标签上添加右键菜单
			tabp.addMouseListener(new java.awt.event.MouseAdapter(){
				public void mousePressed(MouseEvent e) {
			    	maybeShowPopup(e);
				}
		
			    public void mouseReleased(MouseEvent e) {
			    	maybeShowPopup(e);
			    }
		
			    private void maybeShowPopup(MouseEvent e) {
			    	if (e.isPopupTrigger()) {		     
			    		for (int i = 0; i < tabp.getTabCount(); i ++) {
			    			Rectangle rect = tabp.getBoundsAt(i);
			    			if (rect.contains(e.getX(), e.getY())) {
		    				popupmenu.show(e.getComponent(), e.getX(), e.getY());
		    				delPaneIndex=tabp.indexAtLocation(e.getX(), e.getY());
		    				return;
			    			}
			    		}
		    		
			    	}
			    }
			});
		
		
			jpanel.add(uppanel,BorderLayout.NORTH);
			jpanel.add(tabp,BorderLayout.CENTER);
			f.add(jpanel);
			f.pack();
			f.setSize(f.getPreferredSize());
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.show();


			
		
//		String test=config.getString("components.component(2).url");
//		List<Object> list=config.getList("components");
//		
//		System.out.println(list.size());
//
//		System.out.println(test);
//		config.setProperty("components.component(0).url", "1.1.1.1");
//		
//		config.save();
//		System.out.println(config.getString("components.component(0).url"));
//		// 192.23.44.100
//		config.getString("components.component(1).url");
//		// 192.23.44.100
//		//config.getString("components/component[name = 'production']/url");
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private JButton generateButton(XMLConfiguration conf,int index){
		JButton button=new JButton();
		return button;
		
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		init();
	


	}

}
