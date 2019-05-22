## 参数校验
使用aop对方法参数进行参数校验
1. 定义方法切入点
```
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Check {

}
```
2. 定义需要校验的标志
```
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ParamCheck {
    String DEFAULT_CHECK_METHOD = "paramCheck";
    String DEFAULT_CHECK_MESSAGE = "缺少必要参数";

    /**
     * 校验方法
     * @return
     */
    String value() default DEFAULT_CHECK_METHOD;

    /**
     * check异常的提示信息
     * @return
     */
    String msg() default DEFAULT_CHECK_MESSAGE;

}
```
3. 定义切面
```
@Aspect
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "param.check", name = "enabled", havingValue = "true")
public class ParamValidAspect {

    @Pointcut("@annotation(Check)")
    public void paramValid() {
    }

    @Before("paramValid() && @annotation(check)")
    public void doValid(JoinPoint point, Check check) {
        MethodSignature signature = (MethodSignature) point.getSignature();

        Class[] paramTypes = signature.getParameterTypes();
        Object[] args = point.getArgs();
        Method method = signature.getMethod();
        Annotation[][] annotations = method.getParameterAnnotations();

        if (annotations == null || annotations.length == 0) {
            return;
        }
        for (int i = 0; i < paramTypes.length; i++) {
            if (annotations[i] != null && annotations[i].length != 0) {
                List<ParamCheck> checkAnnotations = Arrays.stream(annotations[i])
                        .filter(annotation -> annotation instanceof ParamCheck)
                        .map(annotation -> (ParamCheck) annotation)
                        .collect(Collectors.toList());

                for (ParamCheck paramCheck : checkAnnotations) {
                    try {
                        Preconditions.checkArgument((boolean) paramTypes[i].getDeclaredMethod(paramCheck.value()).invoke(args[i]), paramCheck.msg());
                    } catch (NoSuchMethodException e) {
                        log.info("Class:{} NoSuchMethod:{}", paramTypes[i].getName(), paramCheck.value());
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        log.info("Class:{} Exception:", e);
                    }
                }
            }
        }
    }
}
```

