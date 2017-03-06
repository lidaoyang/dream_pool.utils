package com.util;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class ImageCompressUtil {
	/**
	 * 直接指定压缩后的宽高： (先保存原文件，再压缩、上传) 
	 * 
	 * @param oldFile 要进行压缩的文件全路径
	 * @param width 压缩后的宽度
	 * @param height 压缩后的高度
	 * @param quality 压缩质量
	 * @param smallIcon 文件名的小小后缀(注意，非文件后缀名称),入压缩文件名是yasuo.jpg,则压缩后文件名是yasuo(+smallIcon ).jpg
	 * @return 返回压缩后的文件的全路径
	 */
	public static String zipImageFile(String oldFile, int width, int height,
			float quality, String smallIcon) {
		if (oldFile == null) {
			return null;
		}
		String newImage = null;
		try {
			/** 对服务器上的临时文件进行处理 */
			Image srcFile = ImageIO.read(new File(oldFile));
			/** 宽,高设定 */
			BufferedImage tag = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
			tag.getGraphics().drawImage(srcFile, 0, 0, width, height, null);
			String filePrex = oldFile.substring(0, oldFile.indexOf('.'));
			/** 压缩后的文件名 */
			newImage = filePrex + smallIcon + oldFile.substring(filePrex.length());
			/** 压缩之后临时存放位置 */
			FileOutputStream out = new FileOutputStream(newImage);
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(tag);
			/** 压缩质量 */
			jep.setQuality(quality, true);
			encoder.encode(tag, jep);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return newImage;
	}

	/**
	 * 保存文件到服务器临时路径(用于文件上传)
	 * 
	 * @param fileName
	 * @param is
	 * @return 文件全路径
	 */
	public static String writeFile(String fileName, InputStream is) {
		if (fileName == null || fileName.trim().length() == 0) {
			return null;
		}
		try {
			/** 首先保存到临时文件 */
			FileOutputStream fos = new FileOutputStream(fileName);
			byte[] readBytes = new byte[512];// 缓冲大小
			int readed = 0;
			while ((readed = is.read(readBytes)) > 0) {
				fos.write(readBytes, 0, readed);
			}
			fos.close();
			is.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileName;
	}
	/**
	 * 等比例压缩算法： 算法思想：根据压缩基数和压缩比来压缩原图，生产一张图片效果最接近原图的缩略图
	 * 
	 * @param srcURL 原图地址
	 * @param deskURL 缩略图地址(文件夹名称)
	 * @param fname 缩略图文件名称
	 * @param comBase 压缩基数
	 * @param scale 压缩限制(宽/高)比例 一般用1：当scale>=1,缩略图height=comBase,width按原图宽高比例;若scale<1,缩略图width=comBase,height按原图宽高比例
	 * @throws Exception
	 * @author shenbin
	 * @createTime 2014-12-16
	 * @lastModifyTime 2014-12-16
	 */
	/*public static void resizeFix(String srcURL, String deskURL,String fname,
			double comBase, double scale) throws Exception{
		FileInputStream fis = new FileInputStream(srcURL);
		resizeFix(fis, deskURL,fname, comBase, scale);
	}*/
	/**
	 * 等比例压缩算法： 算法思想：根据压缩基数和压缩比来压缩原图，生产一张图片效果最接近原图的缩略图
	 * 
	 * @param is 原图文件流
	 * @param deskURL 缩略图地址(文件夹名称)
	 * @param fname 缩略图文件名称
	 * @param comBase 压缩基数
	 * @param scale 压缩限制(宽/高)比例 一般用1：当scale>=1,缩略图height=comBase,width按原图宽高比例;若scale<1,缩略图width=comBase,height按原图宽高比例
	 * @throws Exception
	 * @author shenbin
	 * @createTime 2014-12-16
	 * @lastModifyTime 2014-12-16
	 */
	/*public static void resizeFix(InputStream is, String deskURL,String fname,
			double comBase, double scale) throws Exception {
		Image src = ImageIO.read(is);
		int srcHeight = src.getHeight(null);
		int srcWidth = src.getWidth(null);
		int deskHeight = 0;// 缩略图高
		int deskWidth = 0;// 缩略图宽
		double srcScale = (double) srcHeight / srcWidth;
		*//** 缩略图宽高算法 *//*
		if ((double) srcHeight > comBase || (double) srcWidth > comBase) {
			if (srcScale >= scale || 1 / srcScale > scale) {
				if (srcScale >= scale) {
					deskHeight = (int) comBase;
					deskWidth = srcWidth * deskHeight / srcHeight;
				} else {
					deskWidth = (int) comBase;
					deskHeight = srcHeight * deskWidth / srcWidth;
				}
			} else {
				if ((double) srcHeight > comBase) {
					deskHeight = (int) comBase;
					deskWidth = srcWidth * deskHeight / srcHeight;
				} else {
					deskWidth = (int) comBase;
					deskHeight = srcHeight * deskWidth / srcWidth;
				}
			}
		} else {
			deskHeight = srcHeight;
			deskWidth = srcWidth;
		}
		BufferedImage tag = new BufferedImage(deskWidth, deskHeight,BufferedImage.TYPE_3BYTE_BGR);
		tag.getGraphics().drawImage(src, 0, 0, deskWidth, deskHeight, null); // 绘制缩小后的图
		String base = Constant.IMAGE_BASE_PATH;
		String filepath = base + "/" + deskURL;
		File file = new File(filepath);
		if (!file.exists()) {
			file.mkdirs();
		}
		File dest = new File(filepath, fname);
		FileOutputStream deskImage = new FileOutputStream(dest); // 输出到文件流
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(deskImage);
		encoder.encode(tag); // 近JPEG编码
		deskImage.close();
	}*/
	 public static void cutJPG(InputStream input, OutputStream out, int x,  
	            int y, int width, int height) throws IOException {  
	        ImageInputStream imageStream = null;  
	        try {  
	            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("jpg");  
	            ImageReader reader = readers.next();  
	            imageStream = ImageIO.createImageInputStream(input);  
	            reader.setInput(imageStream, true);  
	            ImageReadParam param = reader.getDefaultReadParam();  
	              
	            System.out.println(reader.getWidth(0));  
	            System.out.println(reader.getHeight(0));  
	            Rectangle rect = new Rectangle(x, y, width, height);  
	            param.setSourceRegion(rect);  
	            BufferedImage bi = reader.read(0, param);  
	            ImageIO.write(bi, "jpg", out);  
	        } finally {  
	            imageStream.close();  
	        }  
	    }  
	      
	      
	    public static void cutPNG(InputStream input, OutputStream out, int x,  
	            int y, int width, int height) throws IOException {  
	        ImageInputStream imageStream = null;  
	        try {  
	            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("png");  
	            ImageReader reader = readers.next();  
	            imageStream = ImageIO.createImageInputStream(input);  
	            reader.setInput(imageStream, true);  
	            ImageReadParam param = reader.getDefaultReadParam();  
	              
	            System.out.println(reader.getWidth(0));  
	            System.out.println(reader.getHeight(0));  
	              
	            Rectangle rect = new Rectangle(x, y, width, height);  
	            param.setSourceRegion(rect);  
	            BufferedImage bi = reader.read(0, param);  
	            ImageIO.write(bi, "png", out);  
	        } finally {  
	            imageStream.close();  
	        }  
	    }  
	    /**
		 * 
		 * 图片裁剪区域。Rectangle 指定了坐标空间中的一个区域，通过 Rectangle 对象
		 * 左上顶点的坐标(x，y)、宽度和高度可以定义这个区域。
		 * @param comBase 压缩基数
		 * @param scale 压缩限制(宽/高)比例 一般用1：当scale>=1,缩略图height=comBase,width按原图宽高比例;若scale<1,缩略图width=comBase,height按原图宽高比例
		 */
	/*public static void cutImage(InputStream input, String deskURL,
			String fname, String type, int x, int y, int width, int height,
			double comBase, double scale) throws IOException {
		String base = Constant.IMAGE_BASE_PATH;
		String filepath = base + "/" + deskURL;
		File file = new File(filepath);
		if (!file.exists()) {
			file.mkdirs();
		}
		File dest = new File(filepath, fname);
		FileOutputStream out = new FileOutputStream(dest); // 输出到文件流

		ImageInputStream imageStream = null;
		String imageType = (null == type || "".equals(type)) ? "jpg" : type;
		try {
			// 对is文件流处理 --- 等比压缩图片
			byte[] b = readInputStream(input, comBase, scale);

			// TODD 将压缩后 的b字节数据，生产输入流
			*//**
			 * 
			 * 返回包含所有当前已注册 ImageReader 的 Iterator，这些 ImageReader
			 * 
			 * 声称能够解码指定格式。 参数：formatName - 包含非正式格式名称 .
			 * 
			 * (例如 "jpeg" 或 "tiff")等 。
			 *//*
			Iterator<ImageReader> it = ImageIO
					.getImageReadersByFormatName(imageType);

			ImageReader reader = it.next();

			// 获取图片流
			input = new ByteArrayInputStream(b);
			imageStream = ImageIO.createImageInputStream(input);
			reader.setInput(imageStream, true);
			ImageReadParam param = reader.getDefaultReadParam();
			Rectangle rect = new Rectangle(x, y, width, height);
			param.setSourceRegion(rect);
			BufferedImage bi = reader.read(0, param);
			ImageIO.write(bi, imageType, out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (imageStream != null) {
				try {
					imageStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}*/

		private static byte[] readInputStream(InputStream inStream,double comBase, double scale) throws Exception{  
			
	        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
	        
	        resizeImage(inStream, outStream, comBase, scale, "jpg");
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

		private static void resizeImage(InputStream is, OutputStream os, double comBase, double scale,
				String format) {
			try {
				BufferedImage prevImage = ImageIO.read(is);
				int srcHeight = prevImage.getHeight();
				int srcWidth = prevImage.getWidth();
				int newHeight = 0;// 缩略图高
				int newWidth = 0;// 缩略图宽
				double srcScale = (double) srcHeight / srcWidth;
				/** 缩略图宽高算法 */
				if ((double) srcHeight > comBase || (double) srcWidth > comBase) {
					if (srcScale >= scale || 1 / srcScale > scale) {
						if (srcScale >= scale) {
							newHeight = (int) comBase;
							newWidth = srcWidth * newHeight / srcHeight;
						} else {
							newWidth = (int) comBase;
							newHeight = srcHeight * newWidth / srcWidth;
						}
					} else {
						if ((double) srcHeight > comBase) {
							newHeight = (int) comBase;
							newWidth = srcWidth * newHeight / srcHeight;
						} else {
							newWidth = (int) comBase;
							newHeight = srcHeight * newWidth / srcWidth;
						}
					}
				} else {
					newHeight = srcHeight;
					newWidth = srcWidth;
				}
				BufferedImage image = new BufferedImage(newWidth, newHeight,BufferedImage.TYPE_INT_BGR);
				Graphics graphics = image.createGraphics();
				graphics.drawImage(prevImage, 0, 0, newWidth, newHeight, null);
				ImageIO.write(image, format, os);
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				if (os != null){
					try {
						os.flush();
						os.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	    
	public static void main(String args[]) throws Exception {
//		ImageCompressUtil.zipImageFile("C:/Users/Daoyang/Desktop/pic/1.jpg", 1280, 1280, 1f, "x2");
//		ImageCompressUtil.resizeFix("C:/Users/Daoyang/Desktop/pic/7.jpg", "pic","dddd.jpg", 200,1d);
//		ImageCompressUtil.cutImage(new FileInputStream("C:\\Users\\Daoyang\\Desktop\\pic\\share_big85.jpg"),"pic","dddd.jpg", 
//				"jpg", 0, 100, 168, 1152, 1152,1d);
		ImageCompressUtil.cutPNG(new FileInputStream("C:\\Users\\Daoyang\\Desktop\\pic\\share_big85.jpg"),  
                new FileOutputStream("C:/Users/Daoyang/Desktop/pic/333.jpg"), 0,20,504,1152);  
	}
}
