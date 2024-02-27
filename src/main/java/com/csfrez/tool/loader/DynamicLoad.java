package com.csfrez.tool.loader;

import com.csfrez.tool.utils.SpringAnnotationUtils;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Component
public class DynamicLoad {

    private static Logger logger = LoggerFactory.getLogger(DynamicLoad.class);

    @Autowired
    private ApplicationContext applicationContext;

    private Map<String, MyClassLoader> myClassLoaderCenter = new ConcurrentHashMap<>();

//    @Value("${dynamicLoad.path}")
//    private String path;

    /**
     * 动态加载指定路径下指定jar包
     *
     * @param path
     * @param fileName
     * @param isRegistXxlJob 是否需要注册xxljob执行器，项目首次启动不需要注册执行器
     * @return map<jobHander, Cron> 创建xxljob任务时需要的参数配置
     */
    public void loadJar(String path, String fileName, Boolean isRegistXxlJob) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        File file = new File(path + "/" + fileName);
        Map<String, String> jobPar = new HashMap<>();
        // 获取beanFactory
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        // 获取当前项目的执行器
        try {
            // URLClassloader加载jar包规范必须这么写
            URL url = new URL("jar:file:" + file.getAbsolutePath() + "!/");
            URLConnection urlConnection = url.openConnection();
            JarURLConnection jarURLConnection = (JarURLConnection) urlConnection;
            // 获取jar文件
            JarFile jarFile = jarURLConnection.getJarFile();
            Enumeration<JarEntry> entries = jarFile.entries();

            // 创建自定义类加载器，并加到map中方便管理
            MyClassLoader myClassloader = new MyClassLoader(new URL[]{url}, ClassLoader.getSystemClassLoader());
            myClassLoaderCenter.put(fileName, myClassloader);
            Set<Class> initBeanClass = new HashSet<>(jarFile.size());
            // 遍历文件
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                if (jarEntry.getName().endsWith(".class")) {
                    // 1. 加载类到jvm中
                    // 获取类的全路径名
                    String className = jarEntry.getName().replace('/', '.').substring(0, jarEntry.getName().length() - 6);
                    // 1.1进行反射获取
                    myClassloader.loadClass(className);
                }
            }
            Map<String, Class<?>> loadedClasses = myClassloader.getLoadedClasses();
            XxlJobSpringExecutor xxlJobExecutor = new XxlJobSpringExecutor();
            for (Map.Entry<String, Class<?>> entry : loadedClasses.entrySet()) {
                String className = entry.getKey();
                Class<?> clazz = entry.getValue();
                // 2. 将有@spring注解的类交给spring管理
                // 2.1 判断是否注入spring
                Boolean flag = SpringAnnotationUtils.hasSpringAnnotation(clazz);
                if (flag) {
                    // 2.2交给spring管理
                    BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
                    AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
                    // 此处beanName使用全路径名是为了防止beanName重复
                    String packageName = className.substring(0, className.lastIndexOf(".") + 1);
                    String beanName = className.substring(className.lastIndexOf(".") + 1);
                    beanName = packageName + beanName.substring(0, 1).toLowerCase() + beanName.substring(1);
                    // 2.3注册到spring的beanFactory中
                    beanFactory.registerBeanDefinition(beanName, beanDefinition);
                    // 2.4允许注入和反向注入
                    beanFactory.autowireBean(clazz);
                    beanFactory.initializeBean(clazz, beanName);
                    /*if(Arrays.stream(clazz.getInterfaces()).collect(Collectors.toSet()).contains(InitializingBean.class)){
                        initBeanClass.add(clazz);
                    }*/
                    initBeanClass.add(clazz);
                }

                // 3. 带有XxlJob注解的方法注册任务
                // 3.1 过滤方法
                Map<Method, XxlJob> annotatedMethods = null;
                try {
                    annotatedMethods = MethodIntrospector.selectMethods(clazz,
                            new MethodIntrospector.MetadataLookup<XxlJob>() {
                                @Override
                                public XxlJob inspect(Method method) {
                                    return AnnotatedElementUtils.findMergedAnnotation(method, XxlJob.class);
                                }
                            });
                } catch (Throwable ex) {
                }
                // 3.2 生成并注册方法的JobHander
//                for (Map.Entry<Method, XxlJob> methodXxlJobEntry : annotatedMethods.entrySet()) {
//                    Method executeMethod = methodXxlJobEntry.getKey();
//                    // 获取jobHander和Cron
//                    XxlJobCron xxlJobCron = executeMethod.getAnnotation(XxlJobCron.class);
//                    if (xxlJobCron == null) {
//                        throw new CustomException("500", executeMethod.getName() + "()，没有添加@XxlJobCron注解配置定时策略");
//                    }
//                    if (!CronExpression.isValidExpression(xxlJobCron.value())) {
//                        throw new CustomException("500", executeMethod.getName() + "(),@XxlJobCron参数内容错误");
//                    }
//                    XxlJob xxlJob = methodXxlJobEntry.getValue();
//                    jobPar.put(xxlJob.value(), xxlJobCron.value());
//                    if (isRegistXxlJob) {
//                        executeMethod.setAccessible(true);
//                        // regist
//                        Method initMethod = null;
//                        Method destroyMethod = null;
//                        xxlJobExecutor.registJobHandler(xxlJob.value(), new CustomerMethodJobHandler(clazz, executeMethod, initMethod, destroyMethod));
//                    }
//                }

            }
            // spring bean实际注册
            initBeanClass.forEach(beanFactory::getBean);
        } catch (IOException e) {
            logger.error("读取{} 文件异常", fileName);
            e.printStackTrace();
            throw new RuntimeException("读取jar文件异常: " + fileName);
        }
    }

    /**
     * 动态卸载指定路径下指定jar包
     *
     * @param fileName
     * @return map<jobHander, Cron> 创建xxljob任务时需要的参数配置
     */
    public void unloadJar(String fileName) throws IllegalAccessException, NoSuchFieldException {
        // 获取加载当前jar的类加载器
        MyClassLoader myClassLoader = myClassLoaderCenter.get(fileName);

        // 获取jobHandlerRepository私有属性,为了卸载xxljob任务
        Field privateField = XxlJobExecutor.class.getDeclaredField("jobHandlerRepository");
        // 设置私有属性可访问
        privateField.setAccessible(true);
        // 获取私有属性的值jobHandlerRepository
        XxlJobExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        Map<String, IJobHandler> jobHandlerRepository = (ConcurrentHashMap<String, IJobHandler>) privateField.get(xxlJobSpringExecutor);
        // 获取beanFactory，准备从spring中卸载
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        Map<String, Class<?>> loadedClasses = myClassLoader.getLoadedClasses();

        Set<String> beanNames = new HashSet<>();
        for (Map.Entry<String, Class<?>> entry : loadedClasses.entrySet()) {
            // 1. 将xxljob任务从xxljob执行器中移除
            // 1.1 截取beanName
            String key = entry.getKey();
            String packageName = key.substring(0, key.lastIndexOf(".") + 1);
            String beanName = key.substring(key.lastIndexOf(".") + 1);
            beanName = packageName + beanName.substring(0, 1).toLowerCase() + beanName.substring(1);

            // 获取bean，如果获取失败，表名这个类没有加到spring容器中，则跳出本次循环
            Object bean = null;
            try {
                bean = applicationContext.getBean(beanName);
            } catch (Exception e) {
                // 异常说明spring中没有这个bean
                continue;
            }

            // 1.2 过滤方法
            Map<Method, XxlJob> annotatedMethods = null;
            try {
                annotatedMethods = MethodIntrospector.selectMethods(bean.getClass(),
                        new MethodIntrospector.MetadataLookup<XxlJob>() {
                            @Override
                            public XxlJob inspect(Method method) {
                                return AnnotatedElementUtils.findMergedAnnotation(method, XxlJob.class);
                            }
                        });
            } catch (Throwable ex) {
            }
            // 1.3 将job从执行器中移除
            for (Map.Entry<Method, XxlJob> methodXxlJobEntry : annotatedMethods.entrySet()) {
                XxlJob xxlJob = methodXxlJobEntry.getValue();
                jobHandlerRepository.remove(xxlJob.value());
            }
            // 2.0从spring中移除，这里的移除是仅仅移除的bean，并未移除bean定义
            beanNames.add(beanName);
            beanFactory.destroyBean(beanName, bean);
        }
        // 移除bean定义
        Field mergedBeanDefinitions = beanFactory.getClass()
                .getSuperclass()
                .getSuperclass().getDeclaredField("mergedBeanDefinitions");
        mergedBeanDefinitions.setAccessible(true);
        Map<String, RootBeanDefinition> rootBeanDefinitionMap = ((Map<String, RootBeanDefinition>) mergedBeanDefinitions.get(beanFactory));
        for (String beanName : beanNames) {
            beanFactory.removeBeanDefinition(beanName);
            // 父类bean定义去除
            rootBeanDefinitionMap.remove(beanName);
        }

        // 卸载父任务，子任务已经在循环中卸载
        jobHandlerRepository.remove(fileName);
        // 3.2 从类加载中移除
        try {
            // 从类加载器底层的classes中移除连接
            Field field = ClassLoader.class.getDeclaredField("classes");
            field.setAccessible(true);
            Vector<Class<?>> classes = (Vector<Class<?>>) field.get(myClassLoader);
            classes.removeAllElements();
            // 移除类加载器的引用
            myClassLoaderCenter.remove(fileName);
            // 卸载类加载器
            myClassLoader.unload();
        } catch (NoSuchFieldException e) {
            logger.error("动态卸载的类，从类加载器中卸载失败");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            logger.error("动态卸载的类，从类加载器中卸载失败");
            e.printStackTrace();
        }
        logger.error("{} 动态卸载成功", fileName);

    }
}