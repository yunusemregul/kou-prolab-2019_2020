package com.yunusemregul.prolab23.controllers;

import com.yunusemregul.prolab23.App;
import com.yunusemregul.prolab23.Movie;
import com.yunusemregul.prolab23.User;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class WatchmenuController extends GeneralController
{

	Timer timer;
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
	private Spinner rate_spinner;
	@FXML
	private Slider slider_time;
	@FXML
	private ProgressBar progressbar_time;
	@FXML
	private Text text_time;
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

		rate_spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10));
		rate_spinner.valueProperty().addListener(new ChangeListener()
		{
			@Override
			public void changed(ObservableValue observable, Object oldValue, Object newValue)
			{
				user.rate = (int) newValue;
				User.getInstance().saveMovieData();
			}
		});

		double sliderWidth = 1000;

		slider_time.setMin(0);
		slider_time.setMax(user.getMovie().length);
		slider_time.setMinWidth(sliderWidth);
		slider_time.setMaxWidth(sliderWidth);

		progressbar_time.setMinWidth(sliderWidth);
		progressbar_time.setMaxWidth(sliderWidth);

		setSlider();

		slider_time.valueProperty().addListener(new ChangeListener<Number>()
		{
			public void changed(ObservableValue<? extends Number> ov,
								Number old_val, Number new_val)
			{
				user.watchTime = new_val.intValue();
				progressbar_time.setProgress(new_val.doubleValue() / user.getMovie().length);
				text_time.setText(String.format("%02d:00 / %02d:00", user.watchTime, user.getMovie().length));
			}
		});

		if (user.rate != -1)
		{
			rate_spinner.getValueFactory().setValue(user.rate);
		}

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

	public void setSlider()
	{
		User user = User.getInstance();
		slider_time.setValue(user.watchTime);
		progressbar_time.setProgress((double) user.watchTime / user.getMovie().length);
		text_time.setText(String.format("%02d:00 / %02d:00", user.watchTime, user.getMovie().length));
	}

	public void watch()
	{
		isWatching = !isWatching;

		if (isWatching)
		{
			button_watch.setId("button_stop");

			this.timer = new Timer();

			timer.scheduleAtFixedRate(new TimerTask()
			{
				@Override
				public void run()
				{
					User user = User.getInstance();
					if (user.watchTime < movie.length)
					{
						user.watchTime++;
						setSlider();
						user.saveMovieData();
					}
					else
					{
						timer.cancel();
						timer.purge();
					}
				}
			}, 1000, 1000);
		}
		else
		{
			button_watch.setId("button_watch");
			timer.cancel();
			timer.purge();
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

		if (isWatching)
			watch();

		user.watchTime = 0;
		setSlider();
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

		if (isWatching)
			watch();

		user.watchTime = 0;
		setSlider();
		user.chapter++;
		chapter.setText("BÖLÜM " + user.chapter + "/" + movie.chapterCount);

		User.getInstance().saveMovieData();
	}

	public void openMainmenu()
	{
		try
		{
			App.setRoot("mainmenu");
		} catch (IOException exception)
		{
			exception.printStackTrace();
		}
	}
}
