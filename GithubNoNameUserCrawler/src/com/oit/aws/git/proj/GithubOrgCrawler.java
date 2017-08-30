package com.oit.aws.git.proj;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;
import java.io.File;
import java.io.FileWriter;

import java.util.*;
import java.util.ArrayList;
import java.util.List;

import javax.mail.*;
import javax.mail.internet.*;

import org.kohsuke.github.*;
import org.json.simple.*;
import org.json.simple.parser.*;
import org.json.simple.parser.ParseException;

import com.amazonaws.AmazonClientException;

import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;


public class GithubOrgCrawler {
	
	/*
	 * This enumerated type will represent the state of the program as it progresses
	 * through the user selected options.
	 */
	public enum PROG_STATE{START_UP, LOGIN_SELECT, LOGIN_ENTRY, ORG_SELECT, ORG_ENTRY, PROCESS_DATA, PRE_EXIT, EXIT, ERROR};
	public enum DATA_ENTRY_TYPE{REGULAR, PASSWORD};
	
	/*
	 * Class Variables
	 */
	private static PROG_STATE programState = PROG_STATE.START_UP;
	
	private static String entryStatement;
	private static String loginSelectionStatement;
	private static String loginPromptUsernameStatement;
	private static String loginPromptPasswordStatement;
	private static String loginFailureStatement;
	private static String loginExceededStatement;
	private static String loginSuccessfulStatement;
	private static String organizationSelectionStatement;
	private static String organizationPromptStatement;
	private static String organizationNotFoundStatement;
	private static String organizationNotCredentialedStatement;
	private static String organizationSuccessfulStatement;
	private static String preExitStatement;
	private static String exitStatement;
	private static String errorStatement;
	private static String emailBody;

	/*
	 * Program point of entry and initial operation of program state machine
	 */
	public static void main(String[] args) {
		/*
		 * Begin the setup of messages which will be used by the program to give directives
		 * to the user.
		 */
		setupMessages();
		
		/*
		 * While-loop which will serve as the state machine and flow director for the program
		 */
		while(programState != PROG_STATE.ERROR){
			/*
			 * This conditional statement will determine what statements need to be displayed
			 * based upon state and other input conditions
			 */
			switch(programState){
			case START_UP:
				stateStartUp();
				break;
			case LOGIN_SELECT:
				stateLoginSelect();
				break;
			case LOGIN_ENTRY:
				stateLoginEntry();
				break;
			case ORG_SELECT:
				stateOrganizationSelect();
				break;
			case ORG_ENTRY:
				stateOrganizationEntry();
				break;
			case PROCESS_DATA:
				stateProcessData();
				break;
			case PRE_EXIT:
				statePreExit();
				break;
			case EXIT:
				stateExit();
				break;
			default:
				stateError();
				break;
			}
		}
	}
	
	
	/*
	 * This series of state helper functions will perform all the actions which need to be
	 * performed in each state. They are placed here to keep the entry-point neat and clean.
	 */
	
	private static void stateStartUp(){
		/*
		 * Introductory Print Statement
		 */
		System.out.printf(entryStatement);
		
		/*
		 * Setup variables which will be used to store JSON information pulled from
		 * the loginParameters.json file
		 */
		
		JSONParser	infoParser = new JSONParser();
		JSONObject 	loginInformation;
		JSONObject 	gitHubLoginInformation;
		JSONObject	awsLoginInformation;
		
		String ghUsername = "";
		String ghPassword = "";
		String ghOrganization = "";
		
		String awsUsername = "";
		String awsPassword = "";
		String awsBucketName = "";
		
		/*
		 * Begin pulling parameters from the JSON file attached to the program
		 */
		try{
			loginInformation = (JSONObject) infoParser.parse(new FileReader(System.getProperty("user.dir") + "\\loginParameters.json"));
			
			gitHubLoginInformation = (JSONObject) loginInformation.get("gh_login");
			awsLoginInformation = (JSONObject) loginInformation.get("aws_s3_login");
			
			ghUsername = 	(String) gitHubLoginInformation.get("gh_username");
			ghPassword = 	(String) gitHubLoginInformation.get("gh_password");
			ghOrganization =(String) gitHubLoginInformation.get("gh_organization");
			
			awsUsername = 	(String) awsLoginInformation.get("aws_username");
			awsPassword = 	(String) awsLoginInformation.get("aws_password");
			awsBucketName = (String) awsLoginInformation.get("aws_bucket_name");
			//System.out.println(LoginData.getInstance().toString());//Sanity Check
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
			programState = PROG_STATE.EXIT;
			return;
		}
		catch(IOException e){
			e.printStackTrace();
			programState = PROG_STATE.EXIT;
			return;
		}
		catch (ParseException e) {
			e.printStackTrace();
			programState = PROG_STATE.EXIT;
			return;
		}
		
		/*
		 * Data extraction from the JSON is complete. We will now check to ensure that all fields
		 * were filled. If they have not been we will prompt the user and close the program. If
		 * all fields are accounted for we will change the program state to change over to the
		 * data processing state;
		 */
		if(ghUsername.isEmpty() || ghPassword.isEmpty() || ghOrganization.isEmpty() ||
			awsUsername.isEmpty() || awsPassword.isEmpty() || awsBucketName.isEmpty()){
			System.out.println("Sorry, one ofthe values required to operate this program was not provided. Please check the .JSON file to ensure"
					+ "all key-value pairs have been assigned.\n\n");
			programState = PROG_STATE.EXIT;
			return;
		}
		else
		{
			LoginData.getInstance().setGhUsername(ghUsername);
			LoginData.getInstance().setGhPassword(ghPassword);
			LoginData.getInstance().setGhOrganization(ghOrganization);
			LoginData.getInstance().setAwsUsername(awsUsername);
			LoginData.getInstance().setAwsPassword(awsPassword);
			LoginData.getInstance().setAwsBucketName(awsBucketName);
			//System.out.println(LoginData.getInstance().toString());//Sanity Check
			programState = PROG_STATE.PROCESS_DATA;
		}
	}
	
	private static void stateLoginSelect(){
		
	}
	
	private static void stateLoginEntry(){
		
	}
	
	private static void stateOrganizationSelect(){
		
	}
	
	private static void stateOrganizationEntry(){
		
	}
	
	/*
	 * State function which will perform logins to the appropriate services, processing of received data,
	 * generation of the list of members of an organization, and email those members.
	 */
	private static void stateProcessData(){
		List<GHUser> noNamedOrgUsers = new ArrayList<GHUser>();
		List<GHUser> orgMembers = new ArrayList<GHUser>();
		
		GitHub userGH = null;
		GHOrganization userOrg = null;
		PagedIterable<GHUser> orgMemberList;
		/*
		 * Begin login to GitHub, find organization, obtain the member list
		 */
		try{
			userGH = GitHub.connectUsingPassword(LoginData.getInstance().getGhUsername(), LoginData.getInstance().getGhPassword());
			userOrg = userGH.getOrganization(LoginData.getInstance().getGhOrganization());
			orgMemberList = userOrg.listMembers();
			orgMembers = orgMemberList.asList();
		}
		catch(IOException e){
			e.printStackTrace();
			programState = PROG_STATE.EXIT;
		}
		
		/*
		 * Check to ensure the user is a member or administrator of the group
		 */
		try {
			if(!userOrg.hasMember(userGH.getUser(LoginData.getInstance().getGhUsername()))){
				System.out.printf("The user [%s] is not a member of this group! Please edit login credentials in the JSON file.\n", LoginData.getInstance().getGhUsername());
				programState = PROG_STATE.EXIT;
				return;
			}
		} catch (IOException e) {
			e.printStackTrace();
			programState = PROG_STATE.EXIT;
			return;
		}
		
		/*
		 * Iterate through the member list and add only the user names of those users which have no email to the
		 * list of those who will be emailed to rectify this issue.
		 */
		for(GHUser tempUser : orgMembers){
			try{
				String tempName = tempUser.getName();
				if(tempName == null || tempName == ""){
					noNamedOrgUsers.add(tempUser);
				}
			}
			catch(IOException e){
				noNamedOrgUsers.add(tempUser);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		
		/*
		 * Create a JSON file which will be uploaded to the AWS S3 Bucket
		 */
		JSONObject fileTopHier = new JSONObject();
		JSONArray usernameList = new JSONArray();
		
		for(GHUser tempUser : noNamedOrgUsers){
			usernameList.add(tempUser.getLogin());
		}
		
		fileTopHier.put("userList", usernameList);
		
		try(FileWriter uploadFile = new FileWriter(System.getProperty("user.dir") + "//noNamedOrgUsers.json")){
			uploadFile.write(fileTopHier.toJSONString());
		}
		catch(IOException e){
			System.out.println(e.getMessage());
			e.printStackTrace();
			programState = PROG_STATE.EXIT;
			return;
		}
		
		/*
		 * Initiate transfer manager and upload the list of users to a the AWS S3 Bucket specified in the JSON file
		 */		
		File uploadFile = new File(System.getProperty("user.dir") + "//noNamedOrgUsers.json");
		TransferManager sessionTxMan = TransferManagerBuilder.defaultTransferManager();
		
		try{
			Upload tx = sessionTxMan.upload(LoginData.getInstance().getAwsBucketName(), "gitHubNoNamedOrgUsers", uploadFile);
			tx.waitForCompletion();
		}
		catch(AmazonClientException | InterruptedException e){
			System.err.println(e.getMessage());
			e.printStackTrace();
			programState = PROG_STATE.EXIT;
			return;
		}
		sessionTxMan.shutdownNow();
		
		/*
		 * Email the users who do not have a name associated with their account
		 */
		//Default Email Information
		String toAddr	="";
		String toLogin	="";
		String fromAddr	="no-reply@githubcrawler.com";
		
		//Get and Set System Properties
		Properties sysProperties = System.getProperties();
		sysProperties.put("mail.smtp.host", "smtp.gmail.com");
		sysProperties.put("mail.smtp.socketFactory.port", "465");
		sysProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		sysProperties.put("mail.smtp.auth", "true");
		sysProperties.put("mail.smtp.port", "465");
		
		//Obtain a session using the set properties
		Session session = Session.getDefaultInstance(sysProperties, new javax.mail.Authenticator(){
			protected PasswordAuthentication getPasswordAuthentication(){
				return new PasswordAuthentication("michael.kredt.dev", LoginData.getInstance().getAwsPassword());
			}
		});
		
		for(GHUser tempUser : noNamedOrgUsers){
			//Obtain email address and login name for the unnamed user
			try{
				toAddr = tempUser.getEmail();
			}
			catch(IOException e){
				continue;
			}
			
			if(toAddr == null || toAddr == ""){
				continue;
			}
			
			toLogin = tempUser.getLogin();
			
			//Setup and send email message
			try{
				MimeMessage message = new MimeMessage(session);
				message.setFrom(new InternetAddress(fromAddr));
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddr));
				message.setSubject("GitHub Profile Update");
				message.setContent(String.format(emailBody, toLogin), "text/html");
				
				Transport.send(message);
				System.out.printf("Message successfully sent to [%s]\n", toLogin);
			}
			catch(MessagingException e){
				e.printStackTrace();
			}
		}
		
		/*
		 * Successful completion of data processing and emails sent, program state will be set to  allow the
		 * program to proceed to exit
		 */
		programState = PROG_STATE.EXIT;
	}
	
	private static void statePreExit(){
		
	}
	
	/*
	 * State function which will display exit prompts and perform the actual program exit
	 */
	private static void stateExit(){
		System.out.println("Exiting Program\n");
		System.exit(0);
	}
	
	private static void stateError(){
		
	}
	
	/*
	 * Helper function which will setup all the messages for the various menus, prompts, and statistics
	 * which will be displayed throughout program execution.
	 */
	private static void setupMessages(){
		entryStatement = 	"***********************************\n"
						+ 	"*** GitHub Organization Crawler ***\n"
						+ 	"***  With GitHub API for Java   ***\n"
						+ 	"***	    Michael Kredt       ***\n"
						+ 	"***********************************\n\n";
		
		loginSelectionStatement = 	"Welcome to the login. In order to access GitHub, you will be prompted to enter a username and password."
								+ 	"You may enter a username of your choosing or you may use an account setup for testing purposes. To use"
								+ 	"the testing account, do not enter any text at the next prompt and press the enter key.\n\n";
		
		loginPromptUsernameStatement = "Please enter a GitHub username(case sensitive): ";
		
		loginPromptPasswordStatement = "Please enter the associated password: ";
		
		loginFailureStatement = "Login for user [%s] did not complete successfully. Please ensure the associated password is correct.";
		
		loginExceededStatement = "Login attempts for user [%s] have been exceeded. We will return to the login menu.";
		
		loginSuccessfulStatement = "Login attempt successful for user [%s].\n\n";
		
		organizationSelectionStatement = 	"Welcome to organization selection. In order to meet the requirements of the project, a organization name "
										+ 	"must be provided. This may be an organization of your choosing, however, you will be returned the selection"
										+ 	"prompt if the name for the organization does not exist, the username you logged in is not a member of the"
										+ 	"organization, or the user is not an administrator of the organization.\n\n";
		
		organizationPromptStatement = "Please enter a GitHub organization name (case-sensitive): ";
		
		organizationNotFoundStatement = "Organization [%s] was not found on GitHub. Please ensure the name is correctly spelt and case-sensitive.\n\n";
		
		organizationNotCredentialedStatement = 	"User [%s] was not found to have administrator or member credentials in organization [%s]. Please check "
											+ 	"permissions and ensure the user is a member or administrator of the organization.\n\n";
		
		organizationSuccessfulStatement = "User [%s] was found to be an administrator or member of organization [%s].\n\n";
		
		preExitStatement = "Username list has been processed. You may:\n\t1.Exit\n\t2.Try another organization\n\t3.Login with another username\n\n";
		
		exitStatement = "Logging out of GitHub, exiting program.\n\n";
		
		errorStatement = "Message should never display, check state machine.\n\n";
		
		emailBody = "<p>Dear %s,<br>We have noticed that your GitHub profile does not have a name associated therewith. Please login to <a href=\"https://www.github.com/\">GitHub</a> to rectify"
				+ " this matter.<br><br>Thanks,<br>GitHub Organization Crawler</p>";
	}

}
