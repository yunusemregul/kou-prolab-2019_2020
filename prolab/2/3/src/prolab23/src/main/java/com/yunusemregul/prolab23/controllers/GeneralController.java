package com.yunusemregul.prolab23.controllers;

import com.yunusemregul.prolab23.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * GUI ile alakalı genel kontrolleri sağlayan sınıf. Örneğin GUI yi kapatma ve
 * küçültme butonları tüm ekranlarda olacağı için bu butonların yapacağı işlem
 * bu sınıf tarafından sağlanır.
 */
public class GeneralController
{
	double startY = 0;
	double xOffset = 0;
	double yOffset = 0;

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
		System.exit(0);
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
		} catch (IOException exception)
		{
			exception.printStackTrace();
		}
	}

	@FXML
	public void mousePressed(MouseEvent e)
	{
		Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
		xOffset = stage.getX() - e.getScreenX();
		yOffset = stage.getY() - e.getScreenY();
		startY = e.getY();
	}

	@FXML
	public void mouseDragged(MouseEvent e)
	{
		if (startY > 27)
			return;

		Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
		stage.setX(e.getScreenX() + xOffset);
		stage.setY(e.getScreenY() + yOffset);
	}
}
