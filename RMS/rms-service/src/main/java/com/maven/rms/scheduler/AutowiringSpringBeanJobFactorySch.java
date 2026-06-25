package com.maven.rms.scheduler;
import javax.annotation.PostConstruct;

import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

public class AutowiringSpringBeanJobFactorySch extends SpringBeanJobFactory {

    private AutowireCapableBeanFactory beanFactory;

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        this.beanFactory = applicationContext.getAutowireCapableBeanFactory();
    }

    @Override
    public Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        Object job = super.createJobInstance(bundle);
        beanFactory.autowireBean(job);
        return job;
    }
}
// import org.quartz.spi.JobFactory;
// import org.quartz.spi.TriggerFiredBundle;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
// import org.springframework.context.ApplicationContext;
// import org.springframework.scheduling.quartz.SpringBeanJobFactory;

// public class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory {

//     @Autowired
//     private AutowireCapableBeanFactory beanFactory;

//     @Override
//     public void setApplicationContext(ApplicationContext applicationContext) {
//         super.setApplicationContext(applicationContext);
//         this.beanFactory = applicationContext.getAutowireCapableBeanFactory();
//     }

//     @Override
//     public Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
//         Object job = super.createJobInstance(bundle);
//         beanFactory.autowireBean(job);
//         return job;
//     }
// }

