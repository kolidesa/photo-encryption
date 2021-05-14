import java.io.File; 
import java.io.IOException;
import java.security.SecureRandom;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO; 
import java.util.*; 
 
public class PictureEncryption {
	public static void main(String args[]) throws Exception 
	{
		Scanner scan = new Scanner(System.in); 
		BufferedImage image = null; 
	
	    try
	    {
	    	//Read input file
	    	System.out.println("Enter the image you would like to use: ");
	    	String imageFile = scan.nextLine();
	        image = ImageIO.read(new File(imageFile));

	        //Get message to be placed into photo
	        System.out.println("Enter the message: ");
	        String message = scan.nextLine();
		        
		    //Convert the message into equivalent ASCII
		    ArrayList<Integer> messageHex = new ArrayList<Integer>();
		    for (int i = 0; i < message.length(); i++) {
		    	messageHex.add((int)message.charAt(i));
		    }
	            
		    //Get key and IV 
		    System.out.println("Would you like to enter your own key? (Y/N)");
		    char opt = scan.next().charAt(0);
		    scan.nextLine();
		    String key = "";
      		SecureRandom sr = new SecureRandom();
		    if (opt == 'Y' || opt == 'y') {
		    	System.out.println("Enter the key: ");
		      	key = scan.nextLine();
		      	if (key.length() > 16)
		      		key = key.substring(0, 16);
		      	else if (key.length() < 16) {
			    	for(int i= key.length(); i < 16; i++){
			    	    key += sr.nextInt(10);   
			    	}
		      	}
		    }
		    else {
		    	for(int i= 0; i < 16; i++){
		    	    key += sr.nextInt(10);   
		    	}
		    }
		    System.out.println("The key is: " + key);
		    
		    String iv = "";
		    System.out.println("Would you like to enter your own IV? (Y/N)");
		    opt = scan.next().charAt(0);
		    scan.nextLine();
		    if (opt == 'Y' || opt == 'y') {
		    	int flag = 0;
		    	while (flag == 0) {
		    		System.out.println("Enter the IV: ");
		    		iv = scan.nextLine();
		    		flag = 1;
		    		for (int i = 0; i < iv.length(); i++) {
		    			if ((int)iv.charAt(i) < 48 || (int)iv.charAt(i) > 57)
		    				flag = 0;
		    		}
		    		if (flag == 0)
		    			System.out.println("IV contains an invalid character, please enter another");
		    	}
		    }
		    else {
		    	for(int i= 0; i < sr.nextInt(10); i++){
		    	    iv += sr.nextInt(10);   
		    	}
		    }
		    if (iv.length() > 10)
		    	iv = iv.substring(0, 10);
		    System.out.println("The IV is: " + iv);
		    
		    System.out.println("Processing image...");
		    
		    int intIV = Integer.parseInt(iv);
		    
		    int y1 = 0;
		    int x1 = 0;		    
		    if (intIV < image.getWidth()) {
		    	x1 = intIV;
		    	y1 = 0;
		    }
		    else if (intIV > image.getWidth() * image.getHeight()) {
		    	x1 = intIV % (image.getWidth() * image.getHeight()) % image.getWidth();
		    	y1 = intIV % (image.getWidth() * image.getHeight()) / image.getWidth();
		    }
		    else {
		    	x1 = intIV % image.getWidth();
		    	y1 = (int)(intIV/image.getWidth());
		    }
		    
		    int[] keyVals = new int[16];
		    for (int i = 0; i < 16; i++) {
		    	keyVals[i] = (int)key.charAt(i);
		    }
		    
		    int insert = ((((keyVals[0] + keyVals[1]) * (keyVals[2] + keyVals[3])) +
		    		((keyVals[4] + keyVals[5]) * (keyVals[6] + keyVals[7]))) 
		    		% Math.abs((keyVals[8] + keyVals[9]) * (keyVals[10] + keyVals[11]) - (keyVals[12] + keyVals[13]) * (keyVals[14] + keyVals[15])));
		    
	        int counter = 0;
		    while (messageHex.size() > 0) {
		    	counter++;
		    	if (counter == insert) {
		    		image.setRGB(x1, y1, messageHex.get(0));
		            messageHex.remove(0);
		            counter = 0;
		    	}
		    	
		    	if (x1 == image.getWidth()) {
		    		x1 = 0;
		    		if (y1 == image.getHeight()) {
			    		y1 = 0;
			    	}
		    		else
		    			y1++;
		    	}
		    	else {
		    		x1++;
		    	}
		    }
		    
		    ArrayList<Integer> image1 = new ArrayList<Integer>();
		    ArrayList<Integer> image2 = new ArrayList<Integer>();
		    ArrayList<Integer> image3 = new ArrayList<Integer>();
		    
		    image1.add(0);
		    image1.add(image.getWidth());
		    image2.add(1);
		    image2.add(image.getHeight());
		    image3.add(2);
		    image3.add(message.length());
		    for (int y = 0; y < image.getHeight(); y++) {
		      	for (int x = 0; x < image.getWidth(); x++) {
		      		Color c = new Color(image.getRGB(x, y));
		       		if ((insert + (x * y)) % 3 == 0) {
		       			image1.add(c.getRGB());
		       		}
		       		else if ((insert + (x * y)) % 3 == 1) {
		      			image2.add(c.getRGB());
		       		}
		       		else if ((insert + (x * y)) % 3 == 2) {
		       			image3.add(c.getRGB());
		       		}
		       	}
		    }
		        
		    //Determine appropriate size of photo
		    int []size1 = detSize(image1);
		    int []size2 = detSize(image2);
		    int []size3 = detSize(image3);
		        
		    //Create output photos
	        File output_file1 = new File("output1.png"); 
	        File output_file2 = new File("output2.png"); 
	        File output_file3 = new File("output3.png"); 
		    ImageIO.write(createImage(image1, size1), "png", output_file1);
		    ImageIO.write(createImage(image2, size2), "png", output_file2);
		    ImageIO.write(createImage(image3, size3), "png", output_file3);
	  
		    System.out.println("The file names are: ");
		    System.out.println("output1.png");
		    System.out.println("output2.png");
		    System.out.println("output3.png");
		    System.out.println("Process complete."); 
		       
	        scan.close();
	    } 
	    catch(IOException e) 
	    { 
	        System.out.println("Error: "+e); 
	    }        
	}
	public static int[] detSize(ArrayList<Integer> a) {
	  	int[] len = new int[2];  	
	  	int sqrtVal = (int)Math.sqrt(a.size());
	    if (sqrtVal * sqrtVal == a.size()) {
	    	len[0] = len[1] = sqrtVal;
	    	return len;
	    }
	    else {
	    	len[0] = sqrtVal;
	    	len[1] = (a.size() / (sqrtVal-1));
	    }
	    return len;
	}
	public static BufferedImage createImage(ArrayList<Integer> vals, int[] measurements) {
	   	BufferedImage image = new BufferedImage(measurements[1], measurements[0], BufferedImage.TYPE_INT_RGB);
	   	for (int y = 0; y < image.getHeight(); y++) {
	   		for (int x = 0; x < image.getWidth(); x++) {
	   			
	   			if (vals.size() == 0) {
	   				image.setRGB(x, y, 0);
	   			}
	   			else {
	   				image.setRGB(x, y, vals.get(0));
	   				vals.remove(0);
	   			}
	   		}
	   	}
	   	return image;
	}
}
