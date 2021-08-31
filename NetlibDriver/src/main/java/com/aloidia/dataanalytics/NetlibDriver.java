package com.aloidia.datascience;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.apache.log4j.Logger;

import com.github.fommil.netlib.BLAS;

//import com.sun.java.util.jar.pack.Package.Class.Field;

public class NetlibDriver {
	private final static Logger log = Logger.getLogger(NetlibDriver.class);

	public static void main(String[] args) {

		// //Can we programmatically change LD_LIBRARY_PATH? //This tries to do
		// it via java class path - conflicting documentation on whether this is
		// the right thing you.
		// String newLibPath = "/usr/lib64/atlas/" + File.pathSeparator
		// + System.getProperty("java.library.path");
		// System.setProperty("java.library.path", newLibPath);
		// Field fieldSysPath = ClassLoader.class
		// .getDeclaredField("sys_paths");
		// fieldSysPath.setAccessible(true);
		// if (fieldSysPath != null) {
		// fieldSysPath.set(System.class.getClassLoader(), null);
		// }

		String libPath = System.getProperty("java.library.path");
		// System.setProperty("java.library.path", libPath +
		// ":/usr/lib64/atlas/");
		System.setProperty("java.library.path", libPath
				+ ":/home/bcampbell/workspace/netlib-demo");

		libPath = System.getProperty("java.library.path");

		log.debug("java.library.path = " + libPath);

		log.debug("Loading Library from system path: "
				+ System.getProperty("java.library.path"));
		try {

			System.load("/usr/lib64/atlas/libatlas.so");
			// This does not work despite the library being in the
			// LD_LIBRARYPATH and set up in ldconf
			// System.loadLibrary("netlib-native_system-linux-i686.so");

		} catch (UnsatisfiedLinkError ex) {

			log.debug("Unable to load libatlas.so from System Path");
		}
		try {

			System.out.println(BLAS.getInstance().getClass().getName());

			// get native blas object and make it accessible
			Class nativeBlasClass = Class.forName("org.netlib.blas.NativeBLAS");
			Field nativeBlas = nativeBlasClass.getDeclaredField("INSTANCE");
			Field nInstance = nativeBlas.getClass().getDeclaredField(
					"modifiers");
			nInstance.setAccessible(true);
			nInstance.setInt(nativeBlas, nativeBlas.getModifiers()
					& ~Modifier.FINAL);
			nativeBlas.setAccessible(true);

			Class javaBlasClass = Class.forName("org.netlib.blas.JBLAS");
			Field javaBlas = javaBlasClass.getDeclaredField("INSTANCE");
			Field jInstance = javaBlas.getClass().getDeclaredField("modifiers");
			jInstance.setAccessible(true);
			jInstance.setInt(javaBlas, javaBlas.getModifiers()
					& ~Modifier.FINAL);
			javaBlas.setAccessible(true);

			// get blas current object and make it accessible
			Field blasCurrent = Class.forName("org.netlib.blas.BLAS")
					.getDeclaredField("current");
			Field bInstance = blasCurrent.getClass().getDeclaredField(
					"modifiers");
			bInstance.setAccessible(true);
			bInstance.setInt(blasCurrent, blasCurrent.getModifiers()
					& ~Modifier.FINAL);
			blasCurrent.setAccessible(true);
			// SET TO NativeBLAS
			blasCurrent.set(null, nativeBlas.get(null));
			// SET TO JBLAS
			blasCurrent.set(null, javaBlas.get(null));
		} catch (Exception ex) {
			System.out.print(ex.toString());
			System.out.print(ex.getStackTrace());
		}

		System.out.println(BLAS.getInstance().getClass().getName());
		System.out.println("Starting  Netlib Wrapper");
	}
}
