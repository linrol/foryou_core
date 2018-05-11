package foryou.core.util;

/**
 * @author 罗林 E-mail:1071893649@qq.com
 * @version 创建时间：2018年1月14日 上午2:35:04 类说明
 */
public class JdkVersion {

	private static final String javaVersion;
	private static boolean isJava8;

	static {
		javaVersion = System.getProperty("java.version");
		System.out.println("javaVersion=" + javaVersion);
		if (javaVersion.contains("1.8.")) {
			isJava8 = true;
		}
	}

	public static String getJavaVersion() {
		return javaVersion;
	}

	public static boolean isJava8() {
		return isJava8;
	}

}