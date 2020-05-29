package com.yunusemregul.prolab23;

import java.io.IOException;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class RegisterController extends GeneralController
{

	@FXML
	private VBox vBox;
	@FXML
	private GridPane gPane;

	private Button[] selectedTypes = new Button[3];
	private int stIndex = 0;

	public RegisterController()
	{

	}

	@FXML
	public void initialize()
	{
		ArrayList<String> turler = App.data.getTurler();

		for (int i = 0; i < turler.size(); i++)
		{
			Button but = new Button(turler.get(i));
			but.setId("button_favmovies");
			but.setPrefSize(170, 40);

			but.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(ActionEvent e)
				{
					for (int j = 0; j < 3; j++)
					{
						if (selectedTypes[j] == but)
						{
							return;
						}
					}

					if (selectedTypes[stIndex] != null)
					{
						selectedTypes[stIndex].setId("button_favmovies");
					}

					selectedTypes[stIndex] = but;
					but.setId("button_favmovies_selected");
					stIndex = (stIndex + 1) % 3;
				}
			});

			gPane.setVgap(12);
			gPane.setHgap(17);
			gPane.addRow((int) (i / 3), but);
		}

		for (int i = 0; i < 3 - (turler.size() % 3); i++)
		{
			Pane pane = new Pane();
			pane.setId("pane_favmovies_disabled");
			pane.setPrefSize(170, 40);
			gPane.setVgap(12);
			gPane.setHgap(17);
			gPane.addRow((int) (turler.size() / 3), pane);
		}
	}
	
	public void onFavtypeSelected()
	{
		
	}
	
	/**
	 * Kayıt Ol ekranında Kayıt Ol butonuna tıklandığında çağırılır.
	 * Kullanıcının girdiği bilgilerle yeni bir kullanıcı kayıt etmeye çalışır.
	 *
	 * @param e ActionEvent
	 */
	public void tryRegister(ActionEvent e)
	{

	}

	/**
	 * Kayıt Ol ekranında Giriş Yap butonuna tıklandığında çağırılır.
	 *
	 * @param e ActionEvent
	 */
	public void openLoginMenu(ActionEvent e)
	{
		try
		{
			App.setRoot("login");
		}
		catch (IOException exception)
		{
			exception.printStackTrace();
		}
	}
}
