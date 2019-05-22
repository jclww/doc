```
@Activate(group = Constants.PROVIDER, order = Ordered.HIGHEST_PRECEDENCE)
public class DubboLoggerFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger("dubboLoggerFilter");

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        LOGGER.info("dubbo request => interface = {}, methodName = {}, parameters = {}",
                invoker.getInterface().getName(),
                invocation.getMethodName(),
                JSON.toJSONString(invocation.getArguments()));
        Result result = null;
        try {
            result = invoker.invoke(invocation);
            return result;
        } finally {
            if (result != null) {
                if (result.hasException() && invoker.getInterface() != GenericService.class) {
                    LOGGER.warn("dubbo执行异常", result.getException());
                } else {
                    LOGGER.info("dubbo response => interface = {}, methodName = {}, resultValue = {}, attachment = {} ",
                            invoker.getInterface().getName(),
                            invocation.getMethodName(),
                            JSON.toJSONString(result.getValue()),
                            JSON.toJSONString(result.getAttachments()));
                }
            }
        }
    }
}
```