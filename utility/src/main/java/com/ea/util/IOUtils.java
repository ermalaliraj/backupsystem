package com.ea.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * I/O Util class
 */
public class IOUtils {

	private static final Log log = LogFactory.getLog(IOUtils.class);

	private IOUtils() {		
	}
	
	public static String normalizePath(String path) {
		final String SEP = "/";
		String ret = "";
		if (path == null || path.length() == 0) {
			ret = "." + SEP;
		} else {
			if (path.endsWith(SEP)) {
				ret = path;
			} else {
				ret = path + SEP;
			}
		}
		return ret;
	}

	public static void deleteFile(String filePath) {
		File f = new File(filePath);
		f.delete();
	}

	public static List<File> getFilesFromPath(String pathName, long maxNrFile, long maxBytes) throws FileNotFoundException {
		File folder = new File(pathName);
		if(folder.exists()){
			return getFilesFromPath(folder, maxNrFile, maxBytes);
		} else{
			throw new FileNotFoundException("Cannot find path '"+pathName+"'");
		}
	}

	public static List<File> getFilesFromPath(File folder, long maxNrFiles, long maxBytes) throws FileNotFoundException {
		List<File> list = new ArrayList<File>();
		if(folder.exists()){
			int count = 0;
			for (final File f : folder.listFiles()) {
				if (f.isDirectory()) {
					log.info("'"+f.getName()+"' is a directory. Will not add it to the list.");
				} else {
					if (f.length() <= maxBytes) {
						log.warn("'"+f.getName()+"' is to large. Will not be add to the list. Max byte permitted:"+maxBytes);
					} else {
						if (count < maxNrFiles) {
							list.add(f);
							count++;
						} else{
							//stop when count==maxNrFiles.
						}	
					}
				}
			}
		} else {
			throw new FileNotFoundException("Cannot find folder '"+folder+"'");
		}
		return list;
	}

	public static byte[] getFileContent(String pathName) throws IOException {
		Path path = Paths.get(pathName);
		byte[] data = Files.readAllBytes(path);
		return data;
	}

	public static byte[] getFileContent(File path) throws IOException {
		return getFileContent(path.toString());
	}

	public static void setFileContent(String fileName, byte[] content) throws IOException {
		setFileContent(new File(fileName), content);
	}

	public static void setFileContent(File fileName, byte[] content) throws IOException {
		FileOutputStream out = new FileOutputStream(fileName);
		try {
			if (content != null){
				out.write(content);
			}
		} finally {
			out.close();
		}
	}

	/**
	 * Close a Closeable object
	 */
	public static void closeResource(Closeable closeable) {
		try {
			if (closeable != null){
				closeable.close();
			}
		} catch (IOException e) {
			log.warn("Cannot close resource: " + closeable.toString(), e);
		}
	}

	public static boolean existsFile(String configPath) {
		if(new File(configPath).exists()){
			return true;
		} else {
			return false;	
		}
	}
}
