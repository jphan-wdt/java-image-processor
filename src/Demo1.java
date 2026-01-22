import java.io.*;
import java.util.TreeSet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;

public class Demo1 extends Component implements ActionListener {

    //************************************
    // List of the options(Original, Negative); correspond to the cases:
    //************************************

    String descs[] = {
            "Original",
            "Negative",
            "Rescale 2x",
            "Shift 2x",
            "undo",
    };

    int opIndex;  //option index for
    int lastOp;
    int lastLastOp;

    private BufferedImage bi, biFiltered; // the input image saved as bi;//
    private BufferedImage prevImage;
    int w, h;

    int menuIndex;
    int lastMenu;
    String menuActions[] = {
            "Menu",
            "Undo",
    };

    static String filename = "Goldhill.bmp";

    public Demo1() {
        try {
            bi = ImageIO.read(new File("src/images/"+filename));

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
        return new Dimension(w, h);
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

    String[] getMenuActions(){
        return menuActions;
    }

    void setOpIndex(int i) {
        opIndex = i;
    }

    void setMenuIndex(int i){
        menuIndex = i;
    }

    public void paint(Graphics g) { //  Repaint will call this function so the image will change.
        filterImage();
        menuAction();

        g.drawImage(biFiltered, 0, 0, null);
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
    public BufferedImage ImagePixelRescale(BufferedImage timg, float scale){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);

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

    public BufferedImage ImagePixelShift(BufferedImage timg, float shift){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);

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



    //************************************
    //  You need to register your functioin here
    //************************************
    public void filterImage() {

        if (opIndex == lastOp) {
            return;
        }
        lastLastOp = lastOp;
        lastOp = opIndex;
        prevImage = biFiltered;

        switch (opIndex) {
            case 0: biFiltered = bi; /* original */
                return;
            case 1: biFiltered = ImageNegative(bi); /* Image Negative */
                return;
            //************************************
            case 2: biFiltered = ImagePixelRescale(bi, 2);
                return;
            //************************************
            case 3: biFiltered = ImagePixelShift(bi, 255);
                return;
            case 4:
                biFiltered = prevImage; // Restore previous image
                lastOp = opIndex;
                opIndex = lastLastOp;
                repaint();
        }

    }

    public void menuAction() {
        if (menuIndex == lastMenu){
            return;
        }
        lastMenu = menuIndex;

        switch (menuIndex){
            case 0:
                return;
            case 1:
                System.out.println("UNDO");
                if (prevImage != null) {

                    biFiltered = prevImage; // Restore previous image

                    lastOp = opIndex;
                    opIndex = lastLastOp;
                    repaint();

                }

                setMenuIndex(0);
                break;
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


        if (jb.getActionCommand().equals("Load Image")){
            System.out.println("LOAD IMAGE");

            JFileChooser chooser = new JFileChooser();
            int rval = chooser.showOpenDialog(cb);
            if (rval == JFileChooser.APPROVE_OPTION) {
                File imageFile = chooser.getSelectedFile();
                filename = imageFile.getName().toLowerCase();
                System.out.println(filename);
                try {
                    bi = ImageIO.read(new File("src/images/"+filename));


                } catch (IOException error) {      // deal with the situation that th image has problem;/
                    System.out.println("Image could not be read");

                    System.exit(1);
                }
            }
            repaint();
        }

        if (cb.getActionCommand().equals("Menu")){
            setMenuIndex(cb.getSelectedIndex());
            System.out.println("MENU OPEN");
            repaint();
            cb.setSelectedIndex(0);
        }

        if (cb.getActionCommand().equals("SetFilter")) {
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
        Demo1 de = new Demo1();
        Demo1 de2 = new Demo1();
        f.add("Center", de);
        f.add("West", de2);

        JComboBox choices = new JComboBox(de.getDescriptions());
        choices.setActionCommand("SetFilter");
        choices.addActionListener(de);

        JComboBox formats = new JComboBox(de.getFormats());
        formats.setActionCommand("Formats");
        formats.addActionListener(de);

        JComboBox menu = new JComboBox(de.getMenuActions());
        menu.setActionCommand("Menu");
        menu.addActionListener(de);

        JButton loadButton = new JButton("Load Image");
        loadButton.setActionCommand("Load Image");
        // loadButton.addActionListener(de);
        loadButton.addActionListener(de2);

        JPanel panel = new JPanel();
        panel.add(new JLabel("Menu"));
        panel.add(menu);
        panel.add(choices);
        panel.add(new JLabel("Save As"));
        panel.add(formats);
        panel.add(loadButton);

        f.add("North", panel);

        f.pack();
        f.setVisible(true);
    }
}