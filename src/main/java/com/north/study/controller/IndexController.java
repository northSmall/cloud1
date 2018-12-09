package com.north.study.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {
 
	private static final String FAR_SERVICE_DIR = "http://192.168.30.39:8080/receive";// 远程服务器接受文件的路由
	private static final long yourMaxRequestSize = 10000000;
 
	@RequestMapping("/")
	@ResponseBody
	public String index(Model model) throws IOException {
		return "/index";
	}
 
	@RequestMapping("/upload")
	public String upload(HttpServletRequest request) throws Exception {
		// 判断enctype属性是否为multipart/form-data
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (!isMultipart)
			throw new IllegalArgumentException(
					"上传内容不是有效的multipart/form-data类型.");
 
		// Create a factory for disk-based file items
		DiskFileItemFactory factory = new DiskFileItemFactory();
 
		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);
 
		// 设置上传内容的大小限制（单位：字节）
		upload.setSizeMax(yourMaxRequestSize);
 
		// Parse the request
		List<?> items = upload.parseRequest(request);
 
		Iterator iter = items.iterator();
		while (iter.hasNext()) {
			FileItem item = (FileItem) iter.next();
 
			if (item.isFormField()) {
				// 如果是普通表单字段
				String name = item.getFieldName();
				String value = item.getString();
				// ...
			} else {
				// 如果是文件字段
				String fieldName = item.getFieldName();
				String fileName = item.getName();
				String contentType = item.getContentType();
				boolean isInMemory = item.isInMemory();
				long sizeInBytes = item.getSize();
				// ...
 
				// 上传到远程服务器
				HashMap<String, FileItem> files = new HashMap<String, FileItem>();
				files.put(fileName, item);
				uploadToFarServiceByHttpClient(files);
			}
		}
		return "redirect:/";
	}
 
	private void uploadToFarServiceByHttpClient(HashMap<String, FileItem> files) {
		HttpClient httpclient = new DefaultHttpClient();
		try {
			HttpPost httppost = new HttpPost(FAR_SERVICE_DIR);
			MultipartEntity reqEntity = new MultipartEntity();
			Iterator iter = files.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String key = (String) entry.getKey();
				FileItem val = (FileItem) entry.getValue();
				FileBody filebdody = new FileBody(inputstreamtofile(
						val.getInputStream(), key));
				reqEntity.addPart(key, filebdody);
			}
			httppost.setEntity(reqEntity);
 
			HttpResponse response = httpclient.execute(httppost);
 
			int statusCode = response.getStatusLine().getStatusCode();
 
			if (statusCode == HttpStatus.SC_OK) {
				System.out.println("服务器正常响应.....");
				HttpEntity resEntity = response.getEntity();
				System.out.println(EntityUtils.toString(resEntity,"UTF-8"));// httpclient自带的工具类读取返回数据
				EntityUtils.consume(resEntity);
			}
 
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				httpclient.getConnectionManager().shutdown();
			} catch (Exception ignore) {
 
			}
		}
 
	}
 
	public File inputstreamtofile(InputStream ins, String filename)
			throws IOException {
		File file = new File(filename);
		OutputStream os = new FileOutputStream(file);
		int bytesRead = 0;
		byte[] buffer = new byte[8192];
		while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
			os.write(buffer, 0, bytesRead);
		}
		os.close();
		ins.close();
		return file;
	}
 
}