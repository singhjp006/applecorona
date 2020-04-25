package com.corona.apple.client;

public class AmazonS3Client {

  //    private AmazonS3Helper(){
  //
  //    }
  //
  //    public static final String uploadFileToS3(File fileToUpload, String fileName, String
  // bucketName) {
  //
  //        Regions clientRegion = Regions.AP_SOUTH_1;
  //
  //        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(clientRegion).build();
  //
  //        PutObjectRequest request = new PutObjectRequest(bucketName, fileName, fileToUpload);
  //
  //        if (s3Client.putObject(request).getETag() != null) {
  //            return "https://" + bucketName + ".s3." + clientRegion.getName() + ".amazonaws.com/"
  // + fileName;
  //        } else {
  //            return "";
  //        }
  //    }
  //
  //    public static final String uploadFileToS3(InputStream inputStream, String fileName, String
  // bucketName) {
  //        Regions clientRegion = Regions.AP_SOUTH_1;
  //        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(clientRegion).build();
  //
  //        ObjectMetadata metaData = new ObjectMetadata();
  //
  //        PutObjectRequest request = new PutObjectRequest(bucketName, fileName, inputStream,
  // metaData);
  //
  //        if (s3Client.putObject(request).getETag() != null) {
  //            return "https://" + bucketName + ".s3." + clientRegion.getName() + ".amazonaws.com/"
  // + fileName;
  //        } else {
  //            return "";
  //        }
  //    }
}
