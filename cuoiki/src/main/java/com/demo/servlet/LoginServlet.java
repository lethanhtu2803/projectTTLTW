package com.demo.servlet;

import java.io.IOException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.demo.models.*;
import org.mindrot.jbcrypt.BCrypt;


import com.demo.entities.Account;
import com.demo.entities.Log;
import com.demo.ex.ConfigLog;
import com.demo.ex.LogService;
import com.demo.ex.LoginLogServiceImpl;
import com.demo.helpers.IP2LocationService;
import com.demo.helpers.IPAddressUtil;
import com.demo.helpers.MailHelper;
import com.demo.helpers.RandomStringHelper;
import com.ip2location.IPResult;

@WebServlet("/login")
/**
 * Servlet implementation class LoginServlet
 */
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getParameter("action");
		if (action == null) {
			doGet_Login(request, response);
		} else {
			if(action.equalsIgnoreCase("logout")) {
				doGet_Logout(request, response);
			} else if(action.equalsIgnoreCase("verify")) {
				doGet_Verify(request, response);
			} else if(action.equalsIgnoreCase("message")) {
				doGet_Message(request, response);
			} else if(action.equalsIgnoreCase("test")) {
				doGet_LoginTest(request, response);
			}
		}
	}
	protected void doGet_Verify(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String username = request.getParameter("username");
		String email = request.getParameter("email");
		String securityCode = request.getParameter("securityCode");
		AccountModel accountModel = new AccountModel();
		Account account = accountModel.findAccountByUsername(new String(username.getBytes("ISO-8859-1"), "UTF-8"));
		System.out.println(account);
		if(account.getUsername().equalsIgnoreCase(new String(username.getBytes("ISO-8859-1"), "UTF-8")) && account.getSecurityCode().equalsIgnoreCase(securityCode)) {
			account.setVerify(true);
			System.out.println("Đã cập nhật verify");
			if(accountModel.update(account)) {
				System.out.println("đã cập nhật thành công");
				response.sendRedirect("login");
			}
			
		} else {
			System.out.println("cập nhật thất bại");
			request.getSession().setAttribute("content", "aaaaa");
			response.sendRedirect("login?action=message");
		}
	}
	protected void doGet_Login(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		AccountModel accountModel = new AccountModel();
		request.setAttribute("accounts", accountModel.findAll());
		request.getRequestDispatcher("/WEB-INF/views/login/login.jsp").forward(request, response);
	}
	// test google
	protected void doGet_LoginTest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getRequestDispatcher("/WEB-INF/views/login/googleTest.jsp").forward(request, response);
	}
	protected void doGet_Message(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		request.getRequestDispatcher("/WEB-INF/views/login/test.jsp").forward(request, response);
	}
	protected void doGet_Logout(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getSession().removeAttribute("accountAdmin");
		request.getSession().removeAttribute("accountdetails");
		request.getSession().removeAttribute("accounts");
		request.getSession().removeAttribute("posts");
		request.getRequestDispatcher("/WEB-INF/views/login/login.jsp").forward(request, response);
	}
	protected void doGet_Account(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		request.getRequestDispatcher("/WEB-INF/views/login/login.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getParameter("action");
		if(action.equalsIgnoreCase("login")) {
			doPost_Login(request, response);
		} else if(action.equalsIgnoreCase("register")) {
			doPost_Register(request, response);
		}
	}
	protected void doPost_Login(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		AccountModel accountModel = new AccountModel();
		AccountDetailsModel accountDetailsModel = new AccountDetailsModel();
		Account account = accountModel.findAccountByUsername(new String(username.getBytes("ISO-8859-1"), "UTF-8"));
		FeedbackModel feedbackModel = new FeedbackModel();
		ContactModel contactModel = new ContactModel();
		LogModel logModel = new LogModel();
		ConfigLog configLog = new ConfigLog();
		ChatModel chatModel = new ChatModel();
		
		if(accountModel.login(new String(username.getBytes("ISO-8859-1"), "UTF-8"), password)) {
			logModel.create(new Log(configLog.clientPublicIP, "info","AccountID: " + account.getId() + " Đăng nhập",new ConfigLog().ipconfig(request).getCountryLong(), new java.util.Date(), null, null));
			if(account.getRole() == 0 || account.getRole() == 2) {
				request.getSession().setAttribute("accountAdmin", account);
				request.getSession().removeAttribute("accountdetails");
				request.getSession().removeAttribute("account");
				request.getSession().setAttribute("feedbacks", feedbackModel.findAll().size());
				request.getSession().setAttribute("contacts", contactModel.findAll().size());
				response.sendRedirect("admin/dashboard");
			} else if(account.getRole() == 1) {
				request.getSession().setAttribute("accountdetails", 
						accountDetailsModel.findAccountByAccountID(accountModel.findAccountByUsername(new String(username.getBytes("ISO-8859-1"), "UTF-8")).getId()));
				request.getSession().setAttribute("account", accountModel.findAccountByUsername(new String(username.getBytes("ISO-8859-1"), "UTF-8")));
				request.getSession().setAttribute("msgNoti", chatModel.findChat(account.getId(), 0).size());
				request.getSession().removeAttribute("accountAdmin");
				response.sendRedirect("account");
			}
			
		
		} else {
			logModel.create(new Log(configLog.clientPublicIP, "info"," Đăng nhập thất bại",new ConfigLog().ipconfig(request).getCountryLong(), new java.util.Date(), null, null));
			request.getSession().setAttribute("msg", "Tài khoản hoặc mật khẩu không đúng");
			response.sendRedirect("login");
		}
		
		
	}
	protected void doPost_Register(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String email = request.getParameter("email");
		String regexEmail = "^(.+)@(.+)$";
		String regexPass = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,20}$";
		Pattern patternEmail = Pattern.compile(regexEmail);
		Pattern patternPassword = Pattern.compile(regexPass);
		Matcher matcherEmail = patternEmail.matcher(email);
		Matcher matcherPassword = patternPassword.matcher(password);
		String securityCode = RandomStringHelper.generateRandomString(6);
		Account account = new Account();
		account.setUsername(new String(username.getBytes("ISO-8859-1"), "UTF-8") );
		account.setEmail(email);
		account.setCreated(new Date());
		account.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
		account.setStatus(true);
		account.setVerify(false);
		account.setRole(1);
		account.setSecurityCode(securityCode);
		AccountModel accountModel = new AccountModel();
		AccountGmailModel accountGmailModel = new AccountGmailModel();
		if(matcherEmail.matches() && matcherPassword.matches()) {
			if(accountModel.register(account)) {
				request.getSession().setAttribute("msg", "Đã đăng kí tài khoản thành công. Xin vui lòng đăng nhập tài khoản");
				String content = "Xin chào, đây là email từ $Apartment! Vui lòng nhấp vào <a href='http://localhost:8080/projectGroup2/login?action=verify&username=" + new String(username.getBytes("ISO-8859-1"), "UTF-8") + "&email=" + email +"&securityCode=" + securityCode + "'>Liên kết</a> để xác nhận tài khoản của bạn.";
				MailHelper.MailHelper(email, "Xác nhận tài khoản - $Apartment", content);
				response.sendRedirect("login?action=message");
			} else {
				request.getSession().setAttribute("msg", "Đăng kí không thành công do đã tồn tại người dùng.");
				response.sendRedirect("login");
			}
			
		}
		
	
		
	
	}

}
