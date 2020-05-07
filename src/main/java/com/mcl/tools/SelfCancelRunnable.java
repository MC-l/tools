package com.mcl.tools;

import java.util.UUID;

/**
 * 可自行取消的任务
 * @author cgw
 * @date 2017年11月16日
 */
public abstract class SelfCancelRunnable implements Runnable {
	
	private String key = null;
	public SelfCancelRunnable() {
		this.key = UUID.randomUUID().toString();
	}
	public String getKey() {
		return key;
	}
}
