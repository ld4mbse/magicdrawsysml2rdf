A (MagicDraw) SysML to RDF converter.
========================
This project depends on the [MagicDraw](https://www.nomagic.com/products/magicdraw)
libraries to convert `.mdzip` files to RDF representations (tested with version **18.0.SP6**).

# 1. Prerequisites

### 1.1 Software
To get an executable component of this project you will need to have the
following software installed:

| Software                                                                                        | Version |
| ------------------------------------------------------------------------------------------------|---------|
| [git](https://git-scm.com/download/win)                                                         | 2.14.1  |
| [Java JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) | 1.7     |
| [Apache Maven](http://maven.apache.org/download.cgi)                                            | 3.3.1   |

### 1.2 Configuration

Be sure to have below environment variables properly set in your system.

| Variable               | Value                             |
| -----------------------|-----------------------------------|
| `JAVA_HOME` | Java JDK installation directory.  |
| `M2_HOME`   | Maven installation directory.     |

2. Setting up the environment
-----------------------------
### 2.1 Installing MagicDraw.

As MagicDraw libraries are proprietary, and therefore they are not in any public
maven repository, it's up to the user of this project to get the product with a
valid or temporal license. If you already count with the version required by
this project please skip this step, otherwise buy or download the trial version
from their [site](https://www.nomagic.com/products/magicdraw)
and proceed with the default installation.

### 2.2 Installing MagicDraw's SysML plugin.
Open the MagicDraw software and add the **SysML** plug-in following
the instructions [here](https://www.nomagic.com/support/installation-and-use/plugins-and-profiles-install/sysml-plugin)

### 2.3 Installing the **`oslc4j`** project.
This project depends on an open-source project whose package is not yet on a
maven public repository. So you need to:

1. Open a console terminal.
2. Choose or create a directory to clone the project's code.
3. Change your location to the previous selected/created directory.
4. Execute `git clone https://github.com/ld4mbse/oslc4j.git`
5. Get inside the recently created `oslc4j` directory.
6. Once there, get inside the `org.eclipse.lyo.oslc4j.build` directory.
7. Being there, execute: `mvn install`

### 2.4 Installing the **`oslc4j-magicdraw-resources`** project.
Same as before, you will need to:

1. Open a console terminal.
2. Choose or create a directory to clone the project's code.
3. Change your location to the previous selected/created directory.
4. Execute `git clone https://github.com/ld4mbse/oslc-adapter-magicdraw-sysml.git`
5. Get inside the recently created `oslc-adapter-magicdraw-sysml` directory.
6. Once there, get inside the `edu.gatech.mbsec.adapter.magicdraw.resources` directory.
7. Being there, execute: `mvn install`

3. Getting the source code.
-----------------------------
Finally it's time to get this project's code.

1. On a console terminal, choose or create a directory to clone this project's code.
2. Change your location to the previous selected/created directory.
3. Execute `git clone https://github.com/ld4mbse/magicdrawsysml2rdf.git`

A new `magicdrawsysml2rdf` directory will be created in your
location. This will be the **project's home directory**.

4. Customizing and building your copy.
--------------------------------------
In order to compile this project's code, you will need to tell maven where you
got MagicDraw installed.

1. On a console terminal, change your location to this **project's home directory**.
2. Open the **`pom.xml`** file in some text editor.
3. Modify the **`magicdraw.home`** property value to point to **your MagicDraw installation path**.
4. Save changes and go back to the console.
5. Execute: `mvn package`

This will create a `target` directory with the following three important files among others:
```
magicdrawsysml2rdf.sh
magicdrawsysml2rdf.bat
magicdrawsysml2rdf-1.0-jar-with-dependencies.jar
```
These are the components that you can zip and distribute for execution.

Note: despite the name suffix "`jar-with-dependencies`" this component DOES NOT
include the MagicDraw libraries inside it. Any user wanting to execute this
component on her/his own computer will need to specify the directory of her/his
own copy of the MagicDraw software in order to work.

5. Executing the conversion tool.
--------------------------------------
The three files listed above need to be always on the same directory, and you
can execute the `.sh` file if you are in a unix/linux distribution,
or the `.bat` file if you are in a Windows distribution. The
arguments explained later are and work the same for both files. If some
argument's value contains spaces, you will need to enclose that value with
quotes. Unix/Linux users do not forget to grant execution permission to the file.

### 5.1 Requesting help
The simplest form of execution is to request the help screen:
```
magicdrawsysml2rdf.bat -help
```
or
```
./magicdrawsysml2rdf.sh -help
```
From now on, examples will consider only the Windows syntax.

The above command will print all the available arguments, output formats, and
known namespaces:

```
usage: magicdrawsysml2rdf(.sh|.bat) (<MagicDraw Path> | -help) <options>
 -base <URL>          base URL prefix (http://localhost:8080/ by default)
 -format <format>     output format (Turtle by default)
 -help                prints this file
 -mdzip <file>        mandatory mdzip input file
 -meta <arg>          meta information to be added
 -nsprefix <arg>      allows to define custom prefixes
 -rest <path>         rest services path part (rest/ by default)
 -target <file|url>   output target (console by default)
 -vocab <path>        vocabulary path part (vocab# by default)
<format> = [Turtle, N-Quads, RDF/XML, N3, RDF/JSON, N-Triples, TriG]

=============== KNOWN <PREFIXES:NAMESPACES> FOR META-DATA ===============

<onteventsvocab:http://jena.hpl.hp.com/schemas/2003/03/ont-event#>
<owl2:http://www.w3.org/2002/07/owl#>
<rdftest:http://www.w3.org/2000/10/rdf-tests/rdfcore/testSchema#>
<owlresults:http://www.w3.org/2002/03owlt/resultsOntology#>
<ontdocmanagervocab:http://jena.hpl.hp.com/schemas/2003/03/ont-manager#>
<testmanifest:http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#>
<xsd:http://www.w3.org/2001/XMLSchema#>
<dcterms:http://purl.org/dc/terms/>
<rdfsyntax:http://www.w3.org/TR/rdf-syntax-grammar#>
<rdfs:http://www.w3.org/2000/01/rdf-schema#>
<testmanifestx:http://jena.hpl.hp.com/2005/05/test-manifest-extra#>
<owl:http://www.w3.org/2002/07/owl#>
<dc_11:http://purl.org/dc/elements/1.1/>
<dctypes:http://purl.org/dc/dcmitype/>
<dc_10:http://purl.org/dc/elements/1.0/>
<locationmappingvocab:http://jena.hpl.hp.com/2004/08/location-mapping#>
<rss:http://purl.org/rss/1.0/>
<vcard:http://www.w3.org/2001/vcard-rdf/3.0#>
<owltest:http://www.w3.org/2002/03owlt/testOntology#>
<dc:http://purl.org/dc/elements/1.1/>
<rdf:http://www.w3.org/1999/02/22-rdf-syntax-ns#>
<db:http://jena.hpl.hp.com/2003/04/DB#>
```

### 5.2 Default conversion
When the `-help` argument is not present, there are two mandatory
arguments: the MagicDraw installation path and the input file to convert. To
specify the file to convert you use the `-mdzip` argument. To set up
the MagicDraw installation path you just type it down but **it must be
always the first argument**:
```
magicdrawsysml2rdf.bat "C:\Program Files\MagicDraw" -mdzip myModel.mdzip
```
Without any other argument, the default output format will be
[Turtle](https://www.w3.org/TR/turtle/) and the output will be redirected to
the standard console.

### 5.3 Changing the default format
To select a different output format from the available ones, use the
`-format` argument:
```
magicdrawsysml2rdf.bat "C:\Program Files\MagicDraw" -mdzip myModel.mdzip -format rdf/xml
```
The value for the `-format` argument is case insensitive.

### 5.4 Redirecting output to a different target
You can send the output to a file, or to a remote rdf store server, if you use
the `-target` argument.
```
magicdrawsysml2rdf.bat "C:\Program Files\MagicDraw" -mdzip myModel.mdzip -target myModel.ttl
magicdrawsysml2rdf.bat "C:\Program Files\MagicDraw" -mdzip myModel.mdzip -target http://example.com/rdfstore/rest/graph
```
When the remote rdf store server option is used, `magicdrawsysml2rdf` will:

* Make an http POST request on the given URL.
* Attach the conversion output as a chunked request body.
* Add the `Content-Type` header with the corresponding value of the `-format` argument value.
* Add the `Slug` header with an ID value (for the conversion output) that suggest the server the final URL of the sent data.
* Expect a response status code to render it to the user.
* In case of a success status code, expect the `Location` header with the final URL assigned to the sent data.</ul>

### 5.5 Adding meta-data to the conversion output
Sometimes you may want to add extra data to the converted output, data that may
provide information about the original information, that is meta-data. As
examples we can mention the date of the conversion, or maybe a revision number.
For this cases you can use the `-meta` argument:
```
magicdrawsysml2rdf.bat "C:\Program Files\MagicDraw" -mdzip myModel.mdzip -format rdf/xml -meta dcterms:date=2017-09-14 dcterms:hasVersion=1.0
```
Notice that:

* Each meta-property is separated by others by an space.
* Each meta-property value is separated from the name with the equal sign.
* Each meta-property needs to have a namespace prefix.

Last rule is needed because this meta-properties will shape a
**meta-resource** within the converted data and, as any other
resource, its properties are URLs. Following is the example output
corresponding to the meta-resource generated for this case:
```
<rdf:Description rdf:about="http://localhost:8080/rest/model/80f4b1f9fb595eafb6ba3bea900830a2">
  <dcterms:date rdf:datatype="http://www.w3.org/2001/XMLSchema#string">2017-09-14</dcterms:date>
  <dcterms:hasVersion rdf:datatype="http://www.w3.org/2001/XMLSchema#string">1.0</dcterms:hasVersion>
  <rdf:type rdf:resource="http://localhost:8080/vocab#model"/>
</rdf:Description>
```

### 5.6 Identifying the meta-resource
By default, a hash number is generated to identify the meta-resource:
```
<rdf:Description rdf:about="http://localhost:8080/rest/model/80f4b1f9fb595eafb6ba3bea900830a2">
```
You can, however, customize this id by using a special property in the
`-meta` argument:
```
magicdrawsysml2rdf.bat "C:\Program Files\MagicDraw" -mdzip myModel.mdzip -format rdf/xml -meta dcterms:date=2017-09-14 dcterms:hasVersion=1.0 graph=myData
```
This property is special because it is the only one, along with the `type`
property, that do not require a namespace prefix. With the last modification,
the meta-resource will look like:
```
<rdf:Description rdf:about="http://localhost:8080/rest/model/myData">
  <dcterms:date rdf:datatype="http://www.w3.org/2001/XMLSchema#string">2017-09-14</dcterms:date>
  <dcterms:hasVersion rdf:datatype="http://www.w3.org/2001/XMLSchema#string">1.0</dcterms:hasVersion>
  <rdf:type rdf:resource="http://localhost:8080/vocab#model"/>
</rdf:Description>
```
The `graph` property is especially useful when you use a
[remote RDF store server target](#target) because the ID of the meta-resource
is the one that is always used to set the `Slug` header value. In other words,
with this property you can control the final URL that your data will have in a
remote location.

### 5.7 Control the meta-resource type
By default, the meta-resource will have an `rdf:type` equals to
`http://localhost:8080/vocab#model` which is the *default* base URL
(`http://localhost:8080/`) plus the *default* vocabulary URL path part
(`vocab#`) plus the default meta-resource type string: `model`.
If you want to change this default meta-resource type string, you can use the
other special `-meta` argument property, `type`:
```
magicdrawsysml2rdf.bat "C:\Program Files\MagicDraw" -mdzip myModel.mdzip -format rdf/xml -meta dcterms:date=2017-09-14 dcterms:hasVersion=1.0 graph=myData type=myType
```
to produce:
```
<rdf:Description rdf:about="http://localhost:8080/rest/myType/myData">
  <dcterms:date rdf:datatype="http://www.w3.org/2001/XMLSchema#string">2017-09-14</dcterms:date>
  <dcterms:hasVersion rdf:datatype="http://www.w3.org/2001/XMLSchema#string">1.0</dcterms:hasVersion>
  <rdf:type rdf:resource="http://localhost:8080/vocab#myType"/>
</rdf:Description>
```

### 5.8 Adding custom namespaces/prefixes
`magicdrawsysml2rdf` [knows several namespaces/prefixes](#markdown-header-4.1-requesting-help) you can use to
define meta-properties. When you are defining meta-properties, you just need to
use some of these prefixes and `magicdrawsysml2rdf` will resolve the
corresponding namespace(s). It may be the case, however, that you need to set-up
a meta-property whose namespace is not known by `magicdrawsysml2rdf`.
To add a namespace/prefix definition, you can use the `-nsprefix`
argument:
```
magicdrawsysml2rdf.bat "C:\Program Files\MagicDraw" -mdzip myModel.mdzip -format rdf/xml -meta myPrefix:myProp=myValue -nsprefix myPrefix=http://example.com/
```
The meta-property `myProp` now will be accepted because its
prefix can be resolved to the `http://example.com/` namespace.

### 5.9 Customizing the base URL
The default base URL (`http://localhost:8080/`) can also be customized by using
the `-base` argument:
```
magicdrawsysml2rdf.bat "C:\Program Files\MagicDraw" -mdzip myModel.mdzip -format rdf/xml -meta dcterms:date=2017-09-14 dcterms:hasVersion=1.0 graph=myData type=myType -base http://example.com/
```
to produce:
```
<rdf:Description rdf:about="http://example.com/rest/myType/myData">
  <dcterms:date rdf:datatype="http://www.w3.org/2001/XMLSchema#string">2017-09-14</dcterms:date>
  <dcterms:hasVersion rdf:datatype="http://www.w3.org/2001/XMLSchema#string">1.0</dcterms:hasVersion>
  <rdf:type rdf:resource="http://example.com/vocab#myType"/>
</rdf:Description>
```
Please notice that:

* This change will apply to all resources, not only the meta-resource.
* The `-base` argument is not considered when using a remote RDF store location as target.
In this cases, the same URL base of the remote location is the one that is used.

### 5.10 Customizing the service URL path part.
From the meta-resource URL:
```
<rdf:Description rdf:about="http://example.com/rest/myType/myData">
```
you may notice we have configured everything except the `rest/` path part. You
can configure this path part using the `-rest` argument:
```
magicdrawsysml2rdf.bat "C:\Program Files\MagicDraw" -mdzip myModel.mdzip -format rdf/xml -meta dcterms:date=2017-09-14 dcterms:hasVersion=1.0 graph=myData type=myType -base http://example.com/ -rest services
```
which will produce:
```
<rdf:Description rdf:about="http://example.com/services/myType/myData">
  <dcterms:date rdf:datatype="http://www.w3.org/2001/XMLSchema#string">2017-09-14</dcterms:date>
  <dcterms:hasVersion rdf:datatype="http://www.w3.org/2001/XMLSchema#string">1.0</dcterms:hasVersion>
  <rdf:type rdf:resource="http://example.com/vocab#myType"/>
</rdf:Description>
```
Notice that this change applies to all resources, not to only the meta-resource.

### 5.11 Customizing the vocabulary URL path part.
Vocabulary URLs are constituted with the base URL plus the `vocab#` path part
and the specific type or property you want to refer. You can configure the
`vocab#` path part by using `-vocab` argument:
```
magicdrawsysml2rdf.bat "C:\Program Files\MagicDraw" -mdzip myModel.mdzip -format rdf/xml -meta dcterms:date=2017-09-14 dcterms:hasVersion=1.0 graph=myData type=myType -base http://example.com/ -rest services -vocab myVocab
```
This will produce:
```
<rdf:Description rdf:about="http://example.com/services/myType/myData">
  <dcterms:date rdf:datatype="http://www.w3.org/2001/XMLSchema#string">2017-09-14</dcterms:date>
  <dcterms:hasVersion rdf:datatype="http://www.w3.org/2001/XMLSchema#string">1.0</dcterms:hasVersion>
  <rdf:type rdf:resource="http://example.com/myVocab#myType"/>
</rdf:Description>
```
Notice that this change applies to all resources, not to only the meta-resource.
