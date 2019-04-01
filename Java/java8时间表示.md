[TOC]
## JDK1.8 时间表示
主要包含 `时区` `时间表示`  
> package java.time;
1. **ZoneId** 时区
2. **LocalDate** 日期
3. **LocalTime** 时间

### ZoneId 时区
ZoneId 是抽象类
```
public abstract class ZoneId implements Serializable {
}
```
有两个实现类`ZoneOffset` `ZoneRegion`
#### ZoneOffset
ZoneOffset 按照时间区间表达式计算时区`GMT+08:00`
```
ZoneId zone1 = ZoneId.of("GMT+09:00");
LocalDate date1 = LocalDate.now(zone1);
LocalTime time1 = LocalTime.now(zone1);
```

#### ZoneRegion
ZoneRegion 按照地区名称计算时区`Asia/Shanghai`
```
ZoneId zone2 = ZoneId.of("Asia/Shanghai");
LocalDate date2 = LocalDate.now(zone2);
LocalTime time2 = LocalTime.now(zone2);
```

### LocalDate 日期
LocalDate 表示的日期，但是不包含时间
#### 主要属性
```
    /**
     * The year.
     */
    private final int year;
    /**
     * The month-of-year.
     */
    private final short month;
    /**
     * The day-of-month.
     */
    private final short day;
```
#### 常用实例化方法
```
// 系统判断时区获取当前日期
LocalDate.now()
// 自定义时区获取日期
LocalDate now(ZoneId zone)
// 自定义输入年月日
LocalDate of(int year, int month, int dayOfMonth)
// 根据日期表达式计算 eg:2019-04-01
LocalDate parse(CharSequence text, DateTimeFormatter formatter)
```

### LocalTime 时间
LocalTime 用来表示时间
#### 主要属性
```
    /**
     * The hour.
     */
    private final byte hour;
    /**
     * The minute.
     */
    private final byte minute;
    /**
     * The second.
     */
    private final byte second;
    /**
     * The nanosecond.
     */
    private final int nano;
```
#### 常用实例化方法
```
// 根据系统判定时区，获取时间
LocalTime.now()
// 自定义时区获取当前时间
LocalTime now(ZoneId zone)
// 自定义时分秒
LocalTime of(int hour, int minute, int second)
// 根据时间表示计算时间 eg:13:01:01
LocalTime parse(CharSequence text, DateTimeFormatter formatter)
```
### LocalDateTime 日期+时间
LocalDateTime 是`LocalDate` 和 `LocalTime`结合体，用来表示某一天的时间
#### 主要属性
```
    /**
     * The date part.
     */
    private final LocalDate date;
    /**
     * The time part.
     */
    private final LocalTime time;
```
#### 常用实例化方法
```
// 根据系统判定时区，获取日期+时间
LocalDateTime.now()
// 自定义时区获取当前日期+时间
LocalDateTime now(ZoneId zone)
// 自定义日期时分秒
LocalDateTime of(int year, Month month, int dayOfMonth, int hour, int minute, int second)
// 组合日期 + 时间
LocalDateTime of(LocalDate date, LocalTime time)
// 通过日期时间表达式计算时间 eg:2007-12-03T10:15:30
LocalDateTime parse(CharSequence text, DateTimeFormatter formatter)
```
### 常用方法
`LocalDate`、`LocalTime`和`LocalDateTime`常用的方法
```
// 设置新值
withXXX()

// 时间或者日期增加多少
plusXXX()

// 时间或者日期减少多少
minusXXX()
```

### 其他
#### DateTimeFormatter 日期表达式转换
```
LocalDateTime localDateTime = LocalDateTime.now();
System.out.println(localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a");
System.out.println(localDateTime.format(formatter));
```
#### Date 与 LocalDateTime 转换
因为Date是精确到毫秒的，所以最好转为`LocalDateTime`
```
        Date date = new Date();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());

        LocalDateTime localDateTime2 = LocalDateTime.now();
        Instant instant2 = localDateTime2.atZone(ZoneId.systemDefault()).toInstant();
        Date date2 = Date.from(instant2);

        System.out.println(localDateTime + "\n" + date2);
// 输出：
// 2019-04-01T14:52:28.245
// Mon Apr 01 14:52:28 CST 2019
```
#### Instant 新的时间戳
Instant 是jdk1.8的时间戳类，精确到纳秒
##### 主要属性
```
    /**
     * The number of seconds from the epoch of 1970-01-01T00:00:00Z.
     */
    private final long seconds;
    /**
     * The number of nanoseconds, later along the time-line, from the seconds field.
     * This is always positive, and never exceeds 999,999,999.
     */
    private final int nanos;
```
##### 常用方法
```
// 从毫秒转
Instant ofEpochMilli(long epochMilli)
// 转为毫秒
long toEpochMilli()
```


### tips
1. 每次修改时间或日期数据都是创建新对象(final)
2. 1000_000 == 1000000 （1000_000可读性好）看时间戳看到的
3. 每次看源码都能发现新大陆