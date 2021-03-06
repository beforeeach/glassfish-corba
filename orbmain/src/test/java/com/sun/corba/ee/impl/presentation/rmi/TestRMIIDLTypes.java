/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.corba.ee.impl.presentation.rmi;

import corba.dynamicrmiiiop.testclasses.InvalidEntities;
import corba.dynamicrmiiiop.testclasses.InvalidExceptions;
import corba.dynamicrmiiiop.testclasses.InvalidObjRefs;
import corba.dynamicrmiiiop.testclasses.InvalidValues;
import corba.dynamicrmiiiop.testclasses.ValidEntities;
import corba.dynamicrmiiiop.testclasses.ValidExceptions;
import corba.dynamicrmiiiop.testclasses.ValidObjRefs;
import corba.dynamicrmiiiop.testclasses.ValidRemotes;
import corba.dynamicrmiiiop.testclasses.ValidValues;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestRMIIDLTypes extends TestCase {

    private IDLTypesUtil idlTypesUtil;

    public static Test suite() 
    {
        return new TestSuite(TestRMIIDLTypes.class);
    }

    protected void setUp()
    {
        idlTypesUtil = new IDLTypesUtil();
    }

    protected void tearDown()
    {
    }

    public void testPrimitiveTypes() 
    {
        Class[] primitives = {
            Void.TYPE, Boolean.TYPE, Byte.TYPE, Character.TYPE,
            Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE
        };

        for(int i = 0; i < primitives.length; i++) {
            Class primitive = primitives[i];
            String msg = primitive.getName();
            assertTrue(msg, idlTypesUtil.isPrimitive(primitive));
            assertFalse(msg,idlTypesUtil.isRemoteInterface(primitive));
            assertFalse(msg,idlTypesUtil.isValue(primitive));
            assertFalse(msg,idlTypesUtil.isArray(primitive));
            assertFalse(msg,idlTypesUtil.isException(primitive));
            assertFalse(msg,idlTypesUtil.isObjectReference(primitive));
            assertFalse(msg,idlTypesUtil.isEntity(primitive));
        }

        Class[] nonPrimitives = {
            Byte.class, Short.class, Integer.class, Long.class, Float.class, 
            Double.class, String.class, java.util.Date.class, Object.class
        };

        for(int i = 0; i < nonPrimitives.length; i++) {
            Class nonPrimitive = nonPrimitives[i];
            String msg = nonPrimitive.getName();
            assertFalse(msg, idlTypesUtil.isPrimitive(nonPrimitive));
        }
        
    }

    public void testRemoteInterfaceTypes() 
    {

        Class[] remoteInterfaces = ValidRemotes.CLASSES;

        for(int i = 0; i < remoteInterfaces.length; i++) {
            Class remoteIntf = remoteInterfaces[i];
            String msg = remoteIntf.getName();

            assertTrue(msg, idlTypesUtil.isRemoteInterface(remoteIntf));

            assertFalse(msg,idlTypesUtil.isPrimitive(remoteIntf));
            assertFalse(msg,idlTypesUtil.isValue(remoteIntf));
            assertFalse(msg,idlTypesUtil.isArray(remoteIntf));
            assertFalse(msg,idlTypesUtil.isException(remoteIntf));
            assertFalse(msg,idlTypesUtil.isObjectReference(remoteIntf));
            assertFalse(msg,idlTypesUtil.isEntity(remoteIntf));
        }

        // NOTE invalid remote interfaces are tested in TestIDLNameTranslator
    }
    
    public void testValueTypes() 
    {

        Class[] values = ValidValues.CLASSES;

        for(int i = 0; i < values.length; i++) {
            Class value = values[i];
            String msg = value.getName();

            assertTrue(msg,idlTypesUtil.isValue(value));

            assertFalse(msg, idlTypesUtil.isPrimitive(value));
            assertFalse(msg,idlTypesUtil.isRemoteInterface(value));
            assertFalse(msg,idlTypesUtil.isArray(value));
            assertFalse(msg,idlTypesUtil.isException(value));
            assertFalse(msg,idlTypesUtil.isObjectReference(value));
            assertFalse(msg,idlTypesUtil.isEntity(value));
        }

        Class[] nonValues = InvalidValues.CLASSES;

        for(int i = 0; i < nonValues.length; i++) {
            Class nonValue = nonValues[i];
            String msg = nonValue.getName();
            assertFalse(msg, idlTypesUtil.isValue(nonValue));
        }
        
    }

    public void testExceptionTypes()
    {

        Class[] exceptions = ValidExceptions.CLASSES;

        for(int i = 0; i < exceptions.length; i++) {
            Class excep = exceptions[i];
            String msg = excep.getName();

            assertTrue(msg,idlTypesUtil.isException(excep));
            // a valid exception is always a valid value type !
            assertTrue(msg,idlTypesUtil.isValue(excep));

            assertFalse(msg, idlTypesUtil.isPrimitive(excep));
            assertFalse(msg,idlTypesUtil.isRemoteInterface(excep));
            assertFalse(msg,idlTypesUtil.isArray(excep));
            assertFalse(msg,idlTypesUtil.isObjectReference(excep));
            assertFalse(msg,idlTypesUtil.isEntity(excep));
        }

        Class[] nonExceptions = InvalidExceptions.CLASSES;

        for(int i = 0; i < nonExceptions.length; i++) {
            Class nonException = nonExceptions[i];
            String msg = nonException.getName();
            assertFalse(msg, idlTypesUtil.isException(nonException));
        }        
    }

    public void testObjRefs()
    {

        Class[] objRefs = ValidObjRefs.CLASSES;

        for(int i = 0; i < objRefs.length; i++) {
            Class objRef = objRefs[i];
            String msg = objRef.getName();

            assertTrue(msg,idlTypesUtil.isObjectReference(objRef));

            assertFalse(msg, idlTypesUtil.isPrimitive(objRef));
            assertFalse(msg,idlTypesUtil.isRemoteInterface(objRef));
            assertFalse(msg,idlTypesUtil.isValue(objRef));
            assertFalse(msg,idlTypesUtil.isArray(objRef));
            assertFalse(msg,idlTypesUtil.isException(objRef));
            assertFalse(msg,idlTypesUtil.isEntity(objRef));
        }

        Class[] nonObjRefs = InvalidObjRefs.CLASSES;

        for(int i = 0; i < nonObjRefs.length; i++) {
            Class nonObjRef = nonObjRefs[i];
            String msg = nonObjRef.getName();
            assertFalse(msg, idlTypesUtil.isObjectReference(nonObjRef));
        }        

    }

    public void testEntities()
    {

        Class[] entities = ValidEntities.CLASSES;

        for(int i = 0; i < entities.length; i++) {
            Class entity = entities[i];
            String msg = entity.getName();

            assertTrue(msg,idlTypesUtil.isEntity(entity));
            // An entity type is always a value type
            assertTrue(msg,idlTypesUtil.isValue(entity));

            assertFalse(msg, idlTypesUtil.isPrimitive(entity));
            assertFalse(msg,idlTypesUtil.isRemoteInterface(entity));
            assertFalse(msg,idlTypesUtil.isArray(entity));
            assertFalse(msg,idlTypesUtil.isException(entity));
            assertFalse(msg,idlTypesUtil.isObjectReference(entity));

        }

        Class[] nonEntities = InvalidEntities.CLASSES;

        for(int i = 0; i < nonEntities.length; i++) {
            Class nonEntity = nonEntities[i];
            String msg = nonEntity.getName();
            assertFalse(msg, idlTypesUtil.isEntity(nonEntity));
        }        

    }

}
