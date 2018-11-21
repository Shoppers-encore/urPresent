package handler;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import databean.ChatDataBean;
import databean.ImageInfoDataBean;
import databean.OrderListDataBean;
import databean.ProductDataBean;
import databean.ReviewDataBean;
import databean.TagDataBean;
import databean.UserDataBean;
import db.BoardDao;
import db.ChatDao;
import db.OrderDao;
import db.ProductDao;
import db.TagDao;
import db.UserDao;
import etc.HandlerHelper;

@Controller
public class AdminViewHandler {
	@Resource
	private UserDao userDao;
	@Resource
	private BoardDao boardDao;
	@Resource
	private ProductDao productDao;
	@Resource
	private ChatDao chatDao;
	@Resource
	private OrderDao orderDao;

   @RequestMapping("/admReviewList")
   public ModelAndView admReviewList(HttpServletRequest request, HttpServletResponse response) {
	   try {
	         request.setCharacterEncoding("utf-8");
	      } catch ( UnsupportedEncodingException e ) {
	         e.printStackTrace();
	      }
	  BoardDao boardDao = new BoardDao();
      String id = (String)request.getSession().getAttribute("id");
      UserDataBean userDto = userDao.getUser(id);
      request.setAttribute( "id", id );
      request.setAttribute( "userDto", userDto );
      
      String searchWord = request.getParameter("searchWord");
      String searchType = request.getParameter("searchType");
      int count = 0;
      if( searchWord == "" || searchWord == null ) {
          // No query
    	  count = boardDao.getReviewCount();
          if( count > 0 ) {
             Map<String, String> map = new HandlerHelper().makeCount( count, request );
             List <ReviewDataBean> reviewList = boardDao.getRvList( map );
             request.setAttribute( "reviewList", reviewList );
          }
      } else {
    	  // Yes query
          Map<String,String> search = new HashMap<String,String>();
          search.put("searchType", searchType);
          search.put("searchWord", searchWord);
          count = boardDao.getReviewSearchCount(search);
          //System.out.println("getReviewSearchCount : " + count);

          Map<String, String> map = new HandlerHelper().makeCount(count, request);
          map.put("searchType", searchType);
          map.put("searchWord", searchWord);
		  List <ReviewDataBean> review = boardDao.getRvSearchList( map );
		  //request.setAttribute("searchType", searchType);
		  request.setAttribute("searchWord", searchWord);
		  request.setAttribute( "reviewList", review );
      }
      return new ModelAndView("adm/view/admReviewList");
   }

	@RequestMapping("/admReviewDetail")
	public ModelAndView admReviewDetail(HttpServletRequest request, HttpServletResponse response) {
		int reviewNo = Integer.parseInt( request.getParameter( "reviewNo" ) );
		String pageNum = request.getParameter( "pageNum" );
		String number = request.getParameter( "number" );
		String productCode = request.getParameter( "productCode" );
		ProductDao productDao = new ProductDao();
		String productName = productDao.getProdName( productCode );

	      String id = (String)request.getSession().getAttribute("id");
	      UserDataBean userDto = userDao.getUser(id);
	      request.setAttribute( "id", id );
	      request.setAttribute( "userDto", userDto );
		
		ReviewDataBean reviewDto = boardDao.get( reviewNo );
		if(id !=null) {
			Map<String, String> map = new HashMap<String,String>();
			map.put("reviewNo", new Integer(reviewNo).toString());
			map.put("id", id);
		}
		request.setAttribute( "productName", productName );
		request.setAttribute( "number", number );
		request.setAttribute( "pageNum", pageNum );
		request.setAttribute( "reviewDto", reviewDto );
		return new ModelAndView("adm/view/admReviewDetail");
	}

	@RequestMapping("/tagList")
	public ModelAndView tagList(HttpServletRequest request, HttpServletResponse response) {
	   String id = (String)request.getSession().getAttribute("id");
	   UserDataBean userDto = userDao.getUser(id);
	   request.setAttribute( "id", id );
	   request.setAttribute( "userDto", userDto );
	   TagDao tagDao = new TagDao();
	   List <TagDataBean> tags = tagDao.getTags();
	   request.setAttribute("tags", tags);
	   return new ModelAndView("adm/view/tagList");
	}

	@RequestMapping("/userList")
	public ModelAndView userList(HttpServletRequest request, HttpServletResponse response) {		
		String id = (String)request.getSession().getAttribute("id");
		int count = userDao.getUserListCount();
		Map<String, String> map = new HandlerHelper().makeCount(count, request);
		List<UserDataBean> members = userDao.getList(100, map);
		UserDataBean userDto = userDao.getUser(id);
		request.setAttribute("members", members);
		request.setAttribute("userDto", userDto);
		return new ModelAndView("adm/view/userList");
	}
	@RequestMapping("/admChatView")
	   public ModelAndView admChatView(HttpServletRequest request, HttpServletResponse response) {
		String id = (String)request.getSession().getAttribute("id");
		UserDataBean userDto = userDao.getUser(id);
		request.setAttribute( "id", id );
		request.setAttribute( "userDto", userDto );
	      return new ModelAndView("adm/view/admChatView");
	}
	@RequestMapping("/admProductView")
	public ModelAndView admProductView( HttpServletRequest request, HttpServletResponse response ) {
		String id = (String)request.getSession().getAttribute("id");
		UserDataBean userDto = userDao.getUser(id);
		request.setAttribute( "id", id );
		request.setAttribute( "userDto", userDto );
		
		try {
			request.setCharacterEncoding("utf-8");
		} catch ( UnsupportedEncodingException e ) {
			e.printStackTrace();
		}
		
		String searchWord = request.getParameter("searchWord");
		HandlerHelper hh = new HandlerHelper();
		Map<String, String> map = new HashMap<String,String>();
		int count = 0;
		if(searchWord != null && searchWord != "") {	// IF there IS an input for searchWord
			map.put("searchWord", searchWord);
			count = productDao.getSearchCount(searchWord);			/////////////// 1116고친곳
			System.out.println("count : " + count);
			if( count == 0 ) {
				request.setAttribute("searchWord", searchWord);
				request.setAttribute("count", count);
				return new ModelAndView("adm/view/admProductView");
			} else {		
				map = hh.makeCount(count, request);
				map.put("searchWord", searchWord);
				Map<String, String> cmap = hh.makeCount(count, request);
				cmap.put("searchWord", searchWord);
				List<ProductDataBean> productList = productDao.getNameSearch(cmap);			////////// 1116 고친곳
				request.setAttribute("searchWord", searchWord);
				request.setAttribute("productList", productList);
				return new ModelAndView("adm/view/admProductView");
			}
		} else {										// IF there is NO input for searchWord
			count = productDao.getProductNoSearchCount(map);
			map = hh.makeCount(count, request);
			List<ProductDataBean> productList = productDao.getNoSearchProductList(map);
			request.setAttribute("productCount", count);
			request.setAttribute("productList", productList);
			return new ModelAndView("adm/view/admProductView");
		}
	}
	@RequestMapping("/admProductList")
	public ModelAndView admProductList( HttpServletRequest request, HttpServletResponse response ) {
			String id = (String)request.getSession().getAttribute("id");
			UserDataBean userDto = userDao.getUser(id);
			request.setAttribute( "id", id );
			request.setAttribute( "userDto", userDto );
	
			int count = productDao.getProdCount();
			Map<String,String> map = new HandlerHelper().makeCount(count, request);
			List <ProductDataBean> products = productDao.getProdList(map);
	
			request.setAttribute("products", products);
			request.setAttribute("count", count);
			return new ModelAndView("adm/view/admProductList");
		}
	
	@RequestMapping ( "/admProductDetail" )
	public ModelAndView productDetail ( HttpServletRequest request, HttpServletResponse response ) {
		String id = (String)request.getSession().getAttribute("id");
		UserDataBean userDto = userDao.getUser(id);
		request.setAttribute( "id", id );
		request.setAttribute( "userDto", userDto );
		int ref = Integer.parseInt(request.getParameter("ref"));
		List<ProductDataBean> list =productDao.getProdDetail( ref );
		List<ImageInfoDataBean> imageList = productDao.getImgDetail( ref );
		String[] colors = new HandlerHelper().whatColor(new HandlerHelper().decodeColorCode(list));
		String[] sizes = new HandlerHelper().whatSize(new HandlerHelper().decodeSizeCode(list));
		request.setAttribute("productList", list);
		request.setAttribute("imageList", imageList);
		request.setAttribute("colors", colors);
		request.setAttribute("sizes", sizes);
		return new ModelAndView ( "adm/view/admProductDetail" );
	}
	@RequestMapping("/admOrderDetail")
	public ModelAndView admOrderDetail(HttpServletRequest request, HttpServletResponse response) {
		int orderCode=Integer.parseInt(request.getParameter("orderCode"));
		int count = orderDao.prodFromOrder(orderCode);
		List<OrderListDataBean> orderDetailList=orderDao.getOrderDetail(orderCode);
		
		request.setAttribute("count", count);
		request.setAttribute("orderDetailList", orderDetailList);
		return new ModelAndView("adm/view/admOrderDetail");
	}
	
	@RequestMapping("/admOrderList")
	public ModelAndView admOrderList(HttpServletRequest request, HttpServletResponse response) {
		String id = (String)request.getSession().getAttribute("id");
		UserDataBean userDto = userDao.getUser(id);
		request.setAttribute( "id", id );
		request.setAttribute( "userDto", userDto );
		
		OrderDao orderDao = new OrderDao();
		int count = orderDao.getOrderCount();
				
		Map<String, String> map = new HandlerHelper().makeCount(count, request);
		List<OrderListDataBean> orders = orderDao.getOrderList(map);
		
		request.setAttribute("orders", orders);
		request.setAttribute("count", count);
		return new ModelAndView("adm/view/admOrderList");
	}	
	//chat ajax
	@RequestMapping("/admChatList")
	@ResponseBody
	public List<ChatDataBean> admChatList(HttpServletRequest request,HttpServletResponse response){
		int count = chatDao.getChatListCount();
		List<ChatDataBean> chatList = null;
		if(count > 0) {
			chatList = chatDao.getChatList();
			request.setAttribute("chatList", chatList);
		}
		return chatList;
	}
   @RequestMapping("/admChatting")
   public ModelAndView admChatting(HttpServletRequest request, HttpServletResponse response) {
      String id = request.getParameter("id");
      request.setAttribute("id", id);
      return new ModelAndView("adm/view/admChatting");
   }
   
   @RequestMapping("/admChatInput")
   @ResponseBody
   public void admChatInput(HttpServletRequest request, HttpServletResponse response) {
      String id = request.getParameter("id");
      String chatContent = request.getParameter("chatContent");
      ChatDataBean chat = new ChatDataBean();
      chat.setSender("admin");
      chat.setReceiver(id);
      chat.setChatContent(chatContent);
      chatDao.chatInput(chat);
   }
   @RequestMapping("/admChat")
   @ResponseBody
   public List<ChatDataBean> admChat(HttpServletRequest request, HttpServletResponse response){
      String id = request.getParameter("id");
      List<ChatDataBean> chatData = chatDao.getList(id);
      request.setAttribute("chatData", chatData);
      return chatData;
   }
	@RequestMapping("/admUserOrderList")
	public ModelAndView admUserOrderList(HttpServletRequest request, HttpServletResponse response) {		
		String id = (String)request.getSession().getAttribute("id");
		UserDataBean userDto = userDao.getUser(id);
		int count = userDao.getUserListCount();
		Map<String, String> map = new HandlerHelper().makeCount(count, request);
		List<UserDataBean> members = userDao.getList(100, map);
		request.setAttribute("members", members);
		request.setAttribute("userDto", userDto);
		return new ModelAndView("adm/view/admUserOrderList");
	}
}