package dataserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import mail.Mail;

public class MailServerDataOption {
	public static boolean saveEmail(String receiverName, String senderName, String data) {

		String folderName = receiverName.split("@")[0].trim();
		File receiverFolder = new File("db/" + folderName);
		receiverFolder.mkdir();

		String subject = "";
		int indexSubject_start;
		if ((indexSubject_start = data.toLowerCase().lastIndexOf("subject : ")) >= 0) {
			int indexSubject_end;
			indexSubject_end = data.substring(indexSubject_start, data.length() - 1).indexOf("\n");
			subject = data.substring(indexSubject_start, indexSubject_end).toLowerCase().replaceFirst("subject : ", "")
					.replace(':', '_').replace('\\', '_').replace('/', '_').replace('*', '_').replace('|', '_')
					.replace('>', '_').replace('<', '_').replace('?', '_');
			// file name can't contain \/:*?<>|
		}
		int count = 0;
		for (File file : receiverFolder.listFiles()) {
			if (file.getName().equals(subject + "-" + senderName + ".email")) {
				count++;
			}
		}

		File emailFile = new File("db/" + folderName + "/" + subject + "-" + senderName + ""
				+ (count == 0 ? "" : ("_" + count)) + ".email");

		Date current = new Date();

		String pattern = "EEE, dd MMM yyyy HH:mm:ss Z";
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		String dateStr = format.format(current);

		String writeToFile = dateStr + "\nfrom : " + senderName + "\nto : " + receiverName + "\n" + data;
		FileOutputStream output;
		try {
			output = new FileOutputStream(emailFile);
			output.write(writeToFile.getBytes("UTF-8"));
			output.flush();
			output.close();
			return true;
		} catch (Exception ex) {
			Logger.getLogger(MailServerDataOption.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		}
	}

	public static Mail readMail(File file) {
		String date = "", from = "", to = "", subject = "no subject", content = "";
		try {
			BufferedReader f_in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			date = f_in.readLine();
			from = f_in.readLine().replaceFirst("from : ", "");
			to = f_in.readLine().replaceFirst("to : ", "");
			subject = f_in.readLine().replaceFirst("Subject : ", "");
			String line = "";
			while ((line = f_in.readLine()) != null) {
				content += line + "\n";
			}
			f_in.close();

			return new Mail(date, from, to, subject, content);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String readString(File file) {
		String mail = "";
		try {
			BufferedReader f_in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line = null;
			while ((line = f_in.readLine()) != null) {
				mail += line + "\n";
			}
			f_in.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mail;
	}

	public static double getBytes(File file) {
		double fileSize = 0;
		if (file.exists()) {
			if (file.isDirectory()) {
				File[] listFile = file.listFiles();
				for (File item : listFile) {
					fileSize += item.length();
				}
			} else {
				fileSize = file.length();
			}
		}
		return fileSize;
	}

	public static void deleteMail(int[] delete, File folder) {
		File[] listFiles = folder.listFiles();
		int i = 0;
		while (i < delete.length) {
			System.out.println("delete[" + i + "] : " + delete[i]);
			if (delete[i] == 0) {
				i++;
				continue;
			}
			listFiles[delete[i] - 1].delete();
			i++;
		}
	}
}
