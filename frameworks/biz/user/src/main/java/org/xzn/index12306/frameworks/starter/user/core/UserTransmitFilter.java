package org.xzn.index12306.frameworks.starter.user.core;


import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLDecoder;
import org.springframework.util.StringUtils;
import org.xzn.index12306.framework.starter.bases.constant.UserConstant;

/**
 * @author Nruonan
 * @description 用户信息传输过滤器
 */
public class UserTransmitFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String userId = httpServletRequest.getHeader(UserConstant.USER_ID_KEY);
        if (StringUtils.hasText(userId)){
            String username = httpServletRequest.getHeader(UserConstant.USER_NAME_KEY);
            String nickname = httpServletRequest.getHeader(UserConstant.REAL_NAME_KEY);
            // 解码
            if(StringUtils.hasText(username)){
                username = URLDecoder.decode(username, "UTF-8");
            }
            if (StringUtils.hasText(nickname)){
                nickname = URLDecoder.decode(nickname, "UTF-8");
            }
            String token = httpServletRequest.getHeader(UserConstant.USER_TOKEN_KEY);
            UserInfoDTO userInfo = UserInfoDTO.builder()
                .userId(userId)
                .username(username)
                .realName(nickname)
                .token(token)
                .build();
            // 存入线程池
            UserContext.setUser(userInfo);
        }
        try{
            filterChain.doFilter(servletRequest, servletResponse);
        }finally {
            UserContext.removeUser();
        }
    }
}
