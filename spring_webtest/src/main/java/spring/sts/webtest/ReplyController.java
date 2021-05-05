package spring.sts.webtest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import spring.model.reply.ReplyDTO;
import spring.model.reply.ReplyMapper;
import spring.utility.webtest.Utility;

@RestController
public class ReplyController {

	private static final Logger log = LoggerFactory.getLogger(ReplyController.class);
	
	@Autowired
	private ReplyMapper mapper;
	
	@DeleteMapping("/bbs/reply/{rnum}")
	public ResponseEntity<String> remove(@PathVariable("rnum") int rnum){
		log.info("remove:"+rnum);
		
		return mapper.delete(rnum)==1?new ResponseEntity<String>("success", HttpStatus.OK)
				:new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@PutMapping("/bbs/reply/{rnum}")
	public ResponseEntity<String> modify(@RequestBody ReplyDTO dto,
			@PathVariable("rnum") int rnum){
		log.info("rnum:"+rnum);
		log.info("modify:"+dto);
		return mapper.update(dto)==1?new ResponseEntity<String>("success",HttpStatus.OK)	
				:new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		
	}
	
	@GetMapping("/bbs/reply/{rnum}")
	public ResponseEntity<ReplyDTO> get(@PathVariable("rnum") int rnum){
		log.info("get:"+rnum);
		
		return new ResponseEntity<ReplyDTO>(mapper.read(rnum),HttpStatus.OK);
		
	}
	
	@PostMapping("/bbs/reply/create")
	public ResponseEntity<String> create(@RequestBody ReplyDTO dto){
		log.info("ReplyDTO:"+dto.getContent());
		log.info("ReplyDTO:"+dto.getId());
		log.info("ReplyDTO:"+dto.getBbsno());
		
		dto.setContent(dto.getContent().replaceAll("/n/r", "<br>"));
		
		int flag = mapper.create(dto);
		log.info("flag:"+flag);
		
		return flag==1?new ResponseEntity<String>("success",HttpStatus.OK)
				:new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@GetMapping("/bbs/reply/page")
	public ResponseEntity<String> getPage(@RequestParam("nPage") int nPage,
			@RequestParam("nowPage") int nowPage,
			@RequestParam("bbsno") int bbsno,
			@RequestParam("col") String col,
			@RequestParam("word") String word){
		
		int total = mapper.total(bbsno);
		String url = "read";
		int recordPerPage = 3;
		
		String pageing = Utility.rpaging(total,nowPage,recordPerPage,col,word,url,nPage,bbsno);
		
		return new ResponseEntity<String>(pageing, HttpStatus.OK);
		
	}
	
	@GetMapping("/bbs/reply/list/{bbsno}/{sno}/{eno}")
	public ResponseEntity<List<ReplyDTO>> getList(@PathVariable("bbsno") int bbsno,
			@PathVariable("sno") int sno,
			@PathVariable("eno") int eno){
		
		Map map = new HashMap();
		map.put("sno", sno);
		map.put("eno", eno);
		map.put("bbsno", bbsno);		
		
		return new ResponseEntity<List<ReplyDTO>>(mapper.list(map),HttpStatus.OK);
		
	}	
}
