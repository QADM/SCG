package org.apache.jsp.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import org.springframework.security.ui.AbstractProcessingFilter;
import org.springframework.security.ui.webapp.AuthenticationProcessingFilter;
import org.springframework.security.ui.savedrequest.SavedRequest;
import org.springframework.security.AuthenticationException;
import org.pentaho.platform.uifoundation.component.HtmlComponent;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.util.messages.LocaleHelper;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.web.http.WebTemplateHelper;
import org.pentaho.platform.api.engine.IUITemplater;
import org.pentaho.platform.api.engine.IPluginManager;
import org.pentaho.platform.web.jsp.messages.Messages;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.apache.commons.lang.StringEscapeUtils;
import org.pentaho.platform.web.http.PentahoHttpSessionHelper;
import org.owasp.esapi.ESAPI;

public final class PUCLogin_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {


    // List of request URL strings to look for to send 401

    private List<String> send401RequestList;
  
    public void jspInit() {
      // super.jspInit(); 
      send401RequestList = new ArrayList<String>();
      String unauthList = getServletConfig().getInitParameter("send401List"); //$NON-NLS-1$
      if (unauthList == null) {
        send401RequestList.add("AdhocWebService"); //$NON-NLS-1$
      } else {
        StringTokenizer st = new StringTokenizer(unauthList, ","); //$NON-NLS-1$
        String requestStr;
        while (st.hasMoreElements()) {
          requestStr = st.nextToken();
          send401RequestList.add(requestStr.trim());
        }
      }
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
      out.write('\r');
      out.write('\n');

    response.setCharacterEncoding(LocaleHelper.getSystemEncoding());
    String path = request.getContextPath();

    IPentahoSession userSession = PentahoHttpSessionHelper.getPentahoSession( request );
    // SPRING_SECURITY_SAVED_REQUEST_KEY contains the URL the user originally wanted before being redirected to the login page
    // if the requested url is in the list of URLs specified in the web.xml's init-param send401List,
    // then return a 401 status now and don't show a login page (401 means not authenticated)
  Object reqObj = request.getSession().getAttribute(AbstractProcessingFilter.SPRING_SECURITY_SAVED_REQUEST_KEY);
  String requestedURL = "";
  if (reqObj != null) {
    requestedURL = ((SavedRequest) reqObj).getFullRequestUrl();
    
    String lookFor;
      for (int i=0; i<send401RequestList.size(); i++) {
        lookFor = send401RequestList.get(i);
        if ( requestedURL.indexOf(lookFor) >=0 ) {
          response.sendError(401);
          return;
        }
      }
  }

  
boolean loggedIn = request.getRemoteUser() != null && request.getRemoteUser() != "";
int year = (new java.util.Date()).getYear() + 1900;

boolean showUsers = Boolean.parseBoolean(PentahoSystem.getSystemSetting("login-show-sample-users-hint", "true"));


      out.write("\r\n");
      out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\r\n");
      out.write("<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n");
      out.write("<head>\r\n");
      out.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\r\n");
      out.write("<title>Pentaho User Console - Login</title>\r\n");
      out.write("\r\n");
      out.write("\t");

	  String ua = request.getHeader("User-Agent").toLowerCase();
	  if (!"desktop".equalsIgnoreCase(request.getParameter("mode"))) {		
		if (ua.contains("ipad") || ua.contains("ipod") || ua.contains("iphone") || ua.contains("android") || "mobile".equalsIgnoreCase(request.getParameter("mode"))) {		
		  IPluginManager pluginManager = PentahoSystem.get(IPluginManager.class, PentahoHttpSessionHelper.getPentahoSession(request)); 
		  List<String> pluginIds = pluginManager.getRegisteredPlugins();
		  for (String id : pluginIds) {
		    String mobileRedirect = (String)pluginManager.getPluginSetting(id, "mobile-redirect", null);
		    if (mobileRedirect != null) {
		      // we have a mobile redirect
			  
      out.write("\r\n");
      out.write("\t\t\t  <script type=\"text/javascript\">\r\n");
      out.write("          if(typeof window.top.PentahoMobile != \"undefined\"){\r\n");
      out.write("            window.top.location.reload();\r\n");
      out.write("          } else {\r\n");
      out.write("            document.write('<META HTTP-EQUIV=\"refresh\" CONTENT=\"0;URL=");
      out.print(mobileRedirect);
      out.write("\">');\r\n");
      out.write("          }\r\n");
      out.write("        </script>\r\n");
      out.write("\t\t\t  </head>\r\n");
      out.write("\t\t\t  <BODY>\r\n");
      out.write("\t\t\t\t\t<!-- this div is here for authentication detection (used by mobile, PIR, etc) -->\r\n");
      out.write("\t\t\t\t\t<div style=\"display:none\">j_spring_security_check</div>\r\n");
      out.write("\t\t\t  </BODY>\r\n");
      out.write("\t\t\t  </HTML>\r\n");
      out.write("\t\t\t  ");
			  
			  return;
		    }
		  }
		}
	  }
	
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("<style type=\"text/css\">\r\n");
      out.write("<!--\r\n");
      out.write("html, body {\r\n");
      out.write("  margin:0;\r\n");
      out.write("  padding:0;\r\n");
      out.write("  height:100%;\r\n");
      out.write("  border:none\r\n");
      out.write("}\r\n");
      out.write("#container_header {\r\n");
      out.write("  margin: 0 auto;\r\n");
      out.write("  padding: 0;\r\n");
      out.write("  width:740px;\r\n");
      out.write("  height: 94px;\r\n");
      out.write("  display: block;\r\n");
      out.write("}\r\n");
      out.write("#links{\r\n");
      out.write("Float: right;\r\n");
      out.write("clear: both;\r\n");
      out.write("color: #828282;\r\n");
      out.write("padding: 8px 0 0 0;\r\n");
      out.write("}\r\n");
      out.write("#links a{\r\n");
      out.write("  color: #999;\r\n");
      out.write("  text-decoration: none;\r\n");
      out.write("  font-size: .8em;\r\n");
      out.write("}\r\n");
      out.write("#container_content {\r\n");
      out.write("  margin: 0 auto;\r\n");
      out.write("  padding: 0;\r\n");
      out.write("  width:740px;\r\n");
      out.write("  height: 335px;\r\n");
      out.write("  font-family: Tahoma, Arial, sans-serif;\r\n");
      out.write("  display: block;\r\n");
      out.write("  background-image: url(/pentaho-style/images/login/middle_shadows.png);\r\n");
      out.write("  background-repeat:no-repeat;\r\n");
      out.write("}\r\n");
      out.write("#container_footer {\r\n");
      out.write("  margin: 0 auto;\r\n");
      out.write("  padding: 0;\r\n");
      out.write("  width:740px;\r\n");
      out.write("  height: 100%;\r\n");
      out.write("  color: #000;\r\n");
      out.write("  font-size: .75em;\r\n");
      out.write("  /* padding: 8px 0 0 80px;*/\r\n");
      out.write("  display: block;\r\n");
      out.write("  background-image: url(/pentaho-style/images/login/middle_shadows_footer.png);\r\n");
      out.write("  background-repeat:no-repeat;\r\n");
      out.write("}\r\n");
      out.write("#message {\r\n");
      out.write("  color: #FFF;\r\n");
      out.write("  font-size: 1.05em;\r\n");
      out.write("  font-family: Tahoma, Arial, sans-serif;\r\n");
      out.write("  float: left;\r\n");
      out.write("  clear: both;\r\n");
      out.write("  display: block;\r\n");
      out.write("  width: 260px;\r\n");
      out.write("  padding: 20px 10px 0 40px;\r\n");
      out.write("  line-height: 1.85em;\r\n");
      out.write("}\r\n");
      out.write(".dark {\r\n");
      out.write("  background-image: url(/pentaho-style/images/login/content_bg.png);\r\n");
      out.write("  background-position:bottom;\r\n");
      out.write("  background-repeat:repeat-x;\r\n");
      out.write("  height: 225px;\r\n");
      out.write("}\r\n");
      out.write("a {\r\n");
      out.write("  color: #e17b03\r\n");
      out.write("}\r\n");
      out.write(".IE .pentaho-rounded-panel {\r\n");
      out.write("  border: 1px solid #ccc;\r\n");
      out.write("}\r\n");
      out.write("-->\r\n");
      out.write("</style>\r\n");
      out.write("<meta name=\"gwt:property\" content=\"locale=");
      out.print(ESAPI.encoder().encodeForHTMLAttribute(request.getLocale().toString()));
      out.write("\">\r\n");
      out.write("<link rel=\"shortcut icon\" href=\"/pentaho-style/favicon.ico\" />\r\n");
      out.write("<script language=\"javascript\" type=\"text/javascript\" src=\"webcontext.js\"></script>\r\n");
      out.write("</head>\r\n");
      out.write("\r\n");
      out.write("<body class=\"pentaho-page-background\">\r\n");
      out.write("<div id=\"loginError\" class=\"pentaho-dialog\" style=\"width: 400px; display: none\">\r\n");
      out.write("  <div class=\"Caption\">\r\n");
      out.write("    <span>Login Error</span>\r\n");
      out.write("  </div>\r\n");
      out.write("  <div style=\"width: auto; height: auto;\">\r\n");
      out.write("    <table class=\"dialog-content pentaho-padding-sm\" style=\"width: 100%;\">\r\n");
      out.write("      <tbody>\r\n");
      out.write("        <tr>\r\n");
      out.write("          <td>\r\n");
      out.write("            <span class=\"label\">");
      out.print(Messages.getString("UI.PUC.LOGIN.ERROR"));
      out.write("</span>\r\n");
      out.write("          </td>\r\n");
      out.write("        </tr>\r\n");
      out.write("      </tbody>\r\n");
      out.write("    </table>\r\n");
      out.write("  </div>\r\n");
      out.write("  <table class=\"button-panel\" style=\"width: 100%;\">\r\n");
      out.write("    <tbody>\r\n");
      out.write("      <tr>\r\n");
      out.write("        <td style=\"width: 100%;\"> </td>\r\n");
      out.write("        <td>\r\n");
      out.write("           <button class=\"pentaho-button\" onclick=\"document.getElementById('loginError').style.display='none'\">");
      out.print(Messages.getString("UI.PUC.LOGIN.OK"));
      out.write("</button>\r\n");
      out.write("        </td>\r\n");
      out.write("      </tr>\r\n");
      out.write("    </tbody>\r\n");
      out.write("  </table>\r\n");
      out.write("</div>\r\n");
      out.write("<table width=\"100%\" height=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n");
      out.write("  <tr height=\"94\">\r\n");
      out.write("    <td bgcolor=\"#FFFFFF\"><div id=\"container_header\"><div id=\"links\"><a href=\"http://www.pentaho.com\" target=\"_blank\">www.pentaho.com</a> | <a href=\"http://www.pentaho.com/contact/?puc=y\" target=\"_blank\">");
      out.print(Messages.getString("UI.PUC.LOGIN.CONTACT_US"));
      out.write("</a></div>\r\n");
      out.write("        <div class=\"pentaho-rounded-panel\" style=\"width: 323px; padding: 20px 20px 20px 20px; position: absolute; margin: 199px 0 0 380px;\">\r\n");
      out.write("          <table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n");
      out.write("            <form name=\"login\" id=\"login\" action=\"j_spring_security_check\" method=\"POST\">\r\n");
      out.write("              <tr>\r\n");
      out.write("                <td colspan=\"1\" rowspan=\"7\" style=\"padding: 20px 20px 0 0;\"><img src=\"/pentaho-style/images/login/lock.png\" width=\"100\" height=\"172\"></td>\r\n");
      out.write("                <td colspan=\"2\" ><span style=\"color: #FFF; font-size: 1.7em; font-family: &quot;Franklin Gothic Demi&quot;, Tahoma, Arial; \">");
      out.print(Messages.getString("UI.PUC.LOGIN.LOGIN"));
      out.write("</span></td>\r\n");
      out.write("              </tr>\r\n");
      out.write("              <tr>\r\n");
      out.write("                <td colspan=\"2\" style=\"padding: 10px 0 4px 0;\"><select style=\"display:none;\" id=\"locale\" name=\"locale\">\r\n");
      out.write("                    <option value=\"de\">German</option>\r\n");
      out.write("                    <option value=\"en\" selected=\"selected\">English</option>\r\n");
      out.write("                    <option value=\"es\">Spanish</option>\r\n");
      out.write("                    <option value=\"fr\">French</option>\r\n");
      out.write("                    <option value=\"ja\">Japanese</option>\r\n");
      out.write("                  </select>\r\n");
      out.write("                <label style=\"color: #FFF; font-size:.85em; font-family: Tahoma, Arial, sans-serif; text-shadow: 0px 1px 1px #000;\" for=\"userid\">");
      out.print(Messages.getString("UI.PUC.LOGIN.USERNAME"));
      out.write("</label></td>\r\n");
      out.write("              </tr>\r\n");
      out.write("              <tr>\r\n");
      out.write("                <td colspan=\"2\">\r\n");
      out.write("          <input  style=\"border:1px solid #333; padding: 4px; width:190px;height:17px;\" id=\"j_username\" name=\"j_username\" type=\"text\" value=\"admin\"/>\r\n");
      out.write("                </td>\r\n");
      out.write("              </tr>\r\n");
      out.write("              <tr>\r\n");
      out.write("                <td colspan=\"2\" style=\"padding: 5px 0 4px 0;\">\r\n");
      out.write("          <label style=\"padding: 15px 0 2px 0; color: #FFF; font-size:.85em; font-family: Tahoma, Arial, sans-serif; text-shadow: 0px 1px 1px #000;\" for=\"password\">");
      out.print(Messages.getString("UI.PUC.LOGIN.PASSWORD"));
      out.write("</label>\r\n");
      out.write("        </td>\r\n");
      out.write("              </tr>\r\n");
      out.write("              <tr>\r\n");
      out.write("                <td colspan=\"2\">\r\n");
      out.write("          <input style=\"border:1px solid #333; padding: 4px; width:190px;height:17px;\" id=\"j_password\" name=\"j_password\" type=\"password\" value=\"admin\"/>\r\n");
      out.write("        </td>\r\n");
      out.write("              </tr>\r\n");
      out.write("              <tr>\r\n");
      out.write("                <td colspan=\"2\" align=\"left\" style=\"padding:5px 0 2px 0px;\">\r\n");
      out.write("          <input id=\"launchInNewWindow\" name=\"Launch in new window\" type=\"checkbox\" value=\"\" />\r\n");
      out.write("          <span style=\"padding:0px 0 2px 0px; color:#fff; font-size:.8em; font-family: Tahoma, Arial, sans-serif;\">");
      out.print(Messages.getString("UI.PUC.LOGIN.NEW_WINDOW"));
      out.write("</span>\r\n");
      out.write("        </td>\r\n");
      out.write("              </tr>\r\n");
      out.write("              <tr>\r\n");
      out.write("                <td style=\"padding:4px 0 0 0px;\">\r\n");
      out.write("                ");

                if (showUsers) {
                
      out.write("\r\n");
      out.write("          <img src=\"/pentaho-style/images/login/about.png\" width=\"18\" height=\"16\" align=\"absmiddle\"/>\r\n");
      out.write("          <a style=\"color: #fff; padding: 0 4px 0px 4px; font-size: .8em;\" href=\"#\" onClick=\"toggleEvalPanel()\">");
      out.print(Messages.getString("UI.PUC.LOGIN.EVAL_LOGIN"));
      out.write("</a>\r\n");
      out.write("        ");

        } else {
        
      out.write("\r\n");
      out.write("          &nbsp;\r\n");
      out.write("        ");

        }
        
      out.write("\r\n");
      out.write("                </td>       \r\n");
      out.write("                <td style=\"padding:4px 0 0 0px;\">\r\n");
      out.write("                  <input class=\"pentaho-button\" value=\"");
      out.print(Messages.getString("UI.PUC.LOGIN.LOGIN"));
      out.write("\" type=\"submit\" style=\"float:right; clear: both;\"/>\r\n");
      out.write("                </td>\r\n");
      out.write("              </tr>\r\n");
      out.write("              ");

                if (showUsers) {
              
      out.write("\r\n");
      out.write("              <tr>\r\n");
      out.write("                <td id=\"evaluationPanel\" colspan=\"3\" style=\"padding: 30px 20px 0 0; display: none;\">\r\n");
      out.write("          <table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n");
      out.write("                    <tr>\r\n");
      out.write("                      <td style=\"font-size: .8em;\"><strong>");
      out.print(Messages.getString("UI.PUC.LOGIN.ADMIN_USER"));
      out.write("</strong><br>\r\n");
      out.write("                        ");
      out.print(Messages.getString("UI.PUC.LOGIN.USERNAME"));
      out.write(" joe<br>\r\n");
      out.write("                        ");
      out.print(Messages.getString("UI.PUC.LOGIN.PASSWORD"));
      out.write(" password</td>\r\n");
      out.write("                      <td style=\"font-size: .8em;\"><strong>");
      out.print(Messages.getString("UI.PUC.LOGIN.BUSINESS_USER"));
      out.write("</strong><br>\r\n");
      out.write("                        ");
      out.print(Messages.getString("UI.PUC.LOGIN.USERNAME"));
      out.write(" suzy<br>\r\n");
      out.write("                        ");
      out.print(Messages.getString("UI.PUC.LOGIN.PASSWORD"));
      out.write(" password</td>\r\n");
      out.write("                    </tr>\r\n");
      out.write("                    <tr>\r\n");
      out.write("                    <td colspan=\"3\" style=\"padding: 4px 20px 0 0; font-size: .8em;\"><a href=\"http://www.pentaho.com/helpmeout/\" target=\"_blank\">");
      out.print(Messages.getString("UI.PUC.LOGIN.REQUEST_SUPPORT"));
      out.write("</a><img src=\"/pentaho-style/images/login/help_link.png\" width=\"20\" height=\"20\" align=\"absbottom\"></td>\r\n");
      out.write("                    </tr>\r\n");
      out.write("                  </table>\r\n");
      out.write("        </td>\r\n");
      out.write("              </tr>\r\n");
      out.write("              ");

        }
        
      out.write("\r\n");
      out.write("            </form>\r\n");
      out.write("          </table>\r\n");
      out.write("        </div>\r\n");
      out.write("        <a href=\"http://www.pentaho.com\" target=\"_blank\"><img src=\"/pentaho-style/images/login/logo.png\" alt=\"Pentaho Corporation\" width=\"224\" height=\"94\" border=\"0\" /></a></div></td>\r\n");
      out.write("  </tr>\r\n");
      out.write("  <tr height=\"334\">\r\n");
      out.write("    <td class=\"dark\"><div id=\"container_content\"><img src=\"/pentaho-style/images/login/title_text.png\">\r\n");
      out.write("        <div id=\"message\">");
      out.print(Messages.getString("UI.PUC.LOGIN.MESSAGE"));
      out.write("</div>\r\n");
      out.write("      </div></td>\r\n");
      out.write("  </tr>\r\n");
      out.write("  <tr height=\"100%\">\r\n");
      out.write("    <td bgcolor=\"#FFFFFF\" valign=\"top\"><div id=\"container_footer\" style=\"padding: 4px 20px 0 80px; height:200px;\">");
      out.print(Messages.getString("UI.PUC.LOGIN.COPYRIGHT", String.valueOf(year)));
      out.write("</div></td>\r\n");
      out.write("  </tr>\r\n");
      out.write("</table>\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("<script type=\"text/javascript\">\r\n");
      out.write("\r\n");
      out.write("function DisplayAlert(id,left,top) {\r\n");
      out.write("  document.getElementById(id).style.left=left+'%';\r\n");
      out.write("  document.getElementById(id).style.top=top+'%';\r\n");
      out.write("  document.getElementById(id).style.display='block';\r\n");
      out.write("}\r\n");
      out.write("\r\n");
      out.write("document.getElementById('j_username').focus();\r\n");
      out.write("\r\n");

if (showUsers) {

      out.write("\r\n");
      out.write("\r\n");
      out.write("function toggleEvalPanel() {\r\n");
      out.write("  var evaluationPanel = document.getElementById(\"evaluationPanel\");\r\n");
      out.write("  var display = evaluationPanel.style.display;\r\n");
      out.write("  if (display == \"none\") {\r\n");
      out.write("    evaluationPanel.style.display = \"\";\r\n");
      out.write("  } else {\r\n");
      out.write("    evaluationPanel.style.display = \"none\";\r\n");
      out.write("  }\r\n");
      out.write("}\r\n");

}

      out.write("\r\n");
      out.write("\r\n");
      out.write("function bounceToReturnLocation() {\r\n");
      out.write("  // pass\r\n");
      out.write("  var locale = document.login.locale.options[document.login.locale.selectedIndex].value;\r\n");
      out.write("  \r\n");
      out.write("  var returnLocation = '");
      out.print(ESAPI.encoder().encodeForJavaScript(requestedURL));
      out.write("';\r\n");
      out.write("\r\n");
      out.write("  if(/(iPad|iPod|iPhone)/.test(navigator.userAgent) || window.orientation !== undefined){\r\n");
      out.write("    returnLocation = CONTEXT_PATH+\"content/analyzer/selectSchema\";\r\n");
      out.write("  }\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("  if (document.getElementById(\"launchInNewWindow\").checked) {\r\n");
      out.write("    if (returnLocation != '' && returnLocation != null) {\r\n");
      out.write("      window.open(returnLocation, '_blank', 'menubar=no,location=no,resizable=yes,scrollbars=yes,status=no');\r\n");
      out.write("    } else {\r\n");
      out.write("      window.open(window.location.href.replace(\"Login\", \"Home\") + \"?locale=\" + locale, '_blank', 'menubar=no,location=no,resizable=yes,scrollbars=yes,status=no');\r\n");
      out.write("    }\r\n");
      out.write("  } else {\r\n");
      out.write("    if (returnLocation != '' && returnLocation != null) {\r\n");
      out.write("        window.location.href = returnLocation;\r\n");
      out.write("    } else {\r\n");
      out.write("        window.location.href = window.location.href.replace(\"Login\", \"Home\") + \"?locale=\" + locale;\r\n");
      out.write("    }\r\n");
      out.write("  }\r\n");
      out.write("}\r\n");
      out.write("\r\n");
      out.write("function doLogin() {\r\n");
      out.write("  \r\n");
      out.write("\t// if we have a valid session and we attempt to login on top of it, the server\r\n");
      out.write("\t// will actually log us out and will not log in with the supplied credentials, you must\r\n");
      out.write("\t// login again. So instead, if they're already logged in, we bounce out of here to\r\n");
      out.write("\t// prevent confusion.\r\n");
      out.write("    if (");
      out.print(loggedIn);
      out.write(") {\r\n");
      out.write("      bounceToReturnLocation();\r\n");
      out.write("      return false;\r\n");
      out.write("    }\r\n");
      out.write("  \r\n");
      out.write("    jQuery.ajax({\r\n");
      out.write("        type: \"POST\",\r\n");
      out.write("        url: \"j_spring_security_check\",\r\n");
      out.write("        data: $(\"#login\").serialize(),\r\n");
      out.write("\r\n");
      out.write("        error:function (xhr, ajaxOptions, thrownError){\r\n");
      out.write("      if (xhr.status == 404) {\r\n");
      out.write("        // if we get a 404 it means login was successful but intended resource does not exist\r\n");
      out.write("        // just let it go - let the user get the 404\r\n");
      out.write("        bounceToReturnLocation();\r\n");
      out.write("        return;\r\n");
      out.write("      }\r\n");
      out.write("      //Fix for BISERVER-7525\r\n");
      out.write("      //parsereerror caused by attempting to serve a complex document like a prd report in any presentation format like a .ppt\r\n");
      out.write("      //does not necesarly mean that there was a failure in the login process, status is 200 so just let it serve the archive to the web browser.\r\n");
      out.write("      if (xhr.status == 200 && thrownError == 'parsererror') {\r\n");
      out.write("         document.getElementById(\"j_password\").value = \"\";\r\n");
      out.write("         bounceToReturnLocation();\r\n");
      out.write("\t     return;\r\n");
      out.write("       }\r\n");
      out.write("       // fail\r\n");
      out.write("       DisplayAlert('loginError', 40, 30);\r\n");
      out.write("      },\r\n");
      out.write("            \r\n");
      out.write("        success:function(data, textStatus, jqXHR){\r\n");
      out.write("      if (data.indexOf(\"j_spring_security_check\") != -1) {\r\n");
      out.write("        // fail\r\n");
      out.write("\t    DisplayAlert('loginError', 40, 30);\r\n");
      out.write("\t    return false;\r\n");
      out.write("      } else {\r\n");
      out.write("        document.getElementById(\"j_password\").value = \"\";\r\n");
      out.write("        bounceToReturnLocation();\r\n");
      out.write("      }\r\n");
      out.write("        }\r\n");
      out.write("        \r\n");
      out.write("    });\r\n");
      out.write("    return false;\r\n");
      out.write("}\r\n");
      out.write("\r\n");
      out.write("$(document).ready(function(){\r\n");
      out.write("    $(\"#login\").submit(doLogin);\r\n");
      out.write("\r\n");
      out.write("  if (");
      out.print(loggedIn);
      out.write(") {\r\n");
      out.write("    bounceToReturnLocation();\r\n");
      out.write("  }\r\n");
      out.write("});\r\n");
      out.write("</script>\r\n");
      out.write("<script>\r\n");
      out.write("    var frm = document.getElementById('login');\r\n");
      out.write("    if (frm) {\r\n");
      out.write("        frm.submit();\r\n");
      out.write("    }\r\n");
      out.write("</script>\r\n");
      out.write("</body>\r\n");
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
