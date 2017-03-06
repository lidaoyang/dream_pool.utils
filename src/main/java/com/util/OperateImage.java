package com.util;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class OperateImage {

	// ===源图片路径名称如：c:\1.jpg
	private String srcpath;

	// ===剪切图片存放路径名称。如：c:\2.jpg
	private String subpath;
	public void setSrcpath(String srcpath) {
		this.srcpath = srcpath;
	}
	public void setSubpath(String subpath) {
		this.subpath = subpath;
	}
	// ===剪切点x坐标
	private int x;

	private int y;

	// ===剪切点宽度
	private int width;
	private int height;

	public OperateImage() {
	}

	public OperateImage(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	/**
	 * 
	 * 对图片裁剪，并把裁剪完蛋新图片保存 。
	 */

	public void cut() throws IOException {
		FileInputStream is = null;
		ImageInputStream iis = null;
		try {
			// 读取图片文件
			is = new FileInputStream(srcpath);

			/**
			 * 
			 * 返回包含所有当前已注册 ImageReader 的 Iterator，这些 ImageReader
			 * 
			 * 声称能够解码指定格式。 参数：formatName - 包含非正式格式名称 .
			 * 
			 * (例如 "jpeg" 或 "tiff")等 。
			 */
			Iterator<ImageReader> it = ImageIO
					.getImageReadersByFormatName("jpg");

			ImageReader reader = it.next();

			// 获取图片流
			iis = ImageIO.createImageInputStream(is);

			/**
			 * 
			 * <p>
			 * iis:读取源。true:只向前搜索
			 * </p>
			 * .将它标记为 ‘只向前搜索’。
			 * 
			 * 此设置意味着包含在输入源中的图像将只按顺序读取，可能允许 reader
			 * 
			 * 避免缓存包含与以前已经读取的图像关联的数据的那些输入部分。
			 */
			reader.setInput(iis, true);

			/**
			 * 
			 * <p>
			 * 描述如何对流进行解码的类
			 * <p>
			 * .用于指定如何在输入时从 Java Image I/O
			 * 
			 * 框架的上下文中的流转换一幅图像或一组图像。用于特定图像格式的插件
			 * 
			 * 将从其 ImageReader 实现的 getDefaultReadParam 方法中返回
			 * 
			 * ImageReadParam 的实例。
			 */
			ImageReadParam param = reader.getDefaultReadParam();

			/**
			 * 
			 * 图片裁剪区域。Rectangle 指定了坐标空间中的一个区域，通过 Rectangle 对象
			 * 
			 * 的左上顶点的坐标(x，y)、宽度和高度可以定义这个区域。
			 */
			Rectangle rect = new Rectangle(x, y, width, height);

			// 提供一个 BufferedImage，将其用作解码像素数据的目标。
			param.setSourceRegion(rect);

			/**
			 * 
			 * 使用所提供的 ImageReadParam 读取通过索引 imageIndex 指定的对象，并将
			 * 
			 * 它作为一个完整的 BufferedImage 返回。
			 */
			BufferedImage bi = reader.read(0, param);

			// 保存新图片
			ImageIO.write(bi, "jpg", new File(subpath));
		} finally {
			if (is != null)
				is.close();
			if (iis != null)
				iis.close();
		}

	}
	/**
	 * 
	 * 对图片裁剪，并把裁剪完蛋新图片保存 。
	 * @throws Exception 
	 */

	public void cut(InputStream is,int pix) throws Exception {
		ImageInputStream iis = null;
		try {
			// 读取图片文件
//			is = new FileInputStream(srcpath);
			//new一个文件对象用来保存图片，默认保存当前工程根目录  
//	        File imageFile = new File("C:\\Users\\liutaoq\\Desktop\\cc.jpg");  
//	        //创建输出流  
//	        FileOutputStream os = new FileOutputStream(imageFile);  
//	        os.flush();
//	        is = new FileInputStream(imageFile);
			//TODO 对is文件流处理 --- 等比压缩图片
//			resizeImage(is, os, size, "jpg");
			byte[] b = readInputStream(is,pix);
            
			//TODD 将压缩后 的b字节数据，生产输入流
			/**
			 * 
			 * 返回包含所有当前已注册 ImageReader 的 Iterator，这些 ImageReader
			 * 
			 * 声称能够解码指定格式。 参数：formatName - 包含非正式格式名称 .
			 * 
			 * (例如 "jpeg" 或 "tiff")等 。
			 */
			Iterator<ImageReader> it = ImageIO
					.getImageReadersByFormatName("jpg");

			ImageReader reader = it.next();

			// 获取图片流
			is = new ByteArrayInputStream(b);
			iis = ImageIO.createImageInputStream(is);

			/**
			 * 
			 * <p>
			 * iis:读取源。true:只向前搜索
			 * </p>
			 * .将它标记为 ‘只向前搜索’。
			 * 
			 * 此设置意味着包含在输入源中的图像将只按顺序读取，可能允许 reader
			 * 
			 * 避免缓存包含与以前已经读取的图像关联的数据的那些输入部分。
			 */
			reader.setInput(iis, true);

			/**
			 * 
			 * <p>
			 * 描述如何对流进行解码的类
			 * <p>
			 * .用于指定如何在输入时从 Java Image I/O
			 * 
			 * 框架的上下文中的流转换一幅图像或一组图像。用于特定图像格式的插件
			 * 
			 * 将从其 ImageReader 实现的 getDefaultReadParam 方法中返回
			 * 
			 * ImageReadParam 的实例。
			 */
			ImageReadParam param = reader.getDefaultReadParam();

			/**
			 * 
			 * 图片裁剪区域。Rectangle 指定了坐标空间中的一个区域，通过 Rectangle 对象
			 * 
			 * 的左上顶点的坐标(x，y)、宽度和高度可以定义这个区域。
			 */
			Rectangle rect = new Rectangle(x, y, width, height);

			// 提供一个 BufferedImage，将其用作解码像素数据的目标。
			param.setSourceRegion(rect);

			/**
			 * 
			 * 使用所提供的 ImageReadParam 读取通过索引 imageIndex 指定的对象，并将
			 * 
			 * 它作为一个完整的 BufferedImage 返回。
			 */
			BufferedImage bi = reader.read(0, param);

			// 保存新图片
			ImageIO.write(bi, "jpg", new File(subpath));
		} finally {
			if (is != null)
				is.close();
			if (iis != null)
				iis.close();
		}

	}
	
	

	public  byte[] readInputStream(InputStream inStream,int pix) throws Exception{  
		
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        
        resizeImage(inStream, outStream, pix, "jpg");
        //创建一个Buffer字符串  
        byte[] buffer = new byte[1024];  
        //每次读取的字符串长度，如果为-1，代表全部读取完毕  
        int len = 0;  
        //使用一个输入流从buffer里把数据读取出来  
        while( (len=inStream.read(buffer)) != -1 ){  
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度  
            outStream.write(buffer, 0, len);  
        }  
        //关闭输入流  
        inStream.close();  
        //把outStream里的数据写入内存  
        return outStream.toByteArray();  
    }  

	public void resizeImage(InputStream is, OutputStream os, int size,
			String format) throws IOException {
		BufferedImage prevImage = ImageIO.read(is);
		double width = prevImage.getWidth();
		double height = prevImage.getHeight();
		double percent = size / height;
		int newWidth = (int) (width * percent);
		int newHeight = (int) (height * percent);
		BufferedImage image = new BufferedImage(newWidth, newHeight,
				BufferedImage.TYPE_INT_BGR);
		Graphics graphics = image.createGraphics();
		graphics.drawImage(prevImage, 0, 0, newWidth, newHeight, null);
		ImageIO.write(image, format, os);
		os.flush();
//		is.close();
		os.close();
//		return prevImage;
	}
	
	
	public static void main(String[] args) throws Exception {
        int pix  = 900;
        FileInputStream fis = new FileInputStream("C:\\Users\\Daoyang\\Desktop\\pic\\7.jpg");
        OperateImage o = new OperateImage(0, 0, pix,800);
//		o.setSrcpath(imageStr);
		o.setSubpath("C:\\Users\\Daoyang\\Desktop\\pic\\22222.jpg");
		o.cut(fis,pix);
        
		
       /* String imageStr = "C:\\Users\\liutaoq\\Workspaces\\MyEclipse 9-1\\demo\\source_754x450_0.jpg"; 
		OperateImage o = new OperateImage(0, 0, 470, 470);
		o.setSrcpath(imageStr);
		o.setSubpath("C:\\Users\\liutaoq\\Desktop\\11.jpg");
		o.cut();*/
	}

}
