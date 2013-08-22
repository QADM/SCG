package org.apache.jsp.webHelp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import org.pentaho.platform.web.jsp.messages.Messages;

public final class Viewer_jsp extends org.apache.jasper.runtime.HttpJspBase
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

      out.write('\n');
      out.write('\n');

/*
 * Copyright 2011 Pentaho Corporation.  All rights reserved.
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
 * 
 */
 

      out.write("\n");
      out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"\n");
      out.write("   \"http://www.w3.org/TR/html4/loose.dtd\">\n");
      out.write("\n");
      out.write("<html lang=\"en\">\n");
      out.write("<head>\n");
      out.write("\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n");
      out.write("\t<title>");
      out.print(Messages.getString( "UI.WEB_HELP_TITLE" ));
      out.write("</title>\n");
      out.write("\t<style>\n");
      out.write("\t  body, html{\n");
      out.write("\t    padding: 0px;\n");
      out.write("\t    margin: 0px;\n");
      out.write("  \t  height: 100%;\n");
      out.write("\t  }\n");
      out.write("\t  #footer{\n");
      out.write("\t    position:absolute;\n");
      out.write("\t    width: 100%;\n");
      out.write("\t    height: 20px;\n");
      out.write("\t    background-color: #555;\n");
      out.write("\t    position: fixed;\n");
      out.write("\t    clear:both;\n");
      out.write("\t    bottom: 0px;\n");
      out.write("\t    padding: 4px 0px;\n");
      out.write("\t  }\n");
      out.write("\t  button{\n");
      out.write("\t    float:right;\n");
      out.write("\t  }\n");
      out.write("\t</style>\n");
      out.write("</head>\n");
      out.write("<body>\n");
      out.write("  <div id=\"footer\">\n");
      out.write("    <button onclick=\"window.close()\">");
      out.print(Messages.getString( "UI.WEB_HELP_CLOSE" ));
      out.write("</button>\n");
      out.write("    <button onclick=\"window.print()\">");
      out.print(Messages.getString( "UI.WEB_HELP_PRINT" ));
      out.write("</button>\n");
      out.write("  </div>\n");
      out.write("  <iframe src=\"");
      out.print(request.getContextPath()+"/"+request.getParameter("topic"));
      out.write("\" style=\"height:100%; width:100%; border:none\"/>\n");
      out.write("\n");
      out.write("</body>\n");
      out.write("</html>\n");
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
