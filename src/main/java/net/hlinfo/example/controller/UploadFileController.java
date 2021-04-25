package net.hlinfo.example.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.util.NutMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.coobird.thumbnailator.Thumbnails;
import net.hlinfo.example.etc.FileUploadConf;
import net.hlinfo.example.utils.Funs;
import net.hlinfo.example.utils.RedisKey;
import net.hlinfo.example.utils.RedisUtils;
import net.hlinfo.example.utils.Resp;

@Api(tags = "通用文件上传模块")
@RestController
@RequestMapping("/upload")
public class UploadFileController extends BaseController{
	
	@Autowired
	FileUploadConf fileUploadConf;
	
	@Autowired
	private RedisUtils redisUtils;
	
	@SuppressWarnings("unchecked")
	@Order(value = 0)
	@ApiOperation(value="单文件上传方法",consumes="multipart/form-data")
	@ApiImplicitParams({
		@ApiImplicitParam(name="file",value="文件流数据",required = true,paramType = "form",dataType = "__File")
	})
	@PostMapping("/single")
	public Resp<String> singleFiles(
			@RequestParam(value="file",required = true) MultipartFile file
			,HttpServletRequest request){
		//创建文件在服务器端存放路径
		try {
			String dir = fileUploadConf.getSavePath();
			String ymd = Funs.getNowLocalDate();
			log.debug("dir=" + dir);
			//检查目录写权限
			File fileBaseDir = new File(dir);
			if(!fileBaseDir.canWrite()){
				return new Resp<String>().error("上传目录没有写权限。");
			}
			File fileDir = new File(dir+File.separator+ymd);
			if(!fileDir.exists()) {
				fileDir.mkdirs();
				File f = new File(dir+File.separator+ymd+File.separator+"index.html");
				f.createNewFile();
			}
			
			String fileSuffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")).toLowerCase();
			if(!Arrays.<String>asList(fileUploadConf.extMap().get("all").split(",")).contains(fileSuffix)){
				return new Resp<NutMap>().error("上传文件是不允许上传的文件类型。");
			}
			/*if(Arrays.<String>asList(fileUploadConf.extMap().get("image").split(",")).contains(fileSuffix)){
				if(file.getSize()>2*1024*1024) {
					return new Resp<NutMap>().error("上传图片大小超过限制。单个图片不能超过2M");
				}
			}*/
			
			String fileName = Funs.getFileNameBySm3()+fileSuffix;
			File saveFile = new File(fileDir+File.separator+fileName);
			//上传
			if(Arrays.<String>asList(fileUploadConf.extMap().get("imageNoGif").split(",")).contains(fileSuffix)){
				if(file.getSize()>2*1024*1024) {
					//上传图片大小超过限制，进行压缩处理后保存，按照比例缩小
					this.compress(file,saveFile, 0.7f);
				}else {
					file.transferTo(saveFile);
				}
			}else {
				file.transferTo(saveFile);
			}
			String url = fileUploadConf.getBaseUrl() + "/" + ymd + "/" + fileName;
			log.debug("url=" + url);
			NutMap map = NutMap.NEW();
			map.addv("url", url);
			map.addv("origName", file.getOriginalFilename());
			map.addv("suffix", fileSuffix.replace(".", ""));
			map.addv("size", file.getSize());
			return new Resp<NutMap>().ok("OK", map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Resp<NutMap>().error("上传失败");
		} 
		
	}
	
	@Order(value = 1)
	@ApiOperation(value="批量文件上传方法",consumes="multipart/form-data")
	@ApiImplicitParam(name="files",value="文件流数据",required = true,dataType = "__File",allowMultiple = true)
	@PostMapping("/multiple")
	public Resp<NutMap> multipleFiles(MultipartFile[] files,HttpServletRequest request){
		List<String> imgUrls = new ArrayList<String>();
		List<NutMap> imgUrlitem = new ArrayList<NutMap>();
		try {
			//创建文件在服务器端存放路径
			String dir = fileUploadConf.getSavePath();
			String ymd = Funs.getNowLocalDate();
			//检查目录写权限
			File fileBaseDir = new File(dir);
			if(!fileBaseDir.canWrite()){
				return new Resp<NutMap>().error("上传目录没有写权限。");
			}
			File fileDir = new File(dir+File.separator+ymd);
			if(!fileDir.exists()) {
				fileDir.mkdirs();
				File f = new File(dir+File.separator+ymd+File.separator+"index.html");
				f.createNewFile();
			}
			int en = 0,s = 0;
			for(int i=0;i<files.length;i++) {
				String fileSuffix = files[i].getOriginalFilename().substring(files[i].getOriginalFilename().lastIndexOf(".")).toLowerCase();
				if(!Arrays.<String>asList(fileUploadConf.extMap().get("all").split(",")).contains(fileSuffix)){
					//上传文件是不允许上传的文件类型。
					en++;
					continue;
				}
				/*if(Arrays.<String>asList(fileUploadConf.extMap().get("image").split(",")).contains(fileSuffix)){
					if(files[i].getSize()>2*1024*1024) {
						//上传图片大小超过限制。单个图片不能超过2M
						s++;
						continue;
					}
				}*/
				String fileName = Funs.getFileNameBySm3()+fileSuffix;
				File file = new File(fileDir+File.separator+fileName);
				//上传
				if(Arrays.<String>asList(fileUploadConf.extMap().get("imageNoGif").split(",")).contains(fileSuffix)){
					if(files[i].getSize()>2*1024*1024) {
						//上传图片大小超过限制，进行压缩处理，按照比例缩小
						this.compress(files[i],file, 0.7f);
					}else {
						files[i].transferTo(file);
					}
				}else {
					files[i].transferTo(file);
				}
				imgUrls.add(fileUploadConf.getBaseUrl() + "/" + ymd + "/" + fileName);
				NutMap map = NutMap.NEW();
				map.addv("url", fileUploadConf.getBaseUrl() + "/" + ymd + "/" + fileName);
				map.addv("origName", files[i].getOriginalFilename());
				map.addv("suffix", fileSuffix.replace(".", ""));
				map.addv("size", files[i].getSize());
				imgUrlitem.add(map);
			}
			String enstr = "";
			if(en>0) {
				enstr = "有"+en+"个文件是不允许上传的文件类型已跳过";
			}
			String sstr = "";
			if(s>0) {
				sstr = "有"+s+"张图片大小超过限制已跳过";
			}
			return new Resp().ok("successful！"+enstr+"，"+sstr,NutMap.NEW()
					.addv("imgUrls", imgUrls)
					.addv("imgUrlsItem", imgUrlitem));
		} catch (Exception e) {
			e.printStackTrace();
			return new Resp<NutMap>().error("上传失败");
		}
		
	}
	
	@ApiOperation(value="下载",notes="本方法不需要token")
	@GetMapping("/download")
	public String download(@ApiParam("文件名")@RequestParam(name="fileName", defaultValue = "") String fileName
			, @ApiParam("显示文件名")@RequestParam(name="outFileName", defaultValue = "") String outFileName
			,@ApiParam("code") @RequestParam String code
			,HttpServletRequest request,HttpServletResponse response) {
		
		if(Funs.isBlank(code)) {
			response.reset();
          response.setContentType("text/html");
			response.setHeader("Content-Type", "text/html;charset=UTF-8");
			return "<script>alert('code参数不能为空');</script>";
		}
		String codeCache = redisUtils.getCacheObject(RedisKey.exportExcelCode+code);
		if(Funs.isBlank(codeCache)) {
			response.reset();
          response.setContentType("text/html");
			response.setHeader("Content-Type", "text/html;charset=UTF-8");
			return "<script>alert('鉴权失败，code错误');</script>";
		}
		
		String fileSaveName = fileUploadConf.getSavePath()+File.separator+"exportCache";
		fileSaveName += File.separator + fileName;
		File file = new File(fileSaveName);
		if(!file.exists()) {
			response.reset();
          response.setContentType("text/html");
			response.setHeader("Content-Type", "text/html;charset=UTF-8");
			return "<script>alert('下载导出文件失败【文件不存在】，请重新导出');</script>";
		}
		if(Funs.isBlank(outFileName)) {
			outFileName = "导出Excel-" + Funs.getNowFullTimeNum()+".xlsx";
		}
		//设置下载头部信息
		Funs.setDownloadHeader(request, response, outFileName);
		//下载处理
		byte[] buff = new byte[1024];
		BufferedInputStream bis = null;
		OutputStream os = null;
		try {
			os = response.getOutputStream();
          bis = new BufferedInputStream(new FileInputStream(file));
          int i = bis.read(buff);
          while (i != -1) {
        	  os.write(buff, 0, buff.length);
        	  os.flush();
            i = bis.read(buff);
          	}
        } catch (IOException e) {
          e.printStackTrace();
        } finally {
          if (bis != null) {
            try {
              bis.close();
              file.delete();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
		return null;
	}
	
	/**
	 * 压缩文件
	 * @param file 待处理文件
	 * @param saveFile 处理后保存的文件
	 * @param scale 压缩比
	 */
	private void compress(MultipartFile file,File saveFile,double scale) {
		try {
			Thumbnails.of(file.getInputStream()).scale(scale).toFile(saveFile.getPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
