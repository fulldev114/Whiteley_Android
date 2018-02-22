package com.wai.whiteley.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.wai.whiteley.config.Constants;

public class FileUtil {

	public static String getBackgroundFilePath(Context context) {
		String tempDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.IMAGE_PATH;
		String tempFileName = Constants.BACKGROUND_IMAGE_FILENAME;

		File tempDir = new File(tempDirPath);
		if (!tempDir.exists())
			tempDir.mkdirs();
		File tempFile = new File(tempDirPath + tempFileName);
		if (!tempFile.exists())
			try {
				tempFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		return tempDirPath + tempFileName;
	}
	
	public static boolean downloadFileURL(Context context, String url) {
		
        if(TextUtils.isEmpty(url))
        	return false;
        
        try {
        	URL urlLabel = new URL(url);
        	File fileLabel = new File(context.getFilesDir(), urlLabel.getFile());
        	
        	if(fileLabel.exists())
        		return true;
        	
        	if(!fileLabel.getParentFile().exists()) {
        		fileLabel.getParentFile().mkdirs();
        	}
        	
            InputStream is = urlLabel.openStream();
            FileOutputStream fileOutput = new FileOutputStream(fileLabel);

            byte[] buffer = new byte[1024];
            int bufferLength = 0; // used to store a temporary

            while ((bufferLength = is.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);
            }

            fileOutput.close();
            is.close();
            
            return true;
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
