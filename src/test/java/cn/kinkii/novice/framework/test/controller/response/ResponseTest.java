package cn.kinkii.novice.framework.test.controller.response;

import cn.kinkii.novice.framework.controller.response.AnnotatedResponse;
import cn.kinkii.novice.framework.controller.response.GenericResponse;
import cn.kinkii.novice.framework.controller.response.annotations.ResponseProperty;
import cn.kinkii.novice.framework.test.entity.ComplexEntity;
import cn.kinkii.novice.framework.test.entity.SimpleEntity;
import com.google.common.collect.Lists;
import lombok.Getter;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseTest {

  @Test
  public void testBuildAnnotatedResponse() {
    AnnotatedTestResponse resp = new AnnotatedTestResponse();

    SimpleEntity se = new SimpleEntity("SimpleEntity1", 1);

    Map<String, Long> map = new HashMap<>();
    map.put("ComplexEntityMap", 2L);
    ComplexEntity ce = new ComplexEntity("complex1", Lists.newArrayList(1L, 2L, 3L), map);

    resp.from(se, ce);

    Assert.assertTrue(resp.getTestString().equals("SimpleEntity1"));
    Assert.assertTrue(resp.getTestInteger() == 1);
    Assert.assertTrue(resp.getTestList().get(0) == 1L);
    Assert.assertTrue(resp.getAnotherTestMap().get("ComplexEntityMap") == 2L);

  }

  @Test
  public void testBuildGenericResponse() {
    GenericTestResponse resp = new GenericTestResponse();

    resp.from(new SimpleEntity("SimpleEntity1", 1));

    Assert.assertTrue(resp.getTestString().equals("SimpleEntity1"));
    Assert.assertTrue(resp.getTestIntegerString().equals("SimpleEntity1"));
    Assert.assertTrue(resp.getTestInteger() == 1);
    Assert.assertTrue(resp.getTestIntegerCopy() == 1);

  }

  @Test
  public void test() {
    System.out.println("".matches("\\S+"));
  }

  @Getter
  private class GenericTestResponse extends GenericResponse<SimpleEntity> {
    private String id;
    private String testString;
    private Integer testInteger;
    @ResponseProperty(sourceProperty = "testString") private String testIntegerString;
    @ResponseProperty(sourceClass = SimpleEntity.class, sourceProperty = "testInteger") private Integer testIntegerCopy;
  }


  @Getter
  private class AnnotatedTestResponse extends AnnotatedResponse {
    private String id;
    private String testString;
    private Integer testInteger;
    private Date testDate;
    private List<Long> testList;
    private Map<String, Long> testMap;

    @ResponseProperty(sourceClass = ComplexEntity.class, sourceProperty = "testList") private List<Long> anotherNameList;
    @ResponseProperty(sourceProperty = "testMap") private Map<String, Long> anotherTestMap;

  }

}
