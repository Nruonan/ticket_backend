package org.xzn.index12306.frameworks.starter.user.toolkit;

import static org.xzn.index12306.framework.starter.bases.constant.UserConstant.REAL_NAME_KEY;
import static org.xzn.index12306.framework.starter.bases.constant.UserConstant.USER_ID_KEY;
import static org.xzn.index12306.framework.starter.bases.constant.UserConstant.USER_NAME_KEY;

import com.alibaba.fastjson2.JSON;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.xzn.index12306.frameworks.starter.user.core.UserInfoDTO;

/**
 * JWT 工具类
 *
 * @author Nruonan
 */
@Slf4j
public final class JWTUtil {

    private static final long EXPIRATION = 86400L;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String ISS = "index12306";
    public static final String SECRET = "SecretKey039245678901232039487623456783092349288901402967890140939827";

    /**
     * 生成用户 Token
     *
     * @param userInfo 用户信息
     * @return 用户访问 Token
     */
    public static String generateAccessToken(UserInfoDTO userInfo) {
        Map<String, Object> customerUserMap = new HashMap<>();
        customerUserMap.put(USER_ID_KEY, userInfo.getUserId());
        customerUserMap.put(USER_NAME_KEY, userInfo.getUsername());
        customerUserMap.put(REAL_NAME_KEY, userInfo.getRealName());
        String jwtToken = Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .setIssuedAt(new Date())
                .setIssuer(ISS)
                .setSubject(JSON.toJSONString(customerUserMap))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION * 1000))
                .compact();
        return TOKEN_PREFIX + jwtToken;
    }

    /**
     * 解析用户 Token
     *
     * @param jwtToken 用户访问 Token
     * @return 用户信息
     */
    public static UserInfoDTO parseJwtToken(String jwtToken) {
        // 检查JWT令牌是否存在
        if (StringUtils.hasText(jwtToken)) {
            // 移除JWT令牌前的前缀
            String actualJwtToken = jwtToken.replace(TOKEN_PREFIX, "");
            try {
                // 解析JWT令牌
                Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(actualJwtToken).getBody();
                // 获取令牌的过期时间
                Date expiration = claims.getExpiration();
                // 检查令牌是否未过期
                if (expiration.after(new Date())) {
                    // 获取令牌的主题，即用户信息的JSON字符串
                    String subject = claims.getSubject();
                    // 将用户信息的JSON字符串解析为UserInfoDTO对象并返回
                    return JSON.parseObject(subject, UserInfoDTO.class);
                }
            } catch (ExpiredJwtException ignored) {
                // 捕获过期的JWT令牌异常，此处无需处理，直接忽略
            } catch (Exception ex) {
                // 捕获其他JWT令牌解析异常，并记录错误日志
                log.error("JWT Token解析失败，请检查", ex);
            }
        }
        return null;
    }
}