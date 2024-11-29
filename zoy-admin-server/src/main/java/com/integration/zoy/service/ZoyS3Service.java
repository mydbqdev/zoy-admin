package com.integration.zoy.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.integration.zoy.constants.ZoyConstant;
import com.integration.zoy.exception.GenericErrorResponse;
import com.integration.zoy.model.MimeTypes;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveBucketArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;

@Service
public class ZoyS3Service {
	@Autowired
	private MinioClient minioClient;

	@Value("${app.minio.url}")
	private String url;

	@Value("${app.minio.user.docs.bucket.name}")
	String userDocsBucket;

	public Boolean uploadFile(String bucketName, String s3Path, String contentType, InputStream inputStream) {
		try {
			
			minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).contentType(contentType).object(s3Path)
					.stream(inputStream, inputStream.available(), -1).build());
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public String uploadFile(String bucketName,String folderName,MultipartFile file) throws IOException {
		String objectName="";
		InputStream inputStream=new BufferedInputStream(file.getInputStream());
		String mimeType= file.getContentType();
		try {
			boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
			if (!found) {
				minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
			}
			objectName = folderName+"/"+file.getOriginalFilename();
			minioClient.putObject(
					PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(
							inputStream, inputStream.available(), -1)
					.contentType(mimeType)
					.build());
		} catch (Exception e) {
			throw GenericErrorResponse.builder()
			.message("Unable to upload file to S3")
			.httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
			.build();
		}
		return url+"/"+bucketName+"/"+objectName;
	}

	public Map<String,Object>  downloadFile(String bucketName,String id) {
		Map<String,Object> map = new HashMap<String,Object>();
		try {

			InputStream is = minioClient.getObject(
					GetObjectArgs.builder()
					.bucket(bucketName)
					.object(id)
					.build());
			if(is!=null)
			{
				StatObjectResponse objectStat =
						minioClient.statObject(
								StatObjectArgs.builder()
								.bucket(bucketName)
								.object(id)
								.build());

				map.put("file-content", IOUtils.toByteArray(is));
				map.put("mime-type", objectStat.contentType());
				map.put("file-size", objectStat.size());
				return map;
			}
			else
			{
				throw GenericErrorResponse.builder()
				.message("Unable to download file from S3")
				.httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
				.build();
			}


		} catch (Exception e) {
			throw GenericErrorResponse.builder()
			.message("Unable to download file from S3")
			.httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
			.build();
		}
	}

	public InputStream downloadFile2(String bucketName, String s3Path) 
	{
		InputStream is  = null;
		  try {
			  is = minioClient.getObject(
	                    GetObjectArgs.builder()
	                            .bucket(bucketName)
	                            .object(s3Path)
	                            .build());
	                    
			 
			  return is;

		  } catch (Exception ex) {
				ex.printStackTrace();
				return is;
			}
			
	}
	public String deleteFile(String bucketName, String id) {
		try {
			minioClient.removeObject(
					RemoveObjectArgs.builder()
					.bucket(bucketName)
					.object(id)
					.build());

		} catch (Exception e) {
			throw GenericErrorResponse.builder()
			.message("Unable to remove file from S3")
			.httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
			.build();
		}
		return url+"/"+bucketName+"/"+id;
	}

	public void deleteBucket(String bucketName) {
		try {
			minioClient.removeBucket(
					RemoveBucketArgs.builder()
					.bucket(bucketName)
					.build());

		} catch (Exception e) {
			throw GenericErrorResponse.builder()
			.message("Unable to delete bucket from S3")
			.httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
			.build();
		}
	}

	public void createBucket(String bucketName) {
		try {
			boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
			if (!found) {
				minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
			}

		} catch (Exception e) {
			throw GenericErrorResponse.builder()
			.message("Unable to create bucket to S3")
			.httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
			.build();
		}
	}

	public Boolean uploadRentalAgreement(String userId,String bookingId, String contentType, InputStream  inputStream) 
	{
		String s3Path =  userId + "/" + bookingId + "/" + ZoyConstant.RENTAL_AGGREMENT_PDF_NAME;
		return uploadFile(userDocsBucket, s3Path,contentType,inputStream);
	}

	public Boolean uploadCancellationPdf(String userId,String bookingId, String contentType, InputStream  inputStream) 
	{
		String s3Path =  userId + "/" + bookingId + "/" + ZoyConstant.CANCELLATION_PDF_NAME;
		return uploadFile(userDocsBucket, s3Path,contentType,inputStream);
	}
	
	
	public InputStream downloadRentalAgreement(String userId,String bookingId) 
	{
		String s3Path =  userId + "/" + bookingId + "/" + ZoyConstant.RENTAL_AGGREMENT_PDF_NAME;
		return downloadFile2(userDocsBucket, s3Path);
	}
	
	public InputStream downloadCancellationFile(String userId,String bookingId) 
	{
		String s3Path =  userId + "/" + bookingId + "/" + ZoyConstant.CANCELLATION_PDF_NAME;
		return downloadFile2(userDocsBucket, s3Path);
	}
}
