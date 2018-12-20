<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ include file="../../setting.jsp"%>

<html>
	<%@include file="../view/userHead.jsp"%>
	<body>
		<%@ include file="../../user/view/userTopNavbar.jsp" %>
		<%-- <%@ include file="../../user/view/userSideNavbar.jsp" %> --%>		
	
		<div class="container col-xl-10">
			<c:if test="${productCount eq null or productCount eq 0}">
				<p align="center">${msg_list_x}</p>
			</c:if>
			
			<c:if test="${productCount ne 0}">
				<c:forEach var="product" items="${productList}">
					<div class="card cardItem d-inline-flex mt-4 mb-4">
						<form name="${product.ref}">
							<a href="userProductDetail.jk?ref=${product.ref}"> 
								<img src="/urPresent/save/${product.thumbnail}" name="${product.ref}" class="card-img-top img-fluid">
								<span>${product.productName}</span>
								<input type="hidden" name="id" value="${sessionScope.id}">
								<input type="hidden" name="productCode" value="${product.ref}">
								<input type="hidden" name="var" value="1">
								
								<c:if test="${product.discount eq null or product.discount eq 0}">
									<fmt:formatNumber value="${product.productPrice}" type="currency" currencySymbol="￦"/>
								</c:if> 
								<c:if test="${product.discount ne 0}">
									<fmt:formatNumber value="${product.productPrice-(product.productPrice*product.discount/100)}" 
									type="currency" currencySymbol="￦"/>
								</c:if>
							</a>
							<c:if test="${sessionScope.id ne null}">
								<input type="button" id="cart" name="${product.ref}" value="장바구니">
							</c:if>	
						</form>
					</div>
				</c:forEach>
				
				<div align="center">
					<c:if test="${productCount gt 0}">
						<c:if test="${currentPage ne 1}">
							<a href="main.jk">[◀◀]</a>
							<c:if test="${startPage gt pageBlock}">
								<a href="main.jk?pageNum=${startPage-pageBlock}">[◀]</a>
							</c:if>
						</c:if>
						<c:forEach var="i" begin="${startPage}" end="${endPage}">
							<c:if test="${i eq currentPage}">
								<span>[${i}]</span>
							</c:if>
							<c:if test="${i ne currentPage}">
								<a href="main.jk?pageNum=${i}">[${i}]</a>
							</c:if>
						</c:forEach>
						<c:if test="${currentPage ne pageCount}">
							<c:if test="${pageCount>endPage}">
								<a href="main.jk?pageNum=${startPage+pageBlock}">[▶]</a>
							</c:if>
							<a href="main.jk?pageNum=${pageCount}">[▶▶]</a>
						</c:if>
					</c:if>
				</div>
			</c:if>
		</div>
	</body>

	<script type="text/javascript">
		//<!--
			$(document).ready(
				function(){
		  			var cartDiv = document.getElementById( 'cart' );
		  			$(document).on(
		  				'click',
		  				'input[id=cart]',
		  				
		  				function(){
		  					var name = this.name;
		  					$.ajax({
								type : 'POST',
								url : 'cartInsert.jk',
								data : $('form[name="' + name + '"]').serialize(),
								success: setTimeout( function(){
									alert('장바구니에 상품이 들어갔습니다.');
								}, 500 )
		  					});
		  				}
		  			);
				}
			);
		//-->
	</script>
</html>