/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
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
/*
 * Licensed Materials - Property of IBM
 * RMI-IIOP v1.0
 * Copyright IBM Corp. 1998 1999  All Rights Reserved
 *
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */

package com.sun.corba.ee.impl.corba;


import org.omg.CORBA.Any;
import org.omg.CORBA.ARG_IN;
import org.omg.CORBA.ARG_OUT;
import org.omg.CORBA.ARG_INOUT;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.Environment;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.Request;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;

import org.omg.CORBA.portable.ApplicationException ;
import org.omg.CORBA.portable.RemarshalException ;
import org.omg.CORBA.portable.InputStream ;
import org.omg.CORBA.portable.OutputStream ;

import com.sun.corba.ee.spi.orb.ORB;
import com.sun.corba.ee.spi.presentation.rmi.StubAdapter;
import com.sun.corba.ee.spi.logging.ORBUtilSystemException;

public class RequestImpl 
    extends Request
{
    ///////////////////////////////////////////////////////////////////////////
    // data members

    protected org.omg.CORBA.Object _target;
    protected String             _opName;
    protected NVList             _arguments;
    protected ExceptionList      _exceptions;
    private NamedValue           _result;
    protected Environment        _env;
    private Context              _ctx;
    private ContextList          _ctxList;
    protected ORB                _orb;
    private static final ORBUtilSystemException _wrapper =
        ORBUtilSystemException.self ;

    // invocation-specific stuff
    protected boolean            _isOneWay      = false;
    private int[]                _paramCodes;
    private long[]               _paramLongs;
    private java.lang.Object[]   _paramObjects;

    // support for deferred invocations. 
    // protected instead of private since it needs to be set by the
    // thread object doing the asynchronous invocation.
    protected boolean            gotResponse    = false;

    ///////////////////////////////////////////////////////////////////////////
    // constructor

    // REVISIT - used to be protected.  Now public so it can be
    // accessed from xgiop.
    public RequestImpl (ORB orb,
                        org.omg.CORBA.Object targetObject,
                        Context ctx,
                        String operationName,
                        NVList argumentList,
                        NamedValue resultContainer,
                        ExceptionList exceptionList,
                        ContextList ctxList)
    {

        // initialize the orb
        _orb    = orb;

        // initialize target, context and operation name
        _target     = targetObject;
        _ctx    = ctx;
        _opName = operationName;

        // initialize argument list if not passed in
        if (argumentList == null) {
            _arguments = new NVListImpl(_orb);
        } else {
            _arguments = argumentList;
        }

        // set result container. 
        _result = resultContainer;

        // initialize exception list if not passed in
        if (exceptionList == null) {
            _exceptions = new ExceptionListImpl();
        } else {
            _exceptions = exceptionList;
        }

        // initialize context list if not passed in
        if (ctxList == null) {
            _ctxList = new ContextListImpl(_orb);
        } else {
            _ctxList = ctxList;
        }

        // initialize environment 
        _env    = new EnvironmentImpl();

    }

    public synchronized org.omg.CORBA.Object target()
    {
        return _target;
    }

    public synchronized String operation() 
    {
        return _opName;
    }

    public synchronized NVList arguments() 
    {
        return _arguments;
    }
    
    public synchronized NamedValue result() 
    {
        return _result;
    }
    
    public synchronized Environment env() 
    {
        return _env;
    }
    
    public synchronized ExceptionList exceptions() 
    {
        return _exceptions;
    }
    
    public synchronized ContextList contexts() 
    {
        return _ctxList;
    }
    
    public synchronized Context ctx() 
    {
        if (_ctx == null) {
            _ctx = new ContextImpl(_orb);
        }
        return _ctx;
    }
    
    public synchronized void ctx(Context newCtx) 
    {
        _ctx = newCtx;
    }

    public synchronized Any add_in_arg()
    {
        return _arguments.add(org.omg.CORBA.ARG_IN.value).value();
    }

    public synchronized Any add_named_in_arg(String name)
    {
        return _arguments.add_item(name, org.omg.CORBA.ARG_IN.value).value();
    }

    public synchronized Any add_inout_arg()
    {
        return _arguments.add(org.omg.CORBA.ARG_INOUT.value).value();
    }

    public synchronized Any add_named_inout_arg(String name)
    {
        return _arguments.add_item(name, org.omg.CORBA.ARG_INOUT.value).value();
    }

    public synchronized Any add_out_arg()
    {
        return _arguments.add(org.omg.CORBA.ARG_OUT.value).value();
    }

    public synchronized Any add_named_out_arg(String name)
    {
        return _arguments.add_item(name, org.omg.CORBA.ARG_OUT.value).value();
    }

    public synchronized void set_return_type(TypeCode tc)
    {
        if (_result == null) {
            _result = new NamedValueImpl(_orb);
        }
        _result.value().type(tc);
    }

    public synchronized Any return_value()
    {
        if (_result == null) {
            _result = new NamedValueImpl(_orb);
        }
        return _result.value();
    }

    public synchronized void add_exception(TypeCode exceptionType)
    {
        _exceptions.add(exceptionType);
    }
    
    public synchronized void invoke()
    {
        doInvocation();
    }

    public synchronized void send_oneway()
    {
        _isOneWay = true;
        doInvocation();
    }
    
    public synchronized void send_deferred()
    {
        AsynchInvoke invokeObject = new AsynchInvoke(_orb, this, false);
        new Thread(invokeObject).start();
    }
    
    public synchronized boolean poll_response()
    {
        // this method has to be synchronized even though it seems
        // "readonly" since the thread object doing the asynchronous
        // invocation can potentially update this variable in parallel.
        // updates are currently simply synchronized againt the request
        // object. 
        return gotResponse;
    }
    
    public synchronized void get_response()
        throws org.omg.CORBA.WrongTransaction
    {
        while (gotResponse == false) {
            // release the lock. wait to be notified by the thread that is
            // doing the asynchronous invocation.
            try {
                wait();
            } 
            catch (InterruptedException e) {}
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // private helper methods

    /*
     * The doInvocation operation is where the real mechanics of
     * performing the request invocation is done.
     */
    protected void doInvocation()
    {
        org.omg.CORBA.portable.Delegate delegate = StubAdapter.getDelegate( 
            _target ) ;

        // Initiate Client Portable Interceptors.  Inform the PIHandler that 
        // this is a DII request so that it knows to ignore the second 
        // inevitable call to initiateClientPIRequest in createRequest. 
        // Also, save the RequestImpl object for later use. 
        _orb.getPIHandler().initiateClientPIRequest( true );
        _orb.getPIHandler().setClientPIInfo( this );

        InputStream $in = null;
        try {
            OutputStream $out = delegate.request(null, _opName, !_isOneWay);
            // Marshal args
            try {
                for (int i=0; i<_arguments.count() ; i++) {
                    NamedValue nv = _arguments.item(i);
                    switch (nv.flags()) {
                    case ARG_IN.value:
                        nv.value().write_value($out);
                        break;
                    case ARG_OUT.value:
                        break;
                    case ARG_INOUT.value:
                        nv.value().write_value($out);
                        break;
                    default:
                    }
                }
            } catch ( org.omg.CORBA.Bounds ex ) {
                throw _wrapper.boundsErrorInDiiRequest( ex ) ;
            }

            $in = delegate.invoke(null, $out);
        } catch (ApplicationException e) {
            // REVISIT - minor code.
            // This is already handled in subcontract.
            // REVISIT - uncomment.
            //throw new INTERNAL();
        } catch (RemarshalException e) {
            doInvocation();
        } catch( SystemException ex ) {
            _env.exception(ex);
            // NOTE: The exception should not be thrown.
            // However, JDK 1.4 and earlier threw the exception,
            // so we keep the behavior to be compatible.
            throw ex;
        } finally {
            delegate.releaseReply(null, $in);
        }
    }

    // REVISIT -  make protected after development - so xgiop can get it.
    public synchronized void unmarshalReply(InputStream is)
    {
        // First unmarshal the return value if it is not void
        if ( _result != null ) {
            Any returnAny = _result.value();
            TypeCode returnType = returnAny.type();
            if ( returnType.kind().value() != TCKind._tk_void )
                returnAny.read_value(is, returnType);
        }
        
        // Now unmarshal the out/inout args
        try {
            for ( int i=0; i<_arguments.count() ; i++) {
                NamedValue nv = _arguments.item(i);
                switch( nv.flags() ) {
                case ARG_IN.value:
                    break;
                case ARG_OUT.value:
                case ARG_INOUT.value:
                    Any any = nv.value();       
                    any.read_value(is, any.type());
                    break;
                default:
                }
            }
        } catch ( org.omg.CORBA.Bounds ex ) {
            // Cannot happen since we only iterate till _arguments.count()
        }
    }
}