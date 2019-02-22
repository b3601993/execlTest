package jvm.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 *
 * 
 * @version 1.0
 * @since JDK1.7
 * @author yutao
 * @company 上海朝阳永续信息技术有限公司
 * @copyright (c) 2016 SunTime Co'Ltd Inc.All rights reserved.
 * @date 2018年12月11日下午7:33:33
 */
public class DynamicProxyTest {

	
	interface IHello{
		void sayHello();
	}
	
	static class Hello implements IHello{

		/**
		 * 
		 * @see jvm.proxy.DynamicProxyTest.IHello#sayHello()
		 */
		@Override
		public void sayHello() {
			System.out.println("hello world");
		}
		
	}
	
	static class DynamicProxy implements InvocationHandler{

		Object originalObj;
		
		Object bind(Object originalObj) {
			this.originalObj = originalObj;
			return Proxy.newProxyInstance(originalObj.getClass().getClassLoader(), originalObj.getClass().getInterfaces(), this);
		}
		
		
		/**
		 * 
		 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
		 */
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			
			System.out.println("welcome");
			
			return method.invoke(originalObj, args);
		}
		
	}
	
	
	public static void main(String[] args) {
		
		IHello hello = (IHello) new DynamicProxy().bind(new Hello());
		hello.sayHello();
		
	}
}
