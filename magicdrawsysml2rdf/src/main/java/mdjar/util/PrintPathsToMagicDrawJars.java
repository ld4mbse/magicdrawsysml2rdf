package mdjar.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class PrintPathsToMagicDrawJars {

	private static String CLASS_FILE_TO_FIND = "RequirementProvider.class";
	private static List<String> foundIn = new LinkedList<String>();
	private static String magicDrawInstallDirPath = "C:\\Program Files\\MagicDraw 18.0 sp6";

	public static void main(String[] args) {
		
		String filePath = magicDrawInstallDirPath + "\\lib";
		File start = new File(filePath);
		search(start);
		System.out.println("------RESULTS------");
		for (String s : foundIn) {
			System.out.println(s);
		}

	}

	private static void search(File start) {
		try {
			final FileFilter filter = new FileFilter() {

				public boolean accept(File pathname) {
					return pathname.getName().endsWith(".jar") || pathname.isDirectory();
				}
			};
			StringBuffer buffer = new StringBuffer();
			for (File f : start.listFiles(filter)) {
				if (f.isDirectory()) {
					search(f);
				} else {
					searchJar(f, buffer);
				}
			}
			System.out.println(buffer);
		} catch (Exception e) {
			System.err.println("Error at: " + start.getPath() + " " + e.getMessage());
		}
	}

	private static void searchJar(File f, StringBuffer buffer) {
		try {
//			System.out.println("gere");
			String f1 = f.getPath().replace(magicDrawInstallDirPath, "");
			String f2 = f1.replace("\\", "/");
			
//			<classpathentry kind="var" path="magicdraw.installdir/plugins/com.nomagic.requirements/help.jar"/>
			
//			System.out.println("<classpathentry kind=\"var\" path=\"magicdrawinstalldir" + f2 + "\"/>");
			System.out.println(magicDrawInstallDirPath + f2);
			String str = magicDrawInstallDirPath + f2;
//			str = str.replace("/", "\\");
			str = str.replace("\\\\", "/");
			str = str.replace("\\", "/");
			
			str = str.replace("C:", "file:///C:");
			
			
			
			
//			buffer.append(", new URL(\"" + magicDrawInstallDirPath + f2 + "\")");
			buffer.append(", new URL(\"" + str + "\")");
			
			JarFile jar = new JarFile(f);
			ZipEntry e = jar.getEntry(CLASS_FILE_TO_FIND);
			if (e == null) {
				e = jar.getJarEntry(CLASS_FILE_TO_FIND);
				if (e != null) {
					foundIn.add(f.getPath());
				}
			} else {
				foundIn.add(f.getPath());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}