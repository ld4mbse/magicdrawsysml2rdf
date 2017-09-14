#!/bin/bash
# usage-magicdrawsysml2rdf.sh
if [ $# -eq 0 ]; then
    echo "Error: missing MagicDraw installation path as first argument."
    exit 1
fi
# IF THE FIRST ARGUMENT IS -help (CASE UNSENSITIVE).
if [ "${1,,}" == "-help" ]; then
    java -jar ${project.build.finalName}-jar-with-dependencies.jar -help
    exit 1
fi
if [ ! -e "$1" ]; then
    echo "Error: $1 directory does not exist."
    exit 1
fi
if [ ! -f "${project.build.finalName}-jar-with-dependencies.jar" ];  then
    echo "Error: cannot find ${project.build.finalName}-jar-with-dependencies.jar file."
    exit 1
fi
# TAKES MAGICDRAW DIRECTORY FROM FIRST ARGUMENT.
MD_HOME="$1"
# REPLACES ALL SPACES FOR %20 TO BE URL COMPLIANT.
MD_HOME="${MD_HOME// /%%20}"
# APPENDS A TRAILING SLASH IF NEEDED.
if [ "${MD_HOME: -1}" != "/" ]; then
  MD_HOME="${MD_HOME}/"
fi
# WRITES FIRST PART TO MANIFEST.
printf "Class-Path:" >> manifest.txt
# CREATES THE LIST OF ALL MD DEPENDENCIES.
list=("patch.jar" "md.jar" "md_api.jar" "md_common.jar" "md_common_api.jar" "md_foundation_1.0.jar" "tw_common.jar" "tw_common_api.jar" "launcher.jar" "activation.jar" "annotation.jar" "batik.jar" "cmof14.jar" "com.nomagic.ci.binary_17.0.1.jar" "com.nomagic.ci.metamodel.index_17.0.1.jar" "com.nomagic.ci.metamodel.project_17.0.1.jar" "com.nomagic.ci.persistence.local_17.0.1.jar" "com.nomagic.ci.persistence_17.0.1.jar" "com.nomagic.ci.services_17.0.1.jar" "com.nomagic.ci_17.0.1.jar" "commons-codec-1.3.jar" "commons-compress-1.3.jar" "commons-httpclient-3.1.jar" "commons-logging-1.0.4.jar" "concurrent.jar" "cvsclient.jar" "EccpressoAll.jar" "ehcache-2.10.1.jar" "flexlm.jar" "graphics/clibwrapper_jiio.jar" "graphics/freehep-base.jar" "graphics/freehep-graphics2d.jar" "graphics/freehep-graphicsio-emf.jar" "graphics/freehep-graphicsio-ps.jar" "graphics/freehep-graphicsio.jar" "graphics/jai_imageio.jar" "gson-2.2.4.jar" "HTMLEditorLight.jar" "javassist.jar" "javax_jmi-1_0-fr.jar" "jaxb-impl.jar" "jaxb-libs.jar" "jboss-aop-client.jar" "jboss-aspect-jdk50-client.jar" "jboss-common-core.jar" "jboss-ejb3-common-client.jar" "jboss-ejb3-core-client.jar" "jboss-ejb3-ext-api.jar" "jboss-ejb3-proxy-client.jar" "jboss-ejb3-security-client.jar" "jboss-integration.jar" "jboss-javaee.jar" "jboss-logging-spi.jar" "jboss-mdr.jar" "jboss-messaging-client.jar" "jboss-remoting.jar" "jboss-security-spi.jar" "jboss-serialization.jar" "jbossall-client.jar" "jbosscx-client.jar" "jbosssx-as-client.jar" "jbosssx-client.jar" "jbosssx.jar" "jhall.jar" "jide-action.jar" "jide-charts.jar" "jide-common.jar" "jide-components.jar" "jide-dialogs.jar" "jide-dock.jar" "jide-editor.jar" "jide-gantt.jar" "jide-grids.jar" "jide-properties.jar" "jide-shortcut.jar" "jimi.jar" "jmyspell-core-1.0.0-beta-2.jar" "jna.jar" "jnp-client.jar" "junit.jar" "log4j-1.2.15.jar" "lpg.runtime.java_2.0.17.v201004271640.jar" "namespace.jar" "org.apache.commons.codec_1.3.0.v201101211617.jar" "org.apache.commons.collections_3.2.0.v201005080500.jar" "org.apache.commons.logging_1.1.1.v201101211721.jar" "org.eclipse.core.contenttype_3.4.100.v20110423-0524.jar" "org.eclipse.core.jobs_3.5.100.v20110404.jar" "org.eclipse.core.runtime_3.7.0.v20110110.jar" "org.eclipse.emf.common_2.7.0.v20110513-1719.jar" "org.eclipse.emf.ecore.change_2.7.0.v20110408-2116.jar" "org.eclipse.emf.ecore.xmi_2.7.0.v20110411-2239.jar" "org.eclipse.emf.ecore_2.7.0.v20110513-1719.jar" "org.eclipse.emf.transaction_1.4.0.v20100331-1738.jar" "org.eclipse.equinox.common_3.6.0.v20110506.jar" "org.eclipse.equinox.registry_3.5.100.v20110502.jar" "org.eclipse.ocl.doc_3.0.1.R30x_v201008311245.jar" "org.eclipse.ocl.ecore.edit.source_3.0.0.R30x_v201008251030.jar" "org.eclipse.ocl.ecore.edit_3.0.0.R30x_v201008251030.jar" "org.eclipse.ocl.ecore.source_3.0.1.R30x_v201008251030.jar" "org.eclipse.ocl.ecore_3.0.1.R30x_v201008251030.jar" "org.eclipse.ocl.source_3.0.1.R30x_v201008251030.jar" "org.eclipse.ocl_3.0.1.R30x_v201008251030.jar" "org.eclipse.osgi_3.7.0.v20110513.jar" "org.eclipse.uml2.common_1.5.0.v200905041045.jar" "relaxngDatatype.jar" "trove.jar" "truezip-driver-zip-7.4.3.jar" "truezip-kernel-7.4.3.jar" "uml2.jar" "webservice/axis-config.jar" "webservice/axis.jar" "webservice/commons-discovery-0.2.jar" "webservice/jaxrpc.jar" "webservice/mdserviceclient.jar" "webservice/rsclient.jar" "webservice/saaj.jar" "webservice/wsdl4j-1.5.1.jar" "xalan.jar" "xsdlib.jar" "y.jar")
# WRITES OUTPUT TO DESIRED FILE.
for i in "${list[@]}" ; do
  # IT'S VERY IMPORTANT TO KEEP TWO SPACES AFTER FOLLOWING echo COMMAND, OTHERWISE JAR COMMAND WON'T UNDERSTAND THE SINTAX
  printf "  ${MD_HOME}lib/$i\n" >> manifest.txt
done
printf "\n" >> manifest.txt
command -v jar >/dev/null 2>&1 || { echo >&2 "jar command is not found, be sure you got JDK installed and JAVA_HOME/bin is part of your PATH variable."; exit 1; }
jar -umf manifest.txt ${project.build.finalName}-jar-with-dependencies.jar
rm manifest.txt
# REMOVES THE FIRST ARGUMENT (MD HOME PATH).
shift
java -Xms1024m -Xmx2048m -jar ${project.build.finalName}-jar-with-dependencies.jar $@