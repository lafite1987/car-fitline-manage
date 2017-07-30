package cn.lfy.base.web;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import cn.lfy.base.web.core.UploadBase;
import cn.lfy.common.model.Message;

import com.alibaba.fastjson.JSONObject;

/**
 * 通用上传组件<br>
 * 支持上传所有文件类型
 *
 */
@Controller
public class CommonUploadController extends UploadBase {

	private static final Set<String> SUFFIX = new HashSet<String>( Arrays.asList( "jpg", "png", "jpeg", "txt", "xls", "xlsx", "csv", "mp3", "webp" ) );

	@RequestMapping( "/common/upload" )
	@ResponseBody
	protected final Object upload( MultipartHttpServletRequest request ) throws IOException {
		String baseDir = request.getSession().getServletContext().getRealPath( "/upload/" );
		return Message.newBuilder().data( super.processMultipartFile( request.getFileMap(), baseDir ) );
	}

	@Override
	protected boolean checkExtension( String extension ) {
		if( null == extension ) {
			return false;
		}

		return SUFFIX.contains( extension.toLowerCase() );
	}

	@Override
	protected void postProcess( JSONObject response, String prefixPath ) {

	}
}
