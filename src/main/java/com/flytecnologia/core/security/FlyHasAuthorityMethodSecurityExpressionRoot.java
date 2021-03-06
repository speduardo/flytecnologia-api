package com.flytecnologia.core.security;

import com.flytecnologia.core.config.property.FlyAppProperty;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

public class FlyHasAuthorityMethodSecurityExpressionRoot
        extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {
    private Method method;
    private Object filterObject;
    private Object returnObject;
    private Object target;

    private FlyAppProperty flyAppProperty;

    private final String ROLE_DEBUG = "ROLE_DEBUG";

    public FlyHasAuthorityMethodSecurityExpressionRoot(Authentication authentication) {
        super(authentication);
    }

    public FlyHasAuthorityMethodSecurityExpressionRoot(Authentication authentication,
                                                       MethodInvocation methodInvocation) {
        super(authentication);

        this.method = methodInvocation.getMethod();
    }

    public String getAuthorityCreate() {
        if (flyAppProperty.getApp().isDebug())
            return ROLE_DEBUG;

        FlyRoles flyRoles = target.getClass().getAnnotation(FlyRoles.class);

        if (flyRoles != null) {
            if (!StringUtils.isEmpty(flyRoles.defaultName()))
                return "ROLE_C_" + flyRoles.defaultName().toUpperCase();

            return flyRoles.create();
        }

        return "ACCESS_DENIED";
    }

    public String getAuthority(String role) {
        if (flyAppProperty.getApp().isDebug())
            return ROLE_DEBUG;

        return role;
    }

    public String getAuthorityRead() {
        if (flyAppProperty.getApp().isDebug())
            return ROLE_DEBUG;

        FlyRoles flyRoles = target.getClass().getAnnotation(FlyRoles.class);

        if (flyRoles != null) {
            if (!StringUtils.isEmpty(flyRoles.defaultName()))
                return "ROLE_R_" + flyRoles.defaultName().toUpperCase();

            return flyRoles.read();
        }

        return "ACCESS_DENIED";
    }

    public String getAuthorityUpdate() {
        if (flyAppProperty.getApp().isDebug())
            return ROLE_DEBUG;

        FlyRoles flyRoles = target.getClass().getAnnotation(FlyRoles.class);

        if (flyRoles != null) {
            if (!StringUtils.isEmpty(flyRoles.defaultName()))
                return "ROLE_U_" + flyRoles.defaultName().toUpperCase();
            return flyRoles.update();
        }

        return "ACCESS_DENIED";
    }

    public String getAuthorityDelete() {
        if (flyAppProperty.getApp().isDebug())
            return ROLE_DEBUG;

        FlyRoles flyRoles = target.getClass().getAnnotation(FlyRoles.class);

        if (flyRoles != null) {
            if (!StringUtils.isEmpty(flyRoles.defaultName()))
                return "ROLE_D_" + flyRoles.defaultName().toUpperCase();
            return flyRoles.delete();
        }

        return "ACCESS_DENIED";
    }

    public void setFilterObject(Object filterObject) {
        this.filterObject = filterObject;
    }

    public Object getFilterObject() {
        return filterObject;
    }

    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    public Object getReturnObject() {
        return returnObject;
    }

    @Override
    public Object getThis() {
        return target;
    }

    public void setThis(Object target) {
        this.target = target;
    }

    public void setFlyAppProperty(FlyAppProperty flyAppProperty) {
        this.flyAppProperty = flyAppProperty;
    }
}