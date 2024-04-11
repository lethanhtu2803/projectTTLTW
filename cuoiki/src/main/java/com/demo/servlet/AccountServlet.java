package com.demo.servlet;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.demo.entities.AccountPartial;
import com.demo.entities.Log;
import com.demo.ex.ConfigLog;
import com.demo.helpers.IPAddressUtil;
import com.demo.models.LogModel;
import com.google.gson.Gson;
import org.mindrot.jbcrypt.BCrypt;

import com.demo.entities.Account;
import com.demo.entities.Accountdetails;
import com.demo.helpers.UploadFileHelper;
import com.demo.models.AccountDetailsModel;
import com.demo.models.AccountModel;
@WebServlet("/account")
@MultipartConfig(
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 10,
        fileSizeThreshold = 1024 * 1024 * 10
)
/**
 * Servlet implementation class AccountServlet
 */
public class AccountServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AccountServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		if (action == null) {
			doGet_Index(request, response);
		}
		
	}
	protected void doGet_Index(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Account account = (Account) request.getSession().getAttribute("account");
		if(account == null) {
			request.getRequestDispatcher("/WEB-INF/views/login/login.jsp").forward(request, response);
		} else {
			request.setAttribute("p", "../user/account.jsp");
			request.getRequestDispatcher("/WEB-INF/views/layout/user.jsp").forward(request, response);
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String action = request.getParameter("action");
		if(action == null) {
			doPost_UpdateAccount(request, response);
		}
	}
	
	protected void doPost_UpdateAccount(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Part file = request.getPart("file");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Account account = (Account) request.getSession().getAttribute("account");
		AccountDetailsModel accountDetailsModel = new AccountDetailsModel();
		AccountModel accountModel = new AccountModel();
		Accountdetails accountdetails = new Accountdetails();
		String fullName = request.getParameter("fullName");
		String currentPass = request.getParameter("currentPass");
		String newPass = request.getParameter("newPass");
		String address = request.getParameter("address");
		String email = request.getParameter("email");
		String phoneNumber = request.getParameter("phoneNumber");
		Date birthday = new Date(request.getParameter("birthday"));

		Gson gson = new Gson();
		LogModel logModel = new LogModel();
//		String beforeValue = "{\n" +
//				"   \"fullName\":\"" + accountDetailsModel.findAccountByAccountID(account.getId()).getName() + "\",\n" +
//				"    \"password\":\""+ account.getPassword() + "\",\n" +
//				"    \"address\":\""+ accountDetailsModel.findAccountByAccountID(account.getId()).getAddress() +"\",\n" +
//				"    \"email\":\""+ account.getEmail() +"\",\n" +
//				"    \"phoneNumber\":\""+ accountDetailsModel.findAccountByAccountID(account.getId()).getPhonenumber() +"\",\n" +
//				"    \"birthday\":\""+ accountDetailsModel.findAccountByAccountID(account.getId()).getBirthday() +"\"\n" +
//				" 	\"avatar\":\""+ accountDetailsModel.findAccountByAccountID(account.getId()).getAvatar() +"\"\n" +
//				"}";
		
//		beforeValue.setName(accountDetailsModel.findAccountByAccountID(account.getId()).getName()); // set full name
//		beforeValue.setPassword(account.getPassword()); // set password
//		beforeValue.setAddress(accountDetailsModel.findAccountByAccountID(account.getId()).getAddress()); // set address
//		beforeValue.setEmail(account.getEmail()); // set full name
//		beforeValue.setPhoneNumber(accountDetailsModel.findAccountByAccountID(account.getId()).getPhonenumber()); // set phoneNumber
//		beforeValue.setBirthday(accountDetailsModel.findAccountByAccountID(account.getId()).getBirthday()); // set phoneNumber
//		beforeValue.setAvatar(accountDetailsModel.findAccountByAccountID(account.getId()).getAvatar()); // set ava

		accountdetails.setAccountid(account.getId());
		accountdetails.setAddress(new String(address.getBytes("ISO-8859-1"), "UTF-8"));
		accountdetails.setBirthday(birthday);
		accountdetails.setName(new String(fullName.getBytes("ISO-8859-1"), "UTF-8"));
		accountdetails.setPhonenumber(phoneNumber);
		accountdetails.setUpdatedate(new Date());
//		System.out.println("Check pass: " + BCrypt.checkpw(currentPass, account.getPassword()));
		System.out.println(request.getParameter("newPass").equals(""));

		if(request.getParameter("currentPass") != null && !request.getParameter("currentPass").equals("") && BCrypt.checkpw(currentPass, account.getPassword())) {
			if(request.getParameter("newPass") != null && !request.getParameter("newPass").equals("")) {
				account.setPassword(BCrypt.hashpw(newPass, BCrypt.gensalt()));
			} else if(request.getParameter("email").equals("")){
				account.setPassword(account.getPassword());
			}
		} else {
			account.setPassword(account.getPassword());

		}

		if(request.getParameter("email") != null && !request.getParameter("email").equals("")) {
			account.setEmail(email);
		} else if(request.getParameter("email").equals("")){
			account.setEmail(account.getEmail());
		}
		String avatar = "";
		if(accountDetailsModel.findAccountByAccountID(account.getId()) == null) {
			avatar = "Unknown_person.jpg";
			
			if(file != null && file.getSize() > 0) {
				avatar = UploadFileHelper.uploadFile("assets/user/images",request,file);
			} 
			accountdetails.setAvatar(avatar);
		} else {
			avatar = accountDetailsModel.findAccountByAccountID(account.getId()).getAvatar();
			if(file != null && file.getSize() > 0) {
				avatar = UploadFileHelper.uploadFile("assets/user/images",request,file);
			} 
		
			accountdetails.setAvatar(avatar);
		}

//		String afterValue = "{\n" +
//				"   \"fullName\":\"" + new String(fullName.getBytes("ISO-8859-1"), "UTF-8") + "\",\n" +
//				"    \"password\":\""+ newPass + "\",\n" +
//				"    \"address\":\""+ new String(address.getBytes("ISO-8859-1"), "UTF-8") +"\",\n" +
//				"    \"email\":\""+ email +"\",\n" +
//				"    \"phoneNumber\":\""+ phoneNumber +"\",\n" +
//				"    \"birthday\":\""+ birthday +"\"\n" +
//				" 	\"avatar\":\""+ avatar +"\"\n" +
//				"}";
		String pass = BCrypt.hashpw(newPass, BCrypt.gensalt());
		AccountPartial afterValue = null;
//		afterValue.setName(new String(fullName.getBytes("ISO-8859-1"), "UTF-8")); // set full name
//		afterValue.setPassword(BCrypt.hashpw(newPass, BCrypt.gensalt())); // set password
//		afterValue.setAddress(new String(address.getBytes("ISO-8859-1"), "UTF-8")); // set address
//		afterValue.setEmail(email); // set email
//		afterValue.setPhoneNumber(phoneNumber); // set phoneNumber
//		afterValue.setBirthday(accountDetailsModel.findAccountByAccountID(account.getId()).getBirthday()); // set birthday
//		afterValue.setAvatar(avatar); // set ava
		AccountPartial beforeValue = null;
		if(accountDetailsModel.findAccountByAccountID(account.getId()) == null) {
			if(accountDetailsModel.create(accountdetails) && accountModel.update(account)) {
				beforeValue = new AccountPartial(
						accountDetailsModel.findAccountByAccountID(account.getId()).getName() == null ? null : accountDetailsModel.findAccountByAccountID(account.getId()).getName(),
						account.getPassword() != null ? account.getPassword() : null,
						accountDetailsModel.findAccountByAccountID(account.getId()).getBirthday() != null ? accountDetailsModel.findAccountByAccountID(account.getId()).getBirthday() : null,
						account.getEmail() != null ? account.getEmail() : null,
						accountDetailsModel.findAccountByAccountID(account.getId()).getPhonenumber() != null ? accountDetailsModel.findAccountByAccountID(account.getId()).getName() : null,
						accountDetailsModel.findAccountByAccountID(account.getId()).getAddress() != null ? accountDetailsModel.findAccountByAccountID(account.getId()).getPhonenumber() : null,
						accountDetailsModel.findAccountByAccountID(account.getId()).getAvatar() != null ? accountDetailsModel.findAccountByAccountID(account.getId()).getAvatar() : null);
				System.out.println(gson.toJson(beforeValue));
				new AccountPartial(
						new String(fullName.getBytes("ISO-8859-1"), "UTF-8"),
						pass,
						accountDetailsModel.findAccountByAccountID(account.getId()).getBirthday(),
						email,
						phoneNumber,
						new String(address.getBytes("ISO-8859-1"), "UTF-8"),
						avatar);
				logModel.create(new Log(IPAddressUtil.getPublicIPAddress(), "alert" ,ConfigLog.ipconfig(request).getCountryLong(), new Timestamp(new Date().getTime()), gson.toJson(beforeValue),gson.toJson(afterValue)));
				request.getSession().removeAttribute("accountdetails");
				request.getSession().setAttribute("accountdetails",
						accountDetailsModel.findAccountByAccountID(accountModel.findAccountByUsername(account.getUsername()).getId()));
				request.getSession().setAttribute("msg","Cập nhật thành công");
				response.sendRedirect("account");
			} else {
				request.getSession().setAttribute("msg","Cập nhật thất bại");
				response.sendRedirect("account");
			}
		} else {

			if(accountDetailsModel.update(accountdetails) && accountModel.update(account)) {
				logModel.create(new Log(IPAddressUtil.getPublicIPAddress(), "alert" ,ConfigLog.ipconfig(request).getCountryLong(), new Timestamp(new Date().getTime()), gson.toJson(beforeValue),gson.toJson(afterValue)));
				request.getSession().removeAttribute("accountdetails");
				request.getSession().setAttribute("accountdetails",
						accountDetailsModel.findAccountByAccountID(accountModel.findAccountByUsername(account.getUsername()).getId()));
				request.getSession().setAttribute("msg","Cập nhật thành công");
				response.sendRedirect("account");
			} else {
				request.getSession().setAttribute("msg","Cập nhật thất bại");
				response.sendRedirect("account");
			}
		}
		
	}

}
