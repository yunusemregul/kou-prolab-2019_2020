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
	private static Stage lastStage;

	@Override
	public void start(Stage stage) throws IOException
	{
		Font.loadFont(App.class.getResourceAsStream("fonts/segoeui.ttf"), 10);

		loadScene(stage, "login");
	}
	
	public static void loadScene(Stage stage, String sceneName) throws IOException
	{
		if(lastStage!=null)
			lastStage.close();
		
		scene = new Scene(loadFXML(sceneName));
		scene.getStylesheets().add(App.class.getResource("style.css").toExternalForm());
		stage.setScene(scene);
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.getIcons().add(new Image(App.class.getResourceAsStream("images/app_icon.png")));
		stage.show();
		
		lastStage = stage;
	}

	public static void setRoot(String fxml) throws IOException
	{
		scene.setRoot(loadFXML(fxml));
	}

	public static Parent loadFXML(String fxml) throws IOException
	{
		FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
		return fxmlLoader.load();
	}

	public static void main(String[] args)
	{
		launch();
	}

}
