/**
 * 
 */
package com.legato.services.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.legato.services.service.CommonService;

/**
 * @author AF83580
 *
 */

@Service
public class CommonServiceImpl implements CommonService {
	@Value("${profile.picture.path}")
	private String profilePicturePath;
	@Override
	public boolean saveFile(File file, String path) {
		return false;
	}

	@Override
	public boolean saveFile(MultipartFile file, String strPath, String fileName) throws IOException {
		byte[] bytes = file.getBytes();
		File profileDir = new File(System.getProperty("user.home") + File.separator + profilePicturePath);
		if(!profileDir.exists()) profileDir.mkdirs();
		File profile = new File(profileDir.getAbsolutePath() + File.separator 
				+ (StringUtils.isBlank(fileName) ? file.getOriginalFilename() : fileName) 
				+ "." + FilenameUtils.getExtension(file.getOriginalFilename()));
		boolean validFile = false;
		if(!profile.exists()) validFile = profile.createNewFile();
		if(validFile) {
			Path path1 = Paths.get(profile.getAbsolutePath());
	        Files.write(path1, bytes);
	        return true;
		}return false;
	}
}