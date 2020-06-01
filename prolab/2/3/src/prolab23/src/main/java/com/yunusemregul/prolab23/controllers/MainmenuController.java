package com.yunusemregul.prolab23.controllers;

import com.yunusemregul.prolab23.App;
import com.yunusemregul.prolab23.DataManager;
import com.yunusemregul.prolab23.Movie;
import com.yunusemregul.prolab23.User;
import com.yunusemregul.prolab23.components.MovieBox;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Ana ekranın yani filmlerin izlenebileceği, aranabileceği ekranın kontrollerini sağlayan
 * sınıf.
 */
public class MainmenuController extends GeneralController
{
	/*
	  İlk başta ana ekrandaki komponentlere ulaşım için gerekli değişkenleri
	  tanımlıyoruz.
	 */

	@FXML
	private GridPane mainmenu_gridpane; // Filmlerin ekleneceği grid paneli

	@FXML
	private Button button_movies; // FİLM butonu
	@FXML
	private Button button_series; // DİZİ butonu
	@FXML
	private Button button_all; // TÜMÜ butonu

	/*
	  Arama bölümü için komponentler
	 */
	@FXML
	private TextField search_field; // Arama bölümü
	@FXML
	private Button button_search_movie_name; // FİLM ADI butonu
	@FXML
	private Button button_search_type; // TÜR butonu

	@FXML
	private Text user_name; // Sağ üstteki kullanıcı adı yazısı

	private ArrayList<Movie> movies; // Ana ekranda gösterilecek tüm filmler
	private ArrayList<Movie> shownMovies; // Kullanıcının tıkladığı FİLM ya da DİZİ kriterine göre gösterilmiş tüm filmler

	private Button selectedKind; // Kullanıcının FİLM ya da DİZİ ya da TÜMÜ butonlarından hangisini seçtiği
	private Button selectedSearchType; // Kullanıcının TÜR ya da FİLM ADI butonlarından hangisini seçtiği

	private String searchString = ""; // Aranacak filmlerin içermesi gereken yazı

	public MainmenuController()
	{

	}

	/**
	 * Parametre olarak verilen filme ait izleme sayfasını açar.
	 *
	 * @param movie izlenecek film
	 */
	public static void openWatchmenu(Movie movie)
	{
		User.getInstance().setMovie(movie);
		try
		{
			// Filmi izleme sayfasını aç
			App.setRoot("watchmenu");
		} catch (IOException exception)
		{
			exception.printStackTrace();
		}
	}

	/**
	 * Ana menü sayfası oluşturulduğunda olacak işlemler.
	 */
	@FXML
	@Override
	public void initialize()
	{
		user_name.setText(User.getInstance().name); // Sağ üstteki kullanıcı adını kullanıcının adı yap
		movies = DataManager.getInstance().getMovies(); // Gösterilecek filmler
		shownMovies = new ArrayList<>(); // Gösterilmiş filmler

		// Bu sayfa ilk oluşturulduğunda default olarak FİLM ADI na göre arama yap
		selectSearchType(button_search_movie_name);

		showAll(); // Tüm filmleri göster

		// Arama bölümündeki yazı değiştiğinde arama yapılması için
		search_field.textProperty().addListener(new ChangeListener()
		{
			@Override
			public void changed(ObservableValue observable, Object oldValue, Object newValue)
			{
				searchString = newValue.toString().toLowerCase();
				showOnlySearched();
			}
		});
	}

	/**
	 * Ana ekranda arama yaparken aramanın neye göre yapılacağını belirleyen metot. Bu
	 * metoda 'tür' butonu parametre olarak verildiyse türe göre arama yapılır, 'film adı'
	 * butonu parametre olarak verildiyse film ismine göre arama yapılır ve sonuçlar
	 * gösterilir.
	 *
	 * @param selected seçilen arama yöntemi ('tür' veya 'film adı' butonu)
	 */
	public void selectSearchType(Button selected)
	{
		if (selectedSearchType != null)
		{
			selectedSearchType.setStyle("");
		}

		selected.setStyle("-fx-background-color: #664AC9;");
		selectedSearchType = selected;
	}

	/**
	 * Arama menüsünde TÜR butonuna tıklandığında çağrılan metot.
	 */
	public void searchType()
	{
		selectSearchType(button_search_type);
	}

	/**
	 * Arama mensünde FİLM ADI butonuna tıklandığında çağrılan metot.
	 */
	public void searchMovieName()
	{
		selectSearchType(button_search_movie_name);
	}

	/**
	 * Arama yöntemine göre filmları arayıp gösteren metot.
	 */
	public void showOnlySearched()
	{
		mainmenu_gridpane.getChildren().clear(); // Ekranda gösterilen filmleri temizle

		int count = 0; // Ekrana kaç tane film eklendiği
		for (Movie movie : shownMovies) // Gösterilmiş tüm film ya da diziler için
		{
			// Eğer arama şekli TÜR e göre ise ve film türünde aranan yazı yoksa bu filmi geç
			if (selectedSearchType.getText().equals("TÜR") && !movie.type.toLowerCase().contains(searchString))
			{
				continue;
			}

			// Eğer arama şekli FİLM ADI na göre ise ve film adında aranan yazı yoksa bu filmi geç
			if (selectedSearchType.getText().equals("FİLM ADI") && !movie.name.toLowerCase().contains(searchString))
			{
				continue;
			}

			// Ana ekrana eklenecek film için kutusunu oluştur
			MovieBox box = new MovieBox();
			box.setInfo(movie); // Oluşturulan kutunun bilgilerini film bilgileri yap

			mainmenu_gridpane.setVgap(20);
			mainmenu_gridpane.setHgap(20);
			mainmenu_gridpane.addRow(count / 6, box); // Gride ekle

			count++;
		}
	}

	/**
	 * Ana menüdeki DİZİ FİLM TÜMÜ butonlarına tıklandığında bu metot ilgili parametreyle
	 * çağrılır. Ekrana seçilen tipteki şeyleri getirir. Uygulamam boyunca kind dediğim
	 * şey tip, type dediğim şey de tür.
	 *
	 * @param kind gösterilecek tip
	 */
	public void showOnlyKind(String kind)
	{
		mainmenu_gridpane.getChildren().clear(); // Ekranda gösterilen filmleri temizle
		shownMovies.clear(); // Gösterilen film listesini temizle, arama yaparken buna göre yapılıyor

		int count = 0; // Ekrana kaç tane film eklendiği
		for (Movie movie : movies) // Her film için
		{
			if (kind != null && !movie.kind.contains(kind)) // Eğer film tipi gösterilecek tipte değilse geç
			{
				continue;
			}

			// Ana ekrana eklenecek film için kutusunu oluştur
			MovieBox box = new MovieBox();
			box.setInfo(movie); // Oluşturulan kutunun bilgilerini film bilgileri yap

			mainmenu_gridpane.setVgap(20);
			mainmenu_gridpane.setHgap(20);
			mainmenu_gridpane.addRow(count / 6, box);

			shownMovies.add(movie);

			count++;
		}

		showOnlySearched();
	}

	/**
	 * DİZİ FİLM TÜMÜ butonlarından herhangi birine tıklandığında önceki tıklanmış olanın
	 * arka planını normal hale çeviren, yeni tıklanmış olanın arka planını da mor yapan
	 * metot.
	 *
	 * @param selected tıklanmış olan buton
	 */
	private void selectKind(Button selected)
	{
		if (selectedKind != null)
		{
			selectedKind.setStyle("");
		}

		selected.setStyle("-fx-background-color: #664AC9;");
		selectedKind = selected;
	}

	/**
	 * Ana menüde sadece filmleri göstermek için FİLM butonuna tıklandığında çağrılan
	 * metot.
	 */
	public void showOnlyMovies()
	{
		selectKind(button_movies);
		showOnlyKind("Film");
	}

	/**
	 * Ana menüde sadece dizileri göstermek için DİZİ butonuna tıklandığında çağrılan
	 * metot.
	 */
	public void showOnlySeries()
	{
		selectKind(button_series);
		showOnlyKind("Dizi");
	}

	/**
	 * Ana menüde tüm şeyleri göstermek için TÜMÜ butonuna tıklandığında çağrılan metot.
	 */
	public void showAll()
	{
		selectKind(button_all);
		showOnlyKind(null);
	}
}
