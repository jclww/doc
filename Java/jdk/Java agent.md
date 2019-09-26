Java agent

使用方式：
1. 应用启动参数指定`-javaagent:/xxx/xxx.jar`
2. 应用启动后通过`arrach`接入


## 使用
定义agent类
```
public class AgentBootstrap {
    /**
     * java -javaagent 方式
     *
     * @param args
     * @param inst
     * @since jdk 1.5
     */
    public static void premain(String args, Instrumentation inst) {
        System.out.println("premain called");
        main(args, inst);
    }

    /**
     * @param args
     * @param inst
     * @since jdk 1.6
     */
    public static void agentmain(String args, Instrumentation inst) {
        System.out.println("agentmain called");
        main(args, inst);
    }

    private static void main(String args, Instrumentation inst) {
        System.out.println("hello word agent. args:" + args);
    }
}
```
编译打包
```
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-assembly-plugin</artifactId>
    <executions>
        <execution>
            <goals>
                <goal>single</goal>
            </goals>
            <phase>package</phase>
            <configuration>
                <descriptorRefs>
                    <descriptorRef>jar-with-dependencies</descriptorRef>
                </descriptorRefs>
                <archive>
                    <manifestEntries>
                        <Premain-Class>com.lww.learn.agent.AgentBootstrap</Premain-Class>
                        <Agent-Class>com.lww.learn.agent.AgentBootstrap</Agent-Class>
                        <Can-Redefine-Classes>true</Can-Redefine-Classes>
                        <Can-Retransform-Classes>true</Can-Retransform-Classes>
                        <Specification-Title>${project.name}</Specification-Title>
                        <Specification-Version>${project.version}</Specification-Version>
                        <Implementation-Title>${project.name}</Implementation-Title>
                        <Implementation-Version>${project.version}</Implementation-Version>
                    </manifestEntries>
                </archive>
            </configuration>
        </execution>
    </executions>
</plugin>
```
测试类
```
public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Main hello !");
        System.in.read();
    }
}
```
### 启动使用
启动参数增加`-javaagent:/xxx/xxx.jar`
```
java -javaagent:/xxx/xxx.jar xxx.class
```
输出
```
premain called
hello word agent. args:null
Main hello !
```

### 启动后连接

```
public class AttachTest {
    public static void main(String[] args) throws Exception{
        String jmxAjmxAgentgent = "/xxx/xxx-jar-with-dependencies.jar";
        // Main的pid
        String pid = "60879";
        VirtualMachine virtualmachine = VirtualMachine.attach(pid);
        // 让JVM加载jmx Agent
        virtualmachine.loadAgent(jmxAjmxAgentgent, "param1=1");
        // Detach
        Thread.sleep(10 * 1000);
        virtualmachine.detach();
    }
}
```
输出
```
agentmain called
hello word agent. args:param1=1
```
