package com.example.uambite.audit;

import com.example.uambite.model.Usuario;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
public class AuditAspect {

    private static final Logger log = LoggerFactory.getLogger("AUDIT");

    @Around("execution(* com.example.uambite.controller..*(..)) "
            + "&& @annotation(preAuthorize)")
    public Object auditar(ProceedingJoinPoint pjp, PreAuthorize preAuthorize) throws Throwable {
        Usuario principal = usuarioActual();
        long start = System.currentTimeMillis();
        Object result;
        try {
            result = pjp.proceed();
            long ms = System.currentTimeMillis() - start;
            log.info("OK  method={} rol={} userId={} args={} elapsedMs={}",
                    pjp.getSignature().toShortString(),
                    principal == null ? "<anon>" : principal.getRol(),
                    principal == null ? "<n/a>" : principal.getId(),
                    sanitizarArgs(pjp.getArgs()),
                    ms);
            return result;
        } catch (Throwable t) {
            long ms = System.currentTimeMillis() - start;
            log.warn("DENY method={} rol={} userId={} args={} error={} elapsedMs={}",
                    pjp.getSignature().toShortString(),
                    principal == null ? "<anon>" : principal.getRol(),
                    principal == null ? "<n/a>" : principal.getId(),
                    sanitizarArgs(pjp.getArgs()),
                    t.getClass().getSimpleName(),
                    ms);
            throw t;
        }
    }

    private Usuario usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Usuario u)) {
            return null;
        }
        return u;
    }

    private String sanitizarArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        return Arrays.stream(args)
                .map(a -> a == null ? "null" : a.getClass().getSimpleName() + "#" + safeToString(a))
                .collect(Collectors.joining(", ", "[", "]"));
    }

    private String safeToString(Object o) {
        try {
            String s = o.toString();
            return s.length() > 80 ? s.substring(0, 77) + "..." : s;
        } catch (Exception e) {
            return "<unprintable>";
        }
    }
}
