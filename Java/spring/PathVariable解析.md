## 使用

```
    @GetMapping("/{id}")
    @ResponseBody
    public Object something(@PathVariable("id") Long id) {
        return id;
    }
```

## url解析
将实际url拆分成变量
```
DispatcherServlet#doService => doDispatch => getHandler
                    ||
                    ||
                    \/
AbstractHandlerMapping#getHandler => getHandlerInternal
                    ||
                    ||
                    \/
AbstractHandlerMethodMapping#getHandlerInternal => lookupHandlerMethod => handleMatch
                    ||
                    ||
                    \/
RequestMappingInfoHandlerMapping#handleMatch
```

RequestMappingInfoHandlerMapping#handleMatch
``` java
Set<String> patterns = info.getPatternsCondition().getPatterns();
if (patterns.isEmpty()) {
	bestPattern = lookupPath;
	uriVariables = Collections.emptyMap();
	decodedUriVariables = Collections.emptyMap();
}
else {
	bestPattern = patterns.iterator().next();
	// PathVariable参数解析 AntPathMatcher
	uriVariables = getPathMatcher().extractUriTemplateVariables(bestPattern, lookupPath);
	decodedUriVariables = getUrlPathHelper().decodePathVariables(request, uriVariables);
}
request.setAttribute(BEST_MATCHING_PATTERN_ATTRIBUTE, bestPattern);
// 将变量暂存，会在后面使用 PathVariableMethodArgumentResolver
request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, decodedUriVariables);

```
AntPathMatcher#extractUriTemplateVariables
```
@Override
public Map<String, String> extractUriTemplateVariables(String pattern, String path) {
	Map<String, String> variables = new LinkedHashMap<String, String>();
	// variables 返回的变量map
	boolean result = doMatch(pattern, path, true, variables);
	if (!result) {
		throw new IllegalStateException("Pattern \"" + pattern + "\" is not a match for \"" + path + "\"");
	}
	return variables;
}
```

## url参数绑定
将url中的变量绑定到`Controller`中的参数  

```
DispatcherServlet#doService => doDispatch => ha.handle
                    ||
                    ||
                    \/
AbstractHandlerMethodAdapter#handle => handleInternal => invokeHandlerMethod => invocableMethod.invokeAndHandle
                    ||
                    ||
                    \/
ServletInvocableHandlerMethod#invokeAndHandle => invokeForRequest(父类) => getMethodArgumentValues => this.argumentResolvers.resolveArgument
                    ||
                    ||
                    \/
HandlerMethodArgumentResolverComposite#resolveArgument => resolver.resolveArgument
                    ||
                    ||
                    \/
AbstractNamedValueMethodArgumentResolver#resolveArgument => resolveName
                    ||
                    ||
                    \/
PathVariableMethodArgumentResolver#resolveName
```

PathVariableMethodArgumentResolver#resolveName
```
@Override
@SuppressWarnings("unchecked")
protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
    // 前面通过url路径解析的变量参数
	Map<String, String> uriTemplateVars = (Map<String, String>) request.getAttribute(
			HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
	return (uriTemplateVars != null ? uriTemplateVars.get(name) : null);
}
```

## 核心类
RequestMappingInfoHandlerMapping#handleMatch  : url前置处理   

AntPathMatcher#extractUriTemplateVariables : url变量解析核心处理类  

InvocableHandlerMethod#getMethodArgumentValues : 将http参数解析为Controller参数

