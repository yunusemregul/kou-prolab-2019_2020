package com.yunusemregul.prolab23;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class RegisterController extends GeneralController
{

	@FXML
	private VBox vbox_mostliked;
	@FXML
	private GridPane gPane;

	private Button[] selectedTypes = new Button[3];
	private int stIndex = 0;

	public RegisterController()
	{

	}

	/**
	 * Kayıt Ol menüsü açıldığında çağrılan metot.
	 */
	@FXML
	public void initialize()
	{
		// Datadan film/dizi türlerini çekiyoruz
		ArrayList<String> turler = App.data.getTurler();

		// Türleri kullanıcının en sevdiği türleri seçebilmesi için buton olarak ekliyoruz.
		for (int i = 0; i < turler.size(); i++)
		{
			Button but = new Button(turler.get(i));
			but.setId("button_favmovies");
			but.setPrefSize(170, 40);

			// Sevilen türe tıklandığında çerçevesini yeşil seçilme görüntüsü yapmak için.
			but.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(ActionEvent e)
				{
					// Eğer seçilen tür zaten seçildiyse hiç bir şey yapma
					for (int j = 0; j < 3; j++)
					{
						if (selectedTypes[j] == but)
						{
							return;
						}
					}

					// Eğer 3 ten fazla kez en sevilen tür seçildiyse tüm sevilenleri temizle
					if (stIndex == 3)
					{
						for (int j = 0; j < 3; j++)
						{
							selectedTypes[j].setId("button_favmovies");
							selectedTypes[j] = null;
						}
						// En yüksek puanlı filmler panelini temizle
						onFavtypeCleared();
						stIndex = 0;
					}

					// Seçilen türü seçilmiş türlere ekle
					selectedTypes[stIndex] = but;
					// Butonun çerçevesini yeşil yap seçildiğini belli etmek için
					but.setId("button_favmovies_selected");
					stIndex += 1;

					// Seçilen tür için en yüksek puanlı filmleri göstermek için
					onFavtypeSelected(but.getText());
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

	/**
	 * Kayıt Ol menüsünde kullanıcı beğendiği bir filmi seçtiğinde en yüksek
	 * puanlı filmleri listeleyen metot.
	 *
	 * @param type seçilen en sevilen tip
	 */
	public void onFavtypeSelected(String type)
	{
		VBox mainBox = new VBox();
		mainBox.setPrefWidth(550);
		mainBox.setPadding(new Insets(8, 8 , 8, 8));
		mainBox.setId("button_favmovies");
		mainBox.setAlignment(Pos.CENTER_LEFT);
		VBox.setMargin(mainBox, new Insets(12, 0, 0, 0));

		Label lab = new Label(type + " türünün en iyi ikisi: ");
		mainBox.getChildren().add(lab);

		HBox bests = new HBox();
		bests.setAlignment(Pos.CENTER_LEFT);
		VBox.setMargin(bests, new Insets(8, 0, 0, 0));
		
		HashMap<String, Float> bestTwo = App.data.getTopTwoForTur(type);
		
		for (Map.Entry entry : bestTwo.entrySet())
		{
			HBox bestBox = new HBox();
			bestBox.setAlignment(Pos.CENTER_LEFT);
			bestBox.setId("best_two_box");
			bestBox.setPrefHeight(32);
			
			HBox starsBox = new HBox();
			starsBox.setId("stars_box");
			starsBox.setAlignment(Pos.CENTER_LEFT);
		
			Label starsLabel = new Label(""+entry.getValue());
			HBox.setMargin(starsLabel, new Insets(0, 0, 0, 8));
			starsBox.getChildren().add(starsLabel);
			
			ImageView starsIcon = new ImageView();
			
			
			Label name = new Label(""+entry.getKey());
			HBox.setMargin(name, new Insets(0, 8, 0, 8));
			
			bestBox.getChildren().add(starsBox);
			bestBox.getChildren().add(name);
			bests.getChildren().add(bestBox);
		}

		mainBox.getChildren().add(bests);
		vbox_mostliked.getChildren().add(mainBox);
	}

	/**
	 * En sevilen 3 tip seçilirken kullanıcı 3. den sonra yeni bir tip seçerse
	 * en sevilen tipler sıfırlanır ve tekrar seçim yapması beklenir,
	 * sıfırlandığında bu metot çağrılır ve en yüksek puanlı filmleri gösteren
	 * listeyi temizler.
	 */
	public void onFavtypeCleared()
	{
		vbox_mostliked.getChildren().clear();
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
