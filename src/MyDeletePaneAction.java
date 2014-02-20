import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTabbedPane;


public class MyDeletePaneAction extends AbstractAction {
	JTabbedPane tabp;
	public MyDeletePaneAction(){
		super();
	}
	public MyDeletePaneAction(String s,JTabbedPane tabp){
		super(s);
		this.tabp=tabp;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		tabp.remove(app.delPaneIndex);

	}

}
