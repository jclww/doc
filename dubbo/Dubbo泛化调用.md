## Dubbo协议
上次已经分析过了
[dubbo协议](http://note.youdao.com/noteshare?id=2ce9a76e35fdcd70d2d9bc86df65a037)

## 泛化调用

```
    public static final byte RESPONSE_VALUE = -111;
    public static final byte RESPONSE_NULL_VALUE = -110;
    public static final byte RESPONSE_WITH_EXCEPTION = -112;

    public static final String DUBBO_GENERIC_METHOD_NAME =  "$invokeWithJsonArgs";
    public static final String DUBBO_GENERIC_METHOD_PARA_TYPES  = "Ljava/lang/String;[Ljava/lang/String;[B;";
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        byte[] req = encodeParamRequest2();
//        byte[] req = encodeRequest2();
        System.out.println(Arrays.toString(req));
        System.out.println(new String(req));


        String host = "host";
        int port = prot;
        try {
            System.out.println("连接到主机：" + host + " ，端口号：" + port);
            Socket client = new Socket(host, port);
//            client.setSoTimeout(10000);

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
            if (RESPONSE_NULL_VALUE == flag) {
                System.out.println("NULL");
            }
            if (RESPONSE_WITH_EXCEPTION == flag) {
                System.out.println("EXCEPTION");
            }

            Hessian2ObjectInput hessian2ObjectInput = new Hessian2ObjectInput(bis);
            byte[] object = hessian2ObjectInput.readBytes();
            System.out.println(new String(object));
            //释放资源
            bis.close();

            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    private static byte[] encodeRequest2() throws IOException {

        ByteArrayOutputStream outputStreams = new ByteArrayOutputStream();

        byte[] header = new byte[HEADER_LENGTH];
        // set magic number.
        Bytes.short2bytes(MAGIC, header);
        header[2] |= (byte) -62;

        Random random = new Random();
        Bytes.long2bytes(12321311L, header, 4);

        ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
        Hessian2Output op = new Hessian2Output(dataStream);
        op.writeString(Version.getVersion());
        op.writeString("com.xxx.SystemServiceRemote");
        op.writeString("0.0.0");
        op.writeString(DUBBO_GENERIC_METHOD_NAME);
        op.writeString(DUBBO_GENERIC_METHOD_PARA_TYPES);
        op.writeString("sayHello");


        String[] string1 = new String[0];
        op.writeObject(string1);
        op.writeBytes("[]".getBytes());


        Map<String, String> map = new HashMap<>();
        map.put("path", "com.xxx.SystemServiceRemote");
        map.put("interface", "com.xxx.SystemServiceRemote");
        map.put("version", "0.0.0");
        map.put("timeout", "6000");
        map.put("generic", "true");

        op.writeObject(map);


        op.flushBuffer();
        byte[] data = dataStream.toByteArray();

        Bytes.int2bytes(data.length, header, 12);
        outputStreams.write(header);
        // 16
        outputStreams.write(data);
        return outputStreams.toByteArray();
    }


    private static byte[] encodeParamRequest2() throws IOException {

        ByteArrayOutputStream outputStreams = new ByteArrayOutputStream();

        byte[] header = new byte[HEADER_LENGTH];
        // set magic number.
        Bytes.short2bytes(MAGIC, header);
        header[2] |= (byte) -62;

        Random random = new Random();
        Bytes.long2bytes(12321311L, header, 4);

        ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
        Hessian2Output op = new Hessian2Output(dataStream);
        op.writeString(Version.getVersion());
        op.writeString("com.xxx.department.DepartmentRemoteService");
        op.writeString("0.0.0");
        op.writeString(DUBBO_GENERIC_METHOD_NAME);
        op.writeString(DUBBO_GENERIC_METHOD_PARA_TYPES);
        op.writeString("searchDepartment");


        String[] string1 = new String[0];
        op.writeObject(string1);

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("keyword", "测试");

        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("page", 1);
        jsonObject2.put("pageSize", 10);
        jsonArray.add(jsonObject1);
        jsonArray.add(jsonObject2);
        System.out.println(jsonArray.toJSONString());
        op.writeBytes(jsonArray.toJSONString().getBytes());



        Map<String, String> map = new HashMap<>();
        map.put("path", "com.xxx.DepartmentRemoteService");
        map.put("interface", "com.xxx.DepartmentRemoteService");
        map.put("version", "0.0.0");
        map.put("timeout", "6000");
        map.put("generic", "true");

        op.writeObject(map);


        op.flushBuffer();
        byte[] data = dataStream.toByteArray();

        Bytes.int2bytes(data.length, header, 12);
        outputStreams.write(header);
        // 16
        outputStreams.write(data);
        return outputStreams.toByteArray();
    }
```