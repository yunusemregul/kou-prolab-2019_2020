package com.yunusemregul.prolab23.components;

import com.yunusemregul.prolab23.App;
import com.yunusemregul.prolab23.Movie;
import com.yunusemregul.prolab23.controllers.MainmenuController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

import java.io.IOException;

/**
 * Ana menüde altında İZLE butonu olan film kutularının kontrolünü sağlayan sınıf.
 */

public class MovieBox extends BorderPane
{

	@FXML
	public Button button_watch;
	@FXML
	private Text movie_name;
	@FXML
	private Text movie_type;
	@FXML
	private Text movie_score;
	@FXML
	private Text movie_kind;
	private Movie movie;

	public MovieBox()
	{
		FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("components/box_movie.fxml"));
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

	public void setInfo(Movie movie)
	{
		this.movie = movie;

		String type = "";

		String[] types = movie.type.split(",");

		for (String t : types)
		{
			type += (t.contains(" ") ? t.split(" ")[0] : t) + "\n";
		}

		type = type.trim();

		movie_name.setText(movie.name);
		movie_type.setText(type);
		movie_score.setText(movie.score + "/10");
		movie_kind.setText(movie.kind.equals("") ? "Belirtilmemiş" : movie.kind);
	}

	public void watchMovie()
	{
		MainmenuController.openWatchmenu(movie);
	}
}
