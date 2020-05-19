package com.corona.apple.client;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.InputStream;

public class AmazonS3Client {
    private AmazonS3Client() {
    }

      public static final String uploadFileToS3(File fileToUpload, String fileName, String
   bucketName) {

          Regions clientRegion = Regions.AP_SOUTH_1;

          AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(clientRegion).build();

          PutObjectRequest request = new PutObjectRequest(bucketName, fileName, fileToUpload);

          if (s3Client.putObject(request).getETag() != null) {
              return "https://" + bucketName + ".s3." + clientRegion.getName() + ".amazonaws.com/"
   + fileName;
          } else {
              return "";
          }
      }

      public static final String uploadFileToS3(InputStream inputStream, String fileName, String
   bucketName) {
          Regions clientRegion = Regions.AP_SOUTH_1;
          AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(clientRegion).build();

          ObjectMetadata metaData = new ObjectMetadata();

          PutObjectRequest request = new PutObjectRequest(bucketName, fileName, inputStream,
   metaData);

          if (s3Client.putObject(request).getETag() != null) {
              return "https://" + bucketName + ".s3." + clientRegion.getName() + ".amazonaws.com/"
   + fileName;
          } else {
              return "";
          }
      }
}
