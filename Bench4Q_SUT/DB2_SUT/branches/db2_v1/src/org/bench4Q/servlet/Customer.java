/**
 * 
 * Create at:   2009-5-14
 * @version:    1.0 
 *
 * 
 * Copyright Technology Center for Software Engineering,Institute of Software, Chinese Academy of Sciences 
 * and distributed according to the GNU Lesser General Public Licence. 
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by   
 * the Free Software Foundation; either version 2.1 of the License, or any
 * later version.
 * See Copyright.txt for full copyright information.
 *
 *
 *  http://otc.iscas.ac.cn/
 * Technology Center for Software Engineering
 * Institute of Software, Chinese Academy of Sciences
 * Beijing 100190, China
 *
 * This version is a based on the implementation from University of Wisconsin
 *
 *
 *  * Initial developer(s): Zhiquan Duan, Wei Wang.
 * 
 */
package org.bench4Q.servlet;

import java.sql.ResultSet;
import java.util.Date;

// glorified struct used for passing customer info around.
public class Customer {

	public int c_id;
	public String c_uname;
	public String c_passwd;
	public String c_fname;
	public String c_lname;
	public String c_phone;
	public String c_email;
	public Date c_since;
	public Date c_last_visit;
	public Date c_login;
	public Date c_expiration;
	public double c_discount;
	public double c_balance;
	public double c_ytd_pmt;
	public Date c_birthdate;
	public String c_data;

	// From the addess table
	public int addr_id;
	public String addr_street1;
	public String addr_street2;
	public String addr_city;
	public String addr_state;
	public String addr_zip;
	public int addr_co_id;

	// From the country table
	public String co_name;

	public Customer() {
	}

	public Customer(ResultSet rs) {
		// The result set should have all of the fields we expect.
		// This relies on using field name access. It might be a bad
		// way to break this up since it does not allow us to use the
		// more efficient select by index access method. This also
		// might be a problem since there is no type checking on the
		// result set to make sure it is even a reasonble result set
		// to give to this function.

		try {
			c_id = rs.getInt("c_id");
			c_uname = rs.getString("c_uname");
			c_passwd = rs.getString("c_passwd");
			c_fname = rs.getString("c_fname");
			c_lname = rs.getString("c_lname");

			c_phone = rs.getString("c_phone");
			c_email = rs.getString("c_email");
			c_since = rs.getDate("c_since");
			c_last_visit = rs.getDate("c_last_login");
			c_login = rs.getDate("c_login");
			c_expiration = rs.getDate("c_expiration");
			c_discount = rs.getDouble("c_discount");
			c_balance = rs.getDouble("c_balance");
			c_ytd_pmt = rs.getDouble("c_ytd_pmt");
			c_birthdate = rs.getDate("c_birthdate");
			c_data = rs.getString("c_data");

			addr_id = rs.getInt("addr_id");
			addr_street1 = rs.getString("addr_street1");
			addr_street2 = rs.getString("addr_street2");
			addr_city = rs.getString("addr_city");
			addr_state = rs.getString("addr_state");
			addr_zip = rs.getString("addr_zip");
			addr_co_id = rs.getInt("addr_co_id");

			co_name = rs.getString("co_name");

		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
		}
	}

}
