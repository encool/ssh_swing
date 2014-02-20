import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Robot;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;


import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.InteractiveCallback;
import ch.ethz.ssh2.KnownHosts;
import ch.ethz.ssh2.ServerHostKeyVerifier;
import ch.ethz.ssh2.Session;


public class SessionPool {
	static final String idDSAPath = "~/.ssh/id_dsa";
	static final String idRSAPath = "~/.ssh/id_rsa";
	HashMap sessionpool=new HashMap<String, Session>();
	Connection con=new Connection("192.168.128.132");
	KnownHosts database = new KnownHosts();
	static final String knownHostPath = "./ssh/known_hosts";
	//File file=new File(knownHostPath);
	static SessionPool poolinst;
	static SessionPool getSessionPool(){
		if(poolinst==null){
			poolinst=new SessionPool();
		}
		return poolinst;
	}
	void test(){
		try {
			con.connect();
			String[] ss=con.getRemainingAuthMethods("root");
			for(int i=0;i<ss.length;i++){
				System.out.println(ss[i]);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	synchronized Session getSesByIP(String ip,String user,String password){
		Session s =null;
		if(!sessionpool.containsKey(ip)){
			Connection conn=setConnection(ip,22);
			if(conn!=null){
				try {
					s=doAuthor(conn,user,password);
					sessionpool.put(ip, s);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println(e.toString());
				}
			}
		}else{
			s=(Session) sessionpool.get(ip);
		}
		return s;
		
	}
	Connection setConnection(String ip,int port){
		Connection con=new Connection(ip,port);
		try {
			con.connect(new AdvancedVerifier());
		} catch (IOException e) {
			// TODO Auto-generated catch block  连接不上异常处理要补充
			e.printStackTrace();
//			JDialog dialog=new JDialog(app.f, e.getMessage());
//			dialog.setVisible(true);
			return null;
		}
		return con;
		
	}
	Session doAuthor(Connection con,String user,String psw) throws IOException{
		try
		{
			boolean enableKeyboardInteractive = true;
			boolean enableDSA = true;
			boolean enableRSA = true;
	
			String lastError = null;
	
			while (true)
			{
				if ((enableDSA || enableRSA) && con.isAuthMethodAvailable(user, "publickey"))
				{
					if (enableDSA)
					{
						File key = new File(idDSAPath);
	
						if (key.exists())
						{
							EnterSomethingDialog esd = new EnterSomethingDialog(app.f, "DSA Authentication",
									new String[] { lastError, "Enter DSA private key password:" }, true);
							esd.setVisible(true);
	
							boolean res = con.authenticateWithPublicKey(user, key, esd.answer);
	
							if (res == true)
								break;
	
							lastError = "DSA authentication failed.";
						}
						enableDSA = false; // do not try again
					}
	
					if (enableRSA)
					{
						File key = new File(idRSAPath);
	
						if (key.exists())
						{
							EnterSomethingDialog esd = new EnterSomethingDialog(app.f, "RSA Authentication",
									new String[] { lastError, "Enter RSA private key password:" }, true);
							esd.setVisible(true);
	
							boolean res = con.authenticateWithPublicKey(user, key, esd.answer);
	
							if (res == true)
								break;
	
							lastError = "RSA authentication failed.";
						}
						enableRSA = false; // do not try again
					}
	
					continue;
				}
	
				if (enableKeyboardInteractive && con.isAuthMethodAvailable(user, "keyboard-interactive"))
				{
					InteractiveLogic il = new InteractiveLogic(lastError,psw);
	
					boolean res = con.authenticateWithKeyboardInteractive(user, il);
	
					if (res == true)
						break;
	
					if (il.getPromptCount() == 0)
					{
						// aha. the server announced that it supports "keyboard-interactive", but when
						// we asked for it, it just denied the request without sending us any prompt.
						// That happens with some server versions/configurations.
						// We just disable the "keyboard-interactive" method and notify the user.
	
						lastError = "Keyboard-interactive does not work.";
	
						enableKeyboardInteractive = false; // do not try this again
					}
					else
					{
						lastError = "Keyboard-interactive auth failed."; // try again, if possible
					}
	
					continue;
				}
	
				if (con.isAuthMethodAvailable(user, "password"))
				{
					final EnterSomethingDialog esd = new EnterSomethingDialog(app.f,
							"Password Authentication",
							new String[] { lastError, "Enter password for " + user }, true);
	
					esd.setVisible(true);
	
					if (esd.answer == null)
						throw new IOException("Login aborted by user");
	
					boolean res = con.authenticateWithPassword(user, esd.answer);
	
					if (res == true)
						break;
	
					lastError = "Password authentication failed."; // try again, if possible
	
					continue;
				}
	
				throw new IOException("No supported authentication methods available.");
			}
		}
		catch (IOException e)
		{
			//e.printStackTrace();
			JOptionPane.showMessageDialog(app.f, "Exception: " + e.getMessage());
		}
		return con.openSession();	

	}
	class AdvancedVerifier implements ServerHostKeyVerifier
	{
		public boolean verifyServerHostKey(String hostname, int port, String serverHostKeyAlgorithm,
				byte[] serverHostKey) throws Exception
		{
			final String host = hostname;
			final String algo = serverHostKeyAlgorithm;

			String message;

			/* Check database */

			int result = database.verifyHostkey(hostname, serverHostKeyAlgorithm, serverHostKey);

			switch (result)
			{
			case KnownHosts.HOSTKEY_IS_OK:
				return true;

			case KnownHosts.HOSTKEY_IS_NEW:
				message = "Do you want to accept the hostkey (type " + algo + ") from " + host + " ?\n";
				break;

			case KnownHosts.HOSTKEY_HAS_CHANGED:
				message = "WARNING! Hostkey for " + host + " has changed!\nAccept anyway?\n";
				break;

			default:
				throw new IllegalStateException();
			}

			/* Include the fingerprints in the message */

			String hexFingerprint = KnownHosts.createHexFingerprint(serverHostKeyAlgorithm, serverHostKey);
			String bubblebabbleFingerprint = KnownHosts.createBubblebabbleFingerprint(serverHostKeyAlgorithm,
					serverHostKey);

			message += "Hex Fingerprint: " + hexFingerprint + "\nBubblebabble Fingerprint: " + bubblebabbleFingerprint;

			/* Now ask the user */

			int choice = JOptionPane.showConfirmDialog(app.f, message);

			if (choice == JOptionPane.YES_OPTION)
			{
				/* Be really paranoid. We use a hashed hostname entry */

				String hashedHostname = KnownHosts.createHashedHostname(hostname);

				/* Add the hostkey to the in-memory database */

				database.addHostkey(new String[] { hashedHostname }, serverHostKeyAlgorithm, serverHostKey);

				/* Also try to add the key to a known_host file */

				try
				{
					KnownHosts.addHostkeyToFile(new File(knownHostPath), new String[] { hashedHostname },
							serverHostKeyAlgorithm, serverHostKey);
				}
				catch (IOException ignore)
				{
					ignore.printStackTrace();
				}

				return true;
			}

			if (choice == JOptionPane.CANCEL_OPTION)
			{
				throw new Exception("The user aborted the server hostkey verification.");
			}

			return false;
		}
	}
	class EnterSomethingDialog extends JDialog
	{
		private static final long serialVersionUID = 1L;

		JTextField answerField;
		JPasswordField passwordField;

		final boolean isPassword;

		String answer;

		public EnterSomethingDialog(JFrame parent, String title, String content, boolean isPassword)
		{
			this(parent, title, new String[] { content }, isPassword);
		}

		public EnterSomethingDialog(JFrame parent, String title, String[] content, boolean isPassword)
		{
			super(parent, title, true);

			this.isPassword = isPassword;

			JPanel pan = new JPanel();
			pan.setLayout(new BoxLayout(pan, BoxLayout.Y_AXIS));

			for (int i = 0; i < content.length; i++)
			{
				if ((content[i] == null) || (content[i] == ""))
					continue;
				JLabel contentLabel = new JLabel(content[i]);
				pan.add(contentLabel);

			}

			answerField = new JTextField(20);
			passwordField = new JPasswordField(20);

			if (isPassword)
				pan.add(passwordField);
			else
				pan.add(answerField);

			KeyAdapter kl = new KeyAdapter()
			{
				public void keyTyped(KeyEvent e)
				{
					if (e.getKeyChar() == '\n')
						finish();
				}
			};

			answerField.addKeyListener(kl);
			passwordField.addKeyListener(kl);

			getContentPane().add(BorderLayout.CENTER, pan);

			setResizable(false);
			pack();
			setLocationRelativeTo(null);
		}

		private void finish()
		{
			if (isPassword)
				answer = new String(passwordField.getPassword());
			else
				answer = answerField.getText();

			dispose();
		}
	}
	class InteractiveLogic implements InteractiveCallback
	{
		int promptCount = 0;
		String lastError;
		//最简单的keyboard-interactive 情形 只需输入一次密码，自动读取密码免人工输入
		String psw;

		public InteractiveLogic(String lastError)
		{
			this.lastError = lastError;
		}

		public InteractiveLogic(String lastError2, String psw) {
			// TODO Auto-generated constructor stub
			this.lastError = lastError2;
			this.psw=psw;
		}

		/* the callback may be invoked several times, depending on how many questions-sets the server sends */

		public String[] replyToChallenge(String name, String instruction, int numPrompts, String[] prompt,
				boolean[] echo) throws IOException
		{
			String[] result = new String[numPrompts];
			
			/*仅仅考虑只需要一步password验证的情况*/
			if(numPrompts==1&&prompt[0].equals(new String("Password: "))){
				result[0]=psw;
				promptCount++;
				return result;
				
			}
			return result;
//			for (int i = 0; i < numPrompts; i++)
//			{
//				/* Often, servers just send empty strings for "name" and "instruction" */
//				String[] content = new String[] { lastError, name, instruction, prompt[i] };
//
//				if (lastError != null)
//				{
//					/* show lastError only once */
//					lastError = null;
//				}
//
//				EnterSomethingDialog esd = new EnterSomethingDialog(app.f, "Keyboard Interactive Authentication",
//						content, !echo[i]);
//
//				esd.setVisible(true);
//
//				if (esd.answer == null)
//					throw new IOException("Login aborted by user");
//
//				result[i] = esd.answer;
//				promptCount++;
//			}

			
	
//	            return result;
				
		}

		/* We maintain a prompt counter - this enables the detection of situations where the ssh
		 * server is signaling "authentication failed" even though it did not send a single prompt.
		 */

		public int getPromptCount()
		{
			return promptCount;
		}
	}

}
