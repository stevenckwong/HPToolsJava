package com.stevenckwong.AlmRestfulClient;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

// these are for the password decryption
// import java.io.IOException;
// import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
// import org.apache.commons.codec.binary.Base64;


/**
 * AlmRestfulClient is a Java client that uses REST APIs to access HP
 * Application Lifecycle Management
 *
 * Usage: 
 * Instantiate the object with the host, port and protocol to connect to ALM
 *  	// app = new App("sw-hpalm","8080","http") ;
 *
 * Then set the Domain, Project, username and password
 *   	// app.setAlmParameters("DEFAULT", "MyDemo", "almuser", "P@ssw0rd");
 *
 * Then call signIn to sign in to ALM.
 *   	// app.signIn();
 *
 * Optional: we can call getEntityById to get the entity based on type and also the ID.
 * Currently the method just returns the JSON string so that user can do whatever they want with it.
 * 
 *   	// app.getEntityById("test", "5");
 *
 * Optional: We can also call attachFileToEntity method providing the entity type, the ID of the entity to attach
 * the file to, the name and path where the file is located. 
 *   	// app.attachFileToEntity("test", "5", "myfile.zip", "");
 *   
 * Lastly, we call signOut() to signout from ALM.
    	// app.signOut();
 */
public class App 
{
	private String almHost;
	private String almPort;
	private String almProtocol;
	private String almDomain;
	private String almProject;
	private HttpURLConnection conn;
	private String almUser;
	private String almPassword;

	// Make sure these are the same values with that in the PasswordTool
    private final char[] PASSWORD = "AlmRestfulClientPassword".toCharArray();
    private final byte[] SALT = {
        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
    };	
	
	public App () {

	}
	
	public App(String host, String port, String protocol) {
		
		this.setDefaults(host,port, protocol);
		
	}
	
	private void setDefaults(String host, String port, String protocol) {
		String defaultProtocol = "http";
		if (protocol==null) {
			this.almProtocol = defaultProtocol;
		} else if (!protocol.equals("http") && !protocol.equals("https")) {
			this.almProtocol = defaultProtocol;
		} else {
			this.almProtocol = protocol;
		}
		
		String defaultPort = "8080";
		if (port == null) {
			this.almPort = defaultPort;
		} else {
			this.almPort = port;
		}
		
		String defaultHost = "localhost";
		if (host==null) {
			this.almHost = defaultHost;
		} else {
			this.almHost = host;
		}
	}
	
	public String getAlmAPIURL() {
		String almUrl = this.almProtocol + "://" + this.almHost + ":" + this.almPort + "/qcbin/api";
		return almUrl;
	}
	    
	public String getAlmProjectAPIURL() {
		return this.getAlmAPIURL() + "/domains/" + this.almDomain + "/projects/" + this.almProject;
	}
	
	// The URL with "/qcbin/rest/" accesses the Experimental REST API.
	public String getAlmRESTURL() {
		String almUrl = this.almProtocol + "://" + this.almHost + ":" + this.almPort + "/qcbin/rest";
		return almUrl;
	}
	
	// The URL with "/qcbin/rest/" accesses the Experimental REST API.
	public String getAlmProjectRESTURL() {
		return this.getAlmRESTURL() + "/domains/" + this.almDomain + "/projects/" + this.almProject;
	}
	
	
	public void setAlmParameters(String domain, String project, String user, String password) {
		try {
			this.almDomain = domain;
			this.almProject = project;
			this.almUser = user;
			this.almPassword = this.decrypt(password);
		} catch (IllegalArgumentException e) {
			this.almPassword = "";
			e.printStackTrace();
		}
		catch (IOException ioe) {
			this.almPassword = "";
			ioe.printStackTrace();
		}
		catch (GeneralSecurityException gse) {
			this.almPassword = "";
			gse.printStackTrace();
		}
	}
	
	public void signIn() {
		try {
			
			// We have to set the Cookie Handler with the Cookie Manager to handle Cookie Management from the server.
			CookieManager manager = new CookieManager();
			manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
			CookieHandler.setDefault(manager);
			
			String userAndPassword = this.almUser + ":" + this.almPassword;
			String signInUrlString = this.getAlmAPIURL() + "/authentication/sign-in";	
			URL signInURL = new URL(signInUrlString);
			this.conn = (HttpURLConnection)signInURL.openConnection();
			this.conn.setRequestMethod("POST");
			this.conn.addRequestProperty("Content-Type", "application/json");
			this.conn.addRequestProperty("Accept", "application/json");
			this.conn.addRequestProperty("Authorization", "Basic " + 
					Base64.getEncoder().encodeToString(userAndPassword.getBytes()));
			this.conn.connect();
			
			if (this.conn.getResponseCode()!=200) {
				throw new RuntimeException("Failed: HTTP Error Code: " + this.conn.getResponseCode());
			}
			// Uncomment the below to see the Cookies generated from Signing in.
						
//			CookieStore cookieJar = manager.getCookieStore();
//			List <HttpCookie> cookies = cookieJar.getCookies();
//			for (HttpCookie cookie: cookies) {
//				System.out.println("CookieHandler retrieved cookie after sign-in: " + cookie);
//			}
			
			this.conn.disconnect();
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
			System.out.println("Error encountered during instantiation of URL");
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
			System.out.println("IOException encountered during connection open");
		}
		
	}
	
	public void signOut() {
		try {
			// Uncomment the below to see the Cookies managed in the Cookie Manager.
			
//			CookieManager manager = (CookieManager) CookieHandler.getDefault();
//			CookieStore cookieJar = manager.getCookieStore();
//			List <HttpCookie> cookies = cookieJar.getCookies();
//			for (HttpCookie cookie: cookies) {
//				System.out.println("CookieHandler retrieved cookie before signout: " + cookie);
//			}
			
			String urlString = this.getAlmAPIURL() + "/authentication/sign-out";	
			URL signInURL = new URL(urlString);
			this.conn = (HttpURLConnection)signInURL.openConnection();
			this.conn.setRequestMethod("POST");
			this.conn.addRequestProperty("Accept", "application/json");
			this.conn.connect();
			
			
//			cookieJar = manager.getCookieStore();
//			cookies = cookieJar.getCookies();
//			for (HttpCookie cookie: cookies) {
//				System.out.println("CookieHandler retrieved cookie after signout: " + cookie);
//			}
			
			if (this.conn.getResponseCode()!=200) {
				throw new RuntimeException("Failed: HTTP Error Code: " + this.conn.getResponseCode());
			}
			
			this.conn.disconnect();
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
			System.out.println("Error encountered during instantiation of URL");
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
			System.out.println("IOException encountered during connection open");
		}
		
	}
	
	// Note that ALM 12.50 provides a Tech Preview REST API to access entities other than Defects.
	// Accessing anything other than Defects requires this Tech Preview REST API.
	// Difference is that the URL is /qcbin/rest vs /qcbin/api 
	// 
	public void getEntityById(String type, String id) {
		try {
//			String userAndPassword = this.almUser + ":" + this.almPassword;
			String urlString = this.getAlmProjectRESTURL() + "/" + type + "s/" + id;	
			URL actionURL = new URL(urlString);
			this.conn = (HttpURLConnection)actionURL.openConnection();
			this.conn.setRequestMethod("GET");
			this.conn.addRequestProperty("Content-Type", "application/json");
			this.conn.addRequestProperty("Accept", "application/json");
//			this.conn.addRequestProperty("Authorization", "Basic " + 
//					Base64.getEncoder().encodeToString(userAndPassword.getBytes()));
			this.conn.connect();
			
			if (this.conn.getResponseCode()!=200) {
				throw new RuntimeException("Failed: HTTP Error Code: " + this.conn.getResponseCode() + ",Message:" + 
							this.conn.getResponseMessage());
			}
			
			BufferedReader br = new BufferedReader(new InputStreamReader(this.conn.getInputStream()));
			String output;
			System.out.println("Output from server: \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}
			
			this.conn.disconnect();
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
			System.out.println("Error encountered during instantiation of URL");
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
			System.out.println("IOException encountered during connection open");
		}
				
	}
	
	public void attachFileToEntity(String type, String id, String filename, String filepath) {
		try {
			
			if (filepath==null) {filepath="."; }
			else if (filepath.isEmpty()) {filepath="."; }
			
			File fileToAttach = new File(filepath + "\\" + filename);
			FileInputStream fis = new FileInputStream(fileToAttach);
			byte[] fileContent = new byte[(int)fileToAttach.length()];
			fis.read(fileContent);
			fis.close();
			
			String urlString = this.getAlmProjectRESTURL() + "/" + type + "s/" + id + "/attachments";
			URL actionURL = new URL(urlString);
			this.conn = (HttpURLConnection)actionURL.openConnection();
			this.conn.setRequestMethod("POST");
//			this.conn.addRequestProperty("Content-Type", "multipart/form-data");
//			this.conn.addRequestProperty("filename", filename);
//			this.conn.addRequestProperty("description", "sample test results file");
//			this.conn.addRequestProperty("override-existing-attachment", "y");
			
			this.conn.addRequestProperty("Content-Type", "application/octet-stream");
			this.conn.addRequestProperty("Slug", filename);
			//this.conn.addRequestProperty("data", Base64.getEncoder().encodeToString(fileContent));
			
			
			// this.conn.addRequestProperty("Accept", "application/json");
			this.conn.setDoOutput(true);
			OutputStream out = this.conn.getOutputStream();
			out.write(fileContent);
			
			this.conn.connect();
			
			// Response Code: 201 means file created
			if (this.conn.getResponseCode()!=201) {
				throw new RuntimeException("Failed: HTTP Error Code: " + this.conn.getResponseCode() + ",Message:" + 
							this.conn.getResponseMessage());
			}
			
			out.close();
			this.conn.disconnect();
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
			System.out.println("Error encountered during instantiation of URL");
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
			System.out.println("IOException encountered during connection open");
		}		
	}
	
    private String decrypt(String property) throws GeneralSecurityException, IOException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
        return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
    }

    private byte[] base64Decode(String property) throws IllegalArgumentException {
        // NB: This class is internal, and you probably should use another impl
    	return Base64.getDecoder().decode(property);
        // return new BASE64Decoder().decodeBuffer(property);
    }	
	
	public static App getInstance() {
		String host = System.getProperty("almHost");
		String port = System.getProperty("almPort");
		String protocol = System.getProperty("almProtocol");
		
		App app = new App();
    	app.setDefaults(host, port, protocol);
    	
    	return app;
	}
	
	public static App getInstance(String host, String port, String protocol) {
		App app = new App();
    	app.setDefaults(host, port, protocol);
    	
    	return app;
	}
	
	public static void main(String[] args) {

	}
}
