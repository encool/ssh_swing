import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import org.apache.commons.configuration.XMLConfiguration;


public class MyActionListener implements ActionListener {
	XMLConfiguration conf;
	int index;
	JButton button;
	public MyActionListener(){
		super();
	}
	public MyActionListener(XMLConfiguration config, int index2,
			JButton button) {
		// TODO Auto-generated constructor stub
		super();
		this.conf=config;
		this.index=index2;
		this.button=button;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		button.setText(conf.getString(("components.component(0).url")));
	}

}
