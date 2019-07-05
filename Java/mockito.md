### InjectMocks

### Mock


```
// mock打桩mock返回值
Mockito.when(xxxService.xxxMtthod().thenReturn();

// mock void方法
Mockito.doNothing().when(xxxService).xxxMtthod(Mockito.any()

// mock 返回异常
Mockito.doThrow(new IllegalArgumentException("XXX")).when(xxxService).xxxMtthod(Mockito.any());

// 判断是否执行了某个方法
Mockito.verify(xxxService).xxxMtthod(Mockito.any());

```

### Spy
调用真实方法
```
    @Spy
    private AsyncTaskExecutor asyncTaskExecutor = new ThreadPoolTaskExecutor();
    
    @Before
    public void before() {
        ((ThreadPoolTaskExecutor) asyncTaskExecutor).initialize();
    }

// 使用打桩调用mock方法
Mockito.doReturn(null).when(xxxService).xxxMtthod(Mockito.any());

```

### PowerMock

```
@RunWith(PowerMockRunner.class)
@PrepareForTest({xxxServiceImpl.class})
    @InjectMocks
    private xxxServiceImpl xxxService;
    @Mock
    private xxx2Service xxx2Service;

// spy
xxxServiceImpl xxxService = PowerMockito.spy(this.xxxService);

// power mock私有方法
PowerMockito.doReturn(true).when(xxxService, "xxxMethod", Mockito.any(), Mockito.any(), Mockito.any());

// 验证是否调用某私有方法
PowerMockito.verifyPrivate(xxxService).invoke("xxxMethod", Mockito.any(), Mockito.any(), Mockito.any());

// 测试私有方法
boolean result = Whitebox.invokeMethod(xxxService, "xxxMethod", param);
```

