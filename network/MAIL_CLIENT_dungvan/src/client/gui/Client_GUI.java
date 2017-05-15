/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.JobAttributes.DefaultSelectionType;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

import client.pop3.GetMailPOP3;

/**
 *
 * @author DungVan
 */
public class Client_GUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static String USER_EMAIL;
	private static String PASS_EMAIL;
	private static String CLIENT_TYPE;

	public static String getCLIENT_TYPE() {
		return CLIENT_TYPE;
	}

	public static void setCLIENT_TYPE(String cLIENT_TYPE) {
		CLIENT_TYPE = cLIENT_TYPE;
	}

	public static String getUSER_EMAIL() {
		return USER_EMAIL;
	}

	public static String getPASS_EMAIL() {
		return PASS_EMAIL;
	}

	public static void setPASS_EMAIL(String pASS_EMAIL) {
		PASS_EMAIL = pASS_EMAIL;
	}

	public static void setUSER_EMAIL(String uSER_EMAIL) {
		USER_EMAIL = uSER_EMAIL;
	}

	/**
	 * Creates new form Main_GUI
	 */
	public Client_GUI() {
		initComponents();
		this.getViewMailHeader();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */

	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		mailbody = new JTextArea();
		jScrollPane = new JScrollPane(mailbox);
		jScrollPane2 = new JScrollPane(listmail);
		close = new JLabel();
		minimize = new JLabel();
		logout = new JLabel();
		createmail = new JLabel();
		mailbox = new JLabel();
		moveFrame = new JLabel();
		background = new JLabel();
		listmail = new JList<String>();
		model = new DefaultListModel<String>();

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setUndecorated(true);
		setOpacity(0.9F);
		setResizable(false);
		setSize(new Dimension(300, 300));
		getContentPane().setLayout(new AbsoluteLayout());
		
		listmail.setModel(model);
		listmail.setForeground(new Color(255, 0, 0));
		listmail.setFont(new Font("Consolas", 0, 17));
		listmail.setBackground(new Color(203, 241, 241));
		listmail.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
		listmail.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
				viewMailSelectionListener(e);
			}
		});
		getContentPane().add(listmail, new AbsoluteConstraints(152, 29, 343, 148));

		mailbody.setText("");
		mailbody.setLineWrap(true);
		mailbody.setWrapStyleWord(true);
		mailbody.setBorder(null);
		mailbody.setEditable(false);
		mailbody.setBackground(null);
//		mailbody.setBackground(new Color(244, 241, 185));

		jScrollPane.setBorder(null);
		jScrollPane.setAutoscrolls(false);
		jScrollPane.getViewport().setView(mailbody);
		getContentPane().add(jScrollPane, new AbsoluteConstraints(152, 180, 343, 320));

		jScrollPane2.setBorder(null);
		jScrollPane2.setAutoscrolls(false);
		jScrollPane2.getViewport().setView(listmail);
		getContentPane().add(jScrollPane2, new AbsoluteConstraints(152, 29, 343, 148));

		close.setCursor(new Cursor(Cursor.HAND_CURSOR));
		close.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				closeMouseClicked(evt);
			}
		});
		getContentPane().add(close, new AbsoluteConstraints(470, 0, 30, 30));

		minimize.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				minimizeMouseClicked(evt);
			}
		});
		getContentPane().add(minimize, new AbsoluteConstraints(440, 0, 30, 30));

		logout.setText("logout");
		logout.setForeground(new Color(255, 255, 0));
		logout.setHorizontalAlignment(SwingConstants.CENTER);
		logout.setCursor(new Cursor(Cursor.HAND_CURSOR));
		logout.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				logoutMouseClicked(evt);
			}
		});
		getContentPane().add(logout, new AbsoluteConstraints(10, 160, 100, -1));

		createmail.setText("Create Mail");
		createmail.setForeground(new Color(255, 255, 0));
		createmail.setHorizontalAlignment(SwingConstants.CENTER);
		createmail.setCursor(new Cursor(Cursor.HAND_CURSOR));
		createmail.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				createmailMouseClicked(evt);
			}
		});
		getContentPane().add(createmail, new AbsoluteConstraints(10, 110, 100, -1));

		mailbox.setForeground(new Color(255, 255, 0));
		mailbox.setHorizontalAlignment(SwingConstants.CENTER);
		mailbox.setText("Mail Box");
		mailbox.setCursor(new Cursor(Cursor.HAND_CURSOR));
		mailbox.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				mailboxMouseClicked(evt);
			}
		});
		getContentPane().add(mailbox, new AbsoluteConstraints(10, 60, 100, -1));

		moveFrame.setForeground(new Color(204, 204, 0));
		moveFrame.setText("<html><b>  Main mail client</b></html>");
		moveFrame.setCursor(new Cursor(Cursor.MOVE_CURSOR));
		moveFrame.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				moveFrameMousePressed(evt);
			}
		});
		moveFrame.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				super.mouseDragged(e); // To change body of generated methods,
										// choose Tools | Templates.
				setLocation(e.getXOnScreen() - posX, e.getYOnScreen() - posY);
			}
		});
		getContentPane().add(moveFrame, new AbsoluteConstraints(0, 0, 435, 30));

		background.setIcon(new ImageIcon(getClass().getResource("/client/image/mainGUI.png"))); // NOI18N
		background.setVerticalAlignment(SwingConstants.TOP);
		getContentPane().add(background, new AbsoluteConstraints(0, 0, 500, 500));

		pack();
		setLocationRelativeTo(null);
	}// </editor-fold>

	protected void mailboxMouseClicked(MouseEvent evt) {
		// TODO Auto-generated method stub
		this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		GetMailPOP3 getMail = new GetMailPOP3();
		if (!getMail.getMail(getUSER_EMAIL(), getPASS_EMAIL()))
			JOptionPane.showMessageDialog(this, "server mailbox empty");
		else
			JOptionPane.showMessageDialog(this, "get mail success");
		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		this.getViewMailHeader();
		return;
	}

	protected void createmailMouseClicked(MouseEvent evt) {
		// TODO Auto-generated method stub
		Thread t = new Thread() {
			public void run() {
				new SendMail_GUI().setVisible(true);
			}
		};
		t.start();
	}

	protected void logoutMouseClicked(MouseEvent evt) {
		// TODO Auto-generated method stub
		new Login_GUI().setVisible(true);
		this.dispose();
	}

	private void closeMouseClicked(MouseEvent evt) {
		// TODO add your handling code here:
		System.exit(0);
	}

	private void minimizeMouseClicked(MouseEvent evt) {
		this.setState(Frame.ICONIFIED);
	}

	private void moveFrameMousePressed(MouseEvent evt) {
		// TODO add your handling code here:
		posX = evt.getX();
		posY = evt.getY();
	}

	private void viewMailSelectionListener(ListSelectionEvent e) {
		
		String s = listmail.getSelectedValue();
		File file = new File("db/" + Client_GUI.getUSER_EMAIL().split("@")[0] + "/" + s + ".email");
		if(file.exists()){
		try {
			BufferedReader _in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			String line = "", content = "";
			while((line = _in.readLine())!=null){
				content += line + "\n";
			}
			mailbody.setText(content);
		} catch (UnsupportedEncodingException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} catch (FileNotFoundException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		}
	}

	private void getViewMailHeader() {

		if (!model.isEmpty())
			model.removeAllElements();
		File folder = new File("db/" + getUSER_EMAIL().split("@")[0]);
		folder.mkdirs();
		File[] listFiles = folder.listFiles();
		for (File item : listFiles) {
			model.addElement(item.getName().split(".email")[0]);
		}
	}

	// Variables declaration - do not modify
	private int posX, posY;
	private JList<String> listmail;
	private DefaultListModel<String> model;
	private JScrollPane jScrollPane2;
	private JLabel background;
	private JLabel moveFrame;
	private JLabel mailbox;
	private JLabel createmail;
	private JLabel logout;
	private JLabel minimize;
	private JLabel close;
	private JTextArea mailbody;
	private JScrollPane jScrollPane;
	// End of variables declaration
}
