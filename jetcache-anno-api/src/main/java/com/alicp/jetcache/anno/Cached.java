/**
 * Created on  13-09-04
 */
package com.alicp.jetcache.anno;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cached {
    /**
     *
     * 如果需要连接多个缓存系统，可在配置多个cache area，这个属性指定要使用的那个area的name
     * If you want to use multi backend cache system, you can setup multi "cache area" in configuration,
     * this attribute specifies the name of the "cache area" you want to use.
     * @return the name of cache area
     *
     */
    String area() default CacheConsts.DEFAULT_AREA;

    /**
     *
     * 指定缓存的名称，不是必须的，如果没有指定，会使用类名+方法名。name会被用于远程缓存的key前缀。另外在统计中，一个简短有意义的名字会提高可读性。如果两个@CreateCache的name和area相同，它们会指向同一个Cache实例
     *
     * The name of this Cache instance, optional.
     * If you do not specify, JetCache will auto generate one.
     * The name is used to display statistics information and as part of key prefix when using a remote cache.
     * Do not assign same name to different @Cached annotation which with same area.
     * @return the name of the cache
     */
    String name() default CacheConsts.UNDEFINED_STRING;


    /**
     * Specify whether the method caching is enabled.
     * If set to false, you can enable it in thread context using
     * {@code CacheContext.enableCache(Supplier<T> callback)}
     * @return if the method cache is enabled
     */
    boolean enabled() default CacheConsts.DEFAULT_ENABLED;

    /**
     * 指定expire的单位
     * Specify the time unit of expire.
     * @return the time unit of expire time
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     *
     * 该Cache实例的默认超时时间定义，注解上没有定义的时候会使用全局配置，如果此时全局配置也没有定义，则取无穷大
     * The expire time. Use global config if the attribute value is absent,
     * and if the global config is not defined either, use infinity instead.
     * @return the expire time
     */
    int expire() default CacheConsts.UNDEFINED_INT;

    /**
     * Use to specify the local cache expire time when cacheType=CacheType.BOTH, use "expire" if absent.
     * @return the local cache expire time
     */
    int localExpire() default CacheConsts.UNDEFINED_INT;

    /**
     * 缓存的类型，包括CacheType.REMOTE、CacheType.LOCAL、CacheType.BOTH。如果定义为BOTH，会使用LOCAL和REMOTE组合成两级缓存
     *
     * Type of the Cache instance. May be CacheType.REMOTE, CacheType.LOCAL, CacheType.BOTH.
     * Create a two level cache (local+remote) when value is CacheType.BOTH.
     * @return cache type of the method cache
     */
    CacheType cacheType() default CacheType.REMOTE;

    /**
     *
     * 如果cacheType为CacheType.LOCAL或CacheType.BOTH，这个参数指定本地缓存的最大元素数量，以控制内存占用。注解上没有定义的时候会使用全局配置，如果此时全局配置也没有定义，则取100
     *
     * Specify max elements in local memory when cacheType is CacheType.LOCAL or CacheType.BOTH.
     * Use global config if the attribute value is absent, and if the global config is not defined either,
     * use CacheConsts.DEFAULT_LOCAL_LIMIT instead.
     * @return local maximal elements of the LOCAL/BOTH cache
     */
    int localLimit() default CacheConsts.UNDEFINED_INT;

    /**
     *
     * 如果cacheType为CacheType.REMOTE或CacheType.BOTH，指定远程缓存的序列化方式。JetCache内置的可选值为SerialPolicy.JAVA和SerialPolicy.KRYO。注解上没有定义的时候会使用全局配置，如果此时全局配置也没有定义，则取SerialPolicy.JAVA
     *
     * Specify the serialization policy of remote cache when cacheType is CacheType.REMOTE or CacheType.BOTH.
     * The JetCache build-in serialPolicy are SerialPolicy.JAVA or SerialPolicy.KRYO.
     * Use global config if the attribute value is absent, and if the global config is not defined either,
     * use SerialPolicy.JAVA instead.
     * @return the serialization policy name of cache value
     */
    String serialPolicy() default CacheConsts.UNDEFINED_STRING;

    /**
     *
     * 指定KEY的转换方式，用于将复杂的KEY类型转换为缓存实现可以接受的类型，JetCache内置的可选值为KeyConvertor.FASTJSON和KeyConvertor.NONE。NONE表示不转换，FASTJSON通过fastjson将复杂对象KEY转换成String。如果注解上没有定义，则使用全局配置。
     *
     * Specify the key convertor. Used to convert the complex key object.
     * The JetCache build-in keyConvertor are KeyConvertor.FASTJSON or KeyConvertor.NONE.
     * NONE indicate do not convert, FASTJSON will use fastjson to convert key object to a string.
     * Use global config if the attribute value is absent.
     * @return convertor name of cache key
     */
    String keyConvertor() default CacheConsts.UNDEFINED_STRING;

    /**
     * Specify the key by expression script, optional. If not specified,
     * use all parameters of the target method and keyConvertor to generate one.
     * @return an expression script which specifies key
     */
    String key() default CacheConsts.UNDEFINED_STRING;

    /**
     *
     * 指定NULL值是否应该被缓存
     * Specify whether a null value should be cached.
     * @return whether a null value should be cached
     */
    boolean cacheNullValue() default CacheConsts.DEFAULT_CACHE_NULL_VALUE;

    /**
     * Expression script used for conditioning the method caching, the cache is not
     * used when evaluation result is false.
     * Evaluation occurs before real method invocation.
     */
    String condition() default CacheConsts.UNDEFINED_STRING;

    /**
     * Expression script used for conditioning the method cache updating,
     * the cache updating action is vetoed when the evaluation result is false.
     * Evaluation occurs after real method invocation so we can refer <i>result</i> in script.
     */
    String postCondition() default CacheConsts.UNDEFINED_STRING;

}
