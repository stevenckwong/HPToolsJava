package com.stevenckwong.AlmRestfulClient;

import java.io.File;

public class ZipUploadUtility {
	public static void main(String[] args) {
		String host = System.getProperty("almHost");
		String port = System.getProperty("almPort");
		String protocol = System.getProperty("almProtocol");
		
		// Get an Instance of the Zip utility passing in the folder to zip, and the output zip file
		AppZip zipApp = AppZip.getInstance(".\\RunResults", ".\\RunResults.zip");
		zipApp.generate();

		// Get an instance of the upload utility providing the host, port and protocol of the ALM server.
		App uploadApp = App.getInstance(host, port, protocol);
		// Set the Domain, Project, Username and Password to connect to ALM
		uploadApp.setAlmParameters("DEFAULT", "MyDemo", "steven", "HPS0ftw@re!");
		uploadApp.signIn();
		// Provide the entity type to attach the file to, the ID of the entity, the file and the path
		// to the file. Leave filepath empty to indicate the current directory
		uploadApp.attachFileToEntity("test", "5", ".\\RunResults.zip", "");
		uploadApp.signOut();
	}
}
