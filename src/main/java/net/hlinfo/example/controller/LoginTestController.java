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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.hlinfo.example.etc.security.AuthenticationVo;
import net.hlinfo.example.utils.Funs;
import net.hlinfo.example.utils.Resp;
import net.hlinfo.opt.HashUtils;

@Api(tags="logintest")
@RequestMapping("/logintest")
@RestController
public class LoginTestController extends BaseController{
	@Value("${spring.profiles.active}")
	private String profile;
	
	@ApiOperation("获取SM3的散列值")
	@PostMapping("/sm3")
	public Resp<String> getSM3(@RequestBody AuthenticationVo auth){
		return new Resp<>().ok("获取成功",auth);
	}
	
	
}
