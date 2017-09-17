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
package edu.gatech.mbsec.adapter.magicdraw.builder;


import com.hp.hpl.jena.rdf.model.Model;
import com.nomagic.runtime.ApplicationExitedException;
import static edu.gatech.mbsec.adapter.magicdraw.builder.MagicDrawManager.loadSysMLProject;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;


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
    /**
     * Logger of this class.
     */
    private static final Logger LOG = Logger.getLogger(OSLC4JMagicDrawApplication.class.getName());

	public static Model run(String file, ModelDescriptor descriptor) throws Exception {
        try {
            MagicDrawManager.descriptor = descriptor;
            //MagicDrawManager.loadSysMLProject(file);
            return MagicDrawManager.getModel();
        } catch(Exception ex) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            PrintStream printer = new PrintStream(bos);
            ex.printStackTrace(printer);
            printer.flush();
            LOG.log(Level.SEVERE, "Could not build model.\n{0}", bos.toString());
            throw ex;
        }
	}

    public static void finish() throws ApplicationExitedException {
        if (MagicDrawManager.magicdrawApplication != null) {
            MagicDrawManager.magicdrawApplication.shutdown();
        }
    }

}