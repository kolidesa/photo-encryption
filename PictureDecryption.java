import java.io.File;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO; 
import java.util.*; 
public class PictureDecryption {
	public static void main(String[] args) throws Exception {
		Scanner scan = new Scanner(System.in);
		try {
			
			System.out.println("Enter the file location for the first image: ");
			String loc = scan.nextLine();
			BufferedImage image1 = ImageIO.read(new File(loc));
			
			System.out.println("Enter the file location for the second image: ");
			loc = scan.nextLine();
			BufferedImage image2 = ImageIO.read(new File(loc));
			
			System.out.println("Enter the file location for the third image: ");
			loc = scan.nextLine();
			BufferedImage image3 = ImageIO.read(new File(loc));
			
			System.out.println("Enter the key");
		    String key = scan.nextLine();
		    if (key.length() > 16)
		    	key = key.substring(0, 16);
		    
		    System.out.println("Enter the IV");
		    int iv = scan.nextInt();
			
			System.out.println("Processing images...");
		    
		    int imageWidth = 0, imageHeight = 0, messageSize = 0;
			
		    int[] keyVals = new int[16];
		    for (int i = 0; i < 16; i++) {
		    	keyVals[i] = (int)key.charAt(i);
		    }
		    
		    int insert = ((((keyVals[0] + keyVals[1]) * (keyVals[2] + keyVals[3])) +
		    		((keyVals[4] + keyVals[5]) * (keyVals[6] + keyVals[7]))) 
		    		% Math.abs((keyVals[8] + keyVals[9]) * (keyVals[10] + keyVals[11]) - (keyVals[12] + keyVals[13]) * (keyVals[14] + keyVals[15])));   
		    
						
			ArrayList<Integer> list1 = convertList(image1);
			ArrayList<Integer> list2 = convertList(image2);
			ArrayList<Integer> list3 = convertList(image3);
			
			ArrayList<Integer> pic1 = new ArrayList<Integer>();
			ArrayList<Integer> pic2 = new ArrayList<Integer>();
			ArrayList<Integer> pic3 = new ArrayList<Integer>();
			
			pic1=list1;
			pic2=list2;
			pic3=list3;
			
			if ((list1.get(0) & 0xff) == 0) {
				list1.remove(0);
				pic1 = list1;

				if ((list2.get(0) & 0xff) == 1) {
					list2.remove(0);
					pic2 = list2;
					list3.remove(0);
					pic3 = list3;
				}
				else if ((list3.get(0) & 0xff) == 1) {
					list3.remove(0);
					pic2 = list3;
					list2.remove(0);
					pic3 = list2;
				}
				
			}
			else if ((list2.get(0) & 0xff) == 0) {
				list2.remove(0);
				pic1 = list2;
				
				if ((list1.get(0) & 0xff) == 1) {
					list1.remove(0);
					pic2 = list1;
					list3.remove(0);
					pic3 = list3;
				}
				else if ((list3.get(0) & 0xff) == 1) {
					list3.remove(0);
					pic2 = list3;
					list1.remove(0);
					pic3 = list1;
				}
			}
			else if ((list3.get(0) & 0xff) == 0) {
				list3.remove(0);
				pic1 = list3;
				
				if ((list1.get(0) & 0xff) == 1) {
					list1.remove(0);
					pic2 = list1;
					list2.remove(0);
					pic3 = list2;
				}
				else if ((list2.get(0) & 0xff) == 1) {
					list2.remove(0);
					pic2 = list2;
					list1.remove(0);
					pic3 = list1;
				}
			}
			short temp1 = pic1.get(0).shortValue();
			imageWidth = temp1;
			pic1.remove(0);
			temp1 = pic2.get(0).shortValue();
			imageHeight = temp1;
			pic2.remove(0);
			messageSize = (pic3.get(0)&0xff);
			pic3.remove(0);
			
			BufferedImage picture = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
			
			for (int y = 0; y < imageHeight; y++) {
				for (int x = 0; x < imageWidth; x++) {
					if ((insert + (x * y)) % 3 == 0) {
						picture.setRGB(x, y, pic1.get(0));
						pic1.remove(0);
					}
					else if ((insert + (x * y)) % 3 == 1) {
						picture.setRGB(x, y, pic2.get(0));
						pic2.remove(0);
					}
					else if ((insert + (x * y)) % 3 == 2) {
						picture.setRGB(x, y, pic3.get(0));
						pic3.remove(0);
					}
				}
				
			} 
			
			//Finding the message
			
			char[] messageHex = new char[messageSize];
			
		    int y1 = 0;
		    int x1 = 0;		    
		    if (iv < picture.getWidth()) {
		    	x1 = iv;
		    	y1 = 0;
		    }
		    else if (iv > picture.getWidth() * picture.getHeight()) {
		    	x1 = iv % (picture.getWidth() * picture.getHeight()) % picture.getWidth();
		    	y1 = iv % (picture.getWidth() * picture.getHeight()) / picture.getWidth();
		    }
		    else {
		    	x1 = iv % picture.getWidth();
		    	y1 = (int)(iv/picture.getWidth());
		    }
			
		    int count = 0;
		    int temp = 0;
		    while (temp < messageHex.length) {
		    	count++;
		    	if (count == insert) {
		    		messageHex[temp] = (char)picture.getRGB(x1, y1);
		            count = 0;
		            temp++;
		    	}
		    	
		    	if (x1 == picture.getWidth()) {
		    		x1 = 0;
		    		if (y1 == picture.getHeight()) {
			    		y1 = 0;
			    	}
		    		else
		    			y1++;
		    	}
		    	else {
		    		x1++;
		    	}
		    }
			
			String message = "";
			for (int i = 0; i < messageHex.length; i++) {
				message += (char)(messageHex[i] & 0xff);
			}
			System.out.println("Processing complete.");
			System.out.println("The message is: " + message);
		}
		catch (Exception e) {
			System.out.println(e.toString());
		}
		scan.close();
	}
	public static ArrayList<Integer> convertList(BufferedImage pic) {
		ArrayList<Integer> vals = new ArrayList<Integer>();
		for (int y = 0; y < pic.getHeight(); y++) {
			for (int x = 0; x < pic.getWidth(); x++) {
				Color c = new Color(pic.getRGB(x, y), true);
				vals.add(c.getRGB());
			}
		}
		return vals;
	}
}