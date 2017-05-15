/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import client.smtp.SendMailSMTP;

/**
 *
 * @author DungVan
 */
public class SendMail_GUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates new form SendMail
	 */
	public SendMail_GUI() {
		initComponents();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */

	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		tf_mailTo = new JTextField();
		tf_subject = new JTextField();
		jScrollPane = new JScrollPane();
		mailbody = new JTextArea();
		bt_send = new JButton();
		bt_cancel = new JButton();
		background = new JLabel();

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setUndecorated(true);
		setOpacity(0.8F);
		setResizable(false);
		getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

		tf_mailTo.setBackground(new Color(255, 0, 255));
		tf_mailTo.setForeground(new Color(255, 255, 255));
		tf_mailTo.setFont(new Font("Tahoma", 1, 14));
		tf_mailTo.setBorder(null);
		getContentPane().add(tf_mailTo, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 35, 310, 28));

		tf_subject.setBackground(new Color(255, 0, 255));
		tf_subject.setForeground(new Color(255, 255, 255));
		tf_subject.setFont(new Font("Tahoma", 1, 14));
		tf_subject.setBorder(null);
		getContentPane().add(tf_subject, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 89, 310, 28));

		jScrollPane.setBorder(null);

		mailbody.setBackground(new Color(255, 0, 255));
		mailbody.setForeground(new Color(255, 255, 255));
		mailbody.setLineWrap(true);
		mailbody.setWrapStyleWord(true);
		mailbody.setBorder(null);
		jScrollPane.setViewportView(mailbody);

		getContentPane().add(jScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 145, 378, 240));

		bt_send.setIcon(new ImageIcon(getClass().getResource("/client/image/sendmail/send.png"))); // NOI18N
		bt_send.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				bt_sendMouseClicked(evt);
			}
		});
		getContentPane().add(bt_send, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 400, 70, 23));

		bt_cancel.setIcon(new ImageIcon(getClass().getResource("/client/image/sendmail/cancel_2.png"))); // NOI18N
		bt_cancel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				bt_cancelMouseClicked(evt);
			}
		});
		getContentPane().add(bt_cancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(235, 400, 70, 23));

		background.setIcon(new ImageIcon(getClass().getResource("/client/image/sendmail/sendmail.png"))); // NOI18N
		background.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent evt) {
				backgroundMouseDragged(evt);
			}
		});
		background.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				backgroundMousePressed(evt);
			}
		});
		getContentPane().add(background, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

		pack();
		setLocationRelativeTo(null);
	}// </editor-fold>

	private void bt_sendMouseClicked(MouseEvent evt) {
		/*
		 * call send email here!
		 */
		JFrame frame = this;
		Thread t = new Thread() {
			public void run() {
				try {
					SendMailSMTP sendmail = new SendMailSMTP();
					if (sendmail.sendMail(Client_GUI.getUSER_EMAIL(), tf_mailTo.getText().trim(),
							tf_subject.getText().trim(), mailbody.getText().trim())) {
						tf_mailTo.setText("");
						tf_subject.setText("");
						mailbody.setText("");
						JOptionPane.showMessageDialog(frame, "Mail sent!");
						return;
					} else {
						JOptionPane.showMessageDialog(frame, "Send mail error!\nPlease try again later");
						return;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		t.start();
	}

	private void bt_cancelMouseClicked(MouseEvent evt) {
		this.dispose();
	}

	private void backgroundMousePressed(MouseEvent evt) {
		posX = evt.getX();
		posY = evt.getY();
	}

	private void backgroundMouseDragged(MouseEvent evt) {
		setLocation(evt.getXOnScreen() - posX, evt.getYOnScreen() - posY);
	}

	// Variables declaration - do not modify
	private int posX, posY;
	private JLabel background;
	private JButton bt_cancel;
	private JButton bt_send;
	private JScrollPane jScrollPane;
	private JTextArea mailbody;
	private JTextField tf_mailTo;
	private JTextField tf_subject;
	// End of variables declaration
}
