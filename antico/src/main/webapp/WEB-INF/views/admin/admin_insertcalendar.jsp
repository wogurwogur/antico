<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<% 
   String ctxPath = request.getContextPath(); 
%>

<jsp:include page=".././header/header.jsp" />

<div id="container">
    <jsp:include page=".././admin/admin_sidemenu.jsp" />
    
    <div style="margin-left: 3%;">
	    <h3>일정 등록</h3>
	
		<form name="scheduleFrm">
			<table id="schedule" class="table table-bordered">
				<tr>
					<th>일자</th>
					<td>
						<input type="date" id="calendar_startdate" value="${requestScope.choose_date}" style="height: 30px;"/>&nbsp; 
						<select id="startHour" class="schedule"></select> 시
						<select id="startMinute" class="schedule"></select> 분
						- <input type="date" id="calendar_enddate" value="${requestScope.choose_date}" style="height: 30px;"/>&nbsp;
						<select id="endHour" class="schedule"></select> 시
						<select id="endMinute" class="schedule"></select> 분&nbsp;
						<input type="checkbox" id="allDay"/>&nbsp;<label for="allDay">종일</label>
						
						<input type="hidden" name="calendar_startdate"/>
						<input type="hidden" name="calendar_enddate"/>
					</td>
				</tr>
				<tr>
					<th>제목</th>
					<td><input type="text" id="calendar_title" name="calendar_title" class="form-control"/></td>
				</tr>
				
				<tr>
					<th>장소</th>
					<td><input type="text" name="calendar_place" class="form-control"/></td>
				</tr>
	
				<tr>
					<th>내용</th>
					<td><textarea rows="10" cols="100" style="height: 200px;" name="calendar_content" id="calendar_content"  class="form-control"></textarea></td>
				</tr>
			</table>
		</form>
		
		<div style="float: right;">
			<button type="button" id="register" class="btn_normal" style="margin-right: 10px; ">등록</button>
			<button type="button" class="btn_normal" onclick="javascript:location.href='<%= ctxPath%>/admin/admin_page'">취소</button> 
		</div>
    </div>
</div>

<jsp:include page=".././footer/footer.jsp" />

<script type="text/javascript">
	$(document).ready(function(){

		// 시작시간, 종료시간		
		var html="";
		for(var i=0; i<24; i++){
			if(i<10){
				html+="<option value='0"+i+"'>0"+i+"</option>";
			}
			else{
				html+="<option value="+i+">"+i+"</option>";
			}
		}
		
		$("select#startHour").html(html);
		$("select#endHour").html(html);
		
		// 시작분, 종료분 
		html="";
		for(var i=0; i<60; i=i+5){
			if(i<10){
				html+="<option value='0"+i+"'>0"+i+"</option>";
			}
			else {
				html+="<option value="+i+">"+i+"</option>";
			}
		}
		html+="<option value="+59+">"+59+"</option>"
		
		$("select#startMinute").html(html);
		$("select#endMinute").html(html);
		
		// '종일' 체크박스 클릭시
		$("input#allDay").click(function() {
			var bool = $('input#allDay').prop("checked");
			
			if(bool == true) {
				$("select#startHour").val("00");
				$("select#startMinute").val("00");
				$("select#endHour").val("23");
				$("select#endMinute").val("59");
				$("select#startHour").prop("disabled",true);
				$("select#startMinute").prop("disabled",true);
				$("select#endHour").prop("disabled",true);
				$("select#endMinute").prop("disabled",true);
			} 
			else {
				$("select#startHour").prop("disabled",false);
				$("select#startMinute").prop("disabled",false);
				$("select#endHour").prop("disabled",false);
				$("select#endMinute").prop("disabled",false);
			}
		});
		
		// 등록 버튼 클릭
		$("button#register").click(function(){
		
			// 일자 유효성 검사 (시작일자가 종료일자 보다 크면 안된다!!)
			var startDate = $("input#calendar_startdate").val();	
	    	var sArr = startDate.split("-");
	    	startDate= "";	
	    	for(var i=0; i<sArr.length; i++){
	    		startDate += sArr[i];
	    	}
	    	
	    	var endDate = $("input#calendar_enddate").val();	
	    	var eArr = endDate.split("-");   
	     	var endDate= "";
	     	for(var i=0; i<eArr.length; i++){
	     		endDate += eArr[i];
	     	}
			
	     	var startHour= $("select#startHour").val();
	     	var endHour = $("select#endHour").val();
	     	var startMinute= $("select#startMinute").val();
	     	var endMinute= $("select#endMinute").val();
	        
	     	// 조회기간 시작일자가 종료일자 보다 크면 경고
	        if (Number(endDate) - Number(startDate) < 0) {
	         	alert("종료일이 시작일 보다 작습니다."); 
	         	return;
	        }
	        
	     	// 시작일과 종료일 같을 때 시간과 분에 대한 유효성 검사
	        else if(Number(endDate) == Number(startDate)) {
	        	
	        	if(Number(startHour) > Number(endHour)){
	        		alert("종료일이 시작일 보다 작습니다."); 
	        		return;
	        	}
	        	else if(Number(startHour) == Number(endHour)){
	        		if(Number(startMinute) > Number(endMinute)){
	        			alert("종료일이 시작일 보다 작습니다."); 
	        			return;
	        		}
	        		else if(Number(startMinute) == Number(endMinute)){
	        			alert("시작일과 종료일이 동일합니다."); 
	        			return;
	        		}
	        	}
	        }// end of else if---------------------------------
	    	
			// 제목 유효성 검사
			var calendar_title = $("input#calendar_title").val().trim();
	        if(calendar_title==""){
				alert("제목을 입력하세요."); 
				return;
			}

			// 달력 형태로 만들어야 한다.(시작일과 종료일)
			// 오라클에 들어갈 date 형식(년월일시분초)으로 만들기
			var sdate = startDate+$("select#startHour").val()+$("select#startMinute").val()+"00";
			var edate = endDate+$("select#endHour").val()+$("select#endMinute").val()+"00";
			
			$("input[name=calendar_startdate]").val(sdate);
			$("input[name=calendar_enddate]").val(edate);

			var frm = document.scheduleFrm;
			frm.action="<%= ctxPath%>/admin/admin_registercalendar";
			frm.method="post";
			frm.submit();

		});
	});
</script>

<style>
	#container {
		display: flex;
		width: 70%;
		margin: 0 auto;
	}
</style>    