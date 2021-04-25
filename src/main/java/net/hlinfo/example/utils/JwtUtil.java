package net.hlinfo.example.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.nutz.json.Json;
import org.nutz.lang.util.NutMap;

public class JwtUtil {
	/**
	 * 用户登录成功后生成Jwt 使用Hs256算法 私匙使用用户密码
	 *
	 * @param ttlday jwt过期时间 单位天
	 * @param user 登录成功的user对象 必须要有password值，作为私钥<br>
	 * 				也必须要有username,作为签发人	
	 * @param key 生成签名的时候使用的秘钥secret,这个方法本地封装了的，一般可以<br>
	 * 				从本地配置文件中读取，切记这个秘钥不能外露哦。它就是你服务端的<br>
	 * 				私钥，在任何场景都不应该流露出去。一旦客户端得知这个secret,<br>
					那就意味着客户端是可以自我签发jwt了。
	 * @return
	 */
	public static String createJWT(long ttlday
			, NutMap user
			, String key) {
		// 指定签名的时候使用的签名算法，也就是header那部分，jjwt已经将这部分内容封装好了。
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		// 生成JWT的时间
		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);

		// 创建payload的私有声明（根据特定的业务需要添加，如果要拿这个做验证，一般是需要和jwt的接收方提前沟通好验证方式的）
		Map<String, Object> claims = new HashMap<String, Object>();
		user.forEach((k, v) -> {
			claims.put(k, v);
		});

		// 下面就是在为payload添加各种标准声明和私有声明了
		// 这里其实就是new一个JwtBuilder，设置jwt的body
		JwtBuilder builder = Jwts.builder()
			// 如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
			.setClaims(claims)
			// 设置jti(JWT ID)：是JWT的唯一标识，根据业务需要，这个可以设置为一个不重复的值，主要用来作为一次性token,从而回避重放攻击。
			.setId(UUID.randomUUID().toString())
			// iat: jwt的签发时间
			.setIssuedAt(now)
			// 代表这个JWT的主体，即它的所有人，这个是一个json格式的字符串，可以存放什么userid，roldid之类的，作为什么用户的唯一标志。
			.setSubject(Json.toJson(user))
			// 设置签名使用的签名算法和签名使用的秘钥
			.signWith(signatureAlgorithm, key);
		if (ttlday >= 0) {
			long expMillis = nowMillis + ttlday * 24 * 60 * 60 * 1000;
			Date exp = new Date(expMillis);
			// 设置过期时间
			builder.setExpiration(exp);
		}
		return builder.compact();
	}

	/**
	 * Token的解密
	 * 
	 * @param token 加密后的token
	 * @param key  签名秘钥，和生成的签名的秘钥一模一样
	 * @return
	 */
	public static Claims parseJWT(String token, String key) {

		// 得到DefaultJwtParser
		Claims claims = Jwts.parser()
				// 设置签名的秘钥
				.setSigningKey(key)
				// 设置需要解析的jwt
				.parseClaimsJws(token).getBody();
		return claims;
	}

	/**
	 * 校验token 在这里可以使用官方的校验，token中携带的密码key和数据库一致的话
	 * 就校验通过
	 * 
	 * @param token
	 * @param key 签名秘钥，和生成的签名的秘钥一模一样
	 * @return
	 */
	public static Boolean isVerify(String token, String key, String pwd) {
		// 得到DefaultJwtParser
		Claims claims = Jwts.parser()
				// 设置签名的秘钥
				.setSigningKey(key)
				// 设置需要解析的jwt
				.parseClaimsJws(token).getBody();

		if (claims.get("password").equals(pwd)) {
			return true;
		}

		return false;
	}
	public static void main(String[] args) {
		NutMap user = NutMap.NEW()
			.addv("id", "123")
			.addv("username1", "333331")
			.addv("username2", "333332")
			.addv("username3", "333333")
			.addv("password", "12355447788");
		String token = JwtUtil.createJWT(2000L, user, "0000000000000000000001");
		System.out.println(JwtUtil.parseJWT(token, "0000000000000000000001"));
	}
	/*public static void main(String[] args) {
		NutMap user = NutMap.NEW()
		.addv("id", "123")
		.addv("username", "33333")
		.addv("password", "12355447788");
		String token = JwtUtil.createJWT(20000L, user, "2555555555555522");
		System.out.println(token);
		for(int i = 0; i < 10; i ++) {
			System.out.println(JwtUtil.parseJWT(token, "2555555555555522"));
			System.out.println(JwtUtil.isVerify(token, "2555555555555522", "1122"));
			System.out.println(JwtUtil.isVerify(token, "2555555555555522", "12355447788"));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}*/
}
