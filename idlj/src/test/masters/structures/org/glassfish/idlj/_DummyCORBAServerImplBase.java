package org.glassfish.idlj;


/**
* org/glassfish/idlj/_DummyCORBAServerImplBase.java .
* Generated by the IDL-to-Java compiler (portable), version "4.1"
* from /Users/rgold/projects/glassfish/glassfish-corba/idlj/src/main/idl/org/glassfish/idlj/CORBAServerTest.idl
* Monday, January 29, 2018 11:19:41 AM EST
*/

public abstract class _DummyCORBAServerImplBase extends org.omg.CORBA.portable.ObjectImpl
                implements org.glassfish.idlj.DummyCORBAServer, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors
  public _DummyCORBAServerImplBase ()
  {
  }

  private static java.util.Map<String,Integer> _methods = new java.util.HashMap<String,Integer> ();
  static
  {
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = _methods.get($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:org/glassfish/idlj/MungedRepid:1.0"};

  public String[] _ids ()
  {
    return (String[])__ids.clone ();
  }


} // class _DummyCORBAServerImplBase