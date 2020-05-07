package com.mcl.tools;

import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * 可自行取消的任务
 * @author cgw
 * @date 2017年11月16日
 */
public abstract class SelfCancelCallable<T> implements Callable<T>{
	
	private String key = null;
	public SelfCancelCallable() {
		this.key = UUID.randomUUID().toString();
	}
	public String getKey() {
		return key;
	}
}
