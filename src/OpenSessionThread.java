import ch.ethz.ssh2.Session;


public class OpenSessionThread extends Thread {
	SessionPool sp;
	private String ip;
	private String psw;
	private String user;
	private Session ss=null;
	private UnitPanel up;
	{
		sp=SessionPool.getSessionPool();
	}
	OpenSessionThread(String ip,String user,String psw,UnitPanel up){
		this.up=up;
		this.ip=ip;
		this.user=user;
		this.psw=psw;
	}
	public void run(){
		ss=sp.getSesByIP(ip, user, psw);
		notifyPanel();
	}
	private void notifyPanel() {
		// TODO Auto-generated method stub
		up.notified(!(ss==null));
	}
	public Session getSs() {
		return ss;
	}
	

}
