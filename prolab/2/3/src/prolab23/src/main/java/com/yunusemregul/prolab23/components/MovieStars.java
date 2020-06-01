package com.yunusemregul.prolab23.components;

import com.yunusemregul.prolab23.App;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class MovieStars extends HBox
{

	@FXML
	private Label movie_score;
	@FXML
	private Label movie_name;

	public MovieStars()
	{
		FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("components/box_moviestars.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try
		{
			fxmlLoader.load();
		} catch (IOException exception)
		{
			throw new RuntimeException(exception);
		}
	}

	public void setInfo(String name, String stars)
	{
		movie_name.setText(name);
		movie_score.setText(stars);
	}
}
