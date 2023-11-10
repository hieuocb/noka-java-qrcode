package com.hoclamdev;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Demo3 {
    public static void main(String[] args) {
        String data = "Your QR Code Data"; // Dữ liệu bạn muốn chứa trong mã QR
        String imagePath = "output\\background.jpg"; // Đường dẫn đến hình ảnh có màu

        try {
            BufferedImage qrCodeImage = generateQRCode(data);
            BufferedImage backgroundImage = resize(loadImage(imagePath), 300, 300);

            // Thay thế màu đen trong mã QR bằng hình ảnh có màu
            mergeImages(backgroundImage, qrCodeImage, 0, 0); // Thay (100, 100) bằng vị trí mong muốn

            // Lưu hình ảnh kết quả
            saveImage(backgroundImage, "output\\image.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage generateQRCode(String data) throws Exception {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix bitMatrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, 300, 300, hints);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
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

    private static void mergeImages(BufferedImage background, BufferedImage qrCode, int x, int y) {
        Graphics2D g = background.createGraphics();
        g.drawImage(qrCode, x, y, null);
        g.dispose();
    }

    private static void saveImage(BufferedImage image, String outputPath) throws IOException {
        ImageIO.write(image, "png", new File(outputPath));
    }

}
