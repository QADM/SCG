package org.apache.jsp.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.util.ArrayList;
import java.util.Date;
import java.io.ByteArrayOutputStream;
import org.pentaho.platform.util.web.SimpleUrlFactory;
import org.pentaho.platform.web.jsp.messages.Messages;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.uifoundation.chart.DashboardWidgetComponent;
import org.pentaho.platform.web.http.request.HttpRequestParameterProvider;
import org.pentaho.platform.web.http.session.HttpSessionParameterProvider;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.web.http.WebTemplateHelper;
import org.pentaho.platform.util.VersionHelper;
import org.pentaho.platform.util.messages.LocaleHelper;
import org.pentaho.platform.engine.core.solution.SimpleParameterProvider;
import org.pentaho.platform.uifoundation.chart.ChartHelper;
import org.pentaho.platform.web.http.PentahoHttpSessionHelper;

public final class ChartSamplesDashboard_jsp extends org.apache.jasper.runtime.HttpJspBase
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
 *
 */


	// set the character encoding e.g. UFT-8
	response.setCharacterEncoding(LocaleHelper.getSystemEncoding()); 

	// get the current Pentaho session or create a new one if needed
	IPentahoSession userSession = PentahoHttpSessionHelper.getPentahoSession( request );

      out.write("\r\n");
      out.write("<html>\r\n");
      out.write("<head>\n");
      out.write("      <link rel=\"stylesheet\" type=\"text/css\" href=\"/pentaho-style/active/default.css\"></link>\r\n");
      out.write("<title>Pentaho Chart Examples</title>\r\n");
      out.write("</head>\r\n");
      out.write("\r\n");
      out.write("<body>\r\n");
      out.write("<div style=\"margin:10px;border:0px none #808080;padding:5px;\">\r\n");
      out.write("<table class=\"parameter_table\" width=\"90%\"  border=\"1\" cellspacing=\"5\" cellpadding=\"5\">\r\n");
      out.write("  <tr> \r\n");
      out.write("    <td> <!-- -------------------------- BAR CHART ------------------------- -->\r\n");
      out.write("\t\t");


    		SimpleParameterProvider parameters = new SimpleParameterProvider();
      		parameters.setParameter( "image-width", "370"); //$NON-NLS-1$ //$NON-NLS-2$
      		parameters.setParameter( "image-height", "400"); //$NON-NLS-1$ //$NON-NLS-2$
			StringBuffer content = new StringBuffer(); 
      		ArrayList messages = new ArrayList();
       		ChartHelper.doChart( "bi-developers", "charts", "barchart.xml", parameters, content, userSession, messages, null ); //$NON-NLS-1$ //$NON-NLS-2$

       	
      out.write("\r\n");
      out.write("\t\t<br/>\r\n");
      out.write("\t\t");
      out.print( content.toString() );
      out.write("    \r\n");
      out.write("    \r\n");
      out.write("    </td>\r\n");
      out.write("    \r\n");
      out.write("    <td> <!-- -------------------------- LINE CHART ------------------------- -->\r\n");
      out.write("    \r\n");
      out.write("    \t");

	    	parameters = new SimpleParameterProvider();
      		parameters.setParameter( "image-width", "370"); //$NON-NLS-1$ //$NON-NLS-2$
      		parameters.setParameter( "image-height", "400"); //$NON-NLS-1$ //$NON-NLS-2$
			content = new StringBuffer(); 
      		messages = new ArrayList();
      		ChartHelper.doChart( "bi-developers", "charts", "linechart.xml", parameters, content, userSession, messages, null ); //$NON-NLS-1$ //$NON-NLS-2$
		
      out.write("\r\n");
      out.write("\t\t<br/>\r\n");
      out.write("\t\t");
      out.print( content.toString() );
      out.write("\r\n");
      out.write("    \r\n");
      out.write("    </td>\r\n");
      out.write("  </tr>\n");
      out.write("  <tr>      \r\n");
      out.write("    <td>  <!-- -------------------------- AREA CHART ------------------------- -->\r\n");
      out.write("\r\n");
      out.write("    \t");

	    	parameters = new SimpleParameterProvider();
      		parameters.setParameter( "image-width", "370"); //$NON-NLS-1$ //$NON-NLS-2$
      		parameters.setParameter( "image-height", "400"); //$NON-NLS-1$ //$NON-NLS-2$
			content = new StringBuffer(); 
      		messages = new ArrayList();
      		ChartHelper.doChart( "bi-developers", "charts", "areachart.xml", parameters, content, userSession, messages, null ); //$NON-NLS-1$ //$NON-NLS-2$
		
      out.write("\r\n");
      out.write("\t\t<br/>\r\n");
      out.write("\t\t");
      out.print( content.toString() );
      out.write("\r\n");
      out.write("\t</td>\r\n");
      out.write("    <td> <!-- -------------------------- PIE CHART ------------------------- -->\r\n");
      out.write("\r\n");
      out.write("    \t");

	    	parameters = new SimpleParameterProvider();
      		parameters.setParameter( "image-width", "370"); //$NON-NLS-1$ //$NON-NLS-2$
      		parameters.setParameter( "image-height", "400"); //$NON-NLS-1$ //$NON-NLS-2$
			content = new StringBuffer(); 
      		messages = new ArrayList();
      		ChartHelper.doChart( "bi-developers", "charts", "piechart.xml", parameters, content, userSession, messages, null ); //$NON-NLS-1$ //$NON-NLS-2$
		
      out.write("\r\n");
      out.write("\t\t<br/>\r\n");
      out.write("\t\t");
      out.print( content.toString() );
      out.write("\r\n");
      out.write("\r\n");
      out.write("\t</td>\r\n");
      out.write("  </tr>\n");
      out.write("  <tr>  \r\n");
      out.write("        <td> <!-- -------------------------- BAR LINE COMBO  CHART ------------------------- -->\r\n");
      out.write("\r\n");
      out.write("    \t");

	    	parameters = new SimpleParameterProvider();
      		parameters.setParameter( "image-width", "370"); //$NON-NLS-1$ //$NON-NLS-2$
      		parameters.setParameter( "image-height", "400"); //$NON-NLS-1$ //$NON-NLS-2$
			content = new StringBuffer(); 
      		messages = new ArrayList();
      		ChartHelper.doChart( "bi-developers", "charts", "barlinecombochart.xml", parameters, content, userSession, messages, null ); //$NON-NLS-1$ //$NON-NLS-2$
		
      out.write("\r\n");
      out.write("\t\t<br/>\r\n");
      out.write("\t\t");
      out.print( content.toString() );
      out.write("\r\n");
      out.write("\t</td>\n");
      out.write("    <td> <!-- -------------------------- TIME SERIES CHART ------------------------- -->\r\n");
      out.write("\r\n");
      out.write("    \t");

	    	parameters = new SimpleParameterProvider();
      		parameters.setParameter( "image-width", "370"); //$NON-NLS-1$ //$NON-NLS-2$
      		parameters.setParameter( "image-height", "400"); //$NON-NLS-1$ //$NON-NLS-2$
			content = new StringBuffer(); 
      		messages = new ArrayList();
      		ChartHelper.doChart( "bi-developers", "charts", "timeserieschart.xml", parameters, content, userSession, messages, null ); //$NON-NLS-1$ //$NON-NLS-2$
		
      out.write("\r\n");
      out.write("\t\t<br/>\r\n");
      out.write("\t\t");
      out.print( content.toString() );
      out.write("\r\n");
      out.write("\t</td>\r\n");
      out.write("  </tr>\r\n");
      out.write("  <tr>\r\n");
      out.write("    <td COLSPAN=2> <!-- -------------------------- XY CHART -------------------------------------- -->\r\n");
      out.write("\r\n");
      out.write("    \t");

	    	parameters = new SimpleParameterProvider();
      		parameters.setParameter( "image-width", "740"); //$NON-NLS-1$ //$NON-NLS-2$
      		parameters.setParameter( "image-height", "400"); //$NON-NLS-1$ //$NON-NLS-2$
			content = new StringBuffer(); 
      		messages = new ArrayList();
      		ChartHelper.doChart( "bi-developers", "charts", "xychart.xml", parameters, content, userSession, messages, null ); //$NON-NLS-1$ //$NON-NLS-2$
		
      out.write("\r\n");
      out.write("\t\t<br/>\r\n");
      out.write("\t\t");
      out.print( content.toString() );
      out.write("\r\n");
      out.write("\r\n");
      out.write("\t</td>\n");
      out.write("  </tr>\n");
      out.write("  <tr>\r\n");
      out.write("    <td COLSPAN=2> <!-- ------------------- MULTIPLE PIE CHART (PIE GRID)  ------------------------- -->\r\n");
      out.write("\r\n");
      out.write("    \t");

	    	parameters = new SimpleParameterProvider();
      		parameters.setParameter( "image-width", "740"); //$NON-NLS-1$ //$NON-NLS-2$
      		parameters.setParameter( "image-height", "400"); //$NON-NLS-1$ //$NON-NLS-2$
			content = new StringBuffer(); 
      		messages = new ArrayList();
      		ChartHelper.doChart( "bi-developers", "charts", "piegridchart.xml", parameters, content, userSession, messages, null ); //$NON-NLS-1$ //$NON-NLS-2$
		
      out.write("\r\n");
      out.write("\t\t<br/>\r\n");
      out.write("\t\t");
      out.print( content.toString() );
      out.write("\r\n");
      out.write("\t</td>\n");
      out.write("   </tr>\n");
      out.write("   <tr>\n");
      out.write("       <td> <!-- -------------------------- BUBBLE CHART ------------------------- -->\r\n");
      out.write("    \t");

	    	parameters = new SimpleParameterProvider();
      		parameters.setParameter( "image-width", "370"); //$NON-NLS-1$ //$NON-NLS-2$
      		parameters.setParameter( "image-height", "400"); //$NON-NLS-1$ //$NON-NLS-2$
			content = new StringBuffer(); 
      		messages = new ArrayList();
      		ChartHelper.doChart( "bi-developers", "charts", "bubblechart.xml", parameters, content, userSession, messages, null ); //$NON-NLS-1$ //$NON-NLS-2$
		
      out.write("\r\n");
      out.write("\t\t<br/>\r\n");
      out.write("\t\t");
      out.print( content.toString() );
      out.write("\r\n");
      out.write("\t  </td>\n");
      out.write("    <td> <!-- -------------------------- DIAL CHART ------------------------- -->\r\n");
      out.write("    \r\n");
      out.write("    \t");

	    	parameters = new SimpleParameterProvider();
      		parameters.setParameter( "image-width", "370"); //$NON-NLS-1$ //$NON-NLS-2$
      		parameters.setParameter( "image-height", "400"); //$NON-NLS-1$ //$NON-NLS-2$
			content = new StringBuffer(); 
      		messages = new ArrayList();
      		ChartHelper.doChart( "bi-developers", "charts", "dialchart.xml", parameters, content, userSession, messages, null ); //$NON-NLS-1$ //$NON-NLS-2$
		
      out.write("\r\n");
      out.write("\t\t<br/>\r\n");
      out.write("\t\t");
      out.print( content.toString() );
      out.write("\r\n");
      out.write("    </td>\t\n");
      out.write("  </tr>\t\r\n");
      out.write("</table>\n");
      out.write("</div>\r\n");
      out.write("</body>\r\n");
      out.write("</html>\r\n");
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
