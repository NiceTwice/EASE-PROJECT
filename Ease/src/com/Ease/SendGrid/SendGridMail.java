package com.Ease.SendGrid;

import java.io.IOException;

import com.sendgrid.ASM;
import com.sendgrid.Attachments;
import com.sendgrid.BccSettings;
import com.sendgrid.ClickTrackingSetting;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.FooterSetting;
import com.sendgrid.GoogleAnalyticsSetting;
import com.sendgrid.Mail;
import com.sendgrid.MailSettings;
import com.sendgrid.Method;
import com.sendgrid.OpenTrackingSetting;
import com.sendgrid.Personalization;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.Setting;
import com.sendgrid.SpamCheckSetting;
import com.sendgrid.SubscriptionTrackingSetting;
import com.sendgrid.TrackingSettings;

public class SendGridMail {

	private static final String  API_KEY = "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"; 
	
	/*public static void main(String[] args) throws IOException {
	    Email from = new Email("barthelemy.leveque@ieseg.fr");
	    String subject = "Sending with SendGrid is Fun";
	    Email to = new Email("victor@ease.space");
	    Content content = new Content("text/plain", "and easy to do anywhere, even with Java");
	    Mail mail = new Mail(from, subject, to, content);

	    SendGrid sg = new SendGrid(API_KEY);
	    Request request = new Request();
	    try {
	      request.method = Method.POST;
	      request.endpoint = "mail/send";
	      request.body = mail.build();
	      Response response = sg.api(request);
	      System.out.println(response.statusCode);
	      System.out.println(response.body);
	      System.out.println(response.headers);
	    } catch (IOException ex) {
	      throw ex;
	    }
	  }*/
	public static Mail buildKitchenSink() throws IOException {
	    Mail mail = new Mail();

	    Email fromEmail = new Email();
	    fromEmail.setName("Example User");
	    fromEmail.setEmail("thomas@ease.space");
	    mail.setFrom(fromEmail);

	    mail.setSubject("Hello World from the SendGrid Java Library");

	    Personalization personalization = new Personalization();
	    Email to = new Email();
	    to.setName("Victor Nivet");
	    to.setEmail("victor@ease.space");
	    personalization.addTo(to);
	    to.setName("Benjamin Prigent");
	    to.setEmail("benjamin@ease.space");
	    personalization.addTo(to);
	    Email cc = new Email();
	    cc.setName("Thomas Heniart");
	    cc.setEmail("heniart.thomas@gmail.com");
	    personalization.addCc(cc);
	    cc.setName("Sergii Fisun");
	    cc.setEmail("sergii@ease.space");
	    personalization.addCc(cc);
	    Email bcc = new Email();
	    bcc.setName("titomheniart");
	    bcc.setEmail("titomheniart@hotmail.fr");
	    personalization.addBcc(bcc);
	    personalization.setSubject("Hello World from the Personalized SendGrid Java Library");
	    personalization.addHeader("X-Test", "test");
	    personalization.addHeader("X-Mock", "true");
	    personalization.addSubstitution("%name%", "Example User");
	    personalization.addSubstitution("%city%", "Riverside");
	    personalization.addCustomArg("user_id", "343");
	    personalization.addCustomArg("type", "marketing");
	    personalization.setSendAt(1443636843);
	    mail.addPersonalization(personalization);

	    Personalization personalization2 = new Personalization();
	    Email to2 = new Email();
	    to2.setName("Victor Nivet");
	    to2.setEmail("victor.nivet@ieseg.fr");
	    personalization2.addTo(to2);
	    to2.setName("Benjamin Prigent");
	    to2.setEmail("benjamin.prigent@ieseg.fr");
	    personalization2.addTo(to2);
	    personalization2.setSubject("Hello World from the Personalized SendGrid Java Library");
	    personalization2.addHeader("X-Test", "test");
	    personalization2.addHeader("X-Mock", "true");
	    personalization2.addSubstitution("%name%", "Example User");
	    personalization2.addSubstitution("%city%", "Denver");
	    personalization2.addCustomArg("user_id", "343");
	    personalization2.addCustomArg("type", "marketing");
	    personalization2.setSendAt(1443636843);
	    mail.addPersonalization(personalization2);

	    Content content = new Content();
	    content.setType("text/plain");
	    content.setValue("some text here");
	    mail.addContent(content);
	    content.setType("text/html");
	    content.setValue("<html><body>some text here</body></html>");
	    mail.addContent(content);

	    Attachments attachments = new Attachments();
	    attachments.setContent("TG9yZW0gaXBzdW0gZG9sb3Igc2l0IGFtZXQsIGNvbnNlY3RldHVyIGFkaXBpc2NpbmcgZWxpdC4gQ3JhcyBwdW12");
	    attachments.setType("application/pdf");
	    attachments.setFilename("balance_001.pdf");
	    attachments.setDisposition("attachment");
	    attachments.setContentId("Balance Sheet");
	    mail.addAttachments(attachments);

	    Attachments attachments2 = new Attachments();
	    attachments2.setContent("BwdW");
	    attachments2.setType("image/png");
	    attachments2.setFilename("banner.png");
	    attachments2.setDisposition("inline");
	    attachments2.setContentId("Banner");
	    mail.addAttachments(attachments2);

	    mail.setTemplateId("13b8f94f-bcae-4ec6-b752-70d6cb59f932");

	    mail.addSection("%section1%", "Substitution Text for Section 1");
	    mail.addSection("%section2%", "Substitution Text for Section 2");

	    mail.addHeader("X-Test1", "1");
	    mail.addHeader("X-Test2", "2");

	    mail.addCategory("May");
	    mail.addCategory("2016");

	    mail.addCustomArg("campaign", "welcome");
	    mail.addCustomArg("weekday", "morning");

	    mail.setSendAt(1443636842);

	    ASM asm = new ASM();
	    asm.setGroupId(99);
	    asm.setGroupsToDisplay(new int[] {4,5,6,7,8});
	    mail.setASM(asm);

	    // This must be a valid [batch ID](https://sendgrid.com/docs/API_Reference/SMTP_API/scheduling_parameters.html) to work
	    // mail.setBatchId("sendgrid_batch_id");

	    mail.setIpPoolId("23");

	    MailSettings mailSettings = new MailSettings();
	    BccSettings bccSettings = new BccSettings();
	    bccSettings.setEnable(true);
	    bccSettings.setEmail("thomas@ease.space");
	    mailSettings.setBccSettings(bccSettings);
	    Setting sandBoxMode = new Setting();
	    sandBoxMode.setEnable(true);
	    mailSettings.setSandboxMode(sandBoxMode);
	    Setting bypassListManagement = new Setting();
	    bypassListManagement.setEnable(true);
	    mailSettings.setBypassListManagement(bypassListManagement);
	    FooterSetting footerSetting = new FooterSetting();
	    footerSetting.setEnable(true);
	    footerSetting.setText("Footer Text");
	    footerSetting.setHtml("<html><body>Footer Text</body></html>");
	    mailSettings.setFooterSetting(footerSetting);
	    SpamCheckSetting spamCheckSetting = new SpamCheckSetting();
	    spamCheckSetting.setEnable(true);
	    spamCheckSetting.setSpamThreshold(1);
	    spamCheckSetting.setPostToUrl("https://spamcatcher.sendgrid.com");
	    mailSettings.setSpamCheckSetting(spamCheckSetting);
	    mail.setMailSettings(mailSettings);

	    TrackingSettings trackingSettings = new TrackingSettings();
	    ClickTrackingSetting clickTrackingSetting = new ClickTrackingSetting();
	    clickTrackingSetting.setEnable(true);
	    clickTrackingSetting.setEnableText(true);
	    trackingSettings.setClickTrackingSetting(clickTrackingSetting);
	    OpenTrackingSetting openTrackingSetting = new OpenTrackingSetting();
	    openTrackingSetting.setEnable(true);
	    openTrackingSetting.setSubstitutionTag("Optional tag to replace with the open image in the body of the message");
	    trackingSettings.setOpenTrackingSetting(openTrackingSetting);
	    SubscriptionTrackingSetting subscriptionTrackingSetting = new SubscriptionTrackingSetting();
	    subscriptionTrackingSetting.setEnable(true);
	    subscriptionTrackingSetting.setText("text to insert into the text/plain portion of the message");
	    subscriptionTrackingSetting.setHtml("<html><body>html to insert into the text/html portion of the message</body></html>");
	    subscriptionTrackingSetting.setSubstitutionTag("Optional tag to replace with the open image in the body of the message");
	    trackingSettings.setSubscriptionTrackingSetting(subscriptionTrackingSetting);
	    GoogleAnalyticsSetting googleAnalyticsSetting = new GoogleAnalyticsSetting();
	    googleAnalyticsSetting.setEnable(true);
	    googleAnalyticsSetting.setCampaignSource("some source");
	    googleAnalyticsSetting.setCampaignTerm("some term");
	    googleAnalyticsSetting.setCampaignContent("some content");
	    googleAnalyticsSetting.setCampaignName("some name");
	    googleAnalyticsSetting.setCampaignMedium("some medium");
	    trackingSettings.setGoogleAnalyticsSetting(googleAnalyticsSetting);
	    mail.setTrackingSettings(trackingSettings);

	    Email replyTo = new Email();
	    replyTo.setName("Thomas Heniart");
	    replyTo.setEmail("thomas@ease.space");
	    mail.setReplyTo(replyTo);

	    return mail;
	  }

	  // Minimum required to send an email
	  public static Mail buildHelloEmail() throws IOException {
	    Email from = new Email("thomas@ease.space");
	    String subject = "Hello World from the SendGrid Java Library";
	    Email to = new Email("victor@ease.space");
	    Content content = new Content("text/plain", "some text here");
	    // Note that when you use this constructor an initial personalization object
	    // is created for you. It can be accessed via
	    // mail.personalization.get(0) as it is a List object
	    Mail mail = new Mail(from, subject, to, content);
	    Email email = new Email("benjamin@ease.space");
	    mail.personalization.get(0).addTo(email);

	    return mail;
	  }

	  public static void baselineExample() throws IOException {
	    SendGrid sg = new SendGrid(API_KEY);
	    sg.addRequestHeader("X-Mock", "true");

	    Request request = new Request();
	    Mail helloWorld = buildHelloEmail();
	    try {
	      request.method = Method.POST;
	      request.endpoint = "mail/send";
	      request.body = helloWorld.build();
	      Response response = sg.api(request);
	      System.out.println(response.statusCode);
	      System.out.println(response.body);
	      System.out.println(response.headers);
	    } catch (IOException ex) {
	      throw ex;
	    }
	  }

	  public static void kitchenSinkExample() throws IOException {
	    SendGrid sg = new SendGrid(API_KEY);
	    sg.addRequestHeader("X-Mock", "true");

	    Request request = new Request();
	    Mail kitchenSink = buildKitchenSink();
	    try {
	      request.method = Method.POST;
	      request.endpoint = "mail/send";
	      request.body = kitchenSink.build();
	      Response response = sg.api(request);
	      System.out.println(response.statusCode);
	      System.out.println(response.body);
	      System.out.println(response.headers);
	    } catch (IOException ex) {
	      throw ex;
	    }
	  }

	  public static void main(String[] args) throws IOException {
	    baselineExample();
	    kitchenSinkExample();
	  }
}
