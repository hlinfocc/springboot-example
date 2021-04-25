package net.hlinfo.example.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import ch.qos.logback.core.encoder.EchoEncoder;


/**
 * controller基类
 *
 */
public class BaseController {
	protected static final Logger log = LoggerFactory.getLogger(BaseController.class);
	
	protected void echo(Object obj) {
		if(obj!=null) {
			System.out.println(obj);
		}
	}
}
