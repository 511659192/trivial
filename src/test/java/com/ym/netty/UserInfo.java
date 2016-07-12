package com.ym.netty;

public class UserInfo {

	private String userName;

	private int userId;

	public String getUserName() {

		return userName;

	}

	public void setUserName(String userName) {

		this.userName = userName;

	}

	public int getUserId() {

		return userId;

	}

	public void setUserId(int userId) {

		this.userId = userId;

	}

	public UserInfo buildUserName(String userName) {

		this.userName = userName;

		return this;

	}

	public UserInfo buildUserId(int userId) {

		this.userId = userId;

		return this;

	}

	@Override

	public String toString() {

		return "UserInfo [userName=" + userName + ", userId=" + userId + "]";

	}

}