package com.srm.platform.vendor.utility;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class UploadFileHelper {

	private static String FILE_UPLOAD_PATH;

	@Value("${srm.file.upload.dir}")
	public void setUploadFilePath(String uploadPath) {
		FILE_UPLOAD_PATH = uploadPath;
	}

	public static Resource getResource(String path) {
		Path rootLocation = Paths.get(FILE_UPLOAD_PATH);
		Path file = rootLocation.resolve(path);

		Resource resource;
		try {
			resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	public static File simpleUpload(MultipartFile file, HttpServletRequest request, boolean encrypt_file_name,
			String Upload_folder) {

		String filename = null;
		File serverFile = null;
		try {

			if (!file.isEmpty())

			{
				String applicationPath = FILE_UPLOAD_PATH;
				System.out.println("upload_path=" + applicationPath);
				if (encrypt_file_name) {
					String currentFileName = file.getOriginalFilename();
					String extention = currentFileName.substring(currentFileName.lastIndexOf("."),
							currentFileName.length());
					Long nameRadom = Calendar.getInstance().getTimeInMillis();
					String newfilename = nameRadom + extention;
					filename = newfilename;
				} else
					filename = file.getOriginalFilename();
				byte[] bytes = file.getBytes();
				String rootPath = applicationPath;
				File dir = new File(rootPath + File.separator + Upload_folder);
				if (!dir.exists())
					dir.mkdirs();
				serverFile = new File(dir.getAbsolutePath() + File.separator + filename);
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(bytes);
				stream.close();
				return serverFile;
			} else {
				serverFile = null;
			}
		} catch (Exception e) {
			// TODO: handle exception
			serverFile = null;

		}
		return serverFile;
	}

	public static List<String> MultipleFileUpload(List<MultipartFile> files, HttpServletRequest request,
			boolean encrypt_file_name, String Upload_folder) {
		List<String> filenames = new ArrayList<>();
		try {
			if (files.size() != 0) {
				BufferedOutputStream stream = null;
				String applicationPath = request.getServletContext().getRealPath("");
				for (MultipartFile file : files) {
					String filename = "";
					if (encrypt_file_name) {
						String currentFileName = file.getOriginalFilename();
						String extention = currentFileName.substring(currentFileName.lastIndexOf("."),
								currentFileName.length());
						Long nameRadom = Calendar.getInstance().getTimeInMillis();
						String newfilename = nameRadom + extention;
						filename = newfilename;
					} else
						filename = file.getOriginalFilename();
					byte[] bytes = file.getBytes();

					String rootPath = applicationPath;
					File dir = new File(rootPath + File.separator + Upload_folder);
					if (!dir.exists())
						dir.mkdirs();
					File serverFile = new File(dir.getAbsolutePath() + File.separator + filename);
					stream = new BufferedOutputStream(new FileOutputStream(serverFile));
					stream.write(bytes);
					System.out.println(filename);
					filenames.add(filename);
				}
				stream.close();
			} else {
				filenames = null;
			}
		} catch (Exception e)

		{
			System.out.println(e.getMessage());
			filenames = null;
		}
		return filenames;

	}
}
