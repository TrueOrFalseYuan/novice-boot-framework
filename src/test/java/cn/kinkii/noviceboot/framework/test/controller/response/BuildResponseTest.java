package cn.kinkii.noviceboot.framework.test.controller.response;

import cn.kinkii.noviceboot.framework.controller.response.AnnotatedResponse;
import cn.kinkii.noviceboot.framework.controller.response.annotations.ResponseClass;
import cn.kinkii.noviceboot.framework.controller.response.annotations.ResponseProperty;
import cn.kinkii.noviceboot.framework.test.entity.ComplexEntity;
import cn.kinkii.noviceboot.framework.test.entity.SimpleEntity;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import lombok.Getter;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildResponseTest {


  @Test
  public void testBuildResponse() {
    AnnotatedTestResponse resp = new AnnotatedTestResponse();

    SimpleEntity se = new SimpleEntity("SimpleEntity1", 1);

    Map<String, Long> map = new HashMap<>();
    map.put("ComplexEntityMap", 2L);
    ComplexEntity ce = new ComplexEntity("complex1", Lists.newArrayList(1L, 2L, 3L), map);

    resp.from(se, ce);

    Assert.assertTrue(resp.getTestString().equals("SimpleEntity1"));
    Assert.assertTrue(resp.getTestInteger() == 1);
    Assert.assertTrue(resp.getTestResponseList().get(0).equals("1 - handled"));
    Assert.assertTrue(resp.getTestResponseMap().get("ComplexEntityMap").equals("2 - handled"));

    SimpleEntity se2 = new SimpleEntity("SimpleEntity2", 2);

    AnnotatedErrorResponse errorResp = new AnnotatedErrorResponse();
    errorResp.from(se2);

    Assert.assertTrue(errorResp.getTestString().equals("SimpleEntity2"));
    Assert.assertTrue(errorResp.getTestInteger() == 2);

    Boolean isThrown = false;
    try {
      errorResp.from(ce);
    } catch (Exception e) {
      isThrown = true;
    }
    Assert.assertTrue(errorResp.getTestResponseList().get(0).equals("1 - handled"));
    Assert.assertTrue(isThrown);

  }


  @ResponseClass(sourceClasses = {SimpleEntity.class, ComplexEntity.class})
  @Getter
  private class AnnotatedTestResponse extends AnnotatedResponse {
    private String id;
    private String testString;
    private Integer testInteger;
    private Date testDate;
    private List<String> testResponseList;
    private Map<String, String> testResponseMap;

    @ResponseProperty(sourceClass = ComplexEntity.class, sourceProperty = "testList")
    private void setAnotherNameList(List<Long> list) {
      testResponseList = Lists.transform(list, new Function<Long, String>() {
        @Nullable
        @Override
        public String apply(@Nullable Long input) {
          return input != null ? input + " - handled" : null;
        }
      });
    }

    @ResponseProperty(sourceClass = ComplexEntity.class)
    private void setTestMap(Map<String, Long> map) {
      testResponseMap = new HashMap<>();
      for (String key : map.keySet()) {
        testResponseMap.put(key, map.get(key) + " - handled");
      }
    }

  }


  @ResponseClass(sourceClasses = {SimpleEntity.class, ComplexEntity.class})
  private class AnnotatedErrorResponse extends AnnotatedTestResponse {
    @ResponseProperty(sourceClass = ComplexEntity.class, sourceProperty = "testMap")
    private void setErrorParam(String errorParam) {

    }
  }
}
