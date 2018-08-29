import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import org.opencv.highgui.HighGui;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
public class Main {
	public static void grayscale(String inputBMP) {
	      try {
	          System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
	          File input = new File(inputBMP);
	          BufferedImage image = ImageIO.read(input);	

	          byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
	          Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
	          mat.put(0, 0, data);

	          Mat mat1 = new Mat(image.getHeight(),image.getWidth(),CvType.CV_8UC1);
	          Imgproc.cvtColor(mat, mat1, Imgproc.COLOR_RGB2GRAY);

	          byte[] data1 = new byte[mat1.rows() * mat1.cols() * (int)(mat1.elemSize())];
	          mat1.get(0, 0, data1);
	          BufferedImage image1 = new BufferedImage(mat1.cols(),mat1.rows(), BufferedImage.TYPE_BYTE_GRAY);
	          image1.getRaster().setDataElements(0, 0, mat1.cols(), mat1.rows(), data1);

	          File ouptut = new File("grayscale.bmp");
	          ImageIO.write(image1, "bmp", ouptut);
	          
	       } catch (Exception e) {
	          System.out.println("Error: " + e.getMessage());
	       }
	}
	
	public static void dilatation(String inputBMP) {
		 // Loading the OpenCV core library
	      System.loadLibrary( Core.NATIVE_LIBRARY_NAME );

	      // Reading the Image from the file and storing it in to a Matrix object
	      String file =inputBMP;
	      Mat src = Imgcodecs.imread(file);

	      // Creating an empty matrix to store the result
	      Mat dst = new Mat();

	      // Preparing the kernel matrix object 
	      Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, 
	         new  Size((2*2) + 1, (2*2)+1));

	      // Applying dilate on the Image
	      Imgproc.dilate(src, dst, kernel);

	      // Writing the image
	      Imgcodecs.imwrite("dilation.bmp", dst);

	      System.out.println("Image Processed -Dilatation");
	}
	public static void erosion(String inputBMP,int size) {
	     // Loading the OpenCV core library
      System.loadLibrary( Core.NATIVE_LIBRARY_NAME );

      // Reading the Image from the file and storing it in to a Matrix object
      String file =inputBMP;
      Mat src = Imgcodecs.imread(file);

      // Creating an empty matrix to store the result
      Mat dst = new Mat();

      // Preparing the kernel matrix object
      Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, 
         new  Size(size,size));//(2*2) + 1, (2*2)+1)) //za uklanjanje lobanje 29;

      // Applying erode on the Image
      Imgproc.erode(src, dst, kernel);

      // Writing the image
      Imgcodecs.imwrite("erosion.bmp", dst);

      System.out.println("Image processed - Errosion");
	}
	public static void threshold(String inputBMP,int valueOfThresh)  {
		   
	      System.loadLibrary( Core.NATIVE_LIBRARY_NAME );

	      // Reading the Image from the file and storing it in to a Matrix object
	      String file =inputBMP;
	      Mat src = Imgcodecs.imread(file);

	      // Creating an empty matrix to store the result
	      Mat dst = new Mat();
	      //160 default
	      Imgproc.threshold(src, dst, valueOfThresh, 255, Imgproc.THRESH_BINARY);

	      // Writing the image
	      Imgcodecs.imwrite("threshold.bmp", dst);

	      System.out.println("Image Processed");
	}
	
	
	public static void craniumRemoval(String maskFile,String mriFile) {
	 System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

     // will read image
     Mat image = Imgcodecs.imread(mriFile);
     Mat mask = Imgcodecs.imread(maskFile,Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);

     Rect rectangle = new Rect(10, 10, image.cols() - 20, image.rows() - 20);

     Mat bgdModel = new Mat(); // extracted features for background
     Mat fgdModel = new Mat(); // extracted features for foreground
     Mat source = new Mat(1, 1, CvType.CV_8U, new Scalar(0));
     convertToOpencvValues(mask);

     int iterCount = 1;
     Imgproc.grabCut(image, mask, rectangle, bgdModel, fgdModel, iterCount, Imgproc.GC_INIT_WITH_MASK);

     convertToHumanValues(mask);
     //Imgproc.threshold(mask,mask,128,255,Imgproc.THRESH_TOZERO);

     Mat foreground = new Mat(image.size(), CvType.CV_8UC1, new Scalar(0, 0, 0));
     image.copyTo(foreground, mask);

     Imgcodecs.imwrite("craniumRemoval.bmp", foreground);
	}
	
	private static void convertToHumanValues(Mat mask) {
        byte[] buffer = new byte[3];
        for (int x = 0; x < mask.rows(); x++) {
            for (int y = 0; y < mask.cols(); y++) {
                mask.get(x, y, buffer);
                int value = buffer[0];
                if (value == Imgproc.GC_BGD) {
                    buffer[0] = 0; // for sure background
                } else if (value == Imgproc.GC_PR_BGD) {
                    buffer[0] = 85; // probably background
                } else if (value == Imgproc.GC_PR_FGD) {
                    buffer[0] = (byte) 170; // probably foreground
                } else {
                    buffer[0] = (byte) 255; // for sure foreground

                }
                mask.put(x, y, buffer);
            }
        }
    }

    /**
     * Converts level of grayscale into OpenCV values. White - foreground, Black
     * - background.
     * 
     * @param mask
     */
    private static void convertToOpencvValues(Mat mask) {
        byte[] buffer = new byte[3];
        for (int x = 0; x < mask.rows(); x++) {
            for (int y = 0; y < mask.cols(); y++) {
                mask.get(x, y, buffer);
                int value = buffer[0];
                if (value >= 0 && value < 64) {
                    buffer[0] = Imgproc.GC_BGD; // for sure background
                } else if (value >= 64 && value < 128) {
                    buffer[0] = Imgproc.GC_PR_BGD; // probably background
                } else if (value >= 128 && value < 192) {
                    buffer[0] = Imgproc.GC_PR_FGD; // probably foreground
                } else {
                    buffer[0] = Imgproc.GC_FGD; // for sure foreground

                }
                mask.put(x, y, buffer);
            }
        }
    }
	public static void medianBlur(String inputBMP) {
		try {
			System.loadLibrary( Core.NATIVE_LIBRARY_NAME );


			Mat source = Imgcodecs.imread(inputBMP);

			Mat destination = new Mat();
			Imgproc.medianBlur(source,destination, 9); //mogao bi se dodati prozor za unosenje vrednosti


			Imgcodecs.imwrite( "medianBlur.bmp" , destination);
			System.out.println("Filter uradjen");

		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}


	}

	public static void main(String[] args) {

		//medianBlur("TEST.bmp");
	
		threshold("TEST.bmp",120);
//		erosion("threshold.bmp",40); //sve do ovde se pravi maska
//
//		craniumRemoval("erosion.bmp","TEST.bmp");
//
//
//
//		grayscale("craniumRemoval.bmp");
//		medianBlur("grayscale.bmp");
//
//		threshold("medianBlur.bmp",125);
//		erosion("threshold.bmp",9);
//
//		dilatation("erosion.bmp");
		
	}

}
