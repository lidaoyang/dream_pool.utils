package com.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import net.sf.json.JSONObject;

import com.alibaba.fastjson.JSONArray;

/*
 * FileUtils copied from org.apache.commons.io.FileUtils
 */
public class FileUtils {
	/**
	 * Construct a file from the set of name elements.
	 *
	 * @param directory
	 *            the parent directory
	 * @param names
	 *            the name elements
	 * @return the file
	 */
	public static File getFile(File directory, String... names) {
		if (directory == null) {
			throw new NullPointerException(
					"directorydirectory must not be null");
		}
		if (names == null) {
			throw new NullPointerException("names must not be null");
		}
		File file = directory;
		for (String name : names) {
			file = new File(file, name);
		}
		return file;
	}

	/**
	 * Construct a file from the set of name elements.
	 *
	 * @param names
	 *            the name elements
	 * @return the file
	 */
	public static File getFile(String... names) {
		if (names == null) {
			throw new NullPointerException("names must not be null");
		}
		File file = null;
		for (String name : names) {
			if (file == null) {
				file = new File(name);
			} else {
				file = new File(file, name);
			}
		}
		return file;
	}

	/**
	 * Opens a {@link FileInputStream} for the specified file, providing better
	 * error messages than simply calling <code>new FileInputStream(file)</code>
	 * .
	 * <p>
	 * At the end of the method either the stream will be successfully opened,
	 * or an exception will have been thrown.
	 * <p>
	 * An exception is thrown if the file does not exist. An exception is thrown
	 * if the file object exists but is a directory. An exception is thrown if
	 * the file exists but cannot be read.
	 *
	 * @param file
	 *            the file to open for input, must not be {@code null}
	 * @return a new {@link FileInputStream} for the specified file
	 * @throws FileNotFoundException
	 *             if the file does not exist
	 * @throws IOException
	 *             if the file object is a directory
	 * @throws IOException
	 *             if the file cannot be read
	 */
	public static FileInputStream openInputStream(File file) throws IOException {
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new IOException("File '" + file
						+ "' exists but is a directory");
			}
			if (file.canRead() == false) {
				throw new IOException("File '" + file + "' cannot be read");
			}
		} else {
			throw new FileNotFoundException("File '" + file
					+ "' does not exist");
		}
		return new FileInputStream(file);
	}

	/**
	 * Opens a {@link FileOutputStream} for the specified file, checking and
	 * creating the parent directory if it does not exist.
	 * <p>
	 * At the end of the method either the stream will be successfully opened,
	 * or an exception will have been thrown.
	 * <p>
	 * The parent directory will be created if it does not exist. The file will
	 * be created if it does not exist. An exception is thrown if the file
	 * object exists but is a directory. An exception is thrown if the file
	 * exists but cannot be written to. An exception is thrown if the parent
	 * directory cannot be created.
	 *
	 * @param file
	 *            the file to open for output, must not be {@code null}
	 * @param append
	 *            if {@code true}, then bytes will be added to the end of the
	 *            file rather than overwriting
	 * @return a new {@link FileOutputStream} for the specified file
	 * @throws IOException
	 *             if the file object is a directory
	 * @throws IOException
	 *             if the file cannot be written to
	 * @throws IOException
	 *             if a parent directory needs creating but that fails
	 */
	public static FileOutputStream openOutputStream(File file, boolean append)
			throws IOException {
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new IOException("File '" + file
						+ "' exists but is a directory");
			}
			if (file.canWrite() == false) {
				throw new IOException("File '" + file
						+ "' cannot be written to");
			}
		} else {
			File parent = file.getParentFile();
			if (parent != null) {
				if (!parent.mkdirs() && !parent.isDirectory()) {
					throw new IOException("Directory '" + parent
							+ "' could not be created");
				}
			}
		}
		return new FileOutputStream(file, append);
	}

	public static FileOutputStream openOutputStream(File file)
			throws IOException {
		return openOutputStream(file, false);
	}

	/**
	 * Cleans a directory without deleting it.
	 *
	 * @param directory
	 *            directory to clean
	 * @throws IOException
	 *             in case cleaning is unsuccessful
	 */
	public static void cleanDirectory(File directory) throws IOException {
		if (!directory.exists()) {
			String message = directory + " does not exist";
			throw new IllegalArgumentException(message);
		}

		if (!directory.isDirectory()) {
			String message = directory + " is not a directory";
			throw new IllegalArgumentException(message);
		}

		File[] files = directory.listFiles();
		if (files == null) { // null if security restricted
			throw new IOException("Failed to list contents of " + directory);
		}

		IOException exception = null;
		for (File file : files) {
			try {
				forceDelete(file);
			} catch (IOException ioe) {
				exception = ioe;
			}
		}

		if (null != exception) {
			throw exception;
		}
	}

	// -----------------------------------------------------------------------
	/**
	 * Deletes a directory recursively.
	 *
	 * @param directory
	 *            directory to delete
	 * @throws IOException
	 *             in case deletion is unsuccessful
	 */
	public static void deleteDirectory(File directory) throws IOException {
		if (!directory.exists()) {
			return;
		}

		cleanDirectory(directory);

		if (!directory.delete()) {
			String message = "Unable to delete directory " + directory + ".";
			throw new IOException(message);
		}
	}

	/**
	 * Deletes a file. If file is a directory, delete it and all
	 * sub-directories.
	 * <p>
	 * The difference between File.delete() and this method are:
	 * <ul>
	 * <li>A directory to be deleted does not have to be empty.</li>
	 * <li>You get exceptions when a file or directory cannot be deleted.
	 * (java.io.File methods returns a boolean)</li>
	 * </ul>
	 *
	 * @param file
	 *            file or directory to delete, must not be {@code null}
	 * @throws NullPointerException
	 *             if the directory is {@code null}
	 * @throws FileNotFoundException
	 *             if the file was not found
	 * @throws IOException
	 *             in case deletion is unsuccessful
	 */
	public static void forceDelete(File file) throws IOException {
		if (file.isDirectory()) {
			deleteDirectory(file);
		} else {
			boolean filePresent = file.exists();
			if (!file.delete()) {
				if (!filePresent) {
					throw new FileNotFoundException("File does not exist: "
							+ file);
				}
				String message = "Unable to delete file: " + file;
				throw new IOException(message);
			}
		}
	}

	/**
	 * Deletes a file, never throwing an exception. If file is a directory,
	 * delete it and all sub-directories.
	 * <p>
	 * The difference between File.delete() and this method are:
	 * <ul>
	 * <li>A directory to be deleted does not have to be empty.</li>
	 * <li>No exceptions are thrown when a file or directory cannot be deleted.</li>
	 * </ul>
	 *
	 * @param file
	 *            file or directory to delete, can be {@code null}
	 * @return {@code true} if the file or directory was deleted, otherwise
	 *         {@code false}
	 *
	 */
	public static boolean deleteQuietly(File file) {
		if (file == null) {
			return false;
		}
		try {
			if (file.isDirectory()) {
				cleanDirectory(file);
			}
		} catch (Exception ignored) {
		}

		try {
			return file.delete();
		} catch (Exception ignored) {
			return false;
		}
	}

	/**
	 * Makes a directory, including any necessary but nonexistent parent
	 * directories. If a file already exists with specified name but it is not a
	 * directory then an IOException is thrown. If the directory cannot be
	 * created (or does not already exist) then an IOException is thrown.
	 *
	 * @param directory
	 *            directory to create, must not be {@code null}
	 * @throws NullPointerException
	 *             if the directory is {@code null}
	 * @throws IOException
	 *             if the directory cannot be created or the file already exists
	 *             but is not a directory
	 */
	public static void forceMkdir(File directory) throws IOException {
		if (directory.exists()) {
			if (!directory.isDirectory()) {
				String message = "File " + directory + " exists and is "
						+ "not a directory. Unable to create directory.";
				throw new IOException(message);
			}
		} else {
			if (!directory.mkdirs()) {
				// Double-check that some other thread or process hasn't made
				// the directory in the background
				if (!directory.isDirectory()) {
					String message = "Unable to create directory " + directory;
					throw new IOException(message);
				}
			}
		}
	}

	/**
	 * Returns the size of the specified file or directory. If the provided
	 * {@link File} is a regular file, then the file's length is returned. If
	 * the argument is a directory, then the size of the directory is calculated
	 * recursively. If a directory or subdirectory is security restricted, its
	 * size will not be included.
	 *
	 * @param file
	 *            the regular file or directory to return the size of (must not
	 *            be {@code null}).
	 *
	 * @return the length of the file, or recursive size of the directory,
	 *         provided (in bytes).
	 *
	 * @throws NullPointerException
	 *             if the file is {@code null}
	 * @throws IllegalArgumentException
	 *             if the file does not exist.
	 *
	 */
	public static long sizeOf(File file) {

		if (!file.exists()) {
			String message = file + " does not exist";
			throw new IllegalArgumentException(message);
		}

		if (file.isDirectory()) {
			return sizeOfDirectory(file);
		} else {
			return file.length();
		}

	}

	/**
	 * Counts the size of a directory recursively (sum of the length of all
	 * files).
	 *
	 * @param directory
	 *            directory to inspect, must not be {@code null}
	 * @return size of directory in bytes, 0 if directory is security
	 *         restricted, a negative number when the real total is greater than
	 *         {@link Long#MAX_VALUE}.
	 * @throws NullPointerException
	 *             if the directory is {@code null}
	 */
	public static long sizeOfDirectory(File directory) {
		checkDirectory(directory);

		final File[] files = directory.listFiles();
		if (files == null) { // null if security restricted
			return 0L;
		}
		long size = 0;

		for (final File file : files) {

			size += sizeOf(file);
			if (size < 0) {
				break;

			}

		}

		return size;
	}

	/**
	 * Checks that the given {@code File} exists and is a directory.
	 *
	 * @param directory
	 *            The {@code File} to check.
	 * @throws IllegalArgumentException
	 *             if the given {@code File} does not exist or is not a
	 *             directory.
	 */
	private static void checkDirectory(File directory) {
		if (!directory.exists()) {
			throw new IllegalArgumentException(directory + " does not exist");
		}
		if (!directory.isDirectory()) {
			throw new IllegalArgumentException(directory
					+ " is not a directory");
		}
	}

	public static String mergeFiles(String directory) {
		File f = new File(directory);
		return tree(f);
	}

	// 显示目录的方法
	public static String tree(File f) {
		StringBuilder sb = new StringBuilder();
		sb.append("分类名称,分类ID,相关个股\r\n");
		// 判断传入对象是否为一个文件夹对象
		if (!f.isDirectory()) {
			System.out.println("你输入的不是一个文件夹，请检查路径是否有误！！");
		} else {
			File[] t = f.listFiles();
			for (int i = 0; i < t.length; i++) {
				// 判断文件列表中的对象是否为文件夹对象，如果是则执行tree递归，直到把此文件夹中所有文件输出为止
				if (t[i].isDirectory()) {
					System.out.println(t[i].getName() + "\tttdir");
					tree(t[i]);
				} else {
					String content = readTxtFile(t[i], i);
					sb.append(content).append("\r\n");
				}
			}
		}
		return sb.toString();
	}

	public static ArrayList<HashMap<String, Object>> get_stock_industry(File f,String type,
			ArrayList<HashMap<String, Object>> result) {
		if (result==null) {
			result = new ArrayList<HashMap<String,Object>>();
		}
		// 判断传入对象是否为一个文件夹对象
		if (!f.isDirectory()) {
			System.out.println("你输入的不是一个文件夹，请检查路径是否有误！！");
		} else {
			File[] t = f.listFiles();
			for (int i = 0; i < t.length; i++) {
				HashMap<String, Object> retmap = new HashMap<String, Object>();
				// 判断文件列表中的对象是否为文件夹对象，如果是则执行tree递归，直到把此文件夹中所有文件输出为止
				if (t[i].isDirectory()) {
					type=t[i].getName();
					result= get_stock_industry(t[i],type,result);
				} else {
					ArrayList<String> list = readTxtFile(t[i]);
					if (list!=null&&!list.isEmpty()) {
						String name = t[i].getName().split("[.]")[0];
						retmap.put("industry_type", type);
						retmap.put("industry_name", name);
						retmap.put("stocks", list);
						result.add(retmap);
					}
				}
			}
		}
		return result;
	}
	public static String readTxtFile(File file, int index) {
		String filename = file.getName();
		filename = filename.substring(0, filename.lastIndexOf("."));
		StringBuilder sb = new StringBuilder(filename + "," + index + ",");
		try {
			String encoding = "GBK";
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					sb.append(lineTxt).append("|");
				}
				read.close();
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
		String str = sb.toString();
		if (str.endsWith("|")) {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}
	public static ArrayList<String> readTxtFile(File file) {
		String filename = file.getName();
		filename = filename.substring(0, filename.lastIndexOf("."));
		ArrayList<String> result = new ArrayList<String>();
		try {
			String encoding = "GBK";
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file));// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				int i = 0;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					lineTxt = lineTxt.trim();
					i++;
					if (i%2!=0) {
						continue;
					}
					if (lineTxt.endsWith("HK")) {
						lineTxt = lineTxt.replace(" HK", "");
						lineTxt = String.format("%05d", Integer.valueOf(lineTxt));
					}
					result.add(lineTxt);
				}
				read.close();
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
		return result;
	}
	public static JSONArray readZipFile(File file) {
		JSONArray jarr = new JSONArray();
		ZipFile zf = null;
		InputStream in = null;
		ZipInputStream zin = null;
		try {
			zf = new ZipFile(file);
			in = new BufferedInputStream(new FileInputStream(file));
			zin = new ZipInputStream(in);
			ZipEntry ze;
			while ((ze = zin.getNextEntry()) != null) {
				if (ze.isDirectory()) {
				} else {
					long size = ze.getSize();
					if (size > 0) {
						BufferedReader br = new BufferedReader(
								new InputStreamReader(zf.getInputStream(ze)));
						String line;
						while ((line = br.readLine()) != null) {
							String result = line.substring(line.indexOf("{"));
							JSONObject jo = JSONObject.fromObject(result);
							jarr.add(jo);
						}
						br.close();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (zin != null) {
					zin.closeEntry();
				}
				if (in != null) {
					in.close();
				}
				if (zf != null) {
					zf.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return jarr;
	}

	/** */
	/**
	 * 文件转化为字节数组
	 * 
	 * @Author Sean.guo
	 * @EditTime 2007-8-13 上午11:45:28
	 */
	public static byte[] getBytesFromFile(File f) {
		if (f == null) {
			return null;
		}
		try {
			FileInputStream stream = new FileInputStream(f);
			ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			for (int n; (n = stream.read(b)) != -1;) {
				out.write(b, 0, n);
			}
			stream.close();
			out.close();
			return out.toByteArray();
		} catch (IOException e) {
		}
		return null;
	}

	/** */
	/**
	 * 把字节数组保存为一个文件
	 * 
	 * @Author Sean.guo
	 * @EditTime 2007-8-13 上午11:45:56
	 */
	public static File getFileFromBytes(byte[] b, String outputFile) {
		BufferedOutputStream stream = null;
		File file = null;
		try {
			file = new File(outputFile);
			FileOutputStream fstream = new FileOutputStream(file);
			stream = new BufferedOutputStream(fstream);
			stream.write(b);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return file;
	}

	/** */
	/**
	 * 从字节数组获取对象
	 * 
	 * @Author Sean.guo
	 * @EditTime 2007-8-13 上午11:46:34
	 */
	public static Object getObjectFromBytes(byte[] objBytes) throws Exception {
		if (objBytes == null || objBytes.length == 0) {
			return null;
		}
		ByteArrayInputStream bi = new ByteArrayInputStream(objBytes);
		ObjectInputStream oi = new ObjectInputStream(bi);
		return oi.readObject();
	}

	/** */
	/**
	 * 从对象获取一个字节数组
	 * 
	 * @Author Sean.guo
	 * @EditTime 2007-8-13 上午11:46:56
	 */
	public static byte[] getBytesFromObject(Serializable obj) throws Exception {
		if (obj == null) {
			return null;
		}
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		ObjectOutputStream oo = new ObjectOutputStream(bo);
		oo.writeObject(obj);
		return bo.toByteArray();
	}

	public static void writeToTxt(String content, String filepath)
			throws UnsupportedEncodingException, IOException {
		FileOutputStream out = new FileOutputStream(filepath);
		out.write(content.getBytes("GBK"));
		out.close();
	}
	/**
     * 获取目录下所有文件(按时间排序)
     * 
     * @param path 文件夹路径
     * @return
     */
	public static List<File> getFileSort(String path) {
		List<File> list = getFiles(path, new ArrayList<File>());
		getFileSort(list);
		return list;
	}
	/**
     * 获取目录下所有文件(按时间排序)
     * 
     * @param list 文件列表
     * @return
     */
	public static List<File> getFileSort(List<File> list ) {
		if (list != null && list.size() > 0) {
			Collections.sort(list, new Comparator<File>() {
				public int compare(File file, File newFile) {
					if (file.lastModified() < newFile.lastModified()) {
						return 1;
					} else if (file.lastModified() == newFile.lastModified()) {
						return 0;
					} else {
						return -1;
					}

				}
			});

		}

		return list;
	}
    /**
     * 
     * 获取目录下所有文件
     * 
     * @param realpath
     * @param files
     * @return
     */
    public static List<File> getFiles(String realpath, List<File> files) {
        File realFile = new File(realpath);
        if (realFile.isDirectory()) {
            File[] subfiles = realFile.listFiles();
            for (File file : subfiles) {
                if (file.isDirectory()) {
                    getFiles(file.getAbsolutePath(), files);
                } else {
                    files.add(file);
                }
            }
        }
        return files;
    }
    public static void main(String[] args) {
    	 
        String path = "F:\\wsIncreUS";
 
        List<File> list = getFileSort(path);
 
        for (File file : list) {
            System.out.println(file.getName() + " : " + DateUtils.DateToStr(new Date(file.lastModified()), ""));
        }
    }
}
