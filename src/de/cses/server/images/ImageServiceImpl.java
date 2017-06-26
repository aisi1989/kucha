/*
 * Copyright 2016 
 * Saxon Academy of Science in Leipzig, Germany
 * 
 * This is free software: you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License version 3 (GPL v3) as published by the Free Software Foundation.
 * 
 * This software is distributed WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. Please read the GPL v3 for more details.
 * 
 * You should have received a copy of the GPL v3 along with the software. 
 * If not, you can access it from here: <https://www.gnu.org/licenses/gpl-3.0.txt>.
 */
package de.cses.server.images;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.output.ByteArrayOutputStream;

import de.cses.server.ServerProperties;
import de.cses.server.mysql.MysqlConnector;
import de.cses.shared.ImageEntry;

/**
 * This HttpServlet is used to upload images to the server's main image directory. It also creates a new entry in the Images table of the
 * database.
 * 
 * @author alingnau
 *
 */
@SuppressWarnings("serial")
@MultipartConfig
public class ImageServiceImpl extends HttpServlet {

	private static final int THUMBNAIL_SIZE = 300;
	private MysqlConnector connector = MysqlConnector.getInstance();
	private ServerProperties serverProperties = ServerProperties.getInstance();
	private int newImageID = 0;
	private ImageEntry ie;

	public ImageServiceImpl() {
		super();
	}

	/**
	 * This method is called when the submit button in the image uploader is pressed. Images are stored in the SERVER_IMAGES_PATHNAME
	 * 
	 * @see de.cses.client.images.ImageUploader
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String uploadFileName;
		String fileType, filename=null;

		response.setContentType("text/plain");
		File imgHomeDir = new File(serverProperties.getProperty("home.images"));
		if (!imgHomeDir.exists()) {
			imgHomeDir.mkdirs();
		}
		FileItemFactory factory = new DiskFileItemFactory(1000000, imgHomeDir);
		ServletFileUpload upload = new ServletFileUpload(factory);
		File target = null;
		try {
			try {
				List<?> items = upload.parseRequest(request);
				Iterator<?> it = items.iterator();
				while (it.hasNext()) {
					FileItem item = (FileItem) it.next();
					uploadFileName = item.getName();
					// we take the sub dir from the field name which corresponds with the
					// purpose of the upload (e.g. depictions, backgrounds, ...)
					fileType = uploadFileName.substring(uploadFileName.lastIndexOf(".")).toLowerCase();
					if (item.isFormField()) {
						throw new ServletException("Unsupported non-file property [" + item.getFieldName() + "] with value: " + item.getString());
					} else {
						newImageID = connector.createNewImageEntry();
						if (newImageID > 0) {
							filename = newImageID + fileType;
							ie = connector.getImageEntry(newImageID);
							ie.setFilename(filename);
							connector.updateEntry(ie.getSqlUpdate(ImageEntry.FILENAME));
							target = new File(imgHomeDir, filename);
							item.write(target);
							item.delete();
						}
//						if (filename.endsWith("tif") || filename.endsWith("tiff")) {
//							final BufferedImage tif = ImageIO.read(target);
//							filename = newImageID + ".png";
//					    ImageIO.write(tif, "png", new File(imgHomeDir, filename));
//					    ie.setFilename(filename);
//					    connector.updateEntry(ie.getSqlUpdate(ImageEntry.FILENAME));
//					    target.delete();
//					  }
					}
				}
			} catch (ServletException e) {
				System.err.println("ServletException");
			} catch (Exception e) {
				System.err.println("IllegalStateException");
				throw new IllegalStateException(e);
			}
		} finally {
			if (target != null && target.exists()) {
				System.err.println("Uploaded file: " + target.getAbsolutePath());
			  createThumbnail(target, new File(imgHomeDir, "tn" + newImageID + ".png"));
				response.getWriter().write(String.valueOf(newImageID));
				response.getWriter().close();
			}
		}
	}

	/**
	 * Create a thumbnail image file with a max side length of THUMBNAIL_SIZE
	 * 
	 * @param path
	 *          the file of the image
	 * @param tnFile
	 *          the new thumbnail file
	 */
	private void createThumbnail(File readFile, File tnFile) {
		String type = "png";
		BufferedImage tnImg;

		try {
			// we need to call the scanner in order to detect the additional libraries
			// the libraries used are from https://haraldk.github.io/TwelveMonkeys/
			ImageIO.scanForPlugins();
			
			BufferedImage buf = ImageIO.read(readFile);
			float w = buf.getWidth();
			float h = buf.getHeight();
			System.err.println("w=" + w + " h=" + h);
			if (w == h) {
				tnImg = new BufferedImage(THUMBNAIL_SIZE, THUMBNAIL_SIZE, BufferedImage.TYPE_INT_RGB);
				tnImg.createGraphics().drawImage(buf.getScaledInstance(THUMBNAIL_SIZE, THUMBNAIL_SIZE, Image.SCALE_SMOOTH), 0, 0, null);
			} else if (w > h) {
				float factor = THUMBNAIL_SIZE / w;
				float tnHeight = h * factor;
				tnImg = new BufferedImage(THUMBNAIL_SIZE, Math.round(tnHeight), BufferedImage.TYPE_INT_RGB);
				tnImg.createGraphics().drawImage(buf.getScaledInstance(THUMBNAIL_SIZE, Math.round(tnHeight), Image.SCALE_SMOOTH), 0, 0, null);
			} else {
				float factor = THUMBNAIL_SIZE / h;
				float tnWidth = w * factor;
				tnImg = new BufferedImage(Math.round(tnWidth), THUMBNAIL_SIZE, BufferedImage.TYPE_INT_RGB);
				tnImg.createGraphics().drawImage(buf.getScaledInstance(Math.round(tnWidth), THUMBNAIL_SIZE, Image.SCALE_SMOOTH), 0, 0, null);
			}
			ImageIO.write(tnImg, type, tnFile);
		} catch (IOException e) {
			System.err.println("I/O Exception - thumbnail could not be created!");
		} catch (OutOfMemoryError e) {
			System.err.println("An OutOfMemoryError has occurred while scaling the image!");
		} catch (Exception e) {
			System.err.println("An unknown exception occurred during thumbnail creation!");
		}
	}
	
}
