/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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

import com.sun.corba.ee.spi.copyobject.CopyobjectDefaults;
import com.sun.corba.ee.spi.oa.OAInvocationInfo;
import com.sun.corba.ee.spi.presentation.rmi.DynamicMethodMarshaller;
import com.sun.corba.ee.spi.presentation.rmi.IDLNameTranslator;
import com.sun.corba.ee.spi.presentation.rmi.InvocationInterceptor;
import com.sun.corba.ee.spi.presentation.rmi.PresentationManager;
import com.sun.corba.ee.spi.protocol.ClientDelegate;
import com.sun.corba.ee.spi.protocol.LocalClientRequestDispatcher;
import com.sun.corba.ee.spi.transport.ContactInfoList;
import org.junit.Before;
import org.junit.Test;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.ServantObject;

import java.lang.reflect.Method;
import java.rmi.RemoteException;

import static com.meterware.simplestub.Stub.createStrictStub;
import static com.meterware.simplestub.Stub.createStub;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.not;

public class StubInvocationHandlerImplTest {

    private PresentationManagerFake presentationManager = createStrictStub(PresentationManagerFake.class);
    private ClassDataFake classData = createStrictStub(ClassDataFake.class);

    private CalledObject calledObject = new CalledObject();
    private Method throwExceptionMethod;

    private StubInvocationHandlerImpl handler = new StubInvocationHandlerImpl(presentationManager, classData, new CalledObject());

    @Before
    public void setUp() throws Exception {
        throwExceptionMethod = CalledObject.class.getMethod("throwException");
    }

    @Test
    public void whenColocatedCallThrowsException_exceptionHasStackTrace() throws Throwable {
        try {
            handler.invoke(calledObject, throwExceptionMethod, new Object[0]);
        } catch (TestException exception) {
            assertThat(exception.getStackTrace(), not(emptyArray()));
        }
    }

    static class CalledObject extends ObjectImpl {
        private Delegate delegate = createStrictStub(ClientDelegateFake.class, this);

        @SuppressWarnings({"WeakerAccess", "unused"})
        public void throwException() throws Exception {
            throw new TestException("thrown in test");
        }

        @Override
        public String[] _ids() {
            return new String[0];
        }

        @Override
        public Delegate _get_delegate() {
            return delegate;
        }
    }

    static class TestException extends RuntimeException {
        public TestException(String message) {
            super(message);
        }
    }

    static abstract class PresentationManagerFake implements PresentationManager {
        @Override
        public DynamicMethodMarshaller getDynamicMethodMarshaller(Method method) {
            return createStrictStub(DynamicMethodMarshallerFake.class);
        }
    }

    static abstract class DynamicMethodMarshallerFake implements DynamicMethodMarshaller {
        @Override
        public java.lang.Object[] copyArguments(java.lang.Object[] args, ORB orb) throws RemoteException {
            return args;
        }

        @Override
        public boolean isDeclaredException(Throwable thr) {
            return true;
        }
    }

    static abstract class ClassDataFake implements PresentationManager.ClassData {
        @Override
        public IDLNameTranslator getIDLNameTranslator() {
            return createStrictStub(IDLNameTranslatorFake.class);
        }
    }

    static abstract class IDLNameTranslatorFake implements IDLNameTranslator {
        @Override
        public String getIDLName(Method method) {
            return "methodName";
        }
    }

    static abstract class ClientDelegateFake extends ClientDelegate {
        private Object servant;

        ClientDelegateFake(Object servant) {
            this.servant = servant;
        }

        @Override
        public ContactInfoList getContactInfoList() {
            return createStrictStub(ContactInfoListFake.class);
        }

        @Override
        public ORB orb(Object obj) {
            return createStrictStub(ORBFake.class);
        }

        @Override
        public ServantObject servant_preinvoke(Object self, String operation, Class expectedType) {
            ServantObject servantObject = new ServantObject();
            servantObject.servant = servant;
            return servantObject;
        }
    }

    static abstract class ORBFake extends com.sun.corba.ee.spi.orb.ORB {
        private OAInvocationInfo invocationInfo = new OAInvocationInfo(null, new byte[0]);

        public ORBFake() {
            invocationInfo.setCopierFactory(
                    CopyobjectDefaults.makeFallbackObjectCopierFactory(
                            CopyobjectDefaults.makeReflectObjectCopierFactory(this),
                            CopyobjectDefaults.makeORBStreamObjectCopierFactory(this)));
        }

        @Override
        public InvocationInterceptor getInvocationInterceptor() {
            return createStub(InvocationInterceptor.class);
        }

        @Override
        public OAInvocationInfo peekInvocationInfo() {
            return invocationInfo;
        }
    }

    static abstract class ContactInfoListFake implements ContactInfoList {
        @Override
        public LocalClientRequestDispatcher getLocalClientRequestDispatcher() {
            return createStrictStub(LocalClientRequestDispatcherFake.class);
        }
    }

    static abstract class LocalClientRequestDispatcherFake implements LocalClientRequestDispatcher {
        private boolean useLocalInvocation;

        @SuppressWarnings("unused")
        public LocalClientRequestDispatcherFake() {
            this(true);
        }

        LocalClientRequestDispatcherFake(boolean useLocalInvocation) {
            this.useLocalInvocation = useLocalInvocation;
        }

        @Override
        public boolean useLocalInvocation(Object self) {
            return useLocalInvocation;
        }
    }


}

