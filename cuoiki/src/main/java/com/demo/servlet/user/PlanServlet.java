package com.demo.servlet.user;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.demo.entities.Account;
import com.demo.entities.AccountService;
import com.demo.entities.Accountdetails;
import com.demo.entities.Duration;
import com.demo.entities.Invoice;
import com.demo.entities.Log;
import com.demo.entities.Service;
import com.demo.ex.ConfigLog;
import com.demo.helpers.PostHelper;
import com.demo.models.AccountDetailsModel;
import com.demo.models.AccountServiceModel;
import com.demo.models.DurationModel;
import com.demo.models.InvoiceModel;
import com.demo.models.LogModel;
import com.demo.models.ServiceModel;

/**
 * Servlet implementation class ServiceServlet
 */
@WebServlet("/plan")
public class PlanServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PlanServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String action = request.getParameter("action");
		if (action == null) {
			doGet_Index(request, response);
		} else if (action.equalsIgnoreCase("buy")) {
			doGet_Buy(request, response);
		}
	}

	protected void doGet_Index(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		  String urlString = "https://portal.vietcombank.com.vn/Usercontrols/TVPortal.TyGia/pXML.aspx?b=10"; 

	        HttpURLConnection connection = null;
	        InputStream inputStream = null;
	        try {
	        
	            URL url = new URL(urlString);
	            connection = (HttpURLConnection) url.openConnection();
	            connection.setRequestMethod("GET");

	          
	            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
	          
	                inputStream = connection.getInputStream();

	              
	                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	                DocumentBuilder builder = factory.newDocumentBuilder();

	              
	                Document document = builder.parse(inputStream);

	           
	                NodeList nodeList = document.getElementsByTagName("Exrate");

	                String usdSellRate = null;

	          
	                for (int i = 0; i < nodeList.getLength(); i++) {
	                    Element element = (Element) nodeList.item(i);
	                    if ("USD".equals(element.getAttribute("CurrencyCode"))) {
	                        usdSellRate = element.getAttribute("Sell");
	                        break;
	                    }
	                }

	          
	                request.setAttribute("usdSellRate", usdSellRate);


	            } else {
	                throw new ServletException("Failed to fetch XML file: HTTP code " + connection.getResponseCode());
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            throw new ServletException("Error processing XML file: " + e.getMessage(), e);
	        } finally {
	         
	            if (inputStream != null) {
	                try {
	                    inputStream.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	            if (connection != null) {
	                connection.disconnect();
	            }
	        }
		// TODO Auto-generated method stub
		request.setAttribute("activePlan", "active");
		request.setAttribute("p", "../user/plan.jsp");

		request.getRequestDispatcher("/WEB-INF/views/layout/user.jsp").forward(request, response);
	}

	protected void doGet_Buy(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String serviceId = request.getParameter("id");
		int durationId = Integer.parseInt(request.getParameter("duration"));
		Calendar calendar = Calendar.getInstance();
		AccountServiceModel accountServiceModel = new AccountServiceModel();
		AccountDetailsModel accountDetailsModel = new AccountDetailsModel();
		ServiceModel serviceModel = new ServiceModel();
		DurationModel durationModel = new DurationModel();
		Duration duration = new Duration();
		int spaceIndex = durationModel.findById(durationId).getName().indexOf(' ');
	    String numberString = durationModel.findById(durationId).getName().substring(0, spaceIndex);
		calendar.add(Calendar.MONTH, Integer.parseInt(numberString));
		Date endDate = calendar.getTime();
		duration.setStatus(true);
		LogModel logModel = new LogModel();
		Account account = (Account) request.getSession().getAttribute("account");
		if (account == null) {
			request.getSession().setAttribute("msg", "Bạn cần đăng nhập để mua gói dịch vụ");
			response.sendRedirect("plan");
		} else {
			Accountdetails accountdetails = accountDetailsModel.findAccountByAccountID(account.getId());
			accountdetails = accountDetailsModel.findAccountByAccountID(account.getId());
			if (accountdetails == null) {
				request.getSession().setAttribute("msg", "Bạn cần phải cập nhật thông tin tài khoản để mua dịch vụ");
				response.sendRedirect("account");
			} else {
				if (accountdetails.getBalance() > serviceModel.findByID(Integer.parseInt(serviceId)).getPrice()) {

					AccountService accountService = new AccountService();
					accountService.setAccountID(account.getId());
					accountService.setServiceID(Integer.parseInt(serviceId));
					accountService.setDurationID(durationId);
					accountService.setDescription(
							"Đăng kí gói: " + serviceModel.findByID(Integer.parseInt(serviceId)).getName() + " / " + durationModel.findById(durationId).getName());
					accountService.setCreated(new Date());
					accountService.setEndService(endDate);
					accountService.setStatus(true);
					accountService.setSaleID(0);
					if (accountServiceModel.register(accountService)) {
						logModel.create(new Log(ConfigLog.clientPublicIP, "alert","AccountID: " + account.getId() + " - đã mua gói dịch vụ",new ConfigLog().ipconfig(request).getCountryLong(), new java.util.Date(), null, "Gói dịch vụ đã mua là: " + serviceModel.findByID(Integer.parseInt(serviceId)).getName() + " / " + durationModel.findById(durationId).getName()));
						accountdetails.setBalance(accountdetails.getBalance()
								- serviceModel.findByID(Integer.parseInt(serviceId)).getPrice());
						accountDetailsModel.update(accountdetails);
						request.getSession().removeAttribute("accountdetails");
						request.getSession().setAttribute("accountdetails", accountdetails);
						request.getSession().setAttribute("msg", "Mua thành công");
						response.sendRedirect("plan");
					} else {
						request.getSession().setAttribute("msg", "Mua thất bại");
						response.sendRedirect("plan");
					}

				} else {
					request.getSession().setAttribute("msg", "Bạn không đủ tiền để mua gói dịch vụ này");
					response.sendRedirect("plan");
				}
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
