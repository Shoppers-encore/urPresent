package handler;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Enumeration;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import databean.ImageInfoDataBean;
import databean.ProductDataBean;
import databean.UserDataBean;
import db.ProductDao;
import db.UserDao;
import etc.HandlerHelper;

@Controller
public class AdminProHandler {
	@Resource
	private UserDao userDao;
	public static final int USERLEVEL=9;
	
	@RequestMapping("/admLoginPro")
	public ModelAndView admLoginPro ( HttpServletRequest request, HttpServletResponse response ) {
		String id = request.getParameter( "id" );
		String password = request.getParameter( "password" );
		UserDataBean userDto = userDao.getUser(id);
		int result = 0;
		if( userDto.getUserLevel() == USERLEVEL && 
				userDto.getPassword().equals( password ) ) {
			result = 1; 
		}
		request.setAttribute( "result", result );
		request.getSession().setAttribute("memid", id);
		return new ModelAndView ("adm/pro/admLoginPro");
	}
	
	@SuppressWarnings("null")
	@RequestMapping ( "/productInputPro" )
	   public ModelAndView productInputPro ( HttpServletRequest request, HttpServletResponse response ) throws IOException {
		  ProductDao productDao = new ProductDao(); 
	      //ImageInfoDataBean imgDto = new ImageInfoDataBean();
	      String path =  request.getSession().getServletContext().getRealPath( "/save" );
	      MultipartRequest multi = null;
	      new File( path ).mkdir();      // 폴더 이미 존재하면 생성X 존재 안 하면 생성O 중복X
	      if(-1 < request.getContentType().indexOf("multipart/form-data"))
	         multi = new MultipartRequest( request, path, 1024*1024*5, "UTF-8", new DefaultFileRenamePolicy() );
	      	int ref=Integer.parseInt(multi.getParameter("product_code"));
	      String systemName = null;
	      Enumeration<?> e = multi.getFileNames();
	      while( e.hasMoreElements() ) {
	         String inputName = (String) e.nextElement();
	         //String originName = multi.getOriginalFileName( inputName );
	          systemName = multi.getFilesystemName( inputName );
	         
	         String sname = path + "\\" + systemName;            // love5.png
	         String tname = path + "\\t" + systemName;         // tlove5.png
	         RenderedOp op = JAI.create("fileload", sname);
	         BufferedImage sbuffer = op.getAsBufferedImage();
	         
	         int SIZE = 3;
	         int width = sbuffer.getWidth() / SIZE;
	         int height = sbuffer.getHeight() / SIZE;
	         
	         BufferedImage tbuffer = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
	         Graphics g = tbuffer.getGraphics();
	         g.drawImage(sbuffer, 0, 0, width, height, null );   
	         
	   
	            ImageIO.write( tbuffer, "jpg", new File( tname ) );   
	            ImageIO.write( tbuffer, "png", new File( tname ) );
	            ImageIO.write( tbuffer, "gif", new File( tname ) );
	        

	      	ImageInfoDataBean imgDto = new ImageInfoDataBean();
	          int imageNo = productDao.getImgNo()+1;
	  	      imgDto.setImageNo(imageNo);
	  	      imgDto.setRef( ref );
	  	      imgDto.setImageAddress(systemName);
	  	      int result = productDao.insertImgInfo(imgDto);
		  	   if( result == 1 ) {
		  		   String sql = "INSERT INTO jk_imageInfo (imageno, ref, imageAddress)"
		               		+ "VALUES ("+imageNo+", "+ ref+", '"+systemName+"' );";
	              new HandlerHelper().fileWriter(sql);
		  	   }
		  	 request.setAttribute( "systemName", systemName );
			}
	      
	      
	      String col[] = multi.getParameterValues( "color" );
	      int colors[] = null;
	      if( col != null ) {
	         colors = new int[ col.length ];
	      for( int i=0;i<col.length; i++ ) {
	         colors[i] = Integer.parseInt( col[i] );
	         }
	      }
	      
	      String siz[] = multi.getParameterValues( "size" );
	      int sizes[] = null;
	      if( siz != null ) {
	         sizes = new int[ siz.length ];
	      for( int i=0;i<siz.length; i++ ) {
	         sizes[i] = Integer.parseInt( siz[i] );
	         }
	      }
	     // int ref = Integer.parseInt( multi.getParameter( "product_code" ) );
	      ProductDataBean productDto = new ProductDataBean();
	      String[] product_codes = new HandlerHelper().makeProductCode(colors, sizes, ref);
	      //String product_code[] = this.makeProductCode( int colors[], int sizes[] );
	      for(int i=0; i<product_codes.length;i++) {
	    	  
	    	 String product_name = multi.getParameter( "product_name" );
	    	 int quantity = Integer.parseInt( multi.getParameter( "quantity" ) );
	    	 int category = Integer.parseInt(multi.getParameter("category"));
	    	 String good_content = multi.getParameter("good_content");
	    	
	    	 String saleCheck = multi.getParameter( "sale" );
	    	 int sale;
	    	 
	    	 if( saleCheck == null || saleCheck.equals("") ) {
	    		 productDto.setDiscount( 0 );
	    		 sale = 0;
	    	 } else {
	    		 sale = Integer.parseInt( saleCheck );
	    		 productDto.setDiscount( sale );
	    	 }
	    	 
	    	 int price = Integer.parseInt( multi.getParameter( "price" ) );
	         String thumbnail = "thumbnail";
	        		 //multi.getParameter( "upload1" );
	         productDto.setRef( ref );
	         productDto.setProductCode( product_codes[i] );
	         productDto.setProductName( product_name ); 
	         productDto.setProductContent( good_content );
	         productDto.setProductPrice( price );
	         productDto.setProductRegDate( new Timestamp( System.currentTimeMillis() ) );
	         productDto.setProductCategory( category );
	         productDto.setProductQuantity( quantity );
	         productDto.setThumbnail( thumbnail );
	        
	         
	        int result2 = productDao.input( productDto );
	         
	        productDao.getProdCount();
	         System.out.println("ref:" + ref);
	         if( result2 >= 1 ) {
	               String sql = "INSERT INTO jk_product (ref, productCode, productName, productContent, discount, productPrice, productRegDate, productQuantity, thumbnail, productCategory) "
		               		+ "VALUES (" + ref + ", '" + product_codes[i] +"', " + product_name + ", '" + good_content + "', " + sale + ", " + price +  ", sysdate, " + quantity + "," + category + ", '" + thumbnail + "');";
	               new HandlerHelper().fileWriter(sql);
	         }
	         
	         request.setAttribute( "result", result2 );
	      }  
		return new ModelAndView ( "adm/pro/productInputPro" );
	   }
	
	@RequestMapping ( "/productModifyPro" )
	public String productModifyPro ( HttpServletRequest request, HttpServletResponse response ) {
		return "redirect:productDetail.jk";
	}
	@RequestMapping("/productDeletePro")
	public ModelAndView productDeletePro(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("adm/pro/productDeletePro");
	}
	@RequestMapping("/orderStatusChange")
	public String orderStatusChange(HttpServletRequest request, HttpServletResponse response) {
		return "redirect:admOrderList.jk";
	}
	@RequestMapping("/admReviewDelete")
	public ModelAndView admReviewDelete(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("adm/pro/admReviewDelete");
	}
	@RequestMapping("/tagInputPro")
	public ModelAndView tagInputPro(HttpServletRequest requset, HttpServletResponse response) {
		return new ModelAndView("adm/pro/tagInputPro");
	}
	@RequestMapping("/tagDeletePro")
	public ModelAndView tagDeletePro(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("adm/pro/tagDeletePro");
	}
	@RequestMapping("/tagModifyPro")
	public String tagModifyPro(HttpServletRequest request, HttpServletResponse response) {
		return "redirect:tagList.jk";
	}
}
