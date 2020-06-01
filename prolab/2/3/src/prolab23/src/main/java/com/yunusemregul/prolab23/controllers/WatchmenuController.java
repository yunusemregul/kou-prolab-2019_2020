package com.yunusemregul.prolab23.controllers;

import com.yunusemregul.prolab23.App;
import com.yunusemregul.prolab23.Movie;
import com.yunusemregul.prolab23.User;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class WatchmenuController extends GeneralController
{

	@FXML
	private Button button_watch;

	@FXML
	private Text user_name;

	@FXML
	private Text movie_name;
	@FXML
	private Text movie_type;
	@FXML
	private Text movie_score;
	@FXML
	private Text movie_kind;

	@FXML
	private HBox rate_stars;
	
	@FXML
	private Text chapter;
	
	private Movie movie;
	private boolean isWatching = false;

	public WatchmenuController()
	{

	}

	@FXML
	@Override
	public void initialize()
	{
		User user = User.getInstance();
		user_name.setText(user.name);

		this.movie = user.getMovie();

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
		chapter.setText("BÖLÜM " + user.chapter + "/" + movie.chapterCount);
	}

	public void watch()
	{
		isWatching = !isWatching;

		if (isWatching)
		{
			button_watch.setId("button_stop");
		}
		else
		{
			button_watch.setId("button_watch");
		}

		User.getInstance().saveMovieData();
	}

	public void previousChapter()
	{
		User user = User.getInstance();
		if (user.chapter == 1)
		{
			return;
		}

		user.chapter--;
		chapter.setText("BÖLÜM " + user.chapter + "/" + movie.chapterCount);

		User.getInstance().saveMovieData();
	}

	public void nextChapter()
	{
		User user = User.getInstance();
		if (user.chapter == movie.chapterCount)
		{
			return;
		}

		user.chapter++;
		chapter.setText("BÖLÜM " + user.chapter + "/" + movie.chapterCount);

		User.getInstance().saveMovieData();
	}

	public void openMainmenu()
	{
		try
		{
			App.setRoot("mainmenu");
		}
		catch (IOException exception)
		{
			exception.printStackTrace();
		}
	}
}
