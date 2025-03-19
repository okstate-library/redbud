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

@Service
public class CronJobLister {

	@Autowired
	private ApplicationContext applicationContext;

	public SingletonStringList singletonList;

	public void listCronJobs() {

		singletonList = SingletonStringList.getInstance();
		
		Map<String, Object> beans = applicationContext.getBeansWithAnnotation(Component.class);

		beans.forEach((name, bean) -> {
			Method[] methods = bean.getClass().getDeclaredMethods();

			Arrays.stream(methods).filter(method -> method.isAnnotationPresent(Scheduled.class))
					.forEach(this::printCronJobDetails);

		});
	}

	private void printCronJobDetails(Method method) {

		Scheduled scheduled = method.getAnnotation(Scheduled.class);

		System.out.println("Adding values to the list");
		
		singletonList.addValue("Cron Job Name : " + method.getName());
		singletonList.addValue("Cron Expression: " + scheduled.cron());
//		singletonList.addValue("Fixed Rate: " + scheduled.fixedRate());
//		singletonList.addValue("Fixed Delay: " + scheduled.fixedDelay());
//		singletonList.addValue("Initial Delay: " + scheduled.initialDelay());
		singletonList.addValue("-----------------------------------");
	}
}
