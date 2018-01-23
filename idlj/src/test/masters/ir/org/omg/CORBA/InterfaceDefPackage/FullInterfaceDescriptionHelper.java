package org.omg.CORBA.InterfaceDefPackage;


/**
* org/omg/CORBA/InterfaceDefPackage/FullInterfaceDescriptionHelper.java .
* IGNORE Generated by the IDL-to-Java compiler (portable), version "3.2"
* from idlj/src/main/java/com/sun/tools/corba/ee/idl/ir.idl
* IGNORE Sunday, January 21, 2018 1:54:23 PM EST
*/

abstract public class FullInterfaceDescriptionHelper
{
  private static String  _id = "IDL:omg.org/CORBA/InterfaceDef/FullInterfaceDescription:1.0";

  public static void insert (org.omg.CORBA.Any a, org.omg.CORBA.InterfaceDefPackage.FullInterfaceDescription that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static org.omg.CORBA.InterfaceDefPackage.FullInterfaceDescription extract (org.omg.CORBA.Any a)
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
          org.omg.CORBA.StructMember[] _members0 = new org.omg.CORBA.StructMember [9];
          org.omg.CORBA.TypeCode _tcOf_members0 = null;
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CORBA.IdentifierHelper.id (), "Identifier", _tcOf_members0);
          _members0[0] = new org.omg.CORBA.StructMember (
            "name",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CORBA.RepositoryIdHelper.id (), "RepositoryId", _tcOf_members0);
          _members0[1] = new org.omg.CORBA.StructMember (
            "id",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CORBA.RepositoryIdHelper.id (), "RepositoryId", _tcOf_members0);
          _members0[2] = new org.omg.CORBA.StructMember (
            "defined_in",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CORBA.VersionSpecHelper.id (), "VersionSpec", _tcOf_members0);
          _members0[3] = new org.omg.CORBA.StructMember (
            "version",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_boolean);
          _members0[4] = new org.omg.CORBA.StructMember (
            "is_abstract",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.OperationDescriptionHelper.type ();
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_sequence_tc (0, _tcOf_members0);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CORBA.OpDescriptionSeqHelper.id (), "OpDescriptionSeq", _tcOf_members0);
          _members0[5] = new org.omg.CORBA.StructMember (
            "operations",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.AttributeDescriptionHelper.type ();
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_sequence_tc (0, _tcOf_members0);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CORBA.AttrDescriptionSeqHelper.id (), "AttrDescriptionSeq", _tcOf_members0);
          _members0[6] = new org.omg.CORBA.StructMember (
            "attributes",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CORBA.RepositoryIdHelper.id (), "RepositoryId", _tcOf_members0);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_sequence_tc (0, _tcOf_members0);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.omg.CORBA.RepositoryIdSeqHelper.id (), "RepositoryIdSeq", _tcOf_members0);
          _members0[7] = new org.omg.CORBA.StructMember (
            "base_interfaces",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_TypeCode);
          _members0[8] = new org.omg.CORBA.StructMember (
            "type",
            _tcOf_members0,
            null);
          __typeCode = org.omg.CORBA.ORB.init ().create_struct_tc (org.omg.CORBA.InterfaceDefPackage.FullInterfaceDescriptionHelper.id (), "FullInterfaceDescription", _members0);
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

  public static org.omg.CORBA.InterfaceDefPackage.FullInterfaceDescription read (org.omg.CORBA.portable.InputStream istream)
  {
    org.omg.CORBA.InterfaceDefPackage.FullInterfaceDescription value = new org.omg.CORBA.InterfaceDefPackage.FullInterfaceDescription ();
    value.name = istream.read_string ();
    value.id = istream.read_string ();
    value.defined_in = istream.read_string ();
    value.version = istream.read_string ();
    value.is_abstract = istream.read_boolean ();
    value.operations = org.omg.CORBA.OpDescriptionSeqHelper.read (istream);
    value.attributes = org.omg.CORBA.AttrDescriptionSeqHelper.read (istream);
    value.base_interfaces = org.omg.CORBA.RepositoryIdSeqHelper.read (istream);
    value.type = istream.read_TypeCode ();
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, org.omg.CORBA.InterfaceDefPackage.FullInterfaceDescription value)
  {
    ostream.write_string (value.name);
    ostream.write_string (value.id);
    ostream.write_string (value.defined_in);
    ostream.write_string (value.version);
    ostream.write_boolean (value.is_abstract);
    org.omg.CORBA.OpDescriptionSeqHelper.write (ostream, value.operations);
    org.omg.CORBA.AttrDescriptionSeqHelper.write (ostream, value.attributes);
    org.omg.CORBA.RepositoryIdSeqHelper.write (ostream, value.base_interfaces);
    ostream.write_TypeCode (value.type);
  }

}