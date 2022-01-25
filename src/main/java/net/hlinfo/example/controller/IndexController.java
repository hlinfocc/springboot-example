package net.hlinfo.example.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.hlinfo.example.utils.Funs;
import net.hlinfo.example.utils.Resp;
import net.hlinfo.opt.HashUtils;

@Api(tags="首页")
@RequestMapping("/")
@RestController
public class IndexController extends BaseController{
	@Value("${spring.profiles.active}")
	private String profile;
	
	@ApiOperation("首页-欢迎页面")
	@GetMapping("/")
	public String index(){
		String result = "";
		if(Funs.equals(profile, "dev") || Funs.equals(profile, "test")) {
			result = "<script>location='doc.html';</script>";
		}else {
			result = "welcome";
		}
		return result;
	}
	
	@ApiOperation("读取jar包同级目录的txt文件")
	@GetMapping("/{txtfile:\\w+}.txt")
	public String getTxtFile(@ApiParam("文件名") @PathVariable String txtfile
			,HttpServletRequest request,HttpServletResponse response){
		txtfile = txtfile + ".txt";
		response.reset();
	   response.setContentType("text/plain");
	   response.setHeader("Content-Type", "text/plain;charset=utf-8");
		ApplicationHome jarHome = new ApplicationHome(getClass());
		File jarF = jarHome.getSource();
		String fileName = jarF.getParentFile().toString()+File.separatorChar+txtfile;
		File file = new File(fileName);
		if(!file.exists()) {
			return "welcome";
		}
	    BufferedReader reader = null;
	    StringBuffer sbf = new StringBuffer();
	    try {
	        reader = new BufferedReader(new FileReader(file));
	        String tempStr;
	        while ((tempStr = reader.readLine()) != null) {
	            sbf.append(tempStr);
	          }
	        reader.close();
	        return sbf.toString();
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        if (reader != null) {
	            try {
	                reader.close();
	            } catch (IOException e1) {
	                e1.printStackTrace();
	            }
	        }
	    }
		return sbf.toString();
	}
		
	@ApiOperation("获取SM3的散列值")
	@GetMapping("/sm3")
	public Resp<String> getSM3(@ApiParam("需要SM3加密的字符串")@RequestParam(name="str", defaultValue = "") String str){
		return new Resp<>().ok("获取成功",Funs.sm3(str));
	}
	
	
}
