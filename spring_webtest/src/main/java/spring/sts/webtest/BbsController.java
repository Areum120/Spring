package spring.sts.webtest;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import spring.model.bbs.BbsDTO;
import spring.model.bbs.BbsMapper;
import spring.model.bbs.BbsService;
import spring.model.bbs.BbsVO;
import spring.model.reply.ReplyMapper;
import spring.utility.webtest.Utility;

@Controller
public class BbsController {
	@Autowired
	private BbsMapper mapper;
	@Autowired
	private ReplyMapper rmapper;
	@Autowired
	private BbsService service;
	
	@PostMapping("/bbs/createJPA")
	public String create(BbsVO dto, HttpServletRequest request) {
		String basePath = request.getRealPath("/storage");
		if(dto.getFilenameMF() != null) {
			dto.setFilename(Utility.saveFileSpring(dto.getFilenameMF(), basePath));
			dto.setFilesize((int) dto.getFilenameMF().getSize());
		}		
		try {
			service.insert(dto);
			return "redirect:./list";
		}catch(Exception e) {
			return "./error";
		}
	}

	@GetMapping("/bbs/fileDown")
	public void fileDown(HttpServletRequest request, HttpServletResponse response) throws IOException  {

		ServletContext ctx = request.getSession().getServletContext();
		// 절대경로
		String dir = ctx.getRealPath(request.getParameter("dir"));
		String filename = request.getParameter("filename");
		byte[] files = FileUtils.readFileToByteArray(new File(dir, filename));
		response.setHeader("Content-disposition",
				"attachment; fileName=\"" + URLEncoder.encode(filename, "UTF-8") + "\";");
		// Content-Transfer-Encoding : 전송 데이타의 body를 인코딩한 방법을 표시함.
		response.setHeader("Content-Transfer-Encoding", "binary");
		/**
		 * Content-Disposition가 attachment와 함게 설정되었다면 'Save As'로 파일을 제안하는지 여부에 따라 브라우저가
		 * 실행한다.
		 */
		response.setContentType("application/octet-stream");
		response.setContentLength(files.length);
		response.getOutputStream().write(files);
		response.getOutputStream().flush();
		response.getOutputStream().close();

	}

	@PostMapping(value = "/bbs/delete_Ajax", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Map<String, String> delete_Ajax(@RequestBody BbsDTO dto, HttpServletRequest request) {
		boolean cflag = false;
		int cnt = mapper.checkRefnum(dto.getBbsno());
		if (cnt > 0)
			cflag = true;
		String upDir = request.getRealPath("/storage");
		Map map = new HashMap();
		map.put("bbsno", dto.getBbsno());
		map.put("passwd", dto.getPasswd());

		boolean pflag = false;
		boolean flag = false;

		if (!cflag) {
			int cnt2 = mapper.passCheck(map);
			if (cnt2 > 0)
				pflag = true;
		}
		if (pflag) {
			if (dto.getFilename() != null)
				Utility.deleteFile(upDir, dto.getFilename());
			int cnt3 = mapper.delete(dto.getBbsno());
			if (cnt3 > 0)
				flag = true;
		}

		Map<String, String> map2 = new HashMap<String, String>();

		if (cflag) {
			map2.put("str", "답변있는 글이므로 삭제할 수 없습니다");
			map2.put("color", "blue");
		} else if (!pflag) {
			map2.put("str", "패스워드가 잘못입력되었습니다");
			map2.put("color", "blue");
		} else if (flag) {
			map2.put("str", "삭제 처리되었습니다");
			map2.put("color", "blue");
		} else {
			map2.put("str", "삭제중 에러가 발생했습니다");
			map2.put("color", "blue");
		}

		return map2;
	}

	@GetMapping("/bbs/delete_Ajax")
	public String delete_Ajax() {

		return "/bbs/delete_Ajax";
	}

	@PostMapping("/bbs/delete")
	public String delete(int bbsno, String passwd, String oldfile, HttpServletRequest request) {

		String upDir = request.getRealPath("/storage");
		Map map = new HashMap();
		map.put("bbsno", bbsno);
		map.put("passwd", passwd);
		int cnt = mapper.passCheck(map);
		
		String url = "/bbs/passwdError";
		
		if(cnt > 0) {
			
			try {
				service.delete(bbsno);
				url = "redirect:/bbs/list";
				if (oldfile != null)
					Utility.deleteFile(upDir, oldfile);
			}catch(Exception e) {
				e.printStackTrace();
				url = "/bbs/error";
			}
			
		}
		
	    return url;
	}

	@GetMapping("/bbs/delete")
	public String delete(int bbsno, Model model) {
		boolean flag = false;
		int cnt = mapper.checkRefnum(bbsno);
		if (cnt > 0)
			flag = true;

		model.addAttribute("flag", flag);

		return "/bbs/delete";

	}

	@PostMapping("/bbs/reply")
	public String reply(BbsDTO dto, HttpServletRequest request) {
		String basePath = request.getRealPath("/storage");
		if (dto.getFilenameMF() != null) {
			dto.setFilename(Utility.saveFileSpring(dto.getFilenameMF(), basePath));
			dto.setFilesize((int) dto.getFilenameMF().getSize());
		}

		Map map = new HashMap();
		map.put("grpno", dto.getGrpno());
		map.put("ansnum", dto.getAnsnum());
		mapper.upAnsnum(map);
		boolean flag = false;
		int cnt = mapper.createReply(dto);
		if (cnt > 0)
			flag = true;

		if (flag) {
			return "redirect:/bbs/list"; // 재요청
		} else {
			return "/bbs/error";
		}

	}

	@GetMapping("/bbs/reply")
	public String reply(int bbsno, Model model) {

		model.addAttribute("dto", mapper.readReply(bbsno));
		return "/bbs/reply";
	}

	@PostMapping("/bbs/update")
	public String update(BbsDTO dto, String oldfile, HttpServletRequest request) {
		String basePath = request.getRealPath("/storage");
		if (dto.getFilenameMF() != null) {
			if (oldfile != null)
				Utility.deleteFile(basePath, oldfile);
			dto.setFilename(Utility.saveFileSpring(dto.getFilenameMF(), basePath));
			dto.setFilesize((int) dto.getFilenameMF().getSize());
		}

		Map map = new HashMap();
		map.put("bbsno", dto.getBbsno());
		map.put("passwd", dto.getPasswd());
		boolean pflag = false;
		int cnt = mapper.passCheck(map);
		if (cnt > 0)
			pflag = true;
		boolean flag = false;

		if (pflag) {
			int cnt2 = mapper.update(dto);
			if (cnt2 > 0)
				flag = true;
		}

		if (!pflag) {
			return "/bbs/passwdError";
		} else if (flag) {
			return "redirect:/bbs/list";
		} else {
			return "/bbs/error";
		}

	}

	@GetMapping("/bbs/update")
	public String update(int bbsno, Model model) {

		model.addAttribute("dto", mapper.read(bbsno));

		return "/bbs/update";
	}

	@GetMapping("/bbs/read")
	public String read(int bbsno, Model model, HttpServletRequest request) {

		mapper.upViewcnt(bbsno);

		BbsDTO dto = mapper.read(bbsno);

		String content = dto.getContent().replaceAll("\r\n", "<br>");

		dto.setContent(content);

		model.addAttribute("dto", dto);
		
		/*댓글 관련 시작*/
		int nPage = 1;
		if(request.getParameter("nPage")!=null) {
			nPage = Integer.parseInt(request.getParameter("nPage"));
		}
		int recordPerPage = 3;
		
		int sno = ((nPage - 1) * recordPerPage) + 1;
		int eno = nPage * recordPerPage;
		
		Map map = new HashMap();
		map.put("sno", sno);
		map.put("eno", eno);
		map.put("nPage", nPage);
		
		model.addAllAttributes(map);
		
		/* 댓글 처리 끝 */	

		return "/bbs/read";
	}

	@RequestMapping("/bbs/list")
	public String list(HttpServletRequest request) {
		// 검색관련------------------------
		String col = Utility.checkNull(request.getParameter("col"));
		String word = Utility.checkNull(request.getParameter("word"));

		if (col.equals("total")) {
			word = "";
		}

		// 페이지관련-----------------------
		int nowPage = 1;// 현재 보고있는 페이지
		if (request.getParameter("nowPage") != null) {
			nowPage = Integer.parseInt(request.getParameter("nowPage"));
		}
		int recordPerPage = 3;// 한페이지당 보여줄 레코드갯수

		// DB에서 가져올 순번-----------------
		int sno = ((nowPage - 1) * recordPerPage) + 1;
		int eno = nowPage * recordPerPage;

		Map map = new HashMap();
		map.put("col", col);
		map.put("word", word);
		map.put("sno", sno);
		map.put("eno", eno);

		int total = mapper.total(map);

		// List<BbsDTO> list = dao.list(map);
		List<BbsDTO> list = mapper.list(map);

		String paging = Utility.paging(total, nowPage, recordPerPage, col, word);

		// request에 Model사용 결과 담는다
		request.setAttribute("list", list);
		request.setAttribute("nowPage", nowPage);
		request.setAttribute("col", col);
		request.setAttribute("word", word);
		request.setAttribute("paging", paging);
		request.setAttribute("rmapper", rmapper);

		return "/bbs/list";
	}

	@GetMapping("/bbs/create")
	public String create() {
		return "/bbs/create";
	}

	@PostMapping("/bbs/create")
	public String create(BbsDTO dto, HttpServletRequest request) {
		String basePath = request.getRealPath("/storage");
		if (dto.getFilenameMF() != null) {
			dto.setFilename(Utility.saveFileSpring(dto.getFilenameMF(), basePath));
			dto.setFilesize((int) dto.getFilenameMF().getSize());
		}

		// boolean flag = dao.create(dto);
		boolean flag = false;

		int cnt = mapper.create(dto);
		if (cnt > 0)
			flag = true;

		if (flag) {
			return "redirect:/bbs/list"; // 재요청
		} else {
			return "/bbs/error";
		}

	}

}
