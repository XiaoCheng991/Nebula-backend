package com.nebula.api.interceptor;

import com.nebula.common.annotation.RequirePermission;
import com.nebula.common.constant.AdminConstants;
import com.nebula.common.exception.BusinessException;
import com.nebula.common.exception.ErrorCode;
import com.nebula.config.config.JwtProperties;
import com.nebula.config.util.SecurityContext;
import com.nebula.service.service.system.PermissionService;
import com.nebula.service.service.system.SysRoleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;

@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionInterceptor implements HandlerInterceptor {

    private final JwtProperties jwtProperties;
    private final SysRoleService sysRoleService;
    private final PermissionService permissionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        Method method = handlerMethod.getMethod();
        Class<?> clazz = method.getDeclaringClass();

        Long userId = SecurityContext.getCurrentUserId(jwtProperties.getSecret());
        if (isSuperAdmin(userId)) {
            return true;
        }

        RequirePermission methodAnnotation = method.getAnnotation(RequirePermission.class);
        RequirePermission classAnnotation = clazz.getAnnotation(RequirePermission.class);

        if (methodAnnotation == null && classAnnotation == null) {
            return true;
        }

        String requiredPermission = methodAnnotation != null ? methodAnnotation.value() : classAnnotation.value();

        if (userId == null || !permissionService.hasPermission(userId, requiredPermission)) {
            log.warn("用户无权限访问: {}, 需要权限: {}", request.getRequestURI(), requiredPermission);
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }

        return true;
    }

    private boolean isSuperAdmin(Long userId) {
        if (userId == null) {
            return false;
        }
        return AdminConstants.SUPER_ADMIN_USER_ID.equals(userId) ||
                sysRoleService.hasRole(userId, AdminConstants.SUPER_ADMIN_ROLE_CODE);
    }
}
