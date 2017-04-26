package mail;

import java.io.Serializable;

public class Mail implements Serializable{

	private String date;
	private String from;
	private String to;
	private String subject;
	private String content;
	
	public Mail(String date, String from, String to, String subject, String content) {
		this.date = date;
		this.from = from.replaceFirst("from : ", "");
		this.to = to.replaceFirst("to : ", "");
		this.subject = subject.replaceFirst("Subject : ", "");
		this.content = content;
	}

	public String getDate() {
		return date;
	}

	public String getFrom() {
		return from;
	}

	public String getTo() {
		return to;
	}

	public String getSubject() {
		return subject;
	}

	public String getContent() {
		return content;
	}
}
