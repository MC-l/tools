package com.mcl.tools;

import java.util.Map;
import java.util.concurrent.*;


/**
 * 统一的线程池
 * @author cgw
 * @date 2017年11月11日
 */
public final class ThreadPooler {
	
	private ThreadPooler(){}

	/**
	 * 后台线程池
	 */
	private static final ThreadPoolExecutor daemonPoolExecutor;
	/**
	 * 前台线程池
	 */
	private static final ThreadPoolExecutor frontPoolExecutor;
	/**
	 * 后台定时任务线程池
	 */
	private static final ScheduledExecutorService daemonScheduledPool;
	/**
	 * 前台定时任务线程池
	 */
	private static final ScheduledExecutorService frontScheduledPool;
	
	/**
	 * 用以存储执行任务返回的future
	 */
	private static final Map<String,Future<?>> futureMap = new ConcurrentHashMap<String,Future<?>>();
	

	static {
		// N(threads)=N(cpu)*U(cpu)*(1+W/C)   
		// N(cpu)=CPU的数量=Runtime.getRuntime().availableProcessors(); 
		// U(cpu)= 期望CPU的使用率，0<=U(cpu)<=1 ；
		// W/C=等待时间与运行时间的比率
		
		int cpuCores = Runtime.getRuntime().availableProcessors();
		double U = 0.7;
		double wc = 1;
		int coreSize = (int)(cpuCores * U * (1+wc));
		
		daemonPoolExecutor = new ThreadPoolExecutor(
				coreSize, 				// corePoolSize
				coreSize*2, 			// maximumPoolSize
				2, TimeUnit.MINUTES, 	// keepAliveTime
				new ArrayBlockingQueue<>(500000),	// Bound queue
				newThreadFactory(true,""),
				new ThreadPoolExecutor.AbortPolicy());	// ignore And throw a RejectedExecutionException
		
		
		frontPoolExecutor = new ThreadPoolExecutor(
				coreSize, 				// corePoolSize
				coreSize*2, 			// maximumPoolSize
				2, TimeUnit.MINUTES, 	// keepAliveTime
				new ArrayBlockingQueue<>(500000),	// Bound queue
				newThreadFactory(false,""),
				new ThreadPoolExecutor.AbortPolicy());	// ignore And throw a RejectedExecutionException
		
		
		daemonScheduledPool = Executors.newScheduledThreadPool(10, newThreadFactory(true,""));
		
		frontScheduledPool = Executors.newScheduledThreadPool(10, newThreadFactory(false,""));
	}
	
	/**
	 * 获取线程创建工厂
	 * @param daemon	是否是后台线程
	 * @param name		名字
	 * @return
	 */
	private static ThreadFactory newThreadFactory(boolean daemon,String name){
		return new ThreadFactory(){
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r,(daemon ? "后":"前")+"台线程--"+name);
				thread.setDaemon(daemon);
				return thread;
			};
		};
	}
	
	/**
	 * 提交同步任务到前台线程
	 * @param caller
	 * @return
	 * @throws Exception
	 */
	public static <T> T submitSyncCallerOnFront(Callable<T> caller) throws Exception{
		T ret = null;
		try {
			Future<T> futrue = frontPoolExecutor.submit(caller);
			ret = futrue.get();
		} catch (Exception e) {
			throw e;
		}
		
		return ret;
	}
	/**
	 * 提交异步任务到前台线程
	 * @param caller
	 * @return
	 * @throws Exception
	 */
	public static <T> Future<T> submitAsyncCallerOnFront(Callable<T> caller) throws Exception{
		try {
			return frontPoolExecutor.submit(caller);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 提交同步任务到后台线程
	 * @param caller
	 * @return
	 * @throws Exception
	 */
	public static <T> T submitSyncCallerOnDaemon(Callable<T> caller) throws Exception{
		T ret = null;
		try {
			Future<T> futrue = daemonPoolExecutor.submit(caller);
			ret = futrue.get();
		} catch (Exception e) {
			throw e;
		}
		
		return ret;
	}
	
	/**
	 * 提交异步任务到后台线程
	 * @param caller
	 * @return
	 * @throws Exception
	 */
	public static <T> Future<T> submitAsyncCallerOnDaemon(Callable<T> caller) throws Exception{
		try {
			return daemonPoolExecutor.submit(caller);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 执行异步后台任务
	 * @param run
	 */
	public static void executeAsyncRunnerOnDaemon(Runnable run){
		daemonPoolExecutor.execute(run);
	}
	
	/**
	 * 执行同步后台任务
	 * @param run
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public static void executeSyncRunnerOnDaemon(Runnable run) throws InterruptedException, ExecutionException{
		Future<?> submit = daemonPoolExecutor.submit(run);
		submit.get();	// 阻塞
		return ;
	}
	
	/**
	 * 执行异步前台任务
	 * @param run
	 */
	public static void executeAsyncRunnerOnFront(Runnable run){
		frontPoolExecutor.execute(run);
	}
	/**
	 * 执行同步前台任务
	 * @param run
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public static void executeSyncRunnerOnFront(Runnable run) throws InterruptedException, ExecutionException{
		Future<?> submit = frontPoolExecutor.submit(run);
		submit.get(); // 阻塞
		return ;
	}
	
	/**
	 * 执行定时异步前台任务
	 * @param call	任务
	 * @param delay	延时
	 * @param unit	时间单位
	 * @return
	 */
	public static <T> Future<T> scheduleAsyncCallableOnFront(Callable<T> call, long delay, TimeUnit unit){
		ScheduledFuture<T> future = frontScheduledPool.schedule(call, delay, unit);
		return future;
	}
	
	/**
	 * 执行定时同步前台任务
	 * @param call	任务
	 * @param delay	延时
	 * @param unit	时间单位
	 * @return
	 */
	public static <T> T scheduleSyncCallableOnFront(Callable<T> call, long delay, TimeUnit unit){
		ScheduledFuture<T> future = frontScheduledPool.schedule(call, delay, unit);
		T t = null;
		try {
			t = future.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return t;
	}
	
	/**
	 * 延时-固定频率-执行-前台任务
	 * @param run
	 * @param delay
	 * @param period
	 * @param unit
	 * @return	调用future.cancel(true);可以中断当前任务
	 */
	public static Future<?> scheduleAsyncRatedRunnableOnFront(SelfCancelRunnable run, long delay, long period, TimeUnit unit){
		ScheduledFuture<?> future = frontScheduledPool.scheduleAtFixedRate(run, delay, period, unit);
		futureMap.put(run.getKey(), future);
		return future;
	}
	/**
	 * 延时-执行-前台任务
	 * @param run
	 * @param delay
	 * @param unit
	 * @return	调用future.cancel(true);可以中断当前任务
	 */
	public static Future<?> scheduleAsyncRunnableOnFront(SelfCancelRunnable run, long delay, TimeUnit unit){
		ScheduledFuture<?> future = frontScheduledPool.schedule(run, delay, unit);
		futureMap.put(run.getKey(), future);
		return future;
	}
	
	/**
	 * 延时-固定频率-执行-后台任务
	 * @param run
	 * @param delay
	 * @param period
	 * @param unit
	 * @return	调用future.cancel(true);可以中断当前任务
	 */
	public static Future<?> scheduleAsyncRatedRunnableOnDaemon(SelfCancelRunnable run, long delay, long period, TimeUnit unit){
		ScheduledFuture<?> future = daemonScheduledPool.scheduleAtFixedRate(run, delay, period, unit);
		futureMap.put(run.getKey(), future);
		return future;
	}
	/**
	 * 延时-执行-后台任务
	 * @param run
	 * @param delay
	 * @param unit
	 * @return	调用future.cancel(true);可以中断当前任务
	 */
	public static Future<?> scheduleAsyncRunnableOnDaemon(SelfCancelRunnable run, long delay, TimeUnit unit){
		ScheduledFuture<?> future = daemonScheduledPool.schedule(run, delay, unit);
		futureMap.put(run.getKey(), future);
		return future;
	}
	/**
	 * 延时-执行-前台任务
	 * @param call
	 * @param delay
	 * @param unit
	 * @return	调用future.cancel(true);可以中断当前任务
	 */
	public static <T> Future<T> scheduleAsyncCallableOnFront(SelfCancelCallable<T> call, long delay, TimeUnit unit){
		ScheduledFuture<T> future = frontScheduledPool.schedule(call, delay, unit);
		futureMap.put(call.getKey(), future);
		return future;
	}
	
	/**
	 * 延时-执行-后台任务
	 * @param call
	 * @param delay
	 * @param unit
	 * @return	调用future.cancel(true);可以中断当前任务
	 */
	public static <T> Future<T> scheduleAsyncCallableOnDaemon(SelfCancelCallable<T> call, long delay, TimeUnit unit){
		ScheduledFuture<T> future = daemonScheduledPool.schedule(call, delay, unit);
		futureMap.put(call.getKey(), future);
		return future;
	}
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		SelfCancelCallable<Integer> call = new SelfCancelCallable<Integer>() {

			@Override
			public Integer call() throws Exception {
				return 1+2;
			}
			
		};
		Future<Integer> future = scheduleAsyncCallableOnFront(call,1,TimeUnit.SECONDS);
		
		cancelTask(call);
		System.out.println(future.get());
	}
	
	/**
	 * 取消任务
	 * @param run
	 * @return
	 */
	public static boolean cancelTask(SelfCancelRunnable run){
		if (run == null || run.getKey() == null){
			return true;
		}
		return cancelTask(run.getKey());
	}
	
	/**
	 * 取消任务
	 * @param call
	 * @return
	 */
	public static boolean cancelTask(SelfCancelCallable<?> call){
		if (call == null || call.getKey() == null){
			return true;
		}
		return cancelTask(call.getKey());
	}
	
	private static boolean cancelTask(String taskKey) {
		Future<?> future = futureMap.get(taskKey);
		if (future == null){
			return true;
		}
		boolean cancel = future.cancel(true);
		
		if (cancel || future.isDone()){
			futureMap.remove(taskKey);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 执行定时异步后台任务
	 * @param call	任务
	 * @param delay	延时
	 * @param unit	时间单位
	 * @return
	 */
	public static <T> Future<T> scheduleAsyncCallableOnDaemon(Callable<T> call, long delay, TimeUnit unit){
		ScheduledFuture<T> future = daemonScheduledPool.schedule(call, delay, unit);
		return future;
	}
	
	/**
	 * 执行定时同步后台任务
	 * @param call	任务
	 * @param delay	延时
	 * @param unit	时间单位
	 * @return
	 */
	public static <T> T scheduleSyncCallableOnDaemon(Callable<T> call, long delay, TimeUnit unit){
		ScheduledFuture<T> future = daemonScheduledPool.schedule(call, delay, unit);
		T t = null;
		try {
			t = future.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return t;
	}
	
	/**
	 * 执行定时异步前台任务
	 * @param run
	 * @param delay
	 * @param unit
	 */
	public static void scheduleAsyncRunnableOnFront(Runnable run, long delay, TimeUnit unit){
		frontScheduledPool.schedule(run, delay, unit);
	}
	
	/**
	 * 执行定时同步前台任务
	 * @param run
	 * @param delay
	 * @param unit
	 */
	public static void scheduleSyncRunnableOnFront(Runnable run, long delay, TimeUnit unit){
		ScheduledFuture<?> future = frontScheduledPool.schedule(run, delay, unit);
		try {
			future.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return;
	}
	
	/**
	 * 执行定时异步后台任务
	 * @param run
	 * @param delay
	 * @param unit
	 */
	public static void scheduleAsyncRunnableOnDaemon(Runnable run, long delay, TimeUnit unit){
		daemonScheduledPool.schedule(run, delay, unit);
	}
	
	/**
	 * 执行定时同步后台任务
	 * @param run
	 * @param delay
	 * @param unit
	 */
	public static void scheduleSyncRunnableOnDaemon(Runnable run, long delay, TimeUnit unit){
		ScheduledFuture<?> future = daemonScheduledPool.schedule(run, delay, unit);
		try {
			future.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return;
	}
	
	
	/**
	 * 发送关闭后台线程池信号(不再接受新任务)
	 */
	public static void shutDownDaemonPooler(){
		daemonPoolExecutor.shutdown();
	}
	/**
	 * 强制关闭后台线程池(不再接受新任务且清空任务池里等待的任务)
	 */
	public static void shutDownDaemonPoolerNow(){
		daemonPoolExecutor.shutdownNow();
	}
	
	/**
	 * 发送关闭前台线程池信号(不再接受新任务)
	 */
	public static void shutDownFrontPooler(){
		frontPoolExecutor.shutdown();
	}
	/**
	 * 强制关闭前台线程池(不再接受新任务且清空任务池里等待的任务)
	 */
	public static void shutDownFrontPoolerNow(){
		frontPoolExecutor.shutdownNow();
	}
	
	/**
	 * 发送关闭所有线程池信号(不再接受新任务)
	 */
	public static void shutDownAll(){
		shutDownDaemonPooler();
		shutDownDaemonSchedule();
		shutDownFrontPooler();
		shutDownFrontSchedule();
	}
	
	/**
	 * 强制关闭所有线程池(不再接受新任务且清空任务池里等待的任务)
	 */
	public static void shutDownAllNow(){
		shutDownDaemonPoolerNow();
		shutDownDaemonScheduleNow();
		shutDownFrontPoolerNow();
		shutDownFrontScheduleNow();
	}
	
	/**
	 * 发送关闭后台定时任务线程池信号(不再接受新任务)
	 */
	public static void shutDownDaemonSchedule(){
		daemonScheduledPool.shutdown();
	}
	/**
	 * 强制关闭后台定时任务线程池(不再接受新任务且清空任务池里等待的任务)
	 */
	public static void shutDownDaemonScheduleNow(){
		daemonScheduledPool.shutdownNow();
	}
	
	/**
	 * 发送关闭前台定时任务线程池信号(不再接受新任务)
	 */
	public static void shutDownFrontSchedule(){
		frontScheduledPool.shutdown();
	}
	/**
	 * 强制关闭前台定时任务线程池(不再接受新任务且清空任务池里等待的任务)
	 */
	public static void shutDownFrontScheduleNow(){
		frontScheduledPool.shutdownNow();
	}
	
	/**
	 * 线程池信息
	 * @author cgw
	 * @date 2017年11月12日
	 */
	public static class Info{
		
		/**
		 * 后台线程池的信息
		 * @author cgw
		 * @date 2017年11月12日
		 */
		public static class Daemon {
			
			public static int getActiveCount() {
				return daemonPoolExecutor.getActiveCount();
			}
			public static long getCompletedTaskCount() {
				return daemonPoolExecutor.getCompletedTaskCount();
			}
			public static int getMaximumPoolSize() {
				return daemonPoolExecutor.getMaximumPoolSize();
			}
			public static int getQueueSize() {
				return daemonPoolExecutor.getQueue().size();
			}
			public static int getQueueRemainingCapacity() {
				return daemonPoolExecutor.getQueue().remainingCapacity();
			}
			public static int getCorePoolSize() {
				return daemonPoolExecutor.getCorePoolSize();
			}
			public static long getKeepAliveTime() {
				return daemonPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS);
			}
			public static int getLargestPoolSize() {
				return daemonPoolExecutor.getLargestPoolSize();
			}
			public static int getPoolSize() {
				return daemonPoolExecutor.getPoolSize();
			}
			public static long getTaskCount() {
				return daemonPoolExecutor.getTaskCount();
			}
			/**
			 * 获取后台线程池总体运行情况
			 * @return
			 */
			public static String summary(){
				StringBuilder sb = new StringBuilder();
				
				sb.append("\nDeamon Max Pool Size               "+getMaximumPoolSize());
				sb.append("\nDeamon Pool Size                   "+getPoolSize());
				sb.append("\nDeamon Core Pool Size              "+getCorePoolSize());
				sb.append("\nDeamon Largest Pool Size           "+getLargestPoolSize());
				sb.append("\nDeamon Queue Size                  "+getQueueSize());
				sb.append("\nDeamon Queue Remaining Capacity    "+getQueueRemainingCapacity());
				sb.append("\nDeamon Task Count                  "+getTaskCount());
				sb.append("\nDeamon Active Count                "+getActiveCount());
				sb.append("\nDeamon Completed Task Count        "+getCompletedTaskCount());
				sb.append("\nDeamon Keep Alive Time             "+getKeepAliveTime());
				
				return sb.toString();
				
			}
			
		}
		
		/**
		 * 前台线程池的信息
		 * @author cgw
		 * @date 2017年11月12日
		 */
		public static class Front {
			
			public static int getActiveCount() {
				return frontPoolExecutor.getActiveCount();
			}
			public static long getCompletedTaskCount() {
				return frontPoolExecutor.getCompletedTaskCount();
			}
			public static int getMaximumPoolSize() {
				return frontPoolExecutor.getMaximumPoolSize();
			}
			public static int getQueueSize() {
				return frontPoolExecutor.getQueue().size();
			}
			public static int getQueueRemainingCapacity() {
				return frontPoolExecutor.getQueue().remainingCapacity();
			}
			public static int getCorePoolSize() {
				return frontPoolExecutor.getCorePoolSize();
			}
			public static long getKeepAliveTime() {
				return frontPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS);
			}
			public static int getLargestPoolSize() {
				return frontPoolExecutor.getLargestPoolSize();
			}
			public static int getPoolSize() {
				return frontPoolExecutor.getPoolSize();
			}
			public static long getTaskCount() {
				return frontPoolExecutor.getTaskCount();
			}
			/**
			 * 获取前台线程池总体运行情况
			 * @return
			 */
			public static String summary(){
				StringBuilder sb = new StringBuilder();
				
				sb.append("\nFront Max Pool Size               "+getMaximumPoolSize());
				sb.append("\nFront Pool Size                   "+getPoolSize());
				sb.append("\nFront Core Pool Size              "+getCorePoolSize());
				sb.append("\nFront Largest Pool Size           "+getLargestPoolSize());
				sb.append("\nFront Queue Size                  "+getQueueSize());
				sb.append("\nFront Queue Remaining Capacity    "+getQueueRemainingCapacity());
				sb.append("\nFront Task Count                  "+getTaskCount());
				sb.append("\nFront Active Count                "+getActiveCount());
				sb.append("\nFront Completed Task Count        "+getCompletedTaskCount());
				sb.append("\nFront Keep Alive Time             "+getKeepAliveTime());
				
				return sb.toString();
				
			}
		}
	}
	
}