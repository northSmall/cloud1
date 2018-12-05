package com.example.demo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;

public class DownloadTest {

	@Test
	public void download() throws URISyntaxException, ClientProtocolException, IOException {
		
		 HttpClientBuilder builder = HttpClientBuilder.create();
		 HttpGet httpget = new HttpGet(
				 new URI("https://i10.hoopchina.com.cn/hupuapp/bbs/552/34804552/thread_34_34804552_20181124143316_5840.jpg"));
		 CloseableHttpClient build = builder.build();
		 CloseableHttpResponse execute = build.execute(httpget);
		 File storeFile = new File("G:\\abcd.jpg");
         FileOutputStream output = new FileOutputStream(storeFile);
		 HttpEntity entity = execute.getEntity();
		 if (entity != null) {
             InputStream instream = entity.getContent();
             try {
                 byte b[] = new byte[1024];
                 int j = 0;
                 while( (j = instream.read(b))!=-1){
                     output.write(b,0,j);
                 }
                 output.flush();
                 output.close();
             } catch (IOException ex) {
                 // In case of an IOException the connection will be released
                 // back to the connection manager automatically
                 throw ex;
             } catch (RuntimeException ex) {
                 // In case of an unexpected exception you may want to abort
                 // the HTTP request in order to shut down the underlying
                 // connection immediately.
                 httpget.abort();
                 throw ex;
             } finally {
                 // Closing the input stream will trigger connection release
                 try { instream.close(); } catch (Exception ignore) {}
             }
//             if (storeFile.exists()) {
//                 BufferedImage newImage = ImageUtil.getFileImage(storeFile, width, height);
//                 ImageIO.write(newImage, picType, storeFile);
//                 picSrc = "http://"+ JsonResponseHelper.serverAddress+"/wamei/"+savePath+fileName;
//             }

         }
		 System.out.println(entity);
		 
	}
}
