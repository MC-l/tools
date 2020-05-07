package com.mcl.tools;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/**
 * 系统缓存
 * @author cgw
 * @date 2017年9月5日
 */
public class Cache {

	private static final Map<String, Object> cache = new ConcurrentHashMap<String, Object>();
	private static final Map<String, Long> expireCache = new ConcurrentHashMap<String, Long>();
	private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private static final WriteLock wLock = lock.writeLock();
	private static final ReadLock rLock = lock.readLock();
	private static final int delay = 30;	// clean任务执行的时间频率(分钟)
	
	static {
		autoClean();
	}
	/**
	 * 获取值
	 * @param key
	 * @return 1,null: 如果不存在或者过期,
	 * 		   2,值
	 */
	public static Object get(String key){
		try {
			if (rLock.tryLock(5, TimeUnit.SECONDS)){
				if (!expireCache.containsKey(key) || !isExpired(key)){
					return cache.get(key);
				} 
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			rLock.unlock();
		}
		
		return null;
	}
	
	public static List<?> getStartWith(String prefix){
		if (prefix == null) return Collections.emptyList();
		List<Object> result = new ArrayList<Object>();
		Set<?> keySet = keySet();
		Iterator<?> it = keySet.iterator();
		while (it.hasNext()){
			String key = (String) it.next(); 
			if (key.startsWith(prefix)){
				result.add(get(key));
			}
		}
		return result;
	}
	
	public static String getInfoStartWith(String prefix){
		if (prefix == null) return "{}";
		Iterator<String> it = Cache.keySet().iterator();
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");
		while (it.hasNext()){
			String k = it.next();
			if (k.startsWith(prefix))
			sb.append(k+":"+Cache.get(k)+"\n");
		}
		sb.append("}");
		return sb.toString();
	}
	
	public static Set<String> keySet(){
		return cache.keySet();
	}
	
	public static Map<String, Long> getExpireCache(){
		return Collections.unmodifiableMap(expireCache);	// 由于value是Long类型,享元模式,所以不担心被修改
	}
	
	/**
	 * 出栈并删除超时key
	 * @param key
	 * @return
	 */
	public static Object pop(String key){
		Object obj = null;
		try {
			if (wLock.tryLock(5, TimeUnit.SECONDS)){
				obj = cache.get(key);
				if (obj != null){
					remove(key);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (wLock != null && wLock.isHeldByCurrentThread()){
				wLock.unlock();
			}
		}
		return obj;
	}
	
	public static Object put(String key, Object value){
		return cache.put(key, value);
	}
	
	public static Object put(String key, Object value, long seconds){
		try {
			if (wLock.tryLock(5, TimeUnit.SECONDS)){
				Object obj = cache.put(key, value);
				setExpire(key, seconds);
				return obj;
			}
		} catch (Exception e) {
		} finally {
			if (wLock != null && wLock.isHeldByCurrentThread()){
				wLock.unlock();
			}
		}
		return null;
	}
	
	public static Object putIfAbsent(String key, Object value){
		return cache.putIfAbsent(key, value);
	}
	
	public static Long setExpire(String key, long seconds){
		long now = System.currentTimeMillis();
		long time = seconds*1000;
		return expireCache.putIfAbsent(key, now+time);
	}
	
	public static void reSetExpire(String key, long seconds){
		try {
			if (wLock.tryLock(5, TimeUnit.SECONDS)){
				Object object = get(key);
				Objects.requireNonNull(object, "对象为空,不能重新设置缓存时间");
				remove(key);
				put(key, object, seconds);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (wLock != null && wLock.isHeldByCurrentThread()){
				wLock.unlock();
			}
		}
	}
	
	public static Object remove(String key){
		try {
			if (wLock.tryLock(5, TimeUnit.SECONDS)){
				Object object = cache.remove(key);
				if (expireCache.containsKey(key)){
					expireCache.remove(key);
				}
				return object;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (wLock != null && wLock.isHeldByCurrentThread()){
				wLock.unlock();
			}
		}
		return null;
	}
	
	public static boolean isExpired(String key){
		
		Long expireTime = (Long) expireCache.get(key);
		long now = System.currentTimeMillis();
		if (now < expireTime){
			return false;
		}
		return true;
	}
	
	public static boolean contains(String key){
		return cache.containsKey(key);
	}
	
	public static long size(){
		return cache.size();
	}
	
	/**
	 * 自动清空缓存(如果不设置过期时间,默认永久有效)
	 */
	private static void autoClean(){
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(()->{
			Set<String> keySet = expireCache.keySet();
			Iterator<String> iterator = keySet.iterator();
			while (iterator.hasNext()){
				String key = iterator.next();
				if (isExpired(key)){
					cache.remove(key);
					iterator.remove();
				}
			}
		},delay, delay, TimeUnit.MINUTES);
	}
	
}
