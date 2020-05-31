package com.yunusemregul.prolab23.controllers;

import com.yunusemregul.prolab23.App;
import com.yunusemregul.prolab23.DataManager;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController extends GeneralController
{

	@FXML
	private TextField entry_email;
	@FXML
	private PasswordField entry_pass;

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
		String email = entry_email.getText();
		String pass = entry_pass.getText();

		if (email.length() == 0 || pass.length() == 0)
		{
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Giriş Yaparken Hata");
			String content = "";

			if (email.length() == 0)
			{
				content += "Email bölümü boş olamaz!\n";
			}

			if (pass.length() == 0)
			{
				content += "Şifre bölümü boş olamaz!\n";
			}

			alert.setContentText(content);
			alert.show();
			return;
		}

		boolean success = DataManager.getInstance().loginUser(email, pass);

		if (success)
		{
			try
			{
				App.loadScene(new Stage(), "mainmenu");
			}
			catch (IOException exception)
			{
				exception.printStackTrace();
			}
		}
		else
		{
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Giriş Yaparken Hata");
			alert.setContentText("Email ya da şifre hatalı!");
			alert.show();
		}
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
