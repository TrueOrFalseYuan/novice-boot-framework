package cn.kinkii.novice.framework.data;

import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import lombok.NoArgsConstructor;

import java.lang.annotation.Annotation;
import java.util.List;

@NoArgsConstructor
public class SensitiveDataIntrospector extends JacksonAnnotationIntrospector {

    private List<SensitiveStateChecker> stateCheckers;

    public SensitiveDataIntrospector(List<SensitiveStateChecker> stateCheckers) {
        this.stateCheckers = stateCheckers;
    }

    @Override
    public boolean isAnnotationBundle(Annotation ann) {
        if (ann.annotationType().equals(SensitiveData.class)) {
            if (this.stateCheckers == null || this.stateCheckers.size() == 0) {
                return true;
            } else {
                return !stateCheckers.stream().allMatch(SensitiveStateChecker::shouldDisable);
            }
        } else {
            return super.isAnnotationBundle(ann);
        }
    }

}