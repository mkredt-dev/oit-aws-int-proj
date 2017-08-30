package com.oit.aws.git.proj;

public class LoginData {

	private static LoginData SINGLETON = new LoginData();
	
	private String ghUsername;
	private String ghPassword;
	private String ghOrganization;
	
	private String awsUsername;
	private String awsPassword;
	private String awsBucketName;
	
	/*
	 * Public LoginData Constructor
	 */
	private LoginData() {
		this.ghUsername = "";
		this.ghPassword = "";
		this.ghOrganization = "";
		
		this.awsUsername = "";
		this.awsPassword = "";
		this.awsBucketName = "";
	}
	
	/*
	 * Public Parameterized LoginData Constructor
	 * 
	 * Given the Singleton design pattern, I am unsure if this type of constructor can be
	 * reasonably be used
	 * 
	 * @param ghUsername 		The GitHub username which will be used for login
	 * @param ghPassword		The password associated with the GitHub username
	 * @param ghOrganization	The GitHub organization from which we will pull the member list
	 * @param awsUsername		The AWS S3 username which will be used for login and member list storage
	 * @param awsPassword		The password associated with the AWS S3 username
	 * @param awsBucketName		The name of the S3 Bucket which will be used to store the unnamed member list
	 */
	private LoginData(String ghUsername, String ghPassword, String ghOrganization,
			String awsUsername, String awsPassword, String awsBucketName){
		this.ghUsername = ghUsername;
		this.ghPassword = ghPassword;
		this.ghOrganization = ghOrganization;
		
		this.awsUsername = awsUsername;
		this.awsPassword = awsPassword;
		this.awsBucketName = awsBucketName;
	}

	/*
	 * LoginData Singleton Getter
	 * 
	 * @return SINGLETON		The singleton object which contains login data
	 */
	public static LoginData getInstance() {
		if(SINGLETON == null){
			SINGLETON = new LoginData();
		}
		return SINGLETON;
	}

	/*
	 * Get GitHub Username
	 * 
	 * @return ghUsername		The String representation of the GitHub username
	 */
	public String getGhUsername() {
		return ghUsername;
	}

	/*
	 * Get GitHub Password
	 * 
	 * @return ghPassword		The String representation of the GitHub password
	 */
	public String getGhPassword() {
		return ghPassword;
	}

	/*
	 * Get GitHub Organization
	 * 
	 * @return ghOrganization	The String representation of the GitHub organization
	 */
	public String getGhOrganization() {
		return ghOrganization;
	}

	/*
	 * Get AWS Username
	 * 
	 * @return awsUsername		The String representation of the AWS username
	 */
	public String getAwsUsername() {
		return awsUsername;
	}
	
	/*
	 * Get AWS Password
	 * 
	 * @return awsPassword		The String representation of the AWS password
	 */
	public String getAwsPassword() {
		return awsPassword;
	}

	/*
	 * Get AWS Bucket Name
	 * 
	 * @return awsBucketName	The String representation of the AWS S3 Bucket Name
	 */
	public String getAwsBucketName() {
		return awsBucketName;
	}

	/*
	 * Set GitHub Username
	 * 
	 * @param ghUsername		The String representation of the GitHub username
	 */
	public void setGhUsername(String ghUsername) {
		this.ghUsername = ghUsername;
	}

	/*
	 * Set GitHub Password
	 * 
	 * @param ghPassword		The String representation of the GitHub password
	 */
	public void setGhPassword(String ghPassword) {
		this.ghPassword = ghPassword;
	}

	/*
	 * Set GitHub Organization
	 * 
	 * @param ghOrganization	The String representation of the GitHub organization
	 */
	public void setGhOrganization(String ghOrganization) {
		this.ghOrganization = ghOrganization;
	}

	/*
	 * Set AWS Username
	 * 
	 * @param awsUsername		The String representation of the AWS username
	 */
	public void setAwsUsername(String awsUsername) {
		this.awsUsername = awsUsername;
	}

	/*
	 * Set AWS Password
	 * 
	 * @param awsPassword		The String representation of the AWS password
	 */
	public void setAwsPassword(String awsPassword) {
		this.awsPassword = awsPassword;
	}

	/*
	 * Set AWS Bucket Name
	 * 
	 * @param awsBucketName		The String representation of the AWS S3 Bucket Name
	 */
	public void setAwsBucketName(String awsBucketName) {
		this.awsBucketName = awsBucketName;
	}

	@Override
	public String toString() {
		return "LoginData [ghUsername=" + ghUsername + ", ghPassword=" + ghPassword + ", ghOrganization="
				+ ghOrganization + ", awsUsername=" + awsUsername + ", awsPassword=" + awsPassword + ", awsBucketName="
				+ awsBucketName + "]";
	}
	
}
