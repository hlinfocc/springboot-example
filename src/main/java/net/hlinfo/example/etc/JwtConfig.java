package net.hlinfo.example.etc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="jwt",ignoreInvalidFields=true, ignoreUnknownFields=true)
public class JwtConfig {
	private long ttlday;
	private String jwtkey;
	private long vrcodeDuration;
	
	public long getTtlday() {
		return ttlday;
	}
	public void setTtlday(long ttlday) {
		this.ttlday = ttlday;
	}
	public String getJwtkey() {
		return jwtkey;
	}
	public void setJwtkey(String jwtkey) {
		this.jwtkey = jwtkey;
	}
	public long getVrcodeDuration() {
		return vrcodeDuration;
	}
	public void setVrcodeDuration(long vrcodeDuration) {
		this.vrcodeDuration = vrcodeDuration;
	}
	
}
