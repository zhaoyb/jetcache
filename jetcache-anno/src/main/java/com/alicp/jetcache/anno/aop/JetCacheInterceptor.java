/**
 * Created on  13-09-18 20:33
 */
package com.alicp.jetcache.anno.aop;

import com.alicp.jetcache.anno.method.CacheHandler;
import com.alicp.jetcache.anno.method.CacheInvokeConfig;
import com.alicp.jetcache.anno.method.CacheInvokeContext;
import com.alicp.jetcache.anno.support.ConfigMap;
import com.alicp.jetcache.anno.support.ConfigProvider;
import com.alicp.jetcache.anno.support.GlobalCacheConfig;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;

/**
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
public class JetCacheInterceptor implements MethodInterceptor, ApplicationContextAware {

    //private static final Logger logger = LoggerFactory.getLogger(JetCacheInterceptor.class);

    @Autowired
    private ConfigMap cacheConfigMap;
    private ApplicationContext applicationContext;
    private GlobalCacheConfig globalCacheConfig;
    ConfigProvider configProvider;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     *
     * 被注解方法 处理
     *
     * @param invocation
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        // 判断配置是否存在
        if (configProvider == null) {
            configProvider = applicationContext.getBean(ConfigProvider.class);
        }
        if (configProvider != null && globalCacheConfig == null) {
            globalCacheConfig = configProvider.getGlobalCacheConfig();
        }
        // 如果不存在 globalCacheConfig 或者不开启缓存，则直接调用方法 返回
        if (globalCacheConfig == null || !globalCacheConfig.isEnableMethodCache()) {
            return invocation.proceed();
        }

        Method method = invocation.getMethod();
        Object obj = invocation.getThis();
        CacheInvokeConfig cac = null;
        // 根据类和方法  获取之前解析后放到本地的 缓存配置
        if (obj != null) {
            String key = CachePointcut.getKey(method, obj.getClass());
            cac  = cacheConfigMap.getByMethodInfo(key);
        }

        /*
        if(logger.isTraceEnabled()){
            logger.trace("JetCacheInterceptor invoke. foundJetCacheConfig={}, method={}.{}(), targetClass={}",
                    cac != null,
                    method.getDeclaringClass().getName(),
                    method.getName(),
                    invocation.getThis() == null ? null : invocation.getThis().getClass().getName());
        }
        */

        // 配置项不存在， 直接调用目标方法  返回
        if (cac == null || cac == CacheInvokeConfig.getNoCacheInvokeConfigInstance()) {
            return invocation.proceed();
        }

        // 缓存上下文
        CacheInvokeContext context = configProvider.getCacheContext().createCacheInvokeContext(cacheConfigMap);
        // 类
        context.setTargetObject(invocation.getThis());
        // 目标调用
        context.setInvoker(invocation::proceed);
        // 方法
        context.setMethod(method);
        // 方法参数
        context.setArgs(invocation.getArguments());
        // 配置
        context.setCacheInvokeConfig(cac);
        context.setHiddenPackages(globalCacheConfig.getHiddenPackages());

        // 到这里开始核心流程
        return CacheHandler.invoke(context);
    }

    public void setCacheConfigMap(ConfigMap cacheConfigMap) {
        this.cacheConfigMap = cacheConfigMap;
    }

}
