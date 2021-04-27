package spring.model.reply;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations={"file:src/main/webapp/WEB-INF/spring/root-context.xml",
"file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml"})
public class ReplyMapperTest {
	
	private static final Logger logger = LoggerFactory.getLogger(ReplyMapperTest.class);
	
	@Autowired
	private ReplyMapper mapper;
	
	@Test
	@Ignore
	public void testMapper() {
		logger.info("mapper:"+mapper.getClass().getName());
	}

	@Test
	@Ignore
	public void testCreate() {
		ReplyDTO dto = new ReplyDTO();
		dto.setBbsno(2);
		dto.setContent("댓글입니다.");
		dto.setId("user1");
		
		assertTrue(mapper.create(dto)>0);
	}

	@Test
	public void testList() {
		Map map = new HashMap();
		map.put("bbsno", 2);
		map.put("sno", 1);
		map.put("eno", 3);
		List<ReplyDTO> list = mapper.list(map);
		logger.info("list size:"+list.size());
		for(int i=0;i<list.size();i++) {
			ReplyDTO dto = list.get(i);
			logger.info("dto:"+dto);
		}
	}

	@Test
	public void testTotal() {
		int bbsno = 2;
		int cnt = mapper.total(bbsno);
		logger.info("total:"+cnt);
	}

	@Test
	public void testRead() {
		ReplyDTO dto = mapper.read(1);
		logger.info("dto:"+dto);
	}

	@Test
	@Ignore
	public void testUpdate() {
		ReplyDTO dto = new ReplyDTO();
		dto.setContent("댓글2로 변경");
		dto.setRnum(1);
		assertTrue(mapper.update(dto)>0);
	}
	@Test
	public void testDelete() {
		int rnum = 1;
		assertTrue(mapper.delete(rnum)>0);
	}
}
