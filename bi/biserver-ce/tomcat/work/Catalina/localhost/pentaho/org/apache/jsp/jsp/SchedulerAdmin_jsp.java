package org.apache.jsp.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.util.ArrayList;
import org.pentaho.platform.util.web.SimpleUrlFactory;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.scheduler.SchedulerAdminUIComponent;
import org.pentaho.platform.web.http.request.HttpRequestParameterProvider;
import org.pentaho.platform.web.http.session.HttpSessionParameterProvider;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.web.http.WebTemplateHelper;
import org.pentaho.platform.api.engine.IUITemplater;
import org.pentaho.platform.util.messages.LocaleHelper;
import org.apache.commons.lang.StringUtils;
import org.pentaho.platform.web.jsp.messages.Messages;
import org.pentaho.platform.web.http.PentahoHttpSessionHelper;
import org.pentaho.platform.api.engine.IMessageFormatter;

public final class SchedulerAdmin_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List _jspx_dependants;

  private javax.el.ExpressionFactory _el_expressionfactory;
  private org.apache.AnnotationProcessor _jsp_annotationprocessor;

  public Object getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _el_expressionfactory = _jspxFactory.getJspApplicationContext(getServletConfig().getServletContext()).getExpressionFactory();
    _jsp_annotationprocessor = (org.apache.AnnotationProcessor) getServletConfig().getServletContext().getAttribute(org.apache.AnnotationProcessor.class.getName());
  }

  public void _jspDestroy() {
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;


    try {
      response.setContentType("text/html");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write('\r');
      out.write('\n');

 /*
 * Copyright 2006 Pentaho Corporation.  All rights reserved. 
 * This software was developed by Pentaho Corporation and is provided under the terms 
 * of the Mozilla Public License, Version 1.1, or any later version. You may not use 
 * this file except in compliance with the license. If you need a copy of the license, 
 * please go to http://www.mozilla.org/MPL/MPL-1.1.txt. The Original Code is the Pentaho 
 * BI Platform.  The Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS" 
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to 
 * the license for the specific language governing your rights and limitations.
*/
	response.setCharacterEncoding(LocaleHelper.getSystemEncoding());
	String baseUrl = PentahoSystem.getApplicationContext().getBaseUrl();

	IPentahoSession userSession = PentahoHttpSessionHelper.getPentahoSession( request );
	HttpRequestParameterProvider requestParameters = new HttpRequestParameterProvider( request );
	HttpSessionParameterProvider sessionParameters = new HttpSessionParameterProvider( userSession );
	String thisUrl = baseUrl + "./SchedulerAdmin?"; //$NON-NLS-1$
	
	String mimeType = request.getParameter( "requestedMimeType" );
	if ( StringUtils.isEmpty( mimeType ) ) {
	  mimeType = "text/html";
	}

	SimpleUrlFactory urlFactory = new SimpleUrlFactory( thisUrl );
	ArrayList messages = new ArrayList();

	SchedulerAdminUIComponent admin = new SchedulerAdminUIComponent( urlFactory, messages ); //$NON-NLS-1$

	admin.validate( userSession, null );
	
	admin.setParameterProvider( HttpRequestParameterProvider.SCOPE_REQUEST, requestParameters ); //$NON-NLS-1$
	admin.setParameterProvider( HttpSessionParameterProvider.SCOPE_SESSION, sessionParameters ); //$NON-NLS-1$
	
	String content = admin.getContent( mimeType );
	if ( "text/html".equals( mimeType ) ) {
		if( content == null ) {
			StringBuffer buffer = new StringBuffer();
			PentahoSystem.get(IMessageFormatter.class, userSession).formatErrorMessage( "text/html", Messages.getErrorString( "SCHEDULER_ADMIN.ERROR_0001_DISPLAY_ERROR" ), messages, buffer ); //$NON-NLS-1$ //$NON-NLS-2$
			content = buffer.toString();
		}
	
		String intro = "";
		String footer = "";
		IUITemplater templater = PentahoSystem.get(IUITemplater.class, userSession );
		if( templater != null ) {
			String sections[] = templater.breakTemplate( "template.html", "", userSession ); //$NON-NLS-1$ //$NON-NLS-2$
			if( sections != null && sections.length > 0 ) {
				intro = sections[0];
			}
			if( sections != null && sections.length > 1 ) {
				footer = sections[1];
			}
		} else {
			intro = Messages.getString( "UI.ERROR_0002_BAD_TEMPLATE_OBJECT" );
		}
	
		// Content had $ signs - the regex stuff messes up with $ and \ so...
	    content = content.replaceAll( "\\\\", "\\\\\\\\" );
	    content = content.replaceAll( "\\$", "\\\\\\$" );
		
      out.print( intro );
      out.write("\r\n");
      out.write("\t\t");
      out.print( content );
      out.write("\r\n");
      out.write("\t\t");
      out.print( footer );
    
	} else {
		if( content == null ) {
		  content = "<error msg='" + Messages.getErrorString( "SCHEDULER_ADMIN.ERROR_0001_DISPLAY_ERROR" ) + "'></error>";
		}
		
      out.print(content);
  
	}

    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          try { out.clearBuffer(); } catch (java.io.IOException e) {}
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
