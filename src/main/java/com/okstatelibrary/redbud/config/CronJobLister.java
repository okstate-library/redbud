package com.okstatelibrary.redbud.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.okstatelibrary.redbud.util.SingletonStringList;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

/**
 * Service class responsible for listing all scheduled cron jobs
 * defined in Spring components within the application context.
 * <p>
 * It scans for methods annotated with {@link Scheduled} and collects
 * relevant scheduling details such as cron expression, fixed rate,
 * fixed delay, and initial delay.
 * </p>
 * <p>
 * The information is added to a singleton list {@link SingletonStringList}
 * for further usage or display.
 * </p>
 * 
 * <p><b>Note:</b> This service assumes that scheduled methods are annotated
 * using the {@code @Scheduled} annotation and reside in Spring-managed beans
 * annotated with {@code @Component}.</p>
 * 
 * @author Damith Perera
 */
@Service
public class CronJobLister {

    /**
     * Spring application context used to retrieve beans annotated with {@link Component}.
     */
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * A singleton list instance used to store cron job details.
     */
    public SingletonStringList singletonList;

    /**
     * Scans all Spring-managed beans annotated with {@link Component} for methods
     * annotated with {@link Scheduled}, and collects their scheduling metadata.
     * <p>
     * Each discovered cron job's information is added to the {@code singletonList}.
     * </p>
     */
    public void listCronJobs() {
        singletonList = SingletonStringList.getInstance();

        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(Component.class);

        beans.forEach((name, bean) -> {
            Method[] methods = bean.getClass().getDeclaredMethods();
            Arrays.stream(methods)
                  .filter(method -> method.isAnnotationPresent(Scheduled.class))
                  .forEach(this::printCronJobDetails);
        });
    }

    /**
     * Extracts and logs details of a method annotated with {@link Scheduled},
     * and appends the information to the singleton list.
     *
     * @param method the method that contains the {@link Scheduled} annotation
     */
    private void printCronJobDetails(Method method) {
        Scheduled scheduled = method.getAnnotation(Scheduled.class);

        System.out.println("Adding values to the list");

        singletonList.addValue("Cron Job Name : " + method.getName());
        singletonList.addValue("Cron Expression: " + scheduled.cron());
        singletonList.addValue("Fixed Rate: " + scheduled.fixedRate());
        singletonList.addValue("Fixed Delay: " + scheduled.fixedDelay());
        singletonList.addValue("Initial Delay: " + scheduled.initialDelay());
        singletonList.addValue("-----------------------------------");
    }
}

