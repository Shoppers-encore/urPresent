package db;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import bean.SqlMapClient;
import databean.ReviewDataBean;
public class BoardDao {
	private SqlSession session = SqlMapClient.getSession();
	
	public int getReviewCount() {
		return session.selectOne("User.getReviewCount");
	}
	public List<ReviewDataBean> getReviewList(Map<String, String> needData){
		return session.selectList("User.getReviewList", needData);
	}
	
	public int getMaxReview() {
		return session.selectOne("User.getMaxReview");
	}
	public int insert(ReviewDataBean dto) {
		return session.insert("User.insertReview", dto);
	}
	public ReviewDataBean get(int num) {
		return session.selectOne("User.selectReview", num);
	}
	
	public int getReviewLikes(int num) {
		return session.selectOne("User.getReviewLikes", num);
	}
	public int getReviewLike(Map<String,String> map) {
		return session.selectOne("User.getReviewLike", map);
	}
	public int addCount(int num) {
		return session.update("User.addCount",num);
	}
	public int insertReviewLike(Map<String,String>map) {
		return session.insert("User.insertReviewLike",map);
	}
	public int deleteReviewLike(Map<String,String>map) {
		return session.delete("User.deleteReviewLike",map);
	}
	
	public int delete(int num) {
		return session.delete("User.deleteReview", num);
	}
}
