package com.yunusemregul.prolab23;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * GUI ile alakalı genel kontrolleri sağlayan sınıf. Örneğin GUI yi kapatma ve
 * küçültme butonları tüm ekranlarda olacağı için bu butonların yapacağı işlem
 * bu sınıf tarafından sağlanır.
 */
public class GeneralController implements Initializable
{

	public GeneralController()
	{

	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle)
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
}
