package com.demo.servlet.user;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.fluent.Request;

import com.demo.entities.AccountGmail;
import com.demo.entities.UserGoogleDto;
import com.demo.helpers.GoogleUtils;
import com.demo.models.AccountDetailsModel;
import com.demo.models.AccountGmailModel;
import com.demo.models.AccountModel;



/**
* Servlet implementation class LoginGoogleServlet
*/
@WebServlet("/login-google")
public class LoginGoogleHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	/**
	* @see HttpServlet#HttpServlet()
	*/
	public LoginGoogleHandler() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	* @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	*/
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, 
IOException {
		// TODO Auto-generated method stub
		String code = request.getParameter("code");
		
		if (code == null || code.isEmpty()) {
			RequestDispatcher dis = request.getRequestDispatcher("/WEB-INF/views/login/login.jsp");
			dis.forward(request, response);
			} else {
			String accessToken = GoogleUtils.getToken(code);
			UserGoogleDto googlePojo = GoogleUtils.getUserInfo(accessToken);
			request.setAttribute("pojo", googlePojo);
			AccountGmailModel accountGmailModel = new AccountGmailModel();
			AccountDetailsModel accountDetailsModel = new AccountDetailsModel();
			AccountModel accountModel = new AccountModel();
			AccountGmail acc = accountGmailModel.findUserByGmail(googlePojo.getEmail());
			if(accountModel.findAccountByGmailID(acc.getId()) != null) {
				request.getSession().setAttribute("accountdetails", 
						accountDetailsModel.findAccountByAccountID(accountModel.findAccountByUsername(accountModel.findAccountByGmailID(acc.getId()).getUsername()).getId()));
				request.getSession().setAttribute("account", accountModel.findAccountByUsername(accountModel.findAccountByGmailID(acc.getId()).getUsername()));
				response.sendRedirect("account");
			} else {
				request.getSession().setAttribute("msg", "Đăng nhập thất bại");
				response.sendRedirect("login");
			}
//			RequestDispatcher dis = request.getRequestDispatcher("/WEB-INF/views/user/home.jsp");
//			dis.forward(request, response);
			}
	}

	/**
	* @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	*/
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, 
IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
