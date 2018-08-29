import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Threshold {
		
		//ovo se mora izmeniti tako da korinsik unosi zeljeni folder
		private String destFolder = "C:/Users/Nikola/Desktop/craniumRemovalThreshold.bmp";
		private String inputBMP;
		private int valueOfThresh;
		
		
		
		public Threshold(String inputBMP, int valueOfThresh) {
			this.inputBMP = inputBMP;
			this.valueOfThresh = valueOfThresh;
		}

		public Threshold() {
		}

	public void threshold()  {
		try {
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

			// Reading the Image from the file and storing it in to a Matrix object
			String file = inputBMP;
			Mat src = Imgcodecs.imread(file);

			// Creating an empty matrix to store the result
			Mat dst = new Mat();
			//160 default
			Imgproc.threshold(src, dst, valueOfThresh, 255, Imgproc.THRESH_BINARY);

			// Writing the image
			Imgcodecs.imwrite(destFolder, dst);

			// System.out.println("Image Processed");
		}
		catch (Exception e){
			System.out.println("Error: " + e.getMessage());
		}
	}

		public String getInputBMPImage() {
			return inputBMP;
		}

	public String getDestFolder() {
		return destFolder;
	}

	public void setDestFolder(String destFolder) {
			this.destFolder = destFolder;
		} 
	
}
