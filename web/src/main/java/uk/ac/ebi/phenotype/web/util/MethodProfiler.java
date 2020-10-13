package uk.ac.ebi.phenotype.web.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * This is an Aspect which prints timing information for all methods
 * It is enabled only on the DEV profile, so these timing info statements should
 * not appear in the BETA and PROD logs.
 */
@Component
@Aspect
@Profile("dev")
public class MethodProfiler {

    @Pointcut("(within(uk.ac.ebi.phenotype..*) || within(org.mousephenotype.cda..*)) && !within(uk.ac.ebi.phenotype.web.controller.registerinterest.*) && !within(uk.ac.ebi.phenotype.web.util.DeploymentInterceptor)")
    public void monitoredMethods() {
    }

    @Around("monitoredMethods()")
    public Object profile(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        Object output = pjp.proceed();
        long elapsedTime = System.currentTimeMillis() - start;
        System.out.println("TIMING==>" + elapsedTime + " ms: " + pjp.getSignature().toString());
        return output;
    }

}
