package com.yunusemregul.prolab23.controllers;

import com.yunusemregul.prolab23.App;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * GUI ile alakalı genel kontrolleri sağlayan sınıf. Örneğin GUI yi kapatma ve
 * küçültme butonları tüm ekranlarda olacağı için bu butonların yapacağı işlem
 * bu sınıf tarafından sağlanır.
 */
public class GeneralController
{

	public GeneralController()
	{

	}

	@FXML
	public void initialize()
	{

	}

	/**
	 * Çerçevedeki kırmızı yuvarlak kapatma tuşuna basınca çağırılır.
	 *
	 * @param e ActionEvent
	 */
	@FXML
	public void close(ActionEvent e)
	{
		Stage stage = (Stage) ((Button) e.getSource()).getScene().getWindow();
		stage.close();
	}

	/**
	 * Çerçevedeki sarı yuvarlak küçültme tuşuna basınca çağırılır.
	 *
	 * @param e ActionEvent
	 */
	@FXML
	public void minimize(ActionEvent e)
	{
		Stage stage = (Stage) ((Button) e.getSource()).getScene().getWindow();
		stage.setIconified(true);
	}

	@FXML
	public void logOff()
	{
		try
		{
			App.loadScene(new Stage(), "login");
		}
		catch (IOException exception)
		{
			exception.printStackTrace();
		}
	}
}
