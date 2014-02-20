import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import ch.ethz.ssh2.Session;


public class TerminalHandle{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JTextArea textarea;
	
	Session sess;
	InputStream in;
	OutputStream out;
	
	int x, y;
	
	FontMetrics fm;
	Font font=new Font("Calibri",Font.PLAIN,16);
	
	KeyAdapter kl = new KeyAdapter()
	{	
		public void keyPressed(KeyEvent e){
			int vk=e.getKeyCode();
			//System.out.println(vk);
			if(vk==38){    						//上箭头扫描码27 91 65
				try
				{
					byte[] bytes=new byte[3];
					bytes[0]=new Byte((byte) 27);
					bytes[1]=new Byte((byte) 91);
					bytes[2]=new Byte((byte) 65);
					out.write(bytes);
				
				}
				catch (IOException e1)
				{
				}
				e.consume();
			}
			if(vk==37){    						//上箭头扫描码27 91 65
				try
				{
					byte[] bytes=new byte[3];
					bytes[0]=new Byte((byte) 27);
					bytes[1]=new Byte((byte) 91);
					bytes[2]=new Byte((byte) 64);
					out.write(bytes);
				
				}
				catch (IOException e1)
				{
				}
				e.consume();
			}
			if(vk==39){    						//上箭头扫描码27 91 65
				try
				{
					byte[] bytes=new byte[3];
					bytes[0]=new Byte((byte) 27);
					bytes[1]=new Byte((byte) 91);
					bytes[2]=new Byte((byte) 66);
					out.write(bytes);
				
				}
				catch (IOException e1)
				{
				}
				e.consume();
			}
			if(vk==40){    						//上箭头扫描码27 91 65
				try
				{
					byte[] bytes=new byte[3];
					bytes[0]=new Byte((byte) 27);
					bytes[1]=new Byte((byte) 91);
					bytes[2]=new Byte((byte) 67);
					out.write(bytes);
				
				}
				catch (IOException e1)
				{
				}
				e.consume();
			}
	
		}
		public void keyTyped(KeyEvent e)
		{
			int c = e.getKeyChar();
			System.out.println(c);
			try
			{
				out.write(c);
			}
			catch (IOException e1)
			{
			}
			e.consume();
		}
	};
	TerminalHandle(JTextArea textarea,Session sess,int x,int y){
		super();
		this.textarea=textarea;
		this.textarea.setFont(font);
		this.textarea.setLineWrap(true);
		this.sess=sess;
		KeyStroke enter = KeyStroke.getKeyStroke("ENTER");
		textarea.getInputMap().put(enter, "none");
		KeyStroke tab = KeyStroke.getKeyStroke("TAB");
		textarea.getInputMap().put(tab, "none");
		try {

			int x_width = 90;
			int y_width = 30;

			sess.requestPTY("dumb");
			sess.startShell();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.out=sess.getStdin();
		this.in=sess.getStdout();
		this.x=x;
		this.y=y;
		textarea.setEditable(true);	
		fm=textarea.getFontMetrics(font);
		new RemoteConsumer().start();
		textarea.requestFocus();
		textarea.addKeyListener(kl);	
	}
	
	public TerminalHandle() {
		// TODO Auto-generated constructor stub
		super();
	}
	
	
	class RemoteConsumer extends Thread{

		int posy = 0;
		char[][] lines = new char[y][200];
		int posx = 0;
		
		int posx_pix=0;
		private int caretposit=0;

		private void addText(byte[] data, int len)
		{
			for (int i = 0; i < len; i++)
			{	
				//textarea.setColumns(posx+1);
				//textarea.setRows(posy+1);
				//lines
				char c = (char) (data[i] & 0xff);

				if (c == 8) // Backspace, VERASE
				{
					if (posx < 0)
						continue;
					posx--;
					posx_pix=posx_pix-fm.charWidth(c);
					continue;
				}

				if (c == '\r')  //回车(CR) ，将当前位置移到本行开头  ascci=13
				{
					posx = 0;
					posx_pix=0;
					continue;
				}

				if (c == '\n')
				{
					posy++;
					posx_pix=0;
					if (posy >= y)
					{
						for (int k = 1; k < y; k++)
							lines[k - 1] = lines[k];
						posy--;
						lines[y - 1] = new char[200];
						for (int k = 0; k < x; k++)
							lines[y - 1][k] = ' ';
					}
					continue;
				}

				if (c < 32)
				{
					continue;
				}

				//if (posx >= x)
				if (posx_pix>textarea.getWidth()-5)
				{	
//					System.out.println(textarea.getWidth());
//					System.out.println(posx_pix);
					posx_pix=0;
					posx = 0;
					posy++;
					if (posy >= y)
					{
						posy--;
						for (int k = 1; k < y; k++)
							lines[k - 1] = lines[k];
						lines[y - 1] = new char[200];
						for (int k = 0; k < x; k++)
							lines[y - 1][k] = ' ';
					}
				}

				if (lines[posy] == null)
				{
					lines[posy] = new char[200];
					for (int k = 0; k < x; k++)
						lines[posy][k] = ' ';
				}
				
				lines[posy][posx] = c;
				caretposit+=fm.charWidth(c);
				
//				System.out.println(textarea.getWidth());
//				System.out.println(posx_pix);
				posx_pix=posx_pix+fm.charWidth(c);	//System.out.print(c);
				posx++;
			}

			StringBuffer sb = new StringBuffer(x * y);

			for (int i = 0; i < lines.length; i++)
			{
				if (i != 0)
					sb.append('\n');

				if (lines[i] != null)
				{
					sb.append(lines[i]);
				}

			}
//			textarea.setCaretPosition(x*y);
			setContent(sb.toString());textarea.setCaretPosition(caretposit);System.out.println(caretposit);
		}

		public void run()
		{
			byte[] buff = new byte[8192];

			try
			{
				while (true)
				{
					int len = in.read(buff);
					if (len == -1)
						return;
					addText(buff, len);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	
	}
	public void setContent(String lines)
	{
		// setText is thread safe, it does not have to be called from
		// the Swing GUI thread.
		textarea.setText(lines);

	}
	

}
