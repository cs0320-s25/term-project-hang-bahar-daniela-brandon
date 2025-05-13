package org.example.Handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;
import java.nio.file.Files;

import org.example.Posts.ImageURL;
import org.example.Posts.PostsDataSource;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

import spark.Request;
import spark.Response;
import spark.Route;

public class UploadPostImageHandler implements Route {
	private PostsDataSource dataSource;
	private Gson gson = new Gson();

	public UploadPostImageHandler(PostsDataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public Object handle(Request req, Response res) throws Exception {
		try {
			// Set up multipart config
			MultipartConfigElement multipartConfigElement = new MultipartConfigElement(
					System.getProperty("java.io.tmpdir"));
			req.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);

			Part filePart = req.raw().getPart("file");
			if (filePart == null) {
				res.status(400);
				return "Missing file part";
			}

			String fileName = filePart.getSubmittedFileName();
			if (fileName == null || fileName.isEmpty()) {
				res.status(400);
				return "Missing file name";
			}

			// Create a temporary file
			File file = File.createTempFile("upload-", "-" + fileName);
			try (InputStream inputStream = filePart.getInputStream()) {
                Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
			String imageUrl = dataSource.uploadImage(file);
			res.status(200);
			return gson.toJson(new ImageURL(imageUrl));

		} catch (IllegalStateException e) {
			res.status(400);
			return "Error: " + e.getMessage();
		} catch (java.io.IOException e) {
			res.status(500);
			return "Error processing request: " + e.getMessage();
		} catch (Exception e) {
			res.status(500);
			return "Error processing request: " + e.getMessage();
		}
	}
}