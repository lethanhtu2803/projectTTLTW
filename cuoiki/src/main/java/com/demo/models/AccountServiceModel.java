package com.demo.models;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.demo.entities.Account;
import com.demo.entities.AccountService;
import com.demo.entities.Chat;
import com.demo.entities.ConnectDB;
import com.demo.entities.Service;

public class AccountServiceModel {
	public List<AccountService> findAll(){
		List<AccountService> accountServices = new ArrayList<AccountService>();
		try {
			PreparedStatement preparedStatement = ConnectDB.connection().prepareStatement("select * from account_service where status = 1");
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) {
				AccountService accountService = new AccountService();
				accountService.setId(resultSet.getInt("id"));
				accountService.setAccountID(resultSet.getInt("accountID"));
				accountService.setServiceID(resultSet.getInt("serviceID"));
				accountService.setDurationID(resultSet.getInt("durationID"));
				accountService.setSaleID(resultSet.getInt("saleID"));
				accountService.setCreated(resultSet.getTimestamp("created"));
				accountService.setEndService(resultSet.getTimestamp("endService"));
				accountService.setStatus(resultSet.getBoolean("status"));
				accountServices.add(accountService);
			}
		} catch (Exception e) {
			e.printStackTrace();
			accountServices = null;
			// TODO: handle exception
		} finally {
			ConnectDB.disconnect();
		}
		
		return accountServices;
	}
	
	public List<AccountService> findAll2(){
		List<AccountService> accountServices = new ArrayList<AccountService>();
		try {
			PreparedStatement preparedStatement = ConnectDB.connection().prepareStatement("select * from account_service");
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) {
				AccountService accountService = new AccountService();
				accountService.setId(resultSet.getInt("id"));
				accountService.setAccountID(resultSet.getInt("accountID"));
				accountService.setServiceID(resultSet.getInt("serviceID"));
				accountService.setDurationID(resultSet.getInt("durationID"));
				accountService.setSaleID(resultSet.getInt("saleID"));
				accountService.setCreated(resultSet.getTimestamp("created"));
				accountService.setEndService(resultSet.getTimestamp("endService"));
				accountService.setStatus(resultSet.getBoolean("status"));
				accountServices.add(accountService);
			}
		} catch (Exception e) {
			e.printStackTrace();
			accountServices = null;
			// TODO: handle exception
		} finally {
			ConnectDB.disconnect();
		}
		
		return accountServices;
	}
	
	public AccountService findAccountById(int id, int durationId) {
		AccountService accountService = null;
		try {
			PreparedStatement preparedStatement = ConnectDB.connection().prepareStatement("select * from account_service where id = ? AND durationID = ?");
			preparedStatement.setInt(1, id);
			preparedStatement.setInt(2, durationId);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) {
				accountService = new AccountService();
				accountService.setId(resultSet.getInt("id"));
				accountService.setAccountID(resultSet.getInt("accountID"));
				accountService.setServiceID(resultSet.getInt("serviceID"));
				accountService.setDurationID(resultSet.getInt("durationID"));
				accountService.setSaleID(resultSet.getInt("saleID"));
				accountService.setCreated(resultSet.getTimestamp("created"));
				accountService.setEndService(resultSet.getTimestamp("endService"));
				accountService.setStatus(resultSet.getBoolean("status"));
				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			accountService = null;
			// TODO: handle exception
		} finally {
			ConnectDB.disconnect();
		}
		return accountService;
	}
	
	public AccountService findAccountByAccountId(int acccountId) {
		AccountService accountService = null;
		try {
			PreparedStatement preparedStatement = ConnectDB.connection().prepareStatement("select * from account_service where accountId = ? AND status = 1");
			preparedStatement.setInt(1, acccountId);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) {
				accountService = new AccountService();
				accountService.setId(resultSet.getInt("id"));
				accountService.setAccountID(resultSet.getInt("accountID"));
				accountService.setServiceID(resultSet.getInt("serviceID"));
				accountService.setDurationID(resultSet.getInt("durationID"));
				accountService.setSaleID(resultSet.getInt("saleID"));
				accountService.setCreated(resultSet.getTimestamp("created"));
				accountService.setEndService(resultSet.getTimestamp("endService"));
				accountService.setStatus(resultSet.getBoolean("status"));
				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			accountService = null;
			// TODO: handle exception
		} finally {
			ConnectDB.disconnect();
		}
		return accountService;
	}
	
	public boolean update(AccountService accountService) {
		boolean status = true;
		try {
			PreparedStatement preparedStatement = ConnectDB.connection()
			.prepareStatement("update account_service set accountID = ?, serviceID = ?, durationID = ?, created = ?, endService = ?, status = ?, saleID = ? where id = ?");
			preparedStatement.setInt(1, accountService.getAccountID());
			preparedStatement.setInt(2, accountService.getServiceID());
			preparedStatement.setInt(3, accountService.getDurationID());
			preparedStatement.setTimestamp(4, new Timestamp(accountService.getCreated().getTime()));
			preparedStatement.setTimestamp(5, new Timestamp(accountService.getEndService().getTime()));
			preparedStatement.setBoolean(6, accountService.isStatus());
			preparedStatement.setInt(7, accountService.getSaleID());
			preparedStatement.setInt(8, accountService.getId());
			
			status = preparedStatement.executeUpdate() > 0;
			
			
		} catch (Exception e) {
			e.printStackTrace();
			status = false;
			// TODO: handle exception
		} finally {
			ConnectDB.disconnect();
		}
		return status;
	}
	
	
	public static void main(String[] args) {
		AccountServiceModel accountServiceModel = new AccountServiceModel();
		System.out.println(accountServiceModel.findAccountByAccountId(1));
	}
}
