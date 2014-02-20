import java.io.File;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;


public class ConfigUtility {
	XMLConfiguration xmlcof;
	ConfigUtility(String xmlfpath){
		try {
			xmlcof=new XMLConfiguration(new File(xmlfpath));
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	ConfigUtility(){
		
	}

	String getIpByindex(int index){
		String str="components.component("+Integer.toString(index)+")."+ConstantConf.ip;
		String s=xmlcof.getString(str);
		return s;
	}
	String getPswByindex(int index){
		String str=ConstantConf.firstnode[0]+"."+ConstantConf.secondnode[0]+"("+Integer.toString(index)+")."+ConstantConf.password;
		String s=xmlcof.getString(str);
		return s;
	}
	String getUserByindex(int index){
		String str=ConstantConf.firstnode[0]+"."+ConstantConf.secondnode[0]+"("+Integer.toString(index)+")."+ConstantConf.username;
		String s=xmlcof.getString(str);
		return s;
	}
	public  XMLConfiguration getXmlcof() {
		return xmlcof;
	}
	public  void setXmlcof(XMLConfiguration xmlcof) {
		this.xmlcof = xmlcof;
	}

}
