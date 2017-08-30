# OIT AWS GitHub Organization Crawler
OIT AWS Interview Project by Michael Kredt

Operation Notes
In order to operate this program, you will need to use the Eclipse IDE on a MS Windows operating system. There are also other prerequisites which must be met in order to allow for operation.

Eclipse ISE
Download the GitHubNoNameUserCrawler folder from the repository and create a new project in Eclipse IDE by importing from an existing file system. Select the GitHubNoNameUserCrawler folder. If the project is not recognized by the IDE as a Maven project, import the file system and remove all related Maven files. Convert the project to a Maven project and manually replace the files with the ones which came in the original GitHubNoNameUserCrawler folder.

GitHub and AWS Credentials
If you wish to test something other than the default GitHub organization and account provided, then you will need to modify the data provided in the loginParameters.json file (located in the GitHubNoNameUserCrawler golder). The AWS credentials have already been accounted for, but you will need to ensure the "credentials" and "config" files are placed in your local user ".aws" folder (C:\Users\<username>\.aws\).

JAR Files
There are some included .jar files which were the intended means by which this program was to be operated. Unfortunatly, there is some conflict which is not allowing them to operate at this time; hence the current need to operate within the IDE and not independently on the command line.

Development Notes
The following are my personal notes which describe my process of designing and building the program. Because some of the text may not format properly, I will mention that it chronicles the changes I made to my approach and lists my assumptions.

Introduction
This program is being developed as part of the interview process for the position noted above. For the time being it will be developed in Java, but will be converted into Python (as I do not currently have experience in Python) if time allows. In the sections following, I will list program requirements, determine assumptions which need to be made, and any other supplementary details I need to complete the project.

Requirements
The requirements for the project do not specify a particular language and leave open the mode by which the project may be accomplished. As my current experience with web communication protocols exists mostly within the realm of Java, I will begin this project in Java. While there is no specification for language, the preference will be given to Python or JavaScript. As I have experience in neither of these languages, but know Python to be used almost universally, I will endeavor to rewrite the program when time permits. The requirements for the program are as follows:

	1. Use the GitHub API to go through all public and private GitHub members of a GitHub organization. If the name for the member is not      filled in on their profile information page, send them an email directing them to github.com to rectify the issue.
	2. The list of usernames with nameless profiles into an AWS S3 Bucket.
	3. Save and upload the code into a public GitHub repository.
	4. Document any assumptions made, instructions for how to run the code produced, and any other details which are need-to-know in the        readme.md file of the root directory of the repository.
	5. Reply to the initial email sent with the requirements with a link to the GitHub repository at or by 17:00 MDT on 30 Aug.

Implementation Notes & Assumptions
As not all of the numbered items in the list above are strictly program requirements, I will focus on breaking each of the requirements into more manageable sub-requirements/tasks. Therefore, only requirements 1 and 2 will be present with supplementary information.

	1. Use the GitHub API to go through all public and private GitHub members of a GitHub organization. If the name for the member is not 	filled in on their profile information page, send them an email directing them to github.com to rectify the issue.
	i. Login with a username and password in order to obtain an authorization key. This key will allow us to access information which          determines whether or not we can view concealed (private) members and groups.
		a. There will be an available option to login with a test account or user supplied credentials. This option will be prompted for at        the beginning of the program.
			i. The input will be case sensitive
			ii. If login fails the system will prompt until the authorization server rejects further attempts. The program will return to the           initial login options menu.
		b. The key will be stored in a singleton object which contains other login credentials.
	ii. Obtain the name for an organization
		a. The option will be available to choose from an organization made for testing purposes, a user defined organization
			i. Input will be case sensitive
			ii. If an organization fails to be found after a number of rejections, the prompt will return to the initial organization prompt             menu
			iii. Assumption: There are only a few ways that one can obtain both the public and private (concealed) member of a group: 1, the              authenticated user must be an administrator of the group, or 2, the authenticated user must be a member of the group. This              may change some of my notions made about choosing an organization. If an organization is chosen for which neither of the                previous requirements matches, the request returns with only information pertinent to public users; which is not what the                requirements desire. Therefore, the prompt will also need to notify the user if they are not a member or administrator of the            group, else none of the needful information will be obtained.
		b. The organization name will be stored in a singleton object which will contain other information about the organization and its users
	iii. Obtain the user list of the organization, all members (public and private)
		a. Given the name of an organization, the code will submit a request (by the authenticated user) to receive the list of all public and private members of an organization
		b. Organization member objects will be placed into a HashMap
	iv. Obtain list of users of the organization who have no name filled
		a. The HashMap will be pruned to represent only those members who do not have a name listed on their user profile
		b. These organization members will be put into an appropriate format to be loaded into a cloud-based location
	v. Send an email to the list of users who have no name filled
		a. A default email message will be prepared to send to each user who exists in the now pruned HashMap
	vi. All directives have been completed, the option will be given to perform actions again, logoff and try again, or to terminate             program operation
		a. It may not be necessary to do this, program operation can be simply terminated when operation is complete
		
	2. The list of usernames with nameless profiles into an AWS S3 Bucket
	i. The list which was previously prepared in 1.iv.b will be loaded into an AWS S3 Bucket

Development Notes
As the program is developed, notes will be made here chronicling changes which will be made to the development notes listed in the previous section.

	1. Use the GitHub API to go through all public and private GitHub members of a GitHub organization. If the name for the member is not filled in on their profile information page, send them an email directing them to github.com to rectify the issue.
	i. Login with a username and password in order to obtain an authorization key. This key will allow us to access information which determines whether or not we can view concealed (private) members and groups.
		a. There will be an available option to login with a test account or user supplied credentials. This option will be prompted for at        the beginning of the program. Due to limited time and limited explanations of input methods, the program will instead read login        information from a JSON file which will be in the local directory as the executing program.
			i. The input will be case sensitive
			ii. If login fails the system will prompt until the authorization server rejects further attempts. The program will return to the           initial login options menu.
			iii. All fields must be filled in for the program to begin operation
		b. The key will be stored in a singleton object which contains other login credentials.
	ii. Obtain the name for an organization
		a. The option will be available to choose from an organization made for testing purposes, a user defined organization The                  organization name will be provided from the JSON file previously mentioned
			i. Input will be case sensitive
			ii. If an organization fails to be found after a number of rejections, the prompt will return to the initial organization prompt 						menu There will be no selection menu
			iii. Assumption: There are only a few ways that one can obtain both the public and private (concealed) member of a group: 1, the 						authenticated user must be an administrator of the group, or 2, the authenticated user must be a member of the group. This may 					change some of my notions made about choosing an organization. If an organization is chosen for which neither of the previous 				requirements matches, the request returns with only information pertinent to public users; which is not what the requirements 					desire. Therefore, the prompt will also need to notify the user if they are not a member or administrator of the group, else 						none of the needful information will be obtained. (This will still be done, but there will be no input prompts)
		b. The organization name will be stored in a singleton object which will contain other information about the organization and its users
	iii. Obtain the user list of the organization, all members (public and private)
		a. Given the name of an organization, the code will submit a request (by the authenticated user) to receive the list of all public 				and private members of an organization
		b. Organization member objects will be placed into a HashMap (It may be possible to optimize the process of adding members to the 					list by checking for null or "" values when obtaining information from the JSON, choosing to skip the remainder of the loop 						instead of building a bulky list which we then need to trim. This will also remove the need for the following step as we will 					already have the list we need.)
	iv. Obtain list of users of the organization who have no name filled
		a. The HashMap will be pruned to represent only those members who do not have a name listed on their user profile
		b. These organization members will be put into an appropriate format to be loaded into a cloud-based location
	v. Send an email to the list of users who have no name filled
		a. A default email message will be prepared to send to each user who exists in the now pruned HashMap
	vi. All directives have been completed, the option will be given to perform actions again, logoff and try again, or to terminate program operation
		a. It may not be necessary to do this, program operation can be simply terminated when operation is complete
		
	2. The list of usernames with nameless profiles into an AWS S3 Bucket
	i. The list which was previously prepared in 1.iv.b will be loaded into an AWS S3 Bucket
