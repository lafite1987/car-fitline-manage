package cn.lfy.base.web.core;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import cn.lfy.common.utils.UUIDUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 上传组件
 * 
 * @author liaopeng
 * @date 2015-12-25 下午7:22:47
 */
public abstract class UploadBase {

	protected static final Logger logger = LoggerFactory.getLogger( UploadBase.class );

	protected final JSONArray processMultipartFile(
			Map<String, MultipartFile> multipartFiles, String baseDir ) throws IOException {
		if( null == multipartFiles || multipartFiles.isEmpty() ) {
		}
		JSONArray rs = new JSONArray( multipartFiles.size() );
		for( Entry<String, MultipartFile> en : multipartFiles.entrySet() ) {
			MultipartFile multipartFile = en.getValue();
			String originalFilename = multipartFile.getOriginalFilename();
			String extension = FilenameUtils.getExtension( originalFilename );
			if( !checkExtension( extension ) ) {
				
			}
			String destFileName = saveMultipartFile( multipartFile, baseDir );
			if( logger.isErrorEnabled() ) {
				logger.info( String.format(
						"Upload file - name:%s|size:%d|extension:%s",
						multipartFile.getName(), multipartFile.getSize(),
						extension ) );
			}
			String fileName=multipartFile.getOriginalFilename();
			int lastD=fileName.lastIndexOf( "." );
			JSONObject r = new JSONObject();
			r.put( "name", multipartFile.getName() );			
			r.put( "picName", fileName.substring( 0, lastD ) );
			r.put( "url", destFileName );
			postProcess( r, baseDir );
			rs.add( r );
		}
		return rs;
	}

	protected abstract boolean checkExtension( String extension );

	protected abstract void postProcess( JSONObject response, String baseDir );

	protected String saveMultipartFile( MultipartFile multipartFile, String baseDir )
			throws IOException {
		String name = generateFileName( multipartFile.getOriginalFilename() );
		FileUtils.copyInputStreamToFile( multipartFile.getInputStream(),
				new File( baseDir + "/" + name ) );
		return name;
	}

	protected String generateFileName( String fileName ) {
		String newName = UUIDUtil.uuid()
				+ FilenameUtils.EXTENSION_SEPARATOR
				+ FilenameUtils.getExtension( fileName );
		return newName;
	}
}
