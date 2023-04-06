package com.ayush.smartcontact.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileUploadHelper {

    public final String UPLOAD_DIR = new ClassPathResource("static/image/").getFile().getAbsolutePath();
    
    public FileUploadHelper() throws IOException
    {
    }



    public boolean upload_file(MultipartFile file, String filename){

        try {
            Files.copy(file.getInputStream(), Paths.get(UPLOAD_DIR+ File.separator +filename), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } 
        return true;
    }
    
    public boolean delete_file(String filename) {
    	try {
    		Files.delete(Paths.get(UPLOAD_DIR+File.separator+filename));
    	} catch (Exception e) {
    		e.printStackTrace();
    		return false;
    	}
    	return true;
    }
}