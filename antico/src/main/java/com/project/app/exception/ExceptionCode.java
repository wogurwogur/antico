package com.project.app.exception;

import lombok.Getter;

/*
 * 구체적인 예외 정보들을 정의하기 위한 ENUM 클래스
 */
@Getter
public enum ExceptionCode {
	
	///////////////////////////////////////// 후기 관련 예외 /////////////////////////////////////////////
	
	TRADE_NOT_FOUND(404, "거래내역을 찾을 수 없습니다."), // 조회한 TRADE VO가 NULL인 경우
	
	REVIEW_NOT_FOUND(404, "후기내역을 찾을 수 없습니다."), // 조회한 REVIEW VO가 NULL인 경우
	
	REVIEW_AREADY_EXISTS(400, "이미 후기를 작성하셨습니다."), // 동일한 거래에서 중복되는 후기를 작성하려는 경우
	
	BLACKLIST_AREADY_EXISTS(400, "이미 차단한 회원입니다."), // 이미 차단한 회원을 차단하려는 경우
	
	NOT_CONSUMER_MEMBER(400, "구매자가 아닙니다."),
	
	
    ////////////////////////////////// file upload 관련 예외 /////////////////////////////////////////////

	
	FILE_IS_EMPTY(400, "파일을 다시 업로드 해주세요"), // 클라이언트에서 업로드한 파일이 비어있는 경우
	
	INVALID_FILE_EXTENSION(400, "파일 확장자가 존재하지 않습니다."), // 파일 확장자가 없는 경우
	
	IO_EXCEPTION_FILE_UPLOAD(500, "파일 업로드 중 I/O 작업 오류"), // 파일 I/O 오류일 경우
	
	PUT_OBJECT_EXCEPTION(500, "AWS S3 업로드 오류"), // 파일 업로드 오류
	
	IO_EXCEPTION_FILE_DELETE(500, "파일 삭제 요청 중 I/O 작업 오류"), // 파일 I/O 오류일 경우
	
	SERVER_ERROR(500, "오류가 발생했습니다. 나중에 다시 시도하여 주십시오"),
	
	FILE_SIZE_EXCEED(400, "파일 당 최대 이미지는 5M를 넘을 수 없습니다."), // 파일 당 크기가 초과인 경우

	
    ////////////////////////////////// 상품 구매 결제 관련 예외 /////////////////////////////////////////////
	
	
	PAYMENT_LOAD_FAILD(400, "결제창을 불러올 수 없습니다."), // 결제창 불러오기 예외

	MYPRODUCT_PAY_FAILD(400, "본인의 상품은 구매할 수 없습니다."), // 결제창 불러오기 예외
	
	RPODUCT_NOT_ON_SALE(400, "판매중인 상품이 아닙니다."), // 결제창 불러오기 예외
	
	NOT_PAYMENT_CONSUMER(400, "구매를 하지 않은 상품은 구매확정이 불가합니다."),
	
    ////////////////////////////////// 채팅 관련 예외 /////////////////////////////////////////////
    
	
	SELLER_CREATE_CHAT(400, "일반 판매자가 채팅방을 생성할 수 없습니다."), // 판매자가 자기 자신과 채팅방을 생성하려는 경우
	
	CREATE_CHATROOM_FAILD(500, "채팅방을 생성하지 못하였습니다. 다시 시도해 주십시오."), // 채팅방 생성이 의도치 않게 실패할 경우
    
	JOIN_CHATROOM_FAILD(404, "채팅방 입장을 실패하였습니다. "); // 채팅방 조회를 실패하여 입장을 못하는 경우
	
	private final int status;
	
	private final String message;
	
	ExceptionCode(int status, String message) {
		this.status = status;
		this.message = message;
	}

}
