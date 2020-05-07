package com.mcl.tools;

import java.lang.reflect.Field;

/**
 * Bean工具类
 * @author cgw
 * @date 2017年7月24日
 */
public class BeanUtil {

	/**
	 * 判断指定的bean的所有属性是否为空
	 * @param bean
	 * @return
	 */
	public static boolean isEmpty(Object bean){
		if (bean == null) return true;
		
		Field[] fields = bean.getClass().getDeclaredFields();
		
		if (fields == null) return true;
		
		try {
			for (Field field : fields){
				if (!field.isAccessible()){
					field.setAccessible(true);
				}
				if (field.get(bean) != null){
					return false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
}
