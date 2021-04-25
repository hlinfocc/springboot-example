package net.hlinfo.example.etc;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileUploadConf {
	@Value("${upload.savePath}")
	private String savePath;
	@Value("${upload.baseUrl}")
	private String baseUrl;
	
	public String getSavePath() {
		return savePath;
	}
	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}
	public String getBaseUrl() {
		return baseUrl;
	}
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	@Override
	public String toString() {
		return "FileUploadConf [savePath=" + savePath + ", baseUrl=" + baseUrl + ", getSavePath()=" + getSavePath()
				+ ", getBaseUrl()=" + getBaseUrl() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}
	
	/**
	 * 文件类型
	 * @return
	 */
	public Map<String, String> extMap() {
		String fileext = ".pdf,.xls,xlsx,.doc,.docx,.zip,.gz,.txt,.rar";
		String imgext = ".gif,.jpg,.jpeg,.png,.bmp";
		String img2ext = ".jpg,.jpeg,.png,.bmp";
		HashMap<String, String> extMap = new HashMap<String, String>();
		extMap.put("image", imgext);
		extMap.put("imageNoGif", img2ext);
		extMap.put("file", fileext);
		extMap.put("all", fileext+","+imgext);
		return extMap;
	}
	
	
}

