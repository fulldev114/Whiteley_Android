package com.wai.whiteley;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Environment;

public class AppUtil {

	private static final boolean WRITE_ENABLE = true;
	public static void wiriteDebugLog(String tag, String msg) {
		if (WRITE_ENABLE) {
			//Log.d(tag, msg);
			String sdstr = Environment.getExternalStorageDirectory().getPath();
			if (sdstr != null)
				sdstr += "/Whiteley/";
	
			File sdcard = new File(sdstr);
			sdcard.mkdirs();
			File file = new File(sdcard, "log");
	
			if (file.exists() == false) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	
			try {
				// BufferedWriter for performance, true to set append to file flag
				BufferedWriter buf = new BufferedWriter(new FileWriter(file, true));
				String strAddr = "Tag : " + tag + "\n";
				buf.append(strAddr);
				String strData = "Msg : " + msg + "\n";
				buf.append(strData);
				buf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void deleteLogFile() {
		String sdstr = Environment.getExternalStorageDirectory().getPath();
		if (sdstr != null)
			sdstr += "/Whiteley/";

		File sdcard = new File(sdstr);
		sdcard.mkdirs();
		File file = new File(sdcard, "log");

		if (file.exists()) {
			try {
				file.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}