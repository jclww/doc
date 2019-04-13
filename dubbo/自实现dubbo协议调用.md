## 自定义dubbo协议 - Hessian

### dubbo依赖
> 主要使用dubbo Hessian的序列化工具

```
        <dubbo.version>3.2.0.18-RELEASE</dubbo.version>

        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>dubbo</artifactId>
            <version>${dubbo.version}</version>
        </dependency>
```

### 具体实现
依赖dubbo的序列化方法，初级版

``` java
public class Main {
    // header length.
    protected static final int HEADER_LENGTH = 16;

    // magic header.
    protected static final short MAGIC = (short) 0xdabb;

    public static final byte RESPONSE_VALUE = -111;


    public static void main(String[] args) throws IOException {
        byte[] req = encodeParamRequest();
//        byte[] req = encodeRequest();
        System.out.println(Arrays.toString(req));
        System.out.println(new String(req));


        String host = "ip";
        int port = port;
        try {
            System.out.println("连接到主机：" + host + " ，端口号：" + port);
            Socket client = new Socket(host, port);
            client.setSoTimeout(10000);

            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
            out.write(req);
            out.flush();

            InputStream inFromServer = client.getInputStream();

            BufferedInputStream bis = new BufferedInputStream(inFromServer);

            byte[] header = new byte[HEADER_LENGTH];
            int length = bis.read(header);
            System.out.println("response header:" + Arrays.toString(header) + " length:" + length);

            byte status = header[3];
            if (status != Response.OK) {
                System.out.println("请求出错了");
            }
            byte len = (byte) Bytes.bytes2int(header, 11);
            System.out.println("data length:" + len);


            byte flag = (byte) bis.read();
            if (RESPONSE_VALUE == flag) {
                System.out.println("success");
            }

            Hessian2ObjectInput hessian2ObjectInput = new Hessian2ObjectInput(bis);
            Object object = hessian2ObjectInput.readObject();
            System.out.println("result:" + JSON.toJSONString(object));
            //释放资源
            bis.close();

            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取一个无参调用请求
     *
     * @return
     */
    private static byte[] encodeRequest() throws IOException {

        ByteArrayOutputStream outputStreams = new ByteArrayOutputStream();

        byte[] header = new byte[HEADER_LENGTH];
        // set magic number.
        Bytes.short2bytes(MAGIC, header);
        header[2] |= (byte) -62;

        Random random = new Random();
        Bytes.long2bytes(random.nextInt(), header, 4);


        ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
        Hessian2Output op = new Hessian2Output(dataStream);
        op.writeString(Version.getVersion());
        op.writeString("com.xxx.SystemServiceRemote");
        op.writeString("0.0.0");
        op.writeString("sayHello");
        op.writeString("");

        Map<String, String> map = new HashMap<>();
        map.put("path", "com.xxx.SystemServiceRemote");
        map.put("interface", "com.xxx.SystemServiceRemote");
        map.put("version", "0.0.0");
        map.put("timeout", "6000");
        op.writeObject(map);

        op.flushBuffer();
        byte[] data = dataStream.toByteArray();

        Bytes.int2bytes(data.length, header, 12);
        outputStreams.write(header);
        // 16
        outputStreams.write(data);
        return outputStreams.toByteArray();
    }

    /**
     * 获取一个无参调用请求
     *
     * @return
     */
    private static byte[] encodeParamRequest() throws IOException {

        ByteArrayOutputStream outputStreams = new ByteArrayOutputStream();

        byte[] header = new byte[HEADER_LENGTH];
        // set magic number.
        Bytes.short2bytes(MAGIC, header);
        header[2] |= (byte) -62;

        Random random = new Random();
        Bytes.long2bytes(1232131L, header, 4);

        ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
        Hessian2Output op = new Hessian2Output(dataStream);
        op.writeString(Version.getVersion());
        op.writeString("com.xxx.DepartmentRemoteService");
        op.writeString("0.0.0");
        // 调用的方法 com.xxx.DepartmentRemoteService.someMethod
        op.writeString("someMthod");
        // 方法参数类名
        op.writeString("Lcom/xxx/department/DepartmentConditionDTO;Lcom/xxx/PaginationDTO;");

        JSONObject jsonObject1 = new JSONObject();
        // 参数1的JSON表示
        jsonObject1.put("keyword", "SSS");
        op.writeObject(jsonObject1);
        // 参数2的JSON表示
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("page", 1);
        jsonObject2.put("pageSize", 10);
        op.writeObject(jsonObject2);

        Map<String, String> map = new HashMap<>();
        map.put("path", "com.xxx.DepartmentRemoteService");
        map.put("interface", "com.xxx.DepartmentRemoteService");
        map.put("version", "0.0.0");
        map.put("timeout", "6000");

        op.writeObject(map);


        op.flushBuffer();
        byte[] data = dataStream.toByteArray();

        Bytes.int2bytes(data.length, header, 12);
        outputStreams.write(header);
        // 16
        outputStreams.write(data);
        return outputStreams.toByteArray();
    }

}

```