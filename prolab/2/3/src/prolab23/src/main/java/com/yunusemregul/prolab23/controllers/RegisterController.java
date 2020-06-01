package com.yunusemregul.prolab23.controllers;

import com.yunusemregul.prolab23.App;
import com.yunusemregul.prolab23.DataManager;
import com.yunusemregul.prolab23.components.MovieStars;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegisterController extends GeneralController
{

	@FXML
	private VBox vbox_mostliked;
	@FXML
	private GridPane gPane;

	@FXML
	private TextField entry_name;
	@FXML
	private DatePicker entry_birthdate;
	@FXML
	private TextField entry_email;
	@FXML
	private PasswordField entry_pass;
	@FXML
	private PasswordField entry_pass_again;

	private Button[] selectedTypes = new Button[3];
	private int stIndex = 0;

	public RegisterController()
	{

	}

	/**
	 * Kayıt Ol menüsü açıldığında çağrılan metot.
	 */
	@FXML
	@Override
	public void initialize()
	{
		// Datadan film/dizi türlerini çekiyoruz
		ArrayList<String> turler = DataManager.getInstance().getTurler();

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
		mainBox.setPrefWidth(542);
		mainBox.setPadding(new Insets(8, 8, 8, 8));
		mainBox.setId("button_favmovies");
		mainBox.setAlignment(Pos.CENTER_LEFT);
		VBox.setMargin(mainBox, new Insets(12, 0, 0, 0));

		Label lab = new Label(type + " türünün en iyileri: ");
		mainBox.getChildren().add(lab);

		HBox bests = new HBox();
		bests.setAlignment(Pos.CENTER_LEFT);
		VBox.setMargin(bests, new Insets(8, 0, 0, 0));

		HashMap<String, Float> bestTwo = DataManager.getInstance().getTopTwoForTur(type);

		if (bestTwo.size() > 0)
		{
			int count = 0;
			for (Map.Entry entry : bestTwo.entrySet())
			{
				MovieStars box = new MovieStars();
				box.setInfo(entry.getKey().toString(), String.format("%.1f", entry.getValue()).replace(",", "."));

				if (count > 0)
				{
					HBox.setMargin(box, new Insets(0, 0, 0, 12));
				}

				bests.getChildren().add(box);
				count++;
			}
		}
		else
		{
			Label no_entries = new Label("Henüz yeteri kadar puan verilmemiş!");
			bests.setAlignment(Pos.CENTER);
			bests.getChildren().add(no_entries);
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
		String name = entry_name.getText();
		String birthdate = entry_birthdate.getValue().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
		String email = entry_email.getText();
		String pass = entry_pass.getText();
		String pass_again = entry_pass_again.getText();

		if (name.length() == 0 || birthdate.length() == 0 || email.length() == 0 || pass.length() == 0 || !pass.equals(pass_again))
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Kayıt Olurken Hata");
			String content = "";

			if (name.length() == 0)
			{
				content += "Ad bölümü boş olamaz!\n";
			}

			if (birthdate.length() == 0)
			{
				content += "Doğum günü bölümü boş olamaz!\n";
			}

			if (email.length() == 0)
			{
				content += "Email bölümü boş olamaz!\n";
			}

			if (pass.length() == 0)
			{
				content += "Şifre bölümü boş olamaz!\n";
			}
			else
			{
				if (!pass.equals(pass_again))
				{
					content += "Şifreler eşleşmiyor!";
				}
			}
			alert.setContentText(content);
			alert.show();
			return;
		}

		boolean success = DataManager.getInstance().registerUser(name, email, pass, birthdate);

		if (success)
		{
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Kayıt Başarılı");
			alert.setContentText("Kayıt başarılı, otomatik giriş yaptınız.");
			alert.showAndWait();

			DataManager.getInstance().loginUser(email, pass);

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
			String error = DataManager.getInstance().lastError;

			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Kayıt Olurken Hata");
			alert.setContentText("SQL tarafında beklenmedik bir hata oluştu:\n" + error);

			if (error.contains("UNIQUE constraint failed: Kullanici.email"))
			{
				alert.setContentText("Zaten bu email ile daha önceden kaydolunmuş!");
			}

			alert.show();
		}
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
