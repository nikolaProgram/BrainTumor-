import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
/*
Ova klassa sluzi za otvaranje fajlova, odnosno za import slike.
 */
public class ImageFiles {
	private String filePath;
	

	//metoda za otvaranje fajla
	  private String fileOpen(Stage primaryStage) {
		  
		 
		  final FileChooser fileChooser = new FileChooser();
		  File file = fileChooser.showOpenDialog(primaryStage);
		  
		  if(file != null) {
			  filePath = file.getAbsolutePath();
			  return file.getAbsolutePath();
		  }
		  return "Can't return file path!!";
		}
	  
	  public ImageView postImage(Stage primaryStage){
		    
		  //mozda nije dobra ideja null
			FileInputStream input = null;
			try {
				input = new FileInputStream(fileOpen(primaryStage));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

		    //ovde postaviti po moguctstvu neku sliku bzvz
		    Image image = new Image(input);
		    ImageView imageView = new ImageView(image);
		    
		    return imageView;
	  }

	public String getFilePath() {
		return filePath;
	}
	  
}
