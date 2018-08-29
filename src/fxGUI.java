import java.awt.Desktop;

import java.io.FileInputStream;
import java.io.FileNotFoundException;


import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import javafx.stage.Modality;
import javafx.stage.Stage;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


public class fxGUI extends Application {
	//private Desktop desktop = Desktop.getDesktop();
	private Label infoLabel1 = new Label("-");
	//private Label infoLabel2 = new Label("-");

	private BorderPane bp = new BorderPane();
	private ImageFiles imageFile = new ImageFiles();

	public static void main(String[] args) {
    Application.launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws FileNotFoundException {

	primaryStage.setTitle("Brain Tumor recognition");

    //dodavanje komponenti na scenu
   // bp.setCenter(imageView);
    bp.setTop(menu(primaryStage));
    Scene scene = new Scene(bp, 800, 500);
    primaryStage.setScene(scene);
    primaryStage.show();
  }
  
  //Ovde smo dodali meni
  public MenuBar menu(Stage primaryStage) {
	  MenuBar menuBar = new MenuBar();
	  Menu file = new Menu("File");
	  MenuItem importImage = new MenuItem("Import");
	  Menu analisys = new Menu("Analysis");
	  MenuItem tumRecognition = new MenuItem("Tumor Recognition");

	  analisys.getItems().addAll(tumRecognition);
	  file.getItems().add(importImage);
	  menuBar.getMenus().addAll(file,analisys);

	  //otvaranje file explorea za import slike
	  importImage.setOnAction(new EventHandler<ActionEvent>() {

		@Override
		public void handle(ActionEvent event) {
			ImageView image = imageFile.postImage(primaryStage);
			bp.setCenter(image);
		}
	  });

	 //otvaranje novog prozora za prepoznavanje tumora
	  tumRecognition.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) { secondWindow(primaryStage, "OK"); }
		  });
	  return menuBar;
  }
  public void secondWindow(Stage primaryStage,String buttonName) {

		VBox vbox = new VBox();
		
		Scene secondScene = new Scene(vbox,400,250);
	
		Stage secondWindow = new Stage();
		secondWindow.setResizable(false);
		secondWindow.setTitle("Tumor Recognition");
		secondWindow.setScene(secondScene);
		secondWindow.initModality(Modality.WINDOW_MODAL);
		secondWindow.initOwner(primaryStage);

		secondWindow.setX(primaryStage.getX() + 200);
		secondWindow.setY(primaryStage.getY() + 100);

	  	infoLabel1.setTextFill(Color.BLUE);


		//postavljanje slajdera na svakom prozoru
		vbox.setPadding(new Insets(20));
		vbox.setSpacing(20);
		Label label1 = new Label("Threshold Value: ");

		vbox.getChildren().addAll(label1,slidersThreshold(),infoLabel1);
		Button button = new Button(buttonName);
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
                changeCollor();
                FileInputStream input = null;
                try {
                    input = new FileInputStream("C:/Users/Nikola/Desktop/red.bmp");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Image image = new Image(input);
                ImageView threshImage = new ImageView(image);
                bp.setCenter(threshImage);
			    secondWindow.close();
			}
		});
		button.setPrefSize(90, 10);
		vbox.getChildren().addAll(button);
		
		secondWindow.show();
  }
  //Kreairanje slajdera za threshold i upravanje njime
  public Slider slidersThreshold() {

	  Slider slider= new Slider();
	  slider.setMin(0);
	  slider.setMax(255);
	  slider.setShowTickLabels(true);
      slider.setShowTickMarks(true);
      slider.setBlockIncrement(10);

	  //lisner za menjanje thresholda
      slider.valueProperty().addListener(new ChangeListener<Number>() {
		  @Override
		  public void changed(ObservableValue<? extends Number> observable,
							  Number oldValue, Number newValue) {
			   infoLabel1.setText("Threshold Value: " + newValue);

			  threshold(imageFile.getFilePath(),newValue.intValue());

			  FileInputStream input = null;
			  try {
				  input = new FileInputStream(new Threshold().getDestFolder());
			  } catch (FileNotFoundException e) {
				  e.printStackTrace();
			  }
			  Image image = new Image(input);
			  ImageView threshImage = new ImageView(image);
			  bp.setCenter(threshImage);

		  }
	  });
      
      return slider;
  }

  public void threshold(String inputBMP,int valueOfThresh) {
	  Threshold thr = new Threshold(inputBMP,valueOfThresh);
	  thr.threshold();
  }
  public void changeCollor() {
      System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

      String destFolder = "C:/Users/Nikola/Desktop/red.bmp";
      // Reading the Image from the file and storing it in to a Matrix object
      String file =  "C:/Users/Nikola/Desktop/craniumRemovalThreshold.bmp";
      Mat src = Imgcodecs.imread(file);

      // Creating an empty matrix to store the result
     // Mat dst = new Mat();

      //160 default
      //Imgproc.threshold(src, dst, valueOfThresh, 255, Imgproc.THRESH_BINARY);

      for (int i = 0; i < src.height(); i++) {
          for (int j = 0; j < src.width(); j++) {
          	double[] red = {0,0,255};
              if( src.get(i,j)[0] == 255 && src.get(i,j)[1] == 255 && src.get(i,j)[2] == 255 ) {
                  src.put(i, j, red);
              }

          }
      }

      // Writing the image
      Imgcodecs.imwrite(destFolder, src);

      // System.out.println("Image Processed");
  }
  
}
