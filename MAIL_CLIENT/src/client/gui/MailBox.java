/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import client.pop3.GetMailPOP3;

public class MailBox extends javax.swing.JFrame implements ActionListener {

	private static String USER_EMAIL;
	private static String PASS_EMAIL;
	JButton TDN , logout, send;
	JTextArea ta;
	JPanel pn, pn1, pn2;

	public static String getUSER_EMAIL() {
		return USER_EMAIL;
	}
	public static void setUSER_EMAIL(String user) {
		USER_EMAIL = user;
	}
	public MailBox(String user,String pass) {
		this.USER_EMAIL = user;
		this.PASS_EMAIL=pass;
		setTitle("YOUR MAILBOX");
		setLocation(500, 200);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		TDN = new JButton("Thư đã nhận");
		logout = new JButton("Đăng xuất");
		send = new JButton("Send");
		send.setPreferredSize(new Dimension(100,30));
		logout.setPreferredSize(new Dimension(100,30));
		TDN.setPreferredSize(new Dimension(100,30));
		TDN.addActionListener(this);
		logout.addActionListener(this);
		send.addActionListener(this);
		pn2 = new JPanel(new FlowLayout());
		pn2.setBackground(Color.WHITE);
		Border loweredBevel = BorderFactory.createLoweredBevelBorder();
		pn2.setBorder(loweredBevel);
		pn1 = new JPanel()
		 {
			public void paintComponent(Graphics g) {
				ImageIcon icon = new ImageIcon("image/nen3.jpg");
				Dimension d = getSize();
				g.drawImage(icon.getImage(), 0, 0, d.width, d.height, null);
				setOpaque(false);
				super.paintComponent(g);
			}

		};
		pn1.setLayout(new FlowLayout());
		pn1.add(send);
		pn1.add(TDN);
		pn1.add(logout);
		ta=new JTextArea(6,15);
		ta.setBorder(BorderFactory.createLineBorder(Color.RED));
		pn1.add(ta)
;		pn = new JPanel(new GridLayout(1,2));
		pn.add(pn1);
		pn.add(pn2);
		add(pn);
		setSize(400, 300);

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		try {
			if (e.getSource() == send) {
				Thread t = new Thread() {
					public void run() {
						new SendMail_GUI(USER_EMAIL).setVisible(true);
					}
				};
				t.start();
			}
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null, "error");
		}
		if (e.getSource() == TDN) {
			try {
				GetMailPOP3 pop3 = new GetMailPOP3();
				pop3.connect("localhost", 110);
				pop3.command(USER_EMAIL, PASS_EMAIL);
				ArrayList<String> allMail= pop3.getAllMail(USER_EMAIL);
				for(int i=0;i<allMail.size();i++)
				{
					System.out.println(allMail.get(i));
					JLabel lb=new JLabel(allMail.get(i));
					lb.addMouseListener(new MouseListener() {
						@Override
						public void mouseReleased(MouseEvent e) {}
						@Override
						public void mousePressed(MouseEvent e) {}
						@Override
						public void mouseExited(MouseEvent e) {}
						@Override
						public void mouseEntered(MouseEvent e) {}
						@Override
						public void mouseClicked(MouseEvent e) {
							String nameMail=lb.getText().toString();
							File file = new File("Mailbox/" + USER_EMAIL.split("@")[0].trim());
							file.mkdir();
							if (file.listFiles() == null)
								return ;
							else {
								String contentTa="";
								for (File f : file.listFiles())
									if(f.getName().equals(nameMail)){
										try {
											FileInputStream fis = new FileInputStream(f.getPath());
											DataInputStream dis = new DataInputStream(fis);
											// Đọc dữ liệu
												String line;
												while ((line = dis.readLine()) != null) {
													 contentTa+= line + "\n";
												}
												dis.close();
												ta.setText(contentTa);
										} catch (Exception ex) {
											Logger.getLogger(MailBox.class.getName()).log(Level.SEVERE, null, ex);
											return ;
										}
									}
							}
						}
					});
					pn2.add(lb);
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if (e.getSource() == logout)
			System.exit(0);
	}
}
