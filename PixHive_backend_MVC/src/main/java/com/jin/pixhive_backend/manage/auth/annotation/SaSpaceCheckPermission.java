package com.jin.pixhive_backend.manage.auth.annotation;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.jin.pixhive_backend.manage.auth.StpKit;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Space check permission: must have the specified permission to access the method
 * <p> can be marked on functions and classes (the effect is equivalent to marking on all methods of the same class).
 */
@SaCheckPermission(type = StpKit.SPACE_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface SaSpaceCheckPermission {

    @AliasFor(annotation = SaCheckPermission.class)
    String[] value() default {};

    /**
     * check mode: AND | OR, default: AND
     */
    @AliasFor(annotation = SaCheckPermission.class)
    SaMode mode() default SaMode.AND;

    /**
     * When the permission verification fails, it is a secondary option.
     * As long as one of the two verifies successfully, the verification can be passed
     *
     * <p>
     * eg1：@SaCheckPermission(value="user-add", orRole="admin"),
     * "user-add" or "admin" can pass
     * </p>
     *
     * <p>
     * eg2： orRole = {"admin", "manager", "staff"}. One of the three roles is sufficient.
     * eg3： orRole = {"admin, manager, staff", All three roles must be present simultaneously
     * </p>
     *
     * @return /
     */
    @AliasFor(annotation = SaCheckPermission.class)
    String[] orRole() default {};

}

