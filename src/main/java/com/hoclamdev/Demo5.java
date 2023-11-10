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

public class Demo5 {
    public static void main(String[] args) {
        String data = "https://ocb.com.vn/"; // Dữ liệu bạn muốn chứa trong mã QR
        String imagePath = "output\\background.jpg"; // Đường dẫn đến hình ảnh có màu

        try {
            BufferedImage qrCodeImage = generateQRCode(data);
            BufferedImage backgroundBlank = resize(loadImage(imagePath), 500, 500);
            mergeImages(qrCodeImage, backgroundBlank, 0, 0);
            saveImage(qrCodeImage, "output\\image1.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        for (int i = 0; i < mHeight; i++) {
            for (int j = 0; j < mWidth; j++) {
                if (bitMatrix.get(j, i)) {
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

    private static void mergeImages(BufferedImage qrCode, BufferedImage replacementImage, int x, int y) {
        int qrWidth = qrCode.getWidth();
        int qrHeight = qrCode.getHeight();

        for (int i = 0; i < qrWidth; i++) {
            for (int j = 0; j < qrHeight; j++) {
                Color qrColor = new Color(qrCode.getRGB(i, j));
                if (qrColor.equals(Color.WHITE)) {
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
