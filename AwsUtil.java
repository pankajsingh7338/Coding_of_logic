package com.actolap.wse;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.slf4j.LoggerFactory;


import com.actolap.wse.config.Configuration;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;

public class AWSUtil {
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(AWSUtil.class);

	public static String uploadImageToS3(String folder, String name, byte[] imageByte) {
		String imageKey = folder + "/" + name + "/" + System.currentTimeMillis() / 1000 + ".png";
		BasicAWSCredentials creds = new BasicAWSCredentials(Configuration.AWS_KEY, Configuration.AWS_SECRET);
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.AP_SOUTH_1).withCredentials(new AWSStaticCredentialsProvider(creds)).build();
		try {
			InputStream stream = new ByteArrayInputStream(imageByte);
			ObjectMetadata meta = new ObjectMetadata();
			meta.setContentLength(imageByte.length);
			meta.setContentType("image/jpeg");
			s3Client.putObject(new PutObjectRequest(Configuration.AWS_BUCKET, imageKey, stream, meta).withCannedAcl(CannedAccessControlList.PublicRead));
		} catch (AmazonServiceException ase) {
			logger.info("Caught an AmazonServiceException, which " + "means your request made it " + "to Amazon S3, but was rejected with an error response" + " for some reason.");
			logger.info("Error Message:    " + ase.getMessage());
			logger.info("HTTP Status Code: " + ase.getStatusCode());
			logger.info("AWS Error Code:   " + ase.getErrorCode());
			logger.info("Error Type:       " + ase.getErrorType());
			logger.info("Request ID:       " + ase.getRequestId());
			imageKey = null;
		} catch (AmazonClientException ace) {
			logger.info("Caught an AmazonClientException, which " + "means the client encountered " + "an internal error while trying to " + "communicate with S3, "
					+ "such as not being able to access the network.");
			logger.info("Error Message: " + ace.getMessage());
			imageKey = null;
		}
		return imageKey;
	}
	
	public static String getObjectUrl(String bucketName, String objectKey) {
		BasicAWSCredentials creds = new BasicAWSCredentials(Configuration.AWS_KEY, Configuration.AWS_SECRET);
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.AP_SOUTH_1).withCredentials(new AWSStaticCredentialsProvider(creds)).build();
		 String ret = "";
		try {            
            java.util.Date expiration = new java.util.Date();
            long expTimeMillis = expiration.getTime();
            expTimeMillis += 1000 * 60 * 60;
            expiration.setTime(expTimeMillis);

            // Generate the presigned URL.
            GeneratePresignedUrlRequest generatePresignedUrlRequest = 
                    new GeneratePresignedUrlRequest(bucketName, objectKey)
                    .withMethod(HttpMethod.GET)
                    .withExpiration(expiration);
            URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
            ret = url.toString();
           
        }
        catch(AmazonServiceException e) {
        	logger.info("Error Message: " + e.getMessage());
        }
        catch(SdkClientException e) {
        	logger.info("Error Message: " + e.getMessage());
        }
		return ret;
	}
	
	public static void downloadObject(String bucket_name, String key_name) {
		BasicAWSCredentials creds = new BasicAWSCredentials(Configuration.AWS_KEY, Configuration.AWS_SECRET);
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.AP_SOUTH_1).withCredentials(new AWSStaticCredentialsProvider(creds)).build();
		try {
		    S3Object o = s3Client.getObject(bucket_name, key_name);
		    S3ObjectInputStream s3is = o.getObjectContent();
		    FileOutputStream fos = new FileOutputStream(new File(key_name));
		    byte[] read_buf = new byte[1024];
		    int read_len = 0;
		    while ((read_len = s3is.read(read_buf)) > 0) {
		        fos.write(read_buf, 0, read_len);
		    }
		    s3is.close();
		    fos.close();
		} catch (AmazonServiceException e) {
			logger.info("Error Message: " + e.getMessage());
		} catch (FileNotFoundException e) {
			logger.info("Error Message: " + e.getMessage());
		} catch (IOException e) {
			logger.info("Error Message: " + e.getMessage());
		}
	}
	
	public static String uploadImageFileToAWS(String folder, String name, byte[] imageByte) {
		String imageKey = folder + "/" + name + "/" + System.currentTimeMillis() / 1000 + ".png";
		TransferManager tm = null;
		try {
			BasicAWSCredentials creds = new BasicAWSCredentials(Configuration.AWS_KEY, Configuration.AWS_SECRET);
			AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.AP_SOUTH_1)
					.withCredentials(new AWSStaticCredentialsProvider(creds)).build();
			tm = TransferManagerBuilder.standard()
	                .withS3Client(s3Client)
	                .build();			
			InputStream stream = new ByteArrayInputStream(imageByte);
			ObjectMetadata meta = new ObjectMetadata();
			meta.setContentLength(imageByte.length);
			meta.setContentType("image/jpeg");		
			Upload upload = tm.upload(Configuration.AWS_BUCKET, imageKey, stream, meta);
			try {
				upload.waitForCompletion();	
				s3Client.setObjectAcl(Configuration.AWS_BUCKET, imageKey, CannedAccessControlList.PublicRead);
			} catch (InterruptedException e) {
				logger.info(e.getMessage());
			}
		} catch (AmazonServiceException ase) {
			logger.info("Caught an AmazonServiceException, which " + "means your request made it "
					+ "to Amazon S3, but was rejected with an error response" + " for some reason.");
			logger.info("Error Message:    " + ase.getMessage());
			logger.info("HTTP Status Code: " + ase.getStatusCode());
			logger.info("AWS Error Code:   " + ase.getErrorCode());
			logger.info("Error Type:       " + ase.getErrorType());
			logger.info("Request ID:       " + ase.getRequestId());
			imageKey = null;
		} catch (AmazonClientException ace) {
			logger.info("Caught an AmazonClientException, which " + "means the client encountered "
					+ "an internal error while trying to " + "communicate with S3, "
					+ "such as not being able to access the network.");
			logger.info("Error Message: " + ace.getMessage());
			imageKey = null;
		} finally {
			try {
				tm.shutdownNow();
			} catch (Exception e) {
				logger.info(e.getMessage());
			}			
		}
		return imageKey;
	}
}

