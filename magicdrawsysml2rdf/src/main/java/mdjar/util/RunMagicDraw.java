package mdjar.util;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;



public class RunMagicDraw {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
					
			URL[] classLoaderURLs;
			try {
//				classLoaderURLs = new URL[]{new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/patch.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/activation.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/annotation.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/batik.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/cmof14.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/com.nomagic.ci.binary_17.0.1.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/com.nomagic.ci.metamodel.index_17.0.1.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/com.nomagic.ci.metamodel.project_17.0.1.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/com.nomagic.ci.persistence.local_17.0.1.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/com.nomagic.ci.persistence_17.0.1.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/com.nomagic.ci.services_17.0.1.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/com.nomagic.ci_17.0.1.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/commons-codec-1.3.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/commons-compress-1.3.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/commons-httpclient-3.1.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/commons-logging-1.0.4.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/concurrent.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/cvsclient.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/EccpressoAll.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/ehcache-2.10.1.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/flexlm.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/gson-2.2.4.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/HTMLEditorLight.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/javassist.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/javax_jmi-1_0-fr.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jaxb-impl.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jaxb-libs.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jboss-aop-client.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jboss-aspect-jdk50-client.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jboss-common-core.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jboss-ejb3-common-client.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jboss-ejb3-core-client.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jboss-ejb3-ext-api.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jboss-ejb3-proxy-client.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jboss-ejb3-security-client.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jboss-integration.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jboss-javaee.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jboss-logging-spi.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jboss-mdr.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jboss-messaging-client.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jboss-remoting.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jboss-security-spi.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jboss-serialization.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jbossall-client.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jbosscx-client.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jbosssx-as-client.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jbosssx-client.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jbosssx.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jhall.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jide-action.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jide-charts.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jide-common.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jide-components.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jide-dialogs.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jide-dock.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jide-editor.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jide-gantt.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jide-grids.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jide-properties.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jide-shortcut.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jimi.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jmyspell-core-1.0.0-beta-2.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jna.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jnp-client.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jsr250-api-1.0.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/jsr305.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/junit.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/launcher.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/log4j-1.2.15.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/lpg.runtime.java_2.0.17.v201004271640.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/md.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/md_api.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/md_common.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/md_common_api.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/md_foundation_1.0.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/namespace.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/org.apache.commons.codec_1.3.0.v201101211617.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/org.apache.commons.collections_3.2.0.v201005080500.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/org.apache.commons.logging_1.1.1.v201101211721.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/org.eclipse.core.contenttype_3.4.100.v20110423-0524.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/org.eclipse.core.jobs_3.5.100.v20110404.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/org.eclipse.core.runtime_3.7.0.v20110110.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/org.eclipse.emf.common_2.7.0.v20110513-1719.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/org.eclipse.emf.ecore.change_2.7.0.v20110408-2116.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/org.eclipse.emf.ecore.xmi_2.7.0.v20110411-2239.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/org.eclipse.emf.ecore_2.7.0.v20110513-1719.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/org.eclipse.emf.transaction_1.4.0.v20100331-1738.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/org.eclipse.equinox.common_3.6.0.v20110506.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/org.eclipse.equinox.registry_3.5.100.v20110502.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/org.eclipse.ocl.doc_3.0.1.R30x_v201008311245.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/org.eclipse.ocl.ecore.edit.source_3.0.0.R30x_v201008251030.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/org.eclipse.ocl.ecore.edit_3.0.0.R30x_v201008251030.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/org.eclipse.ocl.ecore.source_3.0.1.R30x_v201008251030.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/org.eclipse.ocl.ecore_3.0.1.R30x_v201008251030.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/org.eclipse.ocl.source_3.0.1.R30x_v201008251030.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/org.eclipse.ocl_3.0.1.R30x_v201008251030.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/org.eclipse.osgi_3.7.0.v20110513.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/org.eclipse.uml2.common_1.5.0.v200905041045.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/relaxngDatatype.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/slf4j-api-1.5.11.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/slf4j-jdk14-1.5.11.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/trove.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/truezip-driver-zip-7.4.3.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/truezip-kernel-7.4.3.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/tw_common.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/tw_common_api.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/uml2.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/xalan.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/xsdlib.jar"), new URL("file:///C:/Program Files/MagicDraw 18.0 sp6/lib/y.jar")
//};
				
				classLoaderURLs = PrintPathsToMagicDrawJars.getClassLoaderURLs("C:\\Program Files\\MagicDraw 18.0 sp6");
				
				URLClassLoader uRLClassLoader = new URLClassLoader(classLoaderURLs);
												
				Class<?> applicationClass = uRLClassLoader.loadClass("com.nomagic.magicdraw.core.Application");
				
				Method applicationGetInstanceMethod = applicationClass.getMethod("getInstance");
				
				Object applicationClassInstance = applicationGetInstanceMethod.invoke(applicationClass);
				
				Class<?> startupParticipantClass = uRLClassLoader.loadClass("com.nomagic.magicdraw.core.StartupParticipant");
				
				
				Class[] applicationStartMethodArgTypes = new Class[5];
				applicationStartMethodArgTypes[0] = boolean.class;
				applicationStartMethodArgTypes[1] = boolean.class;
				applicationStartMethodArgTypes[2] = boolean.class;
				applicationStartMethodArgTypes[3] = String[].class;
				applicationStartMethodArgTypes[4] = startupParticipantClass;
				
				Method method = applicationClass.getMethod("start", applicationStartMethodArgTypes);

				String[] emptyStringArrayArg = new String[]{};
				
				Object[] applicationStartMethodArgs = new Object[5];
				applicationStartMethodArgs[0] = true;
				applicationStartMethodArgs[1] = false;
				applicationStartMethodArgs[2] = false;
				applicationStartMethodArgs[3] = emptyStringArrayArg;
				applicationStartMethodArgs[4] = null;
							
				method.invoke(applicationClassInstance,  applicationStartMethodArgs);
				
				System.out.println("MagicDraw started");
			} 
//			catch (MalformedURLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} 
			catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			catch (ApplicationExitedException e) {
//				e.printStackTrace();
//			}
			
		} 
		

	

}