package com.yunusemregul.prolab23.controllers;

import com.yunusemregul.prolab23.DataManager;
import com.yunusemregul.prolab23.Movie;
import com.yunusemregul.prolab23.components.MovieBox;
import java.util.ArrayList;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class MainmenuController extends GeneralController
{

	@FXML
	private GridPane mainmenu_gridpane;

	@FXML
	private Button button_movies;
	@FXML
	private Button button_series;
	@FXML
	private Button button_all;
	
	@FXML
	private Button button_search_movie_name;
	@FXML
	private Button button_search_type;
	
	@FXML
	private TextField search_field;

	private ArrayList<Movie> movies;
	private ArrayList<Movie> shownMovies;
	
	private Button selectedKind;
	private Button selectedSearchType;
	
	private String searchString = "";

	public MainmenuController()
	{

	}

	@FXML
	@Override
	public void initialize()
	{
		movies = DataManager.getInstance().getMovies();
		shownMovies = new ArrayList<>();

		showAll();

		search_field.textProperty().addListener(new ChangeListener()
		{
			@Override
			public void changed(ObservableValue observable, Object oldValue, Object newValue)
			{
				searchString = newValue.toString().toLowerCase();
				showOnlySearched();
			}
		});
		
		selectSearchType(button_search_movie_name);
	}
	
	public void selectSearchType(Button selected)
	{
		if(selectedSearchType!=null)
			selectedSearchType.setStyle("");
		
		selected.setStyle("-fx-background-color: #664AC9;");
		selectedSearchType = selected;
	}
	
	public void searchType()
	{
		selectSearchType(button_search_type);
	}
	
	public void searchMovieName()
	{
		selectSearchType(button_search_movie_name);
	}
	
	public void showOnlySearched()
	{
		if(searchString.length()==0)
			return;
		
		mainmenu_gridpane.getChildren().clear();
		
		int count = 0;
		for (Movie movie : shownMovies)
		{
			if (selectedSearchType.getText().equals("TÜR") && !movie.type.toLowerCase().contains(searchString))
			{
				continue;
			}
			
			if (selectedSearchType.getText().equals("FİLM ADI") && !movie.name.toLowerCase().contains(searchString))
			{
				continue;
			}

			MovieBox box = new MovieBox();
			box.setInfo(movie);

			mainmenu_gridpane.setVgap(20);
			mainmenu_gridpane.setHgap(20);
			mainmenu_gridpane.addRow(((int) (count / 6)), box);

			count++;
		}
	}

	public void showOnlyKind(String kind)
	{
		mainmenu_gridpane.getChildren().clear();
		shownMovies.clear();

		int count = 0;
		for (Movie movie : movies)
		{
			if (kind != null && !movie.kind.contains(kind))
			{
				continue;
			}

			MovieBox box = new MovieBox();
			box.setInfo(movie);

			mainmenu_gridpane.setVgap(20);
			mainmenu_gridpane.setHgap(20);
			mainmenu_gridpane.addRow(((int) (count / 6)), box);

			shownMovies.add(movie);

			count++;
		}
		
		showOnlySearched();
	}

	private void selectKind(Button selected)
	{
		if(selectedKind!=null)
			selectedKind.setStyle("");
		
		selected.setStyle("-fx-background-color: #664AC9;");
		selectedKind = selected;
	}

	public void showOnlyMovies()
	{
		selectKind(button_movies);
		showOnlyKind("Film");
	}

	public void showOnlySeries()
	{
		selectKind(button_series);
		showOnlyKind("Dizi");
	}

	public void showAll()
	{
		selectKind(button_all);
		showOnlyKind(null);
	}
}
