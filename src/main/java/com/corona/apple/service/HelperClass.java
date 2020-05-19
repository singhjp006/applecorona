package com.corona.apple.service;

import com.corona.apple.client.AmazonS3Client;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.Date;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HelperClass {

    public String uploadFileToS3(MultipartFile productImage) throws IOException {

        return null;
        //return AmazonS3Helper.uploadFileToS3(convertMultipartToFile(productImage), convertMultipartToFile(productImage).getName(), "apple-corona-product-images");
    }

    private byte[] convertFileToBytesArray(File file) throws IOException {
        byte[] bytesArray = new byte[(int) file.length()];

        FileInputStream fis = new FileInputStream(file);
        fis.read(bytesArray); // read file into bytes[]
        fis.close();
        return bytesArray;
    }

    private File convertMultipartToFile(MultipartFile file) throws IOException {
        File convertedFile = new File((new Date()).toString());

        //I think we need not to do this as well, will try
        convertedFile.createNewFile();

        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(file.getBytes());
        fos.close();
        return convertedFile;
    }

    public String uploadFileToS3FromUrl(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        Image image = ImageIO.read(url);

        String fileName = url.getFile().substring(url.getFile().lastIndexOf("/") + 1);

        InputStream is = url.openStream();

        return AmazonS3Client.uploadFileToS3(is, fileName, "apple-corona-product-images");
    }
}
