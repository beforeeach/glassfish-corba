package org.glassfish.idlj.CORBAServerTestPackage;


/**
* org/glassfish/idlj/CORBAServerTestPackage/MungedExceptionHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "4.1"
* from /Users/rgold/projects/glassfish/glassfish-corba/idlj/src/main/idl/org/glassfish/idlj/CORBAServerTest.idl
* Monday, January 29, 2018 11:19:41 AM EST
*/

abstract public class MungedExceptionHelper
{
  private static String  _id = "IDL:org/glassfish/idlj/MungedExRepid:1.0";

  public static void insert (org.omg.CORBA.Any a, org.glassfish.idlj.CORBAServerTestPackage.MungedException that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static org.glassfish.idlj.CORBAServerTestPackage.MungedException extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  private static boolean __active = false;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      synchronized (org.omg.CORBA.TypeCode.class)
      {
        if (__typeCode == null)
        {
          if (__active)
          {
            return org.omg.CORBA.ORB.init().create_recursive_tc ( _id );
          }
          __active = true;
          org.omg.CORBA.StructMember[] _members0 = new org.omg.CORBA.StructMember [0];
          org.omg.CORBA.TypeCode _tcOf_members0 = null;
          __typeCode = org.omg.CORBA.ORB.init ().create_exception_tc (org.glassfish.idlj.CORBAServerTestPackage.MungedExceptionHelper.id (), "MungedException", _members0);
          __active = false;
        }
      }
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static org.glassfish.idlj.CORBAServerTestPackage.MungedException read (org.omg.CORBA.portable.InputStream istream)
  {
    org.glassfish.idlj.CORBAServerTestPackage.MungedException value = new org.glassfish.idlj.CORBAServerTestPackage.MungedException ();
    // read and discard the repository ID
    istream.read_string ();
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, org.glassfish.idlj.CORBAServerTestPackage.MungedException value)
  {
    // write the repository ID
    ostream.write_string (id ());
  }

}