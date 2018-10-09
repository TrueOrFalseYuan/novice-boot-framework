package cn.kinkii.novice.framework.test.controller.request;

import cn.kinkii.novice.framework.controller.request.AnnotatedRequest;
import cn.kinkii.novice.framework.controller.request.GenericRequest;
import cn.kinkii.novice.framework.controller.request.annotations.RequestProperty;
import cn.kinkii.novice.framework.test.entity.ComplexEntity;
import cn.kinkii.novice.framework.test.entity.SimpleEntity;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestTest {

  @Test
  public void testAnnotatedRequest() {

    Map<String, Long> map = new HashMap<>();
    map.put("ComplexEntityMap", 2L);

    AnnotatedTestRequest req = new AnnotatedTestRequest("TestRequest-1", 2, 20, "to SimpleEntity1", Arrays.asList(10L, 20L, 30L), map, 99);

    SimpleEntity se1 = req.to(SimpleEntity.class);
    Assert.assertTrue(se1.getId().equals("TestRequest-1"));
    Assert.assertTrue(se1.getTestString().equals("to SimpleEntity1"));
    Assert.assertTrue(se1.getTestInteger() == 2);
    Assert.assertTrue(se1.getSum() == 101);

    ComplexEntity ce1 = req.to(ComplexEntity.class);
    Assert.assertTrue(ce1.getId().equals("TestRequest-1"));
    Assert.assertTrue(ce1.getTestList().size() == 3);
    Assert.assertTrue(ce1.getTestList().get(2) == 30L);
    Assert.assertTrue(ce1.getTestMap().get("ComplexEntityMap") == 2L);

  }

  @Test
  public void testGenericRequest() {
    SimpleEntity se1 = new GenericTestRequest("GenericTestRequest-1", 1, 10, "to SimpleEntity1").newTarget();

    Assert.assertTrue(se1.getId().equals("GenericTestRequest-1"));
    Assert.assertTrue(se1.getTestString().equals("to SimpleEntity1"));
    Assert.assertTrue(se1.getTestInteger() == 1);
    Assert.assertTrue(se1.getTestIntegerMethod() == 11);


    new GenericTestRequest("GenericTestRequest-2", "to SimpleEntity2").toTarget(se1);

    Assert.assertTrue(se1.getId().equals("GenericTestRequest-2"));
    Assert.assertTrue(se1.getTestString().equals("to SimpleEntity2"));

  }

  @AllArgsConstructor
  private class AnnotatedTestRequest extends AnnotatedRequest {
    private String id;
    private Integer testInteger;
    @RequestProperty(targetProperty = "testIntegerMethod") private Integer testAnotherInteger;
    @RequestProperty(targetProperty = "testString") private String testIntegerString;
    private List<Long> testList;
    @RequestProperty(targetProperty = "testMap") private Map<String, Long> anotherTestMap;

    private Integer summand;

    @RequestProperty(targetProperty = "sum")
    public Integer sum() {
      return testInteger + summand;
    }
  }

  @AllArgsConstructor
  @RequiredArgsConstructor
  private class GenericTestRequest extends GenericRequest<SimpleEntity> {

    @NonNull private String id;
    private Integer testInteger;
    @RequestProperty(targetProperty = "testIntegerMethod") private Integer testAnotherInteger;

    @NonNull
    @RequestProperty(targetProperty = "testString")
    private String testIntegerString;
  }

}
