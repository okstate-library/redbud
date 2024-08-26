package com.okstatelibrary.redbud.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

@Service
public class CronJobLister {

	@Autowired
	private ApplicationContext applicationContext;

	public void listCronJobs() {
		Map<String, Object> beans = applicationContext.getBeansWithAnnotation(Component.class);

		beans.forEach((name, bean) -> {
			Method[] methods = bean.getClass().getDeclaredMethods();

			Arrays.stream(methods).filter(method -> method.isAnnotationPresent(Scheduled.class))
					.forEach(this::printCronJobDetails);

		});
	}

	private void printCronJobDetails(Method method) {
		Scheduled scheduled = method.getAnnotation(Scheduled.class);

		System.out.println("Cron Job Method: " + method.getName());
		System.out.println("Cron Expression: " + scheduled.cron());
		System.out.println("Fixed Rate: " + scheduled.fixedRate());
		System.out.println("Fixed Delay: " + scheduled.fixedDelay());
		System.out.println("Initial Delay: " + scheduled.initialDelay());
		System.out.println("-----------------------------------");
	}
}
