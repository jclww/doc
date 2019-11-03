# 简介
当我们需要检查`RequestBody`中的参数时候，需要获取到参数数据。但是`ServletRequest`中获取`RequestBody`中数据只有通过数据流的形式`request.getInputStream()` 主要的是不支持`mark`非缓冲流

```
// 校验数据流是否支持 mark
request.getInputStream().markSupported();
```

# HttpServletRequestWrapper

`HttpServletRequestWrapper`是`HttpServletRequest`的包装类。我们可以通过先将请求中body数据取出来，当需要的时候直接读取已经读取的数据就满足了

```
public class MyHttpServletRequestWrapper extends HttpServletRequestWrapper {

    public byte[] getRequestBody() {
        return requestBody;
    }

    private byte[] requestBody = null;

    public MyHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        requestBody = StreamUtils.copyToByteArray(request.getInputStream());
    }
    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream bais = new ByteArrayInputStream(requestBody);
        return new ServletInputStream() {
            @Override
            public int read() {
                return bais.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }
        };
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }
}
```

# Filter
使用上面的实例，需要使用到Spring的`Filter`。因为`Filter`提供了让我们包装`HttpServletRequest`的方法
```
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException;
```
使用自定义
```
@Component
@Slf4j
public class MyFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        MyHttpServletRequestWrapper requestWrapper = new MyHttpServletRequestWrapper((HttpServletRequest) request);
        log.info(new String(requestWrapper.getRequestBody()));
        // 将包装类继续使用
        chain.doFilter(requestWrapper, response);
    }
}
```
# 题外话
曾今一度钻牛角尖，希望能通过`HandlerInterceptorAdapter`实现。最终发现无法修改`Request`，一旦读取了数据后。Spring容器内部就无法读取到数据  
```
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		return true;
	}
```


# Filter VS Interceptor

- 过滤器Filter依赖于Servlet容器，基于回调函数，过滤范围大，还能进行资源过滤
- 拦截器Interceptor依赖于框架容器，基于反射机制，只过滤请求
- tomcat容器中执行顺序: Filter -> Servlet -> Interceptor -> Controller

# Links 
过滤器与拦截器的区别:https://segmentfault.com/a/1190000018381259  
过滤器与拦截器的区别:https://juejin.im/post/5d064bc0e51d4510aa0114f5  
RequestBody多次读取:https://javacfox.github.io/2019/06/28/%E5%AE%9E%E7%8E%B0HttpServletRequest-getInputStream%E5%A4%9A%E6%AC%A1%E8%AF%BB%E5%8F%96/