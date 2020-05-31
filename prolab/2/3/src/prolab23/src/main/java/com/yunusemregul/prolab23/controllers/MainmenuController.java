package com.yunusemregul.prolab23.controllers;

import com.yunusemregul.prolab23.DataManager;
import com.yunusemregul.prolab23.Movie;
import com.yunusemregul.prolab23.components.MovieBox;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

public class MainmenuController extends GeneralController
{

	@FXML
	private GridPane mainmenu_gridpane;

	public MainmenuController()
	{

	}

	@FXML
	@Override
	public void initialize()
	{
		ArrayList<Movie> movies = DataManager.getInstance().getMovies();

		for (int i = 0; i < movies.size(); i++)
		{
			Movie movie = movies.get(i);

			MovieBox box = new MovieBox();
			box.setInfo(movie);

			mainmenu_gridpane.setVgap(20);
			mainmenu_gridpane.setHgap(20);
			mainmenu_gridpane.addRow(((int) (i / 6)), box);
		}
	}
}
