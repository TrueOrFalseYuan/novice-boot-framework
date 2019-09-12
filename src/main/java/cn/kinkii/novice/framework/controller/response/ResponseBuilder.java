package cn.kinkii.novice.framework.controller.response;

public class ResponseBuilder {

    public static <R extends AnnotatedResponse> R build(Class<R> clazz, Object... sources) {
        try {
            R result = clazz.newInstance();
            result.from(sources);
            return result;
        } catch (InstantiationException | IllegalAccessException ignored) {
        }
        return null;
    }

    public static <R extends GenericResponse<T>, T> R build(Class<R> clazz, T original) {
        try {
            R result = clazz.newInstance();
            result.from(original);
            return result;
        } catch (InstantiationException | IllegalAccessException ignored) {
        }
        return null;
    }
}
