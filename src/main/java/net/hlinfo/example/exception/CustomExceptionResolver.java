package net.hlinfo.example.exception;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nutz.json.Json;
import org.nutz.lang.util.NutMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import net.hlinfo.example.utils.Resp;


@RestControllerAdvice
public class CustomExceptionResolver {
	private final Logger log = LogManager.getLogger(CustomExceptionResolver.class);
	@Value("${spring.servlet.multipart.max-file-size}")
	private String maxFileSize;
	
	@Value("${spring.servlet.multipart.max-request-size}")
	private String maxRequestSize;
	/**
	 * 捕获全局不可知的异常
	 * @param e
	 * @param req
	 * @return
	 */
	@ExceptionHandler(value=Exception.class)
	public Object handleException(Exception e, HttpServletRequest req) {
		System.out.println("全局异常处理...");
		e.printStackTrace();
		return new Resp<>().error("服务器或网络异常").data(NutMap.NEW()
			.addv("url", req.getRequestURL())
			.addv("message", e.getMessage())
			.addv("stackTrace", e.getStackTrace()));
	}
	/**
	 * 捕获全局参数校验的异常
	 * @param e
	 * @param req
	 * @return
	 */
	@ExceptionHandler(value= { MethodArgumentNotValidException.class})
	public Object handleException(MethodArgumentNotValidException e, HttpServletRequest req) {
		System.out.println("参数异常处理...");
		String msg = "所传数据不合法";
		List<NutMap> errorMaps = new ArrayList<>();
		for (ObjectError error : e.getBindingResult().getAllErrors()) {
			NutMap errorMap = NutMap.WRAP(Json.toJson(error));
			errorMap.remove("arguments");
			errorMap.remove("codes");
			errorMaps.add(errorMap);
		}
		e.printStackTrace();
		return new Resp<>().error(msg).data(errorMaps);
		
	}
	
	/**
	 * 拒绝访问异常
	 * @param e
	 * @param req
	 * @return
	 */
	@ExceptionHandler(value=AccessDeniedException.class)
	public Object accessDeniedException(AccessDeniedException e, HttpServletRequest req) {
		e.printStackTrace();
		return new Resp<>().error(e.getMessage());
	}
	/**
	 * 捕获上传文件异常
	 * @author cy
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value=MultipartException.class)
	public Object fileUploadExceptionHandler(MultipartException e){
		System.out.println("上传文件失败，服务器异常:"+e.getMessage());
		return new Resp<String>().error("上传文件失败，服务器异常");
	}
	
	/**
	 * 捕获上传文件大小超出限制异常
	 * @author cy
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value=MaxUploadSizeExceededException.class)
    public Object uploadException(MaxUploadSizeExceededException e) {
       return new Resp<String>().error("上传文件大小超出限制,单个文件不能超过："+maxFileSize+"，多文件总大小不能超过："+maxRequestSize);
    }

}
