package com.yunusemregul.prolab23;

import java.io.IOException;
import javafx.event.ActionEvent;

public class LoginController extends GeneralController
{

	public LoginController()
	{

	}

	/**
	 * Login ekranında Giriş butonuna tıklandığında çağırılır. Kullanıcının
	 * girdiği kullanıcı adı ve şifre ile giriş yapmaya çalışır.
	 *
	 * @param e ActionEvent
	 */
	public void tryLogIn(ActionEvent e)
	{

	}

	/**
	 * Login ekranında Kayıt Ol butonuna tıklandığında çağırılır.
	 *
	 * @param e ActionEvent
	 */
	public void openKayitOlMenu(ActionEvent e)
	{
		try
		{
			App.setRoot("register");
		}
		catch (IOException exception)
		{
			exception.printStackTrace();
		}
	}
}
