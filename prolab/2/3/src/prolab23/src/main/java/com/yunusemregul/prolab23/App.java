package com.yunusemregul.prolab23;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.StageStyle;

public class App extends Application
{

	private static Scene scene;

	@Override
	public void start(Stage stage) throws IOException
	{
		Font.loadFont(App.class.getResourceAsStream("fonts/segoeui.ttf"), 10);

		scene = new Scene(loadFXML("login"));
		scene.getStylesheets().add(App.class.getResource("style.css").toExternalForm());
		stage.setScene(scene);
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.getIcons().add(new Image(App.class.getResourceAsStream("images/app_icon.png")));
		stage.show();
	}

	static void setRoot(String fxml) throws IOException
	{
		scene.setRoot(loadFXML(fxml));
	}

	static Parent loadFXML(String fxml) throws IOException
	{
		FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
		return fxmlLoader.load();
	}

	public static void main(String[] args)
	{
		launch();
	}

}
