import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.TreeSet;

public class DemoL3E2 extends Component implements ActionListener {

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

    };

    int opIndex;  //option index for
    int lastOp;

    private BufferedImage bi, bi2, biFiltered;   // the input image saved as bi;//
    int w, h;
    private BufferedImage prevImage;
    static String filename = "BaboonRGB.tif";
    float scale = 1.0f;
    int shift = 0;

    public DemoL3E2() {
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
    public BufferedImage ImageNegative(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array

        // Image Negative Operation:
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray[x][y][1] = 255-ImageArray[x][y][1];  //r
                ImageArray[x][y][2] = 255-ImageArray[x][y][2];  //g
                ImageArray[x][y][3] = 255-ImageArray[x][y][3];  //b
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

    public BufferedImage PixelRescale(int[][][] ImageArray, float scale){
        int width = ImageArray.length;
        int height = ImageArray[0].length;

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                int r = ImageArray[x][y][1];  //r
                int g = ImageArray[x][y][2];  //g
                int b = ImageArray[x][y][3];  //b

                r = Math.round(r * scale);
                g = Math.round(g * scale);
                b = Math.round(b * scale);

                r = Math.max(0, Math.min(255, r));
                g = Math.max(0, Math.min(255, g));
                b = Math.max(0, Math.min(255, b));

                ImageArray[x][y][1] = r;
                ImageArray[x][y][2] = g;
                ImageArray[x][y][3] = b;
            }
        }
        return convertToBimage(ImageArray);

    }

    public BufferedImage PixelShift(int[][][] ImageArray, float shift){
        int width = ImageArray.length;
        int height = ImageArray[0].length;

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
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
        return convertToBimage(ImageArray);

    }

    public BufferedImage AddRandomPixelValue(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);

        Random random = new Random();

        // add values
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){

                int r = ImageArray[x][y][1] + random.nextInt(128); //r
                int g = ImageArray[x][y][2] + random.nextInt(128);  //g
                int b = ImageArray[x][y][3] + random.nextInt(128);  //b

                ImageArray[x][y][1] = r;
                ImageArray[x][y][2] = g;
                ImageArray[x][y][3] = b;
            }
        }

        // shift
        ShiftAndRescale(ImageArray, width, height);

        return convertToBimage(ImageArray);
    }

    public int[][][] ShiftAndRescale(int[][][] ImageArray, int width, int height){
        // find min then max

        float min = 255;
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                int r = ImageArray[x][y][1];  //r
                int g = ImageArray[x][y][2];  //g
                int b = ImageArray[x][y][3];  //b

                min = Math.min(min, Math.min(r, Math.min(g, b)));
            }
        }
        PixelShift(ImageArray, -min); // shift lowest to 0

        float max = 0;
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                int r = ImageArray[x][y][1];  //r
                int g = ImageArray[x][y][2];  //g
                int b = ImageArray[x][y][3];  //b

                max = Math.max(max, Math.max(r, Math.max(g, b)));
            }
        }
        max = Math.max(1, max);
        PixelRescale(ImageArray, 255/max); // scale max to 255: (shifted)max * scale = 255

        return ImageArray;
    }

    public BufferedImage ArithmeticAddition(BufferedImage timg1, BufferedImage timg2){

        int[][][] ImageArray1 = convertToArray(timg1);
        int[][][] ImageArray2 = convertToArray(timg2);

        int width = Math.min(timg1.getWidth(), timg2.getWidth());
        int height = Math.min(timg1.getHeight(), timg2.getHeight());

        int[][][] ImageArray = new  int[width][height][4];

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray[x][y][1] = ImageArray1[x][y][1] + ImageArray2[x][y][1];
                ImageArray[x][y][2] = ImageArray1[x][y][2] + ImageArray2[x][y][2];
                ImageArray[x][y][3] = ImageArray1[x][y][3] + ImageArray2[x][y][3];
            }
        }

        ShiftAndRescale(ImageArray, width, height);

        return convertToBimage(ImageArray);
    }

    public BufferedImage ArithmeticSubtraction(BufferedImage timg1, BufferedImage timg2){

        int[][][] ImageArray1 = convertToArray(timg1);
        int[][][] ImageArray2 = convertToArray(timg2);

        int width = Math.min(timg1.getWidth(), timg2.getWidth());
        int height = Math.min(timg1.getHeight(), timg2.getHeight());

        int[][][] ImageArray = new  int[width][height][4];

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray[x][y][1] = ImageArray1[x][y][1] - ImageArray2[x][y][1];
                ImageArray[x][y][2] = ImageArray1[x][y][2] - ImageArray2[x][y][2];
                ImageArray[x][y][3] = ImageArray1[x][y][3] - ImageArray2[x][y][3];
            }
        }

        ShiftAndRescale(ImageArray, width, height);

        return convertToBimage(ImageArray);
    }

    public BufferedImage ArithmeticMultiplication(BufferedImage timg1, BufferedImage timg2){

        int[][][] ImageArray1 = convertToArray(timg1);
        int[][][] ImageArray2 = convertToArray(timg2);

        int width = Math.min(timg1.getWidth(), timg2.getWidth());
        int height = Math.min(timg1.getHeight(), timg2.getHeight());

        int[][][] ImageArray = new  int[width][height][4];

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray[x][y][1] = ImageArray1[x][y][1] * ImageArray2[x][y][1];
                ImageArray[x][y][2] = ImageArray1[x][y][2] * ImageArray2[x][y][2];
                ImageArray[x][y][3] = ImageArray1[x][y][3] * ImageArray2[x][y][3];
            }
        }

        ShiftAndRescale(ImageArray, width, height);

        return convertToBimage(ImageArray);
    }

    public BufferedImage ArithmeticDivision(BufferedImage timg1, BufferedImage timg2){

        int[][][] ImageArray1 = convertToArray(timg1);
        int[][][] ImageArray2 = convertToArray(timg2);

        int width = Math.min(timg1.getWidth(), timg2.getWidth());
        int height = Math.min(timg1.getHeight(), timg2.getHeight());

        int[][][] ImageArray = new  int[width][height][4];

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                if (ImageArray2[x][y][1] == 0) {
                    continue;
                }
                if (ImageArray2[x][y][2] == 0) {
                    continue;
                }
                if (ImageArray2[x][y][3] == 0) {
                    continue;
                }

                ImageArray[x][y][1] = ImageArray1[x][y][1] / ImageArray2[x][y][1];
                ImageArray[x][y][2] = ImageArray1[x][y][2] / ImageArray2[x][y][2];
                ImageArray[x][y][3] = ImageArray1[x][y][3] / ImageArray2[x][y][3];
            }
        }

        ShiftAndRescale(ImageArray, width, height);

        return convertToBimage(ImageArray);
    }

    public BufferedImage BitwiseNOT(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                // lecture 3 - same as negative
                ImageArray[x][y][1] = (~ImageArray[x][y][1]) &0xFF;
                ImageArray[x][y][2] = (~ImageArray[x][y][2]) &0xFF;
                ImageArray[x][y][3] = (~ImageArray[x][y][3]) &0xFF;
            }
        }

        return convertToBimage(ImageArray);
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
        switch (opIndex) {
            case 0:
                prevImage = biFiltered;
                biFiltered = bi; /* original */
                return;
            case 1:
                prevImage = biFiltered;
                biFiltered = ImageNegative(bi); /* Image Negative */
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
                // System.out.println("case 3:");
                // System.out.println(scale);
                biFiltered = PixelRescale(convertToArray(bi), scale);

                return;
            case 4:
                prevImage = biFiltered;
                biFiltered = PixelShift(convertToArray(bi), shift);
                return;
            case 5:
                prevImage = biFiltered;
                biFiltered = AddRandomPixelValue(bi);
                return;
            case 6:
                prevImage = biFiltered;
                biFiltered = ArithmeticAddition(bi, bi2);
                return;
            case 7:
                prevImage = biFiltered;
                biFiltered = ArithmeticSubtraction(bi, bi2);
                return;
            case 8:
                prevImage = biFiltered;
                biFiltered = ArithmeticMultiplication(bi, bi2);
                return;
            case 9:
                prevImage = biFiltered;
                biFiltered = ArithmeticDivision(bi, bi2);
                return;
            case 10:
                prevImage = biFiltered;
                biFiltered = BitwiseNOT(bi);
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

        DemoL3E2 de = new DemoL3E2();
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

        JPanel panel = new JPanel();
        panel.add(choices);
        panel.add(new JLabel("Save As"));
        panel.add(formats);
        panel.add(loadButton1);
        panel.add(loadButton2);

        f.add("North", panel);
        f.pack();
        f.setVisible(true);
    }
}
