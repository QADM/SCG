package org.apache.jsp.mantle;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.util.Locale;
import org.apache.commons.lang.StringUtils;
import org.pentaho.platform.util.messages.LocaleHelper;
import java.net.URLClassLoader;
import java.net.URL;
import java.util.ResourceBundle;
import java.io.File;
import org.owasp.esapi.ESAPI;

public final class Mantle_jsp extends org.apache.jasper.runtime.HttpJspBase
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

      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");

  Locale effectiveLocale = request.getLocale();
  if (!StringUtils.isEmpty(request.getParameter("locale"))) {
    effectiveLocale = new Locale(request.getParameter("locale"));
    request.getSession().setAttribute("locale_override", request.getParameter("locale"));
    LocaleHelper.setLocaleOverride(effectiveLocale);
  } else {
    request.getSession().setAttribute("locale_override", null);
    LocaleHelper.setLocaleOverride(null);
  }
  
  URLClassLoader loader = new URLClassLoader(new URL[] {application.getResource("/mantle/messages/")});
  ResourceBundle properties = ResourceBundle.getBundle("mantleMessages", request.getLocale(), loader);


      out.write("\r\n");
      out.write("\r\n");
      out.write("<html>\r\n");
      out.write("\t<head>\r\n");
      out.write("\t\t<title>Pentaho User Console</title>\r\n");
      out.write("    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>\r\n");
      out.write("\t\t<meta name=\"gwt:property\" content=\"locale=");
      out.print(ESAPI.encoder().encodeForHTMLAttribute(effectiveLocale.toString()));
      out.write("\">\r\n");
      out.write("\t\t<link rel=\"shortcut icon\" href=\"/pentaho-style/favicon.ico\" />\r\n");
      out.write("\t\t<link rel='stylesheet' href='mantle/MantleStyle.css'/>\r\n");
      out.write("    <link rel=\"stylesheet\" href=\"content/data-access/resources/gwt/datasourceEditorDialog.css\"/>\r\n");
      out.write("    <link rel=\"stylesheet\" href=\"mantle/Widgets.css\" />\r\n");
      out.write("\r\n");
      out.write("    <script language=\"javascript\" type=\"text/javascript\" src=\"webcontext.js?context=mantle\"></script>\r\n");
      out.write("        \r\n");
      out.write("    <script type=\"text/javascript\" src=\"mantle/nativeScripts.js\"></script>\r\n");
      out.write("    <script type=\"text/javascript\">\r\n");
      out.write("      if(window.opener && window.opener.reportWindowOpened != undefined){\r\n");
      out.write("        window.opener.reportWindowOpened();\r\n");
      out.write("      }\r\n");
      out.write("\r\n");
      out.write("  \tvar dataAccessAvailable = false; //Used by child iframes to tell if data access is available.\r\n");
      out.write("    /* this function is called by the gwt code when initing, if the user has permission */\r\n");
      out.write("    function initDataAccess(hasAccess) {\r\n");
      out.write("      dataAccessAvailable = hasAccess;\r\n");
      out.write("      if(!hasAccess){\r\n");
      out.write("        return;\r\n");
      out.write("      }\r\n");
      out.write("      if(typeof(addMenuItem) == \"undefined\"){\r\n");
      out.write("        setTimeout(\"initDataAccess(\"+hasAccess+\")\", 1000);\r\n");
      out.write("        return;\r\n");
      out.write("      } else {\r\n");
      out.write("        addMenuItem(\"manageDatasourcesEllipsis\",\"manage_content_menu\", \"ManageDatasourcesCommand\");\r\n");
      out.write("        addMenuItem(\"newDatasource\",\"new_menu\", \"AddDatasourceCommand\");\r\n");
      out.write("      }\r\n");
      out.write("    }\r\n");
      out.write("\r\n");
      out.write("    var datasourceEditorCallback = {\r\n");
      out.write("      onFinish : function(val, transport) {\r\n");
      out.write("      },\r\n");
      out.write("      onError : function(val) {\r\n");
      out.write("        alert('error:' + val);\r\n");
      out.write("      },\r\n");
      out.write("      onCancel : function() {\r\n");
      out.write("      },\r\n");
      out.write("      onReady : function() {\r\n");
      out.write("      }\r\n");
      out.write("    }\r\n");
      out.write("\r\n");
      out.write("    // This allows content panels to have PUC create new datasources. The iframe requesting\r\n");
      out.write("    // the new datasource must have a function \"openDatasourceEditorCallback\" on it's window scope\r\n");
      out.write("    // to be notified of the successful creation of the datasource.\r\n");
      out.write("    function openDatasourceEditorIFrameProxy(windowReference){\r\n");
      out.write("    \tvar callbackHelper = function(bool, transport){\r\n");
      out.write("    \t\twindowReference.openDatasourceEditorCallback(bool, transport);\r\n");
      out.write("    \t}\r\n");
      out.write("    \tpho.openDatasourceEditor(new function(){\r\n");
      out.write("        this.onError = function(err){\r\n");
      out.write("          alert(err);\r\n");
      out.write("        }\r\n");
      out.write("        this.onCancel = function(){\r\n");
      out.write("        }\r\n");
      out.write("        this.onReady = function(){\r\n");
      out.write("        }\r\n");
      out.write("        this.onFinish = function(bool, transport){\r\n");
      out.write("          callbackHelper(bool, transport);\r\n");
      out.write("        }\r\n");
      out.write("      });\r\n");
      out.write("    }\r\n");
      out.write("\r\n");
      out.write("    </script>\r\n");
      out.write("\t</head>\r\n");
      out.write("\r\n");
      out.write("\t<body oncontextmenu=\"return false;\" class=\"pentaho-page-background\">\r\n");
      out.write("\r\n");
      out.write("\t<div id=\"loading\">\r\n");
      out.write("    \t\t<div class=\"loading-indicator\">\r\n");
      out.write("    \t\t\t<img src=\"mantle/large-loading.gif\" width=\"32\" height=\"32\"/>");
      out.print( properties.getString("loadingConsole") );
      out.write("<a href=\"http://www.pentaho.com\"></a><br/>\r\n");
      out.write("    \t\t\t<span id=\"loading-msg\">");
      out.print( properties.getString("pleaseWait") );
      out.write("</span>\r\n");
      out.write("    \t\t</div>\r\n");
      out.write("\t</div>\r\n");
      out.write("\r\n");
      out.write("\t<!-- OPTIONAL: include this if you want history support -->\r\n");
      out.write("\t<iframe id=\"__gwt_historyFrame\" style=\"width:0px;height:0px;border:0;display:none\"></iframe>\r\n");
      out.write("\r\n");
      out.write("\t</body>\r\n");
      out.write("\r\n");
      out.write("\t<script language='javascript' src='mantle/mantle.nocache.js'></script>\r\n");
      out.write("\t<script language='javascript' src='content/data-access/resources/gwt/DatasourceEditor.nocache.js'></script>\r\n");
      out.write("\r\n");
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
