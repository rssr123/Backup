package com.maven.rms.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maven.rms.exceptionhandler.ApplicationException;
import com.maven.rms.models.Email;
import com.maven.rms.repositories.EmailRepository;
import com.maven.rms.utils.ErrorCode;

@Service
@Slf4j
public class EmailService {
	// private static final Logger logger =
	// LoggerFactory.getLogger(EmailService.class);

	private final EmailRepository emailRepo;

	@Autowired
	private JavaMailSender javaMailSender;

	@Value("${spring.mail.username}")
	private String from;
	@Value("${email.attachments.directory}") 
	private String emailDir;
	@Value("${jasper.rcpt.directory}") 
	private String rcptDir;
	
	public EmailService(EmailRepository emailRepo) {
		this.emailRepo = emailRepo;
	}

	// public void sendMailWithAttachment(EmailWithAttachment attachment, Boolean
	// isHTML) throws Exception{
	// Email email = attachment.getEmail();
	// File file = File.createTempFile(attachment.getFileNm(), "." +
	// attachment.getFileType());
	// FileOutputStream fos = new FileOutputStream(file);
	// fos.write(attachment.getFileContent());
	// fos.close();
	// //Vicky 20240716: harccode isHTML to true for attachment
	// //isHTML = true;

	// if(file.equals(null)) {
	// log.error("Detected that file obj passed is null at: " +
	// this.getClass().toString());
	// throw new NullArgumentException("File obj is null!!");
	// }

	// MimeMessagePreparator preparator = new MimeMessagePreparator(){
	// public void prepare(MimeMessage mimeMessage) throws Exception{
	// mimeMessage.setRecipient(Message.RecipientType.TO, new
	// InternetAddress(email.getTo()));
	// mimeMessage.setFrom(new InternetAddress(from));
	// mimeMessage.setSubject(email.getSubject());
	// MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
	// helper.addAttachment(file.getName(), file);
	// //Vicky 20240716: harccode isHTML to true for attachment
	// helper.setText(email.getBody(), true);
	// if(!email.getBcc().isEmpty())
	// helper.setBcc(email.getBcc().split(";"));
	// if(!email.getCc().isEmpty())
	// helper.setCc(email.getCc().split(";"));
	// }};

	// javaMailSender.send(preparator);
	// }

	public void sendMailWithAttachment(Email email, Boolean isHTML) throws ApplicationException {
		/*Email email = attachment.getEmail();

		// Declare the file variable as final array to ensure it's effectively final
		final File[] file = { null };
		try {
			try {
				file[0] = File.createTempFile(attachment.getFileNm(), "." + attachment.getFileType());
				try (FileOutputStream fos = new FileOutputStream(file[0])) {
					fos.write(attachment.getFileContent());
				}
			} catch (IOException ioEx) {
				throw new ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR,
						"Failed to create/write temp attachment file: " + ioEx.getMessage());
			}
		 */

		try {
			final File attachment = new File(email.getAttachmentPath());
			// Hardcode isHTML to true for attachments as per the comment
			isHTML = true;

			MimeMessagePreparator preparator = new MimeMessagePreparator() {
				public void prepare(MimeMessage mimeMessage) throws Exception {
					mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(email.getTo()));
					mimeMessage.setFrom(new InternetAddress(from));
					mimeMessage.setSubject(email.getSubject());
					MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
					helper.addAttachment(attachment.getName(), attachment);
					helper.setText(email.getBody(), true);
					if (email.getBcc() != null && !email.getBcc().isEmpty()) {
						helper.setBcc(email.getBcc().split(";"));
					}
					if (email.getCc() != null && !email.getCc().isEmpty()) {
						helper.setCc(email.getCc().split(";"));
					}
				}
			};

			EmailServerErrorHandling(email, preparator);

		} finally {
			/*if (file[0] != null && file[0].exists()) {
				file[0].delete(); // Ensure the temp file is deleted after sending the email
			}*/
		}
	}

	public void sendSampleEmailWithAttachment() throws Exception {

		byte[] fileContent = "This is the content of the attached file.".getBytes();
		// Convert byte array to a File object
		File tempFile = convertByteArrayToFile(fileContent, "sampleAttachment", "txt");

		Email email = new Email("Receipt", "wewong@persys-tech.com", "", "",
				"PAYMENT SUCCESSFUL - RECEIPT ATTACHED", "This is a sample email with an attachment.", tempFile);
		//EmailWithAttachment emailWithAttachment = new EmailWithAttachment(email, tempFile);

		// save and send email
		email = saveEmailDets(email);
		//emailWithAttachment = saveEmailWithAttDets(emailWithAttachment);
		Boolean emailSent = false;

		//sendMailWithAttachment(emailWithAttachment, true);
		sendMailWithAttachment(email, true);
		emailSent = true;
	}

	private File convertByteArrayToFile(byte[] fileContent, String fileName, String fileExtension) throws IOException {
		// Create a temporary file
		//File tempFile = File.createTempFile(fileName, "." + fileExtension);
		File tempFile = new File(System.getProperty("java.io.tmpdir"), fileName + "." + fileExtension);

		// Write the byte array content to the file
		try (FileOutputStream fos = new FileOutputStream(tempFile)) {
			fos.write(fileContent);
		}

		return tempFile; // Return the created file
	}

	public void sendMail(Email email) throws ApplicationException, Exception {
		if(email.getAttachmentPath() != null || !email.getAttachmentPath().isEmpty())
			sendMailWithAttachment(email, true);
		else {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(email.getTo());
			message.setFrom(from);
			message.setSubject(email.getSubject());
			message.setText(email.getBody());
	
			if (email.getCc() != null && !email.getCc().isEmpty())
				message.setCc(email.getCc().split(";"));
			if (email.getBcc() != null && !email.getBcc().isEmpty())
				message.setBcc(email.getBcc().split(";"));
	
			EmailServerErrorHandling(email, message);
		}
	}

	@Transactional
	public Email saveEmailDets(Email email) {
		// try {
		if(email.getAttachmentPath() != null)
			email.setAttachmentPath(storeFile(email.getAttachmentPath()));
		return emailRepo.save(email);
		// }catch (Exception e) {
		// log.error("Exception in " + this.getClass().toString(), e);
		// return null;
		// }
	}
	/*
	@Transactional
	public EmailWithAttachment saveEmailWithAttDets(EmailWithAttachment att) {
		// if(!emailRepo.hasEmail(att.getEmail().getEmailId()))
		// queueEmail(att.getEmail());

		// If email has no emailId, then the email was not previously saved by JPA
		if (att.getEmail().getEmailId() == null
				|| att.getEmail().getEmailId().equals(null)
				|| att.getEmail().getEmailId().equals(0L)) {
			Email savedEmail = saveEmailDets(att.getEmail());
			att.setEmail(savedEmail);
		}

		// try {
		return attRepo.save(att);
		// }catch (Exception e) {
		// log.error("Exception in " + this.getClass().toString(), e);
		// return null;
		// }
	}
	*/
	@Transactional(readOnly = true)
	public List<Email> getListOfEmailsToSendByStatus(String status) {
		return emailRepo.findAllEmailByStatus(status); // P=Pending, S=Sent, F=Failed, D=Deleted
	}
	/*
	@Transactional(readOnly = true)
	public Optional<EmailWithAttachment> getAttachment(Long emailId) {
		return attRepo.findEmailWithAttachmentByemail_emailId(emailId);
	}
	*/
	public void sendMailHTML(Email email) throws ApplicationException, MailException, MessagingException {
		// try {
		javax.mail.internet.MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

		messageHelper.setTo(email.getTo());
		messageHelper.setFrom(from);
		messageHelper.setSubject(email.getSubject());
		messageHelper.setText(email.getBody(), true); // Set the second parameter to true to indicate HTML content

		if (email.getCc() != null && !email.getCc().isEmpty())
			messageHelper.setCc(email.getCc().split(";"));
		if (email.getBcc() != null && !email.getBcc().isEmpty())
			messageHelper.setBcc(email.getBcc().split(";"));

		EmailServerErrorHandling(email, mimeMessage);
		// } catch (Exception e) {
		// log.error("Exception in " + this.getClass().toString(), e);
		// }
	}

	private void EmailServerErrorHandling(Email email, MimeMessagePreparator preparator) throws ApplicationException {
		try {
			validateEmail(email);
			javaMailSender.send(preparator);
		} catch (MailException | NullPointerException ex) {
			handleEmailException(email, ex);
		}
	}

	private void EmailServerErrorHandling(Email email, SimpleMailMessage message) throws ApplicationException {
		try {
			validateEmail(email);
			javaMailSender.send(message);
		} catch (MailException | NullPointerException ex) {
			handleEmailException(email, ex);
		}
	}

	private void EmailServerErrorHandling(Email email, MimeMessage mimeMessage) throws ApplicationException {
		try {
			validateEmail(email);
			javaMailSender.send(mimeMessage);
		} catch (MailException | NullPointerException ex) {
			handleEmailException(email, ex);
		}
	}

	private void validateEmail(Email email) throws ApplicationException {
		if (javaMailSender == null) {
			throw new ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR, "Mail sender is not configured (null).");
		}
		if (email == null) {
			throw new ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR, "Email object is null.");
		}
		if (email.getTo() == null || email.getTo().trim().isEmpty()) {
			throw new ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR, "Recipient email (to) is missing.");
		}
	}

	private void handleEmailException(Email email, Exception ex) throws ApplicationException {
		log.error("Exception in EmailService: " + ex.getMessage(), ex);
		String to = (email != null && email.getTo() != null) ? email.getTo() : "UNKNOWN_RECIPIENT";
		throw new ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR,
				to + " Sent Failed, " + ex.getClass().getSimpleName() + " >>> " + ex.getMessage());
	}

	// public void sendMailWithReport(String to, String cc, String bcc, String
	// subject, String body
	// , Boolean isHTML, String fileType) throws Exception{

	// MimeMessagePreparator preparator = new MimeMessagePreparator(){
	// public void prepare(MimeMessage mimeMessage) throws Exception{
	// mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
	// mimeMessage.setFrom(new InternetAddress(from));
	// mimeMessage.setSubject(subject);
	// MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
	// helper.addAttachment(file.getName(), file);
	// helper.setText(body, isHTML);
	// if(!bcc.isEmpty())
	// helper.setBcc(bcc.split(";"));
	// if(!cc.isEmpty())
	// helper.setCc(cc.split(";"));
	// }};

	// javaMailSender.send(preparator);
	// }
	
	public String storeFile(String attachmentPath) {
		if (attachmentPath == null || attachmentPath.isEmpty())
			return null;
		if (attachmentPath.contains(emailDir))
			return attachmentPath;
		File attachment = new File(attachmentPath);
		if (!attachment.exists())
			return null;
		String todayPathString = emailDir + new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
		Path todayPath = Paths.get(todayPathString);
		if (!Files.exists(todayPath))
			new File(todayPathString).mkdirs();
		Path targetPath = todayPath.resolve(Paths.get(attachment.getAbsolutePath()).getFileName().toString());
		if (!Paths.get(attachment.getPath()).equals(targetPath))
			try {

				if (attachment.getPath().contains(rcptDir)) {
					Files.copy(Paths.get(attachment.getPath()), targetPath, StandardCopyOption.REPLACE_EXISTING);
				} 
				else {
					Files.move(Paths.get(attachment.getPath()), targetPath, StandardCopyOption.REPLACE_EXISTING);
				}

			} catch (IOException e) {
				log.error("Exception in EmailService, File move func failed! Discarding attachment path storage.", e);
				return null;
			}
		return targetPath.toAbsolutePath().toString();
	}
}
