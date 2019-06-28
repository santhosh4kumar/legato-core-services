/**
 * 
 */
package com.legato.services.service;

import java.io.File;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author AF83580
 *
 */
public interface CommonService {
	boolean saveFile(File file, String path);
	boolean saveFile(MultipartFile file, String path, String fileName) throws IOException;
}