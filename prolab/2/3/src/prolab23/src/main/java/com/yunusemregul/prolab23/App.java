package com.yunusemregul.prolab23;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class App extends Application
{
	private static Scene scene; // JavaFX de aktif olan pencere içeriği
	private static Stage lastStage; // En son gösterilen pencere

	/**
	 * Yeni pencere açmaya yarayan metot. Pencerenin dizaynını dosyalardan okuyup
	 * belirler, verilen pencere içeriğini de oluşturulan pencereye sabitler.
	 *
	 * @param stage     pencere
	 * @param sceneName pencere içeriğinin adı
	 * @throws IOException
	 */
	public static void loadScene(Stage stage, String sceneName) throws IOException
	{
		if (lastStage != null)
			lastStage.close();

		scene = new Scene(loadFXML(sceneName));
		scene.getStylesheets().add(App.class.getResource("style.css").toExternalForm());
		stage.setScene(scene);
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.getIcons().add(new Image(App.class.getResourceAsStream("images/app_icon.png")));
		stage.show();

		lastStage = stage;
	}

	/**
	 * Açık olan pencerenin içeriğini düzenleyen metot.
	 *
	 * @param fxml içerik dosyasının adı
	 * @throws IOException
	 */
	public static void setRoot(String fxml) throws IOException
	{
		scene.setRoot(loadFXML(fxml));
	}

	/**
	 * Bir içerik dosyasını okuyup içeriği döndüren metot
	 *
	 * @param fxml içerik dosyasının adı
	 * @return
	 *
	 * @throws IOException
	 */
	public static Parent loadFXML(String fxml) throws IOException
	{
		FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
		return fxmlLoader.load();
	}

	public static void main(String[] args)
	{
		launch();
	}

	/**
	 * JavaFX uygulamasının ilk başlangıç metodu.
	 *
	 * @param stage pencere
	 * @throws IOException
	 */
	@Override
	public void start(Stage stage) throws IOException
	{
		// Segoe UI fontunu kullanmak için
		Font.loadFont(App.class.getResourceAsStream("fonts/segoeui.ttf"), 10);

		// İlk başta giriş ekranını göster
		loadScene(stage, "login");
	}
}
