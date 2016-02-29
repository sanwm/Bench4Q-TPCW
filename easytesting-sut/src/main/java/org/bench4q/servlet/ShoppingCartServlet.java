package org.bench4q.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.UUID;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import easy.testing.sut.service.ShoppingCartService;

public class ShoppingCartServlet extends HttpServlet {

	private static final long serialVersionUID = -7152962324853746158L;
	private static Logger LOGGER = LoggerFactory.getLogger(ShoppingCartServlet.class);

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		UUID uuid = UUID.randomUUID();
		Date before = new Date(System.currentTimeMillis());
		WebApplicationContext webApplicationContext = WebApplicationContextUtils
				.getWebApplicationContext(this.getServletContext());
		ShoppingCartService shoppingCartService = webApplicationContext.getBean(ShoppingCartService.class);

		Cart cart;
		String url;

		Util.determinePriorityLevel(req);

		PrintWriter out = res.getWriter();

		// Set the content type of this servlet's result.
		res.setContentType("text/html");
		String C_IDstr = req.getParameter("C_ID");

		String SHOPPING_IDstr = req.getParameter("SHOPPING_ID");
		int SHOPPING_ID;
		if (SHOPPING_IDstr == null) {
			Date databaseBefore = new Date(System.currentTimeMillis());
			SHOPPING_ID = shoppingCartService.newShoppingCart().getId();
			Date databaseAfter = new Date(System.currentTimeMillis());
			LOGGER.debug("ShoppingCartServlet - " + uuid.toString() + " - Database - "
					+ (databaseAfter.getTime() - databaseBefore.getTime()) + " ms");

		} else {
			SHOPPING_ID = Integer.parseInt(SHOPPING_IDstr);
		}

		String add_flag = req.getParameter("ADD_FLAG");
		Integer I_ID;

		if (add_flag.equals("Y")) {
			String I_IDstr = req.getParameter("I_ID");
			if (I_IDstr == null) {
				System.out.println("ERROR IN SHOPPING CART, add_flag==Y!");
				out.print("Error- need to specify an I_ID!</BODY></HTML>\n");
				return;
			}
			I_ID = new Integer(Integer.parseInt(I_IDstr));
		} else {
			I_ID = null;
		}

		// We need to parse an arbitrary number of I_ID/QTR pairs from
		// the url line.
		Vector<String> quantities = new Vector<String>();
		Vector<String> ids = new Vector<String>();
		int i = 0;
		String curr_QTYstr;
		String curr_I_IDstr;

		curr_QTYstr = req.getParameter("QTY_" + i);
		curr_I_IDstr = req.getParameter("I_ID_" + i);
		while (curr_I_IDstr != null) {
			ids.addElement(curr_I_IDstr);
			quantities.addElement(curr_QTYstr);
			i++;
			curr_QTYstr = req.getParameter("QTY_" + i);
			curr_I_IDstr = req.getParameter("I_ID_" + i);
		}
		Date databaseBefore = new Date(System.currentTimeMillis());
		cart = Database.doCart(SHOPPING_ID, I_ID, ids, quantities);
		Date databaseAfter = new Date(System.currentTimeMillis());
		LOGGER.debug("ShoppingCartServlet - " + uuid.toString() + " - Database - "
				+ (databaseAfter.getTime() - databaseBefore.getTime()) + " ms");

		// Add the top part of the HTML

		out.print("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD W3 HTML//EN\">\n");
		out.print("<HTML><!--Shopping Cart--> <HEAD><TITLE>Shopping Cart</TITLE></HEAD> \n");
		out.print("<BODY BGCOLOR=\"#ffffff\">\n");
		out.print("<H1 ALIGN=\"center\">Bench4Q</H1>\n");
		out.print("<H1 ALIGN=\"center\">A QoS oriented B2C benchmark for Internetware Middleware</H1>\n");
		out.print("<H2 ALIGN=\"center\">Shopping Cart Page</H2>\n");

		// Print out the promotional processing stuff
		PromotionalProcessing.displayPromotions(this.getClass(), uuid, this.getServletContext(), out, req, res,
				SHOPPING_ID);

		String sessionIdStrToAppend = Util.appendSessionId(req);

		// Display the shopping cart contents
		// by xiaowei zhou, change "$sessionid$" to "jsessionid=", 2010.11.4
		out.print("<FORM ACTION=\"shopping_cart" + sessionIdStrToAppend + "\" METHOD=\"get\">\n");

		out.print("<CENTER><P></P><TABLE BORDER=\"0\">\n");
		out.print("<TR><TD><B>Qty</B></TD><TD><B>Product</B></TD></TR>\n");

		// Print out the entries in the shopping cart
		for (i = 0; i < cart.lines.size(); i++) {
			CartLine line = (CartLine) cart.lines.elementAt(i);
			out.print("<TR><TD VALIGN=\"top\">\n");
			out.print("<INPUT TYPE=HIDDEN NAME=\"I_ID_" + i + "\" value = \"" + line.scl_i_id + "\">\n");
			out.print("<INPUT NAME=\"QTY_" + i + "\" SIZE=\"3\" VALUE=\"" + line.scl_qty + "\"></TD>\n");
			out.print(
					"<TD VALIGN=\"top\">Title:<I>" + line.scl_title + "</I> - Backing: " + line.scl_backing + "<BR>\n");
			out.print("SRP. $" + line.scl_srp + "</B>\n");
			out.print("<FONT COLOR=\"#aa0000\"><B>Your Price: $" + line.scl_cost + "</B></FONT></TD></TR>\n");
		}

		out.print("</TABLE><B><I>Subtotal price: " + cart.SC_SUB_TOTAL + "</I></B>\n");
		url = "customer_registration?SHOPPING_ID=" + SHOPPING_ID;
		if (C_IDstr != null)
			url = url + "&C_ID=" + C_IDstr;
		out.print("<P><BR><A HREF=\"" + res.encodeURL(url));
		out.print("\"><IMG SRC=\"Images/checkout_B.gif\"></A>\n");

		url = "home?SHOPPING_ID=" + SHOPPING_ID;
		if (C_IDstr != null)
			url = url + "&C_ID=" + C_IDstr;
		out.print("<A HREF=\"" + res.encodeURL(url));

		out.print("\"><IMG SRC=\"Images/home_B.gif\"></P></A>\n");
		out.print("<P>If you have changed the quantities and/or taken "
				+ "anything out<BR> of your shopping cart, click here to " + "refresh your shopping cart:</P> ");
		out.print("<INPUT TYPE=HIDDEN NAME=\"ADD_FLAG\" value = \"N\">\n");
		out.print("<INPUT TYPE=HIDDEN NAME=\"SHOPPING_ID\" value = \"" + SHOPPING_ID + "\">\n");
		if (C_IDstr != null)
			out.print("<INPUT TYPE=HIDDEN NAME=\"C_ID\" value = \"" + C_IDstr + "\">\n");

		out.print("<P><INPUT TYPE=\"IMAGE\" NAME=\"Refresh Shopping Cart\"" + "SRC=\"Images/refresh_B.gif\"></P>\n");
		out.print("</CENTER></FORM></BODY></HTML>");
		out.close();

		Date after = new Date(System.currentTimeMillis());
		LOGGER.debug("ShoppingCartServlet - " + uuid.toString() + " - Total - " + (after.getTime() - before.getTime())
				+ " ms");
	}

}
