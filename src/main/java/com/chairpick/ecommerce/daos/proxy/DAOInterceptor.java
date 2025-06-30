package com.chairpick.ecommerce.daos.proxy;

import com.chairpick.ecommerce.daos.interfaces.GenericDAO;
import com.chairpick.ecommerce.daos.interfaces.LoggerDAO;
import com.chairpick.ecommerce.model.DomainEntity;
import com.chairpick.ecommerce.security.AuthenticatedUser;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;


@Aspect
@Component
public class DAOInterceptor {

    private final LoggerDAO loggerDAO;

    public DAOInterceptor(LoggerDAO loggerDAO) {
        this.loggerDAO = loggerDAO;
    }

    @Around("execution(* com.chairpick.ecommerce.daos..*.save(..)) && args(newObject)")
    public Object logInsert(ProceedingJoinPoint joinPoint, Object newObject) throws Throwable {

        Long userId = getAuthenticatedUserId();
        Object result = joinPoint.proceed();
        if (newObject instanceof DomainEntity entity) {
            CompletableFuture.runAsync(() -> loggerDAO.logInsert(entity, userId))
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
        }
        return result;
    }

    @Around("execution(* com.chairpick.ecommerce.daos..*.update(..)) && args(newObject)")
    public Object logUpdate(ProceedingJoinPoint joinPoint, Object newObject) throws Throwable {
        GenericDAO<?> dao = (GenericDAO<?>) joinPoint.getTarget();
        Long userId = getAuthenticatedUserId();

        Long id = ((DomainEntity) newObject).getId();
        Object oldObject = dao.findById(id).orElse(null);

        Object result = joinPoint.proceed();

        if (oldObject != null) {
            CompletableFuture.runAsync(() -> loggerDAO.logUpdate(oldObject, result, userId))
                    .exceptionally(ex -> {
                        ex.printStackTrace();
                        return null;
                    });
        }
        return result;
    }

    private Long getAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return -1L;

        AuthenticatedUser authenticatedUser = auth.getPrincipal() instanceof AuthenticatedUser ? (AuthenticatedUser) auth.getPrincipal() : null;

        if (authenticatedUser != null) {
            return authenticatedUser.getUser().getId();
        }

        return -1L;
    }

}
