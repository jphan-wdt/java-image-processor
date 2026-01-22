import java.io.*;
import java.util.TreeSet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;
import java.util.Random;
import java.util.Arrays;

public class Demo extends Component implements ActionListener {
    // WILLIAM THAI ec221013 220585417

    //************************************
    // List of the options(Original, Negative); correspond to the cases:
    //************************************

    String descs[] = {
            "Original", // 0
            "Negative",
            "Undo",
            "Rescale",
            "Shift",
            "Add Random Pixel Value", // 5
            "Arithmetic Addition",
            "Arithmetic Subtraction",
            "Arithmetic Multiplication",
            "Arithmetic Division",
            "Bitwise NOT", // 10
            "Bitwise AND",
            "Bitwise OR",
            "Bitwise XOR",
            "Image 2 Negative",
            "Negative Linear Transform", // 15
            "Logarithmic Function",
            "Power Law",
            "Random LUT",
            "Bit Plane",
            "Histogram Equalisation", // 20
            "Averaging",
            "Weighted Averaging",
            "4-neighbour Laplacian",
            "8-neighbour Laplacian",
            "4-neighbour Laplacian Enhancement", // 25
            "8-neighbour Laplacian Enhancement",
            "Roberts 1",
            "Roberts 2",
            "Sobel X",
            "Sobel Y", // 30
            "Salt and Pepper",
            "Min Filter",
            "Max Filter",
            "Mid Filter",
            "Median Filter", // 35
            "Simple Threshold",
            "Auto Threshold",

    };

    int opIndex;  //option index for
    int lastOp;

    private BufferedImage bi, bi2, biFiltered;   // the input image saved as bi;//
    int w, h;
    private BufferedImage prevImage;
    static String filename = "Goldhill.tif";
    float scale = 1.0f;
    int shift = 0;
    double pConstant;
    int plane;
    static boolean roiMode = false;
    int threshold;

    public Demo() {
        try {
            bi = ImageIO.read(new File("src/images/"+filename));
            bi2 = bi;

            w = bi.getWidth(null);
            h = bi.getHeight(null);
            System.out.println(bi.getType());
            if (bi.getType() != BufferedImage.TYPE_INT_RGB) {
                BufferedImage bi2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics big = bi2.getGraphics();
                big.drawImage(bi, 0, 0, null);
                biFiltered = bi = bi2;
            }

        } catch (IOException e) {      // deal with the situation that th image has problem;/
            System.out.println("Image could not be read");
            System.out.println("Working dir: " + System.getProperty("user.dir"));

            System.exit(1);
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(w * 3, h);
    }


    String[] getDescriptions() {
        return descs;
    }

    // Return the formats sorted alphabetically and in lower case
    public String[] getFormats() {
        String[] formats = {"bmp","gif","jpeg","jpg","png"};
        TreeSet<String> formatSet = new TreeSet<String>();
        for (String s : formats) {
            formatSet.add(s.toLowerCase());
        }
        return formatSet.toArray(new String[0]);
    }

    void setOpIndex(int i) {
        opIndex = i;
    }

    public void paint(Graphics g) { //  Repaint will call this function so the image will change.
        filterImage();
        BufferedImage biDisplay = displayImages();

        g.drawImage(biDisplay, 0, 0, null);
        // g.drawImage(biFiltered, 0, 0, null);
    }


    //************************************
    //  Convert the Buffered Image to Array
    //************************************
    private static int[][][] convertToArray(BufferedImage image){
        int width = image.getWidth();
        int height = image.getHeight();

        int[][][] result = new int[width][height][4];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = image.getRGB(x,y);
                int a = (p>>24)&0xff;
                int r = (p>>16)&0xff;
                int g = (p>>8)&0xff;
                int b = p&0xff;

                result[x][y][0]=a;
                result[x][y][1]=r;
                result[x][y][2]=g;
                result[x][y][3]=b;
            }
        }
        return result;
    }

    //************************************
    //  Convert the  Array to BufferedImage
    //************************************
    public BufferedImage convertToBimage(int[][][] TmpArray){

        int width = TmpArray.length;
        int height = TmpArray[0].length;

        BufferedImage tmpimg=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                int a = TmpArray[x][y][0];
                int r = TmpArray[x][y][1];
                int g = TmpArray[x][y][2];
                int b = TmpArray[x][y][3];

                //set RGB value

                int p = (a<<24) | (r<<16) | (g<<8) | b;
                tmpimg.setRGB(x, y, p);

            }
        }
        return tmpimg;
    }


    //************************************
    //  Example:  Image Negative
    //************************************
    public BufferedImage ImageNegative(int[][][] ImageArray){
        int width = ImageArray.length;
        int height = ImageArray[0].length;

        // int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array

        // Image Negative Operation:
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                if (ImageArray[x][y][0] == 255) {
                    ImageArray[x][y][1] = 255-ImageArray[x][y][1];  //r
                    ImageArray[x][y][2] = 255-ImageArray[x][y][2];  //g
                    ImageArray[x][y][3] = 255-ImageArray[x][y][3];  //b
                }
            }
        }

        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }


    //************************************
    //  Your turn now:  Add more function below
    //************************************

    public BufferedImage displayImages(){
        int width = bi.getWidth();
        int width2 = bi2.getWidth();
        int height = bi.getHeight();
        int height2 = bi2.getHeight();

        width = Math.min(width, width2);
        height = Math.min(height, height2);

        int[][][] ImageArray = new int[(width * 3)][height][4];
        int[][][] ImageArray1 = convertToArray(bi);
        int[][][] ImageArray2 = convertToArray(biFiltered);
        int[][][] ImageArray3 = convertToArray(bi2);

        // lecture 2 slide
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray[x][y][1] = ImageArray1[x][y][1]; //r image 1
                ImageArray[x][y][2] = ImageArray1[x][y][2]; //g
                ImageArray[x][y][3] = ImageArray1[x][y][3]; //b

                ImageArray[width+x][y][1] = ImageArray2[x][y][1]; //r image middle
                ImageArray[width+x][y][2] = ImageArray2[x][y][2]; //g
                ImageArray[width+x][y][3] = ImageArray2[x][y][3]; //b

                ImageArray[(width * 2) +x][y][1] = ImageArray3[x][y][1]; //r iamge 2
                ImageArray[(width * 2) +x][y][2] = ImageArray3[x][y][2]; //g
                ImageArray[(width * 2) +x][y][3] = ImageArray3[x][y][3]; //b
            }
        }

        return convertToBimage(ImageArray);
    };

    public int[][][] PixelRescale(int[][][] ImageArray, float scale){
        int width = ImageArray.length;
        int height = ImageArray[0].length;

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                if (ImageArray[x][y][0] == 255){
                    //int a = ImageArray[x][y][0];
                    int r = ImageArray[x][y][1];  //r
                    int g = ImageArray[x][y][2];  //g
                    int b = ImageArray[x][y][3];  //b

                    //a =  Math.round(a * scale);
                    r = Math.round(r * scale);
                    g = Math.round(g * scale);
                    b = Math.round(b * scale);

                    r = Math.max(0, Math.min(255, r));
                    g = Math.max(0, Math.min(255, g));
                    b = Math.max(0, Math.min(255, b));

                    //ImageArray[x][y][0] = a;
                    ImageArray[x][y][1] = r;
                    ImageArray[x][y][2] = g;
                    ImageArray[x][y][3] = b;
                }
            }
        }

        return ImageArray;

    }
    public int[][][] PixelShift(int[][][] ImageArray, float shift){
        int width = ImageArray.length;
        int height = ImageArray[0].length;

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                if (ImageArray[x][y][0] == 255) {
                    int r = ImageArray[x][y][1];  //r
                    int g = ImageArray[x][y][2];  //g
                    int b = ImageArray[x][y][3];  //b

                    r = Math.round(r + shift);
                    g = Math.round(g + shift);
                    b = Math.round(b + shift);

                    r = Math.max(0, Math.min(255, r));
                    g = Math.max(0, Math.min(255, g));
                    b = Math.max(0, Math.min(255, b));

                    ImageArray[x][y][1] = r;
                    ImageArray[x][y][2] = g;
                    ImageArray[x][y][3] = b;
                }
            }
        }
        return ImageArray;

    }

    public BufferedImage AddRandomPixelValue(int[][][] ImageArray){
        int width = ImageArray.length;
        int height = ImageArray[0].length;

        Random random = new Random();

        // add values
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                if (ImageArray[x][y][0] == 255) {
                    ImageArray[x][y][1] = ImageArray[x][y][1] + random.nextInt(128); //r
                    ImageArray[x][y][2] = ImageArray[x][y][2] + random.nextInt(128); //g
                    ImageArray[x][y][3] = ImageArray[x][y][3] + random.nextInt(128); //b
                }
            }
        }

        // shift
        ImageArray = ShiftAndRescale(ImageArray, width, height);

        return convertToBimage(ImageArray);
    }

    public int[][][] ShiftAndRescale(int[][][] ImageArray, int width, int height){
        // find min then max

        float min = 255;
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                if (ImageArray[x][y][0] == 255) {
                    int r = ImageArray[x][y][1];  //r
                    int g = ImageArray[x][y][2];  //g
                    int b = ImageArray[x][y][3];  //b

                    min = Math.min(min, Math.min(r, Math.min(g, b)));
                }
            }
        }
        ImageArray = PixelShift(ImageArray, -min); // shift lowest to 0

        float max = 0;
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                if (ImageArray[x][y][0] == 255) {
                    int r = ImageArray[x][y][1];  //r
                    int g = ImageArray[x][y][2];  //g
                    int b = ImageArray[x][y][3];  //b

                    max = Math.max(max, Math.max(r, Math.max(g, b)));
                }
            }
        }
        max = Math.max(1, max);
        ImageArray = PixelRescale(ImageArray, 255/max); // scale max to 255: (shifted)max * scale = 255

        return ImageArray;
    }

    public BufferedImage ArithmeticAddition(int[][][] ImageArray, BufferedImage timg2){

        int[][][] ImageArray1 = ImageArray;
        int[][][] ImageArray2 = convertToArray(timg2);

        int width = Math.min(ImageArray.length, timg2.getWidth());
        int height = Math.min(ImageArray[0].length, timg2.getHeight());

        int[][][] resultArray = ImageArray;

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                if (ImageArray[x][y][0] == 255) {
                    resultArray[x][y][1] = ImageArray1[x][y][1] + ImageArray2[x][y][1];
                    resultArray[x][y][2] = ImageArray1[x][y][2] + ImageArray2[x][y][2];
                    resultArray[x][y][3] = ImageArray1[x][y][3] + ImageArray2[x][y][3];
                }
            }
        }

        resultArray = ShiftAndRescale(resultArray, width, height);

        return convertToBimage(resultArray);
    }
    public BufferedImage ArithmeticSubtraction(int[][][] ImageArray, BufferedImage timg2){

        int[][][] ImageArray1 = ImageArray;
        int[][][] ImageArray2 = convertToArray(timg2);

        int width = Math.min(ImageArray.length, timg2.getWidth());
        int height = Math.min(ImageArray[0].length, timg2.getHeight());

        int[][][] resultArray = ImageArray;

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                if (ImageArray[x][y][0] == 255) {
                    resultArray[x][y][1] = ImageArray1[x][y][1] - ImageArray2[x][y][1];
                    resultArray[x][y][2] = ImageArray1[x][y][2] - ImageArray2[x][y][2];
                    resultArray[x][y][3] = ImageArray1[x][y][3] - ImageArray2[x][y][3];
                }
            }
        }

        resultArray = ShiftAndRescale(resultArray, width, height);

        return convertToBimage(resultArray);
    }
    public BufferedImage ArithmeticMultiplication(int[][][] ImageArray, BufferedImage timg2){

        int[][][] ImageArray1 = ImageArray;
        int[][][] ImageArray2 = convertToArray(timg2);

        int width = Math.min(ImageArray.length, timg2.getWidth());
        int height = Math.min(ImageArray[0].length, timg2.getHeight());

        int[][][] resultArray = ImageArray;

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                if (ImageArray[x][y][0] == 255) {
                    resultArray[x][y][1] = ImageArray1[x][y][1] * ImageArray2[x][y][1];
                    resultArray[x][y][2] = ImageArray1[x][y][2] * ImageArray2[x][y][2];
                    resultArray[x][y][3] = ImageArray1[x][y][3] * ImageArray2[x][y][3];
                }
            }
        }

        resultArray = ShiftAndRescale(resultArray, width, height);

        return convertToBimage(resultArray);
    }
    public BufferedImage ArithmeticDivision(int[][][] ImageArray, BufferedImage timg2){

        int[][][] ImageArray1 = ImageArray;
        int[][][] ImageArray2 = convertToArray(timg2);

        int width = Math.min(ImageArray.length, timg2.getWidth());
        int height = Math.min(ImageArray[0].length, timg2.getHeight());

        int[][][] resultArray = ImageArray;

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                if (resultArray[x][y][0] == 255) {
                    if (ImageArray2[x][y][1] == 0) {
                        continue;
                    }
                    if (ImageArray2[x][y][2] == 0) {
                        continue;
                    }
                    if (ImageArray2[x][y][3] == 0) {
                        continue;
                    }

                    resultArray[x][y][1] = (int) ((float) ImageArray1[x][y][1] / ImageArray2[x][y][1]);
                    resultArray[x][y][2] = (int) ((float) ImageArray1[x][y][2] / ImageArray2[x][y][2]);
                    resultArray[x][y][3] = (int) ((float) ImageArray1[x][y][3] / ImageArray2[x][y][3]);
                }
            }
        }

        resultArray = ShiftAndRescale(resultArray, width, height);

        return convertToBimage(resultArray);
    }

    public BufferedImage BitwiseNOT(int[][][] ImageArray){
        int width = ImageArray.length;
        int height = ImageArray[0].length;

        int[][][] resultArray = ImageArray;

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                if (resultArray[x][y][0] == 255) {
                    // lecture 3 - same as negative
                    resultArray[x][y][1] = (~resultArray[x][y][1]) & 0xFF;
                    resultArray[x][y][2] = (~resultArray[x][y][2]) & 0xFF;
                    resultArray[x][y][3] = (~resultArray[x][y][3]) & 0xFF;
                }
            }
        }

        return convertToBimage(resultArray);
    }
    public BufferedImage BitwiseAND(int[][][] ImageArray, BufferedImage timg2){
        int[][][] ImageArray1 = ImageArray;
        int[][][] ImageArray2 = convertToArray(timg2);

        int width = Math.min(ImageArray.length, timg2.getWidth());
        int height = Math.min(ImageArray[0].length, timg2.getHeight());

        int[][][] resultArray = ImageArray;

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                if (resultArray[x][y][0] == 255) {
                    resultArray[x][y][1] = (ImageArray1[x][y][1] & ImageArray2[x][y][1]) & 0xFF; // R
                    resultArray[x][y][2] = (ImageArray1[x][y][2] & ImageArray2[x][y][2]) & 0xFF; // G
                    resultArray[x][y][3] = (ImageArray1[x][y][3] & ImageArray2[x][y][3]) & 0xFF;
                }
            }
        }

        return convertToBimage(resultArray);
    }
    public BufferedImage BitwiseOR(int[][][] ImageArray, BufferedImage timg2){
        int[][][] ImageArray1 = ImageArray;
        int[][][] ImageArray2 = convertToArray(timg2);

        int width = Math.min(ImageArray.length, timg2.getWidth());
        int height = Math.min(ImageArray[0].length, timg2.getHeight());

        int[][][] resultArray = ImageArray;

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                if (resultArray[x][y][0] == 255) {
                    resultArray[x][y][1] = (ImageArray1[x][y][1] | ImageArray2[x][y][1]) & 0xFF; // R
                    resultArray[x][y][2] = (ImageArray1[x][y][2] | ImageArray2[x][y][2]) & 0xFF; // G
                    resultArray[x][y][3] = (ImageArray1[x][y][3] | ImageArray2[x][y][3]) & 0xFF;
                }
            }
        }

        return convertToBimage(resultArray);
    }
    public BufferedImage BitwiseXOR(int[][][] ImageArray, BufferedImage timg2){
        int[][][] ImageArray1 = ImageArray;
        int[][][] ImageArray2 = convertToArray(timg2);

        int width = Math.min(ImageArray.length, timg2.getWidth());
        int height = Math.min(ImageArray[0].length, timg2.getHeight());

        int[][][] resultArray = ImageArray;

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                if (resultArray[x][y][0] == 255) {
                    resultArray[x][y][1] = (ImageArray1[x][y][1] ^ ImageArray2[x][y][1]) & 0xFF; // R
                    resultArray[x][y][2] = (ImageArray1[x][y][2] ^ ImageArray2[x][y][2]) & 0xFF; // G
                    resultArray[x][y][3] = (ImageArray1[x][y][3] ^ ImageArray2[x][y][3]) & 0xFF;
                }
            }
        }

        return convertToBimage(resultArray);
    }

    public int[][][] ROIProcess(BufferedImage timg1, BufferedImage timg2){

        int[][][] ImageArray1 = convertToArray(timg1);
        int[][][] ImageArray2 = convertToArray(timg2); // roi filter

        int width = Math.min(timg1.getWidth(), timg2.getWidth());
        int height = Math.min(timg1.getHeight(), timg2.getHeight());

        int[][][] ImageArray = new  int[width][height][4];

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){

                int roiPixel = (ImageArray2[x][y][1]
                                + ImageArray2[x][y][2]
                                + ImageArray2[x][y][3]) / 3;

                ImageArray[x][y][1] = ImageArray1[x][y][1];
                ImageArray[x][y][2] = ImageArray1[x][y][2];
                ImageArray[x][y][3] = ImageArray1[x][y][3];

                if (roiMode){
                    if (roiPixel == 255) {
                        ImageArray[x][y][0] = 255;
                    } else {
                        ImageArray[x][y][0] = 0;
                    }
                }
                else{
                    ImageArray[x][y][0] = 255;
                }
            }
        }

        return ImageArray;
    }

    public void ReverseROI(BufferedImage timg){
        bi2 = NegativeLinearTransform(convertToArray(timg));
    }

    public BufferedImage NegativeLinearTransform(int[][][] ImageArray){
        int width = ImageArray.length;
        int height = ImageArray[0].length;

        int[][][] resultArray = ImageArray;

        int L = 256;

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                if (ImageArray[x][y][0] == 255) {
                    resultArray[x][y][1] = L - 1 - resultArray[x][y][1];
                    resultArray[x][y][2] = L - 1 - resultArray[x][y][2];
                    resultArray[x][y][3] = L - 1 - resultArray[x][y][3];
                }
            }
        }

        return convertToBimage(resultArray);
    }

    public BufferedImage LogarithmicFunction(int[][][] ImageArray){
        // s = c log(1+r)
        int width = ImageArray.length;
        int height = ImageArray[0].length;

        int[][][] resultArray = ImageArray;

        double c = 255 / Math.log(256); // c log(1+255) = 255

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                if (ImageArray[x][y][0] == 255) {
                    resultArray[x][y][1] = (int) Math.round(c * Math.log(1 + resultArray[x][y][1]));
                    resultArray[x][y][2] = (int) Math.round(c * Math.log(1 + resultArray[x][y][2]));
                    resultArray[x][y][3] = (int) Math.round(c * Math.log(1 + resultArray[x][y][3]));
                }
            }
        }

        return convertToBimage(resultArray);
    }

    public BufferedImage PowerLaw(int[][][] ImageArray, double pConstant){
        // s = cr^p
        int width = ImageArray.length;
        int height = ImageArray[0].length;

        int[][][] resultArray = ImageArray;

        double c = Math.pow(255, (1 - pConstant)); // c 255^p = 255^1

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                if (ImageArray[x][y][0] == 255) {
                    resultArray[x][y][1] = (int) (c * Math.pow(resultArray[x][y][1], pConstant));
                    resultArray[x][y][2] = (int) (c * Math.pow(resultArray[x][y][2], pConstant));
                    resultArray[x][y][3] = (int) (c * Math.pow(resultArray[x][y][3], pConstant));
                }
            }
        }

        return convertToBimage(resultArray);
    }

    public BufferedImage LUTFunction(int[][][] ImageArray){
        int width = ImageArray.length;
        int height = ImageArray[0].length;

        int[][][] resultArray = ImageArray;

        Random random = new Random();
        int[] LUT = new int[256];

        for(int k=0; k<=255; k++){
            LUT[k] = random.nextInt(256);
        }

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                if (resultArray[x][y][0] == 255) {
                    resultArray[x][y][1] = LUT[resultArray[x][y][1]];
                    resultArray[x][y][2] = LUT[resultArray[x][y][2]];
                    resultArray[x][y][3] = LUT[resultArray[x][y][3]];
                }
            }
        }

        return convertToBimage(resultArray);
    }

    public BufferedImage BitPlane(int[][][] ImageArray, int plane){
        int width = ImageArray.length;
        int height = ImageArray[0].length;

        int[][][] resultArray = ImageArray;

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                if (ImageArray[x][y][0] == 255) {
                    resultArray[x][y][1] = ((resultArray[x][y][1] >> plane) & 1) * 255;
                    resultArray[x][y][2] = ((resultArray[x][y][2] >> plane) & 1) * 255;
                    resultArray[x][y][3] = ((resultArray[x][y][3] >> plane) & 1) * 255;
                }
            }
        }

        return convertToBimage(resultArray);
    }

    public BufferedImage HistogramEqualisation(int[][][] ImageArray){
        int width = ImageArray.length;
        int height = ImageArray[0].length;

        int[][][] resultArray = ImageArray;

        int L = 256;
        // ex 1 calc histogram: r_k -> n_k
        int[] HistogramR = new int[L];
        int[] HistogramG = new int[L];
        int[] HistogramB = new int[L];

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                HistogramR[ImageArray[x][y][1]]++;
                HistogramG[ImageArray[x][y][2]]++;
                HistogramB[ImageArray[x][y][3]]++;
            }
        }

        // ex 2 calc normalised histogram: n_k / n

        double[] nHistogramR = new double[L];
        double[] nHistogramG = new double[L];
        double[] nHistogramB = new double[L];
        int totalPixels = width * height;

        for(int k=0; k<L; k++){ // Normalisation
            nHistogramR[k] = (double)HistogramR[k] / totalPixels; // r
            nHistogramG[k] = (double)HistogramG[k] / totalPixels; // g
            nHistogramB[k] = (double)HistogramB[k] / totalPixels; // b
        }

        // ex 3 cumulative distribution: s'_k
        double[] CDr = new double[L];
        double[] CDg = new double[L];
        double[] CDb = new double[L];

        CDr[0] = nHistogramR[0];
        CDg[0] = nHistogramG[0];
        CDb[0] = nHistogramB[0];

        for(int i = 1; i < L; i++) {
            CDr[i] = CDr[i-1] + nHistogramR[i];
            CDg[i] = CDg[i-1] + nHistogramG[i];
            CDb[i] = CDb[i-1] + nHistogramB[i];
        }

        // multiply s'_k by L-1
        int[] LUTr = new int[L];
        int[] LUTg = new int[L];
        int[] LUTb = new int[L];

        for(int i = 0; i < L; i++) {
            LUTr[i] = (int) Math.round(CDr[i] * (L-1));
            LUTg[i] = (int) Math.round(CDg[i] * (L-1));
            LUTb[i] = (int) Math.round(CDb[i] * (L-1));
        }

        // apply mapping
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                if (ImageArray[x][y][0] == 255) {
                    resultArray[x][y][1] = LUTr[ImageArray[x][y][1]];
                    resultArray[x][y][2] = LUTg[ImageArray[x][y][2]];
                    resultArray[x][y][3] = LUTb[ImageArray[x][y][3]];
                }
            }
        }

        return convertToBimage(resultArray);
    }

    public int[][][] ApplyConvolution(int[][][] ImageArray, float[][] Mask) {
        int width = ImageArray.length;
        int height = ImageArray[0].length;

        int[][][] resultArray = new int[width][height][4];

        int maskSum = 0;

        for(int y=0; y<3; y++){
            for(int x =0; x<3; x++){
                maskSum = maskSum + (int) Mask[x][y];
            }
        }
        // =0 edge detect, =1 enhance, >1 blur
        if (maskSum != 0){
            for(int y=0; y<3; y++){
                for(int x =0; x<3; x++){
                    Mask[x][y] = Mask[x][y] / maskSum ;
                }
            }
        }

        //          t = -1      t = 0     t = 1
        // s = -1 [x-1, y-1]  [x-1, y]  [x-1, y+1] Mask[0][y]
        // s = 0  [ x, y-1 ]  [ x, y ]  [ x, y+1 ] Mask[1][y]
        // s = 1  [x+1, y-1]  [x+1, y]  [x+1, y+1] Mask[2][y]
        //        Mask[x][0] Mask[x][1] Mask[x][2]

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {

                // loop image

                int r = 0, g = 0, b = 0;

                for (int s = -1; s <= 1; s++) {
                    for (int t = -1; t <= 1; t++) {
                        // loop mask

                        r = r + Math.round(Mask[1-s][1-t] * ImageArray[x + s][y + t][1]);
                        g = g + Math.round(Mask[1-s][1-t] * ImageArray[x + s][y + t][2]);
                        b = b + Math.round(Mask[1-s][1-t] * ImageArray[x + s][y + t][3]);

                    }
                }

                r = Math.min(Math.max(r, 0), 255); // R
                g = Math.min(Math.max(g, 0), 255); // G
                b = Math.min(Math.max(b, 0), 255); // B

                if (ImageArray[x][y][0] == 255) {
                    resultArray[x][y][1] = r;
                    resultArray[x][y][2] = g;
                    resultArray[x][y][3] = b;
                } else {
                    resultArray[x][y][1] = ImageArray[x][y][1];
                    resultArray[x][y][2] = ImageArray[x][y][2];
                    resultArray[x][y][3] = ImageArray[x][y][3];
                }
            }
        }

        return resultArray;
    }

    public BufferedImage Averaging(int[][][] ImageArray){
        int[][][] resultArray;
        float[][] Mask = {
                {1, 1, 1},
                {1, 1, 1},
                {1, 1, 1}
        };
        resultArray = ApplyConvolution(ImageArray, Mask);
        return convertToBimage(resultArray);
    }
    public BufferedImage WeightedAveraging(int[][][] ImageArray){
        int[][][] resultArray;
        float[][] Mask = {
                {1, 2, 1},
                {2, 4, 2},
                {1, 2, 1}
        };
        resultArray = ApplyConvolution(ImageArray, Mask);
        return convertToBimage(resultArray);
    }

    public BufferedImage Laplacian4(int[][][] ImageArray){
        int[][][] resultArray;
        float[][] Mask = {
                {0, -1, 0},
                {-1, 4, -1},
                {0, -1, 0}
        };
        resultArray = ApplyConvolution(ImageArray, Mask);
        return convertToBimage(resultArray);
    }
    public BufferedImage Laplacian8(int[][][] ImageArray){
        int[][][] resultArray;
        float[][] Mask = {
                {-1, -1, -1},
                {-1, 8, -1},
                {-1, -1, -1}
        };
        resultArray = ApplyConvolution(ImageArray, Mask);
        return convertToBimage(resultArray);
    }
    public BufferedImage Laplacian4Enhance(int[][][] ImageArray){
        int[][][] resultArray;
        float[][] Mask = {
                {0, -1, 0},
                {-1, 5, -1},
                {0, -1, 0}
        };
        resultArray = ApplyConvolution(ImageArray, Mask);
        return convertToBimage(resultArray);
    }
    public BufferedImage Laplacian8Enhance(int[][][] ImageArray){
        int[][][] resultArray;
        float[][] Mask = {
                {-1, -1, -1},
                {-1, 9, -1},
                {-1, -1, -1}
        };
        resultArray = ApplyConvolution(ImageArray, Mask);
        return convertToBimage(resultArray);
    }

    public BufferedImage Roberts1(int[][][] ImageArray){
        int[][][] resultArray;
        float[][] Mask = {
                {0, 0, 0},
                {0, 0, -1},
                {0, 1, 0}
        };
        resultArray = ApplyConvolution(ImageArray, Mask);

        // abs conversion
        for (int y = 0; y < ImageArray[0].length; y++) {
            for (int x = 0; x < ImageArray.length; x++) {
                if (ImageArray[x][y][0] == 255) {
                    resultArray[x][y][1] = Math.min(255, Math.abs(resultArray[x][y][1]));
                    resultArray[x][y][2] = Math.min(255, Math.abs(resultArray[x][y][2]));
                    resultArray[x][y][3] = Math.min(255, Math.abs(resultArray[x][y][3]));
                }
            }
        }

        return convertToBimage(resultArray);
    }
    public BufferedImage Roberts2(int[][][] ImageArray){
        int[][][] resultArray;
        float[][] Mask = {
                {0, 0, 0},
                {0, -1, 0},
                {0, 0, 1}
        };
        resultArray = ApplyConvolution(ImageArray, Mask);

        for (int y = 0; y < ImageArray[0].length; y++) {
            for (int x = 0; x < ImageArray.length; x++) {
                if (ImageArray[x][y][0] == 255) {
                    resultArray[x][y][1] = Math.min(255, Math.abs(resultArray[x][y][1]));
                    resultArray[x][y][2] = Math.min(255, Math.abs(resultArray[x][y][2]));
                    resultArray[x][y][3] = Math.min(255, Math.abs(resultArray[x][y][3]));
                }
            }
        }

        return convertToBimage(resultArray);
    }
    public BufferedImage SobelX(int[][][] ImageArray){
        int[][][] resultArray;
        float[][] Mask = {
                {-1, 0, 1},
                {-2, 0, 2},
                {-1, 0, 1}
        };
        resultArray = ApplyConvolution(ImageArray, Mask);
        for (int y = 0; y < ImageArray[0].length; y++) {
            for (int x = 0; x < ImageArray.length; x++) {
                if (ImageArray[x][y][0] == 255) {
                    resultArray[x][y][1] = Math.min(255, Math.abs(resultArray[x][y][1]));
                    resultArray[x][y][2] = Math.min(255, Math.abs(resultArray[x][y][2]));
                    resultArray[x][y][3] = Math.min(255, Math.abs(resultArray[x][y][3]));
                }
            }
        }
        return convertToBimage(resultArray);
    }
    public BufferedImage SobelY(int[][][] ImageArray){
        int[][][] resultArray;
        float[][] Mask = {
                {-1, -2, -1},
                {0, 0, 0},
                {1, 2, 1}
        };
        resultArray = ApplyConvolution(ImageArray, Mask);
        for (int y = 0; y < ImageArray[0].length; y++) {
            for (int x = 0; x < ImageArray.length; x++) {
                if (ImageArray[x][y][0] == 255) {
                    resultArray[x][y][1] = Math.min(255, Math.abs(resultArray[x][y][1]));
                    resultArray[x][y][2] = Math.min(255, Math.abs(resultArray[x][y][2]));
                    resultArray[x][y][3] = Math.min(255, Math.abs(resultArray[x][y][3]));
                }
            }
        }
        return convertToBimage(resultArray);
    }

    public BufferedImage SaltandPepper(int[][][] ImageArray){
        int width = ImageArray.length;
        int height = ImageArray[0].length;

        int totalPixels = width * height;
        int noise = totalPixels / 50;

        Random random = new Random();
        for (int i = 0; i < noise; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);

            if (ImageArray[x][y][0] == 255) {
                if (random.nextBoolean()) {
                    ImageArray[x][y][1] = 255;
                    ImageArray[x][y][2] = 255;
                    ImageArray[x][y][3] = 255;
                } else {
                    ImageArray[x][y][1] = 0;
                    ImageArray[x][y][2] = 0;
                    ImageArray[x][y][3] = 0;
                }
            }
        }

        return convertToBimage(ImageArray);
    }

    public BufferedImage minFilter(int[][][] ImageArray){
        // remove salt
        int width = ImageArray.length;
        int height = ImageArray[0].length;

        int[][][] resultArray = new int[width][height][4];

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {

                resultArray[x][y][1] = ImageArray[x][y][1];
                resultArray[x][y][2] = ImageArray[x][y][2];
                resultArray[x][y][3] = ImageArray[x][y][3];
            }
        }

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                if (ImageArray[x][y][0] == 255) {
                    // loop image

                    int r = 255, b = 255, g = 255;
                    for (int s = -1; s <= 1; s++) {
                        for (int t = -1; t <= 1; t++) {
                            // loop mask

                            r = Math.min(r, ImageArray[x + s][y + t][1]);
                            g = Math.min(g, ImageArray[x + s][y + t][2]);
                            b = Math.min(b, ImageArray[x + s][y + t][3]);
                        }
                    }

                    resultArray[x][y][1] = r;
                    resultArray[x][y][2] = g;
                    resultArray[x][y][3] = b;
                }
            }
        }

        return convertToBimage(resultArray);
    }

    public BufferedImage maxFilter(int[][][] ImageArray){
        // rmove pepper
        int width = ImageArray.length;
        int height = ImageArray[0].length;

        int[][][] resultArray = new int[width][height][4];

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {

                resultArray[x][y][1] = ImageArray[x][y][1];
                resultArray[x][y][2] = ImageArray[x][y][2];
                resultArray[x][y][3] = ImageArray[x][y][3];
            }
        }

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                if (ImageArray[x][y][0] == 255) {
                // loop image

                    int r = 0, b = 0, g = 0;
                    for (int s = -1; s <= 1; s++) {
                        for (int t = -1; t <= 1; t++) {
                            // loop mask

                            r = Math.max(r, ImageArray[x + s][y + t][1]);
                            g = Math.max(g, ImageArray[x + s][y + t][2]);
                            b = Math.max(b, ImageArray[x + s][y + t][3]);
                        }
                    }

                    resultArray[x][y][1] = r;
                    resultArray[x][y][2] = g;
                    resultArray[x][y][3] = b;
                }
            }
        }

        return convertToBimage(resultArray);
    }

    public BufferedImage midFilter(int[][][] ImageArray){
        int width = ImageArray.length;
        int height = ImageArray[0].length;

        int[][][] minFiltered = convertToArray(minFilter(ImageArray));
        int[][][] maxFiltered = convertToArray(maxFilter(ImageArray));

        int[][][] resultArray = new int[width][height][4];

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {

                resultArray[x][y][1] = ImageArray[x][y][1];
                resultArray[x][y][2] = ImageArray[x][y][2];
                resultArray[x][y][3] = ImageArray[x][y][3];
            }
        }

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                if (ImageArray[x][y][0] == 255) {

                    resultArray[x][y][1] = (minFiltered[x][y][1] + maxFiltered[x][y][1]) / 2;
                    resultArray[x][y][2] = (minFiltered[x][y][2] + maxFiltered[x][y][2]) / 2;
                    resultArray[x][y][3] = (minFiltered[x][y][3] + maxFiltered[x][y][3]) / 2;
                }
            }
        }

        return convertToBimage(resultArray);
    }

    public BufferedImage medianFilter(int[][][] ImageArray){
        int width = ImageArray.length;
        int height = ImageArray[0].length;

        int[][][] resultArray = ImageArray;

        int[] rWindow = new int[9];
        int[] gWindow = new int[9];
        int[] bWindow = new int[9];

        for(int y=1; y<height-1; y++){
            for(int x=1; x<width-1; x++){
                if (ImageArray[x][y][0] == 255) {
                    int k = 0;
                    for(int s=-1; s<=1; s++){
                        for(int t=-1; t<=1; t++){
                            rWindow[k] = ImageArray[x+s][y+t][1]; //r
                            gWindow[k] = ImageArray[x+s][y+t][2]; //g
                            bWindow[k] = ImageArray[x+s][y+t][3]; //b
                            k++;
                        }
                    }
                    Arrays.sort(rWindow);
                    Arrays.sort(gWindow);
                    Arrays.sort(bWindow);

                    resultArray[x][y][1] = rWindow[4]; //r
                    resultArray[x][y][2] = gWindow[4]; //g
                    resultArray[x][y][3] = bWindow[4]; //b

                }
            }
        }

        return convertToBimage(resultArray);
    }

    public BufferedImage simpleThreshold(int[][][] ImageArray, int threshold) {
        int width = ImageArray.length;
        int height = ImageArray[0].length;

        int[][][] resultArray = ImageArray;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (ImageArray[x][y][0] == 255) {
                    if (
                            Math.round((ImageArray[x][y][1]
                                    + ImageArray[x][y][2]
                                    + ImageArray[x][y][3]) / 3.0f) >= threshold
                    ) {
                        resultArray[x][y][1] = 255;
                        resultArray[x][y][2] = 255;
                        resultArray[x][y][3] = 255;
                    } else {
                        resultArray[x][y][1] = 0;
                        resultArray[x][y][2] = 0;
                        resultArray[x][y][3] = 0;
                    }
                }
            }
        }

        return convertToBimage(resultArray);
    }

    public BufferedImage MeanAndStandardDev(int[][][] ImageArray){
        int width = ImageArray.length;
        int height = ImageArray[0].length;

        int[][][] resultArray = ImageArray;

        int L = 256;
        // calc histogram: rk -> nk
        int[] HistogramR = new int[L];
        int[] HistogramG = new int[L];
        int[] HistogramB = new int[L];

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                HistogramR[ImageArray[x][y][1]]++;
                HistogramG[ImageArray[x][y][2]]++;
                HistogramB[ImageArray[x][y][3]]++;
            }
        }

        // calc normalised histogram: nk / n
        double[] nHistogramR = new double[L];
        double[] nHistogramG = new double[L];
        double[] nHistogramB = new double[L];
        int totalPixels = width * height;

        for(int k=0; k<L; k++){ // Normalisation
            nHistogramR[k] = (double)HistogramR[k] / totalPixels; // r
            nHistogramG[k] = (double)HistogramG[k] / totalPixels; // g
            nHistogramB[k] = (double)HistogramB[k] / totalPixels; // b
        }

        // calc mean
        double meanR = 0, meanG = 0, meanB = 0;
        for (int k = 0; k < L; k++) {
            meanR += k * nHistogramR[k];
            meanG += k * nHistogramG[k];
            meanB += k * nHistogramB[k];
        }

        // calc standard dev
        double stdDevR = 0, stdDevG = 0, stdDevB = 0;
        for (int k = 0; k < L; k++) {
            stdDevR += nHistogramR[k] * Math.pow(k - meanR, 2);
            stdDevG += nHistogramG[k] * Math.pow(k - meanG, 2);
            stdDevB += nHistogramB[k] * Math.pow(k - meanB, 2);
        }

        stdDevR = Math.sqrt(stdDevR);
        stdDevG = Math.sqrt(stdDevG);
        stdDevB = Math.sqrt(stdDevB);

        return convertToBimage(resultArray);
    }

    public BufferedImage AutoThreshold(int[][][] ImageArray){
        int width = ImageArray.length;
        int height = ImageArray[0].length;

        int[][][] resultArray = ImageArray;

        double[][] greyArray = new double[width][height];

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                greyArray[x][y] =
                        (ImageArray[x][y][1]
                        +ImageArray[x][y][2]
                        +ImageArray[x][y][3]) / 3.0f;
            }
        }

        double T_prev = (
                greyArray[0][0]
                        +greyArray[width-1][0]
                        +greyArray[width-1][height-1]
                        +greyArray[0][height-1]) / 4.0f;

        double T_new = 0; 

        while (Math.abs(T_new - T_prev) >= 1.0){
            T_prev = T_new;
            double sumB = 0, sumO = 0;
            int countB = 0, countO = 0;

            // Classify pixels and calculate sums
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (greyArray[x][y] <= T_prev) {
                        sumB += greyArray[x][y];
                        countB++;
                    } else {
                        sumO += greyArray[x][y];
                        countO++;
                    }
                }
            }

            // Calculate means
            double muB = (countB > 0) ? sumB / countB : 0;
            double muO = (countO > 0) ? sumO / countO : 0;
            T_new = (muB + muO) / 2.0;
        }

        System.out.println(T_new);
        return simpleThreshold(ImageArray, (int) T_new);
    }



    //************************************
    //  You need to register your functioin here
    //************************************
    public void filterImage() {
        if (opIndex == lastOp) {
            return;
        }

        int actualLastOp = lastOp;
        lastOp = opIndex;

        int[][][] ImageArray = ROIProcess(biFiltered, bi2);

        switch (opIndex) {
            case 0:
                prevImage = biFiltered;
                biFiltered = bi; /* original */
                return;
            case 1:
                prevImage = biFiltered;
                biFiltered = ImageNegative(ImageArray); /* Image Negative */
                return;
            //************************************
            case 2:
                if (prevImage == null){
                    return;
                }
                biFiltered = prevImage;
                opIndex = actualLastOp;
                lastOp = opIndex;
                return;
            //************************************
            case 3:
                prevImage = biFiltered;
                biFiltered = convertToBimage(PixelRescale(ImageArray, scale));
                return;
            case 4:
                prevImage = biFiltered;
                biFiltered = convertToBimage(PixelShift(ImageArray, shift));
                return;
            case 5:
                prevImage = biFiltered;
                biFiltered = AddRandomPixelValue(ImageArray);
                return;
            case 6:
                prevImage = biFiltered;
                biFiltered = ArithmeticAddition(ImageArray, bi2);
                return;
            case 7:
                prevImage = biFiltered;
                biFiltered = ArithmeticSubtraction(ImageArray, bi2);
                return;
            case 8:
                prevImage = biFiltered;
                biFiltered = ArithmeticMultiplication(ImageArray, bi2);
                return;
            case 9:
                prevImage = biFiltered;
                biFiltered = ArithmeticDivision(ImageArray, bi2);
                return;
            case 10:
                prevImage = biFiltered;
                biFiltered = BitwiseNOT(ImageArray);
                return;
            case 11:
                prevImage = biFiltered;
                biFiltered = BitwiseAND(ImageArray, bi2);
                return;
            case 12:
                prevImage = biFiltered;
                biFiltered = BitwiseOR(ImageArray, bi2);
                return;
            case 13:
                prevImage = biFiltered;
                biFiltered = BitwiseXOR(ImageArray, bi2);
                return;
            case 14:
                ReverseROI(bi2);
                return;
            case 15:
                prevImage = biFiltered;
                biFiltered = NegativeLinearTransform(ImageArray);
                return;
            case 16:
                prevImage = biFiltered;
                biFiltered = LogarithmicFunction(ImageArray);
                return;
            case 17:
                prevImage = biFiltered;
                biFiltered = PowerLaw(ImageArray, pConstant);
                return;
            case 18:
                prevImage = biFiltered;
                biFiltered = LUTFunction(ImageArray);
                return;
            case 19:
                prevImage = biFiltered;
                biFiltered = BitPlane(ImageArray, plane);
                return;
            case 20:
                prevImage = biFiltered;
                biFiltered = HistogramEqualisation(ImageArray);
                return;
            case 21:
                prevImage = biFiltered;
                biFiltered = Averaging(ImageArray);
                return;
            case 22:
                prevImage = biFiltered;
                biFiltered = WeightedAveraging(ImageArray);
                return;
            case 23:
                prevImage = biFiltered;
                biFiltered = Laplacian4(ImageArray);
                return;
            case 24:
                prevImage = biFiltered;
                biFiltered = Laplacian8(ImageArray);
                return;
            case 25:
                prevImage = biFiltered;
                biFiltered = Laplacian4Enhance(ImageArray);
                return;
            case 26:
                prevImage = biFiltered;
                biFiltered = Laplacian8Enhance(ImageArray);
                return;
            case 27:
                prevImage = biFiltered;
                biFiltered = Roberts1(ImageArray);
                return;
            case 28:
                prevImage = biFiltered;
                biFiltered = Roberts2(ImageArray);
                return;
            case 29:
                prevImage = biFiltered;
                biFiltered = SobelX(ImageArray);
                return;
            case 30:
                prevImage = biFiltered;
                biFiltered = SobelY(ImageArray);
                return;
            case 31:
                prevImage = biFiltered;
                biFiltered = SaltandPepper(ImageArray);
                return;
            case 32:
                prevImage = biFiltered;
                biFiltered = minFilter(ImageArray);
                return;
            case 33:
                prevImage = biFiltered;
                biFiltered = maxFilter(ImageArray);
                return;
            case 34:
                prevImage = biFiltered;
                biFiltered = midFilter(ImageArray);
                return;
            case 35:
                prevImage = biFiltered;
                biFiltered = medianFilter(ImageArray);
                return;
            case 36:
                prevImage = biFiltered;
                biFiltered = simpleThreshold(ImageArray, threshold);
                return;
            case 37:
                prevImage = biFiltered;
                biFiltered = AutoThreshold(ImageArray);
                return;

        }

    }



    public void actionPerformed(ActionEvent e) {
        JComboBox cb = new JComboBox();
        JButton jb = new JButton();

        if (e.getSource() instanceof JButton) {
            jb = (JButton) e.getSource();
        } else if (e.getSource() instanceof JComboBox) {
            cb = (JComboBox)e.getSource();
        }

        if (jb.getActionCommand().equals("Load Image 1")){
            JFileChooser chooser = new JFileChooser();
            int rval = chooser.showOpenDialog(cb);
            if (rval == JFileChooser.APPROVE_OPTION) {
                File imageFile = chooser.getSelectedFile();
                filename = imageFile.getName().toLowerCase();
                System.out.println(filename);
                try {
                    bi = ImageIO.read(new File("src/images/"+filename));
                    biFiltered = bi;
                } catch (IOException error) {      // deal with the situation that th image has problem;/
                    System.out.println("Image could not be read");

                    System.exit(1);
                }
            }

            repaint();
        }

        if (jb.getActionCommand().equals("Load Image 2")){
            JFileChooser chooser = new JFileChooser();
            int rval = chooser.showOpenDialog(cb);
            if (rval == JFileChooser.APPROVE_OPTION) {
                File imageFile = chooser.getSelectedFile();
                filename = imageFile.getName().toLowerCase();
                System.out.println(filename);
                try {
                    bi2 = ImageIO.read(new File("src/images/"+filename));
                } catch (IOException error) {      // deal with the situation that th image has problem;/
                    System.out.println("Image could not be read");

                    System.exit(1);
                }
            }

            repaint();
        }

        if (jb.getActionCommand().equals("Toggle ROI")) {
            roiMode = !roiMode;
            System.out.println("ROIMODE" + roiMode);
        }

        if (cb.getActionCommand().equals("SetFilter")) {
            // setOpIndex(cb.getSelectedIndex());
            // repaint();



            if (cb.getSelectedIndex() == 3){
                String input = JOptionPane.showInputDialog("Enter scale factor (0 - 2):");
                try {
                    scale = Float.parseFloat(input);
                    System.out.println(scale);
                    if (scale < 0 || scale > 2) {
                        System.out.println("RANGE");
                        return;
                    }
                } catch (Exception ex) {
                    System.out.println("NUMBER");
                    return;
                }

            }

            if (cb.getSelectedIndex() == 4){
                String input = JOptionPane.showInputDialog("Enter shift amount (integer):");
                try {
                    shift = Integer.parseInt(input);
                } catch (Exception ex) {
                    // System.out.println("HERE2");
                    return;
                }
            }

            if (cb.getSelectedIndex() == 17){
                String input = JOptionPane.showInputDialog("Enter p constant s=cr^p (0.01-25):");
                try {
                    pConstant = Double.parseDouble(input);
                    if (pConstant < 0.01 || pConstant > 25) {
                        System.out.println("RANGE");
                        return;
                    }
                } catch (Exception ex) {
                    // System.out.println("HERE2");
                    return;
                }
            }

            if (cb.getSelectedIndex() == 19){
                String input = JOptionPane.showInputDialog("Enter plane (0-7):");
                try {
                    plane = Integer.parseInt(input);
                    if (plane < 0 || plane > 7) {
                        System.out.println("RANGE");
                        return;
                    }
                } catch (Exception ex) {
                    // System.out.println("HERE2");
                    return;
                }
            }

            if (cb.getSelectedIndex() == 36){
                String input = JOptionPane.showInputDialog("Enter threshold (0-255):");
                try {
                    threshold = Integer.parseInt(input);
                    if (threshold < 0 || threshold > 256) {
                        System.out.println("RANGE");
                        return;
                    }
                } catch (Exception ex) {
                    // System.out.println("HERE2");
                    return;
                }
            }

            setOpIndex(cb.getSelectedIndex());
            repaint();

            setOpIndex(cb.getSelectedIndex());
            repaint();




        } else if (cb.getActionCommand().equals("Formats")) {
            String format = (String)cb.getSelectedItem();
            File saveFile = new File("savedimage."+format);
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(saveFile);
            int rval = chooser.showSaveDialog(cb);
            if (rval == JFileChooser.APPROVE_OPTION) {
                saveFile = chooser.getSelectedFile();
                try {
                    ImageIO.write(biFiltered, format, saveFile);
                } catch (IOException ex) {
                }
            }
        }
    };

    public static void main(String s[]) {
        JFrame f = new JFrame("Image Processing Demo");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });

        Demo de = new Demo();
        // Demo de2 = new Demo();
        f.add("Center", de);
        // f.add("West", de2);


        JComboBox choices = new JComboBox(de.getDescriptions());
        choices.setActionCommand("SetFilter");
        choices.addActionListener(de);

        JComboBox formats = new JComboBox(de.getFormats());
        formats.setActionCommand("Formats");
        formats.addActionListener(de);

        JButton loadButton1 = new JButton("Load Image 1");
        loadButton1.setActionCommand("Load Image 1");
        loadButton1.addActionListener(de);
        // loadButton.addActionListener(de2);

        JButton loadButton2 = new JButton("Load Image 2");
        loadButton2.setActionCommand("Load Image 2");
        loadButton2.addActionListener(de);

        JButton roiButton = new JButton("Toggle ROI");
        roiButton.setActionCommand("Toggle ROI");
        roiButton.addActionListener(de);

        JPanel panel = new JPanel();
        panel.add(choices);
        panel.add(new JLabel("Save As"));
        panel.add(formats);
        panel.add(loadButton1);
        panel.add(loadButton2);
        panel.add(roiButton);

        f.add("North", panel);
        f.pack();
        f.setVisible(true);
    }
}
