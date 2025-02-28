package com.project.app.product.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.app.product.domain.CategoryVO;
import com.project.app.product.domain.ProductImageVO;
import com.project.app.product.domain.ProductVO;
import com.project.app.product.domain.CategoryDetailVO;
import com.project.app.product.service.ProductService;
import com.project.app.component.GetMemberDetail;

/*
 * 상품 컨트롤러
 */
@Controller
@RequestMapping("/product/*")
public class ProductController {

	@Autowired
	ProductService service;

	@Autowired
	private GetMemberDetail get_member_detail;
	
	// 카카오 api키
	@Value("${kakao.apikey}")
	private String kakao_api_key;

	// 상품등록 form 페이지 요청
	@GetMapping("add")
	public ModelAndView add(ModelAndView mav) {

		// 상위 카테고리 정보 가져오기
		List<CategoryVO> category_list = service.getCategory();

		// 하위 카테고리 정보 가져오기
		List<CategoryDetailVO> category_detail_list = service.getCategoryDetail();

		mav.addObject("category_list", category_list);
		mav.addObject("category_detail_list", category_detail_list);

		mav.setViewName("product/add");

		return mav;
	}

	// 상품 등록 완료 요청
	@PostMapping("add")
	public ModelAndView add(Map<String, String> paraMap, ModelAndView mav, ProductVO productvo, ProductImageVO product_imgvo) {
	      	
		  // 로그인한 회원의 회원번호 값 가져오기
		  String fk_member_no = get_member_detail.MemberDetail().getPk_member_no();
		  productvo.setFk_member_no(fk_member_no); // 회원 번호 값 담기
		  // System.out.println("확인용 fk_member_no : " + fk_member_no);
		  	  
		  // 상품번호 채번해오기
		  String c_product_no = service.getNo();
	      productvo.setPk_product_no(c_product_no); // 채번 해온 값 담기
		  // System.out.println(productvo.getFk_member_no());
		
	      // 이미지 정보 가져오기 
	      List<MultipartFile> attach_list = product_imgvo.getAttach();
	      
	      // 상품 등록 완료 후 상품 테이블 및 이미지 테이블에 상품 정보 저장
	      int n = service.addProduct(productvo, product_imgvo, attach_list);

	      if (n > 0) { // 저장이 성공한 경우 성공 페이지로 이동한다.
	    	  
	    	 String pk_product_no = productvo.getPk_product_no();
	    	 mav.addObject("pk_product_no", pk_product_no); // 상품 번호 전달
	         mav.setViewName("product/add_success"); // 상품 등록 완료 페이지

	      } // end of if(n > 0)
	      
	      return mav;
	      	      
	}
			
	// 지역 추가 버튼 클릭 시 사이드바 지역 검색창 요청
	@GetMapping("regionlist")
	public ModelAndView region(ModelAndView mav) {

		mav.setViewName("product/regionlist");
		return mav;

	}

	// 지역 검색창에서 지역 검색 시 자동글 완성하기 및 정보 가져오기
	@GetMapping("region_search")
	@ResponseBody
	public List<Map<String, String>> regionSearch(@RequestParam Map<String, String> paraMap) {

		List<Map<String, Object>> region_list = service.regionSearch(paraMap);

		List<Map<String, String>> map_list = new ArrayList<>();

		if (region_list != null) {
			for (Map<String, Object> region : region_list) {
				Map<String, String> map = new HashMap<>();
				map.put("word", region.get("full_address").toString()); // full_address 값을 word로 저장
				map.put("region_town", region.get("region_town").toString()); // 읍면동
				map.put("fk_region_no", region.get("pk_region_no").toString()); // 지역번호
				map.put("region_lat", region.get("region_lat").toString()); // 위도 값
				map.put("region_lng", region.get("region_lng").toString()); // 경도 값
				map_list.add(map);
			}
		}
		return map_list;
	}


	
	// 상품 목록 조회해오기 (검색어, 카테고리번호, 가격대, 정렬 포함)
	@GetMapping("prodlist")
	public ModelAndView prodlist(ModelAndView mav, 
								@RequestParam(defaultValue = "") String search_prod,
								@RequestParam(defaultValue = "") String category_no,
								@RequestParam(defaultValue = "") String category_detail_no,
								@RequestParam(defaultValue = "") String min_price,
								@RequestParam(defaultValue = "") String max_price,
								@RequestParam(defaultValue = "") String region,
								@RequestParam(defaultValue = "") String town,
								@RequestParam(defaultValue = "") String sort_type) {

		// View 페이지 출력을 위한 정보 가져오기 시작 //
		// 로그인한 회원의 회원번호 값 가져오기
		String fk_member_no = get_member_detail.MemberDetail().getPk_member_no();
		mav.addObject("fk_member_no", fk_member_no);
	
		
		// 상위 카테고리 정보 가져오기
		List<CategoryVO> category_list = service.getCategory();	
			
		// 하위 카테고리 정보 가져오기
		List<CategoryDetailVO> category_detail_list = service.getCategoryDetail();
		
		// 좋아요 정보 가져오기
		List<Map<String, String>> wish_list = service.getWish();
		
		// 지역 정보 가져오기
		List<Map<String, String>> region_list = service.getRegion();
		
        mav.addObject("category_list", category_list); 			 	 // 상위 카테고리 정보 전달
        mav.addObject("category_detail_list", category_detail_list); // 하위 카테고리 정보 전달
        mav.addObject("wish_list", wish_list); 						 // 좋아요 정보 전달
		mav.addObject("region_list", region_list);					 // 지역 정보 전달
		// View 페이지 출력을 위한 정보 가져오기 끝 //

		       
		search_prod = search_prod.trim(); // 검색어 공백 없애주기
		
		// 상품 개수 가져오기 (검색어, 카테고리번호, 가격대, 지역, 정렬 포함)
        int product_list_cnt = service.getProductCnt(search_prod, category_no, category_detail_no, min_price, max_price, region, town, sort_type);
        
        // 상품 가격 정보 가져오기 (검색어, 카테고리번호, 가격대, 지역, 정렬 포함)
        Map<String, String> prodcut_price_info = service.getProductPrice(search_prod, category_no, category_detail_no, min_price, max_price, region, town, sort_type);
        
        mav.addObject("product_list_cnt", product_list_cnt); 	     // 총 개수 전달
        mav.addObject("prodcut_price_info", prodcut_price_info);     // 가격 정보 전달 
        mav.addObject("search_prod", search_prod); 	 		 	     // 검색어 전달
        
        if(product_list_cnt > 0) { // 상품이 존재한다면
        	
        	// 모든 상품에 대한 이미지,지역 정보 가져오기 (검색어, 카테고리번호, 가격대, 지역, 정렬 포함)
            List<Map<String, String>> product_list = service.getProduct(search_prod, category_no, category_detail_no, min_price, max_price, region, town, sort_type); 
            
            mav.addObject("product_list", product_list); // 상품 정보 전달	   
        }
               
        mav.setViewName("product/prodlist");
        
		return mav;
	}

	
	// 상품 목록에서 지역 선택하는 
	@GetMapping("regionlist_lat_lng")
	public ModelAndView regionLatLng(ModelAndView mav) {

		mav.setViewName("product/regionlist_lat_lng");
		return mav;

	}
	
	
	// 상품 목록 지역 선택창에서 현재 위치 클릭하여 근처 동네 5개 알아오기 
	@GetMapping("near_region")
	@ResponseBody
	public List<Map<String, Object>> nearRegion(@RequestParam(defaultValue = "") String current_lat,
												@RequestParam(defaultValue = "") String current_lng) {
		List<Map<String, Object>> near_region_list = service.nearRegion(current_lat, current_lng);
		return near_region_list;
	}
	
	
	// 관심상품에 상품 추가하기 
	@PostMapping("wish_insert")
	@ResponseBody
	public int wishInsert(@RequestParam(defaultValue = "") String fk_product_no,
						   @RequestParam(defaultValue = "") String fk_member_no) {
		
		int result = service.wishInsert(fk_product_no, fk_member_no);
		return result;
	}
	

	// 상품 상세 페이지 조회
	@GetMapping("prod_detail/{pk_product_no}")
	public ModelAndView prodDeatil(ModelAndView mav,
								   @PathVariable String pk_product_no) {
		
		
	    // pk_product_no가 favicon.ico일 경우 처리하지 않도록 조건 추가
	    if ("favicon.ico".equals(pk_product_no)) {
	    	// System.out.println("favicon.ico 요청을 필터링했습니다.");
	        return mav; // 빈 mav 반환
	    }
		
	    //System.out.println("1111 pk_product_no " + pk_product_no);
	    //System.out.println("2222 pk_product_no " + pk_product_no);
	
		// 로그인한 회원의 회원번호 값 가져오기
		String fk_member_no = get_member_detail.MemberDetail().getPk_member_no();
		mav.addObject("fk_member_no", fk_member_no);
		
		// 좋아요 정보 가져오기
		List<Map<String, String>> wish_list = service.getWish();
		mav.addObject("wish_list", wish_list);
		
		// 특정 상품에 대한 이미지 정보 가져오기
		List<ProductImageVO> product_img_list = service.getProductImg(pk_product_no);
		mav.addObject("product_img_list", product_img_list);
		
		// 특정 삼품에 대한 정보 가져오기(지역, 회원, 카테고리)
		Map<String, String> product_map = service.getProductDetail(pk_product_no);
		mav.addObject("product_map", product_map);
		
		// 카카오 api key 전달
		mav.addObject("kakao_api_key", kakao_api_key);
	
		mav.setViewName("product/prod_detail");
		
		return mav;
	}
	
	
	
	// "위로올리기" 클릭 시 상품 등록일자 업데이트 하기
	@PostMapping("reg_update")
	@ResponseBody
	public int regDateUpdate(@RequestParam(defaultValue = "") String pk_product_no) {
		int result = service.regDateUpdate(pk_product_no);
		return result;
	}
	
	
	// "상태변경" 클릭 시 상품 상태 업데이트 하기
	@PostMapping("sale_status_update")
	@ResponseBody
	public int saleStatusUpdate(@RequestParam(defaultValue = "") String pk_product_no,
								@RequestParam(defaultValue = "") String sale_status_no) {
		int result = service.saleStatusUpdate(pk_product_no, sale_status_no);
		return result;
	}
	
	
	// "상품삭제" 클릭 시 상품 삭제하기
	@PostMapping("delete")
	@ResponseBody
	public int delete(@RequestParam(defaultValue = "") String pk_product_no) {
		int result = service.delete(pk_product_no);
		return result;
	}
	
	
	

} // end of public class ProductController
