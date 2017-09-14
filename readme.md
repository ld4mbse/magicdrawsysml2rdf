<h1>A (MagicDraw) SysML to RDF converter.</h1><p>This project depends on the
<a target="_blank" href="https://www.nomagic.com/products/magicdraw">MagicDraw</a>
libraries to convert <code>.mdzip</code> files to RDF representations (tested
with version <strong>18.0.SP6</strong>).</p><ol>
<li><h2>Prerequisites.</h2><ol><li>
<h3>Software.</h3>
To get an executable component of this project you will need to have the
following software installed:<br/>&nbsp;<table width="40%">
<tr><th>Software</th><th>Version</th></tr>
<tr><td><a href="https://git-scm.com/download/win" target="_blank">git</a></td><td>2.14.1</td></tr>
<tr><td><a href="http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html" target="_blank">Java JDK</a></td><td>1.7</td></tr>
<tr><td><a href="http://maven.apache.org/download.cgi" target="_blank">Apache Maven</a></td><td>3.3.1</td></tr>
</table></li><li>
<h3>Configuration.</h3>
Be sure to have below environment variables properly set in your system.<br/>&nbsp;
<table width="50%" style="border:solid"><tr><th>Variable</th><th>Value</th></tr>
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
This project depends on an open-source project which package is not yet on a
maven public repository. So you need to:<br/><ol>
<li>Open a console terminal.</li>
<li>Choose or create a directory to clone the project's code.</li>
<li>Change your location to the previous selected/created directory.</li>
<li>Execute <code>git clone https://github.com/ld4mbse/oslc-adapter-magicdraw-sysml</code></li>
<li>Get inside the recently created <code>oslc-adapter-magicdraw-sysml</code> directory.</li>
<li>Once there, get inside the <code>edu.gatech.mbsec.adapter.magicdraw.resources</code> directory.</li>
<li>Being there, execute: <code>mvn install</code></li>
</ol></li></ol></li>
<li><h2>Getting the source code.</h2>
Finally it's time to get this project's code.<br/><ol>
<li>On a console terminal, choose or create a directory to clone this project's code.</li>
<li>Change your location to the previous selected/created directory.</li>
<li>Execute <code>git clone https://github.com/ld4mbse/magicdrawsysml2rdf.git</code></li></ol>
<p>A new <code>magicdrawsysml2rdf</code> directory will be created in your
location. This will be the <strong>project's home directory</strong>.</p></li>
<li><h2>Customizing and building your copy.</h2>
In order to compile this project's code, you will need to tell maven where you got
MagicDraw installed.<br/><ol>
<li>On a console terminal, change your location to this <strong>project's home directory</strong>.</li>
<li>Open the <strong><code>pom.xml</code></strong> file in some text editor.</li>
<li>Modify the <strong><code>magicdraw.home</code></strong> property value to point to <strong>your MagicDraw installation path</strong>.</li>
<li>Save changes and go back to the console.</li>
<li>Execute: <code>mvn package</code></li></ol>
<p>This will create a <code>target</code> directory with the following three important files among others:<strong><pre>
magicdrawsysml2rdf.sh
magicdrawsysml2rdf.bat
magicdrawsysml2rdf-1.0-jar-with-dependencies.jar</pre></strong>
These are the components that you can zip and distribute for execution.
<p>Note: despite the name suffix "<code>jar-with-dependencies</code>" this component DOES NOT include the MagicDraw libraries inside it. Any user wanting to execute this component on her/his own
computer will need to specify the directory of her/his own copy of the MagicDraw software in order to work.</p></li>
<li><h2>Executing the conversion tool.</h2>
The three files listed above need to be always on the same directory, and you
can execute the <code>.sh</code> file if you are in a unix/linux distribution,
or the <code>.bat</code> file if you are in a Windows distribution. The
arguments explained later are and work the same for both files. If some
argument's value contains spaces, you will need to enclose that value with
quotes. Unix/Linux users do not forget to grant execution permission to the file.
<ol><li><h3><a name="help">Requesting help</a></h3>
The simplest form of execution is to request the help screen:<br/><br/>
<code>magicdrawsysml2rdf.bat -help</code><br/>or<br/>
<code>./magicdrawsysml2rdf.sh -help</code><br/><br/>From now on, examples will
consider only the Windows syntax.<p>The above command will print all the
available arguments, output formats, and known namespaces:<pre>
usage: magicdrawsysml2rdf(.sh|.bat) (&lt;MagicDraw Path&gt; | -help) &lt;options&gt;
 -format &lt;format&gt;     output format (Turtle by default)
 -help                prints this file
 -mdzip &lt;file&gt;        mandatory mdzip input file
 -meta &lt;arg&gt;          meta information to be added
 -nsprefix &lt;arg&gt;      allows to define custom prefixes
 -target &lt;file|url&gt;   output target (console by default)
&lt;format&gt; = [Turtle, N-Quads, RDF/XML, N3, RDF/JSON, N-Triples, TriG]

=============== <a name="knownPrefixes">KNOWN &lt;PREFIXES:NAMESPACES#&gt; FOR META-DATA</a> ===============

&lt;onteventsvocab:http://jena.hpl.hp.com/schemas/2003/03/ont-event#&gt;
&lt;owl2:http://www.w3.org/2002/07/owl#&gt;
&lt;rdftest:http://www.w3.org/2000/10/rdf-tests/rdfcore/testSchema#&gt;
&lt;owlresults:http://www.w3.org/2002/03owlt/resultsOntology#&gt;
&lt;ontdocmanagervocab:http://jena.hpl.hp.com/schemas/2003/03/ont-manager#&gt;
&lt;testmanifest:http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#&gt;
&lt;xsd:http://www.w3.org/2001/XMLSchema#&gt;
&lt;dcterms:http://purl.org/dc/terms/&gt;
&lt;rdfsyntax:http://www.w3.org/TR/rdf-syntax-grammar#&gt;
&lt;rdfs:http://www.w3.org/2000/01/rdf-schema#&gt;
&lt;testmanifestx:http://jena.hpl.hp.com/2005/05/test-manifest-extra#&gt;
&lt;owl:http://www.w3.org/2002/07/owl#&gt;
&lt;dctypes:http://purl.org/dc/dcmitype/&gt;
&lt;dc_11:http://purl.org/dc/elements/1.1/&gt;
&lt;dc_10:http://purl.org/dc/elements/1.0/&gt;
&lt;locationmappingvocab:http://jena.hpl.hp.com/2004/08/location-mapping#&gt;
&lt;rss:http://purl.org/rss/1.0/&gt;
&lt;vcard:http://www.w3.org/2001/vcard-rdf/3.0#&gt;
&lt;rdf:http://www.w3.org/1999/02/22-rdf-syntax-ns#&gt;
&lt;owltest:http://www.w3.org/2002/03owlt/testOntology#&gt;
&lt;dc:http://purl.org/dc/elements/1.1/&gt;
&lt;db:http://jena.hpl.hp.com/2003/04/DB#&gt;
</pre></p></li>
<li><h3>Default conversion</h3>
When the <code>-help</code> argument is not present, there are two mandatory
arguments: the MagicDraw installation path and the input file to convert. To
specify the file to convert you use the <code>-mdzip</code> argument. To set up
the MagicDraw installation path you just type it down but <strong>it must be
always the first argument</strong>:<pre>
magicdrawsysml2rdf.bat "C:\Program Files\MagicDraw" -mdzip myModel.mdzip
</pre>Without any other argument, the default output format will be
<a href="https://www.w3.org/TR/turtle/" target="_blank">Turtle</a> and the
output will be redirected to the standard console.</li>
<li><h3>Changing the default format</h3>
To select a different output format from the available ones, use the
<code>-format</code> argument:<pre>
magicdrawsysml2rdf.bat "C:\Program Files\MagicDraw" -mdzip myModel.mdzip -format rdf/xml
</pre>The value for the <code>-format</code> argument is case insensitive.</li>
<li><h3><a name="target">Redirecting output to a different target</a></h3>
You can send the output to a file, or to a remote rdf store server, if you use
the <code>-target</code> argument.<pre>
magicdrawsysml2rdf.bat "C:\Program Files\MagicDraw" -mdzip myModel.mdzip -target myModel.ttl
magicdrawsysml2rdf.bat "C:\Program Files\MagicDraw" -mdzip myModel.mdzip -target http://example.com/rdfstore/rest/graph
</pre>When the remote rdf store server option is used,
<code>magicdrawsysml2rdf</code> will:<br/><ul>
<li>Make an http POST request on the given URL.</li>
<li>Attach the conversion output as a chunked request body.</li>
<li>Add the <code>Content-Type</code> header with the corresponding value of the <code>-format</code> argument value.</li>
<li>Add the <code>Slug</code> header with an ID value (for the conversion output) that suggest the server the final URL of the sent data.</li>
<li>Expect a response status code to render it to the user.</li>
<li>In case of a success status code, expect the <code>Location</code> header with the final URL assigned to the sent data.</li></ul></li>
<li><h3>Adding meta-data to the conversion output</h3>
Sometimes you may want to add extra data to the converted output, data that may
be providing information about the original information, that is meta-data.
Examples can be the date of the conversion, or maybe a revision number. For this
cases you can use the <code>-meta</code> argument:<pre>
magicdrawsysml2rdf.bat "C:\Program Files\MagicDraw" -mdzip myModel.mdzip -meta dcterms:date=2017-09-14 dcterms:hasVersion=1.0
</pre>Notice that:<br/><ol>
<li>Each meta-property is separated by others by an space.</li>
<li>Each meta-property value is separated from the name with the equal sign.</li>
<li>Each meta-property needs to have a namespace prefix.</li></ol><p>
Last rule is needed because this meta-properties will shape a
<strong>meta-resource</strong> within the converted data and, as any other
resource, its properties are URIs. Next is an example, in <code>rdf/xml</code>
format, of the meta-resource generated for this case:<pre>
&lt;rdf:Description rdf:about="http://localhost:8080/rest/model/80f4b1f9fb595eafb6ba3bea900830a2"&gt;
  &lt;dcterms:date rdf:datatype="http://www.w3.org/2001/XMLSchema#string"&gt;2017-09-14&lt;/dcterms:date&gt;
  &lt;dcterms:hasVersion rdf:datatype="http://www.w3.org/2001/XMLSchema#string"&gt;1.0&lt;/dcterms:hasVersion&gt;
&lt;/rdf:Description&gt;</pre></p></li>
<li><h3>Identifying the meta-resource</h3>
By default, a hash number is generated to identify the meta-resource:<pre>
&lt;rdf:Description rdf:about="http://localhost:8080/rest/model/<strong>80f4b1f9fb595eafb6ba3bea900830a2</strong>"&gt;</pre>
You can, however, customize this id by using a special property in the
<code>-meta</code> argument:<pre>
magicdrawsysml2rdf.bat "C:\Program Files\MagicDraw" -mdzip myModel.mdzip -meta dcterms:date=2017-09-14 dcterms:hasVersion=1.0 graph=myData
</pre><p>This property is special because is the only one that does not require
to have a namespace prefix. With the last modification, the meta-resource will look like:<pre>
&lt;rdf:Description rdf:about="http://localhost:8080/rest/model/<strong>myData</strong>"&gt;
  &lt;dcterms:date rdf:datatype="http://www.w3.org/2001/XMLSchema#string"&gt;2017-09-14&lt;/dcterms:date&gt;
  &lt;dcterms:hasVersion rdf:datatype="http://www.w3.org/2001/XMLSchema#string"&gt;1.0&lt;/dcterms:hasVersion&gt;
&lt;/rdf:Description&gt;</pre></p><p>The <code>graph</code> property is
especially useful when you use a <a href="#target">remote rdf store server target</a>
because the ID of the meta-resource is the one that is always used to set the
<code>Slug</code> header value. In other words, with this property you can control
the final URI that your data will have in a remote location.</p></li>
<li><h3>Adding custom namespaces/prefixes</h3>
<code>magicdrawsysml2rdf</code>
<a href="#knownPrefixes">knows several namespaces/prefixes</a> you can use to
define meta-properties. When you are defining meta-properties, you just need to
use some of these prefixes and <code>magicdrawsysml2rdf</code> will resolve the
corresponding namespace(s). It may be the case, however, that you need to set-up
a meta-property whose namespace is not known by <code>magicdrawsysml2rdf</code>.
To add a namespace/prefix definition, you can use the <code>-nsprefix</code>
argument:<pre>
magicdrawsysml2rdf.bat "C:\Program Files\MagicDraw" -mdzip myModel.mdzip -meta myPrefix:myProp=myValue -nsPrefix myPrefix=http://example.com/
</pre><p>The meta-property <code>myProp</code> now will be accepted because its
prefix can be resolved to the <code>http://example.com/</code> namespace.</p>
</li></ol></li></ol>