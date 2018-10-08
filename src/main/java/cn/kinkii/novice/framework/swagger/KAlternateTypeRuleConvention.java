package cn.kinkii.novice.framework.swagger;

import com.fasterxml.classmate.TypeResolver;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.domain.Pageable;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.AlternateTypeRuleConvention;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import static springfox.documentation.schema.AlternateTypeRules.newRule;

@Configuration
@ConditionalOnBean({TypeResolver.class})
public class KAlternateTypeRuleConvention implements AlternateTypeRuleConvention {

    @Autowired
    private TypeResolver resolver;

    @Override
    public List<AlternateTypeRule> rules() {
        return Arrays.asList(
            newRule(resolver.resolve(Pageable.class), resolver.resolve(Empty.class)),
            newRule(resolver.resolve(Principal.class), resolver.resolve(Empty.class)));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Data
    private static class Empty {
    }
}
