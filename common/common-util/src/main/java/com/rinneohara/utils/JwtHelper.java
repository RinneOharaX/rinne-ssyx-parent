package com.rinneohara.utils;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/9/5 13:48
 */
import io.jsonwebtoken.*;
import org.springframework.util.StringUtils;
import java.util.Date;

public class JwtHelper {

    private static String tokenSignKey = "ssyx";

    public static String createToken(Long userId, String userName) {
        String token = Jwts.builder()
                .setSubject("ssyx-USER")
                .claim("userId", userId)
                .claim("userName", userName)
                .signWith(SignatureAlgorithm.HS512, tokenSignKey)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
        return token;
    }

    public static Long getUserId(String token) {
        if(StringUtils.isEmpty(token)) return null;

        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();

        Integer userId = (Integer)claims.get("userId");
        return userId.longValue();
        // return 1L;
    }

    public static String getUserName(String token) {
        if(StringUtils.isEmpty(token)) return "";

        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        return (String)claims.get("userName");
    }

    public static void removeToken(String token) {
        //jwttoken无需删除，客户端扔掉即可。
    }
//    public static boolean isExpired(String token){
//        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
//        Claims body = claimsJws.getBody();
//        Date expiration = body.getExpiration();
//        return expiration;
//    }
    public static void main(String[] args) {
        String token="eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJSKi6urNANDXYNUtJRSq0oULIyNLM0NTU2NDS30FEqLU4t8kxRsjI2hLD9EnNTgXryI32Lc8ySQnKysk2MCs3ckh2dPC0KfV0yCiLSlWoBlle6cFoAAAA.qL5JRy0z9R6vrAZmMnuOUWFlHAB4K1d89JjFNXwC6jq_zg2pCmNWrzjlvZbQCZ9oJrdxE-__3CTb4eHGDtqT6w";
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims body = claimsJws.getBody();
        Date expiration = body.getExpiration();
        System.out.println(expiration);
    }
}
