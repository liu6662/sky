package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {}

    @Before("autoFillPointCut()")
    public void AutoFill(JoinPoint joinPoint) {
        log.info("开始进行公共字段的填充...");
        // 1. 获取注解并判空
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        if (autoFill == null) {
            log.warn("当前方法未标注@AutoFill注解，跳过公共字段填充");
            return;
        }
        OperationType operationType = autoFill.value();

        // 2. 获取实体参数并判空
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            log.warn("被拦截方法无参数，跳过公共字段填充");
            return ;
        }
        Object entity = args[0];

        // 3. 准备填充数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();
        if (currentId == null) {
            log.warn("当前未获取到登录员工ID，公共字段操作人赋值为0");
            currentId = 0L;
        }

        // 4. 根据操作类型填充字段
        if(operationType == OperationType.INSERT){
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                // 开启暴力反射
                setCreateTime.setAccessible(true);
                setCreateUser.setAccessible(true);
                setUpdateTime.setAccessible(true);
                setUpdateUser.setAccessible(true);

                // 赋值
                setCreateTime.invoke(entity, now);
                setCreateUser.invoke(entity, currentId);
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                log.error("INSERT操作公共字段填充失败", e);
                throw new RuntimeException("公共字段自动填充失败", e);
            }
        } else if (operationType == OperationType.UPDATE) {
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setUpdateTime.setAccessible(true);
                setUpdateUser.setAccessible(true);

                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                log.error("UPDATE操作公共字段填充失败", e);
                throw new RuntimeException("公共字段自动填充失败", e);
            }
        }
    }
}