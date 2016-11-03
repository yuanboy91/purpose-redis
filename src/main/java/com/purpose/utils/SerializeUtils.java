package com.purpose.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
/**
 * 序列化工具类
 * @author: Yuanbo
 * @date 2016年3月28日 下午2:26:10
 * @version V1.0
 */
public class SerializeUtils {

	/**
	 * 序列化
	 * @param Object
	 * @return
	 */
	public static byte[] serialize(Object o) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			ObjectOutputStream outo = new ObjectOutputStream(out);
			outo.writeObject(o);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out.toByteArray();
	}

	/**
	 * 反序列化
	 * @param byte
	 * @return
	 */
	public static Object deserialize(byte[] b) {
		ObjectInputStream oin;
		try {
			oin = new ObjectInputStream(new ByteArrayInputStream(b));
			try {
				return oin.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
}