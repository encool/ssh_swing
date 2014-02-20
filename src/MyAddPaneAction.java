import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTabbedPane;

import org.apache.commons.configuration.XMLConfiguration;


public class MyAddPaneAction extends AbstractAction {

	XMLConfiguration config;
	int index;
	ConfigUtility confutility;
	JTabbedPane tabp;
	int clickedindex=0;
	public MyAddPaneAction(){
		super();
	}
	public MyAddPaneAction(String s,XMLConfiguration conf,int index,JTabbedPane tabp){
		super(s);
		this.config=conf;
		this.index=index;
		this.tabp=tabp;
		confutility=new ConfigUtility();
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(!isContainTab(tabp,(String) this.getValue(NAME))){
			tabp.add((String) this.getValue(NAME),new UnitPanel(config, index));
			
		}
		tabp.setSelectedIndex(clickedindex);
		

	}
	public boolean isContainTab(JTabbedPane tb,String s){
		for(int i=0;i<tb.getTabCount();i++){
			if(tb.getTitleAt(i).equals(s)){
				clickedindex=i;
				return true;
			}
		}
		return false;
		
	}
}


