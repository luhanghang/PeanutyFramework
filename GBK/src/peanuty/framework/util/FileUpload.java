package peanuty.framework.util;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import peanuty.framework.base.BaseBean;
import peanuty.framework.base.Config;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUpload extends BaseBean {

	private Map<String,String> reqH = new HashMap<String,String>();

	private List<Map<String,Object>> fileItems = new ArrayList<Map<String,Object>>();
	
	public final static String SPLIT_STRING = "lUhAnG";

	public FileUpload(HttpServletRequest req) {
		try {
			initialize(req);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private void initialize(HttpServletRequest req) throws Throwable {
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		List items = upload.parseRequest(req);
		//Iterator iter = items.iterator();
        for(Object iObj:items){
        //while (iter.hasNext()) {
			//FileItem item = (FileItem) iter.next();
            FileItem item = (FileItem)iObj;
            if (item.isFormField()) {
				String key = item.getFieldName();
				//String value = new String(item.getString().getBytes("8859_1"),"GBK");
                String value = item.getString(Config.getRequestEncoding());
                if(reqH.get(key) != null){
					value = reqH.get(key) + SPLIT_STRING + value;
				}
				reqH.put(key, value);
			} else {
				Map<String,Object> itemInfo = new HashMap<String,Object>();
				itemInfo.put("fileName", getFileName(item.getName()));
				itemInfo.put("fileSize", item.getSize());
				itemInfo.put("fileItem", item);
				fileItems.add(itemInfo);
			}
		}
	}

    /**
     * Get request Map
     * @return request Map
     */
    public Map<String,String> getReqH() {
		return reqH;
	}

    /**
     * Get file item list
     * @return file item list
     */
    public List<Map<String,Object>> getFileItemList() {
		return fileItems;
	}

    /**
     * Write file
     * @param filePath file path
     * @return the result
     */
    public boolean write(String filePath) {
		try {
			for (Map fileItem: fileItems) {
				FileItem item = (FileItem) fileItem.get("fileItem");
				if(item.getSize() == 0) {
					return false;
				}
				File dir = new File(filePath);
	            if (!dir.exists()){
	              dir.mkdirs();
	            }
				item.write(new File(filePath + "/" + fileItem.get("fileName")));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

    private static String getFileName(String filePathName) {
		int pos = filePathName.lastIndexOf(47);
		if (pos != -1) {
			return filePathName.substring(pos + 1, filePathName.length());
		}
		pos = filePathName.lastIndexOf(92);
		if (pos != -1) {
			return filePathName.substring(pos + 1, filePathName.length());
		}
		return filePathName;
	}


    /**
     * Download File
     * @param filename filename
     * @return file bytes
     * @throws Exception All exceptions
     */
    public static byte[] downloadfile(String filename) throws Exception {
		File file = new File(filename);
		InputStream is = new FileInputStream(file);
		long length = file.length();
		byte[] b = new byte[(int) length];
		int offset = 0;
		int numRead;
		while (offset < b.length
				&& (numRead = is.read(b, offset, b.length - offset)) >= 0) {
			offset += numRead;
		}
		is.close();
		return b;
	}

    /**
     * Delete File
     * @param fileName file name
     * @return the result
     */
    public static boolean deleteFile(String fileName) {
		return deleteFile(new File(fileName));
	}

    /**
     * Delete File
     * @param file file to be deleted
     * @return the result
     */
    public static boolean deleteFile(File file) {
		if (file.isDirectory()) {
			String[] children = file.list();
			for (String child: children) {
				if (!deleteFile(new File(file, child))) {
					return false;
				}
			}
		}
		return file.delete();
	}

    public static boolean move(File src, File target){
        try {
            FileOutputStream fo = new FileOutputStream(target);
            FileInputStream fi = new FileInputStream(src);
            byte[] bytes = new byte[1024];
            int rcount = fi.read(bytes, 0, 1024);
            while (rcount != -1) {
                fo.write(bytes);
                rcount = fi.read(bytes, 0, 1024);
            }
            fi.close();
            fo.close();
            return src.delete();
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
