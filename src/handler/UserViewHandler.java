package handler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import databean.ReviewScoreDataBean;
import databean.BasketDataBean;
import databean.ProductDataBean;
import db.BasketDao;
import databean.ReviewDataBean;
import db.BoardDao;
import db.ProductDao;
import etc.HandlerHelper;


@Controller
public class UserViewHandler {
	@Resource
	private ProductDao productDao;
	@Resource
	private BasketDao basketDao;
	@Resource
	private BoardDao boardDao;
  
	//Main
	@RequestMapping("/main")
	public ModelAndView main(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Map<String, String> map = new HashMap<String, String>();
		map.put("searchWord", "");
		map.put("selectedColors", "");
		int count = productDao.getProductCount(map);
		map = new HandlerHelper().makeCount(count, request);
		map.put("searchWord", "");
		map.put("selectedColors", "");
		List<ProductDataBean> productList = productDao.getProductList(map);
		request.setAttribute("productList", productList);
		request.setAttribute("productCount", count);
		return new ModelAndView("user/view/userMain");
	}
	
	
	// User
	@RequestMapping( "/userMailCheck" )
	public ModelAndView userMailCheck (HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView( "user/view/userMailCheck" );
	}
	
	@RequestMapping("/userMypage")
	public ModelAndView userMypage(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("user/view/userMypage");
	}
	
	
	// Basket
	@RequestMapping( "/basketList" )
	public ModelAndView basketList ( HttpServletRequest request, HttpServletResponse response ) {
		/* Temporarily loaded customer id */
		String id="aaa";		
		//String id=(String)request.getSession().getAttribute("id");
		
		/* Get items from jk_basket using id */
		List<BasketDataBean> basketList=basketDao.getBasketList(id);
		
		/* Total number of items in the basket */
		int basketCount=basketDao.getBasketCount(id);
		
		/* Create a HashMap that can hold color and size options for each item in the basket */
		HashMap<String, HashSet<String>> prodColors=new HashMap<String, HashSet<String>>();
		HashMap<String, HashSet<String>> prodSizes=new HashMap<String, HashSet<String>>();
		
		/* For each item in the basket: */
		for(BasketDataBean product:basketList) {
			HashSet<String> colors=new HashSet<String>();
			HashSet<String> sizes=new HashSet<String>();
			
			// 1-1) Get its productCode and reference number
			String productCode=product.getProductCode();
			String ref;
			// 1-2) Check if productCode=ref (=options not selected)
			if(productCode.length()<7) {
				ref=productCode;
			} else {
				ref=productCode.substring(2,productCode.length()-2);
			}
				
			// 2) Get productCodes with the same reference number
			List<ProductDataBean> prodCodesFromRef=productDao.getProductCodesByRef(ref);
			
			// 3) Get color and size options using the productCodes
			for(ProductDataBean prodCode:prodCodesFromRef) {
				String color=prodCode.getProductCode().substring(0,2);
				colors.add(color);
					
				String size=prodCode.getProductCode().substring(prodCode.getProductCode().length()-2,prodCode.getProductCode().length());
				sizes.add(size);
			}

			// 4) Add color and size options for each item to prodOptions
			prodColors.put(productCode, colors);
			prodSizes.put(productCode, sizes);
		}

		
		request.setAttribute("productColors", prodColors);
		request.setAttribute("productSizes", prodSizes);
		request.setAttribute("basketList", basketList);
		request.setAttribute("basketCount", basketCount);
		
		return new ModelAndView( "/user/view/basketList" );
	}
	
	
	//Product
	@RequestMapping ( "/userProductList" )
	public ModelAndView userProductList ( HttpServletRequest request, HttpServletResponse response ) {
		return new ModelAndView ( "user/userMain" );
	}
	
	@RequestMapping("/userProductDetail")
	public ModelAndView userProductDetail(HttpServletRequest request, HttpServletResponse response) {
		int ref = Integer.parseInt(request.getParameter("ref"));
		List<ProductDataBean> list =productDao.getProductDetail(ref);
		String[] colors = new HandlerHelper().whatColor(new HandlerHelper().decodeColorCode(list));
		String[] sizes = new HandlerHelper().whatSize(new HandlerHelper().decodeSizeCode(list));
		request.setAttribute("productList", list);
		request.setAttribute("colors", colors);
		request.setAttribute("sizes", sizes);
		return new ModelAndView("user/view/userProductDetail");
	}
	@RequestMapping("/userSearchProduct")
	public ModelAndView userSearchProduct(HttpServletRequest request, HttpServletResponse response) {
		Map<String, String> map = new HashMap<String, String>();
		StringBuffer color = new StringBuffer();
		int count = 0;
		if(request.getParameterValues("color") != null || request.getParameter("searchWord")!=null) {
			map.put("searchWord", request.getParameter("searchWord"));
			String[] colors = request.getParameterValues("color");
			if(colors !=null) {
				for(int i = 0 ; i<colors.length ;i++) {
					color.append(colors[i]+" ");
				}
			}
			map.put("selectedColors", color.toString());
			count = productDao.getProductCount(map);
		}
		map = new HandlerHelper().makeCount(count, request);
		List<ProductDataBean> productList = null;
		if(request.getParameterValues("color") != null || request.getParameter("searchWord")!=null) {
			map.put("searchWord", request.getParameter("searchWord"));
			map.put("selectedColors",color.toString());
			productList = productDao.getProductList(map);
		}
		request.setAttribute("productCount", count);
		request.setAttribute("productList", productList);
		return new ModelAndView("user/view/userSearchProduct");
	}
	
	
	// Order
	@RequestMapping("/userOrderDetail")
	public ModelAndView userOrderDetail(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("user/view/userOrderDetail");
	}
	@RequestMapping("/userOrderList")
	public ModelAndView userOrderList(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("user/view/userOrderList");
	}

	
	// Review
	@RequestMapping("/reviewList")
	public ModelAndView reviewList(HttpServletRequest request, HttpServletResponse response) {
		int count = boardDao.getReviewCount();	

		if( count > 0 ) {
			Map<String, String> map = new HandlerHelper().makeCount( count, request );
			List <ReviewDataBean> articles = boardDao.getReviewList( map );
			request.setAttribute( "reviewLists", articles );
		}
		return new ModelAndView("user/view/reviewList");
	}
	
	@RequestMapping( "/reviewDetail" )
	public ModelAndView reviewDetail (HttpServletRequest request, HttpServletResponse response) {
		int num = Integer.parseInt( request.getParameter( "reviewNo" ) );
		String pageNum = request.getParameter( "pageNum" );
		String number = request.getParameter( "number" );
		
		ReviewDataBean reviewDto = boardDao.get( num );
		reviewDto.setReviewScoreSum( boardDao.getReviewLikes(num) );
		String id=(String)request.getSession().getAttribute("memid");

		if(id !=null) {
			Map<String, String> map = new HashMap<String,String>();
			map.put("reviewNo", new Integer(num).toString());
			map.put("id", id);
			int me = boardDao.getReviewLike(map);
			if(me>0) {
				reviewDto.setCheckedme( true );
			}
		}
		reviewDto.setProductName(new ProductDao().getProductName(reviewDto.getProductCode()));
		if(id == null || ! ((String)request.getSession().getAttribute( "memid" )).equals(reviewDto.getId() ) )
			boardDao.addCount(num);
		
		request.setAttribute( "number", number );
		request.setAttribute( "pageNum", pageNum );
		request.setAttribute( "reviewDto", reviewDto );
		
		return new ModelAndView( "user/view/reviewDetail" );
	}
	
	@RequestMapping("/like")
	public String reviewLike(HttpServletRequest request, HttpServletResponse response) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("reviewNo", request.getParameter("reviewNo"));
		map.put("id", (String)request.getSession().getAttribute("memid"));
		boardDao.insertReviewLike(map);
		return "redirect:reviewDetail.jk?reviewNo="+request.getParameter("reviewNo")+"&number="+request.getParameter("number");
	}
	
	@RequestMapping("/cancelLike")
	public String reviewLikeCancel(HttpServletRequest request, HttpServletResponse response) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("reviewNo", request.getParameter("reviewNo"));
		map.put("id", (String)request.getSession().getAttribute("memid"));
		boardDao.deleteReviewLike(map);
		return "redirect:reviewDetail.jk?reviewNo="+request.getParameter("reviewNo")+"&number="+request.getParameter("number");
	}
}
