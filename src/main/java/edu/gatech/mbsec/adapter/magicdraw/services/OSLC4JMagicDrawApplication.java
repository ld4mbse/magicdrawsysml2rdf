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


import cli.ModelDescriptor;
import com.hp.hpl.jena.rdf.model.Model;
import com.nomagic.runtime.ApplicationExitedException;
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

	public static Model run(String file, ModelDescriptor descriptor) throws Exception {
        MagicDrawManager.descriptor = descriptor;
		MagicDrawManager.loadSysMLProjects(file);
        return MagicDrawManager.getModel();
	}

    public static void finish() throws ApplicationExitedException {
        MagicDrawManager.magicdrawApplication.shutdown();
    }

}