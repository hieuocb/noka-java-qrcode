package com.hoclamdev;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Demo4 {
    public static void main(String[] args) {
        String data = "Your QR Code Data"; // Dữ liệu bạn muốn chứa trong mã QR
        String imagePath = "output\\background.jpg"; // Đường dẫn đến hình ảnh có màu

        try {
            BufferedImage qrCodeImage = generateQRCode(data);
            BufferedImage backgroundImage = resize(loadImage(imagePath), 300, 300);

            //BufferedImage backgroundBlank = createBackground(300, 300);//grayImage(backgroundImage, 300, 300);
            BufferedImage backgroundBlank = backgroundImage;
            //mergeImages(backgroundBlank, backgroundImage, 0, 0);

            // Thay thế màu đen trong mã QR bằng hình ảnh có màu
            //mergeImages(backgroundBlank, qrCodeImage, 0, 0);
            mergeImages2(qrCodeImage, backgroundBlank, 0, 0);

            // Lưu hình ảnh kết quả
            //saveImage(backgroundBlank, "output\\image1.png");
            saveImage(qrCodeImage, "output\\image1.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private static BufferedImage grayImage(BufferedImage image, int width, int height) {
//        BufferedImage bwImage = new BufferedImage(width,
//                height, BufferedImage.TYPE_BYTE_GRAY);
//        Graphics g = bwImage.getGraphics();
//        g.drawImage(image,0,0,null);
//        return bwImage;
//    }

    private static BufferedImage blurImage(BufferedImage img) {
        BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), img.getType()) ;
        final int H = img.getHeight() - 1 ;
        final int W = img.getWidth() - 1 ;

        for (int c=0 ; c < img.getRaster().getNumBands() ; c++) // for all the channels/bands
            for (int x=1 ; x < W ; x++) // For all the image
                for (int y=1; y < H ; y++)
                {
                    int newPixel = 0 ;
                    for (int i=-1 ; i <= 1 ; i++) // For the neighborhood
                        for (int j=-1 ; j <= 1 ; j++) {
                            newPixel += img.getRaster().getSample(x + i, y + j, c);
                        }
                    newPixel = (int)(newPixel/9.0 + 0.5);
                    result.getRaster().setSample(x, y, c, newPixel) ;
                }
        return result;
    }

    private static BufferedImage generateQRCode(String data) throws Exception {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);
        hints.put(EncodeHintType.MARGIN, 0);
        int mWidth = 300;
        int mHeight = 300;

        BitMatrix bitMatrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, mWidth, mHeight, hints);

        BufferedImage imageResult = new BufferedImage(mWidth, mHeight, 3);

        Color myWhite = new Color(120, 120, 120); // Color white
        int rgb = myWhite.getRGB();

        for (int i = 0; i < mHeight; i++) {
            for (int j = 0; j < mWidth; j++) {
                if (bitMatrix.get(j, i)) {// True if is is Black
                    imageResult.setRGB(j, i, 0xFFFFFFFF);
                } else {
                    imageResult.setRGB(j, i, 0x00000000);
                }
            }
        }

        return imageResult;
    }

    private static BufferedImage loadImage(String path) throws IOException {
        return ImageIO.read(new File(path));
    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    private static BufferedImage createBackground(int width, int height) {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D ig2 = bi.createGraphics();

        ig2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ig2.setBackground(Color.yellow);
        ig2.clearRect(0, 0, width, height);
        return bi;
    }

    private static void mergeImages(BufferedImage background, BufferedImage qrCode, int x, int y) {
        Graphics2D g = background.createGraphics();
        g.drawImage(qrCode, x, y, null);
        g.dispose();
    }

    private static void mergeImages2(BufferedImage qrCode, BufferedImage replacementImage, int x, int y) {
        int qrWidth = qrCode.getWidth();
        int qrHeight = qrCode.getHeight();

        for (int i = 0; i < qrWidth; i++) {
            for (int j = 0; j < qrHeight; j++) {
                // Lấy màu của điểm ảnh trong mã QR
                Color qrColor = new Color(qrCode.getRGB(i, j));

                // Kiểm tra xem nếu màu là đen (màu mặc định của mã QR)
                if (qrColor.equals(Color.WHITE)) {
                    // Lấy màu từ hình ảnh thay thế và đặt màu mới cho điểm ảnh trong QR
                    Color replacementColor = new Color(replacementImage.getRGB(i, j));
                    qrCode.setRGB(i, j, replacementColor.getRGB());
                }
            }
        }
    }

    private static void saveImage(BufferedImage image, String outputPath) throws IOException {
        ImageIO.write(image, "png", new File(outputPath));
    }
}
