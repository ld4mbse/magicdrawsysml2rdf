<h1>A (MagicDraw) SysML to RDF converter.</h1><p>This project depends on the
<a target="_blank" href="https://www.nomagic.com/products/magicdraw">MagicDraw</a>
libraries to convert <code>.mdzip</code> files to RDF representations (tested
with version <strong>18.0.SP6</strong>).</p><ol>
<li><h2>Prerequisites.</h2><ol><li>
<h3>Software.</h3>
To get an executable component of this project you will need to have the
following software installed:<br/><table width="40%">
<tr><th>Software</th><th>Version</th></tr>
<tr><td><a href="https://git-scm.com/download/win" target="_blank">git</a></td><td>2.14.1</td></tr>
<tr><td><a href="http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html" target="_blank">Java JDK</a></td><td>1.7</td></tr>
<tr><td><a href="http://maven.apache.org/download.cgi" target="_blank">Apache Maven</a></td><td>3.3.1</td></tr>
</table></li><li>
<h3>Configuration.</h3>
Be sure to have below environment variables properly set in your system.<br/><table width="50%" style="border:solid">
<tr><th>Variable</th><th>Value</th></tr>
<tr><td><code>JAVA_HOME</code></td><td>Java JDK installation directory.</td></tr>
<tr><td><code>M2_HOME</code></td><td>Maven installation directory.</td></tr>
</table></li></ol></li>
<li><h2>Setting up the environment.</h2><ol>
<li><h3>Installing MagicDraw.</h3>
As MagicDraw libraries are proprietary, and therefore they are not in any public
maven repository, it's up to the user of this project to get the product with a
valid or temporal license. If you already count with the version required by
this project please skip this step, otherwise buy or download the trial version
from their <a target="_blank" href="https://www.nomagic.com/products/magicdraw">site</a>
and proceed with the default installation.</li>
<li><h3>Installing MagicDraw's SysML plugin.</h3>
Open the MagicDraw software and add the <strong>SysML</strong> plug-in following
the instructions <a target="_blank" href="https://www.nomagic.com/support/installation-and-use/plugins-and-profiles-install/sysml-plugin">here</a>.</li>

<li><h3>Installing <strong><code>oslc4j-magicdraw-resources</code></strong> project.</h3>
Open the MagicDraw software and add the <strong>SysML</strong> plug-in following
the instructions <a target="_blank" href="https://www.nomagic.com/support/installation-and-use/plugins-and-profiles-install/sysml-plugin">here</a>.</li>

</ol>

</li>

<li><h2>Executing the convertion tool.</h2>
</li></ol>
<!--
<p>In order to make this code compilable, a user needs:<ol>
<li>Clone the next repo for dependency projects <a target="_blank" href="https://github.com/ld4mbse/oslc4j">https://github.com/ld4mbse/oslc4j</a>.</li>
<li>Navigate inside the project <strong><code>org.eclipse.lyo.oslc4j.build</code></strong> and execute the following command (it may take awhile to complete):<br/><br /><strong><code>mvn clean install</code></strong><br/>&nbsp;</li>
<li>Clone the next repo for a dependecy project <a target="_blank" href="https://github.com/ld4mbse/oslc-adapter-magicdraw-sysml">https://github.com/ld4mbse/oslc-adapter-magicdraw-sysml</a>.</li>
<li>Install (<strong><code>mvn clean install</code></strong>) the contained project <strong><code>oslc4j-magicdraw-resources</code></strong></li>
</ol></p>
<h2>2. Getting and adjusting this project's code.</h2><ol>
<li>Clone this project's repo and navigate inside the <strong><code>magicdrawsysml2rdf</code></strong> directory.</li>
<li>Open the <strong><code>pom.xml</code></strong> and modify the property <strong><code>magicdraw.home</code></strong> to point to your <strong>MagicDraw home directory</strong> (where you installed it).</li>
</ol></p>
<h2>3. Getting an executable component.</h2>
<p>Staying at the directory where your <code>pom.xml</code> file is, execute a simple:
<br/><br /><strong><code>mvn clean package</code></strong>&nbsp;</p>
<p>This will create the <code>target</code> directory with following three important files among others:<strong><pre>
magicdrawsysml2rdf.sh
magicdrawsysml2rdf.bat
magicdrawsysml2rdf-1.0-jar-with-dependencies.jar</pre></strong>
These are the components that you can zip and distribute for its execution.
<p>Note: despite the name suffix "<code>jar-with-dependencies</code>" this component DOES NOT include the MagicDraw libraries inside it. Any user wanting to execute this component on her/his own
computer will need to specify the directory of her/his own copy of the MagicDraw software in order to work.</p>
<h2>4. Executing the convertion tool.</h2>
<h3>Windows Users.</h3>
<strong><code>magicdrawsysml2rdf.bat &lt;MagicDraw_Home_Directory&gt; -mdzip &lt;mdzip_file_to_convert&gt; -f &lt;output_file_name&gt; -t &lt;desired_format&gt;</code></strong>
<p>Note: the MagicDraw installation path needs to be always the first argument, and in case of it contains spaces use quotes to surround it.</p>
<h3>Unix(Linux) Users.</h3>
<strong><code>magicdrawsysml2rdf.sh &lt;MagicDraw_Home_Directory&gt; -mdzip &lt;mdzip_file_to_convert&gt; -f &lt;output_file_name&gt; -t &lt;desired_format&gt;</code></strong>
<p>Note: do not forget to give execution rights to the shell before run it.</p>
-->