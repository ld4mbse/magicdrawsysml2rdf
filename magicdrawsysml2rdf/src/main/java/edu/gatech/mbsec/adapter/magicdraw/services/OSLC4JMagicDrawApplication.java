/*******************************************************************************
 * Copyright (c) 2012, 2014 IBM Corporation.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 *  
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *
 *     Michael Fiedler     - initial API and implementation for Bugzilla adapter
 *     
 * Modifications performed by:    
 *     Axel Reichwein		- implementation for MagicDraw adapter
 *     (axel.reichwein@koneksys.com)
 *     Sebastian Herzig (sebastian.herzig@me.gatech.edu) - support for publishing OSLC resource shapes     
 *******************************************************************************/
package edu.gatech.mbsec.adapter.magicdraw.services;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import com.nomagic.runtime.ApplicationExitedException;

import cli.MagicDraw2RDF;
import edu.gatech.mbsec.adapter.magicdraw.application.MagicDrawManager;


//import com.nomagic.magicdraw.commandline.CommandLine;

/**
 * OSLC4JMagicDrawApplication registers all entity providers for converting
 * POJOs into RDF/XML, JSON and other formats. OSLC4JMagicDrawApplication
 * registers also registers each servlet class containing the implementation of
 * OSLC RESTful web services.
 * 
 * OSLC4JMagicDrawApplication also reads the user-defined configuration file
 * with loadPropertiesFile(). This is done at the initialization of the web
 * application, for example when the first resource or service of the OSLC
 * MagicDraw adapter is requested.
 * 
 * @author Axel Reichwein (axel.reichwein@koneksys.com)
 * @author Sebastian Herzig (sebastian.herzig@me.gatech.edu)
 */
public class OSLC4JMagicDrawApplication {

	public static String warConfigFilePath = "../oslc4jmagicdraw configuration/config.properties";
	public static String localConfigFilePath = "oslc4jmagicdraw configuration/config.properties";
	public static String configFilePath = null;

	
	public static String magicDrawModelPaths = null;
	public static String portNumber = null;

	private static void closeMagicDrawApplication() {
		try {
			MagicDrawManager.magicdrawApplication.shutdown();
		} catch (ApplicationExitedException e) {
			e.printStackTrace();
		};
	}

	private static void loadPropertiesFile() {
		Properties prop = new Properties();
		InputStream input = null;

		
			try {
				input = new FileInputStream(localConfigFilePath);
				configFilePath = localConfigFilePath;
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} // for war file
		

		// load property file content and convert backslashes into forward
		// slashes
		String str;
		if (input != null) {
			try {
				str = readFile(configFilePath, Charset.defaultCharset());
				prop.load(new StringReader(str.replace("\\", "/")));

				// get the property value
				
				String magicdrawModelsDirectoryFromUser = prop.getProperty("magicDrawModelPaths");
				

				
				magicDrawModelPaths = magicdrawModelsDirectoryFromUser;
				
				portNumber = prop.getProperty("portNumber");
				

				

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {

				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}

	}

	private static void loadPropertiesFile2() {
		Properties prop = new Properties();
		InputStream input = null;

		
			try {
				input = new FileInputStream(localConfigFilePath);
				configFilePath = localConfigFilePath;
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} // for war file
		

		// load property file content and convert backslashes into forward
		// slashes
		String str;
		if (input != null) {
			try {
				str = readFile(configFilePath, Charset.defaultCharset());
				prop.load(new StringReader(str.replace("\\", "/")));

				// get the property value
				
//				String magicdrawModelsDirectoryFromUser = prop.getProperty("magicDrawModelPaths");
//				magicDrawModelPaths = magicdrawModelsDirectoryFromUser;
				
				magicDrawModelPaths = MagicDraw2RDF.magicdrawFileLocations;
				
				portNumber = prop.getProperty("portNumber");
				

				

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {

				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}

	}
	
	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return encoding.decode(ByteBuffer.wrap(encoded)).toString();
	}

	protected static void reloadModels() {
		MagicDrawManager.areSysMLProjectsLoaded = false; // to reload MagicDraw	models
		MagicDrawManager.loadSysMLProjects();	
	}
	
	public static void run() {
		magicDrawModelPaths = MagicDraw2RDF.magicdrawFileLocations;
		if(MagicDraw2RDF.host == null){
			MagicDraw2RDF.host = "localhost";
		}
		if(MagicDraw2RDF.port == null){
			MagicDraw2RDF.port = "8080";
		}
		MagicDrawManager.baseHTTPURI = "http://" + MagicDraw2RDF.host + ":" + MagicDraw2RDF.port + "/oslc4jmagicdraw";
        reloadModels();
        System.out.println("MagicDraw files read. Initialization of OSLC MagicDraw adapter finished.");
		MagicDrawManager.writeRDF();
		closeMagicDrawApplication();
	}
}