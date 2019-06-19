## 定义Enum
```
public enum StatusEnum {
    PAY(1, "PAY"),
    INCALL("INCALL");
    private String status;
    private int code;

    StatusEnum(String status) {
        this.status = status;
    }
    StatusEnum(int code, String status) {
        this.status = status;
        this.code = code;
    }

    public static void main(String[] args) {
        StatusEnum statusEnum = StatusEnum.INCALL;

        System.out.println(statusEnum);
    }
}
```
## 编译后
```
public final class com.xxx.StatusEnum extends java.lang.Enum<com.xxx.StatusEnum>
    
    public static final StatusEnum PAY;

    public static final StatusEnum INCALL;

    private static final StatusEnum[] $VALUES;

    // 生成构造函数1
    StatusEnum(String name, int ordinal, String status) {
        super(name, ordinal);
        this.status = status;
    }
    // 生成构造函数2
    StatusEnum(String name, int ordinal, int code, String status) {
        super(name, ordinal);
        this.status = status;
        this.code = code;
    }
    // 返回所有value
    public static StatusEnum[] values() {
        return $VALUES.clone();
    }

    static {
        PAY = new StatusEnum("PAY", 0, 1, "PAY");
        INCALL = new StatusEnum("INCALL", 1, "INCALL");
        StatusEnum[] $VALUES = new StatusEnum[2];
        $VALUES[0] = PAY;
        $VALUES[1] = INCALL;
        StatusEnum.$VALUES = $VALUES;
    }
```
java.lang.Enum
```
    protected Enum(String name, int ordinal) {
        this.name = name;
        this.ordinal = ordinal;
    }
```

## QA
1.枚举类不能声明调用`Enum`的`super`方法  
A：编译报错:call to super is not allowed in enum constructor


