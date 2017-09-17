/*********************************************************************************************
 * Copyright (c) 2014 Model-Based Systems Engineering Center, Georgia Institute of Technology.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 *  
 *  The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *  and the Eclipse Distribution License is available at
 *  http://www.eclipse.org/org/documents/edl-v10.php.
 *  
 *  Contributors:
 *  
 *	   Sebastian Herzig (sebastian.herzig@gatech.edu)		- initial implementation 
 *	   Axel Reichwein (axel.reichwein@koneksys.com)         - adapted for MagicDraw adapter
 *******************************************************************************************/
package edu.gatech.mbsec.adapter.magicdraw.builder;

import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

/**
 * MDSysMLModelHandler is the base class for handling SysML-compliant modeling
 * elements. Part of the functionality includes identifying model elements as
 * elements complying to SysML, other functions include the creation of SysML
 * modeling elements. Note that this class heavily depends on the MagicDraw
 * SysML profile.
 * 
 * TODO: It may be worth removing the pre-checks from the functions and simply
 * performing a pre-check on construction of the class. Think about this
 * 
 * @author Sebastian Herzig
 * @author Axel Reichwein (axel.reichwein@koneksys.com)
 * @version 0.2
 */
public class MDSysMLModelHandler {

	public static boolean isSysMLElement(Element element, String sysMLStereotypeName) {
		return StereotypesHelper.hasStereotype(element,	sysMLStereotypeName);
	}

}