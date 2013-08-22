package org.apache.jsp.mantle.launch;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.io.InputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.pentaho.platform.util.messages.LocaleHelper;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.api.engine.IPluginManager;
import org.pentaho.platform.api.repository.ISolutionRepository;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.engine.core.system.StandaloneSession;
import org.pentaho.platform.util.logging.Logger;
import org.pentaho.platform.web.jsp.messages.Messages;
import org.pentaho.platform.web.http.PentahoHttpSessionHelper;
import org.apache.commons.lang.StringEscapeUtils;
import org.pentaho.ui.xul.XulOverlay;
import org.pentaho.platform.api.engine.IPluginManager;

public final class launch_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {


  private static ResourceBundle getBundle(String messageUri) {
    Locale locale = LocaleHelper.getLocale();
    IPentahoSession session = new StandaloneSession( "dashboards messages" ); //$NON-NLS-1$
    try {
        if (messageUri.startsWith("content/")) {
          messageUri = "system/" + messageUri.substring(8); //$NON-NLS-1$
        }
      InputStream in = PentahoSystem.get(ISolutionRepository.class, session).getResourceInputStream(messageUri, true, ISolutionRepository.ACTION_EXECUTE);
      return new PropertyResourceBundle( in );
    } catch (Exception e) {
      Logger.error( Messages.class.getName(), "Could not get localization bundle", e ); //$NON-NLS-1$
    }
    return null;
  }

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
      response.setContentType("text/html;charset=utf-8");
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
   * @created Jul 23, 2005
   * @author James Dixon
   *
   */

      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\r\n");
      out.write("<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n");
      out.write("<head>\r\n");
      out.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n");
      out.write("<title>");
      out.print(Messages.getString("UI.PUC.LAUNCH.TITLE"));
      out.write("</title>\r\n");
      out.write("<style type=\"text/css\">\r\n");
      out.write("\r\n");
      out.write("body{\r\n");
      out.write("\ttext-align: center;\r\n");
      out.write("}\r\n");
      out.write("\r\n");
      out.write("#wrapper {\r\n");
      out.write("\tmargin-right: auto;\r\n");
      out.write("\tmargin-left: auto;\r\n");
      out.write("\tmargin-top: 10%;\r\n");
      out.write("\twidth: 684px;\r\n");
      out.write("\ttext-align: center;\r\n");
      out.write("}\r\n");
      out.write("\r\n");
      out.write("</style>\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("<script language=\"javascript\" type=\"text/javascript\" src=\"webcontext.js?context=mantle&cssOnly=true\"></script>\r\n");
      out.write("<script language=\"javascript\" type=\"text/javascript\" src=\"../../js/pentaho-ajax.js\"></script>\r\n");
      out.write("<script type=\"text/javascript\">\r\n");
      out.write("  \r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("  var actionToCmdMap = [];\r\n");
      out.write("  actionToCmdMap['launch_WAQR'] = 'openWAQR()';\r\n");
      out.write("  actionToCmdMap['launch_new_datasource'] = 'newDatasource()';\r\n");
      out.write("  actionToCmdMap['launch_manage_datasources'] = 'manageDatasources()'\r\n");
      out.write("  \r\n");
 
  boolean isCE = true;
  boolean hasAnalyzer = false;
  boolean hasIteractiveReporting = false;
  boolean hasDashboards = false;
  IPluginManager pluginManager = PentahoSystem.get(IPluginManager.class, PentahoHttpSessionHelper.getPentahoSession(request)); 
  if (pluginManager != null) {
    for(XulOverlay overlayObj : pluginManager.getOverlays()) {
      if (overlayObj.getId() != null && overlayObj.getId().equals("launch")) { //$NON-NLS-1$
        ResourceBundle bundle = getBundle(overlayObj.getResourceBundleUri());
        // replace I18N parameters
        Pattern p = Pattern.compile("\\$\\{([^\\}]*)\\}"); //$NON-NLS-1$
        Matcher m = p.matcher(overlayObj.getOverlayXml());
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
          String param = m.group(1);
          m.appendReplacement(sb, bundle.getString(param));
        }
        m.appendTail(sb);
        String overlay = sb.toString();
        
        String actionName = null;
        int id = overlay.indexOf("id=\""); //$NON-NLS-1$
        if (id >= 0) {
          actionName = overlay.substring(id + 4, overlay.indexOf("\"", id + 4)); //$NON-NLS-1$
        }
        if (actionName != null) {	  
          int startCommand = overlay.indexOf("command=\""); //$NON-NLS-1$
          int endCommand = overlay.indexOf("\"", startCommand + 9); //$NON-NLS-1$
          String actionCommand = overlay.substring(startCommand + 9, endCommand);
		  
      out.write("\r\n");
      out.write("\t\t  actionToCmdMap['");
      out.print(actionName);
      out.write("'] = \"");
      out.print( actionCommand);
      out.write("\";\r\n");
      out.write("\t\t  ");

        }
      }
    }
  	hasAnalyzer = pluginManager.getRegisteredPlugins().contains("analyzer");
  	hasIteractiveReporting = pluginManager.getRegisteredPlugins().contains("pentaho-interactive-reporting");
  	hasDashboards = pluginManager.getRegisteredPlugins().contains("dashboards");

  	isCE = !hasAnalyzer && !hasIteractiveReporting && !hasDashboards;
  }

      out.write("\r\n");
      out.write("\r\n");
      out.write("function MM_callJS(jsStr) { //v2.0\r\n");
      out.write("  return eval(jsStr)\r\n");
      out.write("}\r\n");
      out.write("\r\n");
      out.write("function launch_new_WAQR() {\r\n");
      out.write("  launch('launch_WAQR', function() {warning('You do not have Data Source access.')})\r\n");
      out.write("}\r\n");
      out.write("\r\n");
      out.write("function launch_newDatasource() {\r\n");
      out.write("  launch('launch_new_datasource', function() {warning('You do not have Data Source access.')})\r\n");
      out.write("}\r\n");
      out.write("\r\n");
      out.write("function launch_managesDatasources() {\r\n");
      out.write("  launch('launch_manage_datasources', function() {warning('You do not have Data Source access.')})\r\n");
      out.write("}\r\n");
      out.write("\r\n");
      out.write("function launch_newDashboard() {\r\n");
      out.write("    launch('launch_new_dashboard', function() {warning('Dashboards Plug-in missing, corrupted or license not found.')})\r\n");
      out.write("}\r\n");
      out.write("\r\n");
      out.write("function launch(action, defaultAction) {\r\n");
      out.write("  // if we have a plugin to handle this use it\r\n");
      out.write("  if (actionToCmdMap[action]) {\r\n");
      out.write("    if (window.top.mantle_initialized) {\r\n");
      out.write("      eval(\"window.top.\" + actionToCmdMap[action]);\r\n");
      out.write("\t} else {\r\n");
      out.write("      eval(\"window.parent.\" + actionToCmdMap[action]);\r\n");
      out.write("\t}\r\n");
      out.write("  } else {\r\n");
      out.write("    defaultAction();\r\n");
      out.write("  }  \r\n");
      out.write("}\r\n");
      out.write("\r\n");
      out.write("function warning(message) {\r\n");
      out.write("  if (window.top.mantle_initialized) {\r\n");
      out.write("    window.top.mantle_showMessage(\"Error\", message);\r\n");
      out.write("  } else {\r\n");
      out.write("    window.parent.mantle_showMessage(\"Error\", message);\r\n");
      out.write("  }\r\n");
      out.write("}\r\n");
      out.write("\r\n");
      out.write("function checkDA(){ \r\n");
      out.write("\tjQuery.ajax({\r\n");
      out.write("\t\ttype: \"GET\",\r\n");
      out.write("\t\tcache: false,\t\r\n");
      out.write("\t\tdataType: 'text',\r\n");
      out.write("\t\turl: CONTEXT_PATH + 'content/ws-run/metadataServiceDA/getDatasourcePermissions',\r\n");
      out.write("\t\terror:function (xhr, ajaxOptions, thrownError){\r\n");
      out.write("    \t},            \r\n");
      out.write("\t\tsuccess:function(data, textStatus, jqXHR){\r\n");
      out.write("\t\t\tif(data.indexOf(\"EDIT\") > -1) {\r\n");
      out.write("\t\t\t\tdocument.getElementById('datasourcePanel').style.display = 'block';\r\n");
      out.write("\t\t\t}\r\n");
      out.write("\t\t}\r\n");
      out.write("\t}); \r\n");
      out.write("}\r\n");
      out.write("\r\n");
      out.write("</script>\r\n");
      out.write("\r\n");
      out.write("</head>\r\n");
      out.write("<body style=\"height:auto; background:transparent;\" onload=\"checkDA();customizeThemeStyling();\">\r\n");
      out.write("<div id=\"wrapper\">\r\n");
      out.write("  <div class=\"pentaho-launcher-panel-shadowed pentaho-launcher-shine\" id=\"outterWrapper\">\r\n");
      out.write("    ");
 
    if (isCE) {
    
      out.write("\r\n");
      out.write("    <table id=\"proMenuTable\" width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n");
      out.write("      <tr>\r\n");
      out.write("       \t<td align=\"center\" width=\"226\" valign=\"bottom\" class=\"largeGraphicButton\"><img src=\"images/clr.gif\" width=\"226\" height=\"10\"><br><a href=\"#\" onClick=\"launch_new_WAQR()\"><img src=\"images/new_report.png\" border=\"0\"></a></td>\r\n");
      out.write("       \t<td valign=\"bottom\" width=\"3\" class=\"largeGraphicSpacer\"><img src=\"images/clr.gif\" width=\"3\" height=\"11\"></td>\r\n");
      out.write("       \t<td align=\"center\" width=\"226\" valign=\"bottom\" class=\"largeGraphicButton\"><img src=\"images/clr.gif\" width=\"226\" height=\"10\"><br><a href=\"#\" onClick=\"launch('launch_new_analysis', window.top.openAnalysis)\"><img src=\"images/new_analysis.png\" border=\"0\"></a></td>\r\n");
      out.write("      </tr>\r\n");
      out.write("      <tr>\r\n");
      out.write("        <td align=\"center\" class=\"smallButton\"><button class=\"pentaho-button\" id=\"button0\" onClick=\"launch('launch_new_report', window.top.openWAQR)\">New Report</button></td>\r\n");
      out.write("        <td class=\"largeGraphicSpacer\"><img src=\"images/clr.gif\" width=\"3\" height=\"4\"></td>\r\n");
      out.write("        <td align=\"center\" class=\"smallButton\"><button class=\"pentaho-button\" id=\"button0\" onClick=\"launch('launch_new_analysis', window.top.openAnalysis)\">New Analysis</button></td>\r\n");
      out.write("      </tr>\r\n");
      out.write("\t</table>\r\n");
      out.write("    ");

    } else {
    
      out.write("\r\n");
      out.write("    <table id=\"proMenuTable\" width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n");
      out.write("      <tr>\r\n");
      out.write("\t");
 if (hasIteractiveReporting) { 
      out.write("\r\n");
      out.write("       \t<td align=\"center\" width=\"226\" valign=\"bottom\" class=\"largeGraphicButton\"><img src=\"images/clr.gif\" width=\"226\" height=\"10\"><br><a href=\"#\" onClick=\"launch('launch_new_report', window.top.openWAQR)\"><img src=\"images/new_report.png\" border=\"0\"></a></td>\r\n");
      out.write("        ");
 } 
      out.write("\r\n");
      out.write("        ");
 if (hasAnalyzer) { 
      out.write("\r\n");
      out.write("\t\t");
 if (hasIteractiveReporting) { 
      out.write("\r\n");
      out.write("        \t<td valign=\"bottom\" width=\"3\" class=\"largeGraphicSpacer\"><img src=\"images/clr.gif\" width=\"3\" height=\"11\"></td>\r\n");
      out.write("\t\t");
 } 
      out.write("\r\n");
      out.write("       \t<td align=\"center\" width=\"226\" valign=\"bottom\" class=\"largeGraphicButton\"><img src=\"images/clr.gif\" width=\"226\" height=\"10\"><br><a href=\"#\" onClick=\"launch('launch_new_analysis', window.top.openAnalysis)\"><img src=\"images/new_analysis.png\" border=\"0\"></a></td>\r\n");
      out.write("        ");
 } 
      out.write('\r');
      out.write('\n');
      out.write('	');
 if (hasDashboards) { 
      out.write("\r\n");
      out.write("\t\t");
 if (hasIteractiveReporting || hasAnalyzer) { 
      out.write("\r\n");
      out.write("\t\t       \t<td valign=\"bottom\" width=\"3\" class=\"largeGraphicSpacer\"><img src=\"images/clr.gif\" width=\"3\" height=\"11\"></td>\r\n");
      out.write("\t\t");
 } 
      out.write("\r\n");
      out.write("        <td align=\"center\" width=\"226\" valign=\"bottom\" class=\"largeGraphicButton\"><img src=\"images/clr.gif\" width=\"226\" height=\"10\"><br><a href=\"#\" onClick=\"launch_newDashboard()\"><img src=\"images/new_dash.png\" border=\"0\"></a></td>\r\n");
      out.write("\t");
 } 
      out.write("\r\n");
      out.write("      </tr>\r\n");
      out.write("      <tr>\r\n");
      out.write("        ");
 if (hasIteractiveReporting) { 
      out.write("\r\n");
      out.write("        <td align=\"center\" class=\"smallButton\"><button class=\"pentaho-button\" onClick=\"launch('launch_new_report', window.top.openWAQR)\">New Report</button></td>\r\n");
      out.write("        ");
 } 
      out.write("\r\n");
      out.write("        ");
 if (hasAnalyzer) { 
      out.write("\r\n");
      out.write("\t\t");
 if (hasIteractiveReporting) { 
      out.write("\r\n");
      out.write("\t\t        <td class=\"largeGraphicSpacer\"><img src=\"images/clr.gif\" width=\"3\" height=\"4\"></td>\r\n");
      out.write("\t\t");
 } 
      out.write("\r\n");
      out.write("        <td align=\"center\" class=\"smallButton\"><button class=\"pentaho-button\" onClick=\"launch('launch_new_analysis', window.top.openAnalysis)\">New Analysis</button></td>\r\n");
      out.write("        ");
 } 
      out.write('\r');
      out.write('\n');
      out.write('	');
 if (hasDashboards) { 
      out.write("\r\n");
      out.write("                ");
 if (hasIteractiveReporting || hasAnalyzer) { 
      out.write("\r\n");
      out.write("        \t\t<td class=\"largeGraphicSpacer\"><img src=\"images/clr.gif\" width=\"3\" height=\"4\"></td>\r\n");
      out.write("\t\t");
 } 
      out.write("\r\n");
      out.write("\t        <td align=\"center\" class=\"smallButton\"><button class=\"pentaho-button\" onClick=\"launch_newDashboard()\">New Dashboard</button></td>\r\n");
      out.write("\t");
 } 
      out.write("\r\n");
      out.write("      </tr>\r\n");
      out.write("\t </table>");

     }
	 
      out.write("\r\n");
      out.write("\t <table id=\"datasourcePanel\" style=\"display:none\" width=\"684\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n");
      out.write("      <tr>\r\n");
      out.write("        <td colspan=\"3\"><img src=\"../themes/onyx/images/seperator_horz.png\" width=\"684\" height=\"3\"></td>\r\n");
      out.write("      </tr>\r\n");
      out.write("      <tr>\r\n");
      out.write("        <td class=\"newDsPanel\" width=\"227px\"><img src=\"images/new_ds.png\"></td>\r\n");
      out.write("        <td valign=\"top\" class=\"launcher-bottom-text\">Data Sources:<br>Create data sources from a csv or database and define metadata to simplify content creation.</td>\r\n");
      out.write("        <td width=\"227\" valign=\"top\">\r\n");
      out.write("          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n");
      out.write("            <tr>      \r\n");
      out.write("             <td class=\"bottomButtonWrapper\" align=\"center\"><button class=\"pentaho-button\" id=\"button0\" style=\"width: 116px\" onClick=\"launch_newDatasource()\">Create New</button></td>\r\n");
      out.write("            </tr>\r\n");
      out.write("            <tr>\r\n");
      out.write("              <td class=\"bottomButtonWrapper\" align=\"center\"><button class=\"pentaho-button\" id=\"button0\" style=\"width: 116px\" onClick=\"launch_managesDatasources()\">Manage Existing</button></td> \r\n");
      out.write("            </tr>\r\n");
      out.write("          </table>\r\n");
      out.write("        </td>\r\n");
      out.write("      </tr>\r\n");
      out.write("    </table>\r\n");
      out.write("  </div>\r\n");
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
