package com.stevenckwong.AlmRestfulClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/********************************************************
 * Code directly copied from mkyong at http://www.mkyong.com/java/how-to-compress-files-in-zip-format/
 * 
 * @author Administrator
 *
 */

public class AppZip
{
    List<String> fileList;
    private String outputZipFile;
    private String sourceFolder;
    private File fSrcFolder;
    private File fOutZipFile;
    
	
    AppZip(){
    	fileList = new ArrayList<String>();
    }
    
    AppZip(String srcFolder, String outZipFile) {
    	fileList = new ArrayList<String>();
    	fSrcFolder = new File(srcFolder);
    	fOutZipFile = new File(outZipFile);
    	sourceFolder = fSrcFolder.getAbsoluteFile().toString();
    	outputZipFile = fOutZipFile.getAbsoluteFile().toString();
    }
	
    static public AppZip getInstance(String srcFolder, String outZipFile) {
    	AppZip appzip = new AppZip(srcFolder, outZipFile);
    	return appzip;
    }
    
    public void generate() {
    	this.generateFileList(fSrcFolder);
    	this.zipIt(outputZipFile);
    }
    
    public static void main( String[] args )
    {
//    	AppZip appZip = new AppZip();
//    	appZip.generateFileList(new File(sourceFolder));
//    	appZip.zipIt(outputZipFile);
    }
    
    /**
     * Zip it
     * @param zipFile output ZIP file location
     */
    private void zipIt(String zipFile){

     byte[] buffer = new byte[1024];
    	
     try{
    		
    	FileOutputStream fos = new FileOutputStream(zipFile);
    	ZipOutputStream zos = new ZipOutputStream(fos);
    		
    	System.out.println("Output to Zip : " + zipFile);
    		
    	for(String file : this.fileList){
    			
    		System.out.println("File Added : " + file);
    		ZipEntry ze= new ZipEntry(file);
        	zos.putNextEntry(ze);
               
        	FileInputStream in = 
                       new FileInputStream(sourceFolder + File.separator + file);
       	   
        	int len;
        	while ((len = in.read(buffer)) > 0) {
        		zos.write(buffer, 0, len);
        	}
               
        	in.close();
    	}
    		
    	zos.closeEntry();
    	//remember close it
    	zos.close();
          
    	System.out.println("Done");
    }catch(IOException ex){
       ex.printStackTrace();   
    }
   }
    
    /**
     * Traverse a directory and get all files,
     * and add the file into fileList  
     * @param node file or directory
     */
    private void generateFileList(File node){

    	//add file only
	if(node.isFile()){
		String absFilename = node.getAbsoluteFile().toString();
    	System.out.println("Adding Zip Entry: " + absFilename);
    	String filename = generateZipEntry(absFilename);
    	if (filename != null)
    		fileList.add(filename);
		// fileList.add(generateZipEntry(node.get))
	}
		
	if(node.isDirectory()){
		String[] subNote = node.list();
		for(String filename : subNote){
			generateFileList(new File(node, filename));
		}
	}
 
    }

    /**
     * Format the file path for zip
     * @param file file path
     * @return Formatted file path
     */
    private String generateZipEntry(String file){
    	if (file==null) {
    		System.out.print("Adding Zip Entry: File is Null");
    		return null;
    	}
    	String filename = file.substring(sourceFolder.length()+1, file.length());
    	System.out.print("Adding Zip Entry:" + filename);
    	return filename;
    }
}