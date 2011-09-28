<%--

    Copyright 2008 Autentia Real Business Solutions S.L.
    
    This file is part of autentia-util.
    
    autentia-util is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, version 3 of the License.
    
    autentia-util is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.
    
    You should have received a copy of the GNU Lesser General Public License
    along with autentia-util. If not, see <http://www.gnu.org/licenses/>.

--%>

<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="java.lang.reflect.*" %>
<%@page import="java.net.*" %>
<%@page import="java.io.*" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%!
  public String getPathToClassOrJar( Class clazz )
  {
    String cn = "/" + clazz.getName();
    cn = cn.replace('.', '/');
    cn += ".class";
    URL url = clazz.getResource(cn);
    String path = url.getPath();
    return path;
  }

  public void putInfo( JspWriter out, String key, String value ) throws IOException
  {
    out.println("<tr><td style='border-bottom: 1px solid #707070' nowrap><b>"+key+"</b></td><td style='border-bottom: 1px solid #707070'>"+value+"</td></tr>");
  }
%>

<%
  String cmd = request.getParameter("cmd");
  String className = request.getParameter("class");

  if( cmd==null ) cmd = "";
  if( className==null ) className = "";
%>
   
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Class Info</title>
    </head>
    <body>

    <h1>Class Info</h1>
    
    <form method="post">
      Type the fully qualified name of a class (example: java.lang.String) for which you want to get info:<br/>
      <input type="text" name="class" value="<%=className%>" size="128"/><br/><br/>
      <input type="submit" name="cmd" value="Get class info"/><br/>
    </form>
    
    <%
      if( !cmd.equals("") )
      {
	out.println( "<h1>Information for class <i>"+className+"</i>:</h1>" );
	
	out.println( "<table>" );
	try
	{
	  Class clazz = Class.forName(className);
	  
	  String location = getPathToClassOrJar(clazz);
	  putInfo( out, "Class location:", location );
	
	  ClassLoader loader = clazz.getClassLoader();
	  if( loader == null ){
	    putInfo( out, "Class loader:", ClassLoader.getSystemClassLoader().toString() );
	  } else {
	    putInfo( out, "Class loader:", loader.toString() );
	  }
	  
	  Class[] ifaces = clazz.getInterfaces();
	  for( int i=0 ; i<ifaces.length ; i++ )
	  {
	    putInfo( out, "Implemented interface:", ifaces[i].getName() );
	  }
	  
	  Class[] inners = clazz.getDeclaredClasses();
	  for( int i=0 ; i<inners.length ; i++ )
	  {
	    putInfo( out, "Inner class:", inners[i].getName() );
	  }

	  Field[] fields = clazz.getDeclaredFields();
	  for( int i=0 ; i<fields.length ; i++ )
	  {
	    putInfo( out, "Field:", fields[i].toString() );
	  }
	  
	  Constructor[] ctors = clazz.getConstructors();
	  for( int i=0 ; i<ctors.length ; i++ )
	  {
	    putInfo( out, "Constructor:", ctors[i].toString() );
	  }
	  
	  Method[] methods = clazz.getDeclaredMethods();
	  for( int i=0 ; i<methods.length ; i++ )
	  {
	    putInfo( out, "Method:", methods[i].toString() );
	  }
	}
	catch( ClassNotFoundException e )
	{
	  putInfo( out, "Class not found.", "" );
	}
	catch( Throwable e )
	{
	  putInfo( out, "Error analyzing class:", e.toString() );
	}
	out.println( "</table>" );
      }
    %>
    
    </body>
</html>
